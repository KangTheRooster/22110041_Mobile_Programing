package com.example.android.architecture.blueprints.todoapp

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,  // Default timeout duration
    timeUnit: TimeUnit = TimeUnit.SECONDS,  // Default time unit for the timeout
    afterObserve: () -> Unit = {}  // Optional function to execute after observing
): T {
    var data: T? = null
    val latch = CountDownLatch(1)  // Latch to wait for the LiveData to emit a value
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()  // Release the latch when data is received
            this@getOrAwaitValue.removeObserver(this)  // Remove observer to prevent leaks
        }
    }

    this.observeForever(observer)  // Observe LiveData indefinitely for testing

    try {
        afterObserve.invoke()  // Execute additional setup if provided

        // Wait for LiveData to emit a value within the specified time
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")  // Throw exception if timeout is reached
        }
    } finally {
        this.removeObserver(observer)  // Ensure observer is removed even if an exception occurs
    }

    @Suppress("UNCHECKED_CAST")
    return data as T  // Return the received data (throws exception if null)
}
