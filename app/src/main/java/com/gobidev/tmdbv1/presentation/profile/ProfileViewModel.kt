package com.gobidev.tmdbv1.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gobidev.tmdbv1.data.local.SessionManager
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.UserAccount
import com.gobidev.tmdbv1.domain.usecase.GetAccountUseCase
import com.gobidev.tmdbv1.domain.usecase.GetFavoritesUseCase
import com.gobidev.tmdbv1.domain.usecase.GetWatchlistUseCase
import com.gobidev.tmdbv1.domain.usecase.LogoutUseCase
import com.gobidev.tmdbv1.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileUiState {
    data object LoggedOut : ProfileUiState()
    data object Loading : ProfileUiState()
    data class LoggedIn(val account: UserAccount) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val sessionManager: SessionManager,
    private val getAccountUseCase: GetAccountUseCase,
    private val logoutUseCase: LogoutUseCase,
    getFavoritesUseCase: GetFavoritesUseCase,
    getWatchlistUseCase: GetWatchlistUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(
        if (sessionManager.isLoggedIn) ProfileUiState.Loading else ProfileUiState.LoggedOut
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    val favorites: Flow<PagingData<Movie>> = getFavoritesUseCase().cachedIn(viewModelScope)
    val watchlist: Flow<PagingData<Movie>> = getWatchlistUseCase().cachedIn(viewModelScope)

    init {
        if (sessionManager.isLoggedIn) loadAccount()
    }

    fun loadAccount() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            _uiState.value = when (val result = getAccountUseCase()) {
                is Result.Success -> ProfileUiState.LoggedIn(result.data)
                is Result.Error -> ProfileUiState.Error(result.message)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.value = ProfileUiState.LoggedOut
        }
    }
}
