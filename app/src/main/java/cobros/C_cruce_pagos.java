package cobros;

public class C_cruce_pagos {
	private String tipo_documento;
	private String documento_id;
	private int Identificador_pago;
	private String codigo_mov_tipo_doc;
	private String forma_pago_id;
	private String numero_relacion;
	private float valor_cruzado;

	public C_cruce_pagos(){
		this.tipo_documento="";
		this.documento_id="";
		this.Identificador_pago=0;
		this.codigo_mov_tipo_doc="";
		this.forma_pago_id="";
		this.numero_relacion="";
		this.valor_cruzado=0;
	}
	
	public C_cruce_pagos(String tipo_documento,
			String documento_id,
			int Identificador_pago, 
			String codigo_mov_tipo_doc,
			String forma_pago_id,
			String numero_relacion,
			float valor_cruzado){
		this.tipo_documento=tipo_documento;
		this.documento_id=documento_id;
		this.Identificador_pago=Identificador_pago;
		this.codigo_mov_tipo_doc=codigo_mov_tipo_doc;
		this.forma_pago_id=forma_pago_id;
		this.numero_relacion=numero_relacion;
		this.valor_cruzado=valor_cruzado;
	}

	//setters	
	public void setTipoDoc(String tipo_documento) {
		this.tipo_documento=tipo_documento;
	}	

	public void setDocId(String documento_id) {
		this.documento_id=documento_id;
	}	

	public void setIdentificadorPago(int Identificador_pago) {
		this.Identificador_pago=Identificador_pago;
	}	
	
	public void setCodigoMovTipoDoc(String codigo_mov_tipo_doc){
		this.codigo_mov_tipo_doc=codigo_mov_tipo_doc;
	}

	public void setFormaPagoId(String forma_pago_id) {
		this.forma_pago_id=forma_pago_id;
	}	

	public void setNumeroRelacion(String numero_relacion){
		this.numero_relacion=numero_relacion;
	}

	public void setValorCruzado(float valor_cruzado) {
		this.valor_cruzado=valor_cruzado;
	}	

	//getter
		public String getTipoDoc() {
		return this.tipo_documento;
	}

	public String getDocId() {
		return this.documento_id;
	}
	
	public int getIdentificador_pago() {
		return this.Identificador_pago;
	}

	public String getCodigoMovTipoDoc() {
		return this.codigo_mov_tipo_doc;
	}
	
	public String getFormaPagoId() {
		return this.forma_pago_id;
	}

	public String getNumeroRelacion(){
		return this.numero_relacion;
	}
	
	public float getValorCruzado() {
		return this.valor_cruzado;
	}
}