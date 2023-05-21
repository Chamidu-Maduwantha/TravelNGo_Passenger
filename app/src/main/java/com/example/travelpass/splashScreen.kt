package com.example.travelpass

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.core.animation.doOnEnd

class splashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)




      Handler().postDelayed({
            val intent = Intent(this@splashScreen, SelectionPage::class.java)
            startActivity(intent)
        }, 3000)
    }


}
