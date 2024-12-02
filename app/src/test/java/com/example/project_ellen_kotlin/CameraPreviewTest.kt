package com.example.project_ellen_kotlin


import android.Manifest
import android.content.Context
import android.graphics.ImageFormat
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.Preview.SurfaceProvider
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import com.example.project_ellen_kotlin.ui.camera.CameraFragment
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
//Reference: https://github.com/android/camera-samples/blob/main/CameraXBasic/app/src/androidTest/java/com/android/example/cameraxbasic/CameraPreviewTest.kt
@RunWith(RobolectricTestRunner::class)
class CameraPreviewTest : LifecycleOwner, ImageReader.OnImageAvailableListener, Consumer<SurfaceRequest.Result> {
    @get:Rule
    val cameraAccess = GrantPermissionRule.grant(Manifest.permission.CAMERA)
//    @get:Rule
//    val activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Mock
    lateinit var cameraProviderFuture: ProcessCameraProvider
    lateinit var processCameraProvider: ProcessCameraProvider
    private lateinit var context: Context

//    var lifecycle = mock(LifecycleOwner::class.java)
    private var registry: LifecycleRegistry? = null
    private val thread = HandlerThread("CameraPreviewTest").also { it.start() }
    private var executor = Executors.newSingleThreadExecutor()
    private var provider: ProcessCameraProvider? = null // requires main thread


    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        assertNotNull(context)
        provider = ProcessCameraProvider.getInstance(context).get()
        assertNotNull(provider)
    }
    @UiThreadTest
    @After
    fun teardown() {
        provider?.unbindAll()
        executor?.shutdown()
    }

    private val reader = ImageReader.newInstance(1920, 1080, ImageFormat.YUV_420_888, 30)
    private val count = AtomicInteger(0)
    @Before
    fun setupImageReader() {
//        reader.setOnImageAvailableListener(this, Handler(thread.looper))
        reader.setOnImageAvailableListener(this, Handler(thread.looper))
    }

    @After
    fun teardownImageReader() {
        reader.close()
        thread.quit()
    }

//    override fun getLifecycle() = registry!!
    override val lifecycle: Lifecycle
        get() = registry!!

    @Before
    fun markCreated() {
        registry = LifecycleRegistry(this).also{
            it.currentState = Lifecycle.State.INITIALIZED
            it.currentState = Lifecycle.State.CREATED
        }
    }

    @After
    fun markDestroyed() {
        registry?.currentState = Lifecycle.State.DESTROYED
    }

    override fun onImageAvailable(reader: ImageReader?) {
        TODO("Not yet implemented")
    }

    override fun accept(t: SurfaceRequest.Result) {
        when (t.resultCode) {
            SurfaceRequest.Result.RESULT_SURFACE_USED_SUCCESSFULLY -> {
                Log.i("CameraPreviewTest", t.toString())
            }
            SurfaceRequest.Result.RESULT_REQUEST_CANCELLED, SurfaceRequest.Result.RESULT_INVALID_SURFACE, SurfaceRequest.Result.RESULT_SURFACE_ALREADY_PROVIDED, SurfaceRequest.Result.RESULT_WILL_NOT_PROVIDE_SURFACE -> {
                Log.e("CameraPreviewTest", t.toString())
            }
        }
    }

    @Test
    fun useAppContext() {
        assertEquals("com.example.project_ellen_kotlin", context.packageName)
    }

    @Test
    fun testCameraPreview() {

        //init
        val homeFragment : CameraFragment = mock()

        //work
        val result = homeFragment.startCamera()

        assertNotNull(result)

        // select back camera
        registry?.currentState = Lifecycle.State.STARTED
        val selectorBuilder = CameraSelector.Builder()
        val cameraSelector = selectorBuilder.requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
//        provider?.bindToLifecycle(lifecycle, cameraSelector)
//        assertTrue(provider!!.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA))
        if (!provider!!.hasCamera(cameraSelector)) {
            Log.e("CameraPreviewTest", "Back-facing camera not available.")
            return
        }

        // fit the preview size to ImageReader
        val previewBuilder = Preview.Builder()
        previewBuilder.setTargetResolution(Size(reader.width, reader.height))
        previewBuilder.setTargetRotation(Surface.ROTATION_90)
        val preview = previewBuilder.build()

        // acquire camera binding
        provider!!.unbindAll()
        val camera = provider!!.bindToLifecycle(this, selectorBuilder.build(), preview)
        assertNotNull(camera)

        val surfaceConsumer = androidx.core.util.Consumer<SurfaceRequest.Result> { result ->
            when (result.resultCode) {
                SurfaceRequest.Result.RESULT_SURFACE_USED_SUCCESSFULLY -> Log.i("CameraPreviewTest", result.toString())
                SurfaceRequest.Result.RESULT_REQUEST_CANCELLED, SurfaceRequest.Result.RESULT_INVALID_SURFACE, SurfaceRequest.Result.RESULT_SURFACE_ALREADY_PROVIDED, SurfaceRequest.Result.RESULT_WILL_NOT_PROVIDE_SURFACE -> Log.e("CameraPreviewTest", result.toString())
            }
        }

        preview.setSurfaceProvider(executor!!, SurfaceProvider { request: SurfaceRequest ->
            val surface = reader.surface
            Log.i("CameraPreviewTest", String.format("providing: %s", surface))
            request.provideSurface(surface, executor!!, surfaceConsumer)
        })

        // wait until onImageAvailable is invoked. retry several times
        for (repeat in 5 downTo 0) {
            Thread.sleep(600)
            val value = count.get()
            Log.i("CameraPreviewTest", String.format("count: %d", value))
            if (value > 0) return
        }
        Assert.assertNotEquals(0, count.get().toLong())

    }
}