package com.example.devcom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    EditText username, password;
    Button button;
    TextView btn_register;
    Boolean isUsernameValid, isPasswordValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.inputUsername);
        password = findViewById(R.id.inputPhone);
        button = findViewById(R.id.btn);
        btn_register = findViewById(R.id.goRegister);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                letTheUserLoggedIn(v);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

    }

    public void letTheUserLoggedIn(View view) {
        if (!validateFields()) {
            return;
        }

        final String _username = username.getText().toString().trim();
        final String _password = password.getText().toString().trim();

        Query checkUser = FirebaseDatabase.getInstance().getReference("users").orderByChild("username").equalTo(_username);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    username.setError(null);

                    String systemPassword = snapshot.child(_username).child("password").getValue(String.class);

                    if (systemPassword.equals(_password)) {
                        password.setError(null);

                        Intent intent = new Intent(MainActivity.this, dashboard.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(MainActivity.this, "Password does not match!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Data does not exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean validateFields() {

        if (username.getText().toString().isEmpty()) {
            isUsernameValid = false;
            username.setError("Required Field");
        } else {
            isUsernameValid = true;
        }

        if(password.getText().toString().isEmpty()) {
            isPasswordValid = false;
            password.setError("Required Field");
        } else {
            isPasswordValid = true;
        }

        return (isUsernameValid && isPasswordValid);
    }

}












