package pedidos;

import utilidades.Utility;
import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;
import android.os.Bundle;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class Pedidos_Detalles_Tabs extends TabActivity{
	private Context context;
	private Intent i;
	private Bundle extras;
	private TabHost tabHost;
	static private String visita_id;
	static private String cliente_id;
	static private String cliente_nombre;
	static private String pedido_id;
	private ItaloDBAdapter DBAdapter;
	static private Cursor cursor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedidos_detalle_tabs);
		init();
		tabHost = getTabHost();
		i = new Intent(this, Pedidos_Detalles.class);
		i.putExtra("cliente_id", cliente_id);
		i.putExtra("cliente_nombre", cliente_nombre);
		i.putExtra("pedido_id", pedido_id);
		addTab(getString(R.string.resumen), 0,i);
	}

	private void init(){
		context=this;
		extras = getIntent().getExtras();
		visita_id=extras.getString("visita_id");
		cliente_id=extras.getString("cliente_id");
		cliente_nombre=extras.getString("cliente_nombre");
		pedido_id=extras.getString("pedido_id");
		DBAdapter= new ItaloDBAdapter(this);
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean isEditable=true;
		if(isEditable){
			getMenuInflater().inflate(R.menu.pedidos_detalles_tabs_editable, menu);

		}else{
			getMenuInflater().inflate(R.menu.pedidos_detalles_tabs, menu);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
	        case R.id.atras:
   	   			finish();
				return true;
	        case R.id.modificar:
	        	if(visita_id!=null){
	        		DBAdapter.cleanPedidosTemporales();
		        	if(DBAdapter.moveFromEsdPedidoToEstPedido(pedido_id)){
			        	i = new Intent(getApplicationContext(), Pedidos_Nuevo.class);
						i.putExtra("visita_id", visita_id);
		   				i.putExtra("cliente_id", cliente_id);
		   				i.putExtra("cliente_nombre", cliente_nombre);
		   				i.putExtra("pedido_id", pedido_id);
		   				i.putExtra("de", "editar_pedido");
		   				cursor=DBAdapter.getPedidoObsAndDate(pedido_id);
		   				if(cursor.moveToFirst()){
			   				i.putExtra("fecha", cursor.getString(0));
			   				i.putExtra("obs_pedido", cursor.getString(1));
			   				i.putExtra("obs_fact", cursor.getString(2));
			   				i.putExtra("estado", cursor.getString(3));
			   				i.putExtra("OC", cursor.getString(4));
			   				startActivity(i);
			   	   			finish();
		   				}
		        	}else{
		        		Utility.showMessage(context, getString(R.string.hubo_un_error_al_cargar_el_pedido));
		        	}
	        	}
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}
	
	
    private void addTab(String label, int drawableId, Intent i) {
    	TabHost.TabSpec spec = tabHost.newTabSpec(label);
    	View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tabs, getTabWidget(), false);
    	TextView title = (TextView) tabIndicator.findViewById(R.id.title);
    	title.setText(label);
    	spec.setIndicator(tabIndicator).setContent(i);
    	tabHost.addTab(spec);
    }  
	
	@Override
	public void onBackPressed() {}
}
