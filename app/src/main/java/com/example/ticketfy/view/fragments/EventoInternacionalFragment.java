package com.example.ticketfy.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.ticketfy.R;
import com.example.ticketfy.data.db.AppDatabase;
import com.example.ticketfy.data.db.entities.Artista;
import com.example.ticketfy.data.db.entities.Evento;
import com.example.ticketfy.data.db.entities.Ubicacion;
import com.example.ticketfy.view.adapters.EventoAdapter;

import java.util.ArrayList;
import java.util.List;

public class EventoInternacionalFragment extends Fragment {

    private RecyclerView recyclerInternacional;
    private EventoAdapter adapter;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evento_internacional, container, false);

        recyclerInternacional = view.findViewById(R.id.recyclerInternacional);
        recyclerInternacional.setLayoutManager(new LinearLayoutManager(getContext()));

        db = Room.databaseBuilder(requireContext(), AppDatabase.class, "ticketify-db")
                .allowMainThreadQueries()
                .build();

        cargarEventosInternacionales();

        return view;
    }

    private void cargarEventosInternacionales() {
        List<Evento> eventos = db.eventoDao().obtenerPorTipo("Festival");
        List<Evento> festivalesInternacionales = new ArrayList<>();

        for (Evento evento : eventos) {
            Ubicacion ubicacion = db.ubicacionDao().obtenerPorId(evento.idUbicacion);
            if (ubicacion != null && !ubicacion.lugar.toLowerCase().contains("españa")) {
                festivalesInternacionales.add(evento);
            }
        }

        adapter = new EventoAdapter(requireContext(), festivalesInternacionales, db);
        recyclerInternacional.setAdapter(adapter);
    }
}
