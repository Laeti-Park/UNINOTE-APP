package com.example.schoollifeproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

/**
 * 처음 실행시 등장하는 Activity
 * 작성자 : 이준영
 */
class SplashActivity : AppCompatActivity() {

    private val SPLASH_VIEW_TIME: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, SPLASH_VIEW_TIME)
    }
}