package com.ginstudio.deered;

// Coded by Amerogin Kamid

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.text.*;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicReference;

public class login_activity extends AppCompatActivity {

    // Constraint Layout(s):
    ConstraintLayout logoCon, copyRight, rootLayout;

    // Text View(s):
    TextView xsignup, signuptext;

    // Checkbox(es):
    CheckBox rememberme;

    // EditText(s):
    EditText inputEmail;
    EditText inputPass;

    // Button(s):
    Button loginButton;

    // String Patterns for Email or Password
    String emailPattern = "^[A-Za-z\\d._%+-]+@sksu\\.edu\\.ph$";

    // Progress Dialog
    ProgressDialog progressDialog;

    // Firebase Authentication(s)
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    // Firebase & Firestore Database connection
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void PerformAuthentication() {
        String emailLog = inputEmail.getText().toString();
        String passwordLog = inputPass.getText().toString();

        if (!emailLog.matches(emailPattern)) {
            inputEmail.setError("Enter a valid SKSU Institutional Email " +
                    "\n(e.g. firstname_lastname@sksu.edu.ph)");
        }
        if (passwordLog.isEmpty()) {
            inputPass.setError("Please enter a password to log-in.");
        }

        if (!emailLog.matches(emailPattern)) {
            inputEmail.setError("Enter a valid SKSU Institutional Email " +
                    "\n(e.g. firstname_lastname@sksu.edu.ph)");
        } else if (passwordLog.isEmpty()) {
            inputPass.setError("Please enter a password to log-in.");
        } else {
            progressDialog.setMessage("Logging in...");
            progressDialog.setTitle("Log In");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();



                mAuth.signInWithEmailAndPassword(emailLog, passwordLog).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AtomicReference<String> getIdentify = new AtomicReference<>();
                        db.collection("user").document(emailLog)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        // Get the identity
                                        String identity = documentSnapshot.getString("Identifier");
                                        if (identity != null) {
                                            if (identity.equals("Student")) {
                                                sendUserToNextActivityStudent();
                                            } else if (identity.equals("Instructor")) {
                                                sendUserToNextActivityInstructor();
                                            } else {
                                                Toast.makeText(login_activity.this, "Invalid user identity: " + identity, Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(login_activity.this, "User identity not found", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // User document not found
                                        Toast.makeText(login_activity.this, "There was an error getting user info!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        progressDialog.dismiss();
                        Toast.makeText(login_activity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(login_activity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });

        }
    }

    private void sendUserToNextActivityStudent() {
        Intent intent = new Intent(login_activity.this, homestudent_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendUserToNextActivityInstructor() {
        Intent intent = new Intent(login_activity.this, homeinstructor_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId", "RestrictedApi", "SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full-screens the Display
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hides the ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Runs the XML
        setContentView(R.layout.login_layout);

        // ID Class-object link
        logoCon = findViewById(R.id.logoCon);
        copyRight = findViewById(R.id.copyRight);
        rootLayout = findViewById(R.id.backdd);
        xsignup = findViewById(R.id.xsignup);
        signuptext = findViewById(R.id.signuptext);
        rememberme = findViewById(R.id.rememberme);
        inputEmail = findViewById(R.id.inputEmail);
        inputPass = findViewById(R.id.inputPass);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        loginButton = findViewById(R.id.signupButton);

        // This is a constructor, to create a new ProgressDialog object
        // Well, I tried to remove this but, there were nothing happened
        progressDialog = new ProgressDialog(this);

        // Set up a listener to detect when the keyboard is opened or closed
        View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
            if (heightDiff > 200) { // arbitrary value, adjust as needed
                // Keyboard is open, hide the elements
                logoCon.setVisibility(View.GONE);
                copyRight.setVisibility(View.GONE);
                rootLayout.setBackgroundColor(Color.WHITE);

            } else {
                // Keyboard is closed, show the elements
                logoCon.setVisibility(View.VISIBLE);
                copyRight.setVisibility(View.VISIBLE);
                rootLayout.setBackgroundResource(R.drawable.backdrop);
            }
        });

        // Original Color Sample (Gray)
        final int origColor = Color.parseColor("#89898A");
        final ColorStateList origColorStateList = ColorStateList.valueOf(origColor);

        // Red Color Sample - if errors
        final int redColor = Color.parseColor("#FF6347");
        final ColorStateList redColorStateList = ColorStateList.valueOf(redColor);

        loginButton.setOnClickListener(view -> PerformAuthentication());

        // Set up a listener to change the color and text of the login button on click
        loginButton.setOnTouchListener((v, event) -> {
            switch(event.getAction()) {

                // When Log In Button is Pressed
                case MotionEvent.ACTION_DOWN:
                    loginButton.setTextColor(Color.BLACK);
                    loginButton.setBackgroundColor(Color.parseColor("#9AF8C9"));
                    loginButton.setText("Logging in...");

                    // If Institutional Email is empty turn the backgroundTint and Hint red, if not then turn to original
                    if (!TextUtils.isEmpty(inputEmail.getText().toString())) {
                        inputEmail.setHintTextColor(origColor);
                        if (inputEmail instanceof AppCompatEditText) {
                            ((AppCompatEditText) inputEmail).setSupportBackgroundTintList(origColorStateList);
                        } else {
                            inputEmail.setBackgroundTintList(origColorStateList);
                        }
                    } else {
                        inputEmail.setHintTextColor(redColor);
                        if (inputEmail instanceof AppCompatEditText) {
                            ((AppCompatEditText) inputEmail).setSupportBackgroundTintList(redColorStateList);
                        } else {
                            inputEmail.setBackgroundTintList(redColorStateList);
                        }
                    }



                    // If Password is empty turn the backgroundTint and Hint red
                    if (!TextUtils.isEmpty(inputPass.getText().toString())) {
                        inputPass.setHintTextColor(origColor);
                        if (inputPass instanceof AppCompatEditText) {
                            ((AppCompatEditText) inputPass).setSupportBackgroundTintList(origColorStateList);
                        } else {
                            inputPass.setBackgroundTintList(origColorStateList);
                        }
                    } else {
                        inputPass.setHintTextColor(redColor);
                        if (inputPass instanceof AppCompatEditText) {
                            ((AppCompatEditText) inputPass).setSupportBackgroundTintList(redColorStateList);
                        } else {
                            inputPass.setBackgroundTintList(redColorStateList);
                        }
                    }
                    break;

                // When Log In Button is Released
                case MotionEvent.ACTION_UP:
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    loginButton.setTextColor(Color.WHITE);
                    loginButton.setBackgroundColor(Color.BLACK);
                    loginButton.setText("Login");
                    break;
            }
            return false;
        });

        // This is to listen if cursor is visible in Institutional Email EditText,
        // and if so.. then turn it back to original.
        inputEmail.setOnFocusChangeListener((v, hasFocus) -> {

            if (hasFocus) {
                inputEmail.post(() -> {
                    // Check if the text cursor is visible
                    if (inputEmail.isCursorVisible()) {
                        // Change the hint text color of inputEmail to #89898A
                        inputEmail.setHint("Institutional Email");
                        inputEmail.setHintTextColor(Color.parseColor("#89898A"));
                        if (inputEmail instanceof AppCompatEditText) {
                            ((AppCompatEditText) inputEmail).setSupportBackgroundTintList(origColorStateList);
                        } else {
                            inputEmail.setBackgroundTintList(origColorStateList);
                        }
                    }
                });
            }

        });

        // This is to listen if cursor is visible in Password EditText,
        // and if so.. then turn it back to original.
        inputPass.setOnFocusChangeListener((v, hasFocus) -> {

            if (hasFocus) {
                inputPass.post(() -> {
                    // Check if the text cursor is visible
                    if (inputPass.isCursorVisible()) {
                        // Change the hint text color of inputEmail to #89898A
                        inputPass.setHint("Password");
                        inputPass.setHintTextColor(Color.parseColor("#89898A"));
                        if (inputPass instanceof AppCompatEditText) {
                            ((AppCompatEditText) inputPass).setSupportBackgroundTintList(origColorStateList);
                        } else {
                            inputPass.setBackgroundTintList(origColorStateList);
                        }
                    }
                });
            }

        });

        // if Institutional Email text changes, turn the backgroundTint to original
        inputEmail.addTextChangedListener(new TextWatcher() {
            @SuppressLint("RestrictedApi")
            @Override
            public void afterTextChanged(Editable s) {
                // Check if the text cursor is visible
                if (inputEmail.isCursorVisible()) {
                    // Change the hint text color of inputEmail to #89898A
                    inputEmail.setHint("Institutional Email");
                    inputEmail.setHintTextColor(Color.parseColor("#89898A"));
                    if (inputEmail instanceof AppCompatEditText) {
                        ((AppCompatEditText) inputEmail).setSupportBackgroundTintList(origColorStateList);
                    } else {
                        inputEmail.setBackgroundTintList(origColorStateList);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Intended empty (Don't remove for to avoid error.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Intended empty (Don't remove for to avoid error.
            }
        });

        // if Password text changes, turn the backgroundTint to original
        inputPass.addTextChangedListener(new TextWatcher() {
            @SuppressLint("RestrictedApi")
            @Override
            public void afterTextChanged(Editable s) {
                // Check if the text cursor is visible
                if (inputPass.isCursorVisible()) {
                    // Change the hint text color of inputEmail to #89898A
                    inputPass.setHint("Password");
                    inputPass.setHintTextColor(Color.parseColor("#89898A"));
                    if (inputPass instanceof AppCompatEditText) {
                        ((AppCompatEditText) inputPass).setSupportBackgroundTintList(origColorStateList);
                    } else {
                        inputPass.setBackgroundTintList(origColorStateList);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Intended empty (Don't remove for to avoid error.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Intended empty (Don't remove for to avoid error.
            }
        });

        // This function as activity switcher when "Don't have an account? Sign Up" is tapped.
        xsignup.setOnClickListener(v -> startActivity(new Intent(login_activity.this, signup_activity.class)));
        signuptext.setOnClickListener(v -> startActivity(new Intent(login_activity.this, signup_activity.class)));

        // Don't delete below (For safety purposes haha)
    }
}
