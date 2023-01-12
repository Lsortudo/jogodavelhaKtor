package com.example.models

import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.ConcurrentHashMap

class TicTacToeGame {
    private val state = MutableStateFlow(GameState())
    private val playerSockets = ConcurrentHashMap<Char, WebSocketSession>()
    private val gameScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun connectPlayer(session: WebSocketSession): Char? {
        val isPlayerX = state.value.connectedPlayers.any{it == 'X'}
        val player = if(isPlayerX) 'O' else 'X'

        state.update {
            if(state.value.connectedPlayers.contains(player)) {
                return null
            }
            if(!playerSockets.containsKey(player)) {
                playerSockets[player] = session
            }
            it.copy(
                connectedPlayers = it.connectedPlayers + player
            )
        }
        return player
    }
    fun disconnectPlayer(player: Char) {
        playerSockets.remove(player)
        state.update {
            it.copy(
                connectedPlayers = it.connectedPlayers - player
            )
        }
    }
}