package com.di.mvisample.data.mvi

import com.di.mvisample.data.PlayerInfo

sealed class PlayerViewState {

    data class InitviewState(val initText: String) : PlayerViewState()
    object LoadingState : PlayerViewState()
    data class ErrorState(val throwable: Throwable) : PlayerViewState()
    data class SuccessState(val playerInfo: PlayerInfo) : PlayerViewState()
    object enableGuessButton : PlayerViewState()
    object disapleGuessButton : PlayerViewState()
}