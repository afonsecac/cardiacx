package cu.rayrdguezo.cardiacs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.widget.Toast;

import cu.rayrdguezo.cardiacs.modulos.login.LoginFragments;
import cu.rayrdguezo.cardiacs.modulos.login.StartSessionLoginFragments;
import cu.rayrdguezo.cardiacs.utiles.Constantes;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;


public class FragmentActivity extends AppCompatActivity {
    private Bundle savedInstanceState;

    Fragment contenidoFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        inicializarFragments(Constantes.NOMBRE_FRAGMENT_START_SESSION_LOGIN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissionAfterAPI23()) {
                ActivityCompat.requestPermissions(this, new String[]{
                        WRITE_EXTERNAL_STORAGE,
                        CAMERA,
                        ACCESS_COARSE_LOCATION}, 0);
                this.savedInstanceState = savedInstanceState;
            } else {
                this.savedInstanceState = savedInstanceState;
                init();
            }
        } else {
            if (checkPermissionBeforeAPI23()) {
                this.savedInstanceState = savedInstanceState;
                init();
            } else {
                Toast.makeText(this, "Los permisos no están habilitados para poder usar correctamente esta aplicación", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void init() {
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


    private void showDialogPermissionsAlert(String message, DialogInterface.OnClickListener okListener) {
        LayoutInflater inflater  = this.getLayoutInflater();
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Aceptar", okListener)
                .setNegativeButton("Cancelar", okListener)
                .create()
                .show();
    }

    private boolean checkPermissionAfterAPI23() {
        return (ActivityCompat.checkSelfPermission(FragmentActivity.this,WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(FragmentActivity.this,CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(FragmentActivity.this,ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkPermissionBeforeAPI23() {
        return (checkCallingOrSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkCallingOrSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED
                && checkCallingOrSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            int result = grantResults[i];
            if (result != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                    //Si ha denegado un permiso
                    //Explicar que son necesarios y volver a preguntar
                    showDialogPermissionsAlert("Los permisos necesarios para poder usar correctamente esta aplicación están deshabilitados. ¿Desea volver a intentarlo?",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            ActivityCompat.requestPermissions(FragmentActivity.this, new String[]{
                                                    WRITE_EXTERNAL_STORAGE,
                                                    CAMERA,
                                                    ACCESS_COARSE_LOCATION}, 0);
                                            ;
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            finish();
                                            break;
                                    }
                                }
                            });
                } else {
                    //Si le ha dado a "Don't ask again"
                    //Llevar a activity de la configuaracion de la app para cambiar permisos
                    Toast.makeText(FragmentActivity.this, "Habilite los permisos de la aplicación en la configuración", Toast.LENGTH_LONG).show();
                    while (!checkPermissionAfterAPI23()) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);;
                    }
                    init();
                }
                return;
            }
        }
        init();
    }




}