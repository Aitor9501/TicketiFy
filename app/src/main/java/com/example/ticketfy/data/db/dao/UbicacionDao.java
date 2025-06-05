package com.example.ticketfy.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ticketfy.data.db.entities.Ubicacion;

import java.util.List;

@Dao
public interface UbicacionDao {
    @Insert
    long insertar(Ubicacion ubicacion);

    @Query("SELECT * FROM Ubicacion")
    List<Ubicacion> obtenerTodas();

    @Query("SELECT * FROM Ubicacion WHERE idUbicacion = :id")
    Ubicacion obtenerPorId(int id);
    @Query("DELETE FROM Evento")
    void eliminarTodos();
}
