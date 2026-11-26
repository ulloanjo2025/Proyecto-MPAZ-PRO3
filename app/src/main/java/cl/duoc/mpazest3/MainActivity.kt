// Desarrollado por: Jonathan Ulloa, Sebastián Maquera, Camilo Alarcón (root)
package cl.duoc.mpazest3

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cl.duoc.mpazest3.model.ContentStatus
import cl.duoc.mpazest3.model.Role
import cl.duoc.mpazest3.model.Subject
import cl.duoc.mpazest3.ui.screen.ActivityScreen
import cl.duoc.mpazest3.ui.screen.LoginScreen
import cl.duoc.mpazest3.ui.screen.PedagogicalReportScreen
import cl.duoc.mpazest3.ui.screen.StudyScreen
import cl.duoc.mpazest3.ui.screen.SubjectMessageScreen
import cl.duoc.mpazest3.ui.screen.SubjectSelectionScreen
import cl.duoc.mpazest3.ui.screen.UtpContentScreen
import cl.duoc.mpazest3.ui.theme.MpazEst3Theme
import cl.duoc.mpazest3.util.SoundManager
import cl.duoc.mpazest3.viewmodel.AuthViewModel
import cl.duoc.mpazest3.viewmodel.ContentViewModel
import cl.duoc.mpazest3.viewmodel.ProgressViewModel
import cl.duoc.mpazest3.viewmodel.WeatherViewModel
import java.util.Locale

// Las 4 asignaturas del MVP (una unidad cada una)
private val officialSubjects = listOf(
    Subject("1", "Matemática", "Unidad 1: Tablas de multiplicar y cálculo", android.R.drawable.ic_menu_compass, 0xFF00A8E8L),
    Subject("2", "Lenguaje y Comunicación", "Unidad 1: Comprensión lectora", android.R.drawable.ic_menu_agenda, 0xFFFF7F3FL),
    Subject("3", "Ciencias Naturales", "Unidad 1: Plantas y alimentación saludable", android.R.drawable.ic_menu_mapmode, 0xFF06D6A0L),
    Subject("4", "Historia, Geografía y C. S.", "Unidad 1: Ubicarnos en el espacio", android.R.drawable.ic_menu_mylocation, 0xFFEF476FL)
)

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private val authViewModel: AuthViewModel by viewModels()
    private val progressViewModel: ProgressViewModel by viewModels()
    private val contentViewModel: ContentViewModel by viewModels()
    private val weatherViewModel: WeatherViewModel by viewModels()
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar SoundManager para cargar audios desde res/raw/
        SoundManager.init(applicationContext)

        // Paso 3: carga en Room los OA y preguntas la primera vez
        contentViewModel.loadContent()

        // Parcial 4: trae los estudiantes del microservicio y consulta la API externa de clima
        progressViewModel.syncStudents()
        weatherViewModel.loadWeather()

        tts = TextToSpeech(this, this)

        setContent {
            MpazEst3Theme {
                val uiState by authViewModel.uiState.collectAsState()
                // rememberSaveable: la asignatura elegida sobrevive al giro de la pantalla
                var selectedSubjectId by rememberSaveable { mutableStateOf<String?>(null) }
                val selectedSubject = officialSubjects.firstOrNull { it.id == selectedSubjectId }

                when (uiState.selectedRole) {
                    null -> {
                        LoginScreen(
                            authViewModel = authViewModel,
                            onLoginStudent = {
                                authViewModel.validateAndLoginStudent { code, pin ->
                                    progressViewModel.validateStudent(code, pin)
                                }
                            }
                        )
                    }

                    Role.STUDENT -> {
                        val realAttempts by progressViewModel.allAttempts.collectAsState()
                        val studentName by authViewModel.username.collectAsState()
                        val allObjectives by contentViewModel.objectives.collectAsState()
                        val subjectContent by contentViewModel.subjectContent.collectAsState()
                        val drafts by progressViewModel.allDrafts.collectAsState()
                        val weatherText by weatherViewModel.weatherText.collectAsState()
                        var studyDone by rememberSaveable { mutableStateOf(false) }

                        val attemptsPerSubject = remember(realAttempts, studentName) {
                            realAttempts
                                .filter { it.studentName.equals(studentName, ignoreCase = true) }
                                .groupBy { it.subjectId }
                                .mapValues { it.value.size }
                        }

                        // Asignaturas que tienen al menos un OA autorizado por UTP
                        val availableSubjectIds = remember(allObjectives) {
                            allObjectives
                                .filter { it.status == ContentStatus.AUTORIZADO }
                                .map { it.subjectId }
                                .toSet()
                        }

                        // Vuelve al menú de asignaturas y limpia el contenido cargado
                        val leaveSubject = {
                            selectedSubjectId = null
                            studyDone = false
                            contentViewModel.clearSubjectContent()
                        }

                        // Carga el contenido de la asignatura elegida (también si la app se recrea al girar la pantalla)
                        LaunchedEffect(selectedSubjectId) {
                            val subjectIdToLoad = selectedSubjectId
                            if (subjectIdToLoad != null && contentViewModel.subjectContent.value?.subjectId != subjectIdToLoad) {
                                contentViewModel.loadSubjectContent(subjectIdToLoad)
                            }
                        }

                        val currentSubject = selectedSubject

                        if (currentSubject == null) {
                            SubjectSelectionScreen(
                                subjects = officialSubjects,
                                attemptsPerSubject = attemptsPerSubject,
                                availableSubjectIds = availableSubjectIds,
                                weatherText = weatherText,
                                onSubjectSelect = { subject ->
                                    studyDone = false
                                    selectedSubjectId = subject.id
                                },
                                onLogout = { authViewModel.clearRole() }
                            )
                        } else {
                            val content = subjectContent

                            if (content == null || content.subjectId != currentSubject.id) {
                                SubjectMessageScreen(
                                    message = "Cargando tu desafío...",
                                    showLoading = true,
                                    onBack = leaveSubject
                                )
                            } else if (content.questions.isEmpty()) {
                                SubjectMessageScreen(
                                    message = "Esta asignatura aún no tiene contenido autorizado por UTP.",
                                    showLoading = false,
                                    onBack = leaveSubject
                                )
                            } else if (!studyDone) {
                                StudyScreen(
                                    subjectTitle = currentSubject.title,
                                    objectives = content.objectives,
                                    onStart = { studyDone = true },
                                    onBack = leaveSubject,
                                    onPlayAudio = { textToRead ->
                                        tts.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null)
                                    }
                                )
                            } else if (drafts == null) {
                                SubjectMessageScreen(
                                    message = "Cargando tu avance...",
                                    showLoading = true,
                                    onBack = leaveSubject
                                )
                            } else {
                                val activeSubjectTitle = currentSubject.title
                                val draft = drafts?.firstOrNull {
                                    it.studentName == studentName.trim().lowercase() && it.subjectId == activeSubjectTitle
                                }

                                ActivityScreen(
                                    subjectId = activeSubjectTitle,
                                    questions = content.questions,
                                    objectives = content.objectives,
                                    initialQuestionIndex = draft?.questionIndex ?: 0,
                                    initialCorrectAnswersCount = draft?.correctAnswers ?: 0,
                                    initialDetails = draft?.detailsLog.orEmpty(),
                                    onProgress = { index, correct, details ->
                                        progressViewModel.saveQuizProgress(studentName, activeSubjectTitle, index, correct, details)
                                    },
                                    onAnswerSubmitted = { isApproved, score, total, details ->
                                        progressViewModel.saveAttempt(
                                            studentName = studentName,
                                            subjectId = activeSubjectTitle,
                                            isCorrect = isApproved,
                                            score = score,
                                            totalQuestions = total,
                                            detailsSummary = details,
                                            onLimitReached = { leaveSubject() },
                                            onSuccess = { leaveSubject() }
                                        )
                                    },
                                    onSaveAndExit = { savedIndex, savedCorrects, savedDetails ->
                                        progressViewModel.saveQuizProgress(studentName, activeSubjectTitle, savedIndex, savedCorrects, savedDetails)
                                        leaveSubject()
                                    },
                                    onPlayAudioInstruction = { textToRead ->
                                        tts.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null)
                                    }
                                )
                            }
                        }
                    }

                    Role.TEACHER_PARENT -> {
                        val realAttempts by progressViewModel.allAttempts.collectAsState()
                        val registeredStudents by progressViewModel.allStudents.collectAsState()
                        val allObjectives by contentViewModel.objectives.collectAsState()
                        val validations by progressViewModel.allValidations.collectAsState()
                        val teacherName by authViewModel.username.collectAsState()

                        PedagogicalReportScreen(
                            attempts = realAttempts,
                            students = registeredStudents,
                            objectives = allObjectives,
                            validations = validations,
                            onRegisterStudent = { name, code, pin, onResult ->
                                progressViewModel.registerStudent(name, code, pin, onResult)
                            },
                            onUpdateStudent = { student, name, pin, onResult ->
                                progressViewModel.updateStudent(student, name, pin, onResult)
                            },
                            onDeleteStudent = { student, onResult ->
                                progressViewModel.deleteStudent(student, onResult)
                            },
                            onUnlockSubject = { studentName, subjectId ->
                                progressViewModel.unlockSubject(studentName, subjectId)
                            },
                            onValidateReport = { studentName, attemptsCount ->
                                progressViewModel.validateReport(studentName, teacherName, attemptsCount)
                            },
                            onRevokeValidation = { studentName ->
                                progressViewModel.revokeReportValidation(studentName)
                            },
                            onLogout = { authViewModel.clearRole() }
                        )
                    }

                    Role.UTP -> {
                        val objectives by contentViewModel.objectives.collectAsState()
                        val isSyncing by contentViewModel.isSyncing.collectAsState()
                        val syncMessage by contentViewModel.syncMessage.collectAsState()

                        UtpContentScreen(
                            objectives = objectives,
                            isSyncing = isSyncing,
                            syncMessage = syncMessage,
                            onSync = { contentViewModel.syncContent() },
                            onSetStatus = { code, status ->
                                contentViewModel.setObjectiveStatus(code, status)
                            },
                            onLogout = { authViewModel.clearRole() }
                        )
                    }
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("es", "ES")
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}