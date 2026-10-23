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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cl.duoc.mpazest3.model.Role
import cl.duoc.mpazest3.model.Subject
import cl.duoc.mpazest3.ui.screen.ActivityScreen
import cl.duoc.mpazest3.ui.screen.LoginScreen
import cl.duoc.mpazest3.ui.screen.PedagogicalReportScreen
import cl.duoc.mpazest3.ui.screen.SubjectSelectionScreen
import cl.duoc.mpazest3.ui.theme.MpazEst3Theme
import cl.duoc.mpazest3.util.SoundManager
import cl.duoc.mpazest3.viewmodel.AuthViewModel
import cl.duoc.mpazest3.viewmodel.ProgressViewModel
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private val authViewModel: AuthViewModel by viewModels()
    private val progressViewModel: ProgressViewModel by viewModels()
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar SoundManager para cargar audios desde res/raw/
        SoundManager.init(applicationContext)

        tts = TextToSpeech(this, this)

        setContent {
            MpazEst3Theme {
                val uiState by authViewModel.uiState.collectAsState()
                var selectedSubject by remember { mutableStateOf<Subject?>(null) }

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

                        val attemptsPerSubject = remember(realAttempts, studentName) {
                            realAttempts
                                .filter { it.studentName.equals(studentName, ignoreCase = true) }
                                .groupBy { it.subjectId }
                                .mapValues { it.value.size }
                        }

                        if (selectedSubject == null) {
                            val officialSubjects = listOf(
                                Subject("1", "Matemática", "Unidad 1: Números y Operaciones", android.R.drawable.ic_menu_compass, 0xFF00A8E8L),
                                Subject("2", "Lenguaje y Comunicación", "Unidad 1: Lectura y Comprensión", android.R.drawable.ic_menu_agenda, 0xFFFF7F3FL),
                                Subject("3", "Ciencias Naturales", "Unidad 1: El Cuerpo Humano y la Salud", android.R.drawable.ic_menu_mapmode, 0xFF06D6A0L),
                                Subject("4", "Historia, Geografía y C. S.", "Unidad 1: Ubicación en el Mapa y Entorno", android.R.drawable.ic_menu_mylocation, 0xFFEF476FL)
                            )

                            SubjectSelectionScreen(
                                subjects = officialSubjects,
                                attemptsPerSubject = attemptsPerSubject,
                                onSubjectSelect = { subject -> selectedSubject = subject },
                                onLogout = { authViewModel.clearRole() }
                            )
                        } else {
                            val activeSubjectTitle = selectedSubject?.title ?: "Actividad"
                            val draft = progressViewModel.getQuizDraft(activeSubjectTitle)

                            ActivityScreen(
                                subjectId = activeSubjectTitle,
                                initialQuestionIndex = draft.questionIndex,
                                initialCorrectAnswersCount = draft.correctAnswersCount,
                                onAnswerSubmitted = { isApproved, score, total, details ->
                                    progressViewModel.saveAttempt(
                                        studentName = studentName,
                                        subjectId = activeSubjectTitle,
                                        isCorrect = isApproved,
                                        score = score,
                                        totalQuestions = total,
                                        detailsSummary = details,
                                        onLimitReached = { selectedSubject = null },
                                        onSuccess = { selectedSubject = null }
                                    )
                                },
                                onSaveAndExit = { savedIndex, savedCorrects ->
                                    progressViewModel.saveQuizDraft(activeSubjectTitle, savedIndex, savedCorrects)
                                    selectedSubject = null
                                },
                                onPlayAudioInstruction = { textToRead ->
                                    tts.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null)
                                }
                            )
                        }
                    }

                    Role.TEACHER_PARENT -> {
                        val realAttempts by progressViewModel.allAttempts.collectAsState()
                        val registeredStudents by progressViewModel.allStudents.collectAsState()

                        PedagogicalReportScreen(
                            attempts = realAttempts,
                            students = registeredStudents,
                            onRegisterStudent = { name, code, pin, onResult ->
                                progressViewModel.registerStudent(name, code, pin, onResult)
                            },
                            onUnlockSubject = { studentName, subjectId ->
                                progressViewModel.unlockSubject(studentName, subjectId)
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