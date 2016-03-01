package cliente;

public class Cliente_Pedido_Item{
	private String nPedido = "";
	private String valorFactura = "";
	private String estado = "";
	private String fecha = "";

	public Cliente_Pedido_Item(String nPedido, String valorFactura, String estado, String fecha){
		this.nPedido=nPedido;
		this.valorFactura=valorFactura;
		this.estado=estado;
		this.fecha=fecha;
	}
	
	/*
	 * 
	 * Getters
	 * 
	 */
	
	public String getNFactura() {
		return nPedido;
	}
	
	public String getFactura() {
		return valorFactura;
	}
	
	public String getEstado() {
		return estado;
	}
	
	public String getFecha() {
		return fecha;
	}
}