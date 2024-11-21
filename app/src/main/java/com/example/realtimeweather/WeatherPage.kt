package com.example.realtimeweather

import android.graphics.Paint.Align
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.realtimeweather.api.NetworkResponse
import com.example.realtimeweather.api.WeatherApi
import com.example.realtimeweather.api.WeatherModel
import kotlin.math.sin

@Composable
fun WeatherPage(viewModel: WeatherViewModel){

    var weatherResult = viewModel.weatherResult.observeAsState()
    var keyboardConroller = LocalSoftwareKeyboardController.current
    var city by remember {
        mutableStateOf(" ")
    }
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
            ){
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = city,
                onValueChange = {
                    city = it
                },
                label = {
                    Text("Search for any location")
                },
                singleLine = true
            )
            IconButton(
                onClick = {
                    keyboardConroller?.hide()
                    if (isNetworkAvailable(context)){
                        viewModel.getData(city)
                    }else{
                        Toast.makeText(context,"Internet access required",Toast.LENGTH_SHORT).show()
                    }
                }) {
                Icon(imageVector = Icons.Default.Search,
                    contentDescription = "search for any location"
                )
            }
        }
        when(val result = weatherResult.value){
            is NetworkResponse.Error ->{
                Text(result.message)
            }
            is NetworkResponse.Loading ->{
                CircularProgressIndicator()
            }
            is NetworkResponse.Success ->{
                WeatherDetails(result.data)
            }
            null ->{}
        }
    }
}

@Composable
fun WeatherDetails(data:WeatherModel){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location Icon",
                modifier = Modifier.size(40.dp ),
                tint = Color.Red
            )
            Text(data.location.name, fontSize = 30.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(data.location.country, fontSize = 18.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${data.current.temp_c} °C",
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        AsyncImage(
            model ="https:${data.current.condition.icon}".replace("64x64","128x128") ,
            contentDescription = "Condition",
            modifier = Modifier.size(160.dp)
        )
        Text(
            text = data.current.condition.text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        ElevatedCard() {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    WeatherKeyVal("Humidity",data.current.humidity)
                    WeatherKeyVal("Feel Like",data.current.feelslike_c)
                }
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    WeatherKeyVal("Wind Speed","${data.current.wind_kph} kph")
                    WeatherKeyVal("Wind Direction",data.current.wind_dir)
                }
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    WeatherKeyVal("Precipitation","${data.current.precip_mm} mm")
                    WeatherKeyVal("UV",data.current.uv)
                }
            }
        }
    }
}

@Composable
fun WeatherKeyVal(key:String,value:String){
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = key,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )
    }
}