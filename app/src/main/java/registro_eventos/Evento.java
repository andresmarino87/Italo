package registro_eventos;

public class Evento {
	private String id="";
	private String evento = "";
	private String fecha = "";
	private String hora = "";
	private String tipo_evento_id="";
	private String implicaVisita="";
	private String implicaFactura="";
	private String observaciones="";
	
	public Evento(String id, String evento, String fecha, String hora, String tipo_evento_id, String implicaVisita,String implicaFactura, String observaciones){
		this.id=id;
		this.evento=evento;
		this.fecha=fecha;
		this.hora=hora;
		this.tipo_evento_id=tipo_evento_id;
		this.implicaVisita=implicaVisita;
		this.implicaFactura=implicaFactura;
		this.observaciones=observaciones;
	}

	/*
	 * 
	 * Getters
	 * 
	 */

	public String getId() {
		return id;
	}

	public String getEvento() {
		return evento;
	}
		
	public String getFecha() {
		return fecha;
	}
		
	public String getHora() {
		return hora;
	}
	
	public String getTipoEventoId() {
		return tipo_evento_id;
	}

	public String getImplicaVisita() {
		return implicaVisita;
	}

	public String getImplicaFact() {
		return implicaFactura;
	}

	public String getObs() {
		return observaciones;
	}
}
