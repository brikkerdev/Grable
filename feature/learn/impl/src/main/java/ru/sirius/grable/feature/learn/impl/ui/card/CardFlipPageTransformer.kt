package ru.sirius.grable.feature.learn.impl.ui.card

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class CardFlipPageTransformer : ViewPager2.PageTransformer {
    var isScalable: Boolean = false

    override fun transformPage(page: View, position: Float) {
        val percentage = 1 - abs(position)
        page.setCameraDistance(30000f)
        setVisibility(page, position)
        setTranslation(page)
        setSize(page, position, percentage)
        setRotation(page, position, percentage)
    }

    private fun setVisibility(page: View, position: Float) {
        if (position < 0.5 && position > -0.5) {
            page.visibility = View.VISIBLE
        } else {
            page.visibility = View.INVISIBLE
        }
    }

    private fun setTranslation(page: View) {
        val viewPager = requireViewPager(page)
        if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
            val scroll = viewPager.scrollX - page.left
            page.translationX = scroll.toFloat()
        } else {
            val scroll = viewPager.scrollY - page.top
            page.translationY = scroll.toFloat()
        }
    }

    private fun setSize(page: View, position: Float, percentage: Float) {
        // Do nothing, if its not scalable
        if (!this.isScalable) return

        page.scaleX = if (position != 0f && position != 1f) percentage else 1f
        page.scaleY = if (position != 0f && position != 1f) percentage else 1f
    }

    private fun setRotation(page: View, position: Float, percentage: Float) {
        val viewPager = requireViewPager(page)
        if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
            if (position > 0) {
                page.rotationY = -180 * (percentage + 1)
            } else {
                page.rotationY = 180 * (percentage + 1)
            }
        } else {
            if (position > 0) {
                page.rotationX = -180 * (percentage + 1)
            } else {
                page.rotationX = 180 * (percentage + 1)
            }
        }
    }

    private fun requireViewPager(page: View): ViewPager2 {
        val parent = page.parent
        val parentParent = parent.parent

        if (parent is RecyclerView && parentParent is ViewPager2) {
            return parentParent
        }

        throw IllegalStateException(
            "Expected the page view to be managed by a ViewPager2 instance."
        )
    }
}

