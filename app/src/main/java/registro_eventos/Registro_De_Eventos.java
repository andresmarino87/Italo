package registro_eventos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class Registro_De_Eventos extends Activity implements OnClickListener {
	private AlertDialog alertDialog;
	private AlertDialog.Builder dialogBuilder;
	private Bundle extras;
	private TextView vendedor_input;
	private TextView cliente_input;
	private TextView observaciones;
	private TextView fecha_actual_input;
	private Spinner tipo_tiempo_input;
	private Spinner evento_input;
	private TextView fecha_input;
	private TextView hora_input;
	private EditText observaciones_input;
	private EditText cantidad_tiempo_input;
	private LinearLayout layout_fecha;
	private ListView listRegistoVisitas;
	private ListView listFacturas;
	private HorizontalScrollView horizontalScrollView1;
	private View alertView;
	private Context context;
	private CalendarView date_picker;
	private TimePicker timePicker;
	private ItaloDBAdapter DBAdapter;
	private EventosArrayAdapter adapter;
	private RE_facturaArrayAdapter adapterFacturas;
	static private String cliente_id;
	static private String cliente_nombre;
	static private String visita_id;
	static private Long fecha_seleccionada;
	static private Long fecha;
	static private SimpleDateFormat dateFormat;
	static private SimpleDateFormat dateFormat2;
	private Cursor cursor;
	private Cursor cursor_lista;
	private Cursor cursor_tipo_horario;
	private Cursor cursor_facturas;
	private Cursor cursor_modficiar;
	private InputMethodManager inputManager;
	static private ArrayList<Evento> eventos;
	static private boolean res;
	static private boolean horaSeleccionada;
	static private ArrayList<RE_factura> facturas;
	static private Calendar calendar;
	static private Evento auxEvento;
	static private Long fechaMSalvar;
	static private Long horaMSalvar;
	static private String horaIni;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registro_de_eventos);
		init();
		loadName();
		loadList();
//		loadFacturas(listFacturas,cliente_id);
    	evento_input.setOnItemSelectedListener(new OnItemSelectedListener(){
    		@Override
    		public void onItemSelected(AdapterView<?> arg0, View arg1,int pos, long arg3) {
    			if(cursor.moveToPosition(pos)){
    				if(cursor.getString(3)!=null && cursor.getString(3).equalsIgnoreCase("s")){
    					layout_fecha.setVisibility(View.VISIBLE);
    				}else{
    					layout_fecha.setVisibility(View.GONE);
    				}
    			
    				if(cursor.getString(4)!=null && cursor.getString(4).equalsIgnoreCase("s")){
    					horizontalScrollView1.setVisibility(View.VISIBLE);
    					loadFacturas(listFacturas,cliente_id);
    				}else{
    					horizontalScrollView1.setVisibility(View.GONE);
    				}					
    			}
    		}

    		@Override
    		public void onNothingSelected(AdapterView<?> arg0) {}
    	});
	}
	
	@SuppressLint("SimpleDateFormat")
	private void init(){
		context=this;
    	inputManager =  (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE); 
		DBAdapter=new ItaloDBAdapter(this);
		facturas = new ArrayList<RE_factura>();
		dateFormat=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		fecha_seleccionada=(long) 0;
		calendar = Calendar.getInstance();
		dateFormat2= new SimpleDateFormat("MM/dd/yyyy"); 
    	extras = getIntent().getExtras();
    	cliente_id=extras.getString("cliente_id");
    	cliente_nombre=extras.getString("cliente_nombre");
    	visita_id=extras.getString("visita_id");
    	vendedor_input=(TextView) findViewById(R.id.vendedor_input);
    	cliente_input=(TextView) findViewById(R.id.cliente_input);
    	fecha_actual_input=(TextView)findViewById(R.id.fecha_actual_input);
    	cliente_input.setText(cliente_id+" "+cliente_nombre);
    	fecha_input=(TextView) findViewById(R.id.fecha_input);
    	hora_input=(TextView) findViewById(R.id.hora_input);
    	evento_input=(Spinner) findViewById(R.id.evento_input);
    	observaciones_input=(EditText) findViewById(R.id.observaciones_input);
    	listRegistoVisitas=(ListView) findViewById(R.id.listRegistoVisitas);
    	tipo_tiempo_input=(Spinner) findViewById(R.id.tipo_tiempo_input);
    	cantidad_tiempo_input=(EditText) findViewById(R.id.cantidad_tiempo_input);
    	layout_fecha=(LinearLayout)findViewById(R.id.layout_fecha);
		listFacturas=(ListView)findViewById(R.id.listFacturas);
		horizontalScrollView1=(HorizontalScrollView)findViewById(R.id.horizontalScrollView1);
    	fecha_actual_input.setText(DBAdapter.getActualDate());
		eventos=new ArrayList<Evento>();
    	fecha_input.setInputType(EditorInfo.TYPE_NULL);
    	fecha_input.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("InflateParams") @Override
			public void onClick(View v) {
				alertView = getLayoutInflater().inflate(R.layout.date_picker, null);
				dialogBuilder = new AlertDialog.Builder(context);
				dialogBuilder.setTitle(R.string.fecha);
				dialogBuilder.setView(alertView);
				dialogBuilder.setCancelable(false);
				date_picker=(CalendarView)alertView.findViewById(R.id.date_picker);
				dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(calendar.getTimeInMillis() < date_picker.getDate()){
							fecha_seleccionada=date_picker.getDate();
							fecha_input.setText(dateFormat2.format(date_picker.getDate()));
							fecha=date_picker.getDate();
						}else{
							Utility.showMessage(context, getString(R.string.fecha_mayor_hoy));
						}
					}
				});
				dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				dialogBuilder.create().show();
			}
    	});
    	
    	hora_input.setInputType(EditorInfo.TYPE_NULL);
    	hora_input.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("InflateParams") @Override
			public void onClick(View v) {
				alertView = getLayoutInflater().inflate(R.layout.time_picker, null);
				dialogBuilder = new AlertDialog.Builder(context);
				dialogBuilder.setTitle(R.string.hora);
				dialogBuilder.setView(alertView);
				dialogBuilder.setCancelable(false);
				timePicker=(TimePicker)alertView.findViewById(R.id.timePicker);
				timePicker.setIs24HourView(true);
				dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						timePicker.clearFocus();
						timePicker.requestFocus();
						String hours=(timePicker.getCurrentHour().toString().length()==1)?"0"+timePicker.getCurrentHour().toString():timePicker.getCurrentHour().toString();
						String minutes=(timePicker.getCurrentMinute().toString().length()==1)?"0"+timePicker.getCurrentMinute().toString():timePicker.getCurrentMinute().toString();
//						hora_input.setText(timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute());
						hora_input.setText(hours+":"+minutes);
						fecha_seleccionada+=(timePicker.getCurrentHour()*3600000)+(timePicker.getCurrentMinute()*60000);
						horaSeleccionada=true;
					}
				});
				dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				dialogBuilder.create().show();
			}
    	});
		horizontalScrollView1.setVisibility(View.GONE);
    	layout_fecha.setVisibility(View.GONE);
    	horaSeleccionada=false;
    	horaIni=dateFormat.format(new Date());
//    	horaIni="1900-00-00 00:00:00";
    	return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actividad_diaria_gestiones_registro_de_eventos, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.guardar:
				DBAdapter.beginTransaction();
				if(saveEvento(visita_id)){
					DBAdapter.setTransactionSuccessful();
			    	horaIni=dateFormat.format(new Date());
			//    	horaIni="1900-00-00 00:00:00";
			    	inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS); 
					reloadList();
				}else{
//					Utility.showMessage(context, getString(R.string.hubo_un_error_al_guardar_el_evento));
				}
				DBAdapter.endTransaction();
				return true;
			case R.id.atras:
				finish();
	    		return true;
		   	default:
		   		return super.onOptionsItemSelected(item);
		}
	}
    
	private void loadName(){
		vendedor_input.setText(DBAdapter.getVendedor());
		loadEventos();
		loadTipoHorarios();
		return;
	}
	
	private void loadEventos(){
		int i=0;
		cursor=DBAdapter.getEventos();
		String[] eventos;
		if(cursor.moveToFirst()){
			eventos=new String[cursor.getCount()];
			do{
				eventos[i]=cursor.getString(1);
				i++;
			}while(cursor.moveToNext());
			ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, eventos);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			evento_input.setAdapter(adapter);
		}
		return;
	}

	private void loadTipoHorarios(){
		int i=0;
		cursor_tipo_horario=DBAdapter.getTipoHorarios();
		String[] tipo_horario;
		if(cursor_tipo_horario.moveToFirst()){
			tipo_horario=new String[cursor_tipo_horario.getCount()];
			do{
				tipo_horario[i]=cursor_tipo_horario.getString(1);
				i++;
			}while(cursor_tipo_horario.moveToNext());
			ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipo_horario);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			tipo_tiempo_input.setAdapter(adapter);
		}
		return;
	}

	private void loadTipoHorariosMod(Spinner spinner){
		int i=0;
		cursor_tipo_horario=DBAdapter.getTipoHorarios();
		String[] tipo_horario;
		if(cursor_tipo_horario.moveToFirst()){
			tipo_horario=new String[cursor_tipo_horario.getCount()];
			do{
				tipo_horario[i]=cursor_tipo_horario.getString(1);
				i++;
			}while(cursor_tipo_horario.moveToNext());
			ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipo_horario);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
		}
		return;
	}
	
	public boolean saveEvento(String visitaId){
		res=true;
		final String visita_id=visitaId;
		int pos=evento_input.getSelectedItemPosition();
		if(cursor.moveToPosition(pos)){
			if(!eventExists(cursor.getString(0))){
				if(cursor.getString(4)!=null && cursor.getString(4).equalsIgnoreCase("s")){
					if(observaciones_input.getText().toString().length()>50){
						if(cursor.getString(3).equalsIgnoreCase("S")){
							if(horaSeleccionada){
								if(fecha !=null 
									&& fecha!=0
									&& cursor_tipo_horario.moveToPosition(tipo_tiempo_input.getSelectedItemPosition())
									&& cantidad_tiempo_input.getText().toString()!=null
									&& !cantidad_tiempo_input.getText().toString().trim().equalsIgnoreCase("")){
									int tam=0;
									SparseBooleanArray checkedItems = listFacturas.getCheckedItemPositions();
									if (checkedItems != null) {
										tam=checkedItems.size();
										if(tam != 0){
											String timeType=cursor_tipo_horario.getString(0);
											String timeCuantity=cantidad_tiempo_input.getText().toString();
											addToCalendar(fecha_seleccionada,timeType,timeCuantity);
											res=DBAdapter.addEvento(visita_id,cursor.getString(0),observaciones_input.getText().toString(),"DATETIME("+(fecha_seleccionada/1000)+", 'unixepoch','localtime')",timeCuantity.toString(),timeType,0,0,true,-1,horaIni);
											for (int i=0; i<tam; i++) {
												if (checkedItems.valueAt(i) && cursor_facturas.moveToPosition(checkedItems.keyAt(i)) && res) {
													res=DBAdapter.addEventoFactura(visita_id, cursor.getString(0), cursor_facturas.getString(6), cursor_facturas.getString(0), "C");
												}
											}
											if(res){
												cleanEvento();
											}else{
												Utility.showMessage(context, getString(R.string.hubo_un_error_al_guardar_el_evento));
											}
										}else{
											Utility.showMessage(context, getString(R.string.seleccione_al_menos_un_elemento));
											res=false;
										}
									}
								}else{
									Utility.showMessage(context, getString(R.string.para_este_evento_es_necesario_selecionar_una_fecha_y_hora));
								}
							}else{
								Utility.showMessage(context, getString(R.string.para_este_evento_es_necesario_selecionar_una_hora));
							}
						}else{
							if(observaciones_input.getText().toString().length()>50){
								int tam=0;
								SparseBooleanArray checkedItems = listFacturas.getCheckedItemPositions();
								if (checkedItems != null) {
									tam=checkedItems.size();
									if(tam != 0){
										res=DBAdapter.addEvento(visita_id,cursor.getString(0),observaciones_input.getText().toString(),null,null,null,0,0,true,-1,horaIni);
										for (int i=0; i<tam; i++) {
											if (checkedItems.valueAt(i) && cursor_facturas.moveToPosition(checkedItems.keyAt(i)) && res) {
												res=DBAdapter.addEventoFactura(visita_id, cursor.getString(0), cursor_facturas.getString(6), cursor_facturas.getString(0), "C");
											}
										}
										if(res){
											cleanEvento();
										}else{
											Utility.showMessage(context, getString(R.string.hubo_un_error_al_guardar_el_evento));
										}
									}else{
										Utility.showMessage(context, getString(R.string.seleccione_al_menos_un_elemento));
										res=false;
									}
								}
							}else{
								Utility.showMessage(context, getString(R.string.debe_escribir_50_caracteres));
							}
						}
						reloadList();
					}else{
						Utility.showMessage(context, getString(R.string.debe_escribir_50_caracteres));
					}
				}else{
					if(cursor.getString(3)!=null && cursor.getString(3).equalsIgnoreCase("s")){
						if(observaciones_input.getText().toString().length()>50){
							if(horaSeleccionada){
								if(fecha !=null 
									&& fecha!=0 && cursor_tipo_horario.moveToPosition(tipo_tiempo_input.getSelectedItemPosition())
									&& cantidad_tiempo_input.getText().toString()!=null
									&& !cantidad_tiempo_input.getText().toString().trim().equalsIgnoreCase("")
								){
									String timeType=cursor_tipo_horario.getString(0);
									String timeCuantity=cantidad_tiempo_input.getText().toString();
									addToCalendar(fecha_seleccionada,timeType,timeCuantity);
									res=DBAdapter.addEvento(visita_id,cursor.getString(0),observaciones_input.getText().toString(),"DATETIME("+(fecha_seleccionada/1000)+", 'unixepoch','localtime')",timeCuantity,timeType,0,0,false,-1,horaIni);
									if(res){
										cleanEvento();
									}else{
										Utility.showMessage(context, getString(R.string.hubo_un_error_al_guardar_el_evento));
									}
								}else{
									Utility.showMessage(context, getString(R.string.para_este_evento_es_necesario_selecionar_una_fecha_y_hora));
								}
							}else{
								Utility.showMessage(context, getString(R.string.para_este_evento_es_necesario_selecionar_una_hora));
							}
						}else{
							Utility.showMessage(context, getString(R.string.debe_escribir_50_caracteres));
						}
					}else{
						if(observaciones_input.getText().toString().length()>50){
							res=DBAdapter.addEvento(visita_id,cursor.getString(0),observaciones_input.getText().toString(),null,null,null,0,0,false,-1,horaIni);
							if(res){
								cleanEvento();
							}else{
								Utility.showMessage(context, getString(R.string.hubo_un_error_al_guardar_el_evento));
							}
						}else{
							Utility.showMessage(context, getString(R.string.debe_escribir_50_caracteres));
						}
					}
				}
			}else{
				Utility.showMessage(context, getString(R.string.ya_existe_un_evento_de_este_tipo));
			}
		}
		return res;
	}

	@SuppressLint("NewApi")
	private void addToCalendar(long date,String timeType,String timeCuantity){
		DBAdapter.addExtraRutaFromCalendar((date/1000), cliente_id);
		try{
			long timeTotal;
			long timeToSus=0;
			int tType=Integer.parseInt(timeType);
			switch(tType){
				case 0:
					timeToSus=(Integer.parseInt(timeCuantity))*1000*60;
					break;
				case 1:
					timeToSus=(Integer.parseInt(timeCuantity))*1000*60*60;
					break;
				case 2:
					timeToSus=(Integer.parseInt(timeCuantity))*1000*60*60*24;
					break;
			}
			timeTotal=date-timeToSus;

			Uri EVENTS_URI = Uri.parse(getCalendarUriBase(this) + "events");
			ContentResolver cr = getContentResolver();

			// event insert
			ContentValues values = new ContentValues();
			values.put("calendar_id", 1);
			values.put("title", evento_input.getSelectedItem().toString()+" "+cliente_id+" "+cliente_nombre );
			values.put("allDay", 0);
			values.put("dtstart", timeTotal ); // event starts at 11 minutes from now
			values.put("dtend", timeTotal + 30*60*1000); // ends 60 minutes from now
			values.put("description", observaciones_input.getText().toString());
			values.put("eventTimezone", TimeZone.getDefault().getID());
			values.put("hasAlarm", 1);
			Uri event = cr.insert(EVENTS_URI, values);

			// reminder insert
			Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(this) + "reminders");
			values = new ContentValues();
			values.put( "event_id", Long.parseLong(event.getLastPathSegment()));
			values.put( "method", 1 );
			values.put( "minutes", 10 );
			cr.insert( REMINDERS_URI, values );
		}catch(Exception e){
			Log.e("info","error addToCalendar " +e);
		}
		return;
	}
	
	@SuppressWarnings("deprecation")
	private String getCalendarUriBase(Activity act) {

	    String calendarUriBase = null;
	    Uri calendars = Uri.parse("content://calendar/calendars");
	    Cursor managedCursor = null;
	    try {
	        managedCursor = act.managedQuery(calendars, null, null, null, null);
	    } catch (Exception e) {
	    }
	    if (managedCursor != null) {
	        calendarUriBase = "content://calendar/";
	    } else {
	        calendars = Uri.parse("content://com.android.calendar/calendars");
	        try {
	            managedCursor = act.managedQuery(calendars, null, null, null, null);
	        } catch (Exception e) {
	        }
	        if (managedCursor != null) {
	            calendarUriBase = "content://com.android.calendar/";
	        }
	    }
	    return calendarUriBase;
	}
	
	//Funcion de carga de la listas
	public void loadList(){
		cursor_lista=DBAdapter.getEventosXVisita(visita_id);
		loadDatalist();
		adapter=new EventosArrayAdapter(getApplicationContext(), R.layout.item_registro_de_eventos_visitas, eventos);
		listRegistoVisitas.setAdapter(adapter);
		registerForContextMenu(listRegistoVisitas);
		return;
	}
	
	public void loadDatalist(){
		if(cursor_lista.moveToFirst()){
			do{
				eventos.add(new Evento(cursor_lista.getString(0),
						cursor_lista.getString(1),
						cursor_lista.getString(2),
						cursor_lista.getString(3),
						cursor_lista.getString(5),
						cursor_lista.getString(6),
						cursor_lista.getString(7),
						cursor_lista.getString(8)));				
			}while(cursor_lista.moveToNext());
		}
		return;
	}
	
	public void reloadList(){
		eventos.clear();
		cursor_lista=DBAdapter.getEventosXVisita(visita_id);
        loadDatalist();
		adapter.notifyDataSetChanged();
		return;
	}
	
	//Load facturas
	private void loadFacturas(ListView list, String cliente_id) {
		cursor_facturas = DBAdapter.getFacturasXCliente(cliente_id);
		loadDetalleVisitaData();
		adapterFacturas = new RE_facturaArrayAdapter(getApplicationContext(),R.layout.item_radicar_factura, facturas);
		list.setAdapter(adapterFacturas);
//		adapterFacturas.getItem(0)
		return;
	}
	
	private void loadDetalleVisitaData() {
		facturas.clear();
		if(cursor_facturas.moveToFirst()){
			do{
				facturas.add(new RE_factura(
						cursor_facturas.getString(0),
						cursor_facturas.getString(1),
						String.valueOf(cursor_facturas.getInt(2)), 
						cursor_facturas.getInt(3),
						cursor_facturas.getString(4),
						cursor_facturas.getString(5),
						cursor_facturas.getString(6))
				);
			}while(cursor_facturas.moveToNext());
		}
		return;
	}
	
	private void cleanEvento(){
		observaciones_input.setText("");
		fecha_input.setText("");
		hora_input.setText("");
		cantidad_tiempo_input.setText("");
		horaSeleccionada=false;
		evento_input.setSelection(0);
		if(facturas!=null){
			facturas.clear();
			if(adapterFacturas!=null){
				adapterFacturas.notifyDataSetChanged();
			}
		}
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.modificar);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.eliminar);
	}

	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case 0:
				auxEvento=eventos.get(info.position);
				String implicaVisita=(auxEvento.getImplicaVisita() !=null )?eventos.get(info.position).getImplicaVisita():"N";
				String implicaFact=(auxEvento.getImplicaFact() !=null )? eventos.get(info.position).getImplicaFact():"N";
				if(implicaVisita.equalsIgnoreCase("N") && implicaFact.equalsIgnoreCase("N")){
					alertView=getLayoutInflater().inflate(R.layout.eventos_modificar_simple, null);		
	   				dialogBuilder = new AlertDialog.Builder(context);
					dialogBuilder.setTitle(R.string.modificar);
					dialogBuilder.setView(alertView);
					dialogBuilder.setCancelable(false);
					observaciones=(EditText)alertView.findViewById(R.id.observaciones_input);
					observaciones.setText(auxEvento.getObs());
					dialogBuilder.setPositiveButton(R.string.guardar,new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {}
					});
					dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {}
					});
					alertDialog=dialogBuilder.create();
					alertDialog.show();
					final Button createButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
					createButton.setOnClickListener(new EditarEventoSimpleButton(alertDialog));
				}else if(implicaVisita.equalsIgnoreCase("S") && implicaFact.equalsIgnoreCase("N")){
					Date date;
					fechaMSalvar=(long) 0;
					horaMSalvar=(long) 0;
					alertView = getLayoutInflater().inflate(R.layout.evento_modificar_con_fecha, null);
					dialogBuilder = new AlertDialog.Builder(context);
					dialogBuilder.setTitle(R.string.modificar);
					dialogBuilder.setView(alertView);
					dialogBuilder.setCancelable(false);
					final TextView fecha_mod_input=(EditText)alertView.findViewById(R.id.fecha_input);
					final TextView hora_mod_input=(EditText)alertView.findViewById(R.id.hora_input);
					final Spinner tipo_tiempo_mod_input=(Spinner)alertView.findViewById(R.id.tipo_tiempo_input);
					final EditText cantidad_tiempo_mod_input=(EditText)alertView.findViewById(R.id.cantidad_tiempo_input);
					final EditText obs_input=(EditText)alertView.findViewById(R.id.observaciones_input);
					loadTipoHorariosMod(tipo_tiempo_mod_input);
					cursor_modficiar=DBAdapter.getEventoDataFechaMod(auxEvento.getTipoEventoId(), visita_id);
					if(cursor_modficiar.moveToFirst()){
						fecha_mod_input.setText(cursor_modficiar.getString(0));
						hora_mod_input.setText(cursor_modficiar.getString(1)+":"+cursor_modficiar.getString(2));
						cantidad_tiempo_mod_input.setText(cursor_modficiar.getString(3));
						tipo_tiempo_mod_input.setSelection(cursor_modficiar.getInt(4));
						obs_input.setText(cursor_modficiar.getString(5));
						horaMSalvar=(long)(cursor_modficiar.getInt(1)*3600000)+(cursor_modficiar.getInt(2)*60000);
						try {
							date = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH).parse(cursor_modficiar.getString(0));
							fechaMSalvar=date.getTime();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					fecha_mod_input.setInputType(EditorInfo.TYPE_NULL);
					fecha_mod_input.setOnClickListener(new View.OnClickListener() {
						@SuppressLint("InflateParams") @Override
						public void onClick(View v) {
							alertView = getLayoutInflater().inflate(R.layout.date_picker, null);
							dialogBuilder = new AlertDialog.Builder(context);
							dialogBuilder.setTitle(R.string.fecha);
							dialogBuilder.setView(alertView);
							dialogBuilder.setCancelable(false);
							date_picker=(CalendarView)alertView.findViewById(R.id.date_picker);
							dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									if(calendar.getTimeInMillis() < date_picker.getDate()){
										fecha_mod_input.setText(dateFormat2.format(date_picker.getDate()));
										fechaMSalvar=date_picker.getDate();
									}else{
										Utility.showMessage(context, getString(R.string.fecha_mayor_hoy));
									}
								}
							});
							dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {}
							});
							dialogBuilder.create().show();
						} 
			    	});
			    	
					hora_mod_input.setInputType(EditorInfo.TYPE_NULL);
					hora_mod_input.setOnClickListener(new View.OnClickListener() {
						@SuppressLint("InflateParams") @Override
						public void onClick(View v) {
							alertView = getLayoutInflater().inflate(R.layout.time_picker, null);
							dialogBuilder = new AlertDialog.Builder(context);
							dialogBuilder.setTitle(R.string.hora);
							dialogBuilder.setView(alertView);
							dialogBuilder.setCancelable(false);
							timePicker=(TimePicker)alertView.findViewById(R.id.timePicker);
							timePicker.setIs24HourView(true);
							dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									hora_mod_input.setText(timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute());
									horaMSalvar=(long)(timePicker.getCurrentHour()*3600000)+(timePicker.getCurrentMinute()*60000);
								}
							});
							dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {}
							});
							dialogBuilder.create().show();
						}
			    	});
					dialogBuilder.setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					});
					dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					});
					alertDialog=dialogBuilder.create();
					alertDialog.show();
					final Button createButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
					createButton.setOnClickListener(new EditarEventoFechaeButton(alertDialog,tipo_tiempo_mod_input,cantidad_tiempo_mod_input,obs_input));
				}else if(implicaVisita.equalsIgnoreCase("N") && implicaFact.equalsIgnoreCase("S")){
					alertView=getLayoutInflater().inflate(R.layout.eventos_radicar_facturas, null);		
	   				dialogBuilder = new AlertDialog.Builder(context);
					dialogBuilder.setTitle(R.string.modificar);
					dialogBuilder.setView(alertView);
					dialogBuilder.setCancelable(false);
					final EditText obs_mod_input=(EditText)alertView.findViewById(R.id.observaciones_input);
					final ListView listModFacturas=(ListView)alertView.findViewById(R.id.listFacturas);
					obs_mod_input.setText(auxEvento.getObs());
					loadFacturas(listModFacturas,cliente_id);
					dialogBuilder.setPositiveButton(R.string.guardar,new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {}
					});
					dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {}
					});
					alertDialog=dialogBuilder.create();
					alertDialog.show();
					final Button createButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
					createButton.setOnClickListener(new EditarEventoFacturaButton(alertDialog,listModFacturas,obs_mod_input));
					
				}else{
					Date date;
					fechaMSalvar=(long) 0;
					horaMSalvar=(long) 0;

					final View alertView=getLayoutInflater().inflate(R.layout.eventos_radicar_facturas_fecha, null);		
	   				final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
					dialogBuilder.setTitle(R.string.modificar);
					dialogBuilder.setView(alertView);
					dialogBuilder.setCancelable(false);
					
					final TextView fecha_mod_input=(TextView)alertView.findViewById(R.id.fecha_input);
					final TextView hora_mod_input=(TextView)alertView.findViewById(R.id.hora_input);
					final Spinner tipo_tiempo_mod_input=(Spinner)alertView.findViewById(R.id.tipo_tiempo_input);
					final EditText cantidad_tiempo_mod_input=(EditText)alertView.findViewById(R.id.cantidad_tiempo_input);
					final EditText obs_mod_input=(EditText)alertView.findViewById(R.id.observaciones_input);
					final ListView listModFacturas=(ListView)alertView.findViewById(R.id.listFacturas);

					loadTipoHorariosMod(tipo_tiempo_mod_input);
					cursor_modficiar=DBAdapter.getEventoDataFechaMod(auxEvento.getTipoEventoId(), visita_id);
					if(cursor_modficiar.moveToFirst()){
						fecha_mod_input.setText(cursor_modficiar.getString(0));
						hora_mod_input.setText(cursor_modficiar.getString(1)+":"+cursor_modficiar.getString(2));
						cantidad_tiempo_mod_input.setText(cursor_modficiar.getString(3));
						tipo_tiempo_mod_input.setSelection(cursor_modficiar.getInt(4));
						horaMSalvar=(long)(cursor_modficiar.getInt(1)*3600000)+(cursor_modficiar.getInt(2)*60000);
						Log.i("info","horaMSalvar "+horaMSalvar);
						try {
							date = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH).parse(cursor_modficiar.getString(0));
							fechaMSalvar=date.getTime();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					fecha_mod_input.setInputType(EditorInfo.TYPE_NULL);
					fecha_mod_input.setOnClickListener(new View.OnClickListener() {
						@SuppressLint("InflateParams") @Override
						public void onClick(View v) {
							final View alertView = getLayoutInflater().inflate(R.layout.date_picker, null);
							final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
							dialogBuilder.setTitle(R.string.fecha);
							dialogBuilder.setView(alertView);
							dialogBuilder.setCancelable(false);
							date_picker=(CalendarView)alertView.findViewById(R.id.date_picker);
							dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									if(calendar.getTimeInMillis() < date_picker.getDate()){
										fecha_mod_input.setText(dateFormat2.format(date_picker.getDate()));
										fechaMSalvar=date_picker.getDate();
									}else{
										Utility.showMessage(context, getString(R.string.fecha_mayor_hoy));
									}
								}
							});
							dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {}
							});
							dialogBuilder.create().show();
						} 
			    	});
			    	
					hora_mod_input.setInputType(EditorInfo.TYPE_NULL);
					hora_mod_input.setOnClickListener(new View.OnClickListener() {
						@SuppressLint("InflateParams") @Override
						public void onClick(View v) {
							final View alertView = getLayoutInflater().inflate(R.layout.time_picker, null);
							final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
							dialogBuilder.setTitle(R.string.hora);
							dialogBuilder.setView(alertView);
							dialogBuilder.setCancelable(false);
							timePicker=(TimePicker)alertView.findViewById(R.id.timePicker);
							timePicker.setIs24HourView(true);
							dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									hora_mod_input.setText(timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute());
									horaMSalvar=(long)(timePicker.getCurrentHour()*3600000)+(timePicker.getCurrentMinute()*60000);
								}
							});
							dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {}
							});
							dialogBuilder.create().show();
						}
			    	});
					
					obs_mod_input.setText(auxEvento.getObs());
					loadFacturas(listModFacturas,cliente_id);
					dialogBuilder.setPositiveButton(R.string.guardar,new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {}
					});
					dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {}
					});
					final AlertDialog alertDialog=dialogBuilder.create();
					alertDialog.show();
					final Button createButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
					createButton.setOnClickListener(new EditarEventoFacturaFechaButton(
							alertDialog,
							listModFacturas,
							tipo_tiempo_mod_input,
							cantidad_tiempo_mod_input,
							obs_mod_input));
				}
				return true;
			case 1:
				dialogBuilder = new AlertDialog.Builder(this);
	    		dialogBuilder.setTitle(R.string.alerta);
	    		dialogBuilder.setMessage(R.string.esta_seguro_que_desea_borrar_este_evento);
				dialogBuilder.setCancelable(false);
	    		dialogBuilder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
					@Override
	    			public void onClick(DialogInterface dialog, int which) {
						if(DBAdapter.deleteEvento(eventos.get(info.position).getId(), visita_id)){
							DBAdapter.deleteEventoFactura(visita_id, eventos.get(info.position).getTipoEventoId());
							reloadList();
						}else{
							Utility.showMessage(context, R.string.hubo_un_error_al_eliminar_el_evento);
						}
					}
	    		});
	    		dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {}
				});
	    		dialogBuilder.create().show();
	    		return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
	
	private boolean eventExists(String tipo_evento_id){
		for(Evento aux :eventos){
			if(aux.getTipoEventoId().equalsIgnoreCase(tipo_evento_id)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {}
	
	
	private class EditarEventoSimpleButton implements View.OnClickListener {
		private final Dialog dialog;

		public EditarEventoSimpleButton(Dialog dialog) {
	        this.dialog = dialog;
	    }
	    @Override
	    public void onClick(View v) {
	    	if(observaciones.getText().toString().length()>50){
		    	DBAdapter.beginTransaction();
				if(DBAdapter.updateEventoSimple(auxEvento.getId(), visita_id, observaciones.getText().toString())){
					reloadList();
					DBAdapter.setTransactionSuccessful();
					dialog.dismiss();
				}else{
					Utility.showMessage(context, getString(R.string.hubo_un_error_al_modificar_el_evento));
					dialog.dismiss();
				}
		    	DBAdapter.endTransaction();
	    	}else{
				Utility.showMessage(context, getString(R.string.debe_escribir_50_caracteres));
	    	}
	    }			
	}
	
	private class EditarEventoFechaeButton implements View.OnClickListener {
		private final Dialog dialog;
		private final EditText cantidad;
		private final EditText obs;
		private Spinner tipo;

		public EditarEventoFechaeButton(
			Dialog dialog,
			Spinner tipo,
			EditText cantidad,
			EditText obs) {
	        this.dialog = dialog;
	        this.tipo = tipo;
	        this.cantidad = cantidad;
	        this.obs = obs;
	    }

		@Override
	    public void onClick(View v) {
			String typeTime="";
			long fecha_seleccionada=fechaMSalvar+horaMSalvar;
			if(cursor_tipo_horario.moveToPosition(tipo.getSelectedItemPosition())){
				typeTime=cursor_tipo_horario.getString(0);
			}
			
	    	if(obs.getText().toString().length()>50){
	    		if(cantidad.getText().length()>0){
	    			if(fechaMSalvar!=0){
	    				if(horaMSalvar!=0){
							DBAdapter.beginTransaction();
			    			if(DBAdapter.updateEventoFecha(auxEvento.getId(),visita_id,"DATETIME("+(fecha_seleccionada/1000)+", 'unixepoch','localtime')",
				    				cantidad.getText().toString(),
				    				typeTime,
				    				obs.getText().toString())){
			    				addToCalendar(fecha_seleccionada,typeTime,cantidad.getText().toString());
								DBAdapter.setTransactionSuccessful();
								reloadList();
								dialog.dismiss();
				    		}else{
								Utility.showMessage(context, R.string.hubo_un_error_al_modificar_el_evento);
				    		}
							DBAdapter.endTransaction();
	    				}else{
	    					Utility.showMessage(context, R.string.para_este_evento_es_necesario_selecionar_una_hora);
	    				}
	    			}else{
						Utility.showMessage(context, R.string.para_este_evento_es_necesario_selecionar_una_fecha_y_hora);
	    			}
	    		}else{
					Utility.showMessage(context, R.string.para_este_evento_es_necesario_selecionar_una_fecha_y_hora);
					cantidad.setFocusable(true);
	    		}
	    	}else{
				Utility.showMessage(context, R.string.debe_escribir_50_caracteres);
	    	}
	    }			
	}

	private class EditarEventoFacturaButton implements View.OnClickListener {
		private final Dialog dialog;
		private final ListView listFacts;
		private final EditText obs;

		public EditarEventoFacturaButton(
			Dialog dialog,
			ListView listFacts,
			EditText obs) {
	        this.dialog = dialog;
	        this.listFacts = listFacts;
	        this.obs = obs;
	    }

		@Override
	    public void onClick(View v) {
			if(obs.getText().toString().length()>50){
				int tam=0;
				SparseBooleanArray checkedItems = listFacts.getCheckedItemPositions();
				if (checkedItems != null) {
					tam=checkedItems.size();
					if(tam != 0){
				    	DBAdapter.beginTransaction();
						if(DBAdapter.updateEventoSimple(auxEvento.getId(), visita_id, obs.getText().toString())){
							DBAdapter.deleteEventoFactura(visita_id, auxEvento.getId());
							for (int i=0; i<tam; i++) {
								if (checkedItems.valueAt(i) && cursor_facturas.moveToPosition(checkedItems.keyAt(i)) && res) {
									res=DBAdapter.addEventoFactura(visita_id, cursor.getString(0), cursor_facturas.getString(6), cursor_facturas.getString(0), "C");
								}
							}
							reloadList();
							dialog.dismiss();
							cleanEvento();
						}
				    	DBAdapter.endTransaction();
					}else{
						Utility.showMessage(context, R.string.seleccione_al_menos_un_elemento);
						res=false;
					}
				}
			}else{
				Utility.showMessage(context, R.string.debe_escribir_50_caracteres);
			}
	    }			
	}
	
	private class EditarEventoFacturaFechaButton implements View.OnClickListener {
		private final Dialog dialog;
		private final ListView listFacts;
		private final EditText obs;
		private final EditText cantidad;
		private Spinner tipo;

		public EditarEventoFacturaFechaButton(
			Dialog dialog,
			ListView listFacts,
			Spinner tipo,
			EditText cantidad,
			EditText obs) {
	        this.dialog = dialog;
	        this.tipo = tipo;
	        this.cantidad = cantidad;
	        this.listFacts = listFacts;
	        this.obs = obs;
	    }

		@Override
	    public void onClick(View v) {
			Log.i("info","horaMSalvar "+horaMSalvar);
			String typeTime="";
			long fecha_seleccionada=fechaMSalvar+horaMSalvar;
			if(cursor_tipo_horario.moveToPosition(tipo.getSelectedItemPosition())){
				typeTime=cursor_tipo_horario.getString(0);
			}
			
	    	if(obs.getText().toString().length()>50){
	    		if(cantidad.getText().length()>0){
	    			if(fechaMSalvar!=0){
	    				if(horaMSalvar!=0){
	    					int tam=0;
	    					SparseBooleanArray checkedItems = listFacts.getCheckedItemPositions();
	    					if (checkedItems != null) {
	    						tam=checkedItems.size();
	    						if(tam != 0){
	    							DBAdapter.beginTransaction();
	    			    			if(DBAdapter.updateEventoFecha(auxEvento.getId(),visita_id,"DATETIME("+(fecha_seleccionada/1000)+", 'unixepoch','localtime')",
	    			    					cantidad.getText().toString(),
	    				    				typeTime,
	    				    				obs.getText().toString())){
	    								DBAdapter.deleteEventoFactura(visita_id, auxEvento.getId());
	    								for (int i=0; i<tam; i++) {
	    									if (checkedItems.valueAt(i) && cursor_facturas.moveToPosition(checkedItems.keyAt(i)) && res) {
	    										res=DBAdapter.addEventoFactura(visita_id, cursor.getString(0), cursor_facturas.getString(6), cursor_facturas.getString(0), "C");
	    									}
	    								}
	    								reloadList();
	    								dialog.dismiss();
	    								cleanEvento();
	    			    				addToCalendar(fecha_seleccionada,typeTime,cantidad.getText().toString());
	    			    				DBAdapter.setTransactionSuccessful();
	    								dialog.dismiss();
	    				    		}else{
	    								Utility.showMessage(context, R.string.hubo_un_error_al_modificar_el_evento);
	    				    		}
	    			    			DBAdapter.endTransaction();
	    						}else{
	    							DBAdapter.beginTransaction();
	    			    			if(DBAdapter.updateEventoFecha(auxEvento.getId(),visita_id,"DATETIME("+(fecha_seleccionada/1000)+", 'unixepoch','localtime')",
	    			    					cantidad.getText().toString(),
	    				    				typeTime,
	    				    				obs.getText().toString())){
	    								reloadList();
	    								dialog.dismiss();
	    								cleanEvento();
	    			    				addToCalendar(fecha_seleccionada,typeTime,cantidad.getText().toString());
	    			    				DBAdapter.setTransactionSuccessful();
	    								dialog.dismiss();
	    				    		}else{
	    								Utility.showMessage(context, R.string.hubo_un_error_al_modificar_el_evento);
	    				    		}
	    			    			DBAdapter.endTransaction();			
	    						}
	    					}
	    				}else{
	    					Utility.showMessage(context, R.string.para_este_evento_es_necesario_selecionar_una_hora);
	    				}
	    			}else{
						Utility.showMessage(context, R.string.para_este_evento_es_necesario_selecionar_una_fecha_y_hora);
	    			}
	    		}else{
					Utility.showMessage(context, R.string.para_este_evento_es_necesario_selecionar_una_fecha_y_hora);
					cantidad.setFocusable(true);
	    		}
	    	}else{
				Utility.showMessage(context, R.string.debe_escribir_50_caracteres);
	    	}
	    }			
	}
	
	@Override
	public void onClick(View v) {}
	
	@Override
	protected void onResume(){
	   super.onResume();
	   reloadList();
	}
	
	@Override
	protected void onPause(){
	   super.onResume();
	}
}