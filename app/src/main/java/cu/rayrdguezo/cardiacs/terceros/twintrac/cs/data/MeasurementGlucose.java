package cu.rayrdguezo.cardiacs.terceros.twintrac.cs.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Measurement entity for glucose values.
 * 
 * @author hakansahin
 * @date 07.02.2013 15:32:45 hakansahin
 */
@DatabaseTable
public class MeasurementGlucose implements Serializable,
        Comparable<MeasurementGlucose>
{
	public static final String TOM_FIELD_NAME = "timeOfMeasurement";
	public static final String post = "Postprandial";
	public static final String pre = "Präprandial";
	public final static String BEFORE = "BEFORE";
	public final static String AFTER = "AFTER";

	@DatabaseField(generatedId = true)
	private int _id;

	@DatabaseField
	private Date timestamp;

	@DatabaseField(columnName = TOM_FIELD_NAME)
	private Date timeOfMeasurement;

	@DatabaseField(foreign = true)
	private Patient patient;

	@DatabaseField
	private boolean synced;

	@DatabaseField
	private double gluco__si;

	@DatabaseField
	private String unit;

	@DatabaseField
	private String meal__flag;

	@DatabaseField
	private long serverId;

	@DatabaseField(persisted = false)
	private boolean alarm;

	@DatabaseField(persisted = false)
	private long servermid;

	@DatabaseField(persisted = false)
	private boolean deactivated;

	@DatabaseField(persisted = false)
	private Date update;


	public MeasurementGlucose()
	{
		this.synced = false;
	}


	public MeasurementGlucose(double value, String unit, String prePro,
                              Date timeOfMea, Patient patient)
	{
		this.gluco__si = value;
		this.unit = unit;
		if (prePro.equals(post))
		{
			this.meal__flag = AFTER;
		}
		else
		{
			this.meal__flag = BEFORE;
		}
		this.timeOfMeasurement = timeOfMea;
		this.patient = patient;
		this.synced = false;
		this.timestamp = new Date();
	}


	@Override
	public int compareTo(MeasurementGlucose another)
	{
		if ((another == null) || (another.getTimestamp() == null))
		{
			return 1;
		}
		else if (getTimestamp() == null)
		{
			return -1;
		}
		else
		{
			return (getTimestamp()).compareTo(another.getTimestamp());
		}
	}


	public int get_id()
	{
		return _id;
	}


	public String getMeal()
	{
		return meal__flag;
	}


	public Patient getPatient()
	{
		return patient;
	}


	public long getServerId()
	{
		return serverId;
	}


	public Date getTimeOfMeasurement()
	{
		return timeOfMeasurement;
	}


	public Date getTimestamp()
	{
		return timestamp;
	}


	public String getUnit()
	{
		return unit;
	}


	public double getValue()
	{
		return gluco__si;
	}


	public boolean isSynced()
	{
		return synced;
	}


	public void set_id(int _id)
	{
		this._id = _id;
	}


	public void setMeal(String preProString)
	{
		this.meal__flag = preProString;
	}


	public void setPatient(Patient patient)
	{
		this.patient = patient;
	}


	public void setServerId(long serverId)
	{
		this.serverId = serverId;
	}


	public void setSynced(boolean synced)
	{
		this.synced = synced;
	}


	public void setTimeOfMeasurement(Date timeOfMeasurement)
	{
		this.timeOfMeasurement = timeOfMeasurement;
	}


	public void setUnit(String unit)
	{
		this.unit = unit;
	}


	public void setValue(double value)
	{
		this.gluco__si = value;
	}

}
