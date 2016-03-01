package asistente;

import java.util.ArrayList;
import java.util.List;

import utilidades.Utility;

import com.italo_view.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AsistentePresupuestoVentasArrayAdapter extends ArrayAdapter<Asistente_Presupuesto_Ventas_Item> {
	private Context context;
	private List<Asistente_Presupuesto_Ventas_Item> ventas = new ArrayList<Asistente_Presupuesto_Ventas_Item>();
	private TextView row_nombre_input;
	private TextView row_presupuesto_input;
	private TextView row_valor_input;
	private TextView row_porcentaje_input;
	private int layoutId;

	public AsistentePresupuestoVentasArrayAdapter(Context context, int textViewResourceId, List<Asistente_Presupuesto_Ventas_Item> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.ventas = objects;
		this.layoutId=textViewResourceId;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.ventas.size();
	}
	
	public Asistente_Presupuesto_Ventas_Item getItem(int index) {
		return this.ventas.get(index);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent) {
		final Asistente_Presupuesto_Ventas_Item f = getItem(position);
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutId,parent, false);
		}

		row_nombre_input = (TextView) row.findViewById(R.id.row_nombre_input);
		row_presupuesto_input = (TextView) row.findViewById(R.id.row_presupuesto_input);
		row_valor_input = (TextView) row.findViewById(R.id.row_valor_input);
		row_porcentaje_input = (TextView) row.findViewById(R.id.row_porcentaje_input);
				
		row_nombre_input.setText(f.getNombre());
		row_presupuesto_input.setText(Utility.formatNumber(f.getPresupuesto()));
		row_valor_input.setText(Utility.formatNumber(f.getVenta()));
		row_porcentaje_input.setText(Utility.formatNumber(f.getPorcentaje()));

		if (position % 2 == 0) {
			row_nombre_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_presupuesto_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_porcentaje_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		} else {
			row_nombre_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_presupuesto_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_porcentaje_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		}				
		return row;
	}
}
