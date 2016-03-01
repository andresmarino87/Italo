package servicios;

import org.json.JSONException;
import org.json.JSONObject;

import easysync.Respuesta;




public class JsonServicio {

	public static Respuesta transformJSON2Respuesta(String pRespuesta)
			throws JSONException {
		try {
			JSONObject n = new JSONObject(pRespuesta);
			return transformJSON2Respuesta(n);
		} catch (Exception ex) {
			Respuesta _respuesta = new Respuesta();
			_respuesta.setResultado(false);
			//_respuesta.setObservacion("Error: " + ex.getMessage());
			_respuesta.setObservacion("Error: " + pRespuesta);
			_respuesta.setLogId(-1);
			_respuesta.setBaseDatosES("");
			return _respuesta;
		}
	}

	private static Respuesta transformJSON2Respuesta(JSONObject pRespuesta)
			throws JSONException {
		try {
			Respuesta _respuesta = new Respuesta();
			_respuesta.setResultado(pRespuesta.getBoolean("Resultado"));
			_respuesta.setObservacion(pRespuesta.getString("Observacion"));
			_respuesta.setLogId(pRespuesta.getLong("LogId"));
			_respuesta.setBaseDatosES(pRespuesta.getString("BaseDatosES"));
			return _respuesta;
		} catch (Exception ex) {
			Respuesta _respuesta = new Respuesta();
			_respuesta.setResultado(false);
			_respuesta.setObservacion("Error: " + ex.getMessage());
			//_respuesta.setObservacion("Error: " + pRespuesta);
			_respuesta.setLogId(-1);
			_respuesta.setBaseDatosES("");
			return _respuesta;
		}
	}

}