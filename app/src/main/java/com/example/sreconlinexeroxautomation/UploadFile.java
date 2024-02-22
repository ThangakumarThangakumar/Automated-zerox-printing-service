package com.example.sreconlinexeroxautomation;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class UploadFile extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;

    private Button mButtonChooseFile;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private EditText mEditTextFileName;
    private ProgressBar mProgressBar;

    private Uri mFileUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;

    private EditText edtCopiesBlackAndWhite, edtCopiesColor;
    private CheckBox checkboxBlackAndWhite, checkboxColor, checkboxSpiral, checkboxCaligo;
    private Button btnCalculate, btnUpload;
    private TextView tvTotalCost;
    private StorageReference storageRef;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);

        mButtonChooseFile = findViewById(R.id.button_choose_file);
        mButtonUpload = findViewById(R.id.button_upload);
        mTextViewShowUploads = findViewById(R.id.text_view_show_uploads);
        mEditTextFileName = findViewById(R.id.edit_text_file_name);
        mProgressBar = findViewById(R.id.progress_bar);

        checkboxBlackAndWhite = findViewById(R.id.checkboxBlackAndWhite);
        edtCopiesBlackAndWhite = findViewById(R.id.edtCopiesBlackAndWhite);
        checkboxColor = findViewById(R.id.checkboxColor);
        edtCopiesColor = findViewById(R.id.edtCopiesColor);
        checkboxSpiral = findViewById(R.id.checkboxSpiral);
        checkboxCaligo = findViewById(R.id.checkboxCaligo);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvTotalCost = findViewById(R.id.tvTotalCost);
        btnUpload = findViewById(R.id.btnUploadFile);

        btnCalculate.setOnClickListener(view -> calculateTotalCost());

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mButtonChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(UploadFile.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("*/*");  // Allow any file type
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mFileUri = data.getData();
            mEditTextFileName.setText(getFileName(mFileUri));
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment(); // Fallback to last path segment if DISPLAY_NAME column is not available
        }
        return result;
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mFileUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mFileUri));

            mUploadTask = fileReference.putFile(mFileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(UploadFile.this, "Upload successful", Toast.LENGTH_LONG).show();

                            // Get the download URL from the task snapshot
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    Upload upload = new Upload(mEditTextFileName.getText().toString().trim(), downloadUrl);
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadFile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}
