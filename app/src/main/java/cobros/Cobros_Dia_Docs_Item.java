package cobros;

public class Cobros_Dia_Docs_Item {
	private String cobro_id;
	private String tipo_doc;
	private String doc_id;
	private double saldo;
	private double valor_pago;

	public Cobros_Dia_Docs_Item(
		String cobro_id,
		String tipo_doc, 
		String doc_id, 
		double saldo, 
		double valor_pago){
		this.cobro_id=cobro_id;
		this.tipo_doc=tipo_doc;
		this.doc_id=doc_id;
		this.saldo=saldo;
		this.valor_pago=valor_pago;
	}
	
	//getter
	public String getCobroId() {
		return this.cobro_id;
	}

	public String getTipoDoc() {
		return this.tipo_doc;
	}
	
	public String getDocId() {
		return this.doc_id;
	}
	public double getSaldo() {
		return this.saldo;
	}
	
	public double getValorPago() {
		return this.valor_pago;
	}
}
