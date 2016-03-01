package cobros;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Cobros_Pago_Resumen extends Activity {
	static private ItaloDBAdapter DBAdapter;
//	static private TextView debe_input;
//	static private TextView total_input;
	static private ListView listPagoResumen;
//	static private Cursor cursor;
//	static private NumberFormat nf;
	static private ArrayList<pago> pagos;
	static private pagoArrayAdapter adapter;
	static private String eliminar_str;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cobros_pago_resumen);
		Locale.setDefault(new Locale("us", "US"));
		init();
		loadList();
	}

	private void init(){
//		debe_input = (TextView)findViewById(R.id.debe_input);
//		total_input = (TextView)findViewById(R.id.total_input);
		listPagoResumen = (ListView)findViewById(R.id.listPagoResumen);
		DBAdapter=new ItaloDBAdapter(this);
//		nf= NumberFormat.getInstance(new Locale( "es" , "ES"));
		pagos=new ArrayList<pago>();
		eliminar_str=getApplicationContext().getString(R.string.eliminar);
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cobros__pago__resumen, menu);
		return true;
	}
/*/*	static private Intent i;
	static private AlertDialog.Builder dialogBuilder;
	static private ListView listParetos;
	static private TextView total_row;
	static private RadioButton ochenta;
	static private RadioButton veinte;
	static private ItaloDBAdapter DBAdapter;
	static private Cursor cursor;
	static private Cursor cursor_search;
	static private Spinner sector_search;
	static private Spinner ruta_search;
	static private NumberFormat nf;
	static private ArrayList<pareto> paretos;
	static private View alertView;
	static private paretoArrayAdapter adapter;
	static private String todas_str;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paretos);
		init();
		loadList();
		return;
	}
	

	@Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.paretos, menu);
        return true;
    }

	private void loadSector(){
		cursor_search=DBAdapter.getParetosSector();
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
		sector_search.setAdapter(adapter);
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
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, strings);
		ruta_search.setAdapter(adapter);
		return;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.buscar:
				alertView = getLayoutInflater().inflate(R.layout.search_paretos, null);
				dialogBuilder = new AlertDialog.Builder(this);
				dialogBuilder.setTitle(getString(R.string.seleccione_filtros));
				dialogBuilder.setView(alertView);

				ochenta=(RadioButton)alertView.findViewById(R.id.ochenta_search);
				veinte=(RadioButton)alertView.findViewById(R.id.veinte_search);
				sector_search=(Spinner)alertView.findViewById(R.id.sector_search);
				ruta_search=(Spinner)alertView.findViewById(R.id.ruta_search);
				loadSector();
				loadRutas(sector_search.getSelectedItem().toString());
				sector_search.setOnItemSelectedListener((new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
						loadRutas(sector_search.getSelectedItem().toString());
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				}));
				
				dialogBuilder.setPositiveButton(getString(R.string.buscar), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(ochenta.isChecked()){
							reloadList(sector_search.getSelectedItem().toString(),ruta_search.getSelectedItem().toString(),"80");
						}else if(veinte.isChecked()){
							reloadList(sector_search.getSelectedItem().toString(),ruta_search.getSelectedItem().toString(),"20");
						}
					}
				});
				dialogBuilder.create().show();
	    		return true;
	    	case R.id.atras:
				finish();
	    		return true;
	    	default:
	    		return super.onOptionsItemSelected(item);
		}
    }
    
	void loadList(){
		cursor=DBAdapter.getParetos();
		loadDataList();
		adapter=new paretoArrayAdapter(getApplicationContext(), R.layout.item_cartera, paretos);
		listParetos.setAdapter(adapter);
//		registerForContextMenu(listPedidosNegados);
		return;
	}


	
	private void loadDataList(){
		int tam=cursor.getCount();
		if(cursor.moveToFirst()){
			do{
				paretos.add(new pareto(cursor.getString(0),
						cursor.getString(1),
						cursor.getString(2),
						cursor.getString(3),
						Utility.formatNumber(cursor.getFloat(4))));
			}while(cursor.moveToNext());
		}
		total_row.setText(Integer.toString(tam));
		return;
	}
	
	private void reloadList(String sector,String ruta,String porcentaje){
		paretos.clear();
		cursor=DBAdapter.getParetosByFilters(sector,ruta,porcentaje);
		loadDataList();
		adapter.notifyDataSetChanged();
		return;
	}*/
	
	public class pagoArrayAdapter extends ArrayAdapter<pago> {
	    private List<pago> pagos = new ArrayList<pago>();
	    private TextView row_n_pago_input;
	    private TextView row_forma_pago_input;
	    private TextView row_valor_input;

	    public pagoArrayAdapter(Context context, int textViewResourceId,List<pago> objects) {
	        super(context, textViewResourceId, objects);
	        this.pagos = objects;
			notifyDataSetChanged();
	    }

		public int getCount() {
	        return this.pagos.size();
	    }

	    public pago getItem(int index) {
	        return this.pagos.get(index);
	    }

		@SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        if (row == null) {
	            // ROW INFLATION
	        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            row = inflater.inflate(R.layout.item_pagoresumen, parent, false);
	        }else{}

	        pago p = getItem(position);
		    row_n_pago_input = (TextView) row.findViewById(R.id.row_n_pago_input);
		    row_forma_pago_input = (TextView) row.findViewById(R.id.row_forma_pago_input);
		    row_valor_input = (TextView) row.findViewById(R.id.row_valor_input);

		    row_n_pago_input.setText(p.getNPago());
		    row_forma_pago_input.setText(p.getFormaPago());
		    row_valor_input.setText(p.getValor());

	        if(position % 2 == 0){
	        	row_n_pago_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_forma_pago_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_valor_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        }else{
	        	row_n_pago_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_forma_pago_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_valor_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
			}
	        return row;
	    }
	}
	
	public class pago {
		private String nPago = "";
		private String formaPago = "";
		private String valor = "";

		public pago(String nPago, String formaPago, String valor){
			this.nPago=nPago;
			this.formaPago=formaPago;
			this.valor=valor;
		}

		public pago(){
			this.nPago="123465";
			this.formaPago="Efectivo";
			this.valor="1234156";
		}
		
		/*
		 * 
		 * Getters
		 * 
		 */
		
		public String getNPago() {
			return nPago;
		}
		
		public String getFormaPago() {
			return formaPago;
		}

		public String getValor() {
			return valor;
		}
	}
	
	void loadList(){
//		cursor=DBAdapter.getParetos();
		loadDataList();
		adapter=new pagoArrayAdapter(getApplicationContext(), R.layout.item_cartera, pagos);
		listPagoResumen.setAdapter(adapter);
		registerForContextMenu(listPagoResumen);
		return;
	}


	
	private void loadDataList(){
/*		int tam=cursor.getCount();
		if(cursor.moveToFirst()){
			do{
				paretos.add(new pareto(cursor.getString(0),
						cursor.getString(1),
						cursor.getString(2),
						cursor.getString(3),
						Utility.formatNumber(cursor.getFloat(4))));
			}while(cursor.moveToNext());
		}
		total_row.setText(Integer.toString(tam));*/
		for(int i=0;i<5;i++){
			pagos.add(new pago());
		}
		return;
	}
	
	public void reloadList(){
		pagos.clear();
//		cursor=DBAdapter.getParetosByFilters(sector,ruta,porcentaje);
		loadDataList();
		adapter.notifyDataSetChanged();
		return;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
	    super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, 0, Menu.NONE, eliminar_str);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
//		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case 0:
	           	return true;
	        default:
	        	return super.onContextItemSelected(item);
	    }
	}
	
	@Override
	public void onBackPressed() {}
}
