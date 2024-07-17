package com.example.imagevista.presentation.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.imagevista.presentation.favorite_screen.FavoriteScreen
import com.example.imagevista.presentation.favorite_screen.FavoritesViewModel
import com.example.imagevista.presentation.full_image_screen.FullImageScreen
import com.example.imagevista.presentation.full_image_screen.FullImageViewModel
import com.example.imagevista.presentation.home_screen.HomeScreen
import com.example.imagevista.presentation.home_screen.HomeViewModel
import com.example.imagevista.presentation.profile_screen.ProfileScreen
import com.example.imagevista.presentation.search_screen.SearchScreen
import com.example.imagevista.presentation.search_screen.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraphSetup(
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    snackbarHostState: SnackbarHostState,
    searchedQuery: String,
    onSearchQueryChange: (String) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HomeScreen,
    ) {
        composable<Routes.HomeScreen> {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val images = homeViewModel.images.collectAsLazyPagingItems()
            val favoriteImageIds by homeViewModel.favoriteImageIds.collectAsStateWithLifecycle()
            HomeScreen(
                snackbarHostState = snackbarHostState,
                snackBarEvent = homeViewModel.snackbarEvent,
                scrollBehavior = scrollBehavior,
                images = images,
                favoriteImageIds = favoriteImageIds,
                onImageClick = { imageId -> navController.navigate(Routes.FullImageScreen(imageId)) },
                onSearchClick = { navController.navigate(Routes.SearchScreen) },
                onFABClick = { navController.navigate(Routes.BookmarkScreen) },
                onToggleFavoriteStatus = {homeViewModel.toggleFavoriteStatus(it)},
            )
        }

        composable<Routes.SearchScreen> {
            val searchViewModel: SearchViewModel = hiltViewModel()
            val searchedImages = searchViewModel.searchImages.collectAsLazyPagingItems()
            val favoriteImageIds by searchViewModel.favoriteImageIds.collectAsStateWithLifecycle()
            SearchScreen(
                onBackClick = { navController.navigateUp() },
                onImageClick = {
                        imageId -> navController.navigate(Routes.FullImageScreen(imageId))
                },
                snackbarHostState = snackbarHostState,
                snackBarEvent = searchViewModel.snackbarEvent,
                searchedImages = searchedImages,
                searchedQuery = searchedQuery,
                onSearchQueryChange = onSearchQueryChange,
                onSearch = {searchViewModel.searchImages(it)},
                onToggleFavoriteStatus = {searchViewModel.toggleFavoriteStatus(it)},
                favoriteImageIds = favoriteImageIds,
            )
        }

        composable<Routes.BookmarkScreen> {
            val favoritesViewModel: FavoritesViewModel = hiltViewModel()
            val favoriteImages = favoritesViewModel.favoriteImages.collectAsLazyPagingItems()
            val favoriteImageIds by favoritesViewModel.favoriteImageIds.collectAsStateWithLifecycle()
           FavoriteScreen(
               scrollBehavior = scrollBehavior,
               onSearchClick = {navController.navigate(Routes.SearchScreen)},
               favoriteImages = favoriteImages,
               onBackClick = { navController.navigateUp() },
               favoriteImageIds = favoriteImageIds,
               onImageClick = {
                       imageId -> navController.navigate(Routes.FullImageScreen(imageId))
               },
               onToggleFavoriteStatus = {favoritesViewModel.toggleFavoriteStatus(it)},
               snackbarHostState = snackbarHostState,
               snackBarEvent = favoritesViewModel.snackbarEvent,
           )
        }

        composable<Routes.FullImageScreen> {
            val fullImageViewModel: FullImageViewModel = hiltViewModel()
            FullImageScreen(
                snackbarHostState = snackbarHostState,
                snackBarEvent = fullImageViewModel.snackbarEvent,
                image = fullImageViewModel.image,
                onBackClick = { navController.navigateUp() },
                onPhotographerNameClick = { profileLink ->
                    navController.navigate(Routes.ProfileScreen(profileLink))
                },
                onImageDownloadClick = { url, title ->
                    fullImageViewModel.downloadImage(url, title)
                }
            )
        }

        composable<Routes.ProfileScreen> { backStackEntry ->
            val profileLink = backStackEntry.toRoute<Routes.ProfileScreen>().profileLink
            ProfileScreen(
                profileLink = profileLink,
                onBackClick = { navController.navigateUp() },
            )
        }
    }

}