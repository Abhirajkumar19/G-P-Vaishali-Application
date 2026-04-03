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

public class Semester1Activity extends AppCompatActivity {
    private CardView cardCs1, cardMe1, cardEc1, cardEe1, cardCv1, cardCtm1;
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
        setContentView(R.layout.activity_semester1);

        supabaseClient = SupabaseClient.getInstance(this);

        // Initialize views
        cardCs1 = findViewById(R.id.buttoncs1);
        cardMe1 = findViewById(R.id.buttonme1);
        cardEc1 = findViewById(R.id.buttonec1);
        cardEe1 = findViewById(R.id.buttonee1);
        cardCv1 = findViewById(R.id.buttoncv1);
        cardCtm1 = findViewById(R.id.buttonctm1);

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
        cardCs1.setOnClickListener(v -> {
            currentSubject = "CS";
            loadPDFsBySubject("CS");
        });

        cardMe1.setOnClickListener(v -> {
            currentSubject = "ME";
            loadPDFsBySubject("ME");
        });

        cardEc1.setOnClickListener(v -> {
            currentSubject = "EC";
            loadPDFsBySubject("EC");
        });

        cardEe1.setOnClickListener(v -> {
            currentSubject = "EE";
            loadPDFsBySubject("EE");
        });

        cardCv1.setOnClickListener(v -> {
            currentSubject = "CV";
            loadPDFsBySubject("CV");
        });

        cardCtm1.setOnClickListener(v -> {
            currentSubject = "CTM";
            loadPDFsBySubject("CTM");
        });
    }

    private void loadPDFsBySubject(String subject) {
        Toast.makeText(this, "Loading " + subject + "...", Toast.LENGTH_SHORT).show();

        supabaseClient.getPDFsBySemester("1", new SupabaseClient.PDFListCallback() {
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
                        Toast.makeText(Semester1Activity.this,
                                "No content for " + subject, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        Toast.makeText(Semester1Activity.this,
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