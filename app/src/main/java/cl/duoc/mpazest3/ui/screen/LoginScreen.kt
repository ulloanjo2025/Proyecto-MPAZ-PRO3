// Desarrollado por: Jonathan Ulloa (ui/screen)
package cl.duoc.mpazest3.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.mpazest3.viewmodel.AuthViewModel

// Colores institucionales del Colegio Marcela Paz
val SkyBlue = Color(0xFF00A8E8)
val SunYellow = Color(0xFFFFD166)
val GrassGreen = Color(0xFF06D6A0)
val SoftBackground = Color(0xFFF4F9FC)

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginStudent: () -> Unit
) {
    val username by authViewModel.username.collectAsState()
    val password by authViewModel.password.collectAsState()
    val usernameError by authViewModel.usernameError.collectAsState()
    val passwordError by authViewModel.passwordError.collectAsState()

    var isTeacherMode by remember { mutableStateOf(false) }
    var teacherEmail by remember { mutableStateOf("") }
    var teacherRut by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SoftBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Emblema e Insignia Ilustrada
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(4.dp, SkyBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🏔️☀️", fontSize = 28.sp)
                    Text(
                        text = "E-764",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SkyBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Colegio Escritora Marcela Paz",
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "MPAZ PRO 3º",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Selector de Modo (Estudiante / Docente)
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .border(2.dp, SkyBlue.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { isTeacherMode = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isTeacherMode) SkyBlue else Color.Transparent,
                        contentColor = if (!isTeacherMode) Color.White else Color.Gray
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🎒 Estudiante", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { isTeacherMode = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isTeacherMode) SkyBlue else Color.Transparent,
                        contentColor = if (isTeacherMode) Color.White else Color.Gray
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("👩‍🏫 Docente", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Formulario dentro de una Tarjeta
            Card(
                modifier = Modifier.fillMaxWidth(0.92f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!isTeacherMode) {
                        Text(
                            text = "¡Hola! Ingresa para jugar y aprender 🚀",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = username,
                            onValueChange = { authViewModel.onUsernameChanged(it) },
                            label = { Text("Código de Estudiante (ej: JP01)") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SkyBlue) },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SkyBlue,
                                unfocusedBorderColor = Color.LightGray
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { authViewModel.onPasswordChanged(it) },
                            label = { Text("PIN Secreto (4 dígitos)") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SkyBlue) },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SkyBlue,
                                unfocusedBorderColor = Color.LightGray
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onLoginStudent,
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            Text("¡Ingresar a Aprender! ✨", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            text = "Acceso Exclusivo para Profesores",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = username,
                            onValueChange = { authViewModel.onUsernameChanged(it) },
                            label = { Text("Nombre Completo") },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = teacherEmail,
                            onValueChange = { teacherEmail = it },
                            label = { Text("Correo (@mpaz.cl)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = teacherRut,
                            onValueChange = { teacherRut = it },
                            label = { Text("RUT (ej: 12345678-K)") },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                authViewModel.validateAndLoginTeacher(
                                    fullName = username,
                                    email = teacherEmail,
                                    rut = teacherRut
                                )
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GrassGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            Text("Validar e Ingresar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Mensajes de error animados
                    AnimatedVisibility(visible = usernameError != null || passwordError != null) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            usernameError?.let {
                                Text(text = "⚠️ $it", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            passwordError?.let {
                                Text(text = "🔑 $it", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}