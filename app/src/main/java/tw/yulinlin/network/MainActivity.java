package tw.yulinlin.network;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ArrayList<HashMap<String,Object>>list;
    ListView lv_station;
    Button bt_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_map=findViewById(R.id.bt_map);
        bt_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });

        list=new ArrayList<>();
        lv_station=findViewById(R.id.lv_station);

        NetworkTask networkTask=new NetworkTask();
        networkTask.execute("http://www.yichengtech.tw/twblogs/tw_sites.php");
    }

    public class NetworkTask extends AsyncTask<String,Void,String> {

        ProgressDialog dialog;
        String resultString;
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog=new ProgressDialog(MainActivity.this);
            dialog.setCancelable(false);
            dialog=ProgressDialog.show(MainActivity.this, "連線中", "Wait...");
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            HttpClient client=new DefaultHttpClient();
            HttpGet get=new HttpGet(params[0]);
            try {
                HttpResponse response=client.execute(get);
                HttpEntity entity=response.getEntity();
                resultString= EntityUtils.toString(entity);

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
            Log.d("result",resultString);
            parseJSONArray();
        }

        void parseJSONArray()
        {
            try {
                JSONArray jsonArray=new JSONArray(resultString);
                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject stationJSON=jsonArray.getJSONObject(i);
                    HashMap<String,Object> stationHashMap=new HashMap<>();
                    stationHashMap.put("name",stationJSON.getString("site_name"));
                    stationHashMap.put("area",stationJSON.getString("site_area"));
                    stationHashMap.put("id",stationJSON.getString("site_id"));
                    list.add(stationHashMap);
                    Log.d("station", stationJSON.getString("site_name"));
                }

                SimpleAdapter adapter=new SimpleAdapter(
                        MainActivity.this,
                        list,
                        R.layout.stationitem,
                        new String[]{"name","area"},
                        new int[]{R.id.tv_name,R.id.tv_area}
                );
                lv_station.setAdapter(adapter);

                lv_station.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        HashMap<String,Object> item=list.get(i);
                        Toast.makeText(MainActivity.this,(String)item.get("name"),Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(MainActivity.this,SiteActivity.class);
                        Bundle bundle=new Bundle();
                        //bundle.putString("name",(String)item.get("name"));
                        bundle.putString("id",(String)item.get("id"));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}