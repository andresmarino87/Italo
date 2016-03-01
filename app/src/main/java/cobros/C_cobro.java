package cobros;

public class C_cobro {
	private String documento;
	private String tipo_doc;
	private int dias;
	private float valor;
	private float saldo;
	private String fecha_venc;
	private String fecha_fac;

		public C_cobro(String documento, String tipo_doc, int dias, float valor, float saldo, String fecha_venc, String fecha_fac){
			this.documento= documento;
			this.tipo_doc= tipo_doc;
			this.dias= dias;
			this.valor= valor;
			this.saldo = saldo;
			this.fecha_venc= fecha_venc;
			this.fecha_fac= fecha_fac;
		}
		
	/*
	 *Getters	
     */

		public String getDoc() {
			return documento;
		}

		public String getTipoDoc() {
			return tipo_doc;
		}
		public int getDias() {
			return dias;
		}
		public float getValor() {
			return valor;
		}
		public float getSaldo() {
			return saldo;
		}
		public String getFechaVenc() {
			return fecha_venc;
		}
		public String getFechaFac() {
			return fecha_fac;
		}
	}
