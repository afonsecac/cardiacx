package cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac;

import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data.TwinTracDataCreator;

/**
 * Data to create payload to start and stop BT transmission.
 * 
 * @author Hakan Sahin
 * @date 08.06.2016 08:37:29 Hakan Sahin
 */
public class TwinTracPackagePatientRead extends TwinTracData
{
	public static final byte PACKAGE_NUMBER = 0x05;


	public TwinTracPackagePatientRead()
	{
		super(TwinTracDataCreator.READ_FILE_CMD, PACKAGE_NUMBER,
			new byte[] { (byte) 'P', (byte) 'A', (byte) 'T', (byte) 'I',
						(byte) 'E', (byte) 'N', (byte) 'T', (byte) '.',
						(byte) 'D', (byte) 'A', (byte) 'T', 0x00, 0x00, 0x00,
						0x00, 0x00 });
	}

}
