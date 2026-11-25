package cl.duoc.mpazest3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.duoc.mpazest3.model.ContentStatus
// Desarrollado por: Camilo Alarcón
// Un Objetivo de Aprendizaje (OA) del Currículum Nacional con su material de estudio.
@Entity(tableName = "learning_objectives")
data class LearningObjectiveEntity(
    @PrimaryKey
    val code: String,              // Ej: "MA03 OA 08"
    val subjectId: String,         // Igual que Subject.id: "1" Matemática, "2" Lenguaje, "3" Ciencias, "4" Historia
    val unitName: String,          // Ej: "Tablas de multiplicar y cálculo"
    val title: String,             // Ej: "Tablas de multiplicar"
    val summary: String,           // Resumen de estudio (también se lee en voz alta)
    val readingTitle: String? = null,  // Solo Lenguaje: título del texto de lectura
    val readingText: String? = null,   // Solo Lenguaje: texto de lectura
    val status: String = ContentStatus.PENDIENTE  // PENDIENTE, AUTORIZADO o RECHAZADO
)
