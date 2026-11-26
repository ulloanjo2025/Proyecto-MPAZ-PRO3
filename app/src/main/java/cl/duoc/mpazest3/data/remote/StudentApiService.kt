package cl.duoc.mpazest3.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// Endpoints del microservicio de seguimiento (ms-seguimiento).
// Cubre el CRUD completo de estudiantes: Create (POST), Read (GET), Update (PUT) y Delete (DELETE).
interface StudentApiService {

    @GET("api/estudiantes")
    suspend fun getStudents(): List<StudentDto>

    @POST("api/estudiantes")
    suspend fun createStudent(@Body student: StudentRequest): StudentDto

    @PUT("api/estudiantes/{id}")
    suspend fun updateStudent(@Path("id") id: Long, @Body student: StudentRequest): StudentDto

    @DELETE("api/estudiantes/{id}")
    suspend fun deleteStudent(@Path("id") id: Long): Response<Unit>

    @POST("api/estudiantes/login")
    suspend fun login(@Body credentials: LoginRequest): StudentDto

    // Intentos (evaluaciones) de los estudiantes
    @POST("api/intentos")
    suspend fun createAttempt(@Body attempt: AttemptRequest): Response<Unit>

    @DELETE("api/intentos")
    suspend fun deleteAttempts(
        @Query("student") student: String,
        @Query("subject") subject: String
    ): Response<Unit>
}
