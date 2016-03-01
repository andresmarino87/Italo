package easysync;

import java.io.Serializable;

public class RequerimientoSincronismo  implements Serializable{

	
	
	private String login;

	public void setLogin(String value) {
		this.login = value;
	}

	public String getLogin() {
		return this.login;
	}

	private String deviceId;

	public void setDeviceId(String value) {
		this.deviceId = value;
	}

	public String getDeviceId() {
		return this.deviceId;
	}


	private String password;

	public void setPassword(String value) {
		this.password = value;
	}

	public String getPassword() {
		return this.password;
	}

	private boolean sincronismoInicial;

	public void setSincronismoInicial(boolean value) {
		this.sincronismoInicial = value;
	}

	public boolean getSincronismoInicial() {
		return this.sincronismoInicial;
	}

	private int version;

	public void setVersion(int value) {
		this.version = value;
	}

	public int getVersion() {
		return this.version;
	}

	private int revision;

	public void setRevision(int value) {
		this.revision = value;
	}

	public int getRevision() {
		return this.revision;
	}


	private int compilacion;

	public void setCompilacion(int value) {
		this.compilacion = value;
	}

	public int getCompilacion() {
		return this.compilacion;
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

	private String observacion = "";

	public void setObservacion(String value) {
		this.observacion = value;
	}

	public String getObservacion() {
		return this.observacion;
	}

}
