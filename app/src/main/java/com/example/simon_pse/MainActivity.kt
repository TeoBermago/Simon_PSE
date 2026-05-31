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
import androidx.compose.runtime.remember
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
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.rememberCoroutineScope
import androidx.room.Room
import com.example.simon_pse.data.GameEntity
import com.example.simon_pse.data.GameDao
import com.example.simon_pse.data.GameDatabase
import kotlinx.coroutines.flow.emptyFlow
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.simon_pse.ui.theme.Simon_PSETheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.drop
import kotlin.collections.joinToString
import kotlin.collections.take

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val database = Room.databaseBuilder(
            applicationContext,
            GameDatabase::class.java,
            "simon_database" // The name of the physical file on the phone
        ).build()

        val gameDao = database.gameDao()

        // Set and display the UI content
        setContent {
            Simon_PSETheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SimonApp(modifier = Modifier.padding(innerPadding), gameDao)
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
fun SimonApp(modifier: Modifier = Modifier, gameDao: GameDao) {
    var currentScreen by rememberSaveable {mutableStateOf(AppScreen.HISTORY)}                // Screen state
    var selectedGame by remember { mutableStateOf<GameEntity?>(null) }

    when (currentScreen) {
        AppScreen.GAME -> {
            GameScreen(
                modifier = modifier,
                onEndGame = {
                    currentScreen = AppScreen.HISTORY // Cambia schermata
                },
                gameDao = gameDao
            )
        }
        AppScreen.HISTORY -> {
            GamesList(
                modifier = modifier,
                gameButton = {
                    currentScreen = AppScreen.GAME
                },
                onGameClick = { clickedGame ->
                    selectedGame = clickedGame // Salvo la partita
                    currentScreen = AppScreen.GAME_DETAIL // Cambio pagina
                },
                gameDao = gameDao
            )
        }
        AppScreen.GAME_DETAIL -> {
            selectedGame?.let { game ->
                GameDetailsScreen(
                    modifier = modifier,
                    game = game,
                    gameButton = { currentScreen = AppScreen.HISTORY }
                )
            }
        }
    }
}

//Schermata di gioco
@Composable
fun GameScreen(                                                                                     //SCHERMATA Schermata di gioco
    modifier: Modifier = Modifier,
    onEndGame: () -> Unit,
    gameDao: GameDao
) {
    val orientation = LocalConfiguration.current.orientation
    var displayText by rememberSaveable { mutableStateOf("") }
    var generatedSequence by rememberSaveable { mutableStateOf(listOf<Char>()) }             //sequenza di bottoni da premere
    var userClickIndex by rememberSaveable { mutableIntStateOf(0) }                          //indice posizione nella sequenza generata
    var isGameStarted by rememberSaveable { mutableStateOf(false) }                          //stato della partita(attiva o no)
    var isComputerTurn by rememberSaveable { mutableStateOf(false) }                         //il computer sta mostrando una sequenza
    var isGamePaused by rememberSaveable { mutableStateOf(false) }                           //partita in pausa
    var litColor by remember { mutableStateOf<Char?>(null) }
    var showGameOverDialog by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()


    val onColorClick: (Char) -> Unit = { color ->                                                   //filtro/logica tabella bottoni
        if(isGameStarted && !isComputerTurn && !isGamePaused) {                                     //partita in corso e non in pausa
            if (color == generatedSequence[userClickIndex]) {                                       //pressione tasto corretto
                displayText += "$color, "
                if(++userClickIndex == generatedSequence.size) {                                    //l'utente ha completato la sequenza(già corretta)
                    generatedSequence = generatedSequence + generateRandomColor()
                    userClickIndex = 0
                    isComputerTurn = true // Passa il turno al computer
                }
            }
            else {
                isGameStarted = false
                showGameOverDialog = true // Fa apparire il popup di errore
            }
        }
    }

    LaunchedEffect(isComputerTurn) {
        if (isComputerTurn) {
            delay(500)
            displayText = "" // Il testo è vuoto durante la proposta

            for (color in generatedSequence) {
                if (!isGameStarted) break
                while (isGamePaused) delay(100)

                litColor = color // Colore da accendere
                delay(800)
                litColor = null  // Ritornan normale
                delay(300)
            }

            isComputerTurn = false // Passa il turno al giocatore
        }
    }

    val saveAndEndGame = {
        isGameStarted = false

        if (generatedSequence.size > 1 || (generatedSequence.size == 1 && !isComputerTurn)) {
            val errorIdx = userClickIndex
            val streak = if (generatedSequence.isEmpty()) 0 else generatedSequence.size - 1

            scope.launch(Dispatchers.IO) {
                val newGame = GameEntity(
                    sequence = generatedSequence.toString(),
                    errorIndex = errorIdx,
                    longestStreak = streak
                )
                gameDao.insertGame(newGame)
            }
        }

        onEndGame()
        displayText = ""
    }
    BackHandler(enabled = true) {
        saveAndEndGame()
    }

    if (showGameOverDialog) {
        GameOverDialog(
            score = if (generatedSequence.isEmpty()) 0 else generatedSequence.size - 1,
            onDismiss = {
                showGameOverDialog = false
                saveAndEndGame()
            }
        )
    }

    if(orientation == Configuration.ORIENTATION_PORTRAIT) {     
        Column(
            modifier = modifier.fillMaxWidth().fillMaxHeight()
        ) {
            Row(
                modifier = modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ColorButtonsGrid(
                    onColorClick = onColorClick,
                    litColor
                )
            }

            Row(
                modifier = modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomGameScreen(
                    displayText,
                    pause = {pauseState -> isGamePaused = pauseState},
                    endGame = {saveAndEndGame()},
                    startGame = {
                        isGameStarted = true
                        generatedSequence = listOf(generateRandomColor())
                        userClickIndex = 0
                        isComputerTurn = true
                    },
                    isGameStarted,
                    isGamePaused,
                    isComputerTurn
                )
            }
        }
    }

    if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Row( modifier = modifier.fillMaxSize() ) {
            Column(
                modifier = modifier.fillMaxHeight().weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                ColorButtonsGrid(
                    onColorClick = onColorClick,
                    litColor
                )
            }

            Column(
                modifier = modifier.fillMaxHeight().weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                BottomGameScreen(
                    displayText,
                    pause = {pauseState -> isGamePaused = pauseState },
                    endGame = {saveAndEndGame()},
                    startGame = {
                        isGameStarted = true
                        generatedSequence = listOf(generateRandomColor())
                        userClickIndex = 0
                        isComputerTurn = true
                    },
                    isGameStarted,
                    isGamePaused,
                    isComputerTurn
                )
            }
        }
    }
}

data class ButtonData(
    val text: String,
    val backgroundColor: Color,
    val id: Char,
    val onClick: () -> Unit
)

@Composable
fun StandardButton(
        text: String,
        backgroundColor: Color,
        enabled: Boolean = true,
        isHighlighted: Boolean = false, // Evidenzia il bottone quando mostra la sequenza
        onClick: () -> Unit) {
    if(isHighlighted){
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            border = BorderStroke(4.dp, backgroundColor),
            modifier = Modifier.width(120.dp).height(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = backgroundColor, // Text color
            )
        ) {
            Text(text)
        }
    }
    else {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.width(120.dp).height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = Color.Black, // Text color
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.DarkGray
            )
        ) {
            Text(text)
        }
    }
}

@Composable
fun ColorButtonsGrid(onColorClick: (Char) -> Unit, litColor: Char?) {                               //matrice di 6 bottoni colore(macro componente)
    val buttons = listOf(
        ButtonData(stringResource(R.string.red), Color.Red, 'R') { onColorClick('R') },
        ButtonData(stringResource(R.string.green), Color.Green, 'G') {onColorClick('G')},
        ButtonData(stringResource(R.string.blue), Color.Blue, 'B') {onColorClick('B')},
        ButtonData(stringResource(R.string.magenta), Color.Magenta, 'M') {onColorClick('M')},
        ButtonData(stringResource(R.string.yellow), Color.Yellow, 'Y') {onColorClick('Y')},
        ButtonData(stringResource(R.string.cyan), Color.Cyan, 'C') {onColorClick('C')}
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
                riga.forEach { (title, color, id, action) ->
                    StandardButton(
                        text = title,
                        backgroundColor = color,
                        isHighlighted = (id == litColor),
                        onClick = action
                    )
                }
            }
        }
    }

}

//porzione inferiore schermata di gioco(macro componente)
@Composable
fun BottomGameScreen(text: String, pause: (Boolean) -> Unit, endGame: () -> Unit, startGame: () -> Unit, isGameStarted: Boolean, isGamePaused: Boolean, isComputerTurn: Boolean) {
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
            StandardButton(stringResource(R.string.sog_button), Color.Gray,!isGameStarted) {startGame()}
            if (!isGamePaused) {
                StandardButton(stringResource(R.string.pause_button), Color.Gray,isComputerTurn) {pause(true)}
            }
            else {
                StandardButton(stringResource(R.string.resume_button), Color.Gray,isComputerTurn) {pause(false)}
            }
            StandardButton(stringResource(R.string.eog_button), Color.Gray,isGameStarted) {endGame()}
        }
    }
}

fun generateRandomColor(): Char {
    val colors = listOf('R', 'G', 'B', 'M', 'Y', 'C')
    return colors.random()
}

@Composable
fun GameOverDialog(score: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss, // Se l'utente tocca fuori dal popup
        title = { Text("Game Over!") },
        text = { Text("Hai sbagliato! Colori indovinati: $score") },
        confirmButton = {
            Button(onClick = onDismiss) { Text("OK") }
        }
    )
}
//Schermata di gioco FINE

//Lista delle partite
@Composable
fun OpenGameScreen(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
    ) {
        Icon(Icons.Filled.Add, "Floating action button.")
    }
}

@Composable
fun GamesList(                                                                                      //SCHERMATA Lista delle partite
    modifier: Modifier = Modifier,
    gameButton: () -> Unit,
    onGameClick: (GameEntity) -> Unit,
    gameDao: GameDao
) {
    val orientation = LocalConfiguration.current.orientation

    val gamesList by gameDao.getAllGames().collectAsState(initial = emptyList())

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            OpenGameScreen(onClick = gameButton)
        }
    ){ innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        GamesTable(gamesList, onGameClick = onGameClick )
                    }
                }
            } else {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxHeight().weight(1f)) {
                        GamesTable(gamesList, onGameClick = onGameClick)
                    }
                }
            }
        }
    }
}

fun gameString (game: GameEntity): AnnotatedString {
    val sequence = game.sequence.split(',') //Split perchè il toString della lista di caratteri formatta [R, ...}
    val errorIndex = game.errorIndex

    val lastGameString = buildAnnotatedString {
        // Testo NERO (indovinato)
        append(sequence.take(errorIndex).joinToString(", "))

        if (errorIndex > 0 && errorIndex < sequence.size) {
            append(", ")
        }

        // Testo ROSSO (sbagliato)
        withStyle(style = SpanStyle(color = Color.Red)) {
            append(sequence.drop(errorIndex).joinToString(", "))
        }
    }

    return lastGameString
}

@Composable
fun GamesTable(
    games: List<GameEntity>,
    onGameClick: (GameEntity) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(games) { game ->
                val longestStreak: Int = game.longestStreak
                val lastGameString = gameString(game)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clickable { onGameClick(game) },
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                        Alignment.CenterHorizontally
                    )
                ) {
                    Text(
                        text = "$longestStreak",
                        modifier = Modifier.weight(0.3f)
                    )

                    Text(
                        text = lastGameString,
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
fun GameDetailsScreen(                                                                              //SCHRERMATA Dettaglio Partita
    modifier: Modifier = Modifier,
    game: GameEntity,
    gameButton: () -> Unit
) {
    BackHandler(enabled = true) {
        gameButton()
    }

    val orientation = LocalConfiguration.current.orientation
    val longestStreak: Int = game.longestStreak
    val lastGameString = gameString(game)

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->

        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        text = "$longestStreak",
                        modifier = Modifier.weight(0.3f)
                    )

                    Text(
                        text = lastGameString,
                        modifier = Modifier.weight(0.7f),
                    )
                }
            } else {
                Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        text = "$longestStreak",
                        modifier = Modifier.weight(0.3f)
                    )

                    Text(
                        text = lastGameString,
                        modifier = Modifier.weight(0.7f),
                    )
                }
            }
        }
    }
}
//Dettaglio Partita FINE

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardPreview() {

    val fakeDao = object : GameDao {
        override fun insertGame(game: GameEntity) { }
        override fun getAllGames() = emptyFlow<List<GameEntity>>() // Restituisce un flusso vuoto
    }

    Simon_PSETheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            SimonApp(modifier = Modifier.padding(innerPadding), gameDao = fakeDao)
        }
    }
}

