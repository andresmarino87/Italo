package cobros;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.Time;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

public class Cobros extends Activity {
	private Context context;
	private CalendarView date_picker;
	private AlertDialog.Builder dialogBuilder;
	private AlertDialog.Builder dialogBuilderFecha;
	private AlertDialog.Builder timeBuilder;
	private AlertDialog alertDialogFecha;
	private View alertViewFecha;
	private View timeView;
	private Intent i;
	private ListView listCobros;
	private Bundle extras;
	private ItaloDBAdapter DBAdapter;
	static private Cursor cursor;
	static private Cursor cursor_detalle;
	static private ArrayList<C_cobro> cobros;
	static private ArrayList<String> carteras_id;
	private View alertView;
	private TextView nombre_del_cliente_input;
	private TextView codigo_cliente_input;
	private TextView tipo_de_documento_input;
	private TextView numero_de_documento_input;
	private TextView sector_input;
	private TextView fecha_de_documento_input;
	private TextView fecha_de_vencimiento_input;
	private TextView dias_de_vencimiento_input;
	private TextView valor_original_input;
	private TextView valor_documento_input;
	private TextView saldo_actual_input;
	private TextView fecha_del_ultimo_pago_input;
	private TextView documento_input;
	private TextView valor_input;
	private EditText fecha_input;
	private EditText horario_input;
	private EditText fecha_input_search;
	private CalendarView date;
	private RadioGroup edadesGroup;
	static private String cliente_id;
	static private String cliente_nombre;
	static private String visita_id;
	static private TimePicker time;
    static private SimpleDateFormat dateFormat;
    static private SimpleDateFormat dateFormat2;
	static private CobrosArrayAdapter adapter;
	static private String searchDate;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cobros);
		init();
		loadList();
//		Utility.exportDB(context);
	}

	@SuppressLint("SimpleDateFormat")
	private void init(){
		context=this;
		extras = getIntent().getExtras();
		cliente_id=extras.getString("cliente_id");
		cliente_nombre=extras.getString("cliente_nombre");
		visita_id=extras.getString("visita_id");
		DBAdapter=new ItaloDBAdapter(this);
		listCobros=(ListView) this.findViewById(R.id.listCobros);
		cobros=new ArrayList<C_cobro>();
		carteras_id=new ArrayList<String>();
		dateFormat = new SimpleDateFormat("dd/MM/yy");
		dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
		nombre_del_cliente_input=(TextView)findViewById(R.id.nombre_del_cliente_input);
		nombre_del_cliente_input.setText(cliente_id+" "+cliente_nombre);
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.actividad_diaria_gestiones_cobros, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.adicionar_pago:
				carteras_id.clear();
				boolean hasChecked=false;
				int tam=0;
				SparseBooleanArray checkedItems = listCobros.getCheckedItemPositions();
				if (checkedItems != null) {
					tam=checkedItems.size();
					if(tam != 0){
						for (int i=0; i<tam; i++) {
		     		    	if (checkedItems.valueAt(i) && cursor.moveToPosition(checkedItems.keyAt(i))) {
			        			carteras_id.add(cursor.getString(0));
			        			hasChecked=true;
			        		}
		     		    }
		     			if(hasChecked){
			      		    i = new Intent(getApplicationContext(), Cobros_Pago.class);
			   				i.putExtra("visita_id", visita_id);
			   				i.putExtra("cliente_id", cliente_id);
			   				i.putExtra("cliente_nombre", cliente_nombre);
		        		    i.putExtra("carteras_id", carteras_id);
		        		    startActivity(i);
		     		    }else{
		     		    	Utility.showMessage(context, getString(R.string.seleccione_al_menos_un_elemento));
		     		    }
		     		}else{
	     		    	Utility.showMessage(context, getString(R.string.seleccione_al_menos_un_elemento));
		     		}
				}else{
     		    	Utility.showMessage(context, getString(R.string.seleccione_al_menos_un_elemento));
		     	}
				return true;
	        case R.id.atras:
	        	finish();
	        	return true;
	        case R.id.buscar:
	        	searchDate="";
	    		alertView=getLayoutInflater().inflate(R.layout.search_cobros, null);
	    		dialogBuilder= new AlertDialog.Builder(context);
	    		dialogBuilder.setTitle(R.string.buscar);
	    		dialogBuilder.setView(alertView);
	    		dialogBuilder.setCancelable(false);
	    		edadesGroup=(RadioGroup)alertView.findViewById(R.id.edadesGroup);
	    		fecha_input_search=(EditText)alertView.findViewById(R.id.fecha_input);
	    		
	    		fecha_input_search.setInputType(EditorInfo.TYPE_NULL);
	    		fecha_input_search.setOnTouchListener(new View.OnTouchListener() {
	    			@Override
	    			public boolean onTouch(View v, MotionEvent event) {
	    				if( event.getAction() == MotionEvent.ACTION_DOWN){
	    					alertViewFecha = getLayoutInflater().inflate(R.layout.date_picker, null);
	    					dialogBuilderFecha = new AlertDialog.Builder(context);
	    					dialogBuilderFecha.setTitle(R.string.modificar_fecha);
	    					dialogBuilderFecha.setCancelable(false);
	    					dialogBuilderFecha.setView(alertViewFecha);
	    					date_picker=(CalendarView)alertViewFecha.findViewById(R.id.date_picker);
	  //  					Time time = new Time();
	//    					time.setToNow();
//	    					time.month -=2;
	   // 					date_picker.setMinDate(time.normalize(true));
	    					dialogBuilderFecha.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	    						public void onClick(DialogInterface dialog, int which) {
	    							searchDate=dateFormat2.format(date_picker.getDate());
	    							fecha_input_search.setText(dateFormat.format(date_picker.getDate()));
	    						}
	    					});
	    					dialogBuilderFecha.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
	    						public void onClick(DialogInterface dialog, int which) {}
	    					});
	    					dialogBuilderFecha.create().show();
	    				}
	    				return false;
	               	}
	    		});
	    		
	    		
	    		dialogBuilder.setPositiveButton(R.string.buscar, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
	    				switch(edadesGroup.getCheckedRadioButtonId()){
	    					case R.id.menor_a_30:
	    						reloadList(0,searchDate);
	    						break;
	    					case R.id.mayor_a_30:
	    						reloadList(1,searchDate);
	    						break;
	    					case R.id.mayor_a_60:
	    						reloadList(2,searchDate);
	    						break;
	    					case R.id.mayor_a_90:
	    						reloadList(3,searchDate);
	    						break;
	    					case R.id.todas:
	    						reloadList(4,searchDate);
	    						break;
	    					default:
	    						reloadList(-1,searchDate);
	    						break;
	    				}
	    			}
	    		});	    		
	    		dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {}
	    		});
	    		dialogBuilder.create().show();
				return true;
	        case R.id.cobros_sinc:
      		    i = new Intent(getApplicationContext(), Cobros_Sincronizados.class);
   				i.putExtra("visita_id", visita_id);
   				i.putExtra("cliente_id", cliente_id);
   				i.putExtra("cliente_nombre", cliente_nombre);
    		    startActivity(i);
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
	    super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.ver_detalle);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.programar_pago);
	}
	
	private void loadDetalleAlerta(String documento_id){
		cursor_detalle=DBAdapter.getCarteraDetalle(documento_id);
		if(cursor_detalle.moveToFirst()){
			nombre_del_cliente_input.setText(cursor_detalle.getString(0));
			codigo_cliente_input.setText(cursor_detalle.getString(1));
			tipo_de_documento_input.setText(cursor_detalle.getString(2));
			numero_de_documento_input.setText(cursor_detalle.getString(3));
			sector_input.setText(cursor_detalle.getString(4));
			fecha_de_documento_input.setText(cursor_detalle.getString(5));
			fecha_de_vencimiento_input.setText(cursor_detalle.getString(6));
			if(cursor_detalle.getString(7)!= null && cursor_detalle.getString(7).equalsIgnoreCase("01/01/1900")){
				fecha_del_ultimo_pago_input.setText("");
			}else{
				fecha_del_ultimo_pago_input.setText(cursor_detalle.getString(7));
			}
			dias_de_vencimiento_input.setText(String.valueOf(cursor_detalle.getInt(8)));
			valor_original_input.setText("$ "+Utility.formatNumber(cursor_detalle.getFloat(9)));
			valor_documento_input.setText("$ "+Utility.formatNumber(cursor_detalle.getFloat(10)));
			saldo_actual_input.setText("$ "+Utility.formatNumber(cursor_detalle.getFloat(11)));
		}
		return;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case 0:
				alertView = getLayoutInflater().inflate(R.layout.cartera_detalle_alerta, null);
				dialogBuilder = new AlertDialog.Builder(this);
				dialogBuilder.setTitle(getString(R.string.detalles));
				dialogBuilder.setView(alertView);
				nombre_del_cliente_input = (TextView)alertView.findViewById(R.id.nombre_del_cliente_input);
				codigo_cliente_input = (TextView)alertView.findViewById(R.id.codigo_cliente_input);
				tipo_de_documento_input = (TextView)alertView.findViewById(R.id.tipo_de_documento_input);
				numero_de_documento_input = (TextView)alertView.findViewById(R.id.numero_de_documento_input);
				sector_input = (TextView)alertView.findViewById(R.id.sector_input);
				fecha_de_documento_input = (TextView)alertView.findViewById(R.id.fecha_de_documento_input);
				fecha_de_vencimiento_input = (TextView)alertView.findViewById(R.id.fecha_de_vencimiento_input);
				fecha_del_ultimo_pago_input = (TextView)alertView.findViewById(R.id.fecha_del_ultimo_pago_input);
				dias_de_vencimiento_input = (TextView)alertView.findViewById(R.id.dias_de_vencimiento_input);
				valor_original_input = (TextView)alertView.findViewById(R.id.valor_original_input);
				valor_documento_input = (TextView)alertView.findViewById(R.id.valor_documento_input);
				saldo_actual_input = (TextView)alertView.findViewById(R.id.saldo_actual_input);
				if(cursor.moveToPosition(info.position)){
					loadDetalleAlerta(cursor.getString(0));
				}
				dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				dialogBuilder.create().show();
		    	return true;
	        case 1:
	        	
	        	/*cursor.moveToPosition(info.position);
	        	alertView= getLayoutInflater().inflate(R.layout.activity_cobros_programar_pagos, null);
				dialogBuilder = new AlertDialog.Builder(this);
				dialogBuilder.setTitle(R.string.programar_pago);
				dialogBuilder.setView(alertView);
				documento_input = (TextView)alertView.findViewById(R.id.documento_input);
				fecha_input = (EditText)alertView.findViewById(R.id.fecha_input);
				horario_input = (EditText)alertView.findViewById(R.id.horario_input);
				valor_input = (TextView)alertView.findViewById(R.id.valor_input);
				documento_input.setText(cursor.getString(0));
				valor_input.setText(Utility.formatNumber(cursor.getFloat(4)));
				timeBuilder = new AlertDialog.Builder(this);
				fecha_input.setInputType(EditorInfo.TYPE_NULL);
				fecha_input.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						timeView = getLayoutInflater().inflate(R.layout.date_picker, null);
						timeBuilder.setTitle(getString(R.string.fecha));
						timeBuilder.setView(timeView);
						date=(CalendarView)timeView.findViewById(R.id.date_picker);
						timeBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								fecha_input.setText(dateFormat.format(date.getDate()));
							}
						});
						timeBuilder.create().show();
		           	}
				});
				timeBuilder = new AlertDialog.Builder(this);
				horario_input.setInputType(EditorInfo.TYPE_NULL);
				horario_input.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						timeView = getLayoutInflater().inflate(R.layout.time_picker, null);
						timeBuilder.setTitle(getString(R.string.hora));
						timeBuilder.setView(timeView);
						time=(TimePicker)timeView.findViewById(R.id.timePicker);
						time.setIs24HourView(true);
						timeBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								horario_input.setText(checkDigit(time.getCurrentHour())+":"+checkDigit(time.getCurrentMinute()));
							}
						});
						timeBuilder.create().show();
		           	}
				});
//				final Calendar cal = Calendar.getInstance();              
				dialogBuilder.setPositiveButton(getString(R.string.guardar), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						Intent intent = new Intent(Intent.ACTION_EDIT);
						intent.setType("vnd.android.cursor.item/event");
						intent.putExtra("beginTime", cal.getTimeInMillis());
						intent.putExtra("allDay", true);
						intent.putExtra("rrule", "FREQ=YEARLY");
						intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
						intent.putExtra("title", "A Test Event from android app");
						startActivity(intent);
					
					}
				});
				dialogBuilder.create().show();*/ 
				return true;
				
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	/*
	private class checkDate implements View.OnClickListener {
		private final Dialog dialog;
	    public checkDate(Dialog dialog) {
	        this.dialog = dialog;
	    }
	    @Override
	    public void onClick(View v) {
			fechaSelec=0;
			long hoy=0;
			Calendar cal= Calendar.getInstance();
			Time time = new Time();
			time.set(cal.getTime().getTime());
			fechaSelec=date_picker.getDate();
			hoy=time.toMillis(true);
			if(cursor.getString(cursor.getColumnIndex("valida_fecha")).equalsIgnoreCase("<")){
				if(hoy>fechaSelec){
					fecha=dateFormat2.format(fechaSelec);
					fecha_cheque_input.setText(fecha);
					dialog.dismiss();
				}else{
					Utility.showMessage(context, getString(R.string.seleccione_una_fecha_menor));
				}
			}else if(cursor.getString(cursor.getColumnIndex("valida_fecha")).equalsIgnoreCase("<=")){
				if(hoy>=fechaSelec){
					fecha=dateFormat2.format(fechaSelec);
					fecha_cheque_input.setText(fecha);
					dialog.dismiss();
				}else{
					Utility.showMessage(context, getString(R.string.seleccione_una_fecha_actual_o_menor));
				}
				
			}else if(cursor.getString(cursor.getColumnIndex("valida_fecha")).equalsIgnoreCase(">")){
				if(hoy<fechaSelec){
					fecha=dateFormat2.format(fechaSelec);
					fecha_cheque_input.setText(fecha);
					dialog.dismiss();
				}else{
					Utility.showMessage(context, getString(R.string.seleccione_una_fecha_mayor));
				}
			}else if(cursor.getString(cursor.getColumnIndex("valida_fecha")).equalsIgnoreCase(">=")){
				if(hoy<=fechaSelec){
					fecha=dateFormat2.format(fechaSelec);
					fecha_cheque_input.setText(fecha);
					dialog.dismiss();
				}else{
					Utility.showMessage(context, getString(R.string.seleccione_una_fecha_actual_o_mayor));
				}
			}
	    }
	}*/
	
	void loadList(){
		cursor=DBAdapter.getCarteraByClienteParaCobros(cliente_id);
		loadDataList();
		adapter=new CobrosArrayAdapter(getApplicationContext(), R.layout.item_cartera, cobros);
		listCobros.setAdapter(adapter);
		registerForContextMenu(listCobros);
		return;
	}

	void reloadList(){
		cobros.clear();
		cursor=DBAdapter.getCarteraByClienteParaCobros(cliente_id);
		loadDataList();
		adapter.notifyDataSetChanged();
		return;
	}

	void reloadList(int type, String date){
		cobros.clear();
		cursor=DBAdapter.getCarteraByClienteParaCobrosConFiltros(cliente_id,type,date);
		loadDataList();
		adapter.notifyDataSetChanged();
		return;
	}
	
	public void loadDataList(){
		if(cursor != null && cursor.moveToFirst()){
			do{
				cobros.add(new C_cobro(
					cursor.getString(0),
					cursor.getString(1),
					cursor.getInt(2),
					cursor.getFloat(3),
					cursor.getFloat(4),
					cursor.getString(5),
					cursor.getString(6)
				));
			}while(cursor.moveToNext());
		}
	}

	public String checkDigit(int number){
		return number<=9?"0"+number:String.valueOf(number);
	}
	 
	protected void onResume(){
//		Date nowResume = new Date();
//		String [] dAux= cursor_head.getString(3).split("/");
//		int fAux= Integer.parseInt(dAux[2]+dAux[0]+dAux[1]);
//		dateFormat.format(now);
		super.onResume();
		listCobros.clearChoices();
		for (int i = 0; i < listCobros.getCount(); i++)
			listCobros.setItemChecked(i, false);
		reloadList();
	   
/*		if(fAux < Integer.parseInt(dateFormat.format(nowResume).replaceAll("-", ""))){
			isToday=-1;
		}else if(fAux == Integer.parseInt(dateFormat.format(nowResume).replaceAll("-", ""))){
			isToday=0;
		}else{
			isToday=1;
		}*/
	}
		
	@Override
	public void onBackPressed() {}
}

