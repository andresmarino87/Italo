package cartera;

public class Cartera_item {
	private String nombre_cliente;
	private String codigo_cliente;
	private String cartera_tipo_documento;
	private String cartera_referencia;
	private String cartera_fecha_documento;
	private String cartera_fecha_vencimiento;
	private String cartera_demora;
	private String cartera_saldo;

	public Cartera_item(String nombreCliente, String codigoCliente, String tipoDoc, String referecia, String fechaDoc, String fechaVen, String demora, String saldo){
		this.nombre_cliente= nombreCliente;
		this.codigo_cliente= codigoCliente;
		this.cartera_tipo_documento= tipoDoc;
		this.cartera_referencia= referecia;
		this.cartera_fecha_documento= fechaDoc;
		this.cartera_fecha_vencimiento= fechaVen;
		this.cartera_demora= demora;
		this.cartera_saldo= "$ "+saldo;
		
	}
	
/*
 *Getters	
 */

	public String getNombre_cliente() {
		return nombre_cliente;
	}

	public String getCodigo_cliente() {
		return codigo_cliente;
	}
	public String getTipo_Documento() {
		return cartera_tipo_documento;
	}
	public String getReferencia() {
		return cartera_referencia;
	}
	public String getFecha_Doc() {
		return cartera_fecha_documento;
	}
	public String getFecha_Venc() {
		return cartera_fecha_vencimiento;
	}
	public String getDemora() {
		return cartera_demora;
	}
	public String getSaldo() {
		return cartera_saldo;
	}
}