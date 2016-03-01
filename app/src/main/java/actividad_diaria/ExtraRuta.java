package actividad_diaria;

public class ExtraRuta {
	private String distrito_id;
	private String subdistrito_id;
	private String sector_id;
	private String ruta_id;
	private String cliente_id;
	private String cliente_nombre;
	private String tejido;
	private double venta_proy;
	private double cobro_proy;
	private String cartera_venc;
	private String observaciones;

	public ExtraRuta(String distrito_id,
			String subdistrito_id,
			String sector_id, 
			String ruta_id,
			String cliente_id,
			String cliente_nombre,
			String tejido, 
			double venta_proy,
			double cobro_proy,
			String cartera_venc,
			String observaciones){
		this.distrito_id=distrito_id;
		this.subdistrito_id=subdistrito_id;
		this.sector_id=sector_id;
		this.ruta_id=ruta_id;
		this.cliente_id=cliente_id;
		this.cliente_nombre=cliente_nombre;
		this.tejido=tejido;
		this.venta_proy=venta_proy;
		this.cobro_proy=cobro_proy;
		this.cartera_venc=cartera_venc;
		this.observaciones=observaciones;
	}

	/*
	 * 
	 * Getters
	 * 
	 */
	
	public String getDistritoId() {
		return distrito_id;
	}
	public String getSubdistritoId() {
		return subdistrito_id;
	}
	public String getSectorId() {
		return sector_id;
	}
	public String getRutaID() {
		return ruta_id;
	}
	public String getClienteId() {
		return cliente_id;
	}
	public String getClienteNombre() {
		return cliente_nombre;
	}
	public String getTejido() {
		return tejido;
	}
	public double getVentaProy() {
		return venta_proy;
	}
	
	public double getCobroProy() {
		return cobro_proy;
	}

	public String getCarteraVenc() {
		return cartera_venc;
	}
	
	public String getObservacion() {
		return observaciones;
	}
	/* 
	 * 
	 * Seter
	 * 
	 * */
	
	public void setObservacion(String observacion){
		this.observaciones=observacion;
	}
	
}