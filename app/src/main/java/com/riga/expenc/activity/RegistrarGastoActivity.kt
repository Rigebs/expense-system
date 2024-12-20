package com.riga.expenc.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.riga.expenc.DatabaseManager
import com.riga.expenc.R
import com.riga.expenc.CategoriaAdapter
import com.riga.expenc.model.Categoria
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegistrarGastoActivity : AppCompatActivity() {
    private lateinit var databaseManager: DatabaseManager
    private var categoriaSeleccionadaId: Long = 0
    private lateinit var categorias: List<Categoria>
    private lateinit var values: HashMap<String, Long>
    private lateinit var adapter: CategoriaAdapter

    private lateinit var etNombre: EditText
    private lateinit var etMonto: EditText
    private lateinit var etFecha: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var btnMostrarRegistros: Button
    private lateinit var btnAgregarCategoria: Button
    private lateinit var btnFecha: Button
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_gasto)

        databaseManager = DatabaseManager(this)

        etNombre = findViewById(R.id.etNombre)
        etMonto = findViewById(R.id.etMonto)
        etFecha = findViewById(R.id.etFecha)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        btnMostrarRegistros = findViewById(R.id.btnMostrar)
        btnAgregarCategoria = findViewById(R.id.btnAgregarCategoria)
        spinner = findViewById(R.id.spinnerCategoria)
        btnFecha = findViewById(R.id.btnFecha)

        categorias = databaseManager.getAllCategorias()

        values = HashMap()
        for (categoria in categorias) {
            values[categoria.nombre] = categoria.id
        }

        adapter = CategoriaAdapter(this, categorias)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val categoriaSeleccionada = categorias[position]
                categoriaSeleccionadaId = categoriaSeleccionada.id
                Toast.makeText(this@RegistrarGastoActivity, "ID: ${categoriaSeleccionada.id}, Nombre: ${categoriaSeleccionada.nombre}", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                categoriaSeleccionadaId = 0
            }
        }

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val monto = etMonto.text.toString().toDoubleOrNull()
            val fecha = etFecha.text.toString()

            if (nombre.isNotEmpty() && monto != null && fecha.isNotEmpty() && categoriaSeleccionadaId != 0L) {
                databaseManager.addGasto(nombre, monto, fecha, categoriaSeleccionadaId)
                Toast.makeText(this, "Gasto registrado correctamente", Toast.LENGTH_SHORT).show()
                etNombre.text.clear()
                etMonto.text.clear()
                etFecha.text.clear()
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }

        btnMostrarRegistros.setOnClickListener {
            val intent = Intent(this, ListaGastosActivity::class.java)
            startActivity(intent)
        }

        btnAgregarCategoria.setOnClickListener {
            mostrarDialogoAgregarCategoria()
        }

        btnFecha.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun mostrarDialogoAgregarCategoria() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_add_category, null)

        val etNombreCategoria = view.findViewById<EditText>(R.id.etNombreCategoria)
        val btnAgregar = view.findViewById<Button>(R.id.btnAgregar)

        builder.setView(view)
        val dialog = builder.create()

        btnAgregar.setOnClickListener {
            val nombreCategoria = etNombreCategoria.text.toString()
            if (nombreCategoria.isNotEmpty()) {
                databaseManager.addCategoria(nombreCategoria)
                categorias = databaseManager.getAllCategorias()

                // Actualizar el HashMap
                values.clear()
                for (categoria in categorias) {
                    values[categoria.nombre] = categoria.id
                }

                adapter.categorias = categorias
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Por favor, ingrese un nombre para la categoría", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Ajusta el mes porque en Calendar el mes es 0-based
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)

            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate.time)

            etFecha.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

}