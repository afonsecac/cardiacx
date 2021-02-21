package cu.rayrdguezo.cardiacs;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;

public class EstabConexRecibirDatosActivity extends AppCompatActivity {

    private enum Mode
    {
        THREE_CHANNEL_1,
        THREE_CHANNEL_2,
        THREE_CHANNEL_3,
        THREE_CHANNEL_4,
        SIX_CHANNEL_1,
        SIX_CHANNEL_2,
        TWELVE_CHANNEL
    }

    private final String logTag = getClass().getSimpleName();

    //Va a ser igual a 0x4 por ser twintrac el dispositivo al que nos estamos conectando
    private int device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estab_conex_recibir_datos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //para poner la flecha en el menu de ir atras
        if (getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createBtReceiver(Patient pat, String btAddress,
                                  boolean startReceiverFirstTime)
    {
        try
        {
            if (device == TwinTrac)
            {
                next_mode = Mode.THREE_CHANNEL_1;
                twinTracReceiver = new TwinTracBtReceiver(btAddress, device,
                        onlineControl, this, this, pat, this,
                        startReceiverFirstTime);
                twinTracReceiver.execute(this);
            }
        }
        catch (IOException e)
        {
            Log.e(logTag, e.getLocalizedMessage(), e);
        }
    }
}