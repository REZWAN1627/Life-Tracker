package com.rex.lifetracker.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.rex.lifetracker.R

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val s = intent.getStringExtra("LOL")

        findViewById<TextView>(R.id.tatti).text = s
    }
}