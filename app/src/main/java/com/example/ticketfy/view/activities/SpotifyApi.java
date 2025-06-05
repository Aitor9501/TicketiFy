package com.example.ticketfy.view.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SpotifyApi {

    private static final String CLIENT_ID = "416a0cf40a574d7b92b5d5994c29910c";
    private static final String CLIENT_SECRET = "7d358d9404054c97b3699eff121760e2";
    private static final String PREFS_NAME = "SpotifyPrefs";
    private static final String TOKEN_KEY = "access_token";
    private static final String EXPIRATION_KEY = "expires_at";
    private static final String SEARCH_URL = "https://api.spotify.com/v1/search";

    private final Context context;
    private final RequestQueue queue;

    private String accessToken;

    public SpotifyApi(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
        cargarTokenGuardado();
    }

    private void cargarTokenGuardado() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        accessToken = prefs.getString(TOKEN_KEY, null);
        long expiresAt = prefs.getLong(EXPIRATION_KEY, 0);
        long now = System.currentTimeMillis() / 1000;

        if (accessToken != null && now >= expiresAt) {
            accessToken = null; // Expirado, hay que renovar
        }
    }

    public void buscarArtistaSpotify(String nombreArtista, SpotifyCallback callback) {
        if (accessToken == null) {
            obtenerAccessToken(token -> buscarArtistaSpotify(nombreArtista, callback));
            return;
        }

        String url = SEARCH_URL + "?q=" + nombreArtista + "&type=artist&limit=1";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    JSONArray artistas = response.optJSONObject("artists").optJSONArray("items");
                    if (artistas != null && artistas.length() > 0) {
                        JSONObject artista = artistas.optJSONObject(0);
                        callback.onResult(artista);
                    } else {
                        callback.onError("Artista no encontrado");
                    }
                },
                error -> {
                    Log.e("SPOTIFY", "Error buscando artista");
                    callback.onError("Error en la búsqueda");
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        queue.add(request);
    }

    private void obtenerAccessToken(SpotifyTokenCallback callback) {
        String url = "https://accounts.spotify.com/api/token";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String newToken = jsonResponse.getString("access_token");
                        int expiresIn = jsonResponse.getInt("expires_in");
                        long newExpiresAt = (System.currentTimeMillis() / 1000) + expiresIn;

                        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        prefs.edit()
                                .putString(TOKEN_KEY, newToken)
                                .putLong(EXPIRATION_KEY, newExpiresAt)
                                .apply();

                        accessToken = newToken;
                        callback.onTokenReceived(newToken);
                    } catch (Exception e) {
                        Log.e("SPOTIFY", "Error procesando token", e);
                    }
                },
                error -> Log.e("SPOTIFY", "Error pidiendo token: " + error.getMessage())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", auth);
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", "client_credentials");
                return params;
            }
        };

        queue.add(request);
    }

    public void buscarWikipedia(String nombreArtista, WikipediaCallback callback) {
        String url = "https://en.wikipedia.org/api/rest_v1/page/summary/" + nombreArtista.replace(" ", "_");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    String extract = response.optString("extract", "No se encontró información en Wikipedia.");
                    callback.onResult(extract);
                },
                error -> {
                    Log.e("WIKIPEDIA", "Error consultando Wikipedia: " + error.getMessage());
                    callback.onResult("No se encontró información en Wikipedia.");
                }
        );

        queue.add(request);
    }

    public interface WikipediaCallback {
        void onResult(String resumen);
    }

    public interface SpotifyTokenCallback {
        void onTokenReceived(String token);
    }

    public interface SpotifyCallback {
        void onResult(JSONObject artista);
        void onError(String error);
    }
}
