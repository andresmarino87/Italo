package asistente;

import com.italo_view.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Asistente extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_asistente);
		
		final Button button_efectividad_de_visita = (Button) findViewById(R.id.efectividad_de_visita);
		button_efectividad_de_visita.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Intent	newIntent = new Intent(getApplicationContext(), Asistente_Efectividad_De_Visita.class);
				startActivity(newIntent);
			}
		});

		final Button button_asistente_general = (Button) findViewById(R.id.asistente_general);
		button_asistente_general.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Intent	newIntent = new Intent(getApplicationContext(), Asistente_Asistente_General.class);
				startActivity(newIntent);
			}
		});
		
		final Button presupuesto_de_ventas = (Button) findViewById(R.id.presupuesto_de_ventas);
		presupuesto_de_ventas.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Intent	newIntent = new Intent(getApplicationContext(), Asistente_Presupuesto_Ventas.class);
				startActivity(newIntent);
			}
		});
		
		final Button button_acumulado_presupuesto_ventas = (Button) findViewById(R.id.acumulado_presupuesto_ventas);
		button_acumulado_presupuesto_ventas.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Intent	newIntent = new Intent(getApplicationContext(), Asistente_Acumulado_De_Ventas.class);
				startActivity(newIntent);
			}
		});
		
		final Button button_presupuesto_articulos = (Button) findViewById(R.id.presupuesto_articulos);
		button_presupuesto_articulos.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Intent	newIntent = new Intent(getApplicationContext(), Asistente_Presupuesto_Articulos.class);
				startActivity(newIntent);
			}
		});
		
		final Button button_presupuesto_cartera = (Button) findViewById(R.id.presupuesto_cartera);
		button_presupuesto_cartera.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Intent	newIntent = new Intent(getApplicationContext(), Asistente_Presupuesto_Cartera.class);
				startActivity(newIntent);
			}
		});
		
		final Button button_facturacion_impuesto = (Button) findViewById(R.id.facturacion_impuesto);
		button_facturacion_impuesto.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				Intent	newIntent = new Intent(getApplicationContext(), Asistente_Facturacion.class);
				startActivity(newIntent);
			}
		});		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.asistente,menu);
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
	public void onBackPressed() {}
}
