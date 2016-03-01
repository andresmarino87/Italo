package pedidos;

public class Pedidos_child {
	private String id;
	private String descripcion;
	
	public Pedidos_child(String id, String descripcion){
		this.id=id;
		this.descripcion=descripcion;
	}
	
	//Setters
	public void setId(String id) {
		this.id = id;
	}
	
	public void setDescripcion(String descripcion) {
		this.id = descripcion;
	}	
	
	//Getters
	public String getId() {
		return id;
	}
	
	public String getDescripcion() {
		return descripcion;
	}	
}
