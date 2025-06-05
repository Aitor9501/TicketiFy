package com.example.ticketfy.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.ticketfy.R;
import com.example.ticketfy.data.db.AppDatabase;
import com.example.ticketfy.data.db.entities.Usuario;

public class Registro extends AppCompatActivity {

    EditText editNombre, editEmail, editContrasena, editRepetirContrasena;
    Button botonRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_menu);

        editNombre = findViewById(R.id.editTextNombre);
        editEmail = findViewById(R.id.editTextEmail);
        editContrasena = findViewById(R.id.editTextPassword);
        editRepetirContrasena = findViewById(R.id.editTextRepeatPassword);
        botonRegistrar = findViewById(R.id.button);

        botonRegistrar.setOnClickListener(v -> {
            String nombre = editNombre.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String contrasena = editContrasena.getText().toString().trim();
            String repetirContrasena = editRepetirContrasena.getText().toString().trim();

            if (nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty() || repetirContrasena.isEmpty()) {
                Toast.makeText(Registro.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(Registro.this, "Introduce un correo electrónico válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!contrasena.equals(repetirContrasena)) {
                Toast.makeText(Registro.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }



            AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                            AppDatabase.class, "ticketify-db")

                    .allowMainThreadQueries()
                    .build();

            Usuario usuario = new Usuario();
            usuario.nombreUsuario = nombre;
            usuario.email = email;
            usuario.contrasena = contrasena;
            usuario.imagen = "";

            long id = db.usuarioDao().insertar(usuario);

            if (id > 0) {
                SharedPreferences prefs = getSharedPreferences("TicketifyPrefs", MODE_PRIVATE);
                prefs.edit().putString("emailUsuario", email).apply();

                Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Registro.this, MenuPrincipal.class));
                finish();
            } else {
                Toast.makeText(Registro.this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
