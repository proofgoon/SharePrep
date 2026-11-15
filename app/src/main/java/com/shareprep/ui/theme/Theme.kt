package com.shareprep.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Dark = darkColorScheme(
    primary = Color(0xFF9ECAFF),
    onPrimary = Color(0xFF003258),
    secondary = Color(0xFFBBC7DB),
    background = Color(0xFF101418),
    surface = Color(0xFF101418),
    surfaceVariant = Color(0xFF42474E)
)

@Composable
fun SharePrepTheme(content: @Composable () -> Unit) =
    MaterialTheme(colorScheme = Dark, content = content)
