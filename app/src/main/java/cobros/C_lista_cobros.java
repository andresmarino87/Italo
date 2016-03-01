package cobros;

public class C_lista_cobros {
	private String doc_id;
	private String doc_tipo;
	private String fecha_factura;
	private String fecha_vencimiento;
	private int dias_vencimiento;
	private float saldo_pendiente;
	private String doc_tipo_id;
	private String fecha_factura_bd;
	private float valor_pronto_pago;
	private String bandera_saldo;

	public C_lista_cobros(String doc_id, 
			String doc_tipo,
			String fecha_factura,
			String fecha_vencimiento,
			int dias_vencimiento,
			float saldo_pendiente,
			String doc_tipo_id,
			String fecha_factura_bd,
			float valor_pronto_pago,
			String bandera_saldo){
		this.doc_id=doc_id;
		this.doc_tipo=doc_tipo;
		this.fecha_factura=fecha_factura;
		this.fecha_vencimiento=fecha_vencimiento;
		this.dias_vencimiento=dias_vencimiento;
		this.saldo_pendiente=saldo_pendiente;
		this.doc_tipo_id=doc_tipo_id;
		this.fecha_factura_bd=fecha_factura_bd;
		this.valor_pronto_pago=valor_pronto_pago;
		this.bandera_saldo=bandera_saldo;
	}
		
	//getter
	public String getDocId() {
		return this.doc_id;
	}
		
	public String getDocTipo() {
		return this.doc_tipo;
	}
	
	public String getFechaFactura() {
		return this.fecha_factura;
	}
	
	public String getFechaVencimiento() {
		return this.fecha_vencimiento;
	}
	
	public int getDiasVencimiento() {
		return this.dias_vencimiento;
	}
	
	public float getSaldoPendiente() {
		return this.saldo_pendiente;
	}
	
	public String getDocTipoId(){
		return this.doc_tipo_id;
	}

	public String getFechaFacturaBD(){
		return this.fecha_factura_bd;
	}
	
	public float getValorProntoPago(){
		return this.valor_pronto_pago;
	}
	
	public String getBanderaSaldo(){
		return this.bandera_saldo;
	}
}
