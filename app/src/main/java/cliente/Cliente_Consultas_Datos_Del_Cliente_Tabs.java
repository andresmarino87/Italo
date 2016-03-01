package cliente;

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
public class Cliente_Consultas_Datos_Del_Cliente_Tabs extends TabActivity {
	static private Intent i;
	static private Bundle extras;
	static TabHost tabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cliente_consultas_datos_del_cliente_tabs);
		
    	extras = getIntent().getExtras();
		tabHost = getTabHost();
		i = new Intent(this, Cliente_Consultas_Datos_Del_Cliente_General.class);
		i.putExtra("cliente_id", extras.getString("cliente_id"));
		addTab(getString(R.string.general), R.drawable.asistente,i);
		i = new Intent(this,Cliente_Consultas_Datos_Del_Cliente_Comercial.class );
		i.putExtra("cliente_id", extras.getString("cliente_id"));
		addTab(getString(R.string.comercial), R.drawable.atras,i);
		i = new Intent(this,Cliente_Consultas_Datos_Del_Cliente_Financiera.class );
		i.putExtra("cliente_id", extras.getString("cliente_id"));
		addTab(getString(R.string.financiera), R.drawable.atras,i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(
				R.menu.cliente_consultas_datos_del_cliente_tabs, menu);
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
	
    private void addTab(String label, int drawableId, Intent i) {
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
