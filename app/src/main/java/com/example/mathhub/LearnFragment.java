package com.example.mathhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

public class LearnFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn, container, false);

        view.findViewById(R.id.first_concept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDiophantineActivity();
            }
        });


        ImageView imageViewProfile = view.findViewById(R.id.imageView2);
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToProfileActivity();
            }
        });

        return view;
    }

    public void navigateToDiophantineActivity() {
        Intent intent = new Intent(getActivity(), DiophantineActivity.class);
        startActivity(intent);
    }


    public void navigateToProfileActivity() {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);
    }
}
