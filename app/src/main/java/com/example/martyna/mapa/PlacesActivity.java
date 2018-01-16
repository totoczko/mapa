package com.example.martyna.mapa;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        addNameEditText = (EditText) findViewById(addNewName);
        addDescEditText = (EditText) findViewById(addNewDesc);
        addRangeEditText = (EditText) findViewById(addNewRange);

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("places");
    }

    public void saveItemRow(View v){
        String placeName = addNameEditText.getText().toString().trim();
        String placeDesc = addDescEditText.getText().toString().trim();
        String placeRange = addRangeEditText.getText().toString().trim();
//        LatLng placeLoc = new LatLng(-34, 151);

        //new product ID from firebase database
        String placeID = databaseRef.push().getKey();


        //create and save new product
        Place place = new Place(placeID, placeName, placeDesc, placeRange);

        databaseRef.child(placeID).setValue(place);
    }


}
