package tw.yulinlin.network;

import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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
        NetworkTask networkTask = new NetworkTask();
        networkTask.execute("http://www.yichengtech.tw/twblogs/tw_sites.php");

        // Add a marker in Sydney and move the camera
    }

    public class NetworkTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        String resultString;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new ProgressDialog(MapsActivity.this);
            dialog.setCancelable(false);
            dialog = ProgressDialog.show(MapsActivity.this, "連線中", "Wait...");
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(params[0]);
            try {
                HttpResponse response = client.execute(get);
                HttpEntity entity = response.getEntity();
                resultString = EntityUtils.toString(entity);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dialog.dismiss();
//            Toast.makeText(MainActivity.this, resultString, Toast.LENGTH_LONG).show();
            Log.d("result", resultString);
            parseJSONArray();
        }

        void parseJSONArray() {
            try {
                JSONArray jsonArray = new JSONArray(resultString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject site = jsonArray.getJSONObject(i);
                    LatLng site_coordinate =
                            new LatLng(site.getDouble("site_latitude"),
                                       site.getDouble("site_longitude"));
                    if(i%4==0)
                        mMap.addMarker(new MarkerOptions().position(site_coordinate).
                            title(site.getString("site_name")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    else if(i%4==1)
                        mMap.addMarker(new MarkerOptions().position(site_coordinate).
                            title(site.getString("site_name")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                    else if(i%4==2)
                        mMap.addMarker(new MarkerOptions().position(site_coordinate).
                            title(site.getString("site_name")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    else
                        mMap.addMarker(new MarkerOptions().position(site_coordinate).
                            title(site.getString("site_name")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                }
                LatLng center=new LatLng(24.816592206558344, 121.72374909853566);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center,13));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}