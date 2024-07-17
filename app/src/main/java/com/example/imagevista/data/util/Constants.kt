package com.example.imagevista.data.util

import com.example.imagevista.BuildConfig

object Constants {
    const val BASE_URL = BuildConfig.UNSPLASH_BASE_URL
    const val ACCESS_KEY = BuildConfig.UNSPLASH_ACCESS_KEY
    const val SECRET_KEY = BuildConfig.UNSPLASH_SECRET_KEY
    const val IMAGE_VISTA_DATABASE = "unsplash_images.db"
    const val FAVORITE_IMAGE_TABLE = "favorites_images_table"
    const val IMAGE_TABLE = "images_table"
    const val REMOTE_KEY_TABLE = "remote_key_table"
    const val ITEMS_PER_PAGE = 10
    const val IV_LOG_TAG = "ImageVistaLogs"
}