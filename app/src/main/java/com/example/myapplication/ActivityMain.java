package com.example.myapplication;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
public class ActivityMain extends AppCompatActivity {

    Button btnAddData, btnViewData, btnDeleteData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAddData = findViewById(R.id.btnAddData);
        btnViewData = findViewById(R.id.btnViewData);
        btnDeleteData = findViewById(R.id.btndeleteData);
        btnAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityMain.this,
                        "Add Data clicked", Toast.LENGTH_SHORT).show();
            }
        });
        btnViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityMain.this,
                        "View Data clicked", Toast.LENGTH_SHORT).show();
            }
        });
        btnDeleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityMain.this,
                        "Delete Data clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
