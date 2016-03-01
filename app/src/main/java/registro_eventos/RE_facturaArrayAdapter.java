package registro_eventos;

import java.util.List;

import utilidades.Utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.italo_view.R;

public class RE_facturaArrayAdapter extends ArrayAdapter<RE_factura> {
	private Context context;
	private List<RE_factura> facturas;
	private int layoutId;
	
	public RE_facturaArrayAdapter(Context context, int textViewResourceId,List<RE_factura> objects) {
		super(context, textViewResourceId, objects);
		this.facturas = objects;
		this.context=context;
		this.layoutId=textViewResourceId;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.facturas.size();
	}

	public RE_factura getItem(int index) {
		return this.facturas.get(index);
	}

	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutId, parent, false);
		}

		final RE_factura f = getItem(position);
		final TextView row_doc_input = (TextView) row.findViewById(R.id.row_doc_input);
		final TextView row_tipo_doc_input = (TextView) row.findViewById(R.id.row_tipo_doc_input);
		final TextView row_dias_input = (TextView) row.findViewById(R.id.row_dias_input);
		final TextView row_valor_input = (TextView) row.findViewById(R.id.row_valor_input);
		final TextView row_vencimiento_input = (TextView) row.findViewById(R.id.row_vencimiento_input);
		final TextView row_fecha_fact_input = (TextView) row.findViewById(R.id.row_fecha_fact_input);
		
		row_doc_input.setText(f.getId());
		row_tipo_doc_input.setText(f.getTipoDoc());
		row_dias_input.setText(f.getDias());
		row_valor_input.setText(Utility.formatNumber(f.getValor()));
		row_vencimiento_input.setText(f.getVencimiento());
		row_fecha_fact_input.setText(f.getFechaFact());

		if (position % 2 == 0) {
			row_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_tipo_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_dias_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_vencimiento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_fecha_fact_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		} else {
			row_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_tipo_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_dias_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_vencimiento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_fecha_fact_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		}
		return row;
	}
}
