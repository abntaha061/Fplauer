package com.finalplayer.app.ui.player.controls.components.sheets

data class TrackNode(
    val id: Int,
    val type: String,       // "sub" or "audio"
    val lang: String = "",
    val title: String = "",
    val isDefault: Boolean = false,
    val forced: Boolean = false,
    val external: Boolean = false,
    val externalFilename: String? = null
)
