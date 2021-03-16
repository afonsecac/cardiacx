package cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data;

import java.io.ByteArrayOutputStream;

/**
 * @author Moritz Gmelin
 * @version 1.0
 * 
 *          BlueECGData represents the specific data type of the BlueECG device.
 *          It can handle 24 bit serial numbers by packing the 2 higher value
 *          bytes into the payload. It handles signaling for Heart-Rate, Battery
 *          status, Electrode status and Heart-Rate limits. The class was
 *          designed to handle 16 and 24 bit ECG data but is currently only used
 *          with 16 bit data.
 * 
 */

public class BlueECGData extends ECGData
{

	/** This flag is added to an ECG Value if a Pacemaker impulse was detected */
	public static final short PACEMAKER_DETECTED = (short) 0x8000;

	/** Flag set when the specified Heart-Rate-Limits have been broken */
	protected int m_hrlim;

	/** Flag indicating current battery status */
	protected int m_bat;

	/** Flag indicating current electrode contacts */
	protected int m_eStat;

	/** Currently measured heart-rate */
	protected int m_hr;

	/** Flag indicating that a pacemaker impulse has been detected */
	protected boolean flag_pacemaker;


	public BlueECGData(int type, int version)
	{
		super(type, version);
		blow = 8;
		bhigh = 8;
	}


	/**
	 * Initialize the packet with ecg data included.
	 * 
	 * @param data
	 * @param version
	 */

	public BlueECGData(int[] data, int version)
	{
		this(data, 0, data.length, version);
	}


	public BlueECGData(int[] data, int off, int len, int version)
	{
		this(DataCreator.CS_TYPE_ECG_BLUEEKG_24INIT, version);
		values = new int[len];
		System.arraycopy(data, off, values, 0, len);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try
		{
			bos.write(new byte[] { 0 });
			if (version >= 2)
			{
				bos.write(new byte[] { 0 });
			}
			for (int i = 0; i < values.length; i++)
			{
				bos.write(new byte[] {
										(byte) (values[i] & ((1 << blow) - 1)),
										(byte) ((values[i] >> blow) & ((1 << bhigh) - 1)) });
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		payload = bos.toByteArray();
	}


	/**
	 * Gets current battery status of the device.
	 * 
	 * @return value from 0-100
	 */

	public int getBattery()
	{
		return m_bat;
	}


	/**
	 * 
	 * @return bits 0-9 are true if the electrode is connected
	 */

	public int getEStat()
	{
		return m_eStat;
	}


	/**
	 * 0 == noLimitExceeded 1 == lowerLimit Exceeded 2 == upperLimit Exceeded
	 * 
	 * @return
	 */
	public int getHRLimitEx()
	{
		return m_hrlim;
	}


	@Override
	public byte[] getPayload()
	{
		byte[] b2 = new byte[payload.length - (version == 1 ? 1 : 2)];
		System.arraycopy(payload, (version == 1 ? 1 : 2), b2, 0, b2.length);
		return b2;
	}


	/**
	 * Get the serial number of the packet. For protocol version 1 this can be
	 * 16 bit values, for protocol version 2 those are 24 bit values.
	 * 
	 * @return serial number
	 */

	@Override
	public int getSNum()
	{
		if (version == 1)
		{
			int lsb = super.getSNum();
			int msb = payload[0];
			return (0xffff & ((lsb & 0xff) | ((msb & 0xff) << 8)));
		}
		else if (version == 2)
		{
			int lsb = super.getSNum();
			int msb1 = payload[0];
			int msb2 = payload[1];
			return (0xffffff & ((lsb & 0xff) | ((msb1 & 0x7f) << 8) | ((msb2 & 0x7f) << 15)));
		}
		else if ((version == 3) || (version == 5) || (version == 6))
		{
			int lsb = super.getSNum();
			int msb1 = payload[0];
			int msb2 = payload[1];
			return (0xffffff & ((lsb & 0xff) | ((msb1 & 0x7f) << 8) | ((msb2 & 0x7f) << 15)));
		}
		else
		{
			throw new Error("Unknown Packet version " + version);
		}
	}


	/**
	 * @return Returns the flag_pacemaker.
	 */
	public boolean isFlag_pacemaker()
	{
		return flag_pacemaker;
	}


	/**
	 * Decodes the information contained in the payload of the packet. ECG data
	 * is decompressed, battery and electrode status is read and the pacemaker
	 * status is decoded.
	 * 
	 */
	public void processPayload()
	{

		values = new int[payload.length - 1];

		int i = 0;
		int w_Last_Pos_In_Decoded_Data = 0;
		int s8_Buffered_High_Byte = 0;
		int s16_Decoded_Val = 0;
		boolean flag_overflow = false;

		flag_pacemaker = false;

		int off = version == 1 ? 1 : 2;
		int post = version == 5 ? 4 : 0;

		super.setHr((0xff & payload[off]));

		m_bat = (0x03 & (payload[off + 1] >> 5)) * 33;

		m_hrlim = (0x73 & (payload[off + 1] >> 3));

		flag_pacemaker = (payload[off + 1] & 0x80) == 0x80;
		/*
		 * if (flag_pacemaker) { byte d[] = getPacket();
		 * System.out.println("RAW packet v " + version); for (int j = 0; j <
		 * d.length; j++) System.out.print(" " + Integer.toHexString((int) (d[j]
		 * & 0xff))); System.out.println(); }
		 */
		m_eStat = ((payload[off + 2] & 0x7f) | ((payload[off + 1] & 0x07) << 7));
		// m_eStat = (int)(2f * Math.random());

		// if (100 * Math.random() < 2) flag_pacemaker = true;

		// System.out.println ((int)(payload[1] & 0xff) + " " + (int)(payload[2]
		// & 0xff) + " " + (int)(payload[3] & 0xff));

		for (i = off + 3; i < (payload.length - post); i++)
		{
			// if (lc++ < 1000) System.out.println ("<- " +
			// Integer.toHexString((int)(payload[i] & 0xff)));
			if (!flag_overflow)
			{
				if ((payload[i] & 0x01) == 0x01)
				{
					// LSb=1 ->8 bit overflow
					s8_Buffered_High_Byte = payload[i] >> 1;
					flag_overflow = true;
				}
				else
				{
					// LSb==0 -> decoding possible: 2 Byte word decoding
					s16_Decoded_Val = payload[i] >> 1;
					flag_overflow = false;
					// write result to OUTPUT ARRAY
					values[w_Last_Pos_In_Decoded_Data++] = 0x7fff & Math.max(1,
						(s16_Decoded_Val + 16384));
					// values[w_Last_Pos_In_Decoded_Data++]= s16_Decoded_Val;
					// if (lc++ < 1000) System.out.println (" ---> "+
					// values[w_Last_Pos_In_Decoded_Data - 1]);
				}
			}
			else if (flag_overflow)
			{
				s16_Decoded_Val = ((s8_Buffered_High_Byte << 8) | (payload[i] & 0xff));
				// if ((byte)(s8_Buffered_High_Byte & 0x80) == (byte)0x80)
				// s16_Decoded_Val = -65536 + (int)s16_Decoded_Val;
				flag_overflow = false;
				// write result to OUTPUT ARRAY
				values[w_Last_Pos_In_Decoded_Data++] = 0x7fff & Math.max(1,
					(s16_Decoded_Val + 16384));
				// values[w_Last_Pos_In_Decoded_Data++]= s16_Decoded_Val;
				// if (lc++ < 1000) System.out.println (" --> " +
				// values[w_Last_Pos_In_Decoded_Data - 1]);
			}
		}// for

		if (flag_pacemaker)
		{
			for (int j = 0; j < values.length; j++)
			{
				values[j] |= 0x8000;
			}
		}

		int v2[] = new int[w_Last_Pos_In_Decoded_Data];
		System.arraycopy(values, 0, v2, 0, v2.length);
		values = v2;

		//		 System.out.print ("ProcessPayload result");
		//		 for (i = 1;i < 21;i++) System.out.print (" " +
		// Integer.toHexString(payload[i] & 0xff));
		// System.out.println ();
		// for (i = 0;i < 10;i++) System.out.print (" " +
		// Integer.toHexString(values[i]));
		// System.out.println ();
		//		 for (i = 0;i < values.length;i += 2) System.out.print ("\t" + (values[i] - 16384));
		//		 System.out.println ();

	}


	public int realValueAt(int pos)
	{
		return super.valueAt(pos) & 0x7fff;
	}


	/**
	 * @param flag_pacemaker
	 *            The flag_pacemaker to set.
	 */
	public void setFlag_pacemaker(boolean flag_pacemaker)
	{
		this.flag_pacemaker = flag_pacemaker;
		for (int i = 0; i < values.length; i++)
		{
			values[i] |= PACEMAKER_DETECTED;
			if (!flag_pacemaker)
			{
				values[i] ^= PACEMAKER_DETECTED;
			}
		}
	}


	/**
	 * 
	 * @return heartrate detected while packet was generated on the device.
	 */

	@Override
	public void setHr(int hr)
	{
		super.setHr(hr);
		int off = version == 1 ? 1 : 2;
		payload[off] = (byte) (0xff & hr);
	}


	@Override
	public void setPayload(byte[] b)
	{
		payload = b;
	}


	/**
	 * Set the serial number of the packet
	 * 
	 * param serial
	 *            number to be set (16 bit max for protocol version 1, 24 bit
	 *            for version 2)
	 */

	@Override
	public void setSNum(int num)
	{
		super.setSNum(num & 0xff);
		if (version == 1)
		{
			if ((payload == null) || (payload.length == 0))
			{
				payload = new byte[1];
			}
			payload[0] = (byte) (0xff & (num >> 8));
		}
		else if ((version == 2) || (version == 3))
		{
			if ((payload == null) || (payload.length == 0))
			{
				payload = new byte[2];
			}
			payload[0] = (byte) (0x7f & (num >> 8));
			payload[1] = (byte) (0x7f & (num >> 15));
		}
	}


	public short shortValueAt(int pos)
	{
		return (short) (super.valueAt(pos) & 0x7fff);
	}


	@Override
	public String toString()
	{
		return "BlueECGData Packet (" + getSNum() + ") of length "
				+ payload.length;
	}

}