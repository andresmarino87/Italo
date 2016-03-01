package cobros;

import java.io.Serializable;

public class C_pagos implements Serializable{
    private static final long serialVersionUID = 1L;
	
	private int identificador_pago;
	private String tipo_pago_id;
	private String banco_id;
	private String numero_cuenta;
	private String numero_documento;
	private String fecha_docuento;
	private float valor_documento;
	private float valor_procesado;

	public C_pagos(int identificador_pago, 
			String tipo_pago_id,
			String banco_id,
			String numero_cuenta,
			String numero_documento,
			String fecha_docuento,
			float valor_documento,
			float valor_procesado){
		this.identificador_pago=identificador_pago;
		this.tipo_pago_id=tipo_pago_id;
		this.banco_id=banco_id;
		this.numero_cuenta=numero_cuenta;
		this.numero_documento=numero_documento;
		this.fecha_docuento=fecha_docuento;
		this.valor_documento=valor_documento;
		this.valor_procesado=valor_procesado;
	}
	
	//setters
	public void setIdentificador(int identificador_pago) {
		this.identificador_pago=identificador_pago;
	}	
	
	public void setTipoPagoId(String tipo_pago_id) {
		this.tipo_pago_id=tipo_pago_id;
	}	

	public void setBancoId(String banco_id) {
		this.banco_id=banco_id;
	}	

	public void setNumeroCuenta(String numero_cuenta) {
		this.numero_cuenta=numero_cuenta;
	}	

	public void setNumeroDoc(String numero_documento) {
		this.numero_documento=numero_documento;
	}	

	public void setFechaDocumento(String fecha_docuento) {
		this.fecha_docuento=fecha_docuento;
	}	
	
	public void setValorDocumento(float valor_documento){
		this.valor_documento=valor_documento;
	}

	public void setValorProcesado(float valor_procesado){
		this.valor_procesado=valor_procesado;
	}
	
	//getter
	public int getIdentificadorPago() {
		return this.identificador_pago;
	}
	
	public String getTipoPagoId() {
		return this.tipo_pago_id;
	}
	
	public String getBancoId() {
		return this.banco_id;
	}
	
	public String getNumeroCuenta() {
		return this.numero_cuenta;
	}
	
	public String getNumeroDoc() {
		return this.numero_documento;
	}
	
	public String getFechaDoc() {
		return this.fecha_docuento;
	}
	
	public float getValorDocumento(){
		return this.valor_documento;
	}

	public float getValorProcesado(){
		return this.valor_procesado;
	}
	
}
