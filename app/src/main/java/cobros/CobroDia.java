package cobros;

public class CobroDia {
	private String nPago = "";
	private String documento = "";
	private double saldo = 0;
	private double pago = 0;
	private double diferencia = 0;
	private String cobroIdEsu="";
	private String motivoId="";

	public CobroDia(String nPago, String documento, double saldo, double pago, double diferencia, String cobroIdEsu, String motivoId){
		this.nPago=nPago;
		this.documento=documento;
		this.saldo=saldo;
		this.pago=pago;
		this.diferencia=diferencia;
		this.cobroIdEsu=cobroIdEsu;
		this.motivoId=motivoId;
	}
	
	/*
	 * 
	 * Getters
	 * 
	 */
	
	public String getNPago() {
		return nPago;
	}
	
	public String getDocumento() {
		return documento;
	}
	
	public double getSaldo() {
		return saldo;
	}
	
	public double getPago() {
		return pago;
	}
	
	public double getDiferencia() {
		return diferencia;
	}

	public String getCobroIdEsu() {
		return cobroIdEsu;
	}

	public String getMotivoId() {
		return motivoId;
	}
}