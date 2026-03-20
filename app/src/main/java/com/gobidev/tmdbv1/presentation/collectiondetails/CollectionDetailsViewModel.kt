package com.gobidev.tmdbv1.presentation.collectiondetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gobidev.tmdbv1.domain.model.MovieCollectionDetails
import com.gobidev.tmdbv1.domain.usecase.GetCollectionDetailsUseCase
import com.gobidev.tmdbv1.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CollectionDetailsUiState {
    data object Loading : CollectionDetailsUiState
    data class Success(val collection: MovieCollectionDetails) : CollectionDetailsUiState
    data class Error(val message: String) : CollectionDetailsUiState
}

@HiltViewModel
class CollectionDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCollectionDetailsUseCase: GetCollectionDetailsUseCase
) : ViewModel() {

    private val collectionId: Int = checkNotNull(savedStateHandle["collectionId"])
    val collectionName: String = savedStateHandle["collectionName"] ?: ""

    private val _uiState = MutableStateFlow<CollectionDetailsUiState>(CollectionDetailsUiState.Loading)
    val uiState: StateFlow<CollectionDetailsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = CollectionDetailsUiState.Loading
            _uiState.value = when (val result = getCollectionDetailsUseCase(collectionId)) {
                is Result.Success -> CollectionDetailsUiState.Success(result.data)
                is Result.Error -> CollectionDetailsUiState.Error(result.message)
            }
        }
    }
}
