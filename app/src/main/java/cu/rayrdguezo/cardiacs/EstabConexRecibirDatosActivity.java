package cu.rayrdguezo.cardiacs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cu.rayrdguezo.cardiacs.terceros.twintrac.BtReceiverStateListener;
import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.BCTarget;
import cu.rayrdguezo.cardiacs.terceros.twintrac.bluecor.data.Data;
import cu.rayrdguezo.cardiacs.terceros.twintrac.cs.BTReceiver;
import cu.rayrdguezo.cardiacs.terceros.twintrac.cs.data.DatabaseHelper;
import cu.rayrdguezo.cardiacs.terceros.twintrac.cs.data.Patient;
import cu.rayrdguezo.cardiacs.terceros.twintrac.cs.preference.SettingActivity;
import cu.rayrdguezo.cardiacs.terceros.twintrac.ecg.filter.BaseFilter;
import cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac.TwinTracBtReceiver;
import cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac.TwinTracConfigManager;
import cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac.TwinTracOnlineDataHolder;
import cu.rayrdguezo.cardiacs.utiles.MyProgressDialog;
import cu.rayrdguezo.cardiacs.terceros.twintrac.twintrac.TwinTracBtReceiver.TwinTracDataListener;

public class EstabConexRecibirDatosActivity extends OrmLiteBaseActivity<DatabaseHelper> implements BCTarget, View.OnClickListener, TwinTracDataListener, BtReceiverStateListener {

    public static class BT12AxisView extends View
    {
        protected static float hScale, vScale;
        private final Paint paint;


        public BT12AxisView(Context context, AttributeSet attrs)
        {
            super(context, attrs);
            setBackgroundColor(Color.WHITE);
            paint = new Paint();
            paint.setAntiAlias(true);
        }


        protected static void reloadDefaults()
        {
            hScale = new Integer(prefs.getString("mmsec", "10")).intValue();
            vScale = new Integer(prefs.getString("mmmV", "5")).intValue();
        }


        /*
         * (non-Javadoc)
         *
         * @see android.view.View#onDraw(android.graphics.Canvas)
         */
        @Override
        protected void onDraw(Canvas canvas)
        {
            if ((frame_height == 0) || (grid == 0))
            {
                // Set view height by the actual device resolution
                frame_height = getBottom();
                grid = (float) frame_height / 54;
                gv.requestLayout();
            }
            paint.setTextSize(FONT_SIZE * density);
            float width = getRight();
            reloadDefaults();
            float[] line = new float[4];
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(1f);
            float w = 0, y = 0;

            w = width / 8;
            y = frame_height - (25 * density);
            // Scale
            String s = (int) vScale + " [mV]";
            canvas.drawText(s, 12 * density, y + (11 * density), paint);
            s = (int) hScale + " sec";
            canvas.drawText(s, 12 * density, y + (22 * density), paint);
            // Channel labels
            if (EstabConexRecibirDatosActivity.channelsShowing().size() != 0)
            {
                float topMargin = 15.0f * density;
                float bottomMargin = 15.0f * density;
                float signalHeight = (frame_height - topMargin - bottomMargin)
                        / EstabConexRecibirDatosActivity.channelsShowing().size();
                for (int i = 0; i < EstabConexRecibirDatosActivity.channelsShowing().size(); i++)
                {
                    float j = (topMargin + (i * signalHeight) + (signalHeight / 2)) - 1;
                    RectF rect = new RectF(width / 2, j - (12 * density),
                            width, j + (3 * density));
                    paint.setColor(Color.LTGRAY);
                    canvas.drawRoundRect(rect, 3 * density, 3 * density, paint);
                    // Text
                    String label = EstabConexRecibirDatosActivity.channelsShowing().get(
                            i);
                    paint.setColor(Color.BLACK);
                    canvas.drawText(label, (width / 2) + (3 * density),
                            j - (1 * density), paint);
                }
            }

            // Rectangle wave
            line[0] = w / 2;
            line[1] = y;
            line[2] = (w / 2) + w;
            line[3] = y;
            canvas.drawLines(line, paint);
            line[0] = (w / 2) + w;
            line[1] = y;
            line[2] = (w / 2) + w;
            line[3] = y - (5 * vScale * density);
            canvas.drawLines(line, paint);
            line[0] = (w / 2) + w;
            line[1] = y - (5 * vScale * density);
            line[2] = (w / 2) + w + w;
            line[3] = y - (5 * vScale * density);
            canvas.drawLines(line, paint);
            line[0] = (w / 2) + w + w;
            line[1] = y - (5 * vScale * density);
            line[2] = (w / 2) + w + w;
            line[3] = y;
            canvas.drawLines(line, paint);
            line[0] = (w / 2) + w + w;
            line[1] = y;
            line[2] = (w / 2) + w + w + w;
            line[3] = y;
            canvas.drawLines(line, paint);

        }
    }

    public class DisconnectAutomaticPainter implements Runnable
    {
        private boolean continuePaint;
        private boolean applicationFinished = false;


        public void finishApplication()
        {
            this.continuePaint = false;
            this.applicationFinished = true;
        }


        public boolean isApplicationFinished()
        {
            return applicationFinished;
        }


        public boolean isPaintRunning()
        {
            return continuePaint;
        }


        @Override
        public void run()
        {
            continuePaint = true;
            while (continuePaint)
            {
                try
                {
                    Thread.sleep(500);
                    autoFillDataDuringDisconnection(0.5f);
                }
                catch (InterruptedException e)
                {
                }
            }

        }


        public void stopPainting()
        {
            continuePaint = false;
        }

    }

    protected class GraphView extends View
    {
        protected final int lightGray = Color.rgb(210, 210, 210);
        protected final int midGray = Color.rgb(168, 168, 168);
        protected float hScale, vScale;
        protected float width;
        //		protected Bitmap bitmap;
        //		protected Canvas canvas;
        protected float ecg_height;
        protected Paint paint;


        public GraphView(Context context)
        {
            super(context);
            setBackgroundColor(Color.WHITE);
            paint = new Paint();
            paint.setAntiAlias(true);
        }


        /*
         * Calculates ECG-Channels from transmitted Information. The following
         * calculations are done:
         *
         * Channel II, III, V1, V2, V3, V4, V5, V6 are transmitted Channel I,
         * aVR, aVL, aVF are calculated.
         *
         * I = II - III aVR = (III / 2) - II aVL = (II / 2) - III aVF = (II +
         * III) / 2
         *
         * @see android.view.View#onDraw(android.graphics.Canvas)
         */
        @Override
        protected void onDraw(Canvas canvas)
        {
            //			if (canvas == null)
            //			{
            //				Bitmap bitmap = Bitmap.createBitmap(canvas2.getWidth(),
            //					canvas2.getHeight(), Bitmap.Config.ARGB_8888);
            //				canvas = new Canvas(bitmap);
            //			}
            //			else
            //			{
            //				canvas.drawColor(Color.TRANSPARENT,
            //					android.graphics.PorterDuff.Mode.CLEAR);
            //			}
            paint.setTextSize(FONT_SIZE * density);
            Rect rect = canvas.getClipBounds();
            float[] line = new float[4];
            ecg_height = grid * 51;

            // Grid
            for (float j = (float) Math.ceil(rect.top / grid) * grid; j <= rect.bottom; j += grid)
            {
                line[0] = rect.left;
                line[1] = j;
                line[2] = rect.right;
                line[3] = j;
                int n = (int) ((j / grid) + 0.5) - 1;
                if ((n % 5) != 0)
                {
                    paint.setColor(lightGray);
                    canvas.drawLines(line, paint);
                }
            }
            for (float i = (float) Math.ceil(rect.left / grid) * grid; i <= rect.right; i += grid)
            {
                line[0] = i;
                line[1] = rect.top;
                line[2] = i;
                line[3] = rect.bottom;
                int n = (int) ((i / grid) + 0.5);
                if ((n % 5) == 0)
                {
                    paint.setColor(midGray);
                }
                else
                {
                    paint.setColor(lightGray);
                }
                canvas.drawLines(line, paint);
            }
            for (float j = (float) Math.ceil(rect.top / grid) * grid; j <= rect.bottom; j += grid)
            {
                line[0] = rect.left;
                line[1] = j;
                line[2] = rect.right;
                line[3] = j;
                int n = (int) ((j / grid) + 0.5) - 1;
                if ((n % 5) == 0)
                {
                    paint.setColor(midGray);
                    canvas.drawLines(line, paint);
                }
            }

            // Plot
            float height = 50 * grid;
            float topMargin = 15.0f * density;
            float signalHeight = (height - topMargin)
                    / channelsShowing().size();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            Path path = null;
            int s = 0;

            int page = (int) (((width * freq) / hScale) + 0.5) * nChannels;

            int ptr = 0;
            /*if (device == PocketEcg)
            {
                float time = Calendar.getInstance().getTimeInMillis()
                        - startTime;
                ptr = (int) ((time / 1000) * freq * nChannels);
                if (ptr > pointer)
                {
                    ptr = pointer;
                }
            }
            else
            {*/
                ptr = pointer;
            //}
            int nPages = 0;
            if (page != 0)
            {
                nPages = ptr / page;
            }
            int pageIndex = page * nPages;
            int pageIndexLast = (page * (nPages - 1));
            //			Log.d(logTag, "pageIndex=" + pageIndex + ", page=" + page
            //							+ ", nPages=" + nPages + ", lastIndexPos: "
            //							+ pageIndexLast);

            if (I == 1)
            {
                // I = II - III
                path = new Path();
                for (int i = 0; i < (rect.right - (UPD_MARGIN * density)); i++)
                {
                    int index = pageIndex
                            + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                    if (index > pointer)
                    {
                        index = pageIndexLast
                                + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                        if (index < 0)
                        {
                            //							Log.i(logTag, "index is out of bounds: " + index);
                            break;
                        }
                    }
                    float I = 0;
                    /*if (device == SRMed)
                    {
                        float II = ecgValues[index];
                        float III = ecgValues[index + 1];
                        I = II - III;
                    }
                    else if (device == PocketEcg)
                    {
                        I = ecgValues[index];
                    }
                    else*/ if (device == TwinTrac)
                    {
                        float II = ecgValues[index];
                        float III = ecgValues[index + 1];
                        I = II - III;
                        //I = ecgValues[index];
                    }
                    if (i == 0)
                    {
                        path.moveTo(i, topMargin + (s * signalHeight)
                                + (signalHeight / 2));
                    }
                    else
                    {
                        path.lineTo(
                                i,
                                (topMargin + (s * signalHeight) + (signalHeight / 2))
                                        - (I * vScale));
                    }
                }
                canvas.drawPath(path, paint);
                s++;
            }
            if (II == 1)
            {
                // II
                path = new Path();
                for (int i = 0; i < (rect.right - (UPD_MARGIN * density)); i++)
                {
                    int index = pageIndex
                            + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                    if (index > pointer)
                    {
                        index = pageIndexLast
                                + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                        if (index < 0)
                        {
                            break;
                        }
                    }
                    float II = 0;
                    /*if (device == SRMed)
                    {
                        II = ecgValues[index];
                    }
                    else if (device == PocketEcg)
                    {
                        II = ecgValues[index + 1];
                    }
                    else*/ if (device == TwinTrac)
                    {
                        II = ecgValues[index];
                        //						II = ecgValues[index + 1];
                    }
                    if (i == 0)
                    {
                        path.moveTo(i, topMargin + (s * signalHeight)
                                + (signalHeight / 2));
                    }
                    else
                    {
                        path.lineTo(
                                i,
                                (topMargin + (s * signalHeight) + (signalHeight / 2))
                                        - (II * vScale));
                    }
                }
                canvas.drawPath(path, paint);
                s++;
            }
            if (III == 1)
            {
                // III
                path = new Path();
                for (int i = 0; i < (rect.right - (UPD_MARGIN * density)); i++)
                {
                    int index = pageIndex
                            + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                    if (index > pointer)
                    {
                        index = pageIndexLast
                                + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                        if (index < 0)
                        {
                            break;
                        }
                    }
                    float III = 0;
                    /*if (device == SRMed)
                    {
                        III = ecgValues[index + 1];
                    }
                    else if (device == PocketEcg)
                    {
                        float I = ecgValues[index];
                        float II = ecgValues[index + 1];
                        III = II - I;
                    }
                    else*/ if (device == TwinTrac)
                    {
                        III = ecgValues[index + 1];
                        //						float I = ecgValues[index];
                        //						float II = ecgValues[index + 1];
                        //						III = II - I;
                    }
                    if (i == 0)
                    {
                        path.moveTo(i, topMargin + (s * signalHeight)
                                + (signalHeight / 2));
                    }
                    else
                    {
                        path.lineTo(
                                i,
                                (topMargin + (s * signalHeight) + (signalHeight / 2))
                                        - (III * vScale));
                    }
                }
                canvas.drawPath(path, paint);
                s++;
            }
            if (aVR == 1)
            {
                // aVR = (III / 2) - II
                path = new Path();
                for (int i = 0; i < (rect.right - (UPD_MARGIN * density)); i++)
                {
                    int index = pageIndex
                            + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                    if (index > pointer)
                    {
                        index = pageIndexLast
                                + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                        if (index < 0)
                        {
                            break;
                        }
                    }
                    float II = ecgValues[index];
                    float III = ecgValues[index + 1];
                    if (i == 0)
                    {
                        path.moveTo(i, topMargin + (s * signalHeight)
                                + (signalHeight / 2));
                    }
                    else
                    {
                        path.lineTo(
                                i,
                                (topMargin + (s * signalHeight) + (signalHeight / 2))
                                        - (((III / 2) - II) * vScale));
                    }
                }
                canvas.drawPath(path, paint);
                s++;
            }
            if (aVL == 1)
            {
                // aVL = (II / 2) - III
                path = new Path();
                for (int i = 0; i < (rect.right - (UPD_MARGIN * density)); i++)
                {
                    int index = pageIndex
                            + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                    if (index > pointer)
                    {
                        index = pageIndexLast
                                + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                        if (index < 0)
                        {
                            break;
                        }
                    }
                    float II = ecgValues[index];
                    float III = ecgValues[index + 1];
                    if (i == 0)
                    {
                        path.moveTo(i, topMargin + (s * signalHeight)
                                + (signalHeight / 2));
                    }
                    else
                    {
                        path.lineTo(
                                i,
                                (topMargin + (s * signalHeight) + (signalHeight / 2))
                                        - (((II / 2) - III) * vScale));
                    }
                }
                canvas.drawPath(path, paint);
                s++;
            }
            if (aVF == 1)
            {
                // aVF = (II + III) / 2
                path = new Path();
                for (int i = 0; i < (rect.right - (UPD_MARGIN * density)); i++)
                {
                    int index = pageIndex
                            + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                    if (index > pointer)
                    {
                        index = pageIndexLast
                                + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                        if (index < 0)
                        {
                            break;
                        }
                    }
                    float II = ecgValues[index];
                    float III = ecgValues[index + 1];
                    if (i == 0)
                    {
                        path.moveTo(i, topMargin + (s * signalHeight)
                                + (signalHeight / 2));
                    }
                    else
                    {
                        path.lineTo(
                                i,
                                (topMargin + (s * signalHeight) + (signalHeight / 2))
                                        - (((II / 2) + (III / 2)) * vScale));
                    }
                }
                canvas.drawPath(path, paint);
                s++;
            }
            if (V1 == 1)
            {
                // V1
                path = new Path();
                for (int i = 0; i < (rect.right - (UPD_MARGIN * density)); i++)
                {
                    int index = pageIndex
                            + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                    if (index > pointer)
                    {
                        index = pageIndexLast
                                + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                        if (index < 0)
                        {
                            break;
                        }
                    }
                    float V = ecgValues[index + 2];
                    if (i == 0)
                    {
                        path.moveTo(i, topMargin + (s * signalHeight)
                                + (signalHeight / 2));
                    }
                    else
                    {
                        path.lineTo(
                                i,
                                (topMargin + (s * signalHeight) + (signalHeight / 2))
                                        - (V * vScale));
                    }
                }
                canvas.drawPath(path, paint);
                s++;
            }
            if (V2 == 1)
            {
                // V2
                path = new Path();
                for (int i = 0; i < (rect.right - (UPD_MARGIN * density)); i++)
                {
                    int index = pageIndex
                            + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                    if (index > pointer)
                    {
                        index = pageIndexLast
                                + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                        if (index < 0)
                        {
                            break;
                        }
                    }
                    float V = ecgValues[index + 3];
                    if (i == 0)
                    {
                        path.moveTo(i, topMargin + (s * signalHeight)
                                + (signalHeight / 2));
                    }
                    else
                    {
                        path.lineTo(
                                i,
                                (topMargin + (s * signalHeight) + (signalHeight / 2))
                                        - (V * vScale));
                    }
                }
                canvas.drawPath(path, paint);
                s++;
            }
            if (V3 == 1)
            {
                // V3
                path = new Path();
                for (int i = 0; i < (rect.right - (UPD_MARGIN * density)); i++)
                {
                    int index = pageIndex
                            + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                    if (index > pointer)
                    {
                        index = pageIndexLast
                                + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                        if (index < 0)
                        {
                            break;
                        }
                    }
                    float V = ecgValues[index + 4];
                    if (i == 0)
                    {
                        path.moveTo(i, topMargin + (s * signalHeight)
                                + (signalHeight / 2));
                    }
                    else
                    {
                        path.lineTo(
                                i,
                                (topMargin + (s * signalHeight) + (signalHeight / 2))
                                        - (V * vScale));
                    }
                }
                canvas.drawPath(path, paint);
                s++;
            }
            if (V4 == 1)
            {
                // V4
                path = new Path();
                for (int i = 0; i < (rect.right - (UPD_MARGIN * density)); i++)
                {
                    int index = pageIndex
                            + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                    if (index > pointer)
                    {
                        index = pageIndexLast
                                + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                        if (index < 0)
                        {
                            break;
                        }
                    }
                    float V = ecgValues[index + 5];
                    if (i == 0)
                    {
                        path.moveTo(i, topMargin + (s * signalHeight)
                                + (signalHeight / 2));
                    }
                    else
                    {
                        path.lineTo(
                                i,
                                (topMargin + (s * signalHeight) + (signalHeight / 2))
                                        - (V * vScale));
                    }
                }
                canvas.drawPath(path, paint);
                s++;
            }
            if (V5 == 1)
            {
                // V5
                path = new Path();
                for (int i = 0; i < (rect.right - (UPD_MARGIN * density)); i++)
                {
                    int index = pageIndex
                            + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                    if (index > pointer)
                    {
                        index = pageIndexLast
                                + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                        if (index < 0)
                        {
                            break;
                        }
                    }
                    float V = ecgValues[index + 6];
                    if (i == 0)
                    {
                        path.moveTo(i, topMargin + (s * signalHeight)
                                + (signalHeight / 2));
                    }
                    else
                    {
                        path.lineTo(
                                i,
                                (topMargin + (s * signalHeight) + (signalHeight / 2))
                                        - (V * vScale));
                    }
                }
                canvas.drawPath(path, paint);
                s++;
            }
            if (V6 == 1)
            {
                // V6
                path = new Path();
                for (int i = 0; i < (rect.right - (UPD_MARGIN * density)); i++)
                {
                    int index = pageIndex
                            + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                    if (index > pointer)
                    {
                        index = pageIndexLast
                                + ((int) (((i * freq) / hScale) + 0.5) * nChannels);
                        if (index < 0)
                        {
                            break;
                        }
                    }
                    float V = ecgValues[index + 7];
                    if (i == 0)
                    {
                        path.moveTo(i, topMargin + (s * signalHeight)
                                + (signalHeight / 2));
                    }
                    else
                    {
                        path.lineTo(
                                i,
                                (topMargin + (s * signalHeight) + (signalHeight / 2))
                                        - (V * vScale));
                    }
                }
                canvas.drawPath(path, paint);
                s++;
            }

            // White update region on the right
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawRect(rect.right - (UPD_MARGIN * density), 0, rect.right,
                    ecg_height, paint);

            // White region at top & bottom
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawRect(rect.left, 0, rect.right, grid, paint);
            canvas.drawRect(rect.left, ecg_height, rect.right, frame_height,
                    paint);

            Log.i(logTag, "ECG graph painted ");

        }


        /*
         * (non-Javadoc)
         *
         * @see android.view.View#onMeasure(int, int)
         */
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
        {
            if (grid == 0)
            {
                setMeasuredDimension(0, 0);
                return;
            }
            reloadDefaults();
            Display display = getWindowManager().getDefaultDisplay();
            View v = EstabConexRecibirDatosActivity.this.findViewById(R.id.layoutGraphView);
            //width -= density * grid;//11 * grid;
            //width = (((int) (width + 0.5f) / (int) hScale) * (int) hScale);
            Point dimensions = new Point();
            display.getSize(dimensions);
            width = dimensions.x > v.getWidth() ? dimensions.x * 0.7f : v.getWidth() * 0.7f;//display.getWidth();
            Log.i(logTag, "OnMeasure change width: " + width
                    + ", display width: " + dimensions.x
                    + ", v.getWidth: " + v.getWidth() + ", height: "
                    + dimensions.y + ", " + v.getHeight());
            setMeasuredDimension((int) width, dimensions.y);
        }


        protected void reloadDefaults()
        {
            String mmsec = "10";
            String mmmV = "5";
            if (prefs != null)
            {
                mmsec = prefs.getString("mmsec", "10");
                mmmV = prefs.getString("mmmV", "5");
            }
            hScale = new Integer(mmsec).intValue() * grid;
            vScale = new Integer(mmmV).intValue() * grid;
        }
    }

    private MyProgressDialog myProgressDialog;

    protected HorizontalScrollView scroll;
    //private BTReceiver btReceiver;
    //private PocketEcgReceiver pocketReceiver;
    private TwinTracBtReceiver twinTracReceiver;
    private int freq, dMax, dMin, nChannels;
    private float pMin, pMax;
    private float[] ecgValues;
    private byte[] incomingData;
    private int pointer;

    private long startTimeConnectionLost = Long.MIN_VALUE;

    private enum Mode
    {
        THREE_CHANNEL_1,
        THREE_CHANNEL_2,
        THREE_CHANNEL_3,
        THREE_CHANNEL_4,
        SIX_CHANNEL_1,
        SIX_CHANNEL_2,
        TWELVE_CHANNEL
    }

    public static final String EXTRAKEY_ECGDEVICE_BTADDRESS = "btAddress";
    public static final String EXTRAKEY_ECGDEVICE_DURATION = "duration";
    public static final String EXTRAKEY_ECGDEVICE_TYPE = "ECGType";
    public static final String ONLINE_CONTROL = "onlineControl";
    public static final int TwinTrac = 0x4;
    private static final int MESSAGE_PLAY_SOUND = 6161;
    private static final int NEW_ECG_PACKAGE = 1;
    private static final int FONT_SIZE = 10;
    private static final int UPD_MARGIN = 10;
    private static final int PERIOD = 100; // 0.1 sec
    private static final String SETTINGS_PWD = "1111";
    public static int I, II, III, aVR, aVL, aVF, V1, V2, V3, V4, V5, V6;
    protected static GraphView gv;
    private static int frame_height;
    private static float grid;
    private static SharedPreferences prefs;
    private static long timestamp;
    private static float density;

    //private int[] incomingDataTwinECG;
    private int[] dData;

    private BaseFilter[] baseFilter = null;
    private boolean invertValues;

    private static boolean measurementViewClose = false;

    //private Patient liveECGPatient;
    //private MeasurementECG liveECGMeasurement;
    private int liveLastUploadPointer;
    private PowerManager.WakeLock wl;

    private Dialog reconnectDialog;

    private final String logTag = getClass().getSimpleName();
    private final int TRANSMIT_ECG_DATA_SEC = 5;//10;

    private int incomingPointer;
    private Handler handler;

    // Timer object to enable continuous display instead of every 1.7 sec
    private Timer timer;
    private TimerTask timerTask;
    private long startTime;
    private Mode next_mode;

    private boolean onlineControl;
    private Context context;
    private boolean showPatientInfo;
    private Toast toastForPatient;
    private boolean liveECGTransmit;
    private boolean lookedAtStartPosition;

    private Handler soundHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == MESSAGE_PLAY_SOUND)
            {
                ToneGenerator toneG = new ToneGenerator(
                        AudioManager.STREAM_ALARM, 75);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1000);
            }
        }
    };

    //Va a ser igual a 0x4 por ser twintrac el dispositivo al que nos estamos conectando
    private int device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estab_conex_recibir_datos);
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //para poner la flecha en el menu de ir atras
        if (getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }*/

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        this.onlineControl = (Boolean) getIntent().getSerializableExtra(
                ONLINE_CONTROL);
        this.invertValues = false;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.liveECGTransmit = prefs.getBoolean(
                SettingActivity.ECG_LIVE_TRANSMITT, false);
        Log.d(logTag, "liveECGTransmit: " + this.liveECGTransmit);

        scroll = (HorizontalScrollView) findViewById(R.id.bt12GraphScrollView);
        scroll.setHorizontalFadingEdgeEnabled(false);
        if (gv == null)
        {
            gv = new GraphView(this);
            scroll.addView(gv);
        }
        else
        {
            scroll.removeView(gv);
            gv = new GraphView(this);
            scroll.addView(gv);
        }
        LinearLayout hrRate = (LinearLayout) findViewById(R.id.hrRateLayout);
        ScrollView layoutParameter = (ScrollView) findViewById(R.id.layoutParameter);
        boolean hrConfig = prefs.getBoolean(
                getString(R.string.pref_hr_calculation_key), false);
        if (!hrConfig)
        {
            hrRate.setVisibility(View.GONE);
        }

        // Set scale for all screen formats
        float SCALE = getResources().getDisplayMetrics().density;
        density = SCALE;

        timestamp = new Date().getTime();

        Bundle extras = getIntent().getExtras();
        Patient pat = (Patient) getIntent().getSerializableExtra("Patient");
        if (pat != null)
        {
            setPatientNameToTV(pat.getAddress().getLastname() + ",\n"
                    + pat.getAddress().getFirstname());
        }

        String btAddress = extras.getString(EXTRAKEY_ECGDEVICE_BTADDRESS);
        device = extras.getInt(EXTRAKEY_ECGDEVICE_TYPE);
        if (device != TwinTrac)
        {
            layoutParameter.setVisibility(View.GONE);
        }

        //####### Keep the screen on in this activity #####
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //####### Keep the screen on END ######

        Log.d(
                logTag,
                String.format("Connecting \"%s\" and device: %d", btAddress, device));

        //Create a new receiver for different device types
        createBtReceiver(pat, btAddress, true);

        context = this;
        showPatientInfo = false;

        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                //				Log.d(BTReceiverGraphActivity.class.getSimpleName(),
                //					msg.toString());
                if (msg.what == NEW_ECG_PACKAGE)
                {
                    scroll.requestLayout();
                    //					if (device == TwinTrac)
                    //					{
                    //						gv.invalidate();
                    //						return;
                    //					}
                    //gv.invalidate();
                    int left = 0;
                    int ptr = 0;
                    /*if (device == PocketEcg)
                    {
                        float time = new Date().getTime() - startTime;
                        ptr = (int) ((time / 1000) * freq * nChannels);
                        if (ptr > pointer)
                        {
                            ptr = pointer;
                        }
                    }
                    else
                    {*/
                        ptr = pointer;
                    //}
                    int right = (int) (((ptr * gv.hScale) / (nChannels * freq)) + 0.5f)
                            + (int) (UPD_MARGIN * density); // Plus white region
                    Log.i(logTag, "right: value = " + right + ", gv.width = "
                            + gv.width);
                    if (gv.width > 0)
                    {
                        right %= (int) (gv.width + 0.5f);
                    }
                    Log.i(logTag, "Position after region: " + right);
                    gv.invalidate(left, 0, right, (int) gv.ecg_height);
                }
            }
        };

        if (device == TwinTrac)
        {
            freq = 500;
            dMin = 0;
            dMax = 32767;
            pMin = -5.12f;
            pMax = 5.12f;
            nChannels = 2;
            I = 1;
            II = 1;
            III = 1;
            aVR = 0;
            aVL = 0;
            aVF = 0;
            V1 = 0;
            V2 = 0;
            V3 = 0;
            V4 = 0;
            V5 = 0;
            V6 = 0;
        }
        // Default duration: 10s
        ecgValues = new float[10 * freq * nChannels];
        incomingData = new byte[2 * 10 * freq * nChannels];
        //incomingDataTwinECG = new int[2 * 10 * freq * nChannels];
        dData = new int[10 * freq * nChannels];
        pointer = 0;
        incomingPointer = 0;
        lookedAtStartPosition = false;//true;//false;
        liveLastUploadPointer = 0;

        this.prefs = getSharedPreferences(MainActivity.PREFS_FILE, MODE_PRIVATE);

        /**
         * edit: Hakan
         */
        gv.setOnClickListener(this);
        /**
         * finish edit
         */

        /* Power manager to keep the CPU on**/
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                BTReceiver.class.getSimpleName());
        wl.acquire();
         /*keep CPU on END */
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createBtReceiver(Patient pat, String btAddress,
                                  boolean startReceiverFirstTime)
    {
        try
        {
            if (device == TwinTrac)
            {
                next_mode = Mode.THREE_CHANNEL_1;
                twinTracReceiver = new TwinTracBtReceiver(btAddress, device,
                        onlineControl, this, this, pat, this,
                        startReceiverFirstTime);
                twinTracReceiver.execute(this);
            }
        }
        catch (IOException e)
        {
            Log.e(logTag, e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void btReceiverFinishedWithException()
    {
        if (!onlineControl)
        {
            stopReceiverForDevice(true);
            System.out.println("TEST: BT receiver finished with exception!");
            if (!measurementViewClose)
            {
                if (startTimeConnectionLost == Long.MIN_VALUE)
                {
                    openDialogForConnect();
                    startTimeConnectionLost = System.currentTimeMillis();
                }
                final Patient pat = (Patient) getIntent().getSerializableExtra(
                        "Patient");
                final String btAddress = getIntent().getExtras().getString(
                        EXTRAKEY_ECGDEVICE_BTADDRESS);
                new Thread(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        try
                        {
                            Thread.sleep(5000);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        createBtReceiver(pat, btAddress, false);
                    }
                }).start();
            }
        }
    }

    @Override
    public void stopFillingAutomaticValues() {

        //disconnectDrawer.stopPainting();
        if (startTimeConnectionLost > Long.MIN_VALUE)
        {
            long stopTimeConnectionLost = System.currentTimeMillis();
            autoFillDataDuringDisconnection((stopTimeConnectionLost - startTimeConnectionLost) / (1000));
            startTimeConnectionLost = Long.MIN_VALUE;
            reconnectDialog.dismiss();
        }

    }

    private void autoFillDataDuringDisconnection(float durationToPaint)
    {
        int valuesCounter = (int) (durationToPaint * freq * nChannels);
        if (valuesCounter > 0)
        {
            if (device == TwinTrac)
            {
                int divPointer = pointer % nChannels;
                pointer = pointer - divPointer;
                int[] zeroLineECGValues = new int[valuesCounter];
                for (int i = 0; i < zeroLineECGValues.length; i++)
                {
                    zeroLineECGValues[i] = 5000; //Offset for TwinTrac is 5000
                }
                printTwinTracECGValues(zeroLineECGValues);
            }
        }
    }

    private void stopReceiverForDevice(boolean stopMeasurement)
    {
        if (device == TwinTrac)
        {
            if (twinTracReceiver.isStarted())
            {
                twinTracReceiver.setStopMeasurement(stopMeasurement);
                twinTracReceiver.cancel(true);
            }
            if (timer != null)
            {
                timer.cancel();
                timer.purge();
                timer = null;
            }
        }
    }

    private void printTwinTracECGValues(int[] newEcgValues)
    {
        if ((pointer + newEcgValues.length) >= ecgValues.length)
        {
            ecgValues = doubleFloatArraySize(ecgValues);
            dData = doubleIntArraySize(dData);
        }
        for (int i = 0; i < newEcgValues.length; i++)
        {
            if (pointer == ecgValues.length)
            {
                break;
            }
            //calculate the digital value with 3.2767 to get an digital range dmin = 0 to dmax= 32767
            int digitalValue = (int) (newEcgValues[i] * 3.2767d);
            if (invertValues)
            {
                digitalValue = digitalValue - 0x4000;
                digitalValue = digitalValue * (-1);
                digitalValue = digitalValue + 0x4000;
            }
            if (pointer % 2 == 1)
            {
                int v1 = dData[pointer - 1];
                int v2 = digitalValue;
                int v3 = (((v2 - 0x4000) - (v1 - 0x4000)) + 0x4000);
                //				Log.i(logTag, "value v1= " + v1 + ", v2 = " + v2 + ", v3=" + v3
                //								+ ", digitalValue = " + digitalValue);
                dData[pointer - 1] = v2;
                dData[pointer] = v3;
                if (baseFilter != null && freq == 500)
                {
                    v2 = baseFilter[(pointer - 1) % nChannels].newData((short) v2);
                    v3 = baseFilter[(pointer) % nChannels].newData((short) v3);
                }
                // Analog payload
                ecgValues[pointer - 1] = (((v2 - dMin) * (pMax - pMin)) / (dMax - dMin))
                        + pMin;
                ecgValues[pointer] = (((v3 - dMin) * (pMax - pMin)) / (dMax - dMin))
                        + pMin;
            }
            else
            {
                //Log.i(logTag, "Channel 1 ecg_value: " + newEcgValues[i]);
                dData[pointer] = digitalValue;
                ecgValues[pointer] = (((digitalValue - dMin) * (pMax - pMin)) / (dMax - dMin))
                        + pMin;
            }
            pointer++;
        }

        if (liveECGTransmit && !onlineControl)
        {
            int pointer = this.pointer;
            int duration = (pointer - liveLastUploadPointer) / freq / nChannels; //duration einbauen und speichern mit saveEDF();
            if (duration >= TRANSMIT_ECG_DATA_SEC)
            {
                try
                {
                    Log.d(logTag,
                            "new Data liveECGTransmit: "
                                    + liveECGTransmit
                                    + ", duration= "
                                    + ((pointer - liveLastUploadPointer)
                                    / freq / nChannels));
                    saveEDFAsTempFile(false, pointer);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        //Log.d(logTag, "actual pointer index value = " + pointer);
        //		if (timer == null)
        //		{
        //			if (timerTask == null)
        //			{
        //				timerTask = new TimerTask()
        //				{
        //					@Override
        //					public void run()
        //					{
        //						if (handler != null)
        //						{
        //							Message msg = handler.obtainMessage();
        //							msg.what = NEW_ECG_PACKAGE;
        //							handler.sendMessage(msg);
        //						}
        //					}
        //				};
        //			}
        //			timer = new Timer();
        //			timer.scheduleAtFixedRate(timerTask, 0, PERIOD);
        //			startTime = new Date().getTime();
        //		}
        if (handler != null)
        {
            Message msg = handler.obtainMessage();
            msg.what = NEW_ECG_PACKAGE;
            handler.sendMessage(msg);
        }
    }

    private static float[] doubleFloatArraySize(float[] array)
    {
        float array2[] = new float[array.length * 2];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }


    private static int[] doubleIntArraySize(int[] array)
    {
        int array2[] = new int[array.length * 2];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }

    private void openDialogForConnect()
    {

        if (reconnectDialog == null)
        {
            reconnectDialog = new Dialog(this, R.style.DialogWithoutTitle);
            reconnectDialog.setContentView(R.layout.loading_reconnect_bt);
        }
        reconnectDialog.show();

        System.out.print("openDialogForConnect para establecer la reconexion con el dispositivo");

    }

    public void saveEDFAsTempFile(boolean finishMeasurement, int pointer)
            throws IOException
    {
        /*
        // Save database
        if (liveECGMeasurement == null)
        {
            liveECGMeasurement = new MeasurementECG(0, null,
                    new ArrayList<ECGComment>(), new Date(timestamp), null);
            liveECGMeasurement.setSynced(false);
            if (liveECGPatient == null)
            {
                Patient pat = (Patient) getIntent().getSerializableExtra(
                        "Patient");
                String pid = pat.getPatientID();
                try
                {
                    List<Patient> list = getHelper().getPatientDao().queryForAll();
                    for (Patient p : list)
                    {
                        if (p.getPatientID().equals(pid))
                        {
                            liveECGPatient = p;
                            break;
                        }
                    }
                    Log.d(logTag, "Save ECG to " + liveECGPatient.toString());
                    liveECGMeasurement.setPatient(liveECGPatient);
                }
                catch (SQLException e)
                {
                    liveECGMeasurement = null;
                    liveECGPatient = null;
                    Log.d(logTag, e.getMessage());
                }
            }
            // Assign patient
            //@TODO do not save ECG measurement for now
            ECGActivity.getInstance().addMeasurementOnlyMeasurement(
                    liveECGMeasurement);
        }

        if (liveECGMeasurement != null)
        {
            // Save file
            GregorianCalendar start = new GregorianCalendar();
            Date time = new Date(timestamp);
            start.setTime(time);
            EDFObject edfObject = new EDFObject("", "", start, 0);
            //Log.i(logTag,"Anzahl Kanäle: "+nChannels);
            Log.i(logTag, "Current pointer position: " + pointer
                    + ", lastPointerPosition: " + liveLastUploadPointer);
            int duration = (pointer - liveLastUploadPointer) / freq / nChannels;
            for (int ch = 0; ch < nChannels; ch++)
            {
                EDFSignal edfSignal = new EDFSignal("S" + ch, "", "mV", 0, 0,
                        dMin, dMax, "", freq);
                edfSignal.physMin = pMin;
                edfSignal.physMax = pMax;
                edfObject.addSignal(edfSignal);
                byte[] channel = new byte[duration * 2 * freq];
                for (int r = 0; r < (duration * freq * nChannels); r += nChannels)
                {
                    //
                    int value = dData[r + liveLastUploadPointer + ch];
                    channel[r / (nChannels / 2)] = (byte) (value & 0xff);
                    channel[(r / (nChannels / 2)) + 1] = (byte) ((value >> 8) & 0xff);
                }
                edfSignal.setData(channel);
            }
            String fileName = "PK" + liveECGMeasurement.getId() + "_"
                    + new Date().getTime() + ".edf";
            FileOutputStream fos = openFileOutput(fileName,
                    Context.MODE_PRIVATE);
            edfObject.writeEDF(fos);
            if (liveECGPatient.getServerPID() != null)
            {
                Log.d(logTag, "upload measurement with name: " + fileName);
                new Thread(new ECGLiveUploader(liveECGMeasurement, fileName,
                        handler, liveECGPatient, finishMeasurement)).start();
            }
            int diffTimeNotTransmit = pointer - liveLastUploadPointer
                    - (duration * freq * nChannels);
            optimizeBufferArray(pointer, diffTimeNotTransmit);
            //liveLastUploadPointer = pointer - diffTimeNotTransmit;
            Log.i(logTag, "current pointer = " + pointer
                    + ", diffTimeNotTransmitt = " + diffTimeNotTransmit
                    + ", liveLastUploadPointer = "
                    + liveLastUploadPointer);
            //			incomingPointer = 0;
        }

        */
    }

    //---------------- Metodos de la interface BCTarget

    @Override
    public void channelClosed() {

    }

    @Override
    public void newData(Data d) {

    }

    @Override
    public void newData(double[] data) {

    }

    @Override
    public void patientInformation(byte[] data) {

        Log.i(logTag, "Patient Info Show");
        showPatientInfo = true;
        Thread t = new Thread()
        {
            @Override
            public void run()
            {
                parseAndShowPatientInformation(data);
            }
        };
        t.start();

    }

    //------------------ Fin Metodos de la interface BCTarget

    private void parseAndShowPatientInformation(byte[] onlineData)
    {
        int indexer = 5;
        byte[] patientNameArray = new byte[16];
        try
        {
            System.arraycopy(onlineData, indexer, patientNameArray, 0,
                    patientNameArray.length);
            final String firstName = new String(patientNameArray, 0,
                    patientNameArray.length, "UTF-8").replace("" + (char) 0x00, "");
            Log.i(logTag, firstName);
            int duration = 500;
            while (showPatientInfo)
            {

                handler.post(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        if (toastForPatient == null)
                        {
                            toastForPatient = Toast.makeText(context,
                                    firstName, Toast.LENGTH_SHORT);
                            toastForPatient.setGravity(Gravity.BOTTOM
                                    | Gravity.RIGHT, 0, 0);
                        }
                        toastForPatient.show();

                    }
                });

                try
                {
                    Thread.sleep(duration);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            if (toastForPatient != null && toastForPatient.getView().isShown())
            {
                toastForPatient.cancel();
            }

        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }



    //-------------Metodos de la interface TwinTracBtReceiver.TwinTracDataListener

    @Override
    public void configTwinTracDataReceived(TwinTracConfigManager config) {
        freq = config.getSampleRate();
    }

    @Override
    public void errorDuringBluetoothConnection(String title, String message) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton(
                android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                        finish();
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    @Override
    public void newTwinTracECGValuesReceived(TwinTracOnlineDataHolder value) {
        Log.i(logTag, "new ECG values received");
        parseDataForHolder(value);
        int[] newEcgValues = value.getEcgValuesInt();
        printTwinTracECGValues(newEcgValues);
    }

    @Override
    public void receivedPatientInformation(String patientName) {
        setPatientNameToTV(patientName);
    }

    //------------- Fin Metodos de la interface TwinTracBtReceiver.TwinTracDataListener

    private void parseDataForHolder(final TwinTracOnlineDataHolder value)
    {
        runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                //add heart rate if necessary
                if (value.getHr() > Integer.MIN_VALUE)
                {
                    findViewById(R.id.hrRateLayout).setVisibility(View.VISIBLE);
                    TextView hrReate = (TextView) findViewById(R.id.tvHrValue);
                    hrReate.setText(String.format("%d", value.getHr()) + " BPM");
                    checkHrMinMaxValueAndPlaySound(value.getHr());
                }
                else
                {
                    findViewById(R.id.hrRateLayout).setVisibility(View.GONE);
                }
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String tempUnit = prefs.getString(
                        getString(R.string.pref_temperature_unit_key),
                        getString(R.string.pref_temperature_unit_celcius));
                boolean isCelcius = tempUnit.equals(getString(R.string.pref_temperature_unit_celcius));

                //add tempValue
                if (value.getTemp1() > Integer.MIN_VALUE)
                {
                    TextView tvTemp = (TextView) findViewById(R.id.tvTempValue1);
                    double valueTemp1 = 0;
                    if (isCelcius)
                    {
                        valueTemp1 = (value.getTemp1() / 10d) - 20;
                    }
                    else
                    {
                        double valueCelcius = (value.getTemp1() / 10d) - 20;
                        valueTemp1 = valueCelcius * 1.8d + 32;
                    }
                    tvTemp.setText(String.format("%1$,.1f", valueTemp1) + " "
                            + tempUnit);
                }
                else
                {
                    findViewById(R.id.temp1Layout).setVisibility(View.GONE);
                }
                if (value.getTemp2() > Integer.MIN_VALUE)
                {
                    TextView tvTemp = (TextView) findViewById(R.id.tvTempValue2);
                    double valueTemp2 = 0;
                    if (isCelcius)
                    {
                        valueTemp2 = (value.getTemp1() / 10d) - 20;
                    }
                    else
                    {
                        double valueCelcius = (value.getTemp1() / 10d) - 20;
                        valueTemp2 = valueCelcius * 1.8d + 32;
                    }
                    tvTemp.setText(String.format("%1$,.1f", valueTemp2) + " "
                            + tempUnit);
                }
                else
                {
                    findViewById(R.id.temp2Layout).setVisibility(View.GONE);
                }
                if (value.getTemp3() > Integer.MIN_VALUE)
                {
                    findViewById(R.id.temp3Layout).setVisibility(View.VISIBLE);
                    TextView tvTemp = (TextView) findViewById(R.id.tvTempValue3);
                    double valueTemp3 = 0;
                    if (isCelcius)
                    {
                        valueTemp3 = (value.getTemp1() / 10d) - 20;
                    }
                    else
                    {
                        double valueCelcius = (value.getTemp1() / 10d) - 20;
                        valueTemp3 = valueCelcius * 1.8d + 32;
                    }
                    tvTemp.setText(String.format("%1$,.1f", valueTemp3) + " "
                            + tempUnit);
                }
                else
                {
                    findViewById(R.id.temp3Layout).setVisibility(View.GONE);
                }

                //add remain time
                TextView tvRemainTimeWithBt = (TextView) findViewById(R.id.tvRemainingTime);
                tvRemainTimeWithBt.setText(value.getRemainingTime() + "h");

                //add remain time without bt
                TextView tvRemainTimeWithoutBt = (TextView) findViewById(R.id.tvRemainingTimeWithoutBt);
                tvRemainTimeWithoutBt.setText(value.getRemainingTime() * 6
                        + "h");

                //add battery status
                TextView tvBattery = (TextView) findViewById(R.id.tvBatteryValue);
                tvBattery.setText(value.getBatteryCapacityInPercent() + "%");

                ProgressBar aclProgress = (ProgressBar) findViewById(R.id.acc_progressbar);
                aclProgress.setProgress((int) value.getAccAverage());
            }


            private void checkHrMinMaxValueAndPlaySound(int hrValue)
            {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(EstabConexRecibirDatosActivity.this);
                double hrMin = Double.valueOf(prefs.getString(
                        getString(R.string.pref_hr_minimum_value_key), "-1"));
                double hrMax = Double.valueOf(prefs.getString(
                        getString(R.string.pref_hr_maximum_value_key), "-1"));
                if (hrMin > -1 && hrMax > -1)
                {
                    if (hrMin <= hrValue && hrValue <= hrMax)
                    {
                        return;
                    }
                    else
                    {
                        soundHandler.sendEmptyMessage(MESSAGE_PLAY_SOUND);
                    }
                }
            }
        });

    }

    private void setPatientNameToTV(final String patientName)
    {
        runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                TextView p = (TextView) findViewById(R.id.tvPatientInfo);
                p.setText(patientName);
            }
        });
    }

    protected static ArrayList<String> channelsShowing()
    {
        ArrayList<String> arr = new ArrayList<>();
        if (I == 1)
        {
            arr.add("I");
        }
        if (II == 1)
        {
            arr.add("II");
        }
        if (III == 1)
        {
            arr.add("III");
        }
        if (aVR == 1)
        {
            arr.add("aVR");
        }
        if (aVL == 1)
        {
            arr.add("aVL");
        }
        if (aVF == 1)
        {
            arr.add("aVF");
        }
        if (V1 == 1)
        {
            arr.add("V1");
        }
        if (V2 == 1)
        {
            arr.add("V2");
        }
        if (V3 == 1)
        {
            arr.add("V3");
        }
        if (V4 == 1)
        {
            arr.add("V4");
        }
        if (V5 == 1)
        {
            arr.add("V5");
        }
        if (V6 == 1)
        {
            arr.add("V6");
        }
        return arr;
    }

    @Override
    public void onClick(View v)
    {
        switch (next_mode)
        {
            case THREE_CHANNEL_1:
            {
                I = 1;
                II = 1;
                III = 1;
                aVL = 0;
                aVR = 0;
                aVF = 0;
                V1 = 0;
                V2 = 0;
                V3 = 0;
                V4 = 0;
                V5 = 0;
                V6 = 0;
                next_mode = Mode.THREE_CHANNEL_2;
                break;
            }
            case THREE_CHANNEL_2:
            {
                I = 0;
                II = 0;
                III = 0;
                aVL = 1;
                aVR = 1;
                aVF = 1;
                V1 = 0;
                V2 = 0;
                V3 = 0;
                V4 = 0;
                V5 = 0;
                V6 = 0;
                next_mode = Mode.THREE_CHANNEL_3;
                break;
            }
            case THREE_CHANNEL_3:
            {
                I = 0;
                II = 0;
                III = 0;
                aVL = 0;
                aVR = 0;
                aVF = 0;
                V1 = 1;
                V2 = 1;
                V3 = 1;
                V4 = 0;
                V5 = 0;
                V6 = 0;
                next_mode = Mode.THREE_CHANNEL_4;
                break;
            }
            case THREE_CHANNEL_4:
            {
                I = 0;
                II = 0;
                III = 0;
                aVL = 0;
                aVR = 0;
                aVF = 0;
                V1 = 0;
                V2 = 0;
                V3 = 0;
                V4 = 1;
                V5 = 1;
                V6 = 1;
                next_mode = Mode.THREE_CHANNEL_1;
                break;
            }
            case SIX_CHANNEL_1:
            {
                I = 1;
                II = 1;
                III = 1;
                aVL = 1;
                aVR = 1;
                aVF = 1;
                V1 = 0;
                V2 = 0;
                V3 = 0;
                V4 = 0;
                V5 = 0;
                V6 = 0;
                next_mode = Mode.SIX_CHANNEL_2;
                break;
            }
            case SIX_CHANNEL_2:
            {
                I = 0;
                II = 0;
                III = 0;
                aVL = 0;
                aVR = 0;
                aVF = 0;
                V1 = 1;
                V2 = 1;
                V3 = 1;
                V4 = 1;
                V5 = 1;
                V6 = 1;
                next_mode = Mode.SIX_CHANNEL_1;
                break;
            }
            case TWELVE_CHANNEL:
            {
                I = 1;
                II = 1;
                III = 1;
                aVL = 1;
                aVR = 1;
                aVF = 1;
                V1 = 1;
                V2 = 1;
                V3 = 1;
                V4 = 1;
                V5 = 1;
                V6 = 1;
                next_mode = Mode.TWELVE_CHANNEL;
                break;
            }
            default:
        }
        refresh();

    }



    private void refresh()
    {
        gv.reloadDefaults();
        BT12AxisView.reloadDefaults();
        findViewById(R.id.ecgAxisView).invalidate();
        gv.postInvalidate();
    }

}