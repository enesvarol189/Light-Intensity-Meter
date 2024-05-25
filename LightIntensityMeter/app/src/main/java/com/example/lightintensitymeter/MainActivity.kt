package com.example.lightintensitymeter

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
import androidx.compose.runtime.mutableStateOf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.StrokeCap


class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var light: Sensor? = null
    private var lightIntensity = mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        setContent {
            LightIntensityMeterTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // GetValue(lightIntensity.value)
                    // LightMeterPreview()
                    LightMeter(lightIntensity.value)
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
}

// @Composable
// fun GetValue(lightIntensity: Float) {
//     Text("Light Intensity: ${lightIntensity}") 
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

@Composable
fun LightMeter(lightIntensity: Float) {
    val progress = lightIntensity / 40000f
    val color = when {
        lightIntensity < 10000 -> Color.Green
        lightIntensity < 30000 -> Color(0xFFFFA500)
        else -> Color.Red
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5DEB3)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = progress,
            color = color,
            trackColor = Color.Green,
            strokeWidth = 16.dp,
            strokeCap = StrokeCap.Round,
            modifier = Modifier.size(200.dp)
            // modifier = Modifier.padding(6.dp)
        )
        Text(
            text = "${lightIntensity.toInt()} lux",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun LightMeterPreview() {
    LightMeter(20000f)
}
