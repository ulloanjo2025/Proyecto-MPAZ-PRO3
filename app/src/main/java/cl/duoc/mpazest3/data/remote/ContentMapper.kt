package cl.duoc.mpazest3.data.remote

import cl.duoc.mpazest3.data.local.entity.LearningObjectiveEntity
import cl.duoc.mpazest3.data.local.entity.QuestionEntity
import cl.duoc.mpazest3.model.ContentStatus
// Desarrollado por: Jonathan Ulloa
// Convierte lo que llega del servicio (DTO) en lo que se guarda en Room (Entity).
// Si a un elemento le falta un dato o tiene un valor inválido, devuelve null y se descarta.

private val validSubjectIds = setOf("1", "2", "3", "4")

fun ObjectiveDto.toEntityOrNull(): LearningObjectiveEntity? {
    val codeValue = code?.trim()
    val subject = subjectId?.trim()
    val unit = unitName?.trim()
    val titleValue = title?.trim()
    val summaryValue = summary?.trim()

    if (codeValue.isNullOrEmpty() || subject == null || subject !in validSubjectIds) return null
    if (unit.isNullOrEmpty() || titleValue.isNullOrEmpty() || summaryValue.isNullOrEmpty()) return null

    return LearningObjectiveEntity(
        code = codeValue,
        subjectId = subject,
        unitName = unit,
        title = titleValue,
        summary = summaryValue,
        readingTitle = readingTitle?.trim()?.ifEmpty { null },
        readingText = readingText?.trim()?.ifEmpty { null },
        // Todo contenido que llega del servicio parte pendiente: UTP decide si lo autoriza
        status = ContentStatus.PENDIENTE
    )
}

fun QuestionDto.toEntityOrNull(): QuestionEntity? {
    val idValue = id ?: return null
    val difficultyValue = difficulty ?: return null
    val correctValue = correctIndex ?: return null
    val objective = objectiveCode?.trim()
    val statementValue = statement?.trim()
    val a = optionA?.trim()
    val b = optionB?.trim()
    val c = optionC?.trim()
    val explanationValue = explanation?.trim()

    if (objective.isNullOrEmpty() || statementValue.isNullOrEmpty() || explanationValue.isNullOrEmpty()) return null
    if (a.isNullOrEmpty() || b.isNullOrEmpty() || c.isNullOrEmpty()) return null
    if (difficultyValue !in 1..3 || correctValue !in 0..2) return null
    if (setOf(a, b, c).size != 3) return null

    return QuestionEntity(
        id = idValue,
        objectiveCode = objective,
        difficulty = difficultyValue,
        statement = statementValue,
        optionA = a,
        optionB = b,
        optionC = c,
        correctIndex = correctValue,
        explanation = explanationValue
    )
}
