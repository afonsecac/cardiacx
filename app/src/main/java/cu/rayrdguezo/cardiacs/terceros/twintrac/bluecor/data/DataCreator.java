package cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data;

/**
 * 
 * @author mgmelin
 * 
 */
public class DataCreator
{

	public static final int TERM_TYPE = 0x0000, ACK_TYPE = 0x0200,
					NACK_TYPE = 0x0300, REJ_TYPE = 0x0400,
					PROTOCOL_TYPE = 0x0100, DELETE_OLD_VALUES = 0x010a;
	public final static int CS_TYPE_IDENT = 0x0500, CS_TYPE_ECG = 0x0706,
					CS_TYPE_ECG_CMEASY = 0x0607, CS_TYPE_ECG_BLUEEKG = 0x0700,
					CS_TYPE_ECG_BLUEEKG_24INIT = 0x0724,
					CS_TYPE_ECG_BLUEEKG_24REL = 0x725,
					CS_TYPE_WARTUNG = 0x0600, CS_TYPE_ECG_CONFIG = 0x0709,
					CS_TYPE_ECG_CHANNEL_CONFIG = 0x070a;
	public final static int CS_TYPE_BLUEEKG_CONFIG = 0x0901,
					CS_TYPE_BLUEEKG_STARTSTOP = 0x0905,
					CS_TYPE_SRMED_STARTSTOP = 0x0909,
					CS_TYPE_BLUEEKG_CONFIG_RET = 0x0701;
	public final static int CS_TYPE_PROTOCOL = 0x0100;
	public final static int CS_TYPE_REQUEST = 0x0800;

	private static final int INITIAL_REMAINDER_CCITT = 0xFFFF;

	private static final int crctttab[] = new int[] { 0x0000, 0x1021, 0x2042,
														0x3063, 0x4084, 0x50A5,
														0x60C6, 0x70E7, 0x8108,
														0x9129, 0xA14A, 0xB16B,
														0xC18C, 0xD1AD, 0xE1CE,
														0xF1EF, 0x1231, 0x0210,
														0x3273, 0x2252, 0x52B5,
														0x4294, 0x72F7, 0x62D6,
														0x9339, 0x8318, 0xB37B,
														0xA35A, 0xD3BD, 0xC39C,
														0xF3FF, 0xE3DE, 0x2462,
														0x3443, 0x0420, 0x1401,
														0x64E6, 0x74C7, 0x44A4,
														0x5485, 0xA56A, 0xB54B,
														0x8528, 0x9509, 0xE5EE,
														0xF5CF, 0xC5AC, 0xD58D,
														0x3653, 0x2672, 0x1611,
														0x0630, 0x76D7, 0x66F6,
														0x5695, 0x46B4, 0xB75B,
														0xA77A, 0x9719, 0x8738,
														0xF7DF, 0xE7FE, 0xD79D,
														0xC7BC, 0x48C4, 0x58E5,
														0x6886, 0x78A7, 0x0840,
														0x1861, 0x2802, 0x3823,
														0xC9CC, 0xD9ED, 0xE98E,
														0xF9AF, 0x8948, 0x9969,
														0xA90A, 0xB92B, 0x5AF5,
														0x4AD4, 0x7AB7, 0x6A96,
														0x1A71, 0x0A50, 0x3A33,
														0x2A12, 0xDBFD, 0xCBDC,
														0xFBBF, 0xEB9E, 0x9B79,
														0x8B58, 0xBB3B, 0xAB1A,
														0x6CA6, 0x7C87, 0x4CE4,
														0x5CC5, 0x2C22, 0x3C03,
														0x0C60, 0x1C41, 0xEDAE,
														0xFD8F, 0xCDEC, 0xDDCD,
														0xAD2A, 0xBD0B, 0x8D68,
														0x9D49, 0x7E97, 0x6EB6,
														0x5ED5, 0x4EF4, 0x3E13,
														0x2E32, 0x1E51, 0x0E70,
														0xFF9F, 0xEFBE, 0xDFDD,
														0xCFFC, 0xBF1B, 0xAF3A,
														0x9F59, 0x8F78, 0x9188,
														0x81A9, 0xB1CA, 0xA1EB,
														0xD10C, 0xC12D, 0xF14E,
														0xE16F, 0x1080, 0x00A1,
														0x30C2, 0x20E3, 0x5004,
														0x4025, 0x7046, 0x6067,
														0x83B9, 0x9398, 0xA3FB,
														0xB3DA, 0xC33D, 0xD31C,
														0xE37F, 0xF35E, 0x02B1,
														0x1290, 0x22F3, 0x32D2,
														0x4235, 0x5214, 0x6277,
														0x7256, 0xB5EA, 0xA5CB,
														0x95A8, 0x8589, 0xF56E,
														0xE54F, 0xD52C, 0xC50D,
														0x34E2, 0x24C3, 0x14A0,
														0x0481, 0x7466, 0x6447,
														0x5424, 0x4405, 0xA7DB,
														0xB7FA, 0x8799, 0x97B8,
														0xE75F, 0xF77E, 0xC71D,
														0xD73C, 0x26D3, 0x36F2,
														0x0691, 0x16B0, 0x6657,
														0x7676, 0x4615, 0x5634,
														0xD94C, 0xC96D, 0xF90E,
														0xE92F, 0x99C8, 0x89E9,
														0xB98A, 0xA9AB, 0x5844,
														0x4865, 0x7806, 0x6827,
														0x18C0, 0x08E1, 0x3882,
														0x28A3, 0xCB7D, 0xDB5C,
														0xEB3F, 0xFB1E, 0x8BF9,
														0x9BD8, 0xABBB, 0xBB9A,
														0x4A75, 0x5A54, 0x6A37,
														0x7A16, 0x0AF1, 0x1AD0,
														0x2AB3, 0x3A92, 0xFD2E,
														0xED0F, 0xDD6C, 0xCD4D,
														0xBDAA, 0xAD8B, 0x9DE8,
														0x8DC9, 0x7C26, 0x6C07,
														0x5C64, 0x4C45, 0x3CA2,
														0x2C83, 0x1CE0, 0x0CC1,
														0xEF1F, 0xFF3E, 0xCF5D,
														0xDF7C, 0xAF9B, 0xBFBA,
														0x8FD9, 0x9FF8, 0x6E17,
														0x7E36, 0x4E55, 0x5E74,
														0x2E93, 0x3EB2, 0x0ED1,
														0x1EF0 };


	/**
	 * 
	 * @param content
	 * @param version
	 * @return
	 * @throws Exception
	 */
	public Data createData(byte[] content, int version) throws Exception
	{
		boolean beg = content[0] == (byte) 0xfc;
		boolean end = content[content.length - 1] == (byte) 0xfd;
		boolean error = false;
		int lendiff = (beg ? 1 : 0) + (end ? 1 : 0);
		byte[] b2 = unstuff(content, beg ? 1 : 0, content.length - lendiff);
		int crc16 = 0xffff & ((b2[b2.length - 2] & 0xff) | ((b2[b2.length - 1] & 0xff) << 8));
		int crc16data = calculate_crc16(b2, 0, b2.length - 2);
		int crc16inv = 0xffff & (((crc16 & 0xff) << 8) | ((crc16 >> 8) & 0xff));
		if ((crc16 != 65535) && (crc16data != crc16) && (crc16data != crc16inv))
		{
			for (int i = 0; i < content.length; i++)
			{
				System.out.print(" " + Integer.toHexString((content[i] & 0xff)));
			}
			System.out.println("\nFehlerhaftes Paket snum:"
								+ Integer.toHexString(b2[0] & 0xff)
								+ " typ:"
								+ (((b2[2]) << 8) | b2[1])
								+ " len:"
								+ b2.length
								+ " crc16:"
								+ Integer.toHexString(crc16)
								+ "!="
								+ Integer.toHexString(0xffff & calculate_crc16(
									b2, 0, b2.length - 2)));
			error = true;
		}

		int snum = (b2[0] & 0xff);
		int type = ((0xff & (b2[2])) << 8) | (0xff & b2[1]);

		Data retD = createInstance(type, version);

		retD.csCorrect = !error;

		int pl = b2.length - 5;
		if ((type == CS_TYPE_ECG) && ((pl % 2) != 0)
			&& (content[content.length - 2] == (byte) 0xff))
		{
			pl++; // Ganz "ubler Spezialfall, wo die Chechsumme nur 1-Bytig
			// implementiert und falsch ist.
		}

		byte payload[] = new byte[pl];
		System.arraycopy(b2, 3, payload, 0, payload.length);

		/*
		 * if (error) { for (int i = 0;i < content.length;i++) System.out.print
		 * (Integer.toHexString((int)content[i] & 0xff) + " ");
		 * System.out.println ("\n--------- " + pl); }
		 */

		retD.setType(type);
		retD.setSNum(snum);
		try
		{
			retD.setPayload(payload);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			retD.csCorrect = false;
		}
		return retD;
	}


	/**
	 * 
	 * @param type
	 * @param version
	 * @return
	 */
	protected Data createInstance(int type, int version)
	{
		Data retD;

		switch (type)
		{
			case TERM_TYPE:
			retD = new TermData();
				break;
			case ACK_TYPE:
			retD = new AckData();
				break;
			// case NACK_TYPE: retD = new NackData(); break;
			// case REJ_TYPE: retD = new TermData(); break;
			case CS_TYPE_IDENT:
			retD = new IdentData();
				break;
			case CS_TYPE_ECG_CMEASY:
			case CS_TYPE_ECG:
			retD = new ECGData(CS_TYPE_ECG, version);
				break;
			case CS_TYPE_ECG_BLUEEKG:
			retD = new ECGData(CS_TYPE_ECG_BLUEEKG, version);
				break;
			case CS_TYPE_ECG_BLUEEKG_24REL:
			case CS_TYPE_ECG_BLUEEKG_24INIT:
			retD = new BlueECGData(type, version);
				break;
			case CS_TYPE_WARTUNG:
			retD = new Wartung();
				break;
			case CS_TYPE_ECG_CONFIG:
			retD = new ECGConfig();
				break;
			case CS_TYPE_ECG_CHANNEL_CONFIG:
			retD = new ChannelConfig();
				break;
			case CS_TYPE_BLUEEKG_CONFIG:
			case CS_TYPE_BLUEEKG_CONFIG_RET:
			retD = new BlueEKGConfig();
				break;
			case CS_TYPE_BLUEEKG_STARTSTOP:
			retD = new BlueEKGStartStop();
				break;
			case CS_TYPE_SRMED_STARTSTOP:
			retD = new SRMedStartStop();
				break;
			case CS_TYPE_PROTOCOL:
			retD = new BCProtocol();
				break;
			default:
			retD = new Data(type);
		}

		return retD;
	}


	/**
	 * 
	 * @param zeiger_auf_string
	 * @param offset
	 * @param laenge
	 * @return
	 */
	/* crc16 funktion */
	public static int calculate_crc16(byte[] zeiger_auf_string, int offset,
					int laenge)
	{
		int i;
		int result = INITIAL_REMAINDER_CCITT; // Startwert 0xFFFF

		for (i = offset; i < (laenge + offset); i++)
		{
			result = updcrc_tab(result, zeiger_auf_string[i]); // 0x1021;
			// Initial
			// 0xFFFF
		}

		return result;
	}


	/**
	 * 
	 * @param b
	 * @param start
	 * @param len
	 * @return
	 */
	public static byte[] unstuff(byte[] b, int start, int len)
	{
		byte us[] = new byte[b.length];
		int scount = 0;
		for (int i = start; i < (len + start); i++)
		{
			if (b[i] == (byte) 0xfe)
			{
				i++;
				us[scount] = (byte) (b[i] ^ 0x20);
			}
			else
			{
				us[scount] = b[i];
			}
			scount++;
		}
		byte us2[] = new byte[scount];
		System.arraycopy(us, 0, us2, 0, scount);
		return us2;
	}


	/**
	 * 
	 * @param b
	 * @return
	 */
	protected static byte[] stuff(byte[] b)
	{

		byte stuffed[] = new byte[b.length * 2];
		int scount = 0;
		for (int i = 0; i < b.length; i++)
		{
			if ((b[i] == (byte) 0xfc) || (b[i] == (byte) 0xfd)
				|| (b[i] == (byte) 0xfe))
			{
				stuffed[i + scount] = (byte) 0xfe;
				stuffed[i + scount + 1] = (byte) (b[i] ^ 0x20);
				scount++;
			}
			else
			{
				stuffed[i + scount] = b[i];
			}
		}
		byte s2[] = new byte[b.length + scount];
		System.arraycopy(stuffed, 0, s2, 0, s2.length);
		return s2;
	}


	/**
	 * 
	 * @param crc
	 * @param c
	 * @return
	 */
	/* update crc */
	protected static int updcrc_tab(int crc, int c)
	{
		int tmp;
		tmp = (((crc >> 8) & 0xff) ^ (c & 0xff)) & 0xff;
		crc = (((crc << 8) & 0xffff) ^ crctttab[tmp]) & 0xffff;
		return crc;
	}

}
