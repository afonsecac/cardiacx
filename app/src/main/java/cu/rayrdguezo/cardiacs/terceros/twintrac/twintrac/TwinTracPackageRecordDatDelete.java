package cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac;

import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data.TwinTracDataCreator;

/**
 * Data to create payload to start and stop BT transmission.
 * 
 * @author Hakan Sahin
 * @date 08.06.2016 08:37:29 Hakan Sahin
 */
public class TwinTracPackageRecordDatDelete extends TwinTracData
{
	public static final byte PACKAGE_NUMBER = 0x02;


	public TwinTracPackageRecordDatDelete()
	{
		//		super(TwinTracDataCreator.DELETE_RECORD_DAT_FILE_CMD, PACKAGE_NUMBER,
		//			new byte[] { 0x52, 0x45, 0x43, 0x4F, 0x52, 0x44, 0x2E, 0x44, 0x41,
		//						0x54, 0x00, 0x00 });
		super(TwinTracDataCreator.DELETE_RECORD_DAT_FILE_CMD, PACKAGE_NUMBER,
			new byte[] { (byte) 'R', (byte) 'E', (byte) 'C', (byte) 'O',
						(byte) 'R', (byte) 'D', (byte) '.', (byte) 'D',
						(byte) 'A', (byte) 'T', 0x00, 0x00 });

	}
}
