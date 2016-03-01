package easygps;

import bd_utilidades.ItaloDBAdapter;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class EasyGpsItaloService extends Service {

	private EasyGpsRequerimientoReceiver requerimientoEasyGps;
	private EasyGpsCancelarRequerimientoReceiver requerimientoFinEasyGps;
	NotificationManager notificationManagerNuevo;
	NotificationManager notificationManagerFin;
	Context _context;

	public static final String NUEVO_REQUERIMIENTO_EASYGPS = "Nuevo_Requerimiento_EasyGps";
	public static final int NOTIFICATION__NUEVOGPS_ID = 2;

	public static final String FIN_REQUERIMIENTO_EASYGPS = "Fin_Requerimiento_EasyGps";
	public static final int NOTIFICATION_FINGPS_ID = 3;

	private Handler mHandlerEasyGps = new Handler();

	// easygps
	EasyGps easyGps;
	int numeroIntentos = 0;
	Location ubicacion;

	int intervaloTiempo = 1000;
	int timeOutGPS = 60000;
	int tiempoIntentos = 0;

	@Override
	public void onCreate() {

		String svcName = Context.NOTIFICATION_SERVICE;

		notificationManagerNuevo = (NotificationManager) getSystemService(svcName);
		notificationManagerFin = (NotificationManager) getSystemService(svcName);

		_context = getApplicationContext();

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		Log.i("info2", "iniciar servicios gps");

		// filtros de solicitudes de sincronismo
		notificationManagerNuevo
				.cancel(EasyGpsItaloService.NOTIFICATION__NUEVOGPS_ID);
		notificationManagerFin.cancel(EasyGpsItaloService.NOTIFICATION_FINGPS_ID);

		IntentFilter filter;
		filter = new IntentFilter(EasyGpsItaloService.NUEVO_REQUERIMIENTO_EASYGPS);
		requerimientoEasyGps = new EasyGpsRequerimientoReceiver();
		registerReceiver(requerimientoEasyGps, filter);

		IntentFilter filterf;
		filterf = new IntentFilter(EasyGpsItaloService.FIN_REQUERIMIENTO_EASYGPS);
		requerimientoFinEasyGps = new EasyGpsCancelarRequerimientoReceiver();
		registerReceiver(requerimientoFinEasyGps, filterf);

	}

	@Override
	public void onDestroy() {
		// super.onDestroy();

		DestruirGPS();
		mHandlerEasyGps.removeCallbacks(EntregarInicializarCaptura);

	}

	private Runnable EntregarInicializarCaptura = new Runnable() {
		public void run() {
			try {
				// ++numeroIntentos;
				tiempoIntentos += intervaloTiempo;
				Log.i("info", "evaluar coordenadas");
				if (easyGps.mejorLocalizacion != null) {
					ubicacion = easyGps.mejorLocalizacion;
					Log.i("info2", "coordenada evaluada");
					EntregarCoordenada();
				}

				if (tiempoIntentos > timeOutGPS) {
					Log.i("info2", "timeout, mejor coordenada");
					DestruirGPS();
				} else {
					mHandlerEasyGps.postDelayed(EntregarInicializarCaptura,
							intervaloTiempo);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	private void EntregarCoordenada() {
		Log.i("info2", "evaluar coordenadasnn");
		if (ubicacion != null) {
			// reportar coordenadas
			// latitud_visita_actual = ubicacion.getLatitude();
			// longitud_visita_actual = ubicacion.getLongitude();

			Log.i("info2", "easygps reporta coordenadas");

//			ItaloDBAdapter ItaloDBAdapter;
//			ItaloDBAdapter = new ItaloDBAdapter(_context);
//			ItaloDBAdapter.open();
			//ItaloDBAdapter.updateRegistroVisita(ubicacion.getLatitude(),
			//		ubicacion.getLongitude());
//			ItaloDBAdapter.close();

		}
		// easyGps.DestroyEasyGps();
	}

	private void DestruirGPS() {
		try {
			easyGps.DestroyEasyGps();
		} catch (Exception ex) {

		}
	}

	public class EasyGpsRequerimientoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("info2", "mensaje recibido capturar gps");

			notificationManagerNuevo
					.cancel(EasyGpsItaloService.NOTIFICATION_FINGPS_ID);

			DestruirGPS();

			easyGps = null;

			tiempoIntentos = 0;
			numeroIntentos = 0;
			ubicacion = null;

			easyGps = new EasyGps(_context);

			mHandlerEasyGps.removeCallbacks(EntregarInicializarCaptura);
			mHandlerEasyGps.postDelayed(EntregarInicializarCaptura,
					intervaloTiempo);

		}

	}

	public class EasyGpsCancelarRequerimientoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("info2", "mensaje recibido finalizar gps");

			notificationManagerNuevo
					.cancel(EasyGpsItaloService.NOTIFICATION__NUEVOGPS_ID);

			DestruirGPS();

			easyGps = null;

			mHandlerEasyGps.removeCallbacks(EntregarInicializarCaptura);
		}

	}

}
