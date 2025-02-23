package de.sam.abinopolynew

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.NumberPicker
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.sam.abinopolynew.func.hideSystemUI

class NewGame : AppCompatActivity() {

    private lateinit var createGameBottomText: TextView
    private lateinit var seekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_game)
        hideSystemUI(window)

        createGameBottomText = findViewById(R.id.createGameBottomText)
        setBottomTextValue(2)

        val numberPicker: NumberPicker = findViewById(R.id.numberPicker)
        numberPicker.minValue = 2
        numberPicker.maxValue = 5
        numberPicker.value = 2

        numberPicker.setOnValueChangedListener {_, _, newVal ->
            setBottomTextValue(newVal)
        }

        seekBar = findViewById(R.id.sliderButton)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    val threshold = (it.max * 0.9).toInt()

                    if (it.progress >= threshold) {
                        val intent = Intent(this@NewGame, Home::class.java)
                        startActivity(intent)
                    }

                    it.progress = 0
                }
            }
        })
    }

    private fun setBottomTextValue(playerCount: Int) {
        val text = "Spiele mit $playerCount Leuten"
        val spannable = SpannableString(text)

        val start = text.indexOf(playerCount.toString())
        val end = start + playerCount.toString().length

        val pink = ContextCompat.getColor(this, R.color.green)

        spannable.apply {
            setSpan(ForegroundColorSpan(pink), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }


        createGameBottomText.text = spannable
    }
}
