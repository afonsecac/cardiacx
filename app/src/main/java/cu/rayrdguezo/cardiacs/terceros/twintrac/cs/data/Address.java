package cu.rayrdguezo.cardiacs.terceros.twintrac.cs.data;

import java.io.Serializable;
import java.util.Locale;
import java.util.TimeZone;

public class Address implements Serializable, Comparable<Address>
{

	public enum AddressType
	{
		CUSTOM, ACCOUNT, LOGIN, PATIENT, KRANKENHAUS, KRANKENVERSICHERUNG,
		CONTACT, MEETING, ARZT
	}

	public enum Country
	{
		Afghanistan, Åland_Islands, Albania, Algeria, American_Samoa, Andorra,
		Angola, Anguilla, Antarctica, Antigua_and_Barbuda, Argentina, Armenia,
		Aruba, Australia, Austria, Azerbaijan, Bahamas, Bahrain, Bangladesh,
		Barbados, Belarus, Belgium, Belize, Benin, Bermuda, Bhutan, Bolivia,
		Bosnia_and_Herzegovina, Botswana, Bouvet_Island, Brazil,
		Brunei_Darussalam, Bulgaria, Burkina_Faso, Burundi, Cambodia, Cameroon,
		Canada, Cape_Verde, Cayman_Islands, Central_African_Republic, Chad,
		Chile, China, Christmas_Island, Cocos_Islands, Colombia, Comoros,
		Congo, Cook_Islands, Costa_Rica, Côte_D_Ivoire, Croatia, Cuba, Cyprus,
		Czech_Republic, Denmark, Djibouti, Dominica, Dominican_Republic,
		Ecuador, Egypt, El_Salvador, Equatorial_Guinea, Eritrea, Estonia,
		Ethiopia, Falkland_Islands, Faroe_Islands, Fiji, Finland, France,
		French_Guiana, French_Polynesia, Gabon, Gambia, Georgia, Germany,
		Ghana, Gibraltar, Greece, Greenland, Grenada, Guadeloupe, Guam,
		Guatemala, Guernsey, Guinea, Guinea_Bissau, Guyana, Haiti,
		Heard_Island, Honduras, Hong_Kong, Hungary, Iceland, India, Indonesia,
		Iran, Iraq, Ireland, Isle_of_Man, Israel, Italy, Jamaica, Japan,
		Jersey, Jordan, Kazakhstan, Kenya, Kiribati, Korea, Kuwait, Kyrgyzstan,
		Lao, Latvia, Lebanon, Lesotho, Liberia, Libya, Liechtenstein,
		Lithuania, Luxembourg, Macao, Macedonia, Madagascar, Malawi, Malaysia,
		Maldives, Mali, Malta, Marshall_Islands, Martinique, Mauritania,
		Mauritius, Mayotte, Mexico, Micronesia, Moldova, Monaco, Mongolia,
		Montenegro, Montserrat, Morocco, Mozambique, Myanmar, Namibia, Nauru,
		Nepal, Netherlands, Netherlands_Antilles, New_Caledonia, New_Zealand,
		Nicaragua, Niger, Nigeria, Niue, Norfolk_Island, Norway, Oman,
		Pakistan, Palau, Palestinian_Territory, Panama, Papua_New_Guinea,
		Paraguay, Peru, Philippines, Pitcairn, Poland, Portugal, Puerto_Rico,
		Qatar, Réunion, Romania, Russian_Federation, Rwanda, Saint_Helena,
		Saint_Kitts_and_Nevis, Saint_Lucia, Saint_Pierre_and_Miquelon,
		Saint_Vincent_Grenadines, Samoa, San_Marino, Sao_Tome_and_Principe,
		Saudi_Arabia, Senegal, Serbia, Seychelles, Sierra_Leone, Singapore,
		Slovakia, Slovenia, Solomon_Islands, Somalia, South_Africa,
		South_Georgia, Spain, Sri_Lanka, Sudan, Suriname, Svalbard, Swaziland,
		Sweden, Switzerland, Syrian, Taiwan, Tajikistan, Tanzania, Thailand,
		Timor_Leste, Togo, Tokelau, Tonga, Trinidad_and_Tobago, Tunisia,
		Turkey, Turkmenistan, Turks_Islands, Tuvalu, Uganda, Ukraine,
		United_Arab_Emirates, United_Kingdom, United_States, Uruguay,
		Uzbekistan, Vatican_City_State, Vanuatu, Venezuela, Vietnam,
		Virgin_Islands, Wallis_and_Futuna, Western_Sahara, Yemen, Zambia,
		Zimbabwe;

		@Override
		public String toString()
		{
			return name().replaceAll("_", " ");
		}
	}

	/**
	 * Form of address for simple selection of male/female form. The strange
	 * constant names are chosen for compatibility reasons, the corresponding
	 * translation property keys are already used this way...
	 * 
	 * @author stengel
	 * @date 20.06.2011
	 * 
	 */
	public enum FormOfAddress
	{
		/** Mrs/Ms/Fr etc */
		FEMALEFORMOFADDRESS,
		/** Mr/Hr */
		MALEFORMOFADDRESS
	}

	private static final long serialVersionUID = -2399760178154103229L;
	protected long id;
	protected AddressType type = AddressType.PATIENT;
	protected String formofaddress;
	protected String firstname;
	protected String lastname;
	protected String street;
	protected String po;
	protected String city;
	protected Country country;
	protected String phone;
	protected String telefax;
	protected String mobilephone;
	protected String email;
	protected String region;
	protected String misc;
	protected Locale locale = Locale.getDefault();
	protected Long creatorID;
	protected String timezone;
	protected FormOfAddress formOfAddressSelectable;


	public Address()
	{
	}


	public Address(AddressType type)
	{
		this.type = type;
	}


	public void clone(Address a)
	{
		city = a.getCity();
		country = a.getCountry();
		creatorID = a.getCreatorID();
		email = a.getEmail();
		firstname = a.getFirstname();
		formofaddress = a.getFormofaddress();
		lastname = a.getLastname();
		locale = a.getLocale();
		misc = a.getMisc();
		mobilephone = a.getMobilephone();
		phone = a.getPhone();
		po = a.getPo();
		region = a.getRegion();
		street = a.getStreet();
		telefax = a.getTelefax();
	}


	@Override
	public int compareTo(Address o)
	{
		if (o == null)
		{
			return +1;
		}
		String mylastname = getLastname();
		String myfirstname = getFirstname();
		String myname = (mylastname != null ? mylastname : "") + " "
						+ (myfirstname != null ? myfirstname : "");
		myname = myname.toLowerCase().trim();
		String olastname = o.getLastname();
		String ofirstname = o.getFirstname();
		String oname = (olastname != null ? olastname : "") + " "
						+ (ofirstname != null ? ofirstname : "");
		oname = oname.toLowerCase().trim();
		return myname.compareTo(oname);
	}


	public boolean equals(Address a)
	{
		return ((a != null) && (a.getId() == getId()));
	}


	/**
	 * @return the city
	 */
	public String getCity()
	{
		return city;
	}


	/**
	 * @return the country
	 */
	public Country getCountry()
	{
		return country;
	}


	/**
	 * @return the creatorID
	 */
	public Long getCreatorID()
	{
		return creatorID == null ? 0 : creatorID;
	}


	/**
	 * @return the email
	 */
	public String getEmail()
	{
		return email;
	}


	/**
	 * @return the firstname
	 */
	public String getFirstname()
	{
		return firstname;
	}


	/**
	 * @return the formofaddress
	 */
	public String getFormofaddress()
	{
		return formofaddress;
	}


	/**
	 * @return the formOfAddressSelectable
	 */
	public FormOfAddress getFormOfAddressSelectable()
	{
		return formOfAddressSelectable;
	}


	/**
	 * @return the id
	 */
	public long getId()
	{
		return id;
	}


	/**
	 * @return the lastname
	 */
	public String getLastname()
	{
		return lastname;
	}


	/**
	 * @return the locale
	 */
	public Locale getLocale()
	{
		return locale;
	}


	/**
	 * @return the misc
	 */
	public String getMisc()
	{
		return misc;
	}


	/**
	 * @return the mobilephone
	 */
	public String getMobilephone()
	{
		return mobilephone;
	}


	public String getName()
	{
		String ret = "-----";
		if (firstname != null)
		{
			ret = firstname;
		}
		if (lastname != null)
		{
			ret += " " + lastname;
		}
		if (ret.length() > 60)
		{
			ret = ret.substring(0, 60) + "...";
		}
		return ret;
	}


	/**
	 * @return the phone
	 */
	public String getPhone()
	{
		return phone;
	}


	/**
	 * @return the po
	 */
	public String getPo()
	{
		return po;
	}


	/**
	 * @return the region
	 */
	public String getRegion()
	{
		return region;
	}


	/**
	 * @return the street
	 */
	public String getStreet()
	{
		return street;
	}


	/**
	 * @return the telefax
	 */
	public String getTelefax()
	{
		return telefax;
	}


	/**
	 * @return the timezone
	 */
	public TimeZone getTimezone()
	{
		try
		{
			return TimeZone.getTimeZone(timezone);
		}
		catch (NullPointerException e)
		{
			return null;
		}
	}


	/**
	 * @return the type
	 */
	public AddressType getType()
	{
		if (type == null)
		{
			return AddressType.PATIENT;
		}
		return type;
	}


	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city)
	{
		this.city = city;
	}


	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(Country country)
	{
		this.country = country;
	}


	/**
	 * @param creatorID
	 *            the creatorID to set
	 */
	public void setCreatorID(Long creatorID)
	{
		this.creatorID = creatorID;
	}


	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}


	/**
	 * @param firstname
	 *            the firstname to set
	 */
	public void setFirstname(String firstname)
	{
		this.firstname = firstname;
	}


	/**
	 * @param formofaddress
	 *            the formofaddress to set
	 */
	public void setFormofaddress(String formofaddress)
	{
		this.formofaddress = formofaddress;
	}


	/**
	 * @param formOfAddressSelectable
	 *            the formOfAddressSelectable to set
	 */
	public void setFormOfAddressSelectable(FormOfAddress formOfAddressSelectable)
	{
		this.formOfAddressSelectable = formOfAddressSelectable;
	}


	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id)
	{
		this.id = id;
	}


	/**
	 * @param lastname
	 *            the lastname to set
	 */
	public void setLastname(String lastname)
	{
		this.lastname = lastname;
	}


	/**
	 * @param locale
	 *            the locale to set
	 */
	public void setLocale(Locale locale)
	{
		this.locale = locale;
	}


	/**
	 * @param misc
	 *            the misc to set
	 */
	public void setMisc(String misc)
	{
		this.misc = misc;
	}


	/**
	 * @param mobilephone
	 *            the mobilephone to set
	 */
	public void setMobilephone(String mobilephone)
	{
		this.mobilephone = mobilephone;
	}


	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(String phone)
	{
		this.phone = phone;
	}


	/**
	 * @param po
	 *            the po to set
	 */
	public void setPo(String po)
	{
		this.po = po;
	}


	/**
	 * @param region
	 *            the region to set
	 */
	public void setRegion(String region)
	{
		this.region = region;
	}


	/**
	 * @param street
	 *            the street to set
	 */
	public void setStreet(String street)
	{
		this.street = street;
	}


	/**
	 * @param telefax
	 *            the telefax to set
	 */
	public void setTelefax(String telefax)
	{
		this.telefax = telefax;
	}


	/**
	 * @param timezone
	 *            the timezone to set
	 */
	public void setTimezone(TimeZone timezone)
	{
		if (timezone != null)
		{
			this.timezone = timezone.getID();
		}
		else
		{
			this.timezone = null;
		}
	}


	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(AddressType type)
	{
		this.type = type;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Address [id=" + id + ", type=" + type + ", formofaddress="
				+ formofaddress + ", firstname=" + firstname + ", lastname="
				+ lastname + ", street=" + street + ", po=" + po + ", city="
				+ city + ", country=" + country + ", phone=" + phone
				+ ", telefax=" + telefax + ", mobilephone=" + mobilephone
				+ ", email=" + email + ", region=" + region + ", misc=" + misc
				+ ", locale=" + locale + ", creatorID=" + creatorID
				+ ", timezone=" + timezone + ", formOfAddressSelectable="
				+ formOfAddressSelectable + "]";
	}

}
