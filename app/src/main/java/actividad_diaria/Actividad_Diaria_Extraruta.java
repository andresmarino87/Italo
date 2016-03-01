package actividad_diaria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
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

public class Actividad_Diaria_Extraruta extends Activity {
	private Bundle extras;
	private ListView listExtraruta;
	private TextView fecha_input;
	private ItaloDBAdapter DBAdapter;
	private Cursor cursor;
	private Cursor cursor_rutas;
	private ArrayList<ExtraRuta> extraRutas;
	private extraRutasArrayAdapter adapter;
	private Spinner buscar_por_input;
	private Spinner sector_input;
	private Spinner ruta_input;
	private TextView observaciones;
	private Button buscar_button;
	private AutoCompleteTextView search_input;
	static private String de;
	static private String n_plan;
	static private String fecha;
	static private String tipoExtra;
	static private String[] sectores;
	static private String[] rutas;
	static private String[] codigos;
	static private String[] nombres;
	private Context context;
	static private int isToday;
	private InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actividad_diaria_ir_a_extraruta);
		init();
		loadList();
		loadSearchAdapters();
		loadSector();
		loadRutas(sector_input.getSelectedItem().toString());
		loadSearchAutoComplete(buscar_por_input.getSelectedItemPosition());
		sector_input.setOnItemSelectedListener((new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				loadRutas(sector_input.getSelectedItem().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		}));
		buscar_por_input.setOnItemSelectedListener((new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				loadSearchAutoComplete(buscar_por_input.getSelectedItemPosition());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		}));
		buscar_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				reloadList(sector_input.getSelectedItem().toString(),ruta_input.getSelectedItem().toString(),buscar_por_input.getSelectedItemPosition(),search_input.getText().toString());
				imm.hideSoftInputFromWindow(search_input.getWindowToken(), 0);

			}
		});
		return;
	}
	
	private void init(){
		context=this;
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		extras = getIntent().getExtras();
		de=extras.getString("de");
		n_plan=extras.getString("n_plan");
		fecha=extras.getString("fecha");
		isToday=extras.getInt("isToday");
		tipoExtra= (isToday==0) ? "E":"P";
		listExtraruta = (ListView)findViewById(R.id.listExtraruta);
		fecha_input=(TextView)findViewById(R.id.fecha_input);
		buscar_button=(Button)findViewById(R.id.buscar_button);
		buscar_por_input=(Spinner)findViewById(R.id.buscar_por_input);
		sector_input=(Spinner)findViewById(R.id.sector_input);
		ruta_input=(Spinner)findViewById(R.id.ruta_input);
		search_input=(AutoCompleteTextView)findViewById(R.id.search_input);
		fecha_input.setText(fecha);
		DBAdapter=new ItaloDBAdapter(this);
		extraRutas=new ArrayList<ExtraRuta>();
		return;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.actividad_diaria_ir_a_extraruta, menu);
        return true;
    }

	private void loadSector(){
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sectores);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sector_input.setAdapter(adapter);
		return;
	}

	private void loadRutas(String sector){
		int i=1;
		if(de.equalsIgnoreCase("actividad_diaria_consultar")){
			cursor_rutas=DBAdapter.getRutasExtraRutaConsultar(n_plan, sector);
		}else if(de.equalsIgnoreCase("actividad_diaria_crear")){
			cursor_rutas=DBAdapter.getRutasExtraRutaCrear(sector);
		}
		

		rutas=new String[cursor_rutas.getCount()+1];
		rutas[0]=getString(R.string.todas);
		if(cursor_rutas.moveToFirst()){
//			rutas=new String[cursor_rutas.getCount()+1];
//			rutas[0]=getString(R.string.todas);
			do{
				rutas[i]=cursor_rutas.getString(0);
				i++;
			}while(cursor_rutas.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, rutas);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ruta_input.setAdapter(adapter);
		return;
	}

	private void loadSearchAutoComplete(int tipo){
		ArrayAdapter<String>  adapter;
		switch(tipo){
			case 0:
				search_input.setInputType(InputType.TYPE_CLASS_NUMBER);
				adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, codigos);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				search_input.setAdapter(adapter);
				break;
			case 1:
				search_input.setInputType(InputType.TYPE_CLASS_TEXT);
				adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, nombres);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				search_input.setAdapter(adapter);
				break;
		}
		return;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	extras = getIntent().getExtras();
    	switch (item.getItemId()){
			case R.id.atras:
				finish();
        		return true;
	    	case R.id.programar_cobro:
    			return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
    } 
    
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
	    super.onCreateContextMenu(menu, v, menuInfo);
	    getMenuInflater().inflate(R.menu.menu_adicionar_observaciones, menu);
	}
	
	@SuppressLint("InflateParams") @Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.adicionar:
				if(de.equalsIgnoreCase("actividad_diaria_consultar")){
		        	if(DBAdapter.addExtraRutaConsulta(n_plan, extraRutas.get(info.position), tipoExtra)){
		        		finish();
		        	}else{
		        		
		        	}
					
				}else if(de.equalsIgnoreCase("actividad_diaria_crear")){
		        	if(DBAdapter.addExtraRutaCrear(n_plan, extraRutas.get(info.position))){
						finish();
		        	}else{
		        	}
				}
				return true;
	        case R.id.observaciones:
				final ExtraRuta aux=extraRutas.get(info.position);
   				final View alertView=getLayoutInflater().inflate(R.layout.observaciones_editable, null);		
   				final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
				dialogBuilder.setTitle(R.string.observaciones);
				dialogBuilder.setView(alertView);
				dialogBuilder.setCancelable(false);
				observaciones=(EditText)alertView.findViewById(R.id.observaciones);
				observaciones.setText(aux.getObservacion());
				dialogBuilder.setPositiveButton(R.string.guardar,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						aux.setObservacion(observaciones.getText().toString());
					}
				});
				dialogBuilder.setNegativeButton(R.string.cancelar,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
        		dialogBuilder.create().show();
				return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}

	public class extraRutasArrayAdapter extends ArrayAdapter<ExtraRuta> {
	    private List<ExtraRuta> extraRutas = new ArrayList<ExtraRuta>();
	    private int layoutId;

	    public extraRutasArrayAdapter(Context context, int textViewResourceId,List<ExtraRuta> objects) {
	        super(context, textViewResourceId, objects);
	        this.extraRutas = objects;
	        this.layoutId=textViewResourceId;
			notifyDataSetChanged();
	    }

		public int getCount() {
	        return this.extraRutas.size();
	    }

	    public ExtraRuta getItem(int index) {
	        return this.extraRutas.get(index);
	    }

		@SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        if (row == null) {
	        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            row = inflater.inflate(layoutId, parent, false);
	        }

	        final ExtraRuta e = getItem(position);
	        final TextView row_ruta_input = (TextView) row.findViewById(R.id.row_ruta_input);
	        final TextView row_codigo_input = (TextView) row.findViewById(R.id.row_codigo_input);
	        final TextView row_cliente_input = (TextView) row.findViewById(R.id.row_cliente_input);
	        final TextView row_venta_proy_input = (TextView) row.findViewById(R.id.row_venta_proy_input);
	        final TextView row_venta_real_input = (TextView) row.findViewById(R.id.row_venta_real_input);
	        final TextView row_cobro_proy_input = (TextView) row.findViewById(R.id.row_cobro_proy_input);
	        final TextView row_cobro_real_input = (TextView) row.findViewById(R.id.row_cobro_real_input);
	        final TextView row_cartera_venc_input = (TextView) row.findViewById(R.id.row_cartera_venc_input);

	        row_ruta_input.setText(e.getRutaID());
	        row_codigo_input.setText(e.getClienteId());
	        row_cliente_input.setText(e.getClienteNombre());
	        row_venta_proy_input.setText(Utility.formatNumber(e.getVentaProy()));
	        row_venta_real_input.setText(Utility.formatNumber(0));
	        row_cobro_proy_input.setText(Utility.formatNumber(e.getCobroProy()));
	        row_cobro_real_input.setText(Utility.formatNumber(0));
	        row_cartera_venc_input.setText(e.getCarteraVenc());

	        if(position % 2 == 0){
	        	row_ruta_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_codigo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_cliente_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_venta_proy_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_venta_real_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_cobro_proy_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_cobro_real_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_cartera_venc_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        }else{
	        	row_ruta_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_codigo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_cliente_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_venta_proy_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_venta_real_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_cobro_proy_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_cobro_real_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_cartera_venc_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
			}
	        return row;
	    }
	}
	
	void loadList(){
		if(de.equalsIgnoreCase("actividad_diaria_consultar")){
			cursor=DBAdapter.getClientesParaExtraRutaConsulta(n_plan);
		}else{
			cursor=DBAdapter.getClientesParaExtraRutaCrear();
		}
		loadDataList();
		adapter=new extraRutasArrayAdapter(getApplicationContext(), R.layout.item_extraruta, extraRutas);
		listExtraruta.setAdapter(adapter);
		registerForContextMenu(listExtraruta);
		return;
	}
	
	private void loadDataList(){
		if(cursor.moveToFirst()){
			do{
				extraRutas.add(new ExtraRuta(
						cursor.getString(0),
						cursor.getString(1),
						cursor.getString(2),
						cursor.getString(3),
						cursor.getString(4),
						cursor.getString(5),
						cursor.getString(6),
						cursor.getDouble(7),
						cursor.getDouble(8),
						cursor.getString(9),""));
			}while(cursor.moveToNext());
		}
		return;
	}
	
	private void reloadList(String sector,String ruta,int buscarPor,String busqueda){
		boolean isConsulta;
		extraRutas.clear();
		if(de.equalsIgnoreCase("actividad_diaria_consultar")){
			cursor=DBAdapter.loadExtraRutaConsultaXFiltros(n_plan,sector, ruta, buscarPor, busqueda);
			isConsulta=true;
		}else{
			cursor=DBAdapter.loadExtraRutaCrearXFiltros(sector, ruta, buscarPor, busqueda);
			isConsulta=false;
		}
		if(cursor.getCount()==0){
			if(isConsulta){
				if(DBAdapter.existInSearchInConsultaPlan(n_plan,sector, ruta, buscarPor, busqueda)){
					Utility.showMessage(context, R.string.cliente_ya_esta_en_el_plan_de_trabajo);
				}else{
					Utility.showMessage(context, R.string.cliente_no_se_encuentra_en_la_base_de_datos);
				}
			}else{
				if(DBAdapter.existInSearchInCrearPlan(sector, ruta, buscarPor, busqueda)){
					Utility.showMessage(context, R.string.cliente_ya_esta_en_el_plan_de_trabajo);
				}else{
					Utility.showMessage(context, R.string.cliente_no_se_encuentra_en_la_base_de_datos);
				}
			}
		}
		loadDataList();
		adapter.notifyDataSetChanged();
		return;
	}
	
	private void loadSearchAdapters(){
		int i=0;
		int tam=extraRutas.size();
		sectores=new String[tam+1];
		codigos=new String[tam];
		nombres=new String[tam];
		sectores[0]=getString(R.string.todas);
		for(ExtraRuta aux:extraRutas){
			sectores[i+1]=aux.getSectorId();
			codigos[i]=aux.getClienteId();
			nombres[i]=aux.getClienteNombre();
			i++;
		}
		sectores = new HashSet<String>(Arrays.asList(sectores)).toArray(new String[0]);
		codigos = new HashSet<String>(Arrays.asList(codigos)).toArray(new String[0]);
		nombres = new HashSet<String>(Arrays.asList(nombres)).toArray(new String[0]);
		Arrays.sort(sectores, Collections.reverseOrder());
	}
	
	@Override
	public void onBackPressed() {}
}
