/**
 * MohsinApex-Music Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.mohsinraza.mohsinapexmusic.music.models

import com.mohsinraza.mohsinapexmusic.innertube.models.YTItem
import com.mohsinraza.mohsinapexmusic.music.db.entities.LocalItem

data class SimilarRecommendation(
    val title: LocalItem,
    val items: List<YTItem>,
)

