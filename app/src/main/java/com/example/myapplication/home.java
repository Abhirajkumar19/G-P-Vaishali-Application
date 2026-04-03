package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;

public class home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Set up toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Set navigation item click listener
        navigationView.setNavigationItemSelectedListener(this);

        // Set logo click listener
        ImageView logoImage = findViewById(R.id.logo_image);
        logoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(home.this, "College Logo Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.Library) {
            Toast.makeText(this, "Library clicked", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.profile) {
            startActivity(new Intent(home.this, UserProfileActivity.class));
        } else if (id == R.id.College) {
            startActivity(new Intent(home.this, AboutCollege.class));
        } else if (id == R.id.academic) {
            startActivity(new Intent(home.this, homepage.class));
        } else if (id == R.id.Admission) {
            startActivity(new Intent(home.this, admission_process.class));
        } else if (id == R.id.Gallery) {
            startActivity(new Intent(home.this, Gallery.class));
        } else if (id == R.id.Contact) {
            startActivity(new Intent(home.this, contactus.class));
        } else if (id == R.id.Share) {
            shareApp();
        } else if (id == R.id.logout) {
            logout();
        } else if (id == R.id.Member) {
            startActivity(new Intent(home.this, Semester4Activity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // 🔴 Yahan se class ke andar methods hain
    private void shareApp() {
        try {
            File file = new File(getApplicationContext().getApplicationInfo().sourceDir);

            Uri uri = FileProvider.getUriForFile(home.this,
                    getPackageName() + ".fileprovider", file);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/vnd.android.package-archive");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Share App via"));
            Toast.makeText(home.this, "Sharing...", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(home.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void logout() {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        // Session clear
        SessionManager sessionManager = new SessionManager(home.this);
        sessionManager.logout();

        // Login activity par jayein
        Intent intent = new Intent(home.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}