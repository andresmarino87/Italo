package indicadores;

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
public class Indicadores_Tab extends TabActivity  {
	static TabHost tabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_indicadores_tab);

		tabHost = getTabHost();
		addTab(getString(R.string.indicadores_del_dia), R.drawable.asistente,new Intent(this, Indicadores.class));
		addTab(getString(R.string.pedidos), R.drawable.atras,new Intent(this,Indicadores_Pedidos_Resumen.class ));
		addTab(getString(R.string.cobros), R.drawable.atras,new Intent(this,Indicadores_Cobros_Resumen.class ));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.indicadores_tab, menu);
		return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
        	case R.id.atras:
        		finish();
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