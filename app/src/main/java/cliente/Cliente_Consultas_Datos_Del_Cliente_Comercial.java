package cliente;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.widget.TextView;

public class Cliente_Consultas_Datos_Del_Cliente_Comercial extends Activity {
	private Bundle extras;
	private TextView distrito;
	private TextView sector;
	private TextView ruta;
	private TextView tejido;
	private TextView tipo_cliente;
	private TextView cadena;
	private TextView canal_distribuidores;
	private TextView estado;
	private TextView fecha_ingreso;
	private TextView fecha_retiro;
	private TextView sucursal;
	private Cursor cursor;
	private ItaloDBAdapter DBAdapter;
	private TextView cliente_name_input;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cliente_consultas_datos_del_cliente_comercial);
		init();
		loadValues();
	}

	private void init(){	
		extras = getIntent().getExtras();
		distrito=(TextView) findViewById(R.id.distrito_input);
		sector=(TextView) findViewById(R.id.sector_input);
		ruta=(TextView) findViewById(R.id.ruta_input);
		tejido=(TextView) findViewById(R.id.tejido_input);
		tipo_cliente=(TextView) findViewById(R.id.tipo_cliente_input);
		cadena=(TextView) findViewById(R.id.cadena_input);
		canal_distribuidores=(TextView) findViewById(R.id.canal_distribuidores_input);
		estado=(TextView) findViewById(R.id.estado_input);
		fecha_ingreso=(TextView) findViewById(R.id.fecha_ingreso_input);
		fecha_retiro=(TextView) findViewById(R.id.fecha_retiro_input);
		sucursal=(TextView) findViewById(R.id.sucursal_input);
		DBAdapter=new ItaloDBAdapter(this);
		cliente_name_input=(TextView) findViewById(R.id.cliente_name_input);
		return;
	} 

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_back_menu, menu);
		return true;
	}
	
	private void loadValues(){
		String cliente_id=extras.getString("cliente_id");
		cursor=DBAdapter.getClienteInfoComercial(cliente_id);
		if(cursor.moveToFirst()){
			distrito.setText(cursor.getString(0)+" "+cursor.getString(1));
			sector.setText(cursor.getString(2));
			ruta.setText(cursor.getString(3));
			tejido.setText(cursor.getString(4));
			tipo_cliente.setText(cursor.getString(5)+" "+cursor.getString(6));
			cadena.setText(cursor.getString(7)+" "+cursor.getString(8));
			canal_distribuidores.setText(cursor.getString(9)+" "+cursor.getString(10));
			estado.setText(cursor.getString(11));
			fecha_ingreso.setText(cursor.getString(12));
			if(cursor.getString(13)!=null && !cursor.getString(13).equalsIgnoreCase("12/31/1899")){
				fecha_retiro.setText(cursor.getString(13));
			}else{
				fecha_retiro.setText("");
			}
			sucursal.setText(cursor.getString(14));
			cliente_name_input.setText(cliente_id+" "+cursor.getString(15));
		}
		if(cursor!=null){
			cursor.close();
		}
		return;
	}
	
	@Override
	public void onBackPressed() {}
}
