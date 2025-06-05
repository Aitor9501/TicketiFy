package com.example.ticketfy.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.example.ticketfy.R;
import com.example.ticketfy.data.db.AppDatabase;

import com.example.ticketfy.data.db.entities.Artista;
import com.example.ticketfy.data.db.entities.Entrada;
import com.example.ticketfy.data.db.entities.Evento;
import com.example.ticketfy.view.adapters.ScrollHorizontalAdapter;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DetalleConcierto extends BaseActivity  {

    private ActivityResultLauncher<Intent> launcherCompra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.concierto_detallado);
        configurarCabecera();

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
        TextView pregunta3 = findViewById(R.id.pregunta3);
        TextView respuesta3 = findViewById(R.id.respuesta3);
        pregunta3.setOnClickListener(v -> {
            respuesta3.setVisibility(respuesta3.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });

        TextView pregunta4 = findViewById(R.id.pregunta4);
        TextView respuesta4 = findViewById(R.id.respuesta4);
        pregunta4.setOnClickListener(v -> {
            respuesta4.setVisibility(respuesta4.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });

        TextView pregunta5 = findViewById(R.id.pregunta5);
        TextView respuesta5 = findViewById(R.id.respuesta5);
        pregunta5.setOnClickListener(v -> {
            respuesta5.setVisibility(respuesta5.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });
        launcherCompra = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        new AlertDialog.Builder(DetalleConcierto.this)
                                .setTitle("Entrada guardada")
                                .setMessage("Tu entrada ha sido registrada correctamente.")
                                .setPositiveButton("Ver entrada", (dialog, which) -> {

                                    SharedPreferences prefs = getSharedPreferences("TicketifyPrefs", MODE_PRIVATE);
                                    String email = prefs.getString("emailUsuario", null);

                                    String nombreEvento = getIntent().getStringExtra("nombreArtista");
                                    String fecha = getIntent().getStringExtra("fecha");
                                    String ubicacion = getIntent().getStringExtra("ubicacion");
                                    String codigoEntrada = UUID.randomUUID().toString();

                                    int idEvento = getIntent().getIntExtra("idEvento", -1);
                                    double precio = 25.00;

                                    if (email != null) {
                                        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                                                        AppDatabase.class, "ticketify-db")
                                                .allowMainThreadQueries()
                                                .build();

                                        if ((fecha == null || fecha.isEmpty()) || (ubicacion == null || ubicacion.isEmpty())) {
                                            Evento evento = db.eventoDao().obtenerPorId(idEvento);
                                            if (evento != null) {
                                                if (fecha == null || fecha.isEmpty()) fecha = evento.fecha;

                                                if (ubicacion == null || ubicacion.isEmpty()) {
                                                    if (evento.idUbicacion > 0) {
                                                        ubicacion = db.ubicacionDao().obtenerPorId(evento.idUbicacion).nombre;
                                                    }
                                                }
                                            }
                                        }

                                        Entrada entrada = new Entrada(
                                                idEvento,
                                                precio,
                                                nombreEvento,
                                                fecha,
                                                ubicacion,
                                                codigoEntrada,
                                                email
                                        );
                                        db.entradaDao().insertar(entrada);
                                    }

                                    Intent intent = new Intent(DetalleConcierto.this, EntradaEvento.class);
                                    intent.putExtra("nombreEvento", nombreEvento);
                                    intent.putExtra("fecha", fecha);
                                    intent.putExtra("ubicacion", ubicacion);
                                    startActivity(intent);
                                })
                                .setNegativeButton("Cerrar", null)
                                .show();
                    }
                });

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ScrollView scrollView = findViewById(R.id.scrollView);
        Intent intent = getIntent();
        String imagen = intent.getStringExtra("imagenEvento");
        String nombre = intent.getStringExtra("nombreArtista");
        String ubicacion = intent.getStringExtra("ubicacion");
        String urlCompra = intent.getStringExtra("urlCompra");

        ImageView banner = findViewById(R.id.banner);
        if (imagen != null && !imagen.isEmpty()) {
            Glide.with(this).load(imagen).centerCrop().into(banner);
            RecyclerView recyclerEventos = findViewById(R.id.recyclerEventosHorizontales);
            recyclerEventos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

// Obtener eventos del artista desde tu base de datos
            AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                            AppDatabase.class, "ticketify-db")
                    .allowMainThreadQueries()
                    .build();

            int idArtistaActual = getIntent().getIntExtra("idArtista", -1);

            List<Artista> todosArtistas = db.artistaDao().obtenerTodos();
            List<Artista> otrosArtistas = new ArrayList<>();

            for (Artista a : todosArtistas) {
                if (a.idArtista != idArtistaActual) {
                    otrosArtistas.add(a);
                }
            }

            ScrollHorizontalAdapter adapter = new ScrollHorizontalAdapter(this, otrosArtistas);
            recyclerEventos.setAdapter(adapter);
        } else {
            banner.setImageResource(R.drawable.fondoconcierto);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int targetViewId;

                switch (tab.getPosition()) {
                    case 0:
                        targetViewId = R.id.section1;
                        break;
                    case 1:
                        targetViewId = R.id.section2;
                        break;
                    case 2:
                        targetViewId = R.id.section3;
                        break;
                    default:
                        targetViewId = R.id.section1;
                        break;
                }

                final View targetView = findViewById(targetViewId);
                scrollView.post(() -> scrollView.smoothScrollTo(0, targetView.getTop()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        ImageButton infoBtn = findViewById(R.id.btnInfoGeneral);
        infoBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(DetalleConcierto.this)
                    .setTitle("Entradas Generales")
                    .setMessage("Incluye acceso general al evento.\nNo incluye zona VIP ni extras.")
                    .setPositiveButton("Cerrar", null)
                    .show();
        });

        ImageButton infoBtnVIP = findViewById(R.id.btnInfoVip);
        infoBtnVIP.setOnClickListener(v -> {
            new AlertDialog.Builder(DetalleConcierto.this)
                    .setTitle("Entradas VIP")
                    .setMessage("Incluye acceso general al evento.\nAcceso prioritario al evento con ubicaciones numeradas frente al escenario.")
                    .setPositiveButton("Cerrar", null)
                    .show();
        });
    }

    public void comprarEntradasGenerales(View view) {
        String urlCompra = getIntent().getStringExtra("urlCompra");

        Intent i = new Intent(DetalleConcierto.this, WebComprar.class);
        i.putExtra("url", urlCompra);
        launcherCompra.launch(i);
    }
}
