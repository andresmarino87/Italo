package paretos;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.italo_view.R;

public class ParetoArrayAdapter extends ArrayAdapter<Paretos_Item> {
    private List<Paretos_Item> paretos = new ArrayList<Paretos_Item>();
    private Context context;
    private int resourceId;
    
    public ParetoArrayAdapter(Context context, int textViewResourceId,List<Paretos_Item> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.paretos = objects;
        this.resourceId = textViewResourceId;
		notifyDataSetChanged();
    }

	public int getCount() {
        return this.paretos.size();
    }

    public Paretos_Item getItem(int index) {
        return this.paretos.get(index);
    }

	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resourceId, parent, false);
        }

        final Paretos_Item p = getItem(position);
        final TextView row_codigo_input = (TextView) row.findViewById(R.id.row_codigo_input);
        final TextView row_cliente_input = (TextView) row.findViewById(R.id.row_cliente_input);
        final TextView row_sector_input = (TextView) row.findViewById(R.id.row_sector_input);
        final TextView row_ruta_input = (TextView) row.findViewById(R.id.row_ruta_input);
        final TextView row_valor_input = (TextView) row.findViewById(R.id.row_valor_input);

	    row_codigo_input.setText(p.getCodigo());
	    row_cliente_input.setText(p.getCliente());
	    row_sector_input.setText(p.getSector());
	    row_ruta_input.setText(p.getRuta());
	    row_valor_input.setText(p.getTotal());

        if(position % 2 == 0){
        	row_codigo_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_cliente_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_sector_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_ruta_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        }else{
			row_codigo_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_cliente_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_sector_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_ruta_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		}
        return row;
    }
}
