package com.github.curiousnom.kernelflasher.common.types.partitions

import kotlinx.serialization.Serializable

@Serializable
data class FsMgrFlags(
    val logical: Boolean = false
)
