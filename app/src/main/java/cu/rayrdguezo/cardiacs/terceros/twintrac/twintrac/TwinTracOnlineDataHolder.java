package cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac;

public class TwinTracOnlineDataHolder
{
	private final int temp1;
	private final int temp2;
	private final int temp3;
	private final int hr;
	private final int batteryCapacityInPercent;
	private final int remainingTime;
	private final int[] ecgValuesInt;
	private final double accAverage;


	public TwinTracOnlineDataHolder(int temp1, int temp2, int temp3, int hr,
                                    int batteryCapacityInPercent,
                                    int remainingTime, int[] ecgValuesInt,
                                    double accAverage)
	{
		this.temp1 = temp1;
		this.temp2 = temp2;
		this.temp3 = temp3;
		this.hr = hr;
		this.batteryCapacityInPercent = batteryCapacityInPercent;
		this.remainingTime = remainingTime;
		this.ecgValuesInt = ecgValuesInt;
		this.accAverage = accAverage;
	}


	public double getAccAverage()
	{
		return accAverage;
	}


	public int getBatteryCapacityInPercent()
	{
		return batteryCapacityInPercent;
	}


	public int[] getEcgValuesInt()
	{
		return ecgValuesInt;
	}


	public int getHr()
	{
		return hr;
	}


	public int getRemainingTime()
	{
		return remainingTime;
	}


	public int getTemp1()
	{
		return temp1;
	}


	public int getTemp2()
	{
		return temp2;
	}


	public int getTemp3()
	{
		return temp3;
	}

}
