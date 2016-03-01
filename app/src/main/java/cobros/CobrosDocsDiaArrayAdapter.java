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

public class CobrosDocsDiaArrayAdapter extends ArrayAdapter<Cobros_Dia_Docs_Item> {
	private Context context;
	private int layoutId;
	private List<Cobros_Dia_Docs_Item> cobros = new ArrayList<Cobros_Dia_Docs_Item>();
	    
    public CobrosDocsDiaArrayAdapter(Context context, int textViewResourceId,List<Cobros_Dia_Docs_Item> objects) {
        super(context, textViewResourceId, objects);
        this.cobros = objects;
        this.context = context;
        layoutId=textViewResourceId;
		notifyDataSetChanged();
    }

		public int getCount() {
	        return this.cobros.size();
	    }

	    public Cobros_Dia_Docs_Item getItem(int index) {
	        return this.cobros.get(index);
	    }

		@SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        final Cobros_Dia_Docs_Item c = getItem(position);

	        if (row == null) {
	        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            row = inflater.inflate(layoutId, parent, false);
	        }

		    final TextView row_tipo_doc_input = (TextView) row.findViewById(R.id.row_tipo_doc_input);
		    final TextView row_n_documento_input = (TextView) row.findViewById(R.id.row_n_documento_input);
		    final TextView row_saldo_input = (TextView) row.findViewById(R.id.row_saldo_input);
		    final TextView row_valor_pago_input = (TextView) row.findViewById(R.id.row_valor_pago_input);
	    
		    row_tipo_doc_input.setText(c.getTipoDoc());
		    row_n_documento_input.setText(c.getDocId());
		    row_saldo_input.setText(Utility.formatNumber(c.getSaldo()));
		    row_valor_pago_input.setText(Utility.formatNumber(c.getValorPago()));

	        if(position % 2 == 0){
	        	row_tipo_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
	        	row_n_documento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
	        	row_saldo_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
	        	row_valor_pago_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
	        }else{
	        	row_tipo_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
	        	row_n_documento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
	        	row_saldo_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
	        	row_valor_pago_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			}
	        return row;
	    }
	}
