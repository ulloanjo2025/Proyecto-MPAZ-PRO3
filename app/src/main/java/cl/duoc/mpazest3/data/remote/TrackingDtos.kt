package cl.duoc.mpazest3.data.remote

import cl.duoc.mpazest3.data.local.entity.StudentEntity
import com.google.gson.annotations.SerializedName

// Así viajan los estudiantes y los intentos hacia y desde el microservicio de seguimiento (JSON)

data class StudentDto(
    val id: Long?,
    val username: String?,
    val fullName: String?,
    val pin: String?
)

data class StudentRequest(
    val username: String,
    val fullName: String,
    val pin: String
)

data class LoginRequest(
    val username: String,
    val pin: String
)

data class AttemptRequest(
    val studentName: String,
    val subjectId: String,
    val attemptNumber: Int,
    val score: Int,
    val totalQuestions: Int,
    @SerializedName("approved") val isCorrect: Boolean,
    val detailsSummary: String
)

// Convierte el estudiante que llega del microservicio en el que se guarda en Room.
// Si falta algún dato, devuelve null y se descarta.
fun StudentDto.toEntityOrNull(): StudentEntity? {
    val idValue = id ?: return null
    val user = username?.trim()
    val name = fullName?.trim()
    val pinValue = pin?.trim()
    if (user.isNullOrEmpty() || name.isNullOrEmpty() || pinValue.isNullOrEmpty()) return null
    return StudentEntity(id = idValue.toInt(), username = user, fullName = name, pin = pinValue)
}
