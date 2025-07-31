package com.greencoach.model

data class NaverImageResponse(
    val items: List<NaverImageItem>
)
data class NaverImageItem(
    val link: String,
    val thumbnail: String,
    val sizeheight: Int,
    val sizewidth: Int
)
