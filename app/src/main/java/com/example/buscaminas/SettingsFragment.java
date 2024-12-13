package com.example.buscaminas;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;


import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    private Spinner spinnerLanguage;
    private RadioGroup radioDifficult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        radioDifficult = rootView.findViewById(R.id.radioDifficult);
        radioDifficult.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.lvlmuyfacil) {
                setParametros(14, 8, 10);

            } else if (checkedId == R.id.lvlfacil) {
                setParametros(14, 8, 15);

            } else if (checkedId == R.id.lvlmedio) {
                setParametros(21, 12, 30);

            } else if (checkedId == R.id.lvldificil) {
                setParametros(21, 12, 50);

            } else if (checkedId == R.id.lvlmuydificil) {
                setParametros(28, 16, 70);

            } else if (checkedId == R.id.lvlimposible) {
                setParametros(28, 16, 100);

            }

        });

//        musicSwitch = rootView.findViewById(R.id.music_switch);
//
//        musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                getContext().startService(new Intent(getContext(), MusicBackgroundService.class));
//            } else {
//                getContext().stopService(new Intent(getContext(), MusicBackgroundService.class));
//            }
//        });

        spinnerLanguage = rootView.findViewById(R.id.spinner_language);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.language, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);
        spinnerLanguage.setSelection(0);
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {

                } else if (position == 1) {

                } else if (position == 2) {

                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return rootView;
    }

    public void setParametros(int rows, int cols, int totalMinas) {
        SharedPreferences preferences = getActivity().getSharedPreferences("GamePreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("rows", rows);
        editor.putInt("cols", cols);
        editor.putInt("totalMinas", totalMinas);
        editor.apply();
    }

}