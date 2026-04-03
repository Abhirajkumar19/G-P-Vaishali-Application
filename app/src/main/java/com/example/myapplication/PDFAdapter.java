package com.example.myapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PDFAdapter extends RecyclerView.Adapter<PDFAdapter.PDFViewHolder> {
    private List<PDFModel> pdfList;
    private boolean isAdminMode;
    private SupabaseClient supabaseClient;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(PDFModel pdf);
        void onItemClick(PDFModel pdf);
    }

    public PDFAdapter(List<PDFModel> pdfList, boolean isAdminMode) {
        this.pdfList = pdfList;
        this.isAdminMode = isAdminMode;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PDFViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pdf, parent, false);
        return new PDFViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PDFViewHolder holder, int position) {
        PDFModel pdf = pdfList.get(position);

        holder.titleTextView.setText(pdf.getTitle());
        holder.subjectTextView.setText("Subject: " + pdf.getSubject());

        // Format date properly
        String date = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            date = sdf.format(new Date(pdf.getUploadedAt()));
        } catch (Exception e) {
            date = "Unknown date";
        }
        holder.dateTextView.setText("Uploaded: " + date);

        // Handle item click for viewing PDF
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(pdf);
            } else {
                // Fallback to original behavior if listener not set
                Intent intent = new Intent(v.getContext(), PDFViewActivity.class);
                intent.putExtra("pdf_url", pdf.getFileUrl());
                intent.putExtra("pdf_title", pdf.getTitle());
                v.getContext().startActivity(intent);
            }
        });

        // Handle admin mode with delete button
        if (isAdminMode) {
            holder.deleteButton.setVisibility(View.VISIBLE);

            holder.deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(pdf);
                } else {
                    // Fallback to original behavior if listener not set
                    supabaseClient = SupabaseClient.getInstance(v.getContext());
                    supabaseClient.deletePDF(pdf.getId(), pdf.getFileName(),
                            new SupabaseClient.DeleteCallback() {
                                @Override
                                public void onSuccess() {
                                    // Remove from list and notify adapter
                                    int currentPosition = holder.getAdapterPosition();
                                    if (currentPosition != RecyclerView.NO_POSITION) {
                                        pdfList.remove(currentPosition);
                                        notifyItemRemoved(currentPosition);
                                        notifyItemRangeChanged(currentPosition, pdfList.size());

                                        Toast.makeText(v.getContext(),
                                                "PDF deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(String error) {
                                    Toast.makeText(v.getContext(),
                                            "Error deleting PDF: " + error, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return pdfList.size();
    }

    // Method to update the list (useful for search/filter)
    public void updateList(List<PDFModel> newList) {
        pdfList = newList;
        notifyDataSetChanged();
    }

    static class PDFViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, subjectTextView, dateTextView;
        ImageButton deleteButton;

        PDFViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.pdfTitle);
            subjectTextView = itemView.findViewById(R.id.pdfSubject);
            dateTextView = itemView.findViewById(R.id.pdfDate);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
