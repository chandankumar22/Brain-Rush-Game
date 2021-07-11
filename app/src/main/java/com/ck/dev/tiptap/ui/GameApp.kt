package com.ck.dev.tiptap.ui

import android.app.Application
import com.ck.dev.tiptap.BuildConfig
import timber.log.Timber

class GameApp:Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String? {
                    return  String.format("(%s:%s)#%s",
                        element.fileName,
                        element.lineNumber,
                        element.methodName
                    );
                }
            })
        }
    }
}