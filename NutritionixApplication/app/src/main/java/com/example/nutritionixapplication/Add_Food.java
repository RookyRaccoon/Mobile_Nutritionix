package com.example.nutritionixapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class Add_Food extends AppCompatActivity {

    //variables
    EditText queryText;
    TextView result;
    NutritionData nutritionData;
    RelativeLayout relativeLayout;
    ScrollView scrollView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    TextView  calories, protein, totalfat, sugar,
            carbs, sodium, cholesterol, fiber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__food);


    }

    private ArrayList<NutritionData> nutrientCount(){

        CollectionReference collectionReference = db.collection("nutrition");
        final ArrayList<NutritionData> mynutrition = new ArrayList<>();

        collectionReference.whereArrayContains("date", Calendar.getInstance().getTime()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                List<NutritionData> types = documentSnapshots.toObjects(NutritionData.class);
                // Add all to your list
                mynutrition.addAll(types);
            }


        });
        return mynutrition;


    }


    private void updateUI(NutritionData nutritionData) {
        scrollView.setVisibility(View.VISIBLE);

        result.setText( nutrientCount().toString());



    }

}
