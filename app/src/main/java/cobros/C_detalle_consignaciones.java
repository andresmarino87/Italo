package cobros;

import java.io.Serializable;

public class C_detalle_consignaciones implements Serializable{
    private static final long serialVersionUID = 1L;

	private int identificador_pago;
	private int identificador_detalle;
	private String tipo_pago_id;
	private String forma_pago_id;
	private String banco_id;
	private String numero_cuenta;
	private String numero_documento;
	private String fecha_documento;
	private float valor_documento;
	private float valor_proceso;

	public C_detalle_consignaciones(int identificador_pago, 
			int identificador_detalle,
			String tipo_pago_id,
			String forma_pago_id,
			String banco_id,
			String numero_cuenta,
			String numero_documento,
			String fecha_documento,
			float valor_documento,
			float valor_proceso){
		this.identificador_pago=identificador_pago;
		this.identificador_detalle=identificador_detalle;
		this.tipo_pago_id=tipo_pago_id;
		this.forma_pago_id=forma_pago_id;
		this.banco_id=banco_id;
		this.numero_cuenta=numero_cuenta;
		this.numero_documento=numero_documento;
		this.fecha_documento=fecha_documento;
		this.valor_documento=valor_documento;
		this.valor_proceso=valor_proceso;
	}
	
	//setters
	public void setIdentificadorPago(int identificador_pago) {
		this.identificador_pago=identificador_pago;
	}	
	
	public void setIdentificadorDetalle(int identificador_detalle) {
		this.identificador_detalle=identificador_detalle;
	}	

	public void setTipoPagoId(String tipo_pago_id) {
		this.tipo_pago_id=tipo_pago_id;
	}	

	public void setFormaPagoId(String forma_pago_id) {
		this.forma_pago_id=forma_pago_id;
	}	

	public void setBancoId(String banco_id) {
		this.banco_id=banco_id;
	}	

	public void setNumeroCuenta(String numero_cuenta) {
		this.numero_cuenta=numero_cuenta;
	}	
	
	public void setNumeroDocumento(String numero_documento){
		this.numero_documento=numero_documento;
	}

	public void setFechaDoc(String fecha_documento){
		this.fecha_documento=fecha_documento;
	}
	
	public void setValorDoc(float valor_documento){
		this.valor_documento=valor_documento;
	}
	
	public void setValorProceso(float valor_proceso){
		this.valor_proceso=valor_proceso;
	}

	//getter
	public int getIdentificadoPago() {
		return this.identificador_pago;
	}
	
	public int getIdentifiicadorDetalle() {
		return this.identificador_detalle;
	}
	
	public String getTipoPagoId() {
		return this.tipo_pago_id;
	}
	
	public String getFormaPagoId() {
		return this.forma_pago_id;
	}
	
	public String getBancoId() {
		return this.banco_id;
	}
	
	public String getNumeroCuenta() {
		return this.numero_cuenta;
	}

	public String getNumeroDoc(){
		return this.numero_documento;
	}

	public String getFechaDoc(){
		return this.fecha_documento;
	}

	public float getValorDoc(){
		return this.valor_documento;
	}
	
	public float getValorProceso(){
		return this.valor_proceso;
	}
}