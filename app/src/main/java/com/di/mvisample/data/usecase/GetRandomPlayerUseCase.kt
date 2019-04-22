package com.di.mvisample.data.usecase

import com.di.mvisample.data.PlayerInfo
import io.reactivex.Observable
import java.util.*

class GetRandomPlayerUseCase {

    var playerList = mutableListOf<PlayerInfo>()

    init {
        playerList.add(PlayerInfo("Sadio Mané", "10"))
        playerList.add(PlayerInfo("Georginio Wijnaldum", "5"))
        playerList.add(PlayerInfo("James Milner", "7"))
        playerList.add(PlayerInfo("Roberto Firmino", "9"))
        playerList.add(PlayerInfo("Mo Salah", "11"))
        playerList.add(PlayerInfo("Jordan Henderson", "14"))
        playerList.add(PlayerInfo("Steven Gerrard", "8"))
        playerList.add(PlayerInfo("Alex Oxlade-Chamberlain", "21"))
        playerList.add(PlayerInfo("Alisson", "13"))
    }

    fun getPlayerInfo(): Observable<PlayerInfo> {
        val rand = Random()
        val n = rand.nextInt(playerList.size - 1)
        return Observable.just(playerList[n])
    }
}