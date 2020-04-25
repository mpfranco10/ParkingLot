package com.example.parkinglot.ui.dashboard

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.parkinglot.R
import com.example.parkinglot.ScanActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*


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

        var database = FirebaseFirestore.getInstance()

        //val textView: TextView = root.findViewById(R.id.text_dashboard)
        //dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
        //    textView.text = it
        // })



        root.bScan.setOnClickListener {
            val intent = Intent(activity, ScanActivity::class.java)
            startActivity(intent)
        }

        root.bSearch.setOnClickListener { view ->
            var editTextHello = root.findViewById(R.id.textInputEditText) as EditText

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
                            lout.write(editTextHello.text.toString()+";"+ Calendar.getInstance().timeInMillis.toString())
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
        }
    
        return root
    }
}
