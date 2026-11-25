package cl.duoc.mpazest3

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import cl.duoc.mpazest3.ui.screen.LoginScreen
import cl.duoc.mpazest3.viewmodel.AuthViewModel
import org.junit.Rule
import org.junit.Test

// Pruebas de interfaz de la pantalla de ingreso (formularios y errores visibles).
class LoginScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun mostrarLogin() {
        val authViewModel = AuthViewModel()

        composeRule.setContent {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginStudent = {
                    authViewModel.validateAndLoginStudent { _, _ -> null }
                }
            )
        }
    }

    @Test
    fun muestra_los_tres_perfiles_de_ingreso() {
        mostrarLogin()

        composeRule.onNodeWithText("🎒 Estudiante").assertIsDisplayed()
        composeRule.onNodeWithText("👩‍🏫 Docente").assertIsDisplayed()
        composeRule.onNodeWithText("📋 UTP").assertIsDisplayed()
    }

    @Test
    fun un_estudiante_sin_datos_ve_los_dos_mensajes_de_error() {
        mostrarLogin()

        composeRule.onNodeWithText("¡Ingresar a Aprender! ✨").performClick()

        composeRule.onNodeWithText("Ingresa tu código (ej: JP01)", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("El PIN debe ser de 4 dígitos", substring = true).assertIsDisplayed()
    }

    @Test
    fun al_elegir_UTP_sin_datos_se_muestran_los_errores() {
        mostrarLogin()

        composeRule.onNodeWithText("📋 UTP").performClick()
        composeRule.onNodeWithText("Ingresar como UTP").performClick()

        composeRule.onNodeWithText("Ingresa tu código UTP", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("El PIN debe ser de 4 dígitos", substring = true).assertIsDisplayed()
    }

    @Test
    fun el_perfil_docente_pide_un_codigo_ficticio_y_no_un_RUT() {
        mostrarLogin()

        composeRule.onNodeWithText("👩‍🏫 Docente").performClick()

        composeRule.onNodeWithText("Código docente (ej: DOC01)").assertIsDisplayed()
    }
}
