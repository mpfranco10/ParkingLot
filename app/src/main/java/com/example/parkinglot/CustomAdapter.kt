package com.example.parkinglot

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import java.util.ArrayList

class CustomAdapter (private val context: Context, private val imageModelArrayList: ArrayList<ImageModel>) : BaseAdapter() {

    override fun getViewTypeCount(): Int {
        return count
    }

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun getCount(): Int {
        return imageModelArrayList.size
    }

    override fun getItem(position: Int): Any {
        return imageModelArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder

        if (convertView == null) {
            holder = ViewHolder()
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.lv_item, null, true)

            holder.tvname = convertView!!.findViewById(R.id.name) as TextView
            holder.iv = convertView.findViewById(R.id.imgView) as ImageView
            holder.tvprecio = convertView!!.findViewById(R.id.precio) as TextView
            holder.tvhorario = convertView!!.findViewById(R.id.horario) as TextView

            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = convertView.tag as ViewHolder
        }

        holder.tvname!!.setText(imageModelArrayList[position].getNames())
        holder.iv!!.setImageResource(imageModelArrayList[position].getImage_drawables())
        holder.tvprecio!!.setText(imageModelArrayList[position].getPrecios())
        holder.tvhorario!!.setText(imageModelArrayList[position].getHorarios())
        return convertView
    }

    private inner class ViewHolder {

        var tvhorario: TextView? = null
        var tvprecio: TextView? = null
        var tvname: TextView? = null
        internal var iv: ImageView? = null

    }

}