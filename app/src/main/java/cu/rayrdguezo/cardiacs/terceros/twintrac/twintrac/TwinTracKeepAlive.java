package cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac;

import android.util.Log;

import java.io.IOException;

import cu.rayrdguezo.cardiacs.EstabConexRecibirDatosActivity;

/**
 * Keep the bluetooth connection alive by sending data to connected device. This
 * thread is only used for SRMED->CardioScout MultiECG device
 *
 * @author Hakan Sahin
 * @date 30.01.2015 10:10:39 Hakan Sahin
 */
public class TwinTracKeepAlive implements Runnable
{
	private final TwinTracConverter connectedThread;
	private final String TAG = "KeepAlive";
	private volatile boolean running = true;
	private final int device;
	private final long waitTimeMillis;


	public TwinTracKeepAlive(TwinTracConverter connectedThread, int device,
                             long waitTimeMillis)
	{
		this.connectedThread = connectedThread;
		this.device = device;
		running = true;
		this.waitTimeMillis = waitTimeMillis;

	}


	@Override
	public synchronized void run()
	{
		if (device == EstabConexRecibirDatosActivity.TwinTrac)
		{
			Log.d(TAG, "KeepAlive Thread starting");
			while (running)
			{

				try
				{
					Thread.sleep(waitTimeMillis);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				Log.d(this.getClass().getSimpleName(),
					"keep connected for device: " + device);
				try
				{
					this.connectedThread.getOutputStream().write(
						new TwinTracPackageBattery().getPacket());
					this.connectedThread.getOutputStream().flush();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

		}

		Log.d(TAG, "KeepAlive Thread closing");
	}


	public void setRunning(boolean running)
	{
		this.running = running;
	}


	private byte[] getSendBytesPocket(byte[] in)
	{
		byte[] out = new byte[2 * (in.length + 1)];
		int bcs = 0;
		for (int i = 0; i < in.length; i++)
		{
			out[i * 2] = in[i];
			out[(i * 2) + 1] = (byte) 0xff;
			bcs += in[i];
		}
		out[out.length - 2] = (byte) bcs;
		out[out.length - 1] = (byte) 0xff;
		return out;
	}
}
