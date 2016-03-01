package servicios;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import easysync.EasySync;
import easysync.FileSales;
import easysync.RequerimientoSincronismo;
import easysync.Respuesta;

import android.widget.TextView;


public interface SincronismoBaseDatosServicio {
	Respuesta SolicitudSincronismo(
			RequerimientoSincronismo pRequerimientoSincronismo, String pServicio)
			throws JSONException, ClientProtocolException, IOException,
			URISyntaxException, Exception;
	
	Respuesta SolicitudSincronismoURL(
			RequerimientoSincronismo pRequerimientoSincronismo, String pServicio)
			throws JSONException, ClientProtocolException, IOException,
			URISyntaxException, Exception;
	
	// Respuesta RespuestaSolicitudSincronismoBaseDatos(String pNombreArchivo,
	// String pExtension) throws JSONException, ClientProtocolException,
	// IOException;
	Respuesta DownLoadBaseDatos(long pLogId, String pDeviceId,
			String pServicio, String pSincronismoInicial) throws JSONException,
			ClientProtocolException, IOException, Exception;

	Respuesta DownLoadFile(long pLogId, String pDeviceId,
			String pServicio, String pSincronismoInicial, FileSales pFileSales, EasySync pEasySync) throws JSONException,
			ClientProtocolException, IOException, Exception;
	
	Respuesta UpLoadBaseDatos(long pLogId, String pDeviceId, String pServicio)
			throws ClientProtocolException, IOException, JSONException,
			Exception;

	Respuesta UpLoadRecoveryBaseDatos(long pLogId, String pDeviceId,
			String pServicio) throws ClientProtocolException, IOException,
			JSONException, Exception;

	Respuesta CommitSincronismo(
			RequerimientoSincronismo pRequerimientoSincronismo)
			throws ClientProtocolException, JSONException, IOException,
			URISyntaxException, Exception;

	Respuesta InstanciaSincronismo(
			RequerimientoSincronismo pRequerimientoSincronismo, String pServicio)
			throws ClientProtocolException, JSONException, IOException,
			URISyntaxException, Exception;

	Respuesta UpLoadEasyServerFile(long pLogId, String pDeviceId,
			String pServicio, String pNombreArchivo, String pExtension,
			String pTipo, String pArchivo) throws ClientProtocolException,
			IOException, JSONException, Exception;

}
