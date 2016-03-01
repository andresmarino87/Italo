package asistente;

import java.util.Calendar;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

@SuppressWarnings("deprecation")
public class Asistente_Acumulado_De_Ventas extends Activity implements OnTouchListener{
//	static private Intent i;
	private TextView ene ;
	private TextView feb ;
	private TextView mar ;
	private TextView abr ;
	private TextView may ;
	private TextView jun ;
	private TextView jul ;
	private TextView ago ;
	private TextView sep ;
	private TextView oct ;
	private TextView nov ;
	private TextView dic ;
	private TextView periodo_input ;
	private TextView presupuesto_input ;
	private TextView ventas_input ;
	private TextView porcentaje_input ;
	private TextView posicion_input ;
	private RadioButton todos_input ;
	private Spinner sector_input ;
	static private String actualMonth;
	private Calendar calendar;
	private ItaloDBAdapter DBAdapter;
	private Cursor cursor;
	private Cursor cursor_search;
	private Button buscar_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_asistente_acumulado_de_ventas);
		init();
		setActualMonth();
		loadSectores();
		sector_input.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				if(cursor_search.getCount()!=1){
					if(arg2==0){
						todos_input.setChecked(true);
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
				reloadData(sector_input.getSelectedItem().toString(),todos_input.isChecked());
			}});
	}

	private void init(){
		ene=(TextView)findViewById(R.id.ene);
		feb=(TextView)findViewById(R.id.feb);
		mar=(TextView)findViewById(R.id.mar);
		abr=(TextView)findViewById(R.id.abr);
		may=(TextView)findViewById(R.id.may);
		jun=(TextView)findViewById(R.id.jun);
		jul=(TextView)findViewById(R.id.jul);
		ago=(TextView)findViewById(R.id.ago);
		sep=(TextView)findViewById(R.id.sep);
		oct=(TextView)findViewById(R.id.oct);
		nov=(TextView)findViewById(R.id.nov);
		dic=(TextView)findViewById(R.id.dic);
		ene.setOnTouchListener(this);
		feb.setOnTouchListener(this);
		mar.setOnTouchListener(this);
		abr.setOnTouchListener(this);
		may.setOnTouchListener(this);
		jun.setOnTouchListener(this);
		jul.setOnTouchListener(this);
		ago.setOnTouchListener(this);
		sep.setOnTouchListener(this);
		oct.setOnTouchListener(this);
		nov.setOnTouchListener(this);
		dic.setOnTouchListener(this);
		periodo_input=(TextView)findViewById(R.id.periodo_input);
		presupuesto_input=(TextView)findViewById(R.id.presupuesto_input);
		ventas_input=(TextView)findViewById(R.id.ventas_input);
		porcentaje_input=(TextView)findViewById(R.id.porcentaje_input);
		posicion_input=(TextView)findViewById(R.id.posicion_input);
		buscar_button=(Button)findViewById(R.id.buscar_button);
		todos_input=(RadioButton)findViewById(R.id.todos_input) ;
		sector_input=(Spinner)findViewById(R.id.sector_input) ;

		calendar = Calendar.getInstance();
		actualMonth = String.valueOf(calendar.get(Calendar.MONTH)+1);
		DBAdapter=new ItaloDBAdapter(this);
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.asistente_acumulado_de_ventas,menu);
		return true;
	}
	
	private void loadSectores(){
		cursor_search=DBAdapter.getAsistenteAcumuladoVentasSectores();
		String strings[];
		if(cursor_search.getCount()!=1){
			int i=1;
	        strings = new String[cursor_search.getCount()+1];
	        strings[0]="";
			if(cursor_search.moveToFirst()){
				do{
		        	strings[i] = cursor_search.getString(0);
		        	i++;
				}while(cursor_search.moveToNext());
			}

		}else{
			int i=0;
	        strings = new String[cursor_search.getCount()];
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

	@SuppressLint("ClickableViewAccessibility") @Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		switch (arg0.getId()){
			case R.id.ene:
				clearMonth(actualMonth);
				setMonth(ene);
				actualMonth="01";
//				loadData();
				break;
			case R.id.feb:
				clearMonth(actualMonth);
				setMonth(feb);
				actualMonth="02";
//				loadData();
				break;
			case R.id.mar:
				clearMonth(actualMonth);
				setMonth(mar);
				actualMonth="03";
//				loadData();
				break;
			case R.id.abr:
				clearMonth(actualMonth);
				setMonth(abr);
				actualMonth="04";
//				loadData();
				break;
			case R.id.may:
				clearMonth(actualMonth);
				setMonth(may);
				actualMonth="05";
//				loadData();
				break;
			case R.id.jun:
				clearMonth(actualMonth);
				setMonth(jun);
				actualMonth="06";
//				loadData();
				break;
			case R.id.jul:
				clearMonth(actualMonth);
				setMonth(jul);
				actualMonth="07";
//				loadData();
				break;
			case R.id.ago:
				clearMonth(actualMonth);
				setMonth(ago);
				actualMonth="08";
//				loadData();
				break;
			case R.id.sep:
				clearMonth(actualMonth);
				setMonth(sep);
				actualMonth="09";
//				loadData();
				break;
			case R.id.oct:
				clearMonth(actualMonth);
				setMonth(oct);
				actualMonth="10";
//				loadData();
				break;
			case R.id.nov:
				clearMonth(actualMonth);
				setMonth(nov);
				actualMonth="11";
//				loadData();
				break;
			case R.id.dic:
				clearMonth(actualMonth);
				setMonth(dic);
				actualMonth="12";
//				loadData();
				break;
			default:
				break;
		}
		return false;
	}
	
	private boolean clearMonth(String actualMonth){
		int month=Integer.parseInt(actualMonth);
		switch (month){
			case 1:
				ene.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_blanco));
				ene.setTextColor(Color.BLACK);
				break;
			case 2:
				feb.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_blanco));
				feb.setTextColor(Color.BLACK);
				break;
			case 3:
				mar.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_blanco));
				mar.setTextColor(Color.BLACK);
				break;
			case 4:
				abr.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_blanco));
				abr.setTextColor(Color.BLACK);
				break;
			case 5:
				may.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_blanco));
				may.setTextColor(Color.BLACK);
				break;
			case 6:
				jun.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_blanco));
				jun.setTextColor(Color.BLACK);
				break;
			case 7:
				jul.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_blanco));
				jul.setTextColor(Color.BLACK);
				break;
			case 8:
				ago.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_blanco));
				ago.setTextColor(Color.BLACK);
				break;
			case 9:
				sep.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_blanco));
				sep.setTextColor(Color.BLACK);
				break;
			case 10:
				oct.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_blanco));
				oct.setTextColor(Color.BLACK);
				break;
			case 11:
				nov.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_blanco));
				nov.setTextColor(Color.BLACK);
				break;
			case 12:
				dic.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_blanco));
				dic.setTextColor(Color.BLACK);
				break;
			default:
				break;
		}
		return true;
	}
	
	private boolean setMonth(TextView month){
		month.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_azul));
		month.setTextColor(Color.WHITE);
		return true;
	}
	
	private boolean setActualMonth(){
		int month=Integer.parseInt(actualMonth);
		loadData();
		switch (month){
			case 1:
				setMonth(ene);
				break;
			case 2:
				setMonth(feb);
				break;
			case 3:
				setMonth(mar);
				break;
			case 4:
				setMonth(abr);
				break;
			case 5:
				setMonth(may);
				break;
			case 6:
				setMonth(jun);
				break;
			case 7:
				setMonth(jul);
				break;
			case 8:
				setMonth(ago);
				break;
			case 9:
				setMonth(sep);
				break;
			case 10:
				setMonth(oct);
				break;
			case 11:
				setMonth(nov);
				break;
			case 12:
				setMonth(dic);
				break;
			default:
				break;
		}
		return true;
	}

	private void loadData(){
		cursor=DBAdapter.getAsistenteAcumuladoVentas(actualMonth);
		loadValueData();
		return;
	}

	private void reloadData(String sector, boolean todos){
		cursor=DBAdapter.getAsistenteAcumuladoVentas(actualMonth,sector,todos);
		loadValueData();
		return;
	}

	private void loadValueData(){
		if(cursor.moveToFirst()){
			periodo_input.setText(cursor.getString(0));
			presupuesto_input.setText(Utility.formatNumber(cursor.getDouble(1)));
			ventas_input.setText(Utility.formatNumber(cursor.getDouble(2)));
			porcentaje_input.setText(Utility.formatNumber(cursor.getDouble(3)));
			posicion_input.setText(cursor.getString(4));
		}
		return;
	}
	
	@Override
	public void onBackPressed() {}
}