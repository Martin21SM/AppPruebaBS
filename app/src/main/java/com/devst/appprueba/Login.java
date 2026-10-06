package com.devst.appprueba;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class Login extends AppCompatActivity {

    //Creación de Variables Encapsuladas
    Button btnPaginaWeb;
    Button btnGaleria;
    Button btnConfiguracionWifi;
    Button btnCamaraFrontal;
    Button btnAbrirContactos;

    //Campo y botón para el marcador telefónico (con validación)
    EditText etTelefono;
    Button btnLlamar;

    //Botones de los intents explícitos
    Button btnAjustes;
    Button btnDetalle;
    Button btnSwitch;
    Spinner spOpciones;

    //Vista para mostrar el resultado de la galería
    ImageView imgResultado;

    //Variables para Ubicación
    LocationManager locationManager;
    double latitud = 0;
    double longitud = 0;
    boolean ubicacionObtenida = false;

    //Permisos (Codigo)
    final int PERMISO_UBICACION = 100;

    //Intent Implícito 6: resultado de la galería
    private final ActivityResultLauncher<Intent> galeriaLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getData() != null) {
                            Uri uri = result.getData().getData();
                            if (uri != null) {
                                imgResultado.setImageURI(uri);
                            }
                        }
                    });

    //Intent Implícito 11: resultado de contactos
    private final ActivityResultLauncher<Intent> contactosLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getData() != null) {
                            Uri uri = result.getData().getData();
                            if (uri != null) {
                                mostrarContacto(uri);
                            }
                        }
                    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        //Conexión del XML a Java
        btnPaginaWeb = findViewById(R.id.btnPaginaWeb);
        btnGaleria = findViewById(R.id.btnGaleria);
        btnConfiguracionWifi = findViewById(R.id.btnConfiguracionWifi);
        btnCamaraFrontal = findViewById(R.id.btnCamaraFrontal);
        btnAbrirContactos = findViewById(R.id.btnAbrirContactos);
        btnAjustes = findViewById(R.id.btnAjustes);
        btnDetalle = findViewById(R.id.btnDetalle);
        spOpciones = findViewById(R.id.spOpciones);
        btnSwitch = findViewById(R.id.btnSwitch);
        imgResultado = findViewById(R.id.imgResultado);
        etTelefono = findViewById(R.id.etTelefono);
        btnLlamar = findViewById(R.id.btnLlamar);

        //GIF superior del login
        ImageView gifLogin = findViewById(R.id.gifLogin);
        Glide.with(this).asGif().load(R.drawable.castorgif).into(gifLogin);

        //Creamos los Eventos

        //Intent Explícito 2: Login -> ConfigActivity (ajustes)
        btnAjustes.setOnClickListener(v -> {
            Intent ajustes = new Intent(Login.this, ConfigActivity.class);
            startActivity(ajustes);
        });

        //Intent Explícito 1: Login -> DetalleActivity (con datos extra)
        btnDetalle.setOnClickListener(v -> {
            Intent detalle = new Intent(Login.this, DetalleActivity.class);
            detalle.putExtra("titulo", "Producto de ejemplo");
            detalle.putExtra("descripcion", "Esta es la descripción enviada desde Login con putExtra.");
            startActivity(detalle);
        });

        //Intent Explícito 9: abrir Activity según la opción seleccionada
        String[] opciones = {"Ajustes", "Detalle"};
        spOpciones.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, opciones));

        btnSwitch.setOnClickListener(v -> {
            Class<?> destino;

            switch (spOpciones.getSelectedItemPosition()) {
                case 0:
                    destino = ConfigActivity.class;
                    break;
                default:
                    destino = DetalleActivity.class;
                    break;
            }

            Intent intent = new Intent(Login.this, destino);
            if (destino == DetalleActivity.class) {
                intent.putExtra("titulo", "Abierto desde el Spinner");
                intent.putExtra("descripcion", "Esta pantalla se eligió dinámicamente.");
            }
            startActivity(intent);
        });

        //Intent Implícito 2: abrir página web
        btnPaginaWeb.setOnClickListener(v ->
                lanzar(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://developer.android.com"))));

        //Intent Implícito 6: seleccionar imagen de galería
        btnGaleria.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            try {
                galeriaLauncher.launch(Intent.createChooser(intent, "Elige una imagen"));
            } catch (ActivityNotFoundException e) {
                mostrarMensaje("No hay app para elegir imágenes");
            }
        });

        //Intent Implícito 7: ajustes de Wi-Fi
        btnConfiguracionWifi.setOnClickListener(v ->
                lanzar(new Intent(Settings.ACTION_WIFI_SETTINGS)));

        //Intent Implícito 10: cámara frontal
        btnCamaraFrontal.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
            intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
            lanzar(intent);
        });

        //Intent Implícito 11: abrir contactos
        btnAbrirContactos.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            try {
                contactosLauncher.launch(intent);
            } catch (ActivityNotFoundException e) {
                mostrarMensaje("No hay app de contactos");
            }
        });

        //Intent Implícito 3: marcador telefónico con validación
        btnLlamar.setOnClickListener(v -> {
            String telefono = etTelefono.getText().toString().trim();

            if (telefono.isEmpty()) {
                etTelefono.setError("Ingresa un número de teléfono");
                return;
            }
            if (!telefono.matches("^\\+?[0-9]{8,12}$")) {
                etTelefono.setError("Número inválido (8 a 12 dígitos)");
                return;
            }

            lanzar(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telefono)));
        });
    }

    //Lee nombre y número del contacto elegido y lo muestra en un Toast
    private void mostrarContacto(Uri uri) {
        String[] columnas = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        try (Cursor c = getContentResolver().query(uri, columnas, null, null, null)) {
            if (c != null && c.moveToFirst()) {
                mostrarMensaje("Contacto: " + c.getString(0) + " - " + c.getString(1));
            }
        }
    }

    //Lanza un intent implícito sin que la app se cierre si no hay app que lo maneje
    private void lanzar(Intent intent) {
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            mostrarMensaje("No hay ninguna app que pueda manejar esta acción");
        }
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}