package cl.duoc.mpazest3.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
// Desarrollado por: Camilo Alarcón
// Una pregunta con 3 alternativas, vinculada a un OA y a un nivel de dificultad.
@Entity(
    tableName = "questions",
    indices = [Index("objectiveCode")]
)
data class QuestionEntity(
    @PrimaryKey
    val id: Int,                   // 1 a 24 (fijo, así la carga inicial no duplica datos)
    val objectiveCode: String,     // OA al que pertenece, ej: "MA03 OA 08"
    val difficulty: Int,           // 1 = básico, 2 = intermedio, 3 = avanzado
    val statement: String,         // Enunciado
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val correctIndex: Int,         // 0 = A, 1 = B, 2 = C
    val explanation: String        // Explicación que ve (y escucha) el estudiante
)
