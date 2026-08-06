package com.finalplayer.app.player.core

import com.finalplayer.app.ui.player.controls.components.sheets.TrackNode
import java.util.Locale

object TrackSelector {

    suspend fun selectBestSubtitleTrack(
        tracks: List<TrackNode>,
        audioTracks: List<TrackNode>,
        fileName: String,
        preferredLangs: List<String> = listOf("ara", "ar", "eng", "en")
    ): TrackNode? {
        val subTracks = tracks.filter { it.type == "sub" }
        if (subTracks.isEmpty()) return null

        // Pass 00: External Override
        val externalTrack = subTracks.firstOrNull { it.external }
        if (externalTrack != null) return externalTrack

        // Pass A: Anime Detection
        val isAnime = isAnimeContent(audioTracks, fileName)
        if (isAnime) {
            val defaultJpnSub = subTracks.singleOrNull {
                it.isDefault && (it.lang.equals("jpn", ignoreCase = true) || it.lang.equals("ja", ignoreCase = true))
            }
            if (defaultJpnSub != null) return defaultJpnSub

            val animeKeywords = listOf("dialogue", "full", "script")
            val animeSub = subTracks.firstOrNull { track ->
                val titleLower = track.title.lowercase(Locale.ROOT)
                val langMatches = preferredLangs.any { pref -> track.lang.equals(pref, ignoreCase = true) }
                langMatches && animeKeywords.any { kw -> titleLower.contains(kw) }
            }
            if (animeSub != null) return animeSub
        }

        // Pass B: Clean Match
        val forbiddenKeywords = listOf("signs", "songs", "lyrics", "forced", "sdh", "colored")
        val cleanMatch = subTracks.firstOrNull { track ->
            val titleLower = track.title.lowercase(Locale.ROOT)
            val matchesLang = preferredLangs.any { pref -> track.lang.equals(pref, ignoreCase = true) }
            val hasForbiddenKeyword = forbiddenKeywords.any { kw -> titleLower.contains(kw) }
            matchesLang && !hasForbiddenKeyword && !track.forced
        }
        if (cleanMatch != null) return cleanMatch

        // Pass C: Last Resort
        val lastResort = subTracks.firstOrNull { track ->
            preferredLangs.any { pref -> track.lang.equals(pref, ignoreCase = true) }
        }
        if (lastResort != null) return lastResort

        // Pass D: Title-Name Fallback
        val titleKeywords = listOf("subtitle", "subtitles", "english", "arabic", "dialogue", "full", "translation")
        val titleFallback = subTracks.firstOrNull { track ->
            val langEmptyOrUnknown = track.lang.isEmpty() ||
                    track.lang.equals("und", ignoreCase = true) ||
                    track.lang.equals("zxx", ignoreCase = true)
            val titleLower = track.title.lowercase(Locale.ROOT)
            langEmptyOrUnknown && titleKeywords.any { kw -> titleLower.contains(kw) }
        }
        if (titleFallback != null) return titleFallback

        // Pass E: Single Clean Track
        val cleanTracks = subTracks.filter { track ->
            val titleLower = track.title.lowercase(Locale.ROOT)
            !forbiddenKeywords.any { kw -> titleLower.contains(kw) }
        }
        if (cleanTracks.size == 1) {
            return cleanTracks.first()
        }

        return null
    }

    private fun isAnimeContent(audioTracks: List<TrackNode>, fileName: String): Boolean {
        val hasJpnAudio = audioTracks.any {
            it.lang.equals("jpn", ignoreCase = true) ||
                    it.lang.equals("ja", ignoreCase = true) ||
                    it.title.lowercase(Locale.ROOT).contains("japanese")
        }
        val hasBrackets = fileName.contains("[") && fileName.contains("]")
        val hasChecksumHex = Regex("\\[[0-9A-Fa-f]{8}\\]").containsMatchIn(fileName)

        return hasJpnAudio || hasBrackets || hasChecksumHex
    }
}
