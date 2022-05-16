package br.edu.ufabc.cronometro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import br.edu.ufabc.cronometro.databinding.ActivityMainBinding
import java.io.Serializable

enum class State : Serializable { INITIAL, RUNNING, STOPPED }

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var timeElapsed = 0L
    private lateinit var state: State

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reset()
        runTimer()
        bindEvents()
    }

    private fun updateTime() {
        val hours = timeElapsed / 3600
        val minutes = timeElapsed % 3600 / 60
        val seconds = timeElapsed % 60

        binding.textviewTimeElapsed.text = getString(R.string.time_format, hours, minutes, seconds)
    }

    private fun runTimer() {
        val handler = Handler(Looper.getMainLooper())

        handler.post(object : Runnable {
            override fun run() {
                if (state == State.RUNNING) {
                    timeElapsed++
                }
                updateTime()
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun reset() {
        state = State.INITIAL
        timeElapsed = 0L
        binding.buttonStartStop.text = getString(R.string.button_start_stop_default)
        binding.buttonStartStop.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.purple_500
            )
        )
        updateTime()
        formatStart()
    }

    private fun formatStart() {
        binding.buttonStartStop.text = getString(R.string.button_start_stop_default)
        binding.buttonStartStop.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.purple_500
            )
        )
    }

    private fun formatStop() {
        binding.buttonStartStop.text = getString(R.string.button_start_stop_stop)
        binding.buttonStartStop.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.red
            )
        )
    }

    private fun startStop() {
        if (isRunning()) {
            state = State.STOPPED
            formatStart()
        } else {
            state = State.RUNNING
            formatStop()
        }
    }

    private fun isRunning() = state == State.RUNNING

    private fun bindEvents() {
        binding.buttonStartStop.setOnClickListener { startStop() }
        binding.buttonReset.setOnClickListener { reset() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("timeElapsed", timeElapsed)
        outState.putSerializable("state", state)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        timeElapsed = savedInstanceState.getLong("timeElapsed")
        state = savedInstanceState.getSerializable("state") as State
        if (isRunning()) {
            formatStop()
        } else {
            formatStart()
        }
    }
}