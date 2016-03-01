package cliente;

import java.util.ArrayList;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import cobros.CobroDia;
import cobros.CobrosDiasArrayAdapter;

import com.italo_view.R;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

public class Cliente_Consultas_Historicos_Recaudos extends Activity {
	private Bundle extras;
	private ListView listCobrosDelDia;
	private ItaloDBAdapter DBAdapter;
	static private Cursor cursor;
	static private ArrayList<CobroDia> cobros;
	private CobrosDiasArrayAdapter adapter;
	static private String cliente_id;
	static private String cliente_nombre;
	private TextView total_efectivo_input;
	private TextView total_cheque_input;
	private TextView total_transferencia_input;
	private TextView total_consignacion_input;
	private TextView total_cheque_postfechado_input;
	private TextView total_input;
	private TextView cliente_name_input;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cliente_consultas_historicos_recaudos);
		init();
		loadResumen();
		loadList();
	}
	
	private void init(){
		listCobrosDelDia = (ListView)findViewById(R.id.listCobrosDelDia);
		DBAdapter = new ItaloDBAdapter(this);
		cobros = new ArrayList<CobroDia>();
	    extras = getIntent().getExtras();
	    cliente_id = extras.getString("cliente_id");
		cliente_id=extras.getString("cliente_id");
		cliente_nombre= extras.getString("cliente_nombre");
		cliente_name_input=(TextView)findViewById(R.id.cliente_name_input);
		cliente_name_input.setText(cliente_id+" "+cliente_nombre);
		total_efectivo_input= (TextView)findViewById(R.id.total_efectivo_input);
		total_cheque_input= (TextView)findViewById(R.id.total_cheque_input);
		total_transferencia_input= (TextView)findViewById(R.id.total_transferencia_input);
		total_consignacion_input= (TextView)findViewById(R.id.total_consignacion_input);
		total_cheque_postfechado_input= (TextView)findViewById(R.id.total_cheque_postfechado_input);
		total_input= (TextView)findViewById(R.id.total_input);
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.cobros_del_dia_consultas, menu);
		return true;
	}

	private void loadResumen(){
		double efectivo=0;
		double cheque=0;
		double transferecia=0;
		double consignacion=0;
		double postfechado=0;
		cursor=DBAdapter.getCobrosDiaResumenMediosPago(cliente_id);

		if(cursor.moveToFirst()){
			do{
				efectivo+=cursor.getDouble(0);
				cheque+=cursor.getDouble(1);
				transferecia+=cursor.getDouble(2);
				consignacion+=cursor.getDouble(3);
				postfechado+=cursor.getDouble(4);
			}while(cursor.moveToNext());
		}
		total_efectivo_input.setText(Utility.formatNumber(efectivo));
		total_cheque_input.setText(Utility.formatNumber(cheque));
		total_transferencia_input.setText(Utility.formatNumber(transferecia));
		total_consignacion_input.setText(Utility.formatNumber(consignacion));
		total_cheque_postfechado_input.setText(Utility.formatNumber(postfechado));
		total_input.setText(Utility.formatNumber(efectivo+cheque+transferecia+consignacion+postfechado));
		return;
	}
	
	void loadList(){
		cursor=DBAdapter.getCobrosDelDia(cliente_id);
		loadDataList();
		adapter=new CobrosDiasArrayAdapter(getApplicationContext(), R.layout.item_cobros_del_dia, cobros);
		listCobrosDelDia.setAdapter(adapter);
		registerForContextMenu(listCobrosDelDia);
		return;
	}

	
	private void loadDataList(){
		if(cursor.moveToFirst()){
			do{
				if(cursor.getString(7)!=null && cursor.getString(7).equalsIgnoreCase("")){
					cobros.add(new CobroDia(cursor.getString(0),
						cursor.getString(1),
						cursor.getDouble(2),
						cursor.getDouble(3)+cursor.getDouble(4),
						cursor.getDouble(5),
						cursor.getString(6),
						cursor.getString(7)));
				}
			}while(cursor.moveToNext());
		}
		return;
	}
	
	@Override
	public void onBackPressed() {}
}