package cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac;

public class TwinTracConfigManager
{
	/* Documentation index number staring with 0 */
	private final int ECG_CHANNELS_SET_ONLINE_INDEX = 19; // ECG channels begins from 19 but we need the second byte 
	private final int TEMP_CHANNELS_ONLINE_INDEX = 27;
	private final int ACCELEROMETER_CHANNELS_ONLINE_INDEX = 29;
	private final int SAMPLING_RATE_ONLINE_INDEX = 31;
	private final int HEART_RATE_INDEX = 66;

	private final int sampleRate;
	private final boolean ecg1Active;
	private final boolean ecg2Active;
	private final boolean accXActive;
	private final boolean accYActive;
	private final boolean accZActive;
	private final boolean accLActive;
	private final boolean temp1Active;
	private final boolean temp2Active;
	private final boolean temp3Active;
	private final boolean hrActive;


	public TwinTracConfigManager()
	{
		this.sampleRate = 500;
		this.ecg1Active = true;
		this.ecg2Active = true;
		this.accXActive = false;
		this.accYActive = false;
		this.accZActive = false;
		this.accLActive = false;
		this.temp1Active = true;
		this.temp2Active = true;
		this.temp3Active = true;
		this.hrActive = true;

	}


	public TwinTracConfigManager(byte[] configPayload, boolean indexMissing)
	{
		this.sampleRate = getSampleRateFromData(
			configPayload,
			indexMissing ? SAMPLING_RATE_ONLINE_INDEX - 5 : SAMPLING_RATE_ONLINE_INDEX);
		this.ecg1Active = getActiveBitFromContent(configPayload,
			ECG_CHANNELS_SET_ONLINE_INDEX, 0x01, indexMissing);
		this.ecg2Active = getActiveBitFromContent(configPayload,
			ECG_CHANNELS_SET_ONLINE_INDEX, 0x02, indexMissing);
		this.accXActive = getActiveBitFromContent(configPayload,
			ACCELEROMETER_CHANNELS_ONLINE_INDEX, 0x01, indexMissing);
		this.accYActive = getActiveBitFromContent(configPayload,
			ACCELEROMETER_CHANNELS_ONLINE_INDEX, 0x02, indexMissing);
		this.accZActive = getActiveBitFromContent(configPayload,
			ACCELEROMETER_CHANNELS_ONLINE_INDEX, 0x04, indexMissing);
		this.accLActive = getActiveBitFromContent(configPayload,
			ACCELEROMETER_CHANNELS_ONLINE_INDEX, 0x08, indexMissing);
		this.temp1Active = getActiveBitFromContent(configPayload,
			TEMP_CHANNELS_ONLINE_INDEX, 0x01, indexMissing);
		this.temp2Active = getActiveBitFromContent(configPayload,
			TEMP_CHANNELS_ONLINE_INDEX, 0x02, indexMissing);
		this.temp3Active = getActiveBitFromContent(configPayload,
			TEMP_CHANNELS_ONLINE_INDEX, 0x04, indexMissing);
		this.hrActive = getActiveBitFromContent(configPayload,
			HEART_RATE_INDEX, 0x01, indexMissing);
		System.out.println(toString());
	}


	public int getSampleRate()
	{
		return sampleRate;
	}


	public boolean isAccLActive()
	{
		return accLActive;
	}


	public boolean isAccXActive()
	{
		return accXActive;
	}


	public boolean isAccYActive()
	{
		return accYActive;
	}


	public boolean isAccZActive()
	{
		return accZActive;
	}


	public boolean isEcg1Active()
	{
		return ecg1Active;
	}


	public boolean isEcg2Active()
	{
		return ecg2Active;
	}


	public boolean isHeartRateActive()
	{
		return hrActive;
	}


	/**
	 * Temperature values always in the ecg package. ( mail 09.01.2017 13:16
	 * H.Richter)
	 * 
	 * @return
	 * @date 09.01.2017 14:07:22 Hakan Sahin
	 */
	public boolean isTemp1Active()
	{
		//return temp1Active;
		return true;
	}


	/**
	 * Temperature values always in the ecg package. ( mail 09.01.2017 13:16
	 * H.Richter)
	 * 
	 * @return
	 * @date 09.01.2017 14:07:22 Hakan Sahin
	 */
	public boolean isTemp2Active()
	{
		//return temp2Active;
		return true;
	}


	/**
	 * Temperature values always in the ecg package. ( mail 09.01.2017 13:16
	 * H.Richter)
	 * 
	 * @return
	 * @date 09.01.2017 14:07:22 Hakan Sahin
	 */
	public boolean isTemp3Active()
	{
		//return temp3Active;
		return true;
	}


	@Override
	public String toString()
	{
		return "Config done: sampleRate=" + sampleRate + ", ecg1Active="
				+ ecg1Active + ", ecg2Active=" + ecg2Active + ", accXActive="
				+ accXActive + ", accYActive=" + accYActive + ", accZActive="
				+ accZActive + ", accLActive=" + accLActive + ", temp1Active="
				+ temp1Active + ", temp2Active=" + temp2Active
				+ ", temp3Active=" + temp3Active + ", hrActive=" + hrActive;
	}


	private boolean getActiveBitFromContent(byte[] configPayload,
					int beginIndex, int bitIndex, boolean indexMissed)
	{
		int indexBegin = indexMissed ? beginIndex - 5 : beginIndex;
		byte sampleRate = (byte) (configPayload[indexBegin] & (bitIndex & 0xFF));
		return !(sampleRate == 0);
	}


	private int getSampleRateFromData(byte[] configPayload, int sampleRateIndex)
	{
		byte sampleRate = (byte) (configPayload[sampleRateIndex] & 0x0F);
		switch (sampleRate)
		{
			case 1:
			return 125;
			case 2:
			return 250;
			case 3:
			return 500;
			case 4:
			return 1000;
			case 5:
			return 2000;
			case 6:
			return 4000;
			case 7:
			return 8000;
			case 8:
			return 16000;
		}
		return 0;
	}

}
