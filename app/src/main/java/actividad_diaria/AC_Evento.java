package actividad_diaria;

public class AC_Evento {
	private String evento;
	private String hora_ini;
	private String hora_fin;
	private String duracion_min;
	private String observaciones;
	private String bandera;

	public AC_Evento(String evento, String hora_ini,String hora_fin, String duracion_min, String observaciones, String bandera) {
		this.evento =evento;
		this.hora_ini = hora_ini;
		this.hora_fin = hora_fin;
		this.duracion_min = duracion_min;
		this.observaciones = observaciones;
		this.bandera=bandera;
	}

	public String getEvento() {
		return evento;
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
