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

public class Semester2Activity extends AppCompatActivity {
    private CardView cardCs2, cardMe2, cardEc2, cardEe2, cardCv2, cardCtm2;
    private RecyclerView recyclerView;
    private PDFAdapter pdfAdapter;
    private List<PDFModel> pdfList = new ArrayList<>();
    private SupabaseClient supabaseClient;
    private Toolbar toolbar;
    private String currentSubject = "CS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_semester2);

        supabaseClient = SupabaseClient.getInstance(this);

        // Initialize views
        cardCs2 = findViewById(R.id.buttoncs2);
        cardMe2 = findViewById(R.id.buttonme2);
        cardEc2 = findViewById(R.id.buttonec2);
        cardEe2 = findViewById(R.id.buttonee2);
        cardCv2 = findViewById(R.id.buttoncv2);
        cardCtm2 = findViewById(R.id.buttonctm2);

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
        cardCs2.setOnClickListener(v -> {
            currentSubject = "CS";
            loadPDFsBySubject("CS");
        });

        cardMe2.setOnClickListener(v -> {
            currentSubject = "ME";
            loadPDFsBySubject("ME");
        });

        cardEc2.setOnClickListener(v -> {
            currentSubject = "EC";
            loadPDFsBySubject("EC");
        });

        cardEe2.setOnClickListener(v -> {
            currentSubject = "EE";
            loadPDFsBySubject("EE");
        });

        cardCv2.setOnClickListener(v -> {
            currentSubject = "CV";
            loadPDFsBySubject("CV");
        });

        cardCtm2.setOnClickListener(v -> {
            currentSubject = "CTM";
            loadPDFsBySubject("CTM");
        });
    }

    private void loadPDFsBySubject(String subject) {
        Toast.makeText(this, "Loading " + subject + "...", Toast.LENGTH_SHORT).show();

        supabaseClient.getPDFsBySemester("2", new SupabaseClient.PDFListCallback() {
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
                        Toast.makeText(Semester2Activity.this,
                                "No content for " + subject, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() ->
                        Toast.makeText(Semester2Activity.this,
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