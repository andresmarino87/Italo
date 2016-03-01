package cobros;

import java.util.ArrayList;

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
public class Cobros_Pago_Tabs extends TabActivity {
	private Intent i;
	private Bundle extras;
	private TabHost tabHost;
	static private String cliente_id;
	static private String cliente_nombre;
	static private ArrayList<String> carteras_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cobros_pago_tabs);
		init();
		
    	extras = getIntent().getExtras();
		tabHost = getTabHost();
		i = new Intent(this, Cobros_Pago.class);
		i.putExtra("cliente_id", cliente_id);
		i.putExtra("cliente_nombre", cliente_nombre);
	    i.putExtra("carteras_id", carteras_id);
		addTab(getString(R.string.pago), R.drawable.asistente,i);
		i = new Intent(this,Cobros_Pago_Tipo.class );
		i.putExtra("cliente_id", cliente_id);
		i.putExtra("cliente_nombre", cliente_nombre);
	    i.putExtra("carteras_id", carteras_id);
		addTab(getString(R.string.forma_de_pago), R.drawable.atras,i);
		i = new Intent(this,Cobros_Pago_Resumen.class );
		i.putExtra("cliente_id", cliente_id);
		i.putExtra("cliente_nombre", cliente_nombre);
	    i.putExtra("carteras_id", carteras_id);
		addTab(getString(R.string.resumen), R.drawable.atras,i);
	}

	private void init(){
    	extras = getIntent().getExtras();
    	cliente_id=extras.getString("cliente_id");
    	cliente_nombre=extras.getString("cliente_nombre");
    	carteras_id=extras.getStringArrayList("carteras_id");
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cobros_pago_tabs, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		Cobros_Pago_Tipo tab2=null;
		Cobros_Pago_Resumen tab3=null;
		switch (item.getItemId()){
			case R.id.adicionar_pago:
	        	tabHost.setCurrentTab(1);
	        	tab2=(Cobros_Pago_Tipo)this.getCurrentActivity();
	        	if(tab2.addPago()){
		        	tabHost.setCurrentTab(2);
	        		tab3=(Cobros_Pago_Resumen)this.getCurrentActivity();
	        		tab3.reloadList();
	        	}
				return true;
			case R.id.guardar:
				i = new Intent(getApplicationContext(), Cobros.class);
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("nombre_cliente", cliente_nombre);
				startActivity(i);
				finish();
				return true;
	        case R.id.atras:
				i = new Intent(getApplicationContext(), Cobros.class);
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("nombre_cliente", cliente_nombre);
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
}