package com.example.buscaminas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Random;


public class PlayFragment extends Fragment {
    // 8colsx14rows , 12colsx21rows, 16 colsx28rows
    private int rows;
    private int cols;
    private int totalMinas;
    private GridLayout gridLayout;
    private boolean[][] mines;
    boolean[][] descubierto;
    boolean[][] banderitas;
    private ImageView[][] cells;
    private Toolbar toolbar;
    private TextView timerTextView;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private int secondsElapsed = 0;
    private boolean isFirstClick = true;
    private boolean banderitaActivada = false;
    private TextView contadorBanderasRestantes;
    int banderasRestantes;
    public enum EstadoJuego {
        EN_PROGRESO,
        VICTORIA,
        DERROTA
    }
    public static final int EN_PROGRESO = 0;
    public static final int VICTORIA = 1;
    public static final int DERROTA = 2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_play, container, false);

        toolbar = requireActivity().findViewById(R.id.toolbarmain);

        // Personalizar el Toolbar
        toolbar.getMenu().clear(); // Limpiar las opciones del menú anterior
        toolbar.setTitle(""); // Eliminar el título
        toolbar.setNavigationIcon(null);
        setHasOptionsMenu(true);

        // creamos un cronómetro para el Toolbar
        timerTextView = new TextView(requireContext());
        timerTextView.setTextColor(requireContext().getResources().getColor(android.R.color.white));
        timerTextView.setTextSize(18);
        timerTextView.setText("00:00"); // Valor inicial del cronómetro


        // Configurar contador de banderas para el Toolbar
        contadorBanderasRestantes = new TextView(requireContext());
        contadorBanderasRestantes.setTextColor(requireContext().getResources().getColor(android.R.color.white));
        contadorBanderasRestantes.setTextSize(18);

        toolbar.addView(contadorBanderasRestantes);
        toolbar.addView(timerTextView);
        banderasRestantes = totalMinas;
        actualizaContadorBanderasRestantes();


        // Cargar configuraciones desde SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("GamePreferences", Context.MODE_PRIVATE);
        rows = preferences.getInt("rows", 14);  // Valor por defecto 14
        cols = preferences.getInt("cols", 8);   // Valor por defecto 8
        totalMinas = preferences.getInt("totalMinas", 10);
        gridLayout = rootView.findViewById(R.id.gridLayout);
        gridLayout.setRowCount(rows);
        gridLayout.setColumnCount(cols);
        cells = new ImageView[rows][cols];
        mines = new boolean[rows][cols];
        banderitas = new boolean[rows][cols];
        // Crear tablero dinámicamente
        reiniciarJuego();
        return rootView;

    }

    private void actualizaContadorBanderasRestantes() {
        contadorBanderasRestantes.setText(String.format("Banderas: %d           ", banderasRestantes));
    }

    private void actualizarIconoRestart(EstadoJuego estado) {
        Menu menu = toolbar.getMenu(); // Obtén el menú actual
        MenuItem restartItem = menu.findItem(R.id.restart); // Busca el ítem restart

        if (restartItem != null) {
            switch (estado) {
                case VICTORIA:
                    restartItem.setIcon(R.drawable.won); // Ícono de victoria
                    break;
                case DERROTA:
                    restartItem.setIcon(R.drawable.dead); // Ícono de derrota
                    break;
                case EN_PROGRESO:
                    restartItem.setIcon(R.drawable.normal); // Ícono de progreso
                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTimer(); // Detener el cronómetro al salir del fragmento

        // Restaurar el Toolbar original si es necesario
        toolbar.getMenu().clear(); // Limpiar el menú del fragmento
        toolbar.setTitle("BUSCAMINAS");
        requireActivity().invalidateOptionsMenu();
        toolbar.removeView(timerTextView);
        toolbar.removeView(contadorBanderasRestantes); // Eliminar el cronómetro del Toolbar
    }

    private void restartTimer() {
        secondsElapsed = 0;
        timerTextView.setText("00:00");
    }

    private void startTimer() {
        timerHandler = new Handler(Looper.getMainLooper());
        // metodo que se ejecuta cada segundo
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                int minutes = secondsElapsed / 60;
                int seconds = secondsElapsed % 60;
                timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
                secondsElapsed++;
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void stopTimer() {
        if (timerHandler != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        toolbar.getMenu().clear(); // Limpiar las opciones del menú anterior
        toolbar.setTitle(""); // Eliminar el título
        toolbar.setNavigationIcon(null);
        inflater.inflate(R.menu.game_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.restart) {
            mostrarDialogoReiniciar();
            return false;
        } else if (id == R.id.banderita) {
            if (!banderitaActivada) {
                banderitaActivada = true;
                Toast.makeText(getContext(), "Bandera activada", Toast.LENGTH_SHORT).show();
            } else {
                banderitaActivada = false;
                Toast.makeText(getContext(), "Bandera desactivada", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    public int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    private void createBoard() {

        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                int statusbarHeight = getStatusBarHeight(requireContext());

                // alto de toolbar
                int toolbarHeight = toolbar.getHeight();

                // alto de navBar
                BottomNavigationView navigationView = requireActivity().findViewById(R.id.bottom_navigation);
                int navbarHeight = navigationView.getHeight();

                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int anchoPantalla = displayMetrics.widthPixels;
                int altoPantalla = displayMetrics.heightPixels - toolbarHeight - navbarHeight;

                ImageView imageView = new ImageView(getContext());
                GridLayout.LayoutParams parametros = new GridLayout.LayoutParams();
                parametros.width = anchoPantalla / cols; // Ancho de la celda (en píxeles)
                parametros.height = altoPantalla / rows; // Alto de la celda (en píxeles)
                imageView.setLayoutParams(parametros);
                cells[x][y] = imageView;
                // apariencia inicial de la celda
                imageView.setBackgroundResource(R.drawable.botonsinpulsar);

                // clic en la celda
                final int finalX = x;
                final int finalY = y;
                imageView.setOnClickListener(v -> clickEnCelda(finalX, finalY, imageView));


                gridLayout.addView(imageView);
            }
        }
    }

    private void clickEnCelda(int x, int y, ImageView imageView) {
        // fondo del ImageView al ser clickeado
        if (isFirstClick) {
            // Genera las minas asegurando que el primer clic sea seguro
            colocaMinas(x, y);
            isFirstClick = false; // Marca que el primer clic ya ocurrió
        }

        // si esta banderita esta activada y no se ha descubierto la celda
        if (banderitaActivada && !descubierto[x][y]) {
            if (!banderitas[x][y] && banderasRestantes > 0) {
                imageView.setBackgroundResource(R.drawable.flagged);
                reproducirSonido(R.raw.banderita);// Cambia a la imagen de la bandera
                banderitas[x][y] = true;
                banderasRestantes--; // Marca como banderada
                actualizaContadorBanderasRestantes();
            } else if (banderitas[x][y]) {
                imageView.setBackgroundResource(R.drawable.botonsinpulsar); // Cambia a la imagen original
                banderitas[x][y] = false;
                banderasRestantes++; // Desmarca la banderita
                actualizaContadorBanderasRestantes();
                return;
            }
        }
        if (banderitaActivada && banderasRestantes == 0) {
            return;
        }
        if (banderitas[x][y]) {
            return;
        }
        int minasCercanas = cuentaMinasAdjacentes(x, y);
        if (mines[x][y]) {
            reproducirSonido(R.raw.derrota);
            imageView.setBackgroundResource(R.drawable.mine);
            descubierto[x][y] = true;
            revelaTodasLasMinas();
            mostrarDialogoDerrota();

        } else if (minasCercanas == 0) {
            reproducirSonido(R.raw.clickvacio);
            revelaCeldasSeguras(x, y); // Revela celdas adyacentes si no hay minas cercanas
        } else {
            reproducirSonido(R.raw.clicknumero);
            imageView.setBackgroundResource(getNumberDrawable(minasCercanas));
            descubierto[x][y] = true; // Marcar la celda como descubierta
        }
        if (compruebaVictoria()) {
            reproducirSonido(R.raw.victory);
            mostrarDialogoVictoria();
        }
    }

    private void colocaMinas(int librex, int librey) {
        mines = new boolean[rows][cols];
        Random random = new Random();
        int minasColocadas = 0;

        while (minasColocadas < totalMinas) {
            int x = random.nextInt(rows);
            int y = random.nextInt(cols);

            // evita poner bombas donde el primer click
            if (x == librex && y == librey) {
                continue;
            }
            if (!mines[x][y]) {
                mines[x][y] = true;
                minasColocadas++;
            }
        }
    }

    private int cuentaMinasAdjacentes(int origenX, int origenY) {
        int count = 0;

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                int finalX = origenX + x;
                int finalY = origenY + y;

                // Verifica límites del tablero origenY si hay una mina
                if (finalX >= 0 && finalX < rows && finalY >= 0 && finalY < cols && mines[finalX][finalY]) {
                    count++;
                }
            }
        }
        return count;
    }

    //
    private void revelaCeldasSeguras(int x, int y) {
        // Verificar si la posición está fuera del tablero
        if (x < 0 || x >= rows || y < 0 || y >= cols) {
            return;
        }

        // Verificar si ya fue revelada
        if (descubierto[x][y]) {
            return;
        }

        // Contar minas cercanas
        int minasCercanas = cuentaMinasAdjacentes(x, y);

        // Revelar la celda
        if (banderitas[x][y]) {
            banderitas[x][y] = false; // Remover la bandera
            banderasRestantes++;     // Incrementar el contador de banderas restantes
            actualizaContadorBanderasRestantes(); // Actualizar el contador en la interfaz
        }

        cells[x][y].setBackgroundResource(getNumberDrawable(minasCercanas));
        descubierto[x][y] = true; // Marcar la celda como descubierta

        // Si no hay minas cercanas, revelar celdas adyacentes
        if (minasCercanas == 0) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx != 0 || dy != 0) { // Evitar llamar sobre la misma celda

                        revelaCeldasSeguras(x + dx, y + dy);
                    }
                }
            }
        }
        if (compruebaVictoria()) {
            mostrarDialogoVictoria();
        }
    }

    private int getNumberDrawable(int minesNearby) {
        switch (minesNearby) {
            case 0:

                return R.drawable.empty;
            case 1:
                return R.drawable.one;
            case 2:
                return R.drawable.two;
            case 3:
                return R.drawable.three;
            case 4:
                return R.drawable.four;
            case 5:
                return R.drawable.five;
            case 6:
                return R.drawable.six;
            case 7:
                return R.drawable.seven;
            case 8:
                return R.drawable.eight;
            default:
                return R.drawable.botonpulsado;
        }
    }

    private boolean compruebaVictoria() {

        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                // Verificar si hay celdas no descubiertas que no contienen minas
                if (!mines[x][y] && !descubierto[x][y]) {

                    return false; // Si hay una celda no descubierta sin mina, aún no se gana
                }
            }
        }
        // Comprobar si las banderas están correctamente colocadas
        actualizarIconoRestart(EstadoJuego.VICTORIA);
        return true;
    }

    private void revelaTodasLasMinas() {
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                if (mines[x][y]) {
                    cells[x][y].setImageResource(R.drawable.nonbomb);
                }
                actualizarIconoRestart(EstadoJuego.DERROTA);
                cells[x][y].setEnabled(false);
            }
        }
    }

    private void
    reiniciarJuego() {
        // Reinicia el estado del juego
        restartTimer();
        startTimer();
        gridLayout.removeAllViews();
        createBoard();
        isFirstClick = true;
        descubierto = new boolean[rows][cols];
        banderitas = new boolean[rows][cols];
        banderasRestantes = totalMinas; // Reinicia el contador de banderas
        actualizaContadorBanderasRestantes();
        actualizarIconoRestart(EstadoJuego.EN_PROGRESO);
    }

    private void mostrarDialogoVictoria() {
        stopTimer();
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("¡Felicidades!")
                .setMessage("Has ganado el juego. ¿Qué te gustaría hacer?")
                .setPositiveButton("Reiniciar", (dialog, which) -> reiniciarJuego())
                .setNegativeButton("Configuración", (dialog, which) -> {
                    // Cambiar a SettingsFragment
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new SettingsFragment())
                            .addToBackStack(null)
                            .commit();

                    // Sincronizar el BottomNavigationView
                    BottomNavigationView navigationView = requireActivity().findViewById(R.id.bottom_navigation);
                    navigationView.setSelectedItemId(R.id.settings); // Seleccionar el ítem de configuración
                })
                // .setNeutralButton("Configuracion", ((dialog, which) -> getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit()))
                .setCancelable(false) // Evita que el diálogo se cierre al tocar fuera
                .show();


    }

    private void mostrarDialogoReiniciar() {
        stopTimer();
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("¡¡Cuidado!!")
                .setMessage("No has acabado la partida.¿Seguro que quieres reiniciar?")
                .setPositiveButton("Confirmar", (dialog, which) -> reiniciarJuego())
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Ejecutar el método y cerrar el diálogo
                        startTimer();
                        dialog.dismiss();
                    }
                })
                // .setNeutralButton("Configuracion", ((dialog, which) -> getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit()))
                .setCancelable(false) // Evita que el diálogo se cierre al tocar fuera
                .show();


    }

    private void mostrarDialogoDerrota() {
        stopTimer();
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("¡Oh! Has perdido!")
                .setMessage("¿Qué te gustaría hacer?")
                .setPositiveButton("Ganar", (dialog, which) -> reiniciarJuego())
                .setNegativeButton("Configuración", (dialog, which) -> {
                    // Cambiar a SettingsFragment
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new SettingsFragment())
                            .addToBackStack(null)
                            .commit();

                    // Sincronizar el BottomNavigationView
                    BottomNavigationView navigationView = requireActivity().findViewById(R.id.bottom_navigation);
                    navigationView.setSelectedItemId(R.id.settings); // Seleccionar el ítem de configuración
                })
                .setCancelable(false)
                .show();

    }

    private void reproducirSonido(int resourceId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), resourceId);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release); // Liberar recursos al terminar
        mediaPlayer.start();
    }


}