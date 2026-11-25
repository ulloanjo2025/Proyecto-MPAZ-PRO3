package cl.duoc.mpazest3.data.remote
// Desarrollado por: Jonathan Ulloa
import retrofit2.http.GET

// Define los endpoints HTTP del servicio REST
interface ContentApiService {

    // GET {BASE_URL}contenido.json: entrega los OA y las preguntas
    @GET("contenido.json")
    suspend fun getContent(): ContentResponseDto
}
