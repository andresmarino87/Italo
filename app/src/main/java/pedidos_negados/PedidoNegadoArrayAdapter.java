package pedidos_negados;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.italo_view.R;

public class PedidoNegadoArrayAdapter extends ArrayAdapter<Pedidos_Negados_Item> {
    private List<Pedidos_Negados_Item> pedidosNegados = new ArrayList<Pedidos_Negados_Item>();
    private int resourceId;
    private Context context;

    public PedidoNegadoArrayAdapter(Context context, int textViewResourceId,List<Pedidos_Negados_Item> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.pedidosNegados = objects;
        this.resourceId = textViewResourceId;
		notifyDataSetChanged();
    }

	public int getCount() {
        return this.pedidosNegados.size();
    }

    public Pedidos_Negados_Item getItem(int index) {
        return this.pedidosNegados.get(index);
    }

	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            // ROW INFLATION
        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resourceId, parent, false);
        }

        final Pedidos_Negados_Item pedidoNegado = getItem(position);
	    final TextView row_pedido_input = (TextView) row.findViewById(R.id.row_pedido_input);
	    final TextView row_codigo_input = (TextView) row.findViewById(R.id.row_codigo_input);
	    final TextView row_cliente_input = (TextView) row.findViewById(R.id.row_cliente_input);
	    final TextView valor_row_input = (TextView) row.findViewById(R.id.valor_row_input);
        
        row_pedido_input.setText(pedidoNegado.getPedido());
        row_codigo_input.setText(pedidoNegado.getCodigo());
        row_cliente_input.setText(pedidoNegado.getCliente());
        valor_row_input.setText(pedidoNegado.getValor());

        if(position % 2 == 0){
        	row_pedido_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		    row_codigo_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		    row_cliente_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		    valor_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		}else{
        	row_pedido_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		    row_codigo_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		    row_cliente_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		    valor_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		}
        return row;
    }
}