package com.example.imagevista.data.paging


import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.imagevista.data.local.ImageVistaDatabase
import com.example.imagevista.data.local.entity.UnsplashImageEntity
import com.example.imagevista.data.local.entity.UnsplashRemoteKeys
import com.example.imagevista.data.mapper.toEntityList
import com.example.imagevista.data.remote.UnsplashApiService
import com.example.imagevista.data.util.Constants
import com.example.imagevista.data.util.Constants.ITEMS_PER_PAGE

@OptIn(ExperimentalPagingApi::class)
class EditorialFeedRemoteMediator(
    private val apiService: UnsplashApiService,
    private val database: ImageVistaDatabase

) : RemoteMediator<Int, UnsplashImageEntity>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }


    private val editorialFeedDao = database.editorialFeedDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UnsplashImageEntity>
    ): MediatorResult {
        try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    STARTING_PAGE_INDEX
                }

                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevPage = remoteKeys?.prevPage
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    prevPage
                }

                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKeys?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextPage
                }
            }

            val response =
                apiService.getEditorialFeedImages(page = currentPage, perPage = ITEMS_PER_PAGE)
            val endOfPaginationReached = response.isEmpty()
            Log.d(Constants.IV_LOG_TAG, "endOfPaginationReached: $endOfPaginationReached")
            val prevPage = if (currentPage == STARTING_PAGE_INDEX) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    editorialFeedDao.deleteAllEditorialFeedImages()
                    editorialFeedDao.deleteAllRemoteKeys()
                }
                val remoteKeys = response.map { unsplashImageDto ->
                    UnsplashRemoteKeys(
                        id = unsplashImageDto.id,
                        prevPage = prevPage,
                        nextPage = nextPage,
                    )
                }
                editorialFeedDao.insertAllRemoteKeys(remoteKeys)
                editorialFeedDao.insertEditorialFeedImages(response.toEntityList())
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, UnsplashImageEntity>): UnsplashRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { unsplashImage ->
                editorialFeedDao.getRemoteKeys(unsplashImage.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, UnsplashImageEntity>): UnsplashRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { unsplashImage ->
                editorialFeedDao.getRemoteKeys(unsplashImage.id)
            }
    }
}