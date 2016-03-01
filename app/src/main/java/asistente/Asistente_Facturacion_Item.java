package asistente;

public class Asistente_Facturacion_Item {
	private String fecha;
	private double valor;

	public Asistente_Facturacion_Item(String fecha, double valor) {
		this.fecha =fecha;
		this.valor=valor;
	}

	public String getFecha() {
		return fecha;
	}

	public double getValor() {
		return valor;
	}
}
