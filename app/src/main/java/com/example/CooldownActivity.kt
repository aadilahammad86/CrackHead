package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrackheadTheme
import kotlinx.coroutines.delay

class CooldownActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val packageName = intent.getStringExtra("PACKAGE_NAME") ?: ""
        val appName = intent.getStringExtra("APP_NAME") ?: "App"
        val cooldownMinutes = intent.getIntExtra("COOLDOWN_MINUTES", 60)
        val remainingSeconds = intent.getLongExtra("REMAINING_SECONDS", cooldownMinutes * 60L)

        val themePreferences = com.example.data.ThemePreferences(this)

        setContent {
            CrackheadTheme(
                themeMode = themePreferences.themeMode,
                colorSchemeSource = themePreferences.colorSchemeSource
            ) {
                CooldownScreen(
                    appName = appName,
                    cooldownMinutes = cooldownMinutes,
                    initialRemainingSeconds = remainingSeconds,
                    onViewStats = {
                        val intent = Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            putExtra("OPEN_TAB", "insights")
                        }
                        startActivity(intent)
                        finish()
                    },
                    onBackHome = {
                        val intent = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_HOME)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun CooldownScreen(
    appName: String,
    cooldownMinutes: Int,
    initialRemainingSeconds: Long = cooldownMinutes * 60L,
    onViewStats: () -> Unit,
    onBackHome: () -> Unit
) {
    var remainingSeconds by remember { mutableLongStateOf(initialRemainingSeconds) }

    LaunchedEffect(Unit) {
        while (remainingSeconds > 0) {
            delay(1000)
            remainingSeconds--
        }
    }

    val totalSeconds = (cooldownMinutes * 60L).coerceAtLeast(1L)
    val elapsedSeconds = (totalSeconds - remainingSeconds).coerceAtLeast(0L)
    val progress = (elapsedSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "cooldownProgress")

    val minutesPart = remainingSeconds / 60
    val secondsPart = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutesPart, secondsPart)

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            com.example.ui.theme.DarkCanvas,
            com.example.ui.theme.SurfaceContainerHigh,
            com.example.ui.theme.DarkCanvas
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Lock Icon Box
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(com.example.ui.theme.CooldownRedBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = com.example.ui.theme.CooldownRed,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App title
            Text(
                text = "$appName is on cooldown",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = com.example.ui.theme.TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle message
            Text(
                text = "You've hit today's limit. Time to come back to the real world.",
                fontSize = 15.sp,
                color = com.example.ui.theme.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // BACK IN Label
            Text(
                text = "BACK IN",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = com.example.ui.theme.TextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Big Timer Display
            Text(
                text = timeFormatted,
                fontSize = 52.sp,
                fontWeight = FontWeight.ExtraBold,
                color = com.example.ui.theme.TextPrimary
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                trackColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Elapsed / Total labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${elapsedSeconds / 60} min elapsed",
                    fontSize = 13.sp,
                    color = com.example.ui.theme.TextSecondary
                )
                val cooldownLabel = if (cooldownMinutes >= 60 && cooldownMinutes % 60 == 0) {
                    "${cooldownMinutes / 60}h cooldown"
                } else {
                    "${cooldownMinutes}m cooldown"
                }
                Text(
                    text = cooldownLabel,
                    fontSize = 13.sp,
                    color = com.example.ui.theme.TextSecondary
                )
            }
        }

        // Bottom Actions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onViewStats,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "View My Stats",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onBackHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Back to Home Screen",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = com.example.ui.theme.TextSecondary
                )
            }
        }
    }
}
