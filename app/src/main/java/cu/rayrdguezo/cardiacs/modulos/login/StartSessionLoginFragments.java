package cu.rayrdguezo.cardiacs.modulos.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import cu.rayrdguezo.cardiacs.R;
import cu.rayrdguezo.cardiacs.utiles.Constantes;

public class StartSessionLoginFragments extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private CardView cardVStartNewSession, cardVCheckCurrentSession;

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
        View rootView = inflater.inflate(R.layout.fragments_start_session_login, container, false);

        cardVStartNewSession = (CardView) rootView.findViewById(R.id.cardVStartNewSession);
        cardVStartNewSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAOpcionSeleccionda(Constantes.NOMBRE_FRAGMENT_LOGIN);

            }
        });

        cardVCheckCurrentSession = (CardView) rootView.findViewById(R.id.cardVCheckCurrentSession);
        cardVCheckCurrentSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAOpcionSeleccionda(Constantes.NOMBRE_FRAGMENT_LOGIN_SESSION_CODE);
            }
        });



        return rootView;
    }

    private void finalizar(){
        getActivity().finish();
    }

    public void irAOpcionSeleccionda(String nombFragment){

        LoginFragments loginFragments = new LoginFragments();

        if (Constantes.NOMBRE_FRAGMENT_LOGIN.equalsIgnoreCase(nombFragment)){
            Bundle data = new Bundle();
            data.putBoolean(Constantes.KEY_BUNDLE_SHOW_CODE_SESSION,false);
            loginFragments.setArguments(data);
        }

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.framelayout, loginFragments )
                .addToBackStack(Constantes.NOMBRE_FRAGMENT_START_SESSION_LOGIN)
                .commit();



        /*switch (nombFragment){
            case Constantes.NOMBRE_FRAGMENT_LOGIN:{


            }break;

            case Constantes.NOMBRE_FRAGMENT_CHECK_CURRENT_SESSION_LOGIN:{
                LoginFragments loginFragments = new LoginFragments();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.framelayout, loginFragments )
                        .addToBackStack(Constantes.NOMBRE_FRAGMENT_START_SESSION_LOGIN)
                        .commit();
                loginFragments.activarSessionCode();
            }break;

        }

         */



    }


}

