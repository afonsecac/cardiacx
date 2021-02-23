package cu.rayrdguezo.cardiacs.terceros.twintrac.cs.data;

import android.content.res.Resources;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

import cu.rayrdguezo.cardiacs.R;

/**
 * Patient entity to save patient information within the DB.
 * 
 * @author Hakan Sahin
 * @date 08.06.2013 09:11:35 Hakan Sahin
 */
@DatabaseTable
public class Patient implements Serializable, Comparable<Patient>
{
	/**
	 * Enumerates the normal blood types
	 * 
	 * @author mcapper
	 * 
	 */
	public enum Bloodtype
	{
		AP, AN, AU, BP, BN, BU, ABP, ABN, ABU, OP, ON, OU, U_P, U_N, UNKNOWN;
	}

	public enum Maritalstatus
	{
		LEDIG, VERHEIRATET, EHEAEHNLICH, GESCHIEDEN, VERWITWET, UNKNOWN;
	};

	public enum PMStatus
	{
		None, Unknown, Atrial, Ventric, Bi;
	}

	public enum Raucher
	{
		NO, LITTLE, MEDIUM, MUCH, UNKNOWN, YES, FORMERSMOKER;
	}

	public enum Sex
	{
		Male, Female;

		/**
		 * 
		 * @return The address form of the sex like "Herr", "Mister" etc.
		 */
		public String getAddressForm()
		{
			final Resources r = Resources.getSystem();
			if (this == Male)
			{
				return r.getString(R.string.herr);
			}
			else if (this == Female)
			{
				return r.getString(R.string.frau);
			}
			else
			{
				return r.getString(R.string.depp);
			}
		}
	}

	public enum Status
	{
		ACTIVE, ABSENT, HOSPITAL, INACTIVE, ENDED;
	}

	private static final long serialVersionUID = -1389509702644999753L;

	@DatabaseField
	protected String accessToken;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	protected Address address = new Address(Address.AddressType.PATIENT);
	@DatabaseField(generatedId = true)
	protected int id;
	@DatabaseField
	protected double size_si;
	@DatabaseField
	protected double weight_si;
	@DatabaseField
	protected Sex sex;
	@DatabaseField
	protected Date birthdate;
	@DatabaseField(unique = true)
	protected String patientID;
	@DatabaseField(unique = true)
	protected Long serverPID;
	@DatabaseField
	protected Raucher raucher;
	@DatabaseField
	protected Bloodtype bloodtype;
	@DatabaseField
	protected PMStatus pmStatus = PMStatus.None;
	@DatabaseField
	protected Maritalstatus maritalstatus = Maritalstatus.LEDIG;
	@DatabaseField
	protected Status status = Status.ACTIVE;

	//define a collection like a list. This is ORMLite definition
	@ForeignCollectionField(eager = false)
	protected ForeignCollection<MeasurementECG> measurementECGs;
	@ForeignCollectionField
	protected ForeignCollection<MeasurementBP> measurementBPs;
	@ForeignCollectionField
	protected ForeignCollection<MeasurementWeight> measurementWeight;
	@ForeignCollectionField
	protected ForeignCollection<MeasurementGlucose> measurementGlucose;


	@Override
	public int compareTo(Patient o)
	{
		if ((o.getAddress() == null) && (getAddress() == null))
		{
			return id < o.id ? -1 : (id == o.id ? 0 : 1);
		}
		if (o.getAddress() == null)
		{
			return +1;
		}
		if (getAddress() == null)
		{
			return -1;
		}
		return getAddress().compareTo(o.getAddress());
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o2)
	{
		return ((o2 != null) && (o2 instanceof Patient) && (((Patient) o2).getPatientID().equals(patientID)));
	}


	/**
	 * @return the accessToken
	 */
	public String getAccessToken()
	{
		return accessToken;
	}


	/**
	 * @return the address
	 */
	public Address getAddress()
	{
		return address;
	}


	/**
	 * @return the birthdate
	 */
	public Date getBirthdate()
	{
		return birthdate;
	}


	/**
	 * @return the bloodtype
	 */
	public Bloodtype getBloodtype()
	{
		return bloodtype;
	}


	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}


	/**
	 * @return the maritalstatus
	 */
	public Maritalstatus getMaritalstatus()
	{
		return maritalstatus;
	}


	/**
	 * @return the measurementBPs
	 */
	public ForeignCollection<MeasurementBP> getMeasurementBPs()
	{
		return measurementBPs;
	}


	/**
	 * @return the measurementECGs
	 */
	public ForeignCollection<MeasurementECG> getMeasurementECGs()
	{
		return measurementECGs;
	}


	/**
	 * @return the measurementGlucose
	 */
	public ForeignCollection<MeasurementGlucose> getMeasurementGlucose()
	{
		return measurementGlucose;
	}


	/**
	 * @return the measurementWeight
	 */
	public ForeignCollection<MeasurementWeight> getMeasurementWeight()
	{
		return measurementWeight;
	}


	/**
	 * @return the patientID
	 */
	public String getPatientID()
	{
		return patientID;
	}


	/**
	 * @return the pmStatus
	 */
	public PMStatus getPmStatus()
	{
		return pmStatus;
	}


	/**
	 * @return the raucher
	 */
	public Raucher getRaucher()
	{
		return raucher;
	}


	/**
	 * @return the serverPID
	 */
	public Long getServerPID()
	{
		return serverPID;
	}


	/**
	 * @return the sex
	 */
	public Sex getSex()
	{
		return sex;
	}


	/**
	 * @return the size_si
	 */
	public double getSize_si()
	{
		return size_si;
	}


	/**
	 * @return the status
	 */
	public Status getStatus()
	{
		return status;
	}


	/**
	 * @return the weight_si
	 */
	public double getWeight_si()
	{
		return weight_si;
	}


	/**
	 * @param accessToken
	 *            the accessToken to set
	 */
	public void setAccessToken(String accessToken)
	{
		this.accessToken = accessToken;
	}


	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(Address address)
	{
		this.address = address;
	}


	/**
	 * @param birthdate
	 *            the birthdate to set
	 */
	public void setBirthdate(Date birthdate)
	{
		this.birthdate = birthdate;
	}


	/**
	 * @param bloodtype
	 *            the bloodtype to set
	 */
	public void setBloodtype(Bloodtype bloodtype)
	{
		this.bloodtype = bloodtype;
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
	 * @param maritalstatus
	 *            the maritalstatus to set
	 */
	public void setMaritalstatus(Maritalstatus maritalstatus)
	{
		this.maritalstatus = maritalstatus;
	}


	/**
	 * @param measurementBPs
	 *            the measurementBPs to set
	 */
	public void setMeasurementBPs(
					ForeignCollection<MeasurementBP> measurementBPs)
	{
		this.measurementBPs = measurementBPs;
	}


	/**
	 * @param measurementECGs
	 *            the measurementECGs to set
	 */
	public void setMeasurementECGs(
					ForeignCollection<MeasurementECG> measurementECGs)
	{
		this.measurementECGs = measurementECGs;
	}


	/**
	 * param measurementWeight
	 *            the measurementWeight to set
	 */
	public void setMeasurementGlucose(
					ForeignCollection<MeasurementGlucose> measurementGlucose)
	{
		this.measurementGlucose = measurementGlucose;
	}


	/**
	 * @param measurementWeight
	 *            the measurementWeight to set
	 */
	public void setMeasurementWeight(
					ForeignCollection<MeasurementWeight> measurementWeight)
	{
		this.measurementWeight = measurementWeight;
	}


	/**
	 * @param patientID
	 *            the patientID to set
	 */
	public void setPatientID(String patientID)
	{
		this.patientID = patientID;
	}


	/**
	 * @param pmStatus
	 *            the pmStatus to set
	 */
	public void setPmStatus(PMStatus pmStatus)
	{
		this.pmStatus = pmStatus;
	}


	/**
	 * @param raucher
	 *            the raucher to set
	 */
	public void setRaucher(Raucher raucher)
	{
		this.raucher = raucher;
	}


	/**
	 * @param serverPID
	 *            the serverPID to set
	 */
	public void setServerPID(Long serverPID)
	{
		this.serverPID = serverPID;
	}


	/**
	 * @param sex
	 *            the sex to set
	 */
	public void setSex(Sex sex)
	{
		this.sex = sex;
	}


	/**
	 * @param size_si
	 *            the size_si to set
	 */
	public void setSize_si(double size_si)
	{
		this.size_si = size_si;
	}


	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Status status)
	{
		this.status = status;
	}


	/**
	 * @param weight_si
	 *            the weight_si to set
	 */
	public void setWeight_si(double weight_si)
	{
		this.weight_si = weight_si;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Patient [accessToken=" + accessToken + ", address=" + address
				+ ", id=" + id + ", size_si=" + size_si + ", weight_si="
				+ weight_si + ", sex=" + sex + ", birthdate=" + birthdate
				+ ", patientID=" + patientID + ", serverPID=" + serverPID
				+ ", raucher=" + raucher + ", bloodtype=" + bloodtype
				+ ", pmStatus=" + pmStatus + ", maritalstatus=" + maritalstatus
				+ ", status=" + status + ", measurementECGs=" + measurementECGs
				+ ", measurementBP=" + measurementBPs + ", measurementGlucose="
				+ measurementGlucose + ", measurementWeigt="
				+ measurementWeight + "]";
	}

}
