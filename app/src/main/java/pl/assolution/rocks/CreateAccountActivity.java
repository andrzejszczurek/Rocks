package pl.assolution.rocks;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static pl.assolution.rocks.InternetAccessChecker.checkInternetConnection;

public class CreateAccountActivity extends AppCompatActivity implements InternetAccessChecker.InternetAccessListener {

    JSONParser jsonParser = new JSONParser();
    private ProgressDialog progressDialog;
    private static String url_create_user_account = "http://student.agh.edu.pl/~aszczure/createUserAccount.php";
    private static final String TAG_SUCCESS = "success";
    private Integer errorNumber = null;

    protected Button createAccountButton;
    private EditText loginEditText;
    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText rePasswordEditText;
    private CoordinatorLayout snackBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        snackBarView = (CoordinatorLayout) findViewById(R.id.coordinator_layout_create_account);

        loginEditText = (EditText) findViewById(R.id.create_login_et);
        nameEditText = (EditText) findViewById(R.id.create_name_et);
        surnameEditText = (EditText) findViewById(R.id.create_surname_et);
        emailEditText = (EditText) findViewById(R.id.create_email_et);
        passwordEditText = (EditText) findViewById(R.id.create_password_et);
        rePasswordEditText = (EditText) findViewById(R.id.create_repate_password_et);
        createAccountButton = (Button) findViewById(R.id.sign_up_btn);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login = loginEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String surname = surnameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String rePassword = rePasswordEditText.getText().toString();

                if(checkInternetConnection(snackBarView, InternetAccessChecker.isConnected())){
                    validate(login, name, surname, email, password, rePassword);
                }
            }
        });
        checkInternetConnection(snackBarView, InternetAccessChecker.isConnected());
    }

    @Override
    protected void onResume() {
        super.onResume();
        RocksApplication.getInstance().setAccessListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        checkInternetConnection(snackBarView, isConnected);
    }


    private void validate(String login, String name, String surname, String email, String password, String rePassword) {
        Boolean isError = false;

        if(TextUtils.isEmpty(login)) {
            loginEditText.setError("Login jest wymagany");
            isError = true;
        }
        if(TextUtils.isEmpty(name)) {
            nameEditText.setError("Imię jest wymagany");
            isError = true;
        }
        if(TextUtils.isEmpty(surname)) {
            surnameEditText.setError("Nazwisko jest wymagane");
            isError = true;
        }
        if(TextUtils.isEmpty(email)) {
            emailEditText.setError("Hasło jest wymagany");
            isError = true;
        }
        if(!isValidMail(email)) {
            emailEditText.setError("Niepoprawny adres email");
            isError = true;
        }
        if(TextUtils.isEmpty(password)) {
            passwordEditText.setError("Hasło jest wymagane");
            isError = true;
        }
        if(TextUtils.isEmpty(rePassword)) {
            rePasswordEditText.setError("To pole jest wymagane");
            isError = true;
        }
        if(!(password.equals(rePassword))) {
            passwordEditText.getText().clear();
            rePasswordEditText.getText().clear();
            rePasswordEditText.setError("Niezgodne hasła");
            isError = true;
        }
        if(!isError) {
            createAccount(login, name, surname, email, password);
        }
    }

    private boolean isValidMail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void createAccount(String login, String name, String surname, String email, final String password) {
        AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... strings) {

                String login = strings[0];
                String name = strings[1];
                String surname = strings[2];
                String email = strings[3];
                String password = strings[4];
                Boolean flag = false;

                HashMap<String, String> params = new HashMap<>();
                params.put("login", login);
                params.put("name", name);
                params.put("surname", surname);
                params.put("email", email);
                params.put("password", password);
                JSONObject json = jsonParser.makeHttpRequest(url_create_user_account,"POST",params);

                try {
                    int success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        flag = true;
                    } else if (success == -1) {
                        flag = false;
                        errorNumber = -1;
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
                    Toast.makeText(getApplicationContext(), "Konto utworzone", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    finish();
                } else {
                    if(errorNumber == -1) {
                        //Toast.makeText(getApplicationContext(),"Konto o podanej nazwie użytkownika już istnieje ", Toast.LENGTH_SHORT).show();
                        loginEditText.getText().clear();
                        passwordEditText.getText().clear();
                        rePasswordEditText.getText().clear();

                        String msg = "Nazwa użytkownika zajęta";
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.create_login_et), msg ,Snackbar.LENGTH_INDEFINITE);
                        View snackBarView = snackbar.getView();
//                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
                        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackBarView.getLayoutParams();
                        params.gravity = (Gravity.TOP);
                        snackBarView.setLayoutParams(params);
                        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.RED);
                        snackbar.setActionTextColor(Color.WHITE);
                        snackBarView.setBackgroundColor(Color.GRAY);

                        snackbar.setAction("X", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        });

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        }

                        textView.setGravity(Gravity.CENTER_HORIZONTAL);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        snackbar.show();
                    }
                    progressDialog.dismiss();
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(CreateAccountActivity.this);
                progressDialog.setMessage("Jeszcze chwilę...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        };
        task.execute(login, name, surname, email, password);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_about || super.onOptionsItemSelected(item);
    }
}
