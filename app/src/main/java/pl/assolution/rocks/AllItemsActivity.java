package pl.assolution.rocks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.HashMap;

public class AllItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SimplyItem.ItemsList list = new SimplyItem.ItemsList();
    private Bitmap imageBitmap = null;
    private ItemsAdapter itemsAdapter;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ITEMS = "item";
    private static final String TAG_ID_ROCK= "id_rock";
    private static final String TAG_DESIGNATION= "designation";
    private static final String TAG_DESCRIPTION= "type";                                                                //tymczasowe
    private static final String TAG_AUTHOR= "designation";                                                                //tymczasowe
    private static final String TAG_URL_IMAGE= "image";                                                                //tymczasowe
    private static String url_all_items_reading = "http://student.agh.edu.pl/~aszczure/allItemsReading.php";
    private ProgressDialog progressDialog;
    JSONArray items = null;
    JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar =  getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addOnItemTouchListener(new RecyclerViewOnItemClickListener(getApplicationContext(), recyclerView, new RecyclerViewOnItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView tv = (TextView) view.findViewById(R.id.id_rock_tv);
                String id = tv.getText().toString();
                Intent intent = new Intent(getApplicationContext(), ItemInfoActivity.class);
                intent.putExtra(TAG_ID_ROCK, id);
                startActivityForResult(intent, 100);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(getApplicationContext(),"Klikniessssssssssssssta", Toast.LENGTH_SHORT).show();
            }
        }));
        LoadAllItems();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    private void LoadAllItems() {
        AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... strings) {
                HashMap<String, String> params = new HashMap<>();

                JSONObject json = jsonParser.makeHttpRequest(url_all_items_reading,"GET",params);

                Log.d("Wszystkie skaly: ", json.toString());                                                    //Log testowy

                try {
                    int success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        items = json.getJSONArray(TAG_ITEMS);

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject c = items.getJSONObject(i);

                            String id_rock = c.getString(TAG_ID_ROCK);
                            String designation = c.getString(TAG_DESIGNATION);
                            String description = c.getString(TAG_DESCRIPTION);
                            String author = c.getString(TAG_AUTHOR);
                            String url = c.getString(TAG_URL_IMAGE);
                            String imgUrl = "http://student.agh.edu.pl/~aszczure/"+url;

                            try {
                                InputStream inputStream = new java.net.URL(imgUrl).openStream();
                                Log.d("url", imgUrl);
                                imageBitmap = BitmapFactory.decodeStream(inputStream);
                                Log.d("strumien", imageBitmap.toString());

                            }catch (Exception e) {
                                Log.e("Blad", e.getMessage());
                                e.printStackTrace();
                            }
                            Log.d("lista", list.toString());
                            list.add(new SimplyItem(i,id_rock, designation, description, author, imgUrl, imageBitmap ));
                            Log.d("lista od i", list.get(i).toString());

                        }
                    } else {
                        Log.d("Co poszlo nie tak", "dupa");
//                    Intent intent = new Intent(getApplicationContext(), testActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                progressDialog.dismiss();
                itemsAdapter = new ItemsAdapter(list);
                recyclerView.setAdapter(itemsAdapter);
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(AllItemsActivity.this);
                progressDialog.setMessage("Loading. Please wait...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
        }
        };
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                //onBackPressed();
                return true;
            case R.id.action_search:
                intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                finish();
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}



