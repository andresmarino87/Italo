package cobros;

public class C_encabezado {
	private int cobro_id;
	private String cliente_id;
	private String maquina_id;
	private boolean hay_abono;
	private float menor_valor_pago;
	private float mayor_valor_pago;	
	private String Abono_tipo_documento;
	private String Abono_documento_id;

	public C_encabezado(){
		this.cobro_id=0;
		this.cliente_id="";
		this.maquina_id="";
		this.hay_abono=false;
		this.menor_valor_pago=0;
		this.mayor_valor_pago=0;
		this.Abono_tipo_documento="";
		this.Abono_documento_id="";
	}
	
	public C_encabezado(int cobro_id,
			String cliente_id,
			String maquina_id,
			boolean hay_abono,
			float menor_valor_pago,
			float mayor_valor_pago,
			String Abono_tipo_documento,
			String Abono_documento_id){
		this.cobro_id=cobro_id;
		this.cliente_id=cliente_id;
		this.maquina_id=maquina_id;
		this.hay_abono=hay_abono;
		this.menor_valor_pago=menor_valor_pago;
		this.mayor_valor_pago=mayor_valor_pago;
		this.Abono_tipo_documento=Abono_tipo_documento;
		this.Abono_documento_id=Abono_documento_id;
	}
		
	//setters
	public void setCobroId(int cobro_id) {
		this.cobro_id=cobro_id;
	}	
		
	public void setClienteId(String cliente_id) {
		this.cliente_id=cliente_id;
	}	

	public void setMaquinaId(String maquina_id) {
		this.maquina_id=maquina_id;
	}	

	public void setHayAbono(boolean hay_abono) {
		this.hay_abono=hay_abono;
	}	

	public void setMenorValorPago(float menor_valor_pago) {
		this.menor_valor_pago=menor_valor_pago;
	}	
		
	public void setMayorValorPago(float mayor_valor_pago){
		this.mayor_valor_pago=mayor_valor_pago;
	}

	public void setAbonoTipoDoc(String Abono_tipo_documento){
		this.Abono_tipo_documento=Abono_tipo_documento;
	}
		
	public void setAbonoDocumentoId(String Abono_documento_id){
		this.Abono_documento_id=Abono_documento_id;
	}

	//getter
	public int getCobroId() {
		return this.cobro_id;
	}
		
	public String getClienteId() {
		return this.cliente_id;
	}
		
	public String getMaquinaId() {
		return this.maquina_id;
	}
		
	public boolean getHayAbono() {
		return this.hay_abono;
	}

	public float getMenorValorPago() {
		return this.menor_valor_pago;
	}
		
	public float getMayorValorPago(){
		return this.mayor_valor_pago;
	}

	public String getAbonoTipoDoc(){
		return this.Abono_tipo_documento;
	}

	public String getAbonoDocId(){
		return this.Abono_documento_id;
	}
}
