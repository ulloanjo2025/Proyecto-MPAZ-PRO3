package cl.duoc.mpazest3

import cl.duoc.mpazest3.data.local.entity.LearningObjectiveEntity
import cl.duoc.mpazest3.data.local.entity.QuestionEntity
import cl.duoc.mpazest3.data.remote.ContentSyncRules
import cl.duoc.mpazest3.model.ContentStatus
import org.junit.Assert.assertEquals
import org.junit.Test

// Pruebas de la regla que protege la autorización de UTP al sincronizar: sin red ni emulador.
class ContentSyncRulesTest {

    private fun objetivo(summary: String = "Resumen", status: String = ContentStatus.PENDIENTE) =
        LearningObjectiveEntity("MA03 OA 08", "1", "Unidad", "Título", summary, null, null, status)

    private fun pregunta(id: Int = 1, statement: String = "¿Cuánto es 5 × 3?") =
        QuestionEntity(id, "MA03 OA 08", 1, statement, "8", "15", "12", 1, "Porque sí")

    @Test
    fun un_objetivo_nuevo_parte_pendiente() {
        val estado = ContentSyncRules.resolveStatus(null, emptyList(), objetivo(), listOf(pregunta()))
        assertEquals(ContentStatus.PENDIENTE, estado)
    }

    @Test
    fun un_objetivo_sin_cambios_conserva_la_autorizacion_de_UTP() {
        val estado = ContentSyncRules.resolveStatus(
            old = objetivo(status = ContentStatus.AUTORIZADO),
            oldQuestions = listOf(pregunta()),
            incoming = objetivo(),
            incomingQuestions = listOf(pregunta())
        )
        assertEquals(ContentStatus.AUTORIZADO, estado)
    }

    @Test
    fun un_objetivo_rechazado_sin_cambios_sigue_rechazado() {
        val estado = ContentSyncRules.resolveStatus(
            old = objetivo(status = ContentStatus.RECHAZADO),
            oldQuestions = listOf(pregunta()),
            incoming = objetivo(),
            incomingQuestions = listOf(pregunta())
        )
        assertEquals(ContentStatus.RECHAZADO, estado)
    }

    @Test
    fun si_cambia_el_resumen_vuelve_a_pendiente() {
        val estado = ContentSyncRules.resolveStatus(
            old = objetivo(status = ContentStatus.AUTORIZADO),
            oldQuestions = listOf(pregunta()),
            incoming = objetivo(summary = "Resumen nuevo"),
            incomingQuestions = listOf(pregunta())
        )
        assertEquals(ContentStatus.PENDIENTE, estado)
    }

    @Test
    fun si_cambia_una_pregunta_vuelve_a_pendiente() {
        val estado = ContentSyncRules.resolveStatus(
            old = objetivo(status = ContentStatus.AUTORIZADO),
            oldQuestions = listOf(pregunta()),
            incoming = objetivo(),
            incomingQuestions = listOf(pregunta(statement = "¿Cuánto es 6 × 3?"))
        )
        assertEquals(ContentStatus.PENDIENTE, estado)
    }
}
