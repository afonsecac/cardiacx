package cu.rayrdguezo.cardiacs;

import androidx.fragment.app.Fragment;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import cu.rayrdguezo.cardiacs.terceros.twintrac.cs.data.DatabaseHelper;

public class OrmLiteFragment extends Fragment {

    private DatabaseHelper databaseHelper = null;


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (databaseHelper != null)
        {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }


    protected DatabaseHelper getHelper()
    {
        if (databaseHelper == null)
        {
            databaseHelper = OpenHelperManager.getHelper(getActivity(),
                    DatabaseHelper.class);
        }
        return databaseHelper;
    }

}
