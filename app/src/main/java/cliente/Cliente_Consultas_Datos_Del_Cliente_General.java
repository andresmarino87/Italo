package cliente;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.widget.TextView;

public class Cliente_Consultas_Datos_Del_Cliente_General extends Activity {
	static private Bundle extras;
	static private TextView codigo;
	static private TextView id;
	static private TextView sector;
	static private TextView nombre_del_cliente;
	static private TextView razon_social;
	static private TextView direccion;
	static private TextView departamento;
	static private TextView ciudad;
	static private TextView barrio;
	static private TextView telefono_fijo;
	static private TextView celular;
	static private TextView fecha_proxima_visita;
	static private TextView correo_electronico;
	static private Cursor cursor;
	static private ItaloDBAdapter DBAdapter;
	static private TextView cliente_name_input;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cliente_consultas_datos_del_cliente_general);
		init();
		loadValues();
	}

	private void init(){	
		extras = getIntent().getExtras();
		codigo=(TextView) findViewById(R.id.codigo_input);
		id=(TextView) findViewById(R.id.id_input);
		sector=(TextView) findViewById(R.id.sector_input);
		nombre_del_cliente=(TextView) findViewById(R.id.nombre_del_cliente_input);
		razon_social=(TextView) findViewById(R.id.razon_social_input);
		direccion=(TextView) findViewById(R.id.direccion_input);
		departamento=(TextView) findViewById(R.id.departamento_input);
		ciudad=(TextView) findViewById(R.id.ciudad_input);
		barrio=(TextView) findViewById(R.id.barrio_input);
		telefono_fijo=(TextView) findViewById(R.id.telefono_fijo_input);
		celular=(TextView) findViewById(R.id.celular_input);
		fecha_proxima_visita=(TextView) findViewById(R.id.fecha_proxima_visita_input);
		correo_electronico=(TextView) findViewById(R.id.correo_electronico_input);
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
		cursor=DBAdapter.getClienteInfoGeneral(cliente_id);
		if(cursor.moveToFirst()){
			codigo.setText(cursor.getString(0));
			id.setText(cursor.getString(1));
			sector.setText(cursor.getString(2));
			nombre_del_cliente.setText(cursor.getString(3));
			razon_social.setText(cursor.getString(4));
			direccion.setText(cursor.getString(5));
			departamento.setText(cursor.getString(6));
			ciudad.setText(cursor.getString(7));
			barrio.setText(cursor.getString(8));
			telefono_fijo.setText(cursor.getString(9));
			celular.setText(cursor.getString(10));
			fecha_proxima_visita.setText(cursor.getString(12));
			correo_electronico.setText(cursor.getString(11));
			cliente_name_input.setText(cliente_id+" "+cursor.getString(3));
		}
		if(cursor!=null){
			cursor.close();
		}
		return;
	}
	
	@Override
	public void onBackPressed() {}
}
