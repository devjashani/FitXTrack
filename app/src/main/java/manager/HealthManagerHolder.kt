// manager/HealthManagerHolder.kt
package com.yourorg.fitxtrackdemo.manager

import android.content.Context

object HealthManagerHolder {
    private var instance: SimpleHealthManager? = null

    fun getInstance(context: Context): SimpleHealthManager {
        return instance ?: synchronized(this) {
            instance ?: SimpleHealthManager(context.applicationContext).also {
                instance = it
            }
        }
    }

    fun resetInstance() {
        instance?.unregisterListener()
        instance = null
    }
}