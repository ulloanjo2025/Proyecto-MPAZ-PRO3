package cl.duoc.mpazest3.model

import kotlin.random.Random
// Desarrollado por: Sebastián Maquera
// Reglas del quiz que no dependen de la pantalla (así se pueden probar sin emulador).
object QuizRules {

    // Aprueba quien responde bien al menos 2 de cada 3 preguntas (con 6 preguntas: 4 o más)
    fun isApproved(correctAnswers: Int, totalQuestions: Int): Boolean {
        return totalQuestions > 0 && correctAnswers * 3 >= totalQuestions * 2
    }

    // Convierte el detalle guardado (una línea por pregunta) en una lista de líneas
    fun splitDetails(details: String): List<String> {
        return if (details.isBlank()) emptyList() else details.split("\n")
    }

    // Orden en que se muestran las alternativas A, B y C (0, 1 y 2) de una pregunta
    fun shuffledOptionOrder(random: Random = Random.Default): List<Int> {
        return listOf(0, 1, 2).shuffled(random)
    }
}
