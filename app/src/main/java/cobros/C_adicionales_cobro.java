package cobros;

public class C_adicionales_cobro {
	String codigo_mov_tipo_doc;
	String numero_relacion;

	public C_adicionales_cobro(){
		this.codigo_mov_tipo_doc="";
		this.numero_relacion="";
	}
	
	public void setCodigoMovTipoDoc(String codigo_mov_tipo_doc){
		this.codigo_mov_tipo_doc=codigo_mov_tipo_doc;
	}
	
	public void setNumeroRelacion(String numero_relacion){
		this.numero_relacion=numero_relacion;
	}
	
	public String getCodigoMovTipoDoc() {
		return this.codigo_mov_tipo_doc;
	}
	
	public String getNumeroRelacion() {
		return this.numero_relacion;
	}
}
