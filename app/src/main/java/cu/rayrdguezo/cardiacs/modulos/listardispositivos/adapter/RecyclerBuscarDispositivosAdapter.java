package cu.rayrdguezo.cardiacs.modulos.listardispositivos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cu.rayrdguezo.cardiacs.R;
import cu.rayrdguezo.cardiacs.modulos.listardispositivos.model.BuscarDispositivoModel;
import cu.rayrdguezo.cardiacs.utiles.RecyclerItemClickListener;

public class RecyclerBuscarDispositivosAdapter extends RecyclerView.Adapter<RecyclerBuscarDispositivosAdapter.ItemsDispositivosViewHolder> {

    ArrayList<BuscarDispositivoModel> mDataset = new ArrayList<>();
    Context context;
    RecyclerItemClickListener recyclerItemClickListener;

    public RecyclerBuscarDispositivosAdapter(ArrayList<BuscarDispositivoModel> mDataset, Context context) {
        this.mDataset = mDataset;
        this.context = context;
    }

    public RecyclerBuscarDispositivosAdapter(Context context) {
        this.context = context;
    }

    public void setRecyclerItemClickListener(RecyclerItemClickListener recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    public RecyclerItemClickListener getRecyclerItemClickListener(RecyclerItemClickListener recyclerItemClickListener) {
        return recyclerItemClickListener;
    }

    public void setmDataset(ArrayList<BuscarDispositivoModel> mDataset) {
        this.mDataset = mDataset;
    }

    @NonNull
    @Override
    public ItemsDispositivosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.items_recycler_buscar_dispositivos, parent, false);
        return new ItemsDispositivosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemsDispositivosViewHolder holder, final int position) {

        holder.txtVNombre.setText(mDataset.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(context,"Seleccionado el dispositivo "+ mDataset.get(position).getName(),Toast.LENGTH_LONG).show();

                if (recyclerItemClickListener != null) {
                    recyclerItemClickListener.onItemClickRecycler(view, mDataset.get(position), position);
                }
            }
        });

    }

    public void add (BuscarDispositivoModel dispositivoModel){
        mDataset.add(dispositivoModel);
        notifyDataSetChanged();
    }

    public ArrayList<BuscarDispositivoModel> getItems(){
        return mDataset;
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ItemsDispositivosViewHolder extends RecyclerView.ViewHolder {

        TextView txtVNombre;

        public ItemsDispositivosViewHolder(@NonNull View itemView) {
            super(itemView);

            txtVNombre = itemView.findViewById(R.id.txtVNombreDispositivo);

        }
    }



}
