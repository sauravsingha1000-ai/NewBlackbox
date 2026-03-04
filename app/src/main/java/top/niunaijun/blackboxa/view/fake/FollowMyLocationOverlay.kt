package top.niunaijun.blackboxa.view.fake

import android.content.Context
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class FollowMyLocationOverlay(context: Context, mapView: MapView)
    : MyLocationNewOverlay(mapView) {

    override fun onResume() { enableFollowLocation(); super.onResume() }
    override fun onPause() { disableFollowLocation(); super.onPause() }
}
