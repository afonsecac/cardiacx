package cu.rayrdguezo.cardiacs.modulos.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import cu.rayrdguezo.cardiacs.MainActivity;
import cu.rayrdguezo.cardiacs.R;
import cu.rayrdguezo.cardiacs.utiles.Constantes;

public class NewSessionCodeLoginFragments extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private CardView cardVAccept, cardVCancel;

    public NewSessionCodeLoginFragments() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NewSessionCodeLoginFragments newInstance(int sectionNumber) {
        NewSessionCodeLoginFragments fragment = new NewSessionCodeLoginFragments();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragments_new_session_code_login, container, false);

        cardVAccept = (CardView) rootView.findViewById(R.id.cardVAccept);
        cardVAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(intent);
            }
        });

        cardVCancel = (CardView) rootView.findViewById(R.id.cardVCancel);
        cardVCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
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

