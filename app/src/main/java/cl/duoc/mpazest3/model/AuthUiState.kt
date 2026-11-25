// Desarrollado por: Sebastián Maquera (model)
package cl.duoc.mpazest3.model

// Estado de la interfaz de usuario para el proceso de autenticación y selección de rol
data class AuthUiState(
    val selectedRole: Role? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)