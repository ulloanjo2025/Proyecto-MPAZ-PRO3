package cl.duoc.mpazest3.data.repository

import cl.duoc.mpazest3.data.remote.WeatherApiService
import cl.duoc.mpazest3.model.WeatherRules
import com.google.gson.JsonParseException
import java.io.IOException
import retrofit2.HttpException

// Resultado de consultar el clima
sealed class WeatherResult {
    data class Success(val message: String) : WeatherResult()
    data class Error(val message: String) : WeatherResult()
}

// Consulta la API externa de clima (Open-Meteo). Es independiente de los datos propios de la app.
class WeatherRepository(
    private val api: WeatherApiService
) {

    suspend fun getCurrentWeather(city: String, latitude: Double, longitude: Double): WeatherResult {
        return try {
            val current = api.getCurrentWeather(latitude, longitude, "temperature_2m,weather_code").current
            val temperature = current?.temperature
            val code = current?.weatherCode
            if (temperature == null || code == null) {
                WeatherResult.Error("La API de clima no entregó datos.")
            } else {
                WeatherResult.Success(WeatherRules.formatMessage(city, temperature, code))
            }
        } catch (e: HttpException) {
            WeatherResult.Error("La API de clima respondió con error (código " + e.code() + ").")
        } catch (e: IOException) {
            WeatherResult.Error("Sin conexión para consultar el clima.")
        } catch (e: JsonParseException) {
            WeatherResult.Error("La respuesta de la API de clima no tiene el formato esperado.")
        }
    }
}
