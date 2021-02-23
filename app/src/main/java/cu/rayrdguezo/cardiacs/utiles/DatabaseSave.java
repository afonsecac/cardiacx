package cu.rayrdguezo.cardiacs.utiles;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import cu.rayrdguezo.cardiacs.terceros.twintrac.cs.data.DatabaseHelper;
import cu.rayrdguezo.cardiacs.terceros.twintrac.cs.data.Patient;

public class DatabaseSave {




    public static void salvarPaciente(Patient patient, Context context) {

        DatabaseHelper bb = new DatabaseHelper(context);

            try
            {
                bb.getPatientDao().delete(patient);
                Log.d("Log salvar paciente",
                        String.format("Set active patient \"%s\"",
                                patient.toString()));

                listaDePacientes(bb);
            }
            catch (SQLException e)
            {
                Log.e("Log salvar paciente", e.getMessage());
            }
    }

    public static void listaDePacientes(DatabaseHelper bb){

        try {
            Dao<Patient, Integer> dao = bb.getPatientDao();
            QueryBuilder<Patient, Integer> qb = dao.queryBuilder();
            List<Patient> list = dao.query(qb.prepare());
            int size = list.size();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public static Patient paciente(Context context){

        DatabaseHelper bb = new DatabaseHelper(context);
        Patient p =null;

        try {
            Dao<Patient, Integer> dao = bb.getPatientDao();
            QueryBuilder<Patient, Integer> qb = dao.queryBuilder();
            List<Patient> list = dao.query(qb.prepare());
            p = list.get(0);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return p;

    }
}
