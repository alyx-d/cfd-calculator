import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import calculator.ui.Calculator

@Composable
@Preview
fun App() {
    MaterialTheme(colors = lightColors()) {
        Calculator()
    }
}

fun main() = application {
    val state = rememberWindowState()
    state.size = DpSize(500.dp, 500.dp)
    Window(
        state = state,
        onCloseRequest = ::exitApplication,
        title = "Calculator",
    ) {
        App()
    }
}
