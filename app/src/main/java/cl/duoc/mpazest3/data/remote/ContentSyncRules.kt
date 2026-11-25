package cl.duoc.mpazest3.data.remote

import cl.duoc.mpazest3.data.local.entity.LearningObjectiveEntity
import cl.duoc.mpazest3.data.local.entity.QuestionEntity
import cl.duoc.mpazest3.model.ContentStatus
// Desarrollado por: Jonathan Ulloa
// Reglas de la sincronización (sin red ni base de datos, así se pueden probar sin emulador)
object ContentSyncRules {

    // Decide en qué estado queda un OA después de sincronizar:
    // - OA nuevo: PENDIENTE (UTP debe revisarlo)
    // - OA sin cambios: conserva lo que UTP decidió (AUTORIZADO, RECHAZADO o PENDIENTE)
    // - OA con cambios en su material o sus preguntas: vuelve a PENDIENTE (UTP debe revisarlo otra vez)
    fun resolveStatus(
        old: LearningObjectiveEntity?,
        oldQuestions: List<QuestionEntity>,
        incoming: LearningObjectiveEntity,
        incomingQuestions: List<QuestionEntity>
    ): String {
        if (old == null) return ContentStatus.PENDIENTE

        val sameObjective = old.copy(status = incoming.status) == incoming
        val sameQuestions = oldQuestions.sortedBy { it.id } == incomingQuestions.sortedBy { it.id }

        return if (sameObjective && sameQuestions) old.status else ContentStatus.PENDIENTE
    }
}
