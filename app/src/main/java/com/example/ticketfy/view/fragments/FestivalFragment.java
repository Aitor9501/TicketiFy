package com.example.ticketfy.view.fragments;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.ticketfy.view.adapters.ArtistaAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FestivalFragment extends Fragment {

    private RecyclerView recyclerFestivales;
    private ArtistaAdapter adapter;
    private AppDatabase database;
    private List<Artista> artistasFestivales;
    private List<Artista> todosLosArtistasFestivales;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_festival, container, false);

        database = Room.databaseBuilder(requireContext(), AppDatabase.class, "ticketify-db")
                .allowMainThreadQueries()
                .build();

        recyclerFestivales = view.findViewById(R.id.recyclerFestivales);
        recyclerFestivales.setLayoutManager(new LinearLayoutManager(getContext()));

        cargarArtistasFestivales();

        return view;
    }

    private void cargarArtistasFestivales() {
        List<Evento> eventos = database.eventoDao().obtenerPorTipo("Festival");
        todosLosArtistasFestivales = new ArrayList<>();
        Set<String> nombresAgregados = new HashSet<>();

        for (Evento e : eventos) {
            Artista artista = database.artistaDao().obtenerPorId(e.idArtista);
            if (artista != null) {
                String nombreNormalizado = artista.nombre.trim().toLowerCase();
                if (!nombresAgregados.contains(nombreNormalizado)) {
                    todosLosArtistasFestivales.add(artista);
                    nombresAgregados.add(nombreNormalizado);
                }
            }
        }

        artistasFestivales = new ArrayList<>(todosLosArtistasFestivales);
        adapter = new ArtistaAdapter(requireContext(), artistasFestivales);
        recyclerFestivales.setAdapter(adapter);
    }


    public void buscarPorNombre(String texto) {
        List<Artista> resultado = new ArrayList<>();

        if (TextUtils.isEmpty(texto)) {
            resultado.addAll(todosLosArtistasFestivales);
        } else {
            for (Artista artista : todosLosArtistasFestivales) {
                if (artista.nombre.toLowerCase().contains(texto.toLowerCase())) {
                    resultado.add(artista);
                }
            }
        }

        adapter = new ArtistaAdapter(requireContext(), resultado);
        recyclerFestivales.setAdapter(adapter);
    }
}
