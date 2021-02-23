package cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data;

/**
 * <p>
 * Title: Client-Receiver
 * </p>
 * <p>
 * Description: Package zum Empfangen und Behandeln von Daten mobiler Ger"ate
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: avetana GmbH
 * </p>
 * 
 * @author Moritz Gmelin
 * @version 1.0
 * 
 *          This class contains ecg data objects it can handle 12 and 16 bit
 *          values depending of its real type. CS_TYPE_ECG_BLUEEKG or
 *          CS_TYPE_ECG_BLUEEKG_24INIT represent 8 / 8 bits while CS_TYPE_ECG
 *          represents 5 low and 7 high bits.
 * 
 *          The actual digital ecg value can be retrieved with the valueAt()
 *          method
 * 
 */

import java.io.ByteArrayOutputStream;

public class ECGData extends Data
{

	private static final int FULL_BYTE = 0xFF;

	/** allocation of bytes when converting ecg values to byte array */
	protected int blow = 5, bhigh = 7;

	/** Buffer to store ecg values */
	protected int values[] = new int[0];

	/** Hear rate information if known */
	protected int hr;


	public ECGData()
	{
		this(DataCreator.CS_TYPE_ECG_BLUEEKG);
	}


	public ECGData(int type)
	{
		this(type, 1);
	}


	public ECGData(int type, int version)
	{
		super(type, version);
		if ((type == DataCreator.CS_TYPE_ECG_BLUEEKG)
			|| (type == DataCreator.CS_TYPE_ECG_BLUEEKG_24INIT)
			|| (type == DataCreator.CS_TYPE_ECG_BLUEEKG_24REL))
		{
			blow = 8;
			bhigh = 8;
		}
		else
		{
			blow = 7;
			bhigh = 5;
		}
		hr = -1;

	}


	public ECGData(int[] data)
	{
		this(data, 0, data.length);
	}


	/**
	 * 
	 * @param ecgValues
	 *            Array containing all ECG values ordered by channel and time
	 * @param hr
	 *            this heartrate value is, in contrast to {@link #setHr(int)},
	 *            considered when assebling the packet for sending; it will be
	 *            set at the first byte of the payload.
	 */
	public ECGData(int[] ecgValues, int hr)
	{
		this();
		this.hr = hr;
		values = ecgValues;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bos.write((byte) (FULL_BYTE & hr));
		writeValuesToByteStream(bos);
		payload = bos.toByteArray();
	}


	/**
	 * Initialize the object with given ecg data. The data is stored in a byte[]
	 * in either 5 / 7 or 8 / 8 bits sequences.
	 * 
	 * @param data
	 * @param off
	 * @param len
	 */
	public ECGData(int[] data, int off, int len)
	{
		this();
		values = new int[len];
		System.arraycopy(data, off, values, 0, len);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		writeValuesToByteStream(bos);
		payload = bos.toByteArray();
	}


	public int getHr()
	{
		return hr;
	}


	public int numValues()
	{
		return values.length;
	}


	/**
	 * Sets the {@link #hr} field. This field is not taken into concern when
	 * assembling the package for sending!
	 * 
	 * @param hr
	 */
	public void setHr(int hr)
	{
		this.hr = hr;
	}


	/**
	 * Sets the payload to the given byte-array.
	 * 
	 * Additionally this method is called from the DataCreator when the object
	 * is assembled. It parses the data in the payload and initlaizes its values
	 * array.
	 * 
	 * For packets that don't have the type
	 * {@link DataCreator#CS_TYPE_ECG_BLUEEKG_24REL} or
	 * {@link DataCreator#CS_TYPE_ECG_BLUEEKG_24INIT} a heartrate is extracted
	 * from the last.
	 * 
	 * @param b
	 */

	@Override
	public void setPayload(byte b[])
	{
		super.setPayload(b);

		int[] iValues = new int[b.length / 2];

		int count;
		if ((type == DataCreator.CS_TYPE_ECG_BLUEEKG_24REL)
			|| (type == DataCreator.CS_TYPE_ECG_BLUEEKG_24INIT))
		{
			count = createValuesFrom3Bytes(b, iValues);
		}
		else
		{
			// Skip first bytefor HR if odd number of bytes in payload
			if ((b.length % 2) == 1)
			{
				byte[] noHR = new byte[b.length - 1];
				System.arraycopy(b, 1, noHR, 0, noHR.length);
				count = createValuesFrom2Bytes(noHR, iValues);
				setHr(FULL_BYTE & b[0]);
			}
			else
			{
				count = createValuesFrom2Bytes(b, iValues);
			}
		}

		if (count == iValues.length)
		{
			values = iValues;
		}
		else
		{
			values = new int[count];
			System.arraycopy(iValues, 0, values, 0, count);
		}
	}


	@Override
	public String toString()
	{
		return getClass() + " length " + numValues();
	}


	/**
	 * returns digital ecg value at a given position.
	 * 
	 * @param i
	 * @return
	 */

	public int valueAt(int i)
	{
		return values[i];
	}


	/**
	 * Return all raw ECG values
	 * 
	 * @return
	 */

	public int[] values()
	{
		return values;
	}


	/**
	 * @param bytes
	 * @param values
	 * @return
	 */
	protected int createValuesFrom2Bytes(byte[] bytes, int[] values)
	{
		int count = 0;
		for (int i = 0; i < bytes.length;)
		{
			values[count++] = 0xffff & ((bytes[i++] & ((1 << blow) - 1)) | ((bytes[i++] & ((1 << bhigh) - 1)) << blow));
		}
		return count;
	}


	/**
	 * @param bytes
	 * @param values
	 * @return
	 */
	protected int createValuesFrom3Bytes(byte[] bytes, int[] values)
	{
		int count = 0;
		for (int i = 0; i < bytes.length;)
		{
			values[count++] = (bytes[i++] & 0xff) | ((bytes[i++] & 0xff) << 8)
								| ((bytes[i++] & 0xff) << 16);
		}
		return count;
	}


	/**
	 * @param byteArrayOutputStream
	 */
	private void writeValuesToByteStream(
					ByteArrayOutputStream byteArrayOutputStream)
	{
		for (int i = 0; i < values.length; i++)
		{
			if (values[i] < 0xffff)
			{
				byteArrayOutputStream.write(values[i] & ((1 << blow) - 1));
				byteArrayOutputStream.write((values[i] >> blow)
											& ((1 << bhigh) - 1));
			}
		}
	}

}
