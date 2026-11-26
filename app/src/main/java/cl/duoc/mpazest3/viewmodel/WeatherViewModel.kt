package cl.duoc.mpazest3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazest3.data.remote.ApiConfig
import cl.duoc.mpazest3.data.remote.RetrofitInstance
import cl.duoc.mpazest3.data.repository.WeatherRepository
import cl.duoc.mpazest3.data.repository.WeatherResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel de la API externa: expone el texto del clima para mostrarlo en la pantalla del estudiante
class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository(RetrofitInstance.weatherApi)

    // null = todavía no hay dato (o no hay conexión): la pantalla no muestra nada
    private val _weatherText = MutableStateFlow<String?>(null)
    val weatherText: StateFlow<String?> = _weatherText.asStateFlow()

    fun loadWeather() {
        viewModelScope.launch {
            val result = repository.getCurrentWeather(
                ApiConfig.SCHOOL_CITY,
                ApiConfig.SCHOOL_LATITUDE,
                ApiConfig.SCHOOL_LONGITUDE
            )
            _weatherText.value = when (result) {
                is WeatherResult.Success -> result.message
                is WeatherResult.Error -> null
            }
        }
    }
}
