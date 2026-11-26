package cl.duoc.mpazest3.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.mpazest3.data.local.entity.LearningObjectiveEntity
import cl.duoc.mpazest3.model.ContentStatus
// Desarrollado por: Sebastián Maquera
private val UtpRed = Color(0xFFEF476F)

// Nombre de la asignatura según su id (el mismo id que usa Subject en MainActivity)
private fun utpSubjectName(subjectId: String): String = when (subjectId) {
    "1" -> "Matemática"
    "2" -> "Lenguaje y Comunicación"
    "3" -> "Ciencias Naturales"
    "4" -> "Historia, Geografía y C. S."
    else -> "Asignatura " + subjectId
}

private fun utpStatusLabel(status: String): String = when (status) {
    ContentStatus.AUTORIZADO -> "Autorizado"
    ContentStatus.RECHAZADO -> "Rechazado"
    else -> "Pendiente"
}

// Pantalla del perfil UTP: revisa el contenido de cada OA y lo autoriza o rechaza.
// Escribe el estado en Room (tabla learning_objectives) a través de onSetStatus.
@Composable
fun UtpContentScreen(
    objectives: List<LearningObjectiveEntity>,
    isSyncing: Boolean,
    syncMessage: String?,
    onSync: () -> Unit,
    onSetStatus: (String, String) -> Unit,
    onLogout: () -> Unit
) {
    val bySubject = remember(objectives) { objectives.groupBy { it.subjectId }.toSortedMap() }
    val authorized = objectives.count { it.status == ContentStatus.AUTORIZADO }
    val pending = objectives.count { it.status == ContentStatus.PENDIENTE }
    val rejected = objectives.count { it.status == ContentStatus.RECHAZADO }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SoftBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Autorización de contenidos",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "Perfil UTP · Datos ficticios de prueba",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                TextButton(onClick = onLogout) {
                    Text("Salir")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Actualiza el contenido desde el servicio REST (Retrofit)
            Button(
                onClick = onSync,
                enabled = !isSyncing,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (isSyncing) "Actualizando..." else "🔄 Actualizar contenido",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (!syncMessage.isNullOrBlank()) {
                Text(
                    text = syncMessage,
                    fontSize = 12.sp,
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Resumen de estados
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UtpSummaryChip("Autorizados", authorized, GrassGreen, Modifier.weight(1f))
                UtpSummaryChip("Pendientes", pending, SunYellow, Modifier.weight(1f))
                UtpSummaryChip("Rechazados", rejected, UtpRed, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (objectives.isEmpty()) {
                // El contenido se carga en Room al abrir la app; aquí se espera unos segundos
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    for ((subjectId, list) in bySubject) {
                        item(key = "header-" + subjectId) {
                            Text(
                                text = utpSubjectName(subjectId),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155),
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                        items(list, key = { it.code }) { objective ->
                            UtpObjectiveCard(objective, onSetStatus)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UtpSummaryChip(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.18f),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E293B)
            )
            Text(text = label, fontSize = 12.sp, color = Color(0xFF334155))
        }
    }
}

@Composable
private fun UtpObjectiveCard(
    objective: LearningObjectiveEntity,
    onSetStatus: (String, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Animación: el color del estado cambia suavemente al autorizar o rechazar
    val statusColor by animateColorAsState(
        targetValue = when (objective.status) {
            ContentStatus.AUTORIZADO -> GrassGreen
            ContentStatus.RECHAZADO -> UtpRed
            else -> SunYellow
        },
        label = "colorEstado"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = objective.code,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = SkyBlue
                )
                Surface(color = statusColor, shape = RoundedCornerShape(12.dp)) {
                    Text(
                        text = utpStatusLabel(objective.status),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            }

            Text(
                text = objective.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "Unidad: " + objective.unitName,
                fontSize = 12.sp,
                color = Color.Gray
            )

            TextButton(onClick = { expanded = !expanded }) {
                Text(if (expanded) "Ocultar contenido" else "Ver contenido")
            }

            if (expanded) {
                Text(
                    text = objective.summary,
                    fontSize = 14.sp,
                    color = Color(0xFF334155)
                )
                if (!objective.readingText.isNullOrBlank()) {
                    Text(
                        text = "Texto de lectura: " + objective.readingTitle.orEmpty(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF334155),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = objective.readingText.orEmpty(),
                        fontSize = 14.sp,
                        color = Color(0xFF334155)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onSetStatus(objective.code, ContentStatus.AUTORIZADO) },
                    enabled = objective.status != ContentStatus.AUTORIZADO,
                    colors = ButtonDefaults.buttonColors(containerColor = GrassGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Autorizar", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { onSetStatus(objective.code, ContentStatus.RECHAZADO) },
                    enabled = objective.status != ContentStatus.RECHAZADO,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Rechazar")
                }
            }
        }
    }
}
