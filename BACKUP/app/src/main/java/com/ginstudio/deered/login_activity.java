package com.ginstudio.deered;

// Coded by Amerogin Kamid

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.os.Build;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.*;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class login_activity extends AppCompatActivity {

    ConstraintLayout logoCon;
    ConstraintLayout copyRight;
    ConstraintLayout rootLayout;

    TextView xsignup;
    TextView signuptext;
    CheckBox rememberme; // Quick note: Feature is disabled for now.

    // EditTexts
    EditText inputEmail;
    EditText inputPass;

    // Button
    Button loginButton;

    // String Patterns for Email or Password
    String emailPattern = "^[A-Za-z0-9._%+-]+@sksu\\.edu\\.ph$";

    // Progress Dialog
    ProgressDialog progressDialog;

    // Firebase Authentication
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    private void PerformAuthentication() {
        String iemailLog = inputEmail.getText().toString();
        String passwordLog = inputPass.getText().toString();

        if (!iemailLog.matches(emailPattern)) {
            inputEmail.setError("Enter a valid SKSU Institutional Email \n(e.g. firstname_lastname@sksu.edu.ph)");
        } else if (passwordLog.isEmpty()) {
            inputPass.setError("Please enter a password to log-in.");
        } else {
            progressDialog.setMessage("Logging in...");
            progressDialog.setTitle("Log In");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(iemailLog, passwordLog).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        sendUserToNextActivity();
                        Toast.makeText(login_activity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(login_activity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToNextActivity() {
        Intent intent = new Intent(login_activity.this, home_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Full-screens the Display
        Objects.requireNonNull(getSupportActionBar()).hide(); // Hides the ActionBar
        setContentView(R.layout.login_layout); // Runs the XML

        logoCon = findViewById(R.id.logoCon);
        copyRight = findViewById(R.id.copyRight);
        rootLayout = findViewById(R.id.backdd);

        xsignup = findViewById(R.id.xsignup);
        signuptext = findViewById(R.id.signuptext);

        rememberme = findViewById(R.id.rememberme);

        inputEmail = findViewById(R.id.inputEmail);
        inputPass = findViewById(R.id.inputPass);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        loginButton = findViewById(R.id.signupButton);

        // Set up a listener to detect when the keyboard is opened or closed
        View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
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
            }
        });

        // Original Color Sample (Gray)
        final int origColor = Color.parseColor("#89898A");
        final ColorStateList origColorStateList = ColorStateList.valueOf(origColor);

        // Red Color Sample - if errors
        final int redColor = Color.parseColor("#FF6347");
        final ColorStateList redColorStateList = ColorStateList.valueOf(redColor);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerformAuthentication();
            }
        });

        // Set up a listener to change the color and text of the login button on click
        loginButton.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {

                    // When Log In Button is Pressed
                    case MotionEvent.ACTION_DOWN:
                        loginButton.setTextColor(Color.BLACK);
                        loginButton.setBackgroundColor(Color.parseColor("#9AF8C9"));
                        loginButton.setText("Logging in...");

                        // If Intitutional Email is empty turn the backgroundTint and Hint red, if not then turn to original
                        if (!TextUtils.isEmpty(inputEmail.getText().toString())) {
                            inputEmail.setHintTextColor(origColor);
                            if (inputEmail instanceof AppCompatEditText) {
                                ((AppCompatEditText) inputEmail).setSupportBackgroundTintList(origColorStateList);
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                inputEmail.setBackgroundTintList(origColorStateList);
                            }
                        } else {
                            inputEmail.setHintTextColor(redColor);
                            if (inputEmail instanceof AppCompatEditText) {
                                ((AppCompatEditText) inputEmail).setSupportBackgroundTintList(redColorStateList);
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                inputEmail.setBackgroundTintList(redColorStateList);
                            }
                        }



                        // If Password is empty turn the backgroundTint and Hint red
                        if (!TextUtils.isEmpty(inputPass.getText().toString())) {
                            inputPass.setHintTextColor(origColor);
                            if (inputPass instanceof AppCompatEditText) {
                                ((AppCompatEditText) inputPass).setSupportBackgroundTintList(origColorStateList);
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                inputPass.setBackgroundTintList(origColorStateList);
                            }
                        } else {
                            inputPass.setHintTextColor(redColor);
                            if (inputPass instanceof AppCompatEditText) {
                                ((AppCompatEditText) inputPass).setSupportBackgroundTintList(redColorStateList);
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
            }
        });

        // This is to listen if cursor is visible in Intitutional Email EditText,
        // and if so.. then turn it back to original.
        inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    inputEmail.post(new Runnable() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void run() {
                            // Check if the text cursor is visible
                            if (inputEmail.isCursorVisible()) {
                                // Change the hint text color of inputEmail to #89898A
                                inputEmail.setHint("Institutional Email");
                                inputEmail.setHintTextColor(Color.parseColor("#89898A"));
                                if (inputEmail instanceof AppCompatEditText) {
                                    ((AppCompatEditText) inputEmail).setSupportBackgroundTintList(origColorStateList);
                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    inputEmail.setBackgroundTintList(origColorStateList);
                                }
                            }
                        }
                    });
                }

            }
        });

        // This is to listen if cursor is visible in Password EditText,
        // and if so.. then turn it back to original.
        inputPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    inputPass.post(new Runnable() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void run() {
                            // Check if the text cursor is visible
                            if (inputPass.isCursorVisible()) {
                                // Change the hint text color of inputEmail to #89898A
                                inputPass.setHint("Password");
                                inputPass.setHintTextColor(Color.parseColor("#89898A"));
                                if (inputPass instanceof AppCompatEditText) {
                                    ((AppCompatEditText) inputPass).setSupportBackgroundTintList(origColorStateList);
                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    inputPass.setBackgroundTintList(origColorStateList);
                                }
                            }
                        }
                    });
                }

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
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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

        // When xsignup & signuptext EditText is pressed go to signup_activity
        xsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login_activity.this, signup_activity.class));
            }
        });
        signuptext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login_activity.this, signup_activity.class));
            }
        });

        // CheckBox - Disabled feature for now
        rememberme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rememberme.setChecked(false);
            }
        });

        ImageView hiddeneye = findViewById(R.id.hiddeneye);

        hiddeneye.setVisibility(View.GONE);

        hiddeneye.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if (hiddeneye.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.showneye).getConstantState())) {
                    hiddeneye.setImageResource(R.drawable.hiddeneye);
                    inputPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    hiddeneye.setImageResource(R.drawable.showneye);
                    inputPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
            }
        });

        // Don't delete below (For safety purposes haha)
    }
}
