package cl.duoc.mpazest3

import cl.duoc.mpazest3.model.Role
import cl.duoc.mpazest3.viewmodel.AuthViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// Pruebas del login del perfil UTP: se ejecutan en el computador, sin emulador.
class UtpLoginTest {

    @Test
    fun las_credenciales_de_prueba_son_validas() {
        val viewModel = AuthViewModel()
        assertTrue(viewModel.isValidUtpCredentials("UTP01", "1234"))
    }

    @Test
    fun el_codigo_no_distingue_mayusculas_ni_espacios() {
        val viewModel = AuthViewModel()
        assertTrue(viewModel.isValidUtpCredentials(" utp01 ", " 1234 "))
    }

    @Test
    fun un_pin_incorrecto_es_rechazado() {
        val viewModel = AuthViewModel()
        assertFalse(viewModel.isValidUtpCredentials("UTP01", "0000"))
    }

    @Test
    fun un_codigo_incorrecto_es_rechazado() {
        val viewModel = AuthViewModel()
        assertFalse(viewModel.isValidUtpCredentials("JP01", "1234"))
    }

    @Test
    fun el_login_correcto_cambia_el_rol_a_UTP() {
        val viewModel = AuthViewModel()
        assertTrue(viewModel.validateAndLoginUtp("UTP01", "1234"))
        assertEquals(Role.UTP, viewModel.uiState.value.selectedRole)
    }

    @Test
    fun un_pin_corto_muestra_error_y_no_entra() {
        val viewModel = AuthViewModel()
        assertFalse(viewModel.validateAndLoginUtp("UTP01", "12"))
        assertNull(viewModel.uiState.value.selectedRole)
        assertNotNull(viewModel.passwordError.value)
    }
}
