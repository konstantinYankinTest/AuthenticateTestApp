package com.lookout.authenticatetestapp.base

interface EventHandler<T> {

    fun obtainEvent(event: T)
}