package com.example.parkinglot.ui.parkinglots

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.parkinglot.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class ParkinglotsFragment : Fragment() {

    private lateinit var parkinglotsViewModel: ParkinglotsViewModel
    private lateinit var btnPrueba: Button
    private lateinit var btnPrueba2: Button

    private val a = 5
    private val b = 10
    private var lv: ListView? = null
    private var customeAdapter: CustomAdapter? = null
    private var imageModelArrayList: ArrayList<ImageModel>? = null
    private val myImageList = intArrayOf(R.mipmap.ic_park1_foreground, R.mipmap.ic_park2_foreground, R.mipmap.ic_park3_foreground, R.mipmap.ic_park4_foreground, R.mipmap.ic_park5_foreground)
    private val myImageNameList = arrayOf("Park Center", "Parqueadero El Puente", "Parqueadero Lastín", "Parqueadero Titán Plaza", "Parqueadero Bonanza")
    private val myImagePreciosList = arrayOf("\$6.000/hora", "\$5.200/hora", "\$3.500/hora", "\$10.000/hora", "\$9.000/hora")
    private val myImageHorariosList = arrayOf("10:00 AM - 11:00 PM", "11:00 AM - 10:00 PM", "8:00 AM - 8:00 PM", "6:00 AM - 8:00 PM", "8:00 AM - 5:00 PM")


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
        btnPrueba = root.findViewById(R.id.button2)
        btnPrueba2 = root.findViewById(R.id.button3)


        btnPrueba.visibility =  View.GONE
        btnPrueba2.visibility =  View.GONE


        val context = context as MainActivity
        btnPrueba2.setOnClickListener{
        val dbHandler = ParqueaderoOpenHelper(context, null)
        val num = ((1..12).shuffled().first()).toString()
        val nombre = "Parqueadero" + num
        val imagen = "Imagen" + num
        val precio = "Precio" + num
        val horario = "Horario" + num
        val user = Parqueadero(nombre,precio,horario,imagen)
        val valores = "Nombre:"+nombre+",imagen:"+imagen+",precio:"+precio+",horario:"+horario
        dbHandler.addName(user)
            Toast.makeText(getActivity(),"Se agrego el parqueadero con valores: " + valores, Toast.LENGTH_SHORT).show()
        }

        btnPrueba.setOnClickListener{
            val texto = StringBuilder()
            val dbHandler = ParqueaderoOpenHelper(context, null)
            val cursor = dbHandler.getAllName()
            cursor!!.moveToFirst()
            texto.append((cursor.getString(cursor.getColumnIndex(ParqueaderoOpenHelper.COLUMN_NAME))))
            while (cursor.moveToNext()) {
                texto.append((cursor.getString(cursor.getColumnIndex(ParqueaderoOpenHelper.COLUMN_NAME))))
                texto.append("\n")
            }
            cursor.close()
            Toast.makeText(getActivity(),texto, Toast.LENGTH_SHORT).show()
        }

        lv = root.findViewById(R.id.listview) as ListView
        imageModelArrayList = populateList()
        Log.d("hjhjh", imageModelArrayList!!.size.toString() + "")



        //val adapter = ArrayAdapter(context,  R.layout.simple_list_item_1 , arrayList)
        //lv.adapter = adapter


        return root
    }

    private fun populateList(): ArrayList<ImageModel> {

        val list = ArrayList<ImageModel>()


        var imagenes: MutableList<Int> = ArrayList()
        var nombres:MutableList<String> = ArrayList()
        var precios:MutableList<String> = ArrayList()
        var horarios:MutableList<String> = ArrayList()

        val db = FirebaseFirestore.getInstance()
        // [END get_firestore_instance]
        // [START set_firestore_settings]
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        db.firestoreSettings = settings
        // [END set_firestore_settings]
        // TODO: handle loggedInUser authentication
        db.collection("malls")
            .get()
            .addOnSuccessListener { documents ->
                if(documents.isEmpty){
                    Log.w(ContentValues.TAG, "Error obteniendo parqueaderos de Firebase ")
                }
                else{
                    for (document in documents) {
                        val park: ParqueaderoFirebase = document.toObject(ParqueaderoFirebase::class.java)
                        if(park.name!=null){
                            nombres.add(park.name.toString())
                        }
                        else{
                            nombres.add("No nombre")
                        }
                        imagenes.add(R.mipmap.ic_park_a_foreground)
                        if(park.address!=null){
                            precios.add(park.address.toString())
                        }
                        else{
                            nombres.add("No dirección")
                        }
                        horarios.add("horario")
                        //myImagePreciosList.add(park.name.toString())
                        //myImageHorariosList.add(park.name.toString())
                    }

                    for (i in 0 until nombres.size-1) {
                        val imageModel = ImageModel()
                        imageModel.setNames(nombres[i])
                        imageModel.setImage_drawables(imagenes[i])
                        imageModel.setHorarios(horarios[i])
                        imageModel.setPrecios(precios[i])
                        list.add(imageModel)
                    }

                    val context = context as MainActivity
                    customeAdapter = CustomAdapter(context, list!!)
                    lv!!.adapter = customeAdapter

                }

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }

        return list
    }


}
