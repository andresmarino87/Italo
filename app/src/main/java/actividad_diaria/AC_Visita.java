package actividad_diaria;

public class AC_Visita{
	private String visita_id;
	private int cant_eventos;
	private String hora_ini;
	private String hora_fin;
	private String duracion_min;
	private String observaciones;
	private String bandera;

	public AC_Visita(String visita_id, int cant_eventos, String hora_ini,
				String hora_fin, String duracion_min, String observaciones, String bandera) {
		this.visita_id =visita_id;
		this.cant_eventos = cant_eventos;
		this.hora_ini = hora_ini;
		this.hora_fin = hora_fin;
		this.duracion_min = duracion_min;
		this.observaciones = observaciones;
		this.bandera = bandera;
	}

	public String getVisitaId() {
		return visita_id;
	}

	public int getCantEventos() {
		return cant_eventos;
	}

	public String getHoraIni() {
		return hora_ini;
	}

	public String getHoraFin() {
		return hora_fin;
	}

	public String getDuracion() {
		return duracion_min;
	}

	public String getObs() {
		return observaciones;
	}

	public String getBandera() {
		return bandera;
	}
}
