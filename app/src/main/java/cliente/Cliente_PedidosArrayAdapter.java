package cliente;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.italo_view.R;

public class Cliente_PedidosArrayAdapter extends ArrayAdapter<Cliente_Pedido_Item> {
	private Context context;
	private int layoutId;
	private List<Cliente_Pedido_Item> pedidos = new ArrayList<Cliente_Pedido_Item>();

    public Cliente_PedidosArrayAdapter(Context context, int textViewResourceId,List<Cliente_Pedido_Item> objects) {
        super(context, textViewResourceId, objects);
        this.pedidos = objects;
        this.context=context;
        this.layoutId=textViewResourceId;
		notifyDataSetChanged();
    }

	public int getCount() {
        return this.pedidos.size();
    }

    public Cliente_Pedido_Item getItem(int index) {
        return this.pedidos.get(index);
    }

	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            // ROW INFLATION
        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutId, parent, false);
        }

        final Cliente_Pedido_Item p = getItem(position);
        final TextView row_n_pedido_input = (TextView) row.findViewById(R.id.row_n_pedido_input);
        final TextView row_valor_factura_input = (TextView) row.findViewById(R.id.row_valor_factura_input);
        final TextView row_estado_input = (TextView) row.findViewById(R.id.row_estado_input);
        final TextView row_fecha_input = (TextView) row.findViewById(R.id.row_fecha_input);
        
	    row_n_pedido_input.setText(p.getNFactura());
	    row_valor_factura_input.setText(p.getFactura());
	    row_estado_input.setText(p.getEstado());
/*		if(p.getEstado().equalsIgnoreCase("13")){
			row_estado_input.setText("FACTURAR");
		}else if(p.getEstado().equalsIgnoreCase("70")){
			row_estado_input.setText("VENTAS");
		}else if(p.getEstado().equalsIgnoreCase("14")){
			row_estado_input.setText("CREDITOS");
		}else if(p.getEstado().equalsIgnoreCase("71")){
			row_estado_input.setText("ANULADO");
		}*/

	    row_fecha_input.setText(p.getFecha());

        if(position % 2 == 0){
        	row_n_pedido_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_valor_factura_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_estado_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_fecha_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		}else{
			row_n_pedido_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_valor_factura_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_estado_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_fecha_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		}
        return row;
    }
}