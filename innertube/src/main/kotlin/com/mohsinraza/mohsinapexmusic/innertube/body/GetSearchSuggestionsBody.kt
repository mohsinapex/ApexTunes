package com.mohsinraza.mohsinapexmusic.innertube.models.body

import com.mohsinraza.mohsinapexmusic.innertube.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class GetSearchSuggestionsBody(
    val context: Context,
    val input: String,
)

