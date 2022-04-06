package uk.ac.tees.b1636512.vikweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Weather extends AppCompatActivity {
    //Declare Variables
    EditText editTextCity;
    Button button;
    TextView country, city, temp, time;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //Assign variables
        editTextCity = findViewById(R.id.editTextCity);
        button = findViewById(R.id.button);
        country = findViewById(R.id.country);
        city = findViewById(R.id.city);
        time = findViewById(R.id.time);
        temp = findViewById(R.id.temperature);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWeather();
            }
        });
    }

    private void getWeather() {
        String citty = editTextCity.getText().toString();
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+citty+"&appid=fb1709653ed8c7ff7136596f8171eeaf";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // calling API
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    // Get country
                    JSONObject object1 = jsonObject.getJSONObject("sys");
                    String country_get =object1.getString("country");
                    country.setText(country_get);

                    //Get City
                    String city_get = jsonObject.getString("name");
                    city.setText(city_get);

                    //Get Temperature
                    JSONObject object2 = jsonObject.getJSONObject("main");
                    String temp_get = object2.getString("temp");
                    temp.setText(temp_get);

                    //Get weather image icon
                    JSONArray jsonArray = jsonObject.getJSONArray("weather");
                    JSONObject object3 = jsonArray.getJSONObject(0);
                    String img = object3.getString("icon ");
                    Picasso.get().load("http://openweathermap.org/img/wn/"+img+"@2x.png").into(imageView);

                    //Get date and time
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy \nHH:mm:ss");
                    String date = sdf.format(calendar.getTime());
                    time.setText(date);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Weather.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(Weather.this);
        requestQueue.add(stringRequest);
    }
}