package com.di.mvisample.UI.Playerviewwithedittext

import android.arch.lifecycle.ViewModel
import com.di.mvisample.data.mvi.PlayerViewState
import com.di.mvisample.data.mvi.PlayerViewsIntent
import com.di.mvisample.data.usecase.GetRandomPlayerUseCase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class PlayerViewWithEditTextViewModel : ViewModel() {
    private val playerViewsIntent = BehaviorSubject.create<PlayerViewsIntent>()
    private val getRandomPlayerUseCase = GetRandomPlayerUseCase()

    fun bind(intents: Observable<out PlayerViewsIntent>) {
        intents.subscribe(playerViewsIntent)
    }

    fun getPlayerViewState(): Observable<PlayerViewState> {

        return playerViewsIntent.publish { shared ->
            Observable.merge(shared.ofType(PlayerViewsIntent.InitViewsIntent::class.java)
                .flatMap { Observable.just(PlayerViewState.InitviewState("Click Button To Get Player Info"))
                }.cast(PlayerViewState::class.java)
                ,
                Observable.zip(
                    shared.ofType(PlayerViewsIntent.OnEditTextChange::class.java)
                        .flatMap { invalidateGuessButton(it.playerNumber) }
                    ,
                    shared.ofType(PlayerViewsIntent.OnGetPlayerViewsButtonClicked::class.java)
                        .flatMap { onButtonClickIntentState() }
                    , BiFunction { t1, t2 -> PlayerViewState.ErrorState(NullPointerException("hh")) }
                )
            )
        }.cast(PlayerViewState::class.java)//to convert player info to view state


//        return playerViewsIntent.flatMap {
//            when (it) {
//                is PlayerViewsIntent.InitViewsIntent -> {
//                    Observable.just(PlayerViewState.InitviewState("Click Button To Get Player Info"))
//                }
//                is PlayerViewsIntent.OnGetPlayerViewsButtonClicked -> {
//                    onButtonClickIntentState()
//                }
//                is PlayerViewsIntent.OnEditTextChange -> {
//                    invalidateGuessButton(it.playerNumber)
//                }
//            }
//        }.distinctUntilChanged()
    }

    private fun onButtonClickIntentState(): Observable<PlayerViewState> {
        return getRandomPlayerUseCase.getPlayerInfo()
            .delay(2000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .map { PlayerViewState.SuccessState(it) }
            .cast(PlayerViewState::class.java)//to convert player info to view state
            .startWith(PlayerViewState.LoadingState)
            .onErrorReturn { PlayerViewState.ErrorState(it) }
    }

    private fun invalidateGuessButton(playerNumber: String): Observable<PlayerViewState> {
        return Observable.just(playerNumber).filter { isValidPlayerNumber(playerNumber) }
            .map { PlayerViewState.invalidateGuessButton(true) }
            .cast(PlayerViewState::class.java)//to convert player info to view state
            .defaultIfEmpty(PlayerViewState.invalidateGuessButton(false))


    }

    private fun isValidPlayerNumber(playerNumber: String) =
        playerNumber.length > 0 && !playerNumber.equals("0")
                && !playerNumber.equals("00") && !playerNumber.startsWith("0")

}
