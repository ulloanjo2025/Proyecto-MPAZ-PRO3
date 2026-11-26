package cl.duoc.mpazest3

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import cl.duoc.mpazest3.data.local.entity.LearningObjectiveEntity
import cl.duoc.mpazest3.model.ContentStatus
import cl.duoc.mpazest3.ui.screen.StudyScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

// Pruebas de interfaz de la pantalla "Estudia primero".
class StudyScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val objetivo = LearningObjectiveEntity(
        code = "LE03 OA 04",
        subjectId = "2",
        unitName = "Comprensión lectora",
        title = "Comprensión de narraciones",
        summary = "Resumen de narraciones",
        readingTitle = "Tulia y el río",
        readingText = "Tulia era una tortuga pequeña que vivía junto a un río.",
        status = ContentStatus.AUTORIZADO
    )

    @Test
    fun muestra_el_titulo_el_resumen_y_el_texto_de_lectura() {
        composeRule.setContent {
            StudyScreen(
                subjectTitle = "Lenguaje y Comunicación",
                objectives = listOf(objetivo),
                onStart = {},
                onBack = {},
                onPlayAudio = {}
            )
        }

        composeRule.onNodeWithText("📖 ¡Estudia primero!").assertIsDisplayed()
        composeRule.onNodeWithText("Resumen de narraciones").assertIsDisplayed()
        composeRule.onNodeWithText("📖 Tulia y el río").assertIsDisplayed()
    }

    @Test
    fun el_boton_Empezar_el_desafio_llama_a_onStart() {
        var empezo = false
        composeRule.setContent {
            StudyScreen(
                subjectTitle = "Lenguaje y Comunicación",
                objectives = listOf(objetivo),
                onStart = { empezo = true },
                onBack = {},
                onPlayAudio = {}
            )
        }

        // El botón está al final de la pantalla: primero se baja hasta verlo
        composeRule.onNodeWithText("¡Listo! Empezar el desafío 🚀").performScrollTo().performClick()

        assertEquals(true, empezo)
    }

    @Test
    fun el_boton_Volver_llama_a_onBack() {
        var volvio = false
        composeRule.setContent {
            StudyScreen(
                subjectTitle = "Lenguaje y Comunicación",
                objectives = listOf(objetivo),
                onStart = {},
                onBack = { volvio = true },
                onPlayAudio = {}
            )
        }

        composeRule.onNodeWithText("⬅ Volver").performClick()

        assertEquals(true, volvio)
    }
}
