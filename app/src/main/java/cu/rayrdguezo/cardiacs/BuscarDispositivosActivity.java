package cu.rayrdguezo.cardiacs;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import cu.rayrdguezo.cardiacs.modulos.listardispositivos.adapter.RecyclerBuscarDispositivosAdapter;
import cu.rayrdguezo.cardiacs.modulos.listardispositivos.model.BuscarDispositivoModel;
import cu.rayrdguezo.cardiacs.utiles.MyProgressDialog;
import cu.rayrdguezo.cardiacs.utiles.RecyclerItemClickListener;

public class BuscarDispositivosActivity extends AppCompatActivity implements RecyclerItemClickListener {

    public final String[][] deviceList = new String[][] {
            {
                "SRM TwinTrac",
                "2"
            },
            /*{
                "SRM TwinTrac",
                "2"
            }*/
    };

    private final BroadcastReceiver inquiryReceiver = new BroadcastReceiver()
    {
        @SuppressWarnings("unchecked")
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if ( BluetoothDevice.ACTION_FOUND.equals(action) )
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("BuscarDispositivo", "dispositivo bluetooth encontrado: " + device.getName() + " (" + device.getAddress() + ")");
                dialog.ocultar();
                //Toast.makeText(context,"Dispositivo bluetooth encontrado: " + device.getName() + " (" + device.getAddress() + ")",Toast.LENGTH_SHORT).show();
                for (int i = 0; i < deviceList.length; i++)
                {
                    if (device.getName() != null
                            && device.getName().contains(deviceList[i][0]))
                    {
                        adapter.add(new BuscarDispositivoModel(device.getName(),device.getAddress()));

                        break;
                    }
                }

            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                dialog.ocultar();
                dd();
                //Toast.makeText(context,"Busqueda finalizada",Toast.LENGTH_SHORT).show();
            }
        }
    };

    public static final int ACTIVITY_REQUESTCODE_INQUIRY = 0x01;

    public static String INTENT_RESULTEXTRA_DEVICEADDRESS = "intentExtras.deviceAddress";

    public static String EXTRA_SHOULD_START_RECEIVER = "ShouldStartReceiver";
    public static String EXTRA_NUMBER_OF_CHANNELS = "numberOfChannelsFromDevice";

    private BluetoothAdapter btAdapter;
    private boolean shouldStartReceiver;

    private MyProgressDialog dialog;

    private RecyclerView recyclerView;
    private RecyclerBuscarDispositivosAdapter adapter;
    private ArrayList<BuscarDispositivoModel> mDataset = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        setResult(Activity.RESULT_CANCELED); // default result is "cancelled"

        lanzarBrodcast();

        shouldStartReceiver = getIntent().getBooleanExtra( EXTRA_SHOULD_START_RECEIVER, false);

        startInquiry();

        setContentView(R.layout.activity_buscar_dispositivos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //para poner la flecha en el menu de ir atras
        if (getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycBuscarDispositivos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        adapter = new RecyclerBuscarDispositivosAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setRecyclerItemClickListener((RecyclerItemClickListener) this);

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
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (btAdapter != null)
        {
            btAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        unregisterReceiver(inquiryReceiver);
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

    @Override
    public void onItemClickRecycler(View view, Object obj, int position) {

        BuscarDispositivoModel dispositivoSeleccionado = (BuscarDispositivoModel) obj;
        //Toast.makeText(this, "Position: " + String.valueOf(position) + "\n Device: " + selectedDevice.getName()+"\n Address: "+ selectedDevice.getAddress(), Toast.LENGTH_LONG).show();

        int cantDeCanales = cantDeCanales(dispositivoSeleccionado);

        // Crear el resultado del Intent e incluir la direccion MAC
        Intent intent = new Intent();
        intent.putExtra(INTENT_RESULTEXTRA_DEVICEADDRESS, dispositivoSeleccionado.getAddress());
        intent.putExtra(EXTRA_SHOULD_START_RECEIVER, shouldStartReceiver);
        intent.putExtra(EXTRA_NUMBER_OF_CHANNELS, cantDeCanales);

        // Modificar el resultado y terminar este Activity
        setResult(Activity.RESULT_OK, intent);
        finish();

    }

    private void startInquiry()
    {
        Log.d(getClass().getSimpleName(), "Iniciar la busqueda de dispositivos");

        // Indicar el buscar dispositivo
        dialog = new MyProgressDialog(this);
        dialog.mostrar("Buscando dispositivos ...");

        // Request discover from BluetoothAdapter
        btAdapter.startDiscovery();
    }

    public void lanzarBrodcast(){
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //BrodcastBuscarDispositivos d = new BrodcastBuscarDispositivos();
        registerReceiver(inquiryReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(inquiryReceiver, filter);
    }

    private int cantDeCanales(BuscarDispositivoModel selectedDevice)
    {
        for (int i = 0; i < deviceList.length; i++)
        {
            if (selectedDevice.getName().contains(deviceList[i][0]))
            {
                return Integer.parseInt(deviceList[i][1]);
            }
        }
        return 0;
    }

    public void dd(){
        int channelNumber = 2;

        // Create the result Intent and include the MAC address
        Intent intent = new Intent();
        intent.putExtra(INTENT_RESULTEXTRA_DEVICEADDRESS,
                "00:04:3E:9C:28:50");
        intent.putExtra(EXTRA_SHOULD_START_RECEIVER, shouldStartReceiver);
        intent.putExtra(EXTRA_NUMBER_OF_CHANNELS, channelNumber);

        // Set result and finish this Activity
        setResult(Activity.RESULT_OK, intent);
        finish();
    }



}