package com.riga.expenc

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.riga.expenc.activity.ListaGastosActivity
import com.riga.expenc.activity.RegistrarGastoActivity

class MainActivity : AppCompatActivity() {

    private lateinit var cvGastos: CardView
    private lateinit var tvGasto: TextView
    private lateinit var ivGasto: ImageView

    private lateinit var cvRegistrarGasto: CardView
    private lateinit var ivRegistrarGasto: ImageView
    private lateinit var tvRegistrarGasto: TextView

    private lateinit var tvMontoMes: TextView
    private lateinit var tvMontoSemana: TextView
    private lateinit var databaseManager: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initComponents()

        databaseManager = DatabaseManager(this)

        val monto = databaseManager.sumMontosMesActual()

        println("MONTO: $monto")

        val color: Int = ContextCompat.getColor(this, R.color.CardViewSelectedBackground)

        cvGastos.setOnClickListener {
            val intent = Intent(this, ListaGastosActivity::class.java)
            val colorCambio = ContextCompat.getColor(this, R.color.white)
            cvGastos.setCardBackgroundColor(color)
            tvGasto.setTextColor(colorCambio)
            ivGasto.setColorFilter(colorCambio)
            startActivity(intent)
        }

        cvRegistrarGasto.setOnClickListener {
            val cvColor: Int = ContextCompat.getColor(this, R.color.CardViewRegistrar)
            val intent = Intent(this, RegistrarGastoActivity::class.java)
            cvRegistrarGasto.setCardBackgroundColor(cvColor)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        resetColor()
        actualizarMonto()
    }

    private fun resetColor() {
        val originalTextColor: Int = ContextCompat.getColor(this, R.color.black)
        val originalImageColor: Int = ContextCompat.getColor(this, R.color.CardViewBackground)

        val cvRegistrarColor: Int = ContextCompat.getColor(this, R.color.ActivityRegistrarGastoBackground)

        tvGasto.setTextColor(originalTextColor)
        ivGasto.setColorFilter(originalTextColor)
        cvGastos.setCardBackgroundColor(originalImageColor)

        cvRegistrarGasto.setCardBackgroundColor(cvRegistrarColor)
    }

    private fun initComponents(){
        cvGastos = findViewById(R.id.cvGastos)
        tvGasto = findViewById(R.id.tvGasto)
        ivGasto = findViewById(R.id.ivGasto)

        cvRegistrarGasto = findViewById(R.id.cvRegistrarGasto)
        ivRegistrarGasto = findViewById(R.id.ivRegistrarGasto)
        tvRegistrarGasto = findViewById(R.id.tvRegistrarGasto)

        tvMontoMes = findViewById(R.id.tvMontoMes)
        tvMontoSemana = findViewById(R.id.tvMontoSemana)
    }

    private fun actualizarMonto() {
        databaseManager = DatabaseManager(this)
        val montoMes = databaseManager.sumMontosMesActual()
        tvMontoMes.text = "s/$montoMes"

        val montoSemana = databaseManager.sumMontosSemanaActual()
        tvMontoSemana.text = "s/$montoSemana"
    }
}