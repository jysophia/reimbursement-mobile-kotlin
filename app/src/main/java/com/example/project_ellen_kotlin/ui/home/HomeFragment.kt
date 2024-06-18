package com.example.project_ellen_kotlin.ui.home

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.camera.core.*
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.R
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.project_ellen_kotlin.databinding.FragmentHomeBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias CornersListener = () -> Unit

// references:
// https://developer.android.com/codelabs/camerax-getting-started#1
// https://gist.github.com/Rickyip/83108b2006023a2fd3730d350feb477f
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private lateinit var cameraProvider: ProcessCameraProvider
    private var camera: Camera? = null

    private lateinit var safeContext: Context
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Setup the listener for take photo button
        binding.btnAddReceipt.setOnClickListener{ takePhoto() }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = activity?.externalMediaDirs?.firstOrNull()?.let {
            File(it, resources.getString(com.example.project_ellen_kotlin.R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else activity?.filesDir!!
    }

    private fun takePhoto() {

        // reference: https://www.youtube.com/watch?v=r7JbipBL3GM
        val imageFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "userReceipts")

        if (!imageFolder.exists()) {
            imageFolder.mkdir()
        }

        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        val imageAnalyzer = imageAnalyzer ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(safeContext),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    val msg = "Photo capture failed: ${exc.message}"
                    Toast.makeText(safeContext, msg, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, msg)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(safeContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(safeContext)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            cameraProvider = cameraProviderFuture.get()
            bindCameraUserCases()
        }, ContextCompat.getMainExecutor(safeContext))
    }

    private fun bindCameraUserCases() {

        // Preview
        preview = Preview.Builder().build()
            .also{
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

        imageCapture = ImageCapture.Builder().build()
        imageAnalyzer = ImageAnalysis.Builder().build().apply{
            setAnalyzer(Executors.newSingleThreadExecutor(), ImageAnalysis.Analyzer { imageProxy ->
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                // insert your code here.
                Log.d(TAG, "imageProxy: $imageProxy")
                Log.d(TAG, "rotationDegrees: $rotationDegrees")
                // after done, release the ImageProxy object
                imageProxy.close()
            })
        }

        // Select back camera as default
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            // Unbind use cases before rebinding
            cameraProvider.unbindAll()

            // Bind use cases to camera
            cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalyzer, preview, imageCapture)

        } catch(exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun requestPermissions() {}

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(safeContext, it) == PackageManager.PERMISSION_GRANTED
    }



    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        internal const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}