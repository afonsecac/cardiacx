package cu.rayrdguezo.cardiacs;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import cu.rayrdguezo.cardiacs.utiles.MyProgressDialog;

public class BuscarDispositivosActivity extends AppCompatActivity {

    public static final int ACTIVITY_REQUESTCODE_INQUIRY = 0x01;

    public static String INTENT_RESULTEXTRA_DEVICEADDRESS = "intentExtras.deviceAddress";

    public static String EXTRA_SHOULD_START_RECEIVER = "ShouldStartReceiver";
    public static String EXTRA_NUMBER_OF_CHANNELS = "numberOfChannelsFromDevice";

    private BluetoothAdapter btAdapter;
    private boolean shouldStartReceiver;

    private MyProgressDialog dialog;

    private RecyclerView recyclerView;
    //private RecyclerBuscarDispositivosAdapter adapter;
    //private ArrayList<DispositivoModel> mDataset = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_dispositivos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


}