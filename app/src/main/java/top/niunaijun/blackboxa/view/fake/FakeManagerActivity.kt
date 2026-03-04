package top.niunaijun.blackboxa.view.fake

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.databinding.ActivityOsmdroidBinding
import top.niunaijun.blackboxa.view.base.BaseActivity

class FakeManagerActivity : BaseActivity<ActivityOsmdroidBinding>() {
    private val viewModel: FakeLocationViewModel by viewModels {
        FakeLocationFactory(application)
    }

    override fun getViewBinding() = ActivityOsmdroidBinding.inflate(layoutInflater)

    override fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.fake_location)

        Configuration.getInstance().userAgentValue = packageName
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setMultiTouchControls(true)

        val mapController = binding.mapView.controller
        mapController.setZoom(15.0)
        mapController.setCenter(GeoPoint(37.7749, -122.4194)) // Default: San Francisco

        binding.mapView.setOnClickListener { view ->
            // On map click, set fake location
        }
    }

    override fun initData() {
        viewModel.loadLocations()
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
    override fun onResume() { super.onResume(); binding.mapView.onResume() }
    override fun onPause() { super.onPause(); binding.mapView.onPause() }
}
