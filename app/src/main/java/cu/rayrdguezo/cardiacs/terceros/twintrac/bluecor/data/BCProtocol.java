package cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data;

/**
 * 
 * This packet is returned when the device sends its Protocol information upon
 * start of the communication. The protocol version returned by this packet will
 * be used by the DataCreator to interpret packet data correctly.
 * 
 * @author gmelin
 * 
 * 
 */
public class BCProtocol extends Data
{

	public BCProtocol()
	{
		super(DataCreator.CS_TYPE_PROTOCOL);
	}


	public int getMaxBuffer()
	{
		return (getPayload()[3] & 0xff);
	}


	public int getMaxPayloadLen()
	{
		return (getPayload()[1] & 0xff) | ((getPayload()[2] << 8) & 0xff00);
	}


	@Override
	public int getVersion()
	{
		return (getPayload()[0] & 0xff);
	}


	@Override
	public String toString()
	{
		return "Protocol Version " + getVersion() + " ";
	}
}
