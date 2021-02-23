package cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac;

import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data.TwinTracDataCreator;

/**
 * Data to create payload to start and stop BT transmission.
 * 
 * In the document page 31 it is 01h = request for PATIENT.DAT, 02h = request
 * for CONFIG.DAT
 * 
 * After couple of test it is the reverse of the description.
 * 
 * Also 02h = request for PATIENT.DAT, 01h = request for CONFIG.DAT
 * 
 * @author Hakan Sahin
 * @date 08.06.2016 08:37:29 Hakan Sahin
 */
public class TwinTracPackageMode extends TwinTracData
{
	public static final byte PACKAGE_NUMBER_MODE = 0x10;


	public TwinTracPackageMode(boolean patientData)
	{
		super(TwinTracDataCreator.MODE_FILE_CMD, PACKAGE_NUMBER_MODE,
			new byte[] {});
		if (patientData)
		{
			setPayload(new byte[] { 0x02 });
		}
		else
		{
			setPayload(new byte[] { 0x01 });
		}
	}
}
