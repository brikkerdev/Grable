package com.example.api

import coil3.request.Disposable
interface ImageLoader<T>{
    fun load(target: T, model: Any?, crossfade: Boolean = false): Disposable
}