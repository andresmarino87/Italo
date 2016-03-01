package cliente;

public class clienteRow {
	private String tejido;
	private String ruta;
	private String codigo;
	private String cliente;

	public clienteRow(String tejido, String ruta, String codigo, String cliente){
		this.tejido= tejido;
		this.ruta= ruta;
		this.codigo= codigo;
		this.cliente= cliente;
	}
		
	/*
	 *Getters	
     */

	public String getTejido() {
		return tejido;
	}

	public String getRuta() {
		return ruta;
	}
	public String getCodigo() {
		return codigo;
	}
	public String getCliente() {
		return cliente;
	}
}
