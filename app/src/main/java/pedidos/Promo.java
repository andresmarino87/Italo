package pedidos;

public class Promo {
	private String tipoPromo;
	private String promocion_id;
	private double porcDesc;
	private String producto_id_bonificar;
	private int cantidad_bonificada;
	
	public Promo(){
		tipoPromo="";
		promocion_id="";
		porcDesc=0;
		producto_id_bonificar="";
		cantidad_bonificada=0;
	}

	//Setters
	public void setTipoPromo(String tipoPromo){
		this.tipoPromo=tipoPromo;
	}
	
	public void setPromocionId(String promocion_id){
		this.promocion_id=promocion_id;
	}
	
	public void setPorcDesc(double porcDesc){
		this.porcDesc=porcDesc;
	}
	
	public void setProductoIdBonif(String producto_id_bonificar){
		this.producto_id_bonificar=producto_id_bonificar;
	}

	public void setCantBonificada(int cantidad_bonificada){
		this.cantidad_bonificada=cantidad_bonificada;
	}
	
	//Getters
	public String getTipoPromo(){
		return this.tipoPromo;
	}
	
	public String getPromocionId(){
		return this.promocion_id;
	}

	public double getPorcDesc(){
		return this.porcDesc;
	}

	public String getProductoIdBonificar(){
		return this.producto_id_bonificar;
	}

	public int getCantidadBonificada(){
		return this.cantidad_bonificada;
	}
}