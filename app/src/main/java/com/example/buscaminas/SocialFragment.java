package com.example.buscaminas;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class SocialFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_social, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<String> items = Arrays.asList("Rafa", "Andy", "Pere", "Ainara", "Toni", "Hugo","Jaume","Pascual","Ivan","Xavier");
        List<String> positions = Arrays.asList("1", "2", "3", "4", "5", "6","7","8","9","10");
        recyclerView.setAdapter(new MyAdapter(items,positions));
        return rootView;
    }
}