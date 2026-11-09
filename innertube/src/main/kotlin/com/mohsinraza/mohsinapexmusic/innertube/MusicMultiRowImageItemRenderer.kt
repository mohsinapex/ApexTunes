package com.mohsinraza.mohsinapexmusic.innertube.models

import kotlinx.serialization.Serializable

@Serializable
data class MusicMultiRowImageItemRenderer(
    val title: Runs,
    val subtitle: Runs,
    val thumbnail: ThumbnailRenderer,
    val onTap: NavigationEndpoint,
)

