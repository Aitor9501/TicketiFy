package com.example.ticketfy.view.activities;

import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketfy.R;

public class BaseActivity extends AppCompatActivity {

    protected void configurarCabecera() {
        View logo = findViewById(R.id.logo_toolbar);
        View titulo = findViewById(R.id.tituloToolbar);

        View.OnClickListener volverAlInicio = v -> {
            Intent intent = new Intent(this, MenuPrincipal.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        };

        if (logo != null) logo.setOnClickListener(volverAlInicio);
        if (titulo != null) titulo.setOnClickListener(volverAlInicio);
    }
}