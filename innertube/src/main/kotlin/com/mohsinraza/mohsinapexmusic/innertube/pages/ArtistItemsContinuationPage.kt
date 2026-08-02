package com.mohsinraza.mohsinapexmusic.innertube.pages

import com.mohsinraza.mohsinapexmusic.innertube.models.YTItem

data class ArtistItemsContinuationPage(
    val items: List<YTItem>,
    val continuation: String?,
)

