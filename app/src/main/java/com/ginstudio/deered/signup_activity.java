package com.ginstudio.deered;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Map;

public class signup_activity extends AppCompatActivity {

    ConstraintLayout logoCon, copyRight, rootLayout;

    // Go to Log In Activity
    TextView xlogin, logintext;

    // CheckBoxes
    CheckBox student, instructor;

    // EditTexts

    EditText inputEmailS, inputFullname, inputUsername, inputPassS, inputConfirmPass;

    // Button
    Button signupButton;

    // String Patterns for Email or Password
    String emailPattern = "^[A-Za-z\\d._%+-]+@sksu\\.edu\\.ph$";

    // Progress Dialog
    ProgressDialog progressDialog;

    // Firebase Authentication
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    // Firestore Database
    FirebaseFirestore db;

    // Original Color Sample (Gray)
    final int origColor = Color.parseColor("#89898A");
    final ColorStateList origColorStateList = ColorStateList.valueOf(origColor);

    // Red Color Sample - if errors
    final int redColor = Color.parseColor("#FF6347");
    final ColorStateList redColorStateList = ColorStateList.valueOf(redColor);

    // For Signing Up Authentication
    private void PerformAuthentication() {
        String iemailSign = inputEmailS.getText().toString();
        String fullName = inputFullname.getText().toString();
        String userName = inputUsername.getText().toString();
        String passwordSign = inputPassS.getText().toString();
        String conpasswordSign = inputConfirmPass.getText().toString();

        if (passwordSign.isEmpty()) {
            inputPassS.setError("Please create a password.");
        } else if (passwordSign.length() < 6) {
            inputPassS.setError("Password must be at least 6 characters.");
        }
        if (conpasswordSign.isEmpty()) {
            inputConfirmPass.setError("Retype your created password here.");
        }
        if (fullName.isEmpty()) {
            inputFullname.setError("Enter a valid full name \n(e.g. John Juan Valenzuela Dela Cruz)");
        }
        if (userName.isEmpty()) {
            inputUsername.setError("Please create a username.");
        }
        if (!conpasswordSign.equals(passwordSign)) {
            inputConfirmPass.setError("Password does not match.");}

        if (!student.isChecked() && !instructor.isChecked()) {
            // If checkbox is not checked turn the text and box Red
            student.setTextColor(redColor);
            student.setButtonTintList(redColorStateList);
            instructor.setTextColor(redColor);
            instructor.setButtonTintList(redColorStateList);
        } else {
            // Set backs the Checkbox back to color state
            student.setTextColor(origColor);
            student.setButtonTintList(origColorStateList);
            instructor.setTextColor(origColor);
            instructor.setButtonTintList(origColorStateList);
        }

        if (!iemailSign.matches(emailPattern)) {
            inputEmailS.setError("Enter a valid SKSU Institutional Email \n(e.g. firstname_lastname@sksu.edu.ph)");
        } else if (fullName.isEmpty()) {
            inputFullname.setError("Enter a valid full name \n(e.g. John Juan Valenzuela Dela Cruz)");
        } else {
            if (userName.isEmpty()) {
                inputUsername.setError("Please create a username.");
            } else if (passwordSign.isEmpty()) {
                inputPassS.setError("Please create a password.");
            } else if (passwordSign.length() < 6) {
                inputPassS.setError("Password must be at least 6 characters.");
            } else if (!conpasswordSign.equals(passwordSign)) {
                inputConfirmPass.setError("Password does not match.");
            } else {
                if (student.isChecked() || instructor.isChecked()){

                    progressDialog.setMessage("Creating you an account...");
                    progressDialog.setTitle("Sign Up");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    Map<String,Object> user = new HashMap<>();
                    user.put("Institutional Email", iemailSign);
                    user.put("Full Name", fullName);
                    user.put("Username", userName);

                    if (student.isChecked()){
                        user.put("Identifier", "Student");
                    } else if (instructor.isChecked()){
                        user.put("Identifier", "Instructor");
                    }

                    db.collection("user")
                            .document(iemailSign)
                            .set(user, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                //
                            }).addOnFailureListener(e -> Toast.makeText(signup_activity.this, "Failed creating a user, try again!", Toast.LENGTH_SHORT).show());

                    mAuth.createUserWithEmailAndPassword(iemailSign, passwordSign).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            sendUserToNextActivity();
                            Toast.makeText(signup_activity.this, "Successfully registered!", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(signup_activity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(signup_activity.this, "Registration Failed: Please identify yourself if you are a student or an instructor...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void sendUserToNextActivity () {
        Intent intent = new Intent(signup_activity.this, login_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @SuppressLint({"ClickableViewAccessibility", "WrongViewCast", "RestrictedApi", "SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Full-screens the Display
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        } // Hides the ActionBar
        setContentView(R.layout.signup_layout); // Runs the XML
        db = FirebaseFirestore.getInstance();

        logoCon = findViewById(R.id.logoCon);
        copyRight = findViewById(R.id.copyRight);
        rootLayout = findViewById(R.id.backdd);

        xlogin = findViewById(R.id.xlogin);
        logintext = findViewById(R.id.logintext);

        student = findViewById(R.id.student);
        instructor = findViewById(R.id.instructor);

        inputEmailS = findViewById(R.id.inputEmailS);
        inputFullname = findViewById(R.id.inputFullname);
        inputUsername = findViewById(R.id.inputUsername);
        inputPassS = findViewById(R.id.inputPassS);
        inputConfirmPass = findViewById(R.id.inputConfirmPass);
        signupButton = findViewById(R.id.signupButton);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

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

    signupButton.setOnClickListener(view -> PerformAuthentication());

    signupButton.setOnTouchListener((v, event) -> {
        switch(event.getAction()) {

            // When Sign Up Button is Pressed
            case MotionEvent.ACTION_DOWN:
                signupButton.setTextColor(Color.BLACK);
                signupButton.setBackgroundColor(Color.parseColor("#9AF8C9"));
                signupButton.setText("Signing up...");

            // If Intitutional Email is empty turn the backgroundTint and Hint red, if not then turn to original
                if (!TextUtils.isEmpty(inputEmailS.getText().toString())) {
                    inputEmailS.setHintTextColor(origColor);
                    if (inputEmailS instanceof AppCompatEditText) {
                        ((AppCompatEditText) inputEmailS).setSupportBackgroundTintList(origColorStateList);
                    } else {
                        inputEmailS.setBackgroundTintList(origColorStateList);
                    }
                } else {
                    inputEmailS.setHintTextColor(redColor);
                    if (inputEmailS instanceof AppCompatEditText) {
                        ((AppCompatEditText) inputEmailS).setSupportBackgroundTintList(redColorStateList);
                    } else {
                        inputEmailS.setBackgroundTintList(redColorStateList);
                    }
                }

            // If Full Name is empty turn the backgroundTint and Hint red, if not then turn to original
                if (!TextUtils.isEmpty(inputFullname.getText().toString())) {
                    inputFullname.setHintTextColor(origColor);
                    if (inputFullname instanceof AppCompatEditText) {
                        ((AppCompatEditText) inputFullname).setSupportBackgroundTintList(origColorStateList);
                    } else {
                        inputFullname.setBackgroundTintList(origColorStateList);
                    }
                } else {
                    inputFullname.setHintTextColor(redColor);
                    if (inputFullname instanceof AppCompatEditText) {
                        ((AppCompatEditText) inputFullname).setSupportBackgroundTintList(redColorStateList);
                    } else {
                        inputFullname.setBackgroundTintList(redColorStateList);
                    }
                }

            // If Username is empty turn the backgroundTint and Hint red, if not then turn to original
                if (!TextUtils.isEmpty(inputUsername.getText().toString())) {
                    inputUsername.setHintTextColor(origColor);
                    if (inputUsername instanceof AppCompatEditText) {
                        ((AppCompatEditText) inputUsername).setSupportBackgroundTintList(origColorStateList);
                    } else {
                        inputUsername.setBackgroundTintList(origColorStateList);
                    }
                } else {
                    inputUsername.setHintTextColor(redColor);
                    if (inputUsername instanceof AppCompatEditText) {
                        ((AppCompatEditText) inputUsername).setSupportBackgroundTintList(redColorStateList);
                    } else {
                        inputUsername.setBackgroundTintList(redColorStateList);
                    }
                }

            // If Password is empty turn the backgroundTint and Hint red, if not then turn to original
                if (!TextUtils.isEmpty(inputPassS.getText().toString())) {
                    inputPassS.setHintTextColor(origColor);
                    if (inputPassS instanceof AppCompatEditText) {
                        ((AppCompatEditText) inputPassS).setSupportBackgroundTintList(origColorStateList);
                    } else {
                        inputPassS.setBackgroundTintList(origColorStateList);
                    }
                } else {
                    inputPassS.setHintTextColor(redColor);
                    if (inputPassS instanceof AppCompatEditText) {
                        ((AppCompatEditText) inputPassS).setSupportBackgroundTintList(redColorStateList);
                    } else {
                        inputPassS.setBackgroundTintList(redColorStateList);
                    }
                }

            // If Confirm Pass is empty turn the backgroundTint and Hint red, if not then turn to original
                if (!TextUtils.isEmpty(inputConfirmPass.getText().toString())) {
                    inputConfirmPass.setHintTextColor(origColor);
                    if (inputConfirmPass instanceof AppCompatEditText) {
                        ((AppCompatEditText) inputConfirmPass).setSupportBackgroundTintList(origColorStateList);
                    } else {
                        inputConfirmPass.setBackgroundTintList(origColorStateList);
                    }
                } else {
                    inputConfirmPass.setHintTextColor(redColor);
                    if (inputConfirmPass instanceof AppCompatEditText) {
                        ((AppCompatEditText) inputConfirmPass).setSupportBackgroundTintList(redColorStateList);
                    } else {
                        inputConfirmPass.setBackgroundTintList(redColorStateList);
                    }
                }

                break;

            // When Sign Up button is released
            case MotionEvent.ACTION_UP:
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                signupButton.setTextColor(Color.WHITE);
                signupButton.setBackgroundColor(Color.BLACK);
                signupButton.setText("Sign Up");
                break;
        }
        return false;
    });

        // This is to listen if cursor is visible in Intitutional Email EditText,
        // and if so.. then turn it back to original.
        inputEmailS.setOnFocusChangeListener((v, hasFocus) -> {

            if (hasFocus) {
                inputEmailS.post(() -> {
                    // Check if the text cursor is visible
                    if (inputEmailS.isCursorVisible()) {
                        // Change the hint text color of inputEmail to #89898A
                        inputEmailS.setHint("Institutional Email");
                        inputEmailS.setHintTextColor(Color.parseColor("#89898A"));
                        if (inputEmailS instanceof AppCompatEditText) {
                            ((AppCompatEditText) inputEmailS).setSupportBackgroundTintList(origColorStateList);
                        } else {
                            inputEmailS.setBackgroundTintList(origColorStateList);
                        }
                    }
                });
            }
        });

        // This is to listen if cursor is visible in Full Name EditText,
        // and if so.. then turn it back to original.
        inputFullname.setOnFocusChangeListener((v, hasFocus) -> {

            if (hasFocus) {
                inputFullname.post(() -> {
                    // Check if the text cursor is visible
                    if (inputFullname.isCursorVisible()) {
                        // Change the hint text color of inputEmail to #89898A
                        inputFullname.setHint("Full Name");
                        inputFullname.setHintTextColor(Color.parseColor("#89898A"));
                        if (inputFullname instanceof AppCompatEditText) {
                            ((AppCompatEditText) inputFullname).setSupportBackgroundTintList(origColorStateList);
                        } else {
                            inputFullname.setBackgroundTintList(origColorStateList);
                        }
                    }
                });
            }
        });

        // This is to listen if cursor is visible in Username EditText,
        // and if so.. then turn it back to original.
        inputUsername.setOnFocusChangeListener((v, hasFocus) -> {

            if (hasFocus) {
                inputUsername.post(() -> {
                    // Check if the text cursor is visible
                    if (inputUsername.isCursorVisible()) {
                        // Change the hint text color of inputEmail to #89898A
                        inputUsername.setHint("Username");
                        inputUsername.setHintTextColor(Color.parseColor("#89898A"));
                        if (inputUsername instanceof AppCompatEditText) {
                            ((AppCompatEditText) inputUsername).setSupportBackgroundTintList(origColorStateList);
                        } else {
                            inputUsername.setBackgroundTintList(origColorStateList);
                        }
                    }
                });
            }
        });

        // This is to listen if cursor is visible in Password EditText,
        // and if so.. then turn it back to original.
        inputPassS.setOnFocusChangeListener((v, hasFocus) -> {

            if (hasFocus) {
                inputPassS.post(() -> {
                    // Check if the text cursor is visible
                    if (inputPassS.isCursorVisible()) {
                        // Change the hint text color of inputEmail to #89898A
                        inputPassS.setHint("Password");
                        inputPassS.setHintTextColor(Color.parseColor("#89898A"));
                        if (inputPassS instanceof AppCompatEditText) {
                            ((AppCompatEditText) inputPassS).setSupportBackgroundTintList(origColorStateList);
                        } else {
                            inputPassS.setBackgroundTintList(origColorStateList);
                        }
                    }
                });
            }
        });

        // This is to listen if cursor is visible in Confirm Password EditText,
        // and if so.. then turn it back to original.
        inputConfirmPass.setOnFocusChangeListener((v, hasFocus) -> {

            if (hasFocus) {
                inputConfirmPass.post(() -> {
                    // Check if the text cursor is visible
                    if (inputConfirmPass.isCursorVisible()) {
                        // Change the hint text color of inputEmail to #89898A
                        inputConfirmPass.setHint("Confirm Password");
                        inputConfirmPass.setHintTextColor(Color.parseColor("#89898A"));
                        if (inputConfirmPass instanceof AppCompatEditText) {
                            ((AppCompatEditText) inputConfirmPass).setSupportBackgroundTintList(origColorStateList);
                        } else {
                            inputConfirmPass.setBackgroundTintList(origColorStateList);
                        }
                    }
                });
            }
        });

        // if Institutional Email text changes, turn the backgroundTint to original
        inputEmailS.addTextChangedListener(new TextWatcher() {
            @SuppressLint("RestrictedApi")
            @Override
            public void afterTextChanged(Editable s) {
                // Check if the text cursor is visible
                if (inputEmailS.isCursorVisible()) {
                    // Change the hint text color of inputEmail to #89898A
                    inputEmailS.setHint("Institutional Email");
                    inputEmailS.setHintTextColor(Color.parseColor("#89898A"));
                    if (inputEmailS instanceof AppCompatEditText) {
                        ((AppCompatEditText) inputEmailS).setSupportBackgroundTintList(origColorStateList);
                    } else {
                        inputEmailS.setBackgroundTintList(origColorStateList);
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

        // if Full Name text changes, turn the backgroundTint to original
        inputFullname.addTextChangedListener(new TextWatcher() {
            @SuppressLint("RestrictedApi")
            @Override
            public void afterTextChanged(Editable s) {
                // Check if the text cursor is visible
                if (inputFullname.isCursorVisible()) {
                    // Change the hint text color of inputEmail to #89898A
                    inputFullname.setHint("Full Name");
                    inputFullname.setHintTextColor(Color.parseColor("#89898A"));
                    if (inputFullname instanceof AppCompatEditText) {
                        ((AppCompatEditText) inputFullname).setSupportBackgroundTintList(origColorStateList);
                    } else {
                        inputFullname.setBackgroundTintList(origColorStateList);
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

        // if Username text changes, turn the backgroundTint to original
        inputUsername.addTextChangedListener(new TextWatcher() {
                @SuppressLint("RestrictedApi")
                @Override
                public void afterTextChanged(Editable s) {
                    // Check if the text cursor is visible
                    if (inputUsername.isCursorVisible()) {
                        // Change the hint text color of inputEmail to #89898A
                        inputUsername.setHint("Username");
                        inputUsername.setHintTextColor(Color.parseColor("#89898A"));
                        if (inputUsername instanceof AppCompatEditText) {
                            ((AppCompatEditText) inputUsername).setSupportBackgroundTintList(origColorStateList);
                        } else {
                            inputUsername.setBackgroundTintList(origColorStateList);
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
        inputPassS.addTextChangedListener(new TextWatcher() {
            @SuppressLint("RestrictedApi")
            @Override
            public void afterTextChanged(Editable s) {
                // Check if the text cursor is visible
                if (inputPassS.isCursorVisible()) {
                    // Change the hint text color of inputEmail to #89898A
                    inputPassS.setHint("Password");
                    inputPassS.setHintTextColor(Color.parseColor("#89898A"));
                    if (inputPassS instanceof AppCompatEditText) {
                        ((AppCompatEditText) inputPassS).setSupportBackgroundTintList(origColorStateList);
                    } else {
                        inputPassS.setBackgroundTintList(origColorStateList);
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

        // if Confirm Password text changes, turn the backgroundTint to original
        inputConfirmPass.addTextChangedListener(new TextWatcher() {
            @SuppressLint("RestrictedApi")
            @Override
            public void afterTextChanged(Editable s) {
                // Check if the text cursor is visible
                if (inputConfirmPass.isCursorVisible()) {
                    // Change the hint text color of inputEmail to #89898A
                    inputConfirmPass.setHint("Confirm Password");
                    inputConfirmPass.setHintTextColor(Color.parseColor("#89898A"));
                    if (inputConfirmPass instanceof AppCompatEditText) {
                        ((AppCompatEditText) inputConfirmPass).setSupportBackgroundTintList(origColorStateList);
                    } else {
                        inputConfirmPass.setBackgroundTintList(origColorStateList);
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

        student.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                student.setTextColor(origColor);
                student.setButtonTintList(origColorStateList);
                instructor.setTextColor(origColor);
                instructor.setButtonTintList(origColorStateList);
            }
        });

        instructor.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                student.setTextColor(origColor);
                student.setButtonTintList(origColorStateList);
                instructor.setTextColor(origColor);
                instructor.setButtonTintList(origColorStateList);
            }
        });

        // This function as activity switcher when "Already have an account? Log In" is tapped.
        xlogin.setOnClickListener(v -> startActivity(new Intent(signup_activity.this, login_activity.class)));
        logintext.setOnClickListener(v -> startActivity(new Intent(signup_activity.this, login_activity.class)));

        // This is just an identifier if student or instructor
        student.setOnClickListener(view -> instructor.setChecked(false));
        instructor.setOnClickListener(view -> student.setChecked(false));

        // Don't delete below (For safety purposes haha)
    }


}