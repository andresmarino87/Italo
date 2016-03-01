package consignaciones;

import com.italo_view.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TableLayout;

public class Consignaciones_Efectuadas extends Activity {
	static private Intent i;
	static private ListView listConsignado;
	static private Bundle extras;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consignaciones_efectuadas);
		listConsignado = (ListView)findViewById(R.id.listConsignado);
		registerForContextMenu(listConsignado);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.consignaciones_efectuadas, menu);
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
	    super.onCreateContextMenu(menu, v, menuInfo);
	    getMenuInflater().inflate(R.menu.menu_consultar_eliminar, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
			case R.id.consultar:
				i = new Intent(getApplicationContext(), Consignaciones_Consultar.class);
				startActivity(i);
				finish();
				return true;
	        case R.id.eliminar:
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
	   }
	}
	
	@Override
	public void onBackPressed() {}
}