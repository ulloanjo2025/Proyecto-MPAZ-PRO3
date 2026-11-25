package cl.duoc.mpazest3.model

import kotlin.math.roundToInt
// Desarrollado por: Sebastián Maquera
// Una respuesta de un estudiante, ya asociada al OA de su pregunta
data class QuestionResult(
    val objectiveCode: String,
    val isCorrect: Boolean
)

// Desempeño de un estudiante en un OA (sumando todos sus intentos)
data class ObjectivePerformance(
    val objectiveCode: String,
    val correct: Int,
    val total: Int
) {
    val percent: Int
        get() = if (total == 0) 0 else ((correct * 100.0) / total).roundToInt()

    // Misma regla que el quiz: 2 de cada 3 respuestas correctas
    val isStrength: Boolean
        get() = QuizRules.isApproved(correct, total)
}

// Reglas del informe de desempeño (sin pantallas, así se pueden probar sin emulador)
object ReportRules {

    // Cada línea guardada tiene la forma: • P1 [MA03 OA 08]: Correcta
    private val detailPattern = Regex("""P(\d+) \[([A-Z]{2}\d{2} OA \d{2})\]: (Correcta|Incorrecta)""")

    // Lee el detalle de un intento. Las líneas con formato antiguo (sin OA) se ignoran.
    fun parseDetails(details: String): List<QuestionResult> {
        return QuizRules.splitDetails(details).mapNotNull { line ->
            val match = detailPattern.find(line) ?: return@mapNotNull null
            QuestionResult(
                objectiveCode = match.groupValues[2],
                isCorrect = match.groupValues[3] == "Correcta"
            )
        }
    }

    // Suma las respuestas de todos los intentos y las agrupa por OA
    fun performanceByObjective(allDetails: List<String>): List<ObjectivePerformance> {
        val results = allDetails.flatMap { parseDetails(it) }
        return results
            .groupBy { it.objectiveCode }
            .map { (code, list) ->
                ObjectivePerformance(
                    objectiveCode = code,
                    correct = list.count { it.isCorrect },
                    total = list.size
                )
            }
            .sortedBy { it.objectiveCode }
    }

    // Texto simple del informe para compartirlo (no incluye diagnósticos ni decisiones automáticas)
    fun buildReportText(
        studentName: String,
        issuedAt: String,
        validationStatus: String,
        performances: List<ObjectivePerformance>,
        titles: Map<String, String>
    ): String {
        fun label(p: ObjectivePerformance): String {
            val title = titles[p.objectiveCode]
            return if (title.isNullOrBlank()) p.objectiveCode else p.objectiveCode + " (" + title + ")"
        }

        val lines = mutableListOf<String>()
        lines.add("INFORME DE DESEMPEÑO - MPAZ PRO 3.º")
        lines.add("Estudiante: " + studentName)
        lines.add("Fecha de emisión: " + issuedAt)
        lines.add("Estado de validación: " + validationStatus)
        lines.add("")
        lines.add("Rendimiento por Objetivo de Aprendizaje:")
        if (performances.isEmpty()) {
            lines.add("- Sin respuestas asociadas a un OA todavía.")
        }
        performances.forEach {
            lines.add("- " + label(it) + ": " + it.correct + " de " + it.total + " correctas (" + it.percent + "%)")
        }
        lines.add("")
        lines.add("Fortalezas:")
        val strengths = performances.filter { it.isStrength }
        if (strengths.isEmpty()) {
            lines.add("- Ninguna identificada todavía.")
        }
        strengths.forEach { lines.add("- " + label(it)) }
        lines.add("")
        lines.add("Aspectos a reforzar:")
        val toReinforce = performances.filter { !it.isStrength }
        if (toReinforce.isEmpty()) {
            lines.add("- Ninguno identificado.")
        }
        toReinforce.forEach { lines.add("- " + label(it)) }
        lines.add("")
        lines.add("Nota: informe reservado de apoyo docente. No es un diagnóstico ni una decisión académica automática y debe ser validado por el docente.")
        return lines.joinToString("\n")
    }
}
