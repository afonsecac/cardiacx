package cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor;

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
 *          Empfänger für Daten und das Schliessen Event vom Endgerät.
 * 
 * 
 */

import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data.Data;

public interface BCTarget
{

	public void channelClosed();


	public void newData(Data d);


	public void newData(double[] data);


	public void patientInformation(byte[] data);
}
