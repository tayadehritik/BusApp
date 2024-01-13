package com.tayadehritik.busapp.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.material.search.SearchView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.maps.android.PolyUtil
import com.tayadehritik.busapp.R
import com.tayadehritik.busapp.adapters.ListAdapter
import com.tayadehritik.busapp.models.Route
import com.tayadehritik.busapp.network.RouteNetwork
import kotlinx.coroutines.launch

class Home : AppCompatActivity(), OnMapReadyCallback {

    private val COLOR_BLACK_ARGB = -0x1000000
    private val POLYLINE_STROKE_WIDTH_PX = 12

    val arrayList = ArrayList<Route>()
    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val homSearchView:SearchView = findViewById(R.id.homeSearchView)
        auth = Firebase.auth


        val routeNetwork:RouteNetwork = RouteNetwork(auth.currentUser!!.uid)

        lifecycleScope.launch {
            val animalNameList = routeNetwork.allRoutes()
            for(i in animalNameList)
            {
                val route:Route = Route(i.bus,i.route)
                arrayList.add(route)

            }
            val listView:ListView = findViewById(R.id.listview)
            val adapter = ListAdapter(arrayList)
            listView.adapter = adapter
        }




        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

    }
    override fun onMapReady(googleMap: GoogleMap) {

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

    }
}