package com.mohsinraza.mohsinapexmusic.music.ui.screens.equalizer

import com.mohsinraza.mohsinapexmusic.music.eq.data.SavedEQProfile

/**
 * UI State for EQ Screen
 */
data class EQState(
    val profiles: List<SavedEQProfile> = emptyList(),
    val activeProfileId: String? = null,
    val importStatus: String? = null,
    val error: String? = null
)
