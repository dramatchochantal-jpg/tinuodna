package com.example.edugate;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Admindashboard extends AppCompatActivity {
    
    private FirebaseAuth mAuth;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        mAuth = FirebaseAuth.getInstance();

        // ✅ Null check
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, Signinup.class));
            finish();
            return;
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home); // ← i-add ni
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Stay on home
                return true;
            } else if (itemId == R.id.nav_schedule) {
                startActivity(new Intent(this, ScheduleActivity.class));
                return true;
            } else if (itemId == R.id.nav_grades) {
                startActivity(new Intent(this, GradesActivity.class));
                return true;
            } else if (itemId == R.id.nav_messages) {
                startActivity(new Intent(this, MessagesActivity.class));
                return true;
            } else if (itemId == R.id.nav_more) {
                startActivity(new Intent(this, AnnouncementsActivity.class));
                return true;
            }
            return false;
        });
    }
}