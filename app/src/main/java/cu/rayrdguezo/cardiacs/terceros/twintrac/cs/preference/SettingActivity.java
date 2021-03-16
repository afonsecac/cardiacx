package cu.rayrdguezo.cardiacs.terceros.twintrac.cs.preference;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.preference.TwoStatePreference;
import android.util.Log;

import cu.rayrdguezo.cardiacs.EstabConexRecibirDatosActivity;
import cu.rayrdguezo.cardiacs.MainActivity;
import cu.rayrdguezo.cardiacs.R;

/**
 * Application settings class. All application depended settings will handle in
 * this class.
 * 
 * @author Hakan Sahin
 * @date 08.06.2013 09:02:15 Hakan Sahin
 */
public class SettingActivity extends PreferenceActivity implements
        OnPreferenceChangeListener, OnPreferenceClickListener
{
	public final static String ECG_LIVE_TRANSMITT = "de.avetana.apps.cardioscout.liveecgtransmit";
	private final static String TWIN_TRAC_CONFIG = "de.avetana.apps.cardioscout.twintrac.config";

	private PreferenceCategory twintrac;


	//private PreferenceScreen twintracScreen;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.layout.settings);
		EditTextPreference editUrl = (EditTextPreference) findPreference(getString(R.string.url_change_key));
		ListPreference listPreferenceGlucose = (ListPreference) findPreference(getString(R.string.glucose_unit_key));
		ListPreference listPrefWeight = (ListPreference) findPreference(getString(R.string.weight_unit_key));
		TwoStatePreference liveECGPreference = (TwoStatePreference) findPreference(getString(R.string.preferenceKey_liveECGTransmit));
		Preference updateFirmwareTwinTrac = (Preference) findPreference(getString(R.string.preferenceKey_twintracupdate));
		twintrac = (PreferenceCategory) findPreference(getString(R.string.preferenceCat_twintrac_config));
		TwoStatePreference twinTracConfigSwitch = (TwoStatePreference) findPreference(getString(R.string.preferenceKey_twintracconfig));
		ListPreference onlineSampleRate = (ListPreference) findPreference(getString(R.string.pref_online_sampling_key));
		ListPreference sdCardSampleRate = (ListPreference) findPreference(getString(R.string.pref_sdcard_sampling_key));
		ListPreference recordingTime = (ListPreference) findPreference(getString(R.string.pref_recording_time_key));
		ListPreference filter = (ListPreference) findPreference(getString(R.string.pref_filter_select_key));
		SwitchPreference btOnAlwaysOrCounter = (SwitchPreference) findPreference(getString(R.string.pref_bluetooth_on_counter_always_key));
		EditTextPreference btOnTime = (EditTextPreference) findPreference(getString(R.string.pref_bluetooth_on_time_key));
		ListPreference recMode = (ListPreference) findPreference(getString(R.string.pref_recording_modus_key));
		ListPreference recModeEventTime = (ListPreference) findPreference(getString(R.string.pref_recording_modus_time_event_key));
		ListPreference recModeLoopTime = (ListPreference) findPreference(getString(R.string.pref_recording_modus_time_loop_key));
		ListPreference usbMode = (ListPreference) findPreference(getString(R.string.pref_usb_mode_key));
		ListPreference tempUnit = (ListPreference) findPreference(getString(R.string.pref_temperature_unit_key));
		ListPreference softgain = (ListPreference) findPreference(getString(R.string.pref_softgain_key));
		ListPreference autostart = (ListPreference) findPreference(getString(R.string.pref_autostart_key));
		EditTextPreference autostartTime = (EditTextPreference) findPreference(getString(R.string.pref_autostart_time_key));
		//twintracScreen = (PreferenceScreen) findPreference(getString(R.string.preferenceScreen));

		//HR value min max
		EditTextPreference hrControlMinimumValue = (EditTextPreference) findPreference(getString(R.string.pref_hr_minimum_value_key));
		EditTextPreference hrControlMaximumValue = (EditTextPreference) findPreference(getString(R.string.pref_hr_maximum_value_key));

		hrControlMaximumValue.setSummary(hrControlMaximumValue.getText());
		hrControlMinimumValue.setSummary(hrControlMinimumValue.getText());
		editUrl.setSummary(editUrl.getText());
		if (btOnAlwaysOrCounter.isChecked())
		{
			btOnTime.setEnabled(false);
		}
		else
		{
			btOnTime.setEnabled(true);
		}
		btOnTime.setSummary(btOnTime.getText());
		if (listPreferenceGlucose.getValue() == null)
		{
			// to ensure we don't get a null value
			// set first value by default
			listPreferenceGlucose.setValueIndex(0);
		}
		if (listPrefWeight.getValue() == null)
		{
			listPrefWeight.setValueIndex(0);
		}
		if (onlineSampleRate.getValue() == null)
		{
			onlineSampleRate.setValueIndex(0);
		}
		if (sdCardSampleRate.getValue() == null)
		{
			sdCardSampleRate.setValueIndex(0);
		}
		if (recordingTime.getValue() == null)
		{
			recordingTime.setValueIndex(0);
		}
		if (filter.getValue() == null)
		{
			filter.setValueIndex(0);
		}
		if (recMode.getValue() == null)
		{
			recMode.setValueIndex(0);
		}
		if (recModeEventTime.getValue() == null)
		{
			recModeEventTime.setValueIndex(0);
		}
		if (recModeLoopTime.getValue() == null)
		{
			recModeLoopTime.setValueIndex(0);
		}
		if (usbMode.getValue() == null)
		{
			usbMode.setValueIndex(0);
		}
		if (tempUnit.getValue() == null)
		{
			tempUnit.setValueIndex(0);
		}
		if (softgain.getValue() == null)
		{
			softgain.setValueIndex(0);
		}
		if (autostart.getValue() == null)
		{
			autostart.setValueIndex(0);
		}
		listPreferenceGlucose.setSummary(listPreferenceGlucose.getEntry().toString());
		listPrefWeight.setSummary(listPrefWeight.getEntry().toString());
		onlineSampleRate.setSummary(onlineSampleRate.getEntry().toString());
		sdCardSampleRate.setSummary(sdCardSampleRate.getEntry().toString());
		recordingTime.setSummary(recordingTime.getEntry().toString());
		filter.setSummary(filter.getEntry().toString());
		recMode.setSummary(recMode.getEntry().toString());
		recModeEventTime.setSummary(recModeEventTime.getEntry().toString());
		recModeLoopTime.setSummary(recModeLoopTime.getEntry().toString());
		tempUnit.setSummary(tempUnit.getEntry().toString());
		softgain.setSummary(softgain.getEntry().toString());
		autostart.setSummary(autostart.getEntry().toString());
		int recModeValue = Integer.valueOf(recMode.getValue());
		//if selected recording type is standard disable time
		if (recModeValue == 0)
		{
			recModeEventTime.setEnabled(false);
			recModeLoopTime.setEnabled(false);
		}
		else
		{
			recModeEventTime.setEnabled(true);
			recModeLoopTime.setEnabled(true);
		}
		usbMode.setSummary(usbMode.getEntry().toString());
		int autostartValue = Integer.valueOf(autostart.getValue());
		//if selected autotype is "on time" enable time
		if (autostartValue == 4)
		{
			autostartTime.setEnabled(true);
		}
		else
		{
			autostartTime.setEnabled(false);
		}

		updateFirmwareTwinTrac.setOnPreferenceClickListener(new OnPreferenceClickListener()
		{
			@Override
			public boolean onPreferenceClick(Preference preference)
			{
				Log.i(SettingActivity.class.getSimpleName(),
					"starting firmware activity");
				/*Intent startUpdateIntent = new Intent(getApplicationContext(),
					UpdateTwinTracActivity.class);
				startUpdateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(startUpdateIntent);*/
				return true;
			}
		});
		/*
		 * listPreferenceGlucose.setOnPreferenceChangeListener(new
		 * OnPreferenceChangeListener() {
		 * 
		 * @Override public boolean onPreferenceChange(Preference preference,
		 * Object newValue) { preference.setSummary(newValue.toString()); return
		 * true; } });
		 */
		listPreferenceGlucose.setOnPreferenceChangeListener(this);
		listPrefWeight.setOnPreferenceChangeListener(this);
		editUrl.setOnPreferenceChangeListener(this);
		btOnAlwaysOrCounter.setOnPreferenceChangeListener(this);
		btOnTime.setOnPreferenceChangeListener(this);
		twinTracConfigSwitch.setOnPreferenceChangeListener(this);
		onlineSampleRate.setOnPreferenceChangeListener(this);
		sdCardSampleRate.setOnPreferenceChangeListener(this);
		recordingTime.setOnPreferenceChangeListener(this);
		filter.setOnPreferenceChangeListener(this);
		recMode.setOnPreferenceChangeListener(this);
		recModeEventTime.setOnPreferenceChangeListener(this);
		recModeLoopTime.setOnPreferenceChangeListener(this);
		usbMode.setOnPreferenceChangeListener(this);
		tempUnit.setOnPreferenceChangeListener(this);
		softgain.setOnPreferenceChangeListener(this);
		autostart.setOnPreferenceChangeListener(this);
		hrControlMaximumValue.setOnPreferenceChangeListener(this);
		hrControlMinimumValue.setOnPreferenceChangeListener(this);
		SharedPreferences prefsUserActivity = getSharedPreferences(
			MainActivity.PREFS_FILE, MODE_PRIVATE);
		int type = prefsUserActivity.getInt(
			EstabConexRecibirDatosActivity.EXTRAKEY_ECGDEVICE_TYPE, 0);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean isTwinTracConfigAvaiable = prefs.getBoolean(TWIN_TRAC_CONFIG,
			false);
		if (type != EstabConexRecibirDatosActivity.TwinTrac)
		{
			twinTracConfigSwitch.setEnabled(false);
			twintrac.setEnabled(false);
			updateFirmwareTwinTrac.setEnabled(false);
		}
		else
		{
			twinTracConfigSwitch.setEnabled(true);
			updateFirmwareTwinTrac.setEnabled(true);
			if (!isTwinTracConfigAvaiable)
			{
				twintrac.setEnabled(false);
			}
		}

	}


	/*
	 * return true to save the settings. return false the changes were not
	 * stored.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see android.preference.Preference.OnPreferenceChangeListener#
	 * onPreferenceChange (android.preference.Preference, java.lang.Object)
	 */
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue)
	{

		if (preference.getKey().equals(
			getString(R.string.preferenceKey_twintracconfig)))
		{
			boolean value = (boolean) newValue;
			if (value)
			{
				//twintracScreen.addPreference(twintrac);
				twintrac.setEnabled(true);
			}
			else
			{
				//twintracScreen.removePreference(twintrac);
				twintrac.setEnabled(false);
			}
		}
		else if (preference.getKey().equals(
			getString(R.string.pref_recording_modus_key)))
		{
			Log.i("TEST", "new value is: " + newValue);
			int value = Integer.valueOf((String) newValue);
			ListPreference recModeEventTime = (ListPreference) findPreference(getString(R.string.pref_recording_modus_time_event_key));
			ListPreference recModeLoopTime = (ListPreference) findPreference(getString(R.string.pref_recording_modus_time_loop_key));
			if (value == 0)
			{
				recModeEventTime.setEnabled(false);
				recModeLoopTime.setEnabled(false);
			}
			else
			{
				recModeEventTime.setEnabled(true);
				recModeLoopTime.setEnabled(true);
			}
			ListPreference pref = (ListPreference) preference;
			preference.setSummary(pref.getEntries()[pref.findIndexOfValue(newValue.toString())]);
		}
		else if (preference.getKey().equals(
			getString(R.string.pref_bluetooth_on_counter_always_key)))
		{

			if (preference instanceof SwitchPreference)
			{
				if (!((SwitchPreference) preference).isChecked())
				{
					findPreference(
						getString(R.string.pref_bluetooth_on_time_key)).setEnabled(
						false);
				}
				else
				{
					findPreference(
						getString(R.string.pref_bluetooth_on_time_key)).setEnabled(
						true);
				}
			}
		}
		else if (preference.getKey().equals(
			getString(R.string.pref_autostart_key)))
		{
			int value = Integer.valueOf((String) newValue);
			EditTextPreference autostartTime = (EditTextPreference) findPreference(getString(R.string.pref_autostart_time_key));
			if (value == 4)
			{
				autostartTime.setEnabled(true);
			}
			else
			{
				autostartTime.setEnabled(false);
			}
			ListPreference pref = (ListPreference) preference;
			preference.setSummary(pref.getEntries()[pref.findIndexOfValue(newValue.toString())]);
		}
		else if (preference instanceof ListPreference)
		{
			ListPreference pref = (ListPreference) preference;
			preference.setSummary(pref.getEntries()[pref.findIndexOfValue(newValue.toString())]);
		}
		else
		{
			preference.setSummary(newValue.toString());
		}
		return true;
	}


	@Override
	public boolean onPreferenceClick(Preference preference)
	{
		preference.setShouldDisableView(true);
		AlertDialog.Builder errorMessageBuilder = new AlertDialog.Builder(this);
		errorMessageBuilder.setMessage("Test");
		errorMessageBuilder.setNeutralButton(android.R.string.ok,
			new OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.cancel();

				}
			});
		errorMessageBuilder.show();
		return false;
	}


	@Override
	protected void onPause()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		String url = prefs.getString(getString(R.string.url_change_key),
			getString(R.string.url_default_link));
		String glucoseUnit = prefs.getString(
			getString(R.string.glucose_unit_key),
			getString(R.string.glucose_unit_mol));
		String weightUnit = prefs.getString(
			getString(R.string.weight_unit_key),
			getString(R.string.weight_unit_kg));
		boolean liveEcgTransmit = prefs.getBoolean(
			getString(R.string.preferenceKey_liveECGTransmit), false);
		boolean twinTracConfig = prefs.getBoolean(
			getString(R.string.preferenceKey_twintracconfig), false);

		Editor preferencesEditor = prefs.edit();
		/*preferencesEditor.putString(UserActivity.URL_SETTINGS, url);
		preferencesEditor.putString(GlucoseActivity.GLUCOSE_UNIT, glucoseUnit);
		preferencesEditor.putString(WeightActivity.WEIGHT_UNIT, weightUnit);
		preferencesEditor.putString(UserActivity.URL_SETTINGS, url);*/
		preferencesEditor.putBoolean(ECG_LIVE_TRANSMITT, liveEcgTransmit);
		preferencesEditor.putBoolean(TWIN_TRAC_CONFIG, twinTracConfig);
		preferencesEditor.commit();
		super.onPause();
	}
}
