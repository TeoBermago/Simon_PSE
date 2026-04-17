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
import com.example.simon_pse.ui.theme.Simon_PSETheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Set and display the UI content
        setContent {
            Simon_PSETheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

}


@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    Column() {
        Row(
            modifier = modifier.fillMaxWidth(). fillMaxHeight(0.5f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColorButtonsGrid()
        }

        Row(
            modifier = modifier.fillMaxWidth(). fillMaxHeight(0.5f),
            //verticalAlignment = Alignment.CenterVertically
        ) {
            BottomScreen1()
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
        // Set a standard size \
        modifier = Modifier
            .width(150.dp)
            .height(70.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.Black // Text color

        )
    ) {
        Text(text)
    }
}

@Composable
fun ColorButtonsGrid() {
    val buttons = listOf(
        ButtonData(stringResource(R.string.red), Color.Red, {}),
        ButtonData(stringResource(R.string.green), Color.Green, {}),
        ButtonData(stringResource(R.string.blue), Color.Blue, {}),
        ButtonData(stringResource(R.string.magenta), Color.Magenta, {}),
        ButtonData(stringResource(R.string.yellow), Color.Yellow, {}),
        ButtonData(stringResource(R.string.cyan), Color.Cyan, {})
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        // Dividiamo la lista in righe da 2
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
fun BottomScreen1() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(

            text = "banana"
        )
        Row(

            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            StandardButton("Cancella", Color.Gray, {})
            StandardButton("Fine Partita", Color.Gray, {})        }
    }

}




@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardPreview() {
    Simon_PSETheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            GameScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}

