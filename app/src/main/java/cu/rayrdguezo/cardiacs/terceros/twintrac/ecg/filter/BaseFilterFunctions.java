package cu.rayrdguezo.cardiacs.terceros.twintrac.ecg.filter;

/**
 * Filter-Rechen Funktionen von Ksenias C-Code.
 * 
 * @author gmelin
 * 
 */
public class BaseFilterFunctions
{

	static void mdefir1(int l, int iband, float fl, float fh, float fs,
					int iwindow, double b[], double w[], int ierror)
	{

		/*
		 * ----------------------------------------------------------------------
		 * 
		 * Subroutine mdesfir: To Design FIR Filter By Windowed Fourier Series.
		 * fl:low cut-off frequency. fh:high cut-off(For BP,BS). fl,fh,fs in Hz
		 * Digital filter coefficients are returned in b(l)
		 * h(z)=b(0)+b(1)z^(-1)+ ... +b(l-1)z^(-l+1)
		 * 
		 * Input parameters: l : the length of FIR filter. l must be odd. iband:
		 * iband=1 low pass FIR filter. iband=2 high pass FIR filter. iband=3
		 * band pass FIR filter. iband=4 band stop FIR filter. fln,fhn: the edge
		 * frequency desired,in normalized form,0.<=fln,fhn<=.5 |--- | --- | ---
		 * |-- -- | | | | | | | | | | | | | | | | | | | | --|------ -|--------
		 * -|----------- --|-------------- 0 fln 0 fln 0 fln fhn 0 fln fhn fhn=
		 * fhn= iband=1 LP iband=2 HP iband=3 BP iband=4 BS iwindow: window type
		 * desired. iwindow=1: rectangular window , =2: triangular window , =3:
		 * Math.coMath.sin window , =4: Hanning window , =5: Hamming window ,
		 * =6: Blackman window , =7: Papoulis window . w: l dimensioned real
		 * work array. Output parameters: b: l dimensioned real array.the result
		 * is in b(0) to b(l-1). ierror: ierror=0: no errors detected =1:
		 * invalid length (l<=0) =2: invalid window type =3: invalid filter type
		 * =4: invalid cut-off frequency in Chapter 8
		 * ----------------------------
		 * -------------------------------------------
		 */
		double fln, fhn;
		double s;
		double wc2 = 0;
		double wc1;
		int lim, i;
		double dly;
		fln = fl / fs;
		fhn = fh / fs;
		for (i = 0; i < l; i++)
		{
			b[i] = 0;
		}
		ierror = 0;
		dly = l / 2.;
		lim = l / 2;
		if (dly == lim)
		{
			ierror = 1;
		}
		if ((iwindow < 1) || (iwindow > 7))
		{
			ierror = 2;
		}
		if ((iband < 1) || (iband > 4))
		{
			ierror = 3;
		}
		if ((fln < 0.) || (fln > 0.5))
		{
			ierror = 4;
		}
		if ((iband >= 3) && (fln >= fhn))
		{
			ierror = 4;
		}
		if ((iband >= 3) && (fhn >= 0.5))
		{
			ierror = 4;
		}
		if (ierror != 0)
		{
			return;
		}
		dly -= .5;
		lim -= 1;
		wc1 = 2 * Math.PI * fln;
		if (iband >= 3)
		{
			wc2 = 2 * Math.PI * fhn;
		}
		mwindow(w, l, iwindow, ierror);
		switch (iband)
		{
		/* Low pass */
			case 1:
			{
				for (i = 0; i <= lim; i++)
				{
					s = i - dly;
					b[i] = ((Math.sin(wc1 * s)) / (Math.PI * s));
					b[i] = b[i] * w[i];
					b[l - 1 - i] = b[i];
				}
				b[(l - 1) / 2] = wc1 / Math.PI;
				return;
			}
			/* High pass */
			case 2:
			{
				for (i = 0; i <= lim; i++)
				{
					s = i - dly;
					b[i] = ((Math.sin(Math.PI * s) - Math.sin(wc1 * s)) / (Math.PI * s));
					b[i] = b[i] * w[i];
					b[l - 1 - i] = b[i];
				}
				b[(l - 1) / 2] = 1. - (wc1 / Math.PI);
				return;
			}
			/* band pass */
			case 3:
			{
				for (i = 0; i <= lim; i++)
				{
					s = i - dly;
					b[i] = ((Math.sin(wc2 * s) - Math.sin(wc1 * s)) / (Math.PI * s));
					b[i] = b[i] * w[i];
					b[l - 1 - i] = b[i];
				}
				b[(l - 1) / 2] = (wc2 - wc1) / Math.PI;
				return;
			}
			/* band stop */
			case 4:
			{
				for (i = 0; i <= lim; i++)
				{
					s = i - dly;
					b[i] = (((Math.sin(wc1 * s) + Math.sin(Math.PI * s)) - Math.sin(wc2
																					* s)) / (Math.PI * s));
					b[i] = b[i] * w[i];
					b[l - 1 - i] = b[i];
				}
				b[(l - 1) / 2] = ((wc1 + Math.PI) - wc2) / Math.PI;
				return;
			}
			default:
				break;
		}
	}


	static void mwindow(double w[], int n, int iwindow, int ierror)
	{

		/*
		 * ----------------------------------------------------------------------
		 * Routine mwindow: To Obtain Window Function. Input parameters: n : the
		 * length of window data. iwindow: window type desired. if : iwindow=1:
		 * rectangular window , =2: triangular window , =3: Math.coMath.sin
		 * window , =4: Hanning window , =5: Hamming window , =6: Blackman
		 * window , =7: Papoulis window . Output parameters: w: n dimension real
		 * array.the result is in w(0) to w(n-1). ierror:if ierror=0: no error,
		 * =1: Iwindow out of range. in Chapter 8
		 * --------------------------------------------------------------------
		 */
		double pn, a, b, c;
		int i;
		ierror = 1;
		if (iwindow < 1)
		{
			return;
		}
		ierror = 0;
		pn = (2. * Math.PI) / (n);
		switch (iwindow)
		{
			case 1:
			for (i = 0; i < n; i++)
			{
				w[i] = 1.0;
			}
				break;
			case 2:
			for (i = 0; i < n; i++)
			{
				w[i] = 1. - Math.abs(1. - ((2. * i) / (n)));
			}
				break;
			case 3:
			for (i = 0; i < n; i++)
			{
				w[i] = Math.sin((pn * i) / 2.);
			}
				break;
			case 4:
			for (i = 0; i < n; i++)
			{
				w[i] = 0.5 * (1.0 - Math.cos(pn * i));
			}
				break;
			case 5:
			for (i = 0; i < n; i++)
			{
				w[i] = 0.54 - (0.46 * Math.cos(pn * i));
			}
				break;
			case 6:
			for (i = 0; i < n; i++)
			{
				w[i] = (0.42 - (0.5 * Math.cos(pn * i)))
						+ (0.08 * Math.cos(2. * pn * i));
			}
				break;
			case 7:
			for (i = 0; i < n; i++)
			{
				a = Math.abs(Math.sin(pn * i)) / Math.PI;
				b = 1. - ((2. * (Math.abs(i - (n / 2.)))) / (n));
				c = Math.cos(pn * i);
				w[i] = a - (b * c);
			}
		}

	}

}
