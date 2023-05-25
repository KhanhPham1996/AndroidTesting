package com.androiddevs.shoppinglisttestingyt.data.remote

data class ImageResponse(
    val hits: List<ImageResult>,
    val total: Int,
    val totalHits: Int
)