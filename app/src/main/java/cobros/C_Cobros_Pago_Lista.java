package cobros;

public class C_Cobros_Pago_Lista {
	private int identificador_pago;
	private String banco_id;
	private String banco;
	private String tipo_pago_id;
	private String nombre_pago;
	private String cuenta;
	private String numero_transferencia;
	private String fecha_transferencia;
	private float valor;
	
	public C_Cobros_Pago_Lista(int identificador_pago, 
			String banco_id, 
			String banco,
			String tipo_pago_id,
			String nombre_pago,
			String cuenta,
			String numero_transferencia,
			String fecha_transferencia,
			float valor){
		this.identificador_pago=identificador_pago;
		this.banco_id=banco_id;
		this.banco=banco;
		this.tipo_pago_id=tipo_pago_id;
		this.nombre_pago=nombre_pago;
		this.cuenta=cuenta;
		this.numero_transferencia=numero_transferencia;
		this.fecha_transferencia=fecha_transferencia;
		this.valor=valor;
	}
	
	//Setters
	public void setIdentificadorPago(int identificador_pago){
		this.identificador_pago=identificador_pago;
		return;
	}
	
	public void setBancoId(String banco_id){
		this.banco_id=banco_id;
		return;
	}

	public void setBanco(String banco){
		this.banco=banco;
		return;
	}

	public void setTipoPagoId(String tipo_pago_id){
		this.tipo_pago_id=tipo_pago_id;
		return;
	}

	public void setNombrePago(String nombre_pago){
		this.nombre_pago=nombre_pago;
		return;
	}

	public void setCuenta(String cuenta){
		this.cuenta=cuenta;
		return;
	}

	public void setNumeroTransferencia(String numero_transferencia){
		this.numero_transferencia=numero_transferencia;
		return;
	}
	
	public void setFechaTransferencia(String fecha_transferencia){
		this.fecha_transferencia=fecha_transferencia;
		return;
	}
	
	public void setValor(float valor){
		this.valor=valor;
		return;
	}

	//Getters
	public int getIdentificadorPago(){
		return this.identificador_pago;
	}

	public String getBancoId(){
		return this.banco_id;
	}

	public String getBanco(){
		return this.banco;
	}

	public String getTipoPagoId(){
		return this.tipo_pago_id;
	}

	public String getNombrePago(){
		return this.nombre_pago;
	}

	public String getCuenta(){
		return this.cuenta;
	}

	public String getNumeroTransferencia(){
		return this.numero_transferencia;
	}

	public String getFechaTransferencia(){
		return this.fecha_transferencia;
	}

	public float getValor(){
		return this.valor;
	}
}
