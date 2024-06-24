package com.example.project_ellen_kotlin


import android.Manifest
import android.content.Context
import android.os.HandlerThread
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.Executors


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
//Reference: https://github.com/android/camera-samples/blob/main/CameraXBasic/app/src/androidTest/java/com/android/example/cameraxbasic/CameraPreviewTest.kt
@RunWith(RobolectricTestRunner::class)
class CameraPreviewTest {
    @Mock
    private lateinit var context: Context

    var lifecycle = mock(LifecycleOwner::class.java)
    private var registry: LifecycleRegistry? = null
    private val thread = HandlerThread("CameraPreviewTest").also { it.start() }
    private var executor = Executors.newSingleThreadExecutor()
    private var provider: ProcessCameraProvider? = null // requires main thread

    fun getLifecycle() = registry!!

    @JvmField
    @Rule
    val cameraAccess = GrantPermissionRule.grant(Manifest.permission.CAMERA)
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

    @Test
    fun sampleTest() {
        assertEquals(4, 2+2)
    }

    @Test
    fun testCameraPreview() {
        registry?.currentState = Lifecycle.State.STARTED
        val selectorBuilder = CameraSelector.Builder()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        // note to self: figure out how lifecycles work
        provider?.bindToLifecycle(lifecycle, cameraSelector)
        assertTrue(provider!!.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA))

    }
}