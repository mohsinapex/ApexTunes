package com.mohsinraza.mohsinapexmusic.innertube.models.body

import com.mohsinraza.mohsinapexmusic.innertube.models.Context
import com.mohsinraza.mohsinapexmusic.innertube.models.Continuation
import kotlinx.serialization.Serializable

@Serializable
data class BrowseBody(
    val context: Context,
    val browseId: String?,
    val params: String?,
    val continuation: String?
)

