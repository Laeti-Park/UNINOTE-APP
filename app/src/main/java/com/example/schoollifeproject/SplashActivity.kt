package com.example.schoollifeproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.schoollifeproject.databinding.ActivitySplashBinding

/**
 * 처음 실행시 등장하는 Activity
 * */

class SplashActivity : AppCompatActivity() {

    val SPLASH_VIEW_TIME : Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, SPLASH_VIEW_TIME)


    }
}