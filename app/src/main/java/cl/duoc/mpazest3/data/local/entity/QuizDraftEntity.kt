package cl.duoc.mpazest3.data.local.entity

import androidx.room.Entity
// Desarrollado por: Camilo Alarcón
// Avance de un quiz sin terminar: un borrador por estudiante y por asignatura.
// Se guarda en Room, así no se pierde si la app se cierra.
@Entity(
    tableName = "quiz_drafts",
    primaryKeys = ["studentName", "subjectId"]
)
data class QuizDraftEntity(
    val studentName: String,      // Nombre del estudiante en minúsculas (así no se duplica por mayúsculas)
    val subjectId: String,        // Asignatura (el mismo texto que usa student_attempts)
    val questionIndex: Int,       // Pregunta en la que va (0 = primera)
    val correctAnswers: Int,      // Respuestas correctas hasta ahora
    val detailsLog: String        // Detalle de las preguntas ya respondidas (una línea por pregunta)
)
