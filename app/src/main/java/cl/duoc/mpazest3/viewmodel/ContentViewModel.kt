package cl.duoc.mpazest3.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazest3.data.local.AppDatabase
import cl.duoc.mpazest3.data.local.entity.LearningObjectiveEntity
import cl.duoc.mpazest3.data.local.entity.QuestionEntity
import cl.duoc.mpazest3.data.remote.RetrofitInstance
import cl.duoc.mpazest3.data.remote.SyncResult
import cl.duoc.mpazest3.data.repository.ContentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
// Desarrollado por: Sebastián Maquera
// Contenido autorizado de una asignatura: sus OA y las preguntas de esos OA
data class SubjectContent(
    val subjectId: String,
    val objectives: List<LearningObjectiveEntity>,
    val questions: List<QuestionEntity>
)

class ContentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ContentRepository(
        AppDatabase.getDatabase(application).contentDao(),
        RetrofitInstance.api
    )

    // Lista de OA con su estado de autorización (la pantalla de UTP la usará)
    val objectives: StateFlow<List<LearningObjectiveEntity>> = repository.objectives
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Contenido de la asignatura que el estudiante eligió (null mientras carga o si no hay ninguna elegida)
    private val _subjectContent = MutableStateFlow<SubjectContent?>(null)
    val subjectContent: StateFlow<SubjectContent?> = _subjectContent.asStateFlow()

    // Lee de Room SOLO los OA autorizados por UTP de la asignatura y sus preguntas
    fun loadSubjectContent(subjectId: String) {
        viewModelScope.launch {
            _subjectContent.value = null
            val authorizedObjectives = repository.getAuthorizedObjectives(subjectId)
            val questions = repository.getQuizQuestions(subjectId)
            _subjectContent.value = SubjectContent(subjectId, authorizedObjectives, questions)
        }
    }

    fun clearSubjectContent() {
        _subjectContent.value = null
    }

    // Estado de la sincronización con el servicio REST (la pantalla de UTP lo muestra)
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    // Carga el contenido inicial en Room si todavía no existe y después intenta actualizarlo desde el servicio.
    fun loadContent() {
        viewModelScope.launch {
            repository.ensureSeeded()
            runSync()
        }
    }

    // Botón "Actualizar contenido" de UTP
    fun syncContent() {
        viewModelScope.launch {
            runSync()
        }
    }

    private suspend fun runSync() {
        if (_isSyncing.value) return
        _isSyncing.value = true
        _syncMessage.value = describe(repository.syncFromRemote())
        _isSyncing.value = false
    }

    private fun describe(result: SyncResult): String {
        return when (result) {
            is SyncResult.Success -> {
                if (result.objectivesUpdated == 0) {
                    "Contenido al día (" + result.objectivesChecked + " objetivos revisados)."
                } else {
                    "Contenido actualizado: " + result.objectivesUpdated + " objetivo(s) con cambios quedaron pendientes de autorización de UTP."
                }
            }
            SyncResult.NotConfigured ->
                "Falta configurar la dirección del servicio (ApiConfig.kt). Se usa el contenido guardado en el teléfono."
            is SyncResult.Error -> result.message
        }
    }

    // UTP autoriza o rechaza un OA (usar ContentStatus.AUTORIZADO / RECHAZADO)
    fun setObjectiveStatus(code: String, status: String) {
        viewModelScope.launch {
            repository.setObjectiveStatus(code, status)
        }
    }

    suspend fun getAuthorizedObjectives(subjectId: String): List<LearningObjectiveEntity> {
        return repository.getAuthorizedObjectives(subjectId)
    }

    suspend fun getQuizQuestions(subjectId: String): List<QuestionEntity> {
        return repository.getQuizQuestions(subjectId)
    }
}
