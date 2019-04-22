package com.di.mvisample.data

data class PlayerInfo(val name: String, val tShirtNumber: String) {
    override fun toString(): String {
        return "Player Name : $name \n  Player T-Shirt Number : $tShirtNumber"
    }
}