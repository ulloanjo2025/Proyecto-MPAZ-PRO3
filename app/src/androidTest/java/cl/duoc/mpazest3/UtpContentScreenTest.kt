package cl.duoc.mpazest3

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import cl.duoc.mpazest3.data.local.entity.LearningObjectiveEntity
import cl.duoc.mpazest3.model.ContentStatus
import cl.duoc.mpazest3.ui.screen.UtpContentScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

// Pruebas de interfaz (Compose UI Test): se ejecutan en el emulador o en el teléfono.
class UtpContentScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val objetivos = listOf(
        LearningObjectiveEntity("MA03 OA 04", "1", "Unidad", "Sumas y restas", "Resumen de sumas", null, null, ContentStatus.PENDIENTE),
        LearningObjectiveEntity("MA03 OA 08", "1", "Unidad", "Tablas de multiplicar", "Resumen de tablas", null, null, ContentStatus.AUTORIZADO)
    )

    @Test
    fun muestra_los_codigos_de_los_OA_y_el_resumen_de_estados() {
        composeRule.setContent {
            UtpContentScreen(
                objectives = objetivos,
                isSyncing = false,
                syncMessage = null,
                onSync = {},
                onSetStatus = { _, _ -> },
                onLogout = {}
            )
        }

        composeRule.onNodeWithText("MA03 OA 04").assertIsDisplayed()
        composeRule.onNodeWithText("MA03 OA 08").assertIsDisplayed()
        composeRule.onNodeWithText("Autorizados").assertIsDisplayed()
        composeRule.onNodeWithText("Pendientes").assertIsDisplayed()
        composeRule.onNodeWithText("Rechazados").assertIsDisplayed()
    }

    @Test
    fun al_presionar_Autorizar_informa_el_codigo_y_el_nuevo_estado() {
        var codigo = ""
        var estado = ""
        composeRule.setContent {
            UtpContentScreen(
                objectives = objetivos,
                isSyncing = false,
                syncMessage = null,
                onSync = {},
                onSetStatus = { c, e ->
                    codigo = c
                    estado = e
                },
                onLogout = {}
            )
        }

        // El primer botón "Autorizar" es el del primer OA (MA03 OA 04, pendiente)
        composeRule.onAllNodesWithText("Autorizar")[0].performClick()

        assertEquals("MA03 OA 04", codigo)
        assertEquals(ContentStatus.AUTORIZADO, estado)
    }

    @Test
    fun el_boton_Ver_contenido_muestra_el_resumen_del_OA() {
        composeRule.setContent {
            UtpContentScreen(
                objectives = objetivos,
                isSyncing = false,
                syncMessage = null,
                onSync = {},
                onSetStatus = { _, _ -> },
                onLogout = {}
            )
        }

        composeRule.onAllNodesWithText("Ver contenido")[0].performClick()

        composeRule.onNodeWithText("Resumen de sumas").assertIsDisplayed()
    }

    @Test
    fun el_boton_Actualizar_contenido_llama_a_onSync() {
        var llamadas = 0
        composeRule.setContent {
            UtpContentScreen(
                objectives = objetivos,
                isSyncing = false,
                syncMessage = null,
                onSync = { llamadas++ },
                onSetStatus = { _, _ -> },
                onLogout = {}
            )
        }

        composeRule.onNodeWithText("🔄 Actualizar contenido").performClick()

        assertEquals(1, llamadas)
    }

    @Test
    fun mientras_sincroniza_el_boton_queda_desactivado_y_se_ve_el_mensaje() {
        composeRule.setContent {
            UtpContentScreen(
                objectives = objetivos,
                isSyncing = true,
                syncMessage = "Contenido al día (8 objetivos revisados).",
                onSync = {},
                onSetStatus = { _, _ -> },
                onLogout = {}
            )
        }

        composeRule.onNodeWithText("Actualizando...").assertIsNotEnabled()
        composeRule.onNodeWithText("Contenido al día", substring = true).assertIsDisplayed()
    }
}
