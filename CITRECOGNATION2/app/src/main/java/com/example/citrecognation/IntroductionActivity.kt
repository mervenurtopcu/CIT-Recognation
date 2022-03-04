package com.example.citrecognation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler


class IntroductionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)

        // logo ekranı

        Handler().postDelayed({

            val intent = Intent(this@IntroductionActivity,HomeActivity::class.java)
            startActivity(intent)
            finish()
        },3000);

    }
}