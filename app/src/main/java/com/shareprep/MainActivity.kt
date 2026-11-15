package com.shareprep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.shareprep.ui.theme.SharePrepTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = (application as SharePrepApp).repository
        setContent {
            SharePrepTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // Navigation + screens are added in the next steps.
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("SharePrep", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }
        }
    }
}
