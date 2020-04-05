package com.example.parkinglot

class Parqueadero {

    var id: Int = 0
    var userName: String? = null
    var precio: String? = null
    var horario: String? = null
    var imagen: String? = null

    constructor(id: Int, userName: String, precio: String, horario: String, imagen: String) {
        this.id = id
        this.userName = userName
        this.precio = precio
        this.horario = horario
        this.imagen = imagen
    }
    constructor(userName: String, precio: String, horario: String, imagen: String) {
        this.userName = userName
        this.precio = precio
        this.horario = horario
        this.imagen = imagen
    }
}