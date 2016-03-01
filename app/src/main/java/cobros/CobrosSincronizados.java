package cobros;

public class CobrosSincronizados {
	private String sinc = "";
	private String cobro_id = "";
	private String fecha_cobro = "";
	private String hora_cobro = "";
	private float valor_cobro = 0;
	private String motivo_anulacon="";
	private String cobro_esu_id="";
	
	public CobrosSincronizados(String sinc,
		String cobro_id,
		String fecha_cobro,
		String hora_cobro,
		float valor_cobro,
		String motivo_anulacon,
		String cobro_esu_id){
		this.sinc=sinc;
		this.cobro_id=cobro_id;
		this.fecha_cobro=fecha_cobro;
		this.hora_cobro=hora_cobro;
		this.valor_cobro=valor_cobro;
		this.motivo_anulacon=motivo_anulacon;
		this.cobro_esu_id=cobro_esu_id;
	}
		
	/*
	 * 
	 * Getters
	 * 
	 */
		
	public String getSinc() {
		return sinc;
	}
		
	public String getCobroId() {
		return cobro_id;
	}

	public String getFechaCobro() {
		return fecha_cobro;
	}
	
	public String getHoraCobro() {
		return hora_cobro;
	}
		
	public float getValorCobro() {
		return valor_cobro;
	}

	public String getMotivoAnulacion() {
		return motivo_anulacon;
	}

	public String getEsuId() {
		return cobro_esu_id;
	}
}