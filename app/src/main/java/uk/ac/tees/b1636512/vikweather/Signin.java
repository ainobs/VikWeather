package uk.ac.tees.b1636512.vikweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executor;

public class Signin extends AppCompatActivity {

    private Button login;
    private EditText mEmail, mPassword;
    private TextView signupTxt, authStatus;
    private ImageView fingerAuth;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = db.getReference();
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        getSupportActionBar().setTitle("Login");
        signupTxt = findViewById(R.id.signupTxt);
        login = findViewById(R.id.login);
        mEmail = findViewById(R.id.email_signin);
        mPassword = findViewById(R.id.password_signin);
        mAuth = FirebaseAuth.getInstance();
        fingerAuth = findViewById(R.id.fingerAuth);
        authStatus = findViewById(R.id.authStatus);

        //initialize biometric
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(Signin.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // error authentication
                Toast.makeText(Signin.this, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                //Auth success
                Toast.makeText(Signin.this, "logged in successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Signin.this, MainActivity.class));
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(Signin.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        //setup title, description on auth dialog
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Login using fingerprint authentication")
                .setNegativeButtonText("User app password")
                .build();

        //handle auth button click, handle authentication
        fingerAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show auth dialog
                biometricPrompt.authenticate(promptInfo);
            }
        });

        signupTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signin.this, Signup.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!password.isEmpty()) {
               mAuth.signInWithEmailAndPassword(email, password)
                       .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                           @Override
                           public void onSuccess(AuthResult authResult) {
                               Toast.makeText(Signin.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Signin.this, MainActivity.class));
                                finish();
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(Signin.this, "Login Failed !!", Toast.LENGTH_SHORT).show();
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

//    public void btn_SignForm(View view) {
//       // startActivity(new Intent(getApplicationContext(), Signup.class));
//
//
//    }
}