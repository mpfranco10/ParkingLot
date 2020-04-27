package com.example.parkinglot.ui.dashboard

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.parkinglot.ParqueaderoActivity
import com.example.parkinglot.ParqueaderoFirebase
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
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "esta_parqueado"

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
                var sg = editTextHello.text.toString()

                if(sg.trim().length>0) {

                    val connectivityManager =
                        context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val networkInfo = connectivityManager.activeNetworkInfo

                    if (networkInfo != null && networkInfo.isConnected) {
                        val docRef =
                            database.collection("malls").document(editTextHello.text.toString())
                        println("PASA POR ACA")

                        docRef.get()
                            .addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                                if (task.isSuccessful) {
                                    val document = task.result?.toString()
                                    val out = task.result

                                    if (!document?.contains("doc=null")!!) {

                                        //CREAR un objeto a partir de la bD de firebase
                                        val note: ParqueaderoFirebase? = task.result!!.toObject(ParqueaderoFirebase::class.java)

                                        //ponemos en las sharedpreferences la P de PARQUEADO
                                        val sharedPref: SharedPreferences? = activity?.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
                                        if (sharedPref != null) {
                                            Log.d("shared", sharedPref.getString(PREF_NAME, "DEFAULT"))
                                        }
                                        val editor = sharedPref?.edit()
                                        if (editor != null) {
                                            editor.putString(PREF_NAME, "P")
                                            editor.apply()
                                        }



                                        val lfile = File(context?.getFilesDir(), "PARQUEADERO.txt")
                                        lfile.createNewFile()
                                        val lfilewriter = FileWriter(lfile)
                                        val lout = BufferedWriter(lfilewriter)
                                        lout.write(editTextHello.text.toString() + ";" + Calendar.getInstance().timeInMillis.toString())
                                        lout.close()

                                        //se lo mandamos a la otra actividad para que lo muestre
                                        val intent = Intent(activity, ParqueaderoActivity::class.java)
                                        intent.putExtra("extra_object", note)
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(
                                            getActivity(),
                                            "No existe el parqueadero: " + editTextHello.text,
                                            Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.exception)
                                }
                            })
                    } else {
                        Toast.makeText(getActivity(), "No hay internet!", Toast.LENGTH_SHORT)
                            .show();

                        val lfile = File(context?.getFilesDir(), "PENDIENTEPARQUEADERO.txt")
                        lfile.createNewFile()
                        val lfilewriter = FileWriter(lfile)
                        val lout = BufferedWriter(lfilewriter)
                        lout.write(editTextHello.text.toString() + ";" + java.util.Calendar.getInstance().timeInMillis.toString())
                        lout.close()
                    }

                }
            else{
                    Toast.makeText(
                        getActivity(),
                        "Por favor llena el campo de código para buscar tu parqueadero",
                        Toast.LENGTH_LONG
                    ).show()
                }

        }

        //desactivamos los botones de buscar si ya está parqueado !!!
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences(PREF_NAME, PRIVATE_MODE)

        println("corrrrrrrrrrrrrrrrreeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"+sharedPref?.getString(PREF_NAME, "DEFAULT"))
        if (sharedPref != null) {

            if ( sharedPref.getString(PREF_NAME, "DEFAULT").equals("P") ) { //si esta parqueado
                root.bScan.isEnabled = false
                root.bScan.visibility = View.GONE
                root.bSearch.isEnabled = false
                root.bSearch.visibility = View.GONE
                root.buttonActual.isEnabled = true
                root.buttonActual.visibility = View.VISIBLE
            }
            else if ( sharedPref.getString(PREF_NAME, "DEFAULT").equals("N") ) { //si esta parqueado
                root.bScan.isEnabled = true
                root.bScan.visibility = View.VISIBLE
                root.bSearch.isEnabled = true
                root.bSearch.visibility = View.VISIBLE
                root.buttonActual.isEnabled = false
                root.buttonActual.visibility = View.GONE
            }
            else
            {
                val editor = sharedPref?.edit()
                if (editor != null) {
                    editor.putString(PREF_NAME, "N")
                    editor.apply()
                }
                root.bScan.isEnabled = true
                root.bScan.visibility = View.VISIBLE
                root.bSearch.isEnabled = true
                root.bSearch.visibility = View.VISIBLE
                root.buttonActual.isEnabled = false
                root.buttonActual.visibility = View.GONE
            }
        }
        else
        {
                Log.d("shared", sharedPref?.getString(PREF_NAME, "DEFAULT"))
            val editor = sharedPref?.edit()
            if (editor != null) {
                editor.putString(PREF_NAME, "N")
                editor.apply()
            }
            root.bScan.isEnabled = true
            root.bScan.visibility = View.VISIBLE
            root.bSearch.isEnabled = true
            root.bSearch.visibility = View.VISIBLE
            root.buttonActual.isEnabled = false
            root.buttonActual.visibility = View.GONE
        }

        root.buttonActual.setOnClickListener {
            var databaseFinal = FirebaseFirestore.getInstance()

            val lfile = File(context?.getFilesDir(), "PARQUEADERO.txt")
            var result = ""
            lfile.forEachLine {
                result = it.split(";")[0]
            }




                val docRefFinal =
                databaseFinal.collection("malls").document(result)

            docRefFinal.get()
                .addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                    if (task.isSuccessful) {
                        val document = task.result?.toString()
                        val out = task.result

                        if (!document?.contains("doc=null")!!) {
                            val note: ParqueaderoFirebase? = task.result!!.toObject(ParqueaderoFirebase::class.java)
                            val intent = Intent(activity, ParqueaderoActivity::class.java)

                            intent.putExtra("extra_object", note)
                            startActivity(intent)
                        }
                    }
                })
        }
    
        return root
    }
}
