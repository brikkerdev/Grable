package ru.sirius.grable.common

import android.widget.ImageView
import coil3.ImageLoader as CoilImageLoader
import coil3.imageLoader
import coil3.request.Disposable
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.target

fun ImageView.load(
    data: Any?,
    crossfade: Boolean = false,
): Disposable {
    val request = ImageRequest.Builder(context)
        .data(data)
        .target(this)
        .apply { if (crossfade) crossfade(true) }
        .build()
    return context.imageLoader.enqueue(request)
}