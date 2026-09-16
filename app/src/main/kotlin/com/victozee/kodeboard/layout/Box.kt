package com.victozee.kodeboard.layout

class Box {
    var x: Float = 0f
    var y: Float = 0f
    var width: Float = 0f
    var height: Float = 0f

    fun getLeft(): Float = x

    fun getRight(): Float = x + width

    fun getTop(): Float = y

    fun getBottom(): Float = y + height

    companion object {
        @JvmStatic
        fun create(x: Float, y: Float, width: Float, height: Float): Box = Box().apply {
            this.x = x
            this.y = y
            this.width = width
            this.height = height
        }
    }
}
