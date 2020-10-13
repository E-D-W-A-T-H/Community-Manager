package com.example.devcom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

public class RegisterActivity extends AppCompatActivity {

//    Code Changed

    CountryCodePicker ccp;
    EditText phoneNo, inputUsername, inputLogname, inputEmail, inputPassword, confirmPassword;
    Button btnRegister;
    Boolean isDataValid = false;
    FirebaseAuth fAuth;
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputUsername = findViewById(R.id.inputUsername);
        inputLogname = findViewById(R.id.inputLogname);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        phoneNo = findViewById(R.id.phoneNo);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phoneNo);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        fAuth = FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), dashboard.class));
            finish();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = inputPassword.getText().toString().trim();
                final String phone = phoneNo.getText().toString().trim();

                if (password.length() < 6) {
                    inputPassword.setError("Password must be greater than 6 characters.");
                    return;
                }

                if (phone.length() == 0) {
                    phoneNo.setError("Phone Number invalid.");
                }

                validateData(inputUsername);
                validateData(inputLogname);
                validateData(inputEmail);
                validateData(inputPassword);
                validateData(confirmPassword);
                validateData(phoneNo);

                if (!(inputPassword.getText().toString().equals(confirmPassword.getText().toString()))) {
                    isDataValid = false;
                    confirmPassword.setError("Passwords do not match");
                } else {
                    isDataValid = true;
                }

                if (isDataValid) {
                    fAuth.createUserWithEmailAndPassword(inputEmail.getText().toString(), inputPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(RegisterActivity.this, "User Account is Created", Toast.LENGTH_SHORT).show();

                            rootNode = FirebaseDatabase.getInstance();
                            reference = rootNode.getReference("users");

                            String fullname = inputUsername.getText().toString();
                            String username = inputLogname.getText().toString();
                            String email = inputEmail.getText().toString();
                            String password = inputPassword.getText().toString();
                            String phoneno = phoneNo.getText().toString();

                            UserHelperClass helperClass = new UserHelperClass(fullname, username, email, phoneno, password);
                            reference.child(username).setValue(helperClass);

                            Intent intent = new Intent(RegisterActivity.this, manageotp.class);
                            intent.putExtra("mobile", ccp.getFullNumberWithPlus().replace(" ", ""));
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    public void validateData (EditText field){
        if (field.getText().toString().isEmpty()) {
            isDataValid = false;
            field.setError("Required Field");
        } else {
            isDataValid = true;
        }
    }
}