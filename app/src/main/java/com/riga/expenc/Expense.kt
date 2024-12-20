package com.riga.expenc

data class Expense(
    val id: Long,
    val nombre: String,
    val monto: Double,
    val fecha: String,
    val categoriaId: Long
)