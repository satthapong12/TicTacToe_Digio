package com.example.tictactoe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tictactoe.Database.AppDatabase
import com.example.tictactoe.Database.GameResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

enum class Win {
    PLAYER,
    AI,
    DRAW
}
@Composable
fun TicTacToeScreen(navController: NavController) {
    val timestamp = System.currentTimeMillis()
    val context = LocalContext.current
    val dateString = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    val database = AppDatabase.getDatabase(context)
    //true player turn
    // false - AI turn
    val playerTurn = remember { mutableStateOf(true) }
    val moves = remember {
        mutableStateListOf<Boolean?>(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }

    val win = remember {
        mutableStateOf<Win?>(null)
    }

    val onTap: (Offset) -> Unit = {
        if (playerTurn.value && win.value == null){
            val x = (it.x / 333).toInt()
            val y = (it.y / 333).toInt()
            val posInMoves = y * 3 + x
            if (moves[posInMoves] == null){
                moves[posInMoves] = true
                playerTurn.value = false
                win.value = ckectGameOver(moves)
            }
        }
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Tic Tac Toe", fontSize = 30.sp, modifier = Modifier.padding(16.dp))
        Header(playerTurn.value)
        Board(moves,onTap)
        //Ai moves
        if (!playerTurn.value && win.value == null){
            CircularProgressIndicator(color = Color.Red, modifier = Modifier.padding(16.dp))
            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(key1 = Unit ){
                coroutineScope.launch {
                    delay(1500L)
                    while (true){
                        val i = Random.nextInt(9)
                        if (moves[i] == null){
                            moves[i] = false
                            playerTurn.value=true
                            win.value = ckectGameOver(moves)
                            break
                        }
                    }
                }
            }
        }
        if (win.value != null) {
            when (win.value) {
                Win.PLAYER -> {
                    LaunchedEffect(Unit) {
                        withContext(Dispatchers.IO) {
                            database.gameResultDao().insertGameResult(GameResult(winner = Win.PLAYER, timestamp = dateString))
                        }
                    }
                    Text(text = "Player Win ", fontSize = 25.sp)
                }
                Win.AI -> {
                    LaunchedEffect(Unit) {
                        withContext(Dispatchers.IO) {
                            database.gameResultDao().insertGameResult(GameResult(winner = Win.AI, timestamp = dateString))
                        }
                    }
                    Text(text = "AI Win ", fontSize = 25.sp)
                }
                Win.DRAW -> {
                    LaunchedEffect(Unit) {
                        withContext(Dispatchers.IO) {
                            database.gameResultDao().insertGameResult(GameResult(winner = Win.DRAW, timestamp = dateString))
                        }
                    }
                    Text(text = "It's a draw", fontSize = 25.sp)
                }
                else -> {}
            }

            Button(onClick = {
                playerTurn.value = true
                win.value = null
                for (i in 0..8) {
                    moves[i] = null
                }
            }) {
                Text(text = "Reset Game")
            }
        }

        Column (horizontalAlignment = Alignment.CenterHorizontally){
            Button(onClick = {navController.navigate(NavigationItem.History.route)
            }) {
                Text(text = "View History")
            }

        }
    }


}

fun ckectGameOver(m: List<Boolean?>): Win? {
    var win: Win? = null
    if(m[0] == true && m[1] == true && m[2] == true
        || m[3] == true && m[4] == true && m[5] == true
        || m[6] == true && m[7] == true && m[8] == true
        || m[0] == true && m[3] == true && m[6] == true
        || m[1] == true && m[4] == true && m[7] == true
        || m[2] == true && m[5] == true && m[8] == true
        || m[0] == true && m[4] == true && m[8] == true
        || m[2] == true && m[4] == true && m[6] == true
        )
        win = Win.PLAYER

    if(m[0] == false && m[1] == false && m[2] == false
        || m[3] == false && m[4] == false && m[5] == false
        || m[6] == false && m[7] == false && m[8] == false
        || m[0] == false && m[3] == false && m[6] == false
        || m[1] == false && m[4] == false && m[7] == false
        || m[2] == false && m[5] == false && m[8] == false
        || m[0] == false && m[4] == false && m[8] == false
        || m[2] == false && m[4] == false && m[6] == false
    )
        win = Win.AI

    if (win == null){
        var available = false
        for (i in 0..8){
            if (m[i] == null)
                available = true
        }
        if (!available){
            win = Win.DRAW
        }
    }
    return win
}

@Composable
fun Header(playerTurn: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val playerBoxColor = if (playerTurn) Color.Blue else Color.LightGray
        val aiBoxColor = if (playerTurn) Color.LightGray else Color.Red

        Box(
            modifier = Modifier
                .width(100.dp)
                .background(playerBoxColor)
        ) {
            Text(
                text = "Player", modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.width(50.dp))
        Box(
            modifier = Modifier
                .width(100.dp)
                .background(aiBoxColor)
        ) {
            Text(
                text = "AI", modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Center)
            )
        }
    }
}


@Composable
fun Board(moves: List<Boolean?>, onTap: (Offset) -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(32.dp)
            .background(Color.LightGray)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        onTap(offset)
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(3) { i ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { j ->
                        Box(
                            modifier = Modifier
                                .size(103.dp)
                                .background(Color.Black.copy(alpha = 0.8f))
                        ) {
                            getImage(move = moves[i * 3 + j])
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun getImage(move: Boolean?) {
    when (move) {
        true -> Image(
            painter = painterResource(id = R.drawable.ic_x),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(Color.Blue)
        )

        false -> Image(
            painter = painterResource(id = R.drawable.baseline_brightness_1_24),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(Color.Red)
        )
        null -> Image(
            painter = painterResource(id = R.drawable.ic_null),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(Color.Blue)
        )
    }
}


