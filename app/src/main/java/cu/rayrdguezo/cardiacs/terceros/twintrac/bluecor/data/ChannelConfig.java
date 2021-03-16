package cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data;

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
 *          This class stores the configuration of a channel. These objects are
 *          used to configure a SensorDeMultiplexer.
 * 
 */

public class ChannelConfig extends Data
{

	private int chanNum, sampFreq;
	private int digMin, digMax;
	private int physMin, physMax;
	private String unit, chName;


	public ChannelConfig()
	{
		super(DataCreator.CS_TYPE_ECG_CHANNEL_CONFIG);
	}


	public ChannelConfig(int chanNum, int sampFreq, int digMin, int digMax,
                         int physMin, int physMax, String unit, String chName)
	{
		this.chanNum = chanNum;
		this.sampFreq = sampFreq;
		this.digMin = digMin;
		this.digMax = digMax;
		this.physMin = physMin;
		this.physMax = physMax;
		this.unit = unit;
		this.chName = chName;
	}


	public int getChanNum()
	{
		return chanNum;
	}


	public String getChName()
	{
		return chName;
	}


	public int getDigMax()
	{
		return digMax;
	}


	public int getDigMin()
	{
		return digMin;
	}


	@Override
	public byte[] getPacket()
	{
		byte b[] = new byte[1 + 2 + 2 + 3 + 3 + 2 + 4 + 4];
		type = DataCreator.CS_TYPE_ECG_CHANNEL_CONFIG;

		int c = 0;

		b[c++] = (byte) (chanNum & 0xff);
		b[c++] = (byte) (sampFreq & 0xff);
		b[c++] = (byte) ((sampFreq >> 8) & 0xff);
		b[c++] = (byte) (digMin & 0xff);
		b[c++] = (byte) ((digMin >> 8) & 0xff);
		b[c++] = (byte) ((digMin >> 16) & 0xff);
		b[c++] = (byte) (digMax & 0xff);
		b[c++] = (byte) ((digMax >> 8) & 0xff);
		b[c++] = (byte) ((digMax >> 16) & 0xff);

		b[c++] = (byte) (physMin & 0xff);
		b[c++] = (byte) ((physMin >> 8) & 0xff);
		b[c++] = (byte) (physMax & 0xff);
		b[c++] = (byte) ((physMax >> 8) & 0xff);

		for (int i = 0; i < 4; i++)
		{
			b[c++] = (unit.length() > i) ? (byte) unit.charAt(i) : (byte) ' ';
		}
		for (int i = 0; i < 4; i++)
		{
			b[c++] = (chName.length() > i) ? (byte) chName.charAt(i) : (byte) ' ';
		}

		payload = b;
		return super.getPacket();
	}


	public int getPhysMax()
	{
		return physMax;
	}


	public int getPhysMin()
	{
		return physMin;
	}


	public int getSampFreq()
	{
		return sampFreq;
	}


	public String getUnit()
	{
		return unit;
	}


	public void setChanNum(int chanNum)
	{
		this.chanNum = chanNum;
	}


	public void setChName(String chName)
	{
		this.chName = chName;
	}


	public void setDigMax(int digMax)
	{
		this.digMax = digMax;
	}


	public void setDigMin(int digMin)
	{
		this.digMin = digMin;
	}


	@Override
	public void setPayload(byte b[])
	{
		super.setPayload(b);

		int c = 0;
		chanNum = (b[c++]) & 0xff;
		sampFreq = 0xffff & (((b[c++]) & 0xff) | (b[c++] << 8));
		digMin = 0xffffff & ((b[c++] & 0xff) | (0xff00 & (b[c++] << 8)) | (0xff0000 & (b[c++] << 16)));
		digMax = 0xffffff & ((b[c++] & 0xff) | (0xff00 & (b[c++] << 8)) | (0xff0000 & (b[c++] << 16)));

		physMin = ((b[c++]) & 0xff) | (b[c++] << 8);
		physMax = ((b[c++]) & 0xff) | (b[c++] << 8);

		unit = "";
		for (int i = 0; i < 4; i++)
		{
			unit += (char) b[c++];
		}
		chName = "";
		for (int i = 0; i < 4; i++)
		{
			chName += (char) b[c++];
		}

	}


	public void setPhysMax(int physMax)
	{
		this.physMax = physMax;
	}


	public void setPhysMin(int physMin)
	{
		this.physMin = physMin;
	}


	public void setSampFreq(int sampFreq)
	{
		this.sampFreq = sampFreq;
	}


	public void setUnit(String unit)
	{
		this.unit = unit;
	}


	@Override
	public String toString()
	{
		String seq = "ChannelConfig channel " + getChanNum() + " dig ("
						+ getDigMin() + ", " + getDigMax() + ") phys ("
						+ getPhysMin() + ", " + getPhysMax() + ") freq "
						+ getSampFreq() + " name " + getChName() + " ";
		for (int i = 0; i < payload.length; i++)
		{
			seq += (int) (payload[i]) + " ";
		}
		return seq;
	}

}
