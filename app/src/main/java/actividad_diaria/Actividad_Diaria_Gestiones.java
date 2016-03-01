package actividad_diaria;

import pedidos.Pedidos;
import registro_eventos.Registro_De_Eventos;
import visita.Visita;
import cobros.Cobros;

import com.italo_view.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Actividad_Diaria_Gestiones extends Activity {
	private Intent i;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actividad_diaria_gestiones);
		
		final Button button_pedidos = (Button) findViewById(R.id.pedidos);
		button_pedidos.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				i = new Intent(getApplicationContext(), Pedidos.class);
				startActivity(i);
				finish();
			}
		});

		final Button button_cobros = (Button) findViewById(R.id.cobros);
		button_cobros.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				i = new Intent(getApplicationContext(), Cobros.class);
				startActivity(i);
				finish();
			}
		});
		
		final Button button_registro_de_eventos = (Button) findViewById(R.id.registro_de_eventos);
		button_registro_de_eventos.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				i = new Intent(getApplicationContext(), Registro_De_Eventos.class);
				startActivity(i);
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.actividad_diaria_gestiones, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
	        case R.id.atras:
				i = new Intent(getApplicationContext(), Visita.class);
				startActivity(i);
				finish();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed() {}
}
