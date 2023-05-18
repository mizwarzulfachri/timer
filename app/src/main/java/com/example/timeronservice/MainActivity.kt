package com.example.timeronservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.timeronservice.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var timeStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startStopBTN.setOnClickListener {
            startStopTimer()
        }
        binding.resetBtn.setOnClickListener {
            resetTimer()
        }

        serviceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            binding.timeTV.text = getTimeStringFromDouble(time)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hour: Int, min: Int, sec: Int): String = String.format("%02d:%02d:%02d", hour, min, sec)

    private fun startStopTimer() {
        if(timeStarted)
            stopTimer()
        else
            startTimer()
    }

    private fun stopTimer() {
        stopService(serviceIntent)
        binding.startStopBTN.text = "Start"
        binding.startStopBTN.icon = getDrawable(R.drawable.play_btn)
        timeStarted = false
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        startService(serviceIntent)
        binding.startStopBTN.text = "Stop"
        binding.startStopBTN.icon = getDrawable(R.drawable.pause_btn)
        timeStarted = true
    }

    private fun resetTimer() {
        stopTimer()
        time = 0.0
        binding.timeTV.text = getTimeStringFromDouble(time)
    }
}