package com.example.project_ellen_kotlin

import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.example.project_ellen_kotlin.ui.dashboard.DashboardFragment
import com.example.project_ellen_kotlin.ui.home.HomeFragment
import org.mockito.Mock
import org.mockito.Mockito

class DashboardTest {
    @Mock
    private lateinit var context : Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        assertNotNull(context)
    }

    @Test
    fun savePhotoAfterTakePhoto() {

    }
}