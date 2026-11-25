package cl.duoc.mpazest3

import cl.duoc.mpazest3.model.ObjectivePerformance
import cl.duoc.mpazest3.model.ReportRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Pruebas de las reglas del informe de desempeño: se ejecutan en el computador, sin emulador.
class ReportRulesTest {

    @Test
    fun lee_el_codigo_de_OA_y_si_la_respuesta_fue_correcta() {
        val detalle = "• P1 [MA03 OA 08]: Correcta\n• P2 [MA03 OA 04]: Incorrecta (explicación)"
        val resultados = ReportRules.parseDetails(detalle)
        assertEquals(2, resultados.size)
        assertEquals("MA03 OA 08", resultados[0].objectiveCode)
        assertTrue(resultados[0].isCorrect)
        assertEquals("MA03 OA 04", resultados[1].objectiveCode)
        assertFalse(resultados[1].isCorrect)
    }

    @Test
    fun ignora_las_lineas_con_formato_antiguo() {
        assertTrue(ReportRules.parseDetails("• P1: Correcta").isEmpty())
        assertTrue(ReportRules.parseDetails("").isEmpty())
    }

    @Test
    fun suma_los_intentos_de_un_mismo_objetivo() {
        val intento1 = "• P1 [MA03 OA 08]: Correcta\n• P2 [MA03 OA 08]: Incorrecta (x)\n• P3 [MA03 OA 08]: Correcta"
        val intento2 = "• P1 [MA03 OA 08]: Correcta\n• P2 [MA03 OA 08]: Correcta\n• P3 [MA03 OA 08]: Correcta"
        val desempeno = ReportRules.performanceByObjective(listOf(intento1, intento2))
        assertEquals(1, desempeno.size)
        assertEquals(5, desempeno[0].correct)
        assertEquals(6, desempeno[0].total)
        assertEquals(83, desempeno[0].percent)
    }

    @Test
    fun dos_de_tres_es_fortaleza_y_uno_de_tres_es_refuerzo() {
        val fortaleza = ObjectivePerformance("MA03 OA 08", 2, 3)
        assertEquals(67, fortaleza.percent)
        assertTrue(fortaleza.isStrength)

        val refuerzo = ObjectivePerformance("LE03 OA 04", 1, 3)
        assertEquals(33, refuerzo.percent)
        assertFalse(refuerzo.isStrength)
    }

    @Test
    fun el_texto_del_informe_trae_las_secciones_y_aclara_que_no_es_un_diagnostico() {
        val texto = ReportRules.buildReportText(
            studentName = "Juan Pérez",
            issuedAt = "19-11-2026 10:00",
            validationStatus = "Pendiente de validación docente",
            performances = listOf(
                ObjectivePerformance("MA03 OA 08", 3, 3),
                ObjectivePerformance("LE03 OA 04", 1, 3)
            ),
            titles = mapOf("MA03 OA 08" to "Tablas de multiplicar")
        )
        assertTrue(texto.contains("INFORME DE DESEMPEÑO"))
        assertTrue(texto.contains("Fortalezas:"))
        assertTrue(texto.contains("Aspectos a reforzar:"))
        assertTrue(texto.contains("Tablas de multiplicar"))
        assertTrue(texto.contains("Pendiente de validación docente"))
        assertTrue(texto.contains("No es un diagnóstico"))
    }
}
