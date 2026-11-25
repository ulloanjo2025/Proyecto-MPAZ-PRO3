package cl.duoc.mpazest3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
// Desarrollado por: Camilo Alarcón
// Validación del informe de un estudiante por parte del docente.
@Entity(tableName = "report_validations")
data class ReportValidationEntity(
    @PrimaryKey
    val studentName: String,        // Nombre del estudiante en minúsculas
    val validatedBy: String,        // Nombre del docente que validó
    val validatedAt: Long,          // Fecha y hora de la validación (milisegundos)
    val attemptsAtValidation: Int   // Cuántas evaluaciones tenía el estudiante al validar
)
