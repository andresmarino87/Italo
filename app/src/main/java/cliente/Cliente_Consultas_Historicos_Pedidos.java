package cliente;

import java.util.ArrayList;

import pedidos.Pedidos_Detalles_Tabs;
import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class Cliente_Consultas_Historicos_Pedidos extends Activity {
	private ListView listPedidos;
	private TextView subtotal_input;
	private TextView iva_input;
	private TextView total_facturas_input;
	private TextView cliente_name_input;
	static private ArrayList<Cliente_Pedido_Item> pedidos;
	private Cliente_PedidosArrayAdapter adapter;
	private Bundle extras;
	static private String cliente_id;
	static private String cliente_nombre;
	private ItaloDBAdapter DBAdapter;
	private Cursor cursor;
	private Intent i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cliente_consultas_historicos_pedidos);
		init();
		loadList();
	}
	
	private void init(){
		extras = getIntent().getExtras();
		cliente_id=extras.getString("cliente_id");
		cliente_nombre= extras.getString("cliente_nombre");
		cliente_name_input=(TextView)findViewById(R.id.cliente_name_input);
		cliente_name_input.setText(cliente_id+" "+cliente_nombre);
		listPedidos=(ListView)findViewById(R.id.listPedidos);
		subtotal_input=(TextView)findViewById(R.id.subtotal_input);
		iva_input=(TextView)findViewById(R.id.iva_input);
		total_facturas_input=(TextView)findViewById(R.id.total_facturas_input);
		pedidos=new ArrayList<Cliente_Pedido_Item>();
    	DBAdapter=new ItaloDBAdapter(this);
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_back_menu, menu);
		return true;
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
	    super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.ver_detalle);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
	        case 0:
	        	if(cursor.moveToPosition(info.position)){
	        		i = new Intent(getApplicationContext(), Pedidos_Detalles_Tabs.class);
					i.putExtra("cliente_id", cliente_id);
					i.putExtra("cliente_nombre", cliente_nombre);
					i.putExtra("pedido_id", cursor.getString(0));
					startActivity(i);
	        	}
				return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	public void loadList(){
		cursor=DBAdapter.getPedidos(cliente_id);
		loadDatalist();
		adapter=new Cliente_PedidosArrayAdapter(getApplicationContext(), R.layout.item_pedido, pedidos);
		listPedidos.setAdapter(adapter);
		registerForContextMenu(listPedidos);
		return;
	}
	
	public void loadDatalist(){
		double total=0;
		double iva=0;
		if(cursor.moveToFirst()){
			do{
				pedidos.add(new Cliente_Pedido_Item(
						cursor.getString(0),
						Utility.formatNumber(cursor.getDouble(1)),
						cursor.getString(2),
						cursor.getString(3)));				
				iva+=cursor.getDouble(4);
				total+=cursor.getDouble(1);
			}while(cursor.moveToNext());
		}
		subtotal_input.setText(Utility.formatNumber(total-iva));
		iva_input.setText(Utility.formatNumber(iva));
		total_facturas_input.setText(Utility.formatNumber(total));
		return;
	}

	@Override
	public void onBackPressed() {}
}