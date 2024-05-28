package com.example.lightintensitymeter

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.lightintensitymeter.ui.theme.LightIntensityMeterTheme

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.mutableStateOf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.layout.Column
import java.util.Locale
import androidx.compose.material3.Button
import androidx.compose.ui.graphics.lerp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var light: Sensor? = null
    private var lightIntensity = mutableStateOf(0f)
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        textToSpeech = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.US
            }
        }
        setContent {
            LightIntensityMeterTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // GetValue(lightIntensity.value)
                    // LightMeterPreview()
                    LightMeter(lightIntensity.value, textToSpeech)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        light?.also { light ->
            sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_LIGHT) {
            lightIntensity.value = event.values[0]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }
}

// @Composable
// fun GetValue(lightIntensity: Float) {
//     Text("Light Intensity: ${lightIntensity}")  // Use the state variable here
// }

// @Composable
// fun Greeting(name: String, modifier: Modifier = Modifier) {
//     Text(
//             text = "Hello $name!",
//             modifier = modifier
//     )
// }

// @Preview(showBackground = true)
// @Composable
// fun GreetingPreview() {
//     LightIntensityMeterTheme {
//         Greeting("Android")
//     }
// }

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LightMeter(lightIntensity: Float, textToSpeech: TextToSpeech) {
    val progress = lightIntensity / 25000f
    val color = lerpColorMy(Color.Yellow, Color.Red, progress)
    val lightState = when {
        lightIntensity < 1000 -> "Not enough"
        lightIntensity < 10000 -> "Just enough"
        else -> "Too much"
    }
    val drawerState = rememberDrawerState(DrawerValue.Closed)


    Scaffold(
        content = {
            List(5) { index ->
                Text("Room ${index + 1}: ${getRecommendedLightLevel(index)} lux")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = {
                        val scope = rememberCoroutineScope()
                        scope.launch {
                            drawerState.open()

                        }
                    }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    )


    Box(
        modifier = Modifier,
        contentAlignment = Alignment.TopEnd
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                textToSpeech.speak(lightState, TextToSpeech.QUEUE_FLUSH, null, "")
            }) {
                Text("Speak Light State", color = Color.Black)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                val lightLevel = "${lightIntensity.toInt()} lux"
                textToSpeech.speak(lightLevel, TextToSpeech.QUEUE_FLUSH, null, "")
            }) {
                Text("Speak Light Level", color = Color.Black)
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    color = color,
                    trackColor = Color.Green,
                    strokeWidth = 16.dp,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.size(200.dp)
                )
                Text(
                    text = "${lightIntensity.toInt()} lux",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.Blue,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    ),
                    textAlign = TextAlign.Center
                )
            }
            val backgroundColor = when {
                lightIntensity < 1000 -> Color.Red
                lightIntensity < 10000 -> Color.Yellow
                else -> Color.Green
            }
            Text(
                text = lightState,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                ),
                color = Color.Black,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .background(color = backgroundColor, shape = RoundedCornerShape(10.dp))
                    .padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
fun LightMeterPreview() {
    LightMeter(20000f, TextToSpeech(null) {})
}



fun lerpColorMy(start: Color, stop: Color, fraction: Float): Color {
    return androidx.compose.ui.graphics.lerp(start, stop, fraction)
}

fun getRecommendedLightLevel(roomIndex: Int): Int {
    return 1000 * (roomIndex + 1)
}
