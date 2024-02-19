package com.tayadehritik.busapp.home

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.gms.tasks.Task
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.maps.android.PolyUtil
import com.tayadehritik.busapp.MainActivity
import com.tayadehritik.busapp.R
import com.tayadehritik.busapp.adapters.RoutesAdapter
import com.tayadehritik.busapp.databinding.ActivityHomeBinding
import com.tayadehritik.busapp.models.Route
import com.tayadehritik.busapp.models.User
import com.tayadehritik.busapp.models.Users
import com.tayadehritik.busapp.network.RouteNetwork
import com.tayadehritik.busapp.network.UserNetwork
import com.tayadehritik.busapp.service.MyNavigationService
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.converter
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.serialization.deserialize
import io.ktor.websocket.Frame
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class Home : AppCompatActivity(), OnMapReadyCallback {

    private var map: GoogleMap? = null
    private var lastKnownLocation: Location? = null

    private val COLOR_BLACK_ARGB = -0x1000000
    private val POLYLINE_STROKE_WIDTH_PX = 12

    val arrayList = ArrayList<Route>()
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityHomeBinding

    var locationSharingJob: Job? = null
    var locationFetchingJob1: Job? = null
    var locationFetchingJob2: Job? = null

    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var userNetwork:UserNetwork? = null
    private var locationPermissionGranted = false

    lateinit var requestPermissionLauncher:ActivityResultLauncher<String>
    lateinit var requestPermissionMultipleLauncher:ActivityResultLauncher<Array<String>>

    public lateinit var homeSearchBar: SearchBar
    public lateinit var homeSearchView: SearchView
    public lateinit var homeActivity: Home

    private lateinit var currentSelectedRoute: Route

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)


        /*requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted ->
            if(isGranted)
            {
                //startSharingLocation
                println("start sharing location")
                startSharingLocation("100")
            }
            else
            {
                //tell user why you need it
                println("user denied fine location permission, you need to provide that permission for app to work properly")
            }

        }*/

        requestPermissionMultipleLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if(it.values.reduce { acc, b -> acc && b  })
            {
                startSharingLocation("100")
            }
            else
            {
                println("user denied some location")
            }

        }



        binding = ActivityHomeBinding.inflate(layoutInflater)


        homeSearchView = binding.homeSearchView
        homeSearchBar = binding.searchBar
        homeActivity = this

        auth = Firebase.auth
        userNetwork = UserNetwork(auth.currentUser!!.uid)

        val routeNetwork:RouteNetwork = RouteNetwork(auth.currentUser!!.uid)


        lifecycleScope.launch {


            val routes = routeNetwork.allRoutes()
            val adapter = RoutesAdapter(routes,homeActivity)
            println(routes)
            val recyclerView = binding.recyclerView
            recyclerView.adapter = adapter


            /*homeSearchView
                .getEditText()
                .setOnEditorActionListener { v, actionId, event ->
                    homeSearchBar.setText(homeSearchView.getText())
                    homeSearchView.hide()
                    false
                }*/

            homeSearchView.editText.addTextChangedListener {
                adapter.filter.filter(it)
            }

        }


        binding.extendedFab.setOnClickListener {

            //get location permission
            //check if you already have location permission
            when {

                (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) -> {
                    //already have the permission
                    println("already have both notification and location permission")
                    startSharingLocation("100")
                }
                else -> {
                    //requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

                    requestPermissionMultipleLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS,Manifest.permission.ACCESS_FINE_LOCATION))
                }
            }


        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)




        val view = binding.root
        setContentView(view)



    }


    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap

        val polyline1 = googleMap.addPolyline(
            PolylineOptions()
                .startCap(RoundCap())
                .endCap(RoundCap())
                .color(COLOR_BLACK_ARGB)
                .width(POLYLINE_STROKE_WIDTH_PX.toFloat())
                .clickable(true)
                .addAll(
                    PolyUtil.decode("ao{pB}rhaM\\`@nAmARQJL@BFHBBJJSPs@j@eCzB_CtBUTKHa@`@}BnBqApAgAfAEBq@n@o@l@a@\\{BtB[VML?B@D?B?D?B?BAD?BADAFCFCFEDGDGBG@E@A?S@I?IAAAkBfB[\\YXa@Zm@h@kCvBeDrC_@TG@_FpEmGzFwOrNYVWTuAjAi@f@OLm@h@sAnAYZW\\A@MXMXOp@Ot@Ot@Kf@Ib@?BMl@Ox@I^AFOr@Ot@M`AGd@G`@APGf@AHGl@W~BEZKp@WtB]vCq@nFMdAYfCo@bFOpACNOtAo@nFaAfIaAlIUhBOrACLWvBUdCUnDc@jHQfCSdDANI|AGnACbA?b@B^?Z@JFbADdAJvA@HBn@\\bEDl@D^LbAHl@Lt@Fh@H~@HNBP@l@@P?\\Cp@Cb@E`@Kn@GVGXCH?@Qp@Sl@Of@ABIRWh@OXEHU\\IN]`@EF[ZUTA@SPA@k@`@KHi@^]P_@Ti@\\IFE@QLs@b@q@`@CB}@j@MHGDA@MHq@b@q@b@UN[Ri@\\QJA@WLA?WNOB_@`@c@b@uA~AMRKPOb@O`@CHa@~A}BbJy@tCm@bCk@dCGX{@rDQp@_@~AGT[jAm@~BOf@CHKRS\\g@j@g@l@STKJ}AxBeArAmBdCSVKPA@oDrEEDeDfEcAtAg@p@u@`As@~@}H|Ju@dAW\\uCtD{BtC?@oDrEa@f@SXINOTm@vAmBvEIVGXKj@El@Cl@EtAANIpDGtBAv@Ad@C`AGtAAHCl@Ep@GbAI~@Ev@APARADI`AE^Gd@EZIt@If@E\\QnAKn@Mx@Oz@M~@g@rDG\\m@rDg@rCy@xEMx@WdBEV[zBOxAMj@K`@GZc@|BENKZc@tBUnAKb@GLGPS^UZGFUVWXGHc@`@?@A^AP@B?B?@@@@@@@BBvAOVCNCh@EFAJ?LAH?J@H?PBd@JLBf@F~@Pn@N\\HNFPD\\HNHRHJDJDh@NRDTDPDr@Hb@FlAPtBPL@L@dALt@JTD^DV@P?NAHCJEp@YHEDCNGb@O\\Id@El@E@?b@ET?N?XBJBPDZFdAT@@j@J`@H~Bb@r@LB?\\JRBd@FN?FAJAHCHCHEJGRK\\Q`@Q^OREVCTAv@?\\?\\@B?p@@bAA|@EfAEF?rAGhAAx@?~@?z@?bBHlBPF?P@R@J@V?b@Bl@AXCh@A`@?H?\\BT?R@F?V@^Dl@DXCXMj@_@NOVU@?RMJENE\\CN?bADH@rA@dA??E")
                ))
        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(18.6598469, 73.77727039999999), 20f))
        //updateLocationUI()

        //updateLocationOfBusOnMap("100");

    }





    fun updateLocationOfBusOnMap(route:Route) {

        map!!.clear()

        currentSelectedRoute = route

        println("here in update")
        homeSearchView.setText("${route.bus} ${route.route}")
        homeSearchBar.setText("${route.bus} ${route.route}")
        homeSearchView.hide()


        var connection:DefaultClientWebSocketSession? = null

        locationFetchingJob1?.cancel()
        locationFetchingJob2?.cancel()

        locationFetchingJob1 = lifecycleScope.launch {
            try {
                var mapOfUsersAndMarkers = mutableMapOf<String,Marker>()

                connection =  userNetwork!!.openGetUsersTravellingOnBusConnection()
                connection!!.incoming.consumeEach {



                    val users = connection!!.converter!!.deserialize<Users>(it)

                    for(user in users.users)
                    {

                        if(mapOfUsersAndMarkers[user.user_id] == null)
                        {
                            val marker = map!!.addMarker(MarkerOptions().position(LatLng(user.lat, user.long)).title("Bus"))
                            mapOfUsersAndMarkers.put(user.user_id, marker!!)
                        }
                        else
                        {
                            val marker = mapOfUsersAndMarkers[user.user_id]
                            marker!!.position = LatLng(user.lat, user.long)
                        }


                    }

                    var userIdList = mutableListOf<String>()
                    users.users.forEach {
                        userIdList.add(it.user_id)
                    }

                    val filteredMapOfUsersAndMarkers = mapOfUsersAndMarkers.filter{entry ->
                        entry.key !in userIdList
                    } as MutableMap<String, Marker>

                    filteredMapOfUsersAndMarkers.forEach { user_id, marker ->
                        marker.remove()
                        mapOfUsersAndMarkers.remove(user_id)
                    }

                }
            }
            finally {

                //send stop to server
                userNetwork!!.stopGetUsersTravellingOnBusConnection()
            }



        }

        locationFetchingJob2 = lifecycleScope.launch {
            while(true) {
                println("here")
                if(connection != null)
                {
                    connection!!.sendSerialized(route)
                }

                delay(1000)
            }
        }

    }

    fun startSharingLocation(bus:String) {

        if(!this::currentSelectedRoute.isInitialized)
        {
            Toast.makeText(this,"Please select a route you are travelling on",Toast.LENGTH_SHORT).show()
            return
        }



        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000)
            .build()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client:SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            //do location requests here
            println("do location requests here")
            val intent = Intent(this,MyNavigationService::class.java).apply {
                action = "start"
                putExtra("bus",currentSelectedRoute.bus)
                putExtra("route", currentSelectedRoute.route)
            }
            val stopintent = Intent(this,MyNavigationService::class.java).apply {
                action = "stop"
                putExtra("bus",currentSelectedRoute.bus)
                putExtra("route", currentSelectedRoute.route)
            }

            stopService(intent)
            this.startForegroundService(intent)

        }

        task.addOnFailureListener {exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(this@Home,
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }

        }

    }


    companion object{
        val REQUEST_CHECK_SETTINGS = 1
    }

}