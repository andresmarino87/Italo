package pedidos;

public class ValPed {
	private String continuar;
	private String solicitar_compromiso;
	private String compromiso_obligatorio;
	private String estado_pedido;
	private String motivo;
	private String complemento_observacion;
	private String mensaje_de_error;

	public ValPed(){
		continuar="";
		solicitar_compromiso="";
		compromiso_obligatorio="";
		estado_pedido="";
		motivo="";
		complemento_observacion="";
		mensaje_de_error="";
	}
	
	//Getters
	public String getContinuar(){
		return continuar;
	}
	
	public String getSolicitarCompromiso(){
		return solicitar_compromiso;
	}
	
	public String getCompromisoObligatorio(){
		return compromiso_obligatorio;
	}
	
	public String getEstdoPedido(){
		return estado_pedido;
	}
	
	public String getMotivo(){
		return motivo;
	}
	
	public String getComplementoObs(){
		return complemento_observacion;
	}
	
	public String getMensajeError(){
		return mensaje_de_error;
	}

	//Setters
	public void setContinuar(String continuar){
		this.continuar=continuar;
	}
	
	public void setSolicitarCompromiso(String solicitar_compromiso){
		this.solicitar_compromiso=solicitar_compromiso;
	}
	
	public void setCompromisoObligatorio(String compromiso_obligatorio){
		this.compromiso_obligatorio=compromiso_obligatorio;
	}
	
	public void setEstdoPedido(String estado_pedido){
		this.estado_pedido=estado_pedido;
	}
	
	public void setMotivo(String motivo){
		this.motivo=motivo;
	}
	
	public void setComplementoObs(String complemento_observacion){
		this.complemento_observacion=complemento_observacion;
	}
	
	public void setMensajeError(String mensaje_de_error){
		this.mensaje_de_error=mensaje_de_error;
	}
}
