package com.example.pc_asus.nguoimu;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class PlacesOftenComeActivity extends AppCompatActivity {

    ListView lvPlaces;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_often_come);

        lvPlaces= findViewById(R.id.lv_places);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                //  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
               // Toast.makeText(PlacesOftenComeActivity.this, "click", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PlacesOftenComeActivity.this, AddPlaceActivity.class));
            }
        });
    }
}
