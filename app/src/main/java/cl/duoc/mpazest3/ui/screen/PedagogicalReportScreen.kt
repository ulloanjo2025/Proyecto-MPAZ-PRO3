package cl.duoc.mpazest3.ui.screen
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.mpazest3.data.local.entity.StudentAttemptEntity
import cl.duoc.mpazest3.data.local.entity.StudentEntity

@Composable
fun PedagogicalReportScreen(
    attempts: List<StudentAttemptEntity>,
    students: List<StudentEntity>,
    onRegisterStudent: (String, String, String, (Boolean, String) -> Unit) -> Unit,
    onUnlockSubject: (studentName: String, subjectId: String) -> Unit,
    onLogout: () -> Unit
) {
    var showRegisterForm by remember { mutableStateOf(false) }
    var selectedStudentForProfile by remember { mutableStateOf<StudentEntity?>(null) }

    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    // Ordenamiento alfabético de alumnos
    val sortedStudents = remember(students) { students.sortedBy { it.fullName.lowercase() } }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Cabecera Docente
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Gestión Pedagógica 👩‍🏫",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Escuela Marcela Paz E-764",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF476F)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Salir", fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { showRegisterForm = !showRegisterForm }) {
                        Text(
                            text = if (showRegisterForm) "📋 Ver Diagnóstico Pedagógico" else "➕ Registrar Nuevo Alumno",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00A8E8)
                        )
                    }
                }
            }

            if (showRegisterForm) {
                // Formulario de Registro de Alumno
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Registrar Estudiante Oficial",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("Nombre Completo (ej: Juan Pérez)") },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = it },
                                label = { Text("Código de Usuario (ej: JP01)") },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = pin,
                                onValueChange = { if (it.length <= 4) pin = it },
                                label = { Text("PIN Secreto (4 dígitos)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (message.isNotEmpty()) {
                                Text(
                                    text = message,
                                    color = if (message.contains("éxito")) Color(0xFF06D6A0) else MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    onRegisterStudent(fullName, username, pin) { success, msg ->
                                        message = msg
                                        if (success) {
                                            fullName = ""
                                            username = ""
                                            pin = ""
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A8E8)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Guardar Estudiante", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Resumen Global del Curso
                item {
                    val totalEvaluations = attempts.size
                    val totalPassed = attempts.count { it.isCorrect }
                    val totalFailed = totalEvaluations - totalPassed

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📊 Resumen General del 3.º Básico",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0369A1)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Total de evaluaciones realizadas: $totalEvaluations", fontSize = 13.sp)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF06D6A0))
                                    Text(text = " Aprobados: $totalPassed", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Clear, contentDescription = null, tint = Color(0xFFEF476F))
                                    Text(text = " En Refuerzo: $totalFailed", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Lista de Estudiantes (${sortedStudents.size}) - Toca un alumno para ver su Diagnóstico",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Lista Ordenada de Estudiantes con Perfil Detallado
                items(sortedStudents) { student ->
                    val studentAttempts = attempts.filter { it.studentName.equals(student.fullName, ignoreCase = true) }

                    Card(
                        onClick = { selectedStudentForProfile = student },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(Color(0xFFE2E8F0), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF00A8E8))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = student.fullName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(
                                        text = "Código: ${student.username} | PIN: ${student.pin}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "${studentAttempts.size} evaluaciones",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00A8E8),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Modal / Diálogo con el Perfil Pedagógico del Alumno
        selectedStudentForProfile?.let { student ->
            val studentAttempts = attempts.filter { it.studentName.equals(student.fullName, ignoreCase = true) }

            AlertDialog(
                onDismissRequest = { selectedStudentForProfile = null },
                confirmButton = {
                    TextButton(onClick = { selectedStudentForProfile = null }) {
                        Text("Cerrar", fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Column {
                        Text(
                            text = "Diagnóstico Pedagógico 🎓",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = student.fullName,
                            fontSize = 14.sp,
                            color = Color(0xFF00A8E8),
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                text = {
                    LazyColumn(modifier = Modifier.height(350.dp)) {
                        if (studentAttempts.isEmpty()) {
                            item {
                                Text(
                                    text = "El estudiante aún no ha realizado ninguna evaluación.",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(vertical = 20.dp)
                                )
                            }
                        } else {
                            items(studentAttempts) { attempt ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    ) {
                                        // 1. Título de la asignatura en su propio renglón arriba
                                        Text(
                                            text = "${attempt.subjectId} (Intento #${attempt.attemptNumber})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF1E293B),
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // 2. Estado (Aprobado/Refuerzo) en su propio renglón estrictamente abajo
                                        Surface(
                                            color = if (attempt.isCorrect) Color(0xFFE6F9F3) else Color(0xFFFFEBEF),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = if (attempt.isCorrect) "Aprobado (${attempt.score}/${attempt.totalQuestions})" else "Refuerzo (${attempt.score}/${attempt.totalQuestions})",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (attempt.isCorrect) Color(0xFF06D6A0) else Color(0xFFEF476F),
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }

                                        if (attempt.detailsSummary.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Desglose para Refuerzo:",
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
                                            onClick = {
                                                onUnlockSubject(student.fullName, attempt.subjectId)
                                            },
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
                }
            )
        }
    }
}