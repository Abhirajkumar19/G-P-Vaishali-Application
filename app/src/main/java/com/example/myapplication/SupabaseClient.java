package com.example.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SupabaseClient {
    private static final String SUPABASE_URL = "https://lqyktyczmrisiavnioqd.supabase.co";
    private static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxxeWt0eWN6bXJpc2lhdm5pb3FkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzE0MjQzODYsImV4cCI6MjA4NzAwMDM4Nn0.kw0BRYppUjx6_wDplAs8N3KcgsXucJ2LNp2LlQOfELg";
    private static final String BUCKET_NAME = "pdfs";
    private static final String TABLE_NAME = "pdfs";

    private static SupabaseClient instance;
    private Context context;

    private SupabaseClient(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized SupabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new SupabaseClient(context);
        }
        return instance;
    }

    // Upload PDF using REST API
    public void uploadPDF(File pdfFile, String title, String subject,
                          String semester, UploadCallback callback) {
        new UploadPDFTask(callback).execute(pdfFile, title, subject, semester);
    }

    private class UploadPDFTask extends AsyncTask<Object, Void, String> {
        private UploadCallback callback;
        private String errorMessage;

        UploadPDFTask(UploadCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Object... params) {
            HttpURLConnection connection = null;
            try {
                File pdfFile = (File) params[0];
                String title = (String) params[1];
                String subject = (String) params[2];
                String semester = (String) params[3];

                String fileName = UUID.randomUUID().toString() + "_" + pdfFile.getName();

                // Step 1: Upload file to storage
                String fileUrl = uploadFileToStorage(pdfFile, fileName);
                if (fileUrl == null) {
                    throw new Exception("Failed to upload file to storage");
                }

                // Step 2: Insert record into database
                JSONObject record = new JSONObject();
                record.put("title", title);
                record.put("subject", subject);
                record.put("semester", semester);
                record.put("file_url", fileUrl);
                record.put("file_name", fileName);
                record.put("uploaded_at", System.currentTimeMillis());

                insertRecord(record);

                return "success";
            } catch (Exception e) {
                errorMessage = e.getMessage();
                Log.e("UploadPDFTask", "Error uploading PDF", e);
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        private String uploadFileToStorage(File file, String fileName) throws Exception {
            String boundary = "Boundary-" + UUID.randomUUID().toString();
            String lineEnd = "\r\n";
            String twoHyphens = "--";

            URL url = new URL(SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + fileName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            // Set headers
            connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_ANON_KEY);
            connection.setRequestProperty("apikey", SUPABASE_ANON_KEY);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            // Write file data
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

            // Start boundary
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: application/pdf" + lineEnd);
            outputStream.writeBytes(lineEnd);

            // Write file content
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            fileInputStream.close();

            // End boundary
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            outputStream.flush();
            outputStream.close();

            // Check response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 201) {
                // Return public URL
                return SUPABASE_URL + "/storage/v1/object/public/" + BUCKET_NAME + "/" + fileName;
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                throw new Exception("Upload failed: " + response.toString());
            }
        }

        private void insertRecord(JSONObject record) throws Exception {
            URL url = new URL(SUPABASE_URL + "/rest/v1/" + TABLE_NAME);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_ANON_KEY);
            connection.setRequestProperty("apikey", SUPABASE_ANON_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Prefer", "return=minimal");
            connection.setDoOutput(true);

            // Write JSON data
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(record.toString());
            outputStream.flush();
            outputStream.close();

            // Check response
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_CREATED && responseCode != 201) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                throw new Exception("Insert failed: " + response.toString());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && callback != null) {
                callback.onSuccess();
            } else if (callback != null) {
                callback.onError(errorMessage != null ? errorMessage : "Unknown error");
            }
        }
    }

    // Get PDFs by semester
    public void getPDFsBySemester(String semester, PDFListCallback callback) {
        new GetPDFsTask(callback).execute(semester);
    }

    private class GetPDFsTask extends AsyncTask<String, Void, List<PDFModel>> {
        private PDFListCallback callback;
        private String errorMessage;

        GetPDFsTask(PDFListCallback callback) {
            this.callback = callback;
        }

        @Override
        protected List<PDFModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                String semester = params[0];

                URL url = new URL(SUPABASE_URL + "/rest/v1/" + TABLE_NAME + "?semester=eq." + semester + "&order=uploaded_at.desc");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_ANON_KEY);
                connection.setRequestProperty("apikey", SUPABASE_ANON_KEY);
                connection.setRequestProperty("Content-Type", "application/json");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Parse JSON array
                    JSONArray jsonArray = new JSONArray(response.toString());
                    List<PDFModel> pdfs = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        PDFModel pdf = new PDFModel();
                        pdf.setId(obj.getString("id"));
                        pdf.setTitle(obj.getString("title"));
                        pdf.setSubject(obj.getString("subject"));
                        pdf.setSemester(obj.getString("semester"));
                        pdf.setFileUrl(obj.getString("file_url"));
                        pdf.setFileName(obj.getString("file_name"));
                        pdf.setUploadedAt(obj.getLong("uploaded_at"));
                        pdfs.add(pdf);
                    }

                    return pdfs;
                }
            } catch (Exception e) {
                errorMessage = e.getMessage();
                Log.e("GetPDFsTask", "Error getting PDFs", e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<PDFModel> pdfs) {
            if (callback != null) {
                callback.onSuccess(pdfs != null ? pdfs : new ArrayList<>());
            }
        }
    }

    // Delete PDF
    public void deletePDF(String pdfId, String fileName, DeleteCallback callback) {
        new DeletePDFTask(callback).execute(pdfId, fileName);
    }

    private class DeletePDFTask extends AsyncTask<String, Void, Boolean> {
        private DeleteCallback callback;
        private String errorMessage;

        DeletePDFTask(DeleteCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                String pdfId = params[0];
                String fileName = params[1];

                // Step 1: Delete from storage
                URL storageUrl = new URL(SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + fileName);
                connection = (HttpURLConnection) storageUrl.openConnection();
                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_ANON_KEY);
                connection.setRequestProperty("apikey", SUPABASE_ANON_KEY);

                int storageResponse = connection.getResponseCode();
                connection.disconnect();

                // Step 2: Delete from database
                URL dbUrl = new URL(SUPABASE_URL + "/rest/v1/" + TABLE_NAME + "?id=eq." + pdfId);
                connection = (HttpURLConnection) dbUrl.openConnection();
                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_ANON_KEY);
                connection.setRequestProperty("apikey", SUPABASE_ANON_KEY);

                int dbResponse = connection.getResponseCode();

                return (storageResponse == HttpURLConnection.HTTP_OK || storageResponse == 200) &&
                        (dbResponse == HttpURLConnection.HTTP_OK || dbResponse == 204);

            } catch (Exception e) {
                errorMessage = e.getMessage();
                Log.e("DeletePDFTask", "Error deleting PDF", e);
                return false;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (callback != null) {
                if (success) {
                    callback.onSuccess();
                } else {
                    callback.onError(errorMessage != null ? errorMessage : "Delete failed");
                }
            }
        }
    }

    // Callback interfaces
    public interface UploadCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface PDFListCallback {
        void onSuccess(List<PDFModel> pdfs);
        void onError(String error);
    }

    public interface DeleteCallback {
        void onSuccess();
        void onError(String error);
    }
}