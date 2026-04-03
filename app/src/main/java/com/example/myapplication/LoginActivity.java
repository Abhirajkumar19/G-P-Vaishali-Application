package com.example.myapplication;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText etRegistration, etPassword;
    private Button btnLogin;
    private TextView tvForgotUsername, tvForgotPassword, tvRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, home.class));
            finish();
        }

        etRegistration = findViewById(R.id.Registration);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.loginButton);
        tvForgotUsername = findViewById(R.id.forgotusername);
        tvForgotPassword = findViewById(R.id.forgotpassword);
        tvRegister = findViewById(R.id.registerButton);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        tvForgotUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                forgotUsername();
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void loginUser() {
        String registration = etRegistration.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(registration)) {
            etRegistration.setError("Registration number is required");
            return;
        }
        if (!registration.matches("\\d{10}")) {
            etRegistration.setError("Registration number must be 10 digits");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }

        mDatabase.child(registration).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String email = dataSnapshot.child("email").getValue(String.class);
                    if (email != null) {
                        authenticateUser(email, password);
                    }
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Registration number not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this,
                        "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void authenticateUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            sessionManager.createLoginSession(user.getUid(), email);

                            Toast.makeText(LoginActivity.this,
                                    "Login successful!", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LoginActivity.this, home.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Login failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void forgotUsername() {

        Toast.makeText(this,
                "Please contact admin to retrieve your username",
                Toast.LENGTH_LONG).show();
    }

    private void forgotPassword() {

        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }
}