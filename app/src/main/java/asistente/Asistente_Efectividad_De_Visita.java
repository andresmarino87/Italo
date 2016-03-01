package asistente;

import java.util.ArrayList;
import java.util.List;
import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class Asistente_Efectividad_De_Visita extends Activity {
	private TextView total_registros_input;
	private ListView listEfectividadVisita;
	private TextView clientes_visitados_input;
	private TextView clientes_con_pedidos_input;
	private TextView clientes_no_visita_input;
	private TextView clientes_no_venta_input;
	private TextView clientes_ruta_input;
	private TextView clientes_extrarutas_input;
	private TextView efectividad_visitas_input;
	private TextView cumplimiento_input;
	private Cursor cursor;
	private Cursor cursor_search;
	private ArrayList<efectividad_visita> visitas;
	private ItaloDBAdapter DBAdapter;
	private efectividadVisitaArrayAdapter adapter;
	private Spinner sector_input;
	private Spinner ruta_input;
	private Spinner periodo_input;
	private RadioButton todos_input;
	private Button buscar_button;
     
	static private String todas_str;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_asistente_efectividad_de_visita);
		init();
		loadList();
		loadSectores();
		if(sector_input.getSelectedItem()!=null){
			loadRutas(sector_input.getSelectedItem().toString());
		}				
		loadPeriodos();
		sector_input.setOnItemSelectedListener((new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				if(sector_input.getSelectedItem()!=null){
					loadRutas(sector_input.getSelectedItem().toString());
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		}));
		listEfectividadVisita.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,long id) {
				loadDetailsData(position);
			}

	    });
		buscar_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(sector_input.getSelectedItem()!=null || ruta_input.getSelectedItem()!= null || periodo_input.getSelectedItem() != null ){
					reloadList(sector_input.getSelectedItem().toString(),ruta_input.getSelectedItem().toString(),periodo_input.getSelectedItem().toString(),todos_input.isChecked());
					clearDetailsData();
				}
			}
		});
	}
	
	private void init(){
		total_registros_input=(TextView)findViewById(R.id.total_registros_input);
		listEfectividadVisita=(ListView)findViewById(R.id.listEfectividadVisita);
		clientes_visitados_input=(TextView)findViewById(R.id.clientes_visitados_input);
		clientes_con_pedidos_input=(TextView)findViewById(R.id.clientes_con_pedidos_input);
		clientes_no_visita_input=(TextView)findViewById(R.id.clientes_no_visita_input);
		clientes_no_venta_input=(TextView)findViewById(R.id.clientes_no_venta_input);
		clientes_ruta_input=(TextView)findViewById(R.id.clientes_ruta_input);
		clientes_extrarutas_input=(TextView)findViewById(R.id.clientes_extrarutas_input);
		efectividad_visitas_input=(TextView)findViewById(R.id.efectividad_visitas_input);
		cumplimiento_input=(TextView)findViewById(R.id.cumplimiento_input);
		buscar_button=(Button)findViewById(R.id.buscar_button);
		sector_input=(Spinner)findViewById(R.id.sector_input);
		ruta_input=(Spinner)findViewById(R.id.ruta_input);
		periodo_input=(Spinner)findViewById(R.id.periodo_input);
		todos_input=(RadioButton)findViewById(R.id.todos_input);
		DBAdapter=new ItaloDBAdapter(this);
		total_registros_input=(TextView) this.findViewById(R.id.total_registros_input);
		visitas=new ArrayList<efectividad_visita>();
		todas_str=getApplicationContext().getString(R.string.todos);
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.asistente_efectividad_de_visita,menu);
		return true;
	}
	
	private void loadSectores(){
		cursor_search=DBAdapter.getAsistenteEfectividadVisitasSectores();
		int i=0;
        String strings[] = new String[cursor_search.getCount()];
        if(cursor_search.moveToFirst()){
			do{
	        	strings[i] = cursor_search.getString(0);
	        	i++;
			}while(cursor_search.moveToNext());
			ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sector_input.setAdapter(adapter);
        }
		return;
	}

	private void loadRutas(String sector){
		cursor_search=DBAdapter.GetRutasPedidosNegados(sector);
		int i=1;
        String strings[] = new String[cursor_search.getCount()+1];
        strings[0]=todas_str;
        if(cursor_search.moveToFirst()){
        	do{
	        	strings[i] = cursor_search.getString(0);
	        	i++;
			}while(cursor_search.moveToNext());
			ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			ruta_input.setAdapter(adapter);
        }
		return;
	}
	
	private void loadPeriodos(){
		cursor_search=DBAdapter.getAsistenteEfectividadVisitasPeriodo();
		int i=1;
        String strings[] = new String[cursor_search.getCount()+1];
        strings[0]=todas_str;
        if(cursor_search.moveToFirst()){
        	do{
	        	strings[i] = cursor_search.getString(0);
	        	i++;
			}while(cursor_search.moveToNext());
			ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			periodo_input.setAdapter(adapter);
        }
		return;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
//			case R.id.buscar:
/*				alertView=getLayoutInflater().inflate(R.layout.search_asistente_efectividad_de_visita, null);
				dialogBuilder = new AlertDialog.Builder(this);
				dialogBuilder.setTitle(R.string.seleccione_filtros);
				dialogBuilder.setView(alertView);
				dialogBuilder.setCancelable(false);
				dialogBuilder.setPositiveButton(R.string.buscar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(sector_input.getSelectedItem()!=null || ruta_input.getSelectedItem()!= null || periodo_input.getSelectedItem() != null ){
							reloadList(sector_input.getSelectedItem().toString(),ruta_input.getSelectedItem().toString(),periodo_input.getSelectedItem().toString(),todos_input.isChecked());
							clearDetailsData();
						}
					}
				});
				dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				dialogBuilder.create().show();*/
//				return true;
	        case R.id.atras:
				finish();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}

	public void loadList(){
		cursor=DBAdapter.getAsistenteEfectividadVisitas();
		loadDatalist();
		adapter=new efectividadVisitaArrayAdapter(getApplicationContext(), R.layout.item_efectividad_visita, visitas);
		listEfectividadVisita.setAdapter(adapter);
		registerForContextMenu(listEfectividadVisita);
		return;
	}
	
	public void loadDatalist(){
		if(cursor.moveToFirst()){
			do{
				visitas.add(new efectividad_visita(cursor.getString(4),
						cursor.getString(6),
						Utility.formatNumber(cursor.getInt(7)),
						Utility.formatNumber(cursor.getInt(8)),
						Utility.formatNumber(cursor.getDouble(9))));
			}while(cursor.moveToNext());
		}
		total_registros_input.setText(Integer.toString(cursor.getCount()));
		return;
	}
	
	private void reloadList(String sector, String ruta, String periodo, boolean todos){
		visitas.clear();
		cursor=DBAdapter.getAsistenteEfectividadVisitas(sector,ruta,periodo,todos);
        loadDatalist();
		adapter.notifyDataSetChanged();
		return;
	}
	
	public class efectividadVisitaArrayAdapter extends ArrayAdapter<efectividad_visita> {
	    private List<efectividad_visita> eF = new ArrayList<efectividad_visita>();
	    private TextView row_ruta_input;
	    private TextView row_fecha_input;
	    private TextView row_clientes_programados_input;
	    private TextView row_clientes_visitados_input;
	    private TextView row_porcentaje_input;

	    public efectividadVisitaArrayAdapter(Context context, int textViewResourceId,List<efectividad_visita> objects) {
	        super(context, textViewResourceId, objects);
	        this.eF = objects;
			notifyDataSetChanged();
	    }

		public int getCount() {
	        return this.eF.size();
	    }

	    public efectividad_visita getItem(int index) {
	        return this.eF.get(index);
	    }

		@SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        if (row == null) {
	            // ROW INFLATION
	        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            row = inflater.inflate(R.layout.item_efectividad_visita, parent, false);
	        }else{}

	        efectividad_visita e = getItem(position);
	        row_ruta_input = (TextView) row.findViewById(R.id.row_ruta_input);
	        row_fecha_input = (TextView) row.findViewById(R.id.row_fecha_input);
	        row_clientes_programados_input = (TextView) row.findViewById(R.id.row_clientes_programados_input);
	        row_clientes_visitados_input = (TextView) row.findViewById(R.id.row_clientes_visitados_input);
	        row_porcentaje_input = (TextView) row.findViewById(R.id.row_porcentaje_input);

		    row_ruta_input.setText(e.getRuta());
		    row_fecha_input.setText(e.getFecha());
		    row_clientes_programados_input.setText(e.getClientesProgramados());
		    row_clientes_visitados_input.setText(e.getClientesVisitados());
		    row_porcentaje_input.setText(e.getCumplimiento());

	        if(position % 2 == 0){
	        	row_ruta_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_fecha_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_clientes_programados_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_clientes_visitados_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_porcentaje_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			}else{
				row_ruta_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_fecha_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_clientes_programados_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_clientes_visitados_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_porcentaje_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
			}
	        return row;
	    }
	}
	
	public class efectividad_visita {
		private String ruta = "";
		private String fecha = "";
		private String clientes_programados = "";
		private String clientes_visitados = "";
		private String cumplimiento = "";

		public efectividad_visita(String ruta, String fecha, String clientes_programados, String clientes_visitados, String cumplimiento){
			this.ruta=ruta;
			this.fecha=fecha;
			this.clientes_programados=clientes_programados;
			this.clientes_visitados=clientes_visitados;
			this.cumplimiento=cumplimiento;
		}
		
		public efectividad_visita(){
			this.ruta="";
			this.fecha="";
			this.clientes_programados="";
			this.clientes_visitados="";
			this.cumplimiento="";
		}		
		/*
		 * 
		 * Getters
		 * 
		 */
		public String getRuta() {
			return ruta;
		}
		
		public String getFecha() {
			return fecha;
		}
		
		public String getClientesProgramados() {
			return clientes_programados;
		}
		
		public String getClientesVisitados() {
			return clientes_visitados;
		}
		
		public String getCumplimiento() {
			return cumplimiento;
		}
	}
	
	private void loadDetailsData(int pos){
		if(cursor.moveToPosition(pos)){
			clientes_visitados_input.setText(Utility.formatNumber(cursor.getDouble(8)));
			clientes_con_pedidos_input.setText(Utility.formatNumber(cursor.getDouble(10)));
			clientes_no_visita_input.setText(Utility.formatNumber(cursor.getDouble(11)));
			clientes_no_venta_input.setText(Utility.formatNumber(cursor.getDouble(12)));
			clientes_ruta_input.setText(Utility.formatNumber(cursor.getDouble(13)));
			clientes_extrarutas_input.setText(Utility.formatNumber(cursor.getDouble(14)));
			efectividad_visitas_input.setText(Utility.formatNumber(cursor.getDouble(15)));
			cumplimiento_input.setText(Utility.formatNumber(cursor.getDouble(9)));
		}
		return;
	}
	
	private void clearDetailsData(){
		clientes_visitados_input.setText("");
		clientes_con_pedidos_input.setText("");
		clientes_no_visita_input.setText("");
		clientes_no_venta_input.setText("");
		clientes_ruta_input.setText("");
		clientes_extrarutas_input.setText("");
		efectividad_visitas_input.setText("");
		cumplimiento_input.setText("");
		return;		
	}
	
	@Override
	public void onBackPressed() {}
}