package easysync;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.italo_view.GlobaG;

import servicios.SincronismoBaseDatosServicio;
import servicios.SincronismoBaseDatosServicioImpl;
import utilidades.EasyUtilidades;
import utilidades.EasyZip;

import bd_utilidades.EasySyncDB;
import bd_utilidades.ItaloDBAdapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

@SuppressLint("ShowToast")
public class EasySync {
	EasySyncDB easySyncDB;
	Context context;
	String usuario;
	String password;
	String deviceId;
	int version;
	int revision;
	int compilacion;
	
	boolean syncDatos = true;

	public boolean ObtenerNuevaVersion = false;

	// TextView tvMensaje;

	private String easySyncEstado = "SINCRONIZANDO";
	private String easySyncMensaje = "";
	private Date easySyncFechaHora = new Date();
	GlobaG gGlobal;

	public void RegistrarEvento(String _resultado, String _mensaje) {
		easySyncEstado = _resultado;
		easySyncMensaje = _mensaje;
		easySyncFechaHora = new Date(System.currentTimeMillis());

		//Log.i("info", "anunciar estado-"+syncDatos+"//"+easySyncEstado+"//"+easySyncMensaje);
		
		if (syncDatos)
		{
			if (_resultado.indexOf("ERROR") != -1
					|| _mensaje.indexOf("ERROR") != -1) {
				gGlobal.ultimoRequerimientoSincronismo.setObservacion(_mensaje);
			}
		}
		else
		{
			if (_resultado.indexOf("ERROR") != -1
					|| _mensaje.indexOf("ERROR") != -1) {
				gGlobal.ultimoRequerimientoSincronismoFile.setObservacion(_mensaje);
			}
			
		}

		AnunciarEstado();
	}

	private boolean CrearRecovery() throws Exception {
		synchronized (ItaloDBAdapter.Lock) {
			Boolean _resultado = false;

			if (gGlobal.bdEncriptada) {
				EasyUtilidades.EncriptarBD("EasyServerRecovery", "N", context);
			}

			EasyZip _easyZip = new EasyZip();
			_resultado = _easyZip.ZippingFile(gGlobal.DATABASE_PATH
					+ gGlobal.DATABASE_NAME, gGlobal.pathArchivosSync + "/"
					+ gGlobal.NombreRecovery);
			/*
			_resultado = _easyZip.ZippingFile(gGlobal.DATABASE_PATH
					+ gGlobal.DATABASE_NAME_NOVEDADES_UP, gGlobal.pathArchivosSync + "/"
					+ gGlobal.NombreRecoveryUp);
			*/
			return _resultado;
		}

	}

	private void AnunciarEstado() {
		Intent intent;
		if (this.syncDatos)
		{
			intent = new Intent(EasySyncService.NUEVO_ESTADO_EASYSYNC);
			intent.putExtra("estado", easySyncEstado);
			intent.putExtra("mensaje", easySyncMensaje);
			intent.putExtra("fechahora", easySyncFechaHora.toLocaleString());
			intent.putExtra("syncdatos", this.syncDatos);
			context.sendBroadcast(intent);
		}
		else
		{
			intent = new Intent(EasySyncService.NUEVO_ESTADO_EASYSYNC);
			intent.putExtra("estado", easySyncEstado);
			intent.putExtra("mensaje", easySyncMensaje);
			intent.putExtra("fechahora", easySyncFechaHora.toLocaleString());
			intent.putExtra("syncdatos", this.syncDatos);
			context.sendBroadcast(intent);
			
		}
	}

	public EasySync(Context _context, GlobaG GlobaG) {
		context = _context;
		gGlobal = GlobaG;
		// tvMensaje = _tvMensaje;
		// tvMensaje.setText("");
	}

	public EasySync(Context _context, boolean _syncDatos, GlobaG GlobaG) {
		context = _context;
		this.syncDatos = _syncDatos;
		this.gGlobal = GlobaG;
		// tvMensaje = _tvMensaje;
		// tvMensaje.setText("");
	}
	
	public boolean AplicarNovedadesDown(boolean pAplicarNovedadesDown)
			throws Exception {

		boolean aplicarNovedadesDown = false;

		// try {
		// //Log.i("Info", "aplicar novedades 1");

		AbrirBaseDatos();
		// //Log.i("Info", "aplicar novedades 2");
		easySyncDB.attachBaseDatos("Down");
		easySyncDB.attachBaseDatos("Up");
		// //Log.i("Info", "aplicar novedades 3");

		if (pAplicarNovedadesDown) {
			// borrar registros de tablas sin registros
			BorrarRegistrosTablasSinRegistros();
		}

		// registra novedades en tablas
		TablasConNovedades();

		BorrarTablasUpConNovedadesSubidas();

		/*
		 * ////Log.i("info", "antes de compactar"); easySyncDB.execute("VACUUM");
		 * ////Log.i("info", "despues de compactar");
		 */

		aplicarNovedadesDown = true;

		// //Log.i("Info", "aplicar novedades 4");
		CerrarBaseDatos();
		return aplicarNovedadesDown;
		// } catch (Exception ex) {
		// ////Log.i("info",
		// "error en aplicar novedades. Error:" + ex.getMessage());
		// return aplicarNovedadesDown;
		// }
	}

	public boolean GenerarNovedadesUp() {
		boolean generarNovedadesUp = false;

		// //Log.i("Info", "aplicar novedades 1");

		AbrirBaseDatos();
		// //Log.i("Info", "aplicar novedades 2");
		easySyncDB.attachBaseDatos("Up");
		// //Log.i("Info", "aplicar novedades 3");

		// registra novedades en tablas up
		generarNovedadesUp = TablasUpConNovedades();

		// //Log.i("Info", "aplicar novedades 4");
		CerrarBaseDatos();
		return generarNovedadesUp;
	}

	private boolean TablasUpConNovedades() {
		// //Log.i("info", "obtener tablas");
		try {
			Cursor TablasNovedadesCursor;
			String CadSqlDelete = "delete from [tabla] where exists (select *  from dbUp.[tabla]  where [tabla].id= dbUp.[tabla].id)";
			// String CadSqlDeleteNovedades =
			String CadSqlInsertNovedades = "insert into dbUp.[tabla] "
					+ " select null, ca.device_id_dm, ca.login_dm, ca.easy_asignacion_id, t.* "
					+ " from esc_configuracion_aplicacion as ca, [tabla] as t ";
			String CadSql;

			TablasNovedadesCursor = easySyncDB
					.getAllCTablaSincronizacionUpItemsCursor();

			if (TablasNovedadesCursor.moveToFirst())
				do {
					// //Log.i("info", "primer dato");
					String _nombreTabla = TablasNovedadesCursor
							.getString(TablasNovedadesCursor
									.getColumnIndex(easySyncDB.CTABLA_SINCRONIZACION_DOWN_TABLA));
					// //Log.i("info", "tabla-" + _nombreTabla);

					CadSql = CadSqlInsertNovedades.replace("[tabla]",
							_nombreTabla);
					// //Log.i("info", CadSql);
					easySyncDB.execute(CadSql);

					CadSql = CadSqlDelete.replace("[tabla]", _nombreTabla);
					// //Log.i("info", CadSql);
					easySyncDB.execute(CadSql);

					// //Log.i("info", "novedades registradas-" + _nombreTabla);

				} while (TablasNovedadesCursor.moveToNext());

			TablasNovedadesCursor.close();

			return true;
		} catch (Exception ex) {
			// //Log.i("info", "error en cargar novedades up." + ex.getMessage());
			return false;
		}
	}

	public void TablasConNovedades() {
		// //Log.i("info", "obtener tablas");
		Cursor TablasNovedadesCursor;
		String CadSqlDelete = "delete from [tabla] where exists (select *  from dbDown.[tabla]_delete  where [tabla].id= dbDown.[tabla]_delete.id)";
		// String CadSqlDeleteNovedades =
		// "delete from [tabla] where exists (select *  from dbDown.[tabla]  where [tabla].id= dbDown.[tabla].id)";
		String CadSqlInsertNovedades = "insert into [tabla] select *  from dbDown.[tabla]";
		String CadSql;

		TablasNovedadesCursor = easySyncDB
				.getAllCTablaSincronizacionDownItemsCursor();

		if (TablasNovedadesCursor.moveToFirst())
			do {

				// //Log.i("info", "primer dato");
				String _nombreTabla = TablasNovedadesCursor
						.getString(TablasNovedadesCursor
								.getColumnIndex(easySyncDB.CTABLA_SINCRONIZACION_DOWN_TABLA));
				// //Log.i("info", "tabla-" + _nombreTabla);

				CadSql = "select count(*) from dbDown." + _nombreTabla + "_delete";
				Cursor _cursor = easySyncDB.getAllItemCursor(CadSql, null);
				_cursor.moveToFirst();
				int _numeroRegistros = _cursor.getInt(0);
				_cursor.close();

				if (_numeroRegistros > 0) {
					CadSql = CadSqlDelete.replace("[tabla]", _nombreTabla);
					Log.i("info", "cadsql-" + CadSqlDelete + "//" + CadSql);
					Log.i("info", CadSql);
					easySyncDB.execute(CadSql);
				}
				
				CadSql = "select count(*) from dbDown." + _nombreTabla;
				_cursor = easySyncDB.getAllItemCursor(CadSql, null);
				_cursor.moveToFirst();
				_numeroRegistros = _cursor.getInt(0);
				_cursor.close();

				if (_numeroRegistros > 0) {

					CadSql = CadSqlInsertNovedades.replace("[tabla]",
							_nombreTabla);
					// //Log.i("info", CadSql);
					easySyncDB.execute(CadSql);

					// //Log.i("info", "novedades registradas-" + _nombreTabla);
				}

				
			} while (TablasNovedadesCursor.moveToNext());

		TablasNovedadesCursor.close();

	}

	public ArrayList<String> TablasDown() throws Exception {
		// //Log.i("info", "obtener tablas");
		ArrayList<String> _listTablasDown = new ArrayList<String>();
		AbrirBaseDatos();
		Cursor TablasNovedadesCursor;
		String CadSql;

		TablasNovedadesCursor = easySyncDB
				.getAllCTablaSincronizacionDownTodasItemsCursor();

		if (TablasNovedadesCursor.moveToFirst())
			do {
				String _nombreTabla = TablasNovedadesCursor
						.getString(TablasNovedadesCursor
								.getColumnIndex(easySyncDB.CTABLA_SINCRONIZACION_DOWN_TABLA));

				_listTablasDown.add(_nombreTabla);

			} while (TablasNovedadesCursor.moveToNext());

		TablasNovedadesCursor.close();
		CerrarBaseDatos();

		return _listTablasDown;

	}

	public void BorrarTablasUpConNovedadesSubidas() {
		// //Log.i("info", "obtener tablas");
		Cursor TablasNovedadesCursor;
		String CadSqlDelete = "delete from dbUp.[tabla] where exists (select *  from dbDown.[tabla]_delete  where dbUp.[tabla].idr= dbDown.[tabla]_delete.idr)";

		String CadSql;

		// TablasNovedadesCursor = easySyncDB
		// .getAllCTablaSincronizacionUpNovItemsCursor();

		easySyncDB.attachBaseDatos("Up");

		// TablasNovedadesCursor = easySyncDB
		// .getAllCTablaSincronizacionUpItemsCursor();
		TablasNovedadesCursor = easySyncDB
				.getAllCTablaSincronizacionUpNovItemsCursor();

		if (TablasNovedadesCursor.moveToFirst())
			do {
				// //Log.i("info", "primer dato");
				String _nombreTabla = TablasNovedadesCursor
						.getString(TablasNovedadesCursor
								.getColumnIndex(easySyncDB.CTABLA_SINCRONIZACION_DOWN_TABLA));
				// //Log.i("info", "tabla-" + _nombreTabla);

				CadSql = CadSqlDelete.replace("[tabla]", _nombreTabla);
				// //Log.i("info", CadSql);
				easySyncDB.execute(CadSql);

				// //Log.i("info", "novedades eliminadas-" + _nombreTabla);

			} while (TablasNovedadesCursor.moveToNext());

		TablasNovedadesCursor.close();

	}

	public void BorrarRegistrosTablasSinRegistros() {
		Cursor TablasSinRegistrosCursor;

		TablasSinRegistrosCursor = easySyncDB
				.getAllCTablaSinRegistroItemsCursor();

		if (TablasSinRegistrosCursor.moveToFirst())
			do {
				String _nombreTabla = TablasSinRegistrosCursor
						.getString(TablasSinRegistrosCursor
								.getColumnIndex(easySyncDB.CTABLA_SIN_REGISTRO_NOMBRE_TABLA));

				// //Log.i("info", "sin registros: " + _nombreTabla);
				easySyncDB.execute("delete from " + _nombreTabla);

			} while (TablasSinRegistrosCursor.moveToNext());

		/*
		 * if (TablasSinRegistrosCursor.getCount() > 0) { ////Log.i("info",
		 * "antes de compactar"); easySyncDB.execute("VACUUM"); ////Log.i("info",
		 * "despues de compactar"); }
		 */

		TablasSinRegistrosCursor.close();

	}

	public void AplicarNovedadesAuxiliarEasyServer() {

		//Log.i("info", "aplicar novedades auxiliar-incio");

		AbrirBaseDatos();

		easySyncDB.attachBaseDatos("Down");
		easySyncDB.attachBaseDatos("Up");

		try {
			Cursor SqlAuxiliarCursor;

			SqlAuxiliarCursor = easySyncDB
					.getAllCAuxiliarEasyServerItemsCursor("SQL");

			if (SqlAuxiliarCursor.moveToFirst())
				do {
					String _sqlAuxiliar = SqlAuxiliarCursor
							.getString(SqlAuxiliarCursor
									.getColumnIndex(easySyncDB.ESC_AUXILIAR_EASY_SERVER_TEXTO));
					//Log.i("info", "SQL AUXILIAR:" + _sqlAuxiliar);

					try {
						easySyncDB.execute(_sqlAuxiliar);
					} catch (Exception ex) {
						//Log.i("info", "SQL AUXILIAR ERROR:" + ex.getMessage());

					}

				} while (SqlAuxiliarCursor.moveToNext());
			SqlAuxiliarCursor.close();
		} finally {

			easySyncDB.execute("delete from "
					+ easySyncDB.DATABASE_TABLE_ESC_AUXILIAR_EASY_SERVER
					+ " where tipo='SQL'");
		}

		/*
		 * ////Log.i("info", "antes de compactar"); easySyncDB.execute("VACUUM");
		 * ////Log.i("info", "despues de compactar");
		 */

		CerrarBaseDatos();
		//Log.i("info", "aplicar novedades auxiliar-fin");

	}

	private void AbrirBaseDatos() {
		easySyncDB = new EasySyncDB(context);
		easySyncDB.open();
	}

	private void CerrarBaseDatos() {
		try {
//			easySyncDB.close();

		} catch (Exception ex) {

		}
	}

	private boolean AttachBaseDatosNovedades() {

		AbrirBaseDatos();
		easySyncDB.attachBaseDatos("Down");
		easySyncDB.attachBaseDatos("Up");
		CerrarBaseDatos();
		return true;

	}

	public Respuesta DescargarBaseDatos(
			RequerimientoSincronismo pRequerimientoSincronismo,
			SincronismoBaseDatosServicio pSincronismoBaseDatosServicio,
			String pServicio, Respuesta pRespuesta)
			throws ClientProtocolException, JSONException, IOException,
			Exception {

		String sincronismoInicial;
		if (pRequerimientoSincronismo.getSincronismoInicial()) {
			sincronismoInicial = "S";
		} else {
			sincronismoInicial = "N";

		}

		Respuesta _respuesta = pSincronismoBaseDatosServicio.DownLoadBaseDatos(
				pRespuesta.getLogId(), pRequerimientoSincronismo.getDeviceId(),
				pServicio, sincronismoInicial);

		pRespuesta.setResultado(_respuesta.getResultado());
		pRespuesta.setObservacion(_respuesta.getObservacion());
		if (pRespuesta.getResultado()) {
			easySyncDB = new EasySyncDB(context);

			pRespuesta.setResultado(easySyncDB.copyDataBase());

			if (pRespuesta.getResultado() && gGlobal.bdEncriptada) {
				pRespuesta.setResultado(EasyUtilidades.EncriptarBD(pServicio,
						sincronismoInicial, context));
			}

		}

		return pRespuesta;
	}

	public Respuesta SubirBaseDatos(
			RequerimientoSincronismo pRequerimientoSincronismo,
			SincronismoBaseDatosServicio pSincronismoBaseDatosServicio,
			String pServicio, Respuesta pRespuesta)
			throws ClientProtocolException, IOException, JSONException,
			Exception {

		// //Log.i("info", "transferencia up");
		Respuesta _respuesta = pSincronismoBaseDatosServicio.UpLoadBaseDatos(
				pRespuesta.getLogId(), pRequerimientoSincronismo.getDeviceId(),
				pServicio);

		pRespuesta.setResultado(_respuesta.getResultado());
		pRespuesta.setObservacion(_respuesta.getObservacion());

		// //Log.i("info", "fin transferencia up");

		return pRespuesta;
	}

	public Respuesta SubirRecoveryBaseDatos(
			RequerimientoSincronismo pRequerimientoSincronismo,
			SincronismoBaseDatosServicio pSincronismoBaseDatosServicio,
			String pServicio, Respuesta pRespuesta)
			throws ClientProtocolException, IOException, JSONException,
			Exception {

		// //Log.i("info", "transferencia recovery");
		Respuesta _respuesta = pSincronismoBaseDatosServicio
				.UpLoadRecoveryBaseDatos(pRespuesta.getLogId(),
						pRequerimientoSincronismo.getDeviceId(), pServicio);

		pRespuesta.setResultado(_respuesta.getResultado());
		pRespuesta.setObservacion(_respuesta.getObservacion());

		// //Log.i("info", "fin transferencia recovery");

		return pRespuesta;
	}

	public boolean SincronismoInicial(String pUsuario, String pPassword,
			String pDeviveId, int pVersion, int pRevision, int pCompilacion) {
		usuario = pUsuario;
		password = pPassword;
		deviceId = pDeviveId;
		version = pVersion;
		revision = pRevision;
		compilacion = pCompilacion;

		SincronismoBaseDatosServicio sincronismoBaseDatosServicio = new SincronismoBaseDatosServicioImpl(gGlobal);

		RequerimientoSincronismo requerimientoSincronismo = new RequerimientoSincronismo();

		gGlobal.ultimoRequerimientoSincronismo = requerimientoSincronismo;

		requerimientoSincronismo.setDeviceId(deviceId);
		requerimientoSincronismo.setLogin(usuario);
		requerimientoSincronismo.setPassword(password);
		requerimientoSincronismo.setRevision(revision);
		requerimientoSincronismo.setSincronismoInicial(true);
		requerimientoSincronismo.setVersion(version);
		requerimientoSincronismo.setCompilacion(compilacion);
		requerimientoSincronismo.setBaseDatosES("");
		requerimientoSincronismo.setLogId(0);

		// tvMensaje.setText("Solicitud sincronismo");

		// tvMensaje.setText("Solicitud sincronismo. " +
		// e.getLocalizedMessage());

		Respuesta respuesta = new Respuesta();
		try {
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismoURL(
					requerimientoSincronismo, "UrlEasyServer");

			//Log.i("info", "respuesta url:" + respuesta.getObservacion());
			if (respuesta.getObservacion().indexOf("ERROR") == -1) {
				RegistrarEvento("SINCRONIZANDO", "URL SATISFACTORIA. "
						+ respuesta.getObservacion());
				gGlobal.UrlEasyServer = respuesta.getObservacion();
			} else {
				RegistrarEvento("ERROR",
						"URL FALLIDA. " + respuesta.getObservacion());
				return false;
			}

		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "URL FALLIDA EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. JSONException." + e.getMessage()
							+ ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "URL FALLIDA EXCEPTION. JSONException." + e.getMessage()
			// + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. IOException." + e.getMessage()
							+ ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "URL FALLIDA EXCEPTION. IOException." + e.getMessage()
			// + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "URL FALLIDA EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento("ERROR",
					"URL FALLIDA EXCEPTION. Exception." + e.getMessage() + ". "
							+ e.getCause() + ". " + e.getLocalizedMessage());
			// //Log.i("info", "URL FALLIDA EXCEPTION. Exception." +
			// e.getMessage()
			// + ". " + e.getCause() + ". " + e.getLocalizedMessage());
			return false;
		}

		respuesta = new Respuesta();
		try {
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismo(
					requerimientoSincronismo, "RequerimientoSincronismo");

			if (respuesta.getResultado() == true) {
				RegistrarEvento("SINCRONIZANDO", "REQUERIMIENTO SINCRONISMO. "
						+ respuesta.getObservacion());
			} else {
				RegistrarEvento("ERROR", "REQUERIMIENTO SINCRONISMO FALLIDA. "
						+ respuesta.getObservacion());
				return false;
			}

		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"REQUERIMIENTO SINCRONISMO EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"REQUERIMIENTO SINCRONISMO EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"REQUERIMIENTO SINCRONISMO EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"REQUERIMIENTO SINCRONISMO FALLIDA EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento(
					"ERROR",
					"REQUERIMIENTO SINCRONISMO FALLIDA EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		requerimientoSincronismo.setLogId(respuesta.getLogId());
		requerimientoSincronismo.setBaseDatosES(respuesta.getBaseDatosES());

		//Log.i("info", "observacion respuesta. " + respuesta.getObservacion());
		//Log.i("info", "log id. " + respuesta.getLogId());
		//Log.i("info", "base datos. " + respuesta.getBaseDatosES());

		ObtenerNuevaVersion = gGlobal
				.getNuevaVersion(respuesta.getObservacion());

		TransferirArchivos(sincronismoBaseDatosServicio,
				requerimientoSincronismo, respuesta, "Trace");
		// tvMensaje.setText("Descargar base de datos de novedades up vacia");

		// obtener base datos novedades up vacia
		try {
			respuesta = DescargarBaseDatos(requerimientoSincronismo,
					sincronismoBaseDatosServicio, "BaseDatosUp", respuesta);

			if (respuesta.getResultado() == true) {
				RegistrarEvento(
						"SINCRONIZANDO",
						"DESCARGA BASE DE DATOS UP SATISFACTORIA. "
								+ respuesta.getObservacion());
			} else {
				RegistrarEvento("ERROR", "DESCARGA BASE DE DATOS UP FALLIDA. "
						+ respuesta.getObservacion());
				return false;
			}

		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS UP EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS UP EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS UP EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS UP EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS UP EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS UP EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS UP EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS UP EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		// //Log.i("info", "observacion respuesta. " +
		// respuesta.getObservacion());
		// //Log.i("info", "log id. " + respuesta.getLogId());

		// tvMensaje.setText("Descargar base de datos de novedades down vacia");

		// obtener base datos de novedades down
		/*
		 * if (respuesta.getResultado() == true) {
		 * 
		 * respuesta = DescargarBaseDatos(requerimientoSincronismo,
		 * sincronismoBaseDatosServicio, "BaseDatosDown", respuesta);
		 * 
		 * } else { Toast.makeText(context, respuesta.getObservacion(),
		 * Toast.LENGTH_SHORT);
		 * tvMensaje.setText("Descargar base de datos de novedades down vacia. "
		 * + respuesta.getObservacion()); return false; }
		 */

		// //Log.i("info", "observacion respuesta. " +
		// respuesta.getObservacion());
		// //Log.i("info", "log id. " + respuesta.getLogId());

		// tvMensaje.setText("Descargar base de datos down");

		try {
			respuesta = DescargarBaseDatos(requerimientoSincronismo,
					sincronismoBaseDatosServicio, "EasyServerDown", respuesta);
			if (respuesta.getResultado() == true) {
				RegistrarEvento("SINCRONIZANDO",
						"DESCARGA BASE DE DATOS DOWN SATISFACTORIA. "
								+ respuesta.getObservacion());
			} else {
				RegistrarEvento(
						"ERROR",
						"DESCARGA BASE DE DATOS DOWN FALLIDA. "
								+ respuesta.getObservacion());
				return false;
			}
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		// tvMensaje.setText("Commit down");

		try {
			respuesta = sincronismoBaseDatosServicio
					.CommitSincronismo(requerimientoSincronismo);
			if (respuesta.getResultado() == true) {
				RegistrarEvento(
						"SINCRONIZANDO",
						"FIN SINCRONISMO SATISFACTORIA. "
								+ respuesta.getObservacion());
			} else {
				RegistrarEvento("ERROR", "FIN SINCRONISMO FALLIDA. "
						+ respuesta.getObservacion());
			}
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"COMMIT EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "COMMIT EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		try {
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismo(
					requerimientoSincronismo, "FinSincronismo");
			if (respuesta.getResultado() == true) {
				RegistrarEvento(
						"SINCRONIZANDO",
						"FIN SINCRONISMO SATISFACTORIA. "
								+ respuesta.getObservacion());
			} else {
				RegistrarEvento("ERROR", "FIN SINCRONISMO FALLIDA. "
						+ respuesta.getObservacion());
			}
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// Toast.makeText(context, R.string.messageProblemasSincronizacion
			// + ". " + e.getLocalizedMessage(), Toast.LENGTH_SHORT);
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		if (gGlobal.bdEncriptada) {
			try {
				EasyUtilidades.EncriptarBD("EasyServerRecovery", "N", context);
				EasyUtilidades.BorrarArchivos(gGlobal.pathArchivosSync + "/");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (respuesta.getResultado() == true) {
			RegistrarEvento("OK", "OK");
			// RegistrarEvento("ERROR", "ERROR OK");
		} else {
			RegistrarEvento("ERROR FIN SINCRONISMO ",
					"FALLA EN FIN SINCRONISMO");
		}

		return respuesta.getResultado();

	}

	public boolean SincronismoInicialV2(String pUsuario, String pPassword,
			String pDeviveId, int pVersion, int pRevision) {
		usuario = pUsuario;
		password = pPassword;
		deviceId = pDeviveId;
		version = pVersion;
		revision = pRevision;

		SincronismoBaseDatosServicio sincronismoBaseDatosServicio = new SincronismoBaseDatosServicioImpl(gGlobal);

		RequerimientoSincronismo requerimientoSincronismo = new RequerimientoSincronismo();

		requerimientoSincronismo.setDeviceId(deviceId);
		requerimientoSincronismo.setLogin(usuario);
		requerimientoSincronismo.setPassword(password);
		requerimientoSincronismo.setRevision(revision);
		requerimientoSincronismo.setSincronismoInicial(true);
		requerimientoSincronismo.setVersion(version);
		requerimientoSincronismo.setBaseDatosES("");
		requerimientoSincronismo.setLogId(0);

		Respuesta respuesta = new Respuesta();
		try {
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismo(
					requerimientoSincronismo, "RequerimientoSincronismoV2");

			if (respuesta.getResultado() == true) {
				RegistrarEvento(
						"SINCRONIZANDO",
						"AUTENTICACION SATISFACTORIA. "
								+ respuesta.getObservacion());
			} else {
				RegistrarEvento("ERROR",
						"AUTENTICACION FALLIDA. " + respuesta.getObservacion());
				return false;
			}

		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		requerimientoSincronismo.setLogId(respuesta.getLogId());
		requerimientoSincronismo.setBaseDatosES(respuesta.getBaseDatosES());

		// //Log.i("info", "observacion respuesta. " +
		// respuesta.getObservacion());
		// //Log.i("info", "log id. " + respuesta.getLogId());
		// //Log.i("info", "base datos. " + respuesta.getBaseDatosES());

		ObtenerNuevaVersion = gGlobal
				.getNuevaVersion(respuesta.getObservacion());

		try {
			respuesta = DescargarBaseDatos(requerimientoSincronismo,
					sincronismoBaseDatosServicio, "BaseDatosUp", respuesta);

			if (respuesta.getResultado() == true) {
				RegistrarEvento(
						"SINCRONIZANDO",
						"DESCARGA BASE DE DATOS UP SATISFACTORIA. "
								+ respuesta.getObservacion());
			} else {
				RegistrarEvento("ERROR", "DESCARGA BASE DE DATOS UP FALLIDA. "
						+ respuesta.getObservacion());
				return false;
			}

		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS UP EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS UP EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS UP EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS UP EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS UP EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS UP EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS UP EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS UP EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		// //Log.i("info", "observacion respuesta. " +
		// respuesta.getObservacion());
		// //Log.i("info", "log id. " + respuesta.getLogId());

		// //Log.i("info", "observacion respuesta. " +
		// respuesta.getObservacion());
		// //Log.i("info", "log id. " + respuesta.getLogId());

		try {
			respuesta = DescargarBaseDatos(requerimientoSincronismo,
					sincronismoBaseDatosServicio, "EasyServerDown", respuesta);
			if (respuesta.getResultado() == true) {
				RegistrarEvento("SINCRONIZANDO",
						"DESCARGA BASE DE DATOS DOWN SATISFACTORIA. "
								+ respuesta.getObservacion());
			} else {
				RegistrarEvento(
						"ERROR",
						"DESCARGA BASE DE DATOS DOWN FALLIDA. "
								+ respuesta.getObservacion());
				return false;
			}
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		try {
			respuesta = sincronismoBaseDatosServicio
					.CommitSincronismo(requerimientoSincronismo);
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"COMMIT EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "COMMIT EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		if (respuesta.getResultado() == true) {
			RegistrarEvento("COMMIT INICIAL", "COMMIT INICIAL");
		} else {
			RegistrarEvento("ERROR COMMIT", respuesta.getObservacion());
		}

		respuesta.setResultado(SincronismoNovedadV4(pUsuario, pPassword,
				pDeviveId, pVersion, pRevision));

		return respuesta.getResultado();

	}

	public boolean SincronismoInicialV3(String pUsuario, String pPassword,
			String pDeviveId, int pVersion, int pRevision, int pCompilacion) {
		usuario = pUsuario;
		password = pPassword;
		deviceId = pDeviveId;
		version = pVersion;
		revision = pRevision;
		compilacion = pCompilacion;

		SincronismoBaseDatosServicio sincronismoBaseDatosServicio = new SincronismoBaseDatosServicioImpl(gGlobal);

		RequerimientoSincronismo requerimientoSincronismo = new RequerimientoSincronismo();

		gGlobal.ultimoRequerimientoSincronismo = requerimientoSincronismo;

		requerimientoSincronismo.setDeviceId(deviceId);
		requerimientoSincronismo.setLogin(usuario);
		requerimientoSincronismo.setPassword(password);
		requerimientoSincronismo.setRevision(revision);
		requerimientoSincronismo.setSincronismoInicial(true);
		requerimientoSincronismo.setVersion(version);
		requerimientoSincronismo.setCompilacion(compilacion);
		requerimientoSincronismo.setBaseDatosES("");
		requerimientoSincronismo.setLogId(0);

		Respuesta respuesta = new Respuesta();
		try {
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismoURL(
					requerimientoSincronismo, "UrlEasyServer");

			if (respuesta.getObservacion().indexOf("ERROR") == -1) {
				RegistrarEvento("SINCRONIZANDO", "URL SATISFACTORIA. "
						+ respuesta.getObservacion());
				gGlobal.UrlEasyServer = respuesta.getObservacion();
			} else {
				RegistrarEvento("ERROR",
						"URL FALLIDA. " + respuesta.getObservacion());
				return false;
			}

		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "URL FALLIDA EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. JSONException." + e.getMessage()
							+ ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "URL FALLIDA EXCEPTION. JSONException." + e.getMessage()
			// + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. IOException." + e.getMessage()
							+ ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "URL FALLIDA EXCEPTION. IOException." + e.getMessage()
			// + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "URL FALLIDA EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento("ERROR",
					"URL FALLIDA EXCEPTION. Exception." + e.getMessage() + ". "
							+ e.getCause() + ". " + e.getLocalizedMessage());
			// //Log.i("info", "URL FALLIDA EXCEPTION. Exception." +
			// e.getMessage()
			// + ". " + e.getCause() + ". " + e.getLocalizedMessage());
			return false;
		}

		respuesta = new Respuesta();

		respuesta = generarSolicitudSincronismo(requerimientoSincronismo,
				"RequerimientoSincronismoV4", "AUTENTICACION");

		if (respuesta.getResultado() == false)
			return false;

		requerimientoSincronismo.setLogId(respuesta.getLogId());
		requerimientoSincronismo.setBaseDatosES(respuesta.getBaseDatosES());

		// //Log.i("info", "observacion respuesta. " +
		// respuesta.getObservacion());
		// //Log.i("info", "log id. " + respuesta.getLogId());
		// //Log.i("info", "base datos. " + respuesta.getBaseDatosES());

		TransferirArchivos(sincronismoBaseDatosServicio,
				requerimientoSincronismo, respuesta, "Trace");

		respuesta = generarSolicitudSincronismo(requerimientoSincronismo,
				"RequerimientoEasyServer", "REQUERIMIENTO EASYSERVER");
		if (respuesta.getResultado() == false)
			return false;

		ObtenerNuevaVersion = gGlobal
				.getNuevaVersion(respuesta.getObservacion());

		try {
			respuesta = DescargarBaseDatos(requerimientoSincronismo,
					sincronismoBaseDatosServicio, "BaseDatosUp", respuesta);

			if (respuesta.getResultado() == true) {
				RegistrarEvento(
						"SINCRONIZANDO",
						"DESCARGA BASE DE DATOS UP SATISFACTORIA. "
								+ respuesta.getObservacion());
			} else {
				RegistrarEvento("ERROR", "DESCARGA BASE DE DATOS UP FALLIDA. "
						+ respuesta.getObservacion());
				return false;
			}

		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS UP EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS UP EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS UP EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS UP EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS UP EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS UP EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS UP EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS UP EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		// //Log.i("info", "observacion respuesta. " +
		// respuesta.getObservacion());
		// //Log.i("info", "log id. " + respuesta.getLogId());

		while (true) {
			respuesta = generarSolicitudSincronismo(requerimientoSincronismo,
					"RequerimientoEasyServerEstado",
					"ESTADO REQUERIMIENTO EASYSERVER");

			if (respuesta.getResultado() == false)
				return false;

			if (respuesta.getResultado() == true) {
				if (respuesta.getObservacion().equals("OK")) {
					break;
				} else if (respuesta.getObservacion().equals("ERROR")) {
					RegistrarEvento("ERROR",
							"ESTADO REQUERIMIENTO EASYSERVER ERROR.");
					// //Log.i("info", "ESTADO REQUERIMIENTO EASYSERVER ERROR.");
					return false;
				}

			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				// //Log.i("info", "ERROR EN TIMER 1.");
			}

		}

		try {
			respuesta = DescargarBaseDatos(requerimientoSincronismo,
					sincronismoBaseDatosServicio,
					"RequerimientoEasyServerDown", respuesta);
			if (respuesta.getResultado() == true) {
				RegistrarEvento("SINCRONIZANDO",
						"DESCARGA BASE DE DATOS DOWN SATISFACTORIA. "
								+ respuesta.getObservacion());
			} else {
				RegistrarEvento(
						"ERROR",
						"DESCARGA BASE DE DATOS DOWN FALLIDA. "
								+ respuesta.getObservacion());
				return false;
			}
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		/*
		 * try { respuesta = sincronismoBaseDatosServicio
		 * .CommitSincronismo(requerimientoSincronismo); } catch
		 * (ClientProtocolException e) { RegistrarEvento( "ERROR",
		 * "COMMIT EXCEPTION. ClientProtocolException." + e.getMessage() + ". "
		 * + e.getCause() + ". " + e.getLocalizedMessage()); ////Log.i("info",
		 * "COMMIT EXCEPTION. ClientProtocolException." + e.getMessage() + ". "
		 * + e.getCause() + ". " + e.getLocalizedMessage()); return false; }
		 * catch (JSONException e) { RegistrarEvento( "ERROR",
		 * "DESCARGA BASE DE DATOS DOWN EXCEPTION. JSONException." +
		 * e.getMessage() + ". " + e.getCause() + ". " +
		 * e.getLocalizedMessage()); ////Log.i("info",
		 * "DESCARGA BASE DE DATOS DOWN EXCEPTION. JSONException." +
		 * e.getMessage() + ". " + e.getCause() + ". " +
		 * e.getLocalizedMessage()); return false; } catch (IOException e) {
		 * RegistrarEvento( "ERROR",
		 * "DESCARGA BASE DE DATOS DOWN EXCEPTION. IOException." +
		 * e.getMessage() + ". " + e.getCause() + ". " +
		 * e.getLocalizedMessage()); ////Log.i("info",
		 * "DESCARGA BASE DE DATOS DOWN EXCEPTION. IOException." +
		 * e.getMessage() + ". " + e.getCause() + ". " +
		 * e.getLocalizedMessage()); return false; } catch (URISyntaxException
		 * e) { RegistrarEvento( "ERROR",
		 * "DESCARGA BASE DE DATOS DOWN EXCEPTION. URISyntaxException." +
		 * e.getMessage() + ". " + e.getCause() + ". " +
		 * e.getLocalizedMessage()); ////Log.i("info",
		 * "DESCARGA BASE DE DATOS DOWN EXCEPTION. URISyntaxException." +
		 * e.getMessage() + ". " + e.getCause() + ". " +
		 * e.getLocalizedMessage()); return false; } catch (Exception e) {
		 * RegistrarEvento( "ERROR",
		 * "DESCARGA BASE DE DATOS DOWN EXCEPTION. Exception." + e.getMessage()
		 * + ". " + e.getCause() + ". " + e.getLocalizedMessage());
		 * ////Log.i("info", "DESCARGA BASE DE DATOS DOWN EXCEPTION. Exception." +
		 * e.getMessage() + ". " + e.getCause() + ". " +
		 * e.getLocalizedMessage()); return false; }
		 */

		respuesta = generarSolicitudSincronismo(requerimientoSincronismo,
				"RequerimientoCommit", "REQUERIMIENTO COMMIT");
		if (respuesta.getResultado() == false)
			return false;

		while (true) {
			respuesta = generarSolicitudSincronismo(requerimientoSincronismo,
					"RequerimientoCommitEstado", "ESTADO REQUERIMIENTO COMMIT");

			if (respuesta.getResultado() == false)
				return false;

			if (respuesta.getResultado() == true) {
				if (respuesta.getObservacion().equals("OK")) {
					break;
				} else if (respuesta.getObservacion().equals("ERROR")) {
					RegistrarEvento("ERROR",
							"ESTADO REQUERIMIENTO COMMIT ERROR.");
					// //Log.i("info", "ESTADO REQUERIMIENTO COMMIT ERROR.");
					return false;
				}

			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				// //Log.i("info", "ERROR EN TIMER 1.");
			}

		}

		try {
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismo(
					requerimientoSincronismo, "FinSincronismo");
			if (respuesta.getResultado() == true) {
				RegistrarEvento(
						"SINCRONIZANDO",
						"FIN SINCRONISMO SATISFACTORIA. "
								+ respuesta.getObservacion());
			} else {
				RegistrarEvento("ERROR", "FIN SINCRONISMO FALLIDA. "
						+ respuesta.getObservacion());
			}
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// Toast.makeText(context, R.string.messageProblemasSincronizacion
			// + ". " + e.getLocalizedMessage(), Toast.LENGTH_SHORT);
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		if (gGlobal.bdEncriptada) {
			try {
				EasyUtilidades.EncriptarBD("EasyServerRecovery", "N", context);
				EasyUtilidades.BorrarArchivos(gGlobal.pathArchivosSync + "/");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (respuesta.getResultado() == true) {
			RegistrarEvento("OK", "OK");
		} else {
			RegistrarEvento("ERROR FIN SINCRONISMO ",
					"FALLA EN FIN SINCRONISMO");
		}

		return respuesta.getResultado();

	}

	public boolean VerificarRegistrarEvento() {

		try {
			RequerimientoSincronismo requerimientoSincronismo = gGlobal.ultimoRequerimientoSincronismo;

			if (requerimientoSincronismo == null
					|| requerimientoSincronismo.getObservacion().equals("")) {
				return true;
			}

			// //Log.i("info", "reportar registrarevento."
			// + requerimientoSincronismo.getObservacion());

			Respuesta respuesta = new Respuesta();

			respuesta = generarEnviarEvento(requerimientoSincronismo,
					"RegistrarEvento", "REGISTRAR EVENTO");

			if (respuesta.getResultado() == false)
				return false;

			gGlobal.ultimoRequerimientoSincronismo = null;

			return respuesta.getResultado();
		} catch (Exception ex) {
			// //Log.i("info", "Error VerificarRegistrarEvento." +
			// ex.getMessage());
			return false;
		}

	}

	public boolean VerificarRegistrarEventoFile() {

		try {
			RequerimientoSincronismo requerimientoSincronismo = gGlobal.ultimoRequerimientoSincronismoFile;

			if (requerimientoSincronismo == null
					|| requerimientoSincronismo.getObservacion().equals("")) {
				return true;
			}

			// //Log.i("info", "reportar registrarevento."
			// + requerimientoSincronismo.getObservacion());

			Respuesta respuesta = new Respuesta();

			respuesta = generarEnviarEvento(requerimientoSincronismo,
					"RegistrarEvento", "REGISTRAR EVENTO");

			if (respuesta.getResultado() == false)
				return false;

			gGlobal.ultimoRequerimientoSincronismoFile = null;

			return respuesta.getResultado();
		} catch (Exception ex) {
			// //Log.i("info", "Error VerificarRegistrarEvento." +
			// ex.getMessage());
			return false;
		}

	}
	
	public boolean SincronismoNovedad(String pUsuario, String pPassword,
			String pDeviveId, int pVersion, int pRevision) {

		boolean _resultado;

		usuario = pUsuario;
		password = pPassword;
		deviceId = pDeviveId;
		version = pVersion;
		revision = pRevision;

		// //Log.i("info", "sincronimos version:" + version + ".revision:"
		// + revision);

		RegistrarEvento("SINCRONIZANDO", "INICIO SINCRONISMO");
		SincronismoBaseDatosServicio sincronismoBaseDatosServicio = new SincronismoBaseDatosServicioImpl(gGlobal);

		RequerimientoSincronismo requerimientoSincronismo = new RequerimientoSincronismo();

		requerimientoSincronismo.setDeviceId(deviceId);
		requerimientoSincronismo.setLogin(usuario);
		requerimientoSincronismo.setPassword(password);
		requerimientoSincronismo.setRevision(revision);
		requerimientoSincronismo.setSincronismoInicial(false);
		requerimientoSincronismo.setVersion(version);
		requerimientoSincronismo.setBaseDatosES("");
		requerimientoSincronismo.setLogId(0);

		Respuesta respuesta;
		try {
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismo(
					requerimientoSincronismo, "RequerimientoSincronismo");
			if (respuesta.getResultado() == true) {
				RegistrarEvento(
						"SINCRONIZANDO",
						"AUTENTICACION SATISFACTORIA. "
								+ respuesta.getObservacion());

			} else {
				RegistrarEvento("ERROR",
						"AUTENTICACION FALLIDA. " + respuesta.getObservacion());
				return false;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// Toast.makeText(context, R.string.messageProblemasSincronizacion
			// + ". " + e.getLocalizedMessage(), Toast.LENGTH_SHORT);
			RegistrarEvento("ERROR", "AUTENTICACION FALLIDA. " + e.getMessage());
			return false;
		}

		// //Log.i("info", "observacion respuesta. " +
		// respuesta.getObservacion());
		// //Log.i("info", "log id. " + respuesta.getLogId());

		ObtenerNuevaVersion = gGlobal
				.getNuevaVersion(respuesta.getObservacion());

		/*
		 * //obtener novedades de tablas up _resultado = GenerarNovedadesUp();
		 * if (!_resultado) { return false; }
		 */

		synchronized (ItaloDBAdapter.Lock) {
			if (gGlobal.bdEncriptada) {
				try {
					EasyUtilidades.EncriptarBD("EasyServerUp", "N", context);
				} catch (Exception e) {
					RegistrarEvento(
							"ERROR",
							"ENVIO UP EXCEPTION. CREAR ZIP UP. Exception."
									+ e.getMessage() + ". " + e.getCause()
									+ ". " + e.getLocalizedMessage());
					return false;
				}
			}

			EasyZip easyZip = new EasyZip();
			_resultado = easyZip.ZippingFile(gGlobal.DATABASE_PATH
					+ gGlobal.DATABASE_NAME_NOVEDADES_UP,
					gGlobal.pathArchivosSync + "/EasyServerUp.zip");

			if (gGlobal.bdEncriptada) {
				EasyUtilidades.BorrarBD(gGlobal.DATABASE_NAME_NOVEDADES_UP,
						context);
			}
		}

		if (_resultado) {
			// subir base de datos up
			if (respuesta.getResultado() == true) {
				RegistrarEvento("SINCRONIZANDO", "ENVIO UP");

				try {
					respuesta = SubirBaseDatos(requerimientoSincronismo,
							sincronismoBaseDatosServicio, "EasyServerUp",
							respuesta);
					EasyUtilidades
							.BorrarArchivos(gGlobal.pathArchivosSync + "/");
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				RegistrarEvento("SINCRONIZANDO", "RESPUESTA ENVIO UP. "
						+ respuesta.getObservacion());
			} else {
				// Toast.makeText(context, respuesta.getObservacion(),
				// Toast.LENGTH_SHORT);
				return false;
			}

			// //Log.i("info",
			// "observacion respuesta. " + respuesta.getObservacion());
			// //Log.i("info", "log id. " + respuesta.getLogId());

			if (respuesta.getResultado() == true) {
				RegistrarEvento("SINCRONIZANDO", "DESCARGANDO DOWN. ");
				try {
					respuesta = DescargarBaseDatos(requerimientoSincronismo,
							sincronismoBaseDatosServicio, "EasyServerDown",
							respuesta);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				RegistrarEvento("SINCRONIZANDO", "DESCARGANDO DOWN. "
						+ respuesta.getObservacion());

				// //Log.i("info",
				// "observacion respuesta descargar base de datos. "
				// + respuesta.getObservacion());
				// //Log.i("info", "log id. " + respuesta.getLogId());

				if (respuesta.getResultado() == false) {
					RegistrarEvento("ERROR",
							"DESCARGANDO DOWN. " + respuesta.getObservacion());
					return false;
				}

			} else {
				// Toast.makeText(context, respuesta.getObservacion(),
				// Toast.LENGTH_SHORT);
				RegistrarEvento("ERROR",
						"RESPUESTA ENVIO UP. " + respuesta.getObservacion());
				return false;
			}

			RegistrarEvento("SINCRONIZANDO", "APLICAR NOVEDADES. ");
			try {
				if (AplicarNovedadesDown(true)) {

				} else {
					RegistrarEvento("ERROR", "APLICAR NOVEDADES. "
							+ "Se presentaron problemas para aplicar novedades");
					respuesta.setResultado(false);
					respuesta
							.setObservacion("Se presentaron problemas para aplicar novedades");
					return false;
				}
			} catch (Exception e) {
				RegistrarEvento("ERROR", "APLICAR NOVEDADES. " + e.getMessage());
				return false;
			}

			try {

				RegistrarEvento("SINCRONIZANDO",
						"APLICAR AUXILIAR EASYSERVER. ");
				AplicarNovedadesAuxiliarEasyServer();

				RegistrarEvento("SINCRONIZANDO", "REALIZANDO COMMIT. ");
				respuesta = sincronismoBaseDatosServicio
						.CommitSincronismo(requerimientoSincronismo);

				RegistrarEvento("SINCRONIZANDO", "REALIZANDO COMMIT. "
						+ respuesta.getObservacion());

			} catch (Exception e) {
				// TODO Auto-generated catch block
				// Toast.makeText(context,
				// R.string.messageProblemasSincronizacion
				// + ". " + e.getLocalizedMessage(), Toast.LENGTH_SHORT);
				RegistrarEvento("ERROR",
						"APLICANDO AUXILIAR EASYSERVER. " + e.getMessage());
				return false;
			}

			if (respuesta.getResultado() == true) {
				RegistrarEvento("OK", "OK");
			}

		} else {
			RegistrarEvento("SINCRONIZANDO", "ERROR EN ZIP UP.");
			return false;
		}
		// attach base datos
		// AttachBaseDatosNovedades();

		return respuesta.getResultado();

	}

	public boolean SincronismoNovedadV2(String pUsuario, String pPassword,
			String pDeviveId, int pVersion, int pRevision) {

		boolean _resultado;

		_resultado = EasyUtilidades.CheckConnection(context);
		if (_resultado == false) {
			RegistrarEvento("ERROR",
					"No se encuentra disponible canal de comunicaci�n al servidor WIFI o GPRS");
			// //Log.i("info",
			// "No se encuentra disponible canal de comunicaci�n al servidor WIFI o GPRS");
			return false;
		}

		usuario = pUsuario;
		password = pPassword;
		deviceId = pDeviveId;
		version = pVersion;
		revision = pRevision;

		// //Log.i("info", "sincronimos version:" + version + ".revision:"
		// + revision);

		RegistrarEvento("SINCRONIZANDO", "INICIO SINCRONISMO");
		SincronismoBaseDatosServicio sincronismoBaseDatosServicio = new SincronismoBaseDatosServicioImpl(gGlobal);

		RequerimientoSincronismo requerimientoSincronismo = new RequerimientoSincronismo();

		requerimientoSincronismo.setDeviceId(deviceId);
		requerimientoSincronismo.setLogin(usuario);
		requerimientoSincronismo.setPassword(password);
		requerimientoSincronismo.setRevision(revision);
		requerimientoSincronismo.setSincronismoInicial(false);
		requerimientoSincronismo.setVersion(version);
		requerimientoSincronismo.setBaseDatosES("");
		requerimientoSincronismo.setLogId(0);

		Respuesta respuesta = new Respuesta();
		try {
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismo(
					requerimientoSincronismo, "RequerimientoSincronismoV2");
			if (respuesta.getResultado() == true) {
				RegistrarEvento(
						"SINCRONIZANDO",
						"AUTENTICACION SATISFACTORIA. "
								+ respuesta.getObservacion());
				requerimientoSincronismo.setBaseDatosES(respuesta
						.getBaseDatosES());
				requerimientoSincronismo.setLogId(respuesta.getLogId());

			} else {
				RegistrarEvento("ERROR",
						"AUTENTICACION FALLIDA. " + respuesta.getObservacion());
				return false;
			}
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// Toast.makeText(context, R.string.messageProblemasSincronizacion
			// + ". " + e.getLocalizedMessage(), Toast.LENGTH_SHORT);
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		// //Log.i("info", "observacion respuesta. " +
		// respuesta.getObservacion());
		// //Log.i("info", "log id. " + respuesta.getLogId());

		if (respuesta.getResultado() == true) {
			ObtenerNuevaVersion = gGlobal.getNuevaVersion(respuesta
					.getObservacion());
			RegistrarEvento("SINCRONIZANDO", "DESCARGANDO DOWN. ");
			try {
				respuesta = DescargarBaseDatos(requerimientoSincronismo,
						sincronismoBaseDatosServicio, "EasyServerDown",
						respuesta);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				RegistrarEvento("ERROR",
						"DESCARGANDO DOWN EXCEPTION. ClientProtocolException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. ClientProtocolException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. JSONException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. JSONException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. IOException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. IOException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. Exception."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. Exception."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			}
			RegistrarEvento("SINCRONIZANDO",
					"DESCARGANDO DOWN. " + respuesta.getObservacion());

			// //Log.i("info", "observacion respuesta descargar base de datos. "
			// + respuesta.getObservacion());
			// //Log.i("info", "log id. " + respuesta.getLogId());

			if (respuesta.getResultado() == false) {
				RegistrarEvento("ERROR",
						"DESCARGANDO DOWN. " + respuesta.getObservacion());
				return false;
			}

		} else {
			// Toast.makeText(context, respuesta.getObservacion(),
			// Toast.LENGTH_SHORT);
			RegistrarEvento("ERROR",
					"RESPUESTA DESCARGANDO DOWN. " + respuesta.getObservacion());
			return false;
		}

		RegistrarEvento("SINCRONIZANDO", "APLICAR NOVEDADES. ");
		try {
			if (AplicarNovedadesDown(false)) {

			} else {
				RegistrarEvento("ERROR", "APLICAR NOVEDADES. "
						+ "Se presentaron problemas para aplicar novedades");
				respuesta.setResultado(false);
				respuesta
						.setObservacion("Se presentaron problemas para aplicar novedades");
				return false;
			}
		} catch (Exception e) {
			RegistrarEvento("ERROR", "APLICAR NOVEDADES. " + e.getMessage());
			return false;
		}

		RegistrarEvento("SINCRONIZANDO", "APLICAR AUXILIAR EASYSERVER. ");
		AplicarNovedadesAuxiliarEasyServer();

		RegistrarEvento("SINCRONIZANDO", "REALIZANDO COMMIT. ");
		try {
			respuesta = sincronismoBaseDatosServicio
					.CommitSincronismo(requerimientoSincronismo);
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. Exception." + e.getMessage()
							+ ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. Exception." + e.getMessage()
			// + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		RegistrarEvento("SINCRONIZANDO",
				"REALIZANDO COMMIT. " + respuesta.getObservacion());

		if (respuesta.getResultado() == true) {
		} else {
			RegistrarEvento("ERROR COMMIT 1", respuesta.getObservacion());
			return false;
		}

		/*
		 * //obtener novedades de tablas up _resultado = GenerarNovedadesUp();
		 * if (!_resultado) { return false; }
		 */

		// crear archivo up
		synchronized (ItaloDBAdapter.Lock) {

			if (gGlobal.bdEncriptada) {
				try {
					EasyUtilidades.EncriptarBD("EasyServerUp", "N", context);
				} catch (Exception e) {
					RegistrarEvento(
							"ERROR",
							"ENVIO UP EXCEPTION. CREAR ZIP UP. Exception."
									+ e.getMessage() + ". " + e.getCause()
									+ ". " + e.getLocalizedMessage());
					return false;
				}
			}

			EasyZip easyZip = new EasyZip();
			_resultado = easyZip.ZippingFile(gGlobal.DATABASE_PATH
					+ gGlobal.DATABASE_NAME_NOVEDADES_UP,
					gGlobal.pathArchivosSync + "/EasyServerUp.zip");

			if (gGlobal.bdEncriptada) {
				EasyUtilidades.BorrarBD(gGlobal.DATABASE_NAME_NOVEDADES_UP,
						context);
			}

		}

		if (_resultado) {
			// subir base de datos up
			if (respuesta.getResultado() == true) {
				RegistrarEvento("SINCRONIZANDO", "ENVIO UP");

				try {
					respuesta = SubirBaseDatos(requerimientoSincronismo,
							sincronismoBaseDatosServicio, "EasyServerUp",
							respuesta);
					EasyUtilidades
							.BorrarArchivos(gGlobal.pathArchivosSync + "/");
				} catch (ClientProtocolException e) {
					RegistrarEvento(
							"ERROR",
							"ENVIO UP EXCEPTION. ClientProtocolException."
									+ e.getMessage() + ". " + e.getCause()
									+ ". " + e.getLocalizedMessage());
					// //Log.i("info",
					// "ENVIO UP EXCEPTION. ClientProtocolException."
					// + e.getMessage() + ". " + e.getCause()
					// + ". " + e.getLocalizedMessage());
					return false;
				} catch (IOException e) {
					RegistrarEvento(
							"ERROR",
							"ENVIO UP EXCEPTION. IOException." + e.getMessage()
									+ ". " + e.getCause() + ". "
									+ e.getLocalizedMessage());
					// //Log.i("info",
					// "ENVIO UP EXCEPTION. IOException." + e.getMessage()
					// + ". " + e.getCause() + ". "
					// + e.getLocalizedMessage());
					return false;
				} catch (JSONException e) {
					RegistrarEvento(
							"ERROR",
							"ENVIO UP EXCEPTION. JSONException."
									+ e.getMessage() + ". " + e.getCause()
									+ ". " + e.getLocalizedMessage());
					// //Log.i("info",
					// "ENVIO UP EXCEPTION. JSONException."
					// + e.getMessage() + ". " + e.getCause()
					// + ". " + e.getLocalizedMessage());
					return false;
				} catch (Exception e) {
					RegistrarEvento(
							"ERROR",
							"ENVIO UP EXCEPTION. Exception." + e.getMessage()
									+ ". " + e.getCause() + ". "
									+ e.getLocalizedMessage());
					// //Log.i("info",
					// "ENVIO UP EXCEPTION. Exception." + e.getMessage()
					// + ". " + e.getCause() + ". "
					// + e.getLocalizedMessage());
					return false;
				}

				RegistrarEvento("SINCRONIZANDO", "RESPUESTA ENVIO UP. "
						+ respuesta.getObservacion());
			} else {
				// Toast.makeText(context, respuesta.getObservacion(),
				// Toast.LENGTH_SHORT);
				return false;
			}

			// //Log.i("info",
			// "observacion respuesta. " + respuesta.getObservacion());
			// //Log.i("info", "log id. " + respuesta.getLogId());

		} else {
			RegistrarEvento("SINCRONIZANDO", "ERROR EN ZIP UP.");
			return false;
		}
		// attach base datos
		// AttachBaseDatosNovedades();
		if (respuesta.getResultado() == true) {
			RegistrarEvento("SINCRONIZANDO", "DESCARGANDO DOWN. ");
			try {
				respuesta = DescargarBaseDatos(requerimientoSincronismo,
						sincronismoBaseDatosServicio, "EasyServerDown",
						respuesta);
			} catch (ClientProtocolException e) {
				RegistrarEvento("ERROR",
						"DESCARGANDO DOWN EXCEPTION. ClientProtocolException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. ClientProtocolException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (JSONException e) {
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. JSONException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. JSONException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (IOException e) {
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. IOException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. IOException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (Exception e) {
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. Exception."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. Exception."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			}
			RegistrarEvento("SINCRONIZANDO",
					"DESCARGANDO DOWN. " + respuesta.getObservacion());

			// //Log.i("info", "observacion respuesta descargar base de datos. "
			// + respuesta.getObservacion());
			// //Log.i("info", "log id. " + respuesta.getLogId());

			if (respuesta.getResultado() == false) {
				RegistrarEvento("ERROR",
						"DESCARGANDO DOWN. " + respuesta.getObservacion());
				return false;
			}

		} else {
			// Toast.makeText(context, respuesta.getObservacion(),
			// Toast.LENGTH_SHORT);
			RegistrarEvento("ERROR",
					"RESPUESTA ENVIO UP. " + respuesta.getObservacion());
			return false;
		}

		RegistrarEvento("SINCRONIZANDO", "APLICAR NOVEDADES. ");
		try {
			if (AplicarNovedadesDown(true)) {

			} else {
				RegistrarEvento("ERROR", "APLICAR NOVEDADES. "
						+ "Se presentaron problemas para aplicar novedades");
				respuesta.setResultado(false);
				respuesta
						.setObservacion("Se presentaron problemas para aplicar novedades");
				return false;
			}
		} catch (Exception e) {
			RegistrarEvento("ERROR", "APLICAR NOVEDADES. " + e.getMessage());
			return false;
		}

		RegistrarEvento("SINCRONIZANDO", "APLICAR AUXILIAR EASYSERVER. ");

		AplicarNovedadesAuxiliarEasyServer();

		RegistrarEvento("SINCRONIZANDO", "REALIZANDO COMMIT. ");
		try {
			respuesta = sincronismoBaseDatosServicio
					.CommitSincronismo(requerimientoSincronismo);
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. Exception." + e.getMessage()
							+ ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. Exception." + e.getMessage()
			// + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		RegistrarEvento("SINCRONIZANDO",
				"REALIZANDO COMMIT. " + respuesta.getObservacion());

		if (respuesta.getResultado() == true) {
			RegistrarEvento("OK", "OK");
		} else {
			RegistrarEvento("ERROR COMMIT", respuesta.getObservacion());
		}

		return respuesta.getResultado();

	}

	public boolean SincronismoNovedadV3(String pUsuario, String pPassword,
			String pDeviveId, int pVersion, int pRevision, int pCompilacion) {

		boolean _resultado;

		_resultado = EasyUtilidades.CheckConnection(context);
		if (_resultado == false) {
			RegistrarEvento("ERROR",
					"No se encuentra disponible canal de comunicaci�n al servidor WIFI o GPRS");
			// //Log.i("info",
			// "No se encuentra disponible canal de comunicaci�n al servidor WIFI o GPRS");
			return false;
		}

		usuario = pUsuario;
		password = pPassword;
		deviceId = pDeviveId;
		version = pVersion;
		revision = pRevision;
		compilacion = pCompilacion;

		//Log.i("info", "sincronimos versionv3:" + version + ".revision:"
		//	+ revision + ".compilacion:" + compilacion);

		RegistrarEvento("SINCRONIZANDO", "INICIO SINCRONISMO");
		SincronismoBaseDatosServicio sincronismoBaseDatosServicio = new SincronismoBaseDatosServicioImpl(gGlobal);

		RequerimientoSincronismo requerimientoSincronismo = new RequerimientoSincronismo();

		gGlobal.ultimoRequerimientoSincronismo = requerimientoSincronismo;

		requerimientoSincronismo.setDeviceId(deviceId);
		requerimientoSincronismo.setLogin(usuario);
		requerimientoSincronismo.setPassword(password);
		requerimientoSincronismo.setRevision(revision);
		requerimientoSincronismo.setSincronismoInicial(false);
		requerimientoSincronismo.setVersion(version);
		requerimientoSincronismo.setCompilacion(compilacion);
		requerimientoSincronismo.setBaseDatosES("");
		requerimientoSincronismo.setLogId(0);

		Respuesta respuesta = new Respuesta();
		try {
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismoURL(
					requerimientoSincronismo, "UrlEasyServer");

			if (respuesta.getObservacion().indexOf("ERROR") == -1) {
				RegistrarEvento("SINCRONIZANDO", "URL SATISFACTORIA. "
						+ respuesta.getObservacion());
				gGlobal.UrlEasyServer = respuesta.getObservacion();
			} else {
				RegistrarEvento("ERROR",
						"URL FALLIDA. " + respuesta.getObservacion());
				return false;
			}

		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "URL FALLIDA EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. JSONException." + e.getMessage()
							+ ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "URL FALLIDA EXCEPTION. JSONException." + e.getMessage()
			// + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. IOException." + e.getMessage()
							+ ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "URL FALLIDA EXCEPTION. IOException." + e.getMessage()
			// + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "URL FALLIDA EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento("ERROR",
					"URL FALLIDA EXCEPTION. Exception." + e.getMessage() + ". "
							+ e.getCause() + ". " + e.getLocalizedMessage());
			// //Log.i("info", "URL FALLIDA EXCEPTION. Exception." +
			// e.getMessage()
			// + ". " + e.getCause() + ". " + e.getLocalizedMessage());
			return false;
		}

		respuesta = new Respuesta();
		try {
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismo(
					requerimientoSincronismo, "RequerimientoSincronismoV2");

			if (respuesta.getResultado() == true) {
				RegistrarEvento(
						"SINCRONIZANDO",
						"AUTENTICACION SATISFACTORIA. "
								+ respuesta.getObservacion());
				requerimientoSincronismo.setBaseDatosES(respuesta
						.getBaseDatosES());
				requerimientoSincronismo.setLogId(respuesta.getLogId());

			} else {
				RegistrarEvento("ERROR",
						"AUTENTICACION FALLIDA. " + respuesta.getObservacion());
			}
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// Toast.makeText(context, R.string.messageProblemasSincronizacion
			// + ". " + e.getLocalizedMessage(), Toast.LENGTH_SHORT);
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		// //Log.i("info", "observacion respuesta. " +
		// respuesta.getObservacion());
		// //Log.i("info", "log id. " + respuesta.getLogId());

		// revisar necesidad de recovery
		//gGlobal gGlobal;
		//gGlobal = ((gGlobal) context.getApplicationContext());

		if (respuesta.getResultado() == true) {
			ObtenerNuevaVersion = gGlobal.getNuevaVersion(respuesta
					.getObservacion());
		}

		TransferirArchivos(sincronismoBaseDatosServicio,
				requerimientoSincronismo, respuesta, "Trace");

		TransferirArchivos(sincronismoBaseDatosServicio,
				requerimientoSincronismo, respuesta, "Up");

		if (gGlobal.CrearRecovery) {
			try {
				respuesta.setResultado(CrearRecovery());
				if (respuesta.getResultado()) {
					// enviar recovery por web
					RegistrarEvento("SINCRONIZANDO", "ENVIO RECOVERY");

					try {
						respuesta = SubirRecoveryBaseDatos(
								requerimientoSincronismo,
								sincronismoBaseDatosServicio,
								"EasyServerRecovery", respuesta);
					} catch (ClientProtocolException e) {
						RegistrarEvento("ERROR",
								"ENVIO RECOVERY EXCEPTION. ClientProtocolException."
										+ e.getMessage() + ". " + e.getCause()
										+ ". " + e.getLocalizedMessage());
						// //Log.i("info",
						// "ENVIO RECOVERY EXCEPTION. ClientProtocolException."
						// + e.getMessage() + ". " + e.getCause()
						// + ". " + e.getLocalizedMessage());
						// return false;
					} catch (IOException e) {
						RegistrarEvento(
								"ERROR",
								"ENVIO RECOVERY EXCEPTION. IOException."
										+ e.getMessage() + ". " + e.getCause()
										+ ". " + e.getLocalizedMessage());
						// //Log.i("info",
						// "ENVIO RECOVERY EXCEPTION. IOException."
						// + e.getMessage() + ". " + e.getCause() + ". "
						// + e.getLocalizedMessage());
						// return false;
					} catch (JSONException e) {
						RegistrarEvento(
								"ERROR",
								"ENVIO RECOVERY EXCEPTION. JSONException."
										+ e.getMessage() + ". " + e.getCause()
										+ ". " + e.getLocalizedMessage());
						// //Log.i("info",
						// "ENVIO RECOVERY EXCEPTION. JSONException."
						// + e.getMessage() + ". " + e.getCause()
						// + ". " + e.getLocalizedMessage());
						// return false;
					} catch (Exception e) {
						RegistrarEvento(
								"ERROR",
								"ENVIO RECOVERY EXCEPTION. Exception."
										+ e.getMessage() + ". " + e.getCause()
										+ ". " + e.getLocalizedMessage());
						// //Log.i("info", "ENVIO RECOVERY EXCEPTION. Exception."
						// + e.getMessage() + ". " + e.getCause() + ". "
						// + e.getLocalizedMessage());
						// return false;
					}

					RegistrarEvento(
							"SINCRONIZANDO",
							"RESPUESTA ENVIO RECOVERY. "
									+ respuesta.getObservacion());
				} else {
					RegistrarEvento("ERROR", "Problemas para crear recovery.");
					// //Log.i("info", "Problemas para crear recovery.");
				}
			} catch (Exception e) {
				RegistrarEvento(
						"ERROR",
						"Problemas para crear recovery. Exception."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "Problemas para crear recovery. Exception."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
			}
			gGlobal.CrearRecovery = false;
		}

		if (respuesta.getResultado() == true) {
			// ObtenerNuevaVersion = gGlobal.getNuevaVersion(respuesta
			// .getObservacion());
			RegistrarEvento("SINCRONIZANDO", "DESCARGANDO DOWN. ");
			try {
				respuesta = DescargarBaseDatos(requerimientoSincronismo,
						sincronismoBaseDatosServicio, "EasyServerDown",
						respuesta);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				RegistrarEvento("ERROR",
						"DESCARGANDO DOWN EXCEPTION. ClientProtocolException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. ClientProtocolException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. JSONException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. JSONException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. IOException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. IOException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. Exception."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. Exception."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			}
			RegistrarEvento("SINCRONIZANDO",
					"DESCARGANDO DOWN. " + respuesta.getObservacion());

			// //Log.i("info", "observacion respuesta descargar base de datos. "
			// + respuesta.getObservacion());
			// //Log.i("info", "log id. " + respuesta.getLogId());

			if (respuesta.getResultado() == false) {
				RegistrarEvento("ERROR",
						"DESCARGANDO DOWN. " + respuesta.getObservacion());
				return false;
			}

		} else {
			// Toast.makeText(context, respuesta.getObservacion(),
			// Toast.LENGTH_SHORT);
			RegistrarEvento("ERROR",
					"RESPUESTA DESCARGANDO DOWN. " + respuesta.getObservacion());
			return false;
		}

		RegistrarEvento("SINCRONIZANDO", "APLICAR AUXILIAR EASYSERVER. ");

		try {
			if (AplicarNovedadesDown(false)) {

			} else {
				RegistrarEvento("ERROR", "APLICAR NOVEDADES. "
						+ "Se presentaron problemas para aplicar novedades");
				respuesta.setResultado(false);
				respuesta
						.setObservacion("Se presentaron problemas para aplicar novedades");
				return false;
			}
		} catch (Exception e) {
			RegistrarEvento("ERROR", "APLICAR NOVEDADES. " + e.getMessage());
			return false;
		}


		//Log.i("info", "para aplicar novedades v3");
		AplicarNovedadesAuxiliarEasyServer();
		//Log.i("info", "fin para aplicar novedades v3");
		
		/*
		//Log.i("info", "para aplicar novedades v3");
		AplicarNovedadesAuxiliarEasyServer();
		//Log.i("info", "fin para aplicar novedades v3");
		*/

		RegistrarEvento("SINCRONIZANDO", "REALIZANDO COMMIT. ");
		try {
			respuesta = sincronismoBaseDatosServicio
					.CommitSincronismo(requerimientoSincronismo);
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. Exception." + e.getMessage()
							+ ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. Exception." + e.getMessage()
			// + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		RegistrarEvento("SINCRONIZANDO",
				"REALIZANDO COMMIT. " + respuesta.getObservacion());

		if (respuesta.getResultado() == true) {
		} else {
			RegistrarEvento("ERROR COMMIT 1", respuesta.getObservacion());
			return false;
		}

		/*
		 * //obtener novedades de tablas up _resultado = GenerarNovedadesUp();
		 * if (!_resultado) { return false; }
		 */

		_resultado = true;

		if (_resultado) {
			// subir base de datos up
			if (respuesta.getResultado() == true) {
				RegistrarEvento("SINCRONIZANDO", "ENVIO UP");

				try {
					for (int i = 0; i <= gGlobal.numeroIntentos; ++i) {

						synchronized (ItaloDBAdapter.Lock) {

							if (gGlobal.bdEncriptada) {
								try {
									EasyUtilidades.EncriptarBD("EasyServerUp",
											"N", context);
								} catch (Exception e) {
									RegistrarEvento(
											"ERROR",
											"ENVIO UP EXCEPTION. CREAR ZIP UP. Exception."
													+ e.getMessage() + ". "
													+ e.getCause() + ". "
													+ e.getLocalizedMessage());
									return false;
								}
							}

							EasyZip easyZip = new EasyZip();
							_resultado = easyZip
									.ZippingFile(
											gGlobal.DATABASE_PATH
													+ gGlobal.DATABASE_NAME_NOVEDADES_UP,
											gGlobal.pathArchivosSync
													+ "/EasyServerUp.zip");

							if (gGlobal.bdEncriptada) {
								EasyUtilidades.BorrarBD(
										gGlobal.DATABASE_NAME_NOVEDADES_UP,
										context);
							}
						}

						if (!_resultado) {
							RegistrarEvento("SINCRONIZANDO", "ERROR EN ZIP UP.");
							return false;
						}

						respuesta = SubirBaseDatos(requerimientoSincronismo,
								sincronismoBaseDatosServicio, "EasyServerUpV2",
								respuesta);
						if (respuesta.getResultado()) {
							break;
						}
					}
					EasyUtilidades
							.BorrarArchivos(gGlobal.pathArchivosSync + "/");
				} catch (ClientProtocolException e) {
					RegistrarEvento(
							"ERROR",
							"ENVIO UP EXCEPTION. ClientProtocolException."
									+ e.getMessage() + ". " + e.getCause()
									+ ". " + e.getLocalizedMessage());
					// //Log.i("info",
					// "ENVIO UP EXCEPTION. ClientProtocolException."
					// + e.getMessage() + ". " + e.getCause()
					// + ". " + e.getLocalizedMessage());
					return false;
				} catch (IOException e) {
					RegistrarEvento(
							"ERROR",
							"ENVIO UP EXCEPTION. IOException." + e.getMessage()
									+ ". " + e.getCause() + ". "
									+ e.getLocalizedMessage());
					// //Log.i("info",
					// "ENVIO UP EXCEPTION. IOException." + e.getMessage()
					// + ". " + e.getCause() + ". "
					// + e.getLocalizedMessage());
					return false;
				} catch (JSONException e) {
					RegistrarEvento(
							"ERROR",
							"ENVIO UP EXCEPTION. JSONException."
									+ e.getMessage() + ". " + e.getCause()
									+ ". " + e.getLocalizedMessage());
					// //Log.i("info",
					// "ENVIO UP EXCEPTION. JSONException."
					// + e.getMessage() + ". " + e.getCause()
					// + ". " + e.getLocalizedMessage());
					return false;
				} catch (Exception e) {
					RegistrarEvento(
							"ERROR",
							"ENVIO UP EXCEPTION. Exception." + e.getMessage()
									+ ". " + e.getCause() + ". "
									+ e.getLocalizedMessage());
					// //Log.i("info",
					// "ENVIO UP EXCEPTION. Exception." + e.getMessage()
					// + ". " + e.getCause() + ". "
					// + e.getLocalizedMessage());
					return false;
				}

				RegistrarEvento("SINCRONIZANDO", "RESPUESTA ENVIO UP. "
						+ respuesta.getObservacion());
			} else {
				// Toast.makeText(context, respuesta.getObservacion(),
				// Toast.LENGTH_SHORT);
				return false;
			}

			// //Log.i("info",
			// "observacion respuesta. " + respuesta.getObservacion());
			// //Log.i("info", "log id. " + respuesta.getLogId());

		} else {
			RegistrarEvento("SINCRONIZANDO", "ERROR EN ZIP UP.");
			return false;
		}

		// attach base datos
		// AttachBaseDatosNovedades();
		if (respuesta.getResultado() == true) {
			RegistrarEvento("SINCRONIZANDO", "PREPARAR DOWN. ");
			try {
				respuesta = sincronismoBaseDatosServicio.InstanciaSincronismo(
						requerimientoSincronismo, "PrepararEasyServerDown");

			} catch (ClientProtocolException e) {
				RegistrarEvento(
						"ERROR",
						"PREPARAR DOWN EXCEPTION. ClientProtocolException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "PREPARAR DOWN EXCEPTION. ClientProtocolException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (JSONException e) {
				RegistrarEvento(
						"ERROR",
						"PREPARAR DOWN EXCEPTION. JSONException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "PREPARAR DOWN EXCEPTION. JSONException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (IOException e) {
				RegistrarEvento(
						"ERROR",
						"PREPARAR DOWN EXCEPTION. IOException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "PREPARAR DOWN EXCEPTION. IOException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (Exception e) {
				RegistrarEvento(
						"ERROR",
						"PREPARAR DOWN EXCEPTION. Exception." + e.getMessage()
								+ ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "PREPARAR DOWN EXCEPTION. Exception." + e.getMessage()
				// + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			}
		}

		// if (true)
		// {
		// return false;
		// }

		if (respuesta.getResultado() == true) {
			RegistrarEvento("SINCRONIZANDO", "DESCARGANDO DOWN. ");
			try {
				for (int i = 0; i <= gGlobal.numeroIntentos; ++i) {
					respuesta = DescargarBaseDatos(requerimientoSincronismo,
							sincronismoBaseDatosServicio, "EasyServerDown",
							respuesta);
					if (respuesta.getResultado()) {
						break;
					}
				}
			} catch (ClientProtocolException e) {
				RegistrarEvento("ERROR",
						"DESCARGANDO DOWN EXCEPTION. ClientProtocolException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. ClientProtocolException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (JSONException e) {
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. JSONException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. JSONException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (IOException e) {
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. IOException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. IOException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (Exception e) {
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. Exception."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. Exception."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			}
			RegistrarEvento("SINCRONIZANDO",
					"DESCARGANDO DOWN. " + respuesta.getObservacion());

			// //Log.i("info", "observacion respuesta descargar base de datos. "
			// + respuesta.getObservacion());
			// //Log.i("info", "log id. " + respuesta.getLogId());

			if (respuesta.getResultado() == false) {
				RegistrarEvento("ERROR",
						"DESCARGANDO DOWN. " + respuesta.getObservacion());
				return false;
			}

		} else {
			// Toast.makeText(context, respuesta.getObservacion(),
			// Toast.LENGTH_SHORT);
			RegistrarEvento("ERROR",
					"RESPUESTA ENVIO UP. " + respuesta.getObservacion());
			return false;
		}

		/*
		 * while (true) { if (!AplicarNovedadesDown()) {
		 * RegistrarEvento("ERROR", "APLICAR NOVEDADES. " +
		 * respuesta.getObservacion()); break; } }
		 */

		RegistrarEvento("SINCRONIZANDO", "APLICAR NOVEDADES. ");
		try {
			if (AplicarNovedadesDown(true)) {

			} else {
				RegistrarEvento("ERROR", "APLICAR NOVEDADES. "
						+ "Se presentaron problemas para aplicar novedades");
				respuesta.setResultado(false);
				respuesta
						.setObservacion("Se presentaron problemas para aplicar novedades");
				return false;
			}
		} catch (Exception e) {
			RegistrarEvento("ERROR", "APLICAR NOVEDADES. " + e.getMessage());
			return false;
		}

		RegistrarEvento("SINCRONIZANDO", "APLICAR AUXILIAR EASYSERVER. ");

		// AplicarNovedadesAuxiliarEasyServer();

		RegistrarEvento("SINCRONIZANDO", "REALIZANDO COMMIT. ");
		try {
			respuesta = sincronismoBaseDatosServicio
					.CommitSincronismo(requerimientoSincronismo);
			if (respuesta.getResultado() == false) {
				RegistrarEvento("ERROR",
						"REALIZANDO COMMIT. " + respuesta.getObservacion());
				return false;
			}
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. Exception." + e.getMessage()
							+ ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. Exception." + e.getMessage()
			// + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		RegistrarEvento("SINCRONIZANDO",
				"REALIZANDO COMMIT. " + respuesta.getObservacion());

		try {
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismo(
					requerimientoSincronismo, "FinSincronismo");
			if (respuesta.getResultado() == true) {
				RegistrarEvento(
						"SINCRONIZANDO",
						"FIN SINCRONISMO SATISFACTORIA. "
								+ respuesta.getObservacion());
			} else {
				RegistrarEvento("ERROR", "FIN SINCRONISMO FALLIDA. "
						+ respuesta.getObservacion());
			}
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// Toast.makeText(context, R.string.messageProblemasSincronizacion
			// + ". " + e.getLocalizedMessage(), Toast.LENGTH_SHORT);
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		if (respuesta.getResultado() == true) {
			RegistrarEvento("OK", "OK");
		} else {
			RegistrarEvento("ERROR FIN SINCRONISMO ",
					"FALLA EN FIN SINCRONISMO");
		}

		return respuesta.getResultado();

	}

	public boolean SincronismoNovedadV4(String pUsuario, String pPassword,
			String pDeviveId, int pVersion, int pRevision) {

		boolean _resultado;

		_resultado = EasyUtilidades.CheckConnection(context);
		if (_resultado == false) {
			RegistrarEvento("ERROR",
					"No se encuentra disponible canal de comunicaci�n al servidor WIFI o GPRS");
			// //Log.i("info",
			// "No se encuentra disponible canal de comunicaci�n al servidor WIFI o GPRS");
			return false;
		}

		usuario = pUsuario;
		password = pPassword;
		deviceId = pDeviveId;
		version = pVersion;
		revision = pRevision;

		// //Log.i("info", "sincronimos version:" + version + ".revision:"
		// + revision);

		RegistrarEvento("SINCRONIZANDO", "INICIO SINCRONISMO");
		SincronismoBaseDatosServicio sincronismoBaseDatosServicio = new SincronismoBaseDatosServicioImpl(gGlobal);

		RequerimientoSincronismo requerimientoSincronismo = new RequerimientoSincronismo();

		requerimientoSincronismo.setDeviceId(deviceId);
		requerimientoSincronismo.setLogin(usuario);
		requerimientoSincronismo.setPassword(password);
		requerimientoSincronismo.setRevision(revision);
		requerimientoSincronismo.setSincronismoInicial(false);
		requerimientoSincronismo.setVersion(version);
		requerimientoSincronismo.setBaseDatosES("");
		requerimientoSincronismo.setLogId(0);

		Respuesta respuesta = new Respuesta();
		try {
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismo(
					requerimientoSincronismo, "RequerimientoSincronismoV2");
			if (respuesta.getResultado() == true) {
				RegistrarEvento(
						"SINCRONIZANDO",
						"AUTENTICACION SATISFACTORIA. "
								+ respuesta.getObservacion());
				requerimientoSincronismo.setBaseDatosES(respuesta
						.getBaseDatosES());
				requerimientoSincronismo.setLogId(respuesta.getLogId());

			} else {
				RegistrarEvento("ERROR",
						"AUTENTICACION FALLIDA. " + respuesta.getObservacion());
				return false;
			}
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// Toast.makeText(context, R.string.messageProblemasSincronizacion
			// + ". " + e.getLocalizedMessage(), Toast.LENGTH_SHORT);
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		// //Log.i("info", "observacion respuesta. " +
		// respuesta.getObservacion());
		// //Log.i("info", "log id. " + respuesta.getLogId());

		if (respuesta.getResultado() == true) {
			ObtenerNuevaVersion = gGlobal.getNuevaVersion(respuesta
					.getObservacion());

			RegistrarEvento("SINCRONIZANDO", "DESCARGANDO DOWN. ");
			try {
				respuesta = DescargarBaseDatos(requerimientoSincronismo,
						sincronismoBaseDatosServicio, "EasyServerDown",
						respuesta);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				RegistrarEvento("ERROR",
						"DESCARGANDO DOWN EXCEPTION. ClientProtocolException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. ClientProtocolException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. JSONException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. JSONException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. IOException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. IOException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. Exception."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. Exception."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			}
			RegistrarEvento("SINCRONIZANDO",
					"DESCARGANDO DOWN. " + respuesta.getObservacion());

			// //Log.i("info", "observacion respuesta descargar base de datos. "
			// + respuesta.getObservacion());
			// //Log.i("info", "log id. " + respuesta.getLogId());

			if (respuesta.getResultado() == false) {
				RegistrarEvento("ERROR",
						"DESCARGANDO DOWN. " + respuesta.getObservacion());
				return false;
			}

		} else {
			// Toast.makeText(context, respuesta.getObservacion(),
			// Toast.LENGTH_SHORT);
			RegistrarEvento("ERROR",
					"RESPUESTA DESCARGANDO DOWN. " + respuesta.getObservacion());
			return false;
		}

		try {
			if (AplicarNovedadesDown(false)) {

			} else {
				RegistrarEvento("ERROR", "APLICAR NOVEDADES. "
						+ "Se presentaron problemas para aplicar novedades");
				respuesta.setResultado(false);
				respuesta
						.setObservacion("Se presentaron problemas para aplicar novedades");
				return false;
			}
		} catch (Exception e) {
			RegistrarEvento("ERROR", "APLICAR NOVEDADES. " + e.getMessage());
			return false;
		}

		RegistrarEvento("SINCRONIZANDO", "APLICAR AUXILIAR EASYSERVER. ");
		AplicarNovedadesAuxiliarEasyServer();

		RegistrarEvento("SINCRONIZANDO", "REALIZANDO COMMIT. ");
		try {
			respuesta = sincronismoBaseDatosServicio
					.CommitSincronismo(requerimientoSincronismo);
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. Exception." + e.getMessage()
							+ ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. Exception." + e.getMessage()
			// + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		RegistrarEvento("SINCRONIZANDO",
				"REALIZANDO COMMIT. " + respuesta.getObservacion());

		if (respuesta.getResultado() == true) {
		} else {
			RegistrarEvento("ERROR COMMIT 1", respuesta.getObservacion());
			return false;
		}

		/*
		 * //obtener novedades de tablas up _resultado = GenerarNovedadesUp();
		 * if (!_resultado) { return false; }
		 */

		synchronized (ItaloDBAdapter.Lock) {

			if (gGlobal.bdEncriptada) {
				try {
					EasyUtilidades.EncriptarBD("EasyServerUp", "N", context);
				} catch (Exception e) {
					RegistrarEvento(
							"ERROR",
							"ENVIO UP EXCEPTION. CREAR ZIP UP. Exception."
									+ e.getMessage() + ". " + e.getCause()
									+ ". " + e.getLocalizedMessage());
					return false;
				}
			}

			EasyZip easyZip = new EasyZip();
			_resultado = easyZip.ZippingFile(gGlobal.DATABASE_PATH
					+ gGlobal.DATABASE_NAME_NOVEDADES_UP,
					gGlobal.pathArchivosSync + "/EasyServerUp.zip");

			if (gGlobal.bdEncriptada) {
				EasyUtilidades.BorrarBD(gGlobal.DATABASE_NAME_NOVEDADES_UP,
						context);
			}
		}

		if (_resultado) {
			// subir base de datos up
			if (respuesta.getResultado() == true) {
				RegistrarEvento("SINCRONIZANDO", "ENVIO UP");

				try {
					respuesta = SubirBaseDatos(requerimientoSincronismo,
							sincronismoBaseDatosServicio, "EasyServerUpV2",
							respuesta);
					EasyUtilidades
							.BorrarArchivos(gGlobal.pathArchivosSync + "/");
				} catch (ClientProtocolException e) {
					RegistrarEvento(
							"ERROR",
							"ENVIO UP EXCEPTION. ClientProtocolException."
									+ e.getMessage() + ". " + e.getCause()
									+ ". " + e.getLocalizedMessage());
					// //Log.i("info",
					// "ENVIO UP EXCEPTION. ClientProtocolException."
					// + e.getMessage() + ". " + e.getCause()
					// + ". " + e.getLocalizedMessage());
					return false;
				} catch (IOException e) {
					RegistrarEvento(
							"ERROR",
							"ENVIO UP EXCEPTION. IOException." + e.getMessage()
									+ ". " + e.getCause() + ". "
									+ e.getLocalizedMessage());
					// //Log.i("info",
					// "ENVIO UP EXCEPTION. IOException." + e.getMessage()
					// + ". " + e.getCause() + ". "
					// + e.getLocalizedMessage());
					return false;
				} catch (JSONException e) {
					RegistrarEvento(
							"ERROR",
							"ENVIO UP EXCEPTION. JSONException."
									+ e.getMessage() + ". " + e.getCause()
									+ ". " + e.getLocalizedMessage());
					// //Log.i("info",
					// "ENVIO UP EXCEPTION. JSONException."
					// + e.getMessage() + ". " + e.getCause()
					// + ". " + e.getLocalizedMessage());
					return false;
				} catch (Exception e) {
					RegistrarEvento(
							"ERROR",
							"ENVIO UP EXCEPTION. Exception." + e.getMessage()
									+ ". " + e.getCause() + ". "
									+ e.getLocalizedMessage());
					// //Log.i("info",
					// "ENVIO UP EXCEPTION. Exception." + e.getMessage()
					// + ". " + e.getCause() + ". "
					// + e.getLocalizedMessage());
					return false;
				}

				RegistrarEvento("SINCRONIZANDO", "RESPUESTA ENVIO UP. "
						+ respuesta.getObservacion());
			} else {
				// Toast.makeText(context, respuesta.getObservacion(),
				// Toast.LENGTH_SHORT);
				return false;
			}

			// //Log.i("info",
			// "observacion respuesta. " + respuesta.getObservacion());
			// //Log.i("info", "log id. " + respuesta.getLogId());

		} else {
			RegistrarEvento("SINCRONIZANDO", "ERROR EN ZIP UP.");
			return false;
		}

		// attach base datos
		// AttachBaseDatosNovedades();
		if (respuesta.getResultado() == true) {
			RegistrarEvento("SINCRONIZANDO", "PREPARAR DOWN. ");

			// obtener tablas down
			try {
				ArrayList<String> listTablasDown = TablasDown();

				for (String _tabla : listTablasDown) {
					RegistrarEvento("SINCRONIZANDO", "PREPARAR TABLA DOWN "
							+ _tabla);
					// ////Log.i("info", "PREPARAR TABLA DOWN " + _tabla);
					try {
						requerimientoSincronismo.setPassword(_tabla);
						respuesta = sincronismoBaseDatosServicio
								.InstanciaSincronismo(requerimientoSincronismo,
										"PrepararEasyServerDownTabla");

					} catch (ClientProtocolException e) {
						RegistrarEvento(
								"ERROR",
								"PREPARAR DOWN TABLA EXCEPTION " + _tabla
										+ ". ClientProtocolException."
										+ e.getMessage() + ". " + e.getCause()
										+ ". " + e.getLocalizedMessage());
						// //Log.i("info",
						// "PREPARAR DOWN TABLA EXCEPTION " + _tabla
						// + ". ClientProtocolException."
						// + e.getMessage() + ". " + e.getCause()
						// + ". " + e.getLocalizedMessage());
						return false;
					} catch (JSONException e) {
						RegistrarEvento(
								"ERROR",
								"PREPARAR DOWN TABLA EXCEPTION " + _tabla
										+ ". JSONException." + e.getMessage()
										+ ". " + e.getCause() + ". "
										+ e.getLocalizedMessage());
						// //Log.i("info", "PREPARAR DOWN TABLA EXCEPTION " +
						// _tabla
						// + ". JSONException." + e.getMessage() + ". "
						// + e.getCause() + ". " + e.getLocalizedMessage());
						return false;
					} catch (IOException e) {
						RegistrarEvento(
								"ERROR",
								"PREPARAR DOWN TABLA EXCEPTION " + _tabla
										+ ". IOException." + e.getMessage()
										+ ". " + e.getCause() + ". "
										+ e.getLocalizedMessage());
						// //Log.i("info",
						// "PREPARAR DOWN TABLA EXCEPTION " + _tabla
						// + ". IOException." + e.getMessage()
						// + ". " + e.getCause() + ". "
						// + e.getLocalizedMessage());
						return false;
					} catch (Exception e) {
						RegistrarEvento(
								"ERROR",
								"PREPARAR DOWN TABLA EXCEPTION " + _tabla
										+ ". Exception." + e.getMessage()
										+ ". " + e.getCause() + ". "
										+ e.getLocalizedMessage());
						// //Log.i("info",
						// "PREPARAR DOWN TABLA EXCEPTION " + _tabla
						// + ". Exception." + e.getMessage()
						// + ". " + e.getCause() + ". "
						// + e.getLocalizedMessage());
						return false;
					}

				}

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				RegistrarEvento(
						"SINCRONIZANDO",
						"ERROR EN OBTENER LISTADO TABLAS DOWN."
								+ e1.getMessage());
				return false;
			}

		}

		if (respuesta.getResultado() == true) {
			RegistrarEvento("SINCRONIZANDO", "DESCARGANDO DOWN. ");
			try {
				respuesta = DescargarBaseDatos(requerimientoSincronismo,
						sincronismoBaseDatosServicio, "EasyServerDown",
						respuesta);
			} catch (ClientProtocolException e) {
				RegistrarEvento("ERROR",
						"DESCARGANDO DOWN EXCEPTION. ClientProtocolException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. ClientProtocolException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (JSONException e) {
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. JSONException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. JSONException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (IOException e) {
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. IOException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. IOException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (Exception e) {
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. Exception."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. Exception."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			}
			RegistrarEvento("SINCRONIZANDO",
					"DESCARGANDO DOWN. " + respuesta.getObservacion());

			// //Log.i("info", "observacion respuesta descargar base de datos. "
			// + respuesta.getObservacion());
			// //Log.i("info", "log id. " + respuesta.getLogId());

			if (respuesta.getResultado() == false) {
				RegistrarEvento("ERROR",
						"DESCARGANDO DOWN. " + respuesta.getObservacion());
				return false;
			}

		} else {
			// Toast.makeText(context, respuesta.getObservacion(),
			// Toast.LENGTH_SHORT);
			RegistrarEvento("ERROR",
					"PREPARAR DOWN. " + respuesta.getObservacion());
			return false;
		}

		/*
		 * while (true) { if (!AplicarNovedadesDown()) {
		 * RegistrarEvento("ERROR", "APLICAR NOVEDADES. " +
		 * respuesta.getObservacion()); break; } }
		 */

		RegistrarEvento("SINCRONIZANDO", "APLICAR NOVEDADES. ");
		try {
			if (AplicarNovedadesDown(true)) {

			} else {
				RegistrarEvento("ERROR", "APLICAR NOVEDADES. "
						+ "Se presentaron problemas para aplicar novedades");
				respuesta.setResultado(false);
				respuesta
						.setObservacion("Se presentaron problemas para aplicar novedades");
				return false;
			}
		} catch (Exception e) {
			RegistrarEvento("ERROR", "APLICAR NOVEDADES. " + e.getMessage());
			return false;
		}

		RegistrarEvento("SINCRONIZANDO", "APLICAR AUXILIAR EASYSERVER. ");

		AplicarNovedadesAuxiliarEasyServer();

		RegistrarEvento("SINCRONIZANDO", "REALIZANDO COMMIT. ");
		try {
			respuesta = sincronismoBaseDatosServicio
					.CommitSincronismo(requerimientoSincronismo);
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. Exception." + e.getMessage()
							+ ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. Exception." + e.getMessage()
			// + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		RegistrarEvento("SINCRONIZANDO",
				"REALIZANDO COMMIT. " + respuesta.getObservacion());

		if (respuesta.getResultado() == true) {
			RegistrarEvento("OK", "OK");
		} else {
			RegistrarEvento("ERROR COMMIT", respuesta.getObservacion());
		}

		return respuesta.getResultado();

	}

	public boolean SincronismoNovedadV5(String pUsuario, String pPassword,
			String pDeviveId, int pVersion, int pRevision, int pCompilacion)  {

		boolean _resultado;

		/*
		try {
			CrearRecovery();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		*/
		
		_resultado = EasyUtilidades.CheckConnection(context);
		if (_resultado == false) {
			RegistrarEvento("ERROR",
					"No se encuentra disponible canal de comunicaci�n al servidor WIFI o GPRS");
			// //Log.i("info",
			// "No se encuentra disponible canal de comunicaci�n al servidor WIFI o GPRS");
			return false;
		}

		usuario = pUsuario;
		password = pPassword;
		deviceId = pDeviveId;
		version = pVersion;
		revision = pRevision;
		compilacion = pCompilacion;

		//Log.i("info", "sincronimos versionv5:" + version + ".revision:"
		//		+ revision);

		RegistrarEvento("SINCRONIZANDO", "INICIO SINCRONISMO");
		SincronismoBaseDatosServicio sincronismoBaseDatosServicio = new SincronismoBaseDatosServicioImpl(gGlobal);

		RequerimientoSincronismo requerimientoSincronismo = new RequerimientoSincronismo();

		gGlobal.ultimoRequerimientoSincronismo = requerimientoSincronismo;

		requerimientoSincronismo.setDeviceId(deviceId);
		requerimientoSincronismo.setLogin(usuario);
		requerimientoSincronismo.setPassword(password);
		requerimientoSincronismo.setRevision(revision);
		requerimientoSincronismo.setSincronismoInicial(false);
		requerimientoSincronismo.setVersion(version);
		requerimientoSincronismo.setCompilacion(compilacion);
		requerimientoSincronismo.setBaseDatosES("");
		requerimientoSincronismo.setLogId(0);

		Log.i("info", "requermiento sincronismo-"+requerimientoSincronismo.toString());
		
		Respuesta respuesta = new Respuesta();
		try {
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismoURL(
					requerimientoSincronismo, "UrlEasyServer");

			if (respuesta.getObservacion().indexOf("ERROR") == -1) {
				RegistrarEvento("SINCRONIZANDO", "URL SATISFACTORIA. "
						+ respuesta.getObservacion());
				gGlobal.UrlEasyServer = respuesta.getObservacion();
			} else {
				RegistrarEvento("ERROR",
						"URL FALLIDA. " + respuesta.getObservacion());
				return false;
			}

		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "URL FALLIDA EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. JSONException." + e.getMessage()
							+ ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "URL FALLIDA EXCEPTION. JSONException." + e.getMessage()
			// + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. IOException." + e.getMessage()
							+ ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "URL FALLIDA EXCEPTION. IOException." + e.getMessage()
			// + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "URL FALLIDA EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento("ERROR",
					"URL FALLIDA EXCEPTION. Exception." + e.getMessage() + ". "
							+ e.getCause() + ". " + e.getLocalizedMessage());
			// //Log.i("info", "URL FALLIDA EXCEPTION. Exception." +
			// e.getMessage()
			// + ". " + e.getCause() + ". " + e.getLocalizedMessage());
			return false;
		}

		respuesta = new Respuesta();
		try {
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismo(
					requerimientoSincronismo, "RequerimientoSincronismoV2");
			if (respuesta.getResultado() == true) {
				RegistrarEvento(
						"SINCRONIZANDO",
						"AUTENTICACION SATISFACTORIA. "
								+ respuesta.getObservacion());
				requerimientoSincronismo.setBaseDatosES(respuesta
						.getBaseDatosES());
				requerimientoSincronismo.setLogId(respuesta.getLogId());

			} else {
				RegistrarEvento("ERROR",
						"AUTENTICACION FALLIDA. " + respuesta.getObservacion());
				return false;
			}
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// Toast.makeText(context, R.string.messageProblemasSincronizacion
			// + ". " + e.getLocalizedMessage(), Toast.LENGTH_SHORT);
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "AUTENTICACION FALLIDA EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		// //Log.i("info", "observacion respuesta. " +
		// respuesta.getObservacion());
		// //Log.i("info", "log id. " + respuesta.getLogId());
		// revisar necesidad de recovery
		//gGlobal gGlobal;
		//gGlobal = ((gGlobal) context.getApplicationContext());

		if (respuesta.getResultado() == true) {
			ObtenerNuevaVersion = gGlobal.getNuevaVersion(respuesta
					.getObservacion());
		}

		TransferirArchivos(sincronismoBaseDatosServicio,
				requerimientoSincronismo, respuesta, "Trace");

		TransferirArchivos(sincronismoBaseDatosServicio,
				requerimientoSincronismo, respuesta, "Up");

		if (gGlobal.CrearRecovery) {
			try {
				respuesta.setResultado(CrearRecovery());
				if (respuesta.getResultado()) {
					// enviar recovery por web
					RegistrarEvento("SINCRONIZANDO", "ENVIO RECOVERY");

					try {
						respuesta = SubirRecoveryBaseDatos(
								requerimientoSincronismo,
								sincronismoBaseDatosServicio,
								"EasyServerRecovery", respuesta);
					} catch (ClientProtocolException e) {
						RegistrarEvento("ERROR",
								"ENVIO RECOVERY EXCEPTION. ClientProtocolException."
										+ e.getMessage() + ". " + e.getCause()
										+ ". " + e.getLocalizedMessage());
						// //Log.i("info",
						// "ENVIO RECOVERY EXCEPTION. ClientProtocolException."
						// + e.getMessage() + ". " + e.getCause()
						// + ". " + e.getLocalizedMessage());
						// return false;
					} catch (IOException e) {
						RegistrarEvento(
								"ERROR",
								"ENVIO RECOVERY EXCEPTION. IOException."
										+ e.getMessage() + ". " + e.getCause()
										+ ". " + e.getLocalizedMessage());
						// //Log.i("info",
						// "ENVIO RECOVERY EXCEPTION. IOException."
						// + e.getMessage() + ". " + e.getCause() + ". "
						// + e.getLocalizedMessage());
						// return false;
					} catch (JSONException e) {
						RegistrarEvento(
								"ERROR",
								"ENVIO RECOVERY EXCEPTION. JSONException."
										+ e.getMessage() + ". " + e.getCause()
										+ ". " + e.getLocalizedMessage());
						// //Log.i("info",
						// "ENVIO RECOVERY EXCEPTION. JSONException."
						// + e.getMessage() + ". " + e.getCause()
						// + ". " + e.getLocalizedMessage());
						// return false;
					} catch (Exception e) {
						RegistrarEvento(
								"ERROR",
								"ENVIO RECOVERY EXCEPTION. Exception."
										+ e.getMessage() + ". " + e.getCause()
										+ ". " + e.getLocalizedMessage());
						// //Log.i("info", "ENVIO RECOVERY EXCEPTION. Exception."
						// + e.getMessage() + ". " + e.getCause() + ". "
						// + e.getLocalizedMessage());
						// return false;
					}

					RegistrarEvento(
							"SINCRONIZANDO",
							"RESPUESTA ENVIO RECOVERY. "
									+ respuesta.getObservacion());
				} else {
					RegistrarEvento("ERROR", "Problemas para crear recovery.");
					// //Log.i("info", "Problemas para crear recovery.");
				}
			} catch (Exception e) {
				RegistrarEvento(
						"ERROR",
						"Problemas para crear recovery. Exception."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "Problemas para crear recovery. Exception."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
			}
			gGlobal.CrearRecovery = false;
		}

		if (respuesta.getResultado() == true) {

			RegistrarEvento("SINCRONIZANDO", "DESCARGANDO DOWN. ");
			try {
				respuesta = DescargarBaseDatos(requerimientoSincronismo,
						sincronismoBaseDatosServicio, "EasyServerDown",
						respuesta);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				RegistrarEvento("ERROR",
						"DESCARGANDO DOWN EXCEPTION. ClientProtocolException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. ClientProtocolException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. JSONException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. JSONException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. IOException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. IOException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				RegistrarEvento(
						"ERROR",
						"DESCARGANDO DOWN EXCEPTION. Exception."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGANDO DOWN EXCEPTION. Exception."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
				return false;
			}
			RegistrarEvento("SINCRONIZANDO",
					"DESCARGANDO DOWN. " + respuesta.getObservacion());

			// //Log.i("info", "observacion respuesta descargar base de datos. "
			// + respuesta.getObservacion());
			// //Log.i("info", "log id. " + respuesta.getLogId());

			if (respuesta.getResultado() == false) {
				RegistrarEvento("ERROR",
						"DESCARGANDO DOWN. " + respuesta.getObservacion());
				return false;
			}

		} else {
			// Toast.makeText(context, respuesta.getObservacion(),
			// Toast.LENGTH_SHORT);
			RegistrarEvento("ERROR",
					"RESPUESTA DESCARGANDO DOWN. " + respuesta.getObservacion());
			return false;
		}

		RegistrarEvento("SINCRONIZANDO", "APLICAR AUXILIAR EASYSERVER. ");

		try {
			if (AplicarNovedadesDown(false)) {

			} else {
				RegistrarEvento("ERROR", "APLICAR NOVEDADES. "
						+ "Se presentaron problemas para aplicar novedades");
				respuesta.setResultado(false);
				respuesta
						.setObservacion("Se presentaron problemas para aplicar novedades");
				return false;
			}
		} catch (Exception e) {
			RegistrarEvento("ERROR", "APLICAR NOVEDADES. " + e.getMessage());
			return false;
		}

		//Log.i("info", "para aplicar novedades v5");
		AplicarNovedadesAuxiliarEasyServer();
		//Log.i("info", "fin para aplicar novedades v3");


		RegistrarEvento("SINCRONIZANDO", "REALIZANDO COMMIT. ");
		try {
			respuesta = sincronismoBaseDatosServicio
					.CommitSincronismo(requerimientoSincronismo);
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento(
					"ERROR",
					"REALIZANDO COMMIT EXCEPTION. Exception." + e.getMessage()
							+ ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "REALIZANDO COMMIT EXCEPTION. Exception." + e.getMessage()
			// + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		RegistrarEvento("SINCRONIZANDO",
				"REALIZANDO COMMIT. " + respuesta.getObservacion());

		if (respuesta.getResultado() == true) {
		} else {
			RegistrarEvento("ERROR COMMIT 1", respuesta.getObservacion());
			return false;
		}

		/*
		 * //obtener novedades de tablas up _resultado = GenerarNovedadesUp();
		 * if (!_resultado) { return false; }
		 */

		_resultado = true;

		if (_resultado) {
			// subir base de datos up
			if (respuesta.getResultado() == true) {
				RegistrarEvento("SINCRONIZANDO", "ENVIO UP");

				try {
					for (int i = 0; i <= gGlobal.numeroIntentos; ++i) {

						synchronized (ItaloDBAdapter.Lock) {

							if (gGlobal.bdEncriptada) {
								try {
									EasyUtilidades.EncriptarBD("EasyServerUp",
											"N", context);
								} catch (Exception e) {
									RegistrarEvento(
											"ERROR",
											"ENVIO UP EXCEPTION. CREAR ZIP UP. Exception."
													+ e.getMessage() + ". "
													+ e.getCause() + ". "
													+ e.getLocalizedMessage());
									return false;
								}
							}

							EasyZip easyZip = new EasyZip();
							_resultado = easyZip
									.ZippingFile(
											gGlobal.DATABASE_PATH
													+ gGlobal.DATABASE_NAME_NOVEDADES_UP,
											gGlobal.pathArchivosSync
													+ "/EasyServerUp.zip");

							if (gGlobal.bdEncriptada) {
								EasyUtilidades.BorrarBD(
										gGlobal.DATABASE_NAME_NOVEDADES_UP,
										context);
							}
						}

						if (!_resultado) {
							RegistrarEvento("SINCRONIZANDO", "ERROR EN ZIP UP.");
							return false;
						}

						respuesta = SubirBaseDatos(requerimientoSincronismo,
								sincronismoBaseDatosServicio, "EasyServerUpV2",
								respuesta);
						if (respuesta.getResultado()) {
							break;
						}
					}
					EasyUtilidades
							.BorrarArchivos(gGlobal.pathArchivosSync + "/");
				} catch (ClientProtocolException e) {
					RegistrarEvento(
							"ERROR",
							"ENVIO UP EXCEPTION. ClientProtocolException."
									+ e.getMessage() + ". " + e.getCause()
									+ ". " + e.getLocalizedMessage());
					// //Log.i("info",
					// "ENVIO UP EXCEPTION. ClientProtocolException."
					// + e.getMessage() + ". " + e.getCause()
					// + ". " + e.getLocalizedMessage());
					return false;
				} catch (IOException e) {
					RegistrarEvento(
							"ERROR",
							"ENVIO UP EXCEPTION. IOException." + e.getMessage()
									+ ". " + e.getCause() + ". "
									+ e.getLocalizedMessage());
					// //Log.i("info",
					// "ENVIO UP EXCEPTION. IOException." + e.getMessage()
					// + ". " + e.getCause() + ". "
					// + e.getLocalizedMessage());
					return false;
				} catch (JSONException e) {
					RegistrarEvento(
							"ERROR",
							"ENVIO UP EXCEPTION. JSONException."
									+ e.getMessage() + ". " + e.getCause()
									+ ". " + e.getLocalizedMessage());
					// //Log.i("info",
					// "ENVIO UP EXCEPTION. JSONException."
					// + e.getMessage() + ". " + e.getCause()
					// + ". " + e.getLocalizedMessage());
					return false;
				} catch (Exception e) {
					RegistrarEvento(
							"ERROR",
							"ENVIO UP EXCEPTION. Exception." + e.getMessage()
									+ ". " + e.getCause() + ". "
									+ e.getLocalizedMessage());
					// //Log.i("info",
					// "ENVIO UP EXCEPTION. Exception." + e.getMessage()
					// + ". " + e.getCause() + ". "
					// + e.getLocalizedMessage());
					return false;
				}

				RegistrarEvento("SINCRONIZANDO", "RESPUESTA ENVIO UP. "
						+ respuesta.getObservacion());
			} else {
				// Toast.makeText(context, respuesta.getObservacion(),
				// Toast.LENGTH_SHORT);
				return false;
			}

			// //Log.i("info",
			// "observacion respuesta. " + respuesta.getObservacion());
			// //Log.i("info", "log id. " + respuesta.getLogId());

		} else {
			RegistrarEvento("SINCRONIZANDO", "ERROR EN ZIP UP.");
			return false;
		}

		if (respuesta.getResultado() == false)
		{
			RegistrarEvento("ERROR",
					"RESPUESTA ENVIO UP. " + respuesta.getObservacion());
			return false;
		}

		respuesta = generarSolicitudSincronismo(requerimientoSincronismo,
				"RequerimientoEasyServer", "REQUERIMIENTO EASYSERVER");
		
		/*
		respuesta = generarSolicitudSincronismo(requerimientoSincronismo,
				"RequerimientoEasyServer", "REQUERIMIENTO EASYSERVER");
		if (respuesta.getResultado() == false)
			return false;
		*/

		while (true) {
			respuesta = generarSolicitudSincronismo(requerimientoSincronismo,
					"RequerimientoEasyServerEstado",
					"ESTADO REQUERIMIENTO EASYSERVER");

			if (respuesta.getResultado() == false)
				return false;

			if (respuesta.getResultado() == true) {
				if (respuesta.getObservacion().equals("OK")) {
					break;
				} else if (respuesta.getObservacion().equals("ERROR")) {
					RegistrarEvento("ERROR",
							"ESTADO REQUERIMIENTO EASYSERVER ERROR.");
					// //Log.i("info", "ESTADO REQUERIMIENTO EASYSERVER ERROR.");
					return false;
				}

			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				// //Log.i("info", "ERROR EN TIMER 1.");
			}

		}

		try {

			for (int i = 0; i <= gGlobal.numeroIntentos; ++i) {
				respuesta = DescargarBaseDatos(requerimientoSincronismo,
						sincronismoBaseDatosServicio,
						"RequerimientoEasyServerDown", respuesta);
				if (respuesta.getResultado()) {
					break;
				}
			}

			if (respuesta.getResultado() == true) {
				RegistrarEvento("SINCRONIZANDO",
						"DESCARGA BASE DE DATOS DOWN SATISFACTORIA. "
								+ respuesta.getObservacion());
			} else {
				RegistrarEvento(
						"ERROR",
						"DESCARGA BASE DE DATOS DOWN FALLIDA. "
								+ respuesta.getObservacion());
				return false;
			}
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento(
					"ERROR",
					"DESCARGA BASE DE DATOS DOWN EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "DESCARGA BASE DE DATOS DOWN EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		/*
		 * // attach base datos // AttachBaseDatosNovedades(); if
		 * (respuesta.getResultado() == true) { RegistrarEvento("SINCRONIZANDO",
		 * "PREPARAR DOWN. ");
		 * 
		 * // obtener tablas down try { ArrayList<String> listTablasDown =
		 * TablasDown();
		 * 
		 * for (String _tabla : listTablasDown) {
		 * RegistrarEvento("SINCRONIZANDO", "PREPARAR TABLA DOWN " + _tabla); //
		 * ////Log.i("info", "PREPARAR TABLA DOWN " + _tabla); try {
		 * requerimientoSincronismo.setPassword(_tabla); respuesta =
		 * sincronismoBaseDatosServicio
		 * .InstanciaSincronismo(requerimientoSincronismo,
		 * "PrepararEasyServerDownTabla");
		 * 
		 * } catch (ClientProtocolException e) { RegistrarEvento( "ERROR",
		 * "PREPARAR DOWN TABLA EXCEPTION " + _tabla +
		 * ". ClientProtocolException." + e.getMessage() + ". " + e.getCause() +
		 * ". " + e.getLocalizedMessage()); ////Log.i("info",
		 * "PREPARAR DOWN TABLA EXCEPTION " + _tabla +
		 * ". ClientProtocolException." + e.getMessage() + ". " + e.getCause() +
		 * ". " + e.getLocalizedMessage()); return false; } catch (JSONException
		 * e) { RegistrarEvento( "ERROR", "PREPARAR DOWN TABLA EXCEPTION " +
		 * _tabla + ". JSONException." + e.getMessage() + ". " + e.getCause() +
		 * ". " + e.getLocalizedMessage()); ////Log.i("info",
		 * "PREPARAR DOWN TABLA EXCEPTION " + _tabla + ". JSONException." +
		 * e.getMessage() + ". " + e.getCause() + ". " +
		 * e.getLocalizedMessage()); return false; } catch (IOException e) {
		 * RegistrarEvento( "ERROR", "PREPARAR DOWN TABLA EXCEPTION " + _tabla +
		 * ". IOException." + e.getMessage() + ". " + e.getCause() + ". " +
		 * e.getLocalizedMessage()); ////Log.i("info",
		 * "PREPARAR DOWN TABLA EXCEPTION " + _tabla + ". IOException." +
		 * e.getMessage() + ". " + e.getCause() + ". " +
		 * e.getLocalizedMessage()); return false; } catch (Exception e) {
		 * RegistrarEvento( "ERROR", "PREPARAR DOWN TABLA EXCEPTION " + _tabla +
		 * ". Exception." + e.getMessage() + ". " + e.getCause() + ". " +
		 * e.getLocalizedMessage()); ////Log.i("info",
		 * "PREPARAR DOWN TABLA EXCEPTION " + _tabla + ". Exception." +
		 * e.getMessage() + ". " + e.getCause() + ". " +
		 * e.getLocalizedMessage()); return false; }
		 * 
		 * }
		 * 
		 * } catch (Exception e1) { // TODO Auto-generated catch block
		 * RegistrarEvento( "SINCRONIZANDO",
		 * "ERROR EN OBTENER LISTADO TABLAS DOWN." + e1.getMessage()); return
		 * false; }
		 * 
		 * }
		 * 
		 * if (respuesta.getResultado() == true) {
		 * RegistrarEvento("SINCRONIZANDO", "DESCARGANDO DOWN. "); try {
		 * respuesta = DescargarBaseDatos(requerimientoSincronismo,
		 * sincronismoBaseDatosServicio, "EasyServerDown", respuesta); } catch
		 * (ClientProtocolException e) { RegistrarEvento("ERROR",
		 * "DESCARGANDO DOWN EXCEPTION. ClientProtocolException." +
		 * e.getMessage() + ". " + e.getCause() + ". " +
		 * e.getLocalizedMessage()); ////Log.i("info",
		 * "DESCARGANDO DOWN EXCEPTION. ClientProtocolException." +
		 * e.getMessage() + ". " + e.getCause() + ". " +
		 * e.getLocalizedMessage()); return false; } catch (JSONException e) {
		 * RegistrarEvento( "ERROR",
		 * "DESCARGANDO DOWN EXCEPTION. JSONException." + e.getMessage() + ". "
		 * + e.getCause() + ". " + e.getLocalizedMessage()); ////Log.i("info",
		 * "DESCARGANDO DOWN EXCEPTION. JSONException." + e.getMessage() + ". "
		 * + e.getCause() + ". " + e.getLocalizedMessage()); return false; }
		 * catch (IOException e) { RegistrarEvento( "ERROR",
		 * "DESCARGANDO DOWN EXCEPTION. IOException." + e.getMessage() + ". " +
		 * e.getCause() + ". " + e.getLocalizedMessage()); ////Log.i("info",
		 * "DESCARGANDO DOWN EXCEPTION. IOException." + e.getMessage() + ". " +
		 * e.getCause() + ". " + e.getLocalizedMessage()); return false; } catch
		 * (Exception e) { RegistrarEvento( "ERROR",
		 * "DESCARGANDO DOWN EXCEPTION. Exception." + e.getMessage() + ". " +
		 * e.getCause() + ". " + e.getLocalizedMessage()); ////Log.i("info",
		 * "DESCARGANDO DOWN EXCEPTION. Exception." + e.getMessage() + ". " +
		 * e.getCause() + ". " + e.getLocalizedMessage()); return false; }
		 * RegistrarEvento("SINCRONIZANDO", "DESCARGANDO DOWN. " +
		 * respuesta.getObservacion());
		 * 
		 * ////Log.i("info", "observacion respuesta descargar base de datos. " +
		 * respuesta.getObservacion()); ////Log.i("info", "log id. " +
		 * respuesta.getLogId());
		 * 
		 * if (respuesta.getResultado() == false) { RegistrarEvento("ERROR",
		 * "DESCARGANDO DOWN. " + respuesta.getObservacion()); return false; }
		 * 
		 * } else { // Toast.makeText(context, respuesta.getObservacion(), //
		 * Toast.LENGTH_SHORT); RegistrarEvento("ERROR", "PREPARAR DOWN. " +
		 * respuesta.getObservacion()); return false; }
		 * 
		 * /* while (true) { if (!AplicarNovedadesDown()) {
		 * RegistrarEvento("ERROR", "APLICAR NOVEDADES. " +
		 * respuesta.getObservacion()); break; } }
		 */

		RegistrarEvento("SINCRONIZANDO", "APLICAR NOVEDADES. ");
		try {
			if (AplicarNovedadesDown(true)) {

			} else {
				RegistrarEvento("ERROR", "APLICAR NOVEDADES. "
						+ "Se presentaron problemas para aplicar novedades");
				respuesta.setResultado(false);
				respuesta
						.setObservacion("Se presentaron problemas para aplicar novedades");
				return false;
			}
		} catch (Exception e) {
			RegistrarEvento("ERROR", "APLICAR NOVEDADES. " + e.getMessage());
			return false;
		}

		RegistrarEvento("SINCRONIZANDO", "APLICAR AUXILIAR EASYSERVER. ");

		// AplicarNovedadesAuxiliarEasyServer();

		RegistrarEvento("SINCRONIZANDO", "REALIZANDO COMMIT. ");
		/*
		 * try { respuesta = sincronismoBaseDatosServicio
		 * .CommitSincronismo(requerimientoSincronismo); } catch
		 * (ClientProtocolException e) { RegistrarEvento( "ERROR",
		 * "REALIZANDO COMMIT EXCEPTION. ClientProtocolException." +
		 * e.getMessage() + ". " + e.getCause() + ". " +
		 * e.getLocalizedMessage()); ////Log.i("info",
		 * "REALIZANDO COMMIT EXCEPTION. ClientProtocolException." +
		 * e.getMessage() + ". " + e.getCause() + ". " +
		 * e.getLocalizedMessage()); return false; } catch (JSONException e) {
		 * RegistrarEvento( "ERROR",
		 * "REALIZANDO COMMIT EXCEPTION. JSONException." + e.getMessage() + ". "
		 * + e.getCause() + ". " + e.getLocalizedMessage()); ////Log.i("info",
		 * "REALIZANDO COMMIT EXCEPTION. JSONException." + e.getMessage() + ". "
		 * + e.getCause() + ". " + e.getLocalizedMessage()); return false; }
		 * catch (IOException e) { RegistrarEvento( "ERROR",
		 * "REALIZANDO COMMIT EXCEPTION. IOException." + e.getMessage() + ". " +
		 * e.getCause() + ". " + e.getLocalizedMessage()); ////Log.i("info",
		 * "REALIZANDO COMMIT EXCEPTION. IOException." + e.getMessage() + ". " +
		 * e.getCause() + ". " + e.getLocalizedMessage()); return false; } catch
		 * (URISyntaxException e) { RegistrarEvento( "ERROR",
		 * "REALIZANDO COMMIT EXCEPTION. URISyntaxException." + e.getMessage() +
		 * ". " + e.getCause() + ". " + e.getLocalizedMessage());
		 * ////Log.i("info", "REALIZANDO COMMIT EXCEPTION. URISyntaxException." +
		 * e.getMessage() + ". " + e.getCause() + ". " +
		 * e.getLocalizedMessage()); return false; } catch (Exception e) {
		 * RegistrarEvento( "ERROR", "REALIZANDO COMMIT EXCEPTION. Exception." +
		 * e.getMessage() + ". " + e.getCause() + ". " +
		 * e.getLocalizedMessage()); ////Log.i("info",
		 * "REALIZANDO COMMIT EXCEPTION. Exception." + e.getMessage() + ". " +
		 * e.getCause() + ". " + e.getLocalizedMessage()); return false; }
		 * 
		 * RegistrarEvento("SINCRONIZANDO", "REALIZANDO COMMIT. " +
		 * respuesta.getObservacion());
		 * 
		 * if (respuesta.getResultado() == true) { RegistrarEvento("OK", "OK");
		 * } else { RegistrarEvento("ERROR COMMIT", respuesta.getObservacion());
		 * }
		 */
		respuesta = generarSolicitudSincronismo(requerimientoSincronismo,
				"RequerimientoCommit", "REQUERIMIENTO COMMIT");
		if (respuesta.getResultado() == false)
			return false;

		while (true) {
			respuesta = generarSolicitudSincronismo(requerimientoSincronismo,
					"RequerimientoCommitEstado", "ESTADO REQUERIMIENTO COMMIT");

			if (respuesta.getResultado() == false)
				return false;

			if (respuesta.getResultado() == true) {
				if (respuesta.getObservacion().equals("OK")) {
					break;
				} else if (respuesta.getObservacion().equals("ERROR")) {
					RegistrarEvento("ERROR",
							"ESTADO REQUERIMIENTO COMMIT ERROR.");
					// //Log.i("info", "ESTADO REQUERIMIENTO COMMIT ERROR.");
					return false;
				}

			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				// //Log.i("info", "ERROR EN TIMER 1.");
			}

		}

		try {
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismo(
					requerimientoSincronismo, "FinSincronismo");
			if (respuesta.getResultado() == true) {
				RegistrarEvento(
						"SINCRONIZANDO",
						"FIN SINCRONISMO SATISFACTORIA. "
								+ respuesta.getObservacion());
			} else {
				RegistrarEvento("ERROR", "FIN SINCRONISMO FALLIDA. "
						+ respuesta.getObservacion());
			}
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// Toast.makeText(context, R.string.messageProblemasSincronizacion
			// + ". " + e.getLocalizedMessage(), Toast.LENGTH_SHORT);
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}

		if (respuesta.getResultado() == true) {
			RegistrarEvento("OK", "OK");
		} else {
			RegistrarEvento("ERROR FIN SINCRONISMO ",
					"FALLA EN FIN SINCRONISMO");
		}

		return respuesta.getResultado();

	}

	private Respuesta generarEnviarEvento(
			RequerimientoSincronismo pRequerimientoSincronismo,
			String pServicio, String pProceso) {

		SincronismoBaseDatosServicio sincronismoBaseDatosServicio = new SincronismoBaseDatosServicioImpl(gGlobal);

		Respuesta respuesta = new Respuesta();
		respuesta.setLogId(pRequerimientoSincronismo.getLogId());
		respuesta.setObservacion("");
		respuesta.setResultado(false);

		try {
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismo(
					pRequerimientoSincronismo, pServicio);

			// //Log.i("info",
			// pProceso + " ENVIO EVENTO. Resultado:"
			// + respuesta.getResultado() + ".Observacion:"
			// + respuesta.getObservacion());

		} catch (ClientProtocolException e) {
			// //Log.i("info",
			// pProceso + " FALLIDA EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			// return false;
		} catch (JSONException e) {
			// //Log.i("info",
			// pProceso + " FALLIDA EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			// return false;
		} catch (IOException e) {
			// //Log.i("info",
			// pProceso + " FALLIDA EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			// return false;
		} catch (URISyntaxException e) {
			// //Log.i("info",
			// pProceso + " FALLIDA EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			// return false;
		} catch (Exception e) {
			// //Log.i("info",
			// pProceso + " FALLIDA EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			// return false;
		}

		return respuesta;

	}

	private Respuesta generarSolicitudSincronismo(
			RequerimientoSincronismo pRequerimientoSincronismo,
			String pServicio, String pProceso) {

		SincronismoBaseDatosServicio sincronismoBaseDatosServicio = new SincronismoBaseDatosServicioImpl(gGlobal);

		Respuesta respuesta = new Respuesta();
		respuesta.setLogId(pRequerimientoSincronismo.getLogId());
		respuesta.setObservacion("");
		respuesta.setResultado(false);

		try {
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismo(
					pRequerimientoSincronismo, pServicio);

			if (respuesta.getResultado() == true) {
				RegistrarEvento("SINCRONIZANDO",
						pProceso + ". " + respuesta.getObservacion());
			} else {
				RegistrarEvento("ERROR",
						pProceso + " FALLIDA. " + respuesta.getObservacion());
				// return false;
			}

		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					pProceso + " FALLIDA EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// pProceso + " FALLIDA EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			// return false;
		} catch (JSONException e) {
			RegistrarEvento("ERROR", pProceso
					+ " FALLIDA EXCEPTION. JSONException." + e.getMessage()
					+ ". " + e.getCause() + ". " + e.getLocalizedMessage());
			// //Log.i("info",
			// pProceso + " FALLIDA EXCEPTION. JSONException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			// return false;
		} catch (IOException e) {
			RegistrarEvento("ERROR", pProceso
					+ " FALLIDA EXCEPTION. IOException." + e.getMessage()
					+ ". " + e.getCause() + ". " + e.getLocalizedMessage());
			// //Log.i("info",
			// pProceso + " FALLIDA EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			// return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					pProceso + " FALLIDA EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// pProceso + " FALLIDA EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			// return false;
		} catch (Exception e) {
			RegistrarEvento("ERROR", pProceso
					+ " FALLIDA EXCEPTION. Exception." + e.getMessage() + ". "
					+ e.getCause() + ". " + e.getLocalizedMessage());
			// //Log.i("info",
			// pProceso + " FALLIDA EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			// return false;
		}

		return respuesta;

	}

	private boolean TransferirArchivos(
			SincronismoBaseDatosServicio pSincronismoBaseDatosServicio,
			RequerimientoSincronismo pRequerimientoSincronismo,
			Respuesta pRespuesta, String pTipo) {
		try {
			RegistrarEvento("ENVIOTRACE", "INICIO ENVIO TRACE. ");

			// obtener listado de archivos a tranferir
			Respuesta _respuesta = new Respuesta();
			// //Log.i("info", "obtener lisatado de archivos");

			ArrayList<String> listArchivos = new ArrayList<String>();
			if (pTipo.equals("Trace")) {

				listArchivos = EasyUtilidades
						.ListadoArchivos(
								Environment.getExternalStorageDirectory()
										+ "/download", "stacktrace");
			} else if (pTipo.equals("Up")) {
				listArchivos = EasyUtilidades.ListadoArchivos(
						gGlobal.pathArchivosEasy + "/Up", "*");
			}

			////Log.i("info", "revisar lisatado de archivos");

			for (int i = 0; i < listArchivos.size(); ++i) {
				String _file = listArchivos.get(i);
				String _extension = _file.substring(_file.lastIndexOf(".") + 1);
				String _nombreFile = _file.substring(0, _file.lastIndexOf("."));

				////Log.i("info", "transferir archivo-" + _file + "-nombre-"
				//		+ _nombreFile + "-extension-" + _extension);

				try {
					_respuesta = SubirEasyServerFile(pRequerimientoSincronismo,
							pSincronismoBaseDatosServicio, "EasyServerFile",
							_nombreFile, _extension, pTipo, _file, pRespuesta);

					if (_respuesta.getResultado()) {
						// borrar archivo transferido

						File file;

						if (pTipo.equals("Trace")) {
							file = new File(
									Environment.getExternalStorageDirectory()
											+ "/download/" + _file);
							file.delete();
						} else if (pTipo.equals("Up")) {
							file = new File(gGlobal.pathArchivosEasy + "/Up/"
									+ _file);
							file.delete();
						}
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				RegistrarEvento("ENVIOTRACE", "RESPUESTA ENVIO TRACE. "
						+ _respuesta.getObservacion());

			}
		} catch (Exception ex) {
			RegistrarEvento("ENVIOTRACE", "FIN ENVIO TRACE CON NOVEDADES. ");
			return false;
		}
		RegistrarEvento("ENVIOTRACE", "FIN ENVIO TRACE. ");
		return true;
	}

	public Respuesta SubirEasyServerFile(
			RequerimientoSincronismo pRequerimientoSincronismo,
			SincronismoBaseDatosServicio pSincronismoBaseDatosServicio,
			String pServicio, String pNombreArchivo, String pExtension,
			String pTipo, String pArchivo, Respuesta pRespuesta)
			throws ClientProtocolException, IOException, JSONException,
			Exception {

		// //Log.i("info", "transferencia easyserverfile");
		Respuesta _respuesta = pSincronismoBaseDatosServicio
				.UpLoadEasyServerFile(pRespuesta.getLogId(),
						pRequerimientoSincronismo.getDeviceId(), pServicio,
						pNombreArchivo, pExtension, pTipo, pArchivo);

		pRespuesta.setResultado(_respuesta.getResultado());
		pRespuesta.setObservacion(_respuesta.getObservacion());

		// //Log.i("info", "fin transferencia easyserverfile");

		return pRespuesta;
	}

	
	public boolean SincronismoFile(String pUsuario, String pPassword,
			String pDeviveId, int pVersion, int pRevision, int pCompilacion) {

		boolean _resultado;

		_resultado = EasyUtilidades.CheckConnection(context);
		if (_resultado == false) {
			RegistrarEvento("ERROR",
					"No se encuentra disponible canal de comunicaci�n al servidor WIFI o GPRS");
			// //Log.i("info",
			// "No se encuentra disponible canal de comunicaci�n al servidor WIFI o GPRS");
			return false;
		}

		usuario = pUsuario;
		password = pPassword;
		deviceId = pDeviveId;
		version = pVersion;
		revision = pRevision;
		compilacion = pCompilacion;

		//Log.i("info", "sincronimos file versionv4:" + version + ".revision:"
		//		+ revision + ".compilacion:" + compilacion);

		RegistrarEvento("SINCRONIZANDO", "INICIO SINCRONISMO FILE");
		SincronismoBaseDatosServicio sincronismoBaseDatosServicio = new SincronismoBaseDatosServicioImpl(gGlobal);

		RequerimientoSincronismo requerimientoSincronismo = new RequerimientoSincronismo();

		gGlobal.ultimoRequerimientoSincronismoFile = requerimientoSincronismo;

		requerimientoSincronismo.setDeviceId(deviceId);
		requerimientoSincronismo.setLogin(usuario);
		requerimientoSincronismo.setPassword(password);
		requerimientoSincronismo.setRevision(revision);
		requerimientoSincronismo.setSincronismoInicial(false);
		requerimientoSincronismo.setVersion(version);
		requerimientoSincronismo.setCompilacion(compilacion);
		requerimientoSincronismo.setBaseDatosES("");
		requerimientoSincronismo.setLogId(0);
		requerimientoSincronismo.setObservacion("File");

		Respuesta respuesta = new Respuesta();
		try {
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismoURL(
					requerimientoSincronismo, "UrlEasyServer");

			if (respuesta.getObservacion().indexOf("ERROR") == -1) {
				RegistrarEvento("SINCRONIZANDO", "URL SATISFACTORIA. "
						+ respuesta.getObservacion());
				gGlobal.UrlEasyServer = respuesta.getObservacion();
			} else {
				RegistrarEvento("ERROR",
						"URL FALLIDA. " + respuesta.getObservacion());
				return false;
			}

		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			return false;
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. JSONException." + e.getMessage()
							+ ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. IOException." + e.getMessage()
							+ ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"URL FALLIDA EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento("ERROR",
					"URL FALLIDA EXCEPTION. Exception." + e.getMessage() + ". "
							+ e.getCause() + ". " + e.getLocalizedMessage());
			return false;
		}

		respuesta = new Respuesta();
		try {
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismo(
					requerimientoSincronismo, "RequerimientoSincronismoV5");

			if (respuesta.getResultado() == true) {
				RegistrarEvento(
						"SINCRONIZANDO",
						"AUTENTICACION SATISFACTORIA. "
								+ respuesta.getObservacion());
				requerimientoSincronismo.setBaseDatosES(respuesta
						.getBaseDatosES());
				requerimientoSincronismo.setLogId(respuesta.getLogId());

			} else {
				RegistrarEvento("ERROR",
						"AUTENTICACION FALLIDA. " + respuesta.getObservacion());
			}
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			return false;
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			RegistrarEvento(
					"ERROR",
					"AUTENTICACION FALLIDA EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			return false;
		}

		List<FileSales> fileListSync = ObtenerFilesSync();
		
		//Log.i("info", "descragar archivos");
		
		boolean descargaTodosArchivos = true;
		
		//descargar archivos
		for(FileSales _fileSales : fileListSync)
		{
			try {
				
				if ((EasyUtilidades.TipoConnectionWiFi(context))
						|| (!(EasyUtilidades.TipoConnectionWiFi(context))&&(_fileSales.getSolo_descarga_wifi().equals("N")))
					)
				{
					RegistrarEvento("SINCRONIZANDO",
							"DESCARGA FILE . PATH: " + _fileSales.getFile_path_relative() + ". FILE:" + _fileSales.getFile_name());
					respuesta = DescargarFile(requerimientoSincronismo,
							sincronismoBaseDatosServicio, "EasyServerFileDown", _fileSales, respuesta);
					if (respuesta.getResultado() == true) {
						RegistrarEvento("SINCRONIZANDO",
								"DESCARGA FILE SATISFACTORIA. "
										+ respuesta.getObservacion());
						
						EasySyncFileEvaluacion _easySyncFileEvaluacion = new EasySyncFileEvaluacion();
						boolean _revisionFile = _easySyncFileEvaluacion.EvaluarArchivo(_fileSales.getFile_name(), context, _fileSales);
						
						if (!_revisionFile)
						{
							//Log.i("info", "borrar archivo");
							File file = new File(_fileSales.PathFull());
							file.delete();
							//Log.i("info", "borrar archivo-borrado");
						}
						else
						{
							//Log.i("info", "borrar archivo-ok");
							
						}
						
					} else {
						RegistrarEvento(
								"ERROR",
								"DESCARGA FILE FALLIDA. "
										+ respuesta.getObservacion());
					}
				}
				else
				{
					descargaTodosArchivos = false;
				}
			} catch (ClientProtocolException e) {
				RegistrarEvento(
						"ERROR",
						"DESCARGA FILE EXCEPTION. ClientProtocolException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGA BASE DE DATOS DOWN EXCEPTION. ClientProtocolException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
			} catch (JSONException e) {
				RegistrarEvento(
						"ERROR",
						"DESCARGA FILE EXCEPTION. JSONException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGA BASE DE DATOS DOWN EXCEPTION. JSONException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
			} catch (IOException e) {
				RegistrarEvento(
						"ERROR",
						"DESCARGA FILE EXCEPTION. IOException."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGA BASE DE DATOS DOWN EXCEPTION. IOException."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
			} catch (Exception e) {
				RegistrarEvento(
						"ERROR",
						"DESCARGA FILE EXCEPTION. Exception."
								+ e.getMessage() + ". " + e.getCause() + ". "
								+ e.getLocalizedMessage());
				// //Log.i("info",
				// "DESCARGA BASE DE DATOS DOWN EXCEPTION. Exception."
				// + e.getMessage() + ". " + e.getCause() + ". "
				// + e.getLocalizedMessage());
			}		
		}

		//Log.i("info", "para fin sincronismos file");
		
		try {
			requerimientoSincronismo.setObservacion("File");
			respuesta = sincronismoBaseDatosServicio.SolicitudSincronismo(
					requerimientoSincronismo, "FinSincronismo");
			if (respuesta.getResultado() == true) {
				RegistrarEvento(
						"SINCRONIZANDO",
						"FIN SINCRONISMO SATISFACTORIA. "
								+ respuesta.getObservacion());
				requerimientoSincronismo.setObservacion("");
			} else {
				RegistrarEvento("ERROR", "FIN SINCRONISMO FALLIDA. "
						+ respuesta.getObservacion());
			}
		} catch (JSONException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. JSONException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			return false;
		} catch (ClientProtocolException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. ClientProtocolException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. ClientProtocolException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. IOException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. IOException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (URISyntaxException e) {
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. URISyntaxException."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. URISyntaxException."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// Toast.makeText(context, R.string.messageProblemasSincronizacion
			// + ". " + e.getLocalizedMessage(), Toast.LENGTH_SHORT);
			RegistrarEvento(
					"ERROR",
					"FIN SINCRONISMO FALLIDA EXCEPTION. Exception."
							+ e.getMessage() + ". " + e.getCause() + ". "
							+ e.getLocalizedMessage());
			// //Log.i("info",
			// "FIN SINCRONISMO FALLIDA EXCEPTION. Exception."
			// + e.getMessage() + ". " + e.getCause() + ". "
			// + e.getLocalizedMessage());
			return false;
		}
		//Log.i("info", "para fin sincronismos file-"+respuesta.getResultado());

		if (respuesta.getResultado() == true) {
			if (descargaTodosArchivos)
			{
				RegistrarEvento("OK", "OK");
			}
			else
			{
				RegistrarEvento("OK", "OK. Algunos archivos no se descargaron por no estar conectado a WIFI");				
			}
		} else {
			RegistrarEvento("ERROR FIN SINCRONISMO ",
					"FALLA EN FIN SINCRONISMO");
		}

		return respuesta.getResultado();

	}

	private List<FileSales> ObtenerFilesSync() {
		try
		{

			EasySyncDB easySyncDB;		
			easySyncDB = new EasySyncDB(context);
			
	
			List<FileSales> _listFile = easySyncDB.getFileList("");
			
			//Log.i("info", "registro a evaluar files-"+_listFile.size());
			
			if (_listFile.size() > 0)
			{
				int _indiceActual = 1;

				//eliminar archivos duplicados
				if (_listFile.size()>1)
				{
					_indiceActual = 1;
					FileSales _fileSalesAnt = _listFile.get(0);
					
					do 
					{
						FileSales _fileSales = _listFile.get(_indiceActual);
						
						if (_fileSalesAnt.getFile_path_relative().equals(_fileSales.getFile_path_relative())
						&& 	_fileSalesAnt.getFile_name().equals(_fileSales.getFile_name()))
						{
							_listFile.remove(_indiceActual);
						}
						else
						{
							_fileSalesAnt = _listFile.get(_indiceActual);
							++_indiceActual;
						}
						
					}while (_indiceActual < _listFile.size());
				}
				
				//verificar que los archivos existan
				_indiceActual = 0;
				
				do 
				{
					FileSales _fileSales = _listFile.get(_indiceActual);
					boolean _descargarArchivo = EasyUtilidades.descargarArchivo( _fileSales);
					
					if (!(_descargarArchivo))
					{
						_listFile.remove(_indiceActual);
					}
					else
					{
						++_indiceActual;
					}
					
				}while (_indiceActual < _listFile.size());
			}
			
			//Log.i("info", "tamano arreglo-"+_listFile.size());
			easySyncDB.close();
			
			return _listFile;
		}
		catch(Exception ex)
		{
			gGlobal.ultimoRequerimientoSincronismoFile.setObservacion("Error evaluar archivos. " + ex.getMessage());
			//Log.i("info", gGlobal.ultimoRequerimientoSincronismoFile.getObservacion());
			return new ArrayList<FileSales>();
		}
	}


	public Respuesta DescargarFile(
			RequerimientoSincronismo pRequerimientoSincronismo,
			SincronismoBaseDatosServicio pSincronismoBaseDatosServicio,
			String pServicio, FileSales pFileSales, Respuesta pRespuesta)
			throws ClientProtocolException, JSONException, IOException,
			Exception {

		String sincronismoInicial;
		if (pRequerimientoSincronismo.getSincronismoInicial()) {
			sincronismoInicial = "S";
		} else {
			sincronismoInicial = "N";

		}

		Respuesta _respuesta = pSincronismoBaseDatosServicio.DownLoadFile(
				pRespuesta.getLogId(), pRequerimientoSincronismo.getDeviceId(),
				pServicio, sincronismoInicial, pFileSales, this);

		pRespuesta.setResultado(_respuesta.getResultado());
		pRespuesta.setObservacion(_respuesta.getObservacion());

		return pRespuesta;
	}
	
	
}
