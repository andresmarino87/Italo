package cobros_programados;

public class Cobros_Programados_Item {
	private String lNombreCliente;
	private String lCodigoCliente;
	private String lTipoDocumento;
	private String lNumeroDocumento;
	private String lValorDocumento;
	private String lFechaProgramacion;
	

	public Cobros_Programados_Item(String pNombreCliente, String pCodigoCliente, String pTipoDocumento, String pNumeroDocumento, String pValorDocumento, String pFechaProgramacion)
	{
		this.lNombreCliente = pNombreCliente;
		this.lCodigoCliente = pCodigoCliente;
		this.lTipoDocumento = pTipoDocumento;
		this.lNumeroDocumento = pNumeroDocumento;
		this.lValorDocumento = "$ " + pValorDocumento;
		this.lFechaProgramacion = pFechaProgramacion;		
	}
	
/*
 *Getters	
 */

	public String getNombreCliente() {
		return lNombreCliente;
	}
	public String getCodigoCliente() {
		return lCodigoCliente;
	}
	public String getTipoDocumento() {
		return lTipoDocumento;
	}
	public String getNumeroDocumento() {
		return lNumeroDocumento;
	}
	public String getValorDocumento() {
		return lValorDocumento;
	}
	public String getFechaProgramacion() {
		return lFechaProgramacion;
	}
}