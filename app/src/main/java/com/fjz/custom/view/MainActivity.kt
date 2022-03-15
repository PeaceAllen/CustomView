package com.fjz.custom.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.fjz.custom.view.views.CircleProgressView
import com.fjz.custom.view.views.WaveProgressView

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val waveProgressView = findViewById<WaveProgressView>(R.id.waveProgressView)
        waveProgressView.setProgress(800f)


        val btn = findViewById<Button>(R.id.btnReset)
        val circleProgress = findViewById<CircleProgressView>(R.id.circleProgressView)

        btn.setOnClickListener {
            waveProgressView.setProgress(900f)
            circleProgress.updateProgress(circleProgress.progress + 2)
        }

    }

}