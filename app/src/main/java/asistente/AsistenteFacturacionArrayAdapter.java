package asistente;

import java.util.ArrayList;
import java.util.List;

import utilidades.Utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.italo_view.R;

public class AsistenteFacturacionArrayAdapter extends ArrayAdapter<Asistente_Facturacion_Item> {
		private Context context;
		private List<Asistente_Facturacion_Item> facturacion = new ArrayList<Asistente_Facturacion_Item>();
		private TextView row_fecha_input;
		private TextView row_valor_input;
		private int layoutId;

		public AsistenteFacturacionArrayAdapter(Context context, int textViewResourceId, List<Asistente_Facturacion_Item> objects) {
			super(context, textViewResourceId, objects);
			this.context = context;
			this.facturacion = objects;
			this.layoutId=textViewResourceId;
			notifyDataSetChanged();
		}

		public int getCount() {
			return this.facturacion.size();
		}

		public Asistente_Facturacion_Item getItem(int index) {
			return this.facturacion.get(index);
		}

		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		public View getView(int position, View convertView, ViewGroup parent) {
			final Asistente_Facturacion_Item f = getItem(position);
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(layoutId,parent, false);
			}
			
			row_fecha_input = (TextView) row.findViewById(R.id.row_fecha_input);
			row_valor_input = (TextView) row.findViewById(R.id.row_valor_input);
			
			row_fecha_input.setText(String.valueOf(f.getFecha()));
			row_valor_input.setText(Utility.formatNumber(f.getValor()));

			if (position % 2 == 0) {
				row_fecha_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
				row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			} else {
				row_fecha_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
				row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			}				
			return row;
		}
	}
