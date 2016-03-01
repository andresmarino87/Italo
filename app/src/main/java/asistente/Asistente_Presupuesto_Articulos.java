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
import android.graphics.Color;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

@SuppressWarnings("deprecation")
public class Asistente_Presupuesto_Articulos extends Activity {
	private ListView listPresupuestoArticulos;
	private TextView input_registros;
	private TextView unidades_ventas_input;
	private TextView unidades_ventas_label;
	private ItaloDBAdapter DBAdapter;
	private Cursor cursor;
	private Cursor cursor_search;
	private ArrayList<presupuesto_articulos> presupuestoArticulos;
	private presupuestoArticulosArrayAdapter adapter;
	static private String todas_str;
	static private boolean por_ventas;
	private Button por_ventas_button;
	private Button por_unidades_button;
	private Spinner sector_input;
	private EditText porcentaje_input;
	private Spinner buscar_por_input;
	private AutoCompleteTextView text_input;
	private Button buscar_button;
	private InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_asistente_presupuesto_articulos);
		init();
		loadList();
		loadSector();
//		loadPorcentajes();	
		loadSearchAutoComplete(buscar_por_input.getSelectedItemPosition());
		buscar_por_input.setOnItemSelectedListener((new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				loadSearchAutoComplete(buscar_por_input.getSelectedItemPosition());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		}));
		por_ventas_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				por_ventas_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.boton_seleccionado_tab_style));
				por_unidades_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.boton_no_seleccionado_tab_style));
				por_ventas_button.setTextColor(Color.WHITE);
				por_unidades_button.setTextColor(Color.BLACK);
				por_ventas=true;
				unidades_ventas_input.setText(R.string.ventas);
				unidades_ventas_label.setText(R.string.ventas);
				reloadList(sector_input.getSelectedItem().toString(),porcentaje_input.getText().toString(),buscar_por_input.getSelectedItemPosition(),text_input.getText().toString(),true);
			}
		});	
		
		
		por_unidades_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				por_ventas_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.boton_no_seleccionado_tab_style));
				por_unidades_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.boton_seleccionado_tab_style));
				por_ventas_button.setTextColor(Color.BLACK);
				por_unidades_button.setTextColor(Color.WHITE);
				por_ventas=false;
				unidades_ventas_input.setText(R.string.unidades);
				unidades_ventas_label.setText(R.string.unidades);
				reloadList(sector_input.getSelectedItem().toString(),porcentaje_input.getText().toString(),buscar_por_input.getSelectedItemPosition(),text_input.getText().toString(),true);
			}
		});
		
		buscar_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				imm.hideSoftInputFromWindow(porcentaje_input.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(text_input.getWindowToken(), 0);
				reloadList(sector_input.getSelectedItem().toString(),porcentaje_input.getText().toString(),buscar_por_input.getSelectedItemPosition(),text_input.getText().toString(),true);
			}});
		return;
	}

	private void init(){
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		listPresupuestoArticulos = (ListView)findViewById(R.id.listPresupuestoArticulos);
		DBAdapter=new ItaloDBAdapter(this);
		input_registros = (TextView)findViewById(R.id.input_registros);
		unidades_ventas_label=(TextView)findViewById(R.id.unidades_ventas_label);
		unidades_ventas_input = (TextView)findViewById(R.id.unidades_ventas_input);
		presupuestoArticulos=new ArrayList<presupuesto_articulos>();
        todas_str=getApplicationContext().getString(R.string.todos);
        por_ventas=true;
        por_ventas_button=(Button)findViewById(R.id.por_ventas_button);
        por_unidades_button=(Button)findViewById(R.id.por_unidades_button);
		sector_input=(Spinner)findViewById(R.id.sector_input);
		porcentaje_input=(EditText)findViewById(R.id.porcentaje_input);
		buscar_por_input=(Spinner)findViewById(R.id.buscar_por_input);
		text_input=(AutoCompleteTextView)findViewById(R.id.text_input);
		buscar_button=(Button) findViewById(R.id.buscar_button);
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.asistente_presupuesto_articulos,menu);
		return true;
	}
	
	private void loadSector(){
		cursor_search=DBAdapter.getSectoresAsistentePresupuestosArticulos();
		int i=1;
        String strings[] = new String[cursor_search.getCount()+1];
        strings[0]=todas_str;
		if(cursor_search.moveToFirst()){
			do{
	        	strings[i] = cursor_search.getString(0);
	        	i++;
			}while(cursor_search.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sector_input.setAdapter(adapter);
		return;
	}
	
	private void loadSearchAutoComplete(int position){
		cursor_search=DBAdapter.getPresupuestoArticulosSearchAuto();
		int i=0;
		String strings[];
		ArrayAdapter<String>  adapter;
		switch(position){
			case 0:
				text_input.setInputType(InputType.TYPE_CLASS_NUMBER);
				break;
			case 1:
				text_input.setInputType(InputType.TYPE_CLASS_NUMBER);
				break;
			case 2:
				text_input.setInputType(InputType.TYPE_CLASS_NUMBER);
				break;
			case 3:
				text_input.setInputType(InputType.TYPE_CLASS_TEXT);
		        strings = new String[cursor_search.getCount()];
				if(cursor_search.moveToFirst()){
					do{
			        	strings[i] = cursor_search.getString(1);
			        	i++;
					}while(cursor_search.moveToNext());
				}
				adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, strings);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				text_input.setAdapter(adapter);
				break;
			case 4:
				break;

		}
		return;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
	        case R.id.atras:
				finish();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}
	
	public class presupuestoArticulosArrayAdapter extends ArrayAdapter<presupuesto_articulos> {
		private List<presupuesto_articulos> presupuestoArticulos = new ArrayList<presupuesto_articulos>();
	    private TextView row_codigo_input;
	    private TextView row_articulo_input;
	    private TextView row_presupuesto_input;
	    private TextView row_unidades_input;
	    private TextView row_porcentaje_input;
	    
	    public presupuestoArticulosArrayAdapter(Context context, int textViewResourceId,List<presupuesto_articulos> objects) {
	        super(context, textViewResourceId, objects);
	        this.presupuestoArticulos = objects;
			notifyDataSetChanged();
	    }

		public int getCount() {
	        return this.presupuestoArticulos.size();
	    }

	    public presupuesto_articulos getItem(int index) {
	        return this.presupuestoArticulos.get(index);
	    }

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
	            // ROW INFLATION
	        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            row = inflater.inflate(R.layout.item_presupuesto_articulos, parent, false);
	        }

	        presupuesto_articulos p = getItem(position);
	        row_codigo_input = (TextView) row.findViewById(R.id.row_codigo_input);
	        row_articulo_input = (TextView) row.findViewById(R.id.row_articulo_input);
	        row_presupuesto_input = (TextView) row.findViewById(R.id.row_presupuesto_input);
	        row_unidades_input = (TextView) row.findViewById(R.id.row_unidades_input);
	        row_porcentaje_input = (TextView) row.findViewById(R.id.row_porcentaje_input);

	        row_codigo_input.setText(p.getCodigo());
	        row_articulo_input.setText(p.getArticulo());
	        row_presupuesto_input.setText(p.getPresupuesto());
	        row_unidades_input.setText(p.getUnidades());
	        row_porcentaje_input.setText(p.getPorcentaje());

	        if(position % 2 == 0){
	        	row_codigo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_articulo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_presupuesto_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_unidades_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_porcentaje_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        }else{
				row_codigo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_articulo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_presupuesto_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_unidades_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_porcentaje_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
			}
			return row;
		}
	}
	
	public class presupuesto_articulos {
		private String codigo = "";
		private String articulo = "";
		private String presupuesto = "";
		private String unidades = "";
		private String porcentaje = "";

		public presupuesto_articulos(String codigo, String articulo, String presupuesto, String unidades, String porcentaje){
			this.codigo=codigo;
			this.articulo=articulo;
			this.presupuesto=presupuesto;
			this.unidades=unidades;
			this.porcentaje=porcentaje;
		}
		
		/*
		 * 
		 * Getters
		 * 
		 */
		public String getCodigo() {
			return codigo;
		}
		
		public String getArticulo() {
			return articulo;
		}
		
		public String getPresupuesto() {
			return presupuesto;
		}
		
		public String getUnidades() {
			return unidades;
		}
		
		public String getPorcentaje() {
			return porcentaje;
		}
	}
	
	void loadList(){
		cursor=DBAdapter.getAsistentePresupuestosArticulos(por_ventas);
		loadDataList();
		adapter=new presupuestoArticulosArrayAdapter(getApplicationContext(), R.layout.item_presupuesto_articulos, presupuestoArticulos);
		listPresupuestoArticulos.setAdapter(adapter);
		return;
	}
	
	private void loadDataList(){
		if(cursor.moveToFirst()){
			do{
				presupuestoArticulos.add(new presupuesto_articulos(
						cursor.getString(0),
						cursor.getString(1),
						Utility.formatNumber(cursor.getDouble(2)),
						Utility.formatNumber(cursor.getDouble(3)),
						Utility.formatNumber(cursor.getDouble(4))));
			}while(cursor.moveToNext());
		}
		input_registros.setText(Integer.toString(cursor.getCount()));
		return;
	}
	
	private void reloadList(String sector,String porcentaje,int porNombre, String buscar,boolean porFiltro){
		String porc=(porcentaje.trim().equals(""))?"0":porcentaje;
		presupuestoArticulos.clear();
		if(porFiltro){
			cursor=DBAdapter.getAsistentePresupuestosArticulosXFiltro(por_ventas,sector,porc,porNombre,buscar);
		}else{
			cursor=DBAdapter.getAsistentePresupuestosArticulos(por_ventas);
		}
		loadDataList();
		adapter.notifyDataSetChanged();
		return;
	}
	
	@Override
	public void onBackPressed() {}
}
