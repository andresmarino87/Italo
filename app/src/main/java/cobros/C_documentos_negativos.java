package cobros;

import java.io.Serializable;

public class C_documentos_negativos implements Serializable{
    private static final long serialVersionUID = 1L;

	private String tipo_documento;
	private String documento_id;
	private float saldo_anterior;
	private float valor_cobrado;
	private float valor_disponible;
	private float valor_proceso;
	private int antiguedad;
	private String fecha_documento;
	private String observaciones;

	public C_documentos_negativos(String tipo_documento, 
			String documento_id,
			float saldo_anterior,
			float valor_cobrado,
			float valor_disponible,
			float valor_proceso,
			int antiguedad,
			String fecha_documento,
			String observaciones){
		this.tipo_documento=tipo_documento;
		this.documento_id=documento_id;
		this.saldo_anterior=saldo_anterior;
		this.valor_cobrado=valor_cobrado;
		this.valor_disponible=valor_disponible;
		this.valor_proceso=valor_proceso;
		this.antiguedad=antiguedad;
		this.fecha_documento=fecha_documento;
		this.observaciones=observaciones;
	}
	
	//setters
	public void setTipoDoc(String tipo_documento) {
		this.tipo_documento=tipo_documento;
	}	
	
	public void setDocId(String documento_id) {
		this.documento_id=documento_id;
	}	

	public void setSaldoAnterior(float saldo_anterior) {
		this.saldo_anterior=saldo_anterior;
	}	

	public void setValorCobrado(float valor_cobrado) {
		this.valor_cobrado=valor_cobrado;
	}	
	
	public void setValorDisponible(float valor_disponible){
		this.valor_disponible=valor_disponible;
	}

	public void setValorProceso(float valor_proceso){
		this.valor_proceso=valor_proceso;
	}
	
	public void setAntiguedad(int antiguedad){
		this.antiguedad=antiguedad;
	}
	
	public void setFechaDoc(String fecha_documento){
		this.fecha_documento=fecha_documento;
	}
	
	public void setObs(String observaciones){
		this.observaciones=observaciones;
	}
	
	//getter
	public String getTipoDoc() {
		return this.tipo_documento;
	}
	
	public String getDocId() {
		return this.documento_id;
	}
	
	public float getSaldoAnterior() {
		return this.saldo_anterior;
	}
	
	public float getValorCobrado() {
		return this.valor_cobrado;
	}
	
	public float getValorDisponible() {
		return this.valor_disponible;
	}
	
	public float getValorProceso(){
		return this.valor_proceso;
	}

	public int getAntiguedad(){
		return this.antiguedad;
	}
	
	public String getFechaDoc(){
		return this.fecha_documento;
	}
	
	public String getObs(){
		return this.observaciones;
	}
}