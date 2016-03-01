package pedidos_negados;

public class Pedidos_Negados_Item {
	private String id = "";
	private String pedido = "";
	private String codigo = "";
	private String cliente = "";
	private String valor = "";

	public Pedidos_Negados_Item(String id, String pedido, String codigo, String cliente, String valor){
		this.id=id;
		this.pedido=pedido;
		this.codigo=codigo;
		this.cliente=cliente;
		this.valor=valor;
	}
	
	/*
	 * 
	 * Getters
	 * 
	 */
	public String getId() {
		return id;
	}
	
	public String getPedido() {
		return pedido;
	}
	
	public String getCodigo() {
		return codigo;
	}
	
	public String getCliente() {
		return cliente;
	}
	
	public String getValor() {
		return valor;
	}
}
