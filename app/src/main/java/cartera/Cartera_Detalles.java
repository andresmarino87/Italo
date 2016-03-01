package cartera;

import java.util.ArrayList;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;
import cobros_programados.Programar_Cobro;

import com.italo_view.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Cartera_Detalles extends Activity {
	private Context context;
	private Bundle extras;
	private ItaloDBAdapter DBAdapter;
	private ListView listCartera;
	private Cursor cursor;
	private Cursor cursor_detalle;
	private ArrayList<Cartera_item> carteras;
	static private ArrayList<String> facturas_id;
	private Intent i;
	private View alertView;
	private AlertDialog.Builder dialogBuilder;
	private TextView nombre_del_cliente_input;
	private TextView codigo_cliente_input;
	private TextView tipo_de_documento_input;
	private TextView numero_de_documento_input;
	private TextView sector_input;
	private TextView fecha_de_documento_input;
	private TextView fecha_de_vencimiento_input;
	private TextView fecha_del_ultimo_pago_input;
	private TextView dias_de_vencimiento_input;
	private TextView valor_original_input;
	private TextView valor_documento_input;
	private TextView saldo_actual_input;
	private TextView todos_los_clientes_label;
	private TextView cliente_label;
	
	private static String cliente_id;
	private static String cliente_nombre;
	private static int de;
	private static String tipo_documento_id;
	private static String distrito_id;
	private static String subdistrito_id;
	private static String sector_id;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cartera_detalle);
		init();
		loadData();
	}

	private void init(){
		context=this;
		extras = getIntent().getExtras();
		cliente_id=extras.getString("cliente_id");
		cliente_nombre=extras.getString("cliente_nombre");
		de=extras.getInt("de");
		tipo_documento_id = extras.getString("tipo_documento_id");
		distrito_id = extras.getString("distrito_id");
		subdistrito_id = extras.getString("subdistrito_id");
		sector_id = extras.getString("sector_id");
		DBAdapter=new ItaloDBAdapter(this);
		listCartera=(ListView) this.findViewById(R.id.listCartera);
		facturas_id = new ArrayList<String>();
		todos_los_clientes_label=(TextView) this.findViewById(R.id.todos_los_clientes_label);
		cliente_label=(TextView) this.findViewById(R.id.cliente_label);
		if(cliente_id!=null && cliente_nombre!=null){
			todos_los_clientes_label.setText( cliente_id + " " + cliente_nombre);
			cliente_label.setVisibility(LinearLayout.GONE);
		}
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cartera_detalles, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
			case R.id.atras:
				finish();	
				return true;
			case R.id.programar_cobro:
				facturas_id.clear();
				boolean hasChecked=false;
				int tam = 0;
				SparseBooleanArray checkedItems = listCartera.getCheckedItemPositions();
				if (checkedItems != null){
					tam = checkedItems.size();
					if(tam != 0){
						for (int i=0; i<tam; i++){
		     		    	if (checkedItems.valueAt(i) && cursor.moveToPosition(checkedItems.keyAt(i))){
			        			facturas_id.add("'" + cursor.getString(8) + "-" + cursor.getString(3) + "'");
			        			hasChecked = true;
			        		}
		     		    }
		     			if(hasChecked){
			      		    i = new Intent(getApplicationContext(), Programar_Cobro.class);
		        		    i.putExtra("facturas_id", facturas_id);
		        		    startActivity(i);
		     		    }else{
		     		    	Utility.showMessage(context, getString(R.string.seleccione_al_menos_un_elemento_prog_cobro));
		     		    }
		     		}else{
	     		    	Utility.showMessage(context, getString(R.string.seleccione_al_menos_un_elemento_prog_cobro));
		     		}
				}else{
     		    	Utility.showMessage(context, getString(R.string.seleccione_al_menos_un_elemento_prog_cobro));
		     	}
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
	
	private void loadDetalleAlerta(String documento_id){
		cursor_detalle=DBAdapter.getCarteraDetalle(documento_id);
		if(cursor_detalle.moveToFirst()){
			nombre_del_cliente_input.setText(cursor_detalle.getString(0));
			codigo_cliente_input.setText(cursor_detalle.getString(1));
			tipo_de_documento_input.setText(cursor_detalle.getString(2));
			numero_de_documento_input.setText(cursor_detalle.getString(3));
			sector_input.setText(cursor_detalle.getString(4));
			fecha_de_documento_input.setText(cursor_detalle.getString(5));
			fecha_de_vencimiento_input.setText(cursor_detalle.getString(6));
			if(cursor_detalle.getString(7)!= null && cursor_detalle.getString(7).equalsIgnoreCase("01/01/1900")){
				fecha_del_ultimo_pago_input.setText("");
			}else{
				fecha_del_ultimo_pago_input.setText(cursor_detalle.getString(7));
			}
			dias_de_vencimiento_input.setText(String.valueOf(cursor_detalle.getInt(8)));
			valor_original_input.setText("$ "+Utility.formatNumber(cursor_detalle.getDouble(9)));
			valor_documento_input.setText("$ "+Utility.formatNumber(cursor_detalle.getDouble(10)));
			saldo_actual_input.setText("$ "+Utility.formatNumber(cursor_detalle.getDouble(11)));
		}
		return;
	}
	
	@SuppressLint("InflateParams") @Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()){
			case 0:
				alertView = getLayoutInflater().inflate(R.layout.cartera_detalle_alerta, null);
				dialogBuilder = new AlertDialog.Builder(this);
				dialogBuilder.setTitle(getString(R.string.detalles));
				dialogBuilder.setView(alertView);
				nombre_del_cliente_input = (TextView)alertView.findViewById(R.id.nombre_del_cliente_input);
				codigo_cliente_input = (TextView)alertView.findViewById(R.id.codigo_cliente_input);
				tipo_de_documento_input = (TextView)alertView.findViewById(R.id.tipo_de_documento_input);
				numero_de_documento_input = (TextView)alertView.findViewById(R.id.numero_de_documento_input);
				sector_input = (TextView)alertView.findViewById(R.id.sector_input);
				fecha_de_documento_input = (TextView)alertView.findViewById(R.id.fecha_de_documento_input);
				fecha_de_vencimiento_input = (TextView)alertView.findViewById(R.id.fecha_de_vencimiento_input);
				fecha_del_ultimo_pago_input = (TextView)alertView.findViewById(R.id.fecha_del_ultimo_pago_input);
				dias_de_vencimiento_input = (TextView)alertView.findViewById(R.id.dias_de_vencimiento_input);
				valor_original_input = (TextView)alertView.findViewById(R.id.valor_original_input);
				valor_documento_input = (TextView)alertView.findViewById(R.id.valor_documento_input);
				saldo_actual_input = (TextView)alertView.findViewById(R.id.saldo_actual_input);
				cursor.moveToPosition(info.position);
				loadDetalleAlerta(cursor.getString(3));
				dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				dialogBuilder.create().show();
				return true;
			default:
	            return super.onContextItemSelected(item);
		}
	}
	
	public void loadData()
	{
		if (cliente_id != null)
		{
			cursor = DBAdapter.getListaCartera(cliente_id, tipo_documento_id, de);
		}
		else
		{
			cursor = DBAdapter.getListaCartera(distrito_id, subdistrito_id, sector_id, tipo_documento_id, de);
		}
				
		int tam=cursor.getCount();
		carteras=new ArrayList<Cartera_item>(tam);
		if(cursor.moveToFirst()){
			do{
				carteras.add(new Cartera_item(
						cursor.getString(1),
						cursor.getString(0),
						cursor.getString(2),
						cursor.getString(3),
						cursor.getString(4),
						cursor.getString(5),
						String.valueOf(cursor.getInt(6)),
						Utility.formatNumber(cursor.getDouble(7))
						));
			}while(cursor.moveToNext());
		}
		listCartera.setAdapter(new CarteraArrayAdapter(context, R.layout.item_cartera, carteras,cliente_id));
		registerForContextMenu(listCartera);
	}
	
	@Override
	public void onBackPressed() {}
}