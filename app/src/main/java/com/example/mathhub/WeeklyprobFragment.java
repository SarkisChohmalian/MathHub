package com.example.mathhub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class WeeklyprobFragment extends Fragment {

    private TextView problemOfTheWeekTextView;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weeklyprob, container, false);
        db = FirebaseFirestore.getInstance();
        problemOfTheWeekTextView = view.findViewById(R.id.problemOfTheWeekTextView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        retrieveProblemOfTheWeek();
    }

    private void retrieveProblemOfTheWeek() {
        db.collection("user_answers")
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot latestDocument = queryDocumentSnapshots.getDocuments().get(0);
                        String problemOfTheWeek = latestDocument.getString("text");
                        if (problemOfTheWeek != null && !problemOfTheWeek.isEmpty()) {
                            problemOfTheWeekTextView.setText(problemOfTheWeek);
                        } else {
                            Toast.makeText(getContext(), "Empty problem of the week", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "No problem of the week found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadDocumentById(String documentId) {
        db.collection("user_answers").document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String problemOfTheWeek = documentSnapshot.getString("text");
                        if (problemOfTheWeek != null && !problemOfTheWeek.isEmpty()) {
                            problemOfTheWeekTextView.setText(problemOfTheWeek);
                        } else {
                            Toast.makeText(getContext(), "Empty problem of the week", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Document not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
