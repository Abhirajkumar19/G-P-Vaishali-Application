package com.example.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class Semester4Activity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PDFAdapter pdfAdapter;
    private Toolbar toolbar;
    private List<PDFModel> pdfList = new ArrayList<>();
    private SupabaseClient supabaseClient;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_semester4);

        supabaseClient = SupabaseClient.getInstance(this);

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

        // Load data only once
        loadNoticePDFs();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle back button click
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    private void loadNoticePDFs() {
        // Show progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Notice...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        supabaseClient.getPDFsBySemester("NOTICE", new SupabaseClient.PDFListCallback() {
            @Override
            public void onSuccess(List<PDFModel> pdfs) {
                runOnUiThread(() -> {
                    // Always check if dialog exists before dismissing
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    pdfList.clear();

                    for (PDFModel pdf : pdfs) {
                        if (pdf.getSubject().equalsIgnoreCase("NOTICE")) {
                            pdfList.add(pdf);
                        }
                    }

                    pdfAdapter.notifyDataSetChanged();

                    if (pdfList.isEmpty()) {
                        Toast.makeText(Semester4Activity.this,
                                "No Notice available", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    // Always check if dialog exists before dismissing
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                    Toast.makeText(Semester4Activity.this,
                            "Error: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up dialog
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}