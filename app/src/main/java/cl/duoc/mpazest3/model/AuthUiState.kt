package cl.duoc.mpazest3.model

data class AuthUiState(
    val selectedRole: Role? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)