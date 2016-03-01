package actividad_diaria;

import com.italo_view.R;

import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class Actividad_Diaria_Programar_Cobro_Tabs extends TabActivity  {
	private Intent i;
	private Bundle extras;
	private TabHost tabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actividad_diaria_programar_cobro_tab);
		init();
		tabHost = getTabHost();
		i = new Intent(this, Actividad_Diaria_Programar_Cobro.class);
		addTab(getString(R.string.programar_cobro),i);
		i = new Intent(this,Actividad_Diaria_Programar_Cobro_Pendiente.class );
		addTab(getString(R.string.pendiente),i);  
	}
	
	private void init(){
		extras=getIntent().getExtras();
		return;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.actividad_diaria_programar_cobros_tab, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
    	extras = getIntent().getExtras();
		switch (item.getItemId()){
			case R.id.adicionar_cobro:
				finish();
				return true;
			case R.id.eliminar_cobro:
				finish();
				return true;
	        case R.id.salir_app:
        		String de="";
				if(extras !=null){
					de = extras.getString("de");
				}	

				if(de.equalsIgnoreCase("actividad_diaria_consultar")){
					i = new Intent(getApplicationContext(), Actividad_Diaria_Consultar.class);
					startActivity(i);
					finish();
				}else if(de.equalsIgnoreCase("actividad_diaria_crear")){
					String fecha = extras.getString("fecha");
					i = new Intent(getApplicationContext(), Actividad_Diaria_Crear.class);
					i.putExtra("fecha", fecha);
					startActivity(i);
					finish();
				}else if(de.equalsIgnoreCase("actividad_diaria_extrarutas_consultar")){
					i = new Intent(getApplicationContext(), Actividad_Diaria_Extraruta.class);
					i.putExtra("de", "actividad_diaria_consultar");
					startActivity(i);
					finish();
				}else if(de.equalsIgnoreCase("actividad_diaria_extrarutas_crear")){
					i = new Intent(getApplicationContext(), Actividad_Diaria_Extraruta.class);
					i.putExtra("de", "actividad_diaria_crear");
					startActivity(i);
					finish();
				}
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}

	
	
	private void addTab(String label, Intent i) {
    	TabHost.TabSpec spec = tabHost.newTabSpec(label);
    	View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tabs, getTabWidget(), false);
    	TextView title = (TextView) tabIndicator.findViewById(R.id.title);
    	title.setText(label);
    	spec.setIndicator(tabIndicator).setContent(i);
    	tabHost.addTab(spec);
    }
	
	@Override
	public void onBackPressed() {}
}
