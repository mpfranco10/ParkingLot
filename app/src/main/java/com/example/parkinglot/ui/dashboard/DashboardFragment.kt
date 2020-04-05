package com.example.parkinglot.ui.dashboard

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.parkinglot.R
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.time.Instant
import java.time.format.DateTimeFormatter

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
            Toast.makeText(getActivity(), "Función para escanear QR", Toast.LENGTH_SHORT).show();
        }

        root.bSearch.setOnClickListener { view ->
            var editTextHello = root.findViewById(R.id.textInputEditText) as EditText
            guardar(editTextHello.text.toString())
            Toast.makeText(
                getActivity(),
                "Se recibió el código!" + editTextHello.text,
                Toast.LENGTH_SHORT
            ).show();
        }

        return root
    }

    fun guardar(str: String)
    {
        var resp = str
        val lfile = File(context!!.filesDir, "PARQUEOS.txt")
        //lfile.createNewFile()

        lfile.forEachLine {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                resp = it + ";"+ DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString() + "\n" + resp
            }
            else
            {
                resp = it + ";nil" + "\n" + resp
            }
        }

        val lfilewriter = FileWriter(lfile)

        val lout = BufferedWriter(lfilewriter)
        lout.write(resp)
        lout.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}
