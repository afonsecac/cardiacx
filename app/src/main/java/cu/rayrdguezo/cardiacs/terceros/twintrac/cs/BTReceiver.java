package cu.rayrdguezo.cardiacs.terceros.twintrac.cs;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

import cu.rayrdguezo.cardiacs.EstabConexRecibirDatosActivity;
import cu.rayrdguezo.cardiacs.terceros.twintrac.BtReceiverStateListener;
import cu.rayrdguezo.cardiacs.terceros.twintrac.CRC8Calculator;
import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.BCTarget;
import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.Converter;
import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data.Data;

public class BTReceiver extends AsyncTask<BCTarget, Void, Exception>
				implements BCTarget
{
	public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

	private final String btAddress;

	private final int device;

	private final boolean onlineControl;

	/* Converter handles incoming and outgoing data */
	private Converter conv;

	private BluetoothSocket socket;

	private boolean stopMeasurement;

	private final String logTag = BTReceiver.class.getSimpleName();

	private final Context context;

	private final BtReceiverStateListener btReceiverListener;
	private boolean converterFirstTime;
	private boolean currentlySleep;


	/**
	 * Initiate the BT-Connection and prepare the Converter
	 *
	 * param btAddress
	 * param bda
	 *            Bluetooth address of the BT12
	 *
	 * @throws IOException
	 */

	public BTReceiver(String btAddress, int device, boolean onlineControl,
                      Context context,
                      BtReceiverStateListener btReceiverListner,
                      boolean converterFirstTime)
		throws IOException
	{
		this.btAddress = btAddress;
		this.device = device;
		this.onlineControl = onlineControl;
		this.context = context;
		this.btReceiverListener = btReceiverListner;
		this.converterFirstTime = converterFirstTime;
		this.currentlySleep = false;
	}


	/**
	 * Called from the Converter if an interruption is detected.
	 */
	@Override
	public void channelClosed()
	{
		Log.d(getClass().getSimpleName(), "Connection closed");
		cancel(true);
	}


	public byte[] convertShortToByteArray(short val)
	{
		byte[] ret = new byte[2];
		ret[0] = (byte) (val & 0xff);
		ret[1] = (byte) ((val >> 8) & 0xff);
		return ret;

		//		ByteBuffer buffer = ByteBuffer.allocate(2);
		//		buffer.putShort(val);
		//		return buffer.array();
	}


	/**
	 *
	 * @return
	 */
	public boolean isStarted()
	{
		return (socket != null);
	}


	public boolean isStopMeasurement()
	{
		return stopMeasurement;
	}


	/**
	 * Called from the converter when new packets from the device are received
	 * that are not directly intercepted by a sendAndWait method.
	 *
	 */
	@Override
	public void newData(Data d)
	{
		Log.i(logTag, "currentySleep: " + currentlySleep);
		if (currentlySleep)
		{
			converterFirstTime = false;
		}
	}


	@Override
	public void newData(double[] d)
	{
	}


	@Override
	public void patientInformation(byte[] data)
	{

	}


	public void setMarkerInMeasurement(String markerName)
	{
		try
		{
			Log.i(logTag, markerName);
			//			conv.getOutputStream().write(
			//				("WMarker" + 0x0B + 0x00 + "+MARKERNAME").getBytes());
			//length + 1byte CRC
			byte[] length = convertShortToByteArray(
				(short) (markerName.length() + 1));
			Log.i(logTag, "low: " + length[0] + ", high: " + length[1]);
			int i = 0;
			byte[] valueNoCrC = ("WMarker"+ (char) length[0] + (char) length[1]
									+ markerName).getBytes();
			for (byte b : valueNoCrC)
			{
				Log.i(logTag,
					"index: " + (i++) + ", value: " + b + ", " + (char) b);
			}
			byte[] valueWithCRC = new byte[valueNoCrC.length + 1];
			System.arraycopy(valueNoCrC, 0, valueWithCRC, 0, valueNoCrC.length);
			//last value is the 1byte crc
			valueWithCRC[valueWithCRC.length - 1] = CRC8Calculator.calc(
				valueNoCrC, valueNoCrC.length);
			conv.getOutputStream().write(valueWithCRC);
			conv.getOutputStream().flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Toast.makeText(context, "Error: " + e, Toast.LENGTH_LONG).show();
		}

	}


	public void setStopMeasurement(boolean stopMeasurement)
	{
		this.stopMeasurement = stopMeasurement;
	}


	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Exception doInBackground(BCTarget... targets)
	{
		Exception thrown = null;
		try
		{
			runInt(targets);

			while (!isCancelled())
			{
				Thread.sleep(100l);
			}
		}
		catch (Exception e)
		{
			thrown = e;
		}
		finally
		{

		}

		return thrown;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onCancelled()
	 */
	@Override
	protected void onCancelled()
	{
		Log.d(getClass().getSimpleName(), "Cancelled");
		try
		{
			stopReading();
		}
		catch (Exception e)
		{
		}
		btReceiverListener.btReceiverFinishedWithException();
	}


	@Override
	protected void onPostExecute(Exception result)
	{
		Log.d(getClass().getSimpleName(), "Done");

		if (result != null)
		{
			Log.e(getClass().getSimpleName(), "ERRROORR", result);
			btReceiverListener.btReceiverFinishedWithException();
		}
		try
		{
			stopReading();
		}
		catch (Exception e)
		{
		}
	}


	private void runInt(BCTarget... targets) throws Exception
	{
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothDevice btDevice = btAdapter.getRemoteDevice(btAddress);
		Method m = btDevice.getClass().getMethod("createRfcommSocket",
			new Class[] { int.class });
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
		{
			socket = (BluetoothSocket) m.invoke(btDevice, 1);
		}
		else
		{
			socket = btDevice.createRfcommSocketToServiceRecord(
				UUID.fromString(SPP_UUID));
		}
		btAdapter.cancelDiscovery();
		socket.connect();

		/*if (device == EstabConexRecibirDatosActivity.BT12)
		{
			conv = new Converter(socket.getInputStream(),
				socket.getOutputStream());
		}
		else if (device == EstabConexRecibirDatosActivity.SRMed)
		{
			conv = new DummyConverter(socket.getInputStream(),
				socket.getOutputStream(), EstabConexRecibirDatosActivity.SRMed);
		}
		else
		{
			return;
		}*/

		if (device == EstabConexRecibirDatosActivity.TwinTrac)
			return;

		// Register ourselves as listener for incoming packets
		conv.addBCTarget(this);
		for (BCTarget bcTarget : targets)
		{
			conv.addBCTarget(bcTarget);
		}

		// Starts a separate thread to handle IO
		conv.startReading();
		btReceiverListener.stopFillingAutomaticValues();
		currentlySleep = true;
		Thread.sleep(1000);
		Log.d(logTag, "WAKED UP converterFirstTime: " + converterFirstTime);
		currentlySleep = false;
		if (converterFirstTime)
		{
			if (device == EstabConexRecibirDatosActivity.TwinTrac)
				return;

		}
		Log.d(getClass().getSimpleName(), "Started...");
	}


	@TargetApi(11)
	private BluetoothSocket startConncetionHigherApi()
	{
		try
		{

			return BluetoothAdapter.getDefaultAdapter().getRemoteDevice(
				btAddress).createInsecureRfcommSocketToServiceRecord(
					UUID.fromString(SPP_UUID));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Log.e(getClass().getSimpleName(), "", e);
		}
		return null;
	}


	@TargetApi(18)
	private BluetoothSocket startConnectionBiggerThanJellybean()
	{
		BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(
			Context.BLUETOOTH_SERVICE);
		BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
		try
		{
			return mBluetoothAdapter.getRemoteDevice(
				btAddress).createRfcommSocketToServiceRecord(
					UUID.fromString(SPP_UUID));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Log.e(getClass().getSimpleName(), "", e);
		}
		return null;
	}


	private void stopReading() throws Exception
	{
		// Stop ECG transmission
		if (stopMeasurement)
		{
			try
			{
				/*if (device == EstabConexRecibirDatosActivity.BT12)
				{
					conv.sendData(new BlueEKGStartStop(false));
				}
				else if (device == EstabConexRecibirDatosActivity.SRMed)
				{
					// Cannot use conv.sendData() since it appends useless header & footer
					Log.i(logTag, "Messung wird beendet");
					conv.getOutputStream().write(
						new SRMedStartStop(false).getPayload());
					conv.getOutputStream().flush();
				}*/
			}
			catch (Exception e)
			{
				Log.d(getClass().getName(), e.getMessage());
			}
		}
		// Stop the IO process
		conv.stopReading();

		// Close the BT connection
		Thread.sleep(500);
		conv.getInputStream().close();
		conv.getOutputStream().close();
		socket.close();
	}

}
