package cl.duoc.mpazest3

import cl.duoc.mpazest3.data.local.entity.StudentEntity
import cl.duoc.mpazest3.model.Role
import cl.duoc.mpazest3.viewmodel.AuthViewModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

// Pruebas del ViewModel de autenticación con Kotest.
// viewModelScope usa el hilo principal de Android, que no existe en el computador:
// con Dispatchers.setMain (kotlinx-coroutines-test) se reemplaza por uno de pruebas.
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelSpec : FunSpec({

    beforeSpec { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterSpec { Dispatchers.resetMain() }

    test("con el código vacío y un PIN corto muestra los dos errores y no consulta la base de datos") {
        val viewModel = AuthViewModel()
        viewModel.onUsernameChanged("")
        viewModel.onPasswordChanged("12")
        var consultas = 0

        viewModel.validateAndLoginStudent { _, _ ->
            consultas++
            null
        }

        consultas shouldBe 0
        viewModel.usernameError.value shouldBe "Ingresa tu código (ej: JP01)"
        viewModel.passwordError.value shouldBe "El PIN debe ser de 4 dígitos"
        viewModel.uiState.value.selectedRole.shouldBeNull()
    }

    test("con datos válidos y un alumno registrado entra como estudiante") {
        val viewModel = AuthViewModel()
        viewModel.onUsernameChanged("JP01")
        viewModel.onPasswordChanged("1234")

        viewModel.validateAndLoginStudent { codigo, pin ->
            if (codigo == "JP01" && pin == "1234") StudentEntity(1, "JP01", "Juan Pérez", "1234") else null
        }

        viewModel.uiState.value.selectedRole shouldBe Role.STUDENT
        viewModel.username.value shouldBe "Juan Pérez"
    }

    test("un alumno que no está registrado ve un error y no entra") {
        val viewModel = AuthViewModel()
        viewModel.onUsernameChanged("ZZ99")
        viewModel.onPasswordChanged("0000")

        viewModel.validateAndLoginStudent { _, _ -> null }

        viewModel.usernameError.value shouldBe "Código o PIN no registrado"
        viewModel.uiState.value.selectedRole.shouldBeNull()
    }

    test("al escribir en un campo se borra su error") {
        val viewModel = AuthViewModel()
        viewModel.validateAndLoginStudent { _, _ -> null }
        viewModel.usernameError.value shouldBe "Ingresa tu código (ej: JP01)"

        viewModel.onUsernameChanged("J")

        viewModel.usernameError.value.shouldBeNull()
    }

    test("al cerrar sesión vuelve al login") {
        val viewModel = AuthViewModel()
        viewModel.onUsernameChanged("JP01")
        viewModel.onPasswordChanged("1234")
        viewModel.validateAndLoginStudent { _, _ -> StudentEntity(1, "JP01", "Juan Pérez", "1234") }
        viewModel.uiState.value.selectedRole shouldBe Role.STUDENT

        viewModel.clearRole()

        viewModel.uiState.value.selectedRole.shouldBeNull()
    }
})
