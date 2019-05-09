package com.di.mvisample.data.mvi

sealed class PlayerViewsIntent {

    object InitViewsIntent : PlayerViewsIntent()
    object OnGetPlayerViewsButtonClicked : PlayerViewsIntent()
    data class OnEditTextChange(val playerNumber: String) : PlayerViewsIntent()

}