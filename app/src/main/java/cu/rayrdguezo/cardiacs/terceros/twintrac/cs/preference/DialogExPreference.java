package cu.rayrdguezo.cardiacs.terceros.twintrac.cs.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import cu.rayrdguezo.cardiacs.R;

/**
 * This dialog represents the marker text values that can be set to an ecg
 * recording.
 * 
 * @author Hakan Sahin
 * @date 08.06.2013 09:48:29 Hakan Sahin
 */
public class DialogExPreference extends DialogPreference
{
	public final static String MARKER_STRING_VALUE = DialogExPreference.class.getCanonicalName();
	public final static String MARKER1 = "marker1";
	public final static String MARKER2 = "marker2";
	public final static String MARKER3 = "marker3";
	public final static String MARKER4 = "marker4";
	public final static String MARKER5 = "marker5";
	public final static String MARKER6 = "marker6";
	public final static String MARKER7 = "marker7";
	public final static String MARKER8 = "marker8";
	public final static String MARKER9 = "marker9";
	public final static String MARKER10 = "marker10";
	private EditText marker1, marker2, marker3, marker4, marker5, marker6,
					marker7, marker8, marker9, marker10;
	private final Context resource;


	public DialogExPreference(Context oContext, AttributeSet attrs)
	{
		super(oContext, attrs);
		resource = oContext;
	}


	@Override
	protected void onBindDialogView(View view)
	{
		super.onBindDialogView(view);
		SharedPreferences prefs = resource.getSharedPreferences(
			DialogExPreference.MARKER_STRING_VALUE, Context.MODE_PRIVATE);
		marker1 = (EditText) view.findViewById(R.id.editTextMarker1);
		marker2 = (EditText) view.findViewById(R.id.editTextMarker2);
		marker3 = (EditText) view.findViewById(R.id.editTextMarker3);
		marker4 = (EditText) view.findViewById(R.id.editTextMarker4);
		marker5 = (EditText) view.findViewById(R.id.editTextMarker5);
		marker6 = (EditText) view.findViewById(R.id.editTextMarker6);
		marker7 = (EditText) view.findViewById(R.id.editTextMarker7);
		marker8 = (EditText) view.findViewById(R.id.editTextMarker8);
		marker9 = (EditText) view.findViewById(R.id.editTextMarker9);
		marker10 = (EditText) view.findViewById(R.id.editTextMarker10);

		//set marker text
		marker1.setText(prefs.getString(DialogExPreference.MARKER1,
			DialogExPreference.MARKER1));
		marker2.setText(prefs.getString(DialogExPreference.MARKER2,
			DialogExPreference.MARKER2));
		marker3.setText(prefs.getString(DialogExPreference.MARKER3,
			DialogExPreference.MARKER3));
		marker4.setText(prefs.getString(DialogExPreference.MARKER4,
			DialogExPreference.MARKER4));
		marker5.setText(prefs.getString(DialogExPreference.MARKER5,
			DialogExPreference.MARKER5));
		marker6.setText(prefs.getString(DialogExPreference.MARKER6,
			DialogExPreference.MARKER6));
		marker7.setText(prefs.getString(DialogExPreference.MARKER7,
			DialogExPreference.MARKER7));
		marker8.setText(prefs.getString(DialogExPreference.MARKER8,
			DialogExPreference.MARKER8));
		marker9.setText(prefs.getString(DialogExPreference.MARKER9,
			DialogExPreference.MARKER9));
		marker10.setText(prefs.getString(DialogExPreference.MARKER10,
			DialogExPreference.MARKER10));
	}


	@Override
	protected void onDialogClosed(boolean positiveResult)
	{
		super.onDialogClosed(positiveResult);

		if (positiveResult)
		{
			SharedPreferences prefs = resource.getSharedPreferences(
				DialogExPreference.MARKER_STRING_VALUE, Context.MODE_PRIVATE);
			Editor editor = prefs.edit();
			editor.putString(MARKER1, marker1.getText().toString());
			editor.putString(MARKER2, marker2.getText().toString());
			editor.putString(MARKER3, marker3.getText().toString());
			editor.putString(MARKER4, marker4.getText().toString());
			editor.putString(MARKER5, marker5.getText().toString());
			editor.putString(MARKER6, marker6.getText().toString());
			editor.putString(MARKER7, marker7.getText().toString());
			editor.putString(MARKER8, marker8.getText().toString());
			editor.putString(MARKER9, marker9.getText().toString());
			editor.putString(MARKER10, marker10.getText().toString());

			editor.commit();
		}
	}

	//	@Override
	//	protected void onPrepareDialogBuilder(Builder builder)
	//	{
	//		super.onPrepareDialogBuilder(builder);
	//		builder.setCancelable(false);
	//	}
}
