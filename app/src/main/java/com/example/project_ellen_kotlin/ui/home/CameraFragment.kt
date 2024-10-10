package com.example.project_ellen_kotlin.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.project_ellen_kotlin.MainActivity
import com.example.project_ellen_kotlin.Receipt
import com.example.project_ellen_kotlin.databinding.FragmentCameraBinding
import com.example.project_ellen_kotlin.ui.SharedViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


typealias CornersListener = () -> Unit

// references:
// https://developer.android.com/codelabs/camerax-getting-started#1
// https://gist.github.com/Rickyip/83108b2006023a2fd3730d350feb477f
class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private lateinit var cameraProvider: ProcessCameraProvider
    private var camera: Camera? = null

    private lateinit var safeContext: Context
    private lateinit var activity: MainActivity
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val listOfReceipts = mutableListOf<Receipt>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
        activity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCameraBinding.inflate(inflater, container, false)
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
        val mediaDir = activity.externalMediaDirs?.firstOrNull()?.let {
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
        var receipt: Receipt? = null
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(safeContext),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    val msg = "Photo capture failed: ${exc.message}"
                    Toast.makeText(safeContext, msg, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, msg)
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                            val savedUri = Uri.fromFile(photoFile)
//                            viewModel.updateImageUri(savedUri)
                            val bitmap = getBitMapFromUri(savedUri)
                            if (bitmap != null) {
                                transformToReceipt(bitmap, savedUri)
                            }
                }
            }
        )
    }

    // Reference: ChatGPT
    private fun getBitMapFromUri(imageUri: Uri?): Bitmap? {
        return imageUri?.let {
            val inputStream = requireContext().contentResolver.openInputStream(it)
            BitmapFactory.decodeStream(inputStream)
        }
    }

    private fun userInputPurpose(receipt: Receipt) {
        var purpose = ""
        val builder: AlertDialog.Builder = AlertDialog.Builder(safeContext)
        builder.setTitle("Enter the purpose of this receipt")

        // Set up the input
        val input = EditText(safeContext)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT)
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("OK",
            DialogInterface.OnClickListener { dialog, which ->
                purpose = input.getText().toString()
                receipt.setPurpose(purpose)
                viewModel.addReceipt(receipt)
                receipt.getUri()?.let { viewModel.updateImageUri(it) }
            })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createReceiptObject(date: Any, amount: Double, bitmap: Bitmap, uri: Uri): Receipt {
        val receipt = Receipt(date, amount, "", uri)
        receipt.setDate(date)
        receipt.setPrice(amount)
        receipt.setUri(uri)
        receipt.setImageData(bitmap)
        return receipt
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun transformToReceipt(bitmap: Bitmap, uri : Uri) {
        val visionImage = InputImage.fromBitmap(bitmap, 0)
        val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        var resultText = ""
        var receipt: Receipt? = null
        textRecognizer.process(visionImage)
            .addOnSuccessListener { visionText ->
                resultText = visionText.text
                receipt = parseResultText(resultText, bitmap, uri)
                receipt?.let { userInputPurpose(it) }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Text recognition failed: ${e.message}", e)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseResultText(resultText: String, bitmap: Bitmap, uri: Uri): Receipt {
        var textArray = resultText.split("\n")
        // Look for total amount
        val totalAmount = findTotalAmount(textArray)
        if (totalAmount == 0.0) {
            // update string saying "Enter Amount"
        }
        // Look for date
        val date = findDate(textArray)
        val receipt = createReceiptObject(date, totalAmount, bitmap, uri)
        return receipt
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    private fun findDate(textArray: List<String>): Any {
        var text = ""
        var formatChosen = ""

        val formatDict = mapOf(
            "dd-mm-yyyy" to "\\b\\d{2}-\\d{2}-\\d{4}\\b(?:\\s\\d{2}:\\d{2})?",
            "mm/dd/yyyy" to "\\b\\d{2}/\\d{2}/\\d{4}\\b(?:\\s\\d{2}:\\d{2})?",
            "yyyy.mm.dd" to "\\b\\d{4}\\.\\d{2}\\.\\d{2}\\b(?:\\s\\d{2}:\\d{2})?",
            "dd/mm/yy" to "\\b\\d{2}/\\d{2}/\\d{2}\\b(?:\\s\\d{2}:\\d{2})?",
            "dd-mm" to "\\b\\d{2}-\\d{2}\\b(?:\\s\\d{2}:\\d{2})?",
            "yyyy/mm/dd" to "\\b\\d{4}/\\d{2}/\\d{2}\\b(?:\\s\\d{2}:\\d{2})?",
            "english" to "^\\b(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|(O|0)ct|Nov|Dec) \\d{1,2} \\d{4}(\\s?\\d{2}:\\d{2})?"
        )


        for (t in textArray) {
            for ((f, reg) in formatDict) {
                if (t.matches(Regex(reg))) {
                    text = t
                    formatChosen = f
                    break
                }
            }
            if (text.length >= 10) {
                break
            }
        }

        val date = convertDate(formatDict, text, formatChosen)
        return date
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertDate(formatDict: Map<String, String>, text: String, formatChosen: String): Any {
        var pattern = ""
        if (formatChosen == "english") {
            pattern = "MMM dd yyyy"
        } else {
            pattern = formatChosen
        }
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
        val date = LocalDate.parse(text, formatter)
        return date
    }

    private fun findTotalAmount(textArray: List<String>): Double {
        var maximum = 0.00
        val format = "^\\\$?(\\d{1,}(\\.\\d{2}))\$"
        for (text in textArray) {
            if (text.matches(Regex(format))) {
                val cost = text.toDouble()
                if (maximum < cost) {
                    maximum = cost
                }
            }
        }
        return maximum
    }

    fun startCamera() {
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