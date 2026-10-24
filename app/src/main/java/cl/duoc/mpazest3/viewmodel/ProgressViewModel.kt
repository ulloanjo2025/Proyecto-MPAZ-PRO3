// Desarrollado por: Camilo Alarcón (viewmodel)
package cl.duoc.mpazest3.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.mpazest3.data.local.AppDatabase
import cl.duoc.mpazest3.data.local.entity.StudentAttemptEntity
import cl.duoc.mpazest3.data.local.entity.StudentEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class QuizDraft(
    val questionIndex: Int = 0,
    val correctAnswersCount: Int = 0,
    val detailsLog: String = ""
)

class ProgressViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val progressDao = db.studentProgressDao()
    private val studentDao = db.studentDao()

    private val quizDrafts = mutableMapOf<String, QuizDraft>()

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

    fun saveQuizDraft(subjectId: String, questionIndex: Int, correctAnswersCount: Int, detailsLog: String = "") {
        quizDrafts[subjectId] = QuizDraft(questionIndex, correctAnswersCount, detailsLog)
    }

    fun getQuizDraft(subjectId: String): QuizDraft {
        return quizDrafts[subjectId] ?: QuizDraft(0, 0, "")
    }

    fun clearQuizDraft(subjectId: String) {
        quizDrafts.remove(subjectId)
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
            clearQuizDraft(subjectId)
            onSuccess()
        }
    }

    // Desbloquear Asignatura para permitir nuevos intentos al estudiante
    fun unlockSubject(studentName: String, subjectId: String) {
        viewModelScope.launch {
            progressDao.unlockSubjectForStudent(studentName, subjectId)
        }
    }

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
            val existingStudent = studentDao.getStudentByUsername(cleanUser)
            if (existingStudent != null) {
                onResult(false, "¡El código de usuario '$cleanUser' ya está registrado!")
                return@launch
            }

            val newStudent = StudentEntity(
                fullName = fullName.trim(),
                username = cleanUser,
                pin = pin.trim()
            )
            studentDao.insertStudent(newStudent)
            onResult(true, "Estudiante registrado con éxito.")
        }
    }

    suspend fun validateStudent(username: String, pin: String): StudentEntity? {
        return studentDao.loginStudent(username.trim().uppercase(), pin.trim())
    }
}