package com.example.project_ellen_kotlin

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import android.content.Context
import org.mockito.Mock

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