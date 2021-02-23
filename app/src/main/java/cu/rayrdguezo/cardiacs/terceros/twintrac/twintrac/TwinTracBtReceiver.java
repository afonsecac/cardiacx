package cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cu.rayrdguezo.cardiacs.EstabConexRecibirDatosActivity;
import cu.rayrdguezo.cardiacs.R;
import cu.rayrdguezo.cardiacs.terceros.twintrac.BtReceiverStateListener;
import cu.rayrdguezo.cardiacs.terceros.twintrac.CRC8Calculator;
import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.BCTarget;
import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data.TwinTracDataCreator;
import cu.rayrdguezo.cardiacs.terceros.twintrac.cs.data.Patient;

public class TwinTracBtReceiver extends TwinTracBasicReceiver
{
	public interface TwinTracDataListener
	{
		public abstract void configTwinTracDataReceived(
						TwinTracConfigManager config);


		public abstract void errorDuringBluetoothConnection(String title,
                                                            String message);


		public abstract void newTwinTracECGValuesReceived(
						TwinTracOnlineDataHolder values);


		public abstract void receivedPatientInformation(String patientName);
	}

	public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	private static final String logTag = TwinTracBtReceiver.class.getSimpleName();
	private final String btAddress;
	private final int device;
	private final boolean onlineControl;
	private final Context context;
	private final int STARTING_INDEX_ECG = 14;
	private final TwinTracDataListener dataListener;
	private final int MAXIMUM_TRY_SEND_COMMAND = 10;
	private final Patient currentPatient;
	private final BtReceiverStateListener btReceiverListener;
	/* Converter handles incoming and outgoing data */
	private TwinTracConverter conv;
	private BluetoothSocket socket;
	private boolean stopMeasurement;
	private TwinTracConfigManager configManager;
	private boolean configuredConfigManager = false;
	private int trySendCounter = 0;
	private boolean receivedFirstEcho = false;
	private boolean modePatientDatRead = false;
	private boolean readAndWritePatientDat = false;
	private boolean converterFirstTime;
	private boolean currentlySleep;


	/**
	 * Initiate the BT-Connection and prepare the Converter
	 *
	 * @param btAddress
	 * @param pat
	 * @param startReceiverFirstTime
	 *            Bluetooth address of the BT12
	 * @throws IOException
	 */
	public TwinTracBtReceiver(String btAddress, int device,
                              boolean onlineControl, Context context,
                              TwinTracDataListener ecgReceiver, Patient pat,
                              BtReceiverStateListener btReceiverListner,
                              boolean startReceiverFirstTime)
		throws IOException
	{
		super();
		this.btAddress = btAddress;
		this.device = device;
		this.onlineControl = onlineControl;
		this.context = context;
		this.dataListener = ecgReceiver;
		this.currentPatient = pat;
		this.btReceiverListener = btReceiverListner;
		this.converterFirstTime = startReceiverFirstTime;
		this.currentlySleep = false;
	}


	/**
	 * Called from the Converter if an interruption is detected.
	 */
	@Override
	public void channelClosed()
	{
		Log.d(getClass().getSimpleName(), "Connection closed");
		cancel(true);
	}


	/**
	 * @return
	 */
	public boolean isStarted()
	{
		return (socket != null);
	}


	public boolean isStopMeasurement()
	{
		return stopMeasurement;
	}


	/**
	 * Called from the converter when new packets from the device are received
	 * that are not directly intercepted by a sendAndWait method.
	 */

	@Override
	public void patientInformation(byte[] data)
	{

	}


	public void setMarkerInMeasurement(String markerName)
	{
		try
		{
			Log.i(logTag, markerName);
			//			conv.getOutputStream().write(
			//				("WMarker" + 0x0B + 0x00 + "+MARKERNAME").getBytes());
			//length + 1byte CRC
			byte[] length = convertShortToByteArray((short) (markerName.length() + 1));
			Log.i(logTag, "low: " + length[0] + ", high: " + length[1]);
			int i = 0;
			byte[] valueNoCrC = ("WMarker" + (char) length[0]
									+ (char) length[1] + markerName).getBytes();
			for (byte b : valueNoCrC)
			{
				Log.i(logTag, "index: " + (i++) + ", value: " + b + ", "
								+ (char) b);
			}
			byte[] valueWithCRC = new byte[valueNoCrC.length + 1];
			System.arraycopy(valueNoCrC, 0, valueWithCRC, 0, valueNoCrC.length);
			//last value is the 1byte crc
			valueWithCRC[valueWithCRC.length - 1] = CRC8Calculator.calc(
				valueNoCrC, valueNoCrC.length);
			conv.getOutputStream().write(valueWithCRC);
			conv.getOutputStream().flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Toast.makeText(context, "Error: " + e, Toast.LENGTH_LONG).show();
		}

	}


	public void setStopMeasurement(boolean stopMeasurement)
	{
		this.stopMeasurement = stopMeasurement;
	}


	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Exception doInBackground(BCTarget ... targets)
	{
		Exception thrown = null;
		try
		{
			runInt(targets);

			while (!isCancelled())
			{
				Thread.sleep(100l);
			}
		}
		catch (Exception e)
		{
			thrown = e;
		}
		finally
		{

		}

		return thrown;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onCancelled()
	 */
	@Override
	protected void onCancelled()
	{
		Log.d(getClass().getSimpleName(), "Cancelled");
		try
		{
			stopReading();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		btReceiverListener.btReceiverFinishedWithException();
	}


	@Override
	protected void onPostExecute(Exception result)
	{
		Log.d(getClass().getSimpleName(), "Done");

		if (result != null)
		{
			Log.e(getClass().getSimpleName(), "ERRROORR", result);
			//			Toast.makeText(
			//				context,
			//				"Bluetooth socket could not be open. Please start the measurement again.\n Error message: "
			//								+ result.getMessage(), Toast.LENGTH_LONG).show();
			//			dataListener.errorDuringBluetoothConnection(
			//				context.getString(R.string.error_bt_connection_title),
			//				context.getString(R.string.error_bt_connection_content)
			//								+ result.getMessage());
			btReceiverListener.btReceiverFinishedWithException();
		}
		try
		{
			stopReading();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	@Override
	protected void parseReceivedData(List<Byte> receivedData)
	{
		//		Log.i(
		//			logTag,
		//			"received data: "
		//							+ TwinTracDataCreator.byteArrayToHex(receivedData.toArray(new Byte[receivedData.size()]))
		//							+ " (length=" + receivedData.size() + ")");
		byte[] completePackage = getCompletePackage(receivedData);
		while (completePackage != null)
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			completePackage = TwinTracDataCreator.unstuff(completePackage, 0,
				completePackage.length);
			byte responsePNToRequest = TwinTracDataCreator.getResponsedPackageNumber(completePackage);
			Log.d(
				logTag,
				"complete package found: "
								+ TwinTracDataCreator.byteArrayToHex(completePackage)
								+ " (length=" + completePackage.length + ")");
			switch (responsePNToRequest)
			{
			//case TwinTracPackageEcho.PACKAGE_NUMBER:
				case TwinTracDataCreator.ECHO_CMD:
				{
					if (!onlineControl && converterFirstTime)
					{
						trySendCounter = 1;
						conv.writeByte(new TwinTracPackageWriteTime().getPacket());
					}
					else
					{
						trySendCounter = 1;
						conv.writeByte(new TwinTracPackageMode(true).getPacket());
					}
					break;
				}
				//case TwinTracPackageWriteTime.PACKAGE_NUMBER:
				case TwinTracDataCreator.WRITE_TIME_CMD:
				{
					if (!onlineControl)
					{
						trySendCounter = 1;
						conv.writeByte(new TwinTracPackageRecordDatDelete().getPacket());
					}
					break;
				}
				//case TwinTracPackagePatientDelete.PACKAGE_NUMBER:
				case TwinTracDataCreator.DELETE_RECORD_DAT_FILE_CMD:
				{
					if (!onlineControl)
					{
						trySendCounter = 1;
						if (!readAndWritePatientDat)
						{
							conv.writeByte(new TwinTracPackagePatientRead().getPacket());
						}
						else
						{
							conv.writeByte(new TwinTracPackageConfigRead().getPacket());
						}
					}
					break;
				}
				//								case TwinTracPackageConfigRead.PACKAGE_NUMBER:
				case TwinTracDataCreator.READ_FILE_CMD:
				//case TwinTracDataCreator.RESPONSED_OK_CONFIG_CMD:
				{
					if (!onlineControl)
					{
						if (!readAndWritePatientDat)
						{
							byte[] patientDatPackage = parsePatientDatData(
								completePackage, 10, false);
							String firmwareVersion = getFirmwareVersionFromPatientDat(completePackage);
							conv.writeByte(new TwinTracPackagePatientWrite(
								patientDatPackage, currentPatient).getPacket());
						}
						else
						{
							byte[] configPackage = parseConfigData(
								completePackage, 10, false);
							if (prefs.getBoolean(
								context.getString(R.string.preferenceKey_twintracconfig),
								false)
								&& !configuredConfigManager)
							{
								conv.writeByte(new TwinTracPackageConfigWrite(
									configPackage, context).getPacket());
							}
							else
							{
								conv.writeByte(new TwinTracPackageStartRecording().getPacket());
							}
						}
					}

					break;
				}
				//				case TwinTracPackageConfigWrite.PACKAGE_NUMBER:
				case TwinTracDataCreator.WRITE_FILE_CMD:
				{
					if (!onlineControl)
					{
						trySendCounter = 1;
						if (!readAndWritePatientDat)
						{
							readAndWritePatientDat = true;
							conv.writeByte(new TwinTracPackageConfigRead().getPacket());
						}
						else
						{
							configuredConfigManager = true;
							configManager = null;
							conv.writeByte(new TwinTracPackageConfigRead().getPacket());
						}
					}
					break;
				}
				//				case TwinTracPackageStartRecording.PACKAGE_NUMBER:
				case TwinTracDataCreator.START_RECORDING_CMD:
				{
					break;
				}
				case TwinTracDataCreator.ONLINE_ECG_CMD:
				{

					if (currentlySleep)
					{
						converterFirstTime = false;
					}
					parseOnlineData(completePackage);
					//					TwinTracPackageDummyResponse ack = new TwinTracPackageDummyResponse(
					//						completePackage[1]);
					//					Log.i(
					//						logTag,
					//						"send package: "
					//										+ TwinTracDataCreator.byteArrayToHex(ack.getPacket()));
					//					TwinTracPackageACK ackPackage = new TwinTracPackageACK(
					//						TwinTracDataCreator.ONLINE_ECG_CMD, completePackage[1]);
					//					conv.writeByte(ackPackage.getPacket());
					conv.writeByte(new byte[] { 0x00 });
					break;
				}
				case TwinTracDataCreator.MODE_FILE_CMD:
				{
					Log.i(logTag, "MODE package receive!");
					if (!modePatientDatRead)
					{
						trySendCounter = 1;
						String patientName = getPatientName(completePackage);
						Log.i(logTag, "patientName = " + patientName);
						modePatientDatRead = true;
						dataListener.receivedPatientInformation(patientName);
						conv.writeByte(new TwinTracPackageMode(false).getPacket());
					}
					else
					{
						//Mode 0x25 not contains the PA, so changed from 5 to 4 index start
						//						parseConfigData(completePackage, 5, false);
						parseConfigData(completePackage, 4, false);
					}
					break;

				}
				case TwinTracDataCreator.RESPONSED_ERROR_CMD:
				{
					//conv.writeByte(new TwinTracPackageStartRecording().getPacket());
					dataListener.errorDuringBluetoothConnection(
						context.getString(R.string.error_during_bt_transmission_title),
						context.getString(R.string.error_during_bt_transmission_content));
					break;
				}
				case TwinTracDataCreator.RESPONSED_NOT_OK_CMD:
				{
					if (trySendCounter == MAXIMUM_TRY_SEND_COMMAND)
					{
						onCancelled();
						dataListener.errorDuringBluetoothConnection(
							context.getString(R.string.error_during_bt_transmission_title),
							context.getString(R.string.error_during_bt_transmission_content));
					}
					else
					{
						byte failedReponsedPackageNumber = TwinTracDataCreator.getNotOkReponsedPackageNumber(completePackage);
						trySendCounter++;
						switch (failedReponsedPackageNumber)
						{
						//case TwinTracPackageEcho.PACKAGE_NUMBER:
							case TwinTracDataCreator.ECHO_CMD:
							{
								if (!onlineControl)
								{
									conv.writeByte(new TwinTracPackageEcho().getPacket());
								}
								break;
							}
							//case TwinTracPackageWriteTime.PACKAGE_NUMBER:
							case TwinTracDataCreator.WRITE_TIME_CMD:
							{
								if (!onlineControl)
								{
									conv.writeByte(new TwinTracPackageWriteTime().getPacket());
								}
								break;
							}
							//case TwinTracPackagePatientDelete.PACKAGE_NUMBER:
							case TwinTracDataCreator.DELETE_RECORD_DAT_FILE_CMD:
							{
								if (!onlineControl)
								{

									//									conv.writeByte(new TwinTracPackageRecordDatDelete().getPacket());
									conv.writeByte(new TwinTracPackageConfigRead().getPacket());
								}
								break;
							}
							//							case TwinTracPackageConfigRead.PACKAGE_NUMBER:
							//							case TwinTracPackageConfigWrite.PACKAGE_NUMBER:
							case TwinTracDataCreator.MODE_FILE_CMD:
							{
								if (!modePatientDatRead)
								{
									conv.writeByte(new TwinTracPackageMode(true).getPacket());
								}
								else
								{
									conv.writeByte(new TwinTracPackageMode(
										false).getPacket());
								}
								break;

							}
							case TwinTracDataCreator.READ_FILE_CMD:
							case TwinTracDataCreator.WRITE_FILE_CMD:
							{
								if (!onlineControl)
								{
									if (!readAndWritePatientDat)
									{
										conv.writeByte(new TwinTracPackagePatientRead().getPacket());
									}
									else
									{
										configuredConfigManager = false;
										if (!onlineControl)
										{
											conv.writeByte(new TwinTracPackageConfigRead().getPacket());
										}
									}
								}
								break;
							}
							//							case TwinTracPackageStartRecording.PACKAGE_NUMBER:
							case TwinTracDataCreator.START_RECORDING_CMD:
							{
								if (!onlineControl)
								{
									conv.writeByte(new TwinTracPackageEcho().getPacket());
									//conv.writeByte(new TwinTracPackageStartRecording().getPacket());
								}
								break;
							}
						}
					}
					break;
				}
				default:
				//new TwinTracPackageConfigPackage(completePackage, context);
				Log.i(logTag, "Response not cmd not found!");
			}
			completePackage = null;
			completePackage = getCompletePackage(receivedData);
		}
		//		Log.i(logTag,
		//			"available received data: "
		//						+ TwinTracDataCreator.byteArrayToHex(
		//							receivedData.toArray(new Byte[receivedData.size()]))
		//						+ " (length=" + receivedData.size() + ")");
	}


	/**
	 * parse patient name from received patient.dat file
	 *
	 * @param completePackage
	 * @return
	 * @date 27.12.2016 16:08:30 Hakan Sahin
	 */
	private String getFirmwareVersionFromPatientDat(byte[] completePackage)
	{
		byte[] ecgTypeArray = new byte[10];
		byte[] serialNumberArray = new byte[10];
		byte[] firmwareVersionArray = new byte[10];
		int startPatientDatData = 5;
		byte pa = completePackage[4];
		//first 5 bytes 0-4 are START_FLAG, PACKAGE_NR,COMMAND,CONTROL and "pas" as first value in patient.dat
		System.arraycopy(completePackage, startPatientDatData + 131,
			ecgTypeArray, 0, ecgTypeArray.length);
		System.arraycopy(completePackage, startPatientDatData + 131
											+ ecgTypeArray.length,
			serialNumberArray, 0, serialNumberArray.length);
		System.arraycopy(completePackage, startPatientDatData + 131
											+ ecgTypeArray.length
											+ serialNumberArray.length,
			firmwareVersionArray, 0, firmwareVersionArray.length);

		//Replace all control characters \0x00-\0x1F and \0x7F with space ""
		String ecgType = new String(ecgTypeArray).replaceAll("\\p{Cntrl}", "");
		String serialNumber = new String(serialNumberArray).replaceAll(
			"\\p{Cntrl}", "");
		String firmwareVersion = new String(firmwareVersionArray).replaceAll(
			"\\p{Cntrl}", "");
		Log.i(logTag, "ecgType = " + ecgType + ", serialNumber = "
						+ serialNumber + ", firmwareVersion = "
						+ firmwareVersion);
		return firmwareVersion;
	}


	private int getNextIntervalStep()
	{
		int nextIntervalStep = 0;
		if (configManager.isEcg1Active())
		{
			nextIntervalStep = nextIntervalStep + 2;
		}
		if (configManager.isEcg2Active())
		{
			nextIntervalStep = nextIntervalStep + 2;
		}
		if (configManager.isAccXActive())
		{
			nextIntervalStep = nextIntervalStep + 2;
		}
		if (configManager.isAccYActive())
		{
			nextIntervalStep = nextIntervalStep + 2;
		}
		if (configManager.isAccZActive())
		{
			nextIntervalStep = nextIntervalStep + 2;
		}
		if (configManager.isAccLActive())
		{
			nextIntervalStep = nextIntervalStep + 2;
		}
		return nextIntervalStep;
	}


	/**
	 * parse patient name from received patient.dat file
	 *
	 * @param completePackage
	 * @return
	 * @date 27.12.2016 16:08:30 Hakan Sahin
	 */
	private String getPatientName(byte[] completePackage)
	{
		byte[] patientFirstNameArray = new byte[20];
		byte[] patientSecondNameArray = new byte[20];
		//first 5 bytes 0-4 are START_FLAG, PACKAGE_NR,COMMAND,CONTROL and "pas" as first value in patient.dat
		int startPatientDatData = 5;
		byte pa = completePackage[4];
		System.arraycopy(completePackage, startPatientDatData + 18,
			patientFirstNameArray, 0, patientFirstNameArray.length);
		System.arraycopy(completePackage, startPatientDatData + 18
											+ patientFirstNameArray.length,
			patientSecondNameArray, 0, patientSecondNameArray.length);

		//Replace all control characters \0x00-\0x1F and \0x7F with space ""
		String patientFirstName = new String(patientFirstNameArray).replaceAll(
			"\\p{Cntrl}", "");
		String patientLastName = new String(patientSecondNameArray).replaceAll(
			"\\p{Cntrl}", "");

		return patientLastName + ",\n" + patientFirstName;
	}


	/**
	 * Start Flag | Package-No. | Command | Control | Data | Checksum | End Flag
	 * <p>
	 * 0xFC | 1 Byte | 0x28 | 0x30 | XXX Byte(see below) | 4 Bytes | 0xFD
	 * <p>
	 * Byte 1 PN
	 * <p>
	 * Byte 2-11 File size in Byte
	 * <p>
	 * Byte 12 Block Number (00h-FFh - continuously and repetitively)
	 * <p>
	 * Byte 13 - XXX Data
	 *
	 * @param completePackage
	 * @param indexMissing
	 * @return
	 * @date 19.12.2016 14:58:51 Hakan Sahin
	 */
	private byte[] parseConfigData(byte[] completePackage,
					int configDataStartingIndex, boolean indexMissing)
	{
		int configDataEndingIndex = completePackage.length - 5;
		byte[] configDataPackage = new byte[completePackage.length - 5
											- configDataStartingIndex];
		System.arraycopy(completePackage, configDataStartingIndex,
			configDataPackage, 0, configDataPackage.length);
		configManager = new TwinTracConfigManager(configDataPackage,
			indexMissing);
		dataListener.configTwinTracDataReceived(configManager);
		Log.i(
			logTag,
			"complete config package found: "
							+ TwinTracDataCreator.byteArrayToHex(configDataPackage)
							+ " (length=" + configDataPackage.length + ")");
		return configDataPackage;

	}


	/**
	 * copy from page 39
	 * <p>
	 * Start Flag | Package-No. | Command | Control | Data | Checksum | End Flag
	 * 0xFC | 1 Byte | 0x42 | 0x00 | XXX Byte(data see below)| 4 Bytes |0xFD
	 * <p>
	 * Byte 1 - 2 Number of Data Bytes
	 * <p>
	 * Byte 3 - XX Temperature Data belonging to the active channels (2 Byte /
	 * active Channel, in BCD Format, Offset 200)
	 * <p>
	 * Byte XX Battery Capacity in % (1 Byte in BCD Format)
	 * <p>
	 * Byte XX Remaining Recording Time in Hours (1 Byte in HEX Format)
	 * <p>
	 * Byte XX - XXX ECG Daten belonging to the active channels (2 Byte / active
	 * Channel, in BCD Format, Offset 5000)
	 * <p>
	 * Byte XXX - XXX Accelerometer Data belonging to the active channels (2
	 * Byte / active Channel, im BCD Format, Offset 4000)
	 *
	 * @date 19.12.2016 14:39:34 Hakan Sahin
	 */
	private void parseOnlineData(byte[] completePackage)
	{
		//		if (configManager == null)
		//		{
		//			configManager = new TwinTracConfigManager();
		//		}
		if (configManager != null)
		{
			int startIndex = 4;
			int indexData = startIndex;
			int numberOfBytes = get2ByteIntValueFromIndex(completePackage,
				indexData);
			indexData = indexData + 2;
			int temp1 = Integer.MIN_VALUE, temp2 = Integer.MIN_VALUE, temp3 = Integer.MIN_VALUE;
			int heartRate = Integer.MIN_VALUE;
			if (configManager.isTemp1Active())
			{
				temp1 = get2ByteBCDIntValueFromIndex(completePackage, indexData);
				indexData = indexData + 2;
			}
			if (configManager.isTemp2Active())
			{
				temp2 = get2ByteBCDIntValueFromIndex(completePackage, indexData);
				indexData = indexData + 2;
			}
			if (configManager.isTemp3Active())
			{
				if (configManager.isHeartRateActive())
				{
					heartRate = get2ByteIntValueFromIndex(completePackage,
						indexData);
				}
				else
				{
					temp3 = get2ByteBCDIntValueFromIndex(completePackage,
						indexData);
				}
				indexData = indexData + 2;
			}
			int batteryCapacityInPercent = get1ByteBCDIntValueFromIndex(
				completePackage, indexData);
			indexData++;
			int remainingTime = get1ByteIntValueFromIndex(completePackage,
				indexData);
			indexData++;
			List<Integer> ecgValues = new ArrayList<>();
			double lastAccValue = 0;
			int nextIntervalStep = getNextIntervalStep();
			while ((indexData + nextIntervalStep + 2) < (completePackage.length - 5))
			{
				if (configManager.isEcg1Active())
				{
					int ecgChannel1 = get2ByteBCDIntValueFromIndex(
						completePackage, indexData);
					indexData = indexData + 2;
					//					Log.i(logTag, "ecg channel 1: " + ecgChannel1 + ", index: "
					//									+ indexData);
					ecgValues.add(ecgChannel1);
				}
				else
				{
					ecgValues.add(0);
				}
				if (configManager.isEcg2Active())
				{
					int ecgChannel2 = get2ByteBCDIntValueFromIndex(
						completePackage, indexData);
					indexData = indexData + 2;
					//					Log.i(logTag, "ecg channel 2: " + ecgChannel2 + ", index: "
					//									+ indexData);
					ecgValues.add(ecgChannel2);
				}
				else
				{
					ecgValues.add(0);
				}
				double sumAcc = 0;
				if (configManager.isAccXActive())
				{
					int accX = get2ByteBCDIntValueFromIndex(completePackage,
						indexData);
					sumAcc = sumAcc + (accX * accX);
					indexData = indexData + 2;
				}
				if (configManager.isAccYActive())
				{
					int accY = get2ByteBCDIntValueFromIndex(completePackage,
						indexData);
					sumAcc = sumAcc + (accY * accY);
					indexData = indexData + 2;
				}
				if (configManager.isAccZActive())
				{
					int accZ = get2ByteBCDIntValueFromIndex(completePackage,
						indexData);
					sumAcc = sumAcc + (accZ * accZ);
					indexData = indexData + 2;
				}
				lastAccValue = Math.sqrt(sumAcc);
				if (configManager.isAccLActive())
				{
					lastAccValue = get2ByteBCDIntValueFromIndex(
						completePackage, indexData);
					indexData = indexData + 2;
				}
				//jump over 2 more bytes. Did not know what this is
				//indexData = indexData + 2;
			}
			//			Log.i(logTag, "indexData = " + indexData
			//							+ ", completePackage.length - 5 = "
			//							+ (completePackage.length - 5));
			int[] ecgValuesInt = new int[ecgValues.size()];
			for (int i = 0; i < ecgValuesInt.length; i++)
			{
				ecgValuesInt[i] = ecgValues.get(i);
			}
			dataListener.newTwinTracECGValuesReceived(new TwinTracOnlineDataHolder(
				temp1, temp2, temp3, heartRate, batteryCapacityInPercent,
				remainingTime, ecgValuesInt, lastAccValue));
		}
	}


	/**
	 * Start Flag | Package-No. | Command | Control | Data | Checksum | End Flag
	 * <p>
	 * 0xFC | 1 Byte | 0x28 | 0x30 | XXX Byte(see below) | 4 Bytes | 0xFD
	 * <p>
	 * Byte 1 PN
	 * <p>
	 * Byte 2-11 File size in Byte(this is false only 4 bytes)
	 * <p>
	 * Byte 12 Block Number (00h-FFh - continuously and repetitively)
	 * <p>
	 * Byte 13 - XXX Data
	 *
	 * @param completePackage
	 * @param indexMissing
	 * @return
	 * @date 19.12.2016 14:58:51 Hakan Sahin
	 */
	private byte[] parsePatientDatData(byte[] completePackage,
					int configDataStartingIndex, boolean indexMissing)
	{
		int configDataEndingIndex = completePackage.length - 5;
		byte[] configDataPackage = new byte[completePackage.length - 5
											- configDataStartingIndex];
		System.arraycopy(completePackage, configDataStartingIndex,
			configDataPackage, 0, configDataPackage.length);
		Log.i(
			logTag,
			"complete patient package found: "
							+ TwinTracDataCreator.byteArrayToHex(configDataPackage)
							+ " (length=" + configDataPackage.length + ")");
		return configDataPackage;

	}


	private boolean parseVersionInformationPackage(byte[] completePackage)
	{
		byte[] ecgTypeArray = new byte[10];
		byte[] serialNumberArray = new byte[10];
		byte[] firmwareVersionArray = new byte[10];
		int startVersionPackageData = 5;
		byte pa = completePackage[4];
		//first 5 bytes 0-4 are START_FLAG, PACKAGE_NR,COMMAND,CONTROL and "pas" as first value in patient.dat
		System.arraycopy(completePackage, startVersionPackageData,
			ecgTypeArray, 0, ecgTypeArray.length);
		System.arraycopy(completePackage, startVersionPackageData
											+ ecgTypeArray.length,
			serialNumberArray, 0, serialNumberArray.length);
		System.arraycopy(completePackage, startVersionPackageData
											+ ecgTypeArray.length
											+ serialNumberArray.length,
			firmwareVersionArray, 0, firmwareVersionArray.length);

		//Replace all control characters \0x00-\0x1F and \0x7F with space ""
		String ecgType = new String(ecgTypeArray).replaceAll("\\p{Cntrl}", "");
		String serialNumber = new String(serialNumberArray).replaceAll(
			"\\p{Cntrl}", "");
		String firmwareVersion = new String(firmwareVersionArray).replaceAll(
			"\\p{Cntrl}", "");
		Log.i(logTag, "parseVersionInformationPackage: ecgType = " + ecgType
						+ ", serialNumber = " + serialNumber
						+ ", firmwareVersion = " + firmwareVersion);
		return true;
	}


	private void runInt(BCTarget ... targets) throws Exception
	{
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothDevice btDevice = btAdapter.getRemoteDevice(btAddress);
		Method m = btDevice.getClass().getMethod("createRfcommSocket",
			new Class[] { int.class });
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
		{
			socket = (BluetoothSocket) m.invoke(btDevice, 1);
		}
		else
		{
			socket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
		}
		btAdapter.cancelDiscovery();
		socket.connect();

		if (device == EstabConexRecibirDatosActivity.TwinTrac)
		{
			conv = new TwinTracConverter(socket.getInputStream(),
				socket.getOutputStream(), EstabConexRecibirDatosActivity.TwinTrac);
		}
		else
		{
			return;
		}

		// Register ourselves as listener for incoming packets
		conv.addBCTarget(this);
		for (BCTarget bcTarget : targets)
		{
			conv.addBCTarget(bcTarget);
		}

		// Starts a separate thread to handle IO
		conv.startReading();
		btReceiverListener.stopFillingAutomaticValues();
		currentlySleep = true;
		Thread.sleep(1000);
		Log.d(logTag, "WAKED UP converterFirstTime: " + converterFirstTime);
		currentlySleep = false;
		if (device == EstabConexRecibirDatosActivity.TwinTrac)
		{
			// Cannot use conv.sendData() since it appends useless header & footer
			Log.i(logTag, "OnlineControl = " + onlineControl);
			conv.writeByte(new TwinTracPackageEcho().getPacket());
		}
		Log.d(getClass().getSimpleName(), "Started...");
	}


	private void stopReading() throws Exception
	{
		// Stop ECG transmission
		if (stopMeasurement)
		{
			try
			{
				if (device == EstabConexRecibirDatosActivity.TwinTrac)
				{
					// Cannot use conv.sendData() since it appends useless header & footer
					Log.i(logTag, "Messung wird beendet");
					conv.writeByte(new TwinTracPackageStopRecording().getPacket());
				}
			}
			catch (Exception e)
			{
				Log.d(getClass().getName(), e.getMessage());
			}
		}
		// Stop the IO process
		conv.stopReading();

		// Close the BT connection
		Thread.sleep(500);
		conv.getInputStream().close();
		conv.getOutputStream().close();
		Thread.sleep(1000);
		socket.close();
	}

}
