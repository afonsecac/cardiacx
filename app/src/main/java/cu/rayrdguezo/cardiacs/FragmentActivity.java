package cu.rayrdguezo.cardiacs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import cu.rayrdguezo.cardiacs.modulos.login.LoginFragments;
import cu.rayrdguezo.cardiacs.modulos.login.StartSessionLoginFragments;
import cu.rayrdguezo.cardiacs.utiles.Constantes;

public class FragmentActivity extends AppCompatActivity {

    Fragment contenidoFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        inicializarFragments(Constantes.NOMBRE_FRAGMENT_START_SESSION_LOGIN);

    }

    private void inicializarFragments( String items) {

        switch (items){

            case Constantes.NOMBRE_FRAGMENT_LOGIN:{
                contenidoFragments = new LoginFragments();
            }break;

            case Constantes.NOMBRE_FRAGMENT_START_SESSION_LOGIN:{
                contenidoFragments = new StartSessionLoginFragments();
            }break;

            /*
            case Constantes.NOMBRE_FRAGMENT_MAS_INFORMACION:{
                contenidoFragments = new MasInformacionFragments();
            }break;

            case Constantes.NOMBRE_FRAGMENT_ACERCA_DE:{
                contenidoFragments = new AcercaDeFragments();
            }break;

            case Constantes.NOMBRE_FRAGMENT_RESULTADO_CANDIDATO:{
                contenidoFragments = new ResultadoCandidatoFormularioFragments();
            }break;

            case Constantes.NOMBRE_FRAGMENT_RESULTADO_NOCANDIDATO:{
                contenidoFragments = new ResultadoNoCandidatoFormularioFragments();
            }break;

             */

        }

        //carga fragment dinamico
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.framelayout, contenidoFragments )
                .disallowAddToBackStack()
                .commit();


    }



}