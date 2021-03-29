package cu.rayrdguezo.cardiacs.terceros.twintrac.ecg.filter;

import cu.rayrdguezo.cardiacs.utiles.Constantes;

/**
 * Basis-Filter Klasse, welche die eigentlichen Filter Routinen Enthält.
 * 
 * @author gmelin
 * 
 */
public abstract class BaseFilter
{

	public static final int SAMPLE_RATE = 500;
	public static final int NOTCH50Hz_LENGTH_AVERAGE = 10;
	public static final int NOTCH50Hz_LENGTH_AVERAGE100 = 2;
	public static final int NOTCH50Hz_LENGTH_AVERAGE200 = 4;

	public static final int LENGTH_LP35Hz = 71;
	public static final int FILTER_TYPE_LP35Hz = 1; /* lowpass filter */
	public static final int WINDOW_TYPE_LP35Hz = 6; /* window type */
	public static final int FC_LP35Hz = 35; /*
											 * cutoff frequency of highpass
											 * filter
											 */
	public static final int LENGTH_BUFFER_LP35Hz = (LENGTH_LP35Hz) * 2;

	public static final int LENGTH_LP25Hz = 51;
	public static final int FILTER_TYPE_LP25Hz = 1; /* lowpass filter */
	public static final int WINDOW_TYPE_LP25Hz = 6; /* window type */
	public static final int FC_LP25Hz = 25; /*
											 * cutoff frequency of highpass
											 * filter
											 */
	public static final int LENGTH_BUFFER_LP25Hz = (LENGTH_LP25Hz) * 2;

	public static final double GAIN_005HZ = 0.000000098652211;
	public static final double GAIN_012HZ = 0.000000567883576;

	/*
	 * --------------------------------------------------------------------------
	 * --
	 */
	/* Lokale Funktionen und Modulglobale Variablen */
	/*
	 * --------------------------------------------------------------------------
	 * --
	 */

	public static final double N005HZ[] = { 1, 2, 1 };
	public static final double D005HZ[] = { 1, -1.999111423470795,
											0.999111818079638 };

	public static final double N012HZ[] = { 1, 2, 1 };

	public static final double D012HZ[] = { 1, -1.997867416996627,
											0.997869688530932 };
	/* Parameters for Notch 60Hz filter */
	double x1_Notch60Hz = 0, x2_Notch60Hz = 0;
	double y1_Notch60Hz = 0, y2_Notch60Hz = 0;
	/* Parameters for Notch 50Hz filter */
	short x_Notch50Hz[] = new short[NOTCH50Hz_LENGTH_AVERAGE];
	/* Parameters for 35Hz low-pass filter */
	short InitLP35Hz = 0;

	/* Parameters for 25Hz low-pass filter */

	double fLPCoeff_LP35Hz[] = new double[LENGTH_LP35Hz]; /*
														 * FIR Filter
														 * coefficients
														 */
	double fLPWindow_LP35Hz[] = new double[LENGTH_LP35Hz]; /*
															 * FIR Filter
															 * coefficients
															 */
	double x_LP35Hz[] = new double[LENGTH_BUFFER_LP35Hz];
	int n_LP35Hz = LENGTH_LP35Hz - 1;
	short InitLP25Hz = 0;

	/* Parameters for 0.05Hz LP filter */

	double fLPCoeff_LP25Hz[] = new double[LENGTH_LP25Hz]; /*
														 * FIR Filter
														 * coefficients
														 */
	double fLPWindow_LP25Hz[] = new double[LENGTH_LP25Hz]; /*
															 * FIR Filter
															 * coefficients
															 */
	double x_LP25Hz[] = new double[LENGTH_BUFFER_LP25Hz];
	int n_LP25Hz = LENGTH_LP25Hz - 1;

	/* Parameters for 0.12Hz LP filter */

	double x1_LP005Hz = 0, x2_LP005Hz = 0;
	double y1_LP005Hz = 0, y2_LP005Hz = 0;
	double x1_LP012Hz = 0, x2_LP012Hz = 0;
	double y1_LP012Hz = 0, y2_LP012Hz = 0;

	//Flag if a pacemaker value was detected
	private boolean pmd = false;


	public abstract int getOffset();


	public synchronized short newData(short oData)
	{

		if (oData == 0)
		{
			reset();
			return 0;
		}

		boolean pmd = ((oData & Constantes.PACEMAKER_DETECTED) == Constantes.PACEMAKER_DETECTED);

		oData = (short) (oData & 0x7fff);

		//		System.out.println ("Filtering value " + oData);
		oData = prepareValue(oData);
		//		System.out.print (" -> " + oData + " pmd " + pmd);
		oData = applyFilter(oData);
		oData = unPrepareValue(oData);
		//		System.out.println (" -> " + oData);
		oData = (short) (oData & 0x7fff);

		oData = (short) (oData | (pmd ? Constantes.PACEMAKER_DETECTED : 0));
		return oData;

	}


	/**
	 * This function realizes the bandpass filter(0.05Hz~25Hz) at 500 Hz sample
	 * rate.
	 */

	public short oBPfilter005_25Hz(short oData)
	{
		return (oFilterLP25Hz(oHP005HZ(oData)));
	}


	/**
	 * This function realizes the bandpass filter(0.05Hz~35Hz) at 500 Hz sample
	 * rate.
	 */

	public short oBPfilter005_35Hz(short oData)
	{
		return (oFilterLP35Hz(oHP005HZ(oData)));
	}


	/**
	 * This function realizes the bandpass filter(0.12Hz~25Hz) at 500 Hz sample
	 * rate.
	 */

	public short oBPfilter012_25Hz(short oData)
	{

		return (oFilterLP25Hz(oHP012HZ(oData)));

	}


	/**
	 * This function realizes the bandpass filter(0.012Hz~35Hz) at 500 Hz sample
	 * rate.
	 */

	public short oBPfilter012_35Hz(short oData)
	{

		return (oFilterLP35Hz(oHP012HZ(oData)));

	}


	/**
	 * This function is high pass filter that subtract the signal components
	 * below 0.05 Hz from the raw signal.
	 */

	public short oHP005HZ(short oData)
	{

		return (short) (oData - oLP005HZ(oData));

	}


	/**
	 * This function is a low pass filter that estimates the signal components
	 * below 0.05Hz.
	 */
	public short oLP005HZ(short oData)
	{

		double x_LP005Hz = oData;
		double y_LP005Hz = 0;

		y_LP005Hz = (x_LP005Hz + (N005HZ[1] * x1_LP005Hz) + (N005HZ[2] * x2_LP005Hz))
					- (D005HZ[1] * y1_LP005Hz) - (D005HZ[2] * y2_LP005Hz);

		x2_LP005Hz = x1_LP005Hz;
		x1_LP005Hz = x_LP005Hz;
		y2_LP005Hz = y1_LP005Hz;
		y1_LP005Hz = y_LP005Hz;

		return ((short) (y_LP005Hz * GAIN_005HZ));

	}


	/**
	 * This function realizes the 50Hz Notch filter at 500 Hz sample rate.
	 */

	public short oNotch50Hz(short oData)
	{

		int y_Notch50Hz = 0;
		int i;

		x_Notch50Hz[0] = oData;

		for (i = 0; i < NOTCH50Hz_LENGTH_AVERAGE; i++)
		{
			y_Notch50Hz += x_Notch50Hz[i];
		}

		for (i = NOTCH50Hz_LENGTH_AVERAGE - 1; i > 0; i--)
		{
			x_Notch50Hz[i] = x_Notch50Hz[i - 1];
		}

		return ((short) (y_Notch50Hz / NOTCH50Hz_LENGTH_AVERAGE));

	}


	/**
	 * This function realizes the 60Hz Notch filter at 500 Hz sample rate. The
	 * difference equation of the 60 Hz Notch filter at 500 Hz sample rate. y(t)
	 * = x(t) - 1.4579 x(t-1) + x(t-2) + 1.38504 y(t-1) - 0.9025 y(t-2)
	 */

	public short oNotch60Hz(short oData)
	{

		double x_Notch60Hz = oData;
		double y_Notch60Hz = 0;

		y_Notch60Hz = ((x_Notch60Hz - (1.4579 * x1_Notch60Hz)) + x2_Notch60Hz + (1.38504 * y1_Notch60Hz))
						- (0.9025 * y2_Notch60Hz);

		x2_Notch60Hz = x1_Notch60Hz;
		x1_Notch60Hz = x_Notch60Hz;
		y2_Notch60Hz = y1_Notch60Hz;
		y1_Notch60Hz = y_Notch60Hz;

		return ((short) y_Notch60Hz);

	}


	public void reset()
	{

		/* Parameters for Notch 60Hz filter */
		x1_Notch60Hz = 0;
		x2_Notch60Hz = 0;
		y1_Notch60Hz = 0;
		y2_Notch60Hz = 0;

		/* Parameters for Notch 50Hz filter */
		x_Notch50Hz = new short[NOTCH50Hz_LENGTH_AVERAGE];

		/* Parameters for 35Hz low-pass filter */
		InitLP35Hz = 0;
		fLPCoeff_LP35Hz = new double[LENGTH_LP35Hz]; /* FIR Filter coefficients */
		fLPWindow_LP35Hz = new double[LENGTH_LP35Hz]; /* FIR Filter coefficients */
		x_LP35Hz = new double[LENGTH_BUFFER_LP35Hz];
		n_LP35Hz = LENGTH_LP35Hz - 1;

		/* Parameters for 25Hz low-pass filter */

		InitLP25Hz = 0;
		fLPCoeff_LP25Hz = new double[LENGTH_LP25Hz]; /* FIR Filter coefficients */
		fLPWindow_LP25Hz = new double[LENGTH_LP25Hz]; /* FIR Filter coefficients */
		x_LP25Hz = new double[LENGTH_BUFFER_LP25Hz];
		n_LP25Hz = LENGTH_LP25Hz - 1;

		/* Parameters for 0.05Hz LP filter */

		x1_LP005Hz = 0;
		x2_LP005Hz = 0;
		y1_LP005Hz = 0;
		y2_LP005Hz = 0;

		/* Parameters for 0.12Hz LP filter */

		x1_LP012Hz = 0;
		x2_LP012Hz = 0;
		y1_LP012Hz = 0;
		y2_LP012Hz = 0;

		//Flag if a pacemaker value was detected
		pmd = false;

	}


	public void setDataFreq(int f)
	{
	}


	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}


	abstract short applyFilter(short s);


	/**
	 * This function is low pass filter with cutoff frequency 25Hz.
	 */

	short oFilterLP25Hz(short oData)
	{

		short i;
		short error = 0;
		double y_LP25Hz = 0;
		int FH_LP25Hz = ((SAMPLE_RATE / 2) - 1);

		/*
		 * --- Calculation of the lowpass filter coefficients on the first
		 * sample(InitLP25Hz==0). These filter coefficients are stored in
		 * fLPCoeff_LP25Hz[]. then filters are performed on the following signal
		 * (InitLP25Hz!=0) -----
		 */

		if (InitLP25Hz == 0)
		{
			BaseFilterFunctions.mdefir1(LENGTH_LP25Hz, FILTER_TYPE_LP25Hz,
				FC_LP25Hz, FH_LP25Hz, SAMPLE_RATE, WINDOW_TYPE_LP25Hz,
				fLPCoeff_LP25Hz, fLPWindow_LP25Hz, error);
			InitLP25Hz = 1;
		}
		else
		{

			x_LP25Hz[n_LP25Hz] = oData;
			x_LP25Hz[n_LP25Hz + LENGTH_LP25Hz] = oData;
			for (i = 0; i < LENGTH_LP25Hz; i++)
			{
				y_LP25Hz = (x_LP25Hz[n_LP25Hz + i] * fLPCoeff_LP25Hz[i])
							+ y_LP25Hz;
			}

			if (--n_LP25Hz < 0)
			{
				n_LP25Hz = LENGTH_LP25Hz - 1;
			}

		}

		return ((short) y_LP25Hz);

		/*
		 * ----------------------------------------------------------------------
		 * --#
		 */

	}


	/**
	 * This function is low pass filter with cutoff frequency 35Hz.
	 */

	short oFilterLP35Hz(short oData)
	{

		short i;
		short error = 0;
		double y_LP35Hz = 0;

		int FH_LP35Hz = ((SAMPLE_RATE / 2) - 1);

		/*
		 * --- Calculation of the lowpass filter coefficients on the first
		 * sample(InitLP35Hz==0). These filter coefficients are stored in
		 * fLPCoeff_LP35Hz[]. then filters are performed on the following signal
		 * (InitLP35Hz!=0) -----
		 */

		if (InitLP35Hz == 0)
		{
			BaseFilterFunctions.mdefir1(LENGTH_LP35Hz, FILTER_TYPE_LP35Hz,
				FC_LP35Hz, FH_LP35Hz, SAMPLE_RATE, WINDOW_TYPE_LP35Hz,
				fLPCoeff_LP35Hz, fLPWindow_LP35Hz, error);
			InitLP35Hz = 1;
		}
		else
		{

			x_LP35Hz[n_LP35Hz] = oData;
			x_LP35Hz[n_LP35Hz + LENGTH_LP35Hz] = oData;
			for (i = 0; i < LENGTH_LP35Hz; i++)
			{
				y_LP35Hz = (x_LP35Hz[n_LP35Hz + i] * fLPCoeff_LP35Hz[i])
							+ y_LP35Hz;
			}

			if (--n_LP35Hz < 0)
			{
				n_LP35Hz = LENGTH_LP35Hz - 1;
			}

		}

		return ((short) y_LP35Hz);

		/*
		 * ----------------------------------------------------------------------
		 * --#
		 */

	}


	/**
	 * This function is high pass filter that subtract the signal components
	 * below 0.12 Hz from the raw signal.
	 */
	short oHP012HZ(short oData)
	{
		return (short) (oData - oLP012HZ(oData));
	}


	/**
	 * This function is a low pass filter that estimates the signal components
	 * below 0.12Hz.
	 */
	short oLP012HZ(short oData)
	{

		double x_LP012Hz = oData;
		double y_LP012Hz = 0;

		y_LP012Hz = (x_LP012Hz + (N012HZ[1] * x1_LP012Hz) + (N012HZ[2] * x2_LP012Hz))
					- (D012HZ[1] * y1_LP012Hz) - (D012HZ[2] * y2_LP012Hz);

		x2_LP012Hz = x1_LP012Hz;
		x1_LP012Hz = x_LP012Hz;
		y2_LP012Hz = y1_LP012Hz;
		y1_LP012Hz = y_LP012Hz;

		return ((short) (y_LP012Hz * GAIN_012HZ));

	}


	protected short prepareValue(short v)
	{
		if (v == 0)
		{
			return 0;
		}

		pmd = (v & Constantes.PACEMAKER_DETECTED) == Constantes.PACEMAKER_DETECTED;
		v &= 0x7fff;
		v -= 16384;

		return v;
	}


	protected short unPrepareValue(short v)
	{

		v += 16384;
		if (pmd)
		{
			v |= Constantes.PACEMAKER_DETECTED;
		}
		return v;

	}

}
