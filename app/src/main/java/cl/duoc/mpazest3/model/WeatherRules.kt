package cl.duoc.mpazest3.model

import kotlin.math.roundToInt

// Reglas para mostrar el clima (sin red, así se pueden probar sin emulador)
object WeatherRules {

    // Convierte el código de clima (estándar de la OMM) en un texto para niños
    fun describe(code: Int): String {
        return when (code) {
            0 -> "☀️ despejado"
            1, 2 -> "🌤️ poco nublado"
            3 -> "☁️ nublado"
            45, 48 -> "🌫️ con niebla"
            in 51..57 -> "🌦️ con llovizna"
            in 61..67 -> "🌧️ con lluvia"
            in 71..77 -> "❄️ con nieve"
            in 80..82 -> "🌧️ con chubascos"
            in 95..99 -> "⛈️ con tormenta"
            else -> "🌡️ clima variable"
        }
    }

    fun formatMessage(city: String, temperature: Double, code: Int): String {
        return "Hoy en " + city + ": " + temperature.roundToInt() + " °C, " + describe(code)
    }
}
