package cobros;

public class Cobros_Dia_Item {
	private String sinc;
	private String cliente_id;
	private String cliente;
	private String cobro_id;
	private String hora;
	private double totalPago;
	private String motivo_anulacon="";
	private String cobro_esu_id="";
	
	public Cobros_Dia_Item(
			String sinc,
			String cliente_id,
			String cliente, 
			String cobro_id, 
			String hora, 
			double totalPago,
			String motivo_anulacon,
			String cobro_esu_id){
		this.sinc=sinc;
		this.cliente_id=cliente_id;
		this.cliente=cliente;
		this.cobro_id=cobro_id;
		this.hora=hora;
		this.totalPago=totalPago;
		this.motivo_anulacon=motivo_anulacon;
		this.cobro_esu_id=cobro_esu_id;
	}
	
	//setters
	public void setSinc(String sinc) {
		this.sinc=sinc;
	}	

	public void setClienteId(String cliente_id) {
		this.cliente_id=cliente_id;
	}	
	
	public void setCliente(String cliente) {
		this.cliente=cliente;
	}	

	public void setCobroId(String cobro_id) {
		this.cobro_id=cobro_id;
	}	
	
	public void setHora(String hora) {
		this.hora=hora;
	}	

	public void setFecha(double totalPago) {
		this.totalPago=totalPago;
	}	

	//getter
	public String getSinc() {
		return this.sinc;
	}

	public String getClienteId() {
		return this.cliente_id;
	}

	public String getCliente() {
		return this.cliente;
	}

	public String getCobroId() {
		return this.cobro_id;
	}

	public String getHora() {
		return this.hora;
	}
	
	public double getTotalPago() {
		return this.totalPago;
	}
	
	public String getMotivoAnulacion() {
		return motivo_anulacon;
	}

	public String getEsuId() {
		return cobro_esu_id;
	}
}
