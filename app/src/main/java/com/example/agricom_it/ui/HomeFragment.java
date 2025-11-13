package com.example.agricom_it.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.agricom_it.R;
import com.example.agricom_it.api.ApiClient;
import com.example.agricom_it.api.AuthApiService;
import com.example.agricom_it.databinding.FragmentHomeBinding;
import com.example.agricom_it.model.MapArea;
import com.example.agricom_it.model.MapComment;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private static final String TAG = "HomeFragment";

    private int userID = -1;
    private int mapID = -1;

    private Button btnAddArea, btnSaveArea, btnRecenterMap;
    private boolean addingArea = false;

    private final List<GeoPoint> areaPoints = new ArrayList<>();
    private final List<Marker> tempMarkers = new ArrayList<>();

    private final AuthApiService apiService = ApiClient.getService();

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

        if (getArguments() != null && getArguments().containsKey("userID")) {
            userID = getArguments().getInt("userID", -1);
        }

        Configuration.getInstance().load(requireContext(),
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()));

        mapView = binding.map;
        mapView.setMultiTouchControls(true);

        btnAddArea = view.findViewById(R.id.btn_add_area);
        btnSaveArea = view.findViewById(R.id.btn_save_area);
        btnRecenterMap = view.findViewById(R.id.btn_recenter_map);

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        setupMyLocationOverlay();
        setupMapClickListener();
        setupButtons();

        getMapIDFromServer();
    }

    private void getMapIDFromServer() {
        Call<ResponseBody> call = apiService.GetMapID("GetMapId", userID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Map ID fetched successfully");
                    // You could parse JSON if backend sends { "mapID": 123 }
                    // mapID = parsed value;
                } else {
                    Log.e(TAG, "Failed to fetch map ID: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "Error fetching map ID: " + t.getMessage());
            }
        });
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
                saveAreaToServer(areaPoints);
                clearTempMarkers();
                addingArea = false;
                areaPoints.clear();
                Toast.makeText(requireContext(), "Area saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Add at least 3 points for an area", Toast.LENGTH_SHORT).show();
            }
        });
        btnRecenterMap.setOnClickListener(view -> {
            GeoPoint myLocation = myLocationOverlay.getMyLocation();
            if (myLocation != null) {
                IMapController mapController = mapView.getController();
                mapController.setZoom(17.0);
                mapController.setCenter(myLocation);
            }
        });
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
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Comment");

        final EditText input = new EditText(requireContext());
        input.setHint("Enter your comment...");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String commentText = input.getText().toString().trim();
            if (!commentText.isEmpty()) {
                addMarker(point, commentText);
                saveCommentToServer(point, commentText);
            } else {
                Toast.makeText(requireContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveCommentToServer(GeoPoint point, String comment) {
        MapComment mapComment = new MapComment(userID, mapID, point.getLatitude(), point.getLongitude(), comment);
        Call<ResponseBody> call = apiService.SaveMapComment("SaveMapComment", mapComment);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Comment saved to server", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to save comment (server)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Server error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAreaToServer(List<GeoPoint> points) {
        StringBuilder coords = new StringBuilder();
        for (GeoPoint p : points) {
            coords.append(p.getLatitude()).append(",").append(p.getLongitude()).append(";");
        }

        MapArea area = new MapArea(userID, mapID, coords.toString());
        Call<ResponseBody> call = apiService.SaveMapArea("SaveMapArea", area);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Area saved to server", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to save area", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Server error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Marker addMarker(GeoPoint point, String title) {
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
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
        polygon.getFillPaint().setColor(0x401970A9);
        polygon.getOutlinePaint().setColor(0xFF1970A9);
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