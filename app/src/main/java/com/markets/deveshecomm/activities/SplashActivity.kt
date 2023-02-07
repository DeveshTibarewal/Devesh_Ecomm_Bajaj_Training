package com.markets.deveshecomm.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets.Type
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.markets.deveshecomm.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.decorView.windowInsetsController!!.hide(
//                Type.systemBars() or Type.navigationBars() or Type.statusBars()
//            )
//        } else {
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//            )
//        }
        setContentView(R.layout.activity_splash)

        splashScreen.setKeepOnScreenCondition { true }
//        Handler(Looper.getMainLooper()).postDelayed({
//            startActivity(Intent(this, MainActivity::class.java))
//        }, 2000)
        startActivity(Intent(this, MainActivity::class.java))
        finish()

    }
}