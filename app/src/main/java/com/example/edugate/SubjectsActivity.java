package com.example.edugate;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class SubjectsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView tvSubjectsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        tvSubjectsList = findViewById(R.id.tvSubjectsList);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        fetchEnrolledSubjects();
    }

    private void fetchEnrolledSubjects() {
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("Enrollments")
            .whereEqualTo("studentId", uid)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    StringBuilder subjects = new StringBuilder();
                    int count = 0;

                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String subject = doc.getString("subjectName");
                        String teacher = doc.getString("teacherName");
                        if (subject != null) {
                            count++;
                            subjects.append(count).append(". ").append(subject);
                            if (teacher != null)
                                subjects.append(" — ").append(teacher);
                            subjects.append("\n");
                        }
                    }

                    if (tvSubjectsList != null) {
                        tvSubjectsList.setText(count > 0 ?
                            subjects.toString() : "Walay enrolled subjects.");
                    }
                } else {
                    Toast.makeText(this, "Error fetching subjects",
                        Toast.LENGTH_SHORT).show();
                }
            });
    }
}
