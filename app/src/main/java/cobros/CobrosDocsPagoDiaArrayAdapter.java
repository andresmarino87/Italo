package cobros;

import java.util.ArrayList;
import java.util.List;

import com.italo_view.R;

import utilidades.Utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class CobrosDocsPagoDiaArrayAdapter extends ArrayAdapter<Cobros_Dia_Docs_Pago_Item> {
	private Context context;
	private int layoutId;
	private List<Cobros_Dia_Docs_Pago_Item> cobros = new ArrayList<Cobros_Dia_Docs_Pago_Item>();
		    
	public CobrosDocsPagoDiaArrayAdapter(Context context, int textViewResourceId,List<Cobros_Dia_Docs_Pago_Item> objects) {
		super(context, textViewResourceId, objects);
		this.cobros = objects;
		this.context = context;
		layoutId=textViewResourceId;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.cobros.size();
	}
	
	public Cobros_Dia_Docs_Pago_Item getItem(int index) {
		return this.cobros.get(index);
	}

	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		final Cobros_Dia_Docs_Pago_Item c = getItem(position);
		final String formaPago=c.getFormaPago();
		
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutId, parent, false);
		}

		final TextView row_forma_pago_input = (TextView) row.findViewById(R.id.row_forma_pago_input);
		final TextView row_valor_input = (TextView) row.findViewById(R.id.row_valor_input);
		
		if(formaPago.equalsIgnoreCase("EF")){
			row_forma_pago_input.setText(R.string.efectivo);
		}else if(formaPago.equalsIgnoreCase("CH")){
			row_forma_pago_input.setText(R.string.cheque);			
		}else if(formaPago.equalsIgnoreCase("TR")){
			row_forma_pago_input.setText(R.string.transferencia);
		}else if(formaPago.equalsIgnoreCase("NC")){
			row_forma_pago_input.setText(R.string.nota_credito);			
		}
//		row_forma_pago_input.setText(c.getFormaPago());
		
		row_valor_input.setText(Utility.formatNumber(c.getValor()));
		
		if(position % 2 == 0){
			row_forma_pago_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		}else{
			row_forma_pago_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		}
		return row;
	}
}