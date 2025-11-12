package com.example.agricom_it.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.modules.MapTileApproximater;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private Button btnAddArea, btnSaveArea, btnRecenter;
    private boolean addingArea = false;
    private final List<GeoPoint> areaPoints = new ArrayList<>();
    private final List<Marker> tempMarkers = new ArrayList<>();
//    private MapApiServices apiService;

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

//        apiService = RetrofitClient.getInstance().create(ApiService.class);

        btnAddArea = view.findViewById(R.id.btn_add_area);
        btnSaveArea = view.findViewById(R.id.btn_save_area);
        btnRecenter = view.findViewById(R.id.btn_recenter_map);

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        setupMyLocationOverlay();
        setupMapClickListener();
        setupButtons();
    }

    private void setupButtons() {
        btnAddArea.setOnClickListener(v -> {
            addingArea = true;
            areaPoints.clear();
            clearTempMarkers();
            Toast.makeText(requireContext(), "Tap map to mark area vertices", Toast.LENGTH_SHORT).show();
        });

        btnSaveArea.setOnClickListener(v -> {
            if (addingArea && areaPoints.size() > 2) {
                addPolygon(areaPoints);
                clearTempMarkers();
                Toast.makeText(requireContext(), "Area saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Add at least 3 points for an area", Toast.LENGTH_SHORT).show();
            }
            addingArea = false;
            areaPoints.clear();
        });

        btnRecenter.setOnClickListener(view -> {
            recenterMap();
        });
    }

    private void recenterMap(){
        GeoPoint myLocation = myLocationOverlay.getMyLocation();
        if (myLocation != null) {
            IMapController mapController = mapView.getController();
            mapController.setZoom(17.0);
            mapController.setCenter(myLocation);
        }
    }

    private void setupMapClickListener() {
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if (addingArea) {
                    Marker marker = addMarker(p, "Area Point " + (areaPoints.size() + 1));
                    areaPoints.add(p);
                    tempMarkers.add(marker);
                } else {
                    showAddCommentDialog(p);
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

    private void showAddCommentDialog(GeoPoint point) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Add Comment");

        final EditText input = new EditText(requireContext());
        input.setHint("Enter your comment...");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String comment = input.getText().toString().trim();
            if (!comment.isEmpty()) {
                addMarker(point, comment);
            } else {
                Toast.makeText(requireContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private Marker addMarker(GeoPoint point, String title) {
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        marker.setSnippet("Lat: " + point.getLatitude() + ", Lon: " + point.getLongitude());
        mapView.getOverlays().add(marker);
        mapView.invalidate();
        return marker;
    }

    private void clearTempMarkers() {
        for (Marker m : tempMarkers) {
            mapView.getOverlays().remove(m);
        }
        tempMarkers.clear();
        mapView.invalidate();
    }

    private void addPolygon(List<GeoPoint> points) {
        Polygon polygon = new Polygon();
        polygon.setPoints(points);
        polygon.getFillPaint().setColor(0x40FF46A2);
        polygon.getOutlinePaint().setColor(0x404F0048);
        polygon.getOutlinePaint().setStrokeWidth(5f);
        mapView.getOverlays().add(polygon);
        mapView.invalidate();
    }

    private void setupMyLocationOverlay() {
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()), mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        mapView.getOverlays().add(myLocationOverlay);

        myLocationOverlay.runOnFirstFix(() -> {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    GeoPoint myLocation = myLocationOverlay.getMyLocation();
                    if (myLocation != null) {
                        IMapController mapController = mapView.getController();
                        mapController.setZoom(17.0);
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
