package actividad_diaria;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import utilidades.EasyUtilidades;
import utilidades.Utility;
import visita.Visita;

import cliente.Cliente_Consultas_Otros_Eventos;

import com.italo_view.R;

import easygps.EasyGps;
import easysync.EasySync;

import bd_utilidades.ItaloDBAdapter;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class Actividad_Diaria_Consultar extends Activity  {
	static public String Lock = "dblock";
	private AlertDialog alertDialog;
	private ListView listActividadDiaria;
	static private RutaRowArrayAdapter adapter;
	static private ArrayList<RutaRow> rutas;
	private ItaloDBAdapter DBAdapter;
	private Cursor cursor;
	private Cursor cursorTemp;
	private Cursor cursor_head;
	private Cursor cursor_no_visita;
	private Cursor cursor_plan;
	private Bundle extras;
	private View alertView;
	private Context context;
	private AlertDialog.Builder dialogBuilder;
	private Intent i;
	private TextView sector_input;
	private TextView ruta_input;
	private TextView n_plan_input;
	private TextView fecha_input;
	private TextView n_extaruta_input;
	private TextView n_clientes_input;
	private TextView venta_proy_input_0;
	private TextView venta_proy_input_1;
	private TextView venta_real_input_0;
	private TextView venta_real_input_1;
	private TextView cobro_proy_input_0;
	private TextView cobro_proy_input_1;
	private TextView cobro_real_input_0;
	private TextView cobro_real_input_1;
	private TextView observaciones;
	private TextView lunes;
	private TextView martes;
	private TextView miercoles;
	private TextView jueves;
	private TextView viernes;
	private TextView sabado;
	static private String n_plan;
	static private String FLunes;
	static private String FMartes;
	static private String FMiercoles;
	static private String FJueves;
	static private String FViernes;
	static private String FSabado;
	static private String todayIs;
	static private int isToday;
	private Spinner evento_input;
	private EditText n_clientes_nuevos_hoy_input;
	private EditText ventas_clientes_nuevos_hoy_input;
	private EditText accion_de_la_competencia_input;
	private EditText observaciones_input;
	static private double latitud = 0;
	static private double longitud = 0;
	//static private LocationManager lm;
	//static private LocationListener ll;
	static private SimpleDateFormat dateFormat;
	static private int no_visitados = 0;
	static private int num_clientes_con_pedido = 0;
	static private int clientes_no_venta = 0;
	static private float total_pedido = 0;
	static private RutaRow auxContextMenu;
	static private boolean isReopen = false;
	static private String fechaPlan ="";

	
	private Handler mHandlerEasyGps = new Handler();

	EasyGps easyGps;
	int numeroIntentos = 0;
	Location ubicacion;

	int intervaloTiempo = 300;
	int timeOutGPS = 120000;
	int tiempoIntentos = 0;
	
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actividad_diaria);
		init();
		loadHeaderData();
		setDaysDates();
		lunes.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				cursor_plan = DBAdapter.getNPlanXFecha(FLunes);
				if (cursor_plan.moveToFirst()) {
					i = new Intent(getApplicationContext(),Actividad_Diaria_Consultar.class);
					i.putExtra("n_plan", cursor_plan.getString(0));
					if (Integer.parseInt(FLunes.replaceAll("-", "")) < Integer.parseInt(todayIs.replaceAll("-", ""))) {
						i.putExtra("isToday", -1);
					} else if (Integer.parseInt(FLunes.replaceAll("-", "")) == Integer.parseInt(todayIs.replaceAll("-", ""))) {
						i.putExtra("isToday", 0);
					} else {
						i.putExtra("isToday", 1);
					}
					startActivity(i);
					finish();
				} else {
					Utility.showMessage(context,R.string.no_existe_plan_fecha);
				}
				return false;
			}
		});
		martes.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				cursor_plan = DBAdapter.getNPlanXFecha(FMartes);
				if (cursor_plan.moveToFirst()) {
					i = new Intent(getApplicationContext(),Actividad_Diaria_Consultar.class);
					i.putExtra("n_plan", cursor_plan.getString(0));
					if (Integer.parseInt(FMartes.replaceAll("-", "")) < Integer.parseInt(todayIs.replaceAll("-", ""))) {
						i.putExtra("isToday", -1);
					} else if (Integer.parseInt(FMartes.replaceAll("-", "")) == Integer.parseInt(todayIs.replaceAll("-", ""))) {
						i.putExtra("isToday", 0);
					} else {
						i.putExtra("isToday", 1);
					}
					startActivity(i);
					finish();
				} else {
					Utility.showMessage(context,R.string.no_existe_plan_fecha);
				}
				return false;
			}
		});
		miercoles.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				cursor_plan = DBAdapter.getNPlanXFecha(FMiercoles);
				if (cursor_plan.moveToFirst()) {
					i = new Intent(getApplicationContext(),Actividad_Diaria_Consultar.class);
					i.putExtra("n_plan", cursor_plan.getString(0));
					if (Integer.parseInt(FMiercoles.replaceAll("-", "")) < Integer.parseInt(todayIs.replaceAll("-", ""))) {
						i.putExtra("isToday", -1);
					} else if (Integer.parseInt(FMiercoles.replaceAll("-", "")) == Integer.parseInt(todayIs.replaceAll("-", ""))) {
						i.putExtra("isToday", 0);
					} else {
						i.putExtra("isToday", 1);
					}
					startActivity(i);
					finish();
				} else {
					Utility.showMessage(context,R.string.no_existe_plan_fecha);
				}
				return false;
			}
		});
		jueves.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				cursor_plan = DBAdapter.getNPlanXFecha(FJueves);
				if (cursor_plan.moveToFirst()) {
					i = new Intent(getApplicationContext(),Actividad_Diaria_Consultar.class);
					i.putExtra("n_plan", cursor_plan.getString(0));
					if (Integer.parseInt(FJueves.replaceAll("-", "")) < Integer.parseInt(todayIs.replaceAll("-", ""))) {
						i.putExtra("isToday", -1);
					} else if (Integer.parseInt(FJueves.replaceAll("-", "")) == Integer.parseInt(todayIs.replaceAll("-", ""))) {
						i.putExtra("isToday", 0);
					} else {
						i.putExtra("isToday", 1);
					}
					startActivity(i);
					finish();
				} else {
					Utility.showMessage(context,R.string.no_existe_plan_fecha);
				}
				return false;
			}
		});
		
		viernes.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				cursor_plan = DBAdapter.getNPlanXFecha(FViernes);
				if (cursor_plan.moveToFirst()) {
					i = new Intent(getApplicationContext(),Actividad_Diaria_Consultar.class);
					i.putExtra("n_plan", cursor_plan.getString(0));
					if (Integer.parseInt(FViernes.replaceAll("-", "")) < Integer.parseInt(todayIs.replaceAll("-", ""))) {
						i.putExtra("isToday", -1);
					} else if (Integer.parseInt(FViernes.replaceAll("-", "")) == Integer.parseInt(todayIs.replaceAll("-", ""))) {
						i.putExtra("isToday", 0);
					} else {
						i.putExtra("isToday", 1);
					}
					startActivity(i);
					finish();
				} else {
					Utility.showMessage(context,R.string.no_existe_plan_fecha);
				}
				return false;
			}
		});

		sabado.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				cursor_plan = DBAdapter.getNPlanXFecha(FSabado);
				if (cursor_plan.moveToFirst()) {
					i = new Intent(getApplicationContext(),Actividad_Diaria_Consultar.class);
					i.putExtra("n_plan", cursor_plan.getString(0));
					i.putExtra("n_plan", cursor_plan.getString(0));
					if (Integer.parseInt(FSabado.replaceAll("-", "")) < Integer.parseInt(todayIs.replaceAll("-", ""))) {
						i.putExtra("isToday", -1);
					} else if (Integer.parseInt(FSabado.replaceAll("-", "")) == Integer.parseInt(todayIs.replaceAll("-", ""))) {
						i.putExtra("isToday", 0);
					} else {
						i.putExtra("isToday", 1);
					}
					startActivity(i);
					finish();
				} else {
					Utility.showMessage(context,R.string.no_existe_plan_fecha);
				}
				return false;
			}
		});
	}

	@SuppressLint("SimpleDateFormat")
	private void init() {
		Date now = new Date();
		context = this;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat = new SimpleDateFormat("yyyyMMdd");
		todayIs = dateFormat.format(now);
		extras = getIntent().getExtras();
		n_plan = extras.getString("n_plan");
		isToday = extras.getInt("isToday");
		listActividadDiaria = (ListView) findViewById(R.id.listActividadDiaria);
		sector_input = (TextView) findViewById(R.id.sector_input);
		ruta_input = (TextView) findViewById(R.id.ruta_input);
		n_plan_input = (TextView) findViewById(R.id.n_plan_input);
		fecha_input = (TextView) findViewById(R.id.fecha_input);
		n_extaruta_input = (TextView) findViewById(R.id.n_extaruta_input);
		n_clientes_input = (TextView) findViewById(R.id.n_clientes_input);
		lunes = (TextView) findViewById(R.id.lunes);
		martes = (TextView) findViewById(R.id.martes);
		miercoles = (TextView) findViewById(R.id.miercoles);
		jueves = (TextView) findViewById(R.id.jueves);
		viernes = (TextView) findViewById(R.id.viernes);
		sabado = (TextView) findViewById(R.id.sabado);
		venta_proy_input_0 = (TextView) findViewById(R.id.venta_proy_input_0);
		venta_proy_input_1 = (TextView) findViewById(R.id.venta_proy_input_1);
		venta_real_input_0 = (TextView) findViewById(R.id.venta_real_input_0);
		venta_real_input_1 = (TextView) findViewById(R.id.venta_real_input_1);
		cobro_proy_input_0 = (TextView) findViewById(R.id.cobro_proy_input_0);
		cobro_proy_input_1 = (TextView) findViewById(R.id.cobro_proy_input_1);
		cobro_real_input_0 = (TextView) findViewById(R.id.cobro_real_input_0);
		cobro_real_input_1 = (TextView) findViewById(R.id.cobro_real_input_1);
		DBAdapter = new ItaloDBAdapter(this);
		rutas = new ArrayList<RutaRow>();
		isReopen = false;
		return;
	}
	
 
	
	private void CapturarGPS() {
		easyGps = null;

		latitud = 0;
		longitud = 0;
		
		tiempoIntentos = 0;
		numeroIntentos = 0;
		ubicacion = null;

		easyGps = new EasyGps(this);

		mHandlerEasyGps.removeCallbacks(EntregarInicializarCaptura);
		mHandlerEasyGps.postDelayed(EntregarInicializarCaptura, intervaloTiempo);
	}
	

	private Runnable EntregarInicializarCaptura = new Runnable() {
		public void run() {
			try {
				// ++numeroIntentos;
				tiempoIntentos += intervaloTiempo;
				if (easyGps.mejorLocalizacion != null) {
					ubicacion = easyGps.mejorLocalizacion;
					EntregarCoordenada();
				}

				if (tiempoIntentos > timeOutGPS) {
					DestruirGPS();
				} else {
					mHandlerEasyGps.postDelayed(EntregarInicializarCaptura,
							intervaloTiempo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void EntregarCoordenada() {
		if (ubicacion != null) {
			latitud = ubicacion.getLatitude();
			longitud = ubicacion.getLongitude();
			Calendar _calendar = Calendar.getInstance();
			_calendar.setTimeInMillis(ubicacion.getTime());
		}
	}

	@Override
	public void onPause() {
		super.onDestroy();
		DestruirGPS();
		mHandlerEasyGps.removeCallbacks(EntregarInicializarCaptura);
	}

	private void DestruirGPS() {
		try {
			easyGps.DestroyEasyGps();
		} catch (Exception ex) {}
	}

	private void setDaysDates() {
		String[] aux = fecha_input.getText().toString().split("/");
		try {
			GregorianCalendar c = new GregorianCalendar(Integer.parseInt(aux[2]), Integer.parseInt(aux[0]) - 1,Integer.parseInt(aux[1]));
			changeDay(c.get(Calendar.DAY_OF_WEEK));
			c.add(Calendar.DAY_OF_YEAR,Calendar.MONDAY - c.get(Calendar.DAY_OF_WEEK));
			while (c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
					FLunes = dateFormat.format(c.getTime());
				} else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
					FMartes = dateFormat.format(c.getTime());
				} else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
					FMiercoles = dateFormat.format(c.getTime());
				} else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
					FJueves = dateFormat.format(c.getTime());
				} else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
					FViernes = dateFormat.format(c.getTime());
				} else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
					FSabado = dateFormat.format(c.getTime());
				}
				c.add(Calendar.DAY_OF_YEAR, 1);
			}
		} catch (Exception e) {
			FLunes = "";
			FMartes = "";
			FMiercoles = "";
			FJueves = "";
			FViernes = "";
			FSabado = "";
		}
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actividad_diaria_consultar, menu);
		return true;
	}

	@SuppressLint("InflateParams")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.reopen:
			if (isToday == 0 && DBAdapter.isEjecucionPlanFinish(n_plan)) {
				dialogBuilder = new AlertDialog.Builder(this);
				dialogBuilder.setCancelable(false);
				dialogBuilder.setTitle(R.string.alerta);
				dialogBuilder.setMessage(R.string.actividad_finalizada_desea_abrirla);
				dialogBuilder.setPositiveButton(R.string.si,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int which) {
						isReopen = true;
					}
				});
				dialogBuilder.setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int which) {}
				});
				dialogBuilder.create().show();
			}
			return true;
		case R.id.indicadores:
			/*
			 * i = new Intent(getApplicationContext(), Indicadores_Tab.class);
			 * i.putExtra("de", "actividad_diaria_consultar"); startActivity(i);
			 */
			return true;
		case R.id.atras:
			finish();
			return true;
		case R.id.finalizar_actividad:
			if (isToday == 0) {
				if (!DBAdapter.isEjecucionPlanFinish(n_plan) || isReopen) {
					boolean cen = true;
					no_visitados = 0;
					num_clientes_con_pedido = 0;
					clientes_no_venta = 0;
					total_pedido = 0;
					for (RutaRow aux : rutas) {
						if (aux.getEvento().equalsIgnoreCase("02")) {
							clientes_no_venta++;
						} else if (aux.getEvento().equalsIgnoreCase("03")) {
							if (aux.getVenta_real() != 0) {
								num_clientes_con_pedido++;
							} else {
								clientes_no_venta++;
							}
						} else if (aux.getEvento().equalsIgnoreCase("04")) {
							no_visitados++;
						} else if (Integer.valueOf(aux.getNVisitas())>0){
							clientes_no_venta++;
						}else{
							cen = false;
						}
						total_pedido += aux.getVenta_real();
					}
					if (cen) {
						alertView = getLayoutInflater().inflate(R.layout.actividad_diaria_finalizar, null);
						dialogBuilder = new AlertDialog.Builder(this);
						dialogBuilder.setTitle(R.string.alerta);
						dialogBuilder.setView(alertView);
						dialogBuilder.setCancelable(false);
						n_clientes_nuevos_hoy_input = (EditText) alertView.findViewById(R.id.n_clientes_nuevos_hoy_input);
						ventas_clientes_nuevos_hoy_input = (EditText) alertView.findViewById(R.id.ventas_clientes_nuevos_hoy_input);
						accion_de_la_competencia_input = (EditText) alertView.findViewById(R.id.accion_de_la_competencia_input);
						dialogBuilder.setPositiveButton(R.string.si,new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,	int which) {}
						});
						dialogBuilder.setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,	int which) {}
						});
						alertDialog = dialogBuilder.create();
						alertDialog.show();
						final Button createButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
						createButton.setOnClickListener(new FinalizarVisitaButton(alertDialog));
					} else {
						Utility.showMessage(context,R.string.no_se_puede_finalizar_la_actividad);
					}
				} else {
					Utility.showMessage(context,R.string.ya_se_finalizo_la_actividad_diaria);
				}
			}
			return true;
		case R.id.programar_cobro:
			return true;
		case R.id.ir_a_extrarutas:
			if (isToday >= 0) {
				i = new Intent(getApplicationContext(),
						Actividad_Diaria_Extraruta.class);
				i.putExtra("de", "actividad_diaria_consultar");
				i.putExtra("n_plan", n_plan);
				i.putExtra("isToday", isToday);
				i.putExtra("fecha", cursor_head.getString(3));
				startActivity(i);
			}
			return true;
		case R.id.menu_general:
			setResult(RESULT_OK, null);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class FinalizarVisitaButton implements View.OnClickListener {
		private final Dialog dialog;

		public FinalizarVisitaButton(Dialog dialog) {
			this.dialog = dialog;
		}

		@Override
		public void onClick(View v) {
			if (n_clientes_nuevos_hoy_input.getText().toString().length() > 0) {
				if (ventas_clientes_nuevos_hoy_input.getText().toString().length() > 0) {
					if (DBAdapter.addEjecucionPlan(n_plan, no_visitados,
							num_clientes_con_pedido, clientes_no_venta,
							total_pedido,
							ventas_clientes_nuevos_hoy_input.getText().toString(),
							n_clientes_nuevos_hoy_input.getText().toString(),
							accion_de_la_competencia_input.getText().toString())) {
						EasySync _easySync = new EasySync(context, EasyUtilidades.InformacionGloabaG(new ItaloDBAdapter(context)));
						_easySync.GenerarNovedadesUp();
						// llamado de EasySync
						EasyUtilidades.SolicitarEasySync(context, EasyUtilidades.InformacionGloabaG(new ItaloDBAdapter(context)));
						dialog.dismiss();
						finish();
					} else {
						Utility.showMessage(context,getString(R.string.hubo_un_error_al_finalizar_la_actividad));
					}
				} else {
					Utility.showMessage(context,getString(R.string.debe_ingresar_el_monto_clientes_nuevos));
					ventas_clientes_nuevos_hoy_input.requestFocus();
				}
			} else {
				Utility.showMessage(context,getString(R.string.debe_ingresar_clientes_nuevos));
				n_clientes_nuevos_hoy_input.requestFocus();
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		if (rutas.get(info.position).getExtraRuta() != null && rutas.get(info.position).getExtraRuta().equalsIgnoreCase("N")) {
			if (isToday == 0) {
				menu.add(Menu.NONE, 1, Menu.NONE, R.string.visita);
				menu.add(Menu.NONE, 2, Menu.NONE, R.string.motivo_no_visita);
				menu.add(Menu.NONE, 0, Menu.NONE, R.string.observaciones);
				menu.add(Menu.NONE, 5, Menu.NONE, R.string.detalle_visita);
			} else {
				menu.add(Menu.NONE, 0, Menu.NONE, R.string.observaciones);
				menu.add(Menu.NONE, 5, Menu.NONE, R.string.detalle_visita);
			}
		} else {
			if (isToday == 0) {
				if (rutas.get(info.position).getTipoExtra() != null && rutas.get(info.position).getTipoExtra().equalsIgnoreCase("E")) {
					menu.add(Menu.NONE, 1, Menu.NONE, R.string.visita);
					menu.add(Menu.NONE, 2, Menu.NONE, R.string.motivo_no_visita);
					menu.add(Menu.NONE, 0, Menu.NONE, R.string.observaciones);
					menu.add(Menu.NONE, 4, Menu.NONE, R.string.eliminar);
					menu.add(Menu.NONE, 5, Menu.NONE, R.string.detalle_visita);
				} else {
					menu.add(Menu.NONE, 1, Menu.NONE, R.string.visita);
					menu.add(Menu.NONE, 2, Menu.NONE, R.string.motivo_no_visita);
					menu.add(Menu.NONE, 0, Menu.NONE, R.string.observaciones);
					menu.add(Menu.NONE, 5, Menu.NONE, R.string.detalle_visita);
				}
			} else {
				menu.add(Menu.NONE, 0, Menu.NONE, R.string.observaciones);
				menu.add(Menu.NONE, 5, Menu.NONE, R.string.detalle_visita);
			}
		}
	}

	@SuppressLint("InflateParams")
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		//lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
		auxContextMenu = rutas.get(info.position);
		final String visita_id;
		switch (item.getItemId()) {
		case 0:
			alertView = getLayoutInflater().inflate(R.layout.observaciones_editable, null);
			dialogBuilder = new AlertDialog.Builder(context);
			dialogBuilder.setTitle(R.string.observaciones);
			dialogBuilder.setView(alertView);
			observaciones = (EditText) alertView.findViewById(R.id.observaciones);
			observaciones.setText(auxContextMenu.getObservaciones());
			if ((!DBAdapter.isEjecucionPlanFinish(n_plan) || isReopen) && isToday == 0) {
				dialogBuilder.setPositiveButton(R.string.guardar,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int which) {
						if (DBAdapter.updatePlanClienteObservacion(n_plan, auxContextMenu.getClienteId(),observaciones.getText().toString())) {
							reloadList();
						} else {
							Utility.showMessage(context, getString(R.string.error_al_salvar_la_observacion));
						}
					}
				});
			}
			dialogBuilder.setNegativeButton(R.string.cancelar,new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {}
			});
			dialogBuilder.create().show();
			return true;
		case 1:
//			DBAdapter.beginTransaction();
			if (!DBAdapter.isEjecucionPlanFinish(n_plan) || isReopen) {
				//setCurrentLocation();
				if (!DBAdapter.exitsVisitaTemp()) {
					visita_id = DBAdapter.getVisitaId();
					if (DBAdapter.createNewVisita(visita_id, n_plan,
							auxContextMenu.getClienteId(),
							auxContextMenu.getExtraRuta(),
							auxContextMenu.getTipoExtra(), latitud, longitud,
							auxContextMenu.getVenta_proyectada(),
							auxContextMenu.getVenta_real(),
							auxContextMenu.getCobro_proyectado(),
							auxContextMenu.getCobro_real(), null)
							&& DBAdapter.createNewVisitaTemp(visita_id, n_plan,
									auxContextMenu.getClienteId(),
									auxContextMenu.getExtraRuta(),
									auxContextMenu.getTipoExtra(), latitud,
									longitud,
									auxContextMenu.getVenta_proyectada(),
									auxContextMenu.getVenta_real(),
									auxContextMenu.getCobro_proyectado(),
									auxContextMenu.getCobro_real(), null)) {
//						DBAdapter.setTransactionSuccessful();
						i = new Intent(getApplicationContext(), Visita.class);
						i.putExtra("visita_id", visita_id);
						i.putExtra("cliente_id", auxContextMenu.getClienteId());
						i.putExtra("cliente_nombre",auxContextMenu.getClienteNombre());
						startActivity(i);
					} else {
						Utility.showMessage(context,getString(R.string.no_se_puede_inicializar_visita));
					}
				} else {
					if (!DBAdapter.isVisiaTemporalFromYesterday()) {
						cursorTemp = DBAdapter.getVisitaTempData();
						if (cursorTemp.moveToFirst()) {
							visita_id = cursorTemp.getString(2);
							if (!cursorTemp.getString(0).equalsIgnoreCase(auxContextMenu.getClienteId())) {
								dialogBuilder = new AlertDialog.Builder(context);
								dialogBuilder.setTitle(R.string.alerta);
								dialogBuilder.setMessage(getString(R.string.inciar_visita_temp_1)
									+ " "
									+ auxContextMenu.getClienteId()
									+ " "
									+ getString(R.string.inciar_visita_temp_2)
									+ " "
									+ cursorTemp.getString(0)
									+ ".");
								dialogBuilder.setCancelable(false);
								dialogBuilder.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int which) {
//										DBAdapter.setTransactionSuccessful();
										i = new Intent(getApplicationContext(), Visita.class);
										i.putExtra("visita_id",	visita_id);
										i.putExtra("cliente_id",cursorTemp.getString(0));
										i.putExtra("cliente_nombre",cursorTemp.getString(1));
										startActivity(i);
									}
								});
								dialogBuilder.create().show();
							} else {
//								DBAdapter.setTransactionSuccessful();
								i = new Intent(getApplicationContext(),Visita.class);
								i.putExtra("visita_id", visita_id);
								i.putExtra("cliente_id",cursorTemp.getString(0));
								i.putExtra("cliente_nombre",cursorTemp.getString(1));
								startActivity(i);
							}
						} else {
							Utility.showMessage(context,getString(R.string.error_al_recuperar_la_visita));
						}
					} else {
						Cursor getCompleteDataV = DBAdapter.getVisitaTempDataAllData();
						if (getCompleteDataV.moveToFirst()) {
							if (DBAdapter.systemCloseVisita(
									getCompleteDataV.getString(0),
									getCompleteDataV.getString(1),
									getCompleteDataV.getString(2),
									getCompleteDataV.getString(3), latitud,
									longitud, getCompleteDataV.getDouble(4),
									getCompleteDataV.getDouble(5),
									getCompleteDataV.getDouble(6),
									getCompleteDataV.getDouble(7), "")) {
								if(DBAdapter.deleteVisitaTemporal()){
//									DBAdapter.setTransactionSuccessful();
									Utility.showMessage(context,"La visita del cliente "+ getCompleteDataV.getString(8)+ " fue cerrada por el sistema");
								}
							} else {
								Utility.showMessage(context,getString(R.string.hubo_un_error_al_cerrar_la_visita));
							}
						} else {
							Utility.showMessage(context,getString(R.string.hubo_un_error_al_cerrar_la_visita));
						}
						if (getCompleteDataV != null) {
							getCompleteDataV.close();
						}
					}
				}
			} else {
				Utility.showMessage(context,getString(R.string.debe_reabrir_la_actividad_diaria));
			}
//			DBAdapter.endTransaction();
			return true;
		case 2:
			if (!DBAdapter.isEjecucionPlanFinish(n_plan) || isReopen) {
				alertView = getLayoutInflater().inflate(R.layout.activity_actividad_diaria_consultar_motivo_no_visita,null);
				dialogBuilder = new AlertDialog.Builder(this);
				dialogBuilder.setTitle(R.string.motivo_de_no_visita_al_cliente);
				dialogBuilder.setView(alertView);
				final TextView vendedor_input = (TextView) alertView.findViewById(R.id.vendedor_input);
				final TextView cliente_input = (TextView) alertView.findViewById(R.id.cliente_input);
				final TextView fecha_actual_input = (TextView) alertView.findViewById(R.id.fecha_actual_input);
				evento_input = (Spinner) alertView.findViewById(R.id.evento_input);
				observaciones_input = (EditText) alertView.findViewById(R.id.observaciones_input);
				vendedor_input.setText(DBAdapter.getVendedorString());
				fecha_actual_input.setText(DBAdapter.getActualDate());
				cliente_input.setText(auxContextMenu.getClienteId() + " " + auxContextMenu.getClienteNombre());
				loadEventosNoVisita(evento_input);
				dialogBuilder.setPositiveButton(R.string.guardar,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int which) {}
					});
				dialogBuilder.setNegativeButton(R.string.cancelar,new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {}
				});
				dialogBuilder.setNeutralButton(R.string.menu_general,new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						setResult(RESULT_OK, null);
						finish();
					}
				});
				alertDialog = dialogBuilder.create();
				alertDialog.show();
				final Button createButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
				createButton.setOnClickListener(new NoVisitaButton(alertDialog));
			} else {
				Utility.showMessage(context,getString(R.string.debe_reabrir_la_actividad_diaria));
			}
			return true;
		case 4:
			dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle(R.string.alerta);
			dialogBuilder.setMessage(R.string.esta_seguro_que_desea_eliminar_extraruta);
			dialogBuilder.setPositiveButton(R.string.si,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (auxContextMenu.getTipoExtra().equalsIgnoreCase("E")) {
						DBAdapter.deleteExtrarutaConsulta(auxContextMenu.getClienteId(), n_plan);
						reloadList();
					}
				}
			});
			dialogBuilder.setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {}
			});
			dialogBuilder.create().show();
			return true;
		case 5:
			i = new Intent(getApplicationContext(), Cliente_Consultas_Otros_Eventos.class);
			i.putExtra("cliente_id", auxContextMenu.getClienteId());
			i.putExtra("cliente_nombre", auxContextMenu.getClienteNombre());
			i.putExtra("fecha_plan", fechaPlan);
			startActivity(i);

			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private class NoVisitaButton implements View.OnClickListener {
		private final Dialog dialog;

		public NoVisitaButton(Dialog dialog) {
			this.dialog = dialog;
		}

		@Override
		public void onClick(View v) {
			if (observaciones_input.getText().toString().length() > 50) {
				if (cursor_no_visita.moveToPosition(evento_input
						.getSelectedItemPosition())
						&& DBAdapter.insertNoVisita(n_plan,
								auxContextMenu.getClienteId(),
								auxContextMenu.getExtraRuta(),
								auxContextMenu.getTipoExtra(), 0.0f, 0.0f,
								auxContextMenu.getVenta_proyectada(),
								auxContextMenu.getVenta_real(),
								auxContextMenu.getCobro_proyectado(),
								auxContextMenu.getCobro_real(),
								observaciones_input.getText().toString(),
								cursor_no_visita.getString(0))) {
					reloadList();
					dialog.dismiss();
				} else {

				}
			} else {
				Utility.showMessage(context, getString(R.string.debe_escribir_50_caracteres));
			}
		}
	}

	private void loadHeaderData() {
		cursor_head = DBAdapter.getConsultarInfoRuta(n_plan);
		if (cursor_head.moveToFirst()) {
			sector_input.setText(cursor_head.getString(0));
			ruta_input.setText(cursor_head.getString(1));
			n_plan_input.setText(n_plan);
			fecha_input.setText(cursor_head.getString(3));
			fechaPlan=cursor_head.getString(4);
			loadList();
		}
		return;
	}

	// Carga la lista del rutero

	private void loadList() {
		cursor = DBAdapter.getConsultaPlan(n_plan,fechaPlan);
		loadListData();
		adapter = new RutaRowArrayAdapter(getApplicationContext(),R.layout.item_cartera, rutas);
		listActividadDiaria.setAdapter(adapter);
		registerForContextMenu(listActividadDiaria);
		return;
	}

	private void reloadList() {
		rutas.clear();
		cursor = DBAdapter.getConsultaPlan(n_plan,fechaPlan);
		loadListData();
		adapter.notifyDataSetChanged();
		return;
	}

	private void loadListData() {
		String evento = "";
		int extraruta = 0;
		double ventaProyTotal = 0;
		int ventaProyCant = 0;
		double ventaRealTotal = 0;
		int ventaRealCant = 0;
		double cobroProyTotal = 0;
		int cobroProyCant = 0;
		double cobroRealTotal = 0;
		int cobroRealCant = 0;
		double cobroPorCliente=0;
		if (cursor.moveToFirst()) {
			do {
				evento = (cursor.getString(11) != null) ? cursor.getString(11): "";
				cobroPorCliente=DBAdapter.getCobrosClientesPorFecha(cursor.getString(3), fechaPlan);
				rutas.add(new RutaRow(evento, 
						cursor.getString(1), 
						cursor.getString(2), 
						cursor.getString(3),
						cursor.getString(4), 
						cursor.getDouble(5), 
						cursor.getDouble(12), 
						cursor.getDouble(6),
						cobroPorCliente, 
						cursor.getString(7), 
						cursor.getString(10), 
						cursor.getString(8), 
						cursor.getString(9), 
						cursor.getString(13)));
				if (cursor.getString(8).equalsIgnoreCase("s")) {
					extraruta += 1;
				}
				if (cursor.getDouble(5) != 0) {
					ventaProyCant += 1;
					ventaProyTotal += cursor.getDouble(5);
				}
				if (cursor.getDouble(6) != 0) {
					cobroProyCant += 1;
					cobroProyTotal += cursor.getDouble(6);
				}
				if (cursor.getDouble(12) != 0) {
					ventaRealCant += 1;
					ventaRealTotal += cursor.getDouble(12);
				}
				if (cobroPorCliente!= 0) {
					cobroRealCant += 1;
					cobroRealTotal += cobroPorCliente;
				}
			} while (cursor.moveToNext());
			n_extaruta_input.setText(String.valueOf(extraruta));
			n_clientes_input.setText(String.valueOf(rutas.size()));
			venta_proy_input_0.setText(Utility.formatNumber(ventaProyTotal));
			venta_proy_input_1.setText(String.valueOf(ventaProyCant));
			venta_real_input_0.setText(Utility.formatNumber(ventaRealTotal));
			venta_real_input_1.setText(String.valueOf(ventaRealCant));
			cobro_proy_input_0.setText(Utility.formatNumber(cobroProyTotal));
			cobro_proy_input_1.setText(String.valueOf(cobroProyCant));
			cobro_real_input_0.setText(Utility.formatNumber(cobroRealTotal));
			cobro_real_input_1.setText(String.valueOf(cobroRealCant));
		}
		return;
	}

	protected void onResume() {
		
		CapturarGPS();
		
		Date nowResume = new Date();
		String[] dAux = cursor_head.getString(3).split("/");
		int fAux = Integer.parseInt(dAux[2] + dAux[0] + dAux[1]);
		super.onResume();
		reloadList();

		if (fAux < Integer.parseInt(dateFormat.format(nowResume).replaceAll(
				"-", ""))) {
			isToday = -1;
		} else if (fAux == Integer.parseInt(dateFormat.format(nowResume)
				.replaceAll("-", ""))) {
			isToday = 0;
		} else {
			isToday = 1;
		}
	}

	private void loadEventosNoVisita(Spinner evento_input) {
		int i = 0;
		String[] noVisita = null;
		cursor_no_visita = DBAdapter.getEventosNoVisita();
		if (cursor_no_visita.moveToFirst()) {
			noVisita = new String[cursor_no_visita.getCount()];
			do {
				noVisita[i] = cursor_no_visita.getString(1);
				i++;
			} while (cursor_no_visita.moveToNext());
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, noVisita);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			evento_input.setAdapter(adapter);
		}
		return;
	}


	@SuppressWarnings("deprecation")
	private boolean changeDay(int dayToChange) {
		switch (dayToChange) {
		case 2:
			lunes.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.input_azul));
			lunes.setTextColor(Color.WHITE);
			break;
		case 3:
			martes.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.input_azul));
			martes.setTextColor(Color.WHITE);
			break;
		case 4:
			miercoles.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.input_azul));
			miercoles.setTextColor(Color.WHITE);
			break;
		case 5:
			jueves.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.input_azul));
			jueves.setTextColor(Color.WHITE);
			break;
		case 6:
			viernes.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.input_azul));
			viernes.setTextColor(Color.WHITE);
			break;
		case 7:
			sabado.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.input_azul));
			sabado.setTextColor(Color.WHITE);
			break;
		default:
			break;
		}

		return true;
	}

	@Override
	public void onBackPressed() {
	}

}