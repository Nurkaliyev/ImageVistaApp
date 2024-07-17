package com.example.imagevista.presentation.search_screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.example.imagevista.data.util.Constants.IV_LOG_TAG
import com.example.imagevista.domain.model.UnsplashImage
import com.example.imagevista.presentation.component.ImageVerticalGrid
import com.example.imagevista.presentation.component.ZoomedImageCard
import com.example.imagevista.presentation.util.SnackBarEvent
import com.example.imagevista.presentation.util.searchKeywords
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchedImages: LazyPagingItems<UnsplashImage>,
    onBackClick: () -> Unit,
    searchedQuery: String,
    favoriteImageIds: List<String>,
    onSearchQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onToggleFavoriteStatus: (UnsplashImage) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackBarEvent: Flow<SnackBarEvent>,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var isSuggestionChipsVisible by remember { mutableStateOf(false) }
    Log.d(IV_LOG_TAG, "searchedImagesCount: ${searchedImages.itemCount}")
    var showImagePreview by remember { mutableStateOf(false) }
    var activeImage by remember { mutableStateOf<UnsplashImage?>(null) }

    LaunchedEffect(key1 = true) {
        snackBarEvent.collect { event ->
            snackbarHostState.showSnackbar(
                message = event.message,
                duration = event.duration
            )
        }
    }

    LaunchedEffect(key1 = Unit) {
        delay(500)
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SearchBar(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        isSuggestionChipsVisible = it.isFocused
                    }
                ,
                query = searchedQuery,
                onQueryChange = { onSearchQueryChange(it)},
                onSearch = {
                    onSearch(searchedQuery)
                    keyboardController?.hide()
                    focusManager.clearFocus()
                },
                placeholder = { Text(text = "Search...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    IconButton(
                        onClick = { if (searchedQuery.isEmpty()) onSearchQueryChange("") else onBackClick() }
                    ) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                    }
                },
                active = false,
                onActiveChange = {},
                content = {}
            )

            AnimatedVisibility(visible = isSuggestionChipsVisible) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(searchKeywords){ keyword ->
                        SuggestionChip(
                            onClick = {
                                onSearch(keyword)
                                onSearchQueryChange(keyword)
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            },
                            label = { Text(text = keyword) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        )
                    }
                }
            }

            ImageVerticalGrid(
                images = searchedImages,
                onImageClick = onImageClick,
                onDragStart = { image ->
                    activeImage = image
                    showImagePreview = true
                },
                onDragEnd = {
                    showImagePreview = false
                },
                onToggleFavoriteStatus = onToggleFavoriteStatus,
                favoriteImageIds = favoriteImageIds
            )

        }

        ZoomedImageCard(
            image = activeImage,
            isVisible = showImagePreview,
            modifier = Modifier.padding(20.dp)
        )
    }
}