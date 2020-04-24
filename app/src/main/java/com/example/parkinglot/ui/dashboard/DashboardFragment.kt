package com.example.parkinglot.ui.dashboard

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
<<<<<<< Updated upstream
import android.os.Build
=======
import android.net.ConnectivityManager
>>>>>>> Stashed changes
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.parkinglot.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import java.io.BufferedWriter
import java.io.File
<<<<<<< Updated upstream
import java.io.FileOutputStream
import java.io.FileWriter
import java.time.Instant
import java.time.format.DateTimeFormatter
=======
import java.io.FileWriter

>>>>>>> Stashed changes

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
<<<<<<< Updated upstream
            ViewModelProviders.of(this).get(DashboardViewModel::class.java)
=======
                ViewModelProviders.of(this).get(DashboardViewModel::class.java)

        var database = FirebaseFirestore.getInstance()

>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
            guardar(editTextHello.text.toString())
            actualizar(editTextHello.text.toString())
            Toast.makeText(
                getActivity(),
                "Se recibió el código!" + editTextHello.text,
                Toast.LENGTH_SHORT
            ).show();
=======

            val connectivityManager= context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo=connectivityManager.activeNetworkInfo

            if(networkInfo!=null && networkInfo.isConnected)
            {
                val docRef = database.collection("malls").document(editTextHello.text.toString())

                docRef.get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                    if (task.isSuccessful)
                    {
                        val document = task.result?.toString()
                        val out = task.result
                        println (out)
                        println (document)

                        if (!document?.contains("doc=null")!!)
                        {
                            Toast.makeText(getActivity(),"Se recibió el código!" + editTextHello.text, Toast.LENGTH_SHORT).show();
                            val lfile = File(context?.getFilesDir(), "PARQUEADERO.txt")
                            lfile.createNewFile()
                            val lfilewriter = FileWriter(lfile)
                            val lout = BufferedWriter(lfilewriter)
                            lout.write(editTextHello.text.toString()+";"+java.util.Calendar.getInstance().timeInMillis.toString())
                            lout.close()
                        }
                        else
                        {
                            Toast.makeText(getActivity(),"No existe el parqueadero: " + editTextHello.text, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Log.d(TAG, "get failed with ", task.exception)
                    }
                })
            }
            else
            {
                Toast.makeText(getActivity(),"No hay internet!", Toast.LENGTH_SHORT).show();

                val lfile = File(context?.getFilesDir(), "PENDIENTEPARQUEADERO.txt")
                lfile.createNewFile()
                val lfilewriter = FileWriter(lfile)
                val lout = BufferedWriter(lfilewriter)
                lout.write(editTextHello.text.toString()+";"+java.util.Calendar.getInstance().timeInMillis.toString())
                lout.close()
            }
>>>>>>> Stashed changes
        }

        return root
    }

    private fun actualizar(str: String)
    {
        val lfile = File(context!!.filesDir, "PARQUEADERO.txt")
        lfile.createNewFile()

        val lfilewriter = FileWriter(lfile)

        val lout = BufferedWriter(lfilewriter)
        lout.write(str)
        lout.close()
    }

    fun guardar(str: String)
    {
        var resp = str
        val lfile = File(context!!.filesDir, "PARQUEOS.txt")

        if (!lfile.exists())
        {
            lfile.createNewFile()
        }

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
