package com.github.capntrips.kernelflasher

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.capntrips.kernelflasher.ui.screens.RefreshableScreen
import com.github.capntrips.kernelflasher.ui.screens.backups.BackupsContent
import com.github.capntrips.kernelflasher.ui.screens.backups.SlotBackupsContent
import com.github.capntrips.kernelflasher.ui.screens.error.ErrorScreen
import com.github.capntrips.kernelflasher.ui.screens.main.MainContent
import com.github.capntrips.kernelflasher.ui.screens.main.MainViewModel
import com.github.capntrips.kernelflasher.ui.screens.reboot.RebootContent
import com.github.capntrips.kernelflasher.ui.screens.slot.SlotContent
import com.github.capntrips.kernelflasher.ui.screens.slot.SlotFlashContent
import com.github.capntrips.kernelflasher.ui.screens.updates.UpdatesAddContent
import com.github.capntrips.kernelflasher.ui.screens.updates.UpdatesChangelogContent
import com.github.capntrips.kernelflasher.ui.screens.updates.UpdatesContent
import com.github.capntrips.kernelflasher.ui.screens.updates.UpdatesViewContent
import com.github.capntrips.kernelflasher.ui.theme.KernelFlasherTheme
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService
import com.topjohnwu.superuser.nio.FileSystemManager
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File


@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@ExperimentalSerializationApi
@ExperimentalUnitApi
class MainActivity : ComponentActivity() {
    companion object {
        const val TAG: String = "MainActivity"
        init {
            Shell.setDefaultBuilder(Shell.Builder.create().setFlags(Shell.FLAG_MOUNT_MASTER))
        }
    }

    private var rootServiceConnected: Boolean = false
    private var viewModel: MainViewModel? = null
    private lateinit var mainListener: MainListener
    var isAwaitingResult = false

    inner class AidlConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            if (!rootServiceConnected) {
                val ipc: IFilesystemService = IFilesystemService.Stub.asInterface(service)
                val binder: IBinder = ipc.fileSystemService
                onAidlConnected(FileSystemManager.getRemote(binder))
                rootServiceConnected = true
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            setContent {
                KernelFlasherTheme {
                    ErrorScreen(stringResource(R.string.root_service_disconnected))
                }
            }
        }
    }

    private fun ensureBinaries() {
        val binaries = listOf("lptools_static", "httools_static", "magiskboot")
        val filesToChmod = mutableListOf<String>()

        for (filename in binaries) {
            val binary = File(applicationInfo.nativeLibraryDir, "lib$filename.so")
            val dest = File(filesDir, filename)
            if (!dest.exists() || dest.length() != binary.length() || !dest.canExecute()) {
                binary.inputStream().use { inputStream ->
                    dest.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                dest.setExecutable(true, false)
                filesToChmod.add(dest.absolutePath)
            }
        }

        val scriptDest = File(filesDir, "flash_ak3.sh")
        if (!scriptDest.exists() || scriptDest.length() == 0L || !scriptDest.canExecute()) {
            assets.open("flash_ak3.sh").use { inputStream ->
                scriptDest.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            scriptDest.setExecutable(true, false)
            filesToChmod.add(scriptDest.absolutePath)
        }

        if (filesToChmod.isNotEmpty()) {
            Shell.cmd("chmod 755 " + filesToChmod.joinToString(" ")).exec()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        enableEdgeToEdge()
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val scale = ObjectAnimator.ofPropertyValuesHolder(
                splashScreenView.view,
                PropertyValuesHolder.ofFloat(
                    View.SCALE_X,
                    1f,
                    0f
                ),
                PropertyValuesHolder.ofFloat(
                    View.SCALE_Y,
                    1f,
                    0f
                )
            )
            scale.interpolator = AccelerateInterpolator()
            scale.duration = 250L
            scale.doOnEnd { splashScreenView.remove() }
            scale.start()
        }

        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (viewModel?.isRefreshing == false || Shell.isAppGrantedRoot() == false) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )

        Shell.getShell()
        if (Shell.isAppGrantedRoot()!!) {
            val intent = Intent(this, FilesystemService::class.java)
            RootService.bind(intent, AidlConnection())
        } else {
            setContent {
                KernelFlasherTheme {
                    ErrorScreen(stringResource(R.string.root_required))
                }
            }
        }
    }

    fun onAidlConnected(fileSystemManager: FileSystemManager) {
        try {
            ensureBinaries()
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            setContent {
                KernelFlasherTheme {
                    ErrorScreen(e.message!!)
                }
            }
            return
        }
        setContent {
            val navController = rememberNavController()
            viewModel = viewModel {
                val application = checkNotNull(get(ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY))
                MainViewModel(application, fileSystemManager, navController)
            }
            val mainViewModel = viewModel!!
            KernelFlasherTheme {
                if (!mainViewModel.hasError) {
                    mainListener = MainListener {
                        mainViewModel.refresh(this)
                    }
                    val slotViewModelA = mainViewModel.slotA
                    val slotViewModelB = mainViewModel.slotB
                    val backupsViewModel = mainViewModel.backups
                    val updatesViewModel = mainViewModel.updates
                    val rebootViewModel = mainViewModel.reboot
                    BackHandler(enabled = mainViewModel.isRefreshing, onBack = {})
                    val slotContent: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit = { backStackEntry ->
                        val slotSuffix = backStackEntry.arguments?.getString("slotSuffix") ?: ""
                        val slotViewModel = if (slotSuffix == "_b") slotViewModelB else slotViewModelA
                        if (slotViewModel!!.wasFlashSuccess != null && listOf("slot{slotSuffix}", "slot").any { navController.currentDestination!!.route.equals(it) }) {
                            slotViewModel.clearFlash(this@MainActivity)
                        }
                        val title = stringResource(if (slotSuffix == "_a") R.string.slot_a else if (slotSuffix == "_b") R.string.slot_b else R.string.slot)
                        RefreshableScreen(mainViewModel, navController, swipeEnabled = true, title = title) {
                            SlotContent(slotViewModel, slotSuffix, navController)
                        }

                    }
                    val slotFlashContent: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit = { backStackEntry ->
                        val slotSuffix = backStackEntry.arguments?.getString("slotSuffix") ?: ""
                        val slotViewModel = if (slotSuffix == "_b") slotViewModelB else slotViewModelA
                        val currentRoute = navController.currentDestination?.route ?: ""
                        val title = stringResource(
                            if (currentRoute.endsWith("/backup")) R.string.backup
                            else R.string.flash
                        )
                        RefreshableScreen(mainViewModel, navController, title = title) {
                            SlotFlashContent(slotViewModel!!, slotSuffix, navController)
                        }
                    }
                    val slotBackupsContent: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit = { backStackEntry ->
                        val slotSuffix = backStackEntry.arguments?.getString("slotSuffix") ?: ""
                        val slotViewModel = if (slotSuffix == "_b") slotViewModelB else slotViewModelA
                        if (backStackEntry.arguments?.getString("backupId") != null) {
                            backupsViewModel.currentBackup = backStackEntry.arguments?.getString("backupId")
                        } else {
                            backupsViewModel.clearCurrent()
                        }
                        RefreshableScreen(mainViewModel, navController, title = stringResource(R.string.backups)) {
                            SlotBackupsContent(slotViewModel!!, backupsViewModel, slotSuffix, navController)
                        }
                    }
                    val slotBackupFlashContent: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit = { backStackEntry ->
                        val slotSuffix = backStackEntry.arguments?.getString("slotSuffix") ?: ""
                        val slotViewModel = if (slotSuffix == "_b") slotViewModelB else slotViewModelA
                        backupsViewModel.currentBackup = backStackEntry.arguments?.getString("backupId")
                        if (backupsViewModel.backups.containsKey(backupsViewModel.currentBackup)) {
                            RefreshableScreen(mainViewModel, navController, title = stringResource(R.string.flash)) {
                                SlotFlashContent(slotViewModel!!, slotSuffix, navController)
                            }
                        }

                    }
                    NavHost(
                        navController = navController,
                        startDestination = "main",
                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
                                initialOffset = { (it * 0.12f).toInt() }
                            ) + fadeIn(animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing))
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                                targetOffset = { -(it * 0.08f).toInt() }
                            ) + fadeOut(animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing))
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.End,
                                animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
                                initialOffset = { -(it * 0.08f).toInt() }
                            ) + fadeIn(animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing))
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.End,
                                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                                targetOffset = { (it * 0.12f).toInt() }
                            ) + fadeOut(animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing))
                        }
                    ) {
                        composable("main") {
                            RefreshableScreen(mainViewModel, navController, swipeEnabled = true) {
                                MainContent(mainViewModel, navController)
                            }
                        }
                        if (mainViewModel.isAb) {
                            composable("slot{slotSuffix}", content = slotContent)
                            composable("slot{slotSuffix}/flash", content = slotFlashContent)
                            composable("slot{slotSuffix}/flash/ak3", content = slotFlashContent)
                            composable("slot{slotSuffix}/flash/image", content = slotFlashContent)
                            composable("slot{slotSuffix}/flash/image/flash", content = slotFlashContent)
                            composable("slot{slotSuffix}/backup", content = slotFlashContent)
                            composable("slot{slotSuffix}/backup/backup", content = slotFlashContent)
                            composable("slot{slotSuffix}/backups", content = slotBackupsContent)
                            composable("slot{slotSuffix}/backups/{backupId}", content = slotBackupsContent)
                            composable("slot{slotSuffix}/backups/{backupId}/restore", content = slotBackupsContent)
                            composable("slot{slotSuffix}/backups/{backupId}/restore/restore", content = slotBackupsContent)
                            composable("slot{slotSuffix}/backups/{backupId}/flash/ak3", content = slotBackupFlashContent)
                        } else {
                            composable("slot", content = slotContent)
                            composable("slot/flash", content = slotFlashContent)
                            composable("slot/flash/ak3", content = slotFlashContent)
                            composable("slot/flash/image", content = slotFlashContent)
                            composable("slot/flash/image/flash", content = slotFlashContent)
                            composable("slot/backup", content = slotFlashContent)
                            composable("slot/backup/backup", content = slotFlashContent)
                            composable("slot/backups", content = slotBackupsContent)
                            composable("slot/backups/{backupId}", content = slotBackupsContent)
                            composable("slot/backups/{backupId}/restore", content = slotBackupsContent)
                            composable("slot/backups/{backupId}/restore/restore", content = slotBackupsContent)
                            composable("slot/backups/{backupId}/flash/ak3", content = slotBackupFlashContent)
                        }
                        composable("backups") {
                            backupsViewModel.clearCurrent()
                            RefreshableScreen(mainViewModel, navController, title = stringResource(R.string.backups)) {
                                BackupsContent(backupsViewModel, navController)
                            }
                        }
                        composable("backups/{backupId}") { backStackEntry ->
                            backupsViewModel.currentBackup = backStackEntry.arguments?.getString("backupId")
                            if (backupsViewModel.backups.containsKey(backupsViewModel.currentBackup)) {
                                RefreshableScreen(mainViewModel, navController, title = stringResource(R.string.backup)) {
                                    BackupsContent(backupsViewModel, navController)
                                }
                            }
                        }
                        composable("updates") {
                            updatesViewModel.clearCurrent()
                            RefreshableScreen(mainViewModel, navController, title = stringResource(R.string.updates)) {
                                UpdatesContent(updatesViewModel, navController)
                            }
                        }
                        composable("updates/add") {
                            RefreshableScreen(mainViewModel, navController, title = stringResource(R.string.add)) {
                                UpdatesAddContent(updatesViewModel, navController)
                            }
                        }
                        composable("updates/view/{updateId}") { backStackEntry ->
                            val updateId = backStackEntry.arguments?.getString("updateId")!!.toInt()
                            val currentUpdate = updatesViewModel.updates.firstOrNull { it.id == updateId }
                            updatesViewModel.currentUpdate = currentUpdate
                            if (updatesViewModel.currentUpdate != null) {
                                // TODO: enable swipe refresh
                                RefreshableScreen(mainViewModel, navController, title = stringResource(R.string.updates)) {
                                    UpdatesViewContent(updatesViewModel, navController)
                                }
                            }
                        }
                        composable("updates/view/{updateId}/changelog") { backStackEntry ->
                            val updateId = backStackEntry.arguments?.getString("updateId")!!.toInt()
                            val currentUpdate = updatesViewModel.updates.firstOrNull { it.id == updateId }
                            updatesViewModel.currentUpdate = currentUpdate
                            if (updatesViewModel.currentUpdate != null) {
                                RefreshableScreen(mainViewModel, navController, title = stringResource(R.string.changelog)) {
                                    UpdatesChangelogContent(updatesViewModel, navController)
                                }
                            }
                        }
                        composable("reboot") {
                            RefreshableScreen(mainViewModel, navController, title = stringResource(R.string.reboot)) {
                                RebootContent(rebootViewModel, navController)
                            }
                        }
                        composable("error/{error}") { backStackEntry ->
                            val error = backStackEntry.arguments?.getString("error")
                            ErrorScreen(error!!)
                        }
                    }
                } else {
                    ErrorScreen(mainViewModel.error)
                }
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        if (this::mainListener.isInitialized) {
            if (!isAwaitingResult) {
                mainListener.resume()
            }
        }
    }
}
