package cl.duoc.mpazest3

import cl.duoc.mpazest3.data.remote.CurrentWeatherDto
import cl.duoc.mpazest3.data.remote.WeatherApiService
import cl.duoc.mpazest3.data.remote.WeatherResponseDto
import cl.duoc.mpazest3.data.repository.WeatherRepository
import cl.duoc.mpazest3.data.repository.WeatherResult
import cl.duoc.mpazest3.model.WeatherRules
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.mockk
import java.io.IOException
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

// Pruebas de la API externa de clima: la API se simula con MockK (no necesita internet).
class WeatherSpec : FunSpec({

    val api = mockk<WeatherApiService>()

    test("el código 0 se muestra como despejado y el 3 como nublado") {
        WeatherRules.describe(0) shouldContain "despejado"
        WeatherRules.describe(3) shouldContain "nublado"
    }

    test("los códigos de lluvia, tormenta y los desconocidos tienen su texto") {
        WeatherRules.describe(63) shouldContain "lluvia"
        WeatherRules.describe(95) shouldContain "tormenta"
        WeatherRules.describe(999) shouldContain "variable"
    }

    test("el mensaje incluye la ciudad, la temperatura redondeada y el clima") {
        val mensaje = WeatherRules.formatMessage("San Bernardo", 17.6, 0)

        mensaje shouldContain "San Bernardo"
        mensaje shouldContain "18 °C"
        mensaje shouldContain "despejado"
    }

    test("la API entrega datos y se arma el mensaje") {
        coEvery { api.getCurrentWeather(any(), any(), any()) } returns WeatherResponseDto(CurrentWeatherDto(17.6, 0))

        val resultado = WeatherRepository(api).getCurrentWeather("San Bernardo", -33.59, -70.70)

        resultado.shouldBeInstanceOf<WeatherResult.Success>().message shouldContain "18 °C"
    }

    test("si la API no entrega datos se informa el error") {
        coEvery { api.getCurrentWeather(any(), any(), any()) } returns WeatherResponseDto(null)

        val resultado = WeatherRepository(api).getCurrentWeather("San Bernardo", -33.59, -70.70)

        resultado.shouldBeInstanceOf<WeatherResult.Error>()
    }

    test("sin internet se informa que no hay conexión") {
        coEvery { api.getCurrentWeather(any(), any(), any()) } throws IOException("sin red")

        val resultado = WeatherRepository(api).getCurrentWeather("San Bernardo", -33.59, -70.70)

        resultado.shouldBeInstanceOf<WeatherResult.Error>().message shouldContain "Sin conexión"
    }

    test("un error del servidor se informa con su código") {
        coEvery { api.getCurrentWeather(any(), any(), any()) } throws
            HttpException(Response.error<Any>(500, "".toResponseBody()))

        val resultado = WeatherRepository(api).getCurrentWeather("San Bernardo", -33.59, -70.70)

        resultado.shouldBeInstanceOf<WeatherResult.Error>().message shouldContain "500"
    }
})
