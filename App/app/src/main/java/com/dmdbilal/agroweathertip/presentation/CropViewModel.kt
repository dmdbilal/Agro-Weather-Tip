package com.dmdbilal.agroweathertip.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import com.dmdbilal.agroweathertip.data.CropJsonTask
import com.dmdbilal.agroweathertip.domain.CropData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CropViewModel : ViewModel() {
    var temperature = 40.0f
    var humidity = 30.0f
    var pH = 7.0f
    var rainfall = 80.0f

    private val _crops = MutableStateFlow<List<CropData>>(emptyList())
    val crops: StateFlow<List<CropData>> get() = _crops

    private val _location = MutableStateFlow<List<Double>>(emptyList())
    val location: StateFlow<List<Double>> get() = _location

    fun setLocation(value: List<Double>) {
        _location.value = value
    }

    fun getLocation() = location.value

    fun getCropRecommendations() {
        CropJsonTask(temperature, humidity, pH, rainfall) { result ->
            updateCropsLiveData(result)
        }.execute()
    }

    private fun updateCropsLiveData(jsonResponse: String?) {
        val gson = Gson()

        try {
            if (jsonResponse.isNullOrEmpty()) {
                println("JSON response is null or empty.")
            } else {
                // Parse JSON into a list of CropData objects
                val cropDataList =
                    gson.fromJson(jsonResponse, Array<CropData>::class.java).toList()

                // Extract crops and probabilities into separate lists
                val cropsLs = cropDataList.map { it.crop }
                val probabilities = cropDataList.map { it.probability }

                // Create a new list of CropData objects
                val cropsList = mutableListOf<CropData>()

                // Print the result
                for (i in cropsLs.indices) {
                    val crop = CropData(cropsLs[i], probabilities[i])
                    cropsList.add(crop)
                }

                // Update LiveData with the new list
                _crops.value = cropsList
                Log.d("MainActivity", "Crops data saved in viewmodel.")
                for (i in cropsList) {
                    println(i)
                }
            }
        } catch (e: JsonSyntaxException) {
            println("Error parsing JSON: ${e.message}")
        }
    }
}
