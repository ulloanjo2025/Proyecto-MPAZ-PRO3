package cl.duoc.mpazest3.data.remote

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
// Desarrollado por: Jonathan Ulloa
// Singleton que contiene la configuración de Retrofit (se crea una sola vez)
object RetrofitInstance {

    val api: ContentApiService by lazy {
        // Si el servicio no responde en 10 segundos, la app sigue con el contenido guardado
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // Conversor JSON
            .build()
            .create(ContentApiService::class.java)
    }
}
