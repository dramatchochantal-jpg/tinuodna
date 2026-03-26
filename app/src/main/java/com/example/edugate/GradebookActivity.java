package com.example.edugate;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

public class GradebookActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private EditText etStudentId, etSubject, etGrade;
    private TextView tvGradesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gradebook);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        etStudentId = findViewById(R.id.etStudentId);
        etSubject   = findViewById(R.id.etSubject);
        etGrade     = findViewById(R.id.etGrade);
        tvGradesList = findViewById(R.id.tvGradesList);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        Button btnSubmit = findViewById(R.id.btnSubmitGrade);
        if (btnSubmit != null) btnSubmit.setOnClickListener(v -> submitGrade());

        fetchGrades();
    }

    private void submitGrade() {
        String studentId = etStudentId.getText().toString().trim();
        String subject   = etSubject.getText().toString().trim();
        String gradeStr  = etGrade.getText().toString().trim();

        if (studentId.isEmpty() || subject.isEmpty() || gradeStr.isEmpty()) {
            Toast.makeText(this, "Pun-a tanan fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        double gradeValue;
        try {
            gradeValue = Double.parseDouble(gradeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid grade value!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> gradeData = new HashMap<>();
        gradeData.put("studentId", studentId);
        gradeData.put("subject", subject);
        gradeData.put("grade", gradeValue);
        gradeData.put("teacherId", mAuth.getCurrentUser().getUid());
        gradeData.put("timestamp", com.google.firebase.Timestamp.now());

        db.collection("Grades").add(gradeData)
            .addOnSuccessListener(ref -> {
                Toast.makeText(this, "Grade submitted!", Toast.LENGTH_SHORT).show();
                etStudentId.setText("");
                etSubject.setText("");
                etGrade.setText("");
                fetchGrades(); // refresh ang list
            })
            .addOnFailureListener(e ->
                Toast.makeText(this, "Failed: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show());
    }

    private void fetchGrades() {
        String teacherId = mAuth.getCurrentUser().getUid();

        db.collection("Grades")
            .whereEqualTo("teacherId", teacherId)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    StringBuilder list = new StringBuilder();
                    int count = 0;

                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String studentId = doc.getString("studentId");
                        String subject   = doc.getString("subject");
                        Double grade     = doc.getDouble("grade");
                        if (studentId != null && grade != null) {
                            count++;
                            list.append(count).append(". ")
                                .append(subject).append(" | ")
                                .append(studentId).append(" — ")
                                .append(grade).append("\n");
                        }
                    }

                    if (tvGradesList != null) {
                        tvGradesList.setText(count > 0 ?
                            list.toString() : "Walay grades pa.");
                    }
                } else {
                    Toast.makeText(this, "Error fetching grades",
                        Toast.LENGTH_SHORT).show();
                }
            });
    }
}
