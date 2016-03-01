package cobros;

public class C_cruce_docs_negativos {
	private String tipo_documento;
	private String documento_id;
	private String tipo_documento_neg;
	private String documento_id_neg;
	private float valor_cruzado;
	private String forma_pago_id;
	private String fecha_documento;
	private float saldo_anterior;
	
	public C_cruce_docs_negativos(){
		this.tipo_documento="";
		this.documento_id="";
		this.tipo_documento_neg="";
		this.documento_id_neg="";
		this.valor_cruzado=0;
		this.forma_pago_id="";
		this.fecha_documento="";
		this.saldo_anterior=0;
	}
	
	public C_cruce_docs_negativos(String tipo_documento,
			String documento_id,
			String tipo_documento_neg, 
			String documento_id_neg,
			float valor_cruzado,
			String forma_pago_id,
			String fecha_documento,
			float saldo_anterior){
		this.tipo_documento=tipo_documento;
		this.documento_id=documento_id;
		this.tipo_documento_neg=tipo_documento_neg;
		this.documento_id_neg=documento_id_neg;
		this.valor_cruzado=valor_cruzado;
		this.forma_pago_id=forma_pago_id;
		this.fecha_documento=fecha_documento;
		this.saldo_anterior=saldo_anterior;
	}

	//setters	
	public void setTipoDoc(String tipo_documento) {
		this.tipo_documento=tipo_documento;
	}	

	public void setDocId(String documento_id) {
		this.documento_id=documento_id;
	}	

	public void setTipoDocNegativo(String tipo_documento_neg) {
		this.tipo_documento_neg=tipo_documento_neg;
	}	
	
	public void setDocIdNegativo(String documento_id_neg) {
		this.documento_id_neg=documento_id_neg;
	}	
	
	public void setValorCruzado(float valor_cruzado) {
		this.valor_cruzado=valor_cruzado;
	}	

	public void setFormaPagoId(String forma_pago_id) {
		this.forma_pago_id=forma_pago_id;
	}	

	public void setFechaDoc(String fecha_documento){
		this.fecha_documento=fecha_documento;
	}

	public void setSaldoAnterior(float saldo_anterior){
		this.saldo_anterior=saldo_anterior;
	}

	//getter
	public String getTipoDoc() {
		return this.tipo_documento;
	}

	public String getDocId() {
		return this.documento_id;
	}
	
	public String getTipoDocNeg() {
		return this.tipo_documento_neg;
	}
	
	public String getDocIdNeg() {
		return this.documento_id_neg;
	}
	
	public float getValorCruzado() {
		return this.valor_cruzado;
	}

	public String getFormaPagoId() {
		return this.forma_pago_id;
	}
	
	public String getFechaDoc() {
		return this.fecha_documento;
	}
		
	public float getSaldoAnterior(){
		return this.saldo_anterior;
	}
}