package com.example.imagevista.presentation.full_image_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.imagevista.domain.model.UnsplashImage
import com.example.imagevista.domain.repository.Downloader
import com.example.imagevista.domain.repository.ImageRepository
import com.example.imagevista.presentation.navigation.Routes
import com.example.imagevista.presentation.util.SnackBarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class FullImageViewModel @Inject constructor(
    private val repository: ImageRepository,
    private val downloader: Downloader,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val imageId = savedStateHandle.toRoute<Routes.FullImageScreen>().imageId

    private val _snackbarEvent = Channel<SnackBarEvent>()
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    var image: UnsplashImage? by mutableStateOf(null)
        private set

    init {
        getImage()
    }

    private fun getImage() {
        viewModelScope.launch {
            try {
                val result = repository.getImage(imageId)
                image = result
            } catch (error: UnknownHostException) {
                _snackbarEvent.send(
                    SnackBarEvent(
                        message = "No Internet connection. Please check your network connection."
                    )
                )
            } catch (error: Exception) {
                _snackbarEvent.send(
                    SnackBarEvent(
                        message = "Something went wrong: ${error.message}"
                    )
                )
            }
        }
    }

    fun downloadImage(url: String, title: String?) {
        viewModelScope.launch {
            try {
                downloader.downloadFile(url, title)
            } catch (error: Exception) {
                SnackBarEvent(
                    message = "Something went wrong: ${error.message}"
                )
            }
        }
    }

}