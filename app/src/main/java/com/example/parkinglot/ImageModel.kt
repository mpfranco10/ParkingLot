package com.example.parkinglot

class ImageModel {

    var name: String? = null
    var image_drawable: Int = 0
    var precio: String? = null
    var horario: String? = null

    fun getNames(): String {
        return name.toString()
    }

    fun setNames(name: String) {
        this.name = name
    }

    fun getImage_drawables(): Int {
        return image_drawable
    }

    fun setImage_drawables(image_drawable: Int) {
        this.image_drawable = image_drawable
    }

    fun getPrecios(): String {
        return precio.toString()
    }

    fun setPrecios(precio: String) {
        this.precio = precio
    }

    fun getHorarios(): String {
        return horario.toString()
    }

    fun setHorarios(horario: String) {
        this.horario = horario
    }

}