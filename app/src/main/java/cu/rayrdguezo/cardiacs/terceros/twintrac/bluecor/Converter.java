package cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor;

/**
 * <p>
 * Title: BlueCor
 * </p>
 * <p>
 * Description: Corscience Bluetooth Protokolldaten Konverter
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: avetana GmbH
 * </p>
 *
 * @author Moritz Gmelin
 * @version 1.0
 *
 *          Der Converter verwandelt einen Stream von Bytes in Objekte des typs
 *          Data. Hierzu wird der Pakettyp herangezogen und ueber ein Mapping
 *          Schema in DataCreator zum richtigen Typ verwandelt. Die generierten
 *          Data objekte werden dann an alle registrierten BCTargets
 *          weitergemeldet. Zum genauen Aufbau der Pakete, siehe Corscience
 *          Paket-Beschreibung.
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data.Data;
import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data.DataCreator;

public class Converter
{
	private static final int DEFAULT_VERSION = 3;
	protected static final boolean DEBUG = true;
	/** Neu angekommene Daten werden zu allen registrierten Listenern versandt */
	protected BCTarget dataListeners[] = null;

	/** Hiervon werden die Roh-Daten gelesen */
	protected InputStream rawIn;

	/** Hierin werden die Roh-Daten geschrieben */
	protected OutputStream rawOut;

	/** Solange das auf true steht, wird gelesen */
	protected boolean keepReading;

	/** Counter fuer gesendete daten */
	private int sCounter = 0;

	/** Zahl der Listener, die Daten empfangen moechten */
	protected int dlc = 0;

	protected PrintStream logStream = DEBUG ? System.out : null;

	/**
	 * Wenn das auf true steht, dann werden bytes einzelnd gelesen (ist bei
	 * manchen Nokia Handies noetig)
	 */
	private boolean singleByteMode = false;

	/** Fuer den Send and Wait for Zyklus */
	protected boolean wantsAnswer = false;
	protected Data answerData = null;

	/** Protokoll-Version des angebundenen Geraetes */
	protected int version = DEFAULT_VERSION;

	/** Objekt, welches aus den Rohdaten Objekte vom Typ Data erzeugt. */
	protected DataCreator dc;

	/** Counter √ºber die Gesamtzahl der gelesenen Daten */
	protected int totalRead;

	/** COunter ueber Gesamtahl geschriebener Daten */
	private int totalWritten;

	/**
	 * Performs a clean-up at the end of the thread performing
	 * {@link #doReading()}
	 */
	private ConverterThreadCleanupCallback cleanupCallback;


	/**
	 *
	 * Initialisiert einen Coverter unter Angabe einers Input und Output stream.
	 *
	 * @param rawIn
	 *            von hier werden Daten gelesen
	 * @param rawOut
	 *            hierin werden sie geschrieben.
	 */

	public Converter(InputStream rawIn, OutputStream rawOut)
	{
		this.rawIn = rawIn;
		this.rawOut = rawOut;
		version = 1;
		totalWritten = 0;
		dc = new DataCreator();
	}


	/**
	 * Initialisiert einen Coverter unter Angabe einers Input und Output stream.
	 *
	 * @param rawIn
	 *            von hier werden Daten gelesen
	 * @param rawOut
	 *            hierin werden sie geschrieben.
	 * @param dc
	 *            diese Objekt wird verwendet um Data-Objekte zu generieren.
	 */
	public Converter(InputStream rawIn, OutputStream rawOut, DataCreator dc)
	{
		this.rawIn = rawIn;
		this.rawOut = rawOut;
		version = 1;
		this.dc = dc;
		totalWritten = 0;
	}


	/**
	 * Hinzuf√ºgen eines neuen Listeners.
	 *
	 * @param bct
	 */

	public void addBCTarget(BCTarget bct)
	{
		if (dataListeners == null)
		{
			dataListeners = new BCTarget[3];
		}
		if (dlc >= dataListeners.length)
		{
			dlc = 0;
		}
		dataListeners[dlc++] = bct;
		if (bct == null)
		{
			dlc = 0;
		}
	}


	public void doLogging(PrintStream ps)
	{
		logStream = ps;
	}


	/**
	 *
	 * @return The protocol version of this communication.
	 */
	public int getCurrentVersion()
	{
		return version;
	}


	public InputStream getInputStream()
	{
		return rawIn;
	}


	public OutputStream getOutputStream()
	{
		return rawOut;
	}


	public int getSCounter()
	{
		return sCounter;
	}


	/**
	 * @return Returns the totalRead.
	 */
	public int getTotalRead()
	{
		return totalRead;
	}


	/**
	 * @return Returns the totalWritten.
	 */
	public int getTotalWritten()
	{
		return totalWritten;
	}


	// public static void main(String[] args)
	// {
	// Converter converter1 = new Converter(null, null);
	// }

	/**
	 * @return Returns the keepReading.
	 */
	public boolean isKeepReading()
	{
		return keepReading;
	}


	/**
	 * @return Returns the singleByteMode.
	 */
	public boolean isSingleByteMode()
	{
		return singleByteMode;
	}


	public void resetCounter()
	{
		sCounter = 0;
	}


	/**
	 *
	 * Send a data object to the conencted device and wait for an answer object.
	 *
	 * @param send
	 *            data to send
	 * @param timeout
	 *            number of milliseconds to wait for the answer
	 * @return data object sent as answer
	 * @throws IOException
	 *             if communication fails or no answer is received in time.
	 * @throws Exception
	 */

	public synchronized Data sendAndWaitFor(Data send, int timeout)
		throws IOException
	{
		wantsAnswer = true;
		answerData = null;
		sendData(send);
		long startWait = System.currentTimeMillis();
		do
		{
			try
			{
				wait(50);
			}
			catch (Exception e)
			{
			}
		}
		while ((answerData == null)
				&& ((startWait + timeout) > System.currentTimeMillis()));
		if (answerData == null)
		{
			throw new IOException("Timeout waiting for answer");
		}
		return answerData;
	}


	/**
	 * Sends a data object without waiting for an answer.
	 *
	 * @param d
	 * @throws IOException
	 */

	public void sendData(Data d) throws IOException
	{
		d.setSNum(sCounter++);
		byte b[] = d.getPacket();

		//		Log.i(Converter.class.getSimpleName(), "-> ");
		//		for (int i = 0; i < b.length; i++)
		//			Log.i(Converter.class.getSimpleName(),
		//				Integer.toHexString((b[i] & 0xff)) + " ");

		//		System.out.println();

		rawOut.write(b);
		rawOut.flush();
		totalWritten += b.length;
	}


	/**
	 * @param cleanupCallback
	 *            {@link #cleanupCallback}
	 */
	public void setCleanupCallback(
					ConverterThreadCleanupCallback cleanupCallback)
	{
		this.cleanupCallback = cleanupCallback;
	}


	/**
	 * Sets the protocol version of this communication. This might influence the
	 * way the actual data elements are created.
	 *
	 * @param version
	 */
	public void setCurrentVersion(int version)
	{
		this.version = version;
	}


	/**
	 * @param dc
	 *            The dc to set.
	 */
	public void setDataCreator(DataCreator dc)
	{
		this.dc = dc;
	}


	public void setSCounter(int counter)
	{
		sCounter = counter;
	}


	/**
	 * @param singleByteMode
	 *            The singleByteMode to set.
	 */
	public void setSingleByteMode(boolean singleByteMode)
	{
		this.singleByteMode = singleByteMode;
	}


	/**
	 * Startet einen neuen Tread, der die Daten liest und an die angeschlossenen
	 * Listener weiterleitet.
	 *
	 */

	public void startReading()
	{
		keepReading = true;
		Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				doReading();
			}
		};
		new Thread(r).start();
	}


	public void stopReading()
	{
		keepReading = false;
	}


	/**
	 * Method called to generate an object from the byte array read.
	 *
	 * @param b2
	 *            array of data [ 0xfc.......0xfd]
	 * @return object of type Data
	 * @throws Exception
	 */

	protected Data createData(byte b2[]) throws Exception
	{
		return dc.createData(b2, version);
	}


	protected void doReading()
	{
		byte b[] = new byte[1000];
		int pos = 0, parsePos, parseEnd;
		totalRead = 0;
		try
		{
			while (keepReading)
			{

				if (!keepReading)
				{
					if (DEBUG)
					{
						System.out.println("KeepReading==false -> return");
					}
					return;
				}

				if ((b.length - pos) == 0)
				{
					byte b2[] = b;
					b = new byte[b2.length * 2];
					System.arraycopy(b2, 0, b, 0, b2.length);
				}

				int posp = -1;

				if (!singleByteMode)
				{
					posp = rawIn.read(b, pos, b.length - pos);
				}
				else
				{
					int dat = rawIn.read();
					posp = -1;
					if (dat != -1)
					{
						b[pos] = (byte) dat;
						posp = 1;
					}
				}

				if (posp == -1)
				{
					throw new IOException("Channel closed");
					// System.out.println ("Converter Read " + posp + " bytes " +
					// dlc);
					// for (int i = 0;i < posp;i++) System.out.print(" " +
					// Integer.toHexString((int)(b[pos + i] & 0xff)));
					// System.out.println();
				}

				pos += posp;
				totalRead += posp;

				// Parsen von 0-pos
				parsePos = parseEnd = 0;

				while (parsePos < pos)
				{
					//					System.out.print("< ");
					//					 for (int i = 0;i < pos;i++)
					//					 System.out.print(Integer.toHexString((int)(b[i] & 0xff))
					//					 + " ");
					////					 System.out.println ("\n" + pos + " " + parsePos + " " +
					////					 parseEnd + " " + (b[0] == (byte)0xfc));
					//					 System.out.println();

					// System.out.println ("b-length " + b.length + " pos " +
					// pos + " parsePos " + parsePos);

					while ((parsePos < pos) && (b[parsePos] != (byte) 0xfc))
					{
						parsePos++;
					}
					if (parsePos >= pos)
					{
						if (DEBUG)
						{
							System.out.println("parsePos >= pos -> break pos="
												+ pos + " parsePos=" + parsePos);
						}
						break;
					}
					parseEnd = parsePos + 1;
					while ((parseEnd < pos) && (b[parseEnd] != (byte) 0xfd))
					{
						if (b[parsePos] != (byte) 0xfc)
						{
							parsePos = parseEnd; // Wenn nochmal ein 0xfc
						}
						// gefunden wird, dann
						// fehlte am vorherigen
						// Paket das 0xfd
						parseEnd++;
					}

					if (parseEnd >= pos)
					{
						// System.out.println("parseEnd >= pos -> break
						// pos="+pos+ " parseEnd="+parseEnd+"
						// parsePos="+parsePos);
						break;
					}

					byte[] b2 = new byte[(parseEnd - parsePos) + 1];
					System.arraycopy(b, parsePos, b2, 0, b2.length);
					// System.out.println ("Command between " + parsePos + " and
					// " + (parsePos + b2.length));
					try
					{
						if (logStream != null)
						{
							for (int i = 0; i < b2.length; i++)
							{
								String s = Integer.toHexString(0xff & b2[i]);
								if (s.length() == 1)
								{
									s = "0" + s;
								}
								logStream.print(" " + s);
							}
							logStream.println();
						}

						Data d = createData(b2);
						// System.out.println ("New Data at Converter " + d + "
						// listeners " + dataListeners.length + " " + dlc);
						if (wantsAnswer)
						{
							answerData = d;
							wantsAnswer = false;
							synchronized (this)
							{
								notifyAll();
							}
						}
						else
						{
							for (int i = 0; i < dlc; i++)
							{
								dataListeners[i].newData(d);
							}
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					parsePos = parseEnd + 1;

				}

				// Wenn bis zum Ende gelesen wurde ist alles in Ordnung
				if (parsePos == pos)
				{
					pos = 0;
				}
				else
				{ // Ansonsten h"angt noch ein halber befehl rum der zum
					// Anfang kopiert werden muss.
					System.arraycopy(b, parsePos, b, 0, pos - parsePos);
					pos -= parsePos;
				}

			}
		}
		catch (Exception e)
		{
			if (DEBUG)
			{
				e.printStackTrace();
			}
			keepReading = false;
			if (logStream != null)
			{
				logStream.println("Logging of data for " + getClass().getName()
									+ " ended at " + System.currentTimeMillis());
			}
			for (int i = 0; i < dlc; i++)
			{
				dataListeners[i].channelClosed();
			}
		}
		finally
		{
			keepReading = false;

			if (cleanupCallback != null)
			{
				cleanupCallback.cleanup();
			}

			if (DEBUG)
			{
				System.out.println("Converter::End doReading");
			}
		}
	}
}
