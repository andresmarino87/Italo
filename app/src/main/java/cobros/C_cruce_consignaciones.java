package cobros;

public class C_cruce_consignaciones {
	private String tipo_documento;
	private String documento_id;
	private int Identificador_pago;
	private int Identificador_detalle;
	private float valor_cruzado;
	private String codigo_mov_tipo_doc;
	private String numero_relacion;
	private String forma_pago_id;

	public C_cruce_consignaciones(){
		this.tipo_documento="";
		this.documento_id="";
		this.Identificador_pago=0;
		this.Identificador_detalle=0;
		this.valor_cruzado=0;
		this.codigo_mov_tipo_doc="";
		this.numero_relacion="";
		this.forma_pago_id="";
	}
	
	public C_cruce_consignaciones(String tipo_documento,
			String documento_id,
			int Identificador_pago, 
			int Identificador_detalle,
			float valor_cruzado,
			String codigo_mov_tipo_doc,
			String numero_relacion,
			String forma_pago_id){
		this.tipo_documento=tipo_documento;
		this.documento_id=documento_id;
		this.Identificador_pago=Identificador_pago;
		this.Identificador_detalle=Identificador_detalle;
		this.valor_cruzado=valor_cruzado;
		this.codigo_mov_tipo_doc=codigo_mov_tipo_doc;
		this.numero_relacion=numero_relacion;
		this.forma_pago_id=forma_pago_id;
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
	
	public void setIdentificadorDetalle(int Identificador_detalle) {
		this.Identificador_detalle=Identificador_detalle;
	}	

	public void setValorCruzado(float valor_cruzado) {
		this.valor_cruzado=valor_cruzado;
	}	

	public void setCodigoMovTipoDoc(String codigo_mov_tipo_doc){
		this.codigo_mov_tipo_doc=codigo_mov_tipo_doc;
	}

	public void setNumeroRelacion(String numero_relacion){
		this.numero_relacion=numero_relacion;
	}

	public void setFormaPagoId(String forma_pago_id) {
		this.forma_pago_id=forma_pago_id;
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
	
	public int getIdentificadorDetalle() {
		return this.Identificador_detalle;
	}
	
	public float getValorCruzado() {
		return this.valor_cruzado;
	}
	
	public String getCodigoMovTipoDoc() {
		return this.codigo_mov_tipo_doc;
	}
		
	public String getNumeroRelacion(){
		return this.numero_relacion;
	}

	public String getFormaPagoId() {
		return this.forma_pago_id;
	}
}