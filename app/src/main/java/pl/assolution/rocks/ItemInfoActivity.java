package pl.assolution.rocks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static pl.assolution.rocks.InternetAccessChecker.checkInternetConnection;

public class ItemInfoActivity extends AppCompatActivity implements InternetAccessChecker.InternetAccessListener{

    private static final String url_item_info = "http://student.agh.edu.pl/~aszczure/singleItemReading.php";
    private static final String url_comment= "http://student.agh.edu.pl/~aszczure/itemComments.php";
    private static final String url_delete_item= "http://student.agh.edu.pl/~aszczure/itemDeleting.php";
    private static final String TAG_COMMENT = "comment" ;
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
//    private static final String TAG_MOD_DATE= "date_mod";
    private static final String TAG_URL_IMAGE= "image";

    private ProgressDialog progressDialog;
    private Bitmap imageBitmap;
    private ImageView imageView;
    private TextView designationInfo;
    private TextView typeInfo;
    private TextView colorInfo;
    private TextView structureInfo;
    private TextView textureInfo;
    private TextView compositionInfo;
    private TextView binderInfo;
    private TextView otherInfo;
    protected Button addComment;
    private ListView commentListView;
    private String rock_id;
    protected Button editItemBtn;
    protected Button deleteItemBtn;
    protected LinearLayout linearLayout;
    private CoordinatorLayout coordinatorLayoutItemInfo;
    protected List<HashMap<String, String>> commentsList;
    private ScrollView scrollView;
    JSONObject jsonObject = null;
    JSONArray jsonArray = null;
    JSONArray jsonArrayComments = null;
    SimplyItem item;
    JSONParser jsonParser = new JSONParser();
    JSONParser jsonParserComments = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar =  getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        commentsList = new ArrayList<>();
        imageView = (ImageView) findViewById(R.id.rock_image_info);
        designationInfo = (TextView) findViewById(R.id.designation_content_info__tv);
        typeInfo = (TextView) findViewById(R.id.type_content_info_tv);
        colorInfo = (TextView) findViewById(R.id.color_content_info_tv);
        structureInfo = (TextView) findViewById(R.id.structure_content_info_tv);
        textureInfo = (TextView) findViewById(R.id.texture_content_info_tv);
        compositionInfo = (TextView) findViewById(R.id.composition_content_info_tv);
        binderInfo = (TextView) findViewById(R.id.binder_content_info_tv);
        otherInfo = (TextView) findViewById(R.id.other_content_info_tv);
        editItemBtn = (Button) findViewById(R.id.edit__btn);
        deleteItemBtn = (Button) findViewById(R.id.delete_btn);
        commentListView = (ListView) findViewById(R.id.list_tv);
        coordinatorLayoutItemInfo = (CoordinatorLayout) findViewById(R.id.coordinator_layout_item_info);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        addComment = (Button) findViewById(R.id.add_commment_btn);
        linearLayout = (LinearLayout) findViewById(R.id.my_options_layout);

        commentListView.setFocusable(false);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                findViewById(R.id.list_tv).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        commentListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        Intent i = getIntent();
        rock_id = i.getStringExtra(TAG_ID_ROCK);
        String source = i.getStringExtra("source");

        if(source.equals("my")) {
            addComment.setVisibility(View.GONE);
//            commentListView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
            editItemBtn.setVisibility(View.VISIBLE);
            deleteItemBtn.setVisibility(View.VISIBLE);

            editItemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkInternetConnection(coordinatorLayoutItemInfo, InternetAccessChecker.isConnected())) {
                        Intent intent = new Intent(getApplicationContext(), AddItemActivity.class);
                        intent.putExtra("editor", "edit");
                        intent.putExtra(TAG_ID_ROCK, rock_id);
                        intent.putExtra(TAG_DESIGNATION, item.getDesignation());
                        intent.putExtra(TAG_AUTHOR, item.getAuthor());
                        intent.putExtra(TAG_STRUCTURE, item.getStructure());
                        intent.putExtra(TAG_TEXTURE, item.getTexture());
                        intent.putExtra(TAG_COMPOSITION, item.getComposition());
                        intent.putExtra(TAG_BINDER, item.getBinder());
                        intent.putExtra(TAG_COLOR, item.getColor());
                        intent.putExtra(TAG_OTHER, item.getOther());
                        intent.putExtra(TAG_TYPE, item.getType());
                        startActivity(intent);
                    }
                }
            });

            deleteItemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkInternetConnection(coordinatorLayoutItemInfo, InternetAccessChecker.isConnected())) {
                        DeleteItem(rock_id);
                    }
                }
            });
        } else {
            addComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkInternetConnection(coordinatorLayoutItemInfo, InternetAccessChecker.isConnected())) {
                        Intent intent = new Intent(getApplicationContext(), AddCommentActivity.class);
                        intent.putExtra(TAG_ID_ROCK, rock_id);
                        startActivity(intent);
                    }
                }
            });
        }
        if(checkInternetConnection(coordinatorLayoutItemInfo, InternetAccessChecker.isConnected())) {
            GetItemInfo(rock_id);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 666) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RocksApplication.getInstance().setAccessListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intent = getIntent();
        startActivity(intent);
        finish();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        checkInternetConnection(coordinatorLayoutItemInfo, isConnected);
    }

    private void GetItemInfo(String id) {
        AsyncTask<String, String, String> task = new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                HashMap<String, String> params = new HashMap<>();
                params.put("id_rock", strings[0]);

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
                        item = new SimplyItem(0, strings[0], designation, author, type, color, texture, structure, composition, binder, other, imageBitmap, image_url);

                        JSONObject jsonComments = jsonParserComments.makeHttpRequest(url_comment,"GET",params);
                        try {
                            int successComment = jsonComments.getInt(TAG_SUCCESS);
                            if (successComment == 1) {

                                jsonArrayComments = jsonComments.getJSONArray(TAG_ITEM);
                                Log.d("Pobrano: ", jsonComments.toString());
                                for(int i = 0; i < jsonArrayComments.length(); i++) {

                                    JSONObject c = jsonArrayComments.getJSONObject(i);
                                    String authorComment = c.getString(TAG_AUTHOR);
                                    String comment = c.getString(TAG_COMMENT);
//                            String addDate = c.getString(TAG_ADD_DATE);
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put(TAG_AUTHOR, authorComment);
                                    map.put(TAG_COMMENT, comment);
//                            map.put(TAG_ADD_DATE, addDate);
                                    commentsList.add(map);
                                }
                            } else {
                                Log.d("Comment Error", "Nie udało się pobrać komentarzy");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.d("Item Error", "Nie udało się pobrać opisu skały");
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
                progressDialog.setMessage("Pobieranie danych...");
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

                ListAdapter adapter = new SimpleAdapter(
                        ItemInfoActivity.this, commentsList,
                        R.layout.comment_item,
                        new String[] { TAG_AUTHOR, TAG_COMMENT},
                        new int[] { R.id.comment_author_tv, R.id.comment_content_tv});
                commentListView.setAdapter(adapter);
            }
        };
        task.execute(id);
    }

    private void DeleteItem(String id) {
        AsyncTask<String, String, Boolean> task = new AsyncTask<String, String, Boolean>() {
            boolean flag = false;
            @Override
            protected Boolean doInBackground(String... strings) {
                HashMap<String, String> params = new HashMap<>();
                params.put("id_rock", strings[0]);

                JSONObject json = jsonParser.makeHttpRequest(url_delete_item,"POST",params);

                Log.d("Usuwany wpis: ", json.toString());
                try {
                    int success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        flag = true;
                    } else {
                        Log.d("Delete Error", "Nie udało się usunąć");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return flag;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(ItemInfoActivity.this);
                progressDialog.setMessage("Usuwanie...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(Boolean s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
                if(s) {
//                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Intent intent = getIntent();
                    setResult(100, intent);
//                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),"Nie udało się usunać", Toast.LENGTH_LONG).show();
                }
            }
        };
        task.execute(id);
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
