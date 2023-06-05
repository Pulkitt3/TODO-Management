package com.pulkit.todoapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object LiveDataTestUtil {
    fun <T> getValue(liveData: LiveData<T>, timeout: Long = 2, timeUnit: TimeUnit = TimeUnit.SECONDS): T? {
        var value: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(t: T) {
                value = t
                latch.countDown()
                liveData.removeObserver(this)
            }
        }

        GlobalScope.launch(Dispatchers.Main) {  liveData.observeForever(observer) }

        latch.await(timeout, timeUnit)
        return value
    }
}
