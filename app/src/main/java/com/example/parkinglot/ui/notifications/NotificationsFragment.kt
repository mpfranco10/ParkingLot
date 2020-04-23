package com.example.parkinglot.ui.notifications

import android.content.ContentValues.TAG
import android.content.Context
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings


class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel

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
        val t:TextView= root.findViewById(R.id.text_notifications2)
        val tum:TextView= root.findViewById(R.id.text_edad2)
        val ted:TextView= root.findViewById(R.id.text_edad)

        val tp:TextView= root.findViewById(R.id.textPark)

        val btnPrueba: Button = root.findViewById(R.id.boton_park)
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
                //dbHandler.addName(user)
            }
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
