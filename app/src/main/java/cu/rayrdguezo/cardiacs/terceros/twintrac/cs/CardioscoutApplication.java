package cu.rayrdguezo.cardiacs.terceros.twintrac.cs;

import android.app.Application;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

/**
 * Save global variable for project
 */
public class CardioscoutApplication extends Application
{

	public static String PREF_ECG_CONTINUE_RECORDING = "continue.recording";
	private static Context sApplicationContext;
	private static DevicePolicyManager devicePoliceManager;
	private static ComponentName compName;


	/**
	 * load context remotely
	 *
	 * @return Context
	 */
	public static Context getCardioscoutAppContext()
	{
		return sApplicationContext;
	}


	@Override
	public void onCreate()
	{
		super.onCreate();
		sApplicationContext = getApplicationContext();
	}

}
