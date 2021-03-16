package cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data;

/**
 * 
 * @author Moritz Gmelin
 * @version 1.0
 * 
 *          This packet is sent to and received from the BT12 device to signal
 *          the type of data that will be received.
 * 
 */

public class BlueEKGConfig extends Data
{

	public static final byte LEADS8 = 2;
	public static final byte LEADS2 = 1;

	public static final int FREQ500 = 500;


	public BlueEKGConfig()
	{
		super(DataCreator.CS_TYPE_BLUEEKG_CONFIG);
	}


	/**
	 * Build a configuration packets
	 * 
	 * @param freq
	 *            frequency requested (500)
	 * @param channels
	 *            1 = 2 channels, 2 = 8 channels
	 */

	public BlueEKGConfig(int freq, byte channels)
	{
		this();
		setPayload(new byte[] { channels, (byte) (freq / 100) });
	}


	/**
	 * 
	 * @return sampling frequency of the signal
	 */

	public int getFreq()
	{
		return payload[1] * 100;
	}


	/**
	 * 
	 * @return Number of transmitted channels. 1 = 2 channels, 2 = 8 channels
	 */

	public int getNumChannels()
	{
		if (payload[0] == 0)
		{
			return 0;
		}
		else if (payload[0] == 1)
		{
			return 2;
		}
		else if (payload[0] == 2)
		{
			return 3;
		}
		else
		{
			return -1;
		}
	}


	@Override
	public String toString()
	{
		return "BlueEKGConfig Freq : " + getFreq() + " Channels : "
				+ getNumChannels();
	}

}
