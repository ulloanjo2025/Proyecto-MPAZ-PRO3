// Desarrollado por: Jonathan Ulloa (viewmodel)
package cl.duoc.mpazest3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazest3.data.local.entity.StudentEntity
import cl.duoc.mpazest3.model.AuthUiState
import cl.duoc.mpazest3.model.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val username = MutableStateFlow("")
    val password = MutableStateFlow("")

    val usernameError = MutableStateFlow<String?>(null)
    val passwordError = MutableStateFlow<String?>(null)

    // Validar correo con dominio institucional obligatorio
    fun isValidTeacherEmail(email: String): Boolean {
        val emailPattern = Regex("^[a-zA-Z0-9._%+-]+@mpaz\\.cl$")
        return emailPattern.matches(email.trim())
    }

    // Validar formato y Algoritmo Módulo 11 de RUT Chileno (ej: 12345678-K)
    fun isValidRut(rut: String): Boolean {
        val cleanRut = rut.trim().uppercase().replace(".", "")
        val rutPattern = Regex("^[0-9]{7,8}-[0-9K]$")
        if (!rutPattern.matches(cleanRut)) return false

        val parts = cleanRut.split("-")
        val body = parts[0]
        val dv = parts[1][0]

        var sum = 0
        var multiplier = 2
        for (i in body.length - 1 downTo 0) {
            sum += (body[i].code - '0'.code) * multiplier
            multiplier = if (multiplier == 7) 2 else multiplier + 1
        }
        val expectedDv = when (val res = 11 - (sum % 11)) {
            11 -> '0'
            10 -> 'K'
            else -> res.toString()[0]
        }
        return dv == expectedDv
    }

    fun onUsernameChanged(newUsername: String) {
        username.value = newUsername
        usernameError.value = null
    }

    fun onPasswordChanged(newPassword: String) {
        password.value = newPassword
        passwordError.value = null
    }

    fun validateAndLoginStudent(
        onVerifyStudent: suspend (String, String) -> StudentEntity?
    ) {
        val user = username.value.trim()
        val pin = password.value.trim()

        var isValid = true

        if (user.isBlank()) {
            usernameError.value = "Ingresa tu código (ej: JP01)"
            isValid = false
        }

        if (pin.length != 4) {
            passwordError.value = "El PIN debe ser de 4 dígitos"
            isValid = false
        }

        if (!isValid) return

        viewModelScope.launch {
            val student = onVerifyStudent(user, pin)
            if (student != null) {
                username.value = student.fullName
                _uiState.value = AuthUiState(selectedRole = Role.STUDENT)
            } else {
                usernameError.value = "Código o PIN no registrado"
                passwordError.value = "Pídeselo a tu profesor(a)"
            }
        }
    }

    // Validación extendida de Docente (Nombre, Correo @mpaz.cl y RUT)
    fun validateAndLoginTeacher(
        fullName: String,
        email: String,
        rut: String
    ): Boolean {
        var isValid = true

        if (fullName.isBlank()) {
            usernameError.value = "Ingresa tu nombre completo"
            isValid = false
        }

        if (!isValidTeacherEmail(email)) {
            usernameError.value = "El correo debe tener dominio @mpaz.cl"
            isValid = false
        }

        if (!isValidRut(rut)) {
            passwordError.value = "RUT inválido. Usa el formato 12345678-K"
            isValid = false
        }

        if (isValid) {
            username.value = fullName
            _uiState.value = AuthUiState(selectedRole = Role.TEACHER_PARENT)
        }

        return isValid
    }

    fun clearRole() {
        _uiState.value = AuthUiState(selectedRole = null)
        username.value = ""
        password.value = ""
        usernameError.value = null
        passwordError.value = null
    }
}