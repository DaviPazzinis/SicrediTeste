package com.example.sicreditest.View

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.bumptech.glide.Glide
import com.example.sicreditest.Controller.Common.Common
import com.example.sicreditest.Controller.Interface.RetrofitService
import com.example.sicreditest.Controller.Common.DefaultResponse
import com.example.sicreditest.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.checkin_dialog.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class DetailActivity : AppCompatActivity(){

    //Retrofit init
    lateinit var mService : RetrofitService

    // GoogleMaps variables
    lateinit var mapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap

    // Event variables
    var lat: Double? = null
    var long: Double? = null
    var title: String? = null
    var description: String? = null
    var image: String? = null
    var date: Long? = null
    var price: Double? = null
    lateinit var eventId: String

    // Floating Buttons
    lateinit var shareButton : FloatingActionButton
    lateinit var checkinButton : FloatingActionButton

    // SimpleDateFormat variables
    @SuppressLint("SimpleDateFormat")
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm ")

    @SuppressLint("SimpleDateFormat", "SetTextI18n", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)

        // Calls GoogleMaps Fragment
        settingGoogleMapsFragment()

        //Init Retrofit
        mService = Common.retrofitService

        // Buttons Find View
        shareButton = findViewById(R.id.button_detail_shareButton)
        checkinButton = findViewById(R.id.button_detail_checkInButton)

        val nestedScrollView: NestedScrollView = findViewById(R.id.nestedScrollView)
        val transparent_image: ImageView = findViewById(R.id.transparent_image)


        // Funtion that blocks NestedScrollView for Google Maps
        transparent_image.setOnTouchListener(OnTouchListener { v, event ->
            val action = event.action
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    // Disallow ScrollView to intercept touch events.
                    nestedScrollView.requestDisallowInterceptTouchEvent(true)
                    // Disable touch on transparent view
                    false
                }
                MotionEvent.ACTION_UP -> {
                    // Allow ScrollView to intercept touch events.
                    nestedScrollView.requestDisallowInterceptTouchEvent(false)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    nestedScrollView.requestDisallowInterceptTouchEvent(true)
                    false
                }
                else -> true
            }
        })

        // Getting info from MainActivity
        putExtrasInfo()

        // Formating date into new variable
        val dateFormated = format.format(date)

        //Setting All content into Activity/TextViews/ImageViews
        settingInfoIntoActivity(dateFormated)

        // Share Button Listener Calling Share Action Function
        shareButton.setOnClickListener(View.OnClickListener {
            shareAction(dateFormated)
        })

        // CheckIn Button Listener Calling CheckIn Action Function
        checkinButton.setOnClickListener(View.OnClickListener {
            generateDialog()
        })


    }

    fun putExtrasInfo(){
        intent?.let {
            lat = it.extras?.getDouble("Lat")
            long = it.extras?.getDouble("Long")
            description = it.extras?.getString("description")
            image = it.extras?.getString("image")
            date = (it.extras?.getLong("date"))
            title = it.extras?.getString("title")
            price = it.extras?.getDouble("price")
            eventId = it.extras?.getString("eventId").toString()
        }
    }

    fun settingInfoIntoActivity(dateFormated:String){

        // Setting info into TextViews
        textView_detail_date.text = dateFormated
        textView_Detail_Description.text = description
        textView_detail_price.text = "R$ "+price.toString()
        textView_detail_coordenates.text = "Lat: "+lat+" - Long: "+long

        // Setting Title into ActionBar
        getSupportActionBar()?.setTitle(title);

        // Setting Image into ImageView
        Glide.with(this)
            .load(image)
            .error(R.drawable.img_error_loading)
            .into(imageView_detail_eventImage)
    }

    fun generateDialog(){

        //Inflates CheckIn Dialog
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.checkin_dialog, null)

        //Builds CheckIn Dialog
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("CheckIn")

        //Shows CheckIn Dialog
        val mAlertDialog = mBuilder.show()

        // Clear Focus from first TextView
        mDialogView.checkin_textView_name.clearFocus();

        // Check In Button that send a POST
        mDialogView.button_checkinButton.setOnClickListener(View.OnClickListener {


            //CheckIn Information needed
            val eventId: String = eventId
            val name: String = mDialogView.checkin_textView_name.text.toString()
            val email: String = mDialogView.checkin_textView_email.text.toString()


            //Calls the func that POSTS the CheckIn
            if (eventId.isEmpty()){
                Toast.makeText(applicationContext, "Event ID ERROR", Toast.LENGTH_SHORT).show()
            }else if (name.isEmpty()){
                Toast.makeText(applicationContext, "Campo NOME está em vazio", Toast.LENGTH_SHORT).show()
            }else if (name.length < 3){
                Toast.makeText(applicationContext, "Por favor, insira um nome válido", Toast.LENGTH_SHORT).show()
            }else if (name.length > 50){
                Toast.makeText(applicationContext, "Você ultrapassou o numero máximo de caracteres! (50).", Toast.LENGTH_SHORT).show()
            }else if(email.isEmpty()){
                Toast.makeText(applicationContext, "Campo EMAIL está vazio.", Toast.LENGTH_SHORT).show()
            }else if (isValidEmail(email) == false){
                Toast.makeText(applicationContext, "Por favor, insira um email válido", Toast.LENGTH_SHORT).show()
            }else if(!isOnline()){
                Common.noConection(applicationContext)
                this.finish()
            }else {
                //Calls CheckIn Actions that POSTS
                checkInAction(eventId, name, email)

                Toast.makeText(applicationContext, "CheckIn realizado com sucesso! "+name, Toast.LENGTH_SHORT).show()

                //Exit CheckIn Dialog
                mAlertDialog.dismiss()
            }



        })

        //Button that exit the Dialog
        mDialogView.button_cancelButton.setOnClickListener(View.OnClickListener {
            mAlertDialog.dismiss()
        })

    }

    fun checkInAction(eventId: String, name: String, email: String){
        mService.checkIn(eventId, name, email).enqueue(object: Callback<DefaultResponse>{
            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                println("ERRO  ========================================  " + t.message)
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                println("FOI ======================================== " + response.body())
            }

        })
    }

    fun settingGoogleMapsFragment(){
        mapFragment = map as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it
            val location = LatLng(lat!!, long!!)
            googleMap.addMarker(MarkerOptions().position(location).title(title))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))




        }

    }

    fun shareAction(dateFormated:String){
        val myIntent = Intent(Intent.ACTION_SEND)
        myIntent.type = "type/palin"

        val shareSub = title
        val shareBody = description+
                "\n"+
                "\n- O Evento acontecerá na data: "+dateFormated

        myIntent.putExtra(Intent.EXTRA_SUBJECT , shareSub)
        myIntent.putExtra(Intent.EXTRA_TEXT , shareBody)
        startActivity(Intent.createChooser(myIntent, "Sicredi Test"))
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    fun isOnline(): Boolean {
        val cm =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!isOnline()){
            Common.noConection(applicationContext)
            this.finish()
        }
    }

    override fun onStart() {
        super.onStart()
        if (!isOnline()){
            Common.noConection(applicationContext)
            this.finish()
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isOnline()){
            Common.noConection(applicationContext)
            this.finish()
        }
    }

    override fun onPause() {
        super.onPause()
        if (!isOnline()){
            Common.noConection(applicationContext)
            this.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isOnline()){
            Common.noConection(applicationContext)
            this.finish()
        }
    }


}