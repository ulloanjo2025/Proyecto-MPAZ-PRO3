package cl.duoc.mpazest3

import cl.duoc.mpazest3.data.local.seed.ContentSeed
import cl.duoc.mpazest3.model.ContentStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// Pruebas del contenido inicial: se ejecutan en el computador, sin emulador.
class ContentSeedTest {

    @Test
    fun hay_8_objetivos_y_24_preguntas() {
        assertEquals(8, ContentSeed.objectives.size)
        assertEquals(24, ContentSeed.questions.size)
    }

    @Test
    fun cada_asignatura_tiene_2_objetivos() {
        val porAsignatura = ContentSeed.objectives.groupBy { it.subjectId }
        assertEquals(4, porAsignatura.size)
        porAsignatura.values.forEach { assertEquals(2, it.size) }
    }

    @Test
    fun cada_objetivo_tiene_3_preguntas_de_dificultad_1_2_y_3() {
        ContentSeed.objectives.forEach { oa ->
            val dificultades = ContentSeed.questions
                .filter { it.objectiveCode == oa.code }
                .map { it.difficulty }
                .sorted()
            assertEquals(listOf(1, 2, 3), dificultades)
        }
    }

    @Test
    fun toda_pregunta_pertenece_a_un_objetivo_existente() {
        val codigos = ContentSeed.objectives.map { it.code }.toSet()
        ContentSeed.questions.forEach { assertTrue(it.objectiveCode in codigos) }
    }

    @Test
    fun la_respuesta_correcta_es_valida_y_las_alternativas_son_distintas() {
        ContentSeed.questions.forEach { q ->
            assertTrue(q.correctIndex in 0..2)
            assertEquals(3, setOf(q.optionA, q.optionB, q.optionC).size)
            assertTrue(q.explanation.isNotBlank())
        }
    }

    @Test
    fun los_objetivos_de_lenguaje_tienen_texto_de_lectura() {
        ContentSeed.objectives
            .filter { it.subjectId == "2" }
            .forEach { assertTrue(!it.readingText.isNullOrBlank()) }
    }

    @Test
    fun el_contenido_parte_pendiente_de_autorizacion() {
        ContentSeed.objectives.forEach { assertEquals(ContentStatus.PENDIENTE, it.status) }
        // ContentDao escribe 'AUTORIZADO' como texto fijo: este valor debe coincidir
        assertEquals("AUTORIZADO", ContentStatus.AUTORIZADO)
    }
}
