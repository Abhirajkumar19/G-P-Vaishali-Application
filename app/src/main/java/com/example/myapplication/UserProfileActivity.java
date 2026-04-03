package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.io.InputStream;

public class UserProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST = 100;

    // View Mode Views
    private ImageView profileImage;
    private TextView tvName, tvBranch, tvRollNo, tvRegNo, tvEmail;
    private LinearLayout viewModeLayout;

    // Edit Mode Views
    private LinearLayout editModeLayout;
    private TextInputEditText etName, etBranch, etRollNo, etRegNo, etEmail;
    private ImageView btnBack, btnEdit, btnChangePhoto;
    private Button btnSave, btnCancel;

    private DatabaseHelper databaseHelper;
    private Bitmap selectedImageBitmap;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initializeViews();
        databaseHelper = new DatabaseHelper(this);
        loadProfile();

        setupClickListeners();
    }

    private void initializeViews() {
        // View Mode
        profileImage = findViewById(R.id.profileImage);
        tvName = findViewById(R.id.tvName);
        tvBranch = findViewById(R.id.tvBranch);
        tvRollNo = findViewById(R.id.tvRollNo);
        tvRegNo = findViewById(R.id.tvRegNo);
        tvEmail = findViewById(R.id.tvEmail);
        viewModeLayout = findViewById(R.id.viewModeLayout);

        // Edit Mode
        editModeLayout = findViewById(R.id.editModeLayout);
        etName = findViewById(R.id.etName);
        etBranch = findViewById(R.id.etBranch);
        etRollNo = findViewById(R.id.etRollNo);
        etRegNo = findViewById(R.id.etRegNo);
        etEmail = findViewById(R.id.etEmail);

        // Buttons and Icons
        btnBack = findViewById(R.id.btnBack);
        btnEdit = findViewById(R.id.btnEdit);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> toggleEditMode(true));

        btnChangePhoto.setOnClickListener(v -> checkPermissionAndPickImage());

        profileImage.setOnClickListener(v -> {
            if (isEditMode) {
                checkPermissionAndPickImage();
            }
        });

        btnCancel.setOnClickListener(v -> {
            toggleEditMode(false);
            loadProfile(); // Reset to original values
        });

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void toggleEditMode(boolean enable) {
        isEditMode = enable;

        if (enable) {
            viewModeLayout.setVisibility(View.GONE);
            editModeLayout.setVisibility(View.VISIBLE);
            btnChangePhoto.setVisibility(View.VISIBLE);

            // Populate edit fields with current values
            etName.setText(tvName.getText().toString().equals("Not set") ? "" : tvName.getText().toString());
            etBranch.setText(tvBranch.getText().toString().equals("Not set") ? "" : tvBranch.getText().toString());
            etRollNo.setText(tvRollNo.getText().toString().equals("Not set") ? "" : tvRollNo.getText().toString());
            etRegNo.setText(tvRegNo.getText().toString().equals("Not set") ? "" : tvRegNo.getText().toString());
            etEmail.setText(tvEmail.getText().toString().equals("Not set") ? "" : tvEmail.getText().toString());
        } else {
            viewModeLayout.setVisibility(View.VISIBLE);
            editModeLayout.setVisibility(View.GONE);
            btnChangePhoto.setVisibility(View.GONE);
        }
    }

    private void checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST);
        } else {
            openImagePicker();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission required to select image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                selectedImageBitmap = BitmapFactory.decodeStream(inputStream);
                profileImage.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadProfile() {
        Profile profile = databaseHelper.getProfile();

        if (profile != null && profile.getName() != null) {
            if (profile.getImage() != null) {
                profileImage.setImageBitmap(profile.getImage());
                selectedImageBitmap = profile.getImage();
            }

            tvName.setText(profile.getName());
            tvBranch.setText(profile.getBranch());
            tvRollNo.setText(profile.getRollNo());
            tvRegNo.setText(profile.getRegNo());
            tvEmail.setText(profile.getEmail());
        }
    }

    private void saveProfile() {
        if (validateInputs()) {
            Profile profile = new Profile(
                    selectedImageBitmap,
                    etName.getText().toString().trim(),
                    etBranch.getText().toString().trim(),
                    etRollNo.getText().toString().trim(),
                    etRegNo.getText().toString().trim(),
                    etEmail.getText().toString().trim()
            );

            long result = databaseHelper.saveProfile(profile);

            if (result > 0) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                // Update view mode with new values
                tvName.setText(profile.getName());
                tvBranch.setText(profile.getBranch());
                tvRollNo.setText(profile.getRollNo());
                tvRegNo.setText(profile.getRegNo());
                tvEmail.setText(profile.getEmail());

                // Switch back to view mode
                toggleEditMode(false);
            } else {
                Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInputs() {
        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError("Name is required");
            return false;
        }
        if (etBranch.getText().toString().trim().isEmpty()) {
            etBranch.setError("Branch is required");
            return false;
        }
        if (etRollNo.getText().toString().trim().isEmpty()) {
            etRollNo.setError("Roll No is required");
            return false;
        }
        if (etRegNo.getText().toString().trim().isEmpty()) {
            etRegNo.setError("Registration No is required");
            return false;
        }
        if (etEmail.getText().toString().trim().isEmpty()) {
            etEmail.setError("Email is required");
            return false;
        }
        return true;
    }
}