package cl.duoc.mpazest3.data.remote

// Resultado de una operación contra un microservicio (con un mensaje listo para mostrar)
sealed class RemoteResult {
    data class Success(val message: String) : RemoteResult()
    data class Error(val message: String) : RemoteResult()
}
