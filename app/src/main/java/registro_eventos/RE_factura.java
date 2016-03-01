package registro_eventos;

public class RE_factura {
	private String id = "";
	private String tipo_doc = "";
	private String dias = "";
	private float valor = 0;
	private String vencimiento = "";
	private String fecha_factura = "";
	private String tipo_doc_id = "";

	public RE_factura(String id,
		String tipo_doc,
		String dias,
		float valor,
		String vencimiento,
		String fecha_factura,
		String tipo_doc_id) {
		this.id = id;
		this.tipo_doc = tipo_doc;
		this.dias = dias;
		this.valor = valor;
		this.vencimiento = vencimiento;
		this.fecha_factura = fecha_factura;
		this.tipo_doc_id=tipo_doc_id;
	}

	/*
	 * 
	 * Getters
	 */

	public String getId() {
		return id;
	}

	public String getTipoDoc() {
		return tipo_doc;
	}
		
	public String getDias() {
		return dias;
	}

	public float getValor() {
		return valor;
	}

	public String getVencimiento() {
		return vencimiento;
	}

	public String getFechaFact() {
		return fecha_factura;
	}
		
	public String getTipoDocId() {
		return tipo_doc_id;
	}
}
