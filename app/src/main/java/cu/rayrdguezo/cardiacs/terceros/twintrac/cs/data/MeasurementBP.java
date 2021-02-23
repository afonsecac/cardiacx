package cu.rayrdguezo.cardiacs.terceros.twintrac.cs.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Measurement entity for BP
 * 
 * @author hakansahin
 * @date 13.02.2013 16:40:20 hakansahin
 */

@DatabaseTable
public class MeasurementBP implements Serializable, Comparable<MeasurementBP>
{
	public static final String TOM_FIELD_NAME = "timeOfMeasurement";

	/*
	 * <list><de.avetana.data2.entities.MeasurementBP> <alarm>false</alarm>
	 * <id>10855</id> <kommentar>manuelle Eingabe</kommentar>
	 * <deactivated>false</deactivated> <timestamp class="sql-timestamp"
	 * timestamp="1360574812036">2013-02-11 10:26:52.036</timestamp> <update
	 * class="sql-timestamp" timestamp="1360574812036">2013-02-11
	 * 10:26:52.036</update> <timeOfMeasurement class="sql-timestamp"
	 * timestamp="1360574760000">2013-02-11 10:26:00.000</timeOfMeasurement>
	 * <mesType
	 * >DOCTOR_MEASUREMENT</mesType><arrhythmiaFlag>false</arrhythmiaFlag>
	 * <code>
	 * 0</code><hr>60</hr><nibpDias>130</nibpDias><nibpMAD>0</nibpMAD><nibpSys
	 * >160</nibpSys>
	 * <seqNum>0</seqNum><voltage>0.0</voltage></de.avetana.data2.
	 * entities.MeasurementBP></list>
	 */

	//@XStreamAlias("id")
	@DatabaseField(generatedId = true)
	private int _id;

	@DatabaseField
	private Date timestamp;

	@DatabaseField(columnName = TOM_FIELD_NAME)
	private Date timeOfMeasurement;

	@DatabaseField
	private String kommentar;

	@DatabaseField
	private int nibpSys;

	@DatabaseField
	private int nibpDias;

	@DatabaseField
	private int hr;

	@DatabaseField
	private int nibpMAD;

	//this is like a pointer. It shows to patients database
	@DatabaseField(foreign = true)
	private Patient patientID;

	@DatabaseField
	private boolean synced;

	//this parameters was need for XStream
	@DatabaseField(persisted = false)
	private boolean alarm;

	@DatabaseField(persisted = false)
	private boolean deactivated;

	@DatabaseField
	private Date update;

	@DatabaseField(persisted = false)
	private String mesType;

	@DatabaseField(persisted = false)
	private boolean arrhythmiaFlag;

	@DatabaseField(persisted = false)
	private int seqNum;

	@DatabaseField(persisted = false)
	private int code;

	@DatabaseField(persisted = false)
	private double voltage;

	@DatabaseField
	private long id_server;

	@DatabaseField(persisted = false)
	private long servermid;


	//this constructor was need by ormlite
	public MeasurementBP()
	{
		synced = false;
	}


	public MeasurementBP(int nibpSys, int nibpDia, int hr, Date timeOfM,
							Patient patient)
	{
		this.nibpSys = nibpSys;
		this.nibpDias = nibpDia;
		this.hr = hr;
		this.timeOfMeasurement = timeOfM;
		this.patientID = patient;
		synced = false;
		this.timestamp = new Date();
	}


	@Override
	public int compareTo(MeasurementBP another)
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


	public int getHr()
	{
		return hr;
	}


	public String getKommentar()
	{
		return kommentar;
	}


	public int getNibpDias()
	{
		return nibpDias;
	}


	public int getNibpMAD()
	{
		return nibpMAD;
	}


	public int getNibpSys()
	{
		return nibpSys;
	}


	public Patient getPatient()
	{
		return patientID;
	}


	public long getServerId()
	{
		return id_server;
	}


	public Date getTimeOfMeasurement()
	{
		return timeOfMeasurement;
	}


	public Date getTimestamp()
	{
		return timestamp;
	}


	public boolean isSynced()
	{
		return synced;
	}


	public void set_id(int _id)
	{
		this._id = _id;
	}


	public void setHr(int hr)
	{
		this.hr = hr;
	}


	public void setKommentar(String kommentar)
	{
		this.kommentar = kommentar;
	}


	public void setNibpDias(int nibpDias)
	{
		this.nibpDias = nibpDias;
	}


	public void setNibpMAD(int nibpMAD)
	{
		this.nibpMAD = nibpMAD;
	}


	public void setNibpSys(int nibpSys)
	{
		this.nibpSys = nibpSys;
	}


	public void setPatient(Patient patientID)
	{
		this.patientID = patientID;
	}


	public void setServerId(long id)
	{
		this.id_server = id;
	}


	public void setSync(boolean sync)
	{
		this.synced = sync;
	}


	public void setTimeOfMeasurement(Date timeOfMeasurement)
	{
		this.timeOfMeasurement = timeOfMeasurement;
	}

}
