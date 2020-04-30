package com.example.parkinglot.ui.notifications

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
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.parkinglot.*
import com.example.parkinglot.login.ui.login.LoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.fragment_notifications.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import kotlin.math.roundToInt


class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "alreadylogged"
    private var nuser = ""

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        // val textView: TextView = root.findViewById(R.id.text_notifications)
        // notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
        //    textView.text = it
        // })

        var database = FirebaseFirestore.getInstance()
        var databaseInstance =  FirebaseDatabase.getInstance().getReference("malls")

        val t:TextView= root.findViewById(R.id.text_notifications2)
        val tum:TextView= root.findViewById(R.id.text_edad2)
        val ted:TextView= root.findViewById(R.id.text_edad)
        val tsaldo: TextView = root.findViewById(R.id.tv_saldo)

        val btncerrar: Button = root.findViewById(R.id.bClose)
        val btnrecargar: Button = root.findViewById(R.id.bRecargar)
        val db = FirebaseFirestore.getInstance()


        // [END get_firestore_instance]

        var perfil = ""

        // [START set_firestore_settings]
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        db.firestoreSettings = settings
        // [END set_firestore_settings]




        val sharedPref: SharedPreferences? = activity?.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        if (sharedPref != null) {
            val gnombre = sharedPref.getString("nombre", "DEFAULT")
            val guser = sharedPref.getString("username", "DEFAULT")
            nuser = guser.toString()
            val gtel = sharedPref.getString("telefono", "DEFAULT")
            val gsal = sharedPref.getString("saldo", "DEFAULT")

            if(gnombre!="DEFAULT" && gtel!="DEFAULT" && gsal!="DEFAULT"){
                t.text = gnombre
                tum.text = gtel
                ted.text = guser
                tsaldo.text = gsal
            }
        }


        val docRef =
            database.collection("users").document(nuser)

        if(isNetwork()){ //actualizamos los atributos
        docRef.get()
            .addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                if (task.isSuccessful) {
                    val document = task.result?.toString()
                    val out = task.result
                    if (!document?.contains("doc=null")!!) {

                        val note: User? = task.result!!.toObject(User::class.java)
                        if(note!=null){
                            t.text = note.firstname + " " + note.lastname
                            tum.text = note.phone_number.toString()
                            ted.text = note.username.toString()
                            tsaldo.text = note.saldo.toString()

                            val sharedPref: SharedPreferences? = activity?.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
                            val editor = sharedPref?.edit()
                            if (editor != null) {
                                editor.putString("nombre", note.firstname + " " + note.lastname)
                                editor.putString("telefono", note.phone_number.toString())
                                editor.putString("saldo",  note.saldo.toString())
                                editor.apply()
                            }
                        }
                    }
                    else {
                        Toast.makeText(
                            getActivity(),
                            "No existe el usuario",
                            Toast.LENGTH_SHORT
                        ).show();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.exception)
                }
            })}
            ////////////////////////


        btncerrar.setOnClickListener{
            val context = context as MainActivity
            val sharedPref: SharedPreferences? = activity?.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            val editor = sharedPref?.edit()
            if (editor != null) {
                editor.putBoolean(PREF_NAME, true)
                editor.apply()
            }

            val sharedPref2: SharedPreferences? =
                activity?.getSharedPreferences("esta_parqueado", PRIVATE_MODE)
            val editor2 = sharedPref?.edit()
            if (editor2 != null) {
                editor2.putString(PREF_NAME, "N")
                editor2.apply()
            }
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            context.finish()
        }

        btnrecargar.setOnClickListener {
            Toast.makeText(getActivity(),"Próximamente: ", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    fun isNetwork(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        // In here we return true if network is not null and Network is connected
        if(networkInfo != null && networkInfo.isConnected){
            return true
        }
        return false

    }
}
