package com.ariari.mowoori.util

import timber.log.Timber

object LogUtil {
    fun log(tag: String, msg: String) {
        Timber.tag(tag).d(msg)
    }
    fun log(msg:String){
        Timber.d(msg)
    }
}
