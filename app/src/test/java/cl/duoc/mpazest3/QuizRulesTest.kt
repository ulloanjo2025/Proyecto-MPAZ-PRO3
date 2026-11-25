package cl.duoc.mpazest3

import cl.duoc.mpazest3.model.QuizRules
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Pruebas de las reglas del quiz: se ejecutan en el computador, sin emulador.
class QuizRulesTest {

    @Test
    fun con_6_preguntas_se_aprueba_desde_4_aciertos() {
        assertTrue(QuizRules.isApproved(4, 6))
        assertTrue(QuizRules.isApproved(6, 6))
        assertFalse(QuizRules.isApproved(3, 6))
    }

    @Test
    fun con_3_preguntas_se_aprueba_desde_2_aciertos() {
        assertTrue(QuizRules.isApproved(2, 3))
        assertFalse(QuizRules.isApproved(1, 3))
    }

    @Test
    fun sin_preguntas_no_se_aprueba() {
        assertFalse(QuizRules.isApproved(0, 0))
    }

    @Test
    fun el_orden_de_alternativas_siempre_contiene_0_1_y_2() {
        repeat(20) { semilla ->
            val orden = QuizRules.shuffledOptionOrder(Random(semilla))
            assertEquals(listOf(0, 1, 2), orden.sorted())
        }
    }

    @Test
    fun un_detalle_vacio_no_genera_lineas() {
        assertTrue(QuizRules.splitDetails("").isEmpty())
        assertTrue(QuizRules.splitDetails("   ").isEmpty())
    }

    @Test
    fun el_detalle_se_separa_por_lineas() {
        val detalle = "• P1 [MA03 OA 08]: Correcta\n• P2 [MA03 OA 08]: Incorrecta (explicación)"
        val lineas = QuizRules.splitDetails(detalle)
        assertEquals(2, lineas.size)
        assertEquals("• P1 [MA03 OA 08]: Correcta", lineas[0])
    }

    @Test
    fun unir_y_separar_el_detalle_devuelve_lo_mismo() {
        val original = listOf("línea 1", "línea 2", "línea 3")
        assertEquals(original, QuizRules.splitDetails(original.joinToString("\n")))
    }
}
