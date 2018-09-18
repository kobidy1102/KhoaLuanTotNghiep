package com.example.pc_asus.tinhnguyenvien;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc_asus.Modules.DirectionFinder;
import com.example.pc_asus.Modules.DirectionFinderListener;
import com.example.pc_asus.Modules.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;


public class MapViewFragment extends Fragment implements OnMapReadyCallback {
    public static GoogleMap mMap;
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    public List<Marker> originMarkers = new ArrayList<>();
    public List<Marker> destinationMarkers = new ArrayList<>();
    public List<Polyline> polylinePaths = new ArrayList<>();
    public ProgressDialog progressDialog;

    Button btnMore, btnSearch;
    EditText edtEndAddress;
    View view;
    Marker marker = null;
    double longi, lati;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_map_view, container, false);
        Log.e("abc", "camera");


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = mCurrentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //TODO
        CheckConnectionService.keyRoomVideoChat = "D79LimcFQNOkz1gVuok3lQtQDhy1";

        mDatabase.child("NguoiMu").child("Location").child(CheckConnectionService.keyRoomVideoChat).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String latitude = dataSnapshot.child("latitude").getValue().toString();
                String longitude = dataSnapshot.child("longitude").getValue().toString();
                int direction = Integer.parseInt(dataSnapshot.child("direction").getValue().toString());

                lati = Double.parseDouble(latitude);
                longi = Double.parseDouble(longitude);

                Bitmap bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.loca1);

                switch (direction) {
                    case 1:
                        bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.loca1);
                        break;
                    case 2:
                        bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.loca2);
                        break;
                    case 3:
                        bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.loca3);
                        break;
                    case 4:
                        bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.loca4);
                        break;
                    case 5:
                        bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.loca5);
                        break;
                    case 6:
                        bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.loca6);
                        break;
                    case 7:
                        bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.loca7);
                        break;
                    case 8:
                        bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.loca8);
                        break;

                }


                try {
                    marker.remove();


                } catch (Exception e) {
                }
                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latitude),
                        Double.parseDouble(longitude))).icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bmp, 65, 65, false))));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ImageView img_location = view.findViewById(R.id.img_current_location);
        edtEndAddress = view.findViewById(R.id.edt_endAddress);
        btnMore = view.findViewById(R.id.btn_more);
        btnSearch = view.findViewById(R.id.btn_search);

        img_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lati, longi), 14));

            }
        });


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtEndAddress.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Bạn chưa nhập điểm đến", Toast.LENGTH_SHORT).show();
                    return;
                }
                String startAddress = lati + "," + longi;
                DirectionA d= new DirectionA();

                try {
                    new DirectionFinder(d, "320 Trường Chinh", edtEndAddress.getText().toString().trim()).execute();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        });


        return view;

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getActivity(), "onMapReady", Toast.LENGTH_SHORT).show();
        Log.e("abc", "onMapReady");
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            return;
        }


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(13, 107.5), 6));


    }





    class DirectionA implements DirectionFinderListener {

        @Override
        public void onDirectionFinderStart() {
            progressDialog = ProgressDialog.show(getActivity(), "Please wait.",
                    "Finding direction..!", true);

            if (originMarkers != null) {
                for (Marker marker : originMarkers) {
                    marker.remove();
                }
            }

            if (destinationMarkers != null) {
                for (Marker marker : destinationMarkers) {
                    marker.remove();
                }
            }

            if (polylinePaths != null) {
                for (Polyline polyline : polylinePaths) {
                    polyline.remove();
                }
            }
        }

        @Override
        public void onDirectionFinderSuccess(List<Route> routes) {
            progressDialog.dismiss();
            polylinePaths = new ArrayList<>();
            originMarkers = new ArrayList<>();
            destinationMarkers = new ArrayList<>();

            for (Route route : routes) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));

                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                        .title(route.endAddress)
                        .position(route.endLocation)));

                PolylineOptions polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(Color.BLUE).
                        width(10);

                for (int i = 0; i < route.points.size(); i++)
                    polylineOptions.add(route.points.get(i));

                polylinePaths.add(mMap.addPolyline(polylineOptions));
            }
        }
    }
}