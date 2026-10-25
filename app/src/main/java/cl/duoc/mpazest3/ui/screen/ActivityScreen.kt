package cl.duoc.mpazest3.ui.screen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
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
import cl.duoc.mpazest3.ui.components.AudioPlayerButton
import cl.duoc.mpazest3.util.SoundManager
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Question(
    val text: String,
    val option1: String,
    val option2: String,
    val isOption1Correct: Boolean,
    val explanation: String
)

@Composable
fun ActivityScreen(
    subjectId: String,
    initialQuestionIndex: Int = 0,
    initialCorrectAnswersCount: Int = 0,
    onAnswerSubmitted: (isApproved: Boolean, score: Int, total: Int, details: String) -> Unit,
    onSaveAndExit: (Int, Int) -> Unit,
    onPlayAudioInstruction: (String) -> Unit
) {
    val questions = remember(subjectId) {
        when (subjectId) {
            "Matemática" -> listOf(
                Question("¿Cuánto es 15 + 12?", "27", "25", true, "15 + 12 es igual a 27."),
                Question("¿Cuánto es 8 x 4?", "32", "28", true, "8 veces 4 da como resultado 32."),
                Question("¿Cuál es la mitad de 50?", "30", "25", false, "La mitad de 50 es 25."),
                Question("¿Cuánto es 100 - 45?", "55", "65", true, "Si a 100 le quitas 45 te quedan 55."),
                Question("¿Cuántos lados tiene un triángulo?", "3", "4", true, "Un triángulo siempre tiene 3 lados."),
                Question("¿Cuánto es 9 + 9 + 9?", "27", "18", true, "Sumar tres veces 9 da 27.")
            )
            "Lenguaje y Comunicación" -> listOf(
                Question("¿Cuál es el sinónimo de 'Feliz'?", "Enojado", "Contento", false, "'Contento' significa lo mismo que 'Feliz'."),
                Question("¿Cuál palabra es un sustantivo?", "Perro", "Correr", true, "'Perro' es un animal o persona (sustantivo)."),
                Question("¿Cuál es el antónimo de 'Alto'?", "Bajo", "Grande", true, "Lo opuesto a alto es bajo."),
                Question("¿Qué tipo de texto es una receta?", "Instructivo", "Poema", true, "Una receta nos indica pasos a seguir."),
                Question("¿Cuál oración tiene un verbo?", "El niño corre", "La mesa roja", true, "'Corre' es una acción (verbo)."),
                Question("¿Cómo se escribe correctamente?", "Había", "Avía", true, "Había se escribe con 'H' y 'B'.")
            )
            "Ciencias Naturales" -> listOf(
                Question("¿Qué órgano bombea la sangre?", "El Corazón", "El Pulmón", true, "El corazón bombea sangre a todo el cuerpo."),
                Question("¿Qué necesitan las plantas para la fotosíntesis?", "Luz Solar", "Jugo", true, "Las plantas necesitan luz solar y agua."),
                Question("¿El agua en estado sólido es?", "Hielo", "Vapor", true, "El hielo es agua en estado sólido."),
                Question("¿Cuál es un animal vertebrado?", "Perro", "Mariposa", true, "Los perros tienen esqueleto interno."),
                Question("¿El Sol es una?", "Estrella", "Planeta", true, "El Sol es una estrella gigante."),
                Question("¿Qué sentido usas para escuchar?", "Audición", "Tacto", true, "Los oídos te permiten escuchar sonidos.")
            )
            else -> listOf(
                Question("¿En qué continente está Chile?", "Europa", "América", false, "Chile está ubicado en América del Sur."),
                Question("¿Cuál es la capital de Chile?", "Santiago", "Valparaíso", true, "Santiago es la capital de Chile."),
                Question("¿Qué Océano baña las costas de Chile?", "Pacífico", "Atlántico", true, "El Océano Pacífico bordea nuestras costas."),
                Question("¿Cuál es un símbolo patrio?", "Bandera", "Semáforo", true, "La Bandera Chilena es nuestro símbolo patrio."),
                Question("¿Quién es el héroe naval chileno?", "Arturo Prat", "O'Higgins", true, "Arturo Prat comandó el combate de Iquique."),
                Question("¿En qué mapa se ve todo el planeta?", "Planisferio", "Plano urbano", true, "El planisferio representa toda la Tierra.")
            )
        }
    }

    var currentQuestionIndex by remember { mutableIntStateOf(initialQuestionIndex) }
    var correctAnswersCount by remember { mutableIntStateOf(initialCorrectAnswersCount) }
    var answerStatus by remember { mutableStateOf<Boolean?>(null) }
    var showConfetti by remember { mutableStateOf(false) }

    val questionDetailsLog = remember { mutableStateListOf<String>() }

    val currentQuestion = questions[currentQuestionIndex]
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
                            onSaveAndExit(currentQuestionIndex, correctAnswersCount)
                        },
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
                                text = currentQuestion.text,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                modifier = Modifier.weight(1f)
                            )
                            AudioPlayerButton(onPlayAudio = {
                                SoundManager.playClick()
                                onPlayAudioInstruction(currentQuestion.text)
                            })
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Opción 1
                        Button(
                            onClick = {
                                SoundManager.playClick()
                                val isCorrect = currentQuestion.isOption1Correct
                                answerStatus = isCorrect
                                if (isCorrect) {
                                    correctAnswersCount++
                                    showConfetti = true
                                    SoundManager.playWin()
                                    questionDetailsLog.add("• P${currentQuestionIndex + 1}: Correcta")
                                } else {
                                    SoundManager.playFail()
                                    questionDetailsLog.add("• P${currentQuestionIndex + 1}: Incorrecta (${currentQuestion.explanation})")
                                }
                            },
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (answerStatus != null && currentQuestion.isOption1Correct) Color(0xFF06D6A0) else subjectColor
                            ),
                            enabled = answerStatus == null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text(text = currentQuestion.option1, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Opción 2
                        Button(
                            onClick = {
                                SoundManager.playClick()
                                val isCorrect = !currentQuestion.isOption1Correct
                                answerStatus = isCorrect
                                if (isCorrect) {
                                    correctAnswersCount++
                                    showConfetti = true
                                    SoundManager.playWin()
                                    questionDetailsLog.add("• P${currentQuestionIndex + 1}: Correcta")
                                } else {
                                    SoundManager.playFail()
                                    questionDetailsLog.add("• P${currentQuestionIndex + 1}: Incorrecta (${currentQuestion.explanation})")
                                }
                            },
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (answerStatus != null && !currentQuestion.isOption1Correct) Color(0xFF06D6A0) else subjectColor
                            ),
                            enabled = answerStatus == null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text(text = currentQuestion.option2, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                                    } else {
                                        val detailsLog = questionDetailsLog.joinToString("\n")
                                        onAnswerSubmitted(
                                            correctAnswersCount >= 4,
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