package visita;

import paretos.Paretos;
import pedidos_negados.Pedidos_Negados;
import utilidades.*;

import cartera.Cartera;
import cliente.Cliente;

import com.italo_view.R;

import consignaciones.Consignaciones_Tab;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import articulos.Articulos;
import asistente.Asistente;

public class Visita_Menu extends Activity {
	private GridView gridview;
	private Intent i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visita_menu);
		
        gridview = (GridView) findViewById(R.id.menu);
        gridview.setAdapter(new ImageAdapterVisitaMenu(this));
        gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
        		switch(arg2){
    				case 0:
    					i = new Intent(getApplicationContext(), Cliente.class);
    					i.putExtra("de", "menu");
    					startActivity(i);
    					break;
    				case 1:
    					i = new Intent(getApplicationContext(), Articulos.class);
    					i.putExtra("de", "menu");
    					startActivity(i);
    					break;
    				case 2:
    					i = new Intent(getApplicationContext(), Consignaciones_Tab.class);
    					i.putExtra("de", "menu");
    					startActivity(i);
    					break;
    				case 3:
    					i = new Intent(getApplicationContext(), Cartera.class);
    					i.putExtra("de", "menu");
    					startActivity(i);
    					break;
    				case 4:
/*    					i = new Intent(getApplicationContext(), Indicadores_Tab.class);
    					i.putExtra("de", "menu");
    					startActivity(i);
 */   					break;
    				case 5:
    					i = new Intent(getApplicationContext(), Asistente.class);
    					i.putExtra("de", "menu");
    					startActivity(i);
    					break;
    				case 6:
    					i = new Intent(getApplicationContext(), Paretos.class);
    					i.putExtra("de", "menu");
    					startActivity(i);
    					break;
    				case 7:
    					i = new Intent(getApplicationContext(), Pedidos_Negados.class);
    					i.putExtra("de", "menu");
    					startActivity(i);
    					break;
    				default : break;	

    			}
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.visita__menu, menu);
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
    
	@Override
	public void onBackPressed() {}
}