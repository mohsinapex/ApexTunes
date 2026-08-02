package com.mohsinraza.mohsinapexmusic.innertube.models.body

import com.mohsinraza.mohsinapexmusic.innertube.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class GetTranscriptBody(
    val context: Context,
    val params: String,
)

