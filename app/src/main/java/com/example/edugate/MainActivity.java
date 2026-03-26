package com.example.edugate;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Kung walay naka-login → Signinup
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, Signinup.class));
            finish();
            return;
        }

        // Kung naka-login na → i-fetch ang role then redirect sa tamang dashboard
        String uid = mAuth.getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
            .collection("Users")
            .document(uid)
            .get()
            .addOnCompleteListener(task -> {
                Intent intent;
                if (task.isSuccessful() && task.getResult().exists()) {
                    DocumentSnapshot doc = task.getResult();
                    String role = doc.getString("role");
                    if (role == null) role = doc.getString("Role");

                    if ("Student".equalsIgnoreCase(role)) {
                        intent = new Intent(this, Studentdashboard.class);
                    } else if ("Teacher".equalsIgnoreCase(role)) {
                        intent = new Intent(this, Teacherdashboard.class);
                    } else if ("Admin".equalsIgnoreCase(role)) {
                        intent = new Intent(this, Admindashboard.class);
                    } else {
                        // Unknown role → balik sa login
                        mAuth.signOut();
                        intent = new Intent(this, Signinup.class);
                    }
                } else {
                    // Walay Firestore record → balik sa login
                    mAuth.signOut();
                    intent = new Intent(this, Signinup.class);
                }
                startActivity(intent);
                finish();
            });
    }
}
