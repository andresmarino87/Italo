package paretos;

import java.util.ArrayList;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class Paretos extends Activity {
	private ListView listParetos;
	private TextView total_row;
	private RadioButton ochenta;
	private RadioButton veinte;
	private ItaloDBAdapter DBAdapter;
	private Cursor cursor;
	private Cursor cursor_search;
	private Spinner sector_search;
	private Spinner ruta_search;
	private ArrayList<Paretos_Item> paretos;
	private Button buscar_button;
	private ParetoArrayAdapter adapter;
	static private String todas_str;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paretos);
		init();
		loadList();
		loadSector();
		loadRutas(sector_search.getSelectedItem().toString());
		sector_search.setOnItemSelectedListener((new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				loadRutas(sector_search.getSelectedItem().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		}));
		buscar_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(ochenta.isChecked()){
					reloadList(sector_search.getSelectedItem().toString(),ruta_search.getSelectedItem().toString(),"80");
				}else if(veinte.isChecked()){
					reloadList(sector_search.getSelectedItem().toString(),ruta_search.getSelectedItem().toString(),"20");
				}
			}
		});
		return;
	}
	
	private void init(){
		listParetos = (ListView)findViewById(R.id.listParetos);
		DBAdapter=new ItaloDBAdapter(this);
		total_row = (TextView)findViewById(R.id.total_row);
		buscar_button = (Button)findViewById(R.id.buscar_button);
		ochenta=(RadioButton)findViewById(R.id.ochenta_search);
		veinte=(RadioButton)findViewById(R.id.veinte_search);
		sector_search=(Spinner)findViewById(R.id.sector_search);
		ruta_search=(Spinner)findViewById(R.id.ruta_search);
		paretos=new ArrayList<Paretos_Item>();
        todas_str=getString(R.string.todas);
		return;
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.paretos, menu);
        return true;
    }

	private void loadSector(){
		cursor_search=DBAdapter.getParetosSector();
		int i=1;
        String strings[] = new String[cursor_search.getCount()+1];
        strings[0]=todas_str;
		if(cursor_search.moveToFirst()){
			do{
	        	strings[i] = cursor_search.getString(0);
	        	i++;
			}while(cursor_search.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sector_search.setAdapter(adapter);
		return;
	}	

	private void loadRutas(String sector){
		cursor_search=DBAdapter.GetRutasPedidosNegados(sector);
		int i=1;
        String strings[] = new String[cursor_search.getCount()+1];
        strings[0]=todas_str;
        if(cursor_search.moveToFirst()){
			do{
	        	strings[i] = cursor_search.getString(0);
	        	i++;
			}while(cursor_search.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ruta_search.setAdapter(adapter);
		return;
	}
	
    @SuppressLint("InflateParams") @Override
    public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
	    	case R.id.atras:
				finish();
	    		return true;
	    	default:
	    		return super.onOptionsItemSelected(item);
		}
    }
    
	void loadList(){
		cursor=DBAdapter.getParetos();
		loadDataList();
		adapter=new ParetoArrayAdapter(getApplicationContext(), R.layout.item_pareto, paretos);
		listParetos.setAdapter(adapter);
		return;
	}

		
	private void loadDataList(){
		int tam=cursor.getCount();
		if(cursor.moveToFirst()){
			do{
				paretos.add(new Paretos_Item(cursor.getString(0),
						cursor.getString(1),
						cursor.getString(2),
						cursor.getString(3),
						Utility.formatNumber(cursor.getDouble(4))));
			}while(cursor.moveToNext());
		}
		total_row.setText(Integer.toString(tam));
		return;
	}
	
	private void reloadList(String sector,String ruta,String porcentaje){
		paretos.clear();
		cursor=DBAdapter.getParetosByFilters(sector,ruta,porcentaje);
		loadDataList();
		adapter.notifyDataSetChanged();
		return;
	}
	
	@Override
	public void onBackPressed() {}
}