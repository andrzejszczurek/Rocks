package pl.assolution.rocks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static pl.assolution.rocks.InternetAccessChecker.checkInternetConnection;

public class PasswordRecoveryActivity extends AppCompatActivity implements InternetAccessChecker.InternetAccessListener {

    private static final String url_validate = "http://student.agh.edu.pl/~aszczure/dataValidatePasswordRecovery.php";
    private static final String url_change_password = "http://student.agh.edu.pl/~aszczure/changePassword.php";

    public static final String LOGIN = "login";
    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String EMAIL = "email";
    public static final String SUCCESS = "success";
    public static final String PASSWORD = "password";
    private Button validateBtn;
    private Button changePasswordBtn;
    private TextView infoTv;
    private EditText login;
    private EditText name;
    private EditText surname;
    private EditText email;
    private EditText password;
    private EditText rePassword;
    private LinearLayout linearLayoutPasswordRecovery;
    private String loginInfo;

    JSONParser jsonParser = new JSONParser();
    ProgressDialog progressDialog;
    private boolean isCorrectFromServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        linearLayoutPasswordRecovery = (LinearLayout) findViewById(R.id.layout_password_recovery);

        validateBtn = (Button) findViewById(R.id.validate_btn);
        changePasswordBtn = (Button) findViewById(R.id.change_password_btn);

        infoTv = (TextView) findViewById(R.id.info_recovery_tv);

        login = (EditText) findViewById(R.id.recovery_login_et);
        name = (EditText) findViewById(R.id.recovery_name_et);
        surname = (EditText) findViewById(R.id.recovery_surname_et);
        email = (EditText) findViewById(R.id.recovery_email_et);
        password = (EditText) findViewById(R.id.recovery_password_et);
        rePassword = (EditText) findViewById(R.id.recovery_re_password_et);

        validateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String loginText = login.getText().toString();
                loginInfo = loginText;
                String nameText = name.getText().toString();
                String surnameText = surname.getText().toString();
                String emailText = email.getText().toString();

                if (checkInternetConnection(linearLayoutPasswordRecovery, InternetAccessChecker.isConnected())) {
                    if (!validate(loginText, nameText, surnameText, emailText)) {
                        validateFromServerDatabase(loginText, nameText, surnameText, emailText);
                    }
                }
             }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isError = false;
                String loginText = loginInfo;
                String passwordText = password.getText().toString();
                String rePasswordText = rePassword.getText().toString();
                if (checkInternetConnection(linearLayoutPasswordRecovery, InternetAccessChecker.isConnected())) {
                    if (TextUtils.isEmpty(passwordText)) {
                        password.setError("Hasło jest wymagane");
                        isError = true;
                    }
                    if (TextUtils.isEmpty(rePasswordText)) {
                        rePassword.setError("To pole jest wymagane");
                        isError = true;
                    }
                    if (!(passwordText.equals(rePasswordText))) {
                        password.getText().clear();
                        rePassword.getText().clear();
                        rePassword.setError("Niezgodne hasła");
                        isError = true;
                    }
                    if (!isError) {
                        changePasswordInServerDatabase(passwordText, loginText);
                    }
                }
            }
        });
        checkInternetConnection(linearLayoutPasswordRecovery, InternetAccessChecker.isConnected());
    }

    @Override
    protected void onResume() {
        super.onResume();
        RocksApplication.getInstance().setAccessListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        checkInternetConnection(linearLayoutPasswordRecovery, isConnected);
    }

    private boolean validate(String loginText, String nameText, String surnameText, String emailText) {
        boolean isError = false;

        if(TextUtils.isEmpty(loginText)) {
            login.setError("To pole jest wymagane");
            isError = true;
        }
        if(TextUtils.isEmpty(nameText)) {
            name.setError("To pole jest wymagane");
            isError = true;
        }
        if(TextUtils.isEmpty(surnameText)) {
            surname.setError("To pole jest wymagane");
            isError = true;
        }
        if(!isValidMail(emailText)) {
            email.setError("Niepoprawny adres email");
            isError = true;
        }
        return isError;
    }

    private boolean isValidMail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void validateFromServerDatabase(String loginText, String nameText, String surnameText, String emailText) {
        isCorrectFromServer = false;
        AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... strings) {
                boolean isCorrect = false;
                String login = strings[0];
                String name = strings[1];
                String surname = strings[2];
                String email = strings[3];

                HashMap<String, String> params = new HashMap<>();
                params.put(LOGIN, login);
                params.put(NAME, name);
                params.put(SURNAME, surname);
                params.put(EMAIL, email);

                JSONObject json = jsonParser.makeHttpRequest(url_validate, "POST", params);

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

                infoTv.setText("Dane poprawne, ustaw nowe hasło:");
                validateBtn.setVisibility(View.GONE);
                login.setVisibility(View.GONE);
                name.setVisibility(View.GONE);
                surname.setVisibility(View.GONE);
                email.setVisibility(View.GONE);

                password.setVisibility(View.VISIBLE);
                rePassword.setVisibility(View.VISIBLE);
                changePasswordBtn.setVisibility(View.VISIBLE);
                changePasswordBtn.setClickable(true);
            } else {
                login.getText().clear();
                name.getText().clear();
                surname.getText().clear();
                email.getText().clear();
                Toast.makeText(getApplicationContext(), "Niepoprawne dane", Toast.LENGTH_LONG).show();
            }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(PasswordRecoveryActivity.this);
                progressDialog.setMessage("Weryfikacja....");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        };
        task.execute(loginText,nameText,surnameText, emailText);
    }

    private void changePasswordInServerDatabase(String passwordText, String loginText) {
        AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... strings) {
                boolean isCorrect = false;
                String password = strings[0];
                String login = strings[1];

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
                progressDialog = new ProgressDialog(PasswordRecoveryActivity.this);
                progressDialog.setMessage("Zapisywanie nowego hasła...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        };
        task.execute(passwordText, loginText);
    }
}
