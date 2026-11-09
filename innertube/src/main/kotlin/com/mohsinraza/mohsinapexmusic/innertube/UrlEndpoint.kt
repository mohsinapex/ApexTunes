package com.mohsinraza.mohsinapexmusic.innertube.models

import kotlinx.serialization.Serializable

@Serializable
data class UrlEndpoint(
    val url: String? = null,
    val target: String? = null,
)

