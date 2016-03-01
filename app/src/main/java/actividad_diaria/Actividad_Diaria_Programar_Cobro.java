package actividad_diaria;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

public class Actividad_Diaria_Programar_Cobro extends Activity {
	private ListView listFacturas;
	static private ArrayList<facturasRow> facturas;
	private facturasRowArrayAdapter adapterFacturas;
	private ItaloDBAdapter DBAdapter;
	private Button menor_30, mayor_30, mayor_60, mayor_90, todos;
	private TextView fecha;
	private TextView fecha_cobro;
	static private SimpleDateFormat dateFormat;
	private View alertView;
	private AlertDialog.Builder dialogBuilder;
	private CalendarView date_picker;
	private EditText documento;
	private EditText valor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actividad_diaria_programar_cobro);
		init();
		loadTableFacturas();
		listFacturas.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				fecha_cobro.setText(facturas.get(position).getVencimiento());
				documento.setText(facturas.get(position).getDocumento());
				valor.setText(facturas.get(position).getValor());
			}
		});
	}

	@SuppressLint("SimpleDateFormat")
	private void init() {
		listFacturas = (ListView) findViewById(R.id.list_actividad_diaria_cobro_facturas);
		DBAdapter = new ItaloDBAdapter(this);
		facturas = new ArrayList<facturasRow>();
		documento = (EditText) findViewById(R.id.programar_cobro_documento);
		valor = (EditText) findViewById(R.id.programar_cobro_valor);
		fecha = (TextView) findViewById(R.id.Programar_cobro_fecha_hoy);
		dateFormat = new SimpleDateFormat("dd/MM/yy");
		fecha.setText(dateFormat.format(new Date()));
		fecha_cobro = (TextView) findViewById(R.id.programar_cobro_fecha);
		fecha_cobro.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				alertView = getLayoutInflater().inflate(R.layout.date_picker, null);
				dialogBuilder = new AlertDialog.Builder(v.getContext());
				dialogBuilder.setTitle(R.string.modificar_fecha);
				dialogBuilder.setView(alertView);
				date_picker = (CalendarView) alertView.findViewById(R.id.date_picker);
				dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						fecha_cobro.setText(dateFormat.format(date_picker.getDate()));
					}
				});
				dialogBuilder.create().show();
				}
		});
    	menor_30 = (Button) findViewById(R.id.programar_cobro_menor_30);
		menor_30.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				vencimiento("menor_30");
				
			}
		});
		mayor_30 = (Button) findViewById(R.id.programar_cobro_mayor_30);
		mayor_30.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				vencimiento("mayor_30");
				
			}
		});
		mayor_60 = (Button) findViewById(R.id.programar_cobro_mayor_60);
		mayor_60.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				vencimiento("mayor_60");
				
			}
		});
		
		mayor_90 = (Button) findViewById(R.id.programar_cobro_mayor_90);
		mayor_90.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				vencimiento("mayor_90");
				
			}
		});
		
		todos = (Button) findViewById(R.id.programar_cobro_todos);
		todos.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				vencimiento("todos");
				
			}
		});
		return;
	}

	private void loadListDataFacturas() {
		// if(cursor.moveToFirst()){
		// do{
		for (int i = 0; i < 7; i++) {
			facturas.add(new facturasRow());
		}
		// }while(cursor.moveToNext());
		// }
		return;
	}

	void loadTableFacturas() {
		//cursor = DBAdapter.getClientes();
		loadListDataFacturas();
		adapterFacturas = new facturasRowArrayAdapter(getApplicationContext(),
				R.layout.item_cartera, facturas);
		listFacturas.setAdapter(adapterFacturas);
		registerForContextMenu(listFacturas);
	}
	
	void loadTableFacturasVencimiento(String vencimiento) {
		//cursor = DBAdapter.getClientes();
		loadListDataFacturas();
		adapterFacturas = new facturasRowArrayAdapter(getApplicationContext(),
				R.layout.item_cartera, facturas);
		listFacturas.setAdapter(adapterFacturas);
		registerForContextMenu(listFacturas);
	}

	public class facturasRowArrayAdapter extends ArrayAdapter<facturasRow> {
		private List<facturasRow> facturasRows = new ArrayList<facturasRow>();
		private TextView documento_row_input;
		private TextView tdoc_row_input;
		private TextView dias_row_input;
		private TextView valor_row_input;
		private TextView saldo_row_input;
		private TextView vencimiento_row_input;
		private TextView fecha_fact_row_input;

		public facturasRowArrayAdapter(Context context, int textViewResourceId,
				List<facturasRow> objects) {
			super(context, textViewResourceId, objects);
			this.facturasRows = objects;
			notifyDataSetChanged();
		}

		public int getCount() {
			return this.facturasRows.size();
		}

		public facturasRow getItem(int index) {
			return this.facturasRows.get(index);
		}

		@SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				// ROW INFLATION
				LayoutInflater inflater = (LayoutInflater) this.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.item_facturas, parent, false);
			} else {
			}

			facturasRow f = getItem(position);
			documento_row_input = (TextView) row
					.findViewById(R.id.documento_row_input);
			tdoc_row_input = (TextView) row.findViewById(R.id.tdoc_row_input);
			dias_row_input = (TextView) row.findViewById(R.id.dias_row_input);
			valor_row_input = (TextView) row.findViewById(R.id.valor_row_input);
			saldo_row_input = (TextView) row.findViewById(R.id.saldo_row_input);
			vencimiento_row_input = (TextView) row.findViewById(R.id.vencimiento_row_input);
			fecha_fact_row_input = (TextView) row.findViewById(R.id.fecha_fact_row_input);
			documento_row_input.setText(f.getDocumento());
			tdoc_row_input.setText(f.getTdoc());
			dias_row_input.setText(f.getDias());
			valor_row_input.setText(f.getValor());
			saldo_row_input.setText(f.getSaldo());
			vencimiento_row_input.setText(f.getVencimiento());
			fecha_fact_row_input.setText(f.getFecha_fact());
			if (position % 2 == 0) {
				documento_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
				tdoc_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
				dias_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
				valor_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
				saldo_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
				vencimiento_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
				fecha_fact_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			} else {
				documento_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				tdoc_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				dias_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				valor_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				saldo_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				vencimiento_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				fecha_fact_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
			}
			try {
				if (dateFormat.parse(dateFormat.format(new Date())).compareTo(dateFormat.parse(dateFormat.format(dateFormat.parse(f.getVencimiento())))) <=0 ){
					fecha_fact_row_input.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.celdas_verdes));
				}
				else {
					fecha_fact_row_input.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.celdas_rojas));
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return row;
		}
	}

	public class facturasRow {
		private String documento;
		private String tdoc;
		private String dias;
		private String valor;
		private String saldo;
		private String vencimiento;
		private String fecha_fact;

		public facturasRow(String documento, String tdoc, String dias,
				String valor, String saldo, String vencimiento,
				String fecha_fact) {
			this.documento = documento;
			this.tdoc = tdoc;
			this.dias = dias;
			this.valor = valor;
			this.saldo = saldo;
			this.vencimiento = vencimiento;
			this.fecha_fact = fecha_fact;
		}

		public facturasRow() {
			this.documento = "207645";
			this.tdoc = "Factura";
			this.dias = "50";
			this.valor = "$87.546.025";
			this.saldo = "0";
			this.vencimiento = "20/03/13";
			this.fecha_fact = "03/03/13";
		}

		public String getDocumento() {
			return documento;
		}

		public String getTdoc() {
			return tdoc;
		}

		public String getDias() {
			return dias;
		}

		public String getValor() {
			return valor;
		}

		public String getSaldo() {
			return saldo;
		}

		public String getVencimiento() {
			return vencimiento;
		}

		public String getFecha_fact() {
			return fecha_fact;
		}

	}
	
	@SuppressLint("NewApi")
	public void vencimiento(String valor_venc) {
		fecha_cobro.setText("");
		documento.setText("");
		valor.setText("");
		menor_30.setBackground(getResources().getDrawable(R.drawable.boton_no_seleccionado_tab_style));
		mayor_30.setBackground(getResources().getDrawable(R.drawable.boton_no_seleccionado_tab_style));
		mayor_60.setBackground(getResources().getDrawable(R.drawable.boton_no_seleccionado_tab_style));
		mayor_90.setBackground(getResources().getDrawable(R.drawable.boton_no_seleccionado_tab_style));
		todos.setSelected(false);
		
		if (valor_venc.equals("menor_30")) {
			loadTableFacturasVencimiento("-30");
			menor_30.setBackground(getResources().getDrawable(R.drawable.boton_seleccionado_tab_style));
		}
		else if (valor_venc.equals("mayor_30")) {
			loadTableFacturasVencimiento("30");
			mayor_30.setBackground(getResources().getDrawable(R.drawable.boton_seleccionado_tab_style));
		}
		else if (valor_venc.equals("mayor_60")) {
			loadTableFacturasVencimiento("60");
			mayor_60.setBackground(getResources().getDrawable(R.drawable.boton_seleccionado_tab_style));
		}
		else if (valor_venc.equals("mayor_90")) {
			loadTableFacturasVencimiento("90");
			mayor_90.setBackground(getResources().getDrawable(R.drawable.boton_seleccionado_tab_style));
		}
		else if (valor_venc.equals("todos")){
			loadTableFacturas();
			todos.setSelected(true);
		}
	}
	
	@Override
	public void onBackPressed() {}
}
