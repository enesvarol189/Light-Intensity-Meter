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
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.LaunchedEffect
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
fun LightMeter(lightIntensity: Float, textToSpeech: TextToSpeech) {
    val progress = lightIntensity / 25000f
    val color = lerpColorMy(Color(0xff94c6ff), Color(0xff140c82), progress)
    val currentLanguage = remember { mutableStateOf("EN") }
    var previousLanguage = remember { currentLanguage.value }
    LaunchedEffect(currentLanguage.value) {
        if (previousLanguage != currentLanguage.value) {
            previousLanguage = currentLanguage.value
        }
    }
    val lightStateSpeech = when {
        lightIntensity < 1000 ->
            if (currentLanguage.value == "EN") "Illumination is insufficient" else "Aydınlatma yetersiz"
        lightIntensity < 10000 ->
            if (currentLanguage.value == "EN") "Illumination is just right" else "Aydınlatma yeterli"
        else ->
            if (currentLanguage.value == "EN") "Illumination is excessive" else "Aydınlatma aşırı"
    }
    val lightState = when {
        lightIntensity < 1000 ->
            if (currentLanguage.value == "EN") "insufficient" else "yetersiz"
        lightIntensity < 10000 ->
            if (currentLanguage.value == "EN") "just right" else "yeterli"
        else ->
            if (currentLanguage.value == "EN") "excessive" else "aşırı"
    }
    val lightStatusMessage = when {
        lightIntensity < 10 ->
            if (currentLanguage.value == "EN") "Lights are off" else "Işık kapalı"
        else ->
            if (currentLanguage.value == "EN") "Lights are on" else "Işık açık"
    }
    val lightIntensityPrompt = if (currentLanguage.value == "EN") "Luminous intensity is " else "Işık şiddeti "
    // val drawerState = rememberDrawerState(DrawerValue.Closed)
    // val scope = rememberCoroutineScope()

    // Scaffold(
    //     content = { paddingValues ->
    //         Column(modifier = Modifier.padding(paddingValues)) {
    //             TopAppBar(
    //                 title = { Text("Light Intensity Meter") },
    //                 navigationIcon = {
    //                     IconButton(onClick = {
    //                         scope.launch {
    //                             if (drawerState.isClosed) {
    //                                 textToSpeech.speak(drawerState.currentValue.toString(), TextToSpeech.QUEUE_FLUSH, null, "") }

    //                             else if (drawerState.isOpen) {
    //                                 textToSpeech.speak(drawerState.isOpen.toString(),TextToSpeech.QUEUE_FLUSH, null, "")
    //                                 drawerState.close() }
    //                         }
    //                     }) {
    //                         Icon(Icons.Filled.Menu, contentDescription = "Open Navigation Drawer")
    //                     }
    //                 }
    //             )

    //             ModalDrawerSheet(
    //                 drawerContainerColor = Color.LightGray,
    //                 drawerContentColor = Color.Black,
    //                 content = {
    //                     Column(modifier = Modifier.fillMaxSize()) {
    //                         List(5) { index ->
    //                             Text(
    //                                 text = "Room ${index + 1}: ${getRecommendedLightLevel(index)} lux",
    //                                 modifier = Modifier.padding(16.dp)
    //                             )
    //                         }
    //                     }
    //                 }
    //             )
    //         }
    //     }
    // )

    Surface(color = Color(0xff363636)) {
        Box(
            modifier = Modifier.padding(top = 6.dp, start = 6.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Button(onClick = {
                if (currentLanguage.value == "EN") {
                    currentLanguage.value = "TR"
                    textToSpeech.language = Locale.forLanguageTag("tr-TR")
                } else {
                    currentLanguage.value = "EN"
                    textToSpeech.language = Locale.ENGLISH
                }
            }) {
                Text(currentLanguage.value, color = Color.White)
            }
        }

        Box(
            modifier = Modifier.padding(top = 6.dp, end = 6.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                        textToSpeech.speak(lightStatusMessage, TextToSpeech.QUEUE_FLUSH, null, "")
                    },
                    modifier = Modifier.width(128.dp)
                ) {
                    val buttonText = if (currentLanguage.value == "EN") "Light On/Off" else "Işık Açık mı"
                    Text(buttonText, color = Color.White)
                }
                Spacer(modifier = Modifier.height(1.dp))
                Button(onClick = {
                        textToSpeech.speak(lightStateSpeech, TextToSpeech.QUEUE_FLUSH, null, "")
                    },
                    modifier = Modifier.width(128.dp)
                ) {
                    val buttonText = if (currentLanguage.value == "EN") "Light State" else "Işık Durumu"
                    Text(buttonText, color = Color.White)
                }
                Spacer(modifier = Modifier.height(1.dp))
                Button(onClick = {
                        textToSpeech.speak(lightIntensityPrompt + "${lightIntensity.toInt()} lux", TextToSpeech.QUEUE_FLUSH, null, "")
                    },
                    modifier = Modifier.width(128.dp)
                ) {
                    val buttonText = if (currentLanguage.value == "EN") "Light  Level" else "Işık Seviyesi"
                    Text(buttonText, color = Color.White)
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
                        trackColor = Color.Gray,
                        strokeWidth = 16.dp,
                        strokeCap = StrokeCap.Round,
                        modifier = Modifier.size(200.dp)
                    )
                    Text(
                        text = "${lightIntensity.toInt()} lux",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color(0xff6b6899),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        textAlign = TextAlign.Center
                    )
                }
                val backgroundColor = when {
                    lightIntensity < 1000 -> Color(0xff59cfc5)
                    lightIntensity < 10000 -> Color(0xffc7cf59)
                    else -> Color(0xffcf5959)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .background(color = backgroundColor, shape = RoundedCornerShape(10.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = lightState,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        color = Color.Black
                    )
                }
            }
        }

        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = if (currentLanguage.value == "EN") "made by Enes Varol" else "Enes Varol tarafından yapıldı",
                fontSize = 12.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(bottom = 8.dp, end = 8.dp)
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
    return lerp(start, stop, fraction)
}

fun getRecommendedLightLevel(roomIndex: Int): Int {
    return 1000 * (roomIndex + 1)
}
