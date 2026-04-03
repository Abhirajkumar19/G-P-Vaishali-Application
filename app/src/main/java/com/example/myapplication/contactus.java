package com.example.myapplication;

import android.content.Intent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;

public class contactus extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private SessionManager sessionManager;

    // UI Elements
    private LinearLayout emailContainer, phoneContainer, websiteContainer, addressContainer;
    private TextView tvEmail, tvPhone, tvWebsite, tvAddress;
    private TextView btnCall, btnOpen, btnMap;
    private ImageView insta, fb, twitter, linked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);

        // Initialize Firebase and Session
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);

        // Initialize Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Contact Us");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Initialize Views
        initializeViews();

        // Setup Click Listeners
        setupClickListeners();
    }

    private void initializeViews() {
        // Contact containers
        emailContainer = findViewById(R.id.email_container);
        phoneContainer = findViewById(R.id.phone_container);
        websiteContainer = findViewById(R.id.website_container);
        addressContainer = findViewById(R.id.address_container);

        // TextViews
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvWebsite = findViewById(R.id.tvWebsite);
        tvAddress = findViewById(R.id.tvAddress);

        // Action buttons
        btnCall = findViewById(R.id.btnCall);
        btnOpen = findViewById(R.id.btnOpen);
        btnMap = findViewById(R.id.btnMap);

        // Social media icons
        insta = findViewById(R.id.insta);
        fb = findViewById(R.id.fb);
        twitter = findViewById(R.id.twitter);
        linked = findViewById(R.id.linked);
    }

    private void setupClickListeners() {
        // Email container click - Copy email
        emailContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard(tvEmail.getText().toString(), "Email copied to clipboard");
            }
        });

        // Phone container click - Show call button and copy option
        phoneContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle call button visibility
                if (btnCall.getVisibility() == View.GONE) {
                    btnCall.setVisibility(View.VISIBLE);
                } else {
                    btnCall.setVisibility(View.GONE);
                }
            }
        });

        // Call button click - Make phone call
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall(tvPhone.getText().toString());
            }
        });

        // Website container click - Show open button
        websiteContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnOpen.getVisibility() == View.GONE) {
                    btnOpen.setVisibility(View.VISIBLE);
                } else {
                    btnOpen.setVisibility(View.GONE);
                }
            }
        });

        // Open button click - Open website
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebsite(tvWebsite.getText().toString());
            }
        });

        // Address container click - Show map button
        addressContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnMap.getVisibility() == View.GONE) {
                    btnMap.setVisibility(View.VISIBLE);
                } else {
                    btnMap.setVisibility(View.GONE);
                }
            }
        });

        // Map button click - Open in Google Maps
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInGoogleMaps(tvAddress.getText().toString());
            }
        });

        // Social media clicks
        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSocialMedia("instagram", "https://www.instagram.com/gpv7.633official?igsh=MTZnYWpodTVjeXJpbw==");
            }
        });

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSocialMedia("facebook", "https://www.facebook.com/share/19oeRjzS7B/");
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSocialMedia("twitter", "https://x.com/dsttebihar?s=21");
            }
        });

        linked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSocialMedia("linkedin", "https://in.linkedin.com/company/dsttebihar");
            }
        });

        // Long press for copy options
        emailContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyToClipboard(tvEmail.getText().toString(), "Email copied");
                return true;
            }
        });

        phoneContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyToClipboard(tvPhone.getText().toString(), "Phone number copied");
                return true;
            }
        });

        websiteContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyToClipboard(tvWebsite.getText().toString(), "Website copied");
                return true;
            }
        });

        addressContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyToClipboard(tvAddress.getText().toString(), "Address copied");
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle back button click
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // This will call the default back button behavior
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void copyToClipboard(String text, String message) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void makePhoneCall(String phoneNumber) {
        // Remove any non-numeric characters except +
        String number = phoneNumber.replaceAll("[^0-9+]", "");
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    private void openWebsite(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void openInGoogleMaps(String address) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // If Google Maps is not installed, open in browser
            Uri webUri = Uri.parse("https://www.google.com/maps/search/" + Uri.encode(address));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
            startActivity(webIntent);
        }
    }

    private void openSocialMedia(String platform, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}