package cu.rayrdguezo.cardiacs.utiles;

import android.app.ProgressDialog;
import android.content.Context;

public class MyProgressDialog {

    ProgressDialog progressDialog;

    public MyProgressDialog(Context context) {
        this.progressDialog = new ProgressDialog(context);
    }

    public void mostrar(final String texto){
        progressDialog.setMessage(texto);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void mostrar(){
        progressDialog.setMessage("Cargando ...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void ocultar(){
        progressDialog.dismiss();
    }

}
