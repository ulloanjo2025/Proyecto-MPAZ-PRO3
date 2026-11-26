package cl.duoc.mpazest3

import cl.duoc.mpazest3.data.remote.RetrofitInstance
import cl.duoc.mpazest3.data.remote.WeatherApiService
import cl.duoc.mpazest3.data.repository.WeatherRepository
import cl.duoc.mpazest3.data.repository.WeatherResult
import cl.duoc.mpazest3.viewmodel.WeatherViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelSpec : FunSpec({

    beforeSpec {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkObject(RetrofitInstance)
        every { RetrofitInstance.weatherApi } returns mockk<WeatherApiService>(relaxed = true)
        mockkConstructor(WeatherRepository::class)
    }

    afterSpec {
        Dispatchers.resetMain()
        unmockkAll()
    }

    test("actualiza weatherText cuando WeatherRepository devuelve Success") {
        coEvery {
            anyConstructed<WeatherRepository>().getCurrentWeather(any(), any(), any())
        } returns WeatherResult.Success("San Bernardo: 22°C, Despejado")

        val viewModel = WeatherViewModel()
        viewModel.loadWeather()

        viewModel.weatherText.value shouldBe "San Bernardo: 22°C, Despejado"
    }

    test("deja weatherText en null cuando WeatherRepository devuelve Error") {
        coEvery {
            anyConstructed<WeatherRepository>().getCurrentWeather(any(), any(), any())
        } returns WeatherResult.Error("Sin conexión")

        val viewModel = WeatherViewModel()
        viewModel.loadWeather()

        viewModel.weatherText.value.shouldBeNull()
    }
})