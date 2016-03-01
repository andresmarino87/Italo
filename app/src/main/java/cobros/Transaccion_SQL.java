package cobros;

public class Transaccion_SQL {
	private String estado;
	private String sentencia_SQL;
	private String error_id;
	private String error_msj;
		
	public Transaccion_SQL(){
		this.estado="";
		this.sentencia_SQL="";
		this.error_id="";
		this.error_msj="";
	}
		
	//Setters
	public void setEstado(String estado){
		this.estado=estado;
		return;
	}
		
	public void setSentenciaSQL(String sentencia_SQL){
		this.sentencia_SQL=sentencia_SQL;
		return;
	}

	public void setErrorId(String error_id){
		this.error_id=error_id;
		return;
	}

	public void setErrorMsj(String error_msj){
		this.error_msj=error_msj;
		return;
	}

	//Getters
	public String getEstado(){
		return this.estado;
	}

	public String getSentenciaSQL(){
		return this.sentencia_SQL;
	}

	public String getErrorId(){
		return this.error_id;
	}

	public String getErrorMsj(){
		return this.error_msj;
	}
}