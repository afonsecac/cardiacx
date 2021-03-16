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

public class IdentData extends Data
{

	public IdentData()
	{
		super(DataCreator.CS_TYPE_IDENT);
	}


	public IdentData(int manu, int model, String sernum)
	{
		super(DataCreator.CS_TYPE_IDENT);
		payload = new byte[22];
		payload[0] = (byte) manu;
		payload[1] = (byte) model;
		while (sernum.length() < 20)
		{
			sernum += " ";
		}
		System.arraycopy(sernum.substring(0, 20).getBytes(), 0, payload, 2, 20);
	}


	public String getDeviceID()
	{
		switch (payload[1])
		{
			case 1:
			return "Cardiomat Easy";
			case 2:
			return "Cardiomat Trainer";
			case 3:
			return "Cardiowecker";
			case 0x1e:
			return "EMI12";
			default:
			return "Unbekannt";
		}

	}


	public String getManu()
	{
		switch (payload[0])
		{
			case 1:
			return "Corscience";
			default:
			return "Unbekannt";
		}
	}


	public String getSerialNum()
	{
		return new String(payload, 2, Math.min(20, payload.length - 2)).trim();
	}


	public static void main(String args[])
	{
		Data d = new IdentData(1, 3, "0011223344");

		byte[] b = d.getPacket();

		for (int i = 0; i < b.length; i++)
		{
			System.out.print(" (byte)0x" + Integer.toHexString((b[i] & 0xff))
								+ ", ");
		}

		System.out.println();
		System.out.println("" + d.toString());

	}

}
