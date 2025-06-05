package com.example.ticketfy.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ticketfy.data.db.entities.Usuario;

@Dao
public interface UsuarioDao {
    @Insert
    long insertar(Usuario usuario);

    @Query("SELECT * FROM Usuario WHERE email = :email LIMIT 1")
    Usuario buscarPorEmail(String email);
    @Query("DELETE FROM Usuario")
    void eliminarTodos();
    @Update
    void actualizar(Usuario usuario);

    @Query("SELECT * FROM Usuario WHERE email = :identificador OR nombreUsuario = :identificador LIMIT 1")
    Usuario inciiarSesionCorreoUsuario(String identificador);
}
