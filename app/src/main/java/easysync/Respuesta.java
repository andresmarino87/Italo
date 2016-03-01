package easysync;

public class Respuesta {

	private Boolean resultado;

	public void setResultado(Boolean value) {
		this.resultado = value;
	}

	public Boolean getResultado() {
		return this.resultado;
	}

	private String observacion;

	public void setObservacion(String value) {
		this.observacion = value;
	}

	public String getObservacion() {
		return this.observacion;
	}

	private long logId;

	public void setLogId(long value) {
		this.logId = value;
	}

	public long getLogId() {
		return this.logId;
	}

	private String baseDatosES;

	public void setBaseDatosES(String value) {
		this.baseDatosES = value;
	}

	public String getBaseDatosES() {
		return this.baseDatosES;
	}
}
