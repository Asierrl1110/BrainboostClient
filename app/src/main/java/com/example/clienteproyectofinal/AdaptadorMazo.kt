package com.example.clienteproyectofinal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import modelo.DTOMazo

class AdaptadorMazo(private val context : Context, private val lista : MutableList<DTOMazo>) : BaseAdapter() {
    override fun getCount(): Int {
        return lista.size
    }

    override fun getItem(position: Int): Any {
        return lista[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View = convertView ?: LayoutInflater.from(context).inflate(R.layout.layout_mazo,parent,false)

        val tvNombre = view.findViewById<TextView>(R.id.tvNombre)
        val tvCategoria = view.findViewById<TextView>(R.id.tvCategoria)

        val current_mazo = getItem(position) as DTOMazo

        tvNombre.text = current_mazo.nombre.toString()
        tvCategoria.text = current_mazo.categoria.toString()

        return view
    }


}