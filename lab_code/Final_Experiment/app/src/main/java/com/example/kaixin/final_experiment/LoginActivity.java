package com.example.kaixin.final_experiment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by kaixin on 2016/11/30.
 */

public class LoginActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText newPas = (EditText)findViewById(R.id.newPas);
        final EditText conPas = (EditText)findViewById(R.id.confirm);
        final EditText pas = (EditText)findViewById(R.id.pas);
        final ImageButton login_ok = (ImageButton) findViewById(R.id.login_ok);

        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        final String password = pref.getString("password", "");
        final Boolean success = pref.getBoolean("success", false);
        if (success) {
            pas.setVisibility(View.VISIBLE);
            newPas.setVisibility(View.GONE);
            conPas.setVisibility(View.GONE);
        }
        login_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!success) {
                    if (TextUtils.isEmpty(newPas.getText().toString())) {
                        Toast.makeText(LoginActivity.this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(conPas.getText().toString())) {
                        Toast.makeText(LoginActivity.this, "Confirm Password cannot beempty!", Toast.LENGTH_SHORT).show();
                    } else if (!newPas.getText().toString().equals(conPas.getText().toString())) {
                        Toast.makeText(LoginActivity.this, "Password Mismatch!", Toast.LENGTH_SHORT).show();
                    } else if (newPas.getText().toString().equals(conPas.getText().toString())){
                        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                        editor.putString("password", conPas.getText().toString());
                        editor.putBoolean("success", true);
                        editor.commit();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } else if (success) {
                    if (TextUtils.isEmpty(pas.getText().toString())) {
                        Toast.makeText(LoginActivity.this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
                    } else if (pas.getText().toString().equals(password)){
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "invalid password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
