package com.application.frontend

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.splash_text)

        val text = "GreenCoach"
        val spannable = SpannableString(text)

        val greenColor = ContextCompat.getColor(this, R.color.green_text)
        val coachColor = ContextCompat.getColor(this, R.color.coach_text)

        spannable.setSpan(
            ForegroundColorSpan(greenColor),
            0, 5,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            ForegroundColorSpan(coachColor),
            5, text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView.text = spannable

        // 2초 뒤 종료 or 다음 화면 전환
        Handler(Looper.getMainLooper()).postDelayed({
            // 다음 화면 전환 시 사용
            // startActivity(Intent(this, NextActivity::class.java))
            // finish()
        }, 2000)
    }
}