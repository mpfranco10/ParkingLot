package com.example.parkinglot.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.parkinglot.R
import kotlinx.android.synthetic.main.fragment_dashboard.view.*


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        //val textView: TextView = root.findViewById(R.id.text_dashboard)
        //dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
        //    textView.text = it
        // })
        root.bScan.setOnClickListener { view ->
            Toast.makeText(getActivity(),"Función para escanear QR", Toast.LENGTH_SHORT).show();
        }

        root.bSearch.setOnClickListener { view ->
            var editTextHello = root.findViewById(R.id.textInputEditText) as EditText
            Toast.makeText(getActivity(),"Se recibió el código!" + editTextHello.text, Toast.LENGTH_SHORT).show();
        }
    
        return root
    }
}
