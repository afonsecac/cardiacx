package cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac;

import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import cu.rayrdguezo.cardiacs.EstabConexRecibirDatosActivity;
import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.Converter;
import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data.Data;

public class TwinTracConverter extends Converter
{

	private final TwinTracKeepAlive keepAliveThread;
	private final int device;


	public TwinTracConverter(InputStream rawIn, OutputStream rawOut, int device)
	{
		super(rawIn, rawOut);
		if (device == EstabConexRecibirDatosActivity.TwinTrac)
		{
			keepAliveThread = new TwinTracKeepAlive(this, device, 1000L);
		}
		else
		{
			keepAliveThread = null;
		}
		this.device = device;
	}


	/*
	 * write bytes into output stream
	 */
	public void writeByte(byte[] in)
	{
		try
		{
			//			Log.d(TwinTracConverter.class.getSimpleName(),
			//				"Sending data: " + TwinTracDataCreator.byteArrayToHex(in));
			sleepBeforeSend(200);
			this.rawOut.write(in);
			this.rawOut.flush();
		}
		catch (IOException e)
		{
			if (keepAliveThread != null)
			{
				keepAliveThread.setRunning(false);
			}
			e.printStackTrace();
		}
	}


	/**
	 * No parsing of the incoming data Forward to listeners
	 */
	@Override
	protected void doReading()
	{
		byte b[] = new byte[200];
		int pos = 0;
		totalRead = 0;
		try
		{
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2
				&& device == EstabConexRecibirDatosActivity.TwinTrac && false)
			{
				new Thread(keepAliveThread).start();
			}
			while (keepReading)
			{
				if (!keepReading)
				{
					if (DEBUG)
					{
						System.out.println("KeepReading==false -> return");
					}
					return;
				}
				//				if ((b.length - pos) == 0)
				//				{
				//					byte b2[] = b;
				//					b = new byte[b2.length * 2];
				//					System.arraycopy(b2, 0, b, 0, b2.length);
				//				}

				int posp = -1;
				System.out.println("Waiting for data");
				posp = rawIn.read(b, 0, b.length);
				System.out.println("data received: " + posp);

				if (posp == -1)
				{
					throw new IOException("Channel closed");
				}

				byte raw[] = new byte[posp];
				System.arraycopy(b, 0, raw, 0, posp);
				Data d = new Data(0xffff, 0xffff, raw);
				//				System.out.println("buffer b length: " + b.length
				//									+ ", raw. length: " + raw.length);
				for (int i = 0; i < dlc; i++)
				{
					dataListeners[i].newData(d);
				}
				Arrays.fill(b, (byte) 0x00);
				raw = null;
				pos += posp;
				totalRead += posp;

			}
		}
		catch (Exception e)
		{
			if (DEBUG)
			{
				e.printStackTrace();
			}
			keepReading = false;
			if (logStream != null)
			{
				logStream.println("Logging of data for " + getClass().getName()
									+ " ended at " + System.currentTimeMillis());
			}
			for (int i = 0; i < dlc; i++)
			{
				dataListeners[i].channelClosed();
			}
		}
		finally
		{
			keepReading = false;
			if (keepAliveThread != null)
			{
				keepAliveThread.setRunning(false);
			}
			if (DEBUG)
			{
				System.out.println("Converter::End doReading");
			}
		}
	}


	private void sleepBeforeSend(long sleppTime)
	{
		try
		{
			Thread.sleep(sleppTime);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

}
