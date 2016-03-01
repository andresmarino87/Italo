package cliente;

import java.util.ArrayList;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import actividad_diaria.AC_Evento;
import actividad_diaria.AC_Visita;
import actividad_diaria.EventoRowArrayAdapter;
import actividad_diaria.VisitaRowArrayAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Cliente_Consultas_Otros_Eventos extends Activity {
	private ListView listVisitas;
	private ListView listEventos;
	private Bundle extras;
	private TextView cliente_input;
	private static String cliente_id;
	private static String cliente_nombre;
	private static String fecha_plan;
	private static Cursor cursor_detalle;
	private static Cursor cursor_detalle2;
	private static ItaloDBAdapter DBAdapter;
	private static VisitaRowArrayAdapter adapterDetalle;
	static private EventoRowArrayAdapter adapterDetalle2;
	private static ArrayList<AC_Visita> visitas;
	private static ArrayList<AC_Evento> eventos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cliente_consultas_historicos_otros_eventos);
		init();
		loadDetalleVisita(listVisitas,cliente_id);
		listVisitas.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,	int arg2, long arg3) {
				if(cursor_detalle.moveToPosition(arg2)){
					loadDetalleEvento(listEventos,cursor_detalle.getString(0));
				}
			}
		});

	}
	
	private void init(){
		extras = getIntent().getExtras();
		cliente_id=extras.getString("cliente_id");
		cliente_nombre= extras.getString("cliente_nombre");
		fecha_plan=extras.getString("fecha_plan");
		cliente_input=(TextView)findViewById(R.id.cliente_input);
		cliente_input.setText(cliente_id+" "+cliente_nombre);
		listVisitas=(ListView)findViewById(R.id.listVisitas);
		listEventos=(ListView)findViewById(R.id.listEventos);
		DBAdapter=new ItaloDBAdapter(this);
		visitas= new ArrayList<AC_Visita>();
		eventos= new ArrayList<AC_Evento>();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.cliente__consultas__historicos__otros__eventos, menu);
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
	    super.onCreateContextMenu(menu, v, menuInfo);
	    getMenuInflater().inflate(R.menu.menu_detalles, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.atras:
	        	finish();
				return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}	

	private void loadDetalleVisita(ListView visitaView, String cliente_id) {
		if(fecha_plan==null){
			cursor_detalle = DBAdapter.getVisitasXCliente(cliente_id);
		}else{
			cursor_detalle = DBAdapter.getVisitasXClienteDelDia(cliente_id,fecha_plan);
		}
		loadDetalleVisitaData();
		adapterDetalle = new VisitaRowArrayAdapter(getApplicationContext(),R.layout.item_consulta_visitas, visitas);
		visitaView.setAdapter(adapterDetalle);
		return;
	}
	
	private void loadDetalleVisitaData() {
		visitas.clear();
		if(cursor_detalle.moveToFirst()){
			do{
				visitas.add(new AC_Visita(
					cursor_detalle.getString(0),
					cursor_detalle.getInt(1),
					cursor_detalle.getString(2),
					cursor_detalle.getString(3),
					cursor_detalle.getString(4),
					cursor_detalle.getString(5),
					cursor_detalle.getString(6))
				);
			}while(cursor_detalle.moveToNext());
		}
		return;
	}
	
	private void loadDetalleEvento(ListView eventoView, String visita_id) {
		cursor_detalle2 = DBAdapter.getEventosXClienteDelDia(visita_id);
		loadDetalleEventoData();
		adapterDetalle2 = new EventoRowArrayAdapter(getApplicationContext(),R.layout.item_consulta_eventos, eventos);
		eventoView.setAdapter(adapterDetalle2);
		return;
	}
	
	private void loadDetalleEventoData() {
		eventos.clear();
		if(cursor_detalle2.moveToFirst()){
			do{
				eventos.add(new AC_Evento(
					cursor_detalle2.getString(0),
					cursor_detalle2.getString(1),
					cursor_detalle2.getString(2),
					cursor_detalle2.getString(3),
					cursor_detalle2.getString(4),
					cursor_detalle2.getString(5))
				);
			}while(cursor_detalle2.moveToNext());
		}
		return;
	}
	
	@Override
	public void onBackPressed() {}
}
