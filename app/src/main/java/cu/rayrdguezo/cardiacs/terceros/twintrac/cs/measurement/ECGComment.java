package cu.rayrdguezo.cardiacs.terceros.twintrac.cs.measurement;

import java.io.Serializable;
import java.util.Date;

public class ECGComment implements Serializable
{
	private static final long serialVersionUID = -59184307l;

	private String text;
	private Date timestamp;


	public ECGComment(String text, Date timestamp)
	{
		this.text = text;
		this.timestamp = timestamp;
	}


	public String getText()
	{
		return text;
	}


	public Date getTimestamp()
	{
		return timestamp;
	}


	public void setText(String text)
	{
		this.text = text;
	}


	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return String.format("%s @%tc", text, timestamp);
	}
}
