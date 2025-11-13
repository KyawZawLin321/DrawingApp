package com.example.drawingapp

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.File
import java.io.FileOutputStream


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
    private lateinit var galleryButton : ImageButton
    private lateinit var saveButton : ImageButton
    private val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            findViewById<ImageView>(R.id.gallery_image).setImageURI(result.data?.data)
            drawingView.bringToFront()

        }
    val requestPermission: ActivityResultLauncher<Array<String>> =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions->
        permissions.entries.forEach{
            val permissionName=it.key
            val isGranted=it.value
            if(isGranted && permissionName == Manifest.permission.READ_EXTERNAL_STORAGE){
                Toast.makeText(this,"Permission granted", Toast.LENGTH_SHORT).show()

            }else{
                if(permissionName== Manifest.permission.READ_EXTERNAL_STORAGE){
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()

                }
            }
        }

    }

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
        galleryButton=findViewById(R.id.gallery_button)
        saveButton=findViewById(R.id.save_button)
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
        galleryButton.setOnClickListener(this)
        saveButton.setOnClickListener(this)

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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
            R.id.gallery_button->{
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED
                ){
                  requestStoragePermission()
                }else{
                    val pickIntent= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    openGalleryLauncher.launch(pickIntent)
                    }
                }
            R.id.save_button->{
                val layout = findViewById<FrameLayout>(R.id.linear_l1)
                val bitmap = getBitmapFromView(layout)
                CoroutineScope(IO).launch {
                    saveImage(bitmap)
                }

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
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(
            this, Manifest.permission.READ_MEDIA_IMAGES)){
            showRationalDialog()
        }else{
            requestPermission.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showRationalDialog(){
        val builder= AlertDialog.Builder(this)
        builder.setTitle("Storage Permission").setMessage("We need this permission in order" +
                " to access the internal storage")
            .setPositiveButton(R.string.dialog_yes){dialog,_ ->requestPermission.launch(
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.WRITE_EXTERNAL_STORAGE)
            )
            dialog.dismiss()
            }
        builder.create().show()
    }

    private fun getBitmapFromView(view:View): Bitmap{
        val bitmap=createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
        val canvas= Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private suspend fun saveImage(bitmap: Bitmap){
        val filename = "Image_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/DrawingApp")
        }

        val resolver = applicationContext.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            resolver.openOutputStream(uri)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            withContext(Main) {
                Toast.makeText(this@MainActivity, "Saved to Pictures/DrawingApp!", Toast.LENGTH_SHORT).show()
            }
        } else {
            withContext(Main) {
                Toast.makeText(this@MainActivity, "Failed to save image!", Toast.LENGTH_SHORT).show()
            }
        }


    }

}