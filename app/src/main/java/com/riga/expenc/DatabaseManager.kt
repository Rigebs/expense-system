package com.riga.expenc

import android.content.ContentValues
import android.content.Context
import com.riga.expenc.OpenHelper.Companion.TABLE_GASTO
import com.riga.expenc.model.Categoria
import com.riga.expenc.model.Gasto
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DatabaseManager(context: Context) {

    private val dbHelper = OpenHelper(context)

    fun addCategoria(nombre: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(OpenHelper.COLUMN_CATEGORIA_NOMBRE, nombre)
        }

        val result = db.insert(OpenHelper.TABLE_CATEGORIA, null, values)
        println("RESULT: $result")
        return result
    }

    fun sumMontosMesActual(): Double {
        val db = dbHelper.readableDatabase
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM-yyyy", Locale.getDefault())
        val currentMonthYear = dateFormat.format(calendar.time)

        // Extrae el mes y el año actual
        val currentMonth = currentMonthYear.split("-")[0]
        val currentYear = currentMonthYear.split("-")[1]

        val query = """
        SELECT SUM(${OpenHelper.COLUMN_GASTO_MONTO}) as total
        FROM $TABLE_GASTO
        WHERE substr(${OpenHelper.COLUMN_GASTO_FECHA}, 4, 2) = ? 
        AND substr(${OpenHelper.COLUMN_GASTO_FECHA}, 7, 4) = ?
    """

        val cursor = db.rawQuery(query, arrayOf(currentMonth, currentYear))
        var total = 0.0

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
        }
        cursor.close()
        return total
    }

    fun sumMontosSemanaActual(): Double {
        val db = dbHelper.readableDatabase
        val calendar = Calendar.getInstance()

        // Setear el calendario al primer día de la semana (Lunes)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val startOfWeek = dateFormat.format(calendar.time)

        // Setear el calendario al último día de la semana (Domingo)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endOfWeek = dateFormat.format(calendar.time)

        val query = """
        SELECT SUM(${OpenHelper.COLUMN_GASTO_MONTO}) as total
        FROM $TABLE_GASTO
        WHERE fecha BETWEEN ? AND ?
    """

        val cursor = db.rawQuery(query, arrayOf(startOfWeek, endOfWeek))
        var total = 0.0

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
        }
        cursor.close()
        return total
    }

    fun getAllCategorias(): List<Categoria> {
        val categorias = mutableListOf<Categoria>()
        val db = dbHelper.readableDatabase

        val columns = arrayOf(
            OpenHelper.COLUMN_CATEGORIA_ID,
            OpenHelper.COLUMN_CATEGORIA_NOMBRE
        )

        val cursor = db.query(
            OpenHelper.TABLE_CATEGORIA, columns, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(OpenHelper.COLUMN_CATEGORIA_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(OpenHelper.COLUMN_CATEGORIA_NOMBRE))

                val categoria = Categoria(id = id, nombre = nombre)
                categorias.add(categoria)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return categorias
    }

    fun addGasto(nombre: String, monto: Double, fecha: String, categoriaId: Long): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(OpenHelper.COLUMN_GASTO_NOMBRE, nombre)
            put(OpenHelper.COLUMN_GASTO_MONTO, monto)
            put(OpenHelper.COLUMN_GASTO_FECHA, fecha)
            put(OpenHelper.COLUMN_GASTO_CATEGORIA_ID, categoriaId)
        }

        val result = db.insert(TABLE_GASTO, null, values)
        return result
    }

    fun deleteGasto(id: Long) {
        val db = dbHelper.writableDatabase
        db.delete(TABLE_GASTO, "${OpenHelper.COLUMN_GASTO_ID} = ?", arrayOf(id.toString()))
    }

    fun getAllGastos(): List<Gasto> {
        val gastos = mutableListOf<Gasto>()
        val db = dbHelper.readableDatabase

        val columns = arrayOf(
            OpenHelper.COLUMN_GASTO_ID,
            OpenHelper.COLUMN_GASTO_NOMBRE,
            OpenHelper.COLUMN_GASTO_MONTO,
            OpenHelper.COLUMN_GASTO_FECHA,
            OpenHelper.COLUMN_GASTO_CATEGORIA_ID
        )

        val cursor = db.query(
            TABLE_GASTO, columns, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(OpenHelper.COLUMN_GASTO_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(OpenHelper.COLUMN_GASTO_NOMBRE))
                val monto = cursor.getDouble(cursor.getColumnIndexOrThrow(OpenHelper.COLUMN_GASTO_MONTO))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow(OpenHelper.COLUMN_GASTO_FECHA))
                val categoriaId = cursor.getLong(cursor.getColumnIndexOrThrow(OpenHelper.COLUMN_GASTO_CATEGORIA_ID))

                val gasto = Gasto(id = id, nombre = nombre, monto = monto, fecha = fecha, categoriaId = categoriaId)
                gastos.add(gasto)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return gastos
    }

    fun searchGastos(query: String): List<Gasto> {
        val db = dbHelper.readableDatabase
        val gastos = mutableListOf<Gasto>()

        val columns = arrayOf(
            OpenHelper.COLUMN_GASTO_ID,
            OpenHelper.COLUMN_GASTO_NOMBRE,
            OpenHelper.COLUMN_GASTO_MONTO,
            OpenHelper.COLUMN_GASTO_FECHA,
            OpenHelper.COLUMN_GASTO_CATEGORIA_ID
        )

        // Consulta SQL para buscar coincidencias parciales en la columna `nombre`.
        val cursor = db.query(
            TABLE_GASTO,                     // Tabla
            columns,
            "${OpenHelper.COLUMN_GASTO_NOMBRE} LIKE ?",              // Condición WHERE
            arrayOf("%$query%"),          // Valores de los argumentos de la consulta
            null,                         // Group By
            null,                         // Having
            null                          // Order By
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(OpenHelper.COLUMN_GASTO_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(OpenHelper.COLUMN_GASTO_NOMBRE))
                val monto = cursor.getDouble(cursor.getColumnIndexOrThrow(OpenHelper.COLUMN_GASTO_MONTO))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow(OpenHelper.COLUMN_GASTO_FECHA))
                val categoriaId = cursor.getLong(cursor.getColumnIndexOrThrow(OpenHelper.COLUMN_GASTO_CATEGORIA_ID))

                val gasto = Gasto(id = id, nombre = nombre, monto = monto, fecha = fecha, categoriaId = categoriaId)
                gastos.add(gasto)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return gastos
    }

    fun filtrarGastos(categoriaId: Long?, fechaInicio: String?, fechaFin: String?): List<Gasto> {
        val db = dbHelper.readableDatabase
        val gastos = mutableListOf<Gasto>()

        // Construir la consulta básica
        val queryBuilder = StringBuilder()
        queryBuilder.append("SELECT * FROM $TABLE_GASTO WHERE 1=1")

        // Agregar filtro de categoriaId si se proporciona
        val args = mutableListOf<String>()
        if (categoriaId != null) {
            queryBuilder.append(" AND ${OpenHelper.COLUMN_GASTO_CATEGORIA_ID} = ?")
            args.add(categoriaId.toString())
        }

        // Agregar filtro de fechaInicio y fechaFin si se proporcionan
        if (!fechaInicio.isNullOrEmpty() && !fechaFin.isNullOrEmpty()) {
            queryBuilder.append(" AND ${OpenHelper.COLUMN_GASTO_FECHA} BETWEEN ? AND ?")
            args.add(fechaInicio)
            args.add(fechaFin)
        }

        // Ejecutar la consulta
        val cursor = db.rawQuery(queryBuilder.toString(), args.toTypedArray())

        // Recorrer el cursor para construir la lista de resultados
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(OpenHelper.COLUMN_GASTO_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(OpenHelper.COLUMN_GASTO_NOMBRE))
                val monto = cursor.getDouble(cursor.getColumnIndexOrThrow(OpenHelper.COLUMN_GASTO_MONTO))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow(OpenHelper.COLUMN_GASTO_FECHA))
                val catId = cursor.getLong(cursor.getColumnIndexOrThrow(OpenHelper.COLUMN_GASTO_CATEGORIA_ID))

                val gasto = Gasto(id = id, nombre = nombre, monto = monto, fecha = fecha, categoriaId = catId)
                gastos.add(gasto)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return gastos
    }
}