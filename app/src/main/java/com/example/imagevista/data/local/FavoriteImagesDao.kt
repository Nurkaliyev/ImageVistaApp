package com.example.imagevista.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.imagevista.data.local.entity.FavoriteImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteImagesDao {

    @Query("SELECT * FROM favorites_images_table")
    fun getAllFavoriteImages(): PagingSource<Int, FavoriteImageEntity>

    @Upsert
    suspend fun insertFavoriteImage(image: FavoriteImageEntity)

    @Delete
    suspend fun deleteFavoriteImage(image: FavoriteImageEntity)

    @Query("SELECT EXISTS (SELECT 1 FROM favorites_images_table WHERE id = :id)")
    suspend fun isImageFavorite(id: String): Boolean

    @Query("SELECT id FROM favorites_images_table")
    fun getFavoriteImageId(): Flow<List<String>>

}