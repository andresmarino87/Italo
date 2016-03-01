package cliente;

import visita.Visita;
import cobros.Cobros;

import com.italo_view.R;

import cartera.Cartera_Detalles;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Cliente_Consultas extends Activity {
	static private Intent i;
	static private Bundle extras;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cliente_consultas);
		init();

		
		final Button button_pedidos = (Button) findViewById(R.id.pedidos);
		button_pedidos.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				i = new Intent(getApplicationContext(), Cliente_Consultas_Pedidos.class);
				i.putExtra("cliente_id",extras.getString("cliente_id"));
				i.putExtra("cliente_nombre", extras.getString("cliente_nombre"));
				i.putExtra("de", extras.getString("de"));
				startActivity(i);
				finish();
			}
		});
		
//		i.putExtra("de", "actividad_diaria_consultar_visita");
		
		
		final Button button_cobros = (Button) findViewById(R.id.cobros);
		button_cobros.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				i = new Intent(getApplicationContext(), Cobros.class);
				i.putExtra("cliente_id",extras.getString("cliente_id"));
				i.putExtra("nombre_cliente", extras.getString("nombre_cliente"));
//				i.putExtra("de", "cliente_consultas");
				i.putExtra("de", extras.getString("de"));
				startActivity(i);
				finish();
			}
		});
		
		final Button button_datos_del_cliente = (Button) findViewById(R.id.datos_del_cliente);
		button_datos_del_cliente.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				i = new Intent(getApplicationContext(), Cliente_Consultas_Datos_Del_Cliente_Tabs.class);
				i.putExtra("cliente_id",extras.getString("cliente_id"));
				i.putExtra("nombre_cliente", extras.getString("nombre_cliente"));
				i.putExtra("de", extras.getString("de"));
				startActivity(i);
				finish();
			}
		});
		
		final Button button_cartera = (Button) findViewById(R.id.cartera);
		button_cartera.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				i = new Intent(getApplicationContext(), Cartera_Detalles.class);
				i.putExtra("cliente_id",extras.getString("cliente_id"));
				i.putExtra("nombre_cliente", extras.getString("nombre_cliente"));
//				i.putExtra("de", "cliente_consultas");
				i.putExtra("de", extras.getString("de"));
				startActivity(i);
				finish();
			}
		});
		
		final Button button_historicos = (Button) findViewById(R.id.historicos);
		button_historicos.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				i = new Intent(getApplicationContext(), Cliente_Consultas_Historicos_Tabs.class);
				i.putExtra("cliente_id",extras.getString("cliente_id"));
				i.putExtra("nombre_cliente", extras.getString("nombre_cliente"));
				i.putExtra("de", extras.getString("de"));
				startActivity(i);
				finish();
			}
		});		
	}

	private void init(){
		extras = getIntent().getExtras();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cliente_consultas, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
	        case R.id.atras:
	        	String de=extras.getString("de");
	        	if(extras!=null && de!=null){
	        		if(de.equalsIgnoreCase("actividad_diaria_consultar_visita")){
		    			i = new Intent(getApplicationContext(), Visita.class);
		    			startActivity(i);
		    			finish();
		    		}else{
		    			i = new Intent(getApplicationContext(), Cliente.class);
		    			startActivity(i);
		    			finish();
		    		}
	    		}else{
	    			i = new Intent(getApplicationContext(), Cliente.class);
	    			startActivity(i);
	    			finish();
	    		}
    			return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed() {}
}