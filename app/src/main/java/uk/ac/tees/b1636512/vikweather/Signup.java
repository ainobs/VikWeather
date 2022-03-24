package uk.ac.tees.b1636512.vikweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Signup extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private Button signupBtn;
    private TextView signinTxt;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setTitle("Signup Form");

        mEmail = findViewById(R.id.emailReg);
        mPassword = findViewById(R.id.password);
        signupBtn = findViewById(R.id.signupBtn);
        signinTxt = findViewById(R.id.signinTxt);
        mAuth = FirebaseAuth.getInstance();

        signinTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signup.this, Signin.class));
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });
    }

    private void createUser() {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!password.isEmpty()) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(Signup.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Signup.this, Signin.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Signup.this, "Registration Error !!!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                mPassword.setError("Empty fields are not allowed");
            }
        } else if (email.isEmpty()) {
            mEmail.setError("Empty fields are not allowed");
        } else {
            mEmail.setError("Please enter correct email");
        }
    }
}