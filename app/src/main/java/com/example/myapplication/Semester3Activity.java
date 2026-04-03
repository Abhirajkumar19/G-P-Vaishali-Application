package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class Semester3Activity extends AppCompatActivity {
    private CardView cardCs3, cardMe3, cardEc3, cardEe3, cardCv3, cardCtm3;
    private RecyclerView recyclerView;
    private PDFAdapter pdfAdapter;
    private Toolbar toolbar;
    private List<PDFModel> pdfList = new ArrayList<>();
    private SupabaseClient supabaseClient;
    private String currentSubject = "CS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester3);

        supabaseClient = SupabaseClient.getInstance(this);

        // Initialize views
        cardCs3 = findViewById(R.id.buttoncs3);
        cardMe3 = findViewById(R.id.buttonme3);
        cardEc3 = findViewById(R.id.buttonec3);
        cardEe3 = findViewById(R.id.buttonee3);
        cardCv3 = findViewById(R.id.buttoncv3);
        cardCtm3 = findViewById(R.id.buttonctm3);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pdfAdapter = new PDFAdapter(pdfList, false);
        recyclerView.setAdapter(pdfAdapter);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Contact Us");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Set click listeners
        setClickListeners();

    }

    private void setClickListeners() {
        cardCs3.setOnClickListener(v -> {
            currentSubject = "CS";
            loadPDFsBySubject("CS");
        });

        cardMe3.setOnClickListener(v -> {
            currentSubject = "ME";
            loadPDFsBySubject("ME");
        });

        cardEc3.setOnClickListener(v -> {
            currentSubject = "EC";
            loadPDFsBySubject("EC");
        });

        cardEe3.setOnClickListener(v -> {
            currentSubject = "EE";
            loadPDFsBySubject("EE");
        });

        cardCv3.setOnClickListener(v -> {
            currentSubject = "CV";
            loadPDFsBySubject("CV");
        });

        cardCtm3.setOnClickListener(v -> {
            currentSubject = "CTM";
            loadPDFsBySubject("CTM");
        });
    }

    private void loadPDFsBySubject(String subject) {
        Toast.makeText(this, "Loading " + subject + "...", Toast.LENGTH_SHORT).show();

        supabaseClient.getPDFsBySemester("3", new SupabaseClient.PDFListCallback() {
            @Override
            public void onSuccess(List<PDFModel> pdfs) {
                runOnUiThread(() -> {
                    pdfList.clear();
                    for (PDFModel pdf : pdfs) {
                        if (pdf.getSubject() != null && pdf.getSubject().equals(subject)) {
                            pdfList.add(pdf);
                        }
                    }
                    pdfAdapter.notifyDataSetChanged();

                    if (pdfList.isEmpty()) {
                        Toast.makeText(Semester3Activity.this,
                                "No content for " + subject, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        Toast.makeText(Semester3Activity.this,
                                "Error: " + error, Toast.LENGTH_SHORT).show()
                );
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
}