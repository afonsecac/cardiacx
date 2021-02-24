package cu.rayrdguezo.cardiacs.utiles;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cu.rayrdguezo.cardiacs.terceros.twintrac.cs.data.DatabaseHelper;
import cu.rayrdguezo.cardiacs.terceros.twintrac.cs.data.Patient;

public class DatabaseSave {




    public static void salvarPaciente(Patient patient, Context context) {

        DatabaseHelper bb = new DatabaseHelper(context);

        if (listaDePacientes(bb).size() == 0 ) {

            try {
                bb.getPatientDao().create(patient);
                Log.d("Log salvar paciente",
                        String.format("Set active patient \"%s\"",
                                patient.toString()));

                if (listaDePacientes(bb).size() == 1) {
                    Toast.makeText(context, "Paciente creado correctamente", Toast.LENGTH_SHORT).show();
                }


            } catch (SQLException e) {
                Log.e("Log salvar paciente", e.getMessage());
            }
        }else {
            Toast.makeText(context,"El paciente de prueba ya esta creado",Toast.LENGTH_SHORT).show();
        }
    }

    public static List<Patient> listaDePacientes(DatabaseHelper bb){

        List<Patient> list = null;

        try {
            Dao<Patient, Integer> dao = bb.getPatientDao();
            QueryBuilder<Patient, Integer> qb = dao.queryBuilder();
            list = dao.query(qb.prepare());
            int size = list.size();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return list;

    }

    public static Patient paciente(Context context){

        DatabaseHelper bb = new DatabaseHelper(context);
        List<Patient> list = listaDePacientes(bb);
        Patient p = list.get(0);

        return p;

    }
}
