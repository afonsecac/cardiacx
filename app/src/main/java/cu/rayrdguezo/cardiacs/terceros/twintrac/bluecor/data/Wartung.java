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
 */

public class Wartung extends Data
{

	public Wartung()
	{
		super(DataCreator.CS_TYPE_WARTUNG);
	}


	public Wartung(byte[] content) throws Exception
	{
		this();
		payload = new byte[46];
		System.arraycopy(content, 0, payload, 0, 46);
	}


	public int getBatterieLadung()
	{
		byte data[] = new byte[4];
		System.arraycopy(payload, 42, data, 0, 4);
		return (data[0] & 0x01) | ((data[1] & 0x7f) << 1)
				| ((data[2] & 0x01) << 8) | ((data[3] & 0x7f) << 9);

	}


	public byte[] getSelbsttestGross()
	{
		byte b[] = new byte[17];
		System.arraycopy(payload, 17, b, 0, 17);
		return b;
	}


	public byte[] getSelbsttestKlein()
	{
		byte b[] = new byte[17];
		System.arraycopy(payload, 0, b, 0, 17);
		return b;
	}


	public byte[] getSoftwareVersion()
	{
		byte b[] = new byte[8];
		System.arraycopy(payload, 34, b, 0, 8);
		return b;
	}

}
