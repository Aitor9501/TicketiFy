package com.example.ticketfy.view.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.ticketfy.R;
import com.example.ticketfy.data.db.AppDatabase;
import com.example.ticketfy.data.db.entities.Evento;
import com.example.ticketfy.view.adapters.EventoAdapter;

import java.util.List;

public class ItemEventos extends BaseActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_eventos); // Asegúrate de tener este layout creado
        configurarCabecera();

        int idArtista = getIntent().getIntExtra("idArtista", -1);
        if (idArtista == -1) {
            Log.e("ItemEventos", "ID de artista no recibido");
            return;
        }

        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "ticketify-db")
                .allowMainThreadQueries()
                .build();

        List<Evento> eventosDelArtista = db.eventoDao().obtenerPorArtista(idArtista);

        RecyclerView recyclerView = findViewById(R.id.recyclerEventosArtista); // Asegúrate que esté en el layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        EventoAdapter adapter = new EventoAdapter(this, eventosDelArtista, db);
        recyclerView.setAdapter(adapter);
    }
}