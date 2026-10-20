package com.github.curiousnom.kernelflasher

internal class MainListener(private val callback: () -> Unit) {
    fun resume() {
        callback.invoke()
    }
}
