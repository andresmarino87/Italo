	package com.italo_view;

import java.util.List;
import java.util.Locale;


import utilidades.EasyUtilidades;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import bd_utilidades.ItaloDBAdapter;
import easygps.EasyGpsItaloService;
import easysync.EasySyncService;


public class LoginActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

    ItaloDBAdapter italoDBAdapter;
	Cursor cursor;
	Button button_ingresar;
	Button button_sincronizar;
	Button button_borrarbasedatos;
	EditText etPassword;
	EditText etUsuario;
	TextView tvUsuario;
	TextView tvDeviceID;
	TextView espacio;
	TextView tvPrueba;
	TextView tvMensajeSincronismoInicial;
	TextView tvVersion;
	Boolean sincronismoInicial;
	Button buOrientacion;
	ImageView logo;
	
	String password;
	String usuario;
	String nombre_vendedo = "vendddd";

	Display display;
	GlobaG gGlobaG;
	Context context;
	TextView textView1;
	int version = GlobaG.Version;
	int revision = GlobaG.Revision;
	int compilacion = GlobaG.Compilacion;
	String deviceID = "";

	NotificationManager notificationManager;
	private EasySyncEstadoReceiver estadoEasySync;

	private String easySyncEstado = "";
	private String easySyncMensaje = "";
	private String easySyncFechaHora = "";

	ImageView ivIconoEasySync;

	boolean _respuestaAfirmativa = false;

	@SuppressWarnings("static-access")
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActivityManager manager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = manager.getRunningTasks(Integer.MAX_VALUE);
		for (RunningTaskInfo taskInfo : tasks) {
			if (taskInfo.baseActivity.getClassName().equals("com.italo_view.LoginActivity")	&& (taskInfo.numActivities > 1)) {
				finish();
			}
		}

		String svcName = Context.NOTIFICATION_SERVICE;
		notificationManager = (NotificationManager) getSystemService(svcName);
		Log.i("info", "ingreso a login activity");
		
		setContentView(R.layout.activity_login);
		context = this;

		EasyUtilidades.CrearDirectoriosEasySales();
		Log.i("info", "crear actividad ");
		//gGlobaG = ((GlobaG) getApplicationContext());
		gGlobaG = new GlobaG();

		/*
		gGlobaG.AdicionarActividad(this);
		
		gGlobaG.AdicionarActividadRunning(this);
		*/
		
		ivIconoEasySync = (ImageView) findViewById(R.id.item_ruta_icono);

		tvVersion = (TextView) this.findViewById(R.id.tvVersion);
		tvVersion.setText(gGlobaG.getVersion());
		tvUsuario = (TextView) this.findViewById(R.id.tvNombreVendedor);
		button_ingresar = (Button) this.findViewById(R.id.button_ingresar);
		textView1 = (TextView) this.findViewById(R.id.textView1);
		button_ingresar.setOnClickListener(this);
		etPassword = (EditText) findViewById(R.id.editText_password);
		logo=(ImageView)findViewById(R.id.logo);
		logo.setImageResource(R.drawable.default_image);
		logo.setImageDrawable(context.getResources().getDrawableForDensity(R.drawable.default_image, DisplayMetrics.DENSITY_XXXHIGH));
		
	//	context
//    	thumbnail.setImageResource(R.drawable.ic_launcher);
//		mContext.getApplicationContext().getResources().getDrawableForDensity(mThumbIds[position],DisplayMetrics.DENSITY_XXHIGH)
		
		
		etUsuario = (EditText) findViewById(R.id.editText_usuario);
//		etUsuario.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
		etUsuario.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		
//		text.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);

//		etUsuario.setText("Sect172");
//		etPassword.setText("@romanos12:2");
//		etUsuario.setText("Sect111");
//		etPassword.setText("@Miqueas7:18");	
//		etUsuario.setText("Sect305");
//		etPassword.setText("@2Pedro1:4@");
//		etUsuario.setText("Sect114");
///		etPassword.setText("@rom10:17@");		
//		etUsuario.setText("Sect109");
	//	etPassword.setText("@Mateo7:11@");		
//		etUsuario.setText("Sect241");
//		etPassword.setText("@Salmo145:17");
//		etUsuario.setText("Sect116");
	//	etPassword.setText("@jer31:3@");
//		etUsuario.setText("Sect107");
//		etPassword.setText("@Jer33:3@");		
////		etUsuario.setText("Sect106");
//		etPassword.setText("@rom8:31@");		
//		etUsuario.setText("Sect112");
//		etPassword.setText("@jer29:11@");		
//		etUsuario.setText("Gerevent");
//		etPassword.setText("@isaias43:2@");		
//etUsuario.setText("Sect102");
	//	etPassword.setText("@juan10:27@");		
		etUsuario.setText("Sect110");
		etPassword.setText("@Fil4:19@");		
//		etUsuario.setText("Sect101");
	//	etPassword.setText("@juan4:13@");		
//		etUsuario.setText("Sect104");
//		etPassword.setText("@num6:24@");		
//		etUsuario.setText("Sect118");
//		etPassword.setText("@josue1:8@");		
//		etUsuario.setText("Direregi");
//		etPassword.setText("@jeremias33@");
//		etUsuario.setText("Sect115");
//		etPassword.setText("@juan17:3@");
//		etUsuario.setText("Direbogo");
//		etPassword.setText("@juan17:3@");
//		etUsuario.setText("Sect170");
//		etPassword.setText("@juan14:27@");

		tvDeviceID = (TextView) findViewById(R.id.tvDeviceID);
		tvMensajeSincronismoInicial = (TextView) findViewById(R.id.tvMensajeSincronismoInicial);

		Locale.setDefault(new Locale("en", "US"));
		String locale = this.getResources().getConfiguration().locale.getDisplayCountry();
		Log.i("info", "locale:" + locale);
		String d = "1,000.00";
		d = d.replace(",", "");
		float flo = Float.valueOf(d.trim()).floatValue();
		deviceID = new General().getDeviceID(this);
		gGlobaG.setDeviceIdDM(deviceID);
		tvDeviceID.setText("Device id: " + deviceID);
		if (EasyUtilidades.ExisteBaseDatos()) {
			Log.i("info", "cargar informacion inicial");
			// Open or create the database
			italoDBAdapter = new ItaloDBAdapter(context);
			CargarInformacionInicial();
		} else {
			Log.i("info", "sincronismo inicial");
			this.etUsuario.requestFocus();
			sincronismoInicial = true;
			//notificationManager.cancel(EasySyncService.NOTIFICATION_ESTADO_ID);
			IntentFilter filter;
			filter = new IntentFilter(EasySyncService.NUEVO_ESTADO_EASYSYNC);
			estadoEasySync = new EasySyncEstadoReceiver();
			registerReceiver(estadoEasySync, filter);
			gGlobaG.setSincronismoInicial(true);
		}
	}

	@Override
	public void onResume() {
		super.onResume();


	}

	private void CargarBaseDatos() {

	}

	private void CargarInformacionInicial() {
		sincronismoInicial = false;
		gGlobaG.setSincronismoInicial(false);
		this.etPassword.requestFocus();

		Log.i("info", "abrir");
		italoDBAdapter.open();
		Log.i("info", "attach 1");
		italoDBAdapter.attachBaseDatos("Down");
		Log.i("info", "attach 2");
		italoDBAdapter.attachBaseDatos("Up");

		InicializaVendedor();
		// InicializaPassword();
		Log.i("info", "cargar informacion vendedor");
		InicializaInformacionVendedor();

		Log.i("info", "cargar informacion vendedor2");
		display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int marginTop = display.getHeight() / 20;
		Log.i("info", "cargar informacion vendedor3");
		Log.i("info", "fin cargar informacion vendedor");
	}

	private void InicializaInformacionVendedor() {

		cursor = italoDBAdapter.getAllConfiguracionAplicacion();
		Log.i("info", "despuesta de respuesta");
//		startManagingCursor(cursor);
		if (cursor.moveToFirst()) {
			gGlobaG.setLoginDM(cursor.getString(cursor.getColumnIndex(ItaloDBAdapter.esc_configuracion_aplicacion_login_dm)));
			gGlobaG.setDeviceIdDM(cursor.getString(cursor.getColumnIndex(ItaloDBAdapter.esc_configuracion_aplicacion_device_id_dm)));
			gGlobaG.setNombreUsuario(cursor.getString(cursor.getColumnIndex(ItaloDBAdapter.esc_configuracion_aplicacion_nombre_usuario)));
			gGlobaG.setNombreEmpresa(cursor.getString(cursor.getColumnIndex(ItaloDBAdapter.esc_configuracion_aplicacion_nombre_empresa)));
			gGlobaG.setPasswordDM(cursor.getString(cursor.getColumnIndex(ItaloDBAdapter.esc_configuracion_aplicacion_password_dm)));
			gGlobaG.setEasyAsignacionId(Integer.toString(cursor.getInt(cursor.getColumnIndex(ItaloDBAdapter.esc_configuracion_aplicacion_easy_asignacion_id))));
			//gGlobaG.setAgente_vendedor(gGlobaG.getLoginDM().substring(3, 6));
			gGlobaG.setAgente_vendedor(gGlobaG.getLoginDM());
		}
		cursor.close();

		password = gGlobaG.getPasswordDM().toString().trim();
		etUsuario.setText(gGlobaG.getLoginDM());
		// etPassword.setText(gGlobaG.getPasswordDM());
		tvUsuario.setText(gGlobaG.getNombreUsuario());

		Log.i("info", "información cargada de usuario-password:" + password
				+ "-device_id:" + gGlobaG.getDeviceIdDM());
	}

	private void InicializaPassword() {
		cursor = italoDBAdapter.getAllPasswordItemsCursor();
//		startManagingCursor(cursor);
		if (cursor.moveToFirst()) {
			password = cursor.getString(cursor.getColumnIndex(ItaloDBAdapter.PASSWORD_PASSWORD)).trim();
			etPassword = (EditText) this.findViewById(R.id.editText_password);
			etPassword.setText("prt123");
		}
		cursor.close();
	}

	@SuppressWarnings("static-access")
	private void InicializaVendedor() {
		String nv = "";
		cursor = italoDBAdapter.getAllVendedor();
//		startManagingCursor(cursor);
		if (cursor.moveToFirst()) {
			nv = cursor.getString(cursor.getColumnIndex(italoDBAdapter.esd_nombre_asesor));
			gGlobaG.setUsuario_comentario(nv);
		} else {
			tvUsuario.setText("Usuario no registrado");
		}
		cursor.close();
	}

	public void onClick(View v) {
		Log.i("info", "hizo click");

		switch (v.getId()) {
		case R.id.button_ingresar:

			button_ingresar.setEnabled(false);

			Log.i("info", "revisar informacion");
			if (etUsuario.getText().toString().trim().compareTo("") == 0) {
				// favor indicar password
				etUsuario.requestFocus();
				Toast.makeText(getApplicationContext(),R.string.messageTextoIndicarUsuario, Toast.LENGTH_SHORT).show();
				button_ingresar.setEnabled(true);
				return;

			}

			if (etPassword.getText().toString().trim().compareTo("") == 0) {
				// favor indicar password
				etPassword.requestFocus();
				Toast.makeText(getApplicationContext(),	R.string.messageTextoIndicarPassword,Toast.LENGTH_SHORT).show();
				button_ingresar.setEnabled(true);
				return;

			}

			if (sincronismoInicial == false) {
				Log.i("info", "-" + etPassword.getText().toString()  + "-" + password + "-");
				if (etPassword.getText().toString().equals(password)) {
					// llamado de EasySync
					callMenuPrincipal();

				} else {
					Toast.makeText(getApplicationContext(), R.string.messageTextoPasswordInvalido,Toast.LENGTH_SHORT).show();
					etPassword.setText("");
					button_ingresar.setEnabled(true);
				}
			} else {
				callSincronismoInicial();
			}
			break;
		default:
			break;
		}
	}

	private void callSincronismoInicial() {

		gGlobaG.setLoginDM(this.etUsuario.getText().toString().trim());
		gGlobaG.setPasswordDM(this.etPassword.getText().toString().trim());

		EasyUtilidades.SolicitarEasySync(this, gGlobaG);
		

	}

	private void callSincronismoNovedad() {
		// TODO Auto-generated method stub
		/*
		Log.i("info", "sincronismo inicial");
		EasySync easysync = new EasySync(this);
		boolean _resultado = easysync.SincronismoNovedad(gGlobaG.getLoginDM(),
				gGlobaG.getPasswordDM(), gGlobaG.getDeviceIdDM(),
				gGlobaG.Version, gGlobaG.Revision);
		*/
		
		EasyUtilidades.SolicitarEasySync(this, gGlobaG);
		
		/*
		Toast.makeText(this, "Sincronismo novedades finalizado " + _resultado,
				Toast.LENGTH_LONG);
				*/
	}

	private void callMenuPrincipal() {
		// cancelar mensajes
		notificationManager.cancel(EasySyncService.NOTIFICATION_ESTADO_ID);

		estadoEasySync = null;

		// IniciarServicios();
		Intent newIntentEasyGps = new Intent(this, EasyGpsItaloService.class);
		startService(newIntentEasyGps);

		// EasyUtilidades.SolicitarEasySync(this);

		/*
		Intent newIntent = new Intent(context, MainActivity.class);
		startActivityForResult(newIntent, 0);
		*/

		Intent newIntent = new Intent(context, MainActivity.class);
		newIntent.putExtra("global", gGlobaG);
		//newIntent.putExtra("pedido", new Pedido());
		startActivityForResult(newIntent, 0);
		
		
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_login, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case (R.id.borrar_bd): {
			BorrarArchivosBaseDatos();
//			android.os.Process.killProcess(android.os.Process.myPid());
			return true;
		}
		}
		return false;
	}
	
	
	private void BorrarArchivosBaseDatos() {
		_respuestaAfirmativa = false;

		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		alert.setTitle(getResources().getString(R.string.message_advertencia));
		alert.setIcon(getResources().getDrawable(R.drawable.alerta));
		alert.setMessage(getResources().getString(R.string.message_borrar_base_datos));
		alert.setPositiveButton(getResources().getString(R.string.si),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				AlertDialog.Builder alertc = new AlertDialog.Builder(context);
				alertc.setTitle(getResources().getString(R.string.message_advertencia));
				alertc.setIcon(getResources().getDrawable(R.drawable.alerta));
				alertc.setMessage(getResources().getString(R.string.message_confirmacion_borrar_base_datos));
				alertc.setPositiveButton(getResources().getString(R.string.si),new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int whichButton) {
						_respuestaAfirmativa = true;
						_respuestaAfirmativa = EasyUtilidades.BorrarArchivos(GlobaG.DATABASE_PATH);
						if (!_respuestaAfirmativa) {
							return;
						} else {
							android.os.Process.killProcess(android.os.Process.myPid());
							finish();
						}
					}
				});
				alertc.setNegativeButton(getResources().getString(R.string.no),new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int whichButton) {
						return;
					}
				});
				alertc.create().show();
				return;
			}
		});	
		alert.setNegativeButton(getResources().getString(R.string.no),new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				return;
			}
		});
		alert.create().show();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Log.i("info", "esta e la opcion de finalizacion ");
		finish();
	}

	private void IniciarServicios() {

		// DetenerServicios();
		Log.i("info", "iniciar servicios login");

		Intent newIntentEasySync = new Intent(this, EasySyncService.class);
		startService(newIntentEasySync);

		Intent newIntentEasyGps = new Intent(this, EasyGpsItaloService.class);
		startService(newIntentEasyGps);
	}

	private void DetenerServicios() {
		stopService(new Intent(this, EasySyncService.class));
		stopService(new Intent(this, EasyGpsItaloService.class));
	}

	public class EasySyncEstadoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			notificationManager.cancel(EasySyncService.NOTIFICATION_ESTADO_ID);
			Log.i("info2", "mensaje recibido estado easysync");

			Bundle extras = intent.getExtras();
			if (extras != null) {
				easySyncEstado = extras.getString("estado");
				easySyncMensaje = extras.getString("mensaje");
				easySyncFechaHora = extras.getString("fechahora");

				String mensajeEstadoEasySync = getResources().getString(
						R.string.easysync_fechahora)
						+ ": "
						+ easySyncFechaHora
						+ ".\n"
						+ getResources().getString(R.string.easysync_estado)
						+ ": "
						+ easySyncEstado
						+ ".\n"
						+ getResources().getString(
								R.string.easysync_observacion)
						+ ": "
						+ easySyncMensaje + ".\n";
				tvMensajeSincronismoInicial.setText(mensajeEstadoEasySync);

				Log.i("info", "estado:" + easySyncEstado);

				if (easySyncEstado.equals("OK")) {
					if (sincronismoInicial) {
						Log.i("info2", "resultado sincronismo inicial 1");
						ivIconoEasySync.setImageResource(R.drawable.verde);
						italoDBAdapter = new ItaloDBAdapter(context);
						CargarInformacionInicial();
						callMenuPrincipal();
					}
				} else if (easySyncEstado.equals("ERROR")) {
					Log.i("info2", "resultado sincronismo inicial 2");
					ivIconoEasySync.setImageResource(R.drawable.rojo);
					button_ingresar.setEnabled(true);
				} else if (easySyncEstado.equals("SINCRONIZANDO")) {
					Log.i("info2", "resultado sincronismo inicial 3");
					ivIconoEasySync.setImageResource(R.drawable.amarillo);
				} else {
					Log.i("info2", "resultado sincronismo inicial 4");
					ivIconoEasySync.setImageResource(R.drawable.gris);
					button_ingresar.setEnabled(true);
				}
			}

		}

	}

}