package com.ck.dev.tiptap.ui.games.findthenumber

import android.app.Application
import com.ck.dev.tiptap.BuildConfig
import com.ck.dev.tiptap.data.AppDatabase
import timber.log.Timber

class GameApp:Application() {

    companion object{
        lateinit var appDatabase: AppDatabase
    }


    override fun onCreate() {
        Timber.i("onCreate called")
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