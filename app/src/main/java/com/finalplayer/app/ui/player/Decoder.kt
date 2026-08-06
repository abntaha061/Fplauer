package com.finalplayer.app.ui.player

enum class Decoder(val value: String, val displayName: String) {
    HW_PLUS("mediacodec", "HW+"),
    HW_COPY("mediacodec-copy", "HW"),
    SOFTWARE("no", "SW");

    companion object {
        fun getDecoderFromValue(value: String): Decoder = when {
            value.contains("mediacodec-copy") -> HW_COPY
            value.contains("mediacodec") -> HW_PLUS
            else -> SOFTWARE
        }
    }
}
