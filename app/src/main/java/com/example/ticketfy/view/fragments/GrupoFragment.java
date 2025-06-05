package com.example.ticketfy.view.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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

public class GrupoFragment extends Fragment {

    private AppDatabase database;
    private RecyclerView recyclerGrupo;
    private ArtistaAdapter adapter;
    private List<Artista> artistas;
    private List<Artista> todosLosArtistas;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_artista, container, false);

        database = Room.databaseBuilder(requireContext(), AppDatabase.class, "ticketify-db")
                .allowMainThreadQueries()
                .build();

        recyclerGrupo = v.findViewById(R.id.recyclerEventos);
        recyclerGrupo.setLayoutManager(new LinearLayoutManager(requireContext()));

        cargarArtistasGrupo();

        return v;
    }

    private void cargarArtistasGrupo() {
        List<Evento> eventos = database.eventoDao().obtenerTodos();
        todosLosArtistas = new ArrayList<>();
        List<Integer> artistasAgregados = new ArrayList<>();

        Set<String> nombresAgregados = new HashSet<>();
        for (Evento e : eventos) {
            if (!e.tipoEvento.toLowerCase().contains("festival")) {
                Artista artista = database.artistaDao().obtenerPorId(e.idArtista);
                if (artista != null && !nombresAgregados.contains(artista.nombre.toLowerCase())) {
                    todosLosArtistas.add(artista);
                    nombresAgregados.add(artista.nombre.toLowerCase());
                }
            }
        }

        artistas = new ArrayList<>(todosLosArtistas);
        adapter = new ArtistaAdapter(requireContext(), artistas);
        recyclerGrupo.setAdapter(adapter);
        recyclerGrupo.setAdapter(adapter);
        recyclerGrupo.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void buscar(String texto) {
        List<Artista> filtrados = new ArrayList<>();

        if (TextUtils.isEmpty(texto)) {
            filtrados.addAll(todosLosArtistas);
        } else {
            for (Artista artista : todosLosArtistas) {
                if (artista.nombre.toLowerCase().contains(texto.toLowerCase())) {
                    filtrados.add(artista);
                }
            }
        }

        adapter = new ArtistaAdapter(requireContext(), filtrados);
        recyclerGrupo.setAdapter(adapter);
    }
}