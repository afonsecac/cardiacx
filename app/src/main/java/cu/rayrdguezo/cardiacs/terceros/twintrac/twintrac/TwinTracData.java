package cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac;

import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data.Data;
import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data.TwinTracDataCreator;

public class TwinTracData extends Data
{

	public TwinTracData(int type, int snum, byte pload[])
	{
		super(type, snum, pload);
	}


	/**
	 * Converts this data object to a raw byte array. Packet boundaries (0xfc,
	 * 0xfd), byte stuffing sequences (0xfe + data XOR 0x20) and calculates the
	 * checksum.
	 * 
	 * @return byte array representing the data object
	 */
	@Override
	public byte[] getPacket()
	{
		byte b2[] = new byte[payload.length + 3];
		b2[0] = snum;
		b2[1] = (byte) (type & 0xff);
		b2[2] = (byte) (0x00);
		System.arraycopy(payload, 0, b2, 3, payload.length);
		long crc = TwinTracDataCreator.calculate_crc32(b2, 0, b2.length);
		byte b3[] = new byte[4 + b2.length];
		System.arraycopy(b2, 0, b3, 0, b2.length);
		b3[b3.length - 4] = (byte) (crc & 0xff);
		b3[b3.length - 3] = (byte) ((crc >> 8) & 0xff);
		b3[b3.length - 2] = (byte) ((crc >> 16) & 0xff);
		b3[b3.length - 1] = (byte) ((crc >> 24) & 0xff);
		byte[] b4 = TwinTracDataCreator.stuff(b3);
		byte[] b5 = new byte[b4.length + 2];
		System.arraycopy(b4, 0, b5, 1, b4.length);
		b5[0] = (byte) 0xfc;
		b5[b5.length - 1] = (byte) 0xfd;
		return b5;
	}

}
