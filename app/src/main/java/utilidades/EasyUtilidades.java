package utilidades;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.GlobaG;

import easygps.EasyGpsItaloService;
import easysync.EasySyncService;
import easysync.FileSales;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class EasyUtilidades {

	public byte[] getByteFile(String pPathFile, Context _context)
			throws IOException {
		Log.i("info", "archivo." + pPathFile);
		// FileInputStream filein = _context.openFileInput(pPathFile);

		FileInputStream filein = new FileInputStream(pPathFile);

		int read = 0;
		int offset = 0;
		int chunk_size = 1024;
		int buffer_size = 1024;
		int total_size = 0;

		ArrayList<byte[]> chunks = new ArrayList<byte[]>();
		chunks.add(new byte[chunk_size]);
		// first I read data from file chunk by chunk
		while ((read = filein.read(chunks.get(chunks.size() - 1), offset,
				buffer_size)) != -1) {
			total_size += read;
			if (read == buffer_size) {
				chunks.add(new byte[buffer_size]);
			}
		}
		int index = 0;

		// then I create big buffer
		byte[] rawdata = new byte[total_size];

		// then I copy data from every chunk in this buffer
		for (byte[] chunk : chunks) {
			for (byte bt : chunk) {
				index += 0;
				rawdata[index] = bt;
				if (index >= total_size)
					break;
			}
			if (index >= total_size)
				break;
		}

		// and clear chunks array
		chunks.clear();

		Log.i("info", "archivo leido");
		return rawdata;

	}

	private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat(
			"HH:mm, dd/MM");

	public static Date dateFromString(String strDate, String strFormat) {
		SimpleDateFormat curFormater = new SimpleDateFormat(strFormat);
		Date date = new Date();
		try {
			date = curFormater.parse(strDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;

	}

	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static boolean CheckConnection(Context pContext) {

		ConnectivityManager connMgr = (ConnectivityManager) pContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connMgr

		.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		boolean isConnected = networkInfo.isConnected();

		Log.i("info",
				"Extra info:" + networkInfo.getExtraInfo() + ". Subtipo:"
						+ networkInfo.getSubtypeName() + ".tipo:"
						+ networkInfo.getTypeName());

		if (isConnected)

			return isConnected;

		networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		Log.i("info",
				"Extra info:" + networkInfo.getExtraInfo() + ". Subtipo:"
						+ networkInfo.getSubtypeName() + ".tipo:"
						+ networkInfo.getTypeName());

		isConnected = networkInfo.isConnected();

		return isConnected;

	}

	public static int TipoConnection(Context pContext) {

		ConnectivityManager connMgr = (ConnectivityManager) pContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		boolean isConnected = networkInfo.isConnected();
		Log.i("info",
				"Extra info:" + networkInfo.getExtraInfo() + ". Subtipo:"
						+ networkInfo.getSubtypeName() + ".tipo:"
						+ networkInfo.getTypeName());

		if (isConnected) {
			// return 1;
			return 0;
		}

		networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		Log.i("info",
				"Extra info:" + networkInfo.getExtraInfo() + ". Subtipo:"
						+ networkInfo.getSubtypeName() + ".tipo:"
						+ networkInfo.getTypeName());

		isConnected = networkInfo.isConnected();

		if (isConnected && networkInfo.getExtraInfo().indexOf("comcel") != -1) {
			// return 1;
			return 0;
		}

		// return 1;
		return 0;

	}

	public static void ActualizarCajaTexto(final TextView tvMensaje,
			final String pMensaje) {

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				// Log.i("info",tvMensaje.getText() + ". " + pMensaje);
				tvMensaje.setText(pMensaje);
			}
		};
		new Thread(runnable).start();

	}

	public static boolean ExisteArchivo(String pPtahFile) {

		File file = new File(pPtahFile);
		return file.exists();
	}

	public static boolean ExisteBaseDatos() {
		return ExisteArchivo(GlobaG.DATABASE_PATH + GlobaG.DATABASE_NAME);
	}

	public static void SolicitarEasySync(Context pContext, GlobaG gGlobal) {
		// Intent intent = new
		// Intent(EasySyncService.NUEVO_REQUERIMIENTO_EASYSYNC);
		// pContext.sendBroadcast(intent);

		try {
			Intent newIntentEasySync = new Intent(pContext,
					EasySyncService.class);
			newIntentEasySync.putExtra("global", gGlobal);
			pContext.startService(newIntentEasySync);
		} catch (Exception ex) {
			Log.i("info", "error lanzar easysync-" + ex.getMessage());

		}

	}

	public static void SolicitarEasyGps(Context pContext) {
		Intent intent = new Intent(
				EasyGpsItaloService.NUEVO_REQUERIMIENTO_EASYGPS);
		pContext.sendBroadcast(intent);
	}

	public static void CancelarEasyGps(Context pContext) {
		Intent intent = new Intent(
				EasyGpsItaloService.FIN_REQUERIMIENTO_EASYGPS);
		pContext.sendBroadcast(intent);
	}

	public static void CrearDirectoriosEasySales() {
		try {
			File databaseDirectory = new File(GlobaG.DATABASE_PATH);
			// have the object build the directory structure, if needed.
			MakeDirectory(databaseDirectory);

			File downloadDirectory = new File(
					Environment.getExternalStorageDirectory() + "/download/");
			MakeDirectory(downloadDirectory);

			downloadDirectory = new File(GlobaG.pathArchivosSync + "/");
			MakeDirectory(downloadDirectory);

			downloadDirectory = new File(GlobaG.pathArchivosEasy + "/");
			MakeDirectory(downloadDirectory);

			downloadDirectory = new File(GlobaG.pathArchivosEasy + "/up/");
			MakeDirectory(downloadDirectory);

			downloadDirectory = new File(GlobaG.pathArchivosEasy + "/down/");
			MakeDirectory(downloadDirectory);
		} catch (Exception ex) {

		}

	}

	private static void MakeDirectory(File downloadDirectory) {
		try {
			downloadDirectory.mkdir();
		} catch (Exception ex) {

		}
	}

	public static ArrayList<String> ListadoArchivos(String pPath,
			String pExtension) {

		ArrayList<String> listadoArchivos = new ArrayList<String>();
		// Directory path here
		Log.i("info", "revisar diretorio." + pPath);
		String files;
		File folder = new File(pPath);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			files = listOfFiles[i].getName();
			Log.i("info", "evaluar objeto." + files);

			if (listOfFiles[i].isFile()) {
				files = listOfFiles[i].getName();
				Log.i("info", "evaluar archivo." + files);
				if (files.endsWith("." + pExtension)
						|| files.endsWith("." + pExtension.toUpperCase())) {
					listadoArchivos.add(files);
				}
			}
		}
		return listadoArchivos;
	}

	public static boolean BorrarArchivos(String pPath) {

		try {
			ArrayList<String> listadoArchivos = new ArrayList<String>();
			// Directory path here
			Log.i("info", "revisar diretorio." + pPath);
			File folder = new File(pPath);
			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				listOfFiles[i].delete();
			}
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	public static boolean copyDataBase() throws IOException {

		Log.i("info", "para descomprimir");
		EasyZip easyZip = new EasyZip();

		easyZip.UnZippingFile(Environment.getExternalStorageDirectory()
				+ "/download/EasyServerDown.zip", GlobaG.DATABASE_PATH);

		Log.i("info", "para descomprimido");

		return true;
	}

	public static boolean EncriptarBD(String pServicio,
			String sincronismoInicial, Context context) throws Exception {
		try {
			/*
			 * String nombreBDFuente = ""; String nombreBDDestino = ""; boolean
			 * fuenteEncriptada = false; boolean resultado; if
			 * (pServicio.equals("BaseDatosUp")) { nombreBDFuente =
			 * GlobaG.DATABASE_NAME_NOVEDADES_UP; nombreBDDestino = "C" +
			 * GlobaG.DATABASE_NAME_NOVEDADES_UP; fuenteEncriptada = false; }
			 * else if (pServicio.equals("EasyServerUp")) { nombreBDFuente = "C"
			 * + GlobaG.DATABASE_NAME_NOVEDADES_UP; nombreBDDestino =
			 * GlobaG.DATABASE_NAME_NOVEDADES_UP; fuenteEncriptada = true; }
			 * else if (pServicio.equals("EasyServerDown") ||
			 * pServicio.equals("RequerimientoEasyServerDown")) { if
			 * (sincronismoInicial.equals("S")) { nombreBDFuente =
			 * GlobaG.DATABASE_NAME; nombreBDDestino = "C" +
			 * GlobaG.DATABASE_NAME; fuenteEncriptada = false; } else {
			 * nombreBDFuente = GlobaG.DATABASE_NAME_NOVEDADES_DOWN;
			 * nombreBDDestino = "C" + GlobaG.DATABASE_NAME_NOVEDADES_DOWN;
			 * fuenteEncriptada = false; } } else if
			 * (pServicio.equals("EasyServerRecovery")) { nombreBDFuente = "C" +
			 * GlobaG.DATABASE_NAME; nombreBDDestino = GlobaG.DATABASE_NAME;
			 * fuenteEncriptada = true; }
			 * 
			 * // if (pServicio.equals("EasyServerRecovery")) // { //
			 * EasyUtilidades.copyFile(context.getDatabasePath(nombreBDDestino),
			 * // new File(GlobaG.pathArchivosSync + "/A" +
			 * GlobaG.DATABASE_NAME)); // }
			 * 
			 * ExportDB(nombreBDFuente, nombreBDDestino, fuenteEncriptada,
			 * context);
			 * 
			 * if (!pServicio.equals("EasyServerUp") &&
			 * !pServicio.equals("EasyServerRecovery")) {
			 * EasyUtilidades.BorrarBD(nombreBDFuente, context); }
			 * 
			 * if (pServicio.equals("EasyServerRecovery")) {
			 * EasyUtilidades.copyFile(context
			 * .getDatabasePath(nombreBDDestino), new File(
			 * GlobaG.pathArchivosSync + "/" + GlobaG.DATABASE_NAME)); }
			 */
			return true;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public static String NombreBDEncriptada(String nombreBD) {
		if (GlobaG.bdEncriptada) {
			return "C" + nombreBD;
		} else {
			return nombreBD;
		}
	}

	public static void BorrarBD(String nombreBD, Context context) {
		try {
			File databaseFile = context.getDatabasePath(nombreBD);
			databaseFile.delete();
		} catch (Exception ex) {

		}

	}

	public static boolean TipoConnectionWiFi(Context pContext) {

		ConnectivityManager connMgr = (ConnectivityManager) pContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		return false;
		// return networkInfo.isConnected();

	}

	public static boolean descargarArchivo(FileSales pFileSales) {
		boolean _descargarArchivo = false;
		try {
			String _pathRelative = pFileSales.getFile_path_relative();
			_pathRelative = _pathRelative.replace('\\', '/');

			String pathRelative = GlobaG.pathArchivosEasy + "/down"
					+ _pathRelative;
			EasyUtilidades.CrearDirectorioEasySales(pathRelative);

			// obtener archivo
			File file = new File(pFileSales.PathFull());

			if (file.exists()) {

				// verificar tamaño del archivo
				// if ((pFileSales.getFile_size() !=
				// file.length())||(file.lastModified()!=
				// Funciones.getMilliseconds(pFileSales.getFile_date())))
				if ((pFileSales.getFile_size() != file.length())) {
					_descargarArchivo = true;
				}
			} else {
				_descargarArchivo = true;
			}

			// Log.i("info",
			// "evaluar tamano achivo-"
			// + pFileSales.getFile_size()
			// + "-"
			// + file.length()
			// + "-"
			// + file.getPath()
			// + "-"
			// + file.getName()
			// + "-"
			// + file.lastModified()
			// + "-"
			// + Funciones.getMilliseconds(pFileSales
			// .getFile_date()) + "-" + _descargarArchivo);

		} catch (Exception ex) {
			_descargarArchivo = true;
		}

		return _descargarArchivo;

	}

	public static void CrearDirectorioEasySales(String pPath) {

		try {
			File databaseDirectory = new File(pPath + "/");
			MakeDirectory(databaseDirectory);
		} catch (Exception ex) {

			// Log.i("info", "creación directorios easysales:" +
			// ex.getMessage());

		}

	}

	public static GlobaG InformacionGloabaG(ItaloDBAdapter italoDBAdapter) {
		GlobaG gGlobaG = new GlobaG();
		Cursor cursor = italoDBAdapter.getAllConfiguracionAplicacion();
		Log.i("info", "despuesta de respuesta");
		// startManagingCursor(cursor);
		if (cursor.moveToFirst()) {
			gGlobaG.setLoginDM(cursor.getString(cursor
					.getColumnIndex(ItaloDBAdapter.esc_configuracion_aplicacion_login_dm)));
			gGlobaG.setDeviceIdDM(cursor.getString(cursor
					.getColumnIndex(ItaloDBAdapter.esc_configuracion_aplicacion_device_id_dm)));
			gGlobaG.setNombreUsuario(cursor.getString(cursor
					.getColumnIndex(ItaloDBAdapter.esc_configuracion_aplicacion_nombre_usuario)));
			gGlobaG.setNombreEmpresa(cursor.getString(cursor
					.getColumnIndex(ItaloDBAdapter.esc_configuracion_aplicacion_nombre_empresa)));
			gGlobaG.setPasswordDM(cursor.getString(cursor
					.getColumnIndex(ItaloDBAdapter.esc_configuracion_aplicacion_password_dm)));
			gGlobaG.setEasyAsignacionId(Integer.toString(cursor.getInt(cursor
					.getColumnIndex(ItaloDBAdapter.esc_configuracion_aplicacion_easy_asignacion_id))));
			// gGlobaG.setAgente_vendedor(gGlobaG.getLoginDM().substring(3, 6));
			gGlobaG.setAgente_vendedor(gGlobaG.getLoginDM());
		}
		cursor.close();

		return gGlobaG;
	}

	public static String logHeap() {
		Double allocated = new Double(Debug.getNativeHeapAllocatedSize())
				/ new Double((1048576));
		Double available = new Double(Debug.getNativeHeapSize()) / 1048576.0;
		Double free = new Double(Debug.getNativeHeapFreeSize()) / 1048576.0;
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);

		Log.d("info2", "debug. =================================");
		Log.d("info2", "debug.heap native: allocated " + df.format(allocated)
				+ "MB of " + df.format(available) + "MB (" + df.format(free)
				+ "MB free)");
		Log.d("info2",
				"debug.memory: allocated: "
						+ df.format(new Double(Runtime.getRuntime()
								.totalMemory() / 1048576))
						+ "MB of "
						+ df.format(new Double(
								Runtime.getRuntime().maxMemory() / 1048576))
						+ "MB ("
						+ df.format(new Double(Runtime.getRuntime()
								.freeMemory() / 1048576)) + "MB free)");

		String _cadena = "debug. =================================\n"
				+ "debug.heap native: allocated "
				+ df.format(allocated)
				+ "MB of "
				+ df.format(available)
				+ "MB ("
				+ df.format(free)
				+ "MB free)"
				+ "\n"
				+ "debug.memory: allocated: "
				+ df.format(new Double(
						Runtime.getRuntime().totalMemory() / 1048576))
				+ "MB of "
				+ df.format(new Double(
						Runtime.getRuntime().maxMemory() / 1048576))
				+ "MB ("
				+ df.format(new Double(
						Runtime.getRuntime().freeMemory() / 1048576))
				+ "MB free) \n";

		return _cadena;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	public static Bitmap decodeSampledBitmapFromResource(String strPath,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(strPath, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(strPath, options);
	}

}
