package com.di.mvisample.data.mvi

sealed class PlayerViewsIntent {

    object InitViewsIntent : PlayerViewsIntent()
    object OnGetPlayerViewsButtonClicked : PlayerViewsIntent()
}