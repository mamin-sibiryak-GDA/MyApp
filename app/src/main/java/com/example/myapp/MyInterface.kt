package com.example.myapp

import android.widget.ImageView

interface MyInterface {
    fun callback(
        image: ImageView,
        pos: Int
    ) //будем вызывать этот метод с объектом-изображением и позицией элемента в списке
    fun del(pos: Int)
}