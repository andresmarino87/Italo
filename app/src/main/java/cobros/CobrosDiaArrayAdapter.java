package cobros;

import java.util.ArrayList;
import java.util.List;

import utilidades.Utility;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.italo_view.R;

public class CobrosDiaArrayAdapter extends ArrayAdapter<Cobros_Dia_Item> {
		private Context context;
		private int layoutId;
		private List<Cobros_Dia_Item> cobros = new ArrayList<Cobros_Dia_Item>();
	    
	    public CobrosDiaArrayAdapter(Context context, int textViewResourceId,List<Cobros_Dia_Item> objects) {
	        super(context, textViewResourceId, objects);
	        this.cobros = objects;
	        this.context = context;
	        layoutId=textViewResourceId;
			notifyDataSetChanged();
	    }

		public int getCount() {
	        return this.cobros.size();
	    }

	    public Cobros_Dia_Item getItem(int index) {
	        return this.cobros.get(index);
	    }

		@SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        final Cobros_Dia_Item c = getItem(position);

	        if (row == null) {
	        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            row = inflater.inflate(layoutId, parent, false);
	        }

		    final TextView row_sinc_input = (TextView) row.findViewById(R.id.row_sinc_input);
		    final TextView row_cliente_input = (TextView) row.findViewById(R.id.row_cliente_input);
		    final TextView row_cobro_id_input = (TextView) row.findViewById(R.id.row_cobro_id_input);
		    final TextView row_hora_input = (TextView) row.findViewById(R.id.row_hora_input);
		    final TextView row_total_pago_input = (TextView) row.findViewById(R.id.row_total_pago_input);
	    
		    row_sinc_input.setText(c.getSinc());
		    row_cliente_input.setText(c.getClienteId()+" "+c.getCliente());
		    row_cobro_id_input.setText(c.getCobroId());
		    row_hora_input.setText(c.getHora());
		    row_total_pago_input.setText(Utility.formatNumber(c.getTotalPago()));

	        if(position % 2 == 0){
	        	row_sinc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
	        	row_cliente_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
	        	row_cobro_id_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
	        	row_hora_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
	        	row_total_pago_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
	        }else{
	        	row_sinc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
	        	row_cliente_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
	        	row_cobro_id_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
	        	row_hora_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
	        	row_total_pago_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			}
	        return row;
	    }
	}

