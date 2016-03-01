package cobros;

public class Cobros_Dia_Docs_Pago_Item {
	private String forma_pago;
	private double valor;
	
	public Cobros_Dia_Docs_Pago_Item(
		String forma_pago,
		double valor){
		this.forma_pago=forma_pago;
		this.valor=valor;
	}
	
	//getter
	public String getFormaPago() {
		return this.forma_pago;
	}

	public double getValor() {
		return this.valor;
	}	
}