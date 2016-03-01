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
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class Cliente_Consultas_Historicos_Otros_Eventos extends Activity {
	private ListView listVisitas;
	private ListView listEventos;
	private Bundle extras;
	private TextView cliente_input;
	private static String cliente_id;
	private static String cliente_nombre;
	private static Cursor cursor_detalle;
	private static Cursor cursor_detalle2;
	private ItaloDBAdapter DBAdapter;
	private static VisitaRowArrayAdapter adapterDetalle;
	static private EventoRowArrayAdapter adapterDetalle2;
	private static ArrayList<AC_Visita> visitas;
	private static ArrayList<AC_Evento> eventos;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cliente_consultas_historicos_devoluciones);
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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_back_menu, menu);
		return true;
	}
	
	private void loadDetalleVisita(ListView visitaView, String cliente_id) {
		cursor_detalle = DBAdapter.getVisitasXCliente(cliente_id);
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