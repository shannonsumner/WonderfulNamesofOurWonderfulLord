package com.shannonsumner.namesofjesus

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** Starts Hilt, which creates the app's databases, repositories, and ViewModels. */
@HiltAndroidApp
class WonderfulNamesApplication : Application()
