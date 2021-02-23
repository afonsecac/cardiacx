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
 *          Represents an Acknowlege Data-Packet
 * 
 */

public class AckData extends Data
{

	public AckData()
	{
		super(DataCreator.ACK_TYPE);
	}


	public AckData(int snum)
	{
		this();
		payload = new byte[] { (byte) snum };
	}

}
