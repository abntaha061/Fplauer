package com.finalplayer.app.player.core

object MPVLib {
    var activeView: MPVView? = null

    fun setOptionString(name: String, value: String) {
        activeView?.setOptionString(name, value)
    }

    fun setPropertyString(name: String, value: String) {
        activeView?.setPropertyString(name, value)
    }

    fun setPropertyInt(name: String, value: Int) {
        activeView?.setPropertyInt(name, value)
    }

    fun setPropertyFloat(name: String, value: Float) {
        activeView?.setPropertyFloat(name, value)
    }

    fun setPropertyBoolean(name: String, value: Boolean) {
        activeView?.setPropertyBoolean(name, value)
    }

    fun command(vararg args: String) {
        activeView?.command(arrayOf(*args))
    }
}
