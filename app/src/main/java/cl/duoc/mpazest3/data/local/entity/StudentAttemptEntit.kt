// Desarrollado por: Camilo Alarcón (data/local/entity)
package cl.duoc.mpazest3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_attempts")
data class StudentAttemptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val studentName: String,
    val subjectId: String,
    val attemptNumber: Int = 1,
    val score: Int = 0,
    val totalQuestions: Int = 6,
    val isCorrect: Boolean,
    val detailsSummary: String = "" // Almacena el detalle de preguntas acertadas/falladas
)