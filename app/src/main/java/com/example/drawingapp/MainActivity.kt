package com.example.drawingapp

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import yuku.ambilwarna.AmbilWarnaDialog


class MainActivity : AppCompatActivity() , View.OnClickListener{
    private lateinit var drawingView: DrawingView
    private lateinit var brushButton: ImageButton
    private lateinit var purpleButton: ImageButton
    private lateinit var greenButton: ImageButton
    private lateinit var blueButton: ImageButton
    private lateinit var orangeButton: ImageButton
    private lateinit var redButton: ImageButton
    private lateinit var undoButton : ImageButton
    private lateinit var colorPickerButton : ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        brushButton=findViewById(R.id.brush_button)
        drawingView=findViewById(R.id.drawing_view)
        purpleButton=findViewById(R.id.purple_button)
        greenButton=findViewById(R.id.green_button)
        blueButton=findViewById(R.id.blue_button)
        orangeButton=findViewById(R.id.orange_button)
        redButton=findViewById(R.id.red_button)
        undoButton=findViewById(R.id.undo_button)
        colorPickerButton=findViewById(R.id.color_picker_button)
        drawingView.changeBrushSize(23.toFloat())
        brushButton.setOnClickListener {
            showBrushChooseDialog()
        }
        purpleButton.setOnClickListener(this)
        greenButton.setOnClickListener(this)
        blueButton.setOnClickListener(this)
        orangeButton.setOnClickListener(this)
        redButton.setOnClickListener(this)
        undoButton.setOnClickListener(this)
        colorPickerButton.setOnClickListener(this)


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

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.purple_button->{
                drawingView.setColor("#A808FA")
            }
            R.id.green_button->{
                drawingView.setColor("#BDF418")

            }
            R.id.blue_button->{
                drawingView.setColor("#51DFF4")

            }
            R.id.orange_button->{
                drawingView.setColor("#FFAF09")

            }
            R.id.red_button->{
                drawingView.setColor("#F60404")

            }
            R.id.undo_button->{
                drawingView.undoPath()
            }
            R.id.color_picker_button->{
                showColorPickerDialog()
            }
        }
    }
    private fun showColorPickerDialog(){
        val dialog= AmbilWarnaDialog(this,Color.GREEN,object : AmbilWarnaDialog.OnAmbilWarnaListener{
            override fun onCancel(dialog: AmbilWarnaDialog?) {

            }

            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                drawingView.setColor(color)
            }
        })
        dialog.show()
    }
}