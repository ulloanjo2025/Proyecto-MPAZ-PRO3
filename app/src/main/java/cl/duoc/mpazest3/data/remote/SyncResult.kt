package cl.duoc.mpazest3.data.remote
// Desarrollado por: Jonathan Ulloa
// Resultado de sincronizar el contenido con el servicio REST
sealed class SyncResult {
    // La dirección del servicio todavía no se configuró en ApiConfig
    object NotConfigured : SyncResult()

    // Se revisaron objectivesChecked OA; objectivesUpdated tenían cambios
    data class Success(val objectivesChecked: Int, val objectivesUpdated: Int) : SyncResult()

    data class Error(val message: String) : SyncResult()
}
