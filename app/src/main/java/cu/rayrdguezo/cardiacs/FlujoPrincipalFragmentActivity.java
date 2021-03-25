package cu.rayrdguezo.cardiacs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import cu.rayrdguezo.cardiacs.modulos.datospreeliminares.DatosPreeliminaresFragment;
import cu.rayrdguezo.cardiacs.modulos.login.LoginFragments;
import cu.rayrdguezo.cardiacs.modulos.login.StartSessionLoginFragments;
import cu.rayrdguezo.cardiacs.utiles.Constantes;

public class FlujoPrincipalFragmentActivity extends AppCompatActivity {

    private Bundle savedInstanceState;

    Fragment contenidoFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flujo_principal_fragment);

        inicializarFragments(Constantes.NOMBRE_FRAGMENT_DATOS_PREELIMINARES);
    }

    private void inicializarFragments( String items) {

        switch (items){

            case Constantes.NOMBRE_FRAGMENT_DATOS_PREELIMINARES:{
                contenidoFragments = new DatosPreeliminaresFragment();
            }break;

            /*
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
                .replace(R.id.flujoPrincipalFramelayout, contenidoFragments )
                .disallowAddToBackStack()
                .commit();


    }
}