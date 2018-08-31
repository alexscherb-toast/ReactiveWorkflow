package com.example.alex.reactiveworkflow

import android.arch.lifecycle.Observer
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

enum class StateOfPlay {
    PLAYING, VICTORY, DRAW
}

enum class MARK {
    EMPTY, X, O
}

data class Player(
    val id: String,
    val name: String
)

data class GameState(
    val id: String,
    val playerX: Player,
    val playerO: Player,
    val stateOfPlay: StateOfPlay,
    val grid: List<List<MARK>>,
    val activePlayerId: String
)

class GameRunner {
    sealed class Command {
        data class NewGame(val xPlayerName: String, val oPlayerName: String) : Command()
        data class RestoreGame(val clientId: String) : Command()
        data class TakeSquare(val row: Int, val column: Int) : Command()
        object End : Command()
    }

    fun asTransformer() =
        ObservableTransformer<Command, GameState> {
            it.map { command ->
                when (command) {
                    is Command.NewGame -> (handleNewGame())
                    else -> throw IllegalStateException("Unkown state")
                }
            }
        }

    private fun handleNewGame() =
        GameState(
            "",
            Player("", ""),
            Player("", ""), StateOfPlay.PLAYING, ArrayList(), ""
        )
}


abstract class WorkflowScreen<D, out E> protected constructor(
    val key: String,
    val screenData: Observable<D>,
    val eventHandler: E
)

class LoginScreen(
    errorMessage: Observable<String>,
    eventHandler: Events
) : WorkflowScreen<String, LoginScreen.Events>(KEY, errorMessage, eventSink) {
    companion object {
        val KEY = LoginScreen::class.java.simpleName
    }

    interface Events {
        fun onLogin(event: SubmitLogin)
    }

    data class SubmitLogin(
        val email: String,
        val password: String
    )
}