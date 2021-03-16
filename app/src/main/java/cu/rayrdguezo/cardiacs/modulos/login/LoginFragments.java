package cu.rayrdguezo.cardiacs.modulos.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import cu.rayrdguezo.cardiacs.MainActivity;
import cu.rayrdguezo.cardiacs.R;
import cu.rayrdguezo.cardiacs.utiles.Constantes;

public class LoginFragments extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private ImageView imgVAtras;
    private boolean showCodeSession = true;
    private TextInputLayout loginTextInputSessionCode;
    private CardView cardVStart;

    public LoginFragments() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static LoginFragments newInstance(int sectionNumber) {
        LoginFragments fragment = new LoginFragments();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragments_login, container, false);

        cardVStart = (CardView) rootView.findViewById(R.id.cardVStart);
        cardVStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showCodeSession){

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    getActivity().startActivity(intent);

                }else {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.framelayout, new NewSessionCodeLoginFragments() )
                            .addToBackStack(Constantes.NOMBRE_FRAGMENT_LOGIN)
                            .commit();
                }
            }
        });

        loginTextInputSessionCode = (TextInputLayout) rootView.findViewById(R.id.loginTextInputSessionCode);
        Bundle data = this.getArguments();
        if(data != null){
            showCodeSession = data.getBoolean(Constantes.KEY_BUNDLE_SHOW_CODE_SESSION);
            if(!showCodeSession)
                loginTextInputSessionCode.setVisibility(View.GONE);
        }

        /*imgVAtras = (ImageView) rootView.findViewById(R.id.imgVAtras);
        imgVAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalizar();
            }
        });*/



        return rootView;
    }

    private void finalizar(){
        getActivity().finish();
    }



}

