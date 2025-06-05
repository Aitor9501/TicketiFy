package com.example.ticketfy.view.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ticketfy.R;
import com.example.ticketfy.data.db.AppDatabase;
import com.example.ticketfy.data.db.entities.Artista;
import com.example.ticketfy.data.db.entities.Evento;
import com.example.ticketfy.data.db.entities.Ubicacion;
import com.example.ticketfy.data.db.entities.Usuario;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Loggin extends AppCompatActivity {
    boolean datosCargados = false;
    EditText editEmail, editPassword;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textView);
        String textoPlano = "¿Primera vez por aquí?\n\tRegístrate YA";
        SpannableString spannable = new SpannableString(textoPlano);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Loggin.this, Registro.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.RED); 
            }
        };


        spannable.setSpan(clickableSpan, 23, textoPlano.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editEmail = findViewById(R.id.editTextText);
        editPassword = findViewById(R.id.editTextText2);
        Button boton = findViewById(R.id.button);

        boton.setOnClickListener(v -> {
            String idUsuarioEmail = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (idUsuarioEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(Loggin.this, "Introduce el correo y la contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                            AppDatabase.class, "ticketify-db")
                    .allowMainThreadQueries()
                    .build();

            Usuario usuario = db.usuarioDao().inciiarSesionCorreoUsuario(idUsuarioEmail);


            if (usuario == null) {
                Toast.makeText(Loggin.this, "No se encontró el usuario. Regístrate primero.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!usuario.contrasena.equals(password)) {
                Toast.makeText(Loggin.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("TicketifyPrefs", MODE_PRIVATE);
            prefs.edit().putString("emailUsuario", idUsuarioEmail).apply();

            if (!datosCargados) {
                cargarEventosDesdeApi();
            }

            Intent intent = new Intent(Loggin.this, MenuPrincipal.class);
            startActivity(intent);
            finish();
        });

        cargarEventosDesdeApi();
    }

    private void cargarEventosDesdeApi() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {

                AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                                AppDatabase.class, "ticketify-db")

                        .allowMainThreadQueries()
                        .build();

                SharedPreferences prefs = getSharedPreferences("TicketifyPrefs", MODE_PRIVATE);
                String email = prefs.getString("emailUsuario", null);

                if (email != null) {
                    Usuario usuario = db.usuarioDao().buscarPorEmail(email);
                    if (usuario != null) {
                        Log.d("USUARIO_ENCONTRADO", "Nombre: " + usuario.nombreUsuario);
                        handler.post(() ->
                                Toast.makeText(getApplicationContext(), "Bienvenido " + usuario.nombreUsuario, Toast.LENGTH_SHORT).show()
                        );
                    }
                }

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                int totalPages = 3;
                CountDownLatch latch = new CountDownLatch(totalPages);
                HashSet<String> artistasInsertados = new HashSet<>();

                for (int page = 0; page < totalPages; page++) {
                    int finalPage = page;
                    String url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=Ji0UBCiGG3lGojOWOCqMbv3H4nmtkds0&countryCode=ES&segmentId=KZFzniwnSyZfZ7v7nJ&classificationName=music&preferredCountry=es&sort=relevance,desc&size=100&page=" + finalPage;

                    Thread.sleep(1000L * finalPage);

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                            response -> {
                                Log.d("API_DEBUG", "Página " + finalPage + " recibida");

                                procesarEventos(response, db, latch, artistasInsertados, handler);
                            },
                            error -> {
                                Log.e("API_ERROR", "Error en página " + finalPage + ": " + error.getMessage());
                                latch.countDown();
                            });

                    queue.add(request);
                }

                latch.await();
                handler.post(() -> {
                    datosCargados = true;
                    Log.d("DEBUG_FINAL", "\uD83C\uDF89 Todos los eventos cargados");
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void procesarEventos(JSONObject response, AppDatabase db, CountDownLatch latch, HashSet<String> artistasInsertados, Handler handler) {
        new Thread(() -> {
            try {
                JSONArray events = response.getJSONObject("_embedded").getJSONArray("events");
                for (int i = 0; i < events.length(); i++) {
                    JSONObject eventoJson = events.getJSONObject(i);
                    JSONArray attractionsArray = eventoJson.getJSONObject("_embedded").optJSONArray("attractions");
                    if (attractionsArray == null || attractionsArray.length() == 0) continue;
                    JSONObject artistaJson = attractionsArray.getJSONObject(0);
                    String nombreArtista = artistaJson.optString("name", "Artista sin nombre").toLowerCase().replaceAll("\\s+", " ").trim();
                    String infoArtista = artistaJson.optString("info", "");

                    synchronized (artistasInsertados) {
                        if (artistasInsertados.contains(nombreArtista)) continue;
                        artistasInsertados.add(nombreArtista);
                    }

                    JSONArray classifications = eventoJson.optJSONArray("classifications");
                    String nombreEvento = eventoJson.optString("name", "").toLowerCase();
                    String tipo = "Grupo";

                    if (classifications != null && classifications.length() > 0) {
                        JSONObject typeObject = classifications.getJSONObject(0).optJSONObject("type");
                        if (typeObject != null) {
                            String typeName = typeObject.optString("name", "");
                            if ("Festival".equalsIgnoreCase(typeName)) {
                                tipo = "Festival";
                            }
                        }
                    }

                    if (nombreEvento.contains("festival") || nombreEvento.contains("sound") ||
                            nombreEvento.contains("weekend") || nombreEvento.contains("live")) {
                        tipo = "Festival";
                    }

                    Artista artista = new Artista();
                    artista.nombre = nombreArtista;
                    artista.imagen = eventoJson.getJSONArray("images").getJSONObject(0).getString("url");
                    artista.infoArtista = infoArtista.isEmpty() ? "Consulta más en la web oficial:" : infoArtista;

                    JSONObject venue = eventoJson.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0);
                    Ubicacion ubicacion = new Ubicacion();
                    ubicacion.nombre = venue.optString("name", "Lugar desconocido");
                    ubicacion.lugar = venue.optJSONObject("city") != null ?
                            venue.optJSONObject("city").optString("name", "Ciudad desconocida") : "Ciudad no disponible";
                    ubicacion.tipo = venue.optString("type", "Sala");
                    ubicacion.imagen = "";

                    long idArtista = db.artistaDao().insertar(artista);
                    long idUbicacion = db.ubicacionDao().insertar(ubicacion);

                    Evento evento = new Evento();
                    evento.idArtista = (int) idArtista;
                    evento.idUbicacion = (int) idUbicacion;
                    evento.categoria = "music";
                    evento.tipoEvento = tipo;

                    db.eventoDao().insertar(evento);
                    Log.d("TICKETMASTER", "Insertado: " + tipo + " - " + artista.nombre);
                }
                handler.post(() -> datosCargados = true);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }).start();
    }
}