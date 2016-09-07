package pl.assolution.rocks;

import android.content.Intent;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordRecoveryActivity extends AppCompatActivity {

    private Button validateBtn;
    private Button changePasswordBtn;
    private TextView infoTv;
    private EditText login;
    private EditText name;
    private EditText surname;
    private EditText email;
    private EditText password;
    private EditText rePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

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
                String nameText = name.getText().toString();
                String surnameText = surname.getText().toString();
                String emailText = email.getText().toString();

                if(!validate(loginText, nameText, surnameText, emailText)) {
                    Intent intent = new Intent(getApplicationContext(), PasswordRecoveryActivity.class);
                    boolean con = validaFromDatabase(loginText, nameText, surnameText, emailText);
                    intent.putExtra("result", con);
                    startActivityForResult(intent, 100);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100) {
            if(resultCode == RESULT_OK) {
                if(data.getBooleanExtra("result", false)) {
                    infoTv.setText("Dane poprawne, ustaw nowe hasło:");
                    login.setVisibility(View.GONE);
                    name.setVisibility(View.GONE);
                    surname.setVisibility(View.GONE);
                    email.setVisibility(View.GONE);

                    password.setVisibility(View.VISIBLE);
                    rePassword.setVisibility(View.VISIBLE);
                    changePasswordBtn.setVisibility(View.VISIBLE);
                    changePasswordBtn.setClickable(true);

                    changePasswordBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            changePasswwordInServerdatabase();
                        }
                    });



                } else {
                    login.getText().clear();
                    name.getText().clear();
                    surname.getText().clear();
                    email.getText().clear();
                    Toast.makeText(getApplicationContext(), "Niepoprawne dane", Toast.LENGTH_LONG).show();
                }
            }
        }
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

    private boolean validaFromDatabase(String loginText, String nameText, String surnameText, String emailText) {
        return true;
    }

    private void changePasswwordInServerdatabase() {
        Toast.makeText(getApplicationContext(),"Hasło zmienione", Toast.LENGTH_LONG).show();
        finish();
    }
}
