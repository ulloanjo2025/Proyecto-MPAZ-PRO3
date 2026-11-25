// Desarrollado por: Jonathan Ulloa (ui/components)
package cl.duoc.mpazest3.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.mpazest3.model.Subject

// Tarjeta visual interactiva para seleccionar una asignatura
@Composable
fun SubjectCard(
    subject: Subject,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = subject.title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = subject.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}