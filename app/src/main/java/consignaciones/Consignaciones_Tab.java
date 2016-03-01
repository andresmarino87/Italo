package consignaciones;

import com.italo_view.R;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class Consignaciones_Tab extends TabActivity {
	static private Intent i;
	static private AlertDialog.Builder dialogBuilder;
	static TabHost tabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consignaciones_tab);
		
		tabHost = getTabHost();
		addTab(getString(R.string.pagos_efectuados), R.drawable.asistente,new Intent(this, Consignaciones.class));
		addTab(getString(R.string.consignaciones_efectuadas), R.drawable.atras,new Intent(this,Consignaciones_Efectuadas.class ));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.consignaciones_tab, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){		switch (item.getItemId()){
			case R.id.buscar:
				dialogBuilder = new AlertDialog.Builder(this);
				dialogBuilder.setTitle(R.string.seleccione_filtros);
				dialogBuilder.setView(getLayoutInflater().inflate(R.layout.search_consignaciones, null));
				dialogBuilder.setPositiveButton(R.string.buscar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// here you can add functions
					}
				});
				dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				dialogBuilder.create().show();
				return true;
	        case R.id.atras:
				finish();
				return true;
	        case R.id.adicionar:
				i = new Intent(getApplicationContext(), Consignaciones_Adicionar.class);
				startActivity(i);
				finish();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}
	
    private void addTab(String label, int drawableId, Intent i) {
//    	Intent intent = new Intent(this, Indicadores.class);
    	TabHost.TabSpec spec = tabHost.newTabSpec(label);
    	View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tabs, getTabWidget(), false);
    	TextView title = (TextView) tabIndicator.findViewById(R.id.title);
    	title.setText(label);
//    	ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
//   	icon.setImageResource(drawableId);
    	spec.setIndicator(tabIndicator).setContent(i);
    	tabHost.addTab(spec);
    }
	
	@Override
	public void onBackPressed() {}
}
