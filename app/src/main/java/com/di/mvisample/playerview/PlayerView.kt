package com.di.mvisample.playerview

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.di.mvisample.R
import com.di.mvisample.data.mvi.PlayerViewState
import com.di.mvisample.data.mvi.PlayerViewsIntent
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.player_view.*

class PlayerView : Fragment() {

    private lateinit var viewModel: PlayerViewViewModel
    private lateinit var dispose: Disposable
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.player_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PlayerViewViewModel::class.java)
        viewModel.bind(getPlayerViewsIntents())
        dispose = viewModel.getPlayerViewState().subscribe {
            renderViewState(it)
        }
    }

    fun getPlayerViewsIntents(): Observable<PlayerViewsIntent> =
        Observable.merge(initViewsIntent(), onGetPlayerViewsButtonClicked())

    private fun onGetPlayerViewsButtonClicked(): Observable<out PlayerViewsIntent> =
        RxView.clicks(get_player).map { PlayerViewsIntent.OnGetPlayerViewsButtonClicked }


    private fun initViewsIntent(): Observable<out PlayerViewsIntent> =
        Observable.just(PlayerViewsIntent.InitViewsIntent)

    fun renderViewState(state: PlayerViewState) {
        when (state) {
            is PlayerViewState.LoadingState -> {
                handleLoadingState()
            }
            is PlayerViewState.SuccessState -> {
                handleOnSuccessState(state)
            }
            is PlayerViewState.ErrorState -> {
                handleOnErrorState(state)
            }
            is PlayerViewState.InitviewState -> {
                handleOnStartAppState(state)
            }
        }
    }

    private fun handleOnStartAppState(state: PlayerViewState.InitviewState) {
        progressBar.visibility = View.GONE
        get_player.visibility = View.VISIBLE
        player_info.visibility = View.VISIBLE
        player_info.text = state.initText
    }

    private fun handleOnErrorState(state: PlayerViewState.ErrorState) {
        progressBar.visibility = View.GONE
        get_player.visibility = View.VISIBLE
        player_info.visibility = View.VISIBLE
        player_info.text = "Error ${state.throwable}"
    }

    private fun handleOnSuccessState(state: PlayerViewState.SuccessState) {
        progressBar.visibility = View.GONE
        get_player.visibility = View.VISIBLE
        player_info.visibility = View.VISIBLE
        player_info.text = state.playerInfo.toString()
    }

    private fun handleLoadingState() {
        progressBar.visibility = View.VISIBLE
        get_player.visibility = View.GONE
        player_info.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        if (!dispose.isDisposed)
            dispose.dispose()
    }
}
