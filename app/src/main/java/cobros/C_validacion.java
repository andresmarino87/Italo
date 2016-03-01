package cobros;

public class C_validacion {
	private String estado;
	private String mensaje;

	C_validacion(){
		this.estado="";
		this.mensaje="";
	}
	
	C_validacion(String estado, String mensaje){
		this.estado=estado;
		this.mensaje=mensaje;
	}
	
	//Setters 
	public void setEstado(String estado){
		this.estado=estado;
	}
	
	public void setMensaje(String mensaje){
		this.mensaje=mensaje;
	}

	//Getters
	public String getEstado(){
		return this.estado;
	}
	
	public String getMensaje(){
		return this.mensaje;
	}
}
