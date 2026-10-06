package com.devst.appprueba;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DetalleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        //Evita que el Toolbar quede bajo la barra de estado
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Recibir los datos extra enviados desde Login
        String titulo = getIntent().getStringExtra("titulo");
        String descripcion = getIntent().getStringExtra("descripcion");

        //Validación: los extras no pueden ser null ni estar vacíos
        if (titulo == null || titulo.trim().isEmpty()) {
            titulo = "Sin título";
            Toast.makeText(this, "No se recibió el título", Toast.LENGTH_SHORT).show();
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            descripcion = "Sin descripción disponible";
            Toast.makeText(this, "No se recibió la descripción", Toast.LENGTH_SHORT).show();
        }

        ((TextView) findViewById(R.id.tvTitulo)).setText(titulo);
        ((TextView) findViewById(R.id.tvDescripcion)).setText(descripcion);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}