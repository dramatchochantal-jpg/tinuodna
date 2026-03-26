package com.example.edugate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Teacherdashboard extends AppCompatActivity {
    private TextView tvTotalStudents;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_dashboard);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // ✅ Null check
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, Signinup.class));
            finish();
            return;
        }

        tvTotalStudents = findViewById(R.id.tvTotalStudentsCount);
        fetchTeacherStats();

        // Bottom Navigation Logic
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) return true;
            else if (itemId == R.id.nav_schedule) {
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

        // Dashboard Click Listeners
        findViewById(R.id.cardTotalStudents).setOnClickListener(v -> 
            startActivity(new Intent(this, TeacherStudentsActivity.class)));

        findViewById(R.id.btnPostAnnouncement).setOnClickListener(v -> 
            startActivity(new Intent(this, PostAnnouncementActivity.class)));

        findViewById(R.id.btnViewSubmissions).setOnClickListener(v -> 
            startActivity(new Intent(this, ViewSubmissionsActivity.class)));

        findViewById(R.id.btnGradeBook).setOnClickListener(v -> 
            startActivity(new Intent(this, GradebookActivity.class)));
    }

    private void fetchTeacherStats() {
        // Fetch all users with role 'Student'
        db.collection("Users")
            .whereEqualTo("role", "Student")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshot = task.getResult();
                    if (snapshot != null) {
                        int count = snapshot.size();
                        tvTotalStudents.setText(String.valueOf(count));
                    }
                } else {
                    Toast.makeText(this, "Error fetching student count", Toast.LENGTH_SHORT).show();
                }
            });
    }
}