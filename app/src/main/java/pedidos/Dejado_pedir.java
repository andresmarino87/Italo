package pedidos;

public class Dejado_pedir {
	private String id = "";
	private String nombre = "";
	private int cantidad = 0;
	private double base;
	private double iva;
	private double total;
	
	public Dejado_pedir( String id, String nombre, int cantidad,double base, double iva, double total){
		this.id=id;
		this.nombre=nombre;
		this.cantidad=cantidad;
		this.base=base;
		this.iva=iva;
		this.total=total;
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

	public double getBase() {
		return base;
	}

	public double getIVA() {
		return iva;
	}

	public double getTotal() {
		return total;
	}

		
	public void setCantidad(int cant) {
		this.cantidad=cant;
	}

	public void setTotal(double total) {
		this.total=total;
	}
}
