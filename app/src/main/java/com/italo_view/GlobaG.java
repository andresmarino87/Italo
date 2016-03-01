package com.italo_view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import easysync.RequerimientoSincronismo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

@SuppressWarnings("unused")
public class GlobaG implements Serializable {
	// inicio angel
	public static final int Version = 1;
	public static final int Revision = 0;
//	public static final int Compilacion = 1;
	public static final int Compilacion = 107;
	public static final boolean bdEncriptada = false;
	public static final int numeroIntentos = 6;	
	static Context context;
	
	public static final boolean SincronismoProduccion = true;
//	public static final String UrlEasyServerInicial = "http://181.48.111.186/EasyServerItalo";
//	public static String UrlEasyServer = "http://181.48.111.186/EasyServerItalo";
//	public static final String UrlEasyServerInicial = "http://200.91.233.254/EasyServerItalo";
//	public static String UrlEasyServer = "http://200.91.233.254/EasyServerItalo";
//	public static final String UrlEasyServerInicial = "http://kairos.comestiblesitalo.com/EasyServerItalo";
//	public static String UrlEasyServer = "http://kairos.comestiblesitalo.com/EasyServerItalo";
	public static final String UrlEasyServerInicial = "http://olam.comestiblesitalo.com/EasyServerItalo";
	public static String UrlEasyServer = "http://olam.comestiblesitalo.com/EasyServerItalo";
	public static final String UrlEasyServerInicialPruebas = "http://10.0.2.2/EasyServerItalo";
	public static String UrlEasyServerPruebas = "http://10.0.2.2/EasyServerItalo";
	public static String NombreRecovery = "EasySalesDBRecovery.zip";
	//public static String pathArchivosSync = "/data/data/com.easynet.easysales/download";
	public static String pathArchivosSync =  Environment.getExternalStorageDirectory() + "/download/downloaditalo";
	public static final String pathArchivosEasy = Environment.getExternalStorageDirectory() + "/download/italo"; 	
	public static String KeyBD = "qazwer1029";
	public static String nombreZipDown = "EasyServerDown.zip";
	public static final String DATABASE_NAME = "bd_italo.s3db";
	public static final String DATABASE_NAME_NOVEDADES_DOWN = "bd_italo_novedades_down.s3db";
	public static final String DATABASE_NAME_NOVEDADES_UP = "bd_italo_novedades_up.s3db";
	public static final String FILE_DATABASE_ZIP = "bd_easy_sales.zip";
	public static String DATABASE_PATH = "/data/data/com.italo_view/databases/";	
	public static String nombreApk = "Italo.apk";
	
	public RequerimientoSincronismo ultimoRequerimientoSincronismoFile = null;

	public static String getUrlEasyServerInicial(){
		if (GlobaG.SincronismoProduccion){
			return GlobaG.UrlEasyServerInicial;
		}else{
			return GlobaG.UrlEasyServerInicialPruebas;
		}
	}

	public static String getUrlEasyServer(){
		if (GlobaG.SincronismoProduccion){
			return GlobaG.UrlEasyServer;
		}else{
			return GlobaG.UrlEasyServerInicialPruebas;
		}
	}

	public static void setUrlEasyServer(String pUrl)
	{
		if (GlobaG.SincronismoProduccion){
			GlobaG.UrlEasyServer = pUrl;
		}else{
		}
	}
	
	public static final int CantidadRegistros = 1000;
	public static final int CantidadRegistrosNotificacion = 100;
	
	public int orientacion = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

	public int getOrientacion() {
		return orientacion;
	}

	public void setOrientacion(int orientacion) {
		this.orientacion = orientacion;
	}

	public static RequerimientoSincronismo ultimoRequerimientoSincronismo = null;

	public boolean ActualizacionDescargada = false;

	public boolean CrearRecovery = false;

	ArrayList<Activity> arrActividades = new ArrayList<Activity>();
	private boolean homeActivado = false;

	ArrayList<Activity> arrActividadesRunning = new ArrayList<Activity>();

	public void AdicionarActividad(Activity pActividad) {
		if (arrActividades.size() > 0
				&& arrActividades.get(arrActividades.size() - 1).equals(
						pActividad)) {
			return;
		}

		arrActividades.add(pActividad);
	}

	public void AdicionarActividadRunning(Activity pActividad) {
		
		Log.i("info", "cargar actividad" + pActividad.getPackageResourcePath());
		
		if (arrActividadesRunning.size()!=0)
		{
			pActividad.setRequestedOrientation(orientacion);
		}
		
		//arrActividadesRunning.add(pActividad);			
		if (arrActividadesRunning.size() == 0)
		{
			arrActividadesRunning.add(pActividad);			
		}
	}

	public void RemoverActividadRunning() {
		arrActividadesRunning.remove(arrActividadesRunning.size() - 1);
	}

	public boolean EvaluarDestruirActividad(Activity pActividad) {
		if (!homeActivado) {
			return false;
		} else {
			if (arrActividades.size() > 0
					&& arrActividades.get(arrActividades.size() - 1).equals(
							pActividad)) {
				homeActivado = false;
				return false;
			} else {
				return true;
			}
		}
	}

	public void DestruirActividad(Activity pActividad) {
		if (arrActividades.size() > 0
				&& arrActividades.get(arrActividades.size() - 1).equals(
						pActividad)) {
			arrActividades.remove(arrActividades.size() - 1);
		}
	}

	public ArrayList<Activity> getArrActividades() {
		return arrActividades;
	}

	public void setArrActividades(ArrayList<Activity> arrActividades) {
		this.arrActividades = arrActividades;
	}

	public boolean isHomeActivado() {
		return homeActivado;
	}

	public void setHomeActivado(boolean homeActivado) {
		this.homeActivado = homeActivado;
	}

	public static boolean getNuevaVersion(String VersionDisponible) {
		String _versionActual = Version + "." + Revision + "." + Compilacion;

		if (_versionActual.equals(VersionDisponible)) {
			return false;
		} else {
			return true;
		}

	}

	public static String getVersion() {
//		return context.getString(R.string.version)+": " + Version + "." + Revision + "." + Compilacion;
		return "Versión: " + Version + "." + Revision + "." + Compilacion;
	}

	private boolean sincronismo_inicial = false;

	public boolean getSincronismoInicial() {
		return this.sincronismo_inicial;
	}

	public void setSincronismoInicial(boolean value) {
		this.sincronismo_inicial = value;
	}

	private static String login_dm = "";

	public static String getLoginDM() {
		return login_dm.trim();
	}

	public void setLoginDM(String value) {
		login_dm = value.trim();
	}

	private String device_id_dm = "";

	public String getDeviceIdDM() {
		// if (this.device_id_dm.compareTo("9774D56D682E549C") == 0) {
		// return this.device_id_dm + this.login_dm;
		// } else {
		return this.device_id_dm.trim();
		// }
	}

	public void setDeviceIdDM(String value) {
		this.device_id_dm = value.trim();
	}

	private String usuario_comentario = "";

	public String getUsuario_comentario() {
		return usuario_comentario;
	}

	public void setUsuario_comentario(String usuario_comentario) {
		this.usuario_comentario = usuario_comentario;
	}

	private String nombre_usuario = "";

	public String getNombreUsuario() {
		return this.nombre_usuario;
	}

	public void setNombreUsuario(String value) {
		this.nombre_usuario = value;
	}

	private String nombre_empresa = "";

	public String getNombreEmpresa() {
		return this.nombre_empresa;
	}

	public void setNombreEmpresa(String value) {
		this.nombre_empresa = value;
	}

	private String password_dm = "";

	public String getPasswordDM() {
		return this.password_dm;
	}

	public void setPasswordDM(String value) {
		this.password_dm = value;
	}

	private String easy_asignacion_id = "";

	public String getEasyAsignacionId() {
		return this.easy_asignacion_id;
	}

	public void setEasyAsignacionId(String value) {
		this.easy_asignacion_id = value;
	}

	// /fin angel


	// handler listener
	private String localPath = Environment.getExternalStorageDirectory().toString() + "/download";

	String agente_vendedor = "";

	public String getAgente_vendedor() {
		return agente_vendedor;
	}

	public void setAgente_vendedor(String agente_vendedor) {
		this.agente_vendedor = agente_vendedor;
	}

	
}

