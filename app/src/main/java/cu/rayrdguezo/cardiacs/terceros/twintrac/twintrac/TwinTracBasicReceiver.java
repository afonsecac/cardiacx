package cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.BCTarget;
import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data.Data;
import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data.TwinTracDataCreator;

/**
 * Created by H. Sahin on 05.02.2018.
 */

public abstract class TwinTracBasicReceiver extends
        AsyncTask<BCTarget, Void, Exception> implements BCTarget
{
	private final static String logTag = TwinTracBasicReceiver.class.getSimpleName();

	private final List<Byte> receivedDataBuffer;


	protected TwinTracBasicReceiver()
	{
		this.receivedDataBuffer = new ArrayList<>();
	}


	@Override
	public void channelClosed()
	{

	}


	@Override
	public void newData(Data d)
	{
		if (!isCancelled())
		{
			for (int i = 0; i < d.getPayload().length; i++)
			{
				receivedDataBuffer.add(d.getPayload()[i]);
			}
			parseReceivedData(receivedDataBuffer);
		}
	}


	@Override
	public void newData(double[] data)
	{

	}


	@Override
	public void patientInformation(byte[] data)
	{

	}


	protected String convertBCDtoString(byte bcd)
	{
		StringBuffer sb = new StringBuffer();

		byte high = (byte) (bcd & 0xf0);
		high >>>= (byte) 4;
		high = (byte) (high & 0x0f);
		byte low = (byte) (bcd & 0x0f);

		sb.append(high);
		sb.append(low);
		return sb.toString();
	}


	protected byte[] convertShortToByteArray(short val)
	{
		byte[] ret = new byte[2];
		ret[0] = (byte) (val & 0xff);
		ret[1] = (byte) ((val >> 8) & 0xff);
		return ret;
	}


	@Override
	protected Exception doInBackground(BCTarget ... bcTargets)
	{
		return null;
	}


	protected byte[] doubleByteArraySize(byte[] array)
	{
		byte array2[] = new byte[array.length * 2];
		System.arraycopy(array, 0, array2, 0, array.length);
		return array2;
	}


	protected int get1ByteBCDIntValueFromIndex(byte[] completePackage,
					int indexData)
	{

		int value = Integer.parseInt(convertBCDtoString((byte) (completePackage[indexData] & 0xff)));
		return value;
	}


	protected int get1ByteIntValueFromIndex(byte[] completePackage,
					int indexData)
	{
		int value = (completePackage[indexData] & 0xFF);
		return value;
	}


	protected int get2ByteBCDIntValueFromIndex(byte[] completePackage,
					int indexData)
	{

		int value = Integer.parseInt(convertBCDtoString((byte) (completePackage[indexData + 1] & 0xff))
										+ ""
										+ convertBCDtoString((byte) (completePackage[indexData] & 0xff))

		);
		return value;
	}


	protected int get2ByteIntValueFromIndex(byte[] completePackage,
					int indexData)
	{
		int value = (completePackage[indexData] & 0x00FF)
					+ ((completePackage[indexData + 1] << 8) & 0xFF00);
		return value;
	}


	protected byte[] getCompletePackage(List<Byte> receivedData)
	{
		testCorruptedDataPackages(receivedData);
		int indexStart = receivedData.indexOf(TwinTracDataCreator.PACKAGE_START_KEY);
		int indexStop = receivedData.indexOf(TwinTracDataCreator.PACKAGE_STOP_KEY);
		if (indexStart > -1 && indexStop > -1 && indexStart < indexStop)
		{
			byte[] completePackage = new byte[indexStop + 1 - indexStart];
			for (int i = 0; i < completePackage.length; i++)
			{
				completePackage[i] = receivedData.get(i + indexStart);
			}
			receivedData.subList(indexStart, indexStop + 1).clear();
			return completePackage;
		}
		return null;
	}


	protected abstract void parseReceivedData(List<Byte> receivedData);


	protected void testCorruptedDataPackages(List<Byte> receivedData)
	{
		int indexStart = receivedData.indexOf(TwinTracDataCreator.PACKAGE_START_KEY);
		int indexStop = receivedData.indexOf(TwinTracDataCreator.PACKAGE_STOP_KEY);
		if (indexStart > -1 && indexStop > -1 && indexStop < indexStart)
		{
			Log.e(
				logTag,
				"Catched corrupted data: "
								+ TwinTracDataCreator.byteArrayToHex(receivedData.toArray(new Byte[receivedData.size()]))
								+ " (length=" + receivedData.size() + ")");
			receivedData.subList(0, indexStart).clear();
		}
		else if (indexStart > 0 && indexStop == -1)
		{
			Log.e(
				logTag,
				"Catched corrupted data: "
								+ TwinTracDataCreator.byteArrayToHex(receivedData.toArray(new Byte[receivedData.size()]))
								+ " (length=" + receivedData.size() + ")");
			receivedData.subList(0, indexStart).clear();
		}

	}
}
