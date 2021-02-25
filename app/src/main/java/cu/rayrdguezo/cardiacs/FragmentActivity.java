package cu.rayrdguezo.cardiacs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class FragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);



    }

    private void inicializarFragments( String items) {
/*
        switch (items){

            case Constantes.NOMBRE_FRAGMENT_RECORDATORIO_ADMIN:{
                contenidoFragments = new RecordatorioAdminFragments();
            }break;

            case Constantes.NOMBRE_FRAGMENT_MATERIAL_DIDACTICO:{
                contenidoFragments = new MaterialDidacticoFragments();
            }break;

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

        }

        //carga fragment dinamico
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.framelayout, contenidoFragments )
                .disallowAddToBackStack()
                .commit();

        */
    }

}