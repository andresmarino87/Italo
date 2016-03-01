package actividad_diaria;

import java.util.ArrayList;
import java.util.List;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Actividad_Diaria_Programar_Cobro_Pendiente extends Activity {
	private ListView listPendiente;
	static private ArrayList<pendienteRow> pendientes;
	static private pendienteRowArrayAdapter adapterPendiente;
	private ItaloDBAdapter DBAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actividad_diaria_programar_cobro_pendiente);
		init();
		loadTablePendiente();
	}

	private void init() {
		listPendiente = (ListView) findViewById(R.id.list_actividad_diaria_cobro_pendiente);
		DBAdapter = new ItaloDBAdapter(this);
		pendientes = new ArrayList<pendienteRow>();
		return;
	}

	private void loadListDataPendiente() {
		// if(cursor.moveToFirst()){
		// do{
		for (int i = 0; i < 7; i++) {
			pendientes.add(new pendienteRow());
		}
		// }while(cursor.moveToNext());
		// }
		return;
	}

	void loadTablePendiente() {
//		cursor = DBAdapter.getCliente_id();
		loadListDataPendiente();
		adapterPendiente = new pendienteRowArrayAdapter(
				getApplicationContext(), R.layout.item_cartera, pendientes);
		listPendiente.setAdapter(adapterPendiente);
		registerForContextMenu(listPendiente);
	}

	public class pendienteRowArrayAdapter extends ArrayAdapter<pendienteRow> {
		private List<pendienteRow> pendienteRows = new ArrayList<pendienteRow>();
		private TextView documento_row_input;
		private TextView valor_row_input;
		private TextView fecha_fact_row_input;

		public pendienteRowArrayAdapter(Context context,
				int textViewResourceId, List<pendienteRow> objects) {
			super(context, textViewResourceId, objects);
			this.pendienteRows = objects;
			notifyDataSetChanged();
		}

		public int getCount() {
			return this.pendienteRows.size();
		}

		public pendienteRow getItem(int index) {
			return this.pendienteRows.get(index);
		}

		@SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				// ROW INFLATION
				LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.item_pendiente, parent, false);
			} else {}

			pendienteRow f = getItem(position);
			documento_row_input = (TextView) row.findViewById(R.id.documento_row_input);
			valor_row_input = (TextView) row.findViewById(R.id.valor_row_input);
			fecha_fact_row_input = (TextView) row.findViewById(R.id.fecha_fact_row_input);
			documento_row_input.setText(f.getDocumento());
			valor_row_input.setText(f.getValor());
			fecha_fact_row_input.setText(f.getFecha_fact());
			if (position % 2 == 0) {
				valor_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
				documento_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
				fecha_fact_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			} else {
				valor_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				documento_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				fecha_fact_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
			}
			return row;
		}
	}

	public class pendienteRow {
		private String documento;
		private String valor;
		private String fecha_fact;

		public pendienteRow(String documento, String valor, String fecha_fact) {
			this.documento = documento;
			this.valor = valor;
			this.fecha_fact = fecha_fact;
		}

		public pendienteRow() {
			this.documento = "207645";
			this.valor = "$250.000";
			this.fecha_fact = "04/07/12";
		}

		public String getDocumento() {
			return documento;
		}

		public String getValor() {
			return valor;
		}

		public String getFecha_fact() {
			return fecha_fact;
		}

	}
	
	@Override
	public void onBackPressed() {}
}
