package cu.rayrdguezo.cardiacs.modulos.datospreeliminares;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

import cu.rayrdguezo.cardiacs.EstabConexRecibirDatosActivity;
import cu.rayrdguezo.cardiacs.OrmLiteFragment;
import cu.rayrdguezo.cardiacs.R;
import cu.rayrdguezo.cardiacs.modulos.detallesdesesion.DetallesDeSesionFragment;
import cu.rayrdguezo.cardiacs.terceros.twintrac.BtReceiverStateListener;
import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.BCTarget;
import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data.Data;
import cu.rayrdguezo.cardiacs.terceros.twintrac.cs.data.Patient;
import cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac.TwinTracBtReceiver;
import cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac.TwinTracConfigManager;
import cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac.TwinTracOnlineDataHolder;
import cu.rayrdguezo.cardiacs.utiles.Constantes;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DatosPreeliminaresFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DatosPreeliminaresFragment extends OrmLiteFragment implements BCTarget, TwinTracBtReceiver.TwinTracDataListener, BtReceiverStateListener {

    Button btnStart;
    TextView txtVStattBatery;

    String btAddress = "00:04:3E:9C:28:50";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int device = EstabConexRecibirDatosActivity.TwinTrac;
    private EstabConexRecibirDatosActivity.Mode next_mode;
    private TwinTracBtReceiver twinTracReceiver;
    private final String logTag = getClass().getSimpleName();

    public DatosPreeliminaresFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DatosPreeliminaresFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DatosPreeliminaresFragment newInstance(String param1, String param2) {
        DatosPreeliminaresFragment fragment = new DatosPreeliminaresFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_datos_preeliminares, container, false);

        txtVStattBatery = (TextView) rootView.findViewById(R.id.txtVBattValue);

        btnStart = (Button) rootView.findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flujoPrincipalFramelayout, new DetallesDeSesionFragment() )
                        .addToBackStack(Constantes.NOMBRE_FRAGMENT_DATOS_PREELIMINARES)
                        .commit();
            }
        });

        Patient pat = (Patient) getActivity().getIntent().getSerializableExtra("Patient");

        createBtReceiver(pat, btAddress,true);



        return rootView;
    }

    private void createBtReceiver(Patient pat, String btAddress,
                                  boolean startReceiverFirstTime)
    {
        try
        {
            if (device == EstabConexRecibirDatosActivity.TwinTrac)
            {
                //next_mode = EstabConexRecibirDatosActivity.Mode.THREE_CHANNEL_1;
                twinTracReceiver = new TwinTracBtReceiver(btAddress, device,
                        false, getActivity(), this, pat, this,
                        startReceiverFirstTime);
                twinTracReceiver.execute(this);
            }
        }
        catch (IOException e)
        {
            Log.e(logTag, e.getLocalizedMessage(), e);
        }
    }

    //---------------- Metodos de la interface BCTarget

    @Override
    public void channelClosed() {

    }

    @Override
    public void newData(Data d) {

    }

    @Override
    public void newData(double[] data) {

    }

    @Override
    public void patientInformation(byte[] data) {

    }

    //-------------Metodos de la interface BtReceiverStateListener

    @Override
    public void btReceiverFinishedWithException() {

    }

    @Override
    public void stopFillingAutomaticValues() {

    }

    //-------------Metodos de la interface TwinTracBtReceiver.TwinTracDataListener

    @Override
    public void configTwinTracDataReceived(TwinTracConfigManager config) {

    }

    @Override
    public void errorDuringBluetoothConnection(String title, String message) {

    }

    @Override
    public void newTwinTracECGValuesReceived(TwinTracOnlineDataHolder value) {

        Log.i(logTag, "new ECG values received");
        parseDataForHolder(value);
        //int[] newEcgValues = value.getEcgValuesInt();
        //printTwinTracECGValues(newEcgValues);
    }

    @Override
    public void receivedPatientInformation(String patientName) {

    }

    private void parseDataForHolder(final TwinTracOnlineDataHolder value)
    {
        getActivity().runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                //add heart rate if necessary
                /*if (value.getHr() > Integer.MIN_VALUE)
                {
                    findViewById(R.id.hrRateLayout).setVisibility(View.VISIBLE);
                    TextView hrReate = (TextView) findViewById(R.id.tvHrValue);
                    hrReate.setText(String.format("%d", value.getHr()) + " BPM");
                    checkHrMinMaxValueAndPlaySound(value.getHr());
                }
                else
                {
                    findViewById(R.id.hrRateLayout).setVisibility(View.GONE);
                }
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String tempUnit = prefs.getString(
                        getString(R.string.pref_temperature_unit_key),
                        getString(R.string.pref_temperature_unit_celcius));
                boolean isCelcius = tempUnit.equals(getString(R.string.pref_temperature_unit_celcius));

                //add tempValue
                if (value.getTemp1() > Integer.MIN_VALUE)
                {
                    TextView tvTemp = (TextView) findViewById(R.id.tvTempValue1);
                    double valueTemp1 = 0;
                    if (isCelcius)
                    {
                        valueTemp1 = (value.getTemp1() / 10d) - 20;
                    }
                    else
                    {
                        double valueCelcius = (value.getTemp1() / 10d) - 20;
                        valueTemp1 = valueCelcius * 1.8d + 32;
                    }
                    tvTemp.setText(String.format("%1$,.1f", valueTemp1) + " "
                            + tempUnit);
                }
                else
                {
                    findViewById(R.id.temp1Layout).setVisibility(View.GONE);
                }
                if (value.getTemp2() > Integer.MIN_VALUE)
                {
                    TextView tvTemp = (TextView) findViewById(R.id.tvTempValue2);
                    double valueTemp2 = 0;
                    if (isCelcius)
                    {
                        valueTemp2 = (value.getTemp1() / 10d) - 20;
                    }
                    else
                    {
                        double valueCelcius = (value.getTemp1() / 10d) - 20;
                        valueTemp2 = valueCelcius * 1.8d + 32;
                    }
                    tvTemp.setText(String.format("%1$,.1f", valueTemp2) + " "
                            + tempUnit);
                }
                else
                {
                    findViewById(R.id.temp2Layout).setVisibility(View.GONE);
                }
                if (value.getTemp3() > Integer.MIN_VALUE)
                {
                    findViewById(R.id.temp3Layout).setVisibility(View.VISIBLE);
                    TextView tvTemp = (TextView) findViewById(R.id.tvTempValue3);
                    double valueTemp3 = 0;
                    if (isCelcius)
                    {
                        valueTemp3 = (value.getTemp1() / 10d) - 20;
                    }
                    else
                    {
                        double valueCelcius = (value.getTemp1() / 10d) - 20;
                        valueTemp3 = valueCelcius * 1.8d + 32;
                    }
                    tvTemp.setText(String.format("%1$,.1f", valueTemp3) + " "
                            + tempUnit);
                }
                else
                {
                    findViewById(R.id.temp3Layout).setVisibility(View.GONE);
                }

                //add remain time
                TextView tvRemainTimeWithBt = (TextView) findViewById(R.id.tvRemainingTime);
                tvRemainTimeWithBt.setText(value.getRemainingTime() + "h");

                //add remain time without bt
                TextView tvRemainTimeWithoutBt = (TextView) findViewById(R.id.tvRemainingTimeWithoutBt);
                tvRemainTimeWithoutBt.setText(value.getRemainingTime() * 6
                        + "h");

                 */

                //add battery status
                txtVStattBatery.setText(value.getBatteryCapacityInPercent() + "%");

                //ProgressBar aclProgress = (ProgressBar) findViewById(R.id.acc_progressbar);
                //aclProgress.setProgress((int) value.getAccAverage());
            }


            private void checkHrMinMaxValueAndPlaySound(int hrValue)
            {
                /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(EstabConexRecibirDatosActivity.this);
                double hrMin = Double.valueOf(prefs.getString(
                        getString(R.string.pref_hr_minimum_value_key), "-1"));
                double hrMax = Double.valueOf(prefs.getString(
                        getString(R.string.pref_hr_maximum_value_key), "-1"));
                if (hrMin > -1 && hrMax > -1)
                {
                    if (hrMin <= hrValue && hrValue <= hrMax)
                    {
                        return;
                    }
                    else
                    {
                        soundHandler.sendEmptyMessage(MESSAGE_PLAY_SOUND);
                    }
                }*/
            }
        });

    }
}