package pedidos;

import java.util.ArrayList;
import java.util.List;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Pedidos extends Activity {
	private Context context;
	private Intent i;
	private ListView listPedidos;
	private TextView subtotal_input;
	private TextView iva_input;
	private TextView total_facturas_input;
	private TextView cliente_name_input;
	private pedidosArrayAdapter adapter;
	private Bundle extras;
	static private String visita_id;
	static private String cliente_id;
	static private String cliente_nombre;
	static private ArrayList<pedido> pedidos;
	private ItaloDBAdapter DBAdapter;
	static private Cursor cursor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedidos);
		init();
		loadList();
	}

	private void init(){
		context=this;
    	DBAdapter=new ItaloDBAdapter(this);
		listPedidos=(ListView)findViewById(R.id.listPedidos);
		subtotal_input=(TextView)findViewById(R.id.subtotal_input);
		iva_input=(TextView)findViewById(R.id.iva_input);
		total_facturas_input=(TextView)findViewById(R.id.total_facturas_input);
		cliente_name_input=(TextView)findViewById(R.id.cliente_name_input);
		pedidos=new ArrayList<pedido>();
		extras = getIntent().getExtras();
		visita_id=extras.getString("visita_id");
		cliente_id=extras.getString("cliente_id");
		cliente_nombre=extras.getString("cliente_nombre");
		cliente_name_input.setText(cliente_id+" "+cliente_nombre);
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.pedidos, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
        	case R.id.nuevo_pedido:
				i = new Intent(getApplicationContext(), Pedidos_Nuevo.class);
				i.putExtra("visita_id", visita_id);
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				i.putExtra("de", "pedido_nuevo");
				startActivity(i);
				return true;
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
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.eliminar);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
	        case 0:
	        	if(cursor.moveToPosition(info.position)){
	        		i = new Intent(getApplicationContext(), Pedidos_Detalles_Tabs.class);
					i.putExtra("cliente_id", cliente_id);
					i.putExtra("visita_id", visita_id);
					i.putExtra("cliente_nombre", cliente_nombre);
					i.putExtra("pedido_id", cursor.getString(0));
					startActivity(i);
	        	}
				return true;
	        case 1:
	    		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
	    		dialogBuilder.setTitle(R.string.alerta);
	    		dialogBuilder.setMessage(R.string.esta_seguro_que_desea_eliminar_el_pedido);
	    		dialogBuilder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
	    	        	boolean canDelete=true;
	    	        	if(canDelete){
	    	        		DBAdapter.beginTransaction();
	    	        		if(cursor.moveToPosition(info.position)){
	    	        			if(DBAdapter.deletePedido(cursor.getString(0))){
	    	        				if(DBAdapter.deleteEvento(cursor.getString(0))){
	    	        					DBAdapter.setTransactionSuccessful();
	    	        				}
	    	        			}else{
	    	        				Utility.showMessage(context, R.string.error_al_eliminar_pedido);
	    	        			}
	    	        		}
	    	        		DBAdapter.endTransaction();
	    	        	}
    					reloadList();
	    			}
	    		});
	    		dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {}
	    		});
	    		dialogBuilder.create().show();
				return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	public class pedidosArrayAdapter extends ArrayAdapter<pedido> {
	    private List<pedido> pedidos = new ArrayList<pedido>();
	    private int layoutId;

	    public pedidosArrayAdapter(Context context, int textViewResourceId,List<pedido> objects) {
	        super(context, textViewResourceId, objects);
	        this.pedidos = objects;
	        this.layoutId = textViewResourceId;
			notifyDataSetChanged();
	    }

		public int getCount() {
	        return this.pedidos.size();
	    }

	    public pedido getItem(int index) {
	        return this.pedidos.get(index);
	    }

		@SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        if (row == null) {
	            // ROW INFLATION
	    		LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    		row = inflater.inflate(layoutId, parent, false);
	        }

	        final pedido p = getItem(position);
	        final TextView row_n_pedido_input = (TextView) row.findViewById(R.id.row_n_pedido_input);
	        final TextView row_valor_factura_input = (TextView) row.findViewById(R.id.row_valor_factura_input);
	        final TextView row_estado_input = (TextView) row.findViewById(R.id.row_estado_input);
	        final TextView row_fecha_input = (TextView) row.findViewById(R.id.row_fecha_input);
	        
		    row_n_pedido_input.setText(p.getNFactura());
		    row_valor_factura_input.setText(p.getFactura());
			row_estado_input.setText(p.getEstado());
			
/*		    if(p.getEstado().equalsIgnoreCase("13")){
				row_estado_input.setText("FACTURAR");
			}else if(p.getEstado().equalsIgnoreCase("70")){
				row_estado_input.setText("VENTAS");
			}else if(p.getEstado().equalsIgnoreCase("14")){
				row_estado_input.setText("CREDITOS");
			}else if(p.getEstado().equalsIgnoreCase("71")){
				row_estado_input.setText("ANULADO");
			}*/
		    row_fecha_input.setText(p.getFecha());

	        if(position % 2 == 0){
	        	row_n_pedido_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_valor_factura_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_estado_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_fecha_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			}else{
				row_n_pedido_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_valor_factura_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_estado_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_fecha_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
			}
	        return row;
	    }
	}
	
	public class pedido{
		private String nPedido = "";
		private String valorFactura = "";
		private String estado = "";
		private String fecha = "";

		public pedido(String nPedido, String valorFactura, String estado, String fecha){
			this.nPedido=nPedido;
			this.valorFactura=valorFactura;
			this.estado=estado;
			this.fecha=fecha;
		}
		
		/*
		 * 
		 * Getters
		 * 
		 */
		
		public String getNFactura() {
			return nPedido;
		}
		
		public String getFactura() {
			return valorFactura;
		}
		
		public String getEstado() {
			return estado;
		}
		
		public String getFecha() {
			return fecha;
		}
	}

	
	public void loadList(){
		cursor=DBAdapter.getPedidosDelDia(cliente_id);
		loadDatalist();
		adapter=new pedidosArrayAdapter(getApplicationContext(), R.layout.item_pedido, pedidos);
		listPedidos.setAdapter(adapter);
		registerForContextMenu(listPedidos);
		return;
	}
	
	public void loadDatalist(){
		double total=0;
		double iva=0;
		if(cursor.moveToFirst()){
			do{
				pedidos.add(new pedido(
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

	private void reloadList(){
		pedidos.clear();
		cursor=DBAdapter.getPedidosDelDia(cliente_id);
		loadDatalist();
		adapter.notifyDataSetChanged();
		return;
	}
	
	@Override
	protected void onResume(){
	   super.onResume();
	   reloadList();
	}
	
	@Override
	protected void onPause(){
	   super.onResume();
	}
	
	@Override
	public void onBackPressed() {}
}