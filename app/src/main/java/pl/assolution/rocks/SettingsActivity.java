package pl.assolution.rocks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static pl.assolution.rocks.InternetAccessChecker.checkInternetConnection;

public class SettingsActivity extends AppCompatActivity implements InternetAccessChecker.InternetAccessListener {

    private static final String url_validate_old_password = "http://student.agh.edu.pl/~aszczure/passwordValidate.php";
    private static final String url_change_password = "http://student.agh.edu.pl/~aszczure/changePassword.php";
    public static final String SUCCESS = "success";
    public static final String PASSWORD = "password";
    public static final String LOGIN = "login";
    private static final String TAG_USER = "user";

    private EditText actualPasswordEt;
    private EditText newPasswordEt;
    private EditText reNewPasswordEt;
    protected Button changePasswordBtn;
    private LinearLayout linearLayoutPasswordChange;

    JSONParser jsonParser = new JSONParser();
    ProgressDialog progressDialog;
    private String newPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeButtonEnabled(true);
        linearLayoutPasswordChange = (LinearLayout) findViewById(R.id.linera_layout_password_change);

        actualPasswordEt = (EditText) findViewById(R.id.actual_settings_password);
        newPasswordEt = (EditText) findViewById(R.id.recovery_password_settings_et);
        reNewPasswordEt = (EditText) findViewById(R.id.recovery_re_password_settings_et);
        changePasswordBtn = (Button) findViewById(R.id.change_password_settings_btn);

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String actualPassword = actualPasswordEt.getText().toString();
                newPassword = newPasswordEt.getText().toString();
                String reNewPassword = reNewPasswordEt.getText().toString();
                if(!validate(actualPassword, newPassword, reNewPassword)) {
                    if(checkInternetConnection(linearLayoutPasswordChange, InternetAccessChecker.isConnected())) {
                        changePassword(actualPassword);
                    }
                }
            }
        });

        checkInternetConnection(linearLayoutPasswordChange, InternetAccessChecker.isConnected());
    }

    private boolean validate(String actualPassword, String newPassword, String reNewPassword) {
        boolean isError = false;

        if (TextUtils.isEmpty(actualPassword)) {
            actualPasswordEt.setError("Aktualne hasło jest wymagane");
            isError = true;
        }
        if (TextUtils.isEmpty(newPassword)) {
            newPasswordEt.setError("To pole jest wymagane");
            isError = true;
        }

        if (TextUtils.isEmpty(reNewPassword)) {
            reNewPasswordEt.setError("To pole jest wymagane");
            isError = true;
        }
        if (!(newPassword.equals(reNewPassword))) {
            actualPasswordEt.getText().clear();
            newPasswordEt.getText().clear();
            reNewPasswordEt.getText().clear();
            reNewPasswordEt.setError("Niezgodne hasła");
            isError = true;
        }
        return isError;
    }

    private void changePassword(String actualPassword) {
        AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... strings) {
                boolean isCorrect = false;
                SharedPreferences sharedPreferences = getSharedPreferences(TAG_USER, MODE_PRIVATE);
                String login = sharedPreferences.getString(TAG_USER, null);
                String oldPassword = strings[0];

                HashMap<String, String> params = new HashMap<>();
                params.put(PASSWORD, oldPassword);
                params.put(LOGIN, login);
                JSONObject json = jsonParser.makeHttpRequest(url_validate_old_password, "POST", params);

                try {
                    int success = json.getInt(SUCCESS);
                    if(success == 1) {
                        isCorrect = true;
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                return isCorrect;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                progressDialog.dismiss();

                if(aBoolean) {
                    changePasswordInServerDatabase(newPassword);
                } else {
                    actualPasswordEt.getText().clear();
                    newPasswordEt.getText().clear();
                    reNewPasswordEt.getText().clear();
                    Toast.makeText(getApplicationContext(), "Aktualne hasło niepoprawne", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(SettingsActivity.this);
                progressDialog.setMessage("Weryfikacja...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        };
        task.execute(actualPassword);

    }


    @Override
    protected void onResume() {
        super.onResume();
        RocksApplication.getInstance().setAccessListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_settings).setEnabled(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
            case R.id.action_search:
                intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        checkInternetConnection(linearLayoutPasswordChange, isConnected);
    }


    private void changePasswordInServerDatabase(String newPassword) {
        AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... strings) {
                boolean isCorrect = false;
                String password = strings[0];
                SharedPreferences sharedPreferences = getSharedPreferences(TAG_USER, MODE_PRIVATE);
                String login = sharedPreferences.getString(TAG_USER, null);

                HashMap<String, String> params = new HashMap<>();
                params.put(PASSWORD, password);
                params.put(LOGIN, login);

                JSONObject json = jsonParser.makeHttpRequest(url_change_password, "POST", params);

                try {
                    int success = json.getInt(SUCCESS);
                    if(success == 1) {
                        isCorrect = true;
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }
                return isCorrect;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                progressDialog.dismiss();

                if (aBoolean) {
                    finish();
                    Toast.makeText(getApplicationContext(), "Hasło zmienione", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                    Toast.makeText(getApplicationContext(), "Wystapił nieoczekiwany błąd", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(SettingsActivity.this);
                progressDialog.setMessage("Zmieniam hasło...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        };
        task.execute(newPassword);
    }

}
