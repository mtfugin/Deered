package com.ginstudio.deered;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class home_activity extends AppCompatActivity {


    Button logoutButton;
    ProgressDialog progressDialog;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        logoutButton = findViewById(R.id.logoutButton1);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerformAuthentication();
            }
        });
    }



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

    private void sendUserToNextActivity () {
        Intent intent = new Intent(home_activity.this, login_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}