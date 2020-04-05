package com.example.parkinglot.ui.parkinglots

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.parkinglot.MainActivity
import com.example.parkinglot.R
import java.io.File


class ParkinglotsFragment : Fragment() {

    private lateinit var parkinglotsViewModel: ParkinglotsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        parkinglotsViewModel =
                ViewModelProviders.of(this).get(ParkinglotsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_parkinglots, container, false)
        // val textView: TextView = root.findViewById(R.id.text_notifications)
        // notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
        //    textView.text = it
        // })

        var lv = root.findViewById<ListView>(R.id.listview)
        val context = context as MainActivity

        val arrayList = ArrayList<String>()//Creating an empty arraylist

        arrayList.add(" ")
        arrayList.add(" ")

        val lfile = File(context!!.filesDir, "PARQUEOS.txt")

        lfile.forEachLine {
            arrayList.add(it)
        }

        //esto sirve pero sale error no se por que xd
        val adapter = ArrayAdapter(context,  R.layout.simple_list_item_1 , arrayList)
        lv.adapter = adapter

        return root
    }
}
