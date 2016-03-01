package pedidos;

public class Pedido_lista {
	private String producto = "";
	private String presentacion = "";
	private String valorUnitario = "";
	private String sugerido = "";
	private String cantidad = "";
	private String promoEspecie = "";
	private String porcPromo = "";
	private String valorPromo = "";
	private String iva = "";
	private String subtotal = "";

	public Pedido_lista(String producto, String presentacion, String valorUnitario, String sugerido, String cantidad, String promoEspecie, String porcPromo, String valorPromo, String iva, String subtotal){
		this.producto=producto;
		this.presentacion=presentacion;
		this.valorUnitario=valorUnitario;
		this.sugerido=sugerido;
		this.cantidad=cantidad;
		this.promoEspecie=promoEspecie;
		this.porcPromo=porcPromo;
		this.valorPromo=valorPromo;
		this.iva=iva;
		this.subtotal=subtotal;
	}
		
	/*
	 * 
	 * Getters
	 * 
	 */

	public String getProducto() {
		return producto;
	}
	
	public String getPresentacion() {
		return presentacion;
	}
	
	public String getValorUnitario() {
		return valorUnitario;
	}
	
	public String getSugerido() {
		return sugerido;
	}
		
	public String getCantidad() {
		return cantidad;
	}

	public String getPromoEspecie() {
		return promoEspecie;
	}

	public String getPorcPromo() {
		return porcPromo;
	}

	public String getValorPromo() {
		return valorPromo;
	}
		
	public String getIVA() {
		return iva;
	}

	public String getSubtotal(){
		return subtotal;
	}
}
