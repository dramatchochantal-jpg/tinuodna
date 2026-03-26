package com.example.edugate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Signinup extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailInput, passwordInput;
    private RadioGroup roleGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_out);

        mAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        roleGroup = findViewById(R.id.roleGroup);
        Button btnAction = findViewById(R.id.btnAction);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnAction.setOnClickListener(v -> handleLogin());
        tvForgotPassword.setOnClickListener(v -> handleForgotPassword());
    }

    private void handleLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        int selectedId = roleGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedId);
        String uiRole = selectedRadioButton.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Pun-a ang email ug password!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        verifyUserRole(uiRole);
                    } else {
                        // ❌ FAILED - ipakita ang exact error
                        String errorMsg = task.getException() != null ? 
                            task.getException().getMessage() : "Unknown error";
                        Toast.makeText(Signinup.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void verifyUserRole(String uiRole) {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (userId == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                checkRoleMatch(task.getResult(), uiRole);
            } else {
                String error = "No database entry for UID: " + userId;
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                mAuth.signOut();
            }
        });
    }

    private void checkRoleMatch(DocumentSnapshot document, String uiRole) {
        String dbRole = document.getString("role");
        if (dbRole == null) dbRole = document.getString("Role");

        if (dbRole != null && uiRole.equalsIgnoreCase(dbRole.trim())) {
            navigateToDashboard(dbRole.trim());
        } else {
            String msg = "Role Mismatch! You selected '" + uiRole + "' but DB says '" + dbRole + "'";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            mAuth.signOut();
        }
    }

    private void navigateToDashboard(String role) {
        Intent intent;
        if (role.equalsIgnoreCase("Student")) {
            intent = new Intent(this, Studentdashboard.class);
        } else if (role.equalsIgnoreCase("Teacher")) {
            intent = new Intent(this, Teacherdashboard.class);
        } else if (role.equalsIgnoreCase("Admin")) {
            intent = new Intent(this, Admindashboard.class);
        } else {
            Toast.makeText(this, "Unknown role: " + role, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // ✅ SUCCESS - proceed to Dashboard and clear stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void handleForgotPassword() {
        String email = emailInput.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Enter email first", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Reset email sent!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
