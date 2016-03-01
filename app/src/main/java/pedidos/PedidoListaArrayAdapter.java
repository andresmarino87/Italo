package pedidos;

import java.util.ArrayList;
import java.util.List;

import com.italo_view.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PedidoListaArrayAdapter extends ArrayAdapter<Pedido_lista> {
	private Context context;
	private List<Pedido_lista> pedidosLista = new ArrayList<Pedido_lista>();
    private TextView row_producto_input;
    private TextView row_pres_input;
    private TextView row_vr_unitario_input;
    private TextView row_sug_input;
    private TextView row_cant_input;
    private TextView row_promo_especie_input;
    private TextView row_porc_promo_input;
    private TextView row_vr_promo_input;
    private TextView row_iva_input;
    private TextView row_subtotal_input;
	private int layoutId;    
    
    public PedidoListaArrayAdapter(Context context, int textViewResourceId,List<Pedido_lista> objects) {
        super(context, textViewResourceId, objects);
        this.pedidosLista = objects;
        this.context=context;
        this.layoutId=textViewResourceId;
		notifyDataSetChanged();
    }

	public int getCount() {
        return this.pedidosLista.size();
    }

    public Pedido_lista getItem(int index) {
        return this.pedidosLista.get(index);
    }

	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            // ROW INFLATION
        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutId, parent, false);
        }

        final Pedido_lista p = getItem(position);
        row_producto_input = (TextView) row.findViewById(R.id.row_producto_input);
        row_pres_input = (TextView) row.findViewById(R.id.row_pres_input);
        row_vr_unitario_input = (TextView) row.findViewById(R.id.row_vr_unitario_input);
        row_sug_input = (TextView) row.findViewById(R.id.row_sug_input);
        row_cant_input = (TextView) row.findViewById(R.id.row_cant_input);
        row_promo_especie_input = (TextView) row.findViewById(R.id.row_promo_especie_input);
        row_porc_promo_input = (TextView) row.findViewById(R.id.row_porc_promo_input);
        row_vr_promo_input = (TextView) row.findViewById(R.id.row_vr_promo_input);
        row_iva_input = (TextView) row.findViewById(R.id.row_iva_input);
        row_subtotal_input = (TextView) row.findViewById(R.id.row_subtotal_input);
		    
        row_producto_input.setText(p.getProducto());
        row_pres_input.setText(p.getPresentacion());
        row_vr_unitario_input.setText(p.getValorUnitario());
        row_sug_input.setText(p.getSugerido());
        row_cant_input.setText(p.getCantidad());
        row_promo_especie_input.setText(p.getPromoEspecie());
        row_porc_promo_input.setText(p.getPorcPromo());
        row_vr_promo_input.setText(p.getValorPromo());
        row_iva_input.setText(p.getIVA());
        row_subtotal_input.setText(p.getSubtotal());

        if(position % 2 == 0){
        	row_producto_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_pres_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_vr_unitario_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_sug_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_cant_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_promo_especie_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_porc_promo_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_vr_promo_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_iva_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_subtotal_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        }else{
        	row_producto_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
        	row_pres_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
        	row_vr_unitario_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
        	row_sug_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
        	row_cant_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
        	row_promo_especie_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
        	row_porc_promo_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
        	row_vr_promo_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
        	row_iva_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
        	row_subtotal_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
        }
        return row;
	}
}