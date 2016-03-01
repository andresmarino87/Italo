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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Asistente_Presupuesto_Cartera extends Activity {
	private TextView periodo;
	private TextView cumplimiento;
	private TextView presupuesto;
	private TextView cobros;
	private TextView ganado;
	private ItaloDBAdapter DBAdapter;
	private Cursor cursor;
	private Cursor cursor_sector;
	private Cursor cursor_search;
	private Spinner sector_input;
	private Spinner periodo_input;
	private Button buscar_button;
	private RadioButton todos_radio;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_asistente_presupuesto_cartera);
		init();
		loadValues();
		loadSectores();
		loadPeriodos();
		sector_input.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				if(cursor_sector.getCount()!=1){
					if(arg2==0){
						todos_radio.setChecked(true);
						reloadValues(sector_input.getSelectedItem().toString(),periodo_input.getSelectedItem().toString(),todos_radio.isChecked());
					}else{
						todos_radio.setChecked(false);
					}
					
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
			
		});
		todos_radio.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1){
					sector_input.setSelection(0);
				}
			}
			
		});

		
		buscar_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(periodo_input.getSelectedItem()!=null && sector_input.getSelectedItem()!=null){
					reloadValues(sector_input.getSelectedItem().toString(),periodo_input.getSelectedItem().toString(),todos_radio.isChecked());						
				}
			}
		});

	}

	private void init(){
		periodo=(TextView)findViewById(R.id.periodo_input);
		cumplimiento=(TextView)findViewById(R.id.porcentaje_cumplimiento_input);
		presupuesto=(TextView)findViewById(R.id.presupuesto_input);
		cobros=(TextView)findViewById(R.id.cobros_input);
		ganado=(TextView)findViewById(R.id.ganado_input);
		sector_input=(Spinner)findViewById(R.id.sector_input);
		periodo_input=(Spinner)findViewById(R.id.periodo_spinner);
		buscar_button=(Button)findViewById(R.id.buscar_button);
		todos_radio=(RadioButton)findViewById(R.id.todos_radio);
		DBAdapter=new ItaloDBAdapter(this);
		getApplicationContext().getString(R.string.todos);
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.asistente_presupuesto_cartera,menu);
		return true;
	}

	private void loadSectores(){
		cursor_sector=DBAdapter.getAsistentePresupuestoCarteraSectoreS();
		String strings[];
		if(cursor_sector.getCount()!=1){
			int i=1;
			strings = new String[cursor_sector.getCount()+1];
			strings[0]="";
			if(cursor_sector.moveToFirst()){
				do{
		        	strings[i] = cursor_sector.getString(0);
		        	i++;
				}while(cursor_sector.moveToNext());
			}	
	
		}else{
			int i=0;
			strings = new String[cursor_sector.getCount()];
			if(cursor_sector.moveToFirst()){
				do{
		        	strings[i] = cursor_sector.getString(0);
		        	i++;
				}while(cursor_sector.moveToNext());
			}	
		}
		
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sector_input.setAdapter(adapter);
		return;
	}
	
	private void loadPeriodos(){
		cursor_search=DBAdapter.getAsistentePresupuestoCarteraPeriodos();
		int i=0;
        String strings[] = new String[cursor_search.getCount()];
		if(cursor_search.moveToFirst()){
			do{
	        	strings[i] = cursor_search.getString(0);
	        	i++;
			}while(cursor_search.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		periodo_input.setAdapter(adapter);
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
		cursor=DBAdapter.getAsistentePresupuestoCartera();
		loadDataValues();
		return;
	}
	
	private void reloadValues(String sector,String periodo,boolean todos){
		cursor=DBAdapter.getAsistentePresupuestoCarteraXFiltro(sector, periodo, todos);
		loadDataValues();
		return;
	}
	
	private void loadDataValues(){
		if(cursor.moveToFirst()){
			periodo.setText(cursor.getString(0));
			cumplimiento.setText(Utility.formatNumber(cursor.getDouble(1)));
			presupuesto.setText(Utility.formatNumber(cursor.getDouble(2)));
			cobros.setText(Utility.formatNumber(cursor.getDouble(3)));
			ganado.setText(Utility.formatNumber(cursor.getDouble(4)));
		}
		return;
	}
	
	@Override
	public void onBackPressed() {}		
}