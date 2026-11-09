/**
 * MohsinApex-Music Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

@file:Suppress("LocalVariableName")

package com.mohsinraza.mohsinapexmusic.music.ui.utils

fun String.resize(
    width: Int? = null,
    height: Int? = null,
): String {
    if (width == null && height == null) return this
    val requestedSize = maxOf(width ?: 0, height ?: 0)
    if (requestedSize > 480) {
        val maxResolutionMatch =
            Regex("^(https://i\\.ytimg\\.com/vi/[^?#]+/)hqdefault\\.jpg([?#].*)?$")
                .matchEntire(this)
        if (maxResolutionMatch != null) {
            return "${maxResolutionMatch.groupValues[1]}maxresdefault.jpg${maxResolutionMatch.groupValues[2]}"
        }
    }

    // Match BOTH lh3 and yt3 googleusercontent: YouTube migrated music/album art from
    // lh3.googleusercontent.com to yt3.googleusercontent.com. Both serve the same =wW-hH resize
    // params; matching only lh3 silently no-ops on the new host, so the player upscales the raw
    // ~60px thumbnail (blurry). Verified live: a yt3 URL + =w544-h544 returns a sharp full-size image.
    "https://(?:lh3|yt3)\\.googleusercontent\\.com/.*=w(\\d+)-h(\\d+).*".toRegex()
        .matchEntire(this)?.groupValues?.let { group ->
        val (W, H) = group.drop(1).map { it.toInt() }
        var w = width
        var h = height
        if (w != null && h == null) h = (w.toLong() * H / W).toInt()
        if (w == null && h != null) w = (h.toLong() * W / H).toInt()
        return "${substringBefore("=w")}=w$w-h$h-l90-rj"
    }
    val ggphtMatch = Regex("^(https://yt3\\.ggpht\\.com/[^?#]*)=s\\d+([?#].*)?$").matchEntire(this)
    if (ggphtMatch != null) {
        val targetWidth = width ?: height ?: return this
        val targetHeight = height ?: width ?: return this
        return "${ggphtMatch.groupValues[1]}=w$targetWidth-h$targetHeight-p-l90-rj${ggphtMatch.groupValues[2]}"
    }
    return this
}
