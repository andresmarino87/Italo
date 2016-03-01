package easysync;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import servicios.HttpServicio;
import utilidades.EasyUtilidades;

import com.italo_view.EasyInstallActivity;
import com.italo_view.General;
import com.italo_view.GlobaG;
import com.italo_view.Global;
import com.italo_view.R;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

public class EasySyncService extends IntentService {

	NotificationManager notificationManager;

	private Notification newForecastNotification;

	Context _context;
	private List<String> arrRequerimientoEasySync = new ArrayList<String>();

	public static final String NUEVO_REQUERIMIENTO_EASYSYNC = "Nuevo_Requerimiento_EasySync";
	public static final int NOTIFICATION_ID = 1;

	public static final String NUEVO_ESTADO_EASYSYNC = "Nuevo_Estado_EasySync";
	public static final int NOTIFICATION_ESTADO_ID = 2;

	public static final int NOTIFICATION_MENSAJE_ID = 3;
	
	private Handler mHandlerEasySync = new Handler();

	// private Timer easysyncTimer;
	// TimerTask EasySyncNovedades;
	int intervaloTiempo = 25000;
	private boolean sincronizandoFlag = false;

	private String easySyncEstado = "SINCRONIZANDO";
	private String easySyncMensaje = "";
	private Date easySyncFechaHora = new Date();

	GlobaG gGlobal;
	
	public EasySyncService() {
		super("EasySyncService");
		// TODO Auto-generated constructor stub
	}

	private void RegistrarEvento(String _resultado, String _mensaje,
			Boolean _anunciar) {
		easySyncEstado = _resultado;
		easySyncMensaje = _mensaje;
		easySyncFechaHora = new Date(System.currentTimeMillis());

		if (_anunciar) {
			AnunciarEstado();
		}
	}

	private void AnunciarEstado() {
		Intent intent = new Intent(EasySyncService.NUEVO_ESTADO_EASYSYNC);
		intent.putExtra("estado", easySyncEstado);
		intent.putExtra("mensaje", easySyncMensaje);
		intent.putExtra("fechahora", easySyncFechaHora.toLocaleString());
		_context.sendBroadcast(intent);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub

		Log.i("info2", "inicio servicio");

		_context = this;
		// MostrarAlerta();
		AnunciarEstado();

		try {
			Log.i("info2", "ejecucion easysync");

			
			RegistrarEvento("SINCRONIZANDO", "INICIO DE SINCRONISMO", false);

			gGlobal = (GlobaG)intent.getExtras().getSerializable("global");
			Log.i("info2", "ejecucion easysync-"+gGlobal.getLoginDM());
			//gGlobal = ((gGlobal) getApplicationContext());
			EasySync easysync = new EasySync(_context, gGlobal);
			boolean _resultado;

			_resultado = easysync.VerificarRegistrarEvento();

			EasyUtilidades.BorrarArchivos(gGlobal.pathArchivosSync + "/");
			
			if (gGlobal.getSincronismoInicial()) {
				if (EasyUtilidades.TipoConnection(_context) == 1) {
					_resultado = easysync.SincronismoInicial(
							gGlobal.getLoginDM(), gGlobal.getPasswordDM(),
							gGlobal.getDeviceIdDM(), gGlobal.Version,
							gGlobal.Revision, gGlobal.Compilacion);
				} else {
					_resultado = easysync.SincronismoInicialV3(
							gGlobal.getLoginDM(), gGlobal.getPasswordDM(),
							gGlobal.getDeviceIdDM(), gGlobal.Version,
							gGlobal.Revision, gGlobal.Compilacion);

				}

			} else {
				if (EasyUtilidades.TipoConnection(_context) == 1) {
					_resultado = easysync.SincronismoNovedadV3(
							gGlobal.getLoginDM(), gGlobal.getPasswordDM(),
							gGlobal.getDeviceIdDM(), gGlobal.Version,
							gGlobal.Revision, gGlobal.Compilacion);

				} else {
					_resultado = easysync.SincronismoNovedadV5(
							gGlobal.getLoginDM(), gGlobal.getPasswordDM(),
							gGlobal.getDeviceIdDM(), gGlobal.Version,
							gGlobal.Revision, gGlobal.Compilacion);
				}

			}

			Log.i("info2", "sincronizó borrar archivos");
			EasyUtilidades.BorrarArchivos(gGlobal.pathArchivosSync + "/");
			
			Log.i("info2", "verficar registro eventos");
			_resultado = easysync.VerificarRegistrarEvento();
			Log.i("info2", "resultado-"+_resultado);

			if (_resultado) {
				RegistrarEvento("OK", "OK", false);
			} else {
				RegistrarEvento("ERROR", "ERROR", false);
			}

			sincronizandoFlag = false;

			Log.i("info2", "nueva version-"+easysync.ObtenerNuevaVersion);
			if (easysync.ObtenerNuevaVersion) {
				Global.ActualizacionDescargada = false;
				DescargarNuevaVersion();
			}

			Log.i("info2", "detectar notificaciones");
			DetectarNotificaciones();
			Log.i("info2", "fin detectar notificaciones");

			/*
			//llamar sincronismo de file
			Intent newIntentEasySyncFile = new Intent(this, EasySyncFileService.class);
			newIntentEasySyncFile.putExtra("global", gGlobal);
			this.startService(newIntentEasySyncFile);
			*/
			
			try {
				// this.finalize();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		} catch (Exception ex) {
			Log.i("info", "error easysyncservice-"+ex.getMessage());
			sincronizandoFlag = false;
		}

	}
	
	private void DetectarNotificaciones() {
		//DetectarNoficacionMensajes();
		
	}

	/*
	private void DetectarNoficacionMensajes() {

		//Log.i(("info", "verificar notificaciones mensajes");			

		EasySurveyDBAdapter easySalesDBAdapter;
		easySalesDBAdapter = new EasySurveyDBAdapter(_context);
		// Open or create the database
		easySalesDBAdapter.open();		
		String cadSql = "select tipo from esd_mensaje where tipo='RECIBIDO' and estado=1";
		String _mensaje = easySalesDBAdapter.getEscalarString(cadSql, null);
		easySalesDBAdapter.close();
		
		if (!(_mensaje.trim().equals("")))
		{
			//Log.i(("info", "verificar notificaciones pendiente");			
			
			int icon = R.drawable.bandeja_entrada;
			String tickerText = getResources().getString(R.string.mensajesPedientesLeer);
			long when = System.currentTimeMillis();
			newForecastNotification = new Notification(icon, tickerText, when);
					
			String svcName = Context.NOTIFICATION_SERVICE;
			NotificationManager notificationManager = (NotificationManager) getSystemService(svcName);
			Context context = getApplicationContext();
			String expandedText = "mensajesPedientesLeer";
			String expandedTitle = "EasySales";
			Intent startActivityIntent = new Intent(_context,
					MensajeForm.class);
			PendingIntent launchIntent = PendingIntent.getActivity(_context, 0,
					startActivityIntent, 0);
			//Log.i(("info", "7");
			newForecastNotification.setLatestEventInfo(_context, expandedTitle,
					expandedText, launchIntent);
			//Log.i(("info", "8");
			newForecastNotification.when = java.lang.System.currentTimeMillis();
			//Log.i(("info", "9");
			notificationManager.notify(NOTIFICATION_MENSAJE_ID, newForecastNotification);
			
		}
		
	}
	*/

	private void MostrarAlerta() {

		//gGlobal gGlobal = ((gGlobal) getApplicationContext());
		Global.ActualizacionDescargada = true;

		int icon = R.drawable.ic_launcher;
		String tickerText = getResources().getString(
				R.string.actualizacionEasySales);
		long when = System.currentTimeMillis();
		newForecastNotification = new Notification(icon, tickerText, when);

		String svcName = Context.NOTIFICATION_SERVICE;
		NotificationManager notificationManagerInstall = (NotificationManager) getSystemService(svcName);
		Context context = getApplicationContext();
		String expandedText = "Se encuentra disponible una nueva versión de EasySales";
		String expandedTitle = getResources().getString(
				R.string.app_name);
		/*
		Intent startActivityIntent = new Intent(_context,
				EasyInstallActivity.class);
		PendingIntent launchIntent = PendingIntent.getActivity(_context, 0,
				startActivityIntent, 0);
		//Log.i("info", "7");
		newForecastNotification.setLatestEventInfo(_context, expandedTitle,
				expandedText, launchIntent);
		//Log.i("info", "8");
		*/
		Intent startActivityIntent = new Intent(context,
				EasyInstallActivity.class);
		PendingIntent launchIntent = PendingIntent.getActivity(context, 0,
				startActivityIntent, 0);
		//Log.i("info", "7");
		newForecastNotification.setLatestEventInfo(context, expandedTitle,
				expandedText, launchIntent);
		newForecastNotification.when = java.lang.System.currentTimeMillis();
		//Log.i("info", "9");
		notificationManagerInstall.notify(NOTIFICATION_ID,
				newForecastNotification);

	}
	
	

	private void DescargarNuevaVersion() {
		// TODO Auto-generated method stub

		//gGlobal gGlobal = ((gGlobal) getApplicationContext());
		if (Global.ActualizacionDescargada)
			return;

		String deviceId = General.getDeviceID(_context);

		//String url = "http://216.157.16.208/EasyServerUnibol/Sincronismo/Instalador/51/"
		//		+ deviceId;
		
		String url = GlobaG.UrlEasyServerInicial + "/Sincronismo/InstaladorV2/"+gGlobal.getLoginDM()+"/"+ deviceId;
		//String url = gGlobal.UrlEasyServer + "/Sincronismo/InstaladorV2/"+gGlobal.getLoginDM()+"/"+ deviceId;
		
		File file = new File(Environment.getExternalStorageDirectory()
				.toString() + "/download/"+gGlobal.nombreApk);

		FileOutputStream fos = null;
		InputStream is = null;
		try {

			/*
			HttpParams httpParameters = new BasicHttpParams();

			int timeoutConnection = 20000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			int timeoutSocket = 600000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			 */
			
			//HttpClient httpclient = new DefaultHttpClient(httpParameters);
			
			HttpClient httpclient = HttpServicio.getAndroidHttpClient();

			HttpPost httppost = new HttpPost(url);
			Log.i("info", "antes llamado download." + url);

			HttpResponse response = httpclient.execute(httppost);

			Log.i("info", "pp2");

			HttpEntity entity = response.getEntity();

			long length = entity.getContentLength();
			Log.i("info", "pp3-" + String.valueOf(length));
			fos = new FileOutputStream(file);

					
			is = entity.getContent();
			long totalread = 0;
			byte[] bytes = new byte[1024];
			for (;;) {
				int count = is.read(bytes, 0, 1024);
				//Log.i("info", "pp3-leido-//" + count);
				if (count == -1) {
					break;
				}
				fos.write(bytes, 0, count);
				//Log.i("info", NumberFormat.getInstance().format(count)
				//		+ " bytes decargados");
				totalread += count;
			}
			//Log.i("info", "pp1f-"+totalread);

			HttpServicio.CerrarConexion(httpclient);
			
			if (totalread != 0) {
				// generar alerta
				// InstalarAplicacion();
				MostrarAlerta();
			}
			
		} catch (MalformedURLException e) {
			Log.i("info", "pp2f-error1");
			e.printStackTrace();
		} catch (IOException e) {
			Log.i("info", "pp2f-error2." + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				Log.i("info", "pp2f");

				if (is != null)
					is.close();
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private void InstalarAplicacion() {
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/download", "EasySurveyClient.apk");

		Intent intent = new Intent(Intent.ACTION_VIEW);

		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");

		startActivity(intent);
	}

}
