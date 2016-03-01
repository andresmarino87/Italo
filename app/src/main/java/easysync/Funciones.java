package easysync;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * @author Luz Dary M�ndez Uribe
 **/

public class Funciones {

	int cifrasDecimales = 2;

	public int getCifrasDecimales() {
		if (cifrasDecimales == 0) {
			cifrasDecimales = 2;
		}
		return cifrasDecimales;
	}

	public void setCifrasDecimales(int cifrasDecimales) {
		this.cifrasDecimales = cifrasDecimales;
	}

	public static String getIntToString(int i) {
		Integer numero = i;
		return numero.toString();
	}

	public static String getFloatToString(Float d, int nroDecimales) {

		String nro = "";
		for (int i = 0; i < nroDecimales; i++) {
			nro += "#";
		}
		String cadena = "";
		try {

			DecimalFormat df = new DecimalFormat("###." + nro);
			cadena = df.format(d);
			int pos = cadena.indexOf(",") - 6;
			if (cadena.indexOf("-") >= 0 && pos == 1) {
				pos -= 1;
			}
			df = new DecimalFormat("#,###,###." + nro);
			if (pos <= cadena.length() - 3 && pos > 0) {
				String mill = cadena.substring(0, pos);
				String miles = cadena.substring(pos, cadena.length());
				mill = cadena = df.format(Long.valueOf(mill));
				miles = cadena = df.format(Float.valueOf(miles.replace(',',
						'.')));
				cadena = mill + "'" + miles;
			} else {
				cadena = df.format(d);
			}
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace();
		}

		return cadena;
	}

	/*
	public static Float setFloatToString(String cadena) {
		cadena = cadena.replace("'", ".");
		cadena = cadena.replace(".", "");
		cadena = cadena.replace(",", ".");
		return Float.valueOf(cadena);

	}
	*/

	public static String getFloatParteEnteraToString(Float d) {
		if (d == null) {
			d = Float.valueOf("0");
		}
		int entero = (int) Math.floor(d);
		String cadena = "";
		try {

			DecimalFormat df = new DecimalFormat("###");
			cadena = df.format(entero);
			int pos = cadena.length() - 6;
			df = new DecimalFormat("#,###,###");
			if (pos > 0) {
				String mill = cadena.substring(0, pos);
				String miles = cadena.substring(pos, cadena.length());
				mill = cadena = df.format(Long.valueOf(mill));
				miles = cadena = df.format(Long.valueOf(miles));
				cadena = mill + "'" + miles;
			} else {
				cadena = df.format(entero);
			}
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace();
		}

		return cadena;
	}

	public static String getFloatToString(float d, int nroDecimales) {

		String nro = "";
		for (int i = 0; i < nroDecimales; i++) {
			nro += "0";
		}
		String cadena = "";
		try {

			DecimalFormat df = new DecimalFormat("###." + nro);
			cadena = df.format(d);
			int pos = cadena.indexOf(",") - 6;
			if (cadena.indexOf("-") >= 0 && pos == 1) {
				pos -= 1;
			}
			df = new DecimalFormat("#,###,###." + nro);
			if (pos <= cadena.length() - 3 && pos > 0) {
				String mill = cadena.substring(0, pos);
				String miles = cadena.substring(pos, cadena.length());
				mill = cadena = df.format(Long.valueOf(mill));
				miles = cadena = df.format(Float.valueOf(miles.replace('.',
						',')));
				cadena = mill + "'" + miles;
			} else {
				cadena = df.format(d);
			}
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace();
		}

		return cadena;
	}

	/**
	 * Convierte una cadena a entero (int)
	 * 
	 * @param numCadena
	 * @return
	 */
	public static int getStringToInt(String numCadena) {
		int numEntero = Integer.parseInt(numCadena);
		return numEntero;
	}

	/**
	 * Convierte una cadena a fecha (Date)
	 * 
	 * @param fechaCadena
	 * @return
	 */
	public static Date getStringToDate(String fechaCadena) {
		SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy-MM-dd");
		Date fecha = null;
		try {
			fecha = formatoDelTexto.parse(fechaCadena);
		} catch (ParseException ex) {
			Log.i("info_ANDRES", "CATCHh!!!!!!!!!!!!!!!!!!!!!!!!!!" + ex);
			ex.printStackTrace();
		}
		return fecha;
	}

	/**
	 * @author Luz D. Convierte una cadena a dateTime (Date)
	 * @param fechaCadena
	 * @return
	 */
	public static Date getStringToDateTime(String fechaCadena) {
		SimpleDateFormat formatoDelTexto = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date fecha = null;
		try {

			fecha = formatoDelTexto.parse(fechaCadena);

		} catch (ParseException ex) {

			Log.i("info", "exception fecha-"+fechaCadena);
			ex.printStackTrace();

		}
		return fecha;
	}

	public static Date getStringToDateTime2(String fechaCadena) {
		SimpleDateFormat formatoDelTexto = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss");
		Date fecha = new Date();
		try {

			fecha = formatoDelTexto.parse(fechaCadena);

		} catch (ParseException ex) {

			Log.i("info", "exception fecha-"+fechaCadena);
			ex.printStackTrace();

		}
		return fecha;
	}
	
	public static Date getStringToTime(String fechaCadena) {
		SimpleDateFormat formatoDelTexto = new SimpleDateFormat(
				"HH:mm:ss");
		SimpleDateFormat formatoDelTexto2 = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		//Log.i("info", "hora a evaluar:"+fechaCadena);
		Date fecha = null;
		try {

			fechaCadena = fechaCadena.replace("0001-", "1900-");
			
			fecha = formatoDelTexto2.parse(fechaCadena);

		} catch (ParseException ex) {
			try {

				fecha = formatoDelTexto.parse(fechaCadena);

			} catch (ParseException ex2) {
				Log.i("info", "error hora a evaluar:"+fechaCadena+"."+ex2.getMessage());

				ex2.printStackTrace();

			}

		}
		Log.i("info", "hora a evaluars:"+fechaCadena+"-"+fecha);
		return fecha;
	}
	
	public static String formatoTotalPedido(Float d, int nroDecimales) {

		String nro = "";
		for (int i = 0; i < nroDecimales; i++) {
			nro += "#";
		}
		String cadena = "";
		try {

			DecimalFormat df = new DecimalFormat("###." + nro);
			cadena = df.format(d);
			int pos = cadena.indexOf(",") - 6;
			if (cadena.indexOf("-") >= 0 && pos == 1) {
				pos -= 1;
			}
			df = new DecimalFormat("#,###,###." + nro);
			if (pos <= cadena.length() - 3 && pos > 0) {
				String mill = cadena.substring(0, pos);
				String miles = cadena.substring(pos, cadena.length());
				mill = cadena = df.format(Long.valueOf(mill));
				miles = cadena = df.format(Float.valueOf(miles.replace(',',
						'.')));
				cadena = mill + "'" + miles;
			} else {
				cadena = df.format(d);
			}
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace();
		}

		return cadena;
	}

	// public static float setFloatToString(String s) {
	// float f = Float.valueOf(s.trim()).floatValue();
	// return f;
	// }
	/*
	 * public static Float setFloatToString(String cadena) { cadena
	 * =cadena.replace("'", "."); cadena =cadena.replace(".", ","); cadena
	 * =cadena.replace(",", ""); if(cadena.equals("")){cadena="0";} return
	 * Float.valueOf(cadena);
	 * 
	 * }
	 */
	public static Float setFloatToString(String cadena) {
		if (cadena.equals("")) {
			cadena = "0.00";
		} else {
			cadena = cadena.replace(",", "");
		}
		return Float.parseFloat(cadena);
	}

	public static int getDiasTranscurridosEntre(Date fechaDesde, Date fechaHasta) {

		long diff = fechaHasta.getTime() - fechaDesde.getTime();
		long dias = diff / (1000 * 60 * 60 * 24);
		return (int) dias;
	}

	public static int getAniosTranscurridosEntre(Date fechaDesde,
			Date fechaHasta) {

		long diff = fechaHasta.getTime() - fechaDesde.getTime();
		long dias = diff / (1000 * 60 * 60 * 24);
		return (int) dias;
	}

	/**
	 * @author amunoz Determina si el parametro cadena es o no num�rico
	 * 
	 * @param cadena
	 * @return true si cadena es de tipo num�rico, false en otro caso
	 */
	public static boolean isNumeric(String cadena) {
		/*
		 * 
		 * try{ Float.parseFloat(cadena); }catch (NumberFormatException nfe) {
		 * return false; } return true;
		 */
		try {
			if (cadena.equals("0")) {
				cadena = "0.00";
			} else {
				// cadena =cadena.replace("'", ".");
				// cadena =cadena.replace(".", ",");
				cadena = cadena.replace(",", "");
			}
			Float.parseFloat(cadena);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static boolean isFloat(String cadena) {
		boolean isfloat = false;
		if (cadena.equals("")) {
			cadena = "0";
		}
		cadena = cadena.replace("'", ".");
		cadena = cadena.replace(".", "");
		cadena = cadena.replace(",", ".");
		if (Float.valueOf(cadena) != null) {
			isfloat = true;
		} else {
			isfloat = false;
		}
		return isfloat;

	}

	/**
	 * @author amunoz Determina si el parametro e_mail_address es una
	 *         direcci�n e_mail v�lida
	 * 
	 * @param cadena
	 * @return true si la direccion de e_mail es v�lida, false en otro caso
	 */
	public static boolean isEmail(String e_mail_address) {
		Pattern pat = null;
		Matcher mat = null;
		pat = Pattern
				.compile("^([0-9a-zA-Z]([_.w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-w]*[0-9a-zA-Z].)+([a-zA-Z]{2,9}.)+[a-zA-Z]{2,3})$");
		mat = pat.matcher(e_mail_address);
		if (mat.find()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @author amunoz retorna la fecha de hoy
	 * 
	 * @param cadena
	 * @return Date, la fecha de hoy
	 */
	public static Date getTodayDate() {
		Calendar todayDate = Calendar.getInstance();
		return todayDate.getTime();
	}

	/**
	 * @author amunoz retorna la hora actual
	 * 
	 * @param cadena
	 * @return Date, la hora actual
	 * 
	 */
	public static Date getCurrentTime() {
		Calendar currentTime = Calendar.getInstance();
		return currentTime.getTime();
	}

	/**
	 * @author amunoz retorna la fecha como string
	 * 
	 * @param cadena
	 * @return Date, date
	 * 
	 */
	public static String getStringFromDate(Date date) {
		SimpleDateFormat dateFormatFecha = new SimpleDateFormat("yyyy-MM-dd");
		String sFecha = dateFormatFecha.format(date);
		return sFecha;
	}

	public String getTodayToDateString() {
		SimpleDateFormat dateFormatFecha = new SimpleDateFormat("yyyy-MM-dd");
		String fecha_actual = dateFormatFecha.format(getTodayDate());
		return fecha_actual;
	}

	public String getTodayToHourString() {
		SimpleDateFormat dateFormatHora = new SimpleDateFormat("HH:mm:ss");
		String hora_actual = dateFormatHora.format(getTodayDate());
		return hora_actual;
	}

	public static String getDateToDateString(Date fecha) {
		SimpleDateFormat dateFormatFecha = new SimpleDateFormat("yyyy-MM-dd");
		String fecha_actual = dateFormatFecha.format(fecha);
		return fecha_actual;
	}

	public String getToDateToHourString(Date fecha) {
		SimpleDateFormat dateFormatHora = new SimpleDateFormat("HH:mm:ss");
		String hora_actual = dateFormatHora.format(fecha);
		return hora_actual;
	}

	public static String getToDateTimeToHourString(Date fecha) {
		SimpleDateFormat dateFormatHora = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String hora_actual = dateFormatHora.format(fecha);
		String hora = hora_actual.substring(10, 19);
		return hora;
	}

	/**
	 * amunoz
	 * 
	 * @param date
	 * @return
	 */
	public static String getFechaFormateada(Date date) {
		String dia = Integer.toString(date.getDate());
		int mes_int = date.getMonth();
		String mes = "";
		String anio = Integer.toString(date.getYear() + 1900);
		String fecha = "";
		if (mes_int == 0) {
			mes = "Ene";
		}
		if (mes_int == 1) {
			mes = "Feb";
		}
		if (mes_int == 2) {
			mes = "Mar";
		}
		if (mes_int == 3) {
			mes = "Abr";
		}
		if (mes_int == 4) {
			mes = "May";
		}
		if (mes_int == 5) {
			mes = "Jun";
		}
		if (mes_int == 6) {
			mes = "Jul";
		}
		if (mes_int == 7) {
			mes = "Diciembre";
		}
		if (mes_int == 8) {
			mes = "Sep";
		}
		if (mes_int == 9) {
			mes = "Oct";
		}
		if (mes_int == 10) {
			mes = "Nov";
		}
		if (mes_int == 11) {
			mes = "Dic";
		}

		fecha = dia + "-" + mes + "-" + anio;
		return fecha;
	}

	public static String getIntToString(float f, int nroDecimales) {

		Locale.setDefault(new Locale("us", "US"));

		Float d = (float) f;

		// String nro ="";
		// for(int i= 0; i<nroDecimales; i++){
		// nro+="#";
		// }
		// String cadena="";
		// try{
		//
		// DecimalFormat df = new DecimalFormat("###."+nro);
		// cadena=df.format(d);
		// int pos = cadena.indexOf(".")-6;
		// if(cadena.indexOf("-")>=0 && pos==1){
		// pos-=1;
		// }
		// df = new DecimalFormat("#,###,###."+nro);
		// if(pos<=cadena.length()-3 && pos>0){
		// String mill = cadena.substring(0,pos);
		// String miles = cadena.substring(pos,cadena.length());
		// mill = cadena=df.format(Long.valueOf(mill));
		// miles = cadena=df.format(Float.valueOf(miles.replace(',','.')));
		// cadena = mill+"'"+miles;
		// }else{
		// cadena=df.format(d);
		// }
		// }catch(Exception e){
		// e.printStackTrace();
		// e.printStackTrace();
		// }
		//
		//
		// return cadena;

		// String nro ="";
		// for(int i= 0; i<nroDecimales; i++){
		// nro+="#";
		// }
		String cadena = "";
		try {

			String cadCero = "0000000000000000000000000000";
			NumberFormat numberFormat;
			if (nroDecimales == 0) {
				numberFormat = new DecimalFormat("#,###,###");
			} else {
				numberFormat = new DecimalFormat("#,###,###."
						+ cadCero.substring(0, nroDecimales));
			}

			cadena = numberFormat.format(d);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.i("info", "El precio:" + cadena);
		return cadena;
	}

	public static String getIntegerToString(integer di, int nroDecimales) {

		Locale.setDefault(new Locale("us", "US"));

		Float d = Float.valueOf(di.toString());

		// String nro ="";
		// for(int i= 0; i<nroDecimales; i++){
		// nro+="#";
		// }
		// String cadena="";
		// try{
		//
		// DecimalFormat df = new DecimalFormat("###."+nro);
		// cadena=df.format(d);
		// int pos = cadena.indexOf(".")-6;
		// if(cadena.indexOf("-")>=0 && pos==1){
		// pos-=1;
		// }
		// df = new DecimalFormat("#,###,###."+nro);
		// if(pos<=cadena.length()-3 && pos>0){
		// String mill = cadena.substring(0,pos);
		// String miles = cadena.substring(pos,cadena.length());
		// mill = cadena=df.format(Long.valueOf(mill));
		// miles = cadena=df.format(Float.valueOf(miles.replace(',','.')));
		// cadena = mill+"'"+miles;
		// }else{
		// cadena=df.format(d);
		// }
		// }catch(Exception e){
		// e.printStackTrace();
		// e.printStackTrace();
		// }
		//
		//
		// return cadena;

		// String nro ="";
		// for(int i= 0; i<nroDecimales; i++){
		// nro+="#";
		// }
		String cadena = "";
		try {

			String cadCero = "0000000000000000000000000000";
			NumberFormat numberFormat;
			if (nroDecimales == 0) {
				numberFormat = new DecimalFormat("#,###,###");
			} else {
				numberFormat = new DecimalFormat("#,###,###."
						+ cadCero.substring(0, nroDecimales));
			}

			cadena = numberFormat.format(d);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.i("info", "El precio:" + cadena);
		return cadena;
	}

	public static long getMilliseconds(Date pDdate) {
		try
		{
			long _valmil = pDdate.getTime();
			if (_valmil<0) _valmil = 0;
			return _valmil;
		}
		catch(Exception ex)
		{
			return 0;			
		}
	}



}
