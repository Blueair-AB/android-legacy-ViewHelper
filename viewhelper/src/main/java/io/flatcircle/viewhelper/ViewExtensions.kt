package io.flatcircle.viewhelper

import android.view.View

/**
 * Created by jacquessmuts on 2019-04-30
 * Extension classes for Views
 */

fun View.hide() {
    this.show(false)
}

fun View.show(shouldShow: Boolean = true) {
    this.visibility = if (shouldShow) {
        View.VISIBLE
    } else {
        View.GONE
    }
}
