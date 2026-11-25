// Desarrollado por: Sebastián Maquera (ui/screen)
package cl.duoc.mpazest3.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.mpazest3.data.local.entity.LearningObjectiveEntity
import cl.duoc.mpazest3.data.local.entity.QuestionEntity
import cl.duoc.mpazest3.model.QuizRules
import cl.duoc.mpazest3.ui.components.AudioPlayerButton
import cl.duoc.mpazest3.util.SoundManager
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun ActivityScreen(
    subjectId: String,
    questions: List<QuestionEntity>,
    objectives: List<LearningObjectiveEntity>,
    initialQuestionIndex: Int = 0,
    initialCorrectAnswersCount: Int = 0,
    initialDetails: String = "",
    onProgress: (Int, Int, String) -> Unit,
    onAnswerSubmitted: (isApproved: Boolean, score: Int, total: Int, details: String) -> Unit,
    onSaveAndExit: (Int, Int, String) -> Unit,
    onPlayAudioInstruction: (String) -> Unit
) {
    var currentQuestionIndex by remember { mutableIntStateOf(initialQuestionIndex.coerceIn(0, maxOf(questions.lastIndex, 0))) }
    var correctAnswersCount by remember { mutableIntStateOf(initialCorrectAnswersCount) }
    var answerStatus by remember { mutableStateOf<Boolean?>(null) }
    var showConfetti by remember { mutableStateOf(false) }

    // El detalle de las preguntas ya respondidas se recupera si el estudiante retoma el quiz
    val questionDetailsLog = remember {
        mutableStateListOf<String>().apply { addAll(QuizRules.splitDetails(initialDetails)) }
    }

    val currentQuestion = questions[currentQuestionIndex]

    // OA de la pregunta actual (para mostrar el texto de lectura en Lenguaje)
    val currentObjective = objectives.firstOrNull { it.code == currentQuestion.objectiveCode }
    val readingText = currentObjective?.readingText

    // Las alternativas A, B y C se muestran en orden distinto cada vez que aparece la pregunta
    val optionOrder = remember(currentQuestion.id) { QuizRules.shuffledOptionOrder() }
    val optionTexts = listOf(currentQuestion.optionA, currentQuestion.optionB, currentQuestion.optionC)
    val progress = (currentQuestionIndex + 1) / questions.size.toFloat()

    val subjectColor = when (subjectId) {
        "Matemática" -> Color(0xFF00A8E8)
        "Lenguaje y Comunicación" -> Color(0xFFFF7F3F)
        "Ciencias Naturales" -> Color(0xFF06D6A0)
        else -> Color(0xFFEF476F)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF4F9FC)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Barra Superior
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            SoundManager.playClick()
                            onSaveAndExit(currentQuestionIndex, correctAnswersCount, questionDetailsLog.joinToString("\n"))
                        },
                        // Solo se puede guardar y salir antes de responder la pregunta (así no se cuenta dos veces)
                        enabled = answerStatus == null,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF64748B))
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Guardar y Salir", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Surface(
                        color = subjectColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = subjectId,
                            color = subjectColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                // Indicador de Progreso
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Pregunta ${currentQuestionIndex + 1} de ${questions.size}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = "⭐ Aciertos: $correctAnswersCount",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFFFFB703)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = subjectColor,
                        trackColor = Color(0xFFE2E8F0)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Texto de lectura (solo Lenguaje): se ve mientras se responde
                if (currentObjective != null && !readingText.isNullOrBlank()) {
                    ReadingCard(
                        title = currentObjective.readingTitle.orEmpty(),
                        text = readingText,
                        onPlayAudio = {
                            SoundManager.playClick()
                            onPlayAudioInstruction(readingText)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Tarjeta de la Pregunta
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentQuestion.statement,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                modifier = Modifier.weight(1f)
                            )
                            AudioPlayerButton(onPlayAudio = {
                                SoundManager.playClick()
                                onPlayAudioInstruction(currentQuestion.statement)
                            })
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Alternativas A, B y C (en orden mezclado)
                        optionOrder.forEachIndexed { position, optionIndex ->
                            val isCorrectOption = optionIndex == currentQuestion.correctIndex

                            Button(
                                onClick = {
                                    SoundManager.playClick()
                                    answerStatus = isCorrectOption
                                    if (isCorrectOption) {
                                        correctAnswersCount++
                                        showConfetti = true
                                        SoundManager.playWin()
                                        questionDetailsLog.add("• P${currentQuestionIndex + 1} [${currentQuestion.objectiveCode}]: Correcta")
                                    } else {
                                        SoundManager.playFail()
                                        questionDetailsLog.add("• P${currentQuestionIndex + 1} [${currentQuestion.objectiveCode}]: Incorrecta (${currentQuestion.explanation})")
                                    }
                                },
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = subjectColor,
                                    // Al responder, la correcta se ve verde y las demás se ven apagadas
                                    disabledContainerColor = if (isCorrectOption) Color(0xFF06D6A0) else subjectColor.copy(alpha = 0.35f),
                                    disabledContentColor = Color.White
                                ),
                                enabled = answerStatus == null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                            ) {
                                Text(text = optionTexts[optionIndex], fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }

                            if (position < optionOrder.lastIndex) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Panel Retroalimentación con Soporte de Audio
                AnimatedVisibility(
                    visible = answerStatus != null,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    val isWin = answerStatus == true
                    val feedbackText = if (isWin) "¡Respuesta correcta! Sigue aprendiendo" else currentQuestion.explanation

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isWin) Color(0xFFE6F9F3) else Color(0xFFFFF4E5)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            if (isWin) Color(0xFF06D6A0) else Color(0xFFFFB703)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isWin) "🎉" else "💡",
                                    fontSize = 32.sp,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isWin) "¡Excelente trabajo!" else "¡Casi lo logras!",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = if (isWin) Color(0xFF06D6A0) else Color(0xFFFFB703)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = feedbackText,
                                        fontSize = 13.sp,
                                        color = Color(0xFF334155)
                                    )
                                }
                                AudioPlayerButton(onPlayAudio = {
                                    SoundManager.playClick()
                                    onPlayAudioInstruction(feedbackText)
                                })
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    SoundManager.playClick()
                                    answerStatus = null
                                    showConfetti = false

                                    if (currentQuestionIndex + 1 < questions.size) {
                                        currentQuestionIndex++
                                        // Guarda el avance en Room: si la app se cierra, sigue desde aquí
                                        onProgress(currentQuestionIndex, correctAnswersCount, questionDetailsLog.joinToString("\n"))
                                    } else {
                                        val detailsLog = questionDetailsLog.joinToString("\n")
                                        onAnswerSubmitted(
                                            QuizRules.isApproved(correctAnswersCount, questions.size),
                                            correctAnswersCount,
                                            questions.size,
                                            detailsLog
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isWin) Color(0xFF06D6A0) else Color(0xFFFFB703)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                            ) {
                                Text(
                                    text = if (currentQuestionIndex + 1 < questions.size) "Continuar ➡️" else "Ver Resultado Final 🏁",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            if (showConfetti) {
                ConfettiEffect(onFinish = { showConfetti = false })
            }
        }
    }
}

// Tarjeta con el texto de lectura de Lenguaje (con audio), visible mientras se responde
@Composable
private fun ReadingCard(title: String, text: String, onPlayAudio: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9E6)),
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD166))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📖 " + title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF334155),
                    modifier = Modifier.weight(1f)
                )
                AudioPlayerButton(onPlayAudio = onPlayAudio)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = Color(0xFF334155)
            )
        }
    }
}

@Composable
fun ConfettiEffect(onFinish: () -> Unit) {
    val particles = remember {
        List(35) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * 0.4f,
                size = Random.nextFloat() * 10f + 8f,
                color = listOf(
                    Color(0xFFFFD166), Color(0xFF06D6A0), Color(0xFF00A8E8),
                    Color(0xFFFF7F3F), Color(0xFFEF476F), Color(0xFF9B5DE5)
                ).random()
            )
        }
    }
    var animValue by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        val duration = 1800L
        while (System.currentTimeMillis() - startTime < duration) {
            animValue = (System.currentTimeMillis() - startTime) / duration.toFloat()
            delay(16)
        }
        onFinish()
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { p ->
            val currentY = (p.y + animValue * 1.2f) % 1f
            drawCircle(
                color = p.color,
                radius = p.size,
                center = Offset(p.x * size.width, currentY * size.height)
            )
        }
    }
}

private data class ConfettiParticle(val x: Float, val y: Float, val size: Float, val color: Color)