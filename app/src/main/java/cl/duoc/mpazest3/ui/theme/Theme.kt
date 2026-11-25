package cl.duoc.mpazest3.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
// Desarrollado por: Jonathan Ulloa (UI / Tema)
private val LightColorScheme = lightColorScheme(
    primary = AzulCieloPrimary,
    onPrimary = AzulCieloOnPrimary,
    primaryContainer = AzulCieloContainer,
    onPrimaryContainer = AzulCieloOnContainer,
    secondary = NaranjaSecundario,
    onSecondary = NaranjaOnSecundario,
    secondaryContainer = NaranjaContainer,
    tertiary = VerdeExito,
    tertiaryContainer = VerdeExitoContainer,
    error = CoralError,
    errorContainer = CoralErrorContainer,
    background = FondoLudico,
    onBackground = TextoOscuro,
    surface = SuperficieTarjeta,
    onSurface = TextoOscuro,
    onSurfaceVariant = TextoGrisVariante
)

val FormasLudicas = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun MpazEst3Theme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = FormasLudicas,
        content = content
    )
}