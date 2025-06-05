package com.example.ticketfy.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.ticketfy.R;
import com.example.ticketfy.data.db.AppDatabase;
import com.example.ticketfy.data.db.entities.*;


import org.json.JSONArray;
import org.json.JSONObject;

public class InfoArtistaFestival extends BaseActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_artista_grupo);
        configurarCabecera();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int idEvento = getIntent().getIntExtra("idEvento", -1);
        int idArtista = getIntent().getIntExtra("idArtista", -1);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "ticketify-db")
                .allowMainThreadQueries()
                .build();

        Evento evento = null;
        Artista artista;

        if (idEvento != -1) {
            evento = db.eventoDao().obtenerPorId(idEvento);
            artista = db.artistaDao().obtenerPorId(evento.idArtista);
        } else if (idArtista != -1) {
            artista = db.artistaDao().obtenerPorId(idArtista);
        } else {
            artista = null;
        }
        if (artista == null) return;

        Ubicacion ubicacion = null;
        if (evento != null) {
            ubicacion = db.ubicacionDao().obtenerPorId(evento.idUbicacion);
        }
        TextView textViewBio;

        ImageView imagenEvento = findViewById(R.id.imagenEvento);
        if (artista != null && artista.imagen != null) {
            Glide.with(this)
                    .load(artista.imagen)
                    .centerCrop()
                    .into(imagenEvento);
        }

        TextView nombreArtista = findViewById(R.id.textView3);
        if (artista != null) nombreArtista.setText(artista.nombre);
        SpotifyApi spoty = new SpotifyApi(this);
        textViewBio = findViewById(R.id.textViewBio);

        spoty.buscarArtistaSpotify(artista.nombre, new SpotifyApi.SpotifyCallback() {
            @Override
            public void onResult(JSONObject artistaSpotify) {
                try {
                    String nombreSpotify = artistaSpotify.optString("name");
                    int seguidores = artistaSpotify.optJSONObject("followers").optInt("total");
                    String spotifyId = artistaSpotify.optString("id");

                    Button btnSpotify = findViewById(R.id.btnSpotify);
                    btnSpotify.setOnClickListener(view -> {
                        if (spotifyId != null && !spotifyId.isEmpty()) {
                            String url = "https://open.spotify.com/artist/" + spotifyId;
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            intent.setPackage("com.spotify.music");
                            try {
                                startActivity(intent);
                            } catch (Exception e) {
                                // Si no está instalado Spotify, abre en navegador
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            }
                        }
                    });
                    JSONArray genresArray = artistaSpotify.optJSONArray("genres");
                    String generos;
                    if (genresArray != null && genresArray.length() > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < genresArray.length(); i++) {
                            sb.append(genresArray.getString(i));
                            if (i < genresArray.length() - 1) sb.append(", ");
                        }
                        generos = sb.toString();
                    } else {
                        generos = "Género desconocido";
                    }

                    String resumenSpotify = nombreSpotify + "\nSeguidores: " + seguidores + "\nGéneros: " + generos;

                    // Llamada a Wikipedia en español
                    String nombreFormateado = nombreSpotify.replace(" ", "_");
                    String urlWiki = "https://es.wikipedia.org/api/rest_v1/page/summary/" + nombreFormateado;

                    JsonObjectRequest wikiRequest = new JsonObjectRequest(Request.Method.GET, urlWiki, null,
                            response -> {
                                String extracto = response.optString("extract", "");
                                String bioFinal = resumenSpotify + "\n\n" + extracto;
                                runOnUiThread(() -> textViewBio.setText(bioFinal));
                            },
                            error -> {
                                runOnUiThread(() -> textViewBio.setText(resumenSpotify));
                            });

                    Volley.newRequestQueue(InfoArtistaFestival.this).add(wikiRequest);

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> textViewBio.setText("Información no disponible"));
                }
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> textViewBio.setText("Información no disponible"));
            }
        });

        Button botonEventos = findViewById(R.id.button3);
        botonEventos.setOnClickListener(v -> {
            Intent intent = new Intent(this, EventoDetallado.class);
            intent.putExtra("idArtista", artista.idArtista); // se abrirá por artista
            startActivity(intent);
        });

        TextView pregunta1 = findViewById(R.id.pregunta1);
        TextView respuesta1 = findViewById(R.id.respuesta1);
        pregunta1.setOnClickListener(v -> {
            respuesta1.setVisibility(respuesta1.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });

        TextView pregunta2 = findViewById(R.id.pregunta2);
        TextView respuesta2 = findViewById(R.id.respuesta2);
        pregunta2.setOnClickListener(v -> {
            respuesta2.setVisibility(respuesta2.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });

        ImageButton btnFavorito = findViewById(R.id.btnFavorito);
        btnFavorito.setOnClickListener(v -> {
            guardarFavorito(artista);
        });
    }
    private void guardarFavorito(Artista artista) {
        SharedPreferences prefs = getSharedPreferences("TicketifyPrefs", MODE_PRIVATE);
        String email = prefs.getString("emailUsuario", null);

        if (email != null) {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                            AppDatabase.class, "ticketify-db")
                    .allowMainThreadQueries()
                    .build();

            Usuario usuario = db.usuarioDao().buscarPorEmail(email);
            if (usuario != null) {
                boolean esFavorito = db.favoritoDao().esFavorito(usuario.idUsuario, artista.idArtista);

                if (!esFavorito) {
                    db.favoritoDao().insertar(new Favorito(usuario.idUsuario, artista.idArtista));
                    Toast.makeText(this, "Añadido a favoritos", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Ya estaba en favoritos", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
