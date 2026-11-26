package cl.duoc.mpazest3.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.mpazest3.data.local.entity.StudentEntity

// UPDATE: el docente edita el nombre y el PIN de un estudiante (se envía al microservicio)
@Composable
fun EditStudentDialog(
    student: StudentEntity,
    onSave: (String, String, (Boolean, String) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    var fullName by remember { mutableStateOf(student.fullName) }
    var pin by remember { mutableStateOf(student.pin) }
    var message by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar estudiante ✏️", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(text = "Código: " + student.username, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it },
                    label = { Text("PIN (4 dígitos)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                if (message.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = message, fontSize = 12.sp, color = Color(0xFFEF476F))
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !saving,
                onClick = {
                    saving = true
                    message = ""
                    onSave(fullName, pin) { ok, resultMessage ->
                        saving = false
                        if (ok) onDismiss() else message = resultMessage
                    }
                }
            ) {
                Text(if (saving) "Guardando..." else "Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// DELETE: el docente confirma antes de eliminar a un estudiante y sus evaluaciones
@Composable
fun DeleteStudentDialog(
    student: StudentEntity,
    onConfirm: ((Boolean, String) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    var message by remember { mutableStateOf("") }
    var deleting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar estudiante 🗑️", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("¿Eliminar a " + student.fullName + " y todas sus evaluaciones? Esta acción no se puede deshacer.")
                if (message.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = message, fontSize = 12.sp, color = Color(0xFFEF476F))
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !deleting,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF476F)),
                onClick = {
                    deleting = true
                    message = ""
                    onConfirm { ok, resultMessage ->
                        deleting = false
                        if (ok) onDismiss() else message = resultMessage
                    }
                }
            ) {
                Text(if (deleting) "Eliminando..." else "Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
