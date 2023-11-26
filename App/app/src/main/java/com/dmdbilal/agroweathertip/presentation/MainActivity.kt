package com.dmdbilal.agroweathertip.presentation

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dmdbilal.agroweathertip.R
import com.dmdbilal.agroweathertip.data.LocationTracker
import com.dmdbilal.agroweathertip.data.remote.RetrofitClient
import com.dmdbilal.agroweathertip.domain.CropData
import com.dmdbilal.agroweathertip.ui.theme.*
import com.google.android.gms.location.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.*

class MainActivity : ComponentActivity() {
    private val LOCATION_PERMISSION_REQ_CODE = 1000
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: CropViewModel

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgroWeatherTipTheme {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                val navController = rememberNavController()
                viewModel = ViewModelProvider(this)[CropViewModel::class.java]
                val locationTracker = LocationTracker(this, fusedLocationClient, viewModel)
                locationTracker.getCurrentLocation()
                GlobalScope.launch {
                    getWeatherInfo(viewModel)
                }

                NavHost(navController = navController, startDestination = "HomeScreen") {
                    composable("HomeScreen") {
                        HomeScreen(navigation = navController)
                    }
                    composable("GetCropScreen") {
                        GetCropScreen(navigation = navController, viewModel)
                    }
                    composable("HelpScreen") {
                        HelpScreen(navigation = navController)
                    }
                    composable("CropScreen") {
                        CropScreen(navigation = navController, viewModel)
                    }
                    composable("TopCrops") {
                        TopCrops(navigation = navController, viewModel)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQ_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // permission granted
                } else {
                    // permission denied
                    Toast.makeText(
                        this, "You need to grant permission to access location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

suspend fun getWeatherInfo(viewModel: CropViewModel) {
    val apiService = RetrofitClient.create()
    val response = apiService.getForecast(52.52, 13.41, "temperature_2m,relative_humidity_2m")
    val temp = response.current.temperature_2m
    val humd = response.current.relative_humidity_2m
    viewModel.temperature = temp.toFloat()
    viewModel.humidity = humd.toFloat()
}

@Composable
fun HomeScreen(navigation: NavController) {
    Column(
        Modifier
            .fillMaxSize()
            .background(TealGreen),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box {
            Box(
                Modifier
                    .size(470.dp)
                    .fillMaxHeight(05f)
                    .clip(RoundedCornerShape(bottomStart = 53.dp, bottomEnd = 53.dp))
                    .background(RoseWhite)
            )

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FloatingActionButton(
                        onClick = { /*TODO*/ },
                        backgroundColor = Color.White
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile),
                            contentDescription = "Profile"
                        )
                    }

                    FloatingActionButton(
                        onClick = { /*TODO*/ },
                        backgroundColor = Color.White
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_languages),
                            contentDescription = "Languages"
                        )
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.farmer),
                    contentDescription = "",
                    Modifier.size(300.dp)
                )
            }
        }

        Text(
            text = "\"If the farmer is rich, then so is the nation\"",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            softWrap = true
        )

        Button(
            modifier = Modifier.padding(bottom = 50.dp),
            onClick = { navigation.navigate("GetCropScreen") },
            colors = ButtonDefaults.buttonColors(Wattle)
        ) {
            Text(text = "Get Crops", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        }
    }
}

@Composable
fun GetCropScreen(
    navigation: NavController,
    viewModel: CropViewModel
) {
    var pH by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .background(RoseWhite),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navigation.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Go back to homescreen",
                    tint = TealGreen
                )
            }
            Text(text = "Get Crop", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        }

        Column {
            TextField(
                value = viewModel.pH.toString(),
                onValueChange = {viewModel.pH = it.toFloat()},
                label = { Text(text = "Enter pH of the soil") },
                colors = TextFieldDefaults.textFieldColors(
                    unfocusedIndicatorColor = TealGreen,
                    focusedIndicatorColor = TealGreen
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navigation.navigate("HelpScreen") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_help),
                        contentDescription = "How to find pH",
                        tint = TealGreen
                    )
                }
                Text(text = "How to find pH ?", color = TealGreen, fontSize = 20.sp)
            }

            TextField(
                value = viewModel.rainfall.toString(),
                onValueChange = {viewModel.rainfall = it.toFloat()},
                label = { Text(text = "Enter rainfall") },
                colors = TextFieldDefaults.textFieldColors(
                    unfocusedIndicatorColor = TealGreen,
                    focusedIndicatorColor = TealGreen
                )
            )
        }

        Button(
            onClick = {
                val value = viewModel.getLocation()
                viewModel.getCropRecommendations()
                if (viewModel.crops.value.isNotEmpty()) {
                    navigation.navigate("CropScreen")
                }
                Log.d("MainActivity", "Calling viewmodel function.")
            },
            colors = ButtonDefaults.buttonColors(Wattle),
            modifier = Modifier.padding(50.dp)
        ) {
            Text(text = "Get", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun HelpScreen(navigation: NavController) {
    Column(
        Modifier
            .fillMaxSize()
            .background(RoseWhite),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navigation.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Go back to get crop screen",
                    tint = TealGreen
                )
            }
            Text(text = "pH using pH strip", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Column {
                Text(text = "Guidelines", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                
                Spacer(modifier = Modifier.padding(16.dp))
                
                Text(
                    text = "1. Collect a small soil sample.\n\n" +
                            "2. Mix the soil sample with water in a clean container.\n\n" +
                            "3. Dip the pH strip into the soil-water mixture.\n\n" +
                            "4. Wait for a few seconds for the color to develop.\n\n" +
                            "5. Compare the strip's color with the  provided pH color chart.\n\n" +
                            "6. Identify the pH value corresponding to the color match.\n\n" +
                            "7. Repeat the process for multiple soil samples to ensure accuracy.",
                    style = LocalTextStyle.current.merge(
                        TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            ),
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.None
                            )
                        )
                    ),
                    color = TealGreen,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

        }
    }
}

@Composable
fun CropScreen(navigation: NavController, viewModel: CropViewModel) {
    val crops by viewModel.crops.collectAsState()
    Column(
        Modifier
            .fillMaxSize()
            .background(RoseWhite),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), Arrangement.Start) {
            IconButton(onClick = { navigation.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Go back to get crop screen",
                    tint = TealGreen
                )
            }
        }

//        Row(Modifier.fillMaxWidth(), Arrangement.Center, Alignment.CenterVertically) {
//            Icon(
//                imageVector = Icons.Default.Place,
//                contentDescription = "Location Icon",
//                tint = TealGreen
//            )
//            Text(text = "Location", fontSize = 20.sp, color = TealGreen)
//        }

        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.crop_beans),
                contentDescription = "crop",
                Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.padding(16.dp))
            Text(text= if (crops.isEmpty()) " " else crops[0].crop, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.ic_temperature),
                    contentDescription = "Location Icon",
                    tint = TealGreen
                )
                Text(text = "${viewModel.temperature}°C", fontSize = 18.sp, color = TealGreen)
            }

            Row {
                Icon(
                    painter = painterResource(id = R.drawable.ic_waterdrop),
                    contentDescription = "Location Icon",
                    tint = TealGreen
                )
                Text(text = "${viewModel.humidity}%rh", fontSize = 18.sp, color = TealGreen)
            }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = { navigation.navigate("TopCrops") }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_swipeup),
                    contentDescription = "Swipe Up",
                    tint = DustyGrey
                )
            }

        }
    }
}

@Composable
fun TopCrops(navigation: NavController, viewModel: CropViewModel) {
    val crops by viewModel.crops.collectAsState()
    Column {
        for (crop in crops) {
            Crop(crop, viewModel.temperature, viewModel.humidity)
        }
    }
}

@Composable
fun Crop(data: CropData, temp: Float, humd: Float) {
    Row(
        Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.crop_beans),
            contentDescription = "crop",
            Modifier
                .size(30.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.padding(16.dp))
        Column{
            Text(text=data.crop, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Column {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.ic_temperature),
                    contentDescription = "Location Icon",
                    tint = TealGreen
                )
                Text(text = "$temp°C", fontSize = 18.sp, color = TealGreen)
            }

            Row {
                Icon(
                    painter = painterResource(id = R.drawable.ic_waterdrop),
                    contentDescription = "Location Icon",
                    tint = TealGreen
                )
                Text(text = "$humd%rh", fontSize = 18.sp, color = TealGreen)
            }
        }
    }
    Divider(thickness = 1.5.dp)
}
