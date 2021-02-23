package cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data;

/**
 * <p>
 * Title: BlueCor
 * </p>
 * <p>
 * Description: Corscience Bluetooth Protokolldaten Konverter
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: avetana GmbH
 * </p>
 * 
 * @author Moritz Gmelin
 * @version 1.0
 */

public class ECGConfig extends Data
{

	private int numChannels = -1;
	private int channels[] = null;


	public ECGConfig()
	{
		super(DataCreator.CS_TYPE_ECG_CONFIG);
	}


	public ECGConfig(byte sequence[])
	{
		this();
		payload = sequence;
		numChannels = -1;
		channels = null;
	}


	public int[] getChannels()
	{
		getConfig();
		return channels;
	}


	public int numChannels()
	{
		getConfig();
		return numChannels;
	}


	@Override
	public void setPayload(byte b[])
	{
		numChannels = -1;
		super.setPayload(b);
	}


	@Override
	public String toString()
	{
		String seq = "ECGConfig sequence ";
		for (int i = 0; i < payload.length; i++)
		{
			seq += (int) (payload[i]) + " ";
		}
		return seq;
	}


	private void getConfig()
	{
		if (numChannels != -1)
		{
			return;
		}
		numChannels = 0;
		int names[] = new int[payload.length];
		int names2[] = new int[payload.length];
		for (int i = 0; i < names.length; i++)
		{
			names[i] = -1;
		}
		for (int i = 0; i < payload.length; i++)
		{
			if (names[payload[i]] == -1)
			{
				names2[numChannels++] = payload[i];
				names[payload[i]] = 1;
			}
		}
		channels = new int[numChannels];
		System.arraycopy(names2, 0, channels, 0, numChannels);
	}
}
