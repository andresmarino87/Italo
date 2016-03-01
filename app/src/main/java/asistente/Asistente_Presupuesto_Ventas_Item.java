package asistente;

public class Asistente_Presupuesto_Ventas_Item {
	private String nombre;
	private double presupuesto;
	private double venta;
	private double porcentaje;

	public Asistente_Presupuesto_Ventas_Item(String nombre, double presupuesto, double venta, double porcentaje) {
		this.nombre =nombre;
		this.presupuesto =presupuesto;
		this.venta =venta;
		this.porcentaje=porcentaje;
	}

	public String getNombre() {
		return nombre;
	}

	public double getPresupuesto() {
		return presupuesto;
	}

	public double getVenta() {
		return venta;
	}

	public double getPorcentaje() {
		return porcentaje;
	}
}
