package indicadores;

import java.util.ArrayList;
import java.util.List;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class Indicadores_Pedidos_Resumen extends Activity {
	static private ListView listPedidos;
	static private pedidoIndArrayAdapter adapter;
	static private ArrayList<pedidoInd> pedidosInd;
	static private ItaloDBAdapter DBAdapter;
	static private Cursor cursor;
	static private TextView total_input;
	static private TextView subtotal_input;
	static private TextView total_iva_input;
	static private TextView total_pedidos_input;
	static private TextView total_pedidos_anulados_input;
	static private TextView total_con_pedidos_anulados_input;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_indicadores_pedidos_resumen);
		init();
		loadList();
	}

	private void init(){
//		context=this;
		DBAdapter=new ItaloDBAdapter(this);
		total_input=(TextView)findViewById(R.id.total_input);
		subtotal_input=(TextView)findViewById(R.id.subtotal_input);
		total_pedidos_input=(TextView)findViewById(R.id.total_pedidos_input);
		total_iva_input=(TextView)findViewById(R.id.total_iva_input);
		total_pedidos_anulados_input=(TextView)findViewById(R.id.total_pedidos_anulados_input);
		total_con_pedidos_anulados_input=(TextView)findViewById(R.id.total_con_pedidos_anulados_input);
		listPedidos=(ListView)findViewById(R.id.listPedidos);
		pedidosInd=new ArrayList<pedidoInd>();

		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.indicadores__resumen__pedidos, menu);
		return true;
	}

	public class pedidoIndArrayAdapter extends ArrayAdapter<pedidoInd> {
	    private List<pedidoInd> pedidos = new ArrayList<pedidoInd>();
	    private TextView row_codigo_input;
	    private TextView row_cliente_input;
	    private TextView row_documento_input;
	    private TextView row_total_input;
	    private TextView row_fecha_input;
	    private TextView row_hora_input;

	    public pedidoIndArrayAdapter(Context context, int textViewResourceId,List<pedidoInd> objects) {
	        super(context, textViewResourceId, objects);
	        this.pedidos = objects;
			notifyDataSetChanged();
	    }

		public int getCount() {
	        return this.pedidos.size();
	    }

	    public pedidoInd getItem(int index) {
	        return this.pedidos.get(index);
	    }

		public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        final pedidoInd p;
	        if (row == null) {
	            // ROW INFLATION
	        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            row = inflater.inflate(R.layout.item_indicadores_pedido, parent, false);
	        }

	        p = getItem(position);
		    row_codigo_input = (TextView) row.findViewById(R.id.row_codigo_input);
	        row_cliente_input = (TextView) row.findViewById(R.id.row_cliente_input);
	        row_documento_input = (TextView) row.findViewById(R.id.row_documento_input);
	        row_total_input = (TextView) row.findViewById(R.id.row_total_input);
	        row_fecha_input = (TextView) row.findViewById(R.id.row_fecha_input);
	        row_hora_input = (TextView) row.findViewById(R.id.row_hora_input);

		    row_codigo_input.setText(p.getCodigo());
		    row_cliente_input.setText(p.getCliente());
		    row_documento_input.setText(p.getDocumento());
		    row_total_input.setText(Utility.formatNumber(p.getTotal()));
		    row_fecha_input.setText(p.getFecha());
		    row_hora_input.setText(p.getHora());

	        if(position % 2 == 0){
	        	row_codigo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_cliente_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_documento_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_total_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_fecha_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_hora_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        }else{
				row_codigo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_cliente_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_documento_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_total_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_fecha_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_hora_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
			}
	        return row;
	    }
	}
	
	public class pedidoInd {
		private String codigo = "";
		private String cliente = "";
		private String documento = "";
		private double total = 0;
		private String fecha = "";
		private String hora = "";
		private double iva=0;

		public pedidoInd(String codigo, String cliente, String documento, double total, String fecha, String hora){
			this.codigo=codigo;
			this.cliente=cliente;
			this.documento=documento;
			this.total=total;
			this.fecha=fecha;
			this.hora=hora;
		}
		
		/*
		 * 
		 * Getters
		 * 
		 */
		public String getCodigo() {
			return codigo;
		}
		
		public String getCliente() {
			return cliente;
		}
		
		public String getDocumento() {
			return documento;
		}
		
		public double getTotal() {
			return total;
		}

		public String getFecha() {
			return fecha;
		}

		public String getHora() {
			return hora;
		}

		public double getIVA() {
			return iva;
		}
	}
	
	void loadList(){
		cursor=DBAdapter.getIndicadoresPedido();
		loadDataList();
		adapter=new pedidoIndArrayAdapter(getApplicationContext(), R.layout.item_cartera, pedidosInd);
		listPedidos.setAdapter(adapter);
		registerForContextMenu(listPedidos);
		return;
	}

	private void loadDataList(){
		double subtotal=0;
		double iva=0;
		double total=0;
		double total_anulados=0;
		
		if(cursor.moveToFirst()){
			do{
				pedidosInd.add(new pedidoInd(
						cursor.getString(0),
						cursor.getString(1),
						cursor.getString(2),
						cursor.getDouble(3),
						cursor.getString(4),
						cursor.getString(5)));
				if(cursor.getInt(7) != 71){
					subtotal+=cursor.getDouble(8);
					iva+=cursor.getDouble(6);
					total+=cursor.getDouble(3);
				}else{
					total_anulados+=cursor.getDouble(3);
				}
			}while(cursor.moveToNext());
		}
		total_input.setText(Integer.toString(pedidosInd.size()));
		subtotal_input.setText(Utility.formatNumber(subtotal));
		total_pedidos_input.setText(Utility.formatNumber(total));
		total_iva_input.setText(Utility.formatNumber(iva));
		total_pedidos_anulados_input.setText(Utility.formatNumber(total_anulados));
		total_con_pedidos_anulados_input.setText(Utility.formatNumber(total+total_anulados));
		return;
	}

	@Override
	public void onBackPressed() {}
}
