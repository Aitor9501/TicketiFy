package com.example.ticketfy.view.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.ticketfy.R;
import com.example.ticketfy.data.db.AppDatabase;
import com.example.ticketfy.data.db.entities.Entrada;
import com.example.ticketfy.view.adapters.EntradaAdapter;

import java.util.List;

public class MisEntradas extends BaseActivity  {

    private RecyclerView recyclerView;
    private EntradaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entradas_usuario);
        configurarCabecera();

        recyclerView = findViewById(R.id.recyclerEntradas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("TicketifyPrefs", MODE_PRIVATE);
        String email = prefs.getString("emailUsuario", null);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "ticketify-db")
                .allowMainThreadQueries()
                .build();

        List<Entrada> listaEntradas = db.entradaDao().obtenerPorEmail(email);

        if (listaEntradas == null || listaEntradas.isEmpty()) {
            mostrarDialogoSinEntradas();
        } else {
            adapter = new EntradaAdapter(listaEntradas, entrada -> {
                Intent intent = new Intent(MisEntradas.this, EntradaEvento.class);
                intent.putExtra("idEvento", entrada.idEvento);
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);
        }
    }

    private void mostrarDialogoSinEntradas() {
        View vistaDialogo = getLayoutInflater().inflate(R.layout.dialog_entradas, null);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(vistaDialogo)
                .setCancelable(false)
                .create();

        TextView botonAceptar = vistaDialogo.findViewById(R.id.botonAceptarEntradas);
        botonAceptar.setOnClickListener(v -> {
            dialog.dismiss();
            finish(); // Cierra esta pantalla
        });

        dialog.show();
    }
}
