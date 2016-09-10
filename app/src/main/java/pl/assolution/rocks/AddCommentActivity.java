package pl.assolution.rocks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static pl.assolution.rocks.InternetAccessChecker.checkInternetConnection;

public class AddCommentActivity extends AppCompatActivity implements InternetAccessChecker.InternetAccessListener {

    private static final String url_add_comment= "http://student.agh.edu.pl/~aszczure/addComment.php";
    private static final String TAG_ID_ROCK= "id_rock";
    private static final String TAG_USER = "user";
    private static final String TAG_COMMENT = "comment" ;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_AUTHOR= "author";

    private ProgressDialog progressDialog;
    protected Button addComment;
    private RelativeLayout relativeLayout;
    private String id;
    private String author;
    protected EditText authorEt;
    private EditText commentContentEt;
    JSONParser jsonParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);

        Intent intent = getIntent();
        id = intent.getStringExtra(TAG_ID_ROCK);

        addComment = (Button) findViewById(R.id.add_comment_btn);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative_add_comment_layout);
        authorEt = (EditText) findViewById(R.id.comment_author_et);
        commentContentEt = (EditText) findViewById(R.id.comment_content_et);

        SharedPreferences sharedPreferences = getSharedPreferences(TAG_USER, MODE_PRIVATE);
        author = sharedPreferences.getString(TAG_USER, null);
        authorEt.setText(author);

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInternetConnection(relativeLayout, InternetAccessChecker.isConnected())) {
                    String commentContent = commentContentEt.getText().toString();

                    if(TextUtils.isEmpty(commentContent))
                        commentContentEt.setText("Brak treści");
                    AddComment(id, author, commentContent);
                }
            }
        });
        checkInternetConnection(relativeLayout, InternetAccessChecker.isConnected());
    }

    @Override
    protected void onResume() {
        super.onResume();
        RocksApplication.getInstance().setAccessListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        checkInternetConnection(relativeLayout, isConnected);
    }

    private void AddComment(String id, String author, String commentContent) {
        AsyncTask<String, String, String> task = new AsyncTask<String, String, String>() {

            @Override
            protected String doInBackground(String... strings) {

                HashMap<String, String> params = new HashMap<>();
                params.put(TAG_ID_ROCK, strings[0]);
                params.put(TAG_AUTHOR, strings[1]);
                params.put(TAG_COMMENT, strings[2]);

                JSONObject json = jsonParser.makeHttpRequest(url_add_comment,"POST", params);

                try {
                    int success = json.getInt(TAG_SUCCESS);
                    if(success == 1) {
                        Intent intent = getIntent();
                        setResult(666, intent);
                        finish();
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Dodano komentarz", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(AddCommentActivity.this);
                progressDialog.setMessage("Usuwanie. Proszę czekać...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        };
        task.execute(id, author, commentContent);


    }
}
