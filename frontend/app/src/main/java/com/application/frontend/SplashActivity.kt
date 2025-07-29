package com.application.frontend

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // 기존 XML: splash_text 포함

        // 그린 / 코치 부분 색 바꾸기
        val textView = findViewById<TextView>(R.id.splash_text)
        val text = "GreenCoach"
        val spannable = SpannableString(text)
        val green = ContextCompat.getColor(this, R.color.green_text)
        val coach = ContextCompat.getColor(this, R.color.coach_text)
        spannable.setSpan(ForegroundColorSpan(green), 0, 5, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(coach), 5, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannable

        // 2초 뒤 Compose 진입 MainActivity 호출
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}