package asistente;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class Asistente_Asistente_General extends Activity {
	private TextView dias_ventas;
	private TextView dias_trascurridos;
	private TextView porcentaje;
	private TextView cartera;
	private TextView ventas;
	private TextView efectividad;
	private TextView posicion;
	private ItaloDBAdapter DBAdapter;
	private Cursor cursor;
	private Cursor cursor_search;
	private Spinner sector_input;
	private RadioButton todos_input;
	private Button buscar_button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_asistente_asistente_general);
		init();
		loadSectores();
		loadValues();
		sector_input.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				if(cursor_search.getCount()!=1){
					if(arg2==0){
						todos_input.setChecked(true);
						reloadValues(sector_input.getSelectedItem().toString(),todos_input.isChecked());
					}else{

						todos_input.setChecked(false);
					}
					
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
			
		});
		todos_input.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1){
					sector_input.setSelection(0);
				}
			}
			
		});
		buscar_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(sector_input.getSelectedItem()!=null){
					reloadValues(sector_input.getSelectedItem().toString(),todos_input.isChecked());
				}
			}
		});

	}

	private void init(){
		dias_ventas=(TextView)findViewById(R.id.dias_ventas_input);
		dias_trascurridos=(TextView)findViewById(R.id.dias_transcurridos_input);
		porcentaje=(TextView)findViewById(R.id.porcentaje_input);
		cartera=(TextView)findViewById(R.id.cartera_input);
		ventas=(TextView)findViewById(R.id.ventas_input);
		efectividad=(TextView)findViewById(R.id.efectividad_input);
		posicion=(TextView)findViewById(R.id.posicion_input);
		buscar_button=(Button)findViewById(R.id.buscar_button);
		sector_input=(Spinner)findViewById(R.id.sector_input);
		todos_input=(RadioButton)findViewById(R.id.todos_input);
		DBAdapter=new ItaloDBAdapter(this);
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.asistente_asistente_general,menu);
		return true;
	}
	
	private void loadSectores(){
		cursor_search=DBAdapter.getAsistenteGeneralSectoreS();
		String strings[];
		if(cursor_search.getCount()==1){
			int i=0;
	        strings = new String[cursor_search.getCount()];
			if(cursor_search.moveToFirst()){
				do{
		        	strings[i] = cursor_search.getString(0);
		        	i++;
				}while(cursor_search.moveToNext());
			}
		}else{
			int i=1;
	        strings = new String[cursor_search.getCount()+1];
	        strings[0]="";
			if(cursor_search.moveToFirst()){
				do{
		        	strings[i] = cursor_search.getString(0);
		        	i++;
				}while(cursor_search.moveToNext());
			}
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sector_input.setAdapter(adapter);
		return;
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
	
	private void loadValues(){
		cursor=DBAdapter.getAsistenteGeneral(sector_input.getSelectedItem().toString());
		loadDataValues();
		return;
	}
	
	private void reloadValues(String sector,boolean todos){
		cursor=DBAdapter.getAsistenteGeneralByFilters(sector,todos);
		loadDataValues();
		return;
	}
	
	private void loadDataValues(){
		if(cursor.moveToFirst()){
			dias_ventas.setText(cursor.getString(0));
			dias_trascurridos.setText(cursor.getString(1));
			porcentaje.setText(Utility.formatNumber(cursor.getDouble(2)));
			cartera.setText(Utility.formatNumber(cursor.getDouble(3)));
			ventas.setText(Utility.formatNumber(cursor.getDouble(4)));
			efectividad.setText(Utility.formatNumber(cursor.getDouble(5)));
			posicion.setText(cursor.getString(6));
		}
		return;
	}
	
	@Override
	public void onBackPressed() {}
}