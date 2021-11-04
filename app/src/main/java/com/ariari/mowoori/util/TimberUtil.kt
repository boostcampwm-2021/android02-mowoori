package com.ariari.mowoori.util

import timber.log.Timber

object TimberUtil {
    fun timber(tag: String, msg: String) {
        Timber.tag(tag).d(msg)
    }
}
