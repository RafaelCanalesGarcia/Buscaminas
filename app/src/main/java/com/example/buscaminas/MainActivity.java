package com.example.buscaminas;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    Fragment selectedFragment = null;

    //  MediaPlayer backgroundMusic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbarmain);
        setSupportActionBar(toolbar);

// para la musica de fondo
//        backgroundMusic = MediaPlayer.create(this, R.raw.backgroundmusic);
//        backgroundMusic.setLooping(true); // Para que se repita en bucle
//        backgroundMusic.start();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.settings);
        }
        bottomNavigationView.setOnItemSelectedListener(item -> {

            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.play) {
                selectedFragment = new PlayFragment();

            } else if (item.getItemId() == R.id.settings) {
                selectedFragment = new SettingsFragment();

            } else if (item.getItemId() == R.id.social) {
                selectedFragment = new SocialFragment();

            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toolbar toolbar = findViewById(R.id.toolbarmain);
        int id = item.getItemId();
        if (id == R.id.reglas) {
            selectedFragment = new InfoFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            toolbar.setTitle("BUSCAMINAS");
        } else if (id == R.id.clasificacion) {
            selectedFragment = new Ranking();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            toolbar.setTitle("BUSCAMINAS");
        }
        else if (id == R.id.exit) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmar salida")
                    .setMessage("¿Estás seguro de que quieres salir?")
                    .setPositiveButton("Sí", (dialog, which) -> LogOutOfApp())
                    .setNegativeButton("No", null)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void LogOutOfApp() {
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
//            backgroundMusic.pause();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (backgroundMusic != null) {
//            backgroundMusic.start();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (backgroundMusic != null) {
//            backgroundMusic.release();
//            backgroundMusic = null;
//        }
//    }
}
