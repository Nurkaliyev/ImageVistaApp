package com.example.imagevista.presentation.search_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.imagevista.domain.model.UnsplashImage
import com.example.imagevista.domain.repository.ImageRepository
import com.example.imagevista.presentation.util.SnackBarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: ImageRepository
) : ViewModel() {

    private val _snackbarEvent = Channel<SnackBarEvent>()
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    private val _searchImages = MutableStateFlow<PagingData<UnsplashImage>>(PagingData.empty())
    val searchImages = _searchImages

    fun searchImages(query: String) {
        viewModelScope.launch {
            try {
                repository.searchImages(query)
                    .cachedIn(viewModelScope)
                    .collect {
                        _searchImages.value = it
                    }
            } catch (exception: Exception) {
                _snackbarEvent.send(
                    SnackBarEvent(message = "Something went wrong. ${exception.message}")
                )
            }
        }
    }

    val favoriteImageIds: StateFlow<List<String>> = repository.getFavoriteImageIds()
        .catch { exception ->
            _snackbarEvent.send(
                SnackBarEvent(message = "Something went wrong. ${exception.message}")
            )
        }
        .stateIn(
            scope =  viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    fun toggleFavoriteStatus(image: UnsplashImage) {
        viewModelScope.launch {
            try {
                repository.toggleFavoriteStatus(image)
            } catch (error: Exception) {
                _snackbarEvent.send(
                    SnackBarEvent(message = "Something went wrong. ${error.message}")
                )
            }
        }
    }
}