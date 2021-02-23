package cu.rayrdguezo.cardiacs.terceros.twintrac.cs.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * measurement entity for Weight values.
 * 
 * @author hakansahin
 * @date 07.02.2013 15:52:10 hakansahin
 */
@DatabaseTable
public class MeasurementWeight implements Serializable,
        Comparable<MeasurementWeight>
{

	/*
	 * <list><de.avetana.data2.entities.MeasurementWeight><alarm>false</alarm><id
	 * >11198</id> <servermid>1</servermid><deactivated>false</deactivated>
	 * <timestamp class="sql-timestamp" timestamp="1360669857645">2013-02-12
	 * 12:50:57.645</timestamp> <update class="sql-timestamp"
	 * timestamp="1360669857645">2013-02-12 12:50:57.645</update>
	 * <timeOfMeasurement class="sql-timestamp"
	 * timestamp="1360669853154">2013-02-12 12:50:53.154</timeOfMeasurement>
	 * <mesType>SELF_MEASUREMENT</mesType><weight__si>0.0</weight__si>
	 * </de.avetana.data2.entities.MeasurementWeight></list>
	 */
	public static final String TOM_FIELD_NAME = "timeOfMeasurement";

	@DatabaseField(generatedId = true)
	private int _id;

	@DatabaseField
	private final Date timestamp = new Date();

	@DatabaseField(columnName = TOM_FIELD_NAME)
	private Date timeOfMeasurement;

	@DatabaseField(foreign = true)
	private Patient patient;

	@DatabaseField
	private boolean synced;

	@DatabaseField
	private double weight__si;

	@DatabaseField
	private String unit;

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

	@DatabaseField(persisted = false)
	private String mesType;


	public MeasurementWeight()
	{
		this.setSynced(false);
	}


	public MeasurementWeight(double weightValue, String unit, Date timeMeasure,
                             Patient patient)
	{
		this.weight__si = weightValue;
		this.patient = patient;
		this.unit = unit;
		this.timeOfMeasurement = timeMeasure;
	}


	@Override
	public int compareTo(MeasurementWeight another)
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


	public double getWeight()
	{
		return weight__si;
	}


	public boolean isSynced()
	{
		return synced;
	}


	public void set_id(int _id)
	{
		this._id = _id;
	}


	public void setPatient(Patient patient)
	{
		this.patient = patient;
	}


	public void setServerId(long serverId)
	{
		this.serverId = serverId;
	}


	public void setSync(boolean sync)
	{
		this.setSynced(sync);
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


	public void setWeight(double weight)
	{
		this.weight__si = weight;
	}

}
