package paretos;

public class Paretos_Item {
	private String codigo = "";
	private String cliente = "";
	private String sector = "";
	private String ruta = "";
	private String total = "";

	public Paretos_Item(String codigo, String cliente, String sector, String ruta, String total){
		this.codigo=codigo;
		this.cliente=cliente;
		this.sector=sector;
		this.ruta=ruta;
		this.total=total;
	}
	
	/*
	 * 
	 * Getters
	 * 
	 */
	public String getCodigo() {
		return codigo;
	}
	
	public String getCliente() {
		return cliente;
	}
	
	public String getSector() {
		return sector;
	}
	
	public String getRuta() {
		return ruta;
	}
	
	public String getTotal() {
		return total;
	}
}