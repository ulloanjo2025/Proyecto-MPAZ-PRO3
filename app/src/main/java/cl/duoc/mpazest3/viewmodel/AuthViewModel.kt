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

    // El código docente es ficticio: el caso exige datos ficticios y no se piden RUT reales.
    // Formato: DOC + 2 números (ej: DOC01)
    fun isValidTeacherCode(code: String): Boolean {
        return Regex("^DOC[0-9]{2}$").matches(code.trim().uppercase())
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

    // Validación extendida de Docente (Nombre, Correo @mpaz.cl y Código docente ficticio)
    fun validateAndLoginTeacher(
        fullName: String,
        email: String,
        teacherCode: String
    ): Boolean {
        var isValid = true

        // Los errores del nombre y del correo se muestran juntos (antes uno pisaba al otro)
        val identityErrors = mutableListOf<String>()
        if (fullName.isBlank()) {
            identityErrors.add("Ingresa tu nombre completo")
        }
        if (!isValidTeacherEmail(email)) {
            identityErrors.add("El correo debe tener dominio @mpaz.cl")
        }
        if (identityErrors.isNotEmpty()) {
            usernameError.value = identityErrors.joinToString(". ")
            isValid = false
        }

        if (!isValidTeacherCode(teacherCode)) {
            passwordError.value = "Código docente inválido. Usa el formato DOC01"
            isValid = false
        }

        if (isValid) {
            username.value = fullName
            _uiState.value = AuthUiState(selectedRole = Role.TEACHER_PARENT)
        }

        return isValid
    }

    // Credenciales ficticias de prueba para el perfil UTP (el caso exige datos ficticios)
    private val utpCode = "UTP01"
    private val utpPin = "1234"

    // Revisa si el código y el PIN corresponden al usuario UTP de prueba
    fun isValidUtpCredentials(code: String, pin: String): Boolean {
        return code.trim().uppercase() == utpCode && pin.trim() == utpPin
    }

    // Login del perfil UTP: valida el formato y luego las credenciales de prueba
    fun validateAndLoginUtp(code: String, pin: String): Boolean {
        var isValid = true

        if (code.isBlank()) {
            usernameError.value = "Ingresa tu código UTP (ej: UTP01)"
            isValid = false
        }

        if (pin.trim().length != 4) {
            passwordError.value = "El PIN debe ser de 4 dígitos"
            isValid = false
        }

        if (!isValid) return false

        if (isValidUtpCredentials(code, pin)) {
            username.value = "Jefatura UTP"
            _uiState.value = AuthUiState(selectedRole = Role.UTP)
            return true
        }

        usernameError.value = "Código o PIN de UTP incorrecto"
        passwordError.value = "Revisa los datos de prueba del grupo"
        return false
    }

    fun clearRole() {
        _uiState.value = AuthUiState(selectedRole = null)
        username.value = ""
        password.value = ""
        usernameError.value = null
        passwordError.value = null
    }
}