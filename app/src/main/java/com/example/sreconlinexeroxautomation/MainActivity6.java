package com.example.sreconlinexeroxautomation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity6 extends AppCompatActivity {
    private EditText edtCopiesBlackAndWhite, edtCopiesColor;
    private CheckBox checkboxBlackAndWhite, checkboxColor, checkboxSpiral, checkboxCaligo;
    private Button btnCalculate, btnUpload;
    private TextView tvTotalCost;
    private StorageReference storageRef;
    private DatabaseReference mDatabase;

    private static final int REQUEST_CODE_PICK_FILE = 1;
    private static final int REQUEST_CODE_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);
        FirebaseApp.initializeApp(this);
        storageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI components
        checkboxBlackAndWhite = findViewById(R.id.checkboxBlackAndWhite);
        edtCopiesBlackAndWhite = findViewById(R.id.edtCopiesBlackAndWhite);
        checkboxColor = findViewById(R.id.checkboxColor);
        edtCopiesColor = findViewById(R.id.edtCopiesColor);
        checkboxSpiral = findViewById(R.id.checkboxSpiral);
        checkboxCaligo = findViewById(R.id.checkboxCaligo);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvTotalCost = findViewById(R.id.tvTotalCost);
        btnUpload = findViewById(R.id.btnUploadFile);

        // Set click listener for Calculate button
        btnCalculate.setOnClickListener(view -> calculateTotalCost());

        // Set click listener for Upload button
        btnUpload.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(MainActivity6.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(MainActivity6.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            } else {
                // Permission is already granted, proceed with file selection
                pickFile();
            }
        });
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allow any file type to be selected
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_CODE_PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                // Upload the file to Firebase Realtime Database
                uploadFileToFirebase(fileUri);
            } else {
                Toast.makeText(this, "Failed to get file URI", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFileToFirebase(Uri fileUri) {
        // Push a new child node to the database with a unique key
        String uploadId = mDatabase.push().getKey();

        if (uploadId != null) {
            // Set the file URI as the value for the child node
            mDatabase.child(uploadId).setValue(fileUri.toString())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MainActivity6.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity6.this, "Failed to upload file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Failed to generate upload ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateTotalCost() {
        // Retrieve values from UI components
        int copiesBlackAndWhite = Integer.parseInt(edtCopiesBlackAndWhite.getText().toString());
        int copiesColor = Integer.parseInt(edtCopiesColor.getText().toString());

        // Initialize total cost with 0
        int totalCost = 0;

        // Check if Black and White checkbox is checked
        if (checkboxBlackAndWhite.isChecked()) {
            // Calculate cost and add to total
            totalCost += copiesBlackAndWhite * 1; // Rs. 1 per page
        }

        // Check if Color checkbox is checked
        if (checkboxColor.isChecked()) {
            // Calculate cost and add to total
            totalCost += copiesColor * 10; // Rs. 10 per page
        }

        // Check if Spiral Binding checkbox is checked
        if (checkboxSpiral.isChecked()) {
            // Add cost to total
            totalCost += 40; // Rs. 40
        }

        // Check if Caligo Binding checkbox is checked
        if (checkboxCaligo.isChecked()) {
            // Add cost to total
            totalCost += 60; // Rs. 60
        }

        // Update the TextView with the result
        tvTotalCost.setText("Total Cost: Rs. " + totalCost);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with file selection
                pickFile();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
