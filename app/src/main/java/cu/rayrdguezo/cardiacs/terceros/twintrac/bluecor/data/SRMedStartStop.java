package cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data;

/**
 * Data to create payload to start and stop BT transmission.
 * 
 * @author Hakan Sahin
 * @date 08.06.2016 08:37:29 Hakan Sahin
 */
public class SRMedStartStop extends Data
{
	public SRMedStartStop()
	{
		super(DataCreator.CS_TYPE_SRMED_STARTSTOP);
	}


	public SRMedStartStop(boolean start)
	{
		this();
		setPayload(new byte[] { (byte) (start ? 0x61 : 0x1b) });
	}


	public boolean isStart()
	{
		return payload[0] == 0x61;
	}


	@Override
	public String toString()
	{
		return "SRMed " + (payload[0] == 0x61 ? "Start" : "Stop");
	}
}
