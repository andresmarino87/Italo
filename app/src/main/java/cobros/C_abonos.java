package cobros;

public class C_abonos {
	private String motivo_abono_id;
	private String numero_guia_devolucion;
	private String fecha_guia_devolucion;
	private String numero_devolucion;
	private String fecha_devolucion;
	private String numero_solicitud_nc;
	private String fecha_solicitud_nc;
	private String fecha_cancela_saldo;
	private String observaciones_abono;
	private String numero_memorando;
	private String fecha_memorando;
	private String fecha_seguimiento;

	public C_abonos(String motivo_abono_id, 
			String numero_guia_devolucion,
			String fecha_guia_devolucion,
			String numero_devolucion,
			String fecha_devolucion,
			String numero_solicitud_nc,
			String fecha_solicitud_nc,
			String fecha_cancela_saldo,
			String observaciones_abono,
			String numero_memorando,
			String fecha_memorando,
			String fecha_seguimiento){
		this.motivo_abono_id=motivo_abono_id;
		this.numero_guia_devolucion=numero_guia_devolucion;
		this.fecha_guia_devolucion=fecha_guia_devolucion;
		this.numero_devolucion=numero_devolucion;
		this.fecha_devolucion=fecha_devolucion;
		this.numero_solicitud_nc=numero_solicitud_nc;
		this.fecha_solicitud_nc=fecha_solicitud_nc;
		this.fecha_cancela_saldo=fecha_cancela_saldo;
		this.observaciones_abono=observaciones_abono;
		this.numero_memorando=numero_memorando;
		this.fecha_memorando=fecha_memorando;
		this.fecha_seguimiento=fecha_seguimiento;
	}
	
	//setters
	public void setMotivoAbonoId(String motivo_abono_id) {
		this.motivo_abono_id=motivo_abono_id;
	}	
	
	public void setNumeroGuiaDevolucion(String numero_guia_devolucion) {
		this.numero_guia_devolucion=numero_guia_devolucion;
	}	

	public void setFechaGuiaDevolucion(String fecha_guia_devolucion) {
		this.fecha_guia_devolucion=fecha_guia_devolucion;
	}	

	public void setNueroDevolucion(String numero_devolucion) {
		this.numero_devolucion=numero_devolucion;
	}	

	public void setFechaDevolucion(String fecha_devolucion) {
		this.fecha_devolucion=fecha_devolucion;
	}	

	public void setNumeroSolicitudNc(String numero_solicitud_nc) {
		this.numero_solicitud_nc=numero_solicitud_nc;
	}	
	
	public void setFechaSolicitud(String fecha_solicitud_nc){
		this.fecha_solicitud_nc=fecha_solicitud_nc;
	}

	public void setFechaCancelaSaldo(String fecha_cancela_saldo){
		this.fecha_cancela_saldo=fecha_cancela_saldo;
	}
	
	public void setObsAbono(String observaciones_abono){
		this.observaciones_abono=observaciones_abono;
	}
	
	public void setNumeroMemorando(String numero_memorando){
		this.numero_memorando=numero_memorando;
	}
	
	public void setFechaMemorando(String fecha_memorando){
		this.fecha_memorando=fecha_memorando;
	}
	
	public void setFechaSeguimiento(String fecha_seguimiento){
		this.fecha_seguimiento=fecha_seguimiento;
	}
	
	//getter
	public String getMotivoAbonoId() {
		return this.motivo_abono_id;
	}
	
	public String getNumeroGuiaDevolucion() {
		return this.numero_guia_devolucion;
	}
	
	public String getFechaGuiaDevolucion() {
		return this.fecha_guia_devolucion;
	}
	
	public String getNumeroDevolucion() {
		return this.numero_devolucion;
	}
	
	public String getFechaDevolucion() {
		return this.fecha_devolucion;
	}
	
	public String getNumeroSolicitudNc() {
		return this.numero_solicitud_nc;
	}

	public String getFechaSolicitudNc() {
		return this.fecha_solicitud_nc;
	}
	
	public String getFechaCancelaSado() {
		return this.fecha_cancela_saldo;
	}
	
	public String getObsAbono(){
		return this.observaciones_abono;
	}

	public String getNueroMemorando(){
		return this.numero_memorando;
	}

	public String getFechaMemorando(){
		return this.fecha_memorando;
	}
	
	public String getFechaSeguimiento(){
		return this.fecha_seguimiento;
	}
}