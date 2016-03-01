package cobros_programados;

import java.util.ArrayList;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;

public class Programar_Cobro extends Activity {
	private Context context;
	private Bundle extras;
	private ItaloDBAdapter DBAdapter;
	private ListView listFacturas;
	private Cursor cursor;
	private ArrayList<Programar_Cobro_Item> facturas;
	private ArrayList<String> facturas_id;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_programar_cobros);
		init();
		loadData();
	}

	private void init(){
		context = this;
		extras = getIntent().getExtras();
		facturas_id = extras.getStringArrayList("facturas_id");
		DBAdapter = new ItaloDBAdapter(this);
		listFacturas=(ListView) this.findViewById(R.id.listFacturas);
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.programar_cobros, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
			case R.id.atras:
				finish();	
				return true;
			case R.id.guardar_programar_cobro:
				return true;
	    	default:
	    		return super.onOptionsItemSelected(item);
		}
	}
    
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.ver_detalle);
	}
	

	public void loadData()
	{
		cursor = DBAdapter.getDocumentosParaProgramar(facturas_id);
		
		int tam = cursor.getCount();
		facturas = new ArrayList<Programar_Cobro_Item>(tam);
		if(cursor.moveToFirst()){
			do{
				facturas.add(new Programar_Cobro_Item (
						cursor.getString(1),
						cursor.getString(0),
						cursor.getString(2),
						cursor.getString(3),
						Utility.formatNumber(cursor.getDouble(4))
						));
			}while(cursor.moveToNext());
		}
		
		listFacturas.setAdapter(new ProgramarCobroArrayAdapter(context, R.layout.item_programar_cobro, facturas));
		registerForContextMenu(listFacturas);
	}
	
	@Override
	public void onBackPressed() {}
}