package cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac;

import android.util.Log;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;

import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data.TwinTracDataCreator;
import cu.rayrdguezo.cardiacs.terceros.twintrac.cs.data.Patient;
import cu.rayrdguezo.cardiacs.terceros.twintrac.cs.data.Patient.Sex;

/**
 * Data to create payload to start and stop BT transmission.
 * 
 * for more information please look in "TD TwinTrac EN V1.8.pdf" page 15
 * 
 * @author Hakan Sahin
 * @date 08.06.2016 08:37:29 Hakan Sahin
 */
public class TwinTracPackagePatientWrite extends TwinTracData
{
	private static final String logTag = TwinTracPackagePatientWrite.class.getSimpleName();
	public static final byte PACKAGE_NUMBER = 0x06;

	/* Documentation index number staring with 0 */
	private final int PATIENT_ID_INDEX = 5;// byte donde inicia el id
	private final int patientIDLength = 14;//Tamanno en byte del campo id
	private final int PATIENT_FIRST_NAME_INDEX = 19;// byte donde inicia el nombre
	private final int PATIENT_LAST_NAME_INDEX = 39;// byte donde inicia el apellido
	private final int patientNameLength = 20;//Tamanno en byte de los campos nombre y apellido
	private final int PATIENT_GENDER_INDEX = 59;// byte donde inicia el genero (es de tamanno 1 byte)
	private final int PATIENT_DATE_OF_BIRTH_INDEX = 60;// byte donde inicia la fecha
	private final int patientDateLength = 8;//Tamanno en byte del campo fecha


	public TwinTracPackagePatientWrite(byte[] currentPatientDat,
                                       Patient currentPatient)
	{
		super(TwinTracDataCreator.WRITE_FILE_CMD, PACKAGE_NUMBER,
			currentPatientDat);
		currentPatientDat = modifyCurrentPayload(currentPatientDat,
			currentPatient);

		byte[] payloadArray = getPayloadFromData(currentPatientDat);
		setPayload(payloadArray);

		Log.d(
			logTag,
			"Patient Package: "
							+ TwinTracDataCreator.byteArrayToHex(getPacket()));
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
		byte[] fileName = new byte[] { (byte) 'P', (byte) 'A', (byte) 'T',
										(byte) 'I', (byte) 'E', (byte) 'N',
										(byte) 'T', (byte) '.', (byte) 'D',
										(byte) 'A', (byte) 'T', 0x00 };
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
					Patient currentPatient)
	{
		if (currentPatient != null)
		{
			currentConfigPayload = addIntoDatFile(currentConfigPayload,
				PATIENT_ID_INDEX, patientIDLength,
				currentPatient.getPatientID());
			currentConfigPayload = addIntoDatFile(currentConfigPayload,
				PATIENT_FIRST_NAME_INDEX, patientNameLength,
				currentPatient.getAddress().getFirstname());
			currentConfigPayload = addIntoDatFile(currentConfigPayload,
				PATIENT_LAST_NAME_INDEX, patientNameLength,
				currentPatient.getAddress().getLastname());
			currentConfigPayload[PATIENT_GENDER_INDEX] = currentPatient.getSex() == Sex.Female ? (byte) 0x02 : (byte) 0x01;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			currentConfigPayload = addIntoDatFile(currentConfigPayload,
				PATIENT_DATE_OF_BIRTH_INDEX, patientDateLength,
				sdf.format(currentPatient.getBirthdate()));
		}
		return currentConfigPayload;
	}

}
