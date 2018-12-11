package com.adrnangcao.hotrohoctap.test;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Marker marker_1;
    private DataBase dataBase;
    private Cursor cursor;
    private ArrayList<com.adrnangcao.hotrohoctap.test.LatLng> latLngs;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        button = findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }
        });
        dataBase = new DataBase(this);
        latLngs = new ArrayList<>();
        latLngs.clear();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        cursor = dataBase.getdata();
        if (cursor.moveToNext()) {
            cursor.moveToFirst();
            do {
                final LatLng sydney = new LatLng(Double.parseDouble(cursor.getString(1)), Double.parseDouble(cursor.getString(2)));
                latLngs.add(new com.adrnangcao.hotrohoctap.test.LatLng(cursor.getInt(0),cursor.getString(3)));
                mMap.addMarker(new MarkerOptions().position(sydney).title(cursor.getString(3)));
                Log.e("POSITION", cursor.getString(0));
            } while (cursor.moveToNext());

        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.equals(marker_1)) {
                    return true;
                }
                return false;
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                final Dialog dialog = new Dialog(MapsActivity.this);
                dialog.setContentView(R.layout.dialog);
                dialog.findViewById(R.id.sua).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edit(marker);
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.xoa).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete(marker);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;
            }

        });

    }

    public void edit(final Marker marker) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("Sửa !");
        LayoutInflater inflater = (LayoutInflater) builder.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View viewDialog = inflater.inflate(R.layout.dialog_editmap, null);
        builder.setView(viewDialog);
        final EditText edtedkinhdo = viewDialog.findViewById(R.id.edteditkd);
        final EditText edtedvido = viewDialog.findViewById(R.id.edteditvd);
        final EditText edtname = viewDialog.findViewById(R.id.edtedname);
        final LatLng latLng = marker.getPosition();
        String n = marker.getId().substring(1);
        edtedkinhdo.setText(latLng.latitude + "");
        edtedvido.setText(latLng.longitude + "");
        edtname.setText(latLngs.get(Integer.parseInt(n)).getName());
        builder.setPositiveButton("Sửa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String latitude = edtedkinhdo.getText().toString();
                String longitude = edtedvido.getText().toString();
                String name =  edtname.getText().toString();
                String index = marker.getId().substring(1);
                if (latitude.equals("") || longitude.equals("")||name.equals("")) {
                    Toast.makeText(MapsActivity.this, "Nhap du!", Toast.LENGTH_SHORT).show();
                } else {
                    final LatLng sydney1 = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    if (!latLng.toString().equals(sydney1.toString())) {
                        dataBase.update(name,latitude, longitude, latLngs.get(Integer.parseInt(index)).getId());
                        mMap.addMarker(new MarkerOptions().position(sydney1).title(cursor.getColumnName(3)));
                        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(MapsActivity.this, "Sửa thành công !", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MapsActivity.this, "Nhap du!", Toast.LENGTH_SHORT).show();
                    }

                }
                dialogInterface.dismiss();

            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

    public void add() {

        final EditText edtkd = findViewById(R.id.edtkdd);
        final EditText edtvd = findViewById(R.id.edtvdd);
        final EditText edtname = findViewById(R.id.edtname);

        String latitude = edtkd.getText().toString();
        String longitude = edtvd.getText().toString();
        String namee = edtname.getText().toString();
        cursor = dataBase.getdata();
        if (!latitude.isEmpty() && !longitude.isEmpty() && !namee.isEmpty()) {
            final LatLng sydney1 = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            boolean a = false;
            if (cursor.moveToNext()) {
                cursor.moveToFirst();
                do {
                    final LatLng sydney = new LatLng(Double.parseDouble(cursor.getString(1)), Double.parseDouble(cursor.getString(2)));
                    if (sydney.toString().equals(sydney1.toString())) {
                        a = true;
                        break;
                    }
                } while (cursor.moveToNext());

            }
            if (a == false) {
                mMap.addMarker(new MarkerOptions().position(sydney1).title(cursor.getColumnName(3)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney1));
                dataBase.insert(namee,latitude, longitude);
                Toast.makeText(MapsActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        } else {
            Toast.makeText(MapsActivity.this, "Nhap Du", Toast.LENGTH_SHORT).show();
        }
    }
        public void delete(final Marker marker){
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
            builder.setTitle("Thông Báo");
            builder.setMessage("Xác nhận xóa Marker?");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String index = marker.getId().substring(1);
                    dataBase.delete(latLngs.get(Integer.parseInt(index)).getId());
                    marker.remove();
                    Toast.makeText(MapsActivity.this, "Xóa thành công !", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }
    }


