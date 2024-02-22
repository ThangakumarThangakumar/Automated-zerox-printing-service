package com.example.sreconlinexeroxautomation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintManager;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

class MainActivity7 extends AppCompatActivity {
    Button buttonPrint1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPrint1 = findViewById(R.id.buttonPrint1);
        buttonPrint1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle printing for Document 1
                printDocument("Document 1");
            }
        });
    }

    private void printDocument(String documentName) {
        // Get the PrintManager service
        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        if (printManager != null) {
            // Set the document name
            String jobName = getString(R.string.app_name) + " Document";

            // Start a print job
            printManager.print(jobName, new MyPrintDocumentAdapter(this), null);
        }
    }
}
