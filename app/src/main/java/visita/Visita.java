package visita;

import java.util.ArrayList;
import java.util.List;

import pedidos.Pedidos;
import registro_eventos.Registro_De_Eventos;
import utilidades.EasyUtilidades;
import utilidades.Utility;
import bd_utilidades.ItaloDBAdapter;
import cartera.Cartera;
import cliente.Cliente_Consultas_Datos_Del_Cliente_Tabs;
import cliente.Cliente_Consultas_Otros_Eventos;
import cliente.Cliente_Consultas_Pedidos;
import cobros.Cobros;
import cobros.Cobros_Del_Dia_Consultas;

import com.italo_view.R;

import easysync.EasySync;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class Visita extends Activity {
	private Context context;
	private Bundle extras;
	private ItaloDBAdapter DBAdapter;
	private TextView nombre_del_cliente_input;
	private TextView direcion_input;
	private TextView telefono_input;
	private TextView ciudad_input;
	private TextView barrio_input;
	private TextView nit_input;
	private TextView tipo_cliente_input;
	private TextView tipo_estado_input;
	private TextView cupo_disponible_input;
	private TextView cupo_asignado_input;
	private TextView forma_de_pago_input;
	private TextView estado_cartera_input;
	private TextView cupo_asignado_input_0;
	private ListView listSucursales;
	private Intent i;
	static private Cursor cursor;
	static private String bloqueo_pedido;
	static private String cliente_id;
	static private String cliente_nombre;
	static private String visita_id;
	static private ArrayList<sucursal> sucursales;
	static private sucursalArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visita);
		init();
		loadValues();
		loadList();
	}

	private void init(){
		context=this;
    	extras = getIntent().getExtras();
		cliente_id= extras.getString("cliente_id");
		cliente_nombre= extras.getString("cliente_nombre");
		nombre_del_cliente_input=(TextView) findViewById(R.id.nombre_del_cliente_input);
		direcion_input=(TextView) findViewById(R.id.direcion_input);
		telefono_input=(TextView) findViewById(R.id.telefono_input);
		ciudad_input=(TextView) findViewById(R.id.ciudad_input);
		barrio_input=(TextView) findViewById(R.id.barrio_input);
		nit_input=(TextView) findViewById(R.id.nit_input);
		tipo_cliente_input=(TextView) findViewById(R.id.tipo_cliente_input);
		tipo_estado_input=(TextView) findViewById(R.id.tipo_estado_input);
		cupo_disponible_input=(TextView) findViewById(R.id.cupo_disponible_input);
		cupo_asignado_input=(TextView) findViewById(R.id.cupo_asignado_input);
		forma_de_pago_input=(TextView) findViewById(R.id.forma_de_pago_input);
		estado_cartera_input=(TextView) findViewById(R.id.estado_cartera_input);
		cupo_asignado_input_0=(TextView) findViewById(R.id.cupo_asignado_input_0);
		listSucursales = (ListView)findViewById(R.id.listSucursales);
		extras = getIntent().getExtras();
    	cliente_id=extras.getString("cliente_id");
    	cliente_nombre=extras.getString("cliente_nombre");
    	visita_id=extras.getString("visita_id");
    	DBAdapter=new ItaloDBAdapter(this);
		sucursales=new ArrayList<sucursal>();
    	return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.visita_tabs, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.menu_general:
				i = new Intent(getApplicationContext(), Visita_Menu.class);
				i.putExtra("de", "visita");
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				startActivity(i);
				return true;
			case R.id.consultas_pedidos:
				i = new Intent(getApplicationContext(), Cliente_Consultas_Pedidos.class);
				i.putExtra("de", "visita");
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				startActivity(i);
				return true;
			case R.id.consultas_cobros:
				i = new Intent(getApplicationContext(), Cobros_Del_Dia_Consultas.class);
				i.putExtra("de", "visita");
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				startActivity(i);
				return true;
			case R.id.consultas_datos_del_clientes:
				i = new Intent(getApplicationContext(), Cliente_Consultas_Datos_Del_Cliente_Tabs.class);
				i.putExtra("de", "visita");
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				startActivity(i);
				return true;
			case R.id.consultas_cartera:
				i = new Intent(getApplicationContext(), Cartera.class);
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				startActivity(i);
				return true;
			case R.id.consultas_historicos_visitas:
				i = new Intent(getApplicationContext(), Cliente_Consultas_Otros_Eventos.class);
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				startActivity(i);
				return true;
			case R.id.gestiones_pedidos:
				if(bloqueo_pedido!=null && bloqueo_pedido.equalsIgnoreCase("N")){
					i = new Intent(getApplicationContext(), Pedidos.class);
					i.putExtra("de", "visita");
					i.putExtra("visita_id", visita_id);
					i.putExtra("cliente_id", cliente_id);
					i.putExtra("cliente_nombre", cliente_nombre);
					startActivity(i);
				}else{
					Utility.showMessage(context, getString(R.string.el_cliente_se_encuentra_bloqueado_para_la_toma_de_pedidos));
				}
				return true;
			case R.id.gestiones_cobros:
				i = new Intent(getApplicationContext(), Cobros.class);
				i.putExtra("de", "visita");
				i.putExtra("visita_id", visita_id);
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				startActivity(i);
				return true;
			case R.id.gestiones_registro_de_eventos:
				i = new Intent(getApplicationContext(), Registro_De_Eventos.class);
				i.putExtra("de", "visita");
				i.putExtra("visita_id", visita_id);
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				startActivity(i);
				return true;
			case R.id.finalizar_visita:
				if(DBAdapter.existEventoVisita(visita_id)){
		//			DBAdapter.beginTransaction();
					if(DBAdapter.closeVisita(cliente_id, visita_id)){
						DBAdapter.cleanPedidosTemporales();
						EasySync _easySync = new EasySync(this, EasyUtilidades.InformacionGloabaG(new ItaloDBAdapter(context)));
						_easySync.GenerarNovedadesUp();
						// llamado de EasySync
						EasyUtilidades.SolicitarEasySync(this, EasyUtilidades.InformacionGloabaG(new ItaloDBAdapter(context)));
			//			DBAdapter.setTransactionSuccessful();
						finish();
					}else{
						Utility.showMessage(context, getString(R.string.no_se_puede_finalizar_visita));
					}
	//				DBAdapter.endTransaction();
				}else{
					Utility.showMessage(context, getString(R.string.para_finalizar_visita));
				}
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}

	
	private void loadValues(){
		double saldo_act;
		double cupo_act;
		cursor=DBAdapter.getVisitaClienteDatos(cliente_id);
		if(cursor.moveToFirst()){
			bloqueo_pedido=cursor.getString(13);
			String tel=(cursor.getString(3)!=null)?cursor.getString(3):""; 
			String cel=(cursor.getString(4)!=null)?cursor.getString(4):"";
			String estado=(bloqueo_pedido!=null && bloqueo_pedido.equalsIgnoreCase("s"))?getString(R.string.bloqueado):getString(R.string.activo);
			nombre_del_cliente_input.setText(cursor.getString(0)+" - "+cursor.getString(1));
			
			direcion_input.setText(cursor.getString(2));
			telefono_input.setText(tel+" "+cel);
			ciudad_input.setText(cursor.getString(5));
			barrio_input.setText(cursor.getString(6));
			nit_input.setText(cursor.getString(7));
			tipo_cliente_input.setText(cursor.getString(8));
			forma_de_pago_input.setText(cursor.getString(11));
			estado_cartera_input.setText(cursor.getString(12));
//			setCupoDisponible();
			tipo_estado_input.setText(estado);
			
			saldo_act=DBAdapter.getSaldoAct(cliente_id);
			cupo_act=DBAdapter.getCupoAct(cliente_id,saldo_act);
//			cupo_asignado_input.setText(cursor.getString(10));
//			cupo_disponible_input.setText(cursor.getString(10)+" "+cursor.getString(11));
			if(cursor.getString(10).equalsIgnoreCase("OO")){
				cupo_disponible_input.setText("");
			}else{
				cupo_disponible_input.setText(Utility.formatNumber(cupo_act));
			}
			cupo_asignado_input_0.setText(cursor.getString(10));
			cupo_asignado_input.setText(/*cursor.getString(10)+" "+*/Utility.formatNumber(cursor.getDouble(14)));
//			cupo_disponible_input.setText(Utility.formatNumber(cupo_act));
		}else{
			finish();
		}
	}
	
	public class sucursalArrayAdapter extends ArrayAdapter<sucursal> {
	    private List<sucursal> sucursales = new ArrayList<sucursal>();
	    private TextView row_cuenta_input;
	    private TextView row_sucursal_input;
	    private TextView row_sector_input;
	    private int layoutID;

	    public sucursalArrayAdapter(Context context, int textViewResourceId,List<sucursal> objects) {
	        super(context, textViewResourceId, objects);
	        this.sucursales = objects;
	        this.layoutID=textViewResourceId;
			notifyDataSetChanged();
	    }

		public int getCount() {
	        return this.sucursales.size();
	    }

	    public sucursal getItem(int index) {
	        return this.sucursales.get(index);
	    }

		@SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        if (row == null) {
	            // ROW INFLATION
	        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            row = inflater.inflate(layoutID, parent, false);
	        }else{}

	        sucursal s = getItem(position);
	        row_cuenta_input = (TextView) row.findViewById(R.id.row_cuenta_input);
	        row_sucursal_input = (TextView) row.findViewById(R.id.row_sucursal_input);
	        row_sector_input = (TextView) row.findViewById(R.id.row_sector_input);

	        row_cuenta_input.setText(s.getCuenta());
	        row_sucursal_input.setText(s.getSucursal());
		    row_sector_input.setText(s.getSector());

	        if(position % 2 == 0){
	        	row_cuenta_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_sucursal_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_sector_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        }else{
	        	row_cuenta_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_sucursal_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_sector_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
			}
	        return row;
	    }
	}
	
	public class sucursal {
		private String cuenta = "";
		private String sucursal = "";
		private String sector = "";

		public sucursal(String cuenta, String sucursal, String sector){
			this.cuenta=cuenta;
			this.sucursal=sucursal;
			this.sector=sector;
		}
		
		/*
		 * 
		 * Getters
		 * 
		 */
		public String getCuenta() {
			return cuenta;
		}
		
		public String getSucursal() {
			return sucursal;
		}
		
		public String getSector() {
			return sector;
		}
	}
	
	void loadList(){
		cursor=DBAdapter.getClienteSucursales(cliente_id);
		loadDataList();
		adapter=new sucursalArrayAdapter(getApplicationContext(), R.layout.item_sucursales, sucursales);
		listSucursales.setAdapter(adapter);
		return;
	}
	
	private void loadDataList(){
		if(cursor.moveToFirst()){
			do{
				sucursales.add(new sucursal(cursor.getString(0),
						cursor.getString(1),
						cursor.getString(2)));
			}while(cursor.moveToNext());
		}
		return;
	}
	
	@Override
	protected void onResume(){
	   super.onResume();
	}
	
	@Override
	protected void onPause(){
	   super.onResume();
	}
	
	@Override
	public void onBackPressed() {}
}