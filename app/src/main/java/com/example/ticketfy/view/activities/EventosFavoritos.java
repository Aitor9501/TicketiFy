package com.example.ticketfy.view.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.example.ticketfy.R;
import com.example.ticketfy.data.db.AppDatabase;
import com.example.ticketfy.data.db.entities.Artista;
import com.example.ticketfy.data.db.entities.Favorito;
import com.example.ticketfy.data.db.entities.Usuario;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class EventosFavoritos extends BaseActivity  {

    private AppDatabase db;
    private LinearLayout layoutFavoritos;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventos_favoritos);
        configurarCabecera();

        layoutFavoritos = findViewById(R.id.layoutFavoritos);

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "ticketify-db")
                .allowMainThreadQueries()
                .build();

        SharedPreferences prefs = getSharedPreferences("TicketifyPrefs", MODE_PRIVATE);
        String email = prefs.getString("emailUsuario", null);
        if (email == null) return;

        Usuario usuario = db.usuarioDao().buscarPorEmail(email);
        if (usuario == null) return;

        List<Favorito> favoritos = db.favoritoDao().obtenerFavoritosPorUsuario(usuario.idUsuario);

        if (favoritos == null || favoritos.isEmpty()) {
            mostrarDialogoSinFavoritos();
            return;
        }

        for (Favorito favorito : favoritos) {
            Artista artista = db.artistaDao().obtenerPorId(favorito.artistaId);
            if (artista != null) {
                agregarTarjetaArtista(artista, usuario.idUsuario);
            }
        }
    }

    private void agregarTarjetaArtista(Artista artista, int idUsuario) {
        View tarjeta = getLayoutInflater().inflate(R.layout.item_favorito, layoutFavoritos, false);

        ImageView imagen = tarjeta.findViewById(R.id.imagenArtistaFavorito);
        TextView nombre = tarjeta.findViewById(R.id.nombreArtistaFavorito);
        ImageButton btnQuitar = tarjeta.findViewById(R.id.btnQuitarFavorito);
        MaterialButton btnEventos = tarjeta.findViewById(R.id.btnEventosFavoritos);

        Glide.with(this).load(artista.imagen).into(imagen);
        nombre.setText(artista.nombre);

        btnQuitar.setOnClickListener(v -> {
            db.favoritoDao().eliminar(new Favorito(idUsuario, artista.idArtista));
            layoutFavoritos.removeView(tarjeta);

            if (layoutFavoritos.getChildCount() == 0) {
                mostrarDialogoSinFavoritos();
            }
        });

        btnEventos.setOnClickListener(v -> {
            Intent intent = new Intent(EventosFavoritos.this, InfoArtistaFestival.class);
            intent.putExtra("idArtista", artista.idArtista);
            startActivity(intent);
        });

        layoutFavoritos.addView(tarjeta);
    }

    private void mostrarDialogoSinFavoritos() {
        View vistaDialogo = getLayoutInflater().inflate(R.layout.dialog_favoritos, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(vistaDialogo)
                .setCancelable(false)
                .create();

        TextView botonAceptar = vistaDialogo.findViewById(R.id.dialogBotonAceptar);
        botonAceptar.setOnClickListener(v -> {
            dialog.dismiss();
            finish(); // Cierra la actividad
        });

        dialog.show();
    }
}
