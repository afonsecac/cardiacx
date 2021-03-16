package cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Set;

import cu.rayrdguezo.cardiacs.R;
import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data.TwinTracDataCreator;

/**
 * Data to create payload to start and stop BT transmission.
 * 
 * for more information please look in "TD TwinTrac EN V1.8.pdf" page 15
 * 
 * @author Hakan Sahin
 * @date 08.06.2016 08:37:29 Hakan Sahin
 */
public class TwinTracPackageConfigWrite extends TwinTracData
{
	public static final byte PACKAGE_NUMBER = 0x06;
	private static final String logTag = TwinTracPackageConfigWrite.class.getSimpleName();
	/* Documentation index number staring with 0 */
	private final int BLUETOOTH_ON_ALWAYS_OR_COUNTER_INDEX = 13;
	private final int BLUETOOTH_TIME_INDEX = 14;
	private final int ECG_CHANNELS_SET_ONLINE_INDEX = 19;
	private final int ECG_CHANNELS_SET_SDCARD_INDEX = 21;
	private final int TEMP_CHANNELS_ONLINE_INDEX = 27;
	private final int TEMP_CHANNELS_SDCARD_INDEX = 28;
	private final int ACCELEROMETER_CHANNELS_ONLINE_INDEX = 29;
	private final int ACCELEROMETER_CHANNELS_SDCARD_INDEX = 30;
	private final int SAMPLING_RATE_ONLINE_INDEX = 31;
	private final int SAMPLING_RATE_SDCARD_INDEX = 32;
	private final int RECORD_MODE_INDEX = 43;
	private final int RECORD_STANDARD_DAYS_INDEX = 44;
	private final int RECORD_STANDARD_HOURS_INDEX = 45;
	private final int RECORD_EVENT_SECONDS_INDEX = 46;
	private final int RECORD_LOOP_SECONDS_INDEX = 47;
	private final int USB_MODE_INDEX = 64;
	private final int HEART_RATE_INDEX = 66;
	private final int SOFTGAIN_INDEX = 2;
	private final int SD_CARD_IN_STORAGE_IF_ACTIVE_INDEX = 1;
	private final int DOWNLOAD_PROTECTION_INDEX = 1;
	private final int AUTOSTART_INDEX = 3;
	private final int AUTOSTART_TIME_INDEX = 4;


	public TwinTracPackageConfigWrite(byte[] currentConfig, Context context)
	{
		super(TwinTracDataCreator.WRITE_FILE_CMD, PACKAGE_NUMBER, currentConfig);
		currentConfig = modifyCurrentPayload(currentConfig, context);
		currentConfig = addDefaultValuesForApp(currentConfig);

		byte[] payloadArray = getPayloadFromData(currentConfig);
		setPayload(payloadArray);

		Log.d(
			logTag,
			"Config Package: "
							+ TwinTracDataCreator.byteArrayToHex(getPacket()));
	}


	/**
	 * Mail 10.01.2016 18:04 Herr Richter
	 * 
	 * Ansonsten sollten die folgenden Bytes defaultmäßig vorbelegt sein:
	 * 
	 * Byte 5 – 11 = 00h
	 * 
	 * Byte 12 = 03h
	 * 
	 * Byte 15 – 18 = 00h
	 * 
	 * Byte 33 – 42 = 00h
	 * 
	 * Byte 48 = 00h
	 * 
	 * Byte 49 – 60 = 45 43 47 2E 44 41 54 00 00 00 00 00h
	 * 
	 * Byte 61 – 63 = 00h
	 * 
	 * Byte 65 = 00h
	 * 
	 * Byte 67 – 68 = 00 80h
	 * 
	 * Byte 69 = A5h
	 * 
	 * Byte 70 – 256 = 00h
	 * 
	 * @param currentConfig
	 * @return
	 * @date 24.01.2017 11:09:14 Hakan Sahin
	 */
	private byte[] addDefaultValuesForApp(byte[] currentConfig)
	{
		currentConfig = addIntoDatFile(currentConfig, 5, 6, "");
		currentConfig[12] = 0x03;
		currentConfig = addIntoDatFile(currentConfig, 15, 4, "");
		currentConfig = addIntoDatFile(currentConfig, 33, 10, "");
		currentConfig[48] = 0x00;
		currentConfig = addIntoDatFile(currentConfig, 49, new byte[] { 0x45,
																		0x43,
																		0x47,
																		0x2E,
																		0x44,
																		0x41,
																		0x54,
																		0x00,
																		0x00,
																		0x00,
																		0x00,
																		0x00 });
		currentConfig = addIntoDatFile(currentConfig, 61, 3, "");
		currentConfig[65] = 0x00;
		currentConfig = addIntoDatFile(currentConfig, 67,
			new byte[] { 0x00, (byte) 0x80 });
		currentConfig[69] = (byte) 0xA5;
		currentConfig = addIntoDatFile(currentConfig, 70, (255 - 70 + 1), "");
		return currentConfig;
	}


	private byte[] addIntoDatFile(byte[] currentDatFile, int indexStart,
					byte[] addBytes)
	{
		for (int i = 0; i < addBytes.length; i++)
		{
			currentDatFile[indexStart + i] = addBytes[i];
		}
		return currentDatFile;
	}


	private byte[] addIntoDatFile(byte[] currentDatFile, int indexStart,
					int length, String text)
	{
		for (int i = 0; i < length; i++)
		{
			if (i < text.length())
			{
				currentDatFile[indexStart + i] = (byte) text.charAt(i);
			}
			else
			{
				currentDatFile[indexStart + i] = 0x00;
			}
		}
		return currentDatFile;
	}


	private byte getPayloadForChannel(byte currentConfigPayload,
					boolean contains, int bitIndex)
	{
		byte logicByte = 0;
		if (contains)
		{
			switch (bitIndex)
			{
				case 1:
				logicByte = (byte) 0x01; //0000 0001 first bit to 1
				break;
				case 2:
				logicByte = 0x02;//0000 0010 second bit to 1
				break;
				case 3:
				logicByte = 0x04;//0000 0100 third bit to 1
				break;
				case 4:
				logicByte = 0x08;//0000 1000 fourth bit to 1
				break;
				case 5:
				logicByte = 0x10;//0001 0000 fifth bit to 1
				break;
			}
			currentConfigPayload = (byte) ((currentConfigPayload | logicByte) & 0xFF);
			return currentConfigPayload;
		}
		else
		{
			switch (bitIndex)
			{
				case 1:
				logicByte = (byte) 0xFE;//with &: 1111 1110 first bit to 0
				break;
				case 2:
				logicByte = (byte) 0xFD;//with &: 1111 1101 second bit to 0
				break;
				case 3:
				logicByte = (byte) 0xFB;//with &: 1111 1011 third bit to 0
				break;
				case 4:
				logicByte = (byte) 0xF7;//with &: 1111 0111 forth bit to 0
				break;
				case 5:
				logicByte = (byte) 0xEF;//with &: 1110 1111 fifth bit to 0
				break;
			}
			currentConfigPayload = (byte) ((currentConfigPayload & logicByte));
			return currentConfigPayload;
		}
	}


	/**
	 * Start Flag | Package-No. | Command | Control | Data(see bellow) |
	 * Checksum | End Flag
	 * 
	 * 0xFC | 1 Byte | 0x32 | 0x00 | (XXX Byte) | 4 Bytes | 0xFD
	 * 
	 * #DATA
	 * 
	 * Byte 1 PN
	 * 
	 * Byte 2-13 File Name
	 * 
	 * Byte 14-23 File Size in Byte
	 * 
	 * Byte 24 Block Number (00h-FFh - continuously and repetitively)
	 * 
	 * Byte 25 - XXX Data of the File (max. 480 Byte)
	 * 
	 * @param currentPackage
	 * @return
	 * @date 22.12.2016 17:10:44 Hakan Sahin
	 */
	private byte[] getPayloadFromData(byte[] currentPackage)
	{
		//		byte[] configPayload = new byte[currentPackage.length - 5 - 5];
		//		System.arraycopy(currentPackage, 5, configPayload, 0,
		//			configPayload.length);

		byte[] configPayload = currentPackage;

		byte packageNumber = PACKAGE_NUMBER;
		byte[] payload = new byte[1];
		payload[0] = packageNumber;
		byte[] b = payload;
		byte[] fileName = new byte[] { (byte) 'C', (byte) 'O', (byte) 'N',
										(byte) 'F', (byte) 'I', (byte) 'G',
										(byte) '.', (byte) 'D', (byte) 'A',
										(byte) 'T', 0x00, 0x00 };
		payload = new byte[b.length + fileName.length];
		System.arraycopy(b, 0, payload, 0, b.length);
		System.arraycopy(fileName, 0, payload, b.length, fileName.length);
		b = payload;

		int size = configPayload.length;
		byte[] sizeArrayInt = ByteBuffer.allocate(4).putInt(size).array();
		//byte[] sizeArray = new byte[10];
		//System.arraycopy(sizeArrayInt, 0, sizeArray, 6, sizeArrayInt.length);
		byte[] sizeArray = new byte[sizeArrayInt.length];
		for (int i = 0; i < sizeArray.length; i++)
		{
			sizeArray[i] = sizeArrayInt[sizeArrayInt.length - 1 - i];
		}
		Log.i(
			logTag,
			"size: " + size + ", sizearray length: " + sizeArray.length
							+ ", hex: "
							+ TwinTracDataCreator.byteArrayToHex(sizeArray));
		byte blockNumber = 0x00;
		payload = new byte[b.length + sizeArray.length + 1];
		System.arraycopy(b, 0, payload, 0, b.length);
		System.arraycopy(sizeArray, 0, payload, b.length, sizeArray.length);
		payload[payload.length - 1] = blockNumber;
		b = payload;

		payload = new byte[b.length + currentPackage.length];
		System.arraycopy(b, 0, payload, 0, b.length);
		System.arraycopy(configPayload, 0, payload, b.length,
			configPayload.length);

		return payload;
	}


	private byte[] modifyCurrentPayload(byte[] currentConfigPayload,
					Context context)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if (prefs.getBoolean(
			context.getString(R.string.preferenceKey_twintracconfig), false))
		{
			//channels and Temperature
			currentConfigPayload = saveChannelsToView(currentConfigPayload,
				context, prefs);

			//HR rate and temperature
			boolean hrRateActive = prefs.getBoolean(
				context.getString(R.string.pref_hr_calculation_key), false);
			if (hrRateActive)
			{
				byte tempChannelsOnline = (byte) ((currentConfigPayload[TEMP_CHANNELS_ONLINE_INDEX] | 0x04) & 0xFF);
				byte tempChannelsSDCard = (byte) ((currentConfigPayload[TEMP_CHANNELS_SDCARD_INDEX] | 0x04) & 0xFF);
				byte hrValue = (byte) ((currentConfigPayload[HEART_RATE_INDEX] | 0x01) & 0xFF);
				Log.d(
					logTag,
					"add HR value byte. before: "
									+ String.format("%02x, after = %02x",
										currentConfigPayload[HEART_RATE_INDEX],
										hrValue));
				currentConfigPayload[TEMP_CHANNELS_ONLINE_INDEX] = tempChannelsOnline;
				currentConfigPayload[TEMP_CHANNELS_SDCARD_INDEX] = tempChannelsSDCard;
				currentConfigPayload[HEART_RATE_INDEX] = hrValue;
			}
			else
			{
				byte hrValue = (byte) (((currentConfigPayload[HEART_RATE_INDEX] & 0xFE) | 0x00) & 0xFF);
				currentConfigPayload[HEART_RATE_INDEX] = hrValue;
			}

			//Online sampling rate
			byte onlineSamplingRateByteValue = Byte.valueOf(prefs.getString(
				context.getString(R.string.pref_online_sampling_key), "0"));
			byte onlineSamplingRate = (byte) (((currentConfigPayload[SAMPLING_RATE_ONLINE_INDEX] & 0xF0) | (onlineSamplingRateByteValue & 0x0F)) & 0xFF);
			Log.d(
				logTag,
				"online sampling rate pref="
								+ onlineSamplingRateByteValue
								+ ". before: "
								+ String.format(
									"%02x, after = %02x",
									currentConfigPayload[SAMPLING_RATE_ONLINE_INDEX],
									onlineSamplingRate));
			currentConfigPayload[SAMPLING_RATE_ONLINE_INDEX] = onlineSamplingRate;

			//SDCard sampling rate
			byte sdCardSamplingRateByteValue = Byte.valueOf(prefs.getString(
				context.getString(R.string.pref_sdcard_sampling_key), "0"));
			byte sdCardSamplingRate = (byte) (((currentConfigPayload[SAMPLING_RATE_SDCARD_INDEX] & 0xF0) | (sdCardSamplingRateByteValue & 0x0F)) & 0xFF);
			Log.d(
				logTag,
				"sdcard sampling rate pref="
								+ sdCardSamplingRateByteValue
								+ ". before: "
								+ String.format(
									"%02x, after = %02x",
									currentConfigPayload[SAMPLING_RATE_SDCARD_INDEX],
									sdCardSamplingRate));
			currentConfigPayload[SAMPLING_RATE_SDCARD_INDEX] = sdCardSamplingRate;

			//Recording time 
			int recordingTime = Integer.valueOf(prefs.getString(
				context.getString(R.string.pref_recording_time_key), "0"));
			int days = recordingTime / 24;
			int hours = recordingTime % 24;
			currentConfigPayload[RECORD_STANDARD_DAYS_INDEX] = (byte) (days & 0xFF);
			currentConfigPayload[RECORD_STANDARD_HOURS_INDEX] = (byte) (hours & 0xFF);
			Log.d(logTag, "duration. days: " + days + ", hours: " + hours);

			//Check bluetooth counter enabled or always
			boolean bluetoothAlwaysOn = prefs.getBoolean(
				context.getString(R.string.pref_bluetooth_on_counter_always_key),
				true);
			//Bluetooth on 1Bit ->1, Bluetooth counter 2Bit->0, BT always 2Bit ->1
			if (bluetoothAlwaysOn)
			{
				currentConfigPayload[BLUETOOTH_ON_ALWAYS_OR_COUNTER_INDEX] = (byte) ((currentConfigPayload[BLUETOOTH_ON_ALWAYS_OR_COUNTER_INDEX] | ((byte) 0x03)) & 0xFF);
			}
			else
			{
				currentConfigPayload[BLUETOOTH_ON_ALWAYS_OR_COUNTER_INDEX] = (byte) ((currentConfigPayload[BLUETOOTH_ON_ALWAYS_OR_COUNTER_INDEX] & ((byte) 0xFD)) & 0xFF);
				//Bluetooth On time
				int btOnValue = Integer.valueOf(prefs.getString(
					context.getString(R.string.pref_bluetooth_on_time_key), "0"));
				if (btOnValue > -1)
				{
					byte btOnValueByte = (byte) (btOnValue & (byte) 0xFF);
					if (btOnValue > 180)
					{
						btOnValue = 180;
					}
					currentConfigPayload[BLUETOOTH_TIME_INDEX] = btOnValueByte;
				}
			}

			//Recording mode
			int mode = Integer.valueOf(prefs.getString(
				context.getString(R.string.pref_recording_modus_key), "0"));
			if (mode == 0)
			{
				currentConfigPayload[RECORD_MODE_INDEX] = getPayloadForChannel(
					currentConfigPayload[RECORD_MODE_INDEX], false, 1);
			}
			else
			{
				currentConfigPayload[RECORD_MODE_INDEX] = getPayloadForChannel(
					currentConfigPayload[RECORD_MODE_INDEX], true, 1);
			}
			//Recording time event
			byte recordingTimeEvent = 0;
			if (mode == 1)
			{
				recordingTimeEvent = Byte.valueOf(prefs.getString(
					context.getString(R.string.pref_recording_modus_time_event_key),
					"30"));
			}
			currentConfigPayload[RECORD_EVENT_SECONDS_INDEX] = recordingTimeEvent;

			//Recording loop event
			byte recordingTimeLoop = 0;
			if (mode == 1)
			{
				recordingTimeLoop = Byte.valueOf(prefs.getString(
					context.getString(R.string.pref_recording_modus_time_loop_key),
					"0"));
			}
			currentConfigPayload[RECORD_LOOP_SECONDS_INDEX] = recordingTimeLoop;

			//USB mode
			byte usbMode = Byte.valueOf(prefs.getString(
				context.getString(R.string.pref_usb_mode_key), "0"));
			Log.i(logTag, "usbMode preferences = " + usbMode
							+ ", actual usbMode: "
							+ currentConfigPayload[USB_MODE_INDEX]);
			currentConfigPayload[USB_MODE_INDEX] = usbMode;

			//Softgain
			byte softgain = Byte.valueOf(prefs.getString(
				context.getString(R.string.pref_softgain_key), "2"));
			// Only one softgain is possible so set all relevant bits to zero
			currentConfigPayload[SOFTGAIN_INDEX] = (byte) (currentConfigPayload[SOFTGAIN_INDEX] & (byte) 0xE0);
			Log.i(
				logTag,
				"set bit position to one: "
								+ softgain
								+ ", value: "
								+ String.format("%02x",
									currentConfigPayload[SOFTGAIN_INDEX]));
			//set first bit to 1 for all softgain configs
			currentConfigPayload[SOFTGAIN_INDEX] = getPayloadForChannel(
				currentConfigPayload[SOFTGAIN_INDEX], true, 1);
			//add softgain bit to 1
			currentConfigPayload[SOFTGAIN_INDEX] = getPayloadForChannel(
				currentConfigPayload[SOFTGAIN_INDEX], true, softgain);
			Log.i(logTag, String.format("after setting %02x",
				currentConfigPayload[SOFTGAIN_INDEX]));

			//Download protection
			boolean downloadProtect = prefs.getBoolean(
				context.getString(R.string.pref_download_protect_key), true);
			currentConfigPayload[DOWNLOAD_PROTECTION_INDEX] = getPayloadForChannel(
				currentConfigPayload[DOWNLOAD_PROTECTION_INDEX],
				downloadProtect, 3);

			//Autostart
			byte autostart = Byte.valueOf(prefs.getString(
				context.getString(R.string.pref_autostart_key), "0"));
			//delete current setting and config new
			currentConfigPayload[AUTOSTART_INDEX] = 0x00;
			if (autostart > 0)
			{
				currentConfigPayload[AUTOSTART_INDEX] = getPayloadForChannel(
					currentConfigPayload[AUTOSTART_INDEX], true, autostart);
				//forth bit is "on time" so set time also
				if (autostart == 4)
				{
					byte autostartTimeValue = Byte.valueOf(prefs.getString(
						context.getString(R.string.pref_autostart_time_key),
						"0"));
					if (autostartTimeValue > -1)
					{
						if (autostartTimeValue > 180)
						{
							autostartTimeValue = (byte) 180;
						}
						currentConfigPayload[AUTOSTART_TIME_INDEX] = autostartTimeValue;
					}
				}
			}

		}
		return currentConfigPayload;
	}


	private byte[] saveChannelsToView(byte[] currentConfigPayload,
                                      Context context, SharedPreferences prefs)
	{
		Set<String> selectedChannelsOnline = prefs.getStringSet(
			context.getString(R.string.pref_channel_select_online_key), null);
		Set<String> selectedChannelsSdcard = prefs.getStringSet(
			context.getString(R.string.pref_channel_select_sdcard_key), null);

		if (selectedChannelsOnline != null)
		{
			//ECG channels
			currentConfigPayload[ECG_CHANNELS_SET_ONLINE_INDEX] = getPayloadForChannel(
				currentConfigPayload[ECG_CHANNELS_SET_ONLINE_INDEX],
				selectedChannelsOnline.contains(context.getString(R.string.pref_channels_select_ecg1)),
				1);

			currentConfigPayload[ECG_CHANNELS_SET_ONLINE_INDEX] = getPayloadForChannel(
				currentConfigPayload[ECG_CHANNELS_SET_ONLINE_INDEX],
				selectedChannelsOnline.contains(context.getString(R.string.pref_channels_select_ecg2)),
				2);
			Log.i(
				logTag,
				"online ecg channels value: "
								+ String.format(
									"%02x",
									currentConfigPayload[ECG_CHANNELS_SET_ONLINE_INDEX]));

			//Acc channels
			currentConfigPayload[ACCELEROMETER_CHANNELS_ONLINE_INDEX] = getPayloadForChannel(
				currentConfigPayload[ACCELEROMETER_CHANNELS_ONLINE_INDEX],
				selectedChannelsOnline.contains(context.getString(R.string.pref_channels_select_accx)),
				1);
			currentConfigPayload[ACCELEROMETER_CHANNELS_ONLINE_INDEX] = getPayloadForChannel(
				currentConfigPayload[ACCELEROMETER_CHANNELS_ONLINE_INDEX],
				selectedChannelsOnline.contains(context.getString(R.string.pref_channels_select_accy)),
				2);

			currentConfigPayload[ACCELEROMETER_CHANNELS_ONLINE_INDEX] = getPayloadForChannel(
				currentConfigPayload[ACCELEROMETER_CHANNELS_ONLINE_INDEX],
				selectedChannelsOnline.contains(context.getString(R.string.pref_channels_select_accz)),
				3);

			currentConfigPayload[ACCELEROMETER_CHANNELS_ONLINE_INDEX] = getPayloadForChannel(
				currentConfigPayload[ACCELEROMETER_CHANNELS_ONLINE_INDEX],
				selectedChannelsOnline.contains(context.getString(R.string.pref_channels_select_accl)),
				4);

			//Temp channels
			currentConfigPayload[TEMP_CHANNELS_ONLINE_INDEX] = getPayloadForChannel(
				currentConfigPayload[TEMP_CHANNELS_ONLINE_INDEX],
				selectedChannelsOnline.contains(context.getString(R.string.pref_channels_select_temp1)),
				1);

			currentConfigPayload[TEMP_CHANNELS_ONLINE_INDEX] = getPayloadForChannel(
				currentConfigPayload[TEMP_CHANNELS_ONLINE_INDEX],
				selectedChannelsOnline.contains(context.getString(R.string.pref_channels_select_temp2)),
				2);

			currentConfigPayload[TEMP_CHANNELS_ONLINE_INDEX] = getPayloadForChannel(
				currentConfigPayload[TEMP_CHANNELS_ONLINE_INDEX],
				selectedChannelsOnline.contains(context.getString(R.string.pref_channels_select_temp3)),
				3);

		}
		if (selectedChannelsSdcard != null)
		{
			//set flag in byte for sd card channels
			boolean sdCardChannlesActiv = false;
			if (selectedChannelsSdcard.size() > 0)
			{
				sdCardChannlesActiv = true;
			}
			currentConfigPayload[SD_CARD_IN_STORAGE_IF_ACTIVE_INDEX] = getPayloadForChannel(
				currentConfigPayload[SD_CARD_IN_STORAGE_IF_ACTIVE_INDEX],
				sdCardChannlesActiv, 1);

			//ECG channels
			currentConfigPayload[ECG_CHANNELS_SET_SDCARD_INDEX] = getPayloadForChannel(
				currentConfigPayload[ECG_CHANNELS_SET_SDCARD_INDEX],
				selectedChannelsSdcard.contains(context.getString(R.string.pref_channels_select_ecg1)),
				1);
			currentConfigPayload[ECG_CHANNELS_SET_SDCARD_INDEX] = getPayloadForChannel(
				currentConfigPayload[ECG_CHANNELS_SET_SDCARD_INDEX],
				selectedChannelsSdcard.contains(context.getString(R.string.pref_channels_select_ecg2)),
				2);
			Log.i(
				logTag,
				"SDCard ecg channels value: "
								+ String.format(
									"%02x",
									currentConfigPayload[ECG_CHANNELS_SET_SDCARD_INDEX]));

			//Acc channels
			currentConfigPayload[ACCELEROMETER_CHANNELS_SDCARD_INDEX] = getPayloadForChannel(
				currentConfigPayload[ACCELEROMETER_CHANNELS_SDCARD_INDEX],
				selectedChannelsSdcard.contains(context.getString(R.string.pref_channels_select_accx)),
				1);
			currentConfigPayload[ACCELEROMETER_CHANNELS_SDCARD_INDEX] = getPayloadForChannel(
				currentConfigPayload[ACCELEROMETER_CHANNELS_SDCARD_INDEX],
				selectedChannelsSdcard.contains(context.getString(R.string.pref_channels_select_accy)),
				2);
			currentConfigPayload[ACCELEROMETER_CHANNELS_SDCARD_INDEX] = getPayloadForChannel(
				currentConfigPayload[ACCELEROMETER_CHANNELS_SDCARD_INDEX],
				selectedChannelsSdcard.contains(context.getString(R.string.pref_channels_select_accz)),
				3);
			currentConfigPayload[ACCELEROMETER_CHANNELS_SDCARD_INDEX] = getPayloadForChannel(
				currentConfigPayload[ACCELEROMETER_CHANNELS_SDCARD_INDEX],
				selectedChannelsSdcard.contains(context.getString(R.string.pref_channels_select_accl)),
				4);

			//Temp channels
			currentConfigPayload[TEMP_CHANNELS_SDCARD_INDEX] = getPayloadForChannel(
				currentConfigPayload[TEMP_CHANNELS_SDCARD_INDEX],
				selectedChannelsSdcard.contains(context.getString(R.string.pref_channels_select_temp1)),
				1);
			currentConfigPayload[TEMP_CHANNELS_SDCARD_INDEX] = getPayloadForChannel(
				currentConfigPayload[TEMP_CHANNELS_SDCARD_INDEX],
				selectedChannelsSdcard.contains(context.getString(R.string.pref_channels_select_temp2)),
				2);
			currentConfigPayload[TEMP_CHANNELS_SDCARD_INDEX] = getPayloadForChannel(
				currentConfigPayload[TEMP_CHANNELS_SDCARD_INDEX],
				selectedChannelsSdcard.contains(context.getString(R.string.pref_channels_select_temp3)),
				3);
		}
		return currentConfigPayload;
	}
}
