package com.mohsinraza.mohsinapexmusic.innertube.pages

import com.mohsinraza.mohsinapexmusic.innertube.models.Album
import com.mohsinraza.mohsinapexmusic.innertube.models.AlbumItem
import com.mohsinraza.mohsinapexmusic.innertube.models.Artist
import com.mohsinraza.mohsinapexmusic.innertube.models.ArtistItem
import com.mohsinraza.mohsinapexmusic.innertube.models.MusicResponsiveListItemRenderer
import com.mohsinraza.mohsinapexmusic.innertube.models.MusicTwoRowItemRenderer
import com.mohsinraza.mohsinapexmusic.innertube.models.PlaylistItem
import com.mohsinraza.mohsinapexmusic.innertube.models.SongItem
import com.mohsinraza.mohsinapexmusic.innertube.models.YTItem
import com.mohsinraza.mohsinapexmusic.innertube.models.oddElements
import com.mohsinraza.mohsinapexmusic.innertube.utils.parseTime

data class LibraryAlbumsPage(
    val albums: List<AlbumItem>,
    val continuation: String?,
) {
    companion object {
        fun fromMusicTwoRowItemRenderer(renderer: MusicTwoRowItemRenderer): AlbumItem? {
            return AlbumItem(
                        browseId = renderer.navigationEndpoint.browseEndpoint?.browseId ?: return null,
                        playlistId = renderer.thumbnailOverlay?.musicItemThumbnailOverlayRenderer?.content
                            ?.musicPlayButtonRenderer?.playNavigationEndpoint
                            ?.watchPlaylistEndpoint?.playlistId ?: return null,
                        title = renderer.title.runs?.firstOrNull()?.text ?: return null,
                        artists = null,
                        year = renderer.subtitle?.runs?.lastOrNull()?.text?.toIntOrNull(),
                        thumbnail = renderer.thumbnailRenderer.getThumbnailUrl() ?: return null,
                        explicit = renderer.subtitleBadges?.find {
                            it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                        } != null
                    )
        }
    }
}

