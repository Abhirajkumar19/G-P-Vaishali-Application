package com.example.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PDFViewActivity extends AppCompatActivity {
    private PDFView pdfView;
    private TextView titleTextView;
    private String pdfUrl;
    private String pdfTitle;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private File downloadedFile;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        pdfView = findViewById(R.id.pdfView);
        titleTextView = findViewById(R.id.titleTextView);

        pdfUrl = getIntent().getStringExtra("pdf_url");
        pdfTitle = getIntent().getStringExtra("pdf_title");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Contact Us");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        titleTextView.setText(pdfTitle != null ? pdfTitle : "PDF Document");

        // Show progress dialog and download
        showProgressDialog("Loading PDF...");
        downloadAndViewPDF(pdfUrl);
    }

    private void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void downloadAndViewPDF(String urlString) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            FileOutputStream outputStream = null;

            try {
                downloadedFile = new File(getCacheDir(), "temp_" + System.currentTimeMillis() + ".pdf");

                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(30000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = new BufferedInputStream(connection.getInputStream(), 8192);
                    outputStream = new FileOutputStream(downloadedFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.flush();

                    mainHandler.post(() -> {
                        dismissProgressDialog();
                        viewDownloadedPDF();
                    });

                } else {
                    mainHandler.post(() -> {
                        dismissProgressDialog();
                        Toast.makeText(PDFViewActivity.this,
                                "Download failed: Server error " + responseCode,
                                Toast.LENGTH_LONG).show();
                        finish();
                    });
                }
            } catch (Exception e) {
                mainHandler.post(() -> {
                    dismissProgressDialog();
                    Toast.makeText(PDFViewActivity.this,
                            "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                    if (outputStream != null) outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (connection != null) connection.disconnect();
            }
        }).start();
    }

    private void viewDownloadedPDF() {
        if (downloadedFile != null && downloadedFile.exists()) {
            try {
                pdfView.fromFile(downloadedFile)
                        .defaultPage(1)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .enableAnnotationRendering(true)
                        .scrollHandle(new DefaultScrollHandle(this))
                        .spacing(10)
                        .onError(e -> {
                            Toast.makeText(PDFViewActivity.this,
                                    "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            finish();
                        })
                        .load();
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Content file not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
        if (downloadedFile != null && downloadedFile.exists()) {
            downloadedFile.delete();
        }
        if (pdfView != null) {
            pdfView.recycle();
        }
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