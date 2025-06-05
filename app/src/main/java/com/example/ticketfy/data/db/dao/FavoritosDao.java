package com.example.ticketfy.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ticketfy.data.db.entities.Favorito;

import java.util.List;

@Dao
public interface FavoritosDao {
    @Insert
    void insertar(Favorito favorito);

    @Delete
    void eliminar(Favorito favorito);

    @Query("SELECT * FROM Favorito WHERE usuarioId = :usuarioId")
    List<Favorito> obtenerFavoritosPorUsuario(int usuarioId);

    @Query("SELECT EXISTS(SELECT 1 FROM Favorito WHERE usuarioId = :usuarioId AND artistaId = :artistaId)")
    boolean esFavorito(int usuarioId, int artistaId);
}