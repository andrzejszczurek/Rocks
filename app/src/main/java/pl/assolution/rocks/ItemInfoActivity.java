package pl.assolution.rocks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;

public class ItemInfoActivity extends AppCompatActivity {

    private static final String url_item_info = "http://student.agh.edu.pl/~aszczure/singleItemReading.php";;
    JSONParser jsonParser = new JSONParser();
    private ProgressDialog progressDialog;
    private Bitmap imageBitmap;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ITEM = "item";
    private static final String TAG_ID_ROCK= "id_rock";
    private static final String TAG_DESIGNATION= "designation";
    private static final String TAG_AUTHOR= "author";
    private static final String TAG_TYPE= "type";
    private static final String TAG_COLOR= "color";
    private static final String TAG_TEXTURE= "texture";
    private static final String TAG_STRUCTURE= "structure";
    private static final String TAG_BINDER= "binder";
    private static final String TAG_COMPOSITION= "composition";
    private static final String TAG_OTHER= "others";
    private static final String TAG_ADD_DATE= "date_add";
    private static final String TAG_MOD_DATE= "date_mod";
    private static final String TAG_URL_IMAGE= "image";

    private ImageView imageView;
    private TextView designationInfo;
    private TextView typeInfo;
    private TextView colorInfo;
    private TextView structureInfo;
    private TextView textureInfo;
    private TextView compositionInfo;
    private TextView binderInfo;
    private TextView otherInfo;
    private Button addComment;
    private ListView lv;
    private String id;
    JSONObject jsonObject = null;
    JSONArray jsonArray = null;
    SimplyItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar =  getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        imageView = (ImageView) findViewById(R.id.rock_image_info);
        designationInfo = (TextView) findViewById(R.id.designation_content_info__tv);
        typeInfo = (TextView) findViewById(R.id.type_content_info_tv);
        colorInfo = (TextView) findViewById(R.id.color_content_info_tv);
        structureInfo = (TextView) findViewById(R.id.structure_content_info_tv);
        textureInfo = (TextView) findViewById(R.id.texture_content_info_tv);
        compositionInfo = (TextView) findViewById(R.id.composition_content_info_tv);
        binderInfo = (TextView) findViewById(R.id.binder_content_info_tv);
        otherInfo = (TextView) findViewById(R.id.other_content_info_tv);
        addComment = (Button) findViewById(R.id.add_commment_btn);
        lv = (ListView) findViewById(R.id.list_lv);

        Intent i = getIntent();
        id = i.getStringExtra(TAG_ID_ROCK);

        GetItemInfo();
    }

    private void GetItemInfo() {
        AsyncTask<String, String, String> task = new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                HashMap<String, String> params = new HashMap<>();
                params.put("id_rock", id);

                JSONObject json = jsonParser.makeHttpRequest(url_item_info,"GET",params);

                Log.d("Pobrano: ", json.toString());                                                    //Log testowy
                try {
                    int success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        jsonArray = json.getJSONArray(TAG_ITEM);
                        jsonObject = jsonArray.getJSONObject(0);
                        String designation = jsonObject.getString(TAG_DESIGNATION);
                        String author = jsonObject.getString(TAG_AUTHOR);
                        String type= jsonObject.getString(TAG_TYPE);
                        String color = jsonObject.getString(TAG_COLOR);
                        String texture = jsonObject.getString(TAG_TEXTURE);
                        String structure = jsonObject.getString(TAG_STRUCTURE);
                        String composition = jsonObject.getString(TAG_COMPOSITION);
                        String binder = jsonObject.getString(TAG_BINDER);
                        String other = jsonObject.getString(TAG_OTHER);
                        String image_url = jsonObject.getString(TAG_URL_IMAGE);
//                        String date_add = jsonObject.getString(TAG_ADD_DATE);
//                        String date_mod = jsonObject.getString(TAG_MOD_DATE);

                        String imgUrl = "http://student.agh.edu.pl/~aszczure/"+image_url;
                        try {
                            InputStream inputStream = new java.net.URL(imgUrl).openStream();
                            Log.d("url", imgUrl);
                            imageBitmap = BitmapFactory.decodeStream(inputStream);
                            Log.d("strumien", imageBitmap.toString());
                        }catch (Exception e) {
                            Log.e("Blad", e.getMessage());
                            e.printStackTrace();
                        }

                        item = new SimplyItem(0, id, designation, author, type, color, texture, structure, composition, binder, other, imageBitmap, image_url);

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
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(ItemInfoActivity.this);
                progressDialog.setMessage("Loading. Please wait...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
                 imageView.setImageBitmap(item.getImage());
                 designationInfo.setText(item.getDesignation());
                 typeInfo.setText(item.getType());
                 colorInfo.setText(item.getColor());
                 structureInfo.setText(item.getStructure());
                 textureInfo.setText(item.getTexture());
                 compositionInfo.setText(item.getComposition());
                 binderInfo.setText(item.getBinder());
                 otherInfo.setText(item.getOther());
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
        switch(item.getItemId()){
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

}
