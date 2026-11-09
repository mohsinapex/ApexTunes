package com.mohsinraza.mohsinapexmusic.innertube.pages

import com.mohsinraza.mohsinapexmusic.innertube.models.AlbumItem

data class ExplorePage(
    val newReleaseAlbums: List<AlbumItem>,
    val moodAndGenres: List<MoodAndGenres.Item>,
)

