package com.example.martyna.mapa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void mapActivity(View v){
        Intent intent_map = new Intent(this, MapsActivity.class);
        startActivity(intent_map);
    }

    public void addPlaceActivity(View v){
        Intent intent_addplace = new Intent(this, PlacesActivity.class);
        startActivity(intent_addplace);
    }
}
