package com.example.myapplication;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnSendReset;
    private TextView tvBackToLogin;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        etEmail = findViewById(R.id.etEmail);
        btnSendReset = findViewById(R.id.btnSendResetCode);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        btnSendReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndSendReset();
            }
        });

        tvBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void checkEmailAndSendReset() {
        String email = etEmail.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Valid email is required");
            etEmail.requestFocus();
            return;
        }
        mDatabase.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            sendPasswordResetEmail(email);
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    " Email not found",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (databaseError.getMessage().contains("Permission denied")) {
                            sendPasswordResetEmail(email);
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Error: " + databaseError.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendPasswordResetEmail(String email) {
        // Progress show karo (optional)
        btnSendReset.setEnabled(false);
        btnSendReset.setText("Sending...");

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    btnSendReset.setEnabled(true);
                    btnSendReset.setText("Send Reset Email");

                    if (task.isSuccessful()) {
                        new AlertDialog.Builder(this)
                                .setTitle("Success")
                                .setMessage("Password reset email sent to:\n" + email +
                                        "\n\nPlease check your inbox or spam folder.")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    finish();
                                })
                                .show();
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(ForgotPasswordActivity.this,
                                " Failed: " + error,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}