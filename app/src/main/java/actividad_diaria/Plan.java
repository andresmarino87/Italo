package actividad_diaria;

public class Plan {
	private String creador = "";
	private String sector ="";
	private String plan = "";
	private String fecha = "";

	public Plan(String plan, String fecha, String sector_id, String creador) {
		this.sector=sector_id;
		this.plan = plan;
		this.fecha = fecha;
		this.creador=creador;
	}

	/*
	 * 
	 * Getters
	 */
	public String getCreador(){
		return creador;
	}

	public String getSectorId(){
		return sector;
	}
	
	public String getPlan() {
		return plan;
	}

	public String getFecha() {
		return fecha;
	}
}