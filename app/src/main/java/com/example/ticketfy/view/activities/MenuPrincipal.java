package com.example.ticketfy.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.room.Room;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ticketfy.R;
import com.example.ticketfy.data.db.AppDatabase;
import com.example.ticketfy.data.db.entities.Usuario;
import com.example.ticketfy.view.adapters.FragmentAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MenuPrincipal extends BaseActivity {

    private DrawerLayout drawerLayout;
    private FragmentAdapter adapter;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal);

        // Setup de la Toolbar personalizada
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Elementos personalizados
        TextView titulo = findViewById(R.id.tituloToolbar);
        ImageView logo = findViewById(R.id.logo_toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        // Abrir menú lateral izquierdo al pulsar el logo
        logo.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Recuperar nombre de usuario desde preferencias y mostrar saludo
        prefs = getSharedPreferences("TicketifyPrefs", MODE_PRIVATE);
        String email = prefs.getString("emailUsuario", null);

        if (email != null) {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                            AppDatabase.class, "ticketify-db")
                    .allowMainThreadQueries()
                    .build();
                titulo.setText("TicketiFy");
        }

        // Configurar ViewPager y Tabs
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        adapter = new FragmentAdapter(this);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "Grupo" : "Festival");
        }).attach();

        // Drawer lateral izquierdo
        NavigationView navPrincipal = findViewById(R.id.nav_view);
        navPrincipal.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Drawer lateral derecho
        NavigationView navPerfil = findViewById(R.id.nav_view_perfil);
        navPerfil.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_mi_perfil) {
                    startActivity(new Intent(MenuPrincipal.this, PerfilEditar.class));
                } else if (id == R.id.nav_favoritos) {
                    startActivity(new Intent(MenuPrincipal.this, EventosFavoritos.class));
                } else if (id == R.id.nav_mis_entradas) {
                    startActivity(new Intent(MenuPrincipal.this, MisEntradas.class));
                } else if (id == R.id.nav_cerrar_sesion) {
                    prefs.edit().remove("emailUsuario").apply();
                    Intent intent = new Intent(MenuPrincipal.this, Loggin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    return false;
                }
                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cabecera, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("Buscar artista o evento...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscar(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                buscar(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_perfil) {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void buscar(String texto) {
        int position = getCurrentItem();
        if (position == 0) {
            adapter.getGrupoFragment().buscar(texto);
        } else if (position == 1) {
            adapter.getFestivalFragment().buscarPorNombre(texto);
        }
    }

    private int getCurrentItem() {
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        return viewPager.getCurrentItem();
    }
}
