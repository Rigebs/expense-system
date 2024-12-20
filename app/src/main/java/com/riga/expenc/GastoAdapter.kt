package com.riga.expenc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.riga.expenc.activity.ListaGastosActivity
import com.riga.expenc.model.Gasto

class GastoAdapter(private val context: ListaGastosActivity, private val dataSource: List<Gasto>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = convertView ?: inflater.inflate(R.layout.item_gasto, parent, false)

        val nombreTextView = rowView.findViewById<TextView>(R.id.tvNombre)
        val montoTextView = rowView.findViewById<TextView>(R.id.tvMonto)
        val fechaTextView = rowView.findViewById<TextView>(R.id.tvFecha)
        val btnOpciones = rowView.findViewById<ImageView>(R.id.ivOpciones)

        val gasto = getItem(position) as Gasto

        nombreTextView.text = gasto.nombre
        montoTextView.text = gasto.monto.toString()
        fechaTextView.text = gasto.fecha

        btnOpciones.setOnClickListener {
            context.showOptionsDialog(gasto.id)
        }

        return rowView
    }
}