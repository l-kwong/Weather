package com.example.weather;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    // déclaration des champs
    TextView mDate,mCity,mTemp,mDescription,mFLTemp,mHumidity;
    ImageView imgIcon;
    String maVille="Toronto";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDate=findViewById(R.id.mDate);
        mCity=findViewById(R.id.mCity);
        mTemp=findViewById(R.id.mTemp);
        mDescription=findViewById(R.id.mDescription);
        mFLTemp=findViewById(R.id.mFLTemp);
        mHumidity=findViewById(R.id.mHumidity);
        afficher(); // appel de la méthode
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recherche, menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView)menuItem.getActionView();
        searchView.setQueryHint("Écrire le nom de la ville");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                maVille=query;
                afficher();
                // gestion du clavier
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(getCurrentFocus()!=null)
                {
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void afficher()
    {
        String url="http://api.openweathermap.org/data/2.5/weather?q="+ maVille+ "&appid=edaabd12e54121169539140cbc057801&units=metric";
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                try {
                    JSONObject main_object=response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    // Log.d("Tag", "resultat = "+array.toString());
                    JSONObject object = array.getJSONObject(0);
                    // température
                    int tempC=(int)Math.round(main_object.getDouble("temp"));
                    String temp=String.valueOf(tempC);
                    // température feels like
                    int tempF=(int)Math.round(main_object.getDouble("feels_like"));
                    String tempFL=String.valueOf(tempF);
                    // humidity
                    int humidityP=(main_object.getInt("humidity"));
                    String humidity=String.valueOf(humidityP);

                    String description=object.getString("description");
                    String city=response.getString("name");
                    String icon=object.getString("icon");
                    // mettre les valeurs dans les champs
                    mCity.setText(city);
                    mTemp.setText(temp);
                    mDescription.setText(description);
                    mFLTemp.setText(tempFL);
                    mHumidity.setText(humidity);
                    // formattage du temps
                    Calendar calendar=Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM dd");
                    String formatted_data=simpleDateFormat.format(calendar.getTime());

                    mDate.setText(formatted_data);
                    // gestion de l'image
                    String imageUri="http://openweathermap.org/img/w/"+ icon+ ".png";
                    imgIcon=findViewById(R.id.imgIcon);
                    Uri myUri=Uri.parse(imageUri);
                    Picasso.with(MainActivity.this).load(myUri).resize(200, 200).into(imgIcon);
                    // Log.d("image", "message"+ myUri);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // ajouter l'objet dans la pile d'attente
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}