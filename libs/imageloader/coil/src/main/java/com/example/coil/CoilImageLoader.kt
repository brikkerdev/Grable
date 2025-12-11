package com.example.coil

import android.widget.ImageView
import coil3.imageLoader
import coil3.request.Disposable
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.target
import com.example.api.ImageLoader

internal class CoilImageLoader : ImageLoader<ImageView> {
    override fun load(target: ImageView, model: Any?, crossfade: Boolean): Disposable {
        val request = ImageRequest.Builder(target.context)
            .data(model)
            .target(target)
            .apply { if (crossfade) crossfade(true) }
            .build()
        return target.context.imageLoader.enqueue(request)
    }
}