// Desarrollado por: Camilo Alarcón (ui/components)
package cl.duoc.mpazest3.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

// Botón de reproducción de instrucciones por audio para soporte lúdico
@Composable
fun AudioPlayerButton(
    onPlayAudio: () -> Unit
) {
    IconButton(onClick = onPlayAudio) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Reproducir audio de instrucción"
        )
    }
}