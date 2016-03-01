package cobros;

import java.io.Serializable;

public class C_documentos implements Serializable{
    private static final long serialVersionUID = 1L;

	private String tipo_documento;
	private String documento_id;
	private float saldo_anterior;
	private float valor_cobrado;
	private float valor_desc_pronto_pago;
	private String aplicar_desc_pronto_pago;
	private float valor_disponible;
	private float valor_proceso;
	private int antiguedad;
	private String fecha_documento;
	private String bandera_saldo;
	private String diferencia_es_efectivo;
	private String diferencia_es_posfechado;
	private int diferencia_ult_id_pago;
	private int diferencia_ult_id_pago_det;
	private String observaciones;

	public C_documentos(String tipo_documento, 
			String documento_id,
			float saldo_anterior,
			float valor_cobrado,
			float valor_desc_pronto_pago,
			String aplicar_desc_pronto_pago,
			float valor_disponible,
			float valor_proceso,
			int antiguedad,
			String fecha_documento,
			String bandera_saldo,
			String diferencia_es_efectivo,
			String diferencia_es_posfechado,
			int diferencia_ult_id_pago,
			int diferencia_ult_id_pago_det,
			String observaciones){
		this.tipo_documento=tipo_documento;
		this.documento_id=documento_id;
		this.saldo_anterior=saldo_anterior;
		this.valor_cobrado=valor_cobrado;
		this.valor_desc_pronto_pago=valor_desc_pronto_pago;
		this.aplicar_desc_pronto_pago=aplicar_desc_pronto_pago;
		this.valor_disponible=valor_disponible;
		this.valor_proceso=valor_proceso;
		this.antiguedad=antiguedad;
		this.fecha_documento=fecha_documento;
		this.bandera_saldo=bandera_saldo;
		this.diferencia_es_efectivo=diferencia_es_efectivo;
		this.diferencia_es_posfechado=diferencia_es_posfechado;
		this.diferencia_ult_id_pago=diferencia_ult_id_pago;
		this.diferencia_ult_id_pago_det=diferencia_ult_id_pago_det;
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

	public void setValorDescProntoPago(float valor_desc_pronto_pago) {
		this.valor_desc_pronto_pago=valor_desc_pronto_pago;
	}	

	public void setAplicarDescProntoPago(String aplicar_desc_pronto_pago) {
		this.aplicar_desc_pronto_pago=aplicar_desc_pronto_pago;
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
	
	public void setBanderaSaldo(String bandera_saldo){
		this.bandera_saldo=bandera_saldo;
	}
	
	public void setDiferenciaEsEfectivo(String diferencia_es_efectivo){
		this.diferencia_es_efectivo=diferencia_es_efectivo;
	}
	
	public void setDiferenciaEsPosfechado(String diferencia_es_posfechado){
		this.diferencia_es_posfechado=diferencia_es_posfechado;
	}
	
	public void setDiferenciaUltimoIdPago(int diferencia_ult_id_pago){
		this.diferencia_ult_id_pago=diferencia_ult_id_pago;
	}
	
	public void setDiferenciaUltimoIdDet(int diferencia_ult_id_pago_det){
		this.diferencia_ult_id_pago_det=diferencia_ult_id_pago_det;
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
	
	public float getValorDescProntoPago() {
		return this.valor_desc_pronto_pago;
	}
	
	public String getAplicarDescProntoPago() {
		return this.aplicar_desc_pronto_pago;
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
	
	public String getBanderaSaldo(){
		return this.bandera_saldo;
	}
	
	public String getDiferenciaEsEfectivo(){
		return this.diferencia_es_efectivo;
	}
	
	public String getDiferenciaEsPosfechado(){
		return this.diferencia_es_posfechado;
	}
	
	public int getDiferenciaUltIdPago(){
		return this.diferencia_ult_id_pago;
	}
	
	public int getDiferenciaUltIdPagoDet(){
		return this.diferencia_ult_id_pago_det;
	}
	
	public String getObs(){
		return this.observaciones;
	}
}