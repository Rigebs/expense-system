package com.riga.expenc.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.riga.expenc.DatabaseManager
import com.riga.expenc.GastoAdapter
import com.riga.expenc.R
import com.riga.expenc.model.Gasto
import android.view.LayoutInflater
import android.app.DatePickerDialog
import android.app.AlertDialog
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import com.riga.expenc.CategoriaAdapter
import com.riga.expenc.model.Categoria
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ListaGastosActivity : AppCompatActivity() {

    private lateinit var databaseManager: DatabaseManager

    private lateinit var ivBuscar: ImageView
    private lateinit var etBuscar: EditText
    private lateinit var ivAvanzado: ImageView

    private var categoriaSeleccionadaId: Long = 0

    private lateinit var categorias: List<Categoria>
    private lateinit var listaGastos: List<Gasto>
    private lateinit var listView: ListView
    private lateinit var categoriaAdapter: CategoriaAdapter
    private lateinit var gastoAdapter: GastoAdapter
    private lateinit var values: HashMap<String, Long>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_gastos)

        initComponents()
        initListeners()
        list()
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                editText.setText(dateFormat.format(selectedDate.time))
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun list() {
        databaseManager = DatabaseManager(this)
        listaGastos = databaseManager.getAllGastos()

        val adapter = GastoAdapter(this, listaGastos)
        listView.adapter = adapter
    }

    private fun initComponents() {
        ivBuscar = findViewById(R.id.ivBuscar)
        ivAvanzado = findViewById(R.id.ivAvanzado)
        etBuscar = findViewById(R.id.etBuscar)
        listView = findViewById(R.id.lvGastos)
    }

    private fun initListeners() {
        ivBuscar.setOnClickListener { search() }
        ivAvanzado.setOnClickListener { showFilterDialog() }
    }

    private fun search() {
        val query = etBuscar.text
        listaGastos = databaseManager.searchGastos(query.toString())
        gastoAdapter = GastoAdapter(this, listaGastos)

        listView.adapter = gastoAdapter
    }

    private fun showFilterDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_filtro_gasto, null)

        val dialog = builder.setView(view).create()

        val spinnerFiltroCategoria = view.findViewById<Spinner>(R.id.spinnerFilter)
        val etFechaInicio = view.findViewById<EditText>(R.id.etFechaInicio)
        val etFechaFin = view.findViewById<EditText>(R.id.etFechaFin)
        val btnFiltrar = view.findViewById<Button>(R.id.btnFiltrar)

        categorias = databaseManager.getAllCategorias()

        values = HashMap()
        for (categoria in categorias) {
            values[categoria.nombre] = categoria.id
        }

        categoriaAdapter = CategoriaAdapter(this, categorias)
        spinnerFiltroCategoria.adapter = categoriaAdapter

        spinnerFiltroCategoria.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val categoriaSeleccionada = categorias[position]
                    categoriaSeleccionadaId = categoriaSeleccionada.id
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    categoriaSeleccionadaId = 0
                }
            }

        // Configuración de los DatePickers
        etFechaInicio.setOnClickListener { showDatePickerDialog(etFechaInicio) }
        etFechaFin.setOnClickListener { showDatePickerDialog(etFechaFin) }

        // Acción del botón Filtrar
        btnFiltrar.setOnClickListener {
            val selectedFilter = spinnerFiltroCategoria.selectedItem.toString()
            val fechaInicio = etFechaInicio.text.toString()
            val fechaFin = etFechaFin.text.toString()

            listaGastos =
                databaseManager.filtrarGastos(categoriaSeleccionadaId, fechaInicio, fechaFin)

            gastoAdapter = GastoAdapter(this, listaGastos)

            listView.adapter = gastoAdapter

            Toast.makeText(
                this,
                "Filtro: $selectedFilter, Desde: $fechaInicio, Hasta: $fechaFin",
                Toast.LENGTH_LONG
            ).show()

            dialog.dismiss()
        }
        dialog.show()
    }

    fun showOptionsDialog(id: Long) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_options, null)

        val btnEditar = view.findViewById<Button>(R.id.btnEditar)
        val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)

        val dialog = builder.setView(view).create()

        btnEliminar.setOnClickListener {
            showConfirmationDialog()
            databaseManager.deleteGasto(id)
            list()
            Toast.makeText(this, "Gasto eliminado", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        btnEditar.setOnClickListener {
            Toast.makeText(this, "Editar funcionalidad no implementada", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showConfirmationDialog(): Boolean {
        var isConfirmated = true
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)

        val view = inflater.inflate(R.layout.dialog_confirmation, null)
        val dialog = builder.setView(view).create()

        val btnConfirmar = view.findViewById<Button>(R.id.btnConfirmar)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelar)

        btnConfirmar.setOnClickListener { isConfirmated = true }

        btnCancelar.setOnClickListener { isConfirmated = false }
        dialog.show()
        return isConfirmated
    }

}