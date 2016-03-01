package actividad_diaria;

public class RutaRow {
	private String evento;
	private String ruta;
	private String tejido;
	private String cliente_id;
	private String cliente_nombre;
	private double venta_proyectada;
	private double venta_real;
	private double cobro_proyectado;
	private double cobro_real;
	private String cartera_vencida;
	private String observaciones;
	private String extraRuta;
	private String tipoExtraRuta;
	private String nVisitas;

	public RutaRow(String evento, 
		String ruta,
		String tejido,
		String cliente_id, 
		String cliente_nombre,
		double venta_proyectada, 
		double venta_real,
		double cobro_proyectado, 
		double cobro_real,
		String cartera_vencida, 
		String observaciones, 
		String extraRuta,
		String tipoExtraRuta, 
		String nVisitas) {
		this.evento = evento;
		this.ruta = ruta;
		this.tejido = tejido;
		this.cliente_id = cliente_id;
		this.cliente_nombre = cliente_nombre;
		this.venta_proyectada = venta_proyectada;
		this.venta_real = venta_real;
		this.cobro_proyectado = cobro_proyectado;
		this.cobro_real = cobro_real;
		this.cartera_vencida = cartera_vencida;
		this.observaciones = observaciones;
		this.extraRuta = extraRuta;
		this.tipoExtraRuta = tipoExtraRuta;
		this.nVisitas = nVisitas;
	}
	
	public String getEvento() {
		return evento;
	}

	public String getRuta() {
		return ruta;
	}

	public String getTejido() {
		return tejido;
	}

	public String getClienteId() {
		return cliente_id;
	}

	public String getClienteNombre() {
		return cliente_nombre;
	}

	public double getVenta_proyectada() {
		return venta_proyectada;
	}

	public double getVenta_real() {
		return venta_real;
	}

	public double getCobro_proyectado() {
		return cobro_proyectado;
	}

	public double getCobro_real() {
		return cobro_real;
	}

	public String getCartera_vencida() {
		return cartera_vencida;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public String getExtraRuta() {
		return extraRuta;
	}

	public String getTipoExtra() {
		return tipoExtraRuta;
	}

	public String getNVisitas() {
		return nVisitas;
	}
}
