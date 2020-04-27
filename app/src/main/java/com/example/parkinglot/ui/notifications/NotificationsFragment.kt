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
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import kotlin.math.roundToInt


class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "alreadylogged"

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

        val tp:TextView= root.findViewById(R.id.textPark)

        val btnPrueba: Button = root.findViewById(R.id.boton_park)
        val btncerrar: Button = root.findViewById(R.id.bClose)
        val db = FirebaseFirestore.getInstance()


        // [END get_firestore_instance]

        var perfil = ""

        // [START set_firestore_settings]
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        db.firestoreSettings = settings
        // [END set_firestore_settings]

        db.collection("users")
            .whereEqualTo("firstname", "Maria")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    perfil = "${document.id} => ${document.data}"
                    val note: User = document.toObject(User::class.java)
                    t.text = note.firstname + " " + note.lastname
                    tum.text = note.phone_number.toString()
                    ted.text = note.birthday.toString()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        //Cambiar esto a si no hay conexión y si todavia no se ha guardado la info ->
        val context = context as MainActivity
        btnPrueba.setOnClickListener{
            btnPrueba.isEnabled = false
            btnPrueba.isClickable = false
            var datos = ""

            //////////////////////
            db.collection("malls")
                .whereEqualTo("name", "Unicentro")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        datos = "${document.data}"
                        tp.text = datos
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }

            /////////////////////////

            if(isNetwork()) {
                val dbHandler = ParqueaderoOpenHelper(context, null)
                val nombre = "Parqueadero"
                val imagen = "-"
                val precio = "Precio"
                val horario = "Horario"
                val user = Parqueadero(nombre, precio, horario, imagen)
                val valores =
                    "Nombre:" + nombre + ",imagen:" + imagen + ",precio:" + precio + ",horario:" + horario

                val lfile = File(context.getFilesDir(), "PARQUEADERO.txt")
                var resp = ""
                var id = ""
                lfile.forEachLine {
                    resp = it.split(";")[1]
                    id = it.split(";")[0]
                }

                if(!resp.equals("EMPTY"))
                {
                    //dbHandler.addName(user)
                    var units = (java.util.Calendar.getInstance().timeInMillis.toDouble()-resp.toDouble())/3600000

                    val docRef = database.collection("malls").document(id.toString())
                    docRef.get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                        if (task.isSuccessful)
                        {
                            val document = task.result?.toString()

                            if (!document?.contains("doc=null")!!)
                            {
                                var str = task.result?.data.toString()
                                var indx = str.indexOf("parking_car_cost=")+17
                                str = str.substring(indx)
                                indx = str.indexOf(",");
                                str = str.substring(0,indx)

                                val lfile = File(context.getFilesDir(), "USERS.txt")
                                var username = "EMPTY"
                                lfile.forEachLine {
                                    username = it.split(";")[0]
                                }

                                if(!username.equals("EMPTY"))
                                {
                                    units = units*str.toDouble()
                                    Toast.makeText(getActivity(),"Debes: " + units.roundToInt().toString() + " !", Toast.LENGTH_SHORT).show();
                                    val docRef2 = database.collection("users").document(username)

                                    docRef2.get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                                        if (task.isSuccessful)
                                        {
                                            val document2 = task.result?.toString()

                                            if (!document2?.contains("doc=null")!!)
                                            {
                                                var x = task.result?.data.toString()
                                                var indx2 = x.indexOf("saldo=")+6
                                                x = x.substring(indx2)
                                                indx2 = x.indexOf(",");
                                                x = x.substring(0,indx2)

                                                var nSaldo = (x.toDouble()-units).roundToInt()

                                                if(nSaldo>=0)
                                                {
                                                    docRef2.update("saldo",nSaldo)

                                                    lfile.createNewFile()

                                                    val lfilewriter = FileWriter(lfile)
                                                    val lout = BufferedWriter(lfilewriter)
                                                    lout.write("EMPTY"+";"+"EMPTY")
                                                    lout.close()
                                                }
                                                else
                                                {
                                                    Toast.makeText(context,"No suficiente Saldo", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    })
                                }
                                else
                                {
                                    Toast.makeText(context,"NO EXISTES", Toast.LENGTH_SHORT).show();
                                }


                            }
                            else
                            {
                                Toast.makeText(context,"No existe el parqueadero: " + id, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context,"No esta registrado en ningun parqueadero: ", Toast.LENGTH_SHORT).show();
                }
            }


        }

        btncerrar.setOnClickListener{
            val context = context as MainActivity
            val sharedPref: SharedPreferences? = activity?.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            val editor = sharedPref?.edit()
            if (editor != null) {
                editor.putBoolean(PREF_NAME, true)
                editor.apply()
            }
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            context.finish()
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
