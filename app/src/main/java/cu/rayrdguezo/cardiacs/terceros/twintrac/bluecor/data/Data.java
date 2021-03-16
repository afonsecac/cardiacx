package cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data;

/**
 * The Data class is the superclass of all BlueCOR data objects. It contains the
 * payload, serial number, and a type (command) field. It can be used to write a
 * raw byte array.
 * 
 */

public class Data
{

	/** Contains the raw payload */
	protected byte[] payload = new byte[0];

	/** Contains the serial number */
	protected byte snum = 0;

	/** Type ID */
	protected int type;

	/** Flag if the Checksum was found to be correct */
	protected boolean csCorrect = true;

	/** Version of protocol that was used to generate the data */
	protected int version = 1;


	public Data(int type, int snum, byte pload[])
	{
		this(type, snum, pload, 1);
	}


	public Data(int type, int snum, byte pload[], int version)
	{
		setType(type);
		setSNum(snum);
		setPayload(pload);
		setVersion(version);
	}


	protected Data()
	{
	}


	protected Data(int type)
	{
		setType(type);
		setVersion(1);
	}


	protected Data(int type, int version)
	{
		setType(type);
		setVersion(version);
	}


	/**
	 * @return true if the checksum was found to be correct upon parsing the
	 *         packet.
	 */
	public boolean getChecksumCorrect()
	{
		return csCorrect;
	}


	/**
	 * Converts this data object to a raw byte array. Packet boundaries (0xfc,
	 * 0xfd), byte stuffing sequences (0xfe + data ^ 0x20) and calculates the
	 * checksum.
	 * 
	 * @return byte array representing the data object
	 */
	public byte[] getPacket()
	{
		byte b2[] = new byte[payload.length + 3];
		b2[0] = snum;
		b2[1] = (byte) (type & 0xff);
		b2[2] = (byte) ((type >> 8) & 0xff);
		System.arraycopy(payload, 0, b2, 3, payload.length);
		int crc = DataCreator.calculate_crc16(b2, 0, b2.length);
		byte b3[] = new byte[2 + b2.length];
		System.arraycopy(b2, 0, b3, 0, b2.length);
		b3[b3.length - 2] = (byte) (crc & 255);
		b3[b3.length - 1] = (byte) ((crc >> 8) & 255);
		byte[] b4 = DataCreator.stuff(b3);
		byte[] b5 = new byte[b4.length + 2];
		System.arraycopy(b4, 0, b5, 1, b4.length);
		b5[0] = (byte) 0xfc;
		b5[b5.length - 1] = (byte) 0xfd;
		return b5;
	}


	public byte[] getPayload()
	{
		return payload;
	}


	/**
	 * 
	 * @return serial number inside the packet (0-255)
	 */

	public int getSNum()
	{
		return (snum & 0xff);
	}


	public int getType()
	{
		return type;
	}


	/**
	 * @return Returns the protocol version that was used to generate this
	 *         packet.
	 */
	public int getVersion()
	{
		return version;
	}


	/**
	 * Sets the payload of this packet.
	 */
	public void setPayload(byte[] payload)
	{
		this.payload = payload;
	}


	/**
	 * Sets the serial number of the packet
	 * 
	 * @param sNum
	 *            0-255
	 */

	public void setSNum(int sNum)
	{
		snum = (byte) (sNum & 0xff);
	}


	public void setType(int type)
	{
		this.type = type;
	}


	/**
	 * @param version
	 *            The version to set.
	 */
	public void setVersion(int version)
	{
		this.version = version;
	}


	@Override
	public String toString()
	{
		StringBuffer buffer = new StringBuffer(getClass().getName());
		buffer.append(": ");
		buffer.append(Integer.toHexString(getType() & 0xffff));
		buffer.append(", # ");
		buffer.append(getSNum());
		buffer.append(", ");
		buffer.append(getPayload().length);
		buffer.append(" bytes: ");
		for (int i = 0; i < payload.length; i++)
		{
			buffer.append(payload[i]);
			buffer.append(" ");
		}
		return buffer.toString();
	}

	// public static void main (String args[]) throws Exception {
	// /*byte[] data = new byte[] { (byte)0xfc, (byte) 0xe, (byte) 0x10, (byte)
	// 6 , (byte)8 , (byte)0 , (byte)0xd0 , (byte)7 , (byte)0 , (byte)0 ,
	// (byte)0 , (byte)0 , (byte)0xd , (byte)0x11, (byte) 0x3f, (byte) 0x7b,
	// (byte) 0xfd };
	//		
	// DataCreator dc = new DataCreator ();
	// Data d = dc.createData(data, 1);
	// System.out.println (d);
	// System.out.println (d.csCorrect);*/
	// String pl = "fc 4 1 0 ff 0 0 1 2 3 4 5 6 7 8 9 a b c d e f 10 11 12 13 14
	// 15 16 17 18 19 1a 1b 1c 1d 1e 1f 20 21 22 23 24 25 26 27 28 29 2a 2b 2c
	// 2d 2e 2f 30 31 32 33 34 35 36 37 38 39 3a 3b 3c 3d 3e 3f 40 41 42 43 44
	// 45 46 47 48 49 4a 4b 4c 4d 4e 4f 50 51 52 53 54 55 56 57 58 59 5a 5b 5c
	// 5d 5e 5f 60 61 62 63 64 65 66 67 68 69 6a 6b 6c 6d 6e 6f 70 71 72 73 74
	// 75 76 77 78 79 7a 7b 7c 7d 7e 7f 80 81 82 83 84 85 86 87 88 89 8a 8b 8c
	// 8d 8e 8f 90 91 92 93 94 95 96 97 98 99 9a 9b 9c 9d 9e 9f a0 a1 a2 a3 a4
	// a5 a6 a7 a8 a9 aa ab ac ad ae af b0 b1 b2 b3 b4 b5 b6 b7 b8 b9 ba bb bc
	// bd be bf c0 c1 c2 c3 c4 c5 c6 c7 c8 c9 ca cb cc cd ce cf d0 d1 d2 d3 d4
	// d5 d6 d7 d8 d9 da db dc dd de df e0 e1 e2 e3 e4 e5 e6 e7 e8 e9 ea eb ec
	// ed ee ef f0 f1 f2 f3 f4 f5 f6 f7 f8 f9 fa fb fe dc fe dd fe de bb c9 fd";
	//		
	//		
	// }
}
