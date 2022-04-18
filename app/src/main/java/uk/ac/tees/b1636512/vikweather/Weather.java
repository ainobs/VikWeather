package uk.ac.tees.b1636512.vikweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.icu.number.Precision;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Weather extends AppCompatActivity {
    //Declaring  Variables
    EditText editTextCity;
    Button button, testing;
    TextView country, city, temp, time, latitude, longitude, humidity, pressure, sunrise, sunset, wind, description;
    ImageView imageView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.weather_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.share:
                File file = saveImage();
                if(file!=null)
                    share(file);
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            File file = saveImage();
            if(file!=null)
                share(file);
        } else
            Toast.makeText(Weather.this, "permission denied", Toast.LENGTH_SHORT).show();
    }

    // share screenshot image to other apps
    private void share(File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(Weather.this, getPackageName()+".provider",file);
        } else {
            uri = Uri.fromFile(file);
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Screenshot");
        intent.putExtra(Intent.EXTRA_TEXT, "my weather report");
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        try {
            startActivity(Intent.createChooser(intent,"Share using"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    // save screenshot in the pictures folder in the sdcard
    private File saveImage() {
        if(!checkPermission())
            return null;

        try {
            String path = Environment.getExternalStorageDirectory().toString() + "/pictures";
            File fileDir = new File(path);
            if(!fileDir.exists())
                fileDir.mkdir();

            String mPath = path + "/Screenshot_" + new Date().getTime() + ".png";
            Bitmap bitmap = screenShot();
            File file = new File(mPath);
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fOut);
            fOut.flush();
            fOut.close();
            Toast.makeText(Weather.this, "image saved successfully", Toast.LENGTH_SHORT).show();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // create screenshot of the view
    private Bitmap screenShot() {
        View v = findViewById(R.id.parent);
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(),v.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    // check permission to write and read media access
    private boolean checkPermission() {
        int permission = ActivityCompat.checkSelfPermission(Weather.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Weather.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //Assigning variables
        editTextCity = findViewById(R.id.editTextCity);
        button = findViewById(R.id.button);
        country = findViewById(R.id.country);
        city = findViewById(R.id.city);
        time = findViewById(R.id.time);
        temp = findViewById(R.id.temperature);
        imageView = findViewById(R.id.imageView);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        pressure = findViewById(R.id.pressure);
        humidity = findViewById(R.id.humidity);
        sunrise = findViewById(R.id.sunrise);
        sunset = findViewById(R.id.sunset);
        wind = findViewById(R.id.windSpeed);
        description = findViewById(R.id.description);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Prevent button submission when input field is empty */
                if(editTextCity.getText().toString().equals("")) {
                    Toast.makeText(Weather.this, "Please enter a city", Toast.LENGTH_SHORT).show();
                } else {
                    getWeather();
                }
            }
        });
    }

    private void getWeather() {
        String citty = editTextCity.getText().toString();
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+citty+"&appid=fb1709653ed8c7ff7136596f8171eeaf";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // calling API endpoint
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
                    int temp_get = object2.getInt("temp") - 273;
                    temp.setText(temp_get+ "°C");

                    //Get weather image icon
                    JSONArray jsonArray = jsonObject.getJSONArray("weather");
                    JSONObject object3 = jsonArray.getJSONObject(0);
                    String img = object3.getString("icon");
                    Picasso.get().load("http://openweathermap.org/img/wn/"+img+"@2x.png").into(imageView);

                    //Get date and time
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat std;
                    std = new SimpleDateFormat("dd/MM/yyyy \nHH:mm:ss");
                    String date = std.format(calendar.getTime());
                    time.setText(date);

                    //Get Latitude
                    JSONObject object4 = jsonObject.getJSONObject("coord");
                    double lat_get = object4.getDouble("lat");
                    latitude.setText(lat_get+ "° N");

                    //Get Longitude
                    JSONObject object5 = jsonObject.getJSONObject("coord");
                    double long_get = object5.getDouble("lon");
                    longitude.setText(long_get+ "° E");

                    //Get humidity
                    JSONObject object6 = jsonObject.getJSONObject("main");
                    String humidity_get = object6.getString("humidity");
                    humidity.setText(humidity_get);

                    //Get sunrise
                    JSONObject object7 = jsonObject.getJSONObject("sys");
                    String sunrise_get = object7.getString("sunrise");
                    sunrise.setText(sunrise_get);

                    //Get sunset
                    JSONObject object8 = jsonObject.getJSONObject("sys");
                    String sunset_get = object7.getString("sunset");
                    sunset.setText(sunset_get);

                    //Get pressure
                    JSONObject object9 = jsonObject.getJSONObject("main");
                    String pressure_get = object9.getString("pressure");
                    pressure.setText(pressure_get+" hPa");

                    //Get Wind speed
                    JSONObject object10 = jsonObject.getJSONObject("wind");
                    String wind_get = object10.getString("speed");
                    wind.setText(wind_get+" Km/Hr");

                    //Get Description
                    JSONArray jsonArray2 = jsonObject.getJSONArray("weather");
                    JSONObject object11 = jsonArray2.getJSONObject(0);
                    String desc_get = object11.getString("description");
                    description.setText(desc_get);


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