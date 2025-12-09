package com.yourorg.fitxtrackdemo

import android.app.Application
import android.app.ActivityManager
import android.content.Context
import android.os.Process
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth // example usage to verify init
import android.util.Log

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Only initialize in the main app process (avoid double init in other processes)
        if (!isMainProcess()) {
            Log.d("MyApplication", "Not main process — skipping Firebase init")
            return
        }

        // Initialize Firebase (auto init normally happens; this explicitly ensures it)
        val initialized = FirebaseApp.initializeApp(this)
        if (initialized == null) {
            Log.w("MyApplication", "FirebaseApp.initializeApp returned null — firebase may be configured automatically")
        } else {
            Log.d("MyApplication", "Firebase initialized: ${initialized.name}")
        }

        // OPTIONAL: quick smoke-check (don't rely on this in production)
        try {
            val auth = Firebase.auth
            Log.d("MyApplication", "FirebaseAuth ready: $auth")
        } catch (e: Exception) {
            Log.w("MyApplication", "Firebase auth check failed: ${e.message}")
        }
    }

    private fun isMainProcess(): Boolean {
        val pid = Process.myPid()
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processInfo = manager.runningAppProcesses ?: return true
        val myProcess = processInfo.firstOrNull { it.pid == pid } ?: return true
        return myProcess.processName == applicationContext.packageName
    }
}
