package com.mohsinraza.mohsinapexmusic.innertube.models.response

import com.mohsinraza.mohsinapexmusic.innertube.models.Continuation
import com.mohsinraza.mohsinapexmusic.innertube.models.MusicResponsiveListItemRenderer
import com.mohsinraza.mohsinapexmusic.innertube.models.Tabs
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val contents: Contents?,
    val continuationContents: ContinuationContents?,
) {
    @Serializable
    data class Contents(
        val tabbedSearchResultsRenderer: Tabs?,
    )

    @Serializable
    data class ContinuationContents(
        val musicShelfContinuation: MusicShelfContinuation,
    ) {
        @Serializable
        data class MusicShelfContinuation(
            val contents: List<Content>,
            val continuations: List<Continuation>?,
        ) {
            @Serializable
            data class Content(
                val musicResponsiveListItemRenderer: MusicResponsiveListItemRenderer,
            )
        }
    }
}

