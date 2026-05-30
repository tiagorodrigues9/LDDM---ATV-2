package com.fatec.at2_base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fatec.at2_base.games.Game
import com.fatec.at2_base.games.GameApi
import com.fatec.at2_base.games.NewGameRequest
import kotlinx.coroutines.launch

private enum class Screen {
    LIST,
    FORM,
}

@Composable
fun App() {
    MaterialTheme {
        val baseUrl = "http://10.0.2.2:8080"
        val api = remember { GameApi(baseUrl) }
        val scope = rememberCoroutineScope()

        val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

        var isLoading by remember { mutableStateOf(true) }
        var games by remember { mutableStateOf<List<Game>>(emptyList()) }

        var title by remember { mutableStateOf("") }
        var platform by remember { mutableStateOf("") }
        var genre by remember { mutableStateOf("") }
        var yearText by remember { mutableStateOf("") }
        var currentScreen by remember { mutableStateOf(Screen.LIST) }

        fun refresh() {
            scope.launch {
                try {
                    isLoading = true
                    games = api.listGames()
                } catch (t: Throwable) {
                    snackbarHostState.showSnackbar("Erro ao carregar: ${t.message ?: "desconhecido"}")
                } finally {
                    isLoading = false
                }
            }
        }

        LaunchedEffect(Unit) { refresh() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Cadastro de Jogos", style = MaterialTheme.typography.headlineSmall)
            SnackbarHost(hostState = snackbarHostState)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { currentScreen = Screen.LIST }) {
                    Text("Tela de lista")
                }
                Button(onClick = { currentScreen = Screen.FORM }) {
                    Text("Tela de cadastro")
                }
            }

            when (currentScreen) {
                Screen.FORM -> {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text("Novo jogo", style = MaterialTheme.typography.titleMedium)
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Título") },
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = platform,
                                onValueChange = { platform = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Plataforma") },
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = genre,
                                onValueChange = { genre = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Gênero") },
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = yearText,
                                onValueChange = { yearText = it.filter { c -> c.isDigit() }.take(4) },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Ano") },
                                singleLine = true,
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        val year = yearText.toIntOrNull()
                                        if (title.isBlank() || platform.isBlank() || genre.isBlank() || year == null) {
                                            scope.launch { snackbarHostState.showSnackbar("Preencha todos os campos corretamente.") }
                                            return@Button
                                        }
                                        scope.launch {
                                            try {
                                                api.createGame(
                                                    NewGameRequest(
                                                        title = title,
                                                        platform = platform,
                                                        genre = genre,
                                                        year = year,
                                                    ),
                                                )
                                                title = ""
                                                platform = ""
                                                genre = ""
                                                yearText = ""
                                                snackbarHostState.showSnackbar("Jogo cadastrado com sucesso!")
                                                refresh()
                                                currentScreen = Screen.LIST
                                            } catch (t: Throwable) {
                                                snackbarHostState.showSnackbar("Erro ao cadastrar: ${t.message ?: "desconhecido"}")
                                            }
                                        }
                                    },
                                ) {
                                    Text("Cadastrar")
                                }
                            }
                        }
                    }
                }

                Screen.LIST -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { refresh() }) {
                            Text("Atualizar lista")
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("Jogos cadastrados (${games.size})", style = MaterialTheme.typography.titleMedium)
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(games) { g ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(g.title, style = MaterialTheme.typography.titleMedium)
                                        Text("${g.platform} • ${g.genre} • ${g.year}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}