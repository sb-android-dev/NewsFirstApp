package com.sbdev.project.newsfirstapp

import android.view.View
import android.widget.TextView
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import kotlinx.coroutines.*
import java.util.*

fun View.delayOnLifecycle(
    durationInMillis: Long,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    block: () -> Unit
): Job? = findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
    lifecycleOwner.lifecycle.coroutineScope.launch(dispatcher) {
        delay(durationInMillis)
        block()
    }
}

fun setGreeting(userName: String): String{
    val calendar: Calendar = Calendar.getInstance()
    val timeOfDay: Int = calendar.get(Calendar.HOUR_OF_DAY)
    return when {
        timeOfDay < 12 -> {
            String.format("%s, %s", "Good Morning", userName)
        }
        timeOfDay < 16 -> {
            String.format("%s, %s", "Good Afternoon", userName)
        }
        else -> {
            String.format("%s, %s", "Good Evening", userName)
        }
    }
}