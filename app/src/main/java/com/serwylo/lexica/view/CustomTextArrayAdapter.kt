package com.serwylo.lexica.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

/**
 * See [ArrayAdapter], except this allows you to decide how to generate labels based on the
 * lambda passed in.
 *
 * Note this is hard coded to the [android.R.layout.simple_spinner_dropdown_item], but could
 * be made more generic if required.
 */
class CustomTextArrayAdapter<T>(context: Context, val values: List<T>, val toLabel: (obj: T) -> String) : ArrayAdapter<T>(context, android.R.layout.simple_spinner_dropdown_item, values) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val item = getItem(position)

        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = if (item == null) "" else toLabel(item)

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val item = getItem(position)

        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = if (item == null) "" else toLabel(item)

        return view
    }
}