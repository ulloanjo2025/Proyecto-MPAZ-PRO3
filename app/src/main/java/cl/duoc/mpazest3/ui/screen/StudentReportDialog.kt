package cl.duoc.mpazest3.ui.screen
// Desarrollado por: Sebastián Maquera
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.mpazest3.data.local.entity.LearningObjectiveEntity
import cl.duoc.mpazest3.data.local.entity.ReportValidationEntity
import cl.duoc.mpazest3.data.local.entity.StudentAttemptEntity
import cl.duoc.mpazest3.data.local.entity.StudentEntity
import cl.duoc.mpazest3.model.ObjectivePerformance
import cl.duoc.mpazest3.model.ReportRules
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Informe de desempeño de un estudiante (uso reservado del docente).
// Muestra el rendimiento por OA, fortalezas, aspectos a reforzar, fecha y estado de validación.
// No emite diagnósticos ni decisiones académicas automáticas.
@Composable
fun StudentReportDialog(
    student: StudentEntity,
    attempts: List<StudentAttemptEntity>,
    objectives: List<LearningObjectiveEntity>,
    validation: ReportValidationEntity?,
    onValidate: () -> Unit,
    onRevoke: () -> Unit,
    onUnlockSubject: (studentName: String, subjectId: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val performances = remember(attempts) {
        ReportRules.performanceByObjective(attempts.map { it.detailsSummary })
    }
    val titles = remember(objectives) { objectives.associate { it.code to it.title } }
    val dateFormat = remember { SimpleDateFormat("dd-MM-yyyy HH:mm", Locale("es", "CL")) }
    val issuedText = remember { dateFormat.format(Date()) }

    val isValidated = validation != null && validation.attemptsAtValidation == attempts.size
    val validationText = if (validation == null) {
        "Pendiente de validación docente"
    } else if (validation.attemptsAtValidation != attempts.size) {
        "Requiere nueva validación (hay cambios en las evaluaciones)"
    } else {
        "Validado por " + validation.validatedBy + " el " + dateFormat.format(Date(validation.validatedAt))
    }

    val strengths = performances.filter { it.isStrength }
    val toReinforce = performances.filter { !it.isStrength }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Column {
                Text(
                    text = "Informe de desempeño 🎓",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = student.fullName,
                    fontSize = 14.sp,
                    color = Color(0xFF00A8E8),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Emitido: " + issuedText,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 460.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Aviso: el informe apoya al docente, no decide por él
                Surface(
                    color = Color(0xFFFFF9E6),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Informe reservado de apoyo docente. No es un diagnóstico ni una decisión académica automática: debe ser revisado y validado por el docente.",
                        fontSize = 11.sp,
                        color = Color(0xFF7C5E10),
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                ReportSectionTitle("Rendimiento por Objetivo de Aprendizaje")
                if (performances.isEmpty()) {
                    Text(
                        text = "Aún no hay respuestas asociadas a un OA.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                } else {
                    performances.forEach { performance ->
                        OaPerformanceRow(performance, titles[performance.objectiveCode].orEmpty())
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                ReportSectionTitle("Fortalezas")
                if (strengths.isEmpty()) {
                    Text(text = "Sin fortalezas identificadas todavía.", fontSize = 13.sp, color = Color.Gray)
                } else {
                    strengths.forEach {
                        Text(
                            text = "✅ " + oaLabel(it.objectiveCode, titles[it.objectiveCode].orEmpty()),
                            fontSize = 13.sp,
                            color = Color(0xFF334155)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                ReportSectionTitle("Aspectos a reforzar")
                if (toReinforce.isEmpty()) {
                    Text(text = "Ningún aspecto pendiente de refuerzo.", fontSize = 13.sp, color = Color.Gray)
                } else {
                    toReinforce.forEach {
                        Text(
                            text = "📌 " + oaLabel(it.objectiveCode, titles[it.objectiveCode].orEmpty()),
                            fontSize = 13.sp,
                            color = Color(0xFF334155)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Estado de validación del docente
                Surface(
                    color = if (isValidated) Color(0xFFE6F9F3) else Color(0xFFFFF4D6),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Estado: " + validationText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (isValidated) {
                            OutlinedButton(
                                onClick = onRevoke,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Quitar validación", fontSize = 12.sp)
                            }
                        } else {
                            Button(
                                onClick = onValidate,
                                enabled = attempts.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06D6A0)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("✅ Validar informe", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Compartir el informe como texto simple (no necesita permisos)
                OutlinedButton(
                    onClick = {
                        val reportText = ReportRules.buildReportText(
                            studentName = student.fullName,
                            issuedAt = issuedText,
                            validationStatus = validationText,
                            performances = performances,
                            titles = titles
                        )
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Informe de desempeño - " + student.fullName)
                            putExtra(Intent.EXTRA_TEXT, reportText)
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Compartir informe"))
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📤 Compartir informe (texto)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(14.dp))
                ReportSectionTitle("Evaluaciones realizadas")
                if (attempts.isEmpty()) {
                    Text(
                        text = "El estudiante aún no ha realizado ninguna evaluación.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                attempts.forEach { attempt ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text(
                                text = attempt.subjectId + " (Intento #" + attempt.attemptNumber + ")",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF1E293B)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Surface(
                                color = if (attempt.isCorrect) Color(0xFFE6F9F3) else Color(0xFFFFEBEF),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = (if (attempt.isCorrect) "Aprobado" else "Refuerzo") + " (" + attempt.score + "/" + attempt.totalQuestions + ")",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (attempt.isCorrect) Color(0xFF06D6A0) else Color(0xFFEF476F),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            if (attempt.detailsSummary.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Detalle de respuestas:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                                Text(
                                    text = attempt.detailsSummary,
                                    fontSize = 11.sp,
                                    color = Color(0xFF334155),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = { onUnlockSubject(student.fullName, attempt.subjectId) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("🔓 Desbloquear Asignatura (Reiniciar Intentos)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    )
}

private fun oaLabel(code: String, title: String): String {
    return if (title.isBlank()) code else code + " · " + title
}

@Composable
private fun ReportSectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1E293B),
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun OaPerformanceRow(performance: ObjectivePerformance, title: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = oaLabel(performance.objectiveCode, title),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF334155)
        )
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { performance.correct.toFloat() / performance.total.coerceAtLeast(1) },
            color = if (performance.isStrength) Color(0xFF06D6A0) else Color(0xFFEF476F),
            trackColor = Color(0xFFE2E8F0),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
        Text(
            text = performance.correct.toString() + " de " + performance.total + " respuestas correctas (" + performance.percent + "%)",
            fontSize = 11.sp,
            color = Color.Gray
        )
    }
}
