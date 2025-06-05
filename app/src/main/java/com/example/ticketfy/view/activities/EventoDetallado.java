package com.example.ticketfy.view.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.ticketfy.R;
import com.example.ticketfy.auxiliares.EventoApi;
import com.example.ticketfy.data.db.AppDatabase;
import com.example.ticketfy.data.db.entities.Artista;
import com.example.ticketfy.view.adapters.EventoApiAdapter;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class EventoDetallado extends BaseActivity {

    private RecyclerView recyclerView;
    private EventoApiAdapter adapter;
    private final List<EventoApi> eventosApiNacionales = new ArrayList<>();
    private final List<EventoApi> eventosApiInternacionales = new ArrayList<>();
    private final HashSet<String> fechasEventos = new HashSet<>();

    private static final String API_KEY = "Ji0UBCiGG3lGojOWOCqMbv3H4nmtkds0";

    private boolean mostrarSoloNacionales = true; // Cambia a false si quieres mostrar los internacionales

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evento_detallado);
        configurarCabecera();
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mostrarSoloNacionales = tab.getPosition() == 0;
                actualizarLista();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
        int idArtista = getIntent().getIntExtra("idArtista", -1);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "ticketify-db")
                .allowMainThreadQueries()
                .build();

        Artista artista = db.artistaDao().obtenerPorId(idArtista);
        ImageView bannerArtista = findViewById(R.id.bannerArtista);

        if (artista != null && artista.imagen != null && !artista.imagen.isEmpty()) {
            Glide.with(this).load(artista.imagen).centerCrop().into(bannerArtista);
        }

        recyclerView = findViewById(R.id.recyclerEventosDetallado);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventoApiAdapter(this, new ArrayList<>()); // se rellenará luego
        recyclerView.setAdapter(adapter);

        if (artista != null) {
            cargarEventosDesdeApi(artista.nombre);
        }
    }

    private void cargarEventosDesdeApi(String nombreArtista) {
        cargarPagina(nombreArtista, 0);
    }

    private void cargarPagina(String nombreArtista, int pagina) {
        String url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=" + API_KEY +
                "&classificationName=music&keyword=" + nombreArtista + "&page=" + pagina + "&size=20";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject embedded = response.optJSONObject("_embedded");
                        if (embedded != null) {
                            JSONArray events = embedded.getJSONArray("events");

                            for (int i = 0; i < events.length(); i++) {
                                JSONObject eventoJson = events.getJSONObject(i);
                                JSONObject venue = eventoJson.getJSONObject("_embedded")
                                        .getJSONArray("venues").getJSONObject(0);

                                // Obtener país
                                String nombrePais = "";
                                if (venue.has("country") && venue.optJSONObject("country") != null) {
                                    nombrePais = venue.optJSONObject("country").optString("name", "");
                                }

                                String nombreEvento = eventoJson.optString("name");
                                String urlCompra = eventoJson.optString("url", "");

                                JSONArray images = eventoJson.optJSONArray("images");
                                String imagenUrl = (images != null && images.length() > 0)
                                        ? images.getJSONObject(0).getString("url")
                                        : "";

                                String ubicacion = venue.optString("name", "");
                                String fecha = eventoJson.optJSONObject("dates")
                                        .optJSONObject("start")
                                        .optString("localDate", "");

                                if (fechasEventos.contains(fecha)) continue;
                                fechasEventos.add(fecha);

                                boolean esNacional = "Spain".equalsIgnoreCase(nombrePais);
                                EventoApi evento = new EventoApi(nombreEvento, imagenUrl, ubicacion, fecha, urlCompra, esNacional);

                                if ("Spain".equalsIgnoreCase(nombrePais)) {
                                    eventosApiNacionales.add(evento);
                                } else {
                                    eventosApiInternacionales.add(evento);
                                }
                            }

                            actualizarLista();

                            JSONObject pageInfo = response.optJSONObject("page");
                            if (pageInfo != null) {
                                int totalPages = pageInfo.optInt("totalPages", 1);
                                int paginaActual = pageInfo.optInt("number", 0);

                                if (paginaActual + 1 < totalPages) {
                                    cargarPagina(nombreArtista, paginaActual + 1);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("TICKETMASTER", "Error procesando eventos API", e);
                    }
                },
                error -> Log.e("TICKETMASTER", "Error en la API", error)
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void actualizarLista() {
        List<EventoApi> lista = mostrarSoloNacionales ? eventosApiNacionales : eventosApiInternacionales;

        lista.sort((e1, e2) -> {
            try {
                Date f1 = new SimpleDateFormat("yyyy-MM-dd").parse(e1.fecha);
                Date f2 = new SimpleDateFormat("yyyy-MM-dd").parse(e2.fecha);
                return f1.compareTo(f2);
            } catch (Exception e) {
                return 0;
            }
        });

        adapter = new EventoApiAdapter(this, lista);
        recyclerView.setAdapter(adapter);
    }
}
