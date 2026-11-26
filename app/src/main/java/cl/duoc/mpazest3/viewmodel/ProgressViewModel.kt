// Desarrollado por: Camilo Alarcón (viewmodel)
package cl.duoc.mpazest3.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazest3.data.local.AppDatabase
import cl.duoc.mpazest3.data.local.entity.QuizDraftEntity
import cl.duoc.mpazest3.data.local.entity.ReportValidationEntity
import cl.duoc.mpazest3.data.local.entity.StudentAttemptEntity
import cl.duoc.mpazest3.data.local.entity.StudentEntity
import cl.duoc.mpazest3.data.remote.RemoteResult
import cl.duoc.mpazest3.data.remote.RetrofitInstance
import cl.duoc.mpazest3.data.repository.TrackingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProgressViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val progressDao = db.studentProgressDao()
    private val studentDao = db.studentDao()

    // Repositorio que habla con el microservicio de seguimiento (CRUD de estudiantes e intentos)
    private val trackingRepository = TrackingRepository(studentDao, progressDao, RetrofitInstance.studentApi)

    private val draftDao = db.quizDraftDao()
    private val validationDao = db.reportValidationDao()

    val allAttempts: StateFlow<List<StudentAttemptEntity>> = progressDao.getAllAttempts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allStudents: StateFlow<List<StudentEntity>> = studentDao.getAllStudents()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Validaciones de informes hechas por el docente (se guardan en Room)
    val allValidations: StateFlow<List<ReportValidationEntity>> = validationDao.observeAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Borradores de quiz guardados en Room (null = todavía cargando desde la base de datos)
    val allDrafts: StateFlow<List<QuizDraftEntity>?> = draftDao.observeAll()
        .stateIn<List<QuizDraftEntity>?>(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    // El nombre se guarda en minúsculas para que "Ana" y "ana" sean el mismo estudiante
    private fun draftKey(studentName: String): String = studentName.trim().lowercase()

    // Guarda el avance del quiz en Room (se llama al pasar a la siguiente pregunta o al salir)
    fun saveQuizProgress(
        studentName: String,
        subjectId: String,
        questionIndex: Int,
        correctAnswers: Int,
        detailsLog: String
    ) {
        viewModelScope.launch {
            draftDao.upsertDraft(
                QuizDraftEntity(
                    studentName = draftKey(studentName),
                    subjectId = subjectId,
                    questionIndex = questionIndex,
                    correctAnswers = correctAnswers,
                    detailsLog = detailsLog
                )
            )
        }
    }

    // El docente valida el informe de un estudiante (queda su nombre, la fecha y cuántas evaluaciones tenía)
    fun validateReport(studentName: String, teacherName: String, attemptsCount: Int) {
        viewModelScope.launch {
            validationDao.upsertValidation(
                ReportValidationEntity(
                    studentName = draftKey(studentName),
                    validatedBy = teacherName,
                    validatedAt = System.currentTimeMillis(),
                    attemptsAtValidation = attemptsCount
                )
            )
        }
    }

    fun revokeReportValidation(studentName: String) {
        viewModelScope.launch {
            validationDao.deleteValidation(draftKey(studentName))
        }
    }

    // Guardar intento con desglose completo de preguntas
    fun saveAttempt(
        studentName: String,
        subjectId: String,
        isCorrect: Boolean,
        score: Int,
        totalQuestions: Int,
        detailsSummary: String,
        onLimitReached: () -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val previousAttempts = progressDao.getAttemptCountForSubject(studentName, subjectId)

            if (previousAttempts >= 2) {
                draftDao.deleteDraft(draftKey(studentName), subjectId)
                onLimitReached()
                return@launch
            }

            val nextAttemptNumber = previousAttempts + 1
            val attempt = StudentAttemptEntity(
                studentName = studentName,
                subjectId = subjectId,
                attemptNumber = nextAttemptNumber,
                score = score,
                totalQuestions = totalQuestions,
                isCorrect = isCorrect,
                detailsSummary = detailsSummary
            )
            progressDao.insertAttempt(attempt)
            draftDao.deleteDraft(draftKey(studentName), subjectId)
            onSuccess()

            // Después de avisar a la pantalla, el intento también se envía al microservicio
            trackingRepository.sendAttempt(attempt)
        }
    }

    // Desbloquear Asignatura para permitir nuevos intentos al estudiante
    fun unlockSubject(studentName: String, subjectId: String) {
        viewModelScope.launch {
            progressDao.unlockSubjectForStudent(studentName, subjectId)
            draftDao.deleteDraft(draftKey(studentName), subjectId)
            trackingRepository.deleteAttempts(studentName, subjectId)
        }
    }

    // Trae los estudiantes del microservicio (se llama al abrir la app)
    fun syncStudents() {
        viewModelScope.launch {
            trackingRepository.syncStudents()
        }
    }

    // CREATE: registra un estudiante en el microservicio (y en Room)
    fun registerStudent(
        fullName: String,
        username: String,
        pin: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val cleanUser = username.trim().uppercase()

        if (fullName.isBlank() || cleanUser.isBlank() || pin.length != 4) {
            onResult(false, "Completa todos los campos. El PIN debe ser de 4 dígitos.")
            return
        }

        viewModelScope.launch {
            report(trackingRepository.createStudent(cleanUser, fullName.trim(), pin.trim()), onResult)
        }
    }

    // UPDATE: cambia el nombre y el PIN de un estudiante
    fun updateStudent(
        student: StudentEntity,
        fullName: String,
        pin: String,
        onResult: (Boolean, String) -> Unit
    ) {
        if (fullName.isBlank() || pin.trim().length != 4) {
            onResult(false, "Completa el nombre. El PIN debe ser de 4 dígitos.")
            return
        }

        viewModelScope.launch {
            report(trackingRepository.updateStudent(student, fullName.trim(), pin.trim()), onResult)
        }
    }

    // DELETE: elimina un estudiante y sus evaluaciones
    fun deleteStudent(student: StudentEntity, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            report(trackingRepository.deleteStudent(student), onResult)
        }
    }

    // Entrega a la pantalla el resultado de la llamada al microservicio (éxito o error con su mensaje)
    private fun report(result: RemoteResult, onResult: (Boolean, String) -> Unit) {
        when (result) {
            is RemoteResult.Success -> onResult(true, result.message)
            is RemoteResult.Error -> onResult(false, result.message)
        }
    }

    // Ingreso del estudiante: primero en Room y, si no está, en el microservicio
    suspend fun validateStudent(username: String, pin: String): StudentEntity? {
        val code = username.trim().uppercase()
        val cleanPin = pin.trim()
        return studentDao.loginStudent(code, cleanPin) ?: trackingRepository.loginRemote(code, cleanPin)
    }
}
