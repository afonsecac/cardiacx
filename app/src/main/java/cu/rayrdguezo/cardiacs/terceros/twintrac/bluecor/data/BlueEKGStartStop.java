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
 * 
 *          Start Stop Packet
 * 
 */

public class BlueEKGStartStop extends Data
{

	public BlueEKGStartStop()
	{
		super(DataCreator.CS_TYPE_BLUEEKG_STARTSTOP);
	}


	public BlueEKGStartStop(boolean start)
	{
		this();
		setPayload(new byte[] { (byte) (start ? 0x01 : 0x00) });
	}


	public boolean isStart()
	{
		return payload[0] == 0x01;
	}


	@Override
	public String toString()
	{
		return "BlueEKG " + (payload[0] == 0x01 ? "Start" : "Stop");
	}

}
