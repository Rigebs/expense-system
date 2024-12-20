package com.riga.expenc.model

data class Gasto(
    val id: Long,
    val nombre: String,
    val monto: Double,
    val fecha: String,
    val categoriaId: Long
)
