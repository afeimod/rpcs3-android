package net.rpcs3.overlay

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.MotionEvent

class PadOverlayButton(resources: Resources, image: Bitmap, private val digital1: Int, private val digital2: Int) : BitmapDrawable(resources, image) {
    private var pressed = false
    private var locked = -1
    private var origAlpha = alpha
    fun contains(x: Int, y: Int) = bounds.contains(x, y)

    fun onTouch(event: MotionEvent, pointerIndex: Int, padState: State): Boolean {
        val action = event.actionMasked
        var hit = false
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            if (locked == -1) {
                locked = event.getPointerId(pointerIndex)
                pressed = true
                origAlpha = alpha
                alpha = 255
                hit = true
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL) {
            if (locked != -1 && (action == MotionEvent.ACTION_CANCEL || event.getPointerId(pointerIndex) == locked)) {
                pressed = false
                locked = -1
                alpha = origAlpha
                hit = true
            }
        }

        if (pressed) {
            padState.digital[0] = padState.digital[0] or digital1
            padState.digital[1] = padState.digital[1] or digital2
        } else {
            padState.digital[0] = padState.digital[0] and digital1.inv()
            padState.digital[1] = padState.digital[1] and digital2.inv()
        }

        return hit
    }
}
