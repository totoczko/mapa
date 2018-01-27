package com.example.martyna.mapa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.martyna.mapa.R.id.addNewDesc;
import static com.example.martyna.mapa.R.id.addNewName;
import static com.example.martyna.mapa.R.id.addNewRange;

public class PlacesActivity extends AppCompatActivity {

    EditText addNameEditText;
    EditText addDescEditText;
    EditText addRangeEditText;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    public LatLng locToSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        Intent i = getIntent();
        locToSave = i.getParcelableExtra("localisation");
        Log.i("tag", "locToSave: " + locToSave);

        addNameEditText = (EditText) findViewById(addNewName);
        addDescEditText = (EditText) findViewById(addNewDesc);
        addRangeEditText = (EditText) findViewById(addNewRange);

        //connect to Firebase
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("places");
    }

    public void saveItemRow(View v){
        String placeName = addNameEditText.getText().toString().trim();
        String placeDesc = addDescEditText.getText().toString().trim();
        String placeRange = addRangeEditText.getText().toString().trim();


        //new place ID from firebase database
        String placeID = databaseRef.push().getKey();


        //create and save new place
        Place place = new Place(placeID, locToSave, placeName, placeDesc, placeRange);

        databaseRef.child(placeID).setValue(place);
    }


}
