package com.example.edugate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Studentdashboard extends AppCompatActivity {
    private TextView tvWelcome, tvBalance;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_dashboard);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        tvWelcome = findViewById(R.id.welcomeText);
        tvBalance = findViewById(R.id.balanceText);

        fetchStudentData();

        // Bottom Navigation Logic
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_schedule) {
                startActivity(new Intent(this, ScheduleActivity.class));
                return true;
            } else if (itemId == R.id.nav_messages) {
                startActivity(new Intent(this, MessagesActivity.class));
                return true;
            } else if (itemId == R.id.nav_grades) {
                startActivity(new Intent(this, GradesActivity.class));
                return true;
            } else if (itemId == R.id.nav_more) {
                startActivity(new Intent(this, AnnouncementsActivity.class));
                return true;
            }
            return false;
        });

        // Card Click Listeners
        findViewById(R.id.cardEnrolledSubjects).setOnClickListener(v -> 
            startActivity(new Intent(this, SubjectsActivity.class)));
        
        findViewById(R.id.cardPendingTasks).setOnClickListener(v -> 
            startActivity(new Intent(this, TasksActivity.class)));
        
        findViewById(R.id.cardAvgGrade).setOnClickListener(v -> 
            startActivity(new Intent(this, GradesActivity.class)));
    }

    private void fetchStudentData() {
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, Signinup.class));
            finish();
            return;
        }
        
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("Users").document(uid).get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc != null && doc.exists()) {
                        String name = doc.getString("fullName");
                        Long balance = doc.getLong("balance");
                        
                        if (name != null) tvWelcome.setText("Welcome, " + name + "!");
                        if (balance != null) tvBalance.setText("₱" + 
                            String.format("%,d", balance));
                    }
                } else {
                    Toast.makeText(this, "Error fetching data", 
                        Toast.LENGTH_SHORT).show();
                }
            });
    }
}
