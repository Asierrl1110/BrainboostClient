package com.example.clienteproyectofinal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import modelo.DTOTarjeta

class AdaptadorTarjeta(private val context : Context, private val lista : MutableList<DTOTarjeta>) : BaseAdapter() {
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
        val view : View = convertView ?: LayoutInflater.from(context).inflate(R.layout.layout_tarjeta,parent,false)

        val tvPregunta = view.findViewById<TextView>(R.id.tvPregunta)
        val tvNombreMazo = view.findViewById<TextView>(R.id.tvNombreMazo)

        val current_tarjeta = getItem(position) as DTOTarjeta

        tvPregunta.text = current_tarjeta.pregunta.toString()
        tvNombreMazo.text = current_tarjeta.nombreMazo.toString()

        return view
    }
}