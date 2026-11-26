// Desarrollado por: Camilo Alarcón (data/local/entity)
package cl.duoc.mpazest3.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla oficial de alumnos registrados por el docente
@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,     // Ej: "JP01"
    val fullName: String,     // Ej: "Juan Pérez"
    val pin: String           // Ej: "1234"
)