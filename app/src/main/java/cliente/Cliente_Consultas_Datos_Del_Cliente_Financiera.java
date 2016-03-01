package cliente;

import utilidades.Utility;
import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.widget.TextView;

public class Cliente_Consultas_Datos_Del_Cliente_Financiera extends Activity {
	private Bundle extras;
	private TextView condicion_de_pago;
	private TextView forma_de_pago;
	private TextView grupo;
	private TextView cupo;
	private TextView vencimiento;
	private TextView impuesto;
	private TextView estado_cartera;
	private TextView bandera_cliente;
	private TextView cupo_input_0;
	private Cursor cursor;
	private ItaloDBAdapter DBAdapter;
	private TextView cliente_name_input;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cliente_consultas_datos_del_cliente_financiera);
		init();
		loadValues();
	}

	private void init(){	
		extras = getIntent().getExtras();
		condicion_de_pago=(TextView) findViewById(R.id.condicion_de_pago_input);
		forma_de_pago=(TextView) findViewById(R.id.forma_de_pago_input);
		grupo=(TextView) findViewById(R.id.grupo_input);
		cupo=(TextView) findViewById(R.id.cupo_input);
		vencimiento=(TextView) findViewById(R.id.vencimiento_input);
		impuesto=(TextView) findViewById(R.id.impuesto_input);
		estado_cartera=(TextView) findViewById(R.id.estado_catera_input);
		bandera_cliente=(TextView) findViewById(R.id.bandera_cliente_input);
		cupo_input_0=(TextView)findViewById(R.id.cupo_input_0);
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
		String condicion_pago="";
		String cliente_id=extras.getString("cliente_id");
		cursor=DBAdapter.getClienteInfoFinanciera(cliente_id);
		if(cursor.moveToFirst()){
			condicion_pago=(cursor.getString(0).equalsIgnoreCase("CO"))?getString(R.string.contado):getString(R.string.credito);
			condicion_de_pago.setText(condicion_pago);
			forma_de_pago.setText(cursor.getString(1));
			grupo.setText(cursor.getString(2)+" "+cursor.getString(3));
			cupo.setText(Utility.formatNumber(cursor.getDouble(10)));
			cupo_input_0.setText(cursor.getString(4));
			vencimiento.setText(cursor.getString(5));
			impuesto.setText(cursor.getString(6));
			estado_cartera.setText(cursor.getString(7));
			bandera_cliente.setText(cursor.getString(8));
			cliente_name_input.setText(cliente_id+" "+cursor.getString(9));
		}
		if(cursor!=null){
			cursor.close();
		}
		return;
	}
	
	@Override
	public void onBackPressed() {}
}