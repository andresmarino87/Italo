package cliente;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import bd_utilidades.ItaloDBAdapter;

import cartera.Cartera;
import cobros.Cobros_Del_Dia_Consultas;

import com.italo_view.R;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

@SuppressWarnings("deprecation")
public class Cliente extends Activity implements OnTouchListener{
	private Intent i;
	private ListView listCliente;
	private TextView total_rows;
	private TextView lunes;
	private TextView martes;
	private TextView miercoles;
	private TextView jueves;
	private TextView viernes;
	private TextView sabado;
	private Calendar calendar;
	private ItaloDBAdapter DBAdapter;
	private Cursor cursor;
	private Cursor cursor_search;
	private Spinner buscar_por_search;
	private Spinner sector_search;
	private Spinner ruta_search;
	private AutoCompleteTextView text_input_search;
	private clienteRowArrayAdapter adapter;
	private ArrayList<clienteRow> clientes;
	private Button buscar_button;
	static private SimpleDateFormat dateFormat;
	static private int dayMarked;
	static private String todas_str;
	static private String todayIs="";
	static private String FLunes="";
	static private String FMartes="";
	static private String FMiercoles="";
	static private String FJueves="";
	static private String FViernes="";
	static private String FSabado="";
	private InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cliente);
		init();
		setDayActualDay();
		loadTable();
		setDaysDates();
		loadSector();
		if(sector_search.getSelectedItem()!=null){
			loadRutas(sector_search.getSelectedItem().toString());
		}
		loadSearchAutoComplete(buscar_por_search.getSelectedItemPosition());
		sector_search.setOnItemSelectedListener((new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				if(sector_search.getSelectedItem()!=null){
					loadRutas(sector_search.getSelectedItem().toString());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		}));
		buscar_por_search.setOnItemSelectedListener((new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				loadSearchAutoComplete(buscar_por_search.getSelectedItemPosition());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		}));
		buscar_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				try{
					if(sector_search.getSelectedItem()!=null && ruta_search.getSelectedItem()!=null){
						reloadList(sector_search.getSelectedItem().toString(),ruta_search.getSelectedItem().toString(),buscar_por_search.getSelectedItemPosition(),text_input_search.getText().toString());
					}							
				}catch(Exception e){
					
				}
				imm.hideSoftInputFromWindow(text_input_search.getWindowToken(), 0);
			}
		});
	}

	@SuppressLint("SimpleDateFormat")
	private void init(){
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		Date now = new Date();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		todayIs=dateFormat.format(now);
		listCliente=(ListView) findViewById(R.id.listCliente);
		lunes=(TextView)findViewById(R.id.lunes);
		martes=(TextView)findViewById(R.id.martes);
		miercoles=(TextView)findViewById(R.id.miercoles);
		jueves=(TextView)findViewById(R.id.jueves);
		viernes=(TextView)findViewById(R.id.viernes);
		sabado=(TextView)findViewById(R.id.sabado);
		sector_search=(Spinner)findViewById(R.id.sector_search);
		ruta_search=(Spinner)findViewById(R.id.ruta_search);
		buscar_por_search=(Spinner)findViewById(R.id.buscar_por_search);
		text_input_search=(AutoCompleteTextView)findViewById(R.id.text_input_search);
		buscar_button=(Button)findViewById(R.id.buscar_button);
		lunes.setOnTouchListener(this);
		martes.setOnTouchListener(this);
		miercoles.setOnTouchListener(this);
		jueves.setOnTouchListener(this);
		viernes.setOnTouchListener(this);
		sabado.setOnTouchListener(this);
		DBAdapter=new ItaloDBAdapter(this);
		clientes=new ArrayList<clienteRow>();
        total_rows = (TextView)findViewById(R.id.total_rows);
        todas_str=getApplicationContext().getString(R.string.todas);
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.cliente, menu);
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, 2, Menu.NONE, R.string.datos_del_cliente);
		menu.add(Menu.NONE, 5, Menu.NONE, R.string.horarios);
		menu.add(Menu.NONE, 6, Menu.NONE, R.string.contactos);
		menu.add(Menu.NONE, 7, Menu.NONE, R.string.sucursales);
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.pedidos);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.cobros);
		menu.add(Menu.NONE, 3, Menu.NONE, R.string.cartera);
		menu.add(Menu.NONE, 8, Menu.NONE, R.string.detalle_visita);
		menu.add(Menu.NONE, 9, Menu.NONE, R.string.historicos);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final clienteRow aux= clientes.get(info.position);
		switch(item.getItemId()){
			case 0:
				i = new Intent(getApplicationContext(), Cliente_Consultas_Pedidos.class);
				i.putExtra("cliente_id", aux.getCodigo());
				i.putExtra("cliente_nombre", aux.getCliente());
				i.putExtra("de", "cliente");
				startActivity(i);
				return true;
			case 1:
				i = new Intent(getApplicationContext(), Cobros_Del_Dia_Consultas.class);
				i.putExtra("cliente_id", aux.getCodigo());
				i.putExtra("cliente_nombre", aux.getCliente());
				i.putExtra("de", "cliente");
				startActivity(i);
				return true;
			case 2:
				i = new Intent(getApplicationContext(), Cliente_Consultas_Datos_Del_Cliente_Tabs.class);
				i.putExtra("cliente_id", aux.getCodigo());
				i.putExtra("cliente_nombre", aux.getCliente());
				i.putExtra("de", "cliente");
				startActivity(i);
				return true;
			case 3:
				i = new Intent(getApplicationContext(), Cartera.class);
				i.putExtra("cliente_id", aux.getCodigo());
				i.putExtra("cliente_nombre", aux.getCliente());
				startActivity(i);
				return true;
			case 5:
				i = new Intent(getApplicationContext(), Cliente_Horarios.class);
				i.putExtra("cliente_id", aux.getCodigo());
				i.putExtra("cliente_nombre", aux.getCliente());
				startActivity(i);
				return true;
			case 6:
				i = new Intent(getApplicationContext(), Cliente_Contactos.class);
				i.putExtra("cliente_id", aux.getCodigo());
				i.putExtra("cliente_nombre", aux.getCliente());
			startActivity(i);
				return true;
			case 7:
				i = new Intent(getApplicationContext(), Cliente_Sucursales.class);
				i.putExtra("cliente_id", aux.getCodigo());
				i.putExtra("cliente_nombre", aux.getCliente());
				startActivity(i);
				return true;
			case 8:
				i = new Intent(getApplicationContext(), Cliente_Consultas_Otros_Eventos.class);
				i.putExtra("cliente_id", aux.getCodigo());
				i.putExtra("cliente_nombre", aux.getCliente());
				startActivity(i);
				return true;
			case 9:
				i = new Intent(getApplicationContext(), Cliente_Consultas_Historicos_Tabs.class);
				i.putExtra("cliente_id", aux.getCodigo());
				i.putExtra("cliente_nombre", aux.getCliente());
				startActivity(i);
				return true;

			default:
				return true;
		}
	}
	
	private void loadSector(){
		cursor_search=DBAdapter.getClienteSectores();
		String strings[];
		if(cursor_search.getCount()>1){
			int i=1;
	        strings = new String[cursor_search.getCount()+1];
	        strings[0]=getString(R.string.todos);
			if(cursor_search.moveToFirst()){
				do{
		        	strings[i] = cursor_search.getString(0);
		        	i++;
				}while(cursor_search.moveToNext());
			}
		}else{
			int i=0;
	        strings = new String[cursor_search.getCount()];
			if(cursor_search.moveToFirst()){
				do{
		        	strings[i] = cursor_search.getString(0);
		        	i++;
				}while(cursor_search.moveToNext());
			}
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sector_search.setAdapter(adapter);
		return;
	}

	private void loadRutas(String sector){
		cursor_search=DBAdapter.getClienteRutas(sector);
		int i=1;
        String strings[] = new String[cursor_search.getCount()+1];
    	strings[0] = todas_str;
        if(cursor_search.moveToFirst()){
			do{
	        	strings[i] = cursor_search.getString(0);
	        	i++;
			}while(cursor_search.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ruta_search.setAdapter(adapter);
		return;
	}

	private void loadSearchAutoComplete(int tipo){
		cursor_search=DBAdapter.getClienteSearchAuto();
		int i=0; 
		String strings[]=null;
		ArrayAdapter<String>  adapter=null;
		switch(tipo){
		case 0:
			strings = new String[cursor_search.getCount()];
			if(cursor_search.moveToFirst()){
				do{
		        	strings[i] = cursor_search.getString(0);
		        	i++;
				}while(cursor_search.moveToNext());
			}
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, strings);
			text_input_search.setAdapter(adapter);
			break;
		case 1:
			strings = new String[cursor_search.getCount()];
			if(cursor_search.moveToFirst()){
				do{
		        	strings[i] = cursor_search.getString(1);
		        	i++;
				}while(cursor_search.moveToNext());
			}
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, strings);
			text_input_search.setAdapter(adapter);
			break;
		case 2:
	        strings = new String[cursor_search.getCount()];
			if(cursor_search.moveToFirst()){
				do{
		        	strings[i] = cursor_search.getString(2);
		        	i++;
				}while(cursor_search.moveToNext());
			}
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, strings);
			text_input_search.setAdapter(adapter);
			break;
		}
		return;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
    		case R.id.atras:
    			if(cursor!=null){
    				cursor.close();
    			}
				finish();
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }
	
	private void loadListData(){
		int tam=cursor.getCount();
		if(cursor.moveToFirst()){
			do{
				clientes.add(new clienteRow(
           			cursor.getString(2),
           			cursor.getString(3),
           			cursor.getString(0),
           			cursor.getString(1)
				));
			}while(cursor.moveToNext());
		}
        total_rows.setText(String.valueOf(tam));
        return;
	}
	
	public class clienteRowArrayAdapter extends ArrayAdapter<clienteRow> {
	    private List<clienteRow> clienteRows = new ArrayList<clienteRow>();
	    private int layoutId;
	    
	    public clienteRowArrayAdapter(Context context, int textViewResourceId,List<clienteRow> objects) {
	        super(context, textViewResourceId, objects);
	        this.clienteRows = objects;
	        this.layoutId=textViewResourceId;
			notifyDataSetChanged();
	    }

		public int getCount() {
	        return this.clienteRows.size();
	    }

	    public clienteRow getItem(int index) {
	        return this.clienteRows.get(index);
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        if (row == null) {
	            // ROW INFLATION
	        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            row = inflater.inflate(layoutId, parent, false);
	        }

	        final clienteRow c = getItem(position);
	        final TextView tejido_row_input = (TextView) row.findViewById(R.id.tejido_row_input);
	        final TextView ruta_row_input = (TextView) row.findViewById(R.id.ruta_row_input);
	        final TextView codigo_row_input = (TextView) row.findViewById(R.id.codigo_row_input);
	        final TextView cliente_row_input = (TextView) row.findViewById(R.id.cliente_row_input);

	        tejido_row_input.setText(c.getTejido());
	        ruta_row_input.setText(c.getRuta());
	        codigo_row_input.setText(c.getCodigo());
	        cliente_row_input.setText(c.getCliente());

	        if(position % 2 == 0){
		        tejido_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
		        ruta_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
		        codigo_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
		        cliente_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			}else{
		        tejido_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
		        ruta_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
		        codigo_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
		        cliente_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
			}
	        return row;
	    }
	}
	
	private boolean setDayActualDay(){
		calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        dayMarked=day;
        switch (day){
			case 2:
				setDay(lunes);
				break;
			case 3:
				setDay(martes);
				break;
			case 4:
				setDay(miercoles);
				break;
			case 5:
				setDay(jueves);
				break;
			case 6:
				setDay(viernes);
				break;
			case 7:
				setDay(sabado);
				break;
			default:
				break;
        }
		return true;
	}
	
	private boolean changeDay(int dayToChange){
		clearDay();
		dayMarked=dayToChange;
		switch (dayToChange){
			case 2:
				lunes.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_azul));
				lunes.setTextColor(Color.WHITE);
				break;
			case 3:
				martes.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_azul));
				martes.setTextColor(Color.WHITE);
				break;
			case 4:
				miercoles.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_azul));
				miercoles.setTextColor(Color.WHITE);
				break;
			case 5:
				jueves.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_azul));
				jueves.setTextColor(Color.WHITE);
				break;
			case 6:
				viernes.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_azul));
				viernes.setTextColor(Color.WHITE);
				break;
			case 7:
				sabado.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_azul));
				sabado.setTextColor(Color.WHITE);
				break;
			default:
				break;
        }
		
		return true;
	}
	
	private boolean setDay(TextView day){
		day.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_azul));
		day.setTextColor(Color.WHITE);
		return true;
	}
	
	private boolean clearDay(){
		switch (dayMarked){
			case 2:
				lunes.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_blanco));
				lunes.setTextColor(Color.BLACK);
				break;
			case 3:
				martes.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_blanco));
				martes.setTextColor(Color.BLACK);
				break;
			case 4:
				miercoles.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_blanco));
				miercoles.setTextColor(Color.BLACK);
				break;
			case 5:
				jueves.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_blanco));
				jueves.setTextColor(Color.BLACK);
				break;
			case 6:
				viernes.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_blanco));
				viernes.setTextColor(Color.BLACK);
				break;
			case 7:
				sabado.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_blanco));
				sabado.setTextColor(Color.BLACK);
				break;
			default:
				break;
		}
		return true;
	}
	
	@SuppressLint("ClickableViewAccessibility") 
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		switch (arg0.getId()){
			case R.id.lunes:
				reloadList(FLunes);
				changeDay(2);
				break;
			case R.id.martes:
				reloadList(FMartes);
				changeDay(3);
				break;
			case R.id.miercoles:
				reloadList(FMiercoles);
				changeDay(4);
				break;
			case R.id.jueves:
				reloadList(FJueves);
				changeDay(5);
				break;
			case R.id.viernes:
				reloadList(FViernes);
				changeDay(6);
				break;
			case R.id.sabado:
				reloadList(FSabado);
				changeDay(7);
				break;
			default:
				break;
		}
		return false;
	}
	
	void loadTable(){
        cursor=DBAdapter.getClientesXFecha(todayIs);
        loadListData();
        adapter=new clienteRowArrayAdapter(getApplicationContext(), R.layout.item_cliente, clientes);
		listCliente.setAdapter(adapter);
		registerForContextMenu(listCliente);
		return;
	}
	
	private void reloadList(String sector,String ruta,int buscarPor,String busqueda){
		clientes.clear();
		cursor=DBAdapter.getClientesByFilters(sector,ruta,buscarPor,busqueda);
		loadListData();
		adapter.notifyDataSetChanged();
		return;
	}
	
	private void reloadList(String fecha){
		clientes.clear();
		cursor=DBAdapter.getClientesXFecha(fecha);
		loadListData();
		adapter.notifyDataSetChanged();
		return;
	}
	
	private void setDaysDates(){
		String [] aux=todayIs.split("-");
		try{
			GregorianCalendar c = new GregorianCalendar(Integer.parseInt(aux[0]), Integer.parseInt(aux[1])-1, Integer.parseInt(aux[2]));
			c.add(Calendar.DAY_OF_YEAR, Calendar.MONDAY - c.get(Calendar.DAY_OF_WEEK));
			while(c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
	        	if(c.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY){
	        		FLunes=dateFormat.format(c.getTime());
	        	}else if(c.get(Calendar.DAY_OF_WEEK)==Calendar.TUESDAY){
	        		FMartes=dateFormat.format(c.getTime());
	        	}else if(c.get(Calendar.DAY_OF_WEEK)==Calendar.WEDNESDAY){
	        		FMiercoles=dateFormat.format(c.getTime());
	        	}else if(c.get(Calendar.DAY_OF_WEEK)==Calendar.THURSDAY){
	        		FJueves=dateFormat.format(c.getTime());
	        	}else if(c.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY){
	        		FViernes=dateFormat.format(c.getTime());
	        	}else if(c.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
	        		FSabado=dateFormat.format(c.getTime());
	        	}
	            c.add(Calendar.DAY_OF_YEAR, 1);
	        }		
		}catch(Exception e){
    		FLunes="";
    		FMartes="";
    		FMiercoles="";
    		FJueves="";
    		FViernes="";
    		FSabado="";
		}		
		return;
	}
	
	@Override
	protected void onResume(){
		super.onResume();
	}
	
	@Override
	public void onBackPressed() {}
}