package articulos;

import java.util.ArrayList;
import java.util.List;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

@SuppressWarnings("deprecation")
public class Articulos extends Activity {
	private Intent i;
	private ListView listArticulos;
	private TextView row_input;
	private ItaloDBAdapter DBAdapter;
	private Cursor cursor;
    private Cursor cursor_familia;
	private Cursor cursor_subfamilia;
	private Cursor cursor_search;
	private ArrayList<articulo> articulos;
	private articuloArrayAdapter adapter;
	private Spinner buscar_por_input;
	private Spinner familia_input;
	private Spinner subfamilia_input;
	private AutoCompleteTextView search_input;
	private Button buscar_button;
	static private String todos_str;
	static private String familia_id;
	static private String subfamilia_id;
	private InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_articulos);
		init();
		loadList();
		loadFamilias();
		if(familia_input.getSelectedItemPosition()==0){
			familia_id="todos";
		}else{
			if(cursor_familia.moveToPosition(familia_input.getSelectedItemPosition()-1)){
				familia_id=cursor_familia.getString(0);
			}
		}
		loadSubfamilias(familia_id);
		loadSearchAutoComplete(buscar_por_input.getSelectedItemPosition());
		familia_input.setOnItemSelectedListener((new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				if(familia_input.getSelectedItemPosition()==0){
					familia_id="todos";
				}else{
					if(cursor_familia.moveToPosition(familia_input.getSelectedItemPosition()-1)){
						familia_id=cursor_familia.getString(0);
					}
				}

				loadSubfamilias(familia_id);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0){}
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
			public void onClick(View v) {
				if(familia_input.getSelectedItemPosition()==0){
					familia_id="todos";
				}else{
					if(cursor_familia.moveToPosition(familia_input.getSelectedItemPosition()-1)){
						familia_id=cursor_familia.getString(0);
					}
				}
				if(subfamilia_input.getSelectedItemPosition()==0){
					subfamilia_id="todos";
				}else{
					if(cursor_subfamilia.moveToPosition(subfamilia_input.getSelectedItemPosition()-1)){
						subfamilia_id=cursor_subfamilia.getString(0);
					}
				}
				reloadList(buscar_por_input.getSelectedItemPosition(),familia_id,subfamilia_id,search_input.getText().toString());
				imm.hideSoftInputFromWindow(search_input.getWindowToken(), 0);

			}
		});
	}

	private void init(){
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		listArticulos = (ListView)findViewById(R.id.listArticulos);
		DBAdapter=new ItaloDBAdapter(this);
        row_input = (TextView)findViewById(R.id.row_input);
        todos_str=getString(R.string.todos);
        buscar_button=(Button)findViewById(R.id.buscar_button);
		familia_input=(Spinner)findViewById(R.id.familia_input);
		subfamilia_input=(Spinner)findViewById(R.id.subfamilia_input);
		buscar_por_input=(Spinner)findViewById(R.id.buscar_por_input);
		search_input=(AutoCompleteTextView)findViewById(R.id.search_input);
        articulos=new ArrayList<articulo>();
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.articulos, menu);
		return true;
	}

	private void loadFamilias(){
		cursor_familia=DBAdapter.getArticulosFamilias();
		int i=1;
		String strings[] = new String[cursor_familia.getCount()+1];
        strings[0]=todos_str;
        if(cursor_familia.moveToFirst()){
			do{
	        	strings[i] = cursor_familia.getString(1);
	        	i++;
			}while(cursor_familia.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		familia_input.setAdapter(adapter);
		return;
	}

	private void loadSubfamilias(String familia_id){
		cursor_subfamilia=DBAdapter.getArticulosSubfamilias(familia_id);
		int i=1;
        String strings[] = new String[cursor_subfamilia.getCount()+1];
        strings[0]=todos_str;
		if(cursor_subfamilia.moveToFirst()){
			do{
	        	strings[i] = cursor_subfamilia.getString(1);
	        	i++;
			}while(cursor_subfamilia.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		subfamilia_input.setAdapter(adapter);
		return;
	}
	
	private void loadSearchAutoComplete(int tipo){
		cursor_search=DBAdapter.getArticulosSearchAuto();
		int i=0;
		String strings[];
		ArrayAdapter<String>  adapter;
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
				search_input.setAdapter(adapter);
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
				search_input.setAdapter(adapter);
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
				search_input.setAdapter(adapter);
				break;
			case 3:
		        strings = new String[cursor_search.getCount()];
				if(cursor_search.moveToFirst()){
					do{
			        	strings[i] = cursor_search.getString(3);
			        	i++;
					}while(cursor_search.moveToNext());
				}
				adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, strings);
				search_input.setAdapter(adapter);
				break;
			default:
				break;
		}
		return;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		familia_id="";
		switch (item.getItemId()){
	    	case R.id.atras:
				finish();
	    		return true;
	    	default:
	    		return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
	    super.onCreateContextMenu(menu, v, menuInfo);
	    getMenuInflater().inflate(R.menu.menu_detalles, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.detalle:
	        	cursor.moveToPosition(info.position);
	        	i= new Intent(getApplicationContext(), Articulos_Detalles_Descripcion.class);
				i.putExtra("de", "articulos");
				i.putExtra("producto_id", cursor.getString(0));
				startActivity(i);
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	void loadList(){
		cursor=DBAdapter.getArticulos();
		loadDataList();
		adapter=new articuloArrayAdapter(getApplicationContext(), R.layout.item_articulo, articulos);
		listArticulos.setAdapter(adapter);
		registerForContextMenu(listArticulos);
		return;
	}

	public class articuloArrayAdapter extends ArrayAdapter<articulo> {
	    private List<articulo> articulos = new ArrayList<articulo>();
	    private int layoutId;
	    
	    public articuloArrayAdapter(Context context, int textViewResourceId,List<articulo> objects) {
	        super(context, textViewResourceId, objects);
	        this.articulos = objects;
	        this.layoutId=textViewResourceId;
			notifyDataSetChanged();
	    }

		public int getCount() {
	        return this.articulos.size();
	    }

	    public articulo getItem(int index) {
	        return this.articulos.get(index);
	    }

		public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        if (row == null) {
	            // ROW INFLATION
	        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            row = inflater.inflate(layoutId, parent, false);
	        }

	        final articulo a = getItem(position);
	        final TextView row_codigo_input = (TextView) row.findViewById(R.id.row_codigo_input);
	        final TextView row_articulo_input = (TextView) row.findViewById(R.id.row_articulo_input);
	        final TextView row_presentacion_input = (TextView) row.findViewById(R.id.row_presentacion_input);
	        final TextView row_peso_input = (TextView) row.findViewById(R.id.row_peso_input);

		    row_codigo_input.setText(a.getCodigo());
		    row_articulo_input.setText(a.getArticulo());
		    row_presentacion_input.setText(a.getPresentacion());
		    row_peso_input.setText(a.getPeso());

	        if(position % 2 == 0){
	        	row_codigo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_articulo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_presentacion_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_peso_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        }else{
				row_codigo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_articulo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_presentacion_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_peso_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
			}
	        return row;
	    }
	}

	public class articulo {
		private String codigo = "";
		private String articulo = "";
		private String presentacion = "";
		private String peso = "";

		public articulo(String codigo, String articulo, String presentacion, String peso){
			this.codigo=codigo;
			this.articulo=articulo;
			this.presentacion=presentacion;
			this.peso=peso;
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
		
		public String getPresentacion() {
			return presentacion;
		}
		
		public String getPeso() {
			return peso;
		}
	}
	
	private void loadDataList(){
		if(cursor.moveToFirst()){
			do{
				articulos.add(new articulo(cursor.getString(0),
						cursor.getString(1),
						cursor.getString(2),
						Utility.formatNumber(cursor.getDouble(3))+"gr"));
			}while(cursor.moveToNext());
		}
        row_input.setText(String.valueOf(cursor.getCount()));
		return;
	}
	
	private void reloadList(int tipoBus,String familia,String subfamilia,String buscar){
		articulos.clear();
		cursor=DBAdapter.getArticulosXFiltros(tipoBus, familia, subfamilia, buscar);
		loadDataList();
		adapter.notifyDataSetChanged();
		return;
	}
	
	@Override
	public void onBackPressed() {}
}