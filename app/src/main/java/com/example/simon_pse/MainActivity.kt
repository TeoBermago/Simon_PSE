package com.example.simon_pse


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.simon_pse.ui.theme.Simon_PSETheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Set and display the UI content
        setContent {
            Simon_PSETheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SimonApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

}
enum class AppScreen {
    GAME,   // Screen 1
    HISTORY // Screen 2
}


@Composable
fun SimonApp(modifier: Modifier = Modifier) {
    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.GAME)} // Screen state
    val games = rememberSaveable { mutableListOf<String>() }

    when (currentScreen) {
        AppScreen.GAME -> {
            GameScreen(
                modifier = modifier,
                onEndGame = { gameString ->
                    games.add(gameString) // add the game to the games history
                    currentScreen = AppScreen.HISTORY
                }
            )
        }
        AppScreen.HISTORY -> {




        }


    }
}

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    onEndGame: (String) -> Unit
) {
    val orientation = LocalConfiguration.current.orientation
    var displayText by rememberSaveable { mutableStateOf("") }

    if(orientation == Configuration.ORIENTATION_PORTRAIT) {
        Column(
            modifier = modifier.fillMaxWidth().fillMaxHeight()
        ) {
            Row(
                modifier = modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ColorButtonsGrid(updateText = { newText -> displayText += newText })
            }

            Row(
                modifier = modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomScreen1(
                    displayText,
                    clearText = { displayText = "" },
                    endGame = {
                        if (displayText.isNotEmpty()) {
                            onEndGame(displayText)
                            displayText = ""
                        }
                    }
                )
            }
        }
    }

    if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Row( modifier = modifier.fillMaxWidth().fillMaxHeight() ) {
            Column(
                modifier = Modifier.fillMaxHeight().weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                ColorButtonsGrid(updateText = { newText -> displayText += newText })
            }

            Column(
                modifier = Modifier.fillMaxHeight().weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                BottomScreen1(
                    displayText,
                    clearText = { displayText = "" },
                    endGame = {
                        if (displayText.isNotEmpty()) {
                            onEndGame(displayText)
                            displayText = ""
                        }
                    }
                )
            }
        }
    }
}

data class ButtonData(
    val text: String,
    val backgroundColor: Color,
    val onClick: () -> Unit
)

@Composable
fun StandardButton(text: String, backgroundColor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        // Set a standard size
        modifier = Modifier.width(120.dp).height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.Black // Text color

        )
    ) {
        Text(text)
    }
}

@Composable
fun ColorButtonsGrid(updateText: (String) -> Unit) {
    val buttons = listOf(
        ButtonData(stringResource(R.string.red), Color.Red) { updateText("R, ") },
        ButtonData(stringResource(R.string.green), Color.Green) {updateText("G, ")},
        ButtonData(stringResource(R.string.blue), Color.Blue) {updateText("B, ")},
        ButtonData(stringResource(R.string.magenta), Color.Magenta) {updateText("M, ")},
        ButtonData(stringResource(R.string.yellow), Color.Yellow ) {updateText("Y, ")},
        ButtonData(stringResource(R.string.cyan), Color.Cyan ) {updateText("C, ")}
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        // Divide the list in rows of 2
        buttons.chunked(2).forEach { riga ->
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                riga.forEach { (title, color, action) ->
                    StandardButton(
                        text = title,
                        backgroundColor = color,
                        onClick = action
                    )
                }
            }
        }
    }

}

@Composable
fun BottomScreen1(text: String, clearText: () -> Unit, endGame: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            StandardButton(stringResource(R.string.clear), Color.Gray) {clearText()}
            StandardButton(stringResource(R.string.eog), Color.Gray) {endGame()}
        }
    }
}


/*@Composable
fun HistoryScreen() {
    Column() {
        Row

    }
}*/




@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardPreview() {
    Simon_PSETheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            SimonApp(modifier = Modifier.padding(innerPadding))
        }
    }
}

