package cu.rayrdguezo.cardiacs.modulos.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import cu.rayrdguezo.cardiacs.R;

public class StartSessionLoginFragments extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private ImageView imgVAtras;

    public StartSessionLoginFragments() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static StartSessionLoginFragments newInstance(int sectionNumber) {
        StartSessionLoginFragments fragment = new StartSessionLoginFragments();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragments_login, container, false);

        /*imgVAtras = (ImageView) rootView.findViewById(R.id.imgVAtras);
        imgVAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalizar();
            }
        });

         */

        return rootView;
    }

    private void finalizar(){
        getActivity().finish();
    }


}

