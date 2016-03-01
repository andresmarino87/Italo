package pedidos;

import java.util.ArrayList;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Pedidos_Detalles extends Activity {
	private Bundle extras;
	static private String pedido_id;
	static private Cursor cursor;
	static private Cursor cursorAux;
	private ListView listPedidoDetalle;
	private ItaloDBAdapter DBAdapter;
	private TextView n_pedido_input;
	private TextView n_articulos_input;
	private TextView porc_basico_input;
	private TextView desc_adicional_input;
	private TextView cant_item_promo_en_especie_input;
	private TextView valor_bruto_input;
	private TextView descuento_basico_input;
	private TextView descuento_adicional_input;
	private TextView valor_neto_input;
	private TextView valor_total_input;
	private TextView total_venta_input;
	private TextView valor_total_label;
	private TextView total_venta_label;
	private TextView cliente_input;
	static private double vBruto;
	static private double vDescBasico;
	static private double vDescAdicional;
	static private double vTotal;
	static private String cliente_id;
	static private String cliente_nombre;
	private LinearLayout appendGravables;
	static private int i;
	static private ArrayList<Pedido_lista> pedidos;
	private PedidoListaArrayAdapter adapter;
	private TextView total_input;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedidos_detalles);
		init();
		loadData();
		loadList();

	}

	private void init(){
		DBAdapter=new ItaloDBAdapter(this);
    	extras = getIntent().getExtras();
    	pedido_id=extras.getString("pedido_id");
		n_pedido_input=(TextView)findViewById(R.id.n_pedido_input);
		n_articulos_input=(TextView)findViewById(R.id.n_articulos_input);
		porc_basico_input=(TextView)findViewById(R.id.porc_basico_input);
		desc_adicional_input=(TextView)findViewById(R.id.desc_adicional_input);
		cant_item_promo_en_especie_input=(TextView)findViewById(R.id.cant_item_promo_en_especie_input);
		valor_bruto_input=(TextView)findViewById(R.id.valor_bruto_input);
		descuento_basico_input=(TextView)findViewById(R.id.descuento_basico_input);
		descuento_adicional_input=(TextView)findViewById(R.id.descuento_adicional_input);
		valor_neto_input=(TextView)findViewById(R.id.valor_neto_input);
		valor_total_input=(TextView)findViewById(R.id.valor_total_input);
		total_venta_input=(TextView)findViewById(R.id.total_venta_input);
		valor_total_label=(TextView)findViewById(R.id.valor_total_label);
		total_venta_label=(TextView)findViewById(R.id.total_venta_label);
		listPedidoDetalle=(ListView) this.findViewById(R.id.listPedidoDetalle);
		appendGravables=(LinearLayout)findViewById(R.id.appendGravables);
		cliente_input=(TextView)findViewById(R.id.cliente_input);
		cliente_id=extras.getString("cliente_id");
		cliente_nombre=extras.getString("cliente_nombre");
		cliente_input.setText(cliente_id+" "+cliente_nombre);
		total_input=(TextView) findViewById(R.id.total_input);
		pedidos=new ArrayList<Pedido_lista>();
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_back_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void loadData(){
		vBruto=0;
		vDescBasico=0;
		vDescAdicional=0;
		vTotal=0;
		cursor=DBAdapter.getPedidosData(pedido_id);
		if(cursor.moveToFirst()){
			vBruto=cursor.getDouble(4);
			vDescBasico=vBruto*cursor.getDouble(2)/100;
			vDescAdicional=(vBruto-vDescBasico)*cursor.getDouble(3)/100;
			vTotal=vBruto-vDescBasico-vDescAdicional+cursor.getDouble(8);
			n_pedido_input.setText(cursor.getString(0));
//			n_articulos_input.setText(cursor.getString(1));
			n_articulos_input.setText(String.valueOf(DBAdapter.getPedidosNArticulos(pedido_id)));
			porc_basico_input.setText(Utility.formatNumber(cursor.getDouble(2)));
			desc_adicional_input.setText(Utility.formatNumber(cursor.getDouble(3)));
			valor_bruto_input.setText(Utility.formatNumber(vBruto));
			descuento_basico_input.setText(Utility.formatNumber(vDescBasico));
			descuento_adicional_input.setText(Utility.formatNumber(vDescAdicional));
			valor_neto_input.setText(Utility.formatNumber(vBruto-vDescBasico-vDescAdicional));
			valor_total_input.setText(Utility.formatNumber(vTotal));
			total_venta_input.setText(Utility.formatNumber(vTotal));
			cursor=DBAdapter.getCountPromoEspecie(pedido_id);
			if(cursor.moveToFirst()){
				cant_item_promo_en_especie_input.setText(Utility.formatNumber(cursor.getDouble(0)));
			}
			i=0;
			cursor=DBAdapter.getNumberOfGravables(pedido_id);
			if(cursor.moveToFirst()){
				do{
					i++;
					cursorAux=DBAdapter.getNumberOfGravablesSum(pedido_id, cursor.getString(0));
					if(cursorAux.moveToFirst()){
						addGravable(cursor.getString(0),cursorAux.getDouble(0),i);
					}
				}while(cursor.moveToNext());
			}
			if(i% 2 == 1){
				valor_total_label.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_par));
				valor_total_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
				total_venta_label.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_impar));
				total_venta_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
			}else{
				valor_total_label.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_impar));
				valor_total_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				total_venta_label.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_par));
				total_venta_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			}
		}
		return;
	}
	
	@SuppressWarnings("deprecation")
	private void addGravable(String nombre, double valor, int pos){
		try{
			LinearLayout child=new LinearLayout(this);
			TextView grabableLabel=new TextView(this);
			TextView grabableinput=new TextView(this);
			grabableLabel.setText("Gravables "+nombre+"%");
			grabableinput.setText(Utility.formatNumber(valor));
			grabableinput.setGravity(Gravity.RIGHT);
			grabableLabel.setTypeface(Typeface.DEFAULT_BOLD);
			if(pos % 2 == 0){
				grabableLabel.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_par));
				grabableinput.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			}else{
				grabableLabel.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_impar));
				grabableinput.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
			}
			LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			p1.weight=0.5f;
			child.addView(grabableLabel,p1);
			child.addView(grabableinput,p1);
			child.setWeightSum(1f);
			appendGravables.addView(child);
		}catch(Exception e){
			Log.e("info",""+e);
		}
		return;
	}
	
	public void loadList(){
		cursor=DBAdapter.getPedidoDetalle(cliente_id,pedido_id);
		loadDatalist();
		adapter=new PedidoListaArrayAdapter(getApplicationContext(), R.layout.item_pedido_detalle, pedidos);
		listPedidoDetalle.setAdapter(adapter);
		registerForContextMenu(listPedidoDetalle);
		return;
	}
	
	public void loadDatalist(){
		double valorTotal=0;
		if(cursor.moveToFirst()){
			do{
				pedidos.add(new Pedido_lista(
					cursor.getString(11)+" "+cursor.getString(1),
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