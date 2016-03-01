package consignaciones;

import com.italo_view.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;

@SuppressWarnings("deprecation")
public class Consignaciones extends Activity {
	static private AlertDialog alertDialog;
	static private AlertDialog.Builder dialogBuilder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consignaciones);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.consignaciones,menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.buscar:
				dialogBuilder = new AlertDialog.Builder(this);
				dialogBuilder.setTitle(R.string.seleccione_filtros);
				dialogBuilder.setView(getLayoutInflater().inflate(R.layout.search_consignaciones, null));
				dialogBuilder.setPositiveButton(R.string.buscar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				dialogBuilder.setNegativeButton(R.string.cancelar,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				dialogBuilder.create().show();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed() {}
}
