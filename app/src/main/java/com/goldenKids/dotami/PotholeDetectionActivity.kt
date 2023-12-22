package com.goldenKids.dotami

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.combine
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList
import java.util.Collections
import java.util.concurrent.Executors

class PotholeDetectionActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var rectView: RectView
    private lateinit var ortEnvironment: OrtEnvironment
    private lateinit var session: OrtSession
    private lateinit var rootView: View
    private lateinit var flaskApiClient: FlaskApiManager
    private val handler = Handler(Looper.getMainLooper())
    private var flag = true
    private val dataProcess = DataProcess(context = this)
    private lateinit var locationHelper: LocationHelper
    var date =  "yyyy-MM-dd HH:mm:ss" // 날짜 값을 설정하세요
    var location = "서울"
    val uid = "1" // 사용자 ID 값을 설정하세요

    companion object {
        const val PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pothole_detection)
        previewView = findViewById(R.id.previewView)
        rectView = findViewById(R.id.rectView)
        rootView = findViewById(R.id.rootView)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        flaskApiClient = FlaskApiManager()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setPermissions()
        locationHelper = LocationHelper(this)
        load()
        setCamera()
        startFlagUpdater(5)
    }

    private fun startFlagUpdater(seconds: Int) {
        val intervalMillis = seconds * 1000L
        handler.postDelayed(object : Runnable {
            override fun run() {

                flag = true
                handler.postDelayed(this, intervalMillis)
            }
        }, intervalMillis)
    }

    private fun setCamera() {
        val processCameraProvider = ProcessCameraProvider.getInstance(this).get()
        previewView.scaleType = PreviewView.ScaleType.FILL_CENTER
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        val preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_16_9).build()
        preview.setSurfaceProvider(previewView.surfaceProvider)
        val analysis = ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
        analysis.setAnalyzer(Executors.newSingleThreadExecutor()) {
            imageProcess(it)
            it.close()
        }
        processCameraProvider.bindToLifecycle(this, cameraSelector, preview, analysis)
    }

    private fun imageProcess(imageProxy: ImageProxy) {
        val bitmap = dataProcess.imageToBitmap(imageProxy)
        val floatBuffer = dataProcess.bitmapToFloatBuffer(bitmap)
        val inputName = session.inputNames.iterator().next()
        val shape = longArrayOf(
            DataProcess.BATCH_SIZE.toLong(),
            DataProcess.PIXEL_SIZE.toLong(),
            DataProcess.INPUT_SIZE.toLong(),
            DataProcess.INPUT_SIZE.toLong()
        )
        val inputTensor = OnnxTensor.createTensor(ortEnvironment, floatBuffer, shape)
        val resultTensor = session.run(Collections.singletonMap(inputName, inputTensor))
        val outputs = resultTensor.get(0).value as Array<*>
        val results = dataProcess.outputsToNPMSPredictions(outputs)
        rectView.transformRect(results)
        rectView.invalidate()
        if ((!results.isEmpty()) && flag) {
            date =getCurrentDateTime()
            flag = false
            println("Test")
            locationHelper = LocationHelper(this)
            locationHelper.requestLocation { latitude, longitude ->
                location = latitude.toString()+","+longitude.toString()

            }
            println(location)
           // captureAndPrintBitmap(imageProxy)
        }
    }

    private fun load() {
        dataProcess.loadModel()
        dataProcess.loadLabel()
        ortEnvironment = OrtEnvironment.getEnvironment()
        session = ortEnvironment.createSession(
            this.filesDir.absolutePath.toString() + "/" + DataProcess.FILE_NAME,
            OrtSession.SessionOptions()
        )
        rectView.setClassLabel(dataProcess.classes)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION) {
            grantResults.forEach {
                if (it != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한을 허용하지 않으면 사용할 수 없습니다", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setPermissions() {
        val permissions = ArrayList<String>()
        permissions.add(android.Manifest.permission.CAMERA)
        permissions.forEach {
            if (ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSION)
            }
        }
    }

    private fun captureAndPrintBitmap(imageProxy: ImageProxy) {
        val rectViewBitmap = Bitmap.createBitmap(previewView.width, previewView.height, Bitmap.Config.ARGB_8888)
        val rectViewCanvas = Canvas(rectViewBitmap)
        rectView.draw(rectViewCanvas)
        var cameraBitmap = imageProxy.toBitmap()
        cameraBitmap = Bitmap.createScaledBitmap(cameraBitmap, previewView.width, previewView.height, true)
        var bitmap = combineBitmaps(cameraBitmap, rectViewBitmap)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)


        flaskApiClient.uploadImage(date, location, uid, base64String) { response ->
            if (response != null) {
                val message = response.message
                val imageUrl = response.image_url
                println(imageUrl)
            } else {
                println("애송이 실패다")
            }
        }

        bitmap.recycle()
    }

    private fun combineBitmaps(backgroundBitmap: Bitmap, overlayBitmap: Bitmap): Bitmap {
        val combinedBitmap = Bitmap.createBitmap(
            backgroundBitmap.width, backgroundBitmap.height,
            backgroundBitmap.config
        )
        val canvas = Canvas(combinedBitmap)
        canvas.drawBitmap(backgroundBitmap, 0f, 0f, null)
        val left = (combinedBitmap.width - overlayBitmap.width) / 2f
        val top = (combinedBitmap.height - overlayBitmap.height) / 2f
        canvas.drawBitmap(overlayBitmap, left, top, null)
        return combinedBitmap
    }

    fun getCurrentDateTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return currentDateTime.format(formatter)
    }
}
