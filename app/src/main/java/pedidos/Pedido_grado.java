package pedidos;

public class Pedido_grado {
	private String id = "";
	private String nombre = "";
	private int cantidad = 0;
	private String ProductoPedidoId = "";
	private double precio_unitario;
	private double iva;

	public Pedido_grado( String id, String nombre, int cantidad,String ProductoPedidoId, double iva,double precio_unitario){
		this.id=id;
		this.nombre=nombre;
		this.cantidad=cantidad;
		this.ProductoPedidoId=ProductoPedidoId;
		this.precio_unitario=precio_unitario;
		this.iva=iva;
	}
	
	/*
	 * 
	 * Getters
	 * 
	 */
	public String getId() {
		return id;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public int getCantidad() {
		return cantidad;
	}

	public String getProductoPedidoId() {
		return ProductoPedidoId;
	}
	
	public double getPrecioUnitario() {
		return precio_unitario;
	}

	public double getIva() {
		return iva;
	}

	public void setCantidad(int cant) {
		this.cantidad=cant;
	}
}