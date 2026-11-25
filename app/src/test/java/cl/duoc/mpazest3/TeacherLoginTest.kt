package cl.duoc.mpazest3

import cl.duoc.mpazest3.model.Role
import cl.duoc.mpazest3.viewmodel.AuthViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Pruebas del login del docente (código ficticio en vez de RUT): se ejecutan sin emulador.
class TeacherLoginTest {

    @Test
    fun el_codigo_docente_valido_tiene_DOC_y_dos_numeros() {
        val viewModel = AuthViewModel()
        assertTrue(viewModel.isValidTeacherCode("DOC01"))
        assertTrue(viewModel.isValidTeacherCode(" doc12 "))
    }

    @Test
    fun los_codigos_con_otro_formato_se_rechazan() {
        val viewModel = AuthViewModel()
        assertFalse(viewModel.isValidTeacherCode("DOC1"))
        assertFalse(viewModel.isValidTeacherCode("ABC01"))
        assertFalse(viewModel.isValidTeacherCode("12345678-5"))
        assertFalse(viewModel.isValidTeacherCode(""))
    }

    @Test
    fun el_correo_debe_ser_del_dominio_mpaz_cl() {
        val viewModel = AuthViewModel()
        assertTrue(viewModel.isValidTeacherEmail("profe@mpaz.cl"))
        assertFalse(viewModel.isValidTeacherEmail("profe@gmail.com"))
    }

    @Test
    fun el_login_correcto_cambia_el_rol_a_docente() {
        val viewModel = AuthViewModel()
        assertTrue(viewModel.validateAndLoginTeacher("Ana Pérez", "ana@mpaz.cl", "DOC01"))
        assertEquals(Role.TEACHER_PARENT, viewModel.uiState.value.selectedRole)
    }

    @Test
    fun un_nombre_vacio_y_un_correo_invalido_muestran_los_dos_errores() {
        val viewModel = AuthViewModel()
        assertFalse(viewModel.validateAndLoginTeacher("", "x@gmail.com", "DOC01"))
        val error = viewModel.usernameError.value.orEmpty()
        assertTrue(error.contains("nombre"))
        assertTrue(error.contains("@mpaz.cl"))
    }
}
