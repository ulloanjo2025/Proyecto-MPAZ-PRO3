package cl.duoc.mpazest3.data.remote
// Desarrollado por: Jonathan Ulloa
// Así llega el contenido desde el servicio REST (JSON).
// Todos los campos aceptan null porque Gson deja null lo que falta en el JSON;
// ContentMapper revisa cada elemento antes de guardarlo.

data class ContentResponseDto(
    val version: Int?,
    val objectives: List<ObjectiveDto>?,
    val questions: List<QuestionDto>?
)

data class ObjectiveDto(
    val code: String?,
    val subjectId: String?,
    val unitName: String?,
    val title: String?,
    val summary: String?,
    val readingTitle: String?,
    val readingText: String?,
    // Se ignora: la autorización la decide UTP en la app, no el servicio
    val status: String?
)

data class QuestionDto(
    val id: Int?,
    val objectiveCode: String?,
    val difficulty: Int?,
    val statement: String?,
    val optionA: String?,
    val optionB: String?,
    val optionC: String?,
    val correctIndex: Int?,
    val explanation: String?
)
