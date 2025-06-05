package com.example.ticketfy.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ticketfy.data.db.entities.Artista;

import java.util.List;

@Dao
public interface ArtistaDao {
    @Insert
    long insertar(Artista artista);

    @Query("SELECT * FROM Artista")
    List<Artista> obtenerTodos();

    @Query("SELECT * FROM Artista WHERE idArtista = :id")
    Artista obtenerPorId(int id);

    @Query("DELETE FROM Evento")
    void eliminarTodos();
    @Query("SELECT * FROM Artista WHERE LOWER(nombre) = LOWER(:nombre) LIMIT 1")
    Artista buscarPorNombre(String nombre);


}