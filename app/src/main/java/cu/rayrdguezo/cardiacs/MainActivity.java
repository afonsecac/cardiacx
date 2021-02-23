package cu.rayrdguezo.cardiacs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data.Data;
import cu.rayrdguezo.cardiacs.terceros.twintrac.cs.data.Patient;
import cu.rayrdguezo.cardiacs.utiles.DatabaseSave;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    public static final String PREFS_FILE = "CardioScoutPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                //startBT12Receiver("00:04:3E:9C:28:50",4,false);
                startConexion("00:04:3E:9C:28:50",4,false);

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_online_control) {

            Intent intent = new Intent(this, BuscarDispositivosActivity.class);
            intent.putExtra(BuscarDispositivosActivity.EXTRA_SHOULD_START_RECEIVER, true);
            startActivityForResult(intent, BuscarDispositivosActivity.ACTIVITY_REQUESTCODE_INQUIRY);

        }else if(id == R.id.nav_offline_control){

            Intent intent = new Intent(this, BuscarDispositivosActivity.class);
            intent.putExtra(BuscarDispositivosActivity.EXTRA_SHOULD_START_RECEIVER, false);
            startActivityForResult(intent, BuscarDispositivosActivity.ACTIVITY_REQUESTCODE_INQUIRY);

        }else if (id == R.id.nav_slideshow){
            crearPaciente();
        }else if (id == R.id.nav_guardar_btaddress){
            saveBTAddress();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {

            case BuscarDispositivosActivity.ACTIVITY_REQUESTCODE_INQUIRY:
            {
                if (resultCode == RESULT_OK)
                {
                    Bundle bundle = data.getExtras();
                    // Select ECG type (2-channel/8-channel)
                    int numberChannels = bundle.getInt(BuscarDispositivosActivity.EXTRA_NUMBER_OF_CHANNELS);
                    int type = 0;
                    if (numberChannels == 2)
                    {
                        //type = BTRecibirGraficarActivity.TwinTrac;
                        type = 0x4;
                    }

                    String btAddress = bundle.getString(BuscarDispositivosActivity.INTENT_RESULTEXTRA_DEVICEADDRESS);
                    startBT12Receiver(btAddress, type, true);
                }

                break;
            }
            default:
            {
                break;
            }
        }



    }

    private void startBT12Receiver(String btAddress, int device,
                                   boolean onlineControl)
    {
        Intent bt12ReceiverGraphIntent = new Intent(MainActivity.this,
                EstabConexRecibirDatosActivity.class);

        bt12ReceiverGraphIntent.putExtra(
                EstabConexRecibirDatosActivity.EXTRAKEY_ECGDEVICE_BTADDRESS, btAddress);
        bt12ReceiverGraphIntent.putExtra(
                EstabConexRecibirDatosActivity.EXTRAKEY_ECGDEVICE_TYPE, device);
        bt12ReceiverGraphIntent.putExtra(
                EstabConexRecibirDatosActivity.ONLINE_CONTROL, onlineControl);
        startActivity(bt12ReceiverGraphIntent);


        Toast.makeText(MainActivity.this, "startBT12Receiver",Toast.LENGTH_SHORT).show();
    }

    public void startConexion(String btAddress, int device,
                              boolean onlineControl){
        Intent bt12ReceiverGraphIntent = new Intent(MainActivity.this, EstabConexRecibirDatosActivity.class);

        Patient patient = DatabaseSave.paciente(getApplicationContext());

        bt12ReceiverGraphIntent.putExtra(EstabConexRecibirDatosActivity.EXTRAKEY_ECGDEVICE_BTADDRESS, btAddress);
        bt12ReceiverGraphIntent.putExtra(EstabConexRecibirDatosActivity.EXTRAKEY_ECGDEVICE_TYPE, device);
        bt12ReceiverGraphIntent.putExtra("Patient", patient );
        bt12ReceiverGraphIntent.putExtra(EstabConexRecibirDatosActivity.ONLINE_CONTROL, onlineControl);
        startActivity(bt12ReceiverGraphIntent);
    }

    private void crearPaciente(){



        Calendar cal = Calendar.getInstance();
        cal.set(1990, 10,
                20, 0, 0, 0);

        // Build patient
        Patient patient = new Patient();
        patient.getAddress().setFirstname("Juan");
        patient.getAddress().setLastname("Ruiz");
        patient.setPatientID("1234");
        patient.setBirthdate(cal.getTime());
        patient.setSex(Patient.Sex.valueOf("Male"));

        DatabaseSave.salvarPaciente(patient,getApplicationContext());

    }

    private void saveBTAddress()
    {
        SharedPreferences preferences = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(EstabConexRecibirDatosActivity.EXTRAKEY_ECGDEVICE_BTADDRESS, "00:04:3E:9C:28:50");
        editor.putInt(EstabConexRecibirDatosActivity.EXTRAKEY_ECGDEVICE_TYPE, EstabConexRecibirDatosActivity.TwinTrac);
        editor.commit();
    }
}