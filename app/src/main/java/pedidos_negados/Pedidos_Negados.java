package pedidos_negados;
import java.util.ArrayList;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class Pedidos_Negados extends Activity {
	private ItaloDBAdapter DBAdapter;
	private ListView listPedidosNegados;
	private TextView total_rows;
	private TextView total_input;
	private Spinner distrito_input_search;
	private Spinner subdistrito_input_search;
	private Spinner sector_input_search;
	private Spinner ruta_input_search;
	private Spinner motivo_input_search;
	private RadioButton todos_input;
	private Button diario_button;
	private Button mensual_button;
	private Button buscar_button;
	static private Cursor cursor;
	static private Cursor cursor_search;
	static private Cursor cursor_detalles;
	static private ArrayList<Pedidos_Negados_Item> pedidos_negados;
	private PedidoNegadoArrayAdapter adapter;
	static private boolean diario;
	static private String todas_str;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedidos_negados);
		init();
		diario_button=(Button) this.findViewById(R.id.diario_button);
		mensual_button=(Button) this.findViewById(R.id.mensual_button);
		diario_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				diario_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.boton_seleccionado_tab_style));
				mensual_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.boton_no_seleccionado_tab_style));
				diario_button.setTextColor(Color.WHITE);
				mensual_button.setTextColor(Color.BLACK);
				diario=true;
				reloadList(diario,"Todos","","","","",true,false);
			}
		});	
		mensual_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				diario_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.boton_no_seleccionado_tab_style));
				mensual_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.boton_seleccionado_tab_style));
				diario_button.setTextColor(Color.BLACK);
				mensual_button.setTextColor(Color.WHITE);
				diario=false;
				reloadList(diario,"Todos","","","","",true,false);
			}
		});
		
		loadList();
		loadMotivosNegacion();
		loadDistritos();
		loadSubdistrito(distrito_input_search.getSelectedItem().toString());
		loadSectores(subdistrito_input_search.getSelectedItem().toString());
		loadRutas(sector_input_search.getSelectedItem().toString());
		distrito_input_search.setOnItemSelectedListener((new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				loadSubdistrito(distrito_input_search.getSelectedItem().toString());
				loadSectores(subdistrito_input_search.getSelectedItem().toString());
				loadRutas(ruta_input_search.getSelectedItem().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		}));
		subdistrito_input_search.setOnItemSelectedListener((new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				loadSectores(subdistrito_input_search.getSelectedItem().toString());
				loadRutas(sector_input_search.getSelectedItem().toString());
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		}));
		sector_input_search.setOnItemSelectedListener((new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				loadRutas(sector_input_search.getSelectedItem().toString());
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		}));
		buscar_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				reloadList(diario,distrito_input_search.getSelectedItem().toString(),subdistrito_input_search.getSelectedItem().toString(),sector_input_search.getSelectedItem().toString(),ruta_input_search.getSelectedItem().toString(),motivo_input_search.getSelectedItem().toString(),todos_input.isChecked(),true);
			}
		});
	}

	private void init(){
		getIntent().getExtras();
		DBAdapter=new ItaloDBAdapter(this);
		listPedidosNegados=(ListView) findViewById(R.id.listPedidosNegados);
		total_rows=(TextView) findViewById(R.id.total_rows);
		total_input=(TextView) findViewById(R.id.total_input);
		diario_button=(Button) findViewById(R.id.diario_button);
		mensual_button=(Button) findViewById(R.id.mensual_button);
		buscar_button=(Button) findViewById(R.id.buscar_button);
		distrito_input_search=(Spinner)findViewById(R.id.distrito_input);
		subdistrito_input_search=(Spinner)findViewById(R.id.subdistrito_input);
		sector_input_search=(Spinner)findViewById(R.id.sector_input);
		ruta_input_search=(Spinner)findViewById(R.id.ruta_input);
		motivo_input_search=(Spinner)findViewById(R.id.motivo_input);
		todos_input=(RadioButton)findViewById(R.id.todos_input);
		pedidos_negados=new ArrayList<Pedidos_Negados_Item>();
		diario=true;
		todas_str=getApplicationContext().getString(R.string.todos);
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pedidos_negados, menu);
		return true;
	}

	private void loadDistritos(){
		cursor_search=DBAdapter.GetDistritosPedidosNegados();
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
		distrito_input_search.setAdapter(adapter);
		return;
	}
	
	private void loadSubdistrito(String distrito){
		cursor_search=DBAdapter.GetSubdistritosByDistritoCartera(distrito);
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
		subdistrito_input_search.setAdapter(adapter);
		return;
	}
	
	private void loadSectores(String subdistrito){
		cursor_search=DBAdapter.GetSectorBySubdistritosCartera(subdistrito);
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
		sector_input_search.setAdapter(adapter);
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
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ruta_input_search.setAdapter(adapter);
		return;
	}

	private void loadMotivosNegacion(){
		cursor_search=DBAdapter.GetPedidosNegadosMotivos();
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
		motivo_input_search.setAdapter(adapter);
		return;
	}
	
	@SuppressLint("InflateParams") @Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
	    	case R.id.atras:
				finish();
	    		return true;
	    	default:
	    		return super.onOptionsItemSelected(item);
		}
	}  
	
	public void loadList(){
		cursor=DBAdapter.getPedidosNegados(diario);
		loadDatalist();
		adapter=new PedidoNegadoArrayAdapter(getApplicationContext(), R.layout.item_pedido_negados, pedidos_negados);
		listPedidosNegados.setAdapter(adapter);
		registerForContextMenu(listPedidosNegados);
		return;
	}
	
	public void loadDatalist(){
		double valorTotal=0;
		if(cursor.moveToFirst()){
			do{
				pedidos_negados.add(new Pedidos_Negados_Item(cursor.getString(0),
						cursor.getString(1),
						cursor.getString(2),
						cursor.getString(3),
						Utility.formatNumber(cursor.getDouble(4))));				
				valorTotal=valorTotal+cursor.getDouble(4);
			}while(cursor.moveToNext());
		}
		total_input.setText(Utility.formatNumber(valorTotal));
		total_rows.setText(Integer.toString(cursor.getCount()));
		return;
	}
	
	private void reloadList(boolean type,String distrito,String subdistrito,String sector,String ruta,String motivo,boolean todos,boolean byFilter){
		pedidos_negados.clear();
		if(byFilter){
			cursor=DBAdapter.getPedidosNegadosByFilters(type,distrito,subdistrito,sector,ruta,motivo,todos);
		}else{
			cursor=DBAdapter.getPedidosNegados(type);
		}
        loadDatalist();
		adapter.notifyDataSetChanged();
		return;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
	    super.onCreateContextMenu(menu, v, menuInfo);
	    getMenuInflater().inflate(R.menu.menu_detalles, menu);
	}

	@SuppressLint("InflateParams") @Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
        case R.id.detalle:
			final View alertView=getLayoutInflater().inflate(R.layout.pedidos_negados_detalle, null);
			final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setCancelable(false);
			dialogBuilder.setTitle(R.string.detalle);
			dialogBuilder.setView(alertView);
			final TextView nombre_del_cliente_input=(TextView)alertView.findViewById(R.id.nombre_del_cliente_input);
			final TextView pedido_input=(TextView)alertView.findViewById(R.id.pedido_input);
			final TextView sector_input=(TextView)alertView.findViewById(R.id.sector_input);
			final TextView ruta_input=(TextView)alertView.findViewById(R.id.ruta_input);
			final TextView motivo_input=(TextView)alertView.findViewById(R.id.motivo_input);
			final TextView fecha_pedido_input=(TextView)alertView.findViewById(R.id.fecha_pedido_input);
			final TextView fecha_negacion_input=(TextView)alertView.findViewById(R.id.fecha_negacion_input);
			final TextView valor_pedido_input=(TextView)alertView.findViewById(R.id.valor_pedido_input);
			cursor.moveToPosition(info.position);
			cursor_detalles=DBAdapter.getPedidosNegadosDetalles(cursor.getString(1));
			if(cursor_detalles.moveToFirst()){
				nombre_del_cliente_input.setText(cursor_detalles.getString(0));
				pedido_input.setText(cursor_detalles.getString(1));
				sector_input.setText(cursor_detalles.getString(2));
				ruta_input.setText(cursor_detalles.getString(3));
				motivo_input.setText(cursor_detalles.getString(4));
				fecha_pedido_input.setText(cursor_detalles.getString(5));
				fecha_negacion_input.setText(cursor_detalles.getString(6));
				valor_pedido_input.setText(Utility.formatNumber(cursor_detalles.getDouble(7)));
			}
			dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			dialogBuilder.create().show();
           	return true;
        default:
        	return super.onContextItemSelected(item);
	    }
	}
	
	@Override
	public void onBackPressed() {}
}