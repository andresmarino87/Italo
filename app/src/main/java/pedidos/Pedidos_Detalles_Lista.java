package pedidos;

import java.util.ArrayList;
import java.util.List;
import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Pedidos_Detalles_Lista extends Activity {
	private ListView listPedidoDetalle;
	private ItaloDBAdapter DBAdapter;
	static private Cursor cursor;
	static private ArrayList<pedido_lista> pedidos;
	private pedidoListaArrayAdapter adapter;
	static private String cliente_id;
	static private String cliente_nombre;
	static private String pedido_id;
	private Bundle extras;
	private TextView total_input;
	private TextView cliente_input;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedidos_detalles_lista);
		init();
		loadList();
	}
	
	private void init(){
		extras=getIntent().getExtras();
		DBAdapter=new ItaloDBAdapter(this);
		listPedidoDetalle=(ListView) this.findViewById(R.id.listPedidoDetalle);
		pedido_id=extras.getString("pedido_id");
		cliente_id=extras.getString("cliente_id");
		cliente_nombre=extras.getString("cliente_nombre");
		cliente_input=(TextView) findViewById(R.id.cliente_input);
		total_input=(TextView) findViewById(R.id.total_input);
		pedidos=new ArrayList<pedido_lista>();
		getApplicationContext().getString(R.string.eliminar);
		getApplicationContext().getString(R.string.modificar);
		cliente_input.setText(cliente_id+" "+cliente_nombre);
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pedidos_detalles_lista, menu);
		return true;
	}

	public class pedidoListaArrayAdapter extends ArrayAdapter<pedido_lista> {
	    private List<pedido_lista> pedidosLista = new ArrayList<pedido_lista>();
	    private TextView row_producto_input;
	    private TextView row_pres_input;
	    private TextView row_vr_unitario_input;
	    private TextView row_sug_input;
	    private TextView row_cant_input;
	    private TextView row_promo_especie_input;
	    private TextView row_porc_promo_input;
	    private TextView row_vr_promo_input;
	    private TextView row_iva_input;
	    private TextView row_subtotal_input;
	    
	    public pedidoListaArrayAdapter(Context context, int textViewResourceId,List<pedido_lista> objects) {
	        super(context, textViewResourceId, objects);
	        this.pedidosLista = objects;
			notifyDataSetChanged();
	    }

		public int getCount() {
	        return this.pedidosLista.size();
	    }

	    public pedido_lista getItem(int index) {
	        return this.pedidosLista.get(index);
	    }

		@SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        if (row == null) {
	            // ROW INFLATION
	        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            row = inflater.inflate(R.layout.item_pedido_detalle, parent, false);
	        }else{}

	        pedido_lista p = getItem(position);
	        row_producto_input = (TextView) row.findViewById(R.id.row_producto_input);
	        row_pres_input = (TextView) row.findViewById(R.id.row_pres_input);
	        row_vr_unitario_input = (TextView) row.findViewById(R.id.row_vr_unitario_input);
	        row_sug_input = (TextView) row.findViewById(R.id.row_sug_input);
	        row_cant_input = (TextView) row.findViewById(R.id.row_cant_input);
	        row_promo_especie_input = (TextView) row.findViewById(R.id.row_promo_especie_input);
	        row_porc_promo_input = (TextView) row.findViewById(R.id.row_porc_promo_input);
	        row_vr_promo_input = (TextView) row.findViewById(R.id.row_vr_promo_input);
	        row_iva_input = (TextView) row.findViewById(R.id.row_iva_input);
	        row_subtotal_input = (TextView) row.findViewById(R.id.row_subtotal_input);
		    
     
	        row_producto_input.setText(p.getProducto());
	        row_pres_input.setText(p.getPresentacion());
	        row_vr_unitario_input.setText(p.getValorUnitario());
	        row_sug_input.setText(p.getSugerido());
	        row_cant_input.setText(p.getCantidad());
	        row_promo_especie_input.setText(p.getPromoEspecie());
	        row_porc_promo_input.setText(p.getPorcPromo());
	        row_vr_promo_input.setText(p.getValorPromo());
	        row_iva_input.setText(p.getIVA());
	        row_subtotal_input.setText(p.getSubtotal());

	        
	        if(position % 2 == 0){
	        	row_producto_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_pres_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			    row_vr_unitario_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			    row_sug_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			    row_cant_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			    row_promo_especie_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			    row_porc_promo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			    row_vr_promo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			    row_iva_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			    row_subtotal_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			}else{
				row_producto_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_pres_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_vr_unitario_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_sug_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_cant_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_promo_especie_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_porc_promo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_vr_promo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_iva_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_subtotal_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
			}
	        return row;
	    }
	}

	public class pedido_lista {
		private String producto = "";
		private String presentacion = "";
		private String valorUnitario = "";
		private String sugerido = "";
		private String cantidad = "";
		private String promoEspecie = "";
		private String porcPromo = "";
		private String valorPromo = "";
		private String iva = "";
		private String subtotal = "";

		public pedido_lista(String producto, String presentacion, String valorUnitario, String sugerido, String cantidad, String promoEspecie, String porcPromo, String valorPromo, String iva, String subtotal){
			this.producto=producto;
			this.presentacion=presentacion;
			this.valorUnitario=valorUnitario;
			this.sugerido=sugerido;
			this.cantidad=cantidad;
			this.promoEspecie=promoEspecie;
			this.porcPromo=porcPromo;
			this.valorPromo=valorPromo;
			this.iva=iva;
			this.subtotal=subtotal;
		}
		
		/*
		 * 
		 * Getters
		 * 
		 */
	
		public String getProducto() {
			return producto;
		}
		
		public String getPresentacion() {
			return presentacion;
		}
		
		public String getValorUnitario() {
			return valorUnitario;
		}
		
		public String getSugerido() {
			return sugerido;
		}
		
		public String getCantidad() {
			return cantidad;
		}

		public String getPromoEspecie() {
			return promoEspecie;
		}

		public String getPorcPromo() {
			return porcPromo;
		}

		public String getValorPromo() {
			return valorPromo;
		}
		
		public String getIVA() {
			return iva;
		}

		public String getSubtotal(){
			return subtotal;
		}
	}
	
	public void loadList(){
		cursor=DBAdapter.getPedidoDetalle(cliente_id,pedido_id);
		loadDatalist();
		adapter=new pedidoListaArrayAdapter(getApplicationContext(), R.layout.item_cartera, pedidos);
		listPedidoDetalle.setAdapter(adapter);
		registerForContextMenu(listPedidoDetalle);
		return;
	}
	
	public void loadDatalist(){
		double valorTotal=0;
		if(cursor.moveToFirst()){
			do{
				pedidos.add(new pedido_lista(
						cursor.getString(1),
						cursor.getString(2),
						Utility.formatNumber(cursor.getDouble(3)),
						cursor.getString(4),
						cursor.getString(5),
						cursor.getString(6),
						cursor.getString(7),
						cursor.getString(8),
						cursor.getString(12),
						Utility.formatNumber(cursor.getDouble(10))));
				valorTotal=valorTotal+cursor.getDouble(10);
			}while(cursor.moveToNext());
		}
		total_input.setText(Utility.formatNumber(valorTotal));
		return;
	}
	
	@Override
	public void onBackPressed() {}
}