package consignaciones;

import com.italo_view.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class Consignaciones_Adicionar extends Activity {
	static private Intent i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consignaciones_adicionar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.consignaciones_adicionar, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.eliminar:
				return true;
			case R.id.guardar:
	    		return true;
	
			case R.id.atras:
					i= new Intent(getApplicationContext(), Consignaciones_Tab.class);
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
