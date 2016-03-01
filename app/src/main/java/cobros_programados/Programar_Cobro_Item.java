package cobros_programados;

public class Programar_Cobro_Item {
	private String cartera_tipo_documento;
	private String cartera_numero_documento;
	private String cartera_fecha_documento;
	private String cartera_fecha_vencimiento;
	private String cartera_saldo_pendiente;

	public Programar_Cobro_Item(String pTipoDocumento, String pNumeroDocumento, String pFechaDocumento, String pFechaVencimiento, String pSaldoPendiente)
	{
		this.cartera_tipo_documento = pTipoDocumento;
		this.cartera_numero_documento = pNumeroDocumento;
		this.cartera_fecha_documento = pFechaDocumento;
		this.cartera_fecha_vencimiento = pFechaVencimiento;
		this.cartera_saldo_pendiente = "$ " + pSaldoPendiente;
	}
	
/*
 *Getters	
 */

	public String getTipoDocumento() {
		return cartera_tipo_documento;
	}
	public String getNumeroDocumento() {
		return cartera_numero_documento;
	}
	public String getFechaDocumento() {
		return cartera_fecha_documento;
	}
	public String getFechaVencimiento() {
		return cartera_fecha_vencimiento;
	}
	public String getSaldoPendiente() {
		return cartera_saldo_pendiente;
	}
}