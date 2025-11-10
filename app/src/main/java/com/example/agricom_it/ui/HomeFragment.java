package com.example.agricom_it.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.agricom_it.R;
import com.example.agricom_it.databinding.FragmentHomeBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.views.overlay.MapEventsOverlay;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private Button btnAddPoint, btnAddPolygon, btnSave;
    private boolean addingPoints = false;
    private boolean addingPolygon = false;
    private final List<GeoPoint> tempPoints = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Configuration.getInstance().load(requireContext(),
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()));

        mapView = binding.map;
        mapView.setMultiTouchControls(true);

        btnAddPoint = view.findViewById(R.id.btn_add_point);
        btnAddPolygon = view.findViewById(R.id.btn_add_polygon);
        btnSave = view.findViewById(R.id.btn_save);

        setupMyLocationOverlay();
        setupButtons();

        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if (addingPoints || addingPolygon) {
                    addMarker(p);
                    tempPoints.add(p);
                }
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        mapView.getOverlays().add(new MapEventsOverlay(mReceive));
    }

    private void setupButtons() {
        btnAddPoint.setOnClickListener(v -> {
            addingPoints = true;
            addingPolygon = false;
            tempPoints.clear();
            Toast.makeText(requireContext(), "Tap on map to add points", Toast.LENGTH_SHORT).show();
        });

        btnAddPolygon.setOnClickListener(v -> {
            addingPolygon = true;
            addingPoints = false;
            tempPoints.clear();
            Toast.makeText(requireContext(), "Tap on map to add polygon vertices", Toast.LENGTH_SHORT).show();
        });

        btnSave.setOnClickListener(v -> {
            if (addingPolygon && tempPoints.size() > 2) {
                addPolygon(tempPoints);
                Toast.makeText(requireContext(), "Polygon added!", Toast.LENGTH_SHORT).show();
            } else if (addingPoints && !tempPoints.isEmpty()) {
                Toast.makeText(requireContext(), "Points saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "No spatial data to save.", Toast.LENGTH_SHORT).show();
            }

            addingPoints = false;
            addingPolygon = false;
            tempPoints.clear();
        });
    }

    private void addMarker(GeoPoint point) {
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("User Point");
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }

    private void addPolygon(List<GeoPoint> points) {
        Polygon polygon = new Polygon();
        polygon.setPoints(points);
        polygon.setFillColor(0x401970A9); // semi-transparent
        polygon.setStrokeColor(0xFF1970A9);
        polygon.setStrokeWidth(5f);
        polygon.setTitle("User Polygon");
        mapView.getOverlays().add(polygon);
        mapView.invalidate();
    }

    private void setupMyLocationOverlay() {
        myLocationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(requireContext()), mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        mapView.getOverlays().add(myLocationOverlay);

        myLocationOverlay.runOnFirstFix(() -> {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    GeoPoint myLocation = myLocationOverlay.getMyLocation();
                    if (myLocation != null) {
                        IMapController mapController = mapView.getController();
                        mapController.setZoom(17.5);
                        mapController.setCenter(myLocation);
                    }
                });
            }
        });
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(requireContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, REQUEST_PERMISSIONS_REQUEST_CODE);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupMyLocationOverlay();
            } else {
                Toast.makeText(requireContext(), "Map requires location permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
