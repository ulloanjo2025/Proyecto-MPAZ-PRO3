package cl.duoc.mpazest3.ui.screen
// Desarrollado por: Sebastián Maquera
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.mpazest3.data.local.entity.LearningObjectiveEntity
import cl.duoc.mpazest3.ui.components.AudioPlayerButton
import cl.duoc.mpazest3.util.SoundManager

// Pantalla de estudio: el estudiante lee (y escucha) el material antes del desafío.
// Solo recibe los OA que UTP ya autorizó.
@Composable
fun StudyScreen(
    subjectTitle: String,
    objectives: List<LearningObjectiveEntity>,
    onStart: () -> Unit,
    onBack: () -> Unit,
    onPlayAudio: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SoftBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        SoundManager.playClick()
                        onBack()
                    },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("⬅ Volver", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "📖 ¡Estudia primero!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E293B)
            )
            Text(
                text = subjectTitle,
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            objectives.forEach { objective ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = objective.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                modifier = Modifier.weight(1f)
                            )
                            AudioPlayerButton(onPlayAudio = {
                                SoundManager.playClick()
                                onPlayAudio(objective.title + ". " + objective.summary)
                            })
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = objective.summary,
                            fontSize = 18.sp,
                            lineHeight = 26.sp,
                            color = Color(0xFF334155)
                        )

                        val readingText = objective.readingText
                        if (!readingText.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9E6)),
                                border = BorderStroke(2.dp, Color(0xFFFFD166))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "📖 " + objective.readingTitle.orEmpty(),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF334155),
                                            modifier = Modifier.weight(1f)
                                        )
                                        AudioPlayerButton(onPlayAudio = {
                                            SoundManager.playClick()
                                            onPlayAudio(readingText)
                                        })
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = readingText,
                                        fontSize = 18.sp,
                                        lineHeight = 26.sp,
                                        color = Color(0xFF334155)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    SoundManager.playClick()
                    onStart()
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06D6A0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("¡Listo! Empezar el desafío 🚀", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Pantalla simple de mensaje: se usa mientras carga el contenido o si no hay contenido autorizado.
@Composable
fun SubjectMessageScreen(
    message: String,
    showLoading: Boolean,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SoftBackground
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (showLoading) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Text(
                    text = message,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = onBack,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("⬅ Volver", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
