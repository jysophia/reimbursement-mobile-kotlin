package com.example.project_ellen_kotlin.ui.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class FigmaComponents {

    @Composable
    fun FigmaDialogContent(onDismiss: () -> Unit) {
        Box(
            modifier= Modifier
                .shadow(elevation = 4.dp, spotColor = Color(0x40000000), ambientColor = Color(0x40000000))
                .padding(0.dp)
                .width(312.dp)
                .height(217.dp)
                .background(
                    color = Variables.SchemesSurfaceContainerLowest,
                    shape = RoundedCornerShape(size = 28.dp)
                )
                .padding(bottom = 15.dp)
        ) {
            LinearProgressIndicator(
                modifier= Modifier
                    .width(264.dp)
                    .height(12.dp)
                    .padding(start = 2.dp, top = 10.dp, bottom = 4.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        color = Variables.SchemesSecondaryContainer,
                        shape = RoundedCornerShape(size = 8.dp)
                    )
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Child views.
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Your custom Figma design", style = MaterialTheme.typography.h6)

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = onDismiss) {
                    Text("Dismiss")
                }
            }
        }
    }

    object Variables {
        val SchemesSurfaceContainerLowest: Color = Color(0xFFFFFFFF)
        val SchemesSecondaryContainer: Color = Color(0xFFE8DEF8)
    }
}