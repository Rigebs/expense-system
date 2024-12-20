package com.riga.expenc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.riga.expenc.model.Categoria

class CategoriaAdapter(context: Context, var categorias: List<Categoria>) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return categorias.size
    }

    override fun getItem(position: Int): Any {
        return categorias[position]
    }

    override fun getItemId(position: Int): Long {
        return categorias[position].id
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: inflater.inflate(R.layout.item_spinner, parent, false)
        val categoria = getItem(position) as Categoria
        val textView = view.findViewById<TextView>(R.id.spinnerItemText)
        textView.text = categoria.nombre
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: inflater.inflate(R.layout.spinner_dropdown_item, parent, false)
        val categoria = getItem(position) as Categoria
        val textView = view.findViewById<TextView>(R.id.spinnerItemText)
        textView.text = categoria.nombre
        return view
    }
}