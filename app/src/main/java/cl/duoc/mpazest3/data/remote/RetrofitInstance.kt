package cl.duoc.mpazest3.data.remote

import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton que contiene la configuración de Retrofit (se crea una sola vez).
// Hay tres servicios distintos: dos microservicios propios y una API externa.
object RetrofitInstance {

    private val client: OkHttpClient by lazy {
        // Si el servicio no responde en 10 segundos, la app sigue con los datos guardados
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    private fun retrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // Conversor JSON
            .build()
    }

    // Microservicio de contenido (OA y preguntas)
    val api: ContentApiService by lazy {
        retrofit(ApiConfig.BASE_URL).create(ContentApiService::class.java)
    }

    // Microservicio de seguimiento (CRUD de estudiantes e intentos)
    val studentApi: StudentApiService by lazy {
        retrofit(ApiConfig.TRACKING_BASE_URL).create(StudentApiService::class.java)
    }

    // API externa de clima (no interfiere con los datos propios)
    val weatherApi: WeatherApiService by lazy {
        retrofit(ApiConfig.WEATHER_BASE_URL).create(WeatherApiService::class.java)
    }
    // API externa PokéAPI (recompensa motivacional al terminar el quiz)
    val pokeApi: PokeApiService by lazy {
        retrofit("https://pokeapi.co/").create(PokeApiService::class.java)
    }
}
