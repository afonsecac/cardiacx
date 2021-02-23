package cu.rayrdguezo.cardiacs.terceros.twintrac.cs.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Database handler: Interface between the application and his database. A DAO
 * will create for each measurement, to add, delete or update measurement
 * values.
 * 
 * @author Hakan Sahin
 * @date 08.06.2013 09:08:35 Hakan Sahin
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{

	/* default */static final String DATABASE_NAME = "de.avetana.cs.DB";
	/* default *///static final int DATABASE_VERSION = 1;
	static final int DATABASE_VERSION = 2;
	private Dao<MeasurementECG, Integer> ecgDao;
	private Dao<Patient, Integer> patientDao;
	private Dao<MeasurementBP, Integer> bpDao;
	private Dao<MeasurementGlucose, Integer> glucoseDao;
	private Dao<MeasurementWeight, Integer> weightDao;


	public DatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	/**
	 * @return the ecgDao
	 */
	public Dao<MeasurementBP, Integer> getBpDao() throws SQLException
	{
		if (bpDao == null)
		{
			bpDao = getDao(MeasurementBP.class);
		}
		return bpDao;
	}


	/**
	 * @return the ecgDao
	 */
	public Dao<MeasurementECG, Integer> getEcgDao() throws SQLException
	{
		if (ecgDao == null)
		{
			ecgDao = getDao(MeasurementECG.class);
		}
		return ecgDao;
	}


	/**
	 * @return the ecgDao
	 */
	public Dao<MeasurementGlucose, Integer> getGlucoseDao() throws SQLException
	{
		if (glucoseDao == null)
		{
			glucoseDao = getDao(MeasurementGlucose.class);
		}
		return glucoseDao;
	}


	/**
	 * @return the patientDao
	 */
	public Dao<Patient, Integer> getPatientDao() throws SQLException
	{
		if (patientDao == null)
		{
			patientDao = getDao(Patient.class);
		}
		return patientDao;
	}


	/**
	 * @return the ecgDao
	 */
	public Dao<MeasurementWeight, Integer> getWeightDao() throws SQLException
	{
		if (weightDao == null)
		{
			weightDao = getDao(MeasurementWeight.class);
		}
		return weightDao;
	}


	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase,
                         ConnectionSource connectionSource)
	{
		try
		{
			TableUtils.createTable(connectionSource, MeasurementECG.class);
			TableUtils.createTable(connectionSource, Patient.class);
			TableUtils.createTable(connectionSource, MeasurementBP.class);
			TableUtils.createTable(connectionSource, MeasurementGlucose.class);
			TableUtils.createTable(connectionSource, MeasurementWeight.class);
		}
		catch (SQLException e)
		{
			Log.e(DatabaseHelper.class.getName(), "Unable to create datbases",
				e);
		}
	}


	@Override
	public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion,
                          int newVersion)
	{
		try
		{
			TableUtils.dropTable(connectionSource, MeasurementECG.class, true);
			TableUtils.dropTable(connectionSource, Patient.class, true);
			TableUtils.dropTable(connectionSource, MeasurementBP.class, true);
			TableUtils.dropTable(connectionSource, MeasurementGlucose.class,
				true);
			TableUtils.dropTable(connectionSource, MeasurementWeight.class,
				true);
			onCreate(database, connectionSource);
		}
		catch (SQLException e)
		{
			Log.e(DatabaseHelper.class.getName(),
				"Unable to upgrade database from version " + oldVersion
								+ " to " + newVersion, e);
		}
	}

}
