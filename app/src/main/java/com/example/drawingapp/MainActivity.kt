package com.example.drawingapp

import android.app.Dialog
import android.os.Bundle
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var drawingView: DrawingView
    private lateinit var brushButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        brushButton=findViewById(R.id.brush_button)
        drawingView=findViewById(R.id.drawing_view)
        drawingView.changeBrushSize(23.toFloat())
        brushButton.setOnClickListener {
            showBrushChooseDialog()
        }

    }
    private fun showBrushChooseDialog(){
        val brushDialog= Dialog(this@MainActivity)
        brushDialog.setContentView(R.layout.dialog_brush)
        val seekBarProgress=brushDialog.findViewById<SeekBar>(R.id.dialog_seek_bar)
        val showProgressTv=brushDialog.findViewById<TextView>(R.id.dialog_text_view_progress)
        seekBarProgress.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                drawingView.changeBrushSize(seekBar.progress.toFloat())
                showProgressTv.text=seekBar.progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        brushDialog.show()
    }
}