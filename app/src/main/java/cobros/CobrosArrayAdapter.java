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

public class CobrosArrayAdapter extends ArrayAdapter<C_cobro> {
	    private Context context;
	    private List<C_cobro> cobros = new ArrayList<C_cobro>();

	    public CobrosArrayAdapter(Context context, int textViewResourceId,List<C_cobro> objects) {
	        super(context, textViewResourceId, objects);
	        this.context = context;
	        this.cobros = objects;
			notifyDataSetChanged();
	    }

		public int getCount() {
	        return this.cobros.size();
	    }

	    public C_cobro getItem(int index) {
	        return this.cobros.get(index);
	    }

	    @SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        if (row == null) {
	            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        	row = inflater.inflate(R.layout.item_cobro_cartera, parent, false);
	        }

	        final C_cobro c = getItem(position);
	        final TextView row_doc_input = (TextView) row.findViewById(R.id.row_doc_input);
	        final TextView row_tipo_doc_input = (TextView) row.findViewById(R.id.row_tipo_doc_input);
	        final TextView row_dias_input = (TextView) row.findViewById(R.id.row_dias_input);
	        final TextView row_valor_input = (TextView) row.findViewById(R.id.row_valor_input);
	        final TextView row_saldo_input = (TextView) row.findViewById(R.id.row_saldo_input);
	        final TextView row_vencimiento_input = (TextView) row.findViewById(R.id.row_vencimiento_input);
	        final TextView row_fecha_fact_input = (TextView) row.findViewById(R.id.row_fecha_fact_input);

	        row_doc_input.setText(c.getDoc());
	        row_tipo_doc_input.setText(c.getTipoDoc());
	        row_dias_input.setText(String.valueOf(c.getDias()));
	        row_valor_input.setText(Utility.formatNumber(c.getValor()));
	        row_saldo_input.setText(Utility.formatNumber(c.getSaldo()));
	        row_vencimiento_input.setText(c.getFechaVenc());
	        row_fecha_fact_input.setText(c.getFechaFac());

	        if(position % 2 == 0){
	        	row_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
	        	row_tipo_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
	        	row_dias_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
	        	row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
	        	row_saldo_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
	        	row_vencimiento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
	        	row_fecha_fact_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
	        }else{
	        	row_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
	        	row_tipo_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
	        	row_dias_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
	        	row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
	        	row_saldo_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
	        	row_vencimiento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
	        	row_fecha_fact_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			}
	        return row;
	    }
}
