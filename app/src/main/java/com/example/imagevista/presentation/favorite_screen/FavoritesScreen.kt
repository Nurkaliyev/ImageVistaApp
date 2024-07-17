package com.example.imagevista.presentation.favorite_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.example.imagevista.R
import com.example.imagevista.domain.model.UnsplashImage
import com.example.imagevista.presentation.component.ImageVerticalGrid
import com.example.imagevista.presentation.component.ImageVistaTopAppBar
import com.example.imagevista.presentation.component.ZoomedImageCard
import com.example.imagevista.presentation.util.SnackBarEvent
import kotlinx.coroutines.flow.Flow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    scrollBehavior: TopAppBarScrollBehavior,
    onSearchClick: () -> Unit,
    favoriteImages: LazyPagingItems<UnsplashImage>,
    onBackClick: () -> Unit,
    favoriteImageIds: List<String>,
    onImageClick: (String) -> Unit,
    onToggleFavoriteStatus: (UnsplashImage) -> Unit,
    snackbarHostState: SnackbarHostState,
    snackBarEvent: Flow<SnackBarEvent>,
) {
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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ImageVistaTopAppBar(
                title = "Favorite Images",
                scrollBehavior = scrollBehavior,
                onSearchClick = onSearchClick,
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                }
            )

            ImageVerticalGrid(
                images = favoriteImages,
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


        if (favoriteImages.itemCount == 0){
            EmptyState(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(id = R.drawable.img_empty_bookmarks),
            contentDescription = null
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = "No Saved Images",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Images you saved will be stored here",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )

    }
}