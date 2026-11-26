package cl.duoc.mpazest3.data.remote

import retrofit2.http.GET

// Define los endpoints HTTP del microservicio de contenido (ms-contenido)
interface ContentApiService {

    // GET http://10.0.2.2:8081/api/contenido: entrega los OA y las preguntas
    @GET("api/contenido")
    suspend fun getContent(): ContentResponseDto
}
