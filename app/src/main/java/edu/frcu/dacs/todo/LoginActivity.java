package edu.frcu.dacs.todo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    Button Btnregister;
    EditText inputEmail;
    EditText inputPassword;
    private TextView loginErrorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.pword);
        Btnregister = (Button) findViewById(R.id.registerbtn);
        btnLogin = (Button) findViewById(R.id.login);
        loginErrorMsg = (TextView) findViewById(R.id.loginErrorMsg);

        Btnregister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), RegistrationActivity.class);
                startActivityForResult(myIntent, 0);
                finish();
            }});

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                if (  ( !inputEmail.getText().toString().equals("")) && ( !inputPassword.getText().toString().equals("")) )
                {
                    //Call to the server
                    Toast.makeText(getApplicationContext(),
                            "Login en el server", Toast.LENGTH_SHORT).show();

                    //Success save token in shared preferences
                    SharedPreferences settings = getApplicationContext().getSharedPreferences("preferencesfile", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("TOKEN", "aToken");
                    editor.commit();

                    //Load main activity
                    Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
                    startActivityForResult(myIntent, 0);
                    finish();
                }
                else if ( ( !inputEmail.getText().toString().equals("")) )
                {
                    Toast.makeText(getApplicationContext(),
                            "Password estta vacio", Toast.LENGTH_SHORT).show();
                }
                else if ( ( !inputPassword.getText().toString().equals("")) )
                {
                    Toast.makeText(getApplicationContext(),
                            "Email esta vacio", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Email y Password estan vacios", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
