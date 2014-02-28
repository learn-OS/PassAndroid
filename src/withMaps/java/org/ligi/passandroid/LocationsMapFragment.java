package org.ligi.passandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.ligi.passandroid.model.PassLocation;
import org.ligi.passandroid.ui.TicketViewActivityBase;

import java.util.List;

public class LocationsMapFragment extends SupportMapFragment {
    private TicketViewActivityBase base_activity;
    public boolean click_to_fullscreen = false;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        base_activity = (TicketViewActivityBase) getActivity();

        if (!(getActivity() instanceof TicketViewActivityBase)) {
            throw new IllegalArgumentException("LocationsMapFragment must be used inside a TicketViewActivityBase");
        }

        final MapInitRunnable mapInitRunnable = new MapInitRunnable();

        handler = new Handler();
        handler.postDelayed(mapInitRunnable, 42);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private class MapInitRunnable implements Runnable {

        @Override
        public void run() {
            final GoogleMap map = getMap();
            if (map == null) {
                // map not yet initialized - retry http://stackoverflow.com/questions/19219255/supportmapfragment-getmap-returns-null
                handler.postDelayed(this, 230);
                return;
            }

            LatLngBounds.Builder boundser = new LatLngBounds.Builder();

            List<PassLocation> locations = base_activity.passbook.getLocations();

            if (locations.size() > 0) {
                for (PassLocation l : locations) {

                    // yea that looks stupid but need to split LatLng free/nonfree - google play services ^^
                    LatLng latLng = new LatLng(l.latlng.lat, l.latlng.lon);
                    map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(l.getDescription())
                            //.icon(BitmapDescriptorFactory.fromBitmap(base_activity.passbook.getIconBitmap())));
                    );

                    boundser = boundser.include(latLng);
                }

                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_VIEW);

                        i.setData(Uri.parse("geo:" + marker.getPosition().latitude + "," + marker.getPosition().longitude + "?q=" + marker.getTitle()));
                        getActivity().startActivity(i);
                    }
                });
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundser.build(), 100));

                // limit zoom-level to 17 - otherwise we could be so zoomed in that it looks buggy
                map.moveCamera(CameraUpdateFactory.zoomTo(Math.min(17f, map.getCameraPosition().zoom)));

                // Remove listener to prevent position reset on camera move.
                map.setOnCameraChangeListener(null);
                if (click_to_fullscreen)
                    map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            Intent i = new Intent(getActivity(), FullscreenMapActivity.class);
                            i.putExtra("path", base_activity.passbook.getPath());
                            getActivity().startActivity(i);
                        }
                    });
            }
        }
    }
}