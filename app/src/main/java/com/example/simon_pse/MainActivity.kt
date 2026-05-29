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
import androidx.compose.ui.text.style.TextOverflow
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
    HISTORY,                                                                                        // Lista delle Partite
    GAME,                                                                                           // Schermata di Gioco
    GAME_DETAIL                                                                                     // Dettaglio Partita
}

//Modulo di navigazioine
@Composable
fun SimonApp(modifier: Modifier = Modifier) {
    var currentScreen by rememberSaveable {mutableStateOf(AppScreen.HISTORY)}                // Screen state
    var games by rememberSaveable { mutableStateOf(listOf<String>()) }

    when (currentScreen) {
        AppScreen.GAME -> {
            GameScreen(
                modifier = modifier,
                onEndGame = { gameString ->
                    games += gameString // add the game to the games history
                    currentScreen = AppScreen.HISTORY
                }
            )
        }
        AppScreen.HISTORY -> {
            GamesList(
                modifier = modifier,
                games = games,
                backButton = {
                    currentScreen = AppScreen.GAME
                }
            )
        }
        AppScreen.GAME_DETAIL -> {
            GameDetailsScreen()
        }
    }
}

//Schermata di gioco
@Composable
fun GameScreen(                                                                                     //SCHERMATA Schermata di gioco
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
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ColorButtonsGrid(updateText = { newText -> displayText += newText })
            }

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomGameScreen(
                    displayText,
                    clearText = { displayText = "" },
                    endGame = {
                        onEndGame(displayText)
                        displayText = ""
                    }
                )
            }
        }
    }

    if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Row( modifier = modifier.fillMaxSize() ) {
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
                BottomGameScreen(
                    displayText,
                    clearText = { displayText = "" },
                    endGame = {
                        onEndGame(displayText)
                        displayText = ""
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
fun ColorButtonsGrid(updateText: (String) -> Unit) {                                                //matrice di 6 bottoni colore(macro componente)
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
fun BottomGameScreen(text: String, clearText: () -> Unit, endGame: () -> Unit) {                    //porzione inferiore schermata di gioco(macro componente)
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
            StandardButton(stringResource(R.string.clear_button), Color.Gray) {clearText()}
            StandardButton(stringResource(R.string.eog_button), Color.Gray) {endGame()}
        }
    }
}
//Schermata di gioco FINE

//Lista delle partite
@Composable
fun Example(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
    ) {
        Icon(Icons.Filled.Add, "Floating action button.")
    }
}

@Composable
fun GamesList(                                                                                      //SCHERMATA Lista delle partite
    modifier: Modifier = Modifier,
    games: List<String>,
    backButton: () -> Unit
) {
    val orientation = LocalConfiguration.current.orientation
    BackHandler(onBack = backButton)

    if(orientation == Configuration.ORIENTATION_PORTRAIT) {
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = modifier.fillMaxSize(),
                //horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    GamesTable(games)
                }
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    //StandardButton(stringResource(R.string.back_button), Color.Gray) {backButton()}
                    //Example(backButton)
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp) // Distanza di sicurezza dai bordi dello schermo
            ) {
                Example(onClick = backButton)
            }
        }
    }
    if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Box(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = modifier.fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    GamesTable(games)
                }
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    //StandardButton(stringResource(R.string.back_button), Color.Gray) {backButton()}
                    //Example(backButton)
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp) // Distanza di sicurezza dai bordi dello schermo
            ) {
                Example(onClick = backButton)
            }
        }
    }
}

@Composable
fun GamesTable(games: List<String>) {
    Column(
        modifier = Modifier.fillMaxSize()

    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(games) { game ->
                val buttonsClicked: Int = game.count { it == ',' }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                        Alignment.CenterHorizontally
                    )
                ) {
                    Text(
                        text = "$buttonsClicked",
                        modifier = Modifier.weight(0.3f)
                    )

                    Text(
                        text = game,
                        modifier = Modifier.weight(0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
//Lista delle partite FINE

//Dettaglio Partita
@Composable
fun GameDetailsScreen() {                                                                           //SCHRERMATA Dettaglio Partita
    Text("Resoconto Partita")
}
//Dettaglio Partita FINE

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardPreview() {
    Simon_PSETheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            SimonApp(modifier = Modifier.padding(innerPadding))
        }
    }
}

