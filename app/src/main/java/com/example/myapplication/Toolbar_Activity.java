package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class Toolbar_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawerlayout);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Set up toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu); // Custom hamburger icon
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Set navigation item click listener
        navigationView.setNavigationItemSelectedListener(this);

        // Optional: Set logo click listener
        ImageView logoImage = findViewById(R.id.logo_image);
        logoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Toolbar_Activity.this, "College Logo Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Handle hamburger icon click
        if (id == android.R.id.home) {
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
            startActivity(new Intent(Toolbar_Activity.this, UserProfileActivity.class));
        } else if (id == R.id.College) {
            startActivity(new Intent(Toolbar_Activity.this, AboutCollege.class));
        } else if (id == R.id.academic) {
            startActivity(new Intent(Toolbar_Activity.this, homepage.class));
        } else if (id == R.id.Admission) {
            startActivity(new Intent(Toolbar_Activity.this, admission_process.class));
        } else if (id == R.id.Contact) {
            startActivity(new Intent(Toolbar_Activity.this, contactus.class));
        } else if (id == R.id.Share) {
            shareApp();
        } else if (id == R.id.logout) {
            logout();
        } else if (id == R.id.Member) {
            startActivity(new Intent(Toolbar_Activity.this, Semester4Activity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this app!");
        String appLink = "https://play.google.com/store/apps/details?id=" + getPackageName();
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey! Check out this cool app:\n" + appLink);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void logout() {
        Intent intent = new Intent(Toolbar_Activity.this, LoginActivity.class);
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