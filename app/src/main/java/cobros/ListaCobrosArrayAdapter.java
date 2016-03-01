package cobros;

import java.util.ArrayList;
import java.util.List;

import com.italo_view.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListaCobrosArrayAdapter extends ArrayAdapter<C_lista_cobros> {
	private Context context;
	private ArrayList<C_lista_cobros> carteras = new ArrayList<C_lista_cobros>();
	private TextView row_tipo_doc_input;
	private TextView row_numero_doc_input;
	private TextView row_fecha_fact_input;
	private TextView row_fecha_venc_input;
	private TextView row_dias_venc_input;
	
	public ListaCobrosArrayAdapter(Context context, int textViewResourceId,List<C_lista_cobros> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.carteras = (ArrayList<C_lista_cobros>) objects;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.carteras.size();
	}

	public C_lista_cobros getItem(int index) {
		return this.carteras.get(index);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		C_lista_cobros c = getItem(position);
		
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.item_cobros_lista,parent, false);
		}
		
		row_tipo_doc_input = (TextView) row.findViewById(R.id.row_tipo_doc_input);
		row_numero_doc_input = (TextView) row.findViewById(R.id.row_numero_doc_input);
		row_fecha_fact_input = (TextView) row.findViewById(R.id.row_fecha_fact_input);
		row_fecha_venc_input = (TextView) row.findViewById(R.id.row_fecha_venc_input);
		row_dias_venc_input = (TextView) row.findViewById(R.id.row_dias_venc_input);

		row_tipo_doc_input.setText(c.getDocTipo());
		row_numero_doc_input.setText(c.getDocId());
		row_fecha_fact_input.setText(c.getFechaFactura());
		row_fecha_venc_input.setText(c.getFechaVencimiento());
		row_dias_venc_input.setText(String.valueOf(c.getDiasVencimiento()));

		if (position % 2 == 0) {
			row_tipo_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_numero_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_fecha_fact_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_fecha_venc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_dias_venc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		} else {
			row_tipo_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_numero_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_fecha_fact_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_fecha_venc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_dias_venc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		}				
		return row;
	}
}
