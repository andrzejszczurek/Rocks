package pl.assolution.rocks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

import static pl.assolution.rocks.InternetAccessChecker.checkInternetConnection;

public class LoginActivity extends Activity implements InternetAccessChecker.InternetAccessListener {

    JSONParser jsonParser = new JSONParser();
    private ProgressDialog progressDialog;
    private static String url_login_user = "http://student.agh.edu.pl/~aszczure/loginUser.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_LOGIN= "login";
    private static final String TAG_ITEM = "item";
    private static String user_login = null;
    private EditText loginEditText;
    private EditText passwordEditText;
    protected Button passwordForgot;
    private LinearLayout linearLayoutLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button loginButton = (Button) findViewById(R.id.login_btn);
        Button createAccountButton = (Button) findViewById(R.id.create_account_btn);
        loginEditText = (EditText) findViewById(R.id.login_et);
        passwordEditText = (EditText) findViewById(R.id.password_et);
        passwordForgot = (Button) findViewById(R.id.password_forgot_btn);
        linearLayoutLogin = (LinearLayout) findViewById(R.id.login_linear_layout);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                    startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login = loginEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                boolean isError = false;

                if(checkInternetConnection(linearLayoutLogin, InternetAccessChecker.isConnected())) {
                    if(TextUtils.isEmpty(login)) {
                        loginEditText.setError("Login jest wymagany");
                        isError = true;
                    }
                    if(TextUtils.isEmpty(password)) {
                        passwordEditText.setError("Hasło jest wymagane");
                        isError = true;
                    }
                    if(!isError) {
                        login(login, password);
                    }
                }
            }
        });

        passwordForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intentPass = new Intent(getApplicationContext(), PasswordRecoveryActivity.class);
                    startActivity(intentPass);
            }
        });
        checkInternetConnection(linearLayoutLogin, InternetAccessChecker.isConnected());
    }

    @Override
    protected void onResume() {
        super.onResume();
        RocksApplication.getInstance().setAccessListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        checkInternetConnection(linearLayoutLogin, isConnected);
    }

    private void login(String login, String password) {

        final AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... strings) {
                String login = strings[0];
                String password = strings[1];
                Boolean flag = false;
                HashMap<String, String> params = new HashMap<>();
                params.put("login", login);
                params.put("password", password);
                JSONObject json = jsonParser.makeHttpRequest(url_login_user,"POST",params);
                try {
                    int success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        user_login = json.getJSONArray(TAG_ITEM).getJSONObject(0).getString(TAG_LOGIN);
                        flag = true;
                    } else {
                        flag = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return flag;
            }
            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if(result) {

//                    SharedPreferences.Editor editor = getSharedPreferences(USER, MODE_PRIVATE).edit();
//                    editor.putString(USER, user_name);
//                    editor.apply();

                    ((RocksApplication)getApplication()).getLoginManager().saveLogin(user_login);

                    Toast.makeText(getApplicationContext(),"Zalogowano jako "+user_login, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("userName", user_login);
                    startActivity(intent);
                    progressDialog.dismiss();
                    finish();
                } else {
                    Log.d("Nie zalogowano", "Brak użytkownika[1]");
                    Toast.makeText(getApplicationContext(), "Niepoprawny login lub hasło", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Loading products. Please wait...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        };
        task.execute(login, password);
    }
}
