package com.example.ticketfy.view.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.room.Room;

import com.example.ticketfy.R;
import com.example.ticketfy.data.db.AppDatabase;
import com.example.ticketfy.data.db.entities.Usuario;

public class PerfilEditar extends BaseActivity {

    EditText editNombre, editEmail, editContrasena, editContrasenaActual;
    Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil);
        configurarCabecera();

        editNombre = findViewById(R.id.editNombre);
        editEmail = findViewById(R.id.editEmail);
        editContrasena = findViewById(R.id.editContrasena);
        editContrasenaActual = findViewById(R.id.editContrasenaActual);
        btnGuardar = findViewById(R.id.btnGuardar);

        SharedPreferences prefs = getSharedPreferences("TicketifyPrefs", MODE_PRIVATE);
        String emailActual = prefs.getString("emailUsuario", null);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "ticketify-db")
                .allowMainThreadQueries()
                .build();

        Usuario usuario = db.usuarioDao().buscarPorEmail(emailActual);

        if (usuario != null) {
            editNombre.setText(usuario.nombreUsuario);
            editEmail.setText(usuario.email);
            editContrasena.setText("");
        }

        btnGuardar.setOnClickListener(v -> {
            if (usuario == null) return;

            String nombreNuevo = editNombre.getText().toString().trim();
            String emailNuevo = editEmail.getText().toString().trim();
            String nuevaContra = editContrasena.getText().toString().trim();
            String contraActual = editContrasenaActual.getText().toString().trim();

            if (!Patterns.EMAIL_ADDRESS.matcher(emailNuevo).matches()) {
                Toast.makeText(this, "Introduce un correo electrónico válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (contraActual.isEmpty()) {
                Toast.makeText(this, "Introduce tu contraseña actual para guardar los cambios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!usuario.contrasena.equals(contraActual)) {
                Toast.makeText(this, "La contraseña actual no es correcta", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!nuevaContra.isEmpty()) {
                usuario.contrasena = nuevaContra;
            }

            usuario.nombreUsuario = nombreNuevo;
            usuario.email = emailNuevo;

            db.usuarioDao().actualizar(usuario);
            prefs.edit().putString("emailUsuario", usuario.email).apply();

            Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
