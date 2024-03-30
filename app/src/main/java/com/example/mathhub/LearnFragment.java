package com.example.mathhub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import android.content.Intent;

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

        view.findViewById(R.id.second_concept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToInductionActivity();
            }
        });

        view.findViewById(R.id.third_concept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToBezoutActivity();
            }
        });

        return view;
    }

    public void navigateToDiophantineActivity() {
        Intent intent = new Intent(getActivity(), DiophantineActivity.class);
        startActivity(intent);
    }

    public void navigateToInductionActivity() {
        Intent intent = new Intent(getActivity(), InductionActivity.class);
        startActivity(intent);
    }

    public void navigateToBezoutActivity() {
        Intent intent = new Intent(getActivity(), BezoutActivity.class);
        startActivity(intent);
    }
}
