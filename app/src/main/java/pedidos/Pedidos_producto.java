package pedidos;

public class Pedidos_producto {
	private String name;
	private String imageName;
	private String id;
	private String precio;
	private String detalleId;
//	private String cantidad_sugerida;
	private String cantidad;
	private String presentacion;
	private String valorBruto;
	
	public Pedidos_producto(String name, String imageName,String id,String precio,String detalleId,String cantidad,String presentacion, String valorBruto){
		this.name= name;
		this.imageName= imageName;
		this.id=id;
		this.precio=precio;
		this.detalleId=detalleId;
//		this.cantidad_sugerida=cantidad_sugerida;
		this.cantidad=cantidad;
		this.presentacion=presentacion;
		this.valorBruto=valorBruto;
	}
		
	/*
	 *Getters	
     */

	public String getName() {
		return name;
	}

	public String getImageName() {
		return imageName;
	}
	
	public String getId(){
		return id;
	}
		
	public String getPrecio(){
		return precio;
	}
	
	public String getDetalleId(){
		return detalleId;
	}

	public String getCantidad(){
		return cantidad;
	}
		
	public String getPresentacion(){
		return presentacion;
	}

	public String getValorBruto(){
		return valorBruto;
	}
}
