package com.example.nutritionixapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ViewUtils;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class MainActivity extends AppCompatActivity {

    //variables
    EditText queryText;
    TextView responseView;
    ProgressBar progressBar;
    NutritionData nutritionData;
    RelativeLayout relativeLayout;
    ScrollView scrollView;
    Button queryButton;
    Button addItem;

    //random firestore id for my documents
    public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz|!£$%&/=@#";
    public static Random RANDOM = new Random();

    ImageView foodPicture;
    TextView foodName, calories, protein, totalfat, sugar, servingSize,
            carbs, sodium, cholesterol, fiber;

    String product;
    //Please note, when authenticating with the API,
    // you must send the x-app-id and x-app-key params as headers,
    // and not as query string parameters.
    //nutritionix request header
    static final String API_ID = "7da56e4c";
    static final String API_KEY = "6ee57c16d6ebfe7837e7dd1c8b0c872b";
    static final String API_REMOTE_USER = "0";
    static final String API_URL = "https://trackapi.nutritionix.com/v2/natural/nutrients";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // responseView = (TextView) findViewById(R.id.responseView);
        queryText = (EditText) findViewById(R.id.queryText);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        queryButton = (Button) findViewById(R.id.queryButton);
        foodName = (TextView) findViewById(R.id.foodItem);
        servingSize = (TextView) findViewById(R.id.foodQuantity);
        calories = (TextView) findViewById(R.id.calories);
        protein = (TextView) findViewById(R.id.protein);
        totalfat = (TextView) findViewById(R.id.fat);
        sugar = (TextView) findViewById(R.id.sugar);
        carbs = (TextView) findViewById(R.id.carbs);
        sodium = (TextView) findViewById(R.id.sodium);
        cholesterol = (TextView) findViewById(R.id.cholesterol);
        fiber = (TextView) findViewById(R.id.fibre);
        foodPicture = (ImageView) findViewById(R.id.foodPic);
        addItem = (Button) findViewById(R.id.addItem);
        scrollView.setVisibility(View.INVISIBLE);
        addItem.setVisibility(View.INVISIBLE);

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product = queryText.getText().toString();
                connectNutritionix(product);
                addItem.setVisibility(View.VISIBLE);

            }
        });


        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String userID = randomString(15);

                DocumentReference documentReference= db.collection("nutrition").document(userID);

                Map<String,Object>  nutritiondata = new HashMap<>();
                nutritiondata.put("foodname", nutritionData.getFoodName());
                nutritiondata.put("calories", nutritionData.getCalories());
                nutritiondata.put("protein", nutritionData.getProtein());
                nutritiondata.put("carbs", nutritionData.getTotalCarbonhydrate());
                nutritiondata.put("fiber", nutritionData.getDiertaryFiber());
                nutritiondata.put("sodium", nutritionData.getSodium());
                nutritiondata.put("fat", nutritionData.getTotalfat());
                nutritiondata.put("cholesterol", nutritionData.getCholesterol());
                nutritiondata.put("sugar", nutritionData.getSugar());

                nutritiondata.put("date", Calendar.getInstance().getTime());

                documentReference.set(nutritiondata).addOnSuccessListener(new OnSuccessListener<Void>()
                {
    @Override
    public void onSuccess(Void aVoid) {
        Log.d("NutritionApp", "success adding document in db");
    }
                });
               // updateDailyUI(nutritionData);

            }
        });
    }





    private void openAddFood(){
        Intent intent = new Intent(this,Add_Food.class);
        startActivity(intent);
    }

    private void createDB(){
        CollectionReference nutritionref = db.collection("nutrition");



    }
    //search for food item
        private void connectNutritionix(String foodName) {
            StringEntity stringEntity = null;
            AsyncHttpClient client = new AsyncHttpClient();

            client.addHeader("x-app-id", API_ID);
            client.addHeader("x-app-key", API_KEY);
            client.addHeader("x-remote-user-id", API_REMOTE_USER);

            JSONObject jsonObject = new JSONObject();


            try {
                jsonObject.put("query", foodName);

            } catch (JSONException e) {
                Log.d("App", e.toString());
            }
            try {
                stringEntity = new StringEntity(jsonObject.toString());
            } catch (UnsupportedEncodingException e) {
                Log.d("App", e.toString());
            }

            client.post(this, API_URL, stringEntity, "application/json",
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("Nutritionapp", "Success" + response.toString());

                            nutritionData = NutritionData.fromJson(response);
                            queryText.setCursorVisible(false);
                                new DownloadImage(foodPicture)
                                    .execute(nutritionData.getPhotoUrl());
                            foodPicture.setVisibility(View.VISIBLE);

                            updateUI(nutritionData);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("NutritionApp", "failed" + errorResponse.toString());
                            Toast.makeText(MainActivity.this, "Sorry, We couldn't match any of your foods",
                                    Toast.LENGTH_SHORT).show();

                            queryText.setCursorVisible(false);



                        }
                    });

        }

    private void updateUI(NutritionData nutritionData) {
        scrollView.setVisibility(View.VISIBLE);
        foodName.setText(nutritionData.getFoodName());
        servingSize.setText(nutritionData.getServingSize());

        calories.setText("Calories : " + String.valueOf(nutritionData.getCalories()) +"kcal");
        protein.setText("Protein : " + String.valueOf(nutritionData.getProtein()) + "g");
        totalfat.setText("Fat : " +String.valueOf(nutritionData.getTotalfat()) + "g");
        sugar.setText("Sugar : " +String.valueOf(nutritionData.getSugar()) + "g");
        carbs.setText("Carbs : "+ String.valueOf(nutritionData.getTotalCarbonhydrate() + "g"));
        sodium.setText("Sodium: " +String.valueOf(nutritionData.getSodium()) + "mg");
        cholesterol.setText("Cholesterol : " +String.valueOf(nutritionData.getCholesterol()) + "mg");
        fiber.setText("Fiber : "  +nutritionData.getDiertaryFiber() + "g");

        relativeLayout.setVisibility(View.VISIBLE);

    }




// id for my firestore documents
    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }

        return sb.toString();
    }


    }
