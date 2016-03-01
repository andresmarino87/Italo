package servicios;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.italo_view.GlobaG;

import easysync.EasySync;
import easysync.FileSales;
import easysync.RequerimientoSincronismo;
import easysync.Respuesta;


import android.util.Log;
import android.widget.TextView;


public class SincronismoBaseDatosServicioImpl implements
		SincronismoBaseDatosServicio {
	// String ipServidor = "186.31.247.101";
	//String ipServidor = "216.157.16.208";
	//String ipServidor = "216.157.17.91";
	//String ipServidor = "www.easysales.com.co";
	//String ipServidor = "10.0.2.2";
	//String ipServidor = "190.25.157.13:85";
	//String ipServidor = "216.157.17.91";
	//String ipServidor = "181.48.111.186:444";
	//String ipServidor = "216.157.16.208";
	int MaximoNumeroIntentos = 6;

	GlobaG gGlobal;
	
	public SincronismoBaseDatosServicioImpl(GlobaG GlobaG)
	{
		gGlobal = GlobaG;
	}
	
	public Respuesta SolicitudSincronismoURL(
			RequerimientoSincronismo pRequerimientoSincronismo, String pServicio)
			throws JSONException, ClientProtocolException, IOException,
			URISyntaxException, Exception {

		// String _url =
		// "http://186.31.247.101/EasyServerUnibol/Sincronismo/RequerimientoSincronismo";
		String _url = gGlobal.UrlEasyServerInicial + "/Sincronismo/"
				+ pServicio;
		int numeroIntentos = 1;
		String response = "";
		Respuesta respuesta = new Respuesta();

		//Log.i("info", _url);
		
		while (numeroIntentos <= MaximoNumeroIntentos) {
			try {
				response = HttpServicio.requerimientoSincronismo(_url,
						pRequerimientoSincronismo);

				respuesta = JsonServicio.transformJSON2Respuesta(response);

				//Log.i("info",
				//		"Respuesta upload. intento "
				//				+ String.valueOf(numeroIntentos) + ". "
				//				+ respuesta.getObservacion());
				if (respuesta.getObservacion().indexOf("Cannot initialize") != -1) {
					//Log.i("info",
					//		"Cannot initialize. intento "
					//				+ String.valueOf(numeroIntentos));
					if (numeroIntentos == MaximoNumeroIntentos) {
						break;
					} else {
						++numeroIntentos;
					}
				} else {
					break;
				}

			} catch (IOException e) {
				if (e.getMessage().indexOf("timed out") != -1) {
					//Log.i("info",
					//		"timed out. intento "
					//				+ String.valueOf(numeroIntentos));
					if (numeroIntentos == MaximoNumeroIntentos) {
						throw e;
					} else {
						++numeroIntentos;
					}
				}
			}catch (Exception e) {
				//Log.i("info", "URL FALLIDA EXCEPTION. Exception." + e.getMessage()
				//		+ ". " + e.getCause() + ". " + e.getLocalizedMessage());
				throw e;
			}


		}
		return respuesta;

	}
	
	public Respuesta SolicitudSincronismo(
			RequerimientoSincronismo pRequerimientoSincronismo, String pServicio)
			throws JSONException, ClientProtocolException, IOException,
			URISyntaxException, Exception {

		// String _url =
		// "http://186.31.247.101/EasyServerUnibol/Sincronismo/RequerimientoSincronismo";
		//String _url = "http://" + ipServidor + "/EasyServerUnibol/Sincronismo/"
		//		+ pServicio;
		String _url = gGlobal.UrlEasyServer + "/Sincronismo/"
				+ pServicio;

		int numeroIntentos = 1;
		String response = "";
		Respuesta respuesta = new Respuesta();

		while (numeroIntentos <= MaximoNumeroIntentos) {
			try {
				response = HttpServicio.requerimientoSincronismo(_url,
						pRequerimientoSincronismo);

				respuesta = JsonServicio.transformJSON2Respuesta(response);

				//Log.i("info",
				//		"Respuesta upload. intento "
				//				+ String.valueOf(numeroIntentos) + ". "
				//				+ respuesta.getObservacion());
				if (respuesta.getObservacion().indexOf("Cannot initialize") != -1) {
					//Log.i("info",
					//		"Cannot initialize. intento "
					//				+ String.valueOf(numeroIntentos));
					if (numeroIntentos == MaximoNumeroIntentos) {
						break;
					} else {
						++numeroIntentos;
					}
				} else {
					break;
				}

			} catch (IOException e) {
				if (e.getMessage().indexOf("timed out") != -1) {
					//Log.i("info",
					//		"timed out. intento "
					//				+ String.valueOf(numeroIntentos));
					if (numeroIntentos == MaximoNumeroIntentos) {
						throw e;
					} else {
						++numeroIntentos;
					}
				}
			}

		}
		return respuesta;

	}

	public Respuesta DownLoadBaseDatos(long pLogId, String pDeviceId,
			String pServicio, String pSincronismoInicial) throws JSONException,
			ClientProtocolException, IOException, Exception {

		// String _url = "http://186.31.247.101/EasyServerUnibol/Sincronismo/"
		// + pServicio;

		//String _url = "http://" + ipServidor + "/EasyServerUnibol/Sincronismo/"
		//		+ pServicio + "/" + pLogId + "/" + pDeviceId;
		String _url = gGlobal.UrlEasyServer + "/Sincronismo/"
				+ pServicio + "/" + pLogId + "/" + pDeviceId;
						
		if (pServicio != "BaseDatosUp") {
			_url = _url + "/" + pSincronismoInicial;
		}

		// String response = HttpServicio.easyServerDown(_url, pLogId,
		// pDeviceId,
		// pSincronismoInicial);

		int numeroIntentos = 1;
		String response = "";
		while (numeroIntentos <= MaximoNumeroIntentos) {
			try {
				response = HttpServicio.easyServerDown(_url, pLogId, pDeviceId,
						pSincronismoInicial);
				break;
			} catch (IOException e) {
				if (e.getMessage().indexOf("timed out") != -1) {
					//Log.i("info",
					//		"timed out. intento "
					//				+ String.valueOf(numeroIntentos));
					if (numeroIntentos == MaximoNumeroIntentos) {
						throw e;
					} else {
						++numeroIntentos;
					}
				}
			}

		}

		Respuesta _respuesta = new Respuesta();
		if (response.compareTo("OK") == 0) {
			_respuesta.setBaseDatosES("");
			_respuesta.setLogId(pLogId);
			_respuesta.setObservacion("OK");
			_respuesta.setResultado(true);

		} else {
			_respuesta.setBaseDatosES("");
			_respuesta.setLogId(pLogId);
			_respuesta.setObservacion("ERROR");
			_respuesta.setResultado(false);

		}

		return _respuesta;
	}

	public Respuesta DownLoadFile(long pLogId, String pDeviceId,
			String pServicio, String pSincronismoInicial, FileSales pFileSales, EasySync pEasySync) throws JSONException,
			ClientProtocolException, IOException, Exception {

		String _url = gGlobal.UrlEasyServer + "/Sincronismo/"
				+ pServicio + "/" + pLogId + "/" + pDeviceId + "/" + pFileSales.getId();
						
		int numeroIntentos = 1;
		String response = "";
		while (numeroIntentos <= MaximoNumeroIntentos) {
			try {
				response = HttpServicio.easyServerDown(_url, pLogId, pDeviceId,
						pSincronismoInicial, pFileSales, pEasySync);
				break;
			} catch (IOException e) {
				if (e.getMessage().indexOf("timed out") != -1) {
					//Log.i("info",
					//		"timed out. intento "
					//				+ String.valueOf(numeroIntentos));
					if (numeroIntentos == MaximoNumeroIntentos) {
						throw e;
					} else {
						++numeroIntentos;
					}
				}
			}

		}

		Respuesta _respuesta = new Respuesta();
		if (response.compareTo("OK") == 0) {
			_respuesta.setBaseDatosES("");
			_respuesta.setLogId(pLogId);
			_respuesta.setObservacion("OK");
			_respuesta.setResultado(true);

		} else {
			_respuesta.setBaseDatosES("");
			_respuesta.setLogId(pLogId);
			_respuesta.setObservacion("ERROR");
			_respuesta.setResultado(false);

		}

		return _respuesta;
	}
	
	public Respuesta UpLoadBaseDatos(long pLogId, String pDeviceId,
			String pServicio) throws ClientProtocolException, IOException,
			JSONException, Exception {

		// String _url = "http://186.31.247.101/EasyServerUnibol/Sincronismo/"
		// + pServicio;
		//String _url = "http://" + ipServidor + "/EasyServerUnibol/Sincronismo/"
		//		+ pServicio;
		String _url = gGlobal.UrlEasyServer + "/Sincronismo/"
				+ pServicio;
		
		

		// String response = HttpServicio.UpFile(_url, pLogId, pDeviceId);
		int numeroIntentos = 1;
		String response = "";
		Respuesta respuesta = new Respuesta();
		while (numeroIntentos <= MaximoNumeroIntentos) {
			try {
				response = HttpServicio.UpFile(_url, pLogId, pDeviceId);

				respuesta = JsonServicio.transformJSON2Respuesta(response);

				//Log.i("info",
				//		"Respuesta upload. intento "
				//				+ String.valueOf(numeroIntentos) + ". "
				//				+ respuesta.getObservacion());
				if (respuesta.getObservacion().indexOf("Cannot initialize") != -1) {
					//Log.i("info",
					//		"Cannot initialize. intento "
					//				+ String.valueOf(numeroIntentos));
					if (numeroIntentos == MaximoNumeroIntentos) {
						break;
					} else {
						++numeroIntentos;
					}
				} else {
					break;
				}

			} catch (IOException e) {
				if (e.getMessage().indexOf("timed out") != -1) {
					//Log.i("info",
					//		"timed out. intento "
					//				+ String.valueOf(numeroIntentos));
					if (numeroIntentos == MaximoNumeroIntentos) {
						throw e;
					} else {
						++numeroIntentos;
					}
				}
			} catch (Exception e) {
				//Log.i("info",
				//		"exception. intento " + String.valueOf(numeroIntentos));
				throw e;

			}
		}

		return respuesta;
	}

	public Respuesta UpLoadRecoveryBaseDatos(long pLogId, String pDeviceId,
			String pServicio) throws ClientProtocolException, IOException,
			JSONException, Exception {

		// String _url = "http://186.31.247.101/EasyServerUnibol/Sincronismo/"
		// + pServicio;
		//String _url = "http://" + ipServidor + "/EasyServerUnibol/Sincronismo/"
		//		+ pServicio;
		String _url = gGlobal.UrlEasyServer + "/Sincronismo/"
				+ pServicio;
		
				

		// String response = HttpServicio.UpFile(_url, pLogId, pDeviceId);
		int numeroIntentos = 1;
		String response = "";
		Respuesta respuesta = new Respuesta();
		while (numeroIntentos <= MaximoNumeroIntentos) {
			try {
				response = HttpServicio.UpFileRecovery(_url, pLogId, pDeviceId);

				respuesta = JsonServicio.transformJSON2Respuesta(response);

				//Log.i("info",
				//		"Respuesta upload. intento "
				//				+ String.valueOf(numeroIntentos) + ". "
				//				+ respuesta.getObservacion());
				if (respuesta.getObservacion().indexOf("Cannot initialize") != -1) {
					//Log.i("info",
					//		"Cannot initialize. intento "
					//				+ String.valueOf(numeroIntentos));
					if (numeroIntentos == MaximoNumeroIntentos) {
						break;
					} else {
						++numeroIntentos;
					}
				} else {
					break;
				}

			} catch (IOException e) {
				if (e.getMessage().indexOf("timed out") != -1) {
					//Log.i("info",
					//		"timed out. intento "
					//				+ String.valueOf(numeroIntentos));
					if (numeroIntentos == MaximoNumeroIntentos) {
						throw e;
					} else {
						++numeroIntentos;
					}
				}
			} catch (Exception e) {
				//Log.i("info",
				//		"exception. intento " + String.valueOf(numeroIntentos));
				throw e;

			}
		}

		return respuesta;
	}

	public Respuesta UpLoadEasyServerFile(long pLogId, String pDeviceId,
			String pServicio, String pNombreArchivo, String pExtension,
			String pTipo, String pArchivo) throws ClientProtocolException,
			IOException, JSONException, Exception {

		// String _url = "http://186.31.247.101/EasyServerUnibol/Sincronismo/"
		// + pServicio;
		//String _url = "http://" + ipServidor + "/EasyServerUnibol/Sincronismo/"
		//		+ pServicio;
		String _url = gGlobal.UrlEasyServer + "/Sincronismo/"
				+ pServicio;
		
				

		// String response = HttpServicio.UpFile(_url, pLogId, pDeviceId);
		int numeroIntentos = 1;
		String response = "";
		Respuesta respuesta = new Respuesta();
		while (numeroIntentos <= MaximoNumeroIntentos) {
			try {
				response = HttpServicio.UpEasyServerFile(_url, pLogId,
						pDeviceId, pNombreArchivo, pExtension, pTipo, pArchivo);

				respuesta = JsonServicio.transformJSON2Respuesta(response);

				//Log.i("info",
				//		"Respuesta uploadEasyServerFile. intento "
				//				+ String.valueOf(numeroIntentos) + ". "
				//				+ respuesta.getObservacion());
				if (respuesta.getObservacion().indexOf("Cannot initialize") != -1) {
					//Log.i("info",
					//		"Cannot initialize. intento "
					//				+ String.valueOf(numeroIntentos));
					if (numeroIntentos == MaximoNumeroIntentos) {
						break;
					} else {
						++numeroIntentos;
					}
				} else {
					break;
				}

			} catch (IOException e) {
				if (e.getMessage().indexOf("timed out") != -1) {
					//Log.i("info",
					//		"timed out. intento "
					//				+ String.valueOf(numeroIntentos));
					if (numeroIntentos == MaximoNumeroIntentos) {
						throw e;
					} else {
						++numeroIntentos;
					}
				}
			} catch (Exception e) {
				//Log.i("info",
				//		"exception. intento " + String.valueOf(numeroIntentos));
				throw e;

			}
		}

		return respuesta;
	}

	public Respuesta CommitSincronismo(
			RequerimientoSincronismo pRequerimientoSincronismo)
			throws ClientProtocolException, JSONException, IOException,
			URISyntaxException, Exception {

		// String _url =
		// "http://186.31.247.101/EasyServerUnibol/Sincronismo/RequerimientoSincronismo";
		//String _url = "http://" + ipServidor
		//		+ "/EasyServerUnibol/Sincronismo/CommitDown";
		String _url = gGlobal.UrlEasyServer + "/Sincronismo/CommitDown";

				
		// String response = HttpServicio.requerimientoSincronismo(_url,
		// pRequerimientoSincronismo);

		int numeroIntentos = 1;
		String response = "";
		while (numeroIntentos <= MaximoNumeroIntentos) {
			try {
				response = HttpServicio.requerimientoSincronismo(_url,
						pRequerimientoSincronismo);
				break;
			} catch (IOException e) {
				if (e.getMessage().indexOf("timed out") != -1) {
					//Log.i("info",
					//		"timed out. intento "
					//				+ String.valueOf(numeroIntentos));
					if (numeroIntentos == MaximoNumeroIntentos) {
						throw e;
					} else {
						++numeroIntentos;
					}
				}
			}

		}

		return JsonServicio.transformJSON2Respuesta(response);
	}

	public Respuesta InstanciaSincronismo(
			RequerimientoSincronismo pRequerimientoSincronismo, String pServicio)
			throws ClientProtocolException, JSONException, IOException,
			URISyntaxException, Exception {

		// String _url =
		// "http://186.31.247.101/EasyServerUnibol/Sincronismo/RequerimientoSincronismo";
		//String _url = "http://" + ipServidor + "/EasyServerUnibol/Sincronismo/"
		//		+ pServicio;
		String _url = gGlobal.UrlEasyServer + "/Sincronismo/"
				+ pServicio;

				
		// //Log.i("info", "requerimiento de preparar down:" + _url);

		// String response = HttpServicio.requerimientoSincronismo(_url,
		// pRequerimientoSincronismo);

		int numeroIntentos = 1;
		String response = "";
		while (numeroIntentos <= MaximoNumeroIntentos) {
			try {
				response = HttpServicio.requerimientoSincronismo(_url,
						pRequerimientoSincronismo);
				break;
			} catch (IOException e) {
				if (e.getMessage().indexOf("timed out") != -1) {
					//Log.i("info",
					//		"timed out. intento "
					//				+ String.valueOf(numeroIntentos));
					if (numeroIntentos == MaximoNumeroIntentos) {
						throw e;
					} else {
						++numeroIntentos;
					}
				}
			}

		}

		//Log.i("info", "respuesta preparar: " + response);
		return JsonServicio.transformJSON2Respuesta(response);
	}

}
