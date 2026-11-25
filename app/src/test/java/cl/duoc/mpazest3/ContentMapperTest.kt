package cl.duoc.mpazest3

import cl.duoc.mpazest3.data.remote.ContentResponseDto
import cl.duoc.mpazest3.data.remote.ObjectiveDto
import cl.duoc.mpazest3.data.remote.QuestionDto
import cl.duoc.mpazest3.data.remote.toEntityOrNull
import cl.duoc.mpazest3.model.ContentStatus
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

// Pruebas de la lectura del JSON del servicio: se ejecutan en el computador, sin red ni emulador.
class ContentMapperTest {

    private fun objetivo(
        code: String? = "MA03 OA 08",
        subjectId: String? = "1",
        title: String? = "Tablas de multiplicar",
        status: String? = "AUTORIZADO"
    ) = ObjectiveDto(code, subjectId, "Unidad", title, "Resumen", null, null, status)

    private fun pregunta(
        difficulty: Int? = 1,
        correctIndex: Int? = 1,
        optionC: String? = "12"
    ) = QuestionDto(1, "MA03 OA 08", difficulty, "¿Cuánto es 5 × 3?", "8", "15", optionC, correctIndex, "Porque sí")

    @Test
    fun un_objetivo_valido_se_convierte_y_parte_pendiente_aunque_el_servicio_diga_otra_cosa() {
        val entidad = objetivo(status = "AUTORIZADO").toEntityOrNull()
        assertNotNull(entidad)
        assertEquals("MA03 OA 08", entidad!!.code)
        assertEquals(ContentStatus.PENDIENTE, entidad.status)
    }

    @Test
    fun un_objetivo_con_datos_faltantes_o_asignatura_inexistente_se_descarta() {
        assertNull(objetivo(code = null).toEntityOrNull())
        assertNull(objetivo(code = "  ").toEntityOrNull())
        assertNull(objetivo(title = null).toEntityOrNull())
        assertNull(objetivo(subjectId = "9").toEntityOrNull())
    }

    @Test
    fun una_pregunta_valida_se_convierte() {
        val entidad = pregunta().toEntityOrNull()
        assertNotNull(entidad)
        assertEquals(1, entidad!!.correctIndex)
    }

    @Test
    fun una_pregunta_con_valores_invalidos_se_descarta() {
        assertNull(pregunta(difficulty = 5).toEntityOrNull())
        assertNull(pregunta(correctIndex = 3).toEntityOrNull())
        assertNull(pregunta(correctIndex = null).toEntityOrNull())
        assertNull(pregunta(optionC = "8").toEntityOrNull())
    }

    @Test
    fun el_json_del_servicio_se_lee_con_gson_y_se_descartan_los_elementos_invalidos() {
        val json = """
            {
              "version": 1,
              "objectives": [
                {"code": "MA03 OA 08", "subjectId": "1", "unitName": "U", "title": "T", "summary": "S"},
                {"code": "XX", "subjectId": "9", "unitName": "U", "title": "T", "summary": "S"}
              ],
              "questions": [
                {"id": 1, "objectiveCode": "MA03 OA 08", "difficulty": 1, "statement": "Q", "optionA": "a", "optionB": "b", "optionC": "c", "correctIndex": 0, "explanation": "e"},
                {"id": 2, "objectiveCode": "MA03 OA 08", "difficulty": 1, "statement": "Q"}
              ]
            }
        """.trimIndent()

        val respuesta = Gson().fromJson(json, ContentResponseDto::class.java)

        assertEquals(1, respuesta.objectives.orEmpty().mapNotNull { it.toEntityOrNull() }.size)
        assertEquals(1, respuesta.questions.orEmpty().mapNotNull { it.toEntityOrNull() }.size)
    }
}
