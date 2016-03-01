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
public class Cliente_Consultas_Historicos_Tabs extends TabActivity {
	private Intent i;
	private Bundle extras;
	private TabHost tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cliente_consultas_historicos_tabs);
    	extras = getIntent().getExtras();

		tabHost = getTabHost();
		i = new Intent(this,Cliente_Consultas_Historicos_Pedidos.class );
		i.putExtra("cliente_id", extras.getString("cliente_id"));
		i.putExtra("cliente_nombre", extras.getString("cliente_nombre"));
		addTab(getString(R.string.pedidos), R.drawable.asistente,i);
		i = new Intent(this,Cliente_Consultas_Historicos_Recaudos.class );
		i.putExtra("cliente_id", extras.getString("cliente_id"));
		i.putExtra("cliente_nombre", extras.getString("cliente_nombre"));
		addTab(getString(R.string.cobros), R.drawable.atras,i);
		i = new Intent(this,Cliente_Consultas_Historicos_Otros_Eventos.class );
		i.putExtra("cliente_id", extras.getString("cliente_id"));
		i.putExtra("cliente_nombre", extras.getString("cliente_nombre"));
		addTab(getString(R.string.otras_gestiones), R.drawable.atras,i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.cliente_consultas_historicos_tabs,	menu);
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
