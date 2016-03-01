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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import servicios.HttpServicio;

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

public class EasySyncFileService extends IntentService {

	NotificationManager notificationManager;

	private Notification newForecastNotification;

	Context _context;
	private List<String> arrRequerimientoEasySync = new ArrayList<String>();

	public static final String NUEVO_REQUERIMIENTO_EASYSYNC = "Nuevo_Requerimiento_EasySyncFile";
	public static final int NOTIFICATION_ID = 4;

	public static final String NUEVO_ESTADO_EASYSYNC = "Nuevo_Estado_EasySyncFile";
	public static final int NOTIFICATION_ESTADO_ID = 5;

	public static final int NOTIFICATION_MENSAJE_ID = 6;
	
	private Handler mHandlerEasySync = new Handler();

	// private Timer easysyncTimer;
	// TimerTask EasySyncNovedades;
	int intervaloTiempo = 25000;
	private boolean sincronizandoFlag = false;

	private String easySyncEstado = "SINCRONIZANDO";
	private String easySyncMensaje = "";
	private Date easySyncFechaHora = new Date();

	GlobaG gGlobal;
	
	public EasySyncFileService() {
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
		Intent intent = new Intent(EasySyncFileService.NUEVO_ESTADO_EASYSYNC);
		intent.putExtra("estado", easySyncEstado);
		intent.putExtra("mensaje", easySyncMensaje);
		intent.putExtra("fechahora", easySyncFechaHora.toLocaleString());
		intent.putExtra("syncdatos", false);
		_context.sendBroadcast(intent);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub

		//Log.i("info2", "inicio servicio");
		gGlobal = (GlobaG)intent.getExtras().getSerializable("global");

		_context = this;
		// MostrarAlerta();
		AnunciarEstado();

		try {
			//Log.i("info2", "ejecucion easysync");

			RegistrarEvento("SINCRONIZANDO", "INICIO DE SINCRONISMO FILE", false);

			EasySync easysync = new EasySync(_context, false, gGlobal);
			/*
			 * boolean _resultado = easysync.SincronismoNovedad(
			 * gGlobal.getLoginDM(), gGlobal.getPasswordDM(),
			 * gGlobal.getDeviceIdDM(), gGlobal.Version, gGlobal.Revision);
			 */
			boolean _resultado;

			_resultado = easysync.VerificarRegistrarEventoFile();

			_resultado = easysync.SincronismoFile(
					gGlobal.getLoginDM(), gGlobal.getPasswordDM(),
					gGlobal.getDeviceIdDM(), gGlobal.Version,
					gGlobal.Revision, gGlobal.Compilacion);

			
			Log.i("info2", "verficar registro eventos file");
			_resultado = easysync.VerificarRegistrarEventoFile();
			Log.i("info2", "resultado file-"+_resultado);

			// if (_resultado == true) {
			// }
			if (_resultado) {
				//Log.i("info", "registrar evento ok 1");
				RegistrarEvento("OK", "OK", false);
			} else {
				RegistrarEvento("ERROR", "ERROR", false);
			}

			// mHandlerEasySync.postDelayed(mEasySyncNovedades,
			// intervaloTiempo);
			//Log.i("info2", "fin ejecucion easysync");
			sincronizandoFlag = false;

		} catch (Exception ex) {
			sincronizandoFlag = false;
		}

	}
	
	private void DetectarNotificaciones() {
		//DetectarNoficacionMensajes();
		
	}

	private void MostrarAlerta() {

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

		if (Global.ActualizacionDescargada)
			return;

		String deviceId = General.getDeviceID(_context);

		//String url = "http://216.157.16.208/EasyServerUnibol/Sincronismo/Instalador/51/"
		//		+ deviceId;
		
		String url = "https://216.157.17.91/EasyServerKernel/Sincronismo/InstaladorV2/"+gGlobal.getLoginDM()+"/"+ deviceId;
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
