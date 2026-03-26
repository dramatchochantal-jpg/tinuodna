package com.example.edugate;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class TasksActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView tvTasksList, tvPendingCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        tvTasksList = findViewById(R.id.tvTasksList);
        tvPendingCount = findViewById(R.id.tvPendingCount);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        fetchPendingTasks();
    }

    private void fetchPendingTasks() {
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("Tasks")
            .whereEqualTo("studentId", uid)
            .whereEqualTo("status", "pending")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    StringBuilder tasks = new StringBuilder();
                    int count = 0;

                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String title = doc.getString("title");
                        String dueDate = doc.getString("dueDate");
                        if (title != null) {
                            count++;
                            tasks.append(count).append(". ").append(title);
                            if (dueDate != null)
                                tasks.append(" — Due: ").append(dueDate);
                            tasks.append("\n");
                        }
                    }

                    if (tvPendingCount != null)
                        tvPendingCount.setText(String.valueOf(count));

                    if (tvTasksList != null) {
                        tvTasksList.setText(count > 0 ?
                            tasks.toString() : "Walay pending tasks! 🎉");
                    }
                } else {
                    Toast.makeText(this, "Error fetching tasks",
                        Toast.LENGTH_SHORT).show();
                }
            });
    }
}
