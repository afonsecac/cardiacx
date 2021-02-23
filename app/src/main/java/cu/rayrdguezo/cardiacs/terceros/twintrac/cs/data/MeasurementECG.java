package cu.rayrdguezo.cardiacs.terceros.twintrac.cs.data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import cu.rayrdguezo.cardiacs.terceros.twintrac.cs.measurement.ECGComment;

/**
 * Measurement entity for ECG values
 * 
 * @author Hakan Sahin
 * @date 08.06.2013 09:10:35 Hakan Sahin
 */
@DatabaseTable
public class MeasurementECG implements Serializable, Comparable<MeasurementECG>
{
	private static final long serialVersionUID = -890782787191732478L;
	public static final String TOM_FIELD_NAME = "timeOfMeasurement";

	/** The primary key in sql */
	@DatabaseField(generatedId = true)
	protected int id;
	/** The time the measurement has been taken */
	@DatabaseField(columnName = TOM_FIELD_NAME)
	protected Date timeOfMeasurement;
	/** The flag of whether the measurement has been synced with server */
	@DatabaseField
	protected boolean synced;
	/** The serialNo of the device the measurement has been made with */
	@DatabaseField
	protected String serialNo;

	/** Recording ID */
	@DatabaseField
	protected int recordingID;
	/** Befund */
	@DatabaseField
	protected String findings;
	/** ECG comment array */
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	protected ArrayList<ECGComment> commentArray;
	/** Uploaded content */
	@DatabaseField
	protected long uploadOffset;

	@DatabaseField(canBeNull = false, foreign = true)
	protected Patient patient;


	public MeasurementECG()
	{
		timeOfMeasurement = new Date();
		serialNo = "";
		synced = false;
	}


	/**
	 * 
	 * param recordingID
	 *            {@link #recordingID}
	 * param findings
	 *            {@link #findings}
	 * param commentArray
	 *            {@link #commentArray}
	 * @param timeOfMeasurement
	 *            {@link #timeOfMeasurement}
	 * @param serialNo
	 *            {@link #serialNo}
	 */
	public MeasurementECG(int rID, String finds,
                          ArrayList<ECGComment> comments,
                          Date timeOfMeasurement, String serialNo)
	{
		this(timeOfMeasurement, serialNo, false);
		recordingID = rID;
		findings = finds;
		commentArray = comments;
		uploadOffset = 0;
		if (findings == null)
		{
			findings = "";
		}
	}


	/**
	 * @param timeOfMeasurement
	 *            {@link #timeOfMeasurement}
	 * @param serialNo
	 *            {@link #serialNo}
	 * @param synced
	 *            {@link #synced}
	 */
	protected MeasurementECG(Date timeOfMeasurement, String serialNo,
                             boolean synced)
	{
		this.timeOfMeasurement = timeOfMeasurement;
		this.serialNo = serialNo;
		this.synced = synced;
	}


	/**
	 * 
	 * param recordingID
	 *            {@link #recordingID}
	 * param findings
	 *            {@link #findings}
	 * param commentArray
	 *            {@link #commentArray}
	 * param timeOfMeasurement
	 *            {@link #timeOfMeasurement}
	 * param serialNo
	 *            {@link #serialNo}
	 */
	protected MeasurementECG(int rID, String finds,
                             ArrayList<ECGComment> comments, long timestamp,
                             String serialNo)
	{
		this(rID, finds, comments, new Date(timestamp), serialNo);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MeasurementECG o)
	{
		if ((o == null) || (o.getTimeOfMeasurement() == null))
		{
			return 1;
		}
		else if (getTimeOfMeasurement() == null)
		{
			return -1;
		}
		else
		{
			return (getTimeOfMeasurement()).compareTo(o.getTimeOfMeasurement());
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o2)
	{
		return ((o2 != null) && (o2 instanceof MeasurementECG) && (((MeasurementECG) o2).getId() == id));
	}


	/**
	 * @return the commentArray
	 */
	public ArrayList<ECGComment> getCommentArray()
	{
		return commentArray;
	}


	/**
	 * @return the findings
	 */
	public String getFindings()
	{
		return findings;
	}


	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}


	/**
	 * @return the patient
	 */
	public Patient getPatient()
	{
		return patient;
	}


	/**
	 * @return the recordingID
	 */
	public int getRecordingID()
	{
		return recordingID;
	}


	/**
	 * @return the serialNo
	 */
	public String getSerialNo()
	{
		return serialNo;
	}


	/**
	 * @return the timeOfMeasurement
	 */
	public Date getTimeOfMeasurement()
	{
		return timeOfMeasurement;
	}


	/**
	 * @return the uploadOffset
	 */
	public long getUploadOffset()
	{
		return uploadOffset;
	}


	/**
	 * @return the synced
	 */
	public boolean isSynced()
	{
		return synced;
	}


	/**
	 * @param commentArray
	 *            the commentArray to set
	 */
	public void setCommentArray(ArrayList<ECGComment> commentArray)
	{
		this.commentArray = commentArray;
	}


	/**
	 * @param findings
	 *            the findings to set
	 */
	public void setFindings(String findings)
	{
		this.findings = findings;
	}


	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id)
	{
		this.id = id;
	}


	/**
	 * @param patient
	 *            the patient to set
	 */
	public void setPatient(Patient patient)
	{
		this.patient = patient;
	}


	/**
	 * @param recordingID
	 *            the recordingID to set
	 */
	public void setRecordingID(int recordingID)
	{
		this.recordingID = recordingID;
	}


	/**
	 * @param serialNo
	 *            the serialNo to set
	 */
	public void setSerialNo(String serialNo)
	{
		this.serialNo = serialNo;
	}


	/**
	 * @param synced
	 *            the synced to set
	 */
	public void setSynced(boolean synced)
	{
		this.synced = synced;
	}


	/**
	 * @param timeOfMeasurement
	 *            the timeOfMeasurement to set
	 */
	public void setTimeOfMeasurement(Date timeOfMeasurement)
	{
		this.timeOfMeasurement = timeOfMeasurement;
	}


	/**
	 * @param uploadOffset
	 *            the uploadOffset to set
	 */
	public void setUploadOffset(long uploadOffset)
	{
		this.uploadOffset = uploadOffset;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return String.format("MeasurementECG (%s) (%s) [@%tc from %s]\n%s",
			findings, commentArray.toString(), getTimeOfMeasurement(),
			getSerialNo(), patient.toString());
	}
}
