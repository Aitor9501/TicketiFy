package com.example.ticketfy.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ticketfy.data.db.entities.Evento;

import java.util.List;

@Dao
public interface EventoDao {
    @Insert
    long insertar(Evento evento);

    @Query("SELECT * FROM Evento")
    List<Evento> obtenerTodos();

    @Query("SELECT * FROM Evento WHERE idEvento = :id")
    Evento obtenerPorId(int id);

    @Query("SELECT * FROM Evento WHERE idArtista = :idArtista")
    List<Evento> obtenerPorArtista(int idArtista);
    @Query("DELETE FROM Evento")
    void eliminarTodos();

    @Query("SELECT * FROM Evento WHERE tipoEvento = :tipo")
    List<Evento> obtenerPorTipo(String tipo);

    @Query("SELECT * FROM Evento WHERE idArtista IN (SELECT idArtista FROM Artista WHERE LOWER(nombre) = LOWER(:nombreArtista))")
    List<Evento> obtenerPorNombreArtista(String nombreArtista);

}
