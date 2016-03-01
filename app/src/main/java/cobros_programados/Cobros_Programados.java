package cobros_programados;

import java.util.ArrayList;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;
import com.italo_view.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Cobros_Programados extends Activity {
	private Context context;
	private Bundle extras;
	private ItaloDBAdapter DBAdapter;
	private ListView listCobrosProgramados;
	private Cursor cursor;
	private Cursor cursor_detalle;
	private ArrayList<Cobros_Programados_Item> cobros_programados;
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
	private static String distrito_id;
	private static String subdistrito_id;
	private static String sector_id;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cobros_programados);
		init();
		loadData();
	}

	private void init(){
		context=this;
		extras = getIntent().getExtras();
		cliente_id=extras.getString("cliente_id");
		cliente_nombre=extras.getString("cliente_nombre");
		distrito_id = extras.getString("distrito_id");
		subdistrito_id = extras.getString("subdistrito_id");
		sector_id = extras.getString("sector_id");
		DBAdapter=new ItaloDBAdapter(this);
		listCobrosProgramados=(ListView) this.findViewById(R.id.listCobrosProgramados);
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
		getMenuInflater().inflate(R.menu.cobros_programados, menu);
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
				final View alertView = getLayoutInflater().inflate(R.layout.cartera_detalle_alerta, null);
				final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
				dialogBuilder.setTitle(R.string.detalles);
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
				dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
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
			cursor = DBAdapter.getCobrosProgramadosFuturos(cliente_id, null, null, null);
		else
			cursor = DBAdapter.getCobrosProgramadosFuturos(null, distrito_id,  subdistrito_id,  sector_id);
						
		int tam = cursor.getCount();
		cobros_programados = new ArrayList<Cobros_Programados_Item>(tam);
		if(cursor.moveToFirst()){
			do{
				cobros_programados.add(new Cobros_Programados_Item(
						cursor.getString(0),
						cursor.getString(1),
						cursor.getString(2),
						cursor.getString(3),
						cursor.getString(4),
						cursor.getString(5)));
			}while(cursor.moveToNext());
		}
		
		listCobrosProgramados.setAdapter(new CobrosProgramadosArrayAdapter(context, R.layout.item_cobros_programados, cobros_programados, cliente_id));
		registerForContextMenu(listCobrosProgramados);
	}
	
	@Override
	public void onBackPressed() {}
}