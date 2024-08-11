package com.androidcoursehogent.postealapp.data

open class Event<out T> (private val content: T) {

    var hasBeenHandled = false
        private set

    fun getContentOrNull(): T?{
        return if(hasBeenHandled) {
            null
        } else{
            hasBeenHandled = true
            content
        }
    }

}