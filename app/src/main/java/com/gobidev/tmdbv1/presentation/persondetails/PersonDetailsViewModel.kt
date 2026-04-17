package com.gobidev.tmdbv1.presentation.persondetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gobidev.tmdbv1.domain.model.PersonCastCredit
import com.gobidev.tmdbv1.domain.model.PersonDetails
import com.gobidev.tmdbv1.domain.usecase.GetPersonCreditsUseCase
import com.gobidev.tmdbv1.domain.usecase.GetPersonDetailsUseCase
import com.gobidev.tmdbv1.domain.usecase.GetPersonExternalIdsUseCase
import com.gobidev.tmdbv1.domain.util.Result
import com.gobidev.tmdbv1.presentation.components.ExternalIdsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PersonDetailsUiState {
    data object Loading : PersonDetailsUiState()
    data class Success(val person: PersonDetails) : PersonDetailsUiState()
    data class Error(val message: String) : PersonDetailsUiState()
}

sealed class PersonCreditsUiState {
    data object Loading : PersonCreditsUiState()
    data class Success(val credits: List<PersonCastCredit>) : PersonCreditsUiState()
    data class Error(val message: String) : PersonCreditsUiState()
}

@HiltViewModel
class PersonDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPersonDetailsUseCase: GetPersonDetailsUseCase,
    private val getPersonCreditsUseCase: GetPersonCreditsUseCase,
    private val getPersonExternalIdsUseCase: GetPersonExternalIdsUseCase
) : ViewModel() {

    private val personId = savedStateHandle.get<Int>("personId") ?: -1

    private val _uiState = MutableStateFlow<PersonDetailsUiState>(PersonDetailsUiState.Loading)
    val uiState: StateFlow<PersonDetailsUiState> = _uiState.asStateFlow()

    private val _creditsState = MutableStateFlow<PersonCreditsUiState>(PersonCreditsUiState.Loading)
    val creditsState: StateFlow<PersonCreditsUiState> = _creditsState.asStateFlow()

    private val _externalIdsState = MutableStateFlow<ExternalIdsUiState>(ExternalIdsUiState.Loading)
    val externalIdsState: StateFlow<ExternalIdsUiState> = _externalIdsState.asStateFlow()

    init {
        if (personId != -1) {
            loadDetails()
            loadCredits()
            loadExternalIds()
        } else {
            _uiState.value = PersonDetailsUiState.Error("Invalid person ID")
        }
    }

    private fun loadDetails() {
        viewModelScope.launch {
            when (val result = getPersonDetailsUseCase(personId)) {
                is Result.Success -> _uiState.value = PersonDetailsUiState.Success(result.data)
                is Result.Error -> _uiState.value = PersonDetailsUiState.Error(result.message)
            }
        }
    }

    private fun loadCredits() {
        viewModelScope.launch {
            when (val result = getPersonCreditsUseCase(personId)) {
                is Result.Success -> _creditsState.value = PersonCreditsUiState.Success(result.data)
                is Result.Error -> _creditsState.value = PersonCreditsUiState.Error(result.message)
            }
        }
    }

    private fun loadExternalIds() {
        viewModelScope.launch {
            when (val result = getPersonExternalIdsUseCase(personId)) {
                is Result.Success -> {
                    val ids = result.data
                    _externalIdsState.value = if (ids.hasAny()) ExternalIdsUiState.Success(ids)
                    else ExternalIdsUiState.Empty
                }
                is Result.Error -> _externalIdsState.value = ExternalIdsUiState.Error(result.message)
            }
        }
    }
}
