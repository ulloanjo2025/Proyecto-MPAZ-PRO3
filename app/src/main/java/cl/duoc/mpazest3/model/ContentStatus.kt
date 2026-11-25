package cl.duoc.mpazest3.model
// Desarrollado por: Sebastián Maquera
// Estado de autorización de un contenido (lo decide el perfil UTP).
// Si cambias estos textos, cambia también los 'AUTORIZADO' escritos en ContentDao.
object ContentStatus {
    const val PENDIENTE = "PENDIENTE"
    const val AUTORIZADO = "AUTORIZADO"
    const val RECHAZADO = "RECHAZADO"
}

// Nivel de dificultad de una pregunta.
object Difficulty {
    const val BASICO = 1
    const val INTERMEDIO = 2
    const val AVANZADO = 3

    fun label(level: Int): String = when (level) {
        BASICO -> "Básico"
        INTERMEDIO -> "Intermedio"
        else -> "Avanzado"
    }
}
