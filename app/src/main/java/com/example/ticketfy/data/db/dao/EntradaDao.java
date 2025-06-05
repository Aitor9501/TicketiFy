package com.example.ticketfy.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import com.example.ticketfy.data.db.entities.Entrada;

import java.util.List;

@Dao
public interface EntradaDao {

    @Insert
    void insertar(Entrada entrada);

    @Query("SELECT * FROM Entrada")
    List<Entrada> obtenerTodas();

    @Query("SELECT * FROM Entrada WHERE idEvento = :idEvento")
    List<Entrada> obtenerPorEvento(int idEvento);

    @Delete
    void eliminar(Entrada entrada);
    @Query("SELECT * FROM Entrada WHERE emailUsuario = :email")
    List<Entrada> obtenerPorEmail(String email);
}
