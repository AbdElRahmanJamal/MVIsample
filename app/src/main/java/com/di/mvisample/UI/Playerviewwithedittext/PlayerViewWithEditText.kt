package com.di.mvisample.UI.Playerviewwithedittext

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
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.player_view_with_edit_text.*

class PlayerViewWithEditText : Fragment() {

    private lateinit var viewModel: PlayerViewWithEditTextViewModel
    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.player_view_with_edit_text, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PlayerViewWithEditTextViewModel::class.java)
        viewModel.bind(getPlayerViewsIntents())
        val dispose = viewModel.getPlayerViewState().subscribe {
            renderViewState(it)
        }
        disposables.add(dispose)
    }

    fun getPlayerViewsIntents(): Observable<PlayerViewsIntent> =
        Observable.merge(editTextIntent(), onGetPlayerViewsButtonClicked(), initViewsIntent())

    private fun onGetPlayerViewsButtonClicked(): Observable<out PlayerViewsIntent> =
        RxView.clicks(get_player_btn).map { PlayerViewsIntent.OnGetPlayerViewsButtonClicked }

    private fun editTextIntent(): Observable<out PlayerViewsIntent> =
        RxTextView.textChanges(player_number).map {
            PlayerViewsIntent
                .OnEditTextChange(player_number.text.toString())
        }

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
            is PlayerViewState.invalidateGuessButton -> {
                get_player_btn.isEnabled = state.isEnable
            }
        }
    }

    private fun handleOnStartAppState(state: PlayerViewState.InitviewState) {
        progressBar1.visibility = View.GONE
        get_player_btn.isEnabled = false
        get_player_btn.visibility = View.VISIBLE
        guess_state.visibility = View.VISIBLE
        guess_state.text = state.initText
    }

    private fun handleOnErrorState(state: PlayerViewState.ErrorState) {
        progressBar1.visibility = View.GONE
        get_player_btn.visibility = View.VISIBLE
        guess_state.visibility = View.VISIBLE
        guess_state.text = "Error ${state.throwable}"
    }

    private fun handleOnSuccessState(state: PlayerViewState.SuccessState) {
        progressBar1.visibility = View.GONE
        get_player_btn.visibility = View.VISIBLE
        guess_state.visibility = View.VISIBLE
        guess_state.text = state.playerInfo.toString()
    }

    private fun handleLoadingState() {
        progressBar1.visibility = View.VISIBLE
        get_player_btn.visibility = View.GONE
        guess_state.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        if (!disposables.isDisposed)
            disposables.dispose()
    }
}
