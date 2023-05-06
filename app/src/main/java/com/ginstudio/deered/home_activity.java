package com.ginstudio.deered;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;

public class home_activity extends AppCompatActivity {
    Button logoutButton;
    ProgressDialog progressDialog;
    TextView userNameDisplay, fullNameDisplay, institutionalEmailDisplay;
    ConstraintLayout constraint_White;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Home Layout XML link
        setContentView(R.layout.home_layout);

        // Full-screens the Display
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hides the ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // ID Class-object link
        logoutButton = findViewById(R.id.logoutButton1);
        userNameDisplay = findViewById(R.id.userNameDisplay);
        fullNameDisplay = findViewById(R.id.fullNameDisplay);
        institutionalEmailDisplay = findViewById(R.id.institutionalEmailDisplay);
        constraint_White = findViewById(R.id.constraintwhite);

        // Firebase & Firestore Database connection
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // To fetch and store the logged in user email for future purposes
        String userEmail = Objects.requireNonNull(auth.getCurrentUser()).getEmail();

        // To predefine that userEmail shouldn't be null
        assert userEmail != null;

        // Predefined colors for color state list
        final int blkColor = Color.parseColor("#000000");
        final ColorStateList blkColorStateList = ColorStateList.valueOf(blkColor);
        final int whiteColor = Color.parseColor("#FFFFFF");
        final ColorStateList whiteColorStateList = ColorStateList.valueOf(whiteColor);

        // To tell user that the his/her userdata is currently fetching from the Firebase server
        constraint_White.setBackgroundTintList(blkColorStateList);
        progressDialog = new ProgressDialog(home_activity.this);
        progressDialog.setMessage("This might not take long as long as you are connected to a reliable internet.");
        progressDialog.setTitle("Fetching Data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // To match email to the email Document ID from Firebase to fetch the data
        // If it exists, get data by getString, else Perform the Log Out authentication with error notification
        db.collection("user").document(userEmail)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        constraint_White.setBackgroundTintList(whiteColorStateList);
                        progressDialog.dismiss();
                        // User document found, do something with it
                        String fullName = documentSnapshot.getString("Full Name");
                        String username = documentSnapshot.getString("Username");
                        String institutional_email = documentSnapshot.getString("Institutional Email");
                        userNameDisplay.setText(username);
                        fullNameDisplay.setText(fullName);
                        institutionalEmailDisplay.setText(institutional_email);
                        Toast.makeText(home_activity.this, "Data retrieved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        // User document not found
                        Toast.makeText(home_activity.this, "There was an error getting user info!", Toast.LENGTH_SHORT).show();
                        PerformAuthentication();
                    }
                })
                .addOnFailureListener(e -> {
                    PerformAuthentication();
                    Toast.makeText(home_activity.this, "There was an unexpected error!", Toast.LENGTH_SHORT).show();
                });

        logoutButton.setOnClickListener(view -> PerformAuthentication());
    }

    // PerformAuthentication -> Log Out
    private void PerformAuthentication() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging you out...");
        progressDialog.setTitle("Log Out");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        try {
            mAuth.signOut();
            progressDialog.dismiss();
            sendUserToNextActivity();
            Toast.makeText(home_activity.this, "Successfully logged out!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(home_activity.this, "Failed logging out.", Toast.LENGTH_SHORT).show();
        }
    }

    // Switch to Log In Activity Page after logging out
    private void sendUserToNextActivity () {
        Intent intent = new Intent(home_activity.this, login_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}