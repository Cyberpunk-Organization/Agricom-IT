package com.example.agricom_it.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.agricom_it.R;
import com.example.agricom_it.api.ApiClient;
import com.example.agricom_it.api.AuthApiService;
import com.example.agricom_it.databinding.FragmentHomeBinding;
import com.example.agricom_it.model.map.Coordinates;
import com.example.agricom_it.model.map.MapArea;
import com.example.agricom_it.model.map.MapComment;

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

    //--------------------------------------------------------------------------------[onCreateView]
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Intent intent = requireActivity().getIntent();
        if (intent != null && intent.hasExtra("login_id")) {
            userID = intent.getIntExtra("login_id", -1);
        }

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    //-------------------------------------------------------------------------------[onViewCreated]
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

    //--------------------------------------------------------------------------------[setupButtons]
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

    //-----------------------------------------------------------------------[setupMapClickListener]
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

    //------------------------------------------------------------------------[showAddCommentDialog]
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

    //-------------------------------------------------------------------------[saveCommentToServer]
    private void saveCommentToServer(GeoPoint point, String comment) {

        Coordinates coordinates = new Coordinates(point.getLatitude(), point.getLongitude());
        MapComment mapComment = new MapComment(userID, coordinates, comment);

        Call<ResponseBody> call = apiService.AddMapComment("AddMapComment", userID, mapComment.toJsonString());

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

    //----------------------------------------------------------------------------[saveAreaToServer]
    private void saveAreaToServer(List<GeoPoint> points) {
        StringBuilder coords = new StringBuilder();
        for (GeoPoint p : points) {
            coords.append(p.getLatitude()).append(",").append(p.getLongitude()).append(";");
        }

        MapArea area = new MapArea(coords.toString());
        final String areaJson;
        try {
            areaJson = area.toJsonString();
        } catch (org.json.JSONException e) {
            Log.e(TAG, "Failed to serialize area to JSON: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Failed to prepare area data", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<ResponseBody> call = apiService.SaveMapArea("SaveMapArea", userID, areaJson);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String raw = response.body().string();
                        org.json.JSONObject outer = new org.json.JSONObject(raw);

                        boolean success = outer.optBoolean("success", false);
                        if (!success) {
                            final String err = outer.optString("error", "unknown");
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "Server error: " + err, Toast.LENGTH_SHORT).show());
                            return;
                        }

                        Object dataObj = outer.get("data"); // can be a String, JSONArray or JSONObject
                        org.json.JSONArray areasArray = null;

                        if (dataObj instanceof String) {
                            // PHP stored valid JSON in a string -> parse it
                            areasArray = new org.json.JSONArray((String) dataObj);
                        } else if (dataObj instanceof org.json.JSONArray) {
                            areasArray = (org.json.JSONArray) dataObj;
                        } else if (dataObj instanceof org.json.JSONObject) {
                            // e.g. RemoveMapArea returns data { "AreaData": [...], "removed": {...} }
                            org.json.JSONObject dataJson = (org.json.JSONObject) dataObj;
                            areasArray = dataJson.optJSONArray("AreaData");
                        }

                        if (areasArray != null) {
                            // iterate areas and draw them on the map
                            for (int i = 0; i < areasArray.length(); i++) {
                                org.json.JSONObject areaObj = areasArray.optJSONObject(i);
                                String coordsStr = null;

                                if (areaObj != null) {
                                    // Try common field names; adjust to match your MapArea storage shape
                                    if (areaObj.has("coords"))
                                        coordsStr = areaObj.optString("coords");
                                    else if (areaObj.has("coordinates"))
                                        coordsStr = areaObj.optString("coordinates");
                                    else if (areaObj.has("area"))
                                        coordsStr = areaObj.optString("area");
                                    else {
                                        // fallback: maybe the element is directly the coords string
                                        coordsStr = areaObj.toString();
                                    }
                                } else {
                                    // element might be a plain string
                                    coordsStr = areasArray.optString(i);
                                }

                                final List<GeoPoint> polyPoints = parseCoordsString(coordsStr);
                                if (!polyPoints.isEmpty()) {
                                    requireActivity().runOnUiThread(() -> {
                                        addPolygon(polyPoints);
                                    });
                                }
                            }

                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "Area saved and map updated", Toast.LENGTH_SHORT).show());
                        } else {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "Area saved (no area list returned)", Toast.LENGTH_SHORT).show());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Failed parsing save response: " + e.getMessage(), e);
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Failed to save area: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Server error: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    //-----------------------------------------------------------------------------------[addMarker]
    private Marker addMarker(GeoPoint point, String title) {
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        mapView.getOverlays().add(marker);
        mapView.invalidate();
        return marker;
    }

    //----------------------------------------------------------------------------[clearTempMarkers]
    private void clearTempMarkers() {
        for (Marker m : tempMarkers) {
            mapView.getOverlays().remove(m);
        }
        tempMarkers.clear();
        mapView.invalidate();
    }

    //----------------------------------------------------------------------------------[addPolygon]
    private void addPolygon(List<GeoPoint> points) {
        Polygon polygon = new Polygon();
        polygon.setPoints(points);
        polygon.getFillPaint().setColor(0x401970A9);
        polygon.getOutlinePaint().setColor(0xFF1970A9);
        polygon.getOutlinePaint().setStrokeWidth(5f);
        mapView.getOverlays().add(polygon);
        mapView.invalidate();
    }

    //----------------------------------------------------------------------[setupMyLocationOverlay]
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

    //---------------------------------------------------------------[requestPermissionsIfNecessary]
    private void requestPermissionsIfNecessary(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(requireContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, REQUEST_PERMISSIONS_REQUEST_CODE);
                return;
            }
        }
    }

    //------------------------------------------------------------------[onRequestPermissionsResult]
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

    //------------------------------------------------------------------------------------[onResume]
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    //-------------------------------------------------------------------------------------[onPause]
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    //-------------------------------------------------------------------------------[onDestroyView]
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //---------------------------------------------------------------------------[parseCoordsString]
    private List<GeoPoint> parseCoordsString(String s) {
        List<GeoPoint> result = new ArrayList<>();
        if (s == null) return result;
        s = s.trim();
        if (s.isEmpty()) return result;

        // expected format: "lat,lon;lat,lon;..."
        String[] pairs = s.split(";");
        for (String pair : pairs) {
            pair = pair.trim();
            if (pair.isEmpty()) continue;
            String[] parts = pair.split(",");
            if (parts.length < 2) continue;
            try {
                double lat = Double.parseDouble(parts[0].trim());
                double lon = Double.parseDouble(parts[1].trim());
                result.add(new GeoPoint(lat, lon));
            } catch (NumberFormatException ignored) {}
        }
        return result;
    }

    //--------------------------------------------------------------------------[getMapIDFromServer]
    private void getMapIDFromServer() {
        Call<ResponseBody> call = apiService.GetMapID("GetMapID", userID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String raw = response.body().string();
                        org.json.JSONObject json = new org.json.JSONObject(raw);
                        if (json.optBoolean("success", false)) {
                            // extract the integer stored in "data"
                            mapID = json.optInt("data", -1);

                            // Only fetch map data after mapID is available
                            if (mapID != -1) {
                                getMapDataFromServer();
                            } else {
                                Log.e(TAG, "Invalid mapID, skipping GetMapData");
                            }
                        } else {
                            Log.e(TAG, "Server returned success=false: " + raw);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing Map ID: " + e.getMessage(), e);
                    }
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

    //------------------------------------------------------------------------[getMapDataFromServer]
    private void getMapDataFromServer() {
        Call<ResponseBody> call = apiService.GetMapData("GetMapData", mapID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String raw = response.body().string();
                        org.json.JSONObject outer = new org.json.JSONObject(raw);
                        if (!outer.optBoolean("success", false)) {
                            Log.e(TAG, "GetMapData returned success=false: " + raw);
                            return;
                        }

                        Object dataObj = outer.opt("data");
                        org.json.JSONArray itemsArray = null;

                        // If data is an object, normalize and also handle top-level comments
                        if (dataObj instanceof org.json.JSONObject) {
                            org.json.JSONObject dataJson = (org.json.JSONObject) dataObj;

                            String[] commentKeys = new String[]{"comments", "Comments"};
                            for (String ck : commentKeys) {
                                if (dataJson.has(ck)) {
                                    Object commentsObj = dataJson.opt(ck);
                                    org.json.JSONArray commentsArr = null;
                                    if (commentsObj instanceof org.json.JSONArray) {
                                        commentsArr = (org.json.JSONArray) commentsObj;
                                    } else if (commentsObj instanceof String) {
                                        String cstr = ((String) commentsObj).trim();
                                        try {
                                            commentsArr = new org.json.JSONArray(cstr);
                                        } catch (Exception ex) {
                                            try {
                                                commentsArr = new org.json.JSONArray(cstr.replace("\\\"", "\""));
                                            } catch (Exception ignored) {}
                                        }
                                    }
                                    if (commentsArr != null) {
                                        for (int ci = 0; ci < commentsArr.length(); ci++) {
                                            org.json.JSONObject comObj = commentsArr.optJSONObject(ci);
                                            if (comObj != null) {
                                                handleCommentObject(comObj);
                                            } else {
                                                String comStr = commentsArr.optString(ci, null);
                                                if (comStr != null) {
                                                    List<GeoPoint> pts = parseCoordsString(comStr);
                                                    if (!pts.isEmpty()) {
                                                        final GeoPoint p = pts.get(0);
                                                        requireActivity().runOnUiThread(() -> addMarker(p, "Comment"));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (dataJson.has("AreaData")) {
                                Object areaDataObj = dataJson.opt("AreaData");
                                if (areaDataObj instanceof org.json.JSONArray) {
                                    itemsArray = (org.json.JSONArray) areaDataObj;
                                } else if (areaDataObj instanceof String) {
                                    try {
                                        itemsArray = new org.json.JSONArray((String) areaDataObj);
                                    } catch (Exception ex) {
                                        Log.e(TAG, "Unable to parse AreaData string as JSONArray: " + ex.getMessage());
                                    }
                                }
                            } else {
                                // fallback: wrap the object into an array
                                itemsArray = new org.json.JSONArray();
                                itemsArray.put(dataJson);
                            }
                        } else if (dataObj instanceof org.json.JSONArray) {
                            itemsArray = (org.json.JSONArray) dataObj;
                        } else if (dataObj instanceof String) {
                            String dataStr = ((String) dataObj).trim();
                            try {
                                itemsArray = new org.json.JSONArray(dataStr);
                            } catch (Exception ex1) {
                                try {
                                    org.json.JSONObject tmp = new org.json.JSONObject(dataStr);
                                    if (tmp.has("AreaData")) {
                                        Object areaDataObj = tmp.opt("AreaData");
                                        if (areaDataObj instanceof org.json.JSONArray) {
                                            itemsArray = (org.json.JSONArray) areaDataObj;
                                        } else if (areaDataObj instanceof String) {
                                            try {
                                                itemsArray = new org.json.JSONArray((String) areaDataObj);
                                            } catch (Exception ex2) {
                                                Log.e(TAG, "Unable to parse nested AreaData: " + ex2.getMessage());
                                            }
                                        }
                                    } else {
                                        itemsArray = new org.json.JSONArray();
                                        itemsArray.put(tmp);
                                    }
                                } catch (Exception ex3) {
                                    itemsArray = new org.json.JSONArray();
                                    itemsArray.put(dataStr);
                                }
                            }
                        }

                        // Process itemsArray for areas/comments
                        if (itemsArray != null && itemsArray.length() > 0) {
                            for (int i = 0; i < itemsArray.length(); i++) {
                                Object elem = itemsArray.opt(i);
                                String rawElemStr = null;
                                org.json.JSONObject areaObj = null;

                                if (elem instanceof org.json.JSONObject) {
                                    areaObj = (org.json.JSONObject) elem;
                                    rawElemStr = areaObj.toString();
                                } else if (elem instanceof String) {
                                    rawElemStr = (String) elem;
                                } else {
                                    rawElemStr = String.valueOf(elem);
                                }

                                final List<GeoPoint> polyPoints = parseAreaJsonString(rawElemStr);
                                if (!polyPoints.isEmpty()) {
                                    requireActivity().runOnUiThread(() -> addPolygon(polyPoints));
                                }

                                if (areaObj != null) {
                                    boolean looksLikeComment = areaObj.has("commentID") || areaObj.has("comment") || areaObj.has("text");
                                    Object coordsField = areaObj.opt("coordinates");

                                    if (!looksLikeComment && coordsField != null) {
                                        if (coordsField instanceof org.json.JSONObject)
                                            looksLikeComment = true;
                                        if (coordsField instanceof org.json.JSONArray) {
                                            org.json.JSONArray ca = (org.json.JSONArray) coordsField;
                                            if (ca.length() == 2 && !(ca.opt(0) instanceof org.json.JSONArray))
                                                looksLikeComment = true;
                                        }
                                    }
                                    if (looksLikeComment) {
                                        handleCommentObject(areaObj);
                                    }
                                }

                                if (areaObj != null) {
                                    org.json.JSONArray comments = areaObj.optJSONArray("comments");
                                    if (comments == null && areaObj.has("Comments"))
                                        comments = areaObj.optJSONArray("Comments");

                                    if (comments != null) {
                                        for (int c = 0; c < comments.length(); c++) {
                                            org.json.JSONObject com = comments.optJSONObject(c);
                                            if (com == null) continue;
                                            double lat = com.optDouble("latitude", Double.NaN);
                                            double lon = com.optDouble("longitude", Double.NaN);

                                            String text = com.optString("comment", com.optString("text", null));
                                            if (!Double.isNaN(lat) && !Double.isNaN(lon) && text != null) {
                                                final GeoPoint gp = new GeoPoint(lat, lon);
                                                final String title = text;
                                                requireActivity().runOnUiThread(() -> addMarker(gp, title));
                                            }
                                        }
                                    }
                                }
                            }

                            requireActivity().runOnUiThread(() -> {
                                Log.i(TAG, "Map data loaded");
                                Toast.makeText(requireContext(), "Map data loaded", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Log.i(TAG, "No map data available");
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "No map data available", Toast.LENGTH_SHORT).show());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing GetMapData response: " + e.getMessage(), e);
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Log.e(TAG, "Failed to fetch map data: " + response.code());
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Failed to fetch map data: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "GetMapData onFailure: " + t.getMessage(), t);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Server error: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    //-------------------------------------------------------------------------[parseAreaJsonString]
    private List<GeoPoint> parseAreaJsonString(String raw) {
        List<GeoPoint> result = new ArrayList<>();
        if (raw == null) return result;
        String s = raw.trim();
        if (s.isEmpty()) return result;

        if (s.contains(",") && s.contains(";") && !s.toLowerCase().contains("coordinates") && !s.toLowerCase().contains("coords")) {
            return parseCoordsString(s);
        }

        String jsonStr = s.replace('\'', '\"');

        try {
            if (jsonStr.startsWith("[")) {
                org.json.JSONArray arr = new org.json.JSONArray(jsonStr);
                return parseCoordinatesArray(arr);
            }

            org.json.JSONObject obj = new org.json.JSONObject(jsonStr);
            org.json.JSONArray coords = obj.optJSONArray("coordinates");

            if (coords != null) {
                result.addAll(parseCoordinatesArray(coords));
            } else {
                // maybe fields stored as strings
                String maybe = obj.optString("coordinates", null);
                return parseCoordsString(maybe);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing area JSON string: " + e.getMessage());
            return parseCoordsString(raw);
        }
        return result;
    }

    //-----------------------------------------------------------------------[parseCoordinatesArray]
    private List<GeoPoint> parseCoordinatesArray(org.json.JSONArray coords) {
        List<GeoPoint> res = new ArrayList<>();
        if (coords == null) return res;

        for (int i = 0; i < coords.length(); i++) {
            Object el = coords.opt(i);
            try {
                if (el instanceof org.json.JSONArray) {
                    org.json.JSONArray pair = (org.json.JSONArray) el;
                    if (pair.length() > 0 && pair.opt(0) instanceof org.json.JSONArray) {
                        org.json.JSONArray ring = pair.optJSONArray(0);
                        if (ring != null) {
                            res.addAll(parseCoordinatesArray(ring));
                            continue;
                        }
                    }
                    if (pair.length() >= 2) {
                        double a = pair.optDouble(0, Double.NaN);
                        double b = pair.optDouble(1, Double.NaN);
                        if (!Double.isNaN(a) && !Double.isNaN(b)) {
                            double lat = a, lon = b;
                            if (a < -90 || a > 90) {
                                lat = b;
                                lon = a;
                            }
                            if (b < -90 || b > 90) {
                                lat = a;
                                lon = b;
                            }
                            res.add(new GeoPoint(lat, lon));
                        }
                    }
                } else if (el instanceof org.json.JSONObject) {
                    org.json.JSONObject o = (org.json.JSONObject) el;
                    double lat = Double.NaN, lon = Double.NaN;

                    if (o.has("latitude")) {
                        lat = o.optDouble("latitude", Double.NaN);
                    }

                    if (o.has("longitude")) {
                        lon = o.optDouble("longitude", Double.NaN);
                    }

                    if (!Double.isNaN(lat) && !Double.isNaN(lon)) {
                        res.add(new GeoPoint(lat, lon));
                    }
                }
            } catch (Exception ignored) {}
        }
        return res;
    }

    //-------------------------------------------------------------------------[handleCommentObject]
    private void handleCommentObject(org.json.JSONObject com) {
        if (com == null) return;

        double lat = Double.NaN;
        double lon = Double.NaN;
        String text = null;

        try {
            Object coordsObj = null;
            coordsObj = com.opt("coordinates");

            if (coordsObj != null) {
                if (coordsObj instanceof org.json.JSONObject) {
                    org.json.JSONObject c = (org.json.JSONObject) coordsObj;

                    lat = c.optDouble("latitude", Double.NaN);
                    if (Double.isNaN(lat)) {
                        // sometimes lat/lon are strings
                        String latS = c.optString("lat", null);
                        try {
                            lat = Double.parseDouble(latS);
                        } catch (Exception ignored) {}
                    }

                    lon = c.optDouble("longitude", Double.NaN);
                    if (Double.isNaN(lon)) {
                        String lonS = c.optString("lng", null);
                        try {
                            lon = Double.parseDouble(lonS);
                        } catch (Exception ignored) {}
                    }
                }
            }
        } catch (Exception ignored) {
            Log.e(TAG, "Error parsing comment coordinates: " + ignored.getMessage());
        }

        text = com.optString("comment", null);

        if (!Double.isNaN(lat) && !Double.isNaN(lon) && !text.trim().isEmpty()) {
            final GeoPoint gp = new GeoPoint(lat, lon);
            final String title = text;
            requireActivity().runOnUiThread(() -> addMarker(gp, title));
            return;
        }

        try {
            String raw = com.toString();
            int idx = raw.indexOf(",");
            if (idx > 0 && raw.contains(";")) {
                List<GeoPoint> pts = parseCoordsString(raw);
                if (!pts.isEmpty()) {
                    final GeoPoint p = pts.get(0);
                    final String title = !text.trim().isEmpty() ? text : "Comment";
                    requireActivity().runOnUiThread(() -> addMarker(p, title));
                    return;
                }
            }
        } catch (Exception ignored) {}

        try {
            Log.e(TAG, "Unhandled comment object format: " + com);
        } catch (Exception ignored) {}
    }
}