package servicios;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.text.NumberFormat;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.italo_view.GlobaG;

import easysync.EasySync;
import easysync.FileSales;
import easysync.Funciones;
import easysync.RequerimientoSincronismo;
import easysync.SolicitudSincronismoBaseDatos;


import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

public class HttpServicio {

	private static final int timeoutConnection = 40000;
	private static final int timeoutSocket = 240000;

	public static void CerrarConexion(HttpClient pHttpClient) {
		try {
			pHttpClient.getConnectionManager().shutdown();
		} catch (Exception ex) {
			//Log.i("info2", "cerrando conexion-" + ex.getMessage());
		}
	}

	public static HttpParams parametrosHttp() {
		HttpParams httpParameters = new BasicHttpParams();

		/*
		 * HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);
		 * HttpProtocolParams.setContentCharset(httpParameters,
		 * HTTP.DEFAULT_CONTENT_CHARSET);
		 * HttpProtocolParams.setUseExpectContinue(httpParameters, true);
		 * 
		 * HttpConnectionParams.setStaleCheckingEnabled(httpParameters, false);
		 */

		// int timeoutConnection = 60000;
		// int timeoutConnection = 5000;
		// HttpConnectionParams.setConnectionTimeout(httpParameters,
		// timeoutConnection);
		// int timeoutSocket = 1200000;
		// int timeoutSocket = 35000;
		// HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		httpParameters.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				timeoutConnection);
		httpParameters.setParameter(CoreConnectionPNames.SO_TIMEOUT,
				timeoutSocket);

		ConnManagerParams.setTimeout(httpParameters, timeoutSocket);
		// HttpConnectionParams.setTcpNoDelay(httpParameters, false);

		return httpParameters;
	}

	/*
	 * public static HttpClient getAndroidHttpClient() { HttpClient client =
	 * HttpClient.newInstance(null);
	 * HttpConnectionParams.setConnectionTimeout(client.getParams(),
	 * timeoutConnection); HttpConnectionParams.setSoTimeout(client.getParams(),
	 * timeoutSocket); HttpConnectionParams.setLinger(client.getParams(),
	 * timeoutSocket);
	 * 
	 * HttpParams httpParameters = new BasicHttpParams();
	 * httpParameters.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
	 * timeoutConnection);
	 * httpParameters.setParameter(CoreConnectionPNames.SO_TIMEOUT,
	 * timeoutSocket); ConnManagerParams.setTimeout(client.getParams(),
	 * timeoutSocket);
	 * 
	 * return client; }
	 */
	public static HttpClient getAndroidHttpClient2() {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(),
				timeoutConnection);
		HttpConnectionParams.setSoTimeout(client.getParams(), timeoutSocket);
		HttpConnectionParams.setLinger(client.getParams(), timeoutSocket);

		HttpParams httpParameters = new BasicHttpParams();
		httpParameters.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				timeoutConnection);
		httpParameters.setParameter(CoreConnectionPNames.SO_TIMEOUT,
				timeoutSocket);
		ConnManagerParams.setTimeout(client.getParams(), timeoutSocket);

		return client;
	}

	// throws Exception
	public static HttpClient getAndroidHttpClient() {

		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			// SSLSocketFactory sf = new SSLSocketFactory(trustStore);
			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);

			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			DefaultHttpClient client = new DefaultHttpClient(ccm, params);

			HttpConnectionParams.setConnectionTimeout(client.getParams(),
					timeoutConnection);
			HttpConnectionParams
					.setSoTimeout(client.getParams(), timeoutSocket);
			HttpConnectionParams.setLinger(client.getParams(), timeoutSocket);

			HttpParams httpParameters = new BasicHttpParams();
			httpParameters.setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, timeoutConnection);
			httpParameters.setParameter(CoreConnectionPNames.SO_TIMEOUT,
					timeoutSocket);
			ConnManagerParams.setTimeout(client.getParams(), timeoutSocket);

			return client;
		} catch (Exception ex) {
			//Log.i("info2", "error socket - " + ex.getMessage());
			return new DefaultHttpClient();
		}
	}

	public static String doGet(String path) throws Exception {

		BufferedReader in = null;
		HttpClient client = getAndroidHttpClient();
		try {
			// HttpClient client = new HttpClient(parametrosHttp());
			/*
			 * HttpParams httpParameters = new BasicHttpParams();
			 * HttpConnectionParams.setConnectionTimeout(httpParameters,
			 * timeoutConnection);
			 * HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			 * HttpConnectionParams.setStaleCheckingEnabled(httpParameters,
			 * false); HttpConnectionParams.setTcpNoDelay(httpParameters,
			 * false);
			 * 
			 * HttpClient client = new HttpClient(httpParameters);
			 */
			HttpGet request = new HttpGet();
			request.setURI(new URI(path));
			HttpResponse response = client.execute(request);

			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));

			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String array = sb.toString();
			// client.close();
			CerrarConexion(client);
			return array;
		} finally {
			CerrarConexion(client);
			// client.close();
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String doPost(String path, String text) throws Exception {
		HttpPost httpost = new HttpPost(new URI(path));
		httpost.setHeader("Accept", "application/json");
		httpost.setHeader("Content-type", "application/json; charset=utf-8");

		JSONObject holder = new JSONObject();
		holder.put("Text", text);

		// StringEntity se = new StringEntity(holder.toString());
		StringEntity se = new StringEntity(text);

		httpost.setEntity(se);

		// HttpClient httpclient = new HttpClient(parametrosHttp());
		HttpClient httpclient = getAndroidHttpClient();
		/*
		 * HttpParams httpParameters = new BasicHttpParams();
		 * HttpConnectionParams.setConnectionTimeout(httpParameters,
		 * timeoutConnection); HttpConnectionParams.setSoTimeout(httpParameters,
		 * timeoutSocket);
		 * HttpConnectionParams.setStaleCheckingEnabled(httpParameters, false);
		 * HttpConnectionParams.setTcpNoDelay(httpParameters, false); HttpClient
		 * httpclient = new HttpClient(httpParameters);
		 */
		HttpResponse response = httpclient.execute(httpost);

		StringBuffer sb = new StringBuffer();

		BufferedReader in = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		String line;
		String NL = System.getProperty("line.separator");
		while ((line = in.readLine()) != null) {
			sb.append(line + NL);
		}
		in.close();

		// httpclient.close();
		CerrarConexion(httpclient);

		return sb.toString();
	}

	public static String doPost(String path, String pNombreArchivo,
			String pExtension) throws Exception {
		String url = path + pNombreArchivo + "/" + pExtension;
		// //Log.i("info", "inicio servicio." + url);
		File file = new File(Environment.getExternalStorageDirectory(),
				"arc.txt");
		// try {

		/*
		 * HttpParams httpParameters = new BasicHttpParams(); // Set the timeout
		 * in milliseconds until a connection is established. int
		 * timeoutConnection = 3000;
		 * HttpConnectionParams.setConnectionTimeout(httpParameters,
		 * timeoutConnection); // Set the default socket timeout (SO_TIMEOUT) //
		 * in milliseconds which is the timeout for waiting for data. int
		 * timeoutSocket = 3000;
		 * HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		 */

		// HttpClient httpclient = new HttpClient(parametrosHttp());
		HttpClient httpclient = getAndroidHttpClient();
		/*
		 * HttpParams httpParameters = new BasicHttpParams();
		 * HttpConnectionParams.setConnectionTimeout(httpParameters,
		 * timeoutConnection); HttpConnectionParams.setSoTimeout(httpParameters,
		 * timeoutSocket);
		 * HttpConnectionParams.setStaleCheckingEnabled(httpParameters, false);
		 * HttpConnectionParams.setTcpNoDelay(httpParameters, false); HttpClient
		 * httpclient = new HttpClient(httpParameters);
		 */

		HttpPost httppost = new HttpPost(url);
		// InputStreamEntity reqEntity = new InputStreamEntity(
		// new FileInputStream(file), -1);
		// reqEntity.setContentType("text/plain");
		// reqEntity.setChunked(false); // Send in multiple parts if needed
		// httppost.setEntity(reqEntity);
		// //Log.i("info", "antes llamado download");

		HttpResponse response = httpclient.execute(httppost);
		// Do something with response...

		try {
			File f = new File(Environment.getExternalStorageDirectory()
					.toString() + "/" + pNombreArchivo + "." + pExtension);
			InputStream myRawResource = response.getEntity().getContent();
			OutputStream out = new FileOutputStream(f);
			// //Log.i("info", "despues servicio." + f.getPath());
			byte buf[] = new byte[1024];
			int len;
			while ((len = myRawResource.read(buf)) > 0)
				out.write(buf, 0, len);
			out.close();
			myRawResource.close();
		} catch (IOException e) {
			//Log.i("info", "error despues servicio." + e.getMessage());
		}

		StringBuffer sb = new StringBuffer();

		BufferedReader in = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		String line;
		String NL = System.getProperty("line.separator");
		while ((line = in.readLine()) != null) {
			sb.append(line + NL);
		}
		in.close();

		// //Log.i("info", "fin llamado." + sb.toString());
		// httpclient.close();
		CerrarConexion(httpclient);

		return sb.toString();

		// } catch (Exception e) {
		// show error
		// return e.getMessage();
		// }

	}

	public static String UpFile(String path, long pLogId, String pDeviceId)
			throws ClientProtocolException, IOException, JSONException,
			Exception {
		String url = path + "/" + pLogId + "/" + pDeviceId;
		// //Log.i("info", "inicio servicio." + url);
		File file = new File(GlobaG.pathArchivosSync, "EasyServerUp.zip");
		// //Log.i("info", "path-" + file.getAbsolutePath() + ".." +
		// file.getName()
		// + ".." + String.valueOf(file.length()));
		HttpClient httpclient = getAndroidHttpClient();
		HttpPost httppost = new HttpPost(url);

		InputStreamEntity reqEntity = new InputStreamEntity(
				new FileInputStream(file), -1);
		reqEntity.setContentType("text/plain");

		reqEntity.setChunked(true); // Send in multiple parts if needed

		httppost.setEntity(reqEntity);

		// //Log.i("info",
		// "antes llamado: "
		// + String.valueOf(reqEntity.getContentLength()));

		HttpResponse response = httpclient.execute(httppost);
		// Do something with response...

		StringBuffer sb = new StringBuffer();

		BufferedReader in = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		String line;
		String NL = System.getProperty("line.separator");
		while ((line = in.readLine()) != null) {
			sb.append(line + NL);
		}
		in.close();

		// //Log.i("info", "fin llamado." + sb.toString());
		// httpclient.close();
		CerrarConexion(httpclient);

		return sb.toString();

	}

	public static String UpFileRecovery(String path, long pLogId,
			String pDeviceId) throws ClientProtocolException, IOException,
			JSONException, Exception {
		String url = path + "/" + pLogId + "/" + pDeviceId;
		// //Log.i("info", "inicio servicio." + url);
		File file = new File(GlobaG.pathArchivosSync, GlobaG.NombreRecovery);
		// //Log.i("info", "path-" + file.getAbsolutePath() + ".." +
		// file.getName()
		// + ".." + String.valueOf(file.length()));
		HttpClient httpclient = getAndroidHttpClient();
		HttpPost httppost = new HttpPost(url);

		InputStreamEntity reqEntity = new InputStreamEntity(
				new FileInputStream(file), -1);
		reqEntity.setContentType("text/plain");

		reqEntity.setChunked(true); // Send in multiple parts if needed

		httppost.setEntity(reqEntity);

		//Log.i("info",
		//		"antes llamado recovery: "
		//				+ String.valueOf(reqEntity.getContentLength()));
		HttpResponse response = httpclient.execute(httppost);
		// Do something with response...

		StringBuffer sb = new StringBuffer();

		BufferedReader in = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		String line;
		String NL = System.getProperty("line.separator");
		while ((line = in.readLine()) != null) {
			sb.append(line + NL);
		}
		in.close();

		// //Log.i("info", "fin llamado." + sb.toString());
		// httpclient.close();
		CerrarConexion(httpclient);

		return sb.toString();

	}

	public static String UpEasyServerFile(String path, long pLogId,
			String pDeviceId, String pNombreArchivo, String pExtension,
			String pTipo, String pArchivo) throws ClientProtocolException,
			IOException, JSONException, Exception {
		String url = path + "/" + pLogId + "/" + pDeviceId + "/"
				+ pNombreArchivo + "/" + pExtension + "/" + pTipo;

		File file;
		if (pTipo.equals("Trace")) {
			file = new File(Environment.getExternalStorageDirectory()
					+ "/download", pArchivo);
		} else if (pTipo.equals("Up")) {
			file = new File(GlobaG.pathArchivosEasy + "/up", pArchivo);
		} else {
			file = new File(Environment.getExternalStorageDirectory()
					+ "/download", pArchivo);
		}

		HttpClient httpclient = getAndroidHttpClient();
		HttpPost httppost = new HttpPost(url);

		InputStreamEntity reqEntity = new InputStreamEntity(
				new FileInputStream(file), -1);
		reqEntity.setContentType("text/plain");

		reqEntity.setChunked(true); // Send in multiple parts if needed

		httppost.setEntity(reqEntity);

		HttpResponse response = httpclient.execute(httppost);
		// Do something with response...

		StringBuffer sb = new StringBuffer();

		BufferedReader in = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		String line;
		String NL = System.getProperty("line.separator");
		while ((line = in.readLine()) != null) {
			sb.append(line + NL);
		}
		in.close();

		// httpclient.close();
		CerrarConexion(httpclient);

		return sb.toString();

	}

	public static String easyServerDown(String path, long pLogId,
			String pDeviceId, String pSincronismoInicial) throws Exception {
		String url = path;
		long lenTranferido = 0;
		HttpClient httpclient = getAndroidHttpClient();
		HttpPost httppost = new HttpPost(url);

		HttpResponse response = httpclient.execute(httppost);
		try {
			File f = new File(GlobaG.pathArchivosSync + "/"
					+ GlobaG.nombreZipDown);
			InputStream myRawResource = response.getEntity().getContent();
			OutputStream out = new FileOutputStream(f);
			byte buf[] = new byte[1024];
			int len;
			while ((len = myRawResource.read(buf)) > 0) {

				lenTranferido += len;
				out.write(buf, 0, len);
			}
			out.close();
			myRawResource.close();
			CerrarConexion(httpclient);

			if (lenTranferido == 0) {
				return "ERROR";
			} else {
				return "OK";
			}
		} catch (IOException e) {
			CerrarConexion(httpclient);

			//Log.i("info", "error despues servicio." + e.getMessage());
			return "ERROR. " + e.getMessage();
		}

	}

	public static String easyServerDown(String path, long pLogId,
			String pDeviceId, String pSincronismoInicial, FileSales pFileSales,
			EasySync pEasySync) throws Exception {
		String url = path;
		long lenTranferido = 0;
		HttpClient httpclient = getAndroidHttpClient();
		HttpPost httppost = new HttpPost(url);
		int progresoAnt = 0;

		HttpResponse response = httpclient.execute(httppost);
		try {

			File f = new File(pFileSales.PathFull());
			f.delete();

			f = new File(pFileSales.PathFull());
			InputStream myRawResource = response.getEntity().getContent();
			OutputStream out = new FileOutputStream(f);
			byte buf[] = new byte[1024];
			int len;
			while ((len = myRawResource.read(buf)) > 0) {

				lenTranferido += len;
				out.write(buf, 0, len);

				int _relacion = (int) ((float) lenTranferido * 100 / (float) pFileSales
						.getFile_size());
				//Log.i("info",
				//		"relacion-" + _relacion + "-" + (float) lenTranferido
				//				+ "-" + (float) pFileSales.getFile_size());
				int _progreso = _relacion - (_relacion % 5);

				if (_progreso != progresoAnt) {
					pEasySync.RegistrarEvento(
							"SINCRONIZANDO",
							"DESCARGA FILE . PATH: "
									+ pFileSales.getFile_path_relative()
									+ ". FILE:" + pFileSales.getFile_name()
									+ ". " + String.valueOf(_progreso) + "%");
					progresoAnt = _progreso;
				}

			}
			out.close();

			myRawResource.close();

			//Log.i("info",
			//		"ultima modificacion-"
			//				+ Funciones.getMilliseconds(pFileSales
			//						.getFile_date()) + "//"
			//				+ pFileSales.getFile_date());
			f.setLastModified(Funciones.getMilliseconds(pFileSales
					.getFile_date()));

			CerrarConexion(httpclient);

			if (lenTranferido == 0) {
				return "ERROR";
			} else {
				return "OK";
			}
		} catch (IOException e) {
			CerrarConexion(httpclient);

			//Log.i("info", "error despues servicio." + e.getMessage());
			return "ERROR. " + e.getMessage();
		}

	}

	public static String doPost(String path,
			SolicitudSincronismoBaseDatos pSolicitudSincronismoBaseDatos)
			throws Exception {

		String url = path + pSolicitudSincronismoBaseDatos.getLogin() + "/"
				+ pSolicitudSincronismoBaseDatos.getDeviceId();
		// //Log.i("info", "inicio servicio." + url);
		File file = new File(Environment.getExternalStorageDirectory(),
				"BDEAsySales.zip");
		HttpClient httpclient = getAndroidHttpClient();
		try {
			// HttpClient httpclient = new HttpClient(parametrosHttp());
			/*
			 * HttpParams httpParameters = new BasicHttpParams();
			 * HttpConnectionParams.setConnectionTimeout(httpParameters,
			 * timeoutConnection);
			 * HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			 * HttpConnectionParams.setStaleCheckingEnabled(httpParameters,
			 * false); HttpConnectionParams.setTcpNoDelay(httpParameters,
			 * false); HttpClient httpclient = new HttpClient(httpParameters);
			 */

			HttpPost httppost = new HttpPost(url);

			InputStreamEntity reqEntity = new InputStreamEntity(
					new FileInputStream(file), -1);
			// reqEntity.setContentType("binary/octet-stream");
			reqEntity.setContentType("text/plain");

			reqEntity.setChunked(true); // Send in multiple parts if needed
			httppost.setEntity(reqEntity);

			// //Log.i("info", "antes llamado");

			HttpResponse response = httpclient.execute(httppost);
			// Do something with response...

			StringBuffer sb = new StringBuffer();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			String line;
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();

			// //Log.i("info", "fin llamado." + sb.toString());

			// httpclient.close();
			CerrarConexion(httpclient);

			return sb.toString();

		} catch (Exception e) {
			// show error
			// httpclient.close();
			CerrarConexion(httpclient);

			return e.getMessage();
		}

		/*
		 * HttpURLConnection connection = null; DataOutputStream outputStream =
		 * null; DataInputStream inputStream = null;
		 * 
		 * String pathToOurFile = Environment.getExternalStorageDirectory() +
		 * "/EasyDemo_jad.apk"; String urlServer = path; String lineEnd =
		 * "\r\n"; String twoHyphens = "--"; String boundary = "*****";
		 * 
		 * int bytesRead, bytesAvailable, bufferSize; byte[] buffer; int
		 * maxBufferSize = 1*1024*1024;
		 * 
		 * try { FileInputStream fileInputStream = new FileInputStream(new
		 * File(pathToOurFile) );
		 * 
		 * URL url = new URL(urlServer); connection = (HttpURLConnection)
		 * url.openConnection();
		 * 
		 * // Allow Inputs & Outputs connection.setDoInput(true);
		 * connection.setDoOutput(true); connection.setUseCaches(false);
		 * 
		 * // Enable POST method connection.setRequestMethod("POST");
		 * 
		 * connection.setRequestProperty("Connection", "Keep-Alive");
		 * //connection.setRequestProperty("Content-Type",
		 * "multipart/form-data;boundary="+boundary);
		 * connection.setRequestProperty("Content-Type",
		 * "text/xml; charset=utf-8");
		 * 
		 * 
		 * outputStream = new DataOutputStream( connection.getOutputStream() );
		 * //outputStream.writeBytes(twoHyphens + boundary + lineEnd);
		 * //outputStream.writeBytes(
		 * "Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" +
		 * pathToOurFile +"\"" + lineEnd); //outputStream.writeBytes(lineEnd);
		 * 
		 * bytesAvailable = fileInputStream.available(); bufferSize =
		 * Math.min(bytesAvailable, maxBufferSize); buffer = new
		 * byte[bufferSize];
		 * 
		 * // Read file bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		 * 
		 * while (bytesRead > 0) { outputStream.write(buffer, 0, bufferSize);
		 * bytesAvailable = fileInputStream.available(); bufferSize =
		 * Math.min(bytesAvailable, maxBufferSize); bytesRead =
		 * fileInputStream.read(buffer, 0, bufferSize); }
		 * 
		 * outputStream.writeBytes(lineEnd); outputStream.writeBytes(twoHyphens
		 * + boundary + twoHyphens + lineEnd);
		 * 
		 * // Responses from the server (code and message) int
		 * serverResponseCode = connection.getResponseCode(); String
		 * serverResponseMessage = connection.getResponseMessage();
		 * 
		 * fileInputStream.close(); outputStream.flush(); outputStream.close();
		 * 
		 * //Log.i("info","resultado servicio." + serverResponseMessage);
		 * 
		 * return serverResponseMessage; } catch (Exception ex) { //Exception
		 * handling //Log.i("info","error servicio." + ex.getMessage()); return
		 * ex.getMessage(); }
		 */
		/*
		 * String url = path; File file = new
		 * File(Environment.getExternalStorageDirectory(), "CEUpdate.zip"); try
		 * { HttpClient httpclient = new HttpClient();
		 * 
		 * HttpPost httppost = new HttpPost(url);
		 * 
		 * InputStreamEntity reqEntity = new InputStreamEntity( new
		 * FileInputStream(file), -1);
		 * reqEntity.setContentType("binary/octet-stream");
		 * //reqEntity.setContentType("text/xml; charset=utf-8");
		 * 
		 * reqEntity.setChunked(true); // Send in multiple parts if needed
		 * httppost.setEntity(reqEntity);
		 * 
		 * //Log.i("info","antes del llamado de funcion." + httppost.toString());
		 * 
		 * HttpResponse response = httpclient.execute(httppost); //Do something
		 * with response... //Log.i("info","respuesta." +
		 * response.getEntity().toString());
		 * 
		 * return response.getEntity().toString(); } catch (Exception e) { //
		 * show error //Log.i("info","error." + e.getMessage()); return
		 * e.getMessage(); }
		 */

		/*
		 * HttpPost httpost = new HttpPost(new URI(path));
		 * httpost.setHeader("Accept", "application/json");
		 * httpost.setHeader("Content-type", "application/json; charset=utf-8");
		 * 
		 * JSONObject holder = new JSONObject(); holder.put("Login",
		 * pSolicitudSincronismoBaseDatos.getLogin()); holder.put("DeviceId",
		 * pSolicitudSincronismoBaseDatos.getDeviceId());
		 * //holder.put("BaseDatos", null); //holder.put("BaseDatos", "[1,2]");
		 * //holder.put("BaseDatos",
		 * pSolicitudSincronismoBaseDatos.getBaseDatos());
		 * //holder.put("BaseDatos", new byte[]{1,2});
		 * 
		 * /* //StringEntity se = new
		 * StringEntity(holder.toString().substring(0,
		 * holder.toString().length()-1) + ",\"BaseDatos\":[1,2]}" );
		 * StringEntity se = new StringEntity(holder.toString());
		 * 
		 * ByteArrayEntity ba = new ByteArrayEntity(
		 * holder.toString().getBytes("UTF8"));
		 * 
		 * //httpost.setEntity(se); httpost.setEntity(ba);
		 * 
		 * //Log.i("info","preparar objeto." + ba.toString() );
		 * 
		 * HttpClient httpclient = new HttpClient(); HttpResponse response =
		 * httpclient.execute(httpost); //Log.i("info","se recibio respuesta." +
		 * path);
		 * 
		 * StringBuffer sb = new StringBuffer();
		 * 
		 * BufferedReader in = new BufferedReader(new InputStreamReader(response
		 * .getEntity().getContent()));
		 * 
		 * String line; String NL = System.getProperty("line.separator"); while
		 * ((line = in.readLine()) != null) { sb.append(line + NL); }
		 * in.close(); return sb.toString();
		 */
		/*
		 * StringEntity se = new StringEntity(holder.toString());
		 * //Log.i("info","antes http string." + se);
		 * 
		 * httpost.setEntity(se);
		 * 
		 * HttpClient httpclient = new HttpClient(); //Log.i("info","antes http."
		 * + httpost.toString()); HttpResponse response =
		 * httpclient.execute(httpost);
		 * 
		 * //Log.i("info","despues del llamado de servicio." + response);
		 * 
		 * StringBuffer sb = new StringBuffer();
		 * 
		 * BufferedReader in = new BufferedReader(new InputStreamReader(response
		 * .getEntity().getContent()));
		 * 
		 * String line; String NL = System.getProperty("line.separator"); while
		 * ((line = in.readLine()) != null) { sb.append(line + NL); }
		 * in.close();
		 * 
		 * 
		 * return sb.toString();
		 */

	}

	public static String requerimientoSincronismo(String path,
			RequerimientoSincronismo pRequerimientoSincronismo)
			throws JSONException, ClientProtocolException, IOException,
			URISyntaxException, Exception {

		// //Log.i("info", "path." + path);

		try {
			HttpPost httpost = new HttpPost(new URI(path));

			httpost.setHeader("Accept", "application/json");
			httpost.setHeader("Content-type", "application/json; charset=utf-8");

			JSONObject holder = new JSONObject();

			holder.put("DeviceId", pRequerimientoSincronismo.getDeviceId());
			holder.put("Login", pRequerimientoSincronismo.getLogin());
			holder.put("Password", pRequerimientoSincronismo.getPassword());
			holder.put("Revision", pRequerimientoSincronismo.getRevision());
			holder.put("Version", pRequerimientoSincronismo.getVersion());
			holder.put("Compilacion",
					pRequerimientoSincronismo.getCompilacion());
			holder.put("Observacion",
					pRequerimientoSincronismo.getObservacion());
			holder.put("SincronismoInicial",
					pRequerimientoSincronismo.getSincronismoInicial());
			holder.put("LogId", pRequerimientoSincronismo.getLogId());
			holder.put("BaseDatosES",
					pRequerimientoSincronismo.getBaseDatosES());

			// //Log.i("info", "pDeviceId" +
			// pRequerimientoSincronismo.getDeviceId());
			// //Log.i("info", "pLogin" + pRequerimientoSincronismo.getLogin());
			// //Log.i("info", "pPassword" +
			// pRequerimientoSincronismo.getPassword());
			// //Log.i("info", "pRevision" +
			// pRequerimientoSincronismo.getRevision());
			// //Log.i("info", "pVersion" +
			// pRequerimientoSincronismo.getVersion());
			// //Log.i("info",
			// "pSincronismoInicial"
			// + pRequerimientoSincronismo.getSincronismoInicial());
			// //Log.i("info", "pLogId" + pRequerimientoSincronismo.getLogId());
			// //Log.i("info",
			// "pBaseDatosES" + pRequerimientoSincronismo.getBaseDatosES());
			// //Log.i("info",
			// "Observacion" + pRequerimientoSincronismo.getObservacion());

			StringEntity se = new StringEntity(holder.toString());

			httpost.setEntity(se);

			// //Log.i("info", "pp1");
			HttpClient httpclient = getAndroidHttpClient();
			// //Log.i("info", "pp2");
			HttpResponse response = httpclient.execute(httpost);

			// EasyUtilidades.ActualizarCajaTexto(ptvMensaje,
			// "Respuesta de requerimiento de sincronización");
			// //Log.i("info", "pp3");

			StringBuffer sb = new StringBuffer();

			// //Log.i("info", "pp4-"+sb);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			// //Log.i("info", "pp4");

			String line;
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			// //Log.i("info", "pp5+" + sb.toString());
			// httpclient.close();
			CerrarConexion(httpclient);

			return sb.toString();
		} catch (Exception ex) {
			throw ex;
			// //Log.i("info", "error p6: " + ex.toString());
			// return "Error";
		}

		// return "Error";

	}

	public static boolean doDelete(String path, int id) throws Exception {
		StringBuilder url = new StringBuilder();
		url.append(path).append(id);

		HttpDelete httpdelete = new HttpDelete(url.toString());

		// HttpClient httpclient = new HttpClient(parametrosHttp());
		HttpClient httpclient = getAndroidHttpClient();
		/*
		 * HttpParams httpParameters = new BasicHttpParams();
		 * HttpConnectionParams.setConnectionTimeout(httpParameters,
		 * timeoutConnection); HttpConnectionParams.setSoTimeout(httpParameters,
		 * timeoutSocket);
		 * HttpConnectionParams.setStaleCheckingEnabled(httpParameters, false);
		 * HttpConnectionParams.setTcpNoDelay(httpParameters, false); HttpClient
		 * httpclient = new HttpClient(httpParameters);
		 */
		HttpResponse response = httpclient.execute(httpdelete);

		StatusLine statusLine = response.getStatusLine();
		// httpclient.close();
		CerrarConexion(httpclient);

		return statusLine.getStatusCode() == 200;
	}

	public static String getPuntosInteres(String pUrlServicio,
			SolicitudSincronismoBaseDatos pSolicitudSincronismoBaseDatos)
			throws Exception {

		HttpPost httpost = new HttpPost(new URI(pUrlServicio));
		httpost.setHeader("Accept", "application/json");
		httpost.setHeader("Content-type", "application/json; charset=utf-8");

		JSONObject holder = new JSONObject();

		holder.put("Login", pSolicitudSincronismoBaseDatos.getLogin());
		holder.put("DeviceId", pSolicitudSincronismoBaseDatos.getDeviceId());
		// holder.put("NumeroRegistros",
		// pSolicitudSincronismoBaseDatos.getBaseDatos());

		StringEntity se = new StringEntity(holder.toString());

		httpost.setEntity(se);

		// HttpClient httpclient = new HttpClient(parametrosHttp());
		HttpClient httpclient = getAndroidHttpClient();
		/*
		 * HttpParams httpParameters = new BasicHttpParams();
		 * HttpConnectionParams.setConnectionTimeout(httpParameters,
		 * timeoutConnection); HttpConnectionParams.setSoTimeout(httpParameters,
		 * timeoutSocket);
		 * HttpConnectionParams.setStaleCheckingEnabled(httpParameters, false);
		 * HttpConnectionParams.setTcpNoDelay(httpParameters, false); HttpClient
		 * httpclient = new HttpClient(httpParameters);
		 */

		HttpResponse response = httpclient.execute(httpost);

		StringBuffer sb = new StringBuffer();

		BufferedReader in = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		String line;
		String NL = System.getProperty("line.separator");
		while ((line = in.readLine()) != null) {
			sb.append(line + NL);
		}
		in.close();
		// httpclient.close();
		CerrarConexion(httpclient);

		return sb.toString();

	}
}
