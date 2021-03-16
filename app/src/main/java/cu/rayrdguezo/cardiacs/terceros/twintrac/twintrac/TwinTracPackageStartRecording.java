package cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac;

import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data.TwinTracDataCreator;

/**
 * Data to create payload to start and stop BT transmission.
 * 
 * @author Hakan Sahin
 * @date 08.06.2016 08:37:29 Hakan Sahin
 */
public class TwinTracPackageStartRecording extends TwinTracData
{
	public static final byte PACKAGE_NUMBER = 0x03;


	public TwinTracPackageStartRecording()
	{
		super(TwinTracDataCreator.START_RECORDING_CMD, PACKAGE_NUMBER,
			new byte[] {});
		setPayload(TwinTracDataCreator.createDateTimeBCD());
	}
}
