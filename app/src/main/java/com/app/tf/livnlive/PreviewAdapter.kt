package com.app.tf.livnlive

import android.content.Context
import android.widget.TextView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.BaseAdapter

class CustomObject(val prop1: String, val prop2: String) {

}

class CustomAdapter(context: Context, private val objects: ArrayList<CustomObject>) : BaseAdapter() {

    private val inflater: LayoutInflater

    private inner class ViewHolder {
        internal var textView1: TextView? = null
        internal var textView2: TextView? = null
    }

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return objects.size
    }

    override fun getItem(position: Int): CustomObject {
        return objects[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        var holder: ViewHolder? = null
        if (convertView == null) {
            holder = ViewHolder()
            convertView = inflater.inflate(R.layout.preview_textfield, null)
            holder.textView1 = convertView.findViewById(R.id.heading)
            holder.textView2 = convertView.findViewById(R.id.detail)
            convertView.setTag(holder)
        } else {
            holder = convertView!!.getTag() as ViewHolder?
        }
        holder?.textView1!!.setText(objects[position].prop1)
        holder?.textView2!!.setText(objects[position].prop2)
        return convertView!!
    }
}

