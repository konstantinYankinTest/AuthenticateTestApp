package com.lookout.authenticatetestapp.extention

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

fun <T> Flow<T>.launchWhenStarted(lifecycleOwner: LifecycleOwner) =
    lifecycleOwner.lifecycleScope.launchWhenStarted {
        this@launchWhenStarted.collect()
    }
