package com.shannonsumner.namesofjesus

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/** Runs device tests in Hilt's test application so `@HiltAndroidTest` classes can inject. */
class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(loader: ClassLoader?, name: String?, context: Context?): Application =
        super.newApplication(loader, HiltTestApplication::class.java.name, context)
}
