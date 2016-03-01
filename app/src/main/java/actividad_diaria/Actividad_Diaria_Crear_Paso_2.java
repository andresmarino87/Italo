package actividad_diaria;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class Actividad_Diaria_Crear_Paso_2 extends Activity {
	static private final int REQUEST_EXIT = 0;
	private Intent i;
	private TextView vendedor_input;
	private TextView fecha_input;
	private TextView fecha_actualizacion_input;
	private TextView n_plan_input;
	private EditText clientes_nuevos_proyectados_hoy_input;
	private TextView acumulado_clientes_nuevo_mes_input;
	private TextView venta_acumulada_mes_ayer_input;
	private TextView venta_acumulada_mes_hoy_input;
	private TextView cuota_de_ventas_mes_input;
	private TextView porcentaje_venta_vrs_cuota_hoy_input;
	private TextView venta_acumulada_hoy_c_nvos_input;
	private TextView valor_hoy_c_nvos_input;
	private TextView cobro_acumulado_ayer_input;
	private TextView cobro_acumulado_hoy_input;
	private TextView cuota_de_cobros_mes_input;
	private TextView porcentaje_cobrado_vrs_cuota_hoy_input;
	private TextView cartera_morosa_cobrada_hoy_input;
	private TextView n_pedidos_negados_input;
	private TextView valor_input;
	private TextView n_pedidos_recuperados_input;
	private TextView valor_2_input;
	private Cursor cursor;
	private ItaloDBAdapter DBAdapter;
	static private Bundle extras;
	static private String fecha;
	static private String sector_id;
	static private String ruta_id;
	static private String n_plan;
	static private String vendedor;
	static private SimpleDateFormat dateFormat2;
	static private SimpleDateFormat dateFormat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actividad_diaria_crear_paso_2);
		init();
		loadData();
	}

	@SuppressLint("SimpleDateFormat")
	private void init(){
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat2 = new SimpleDateFormat("MM/dd/yyyy");
		extras=getIntent().getExtras();
		fecha=extras.getString("fecha");
		n_plan=extras.getString("n_plan");
		sector_id=extras.getString("sector_id");
		ruta_id=extras.getString("ruta_id");
		vendedor=extras.getString("vendedor");
		vendedor_input=(TextView)findViewById(R.id.vendedor_input);
		fecha_input=(TextView)findViewById(R.id.fecha_input);
		fecha_actualizacion_input=(TextView)findViewById(R.id.fecha_actualizacion_input);
		n_plan_input=(TextView)findViewById(R.id.n_plan_input);
		clientes_nuevos_proyectados_hoy_input=(EditText)findViewById(R.id.clientes_nuevos_proyectados_hoy_input);
		acumulado_clientes_nuevo_mes_input=(TextView)findViewById(R.id.acumulado_clientes_nuevo_mes_input);
		venta_acumulada_mes_ayer_input=(TextView)findViewById(R.id.venta_acumulada_mes_ayer_input);
		venta_acumulada_mes_hoy_input=(TextView)findViewById(R.id.venta_acumulada_mes_hoy_input);
		cuota_de_ventas_mes_input=(TextView)findViewById(R.id.cuota_de_ventas_mes_input);
		porcentaje_venta_vrs_cuota_hoy_input=(TextView)findViewById(R.id.porcentaje_venta_vrs_cuota_hoy_input);
		venta_acumulada_hoy_c_nvos_input=(TextView)findViewById(R.id.venta_acumulada_hoy_c_nvos_input);
		valor_hoy_c_nvos_input=(TextView)findViewById(R.id.valor_hoy_c_nvos_input);
		cobro_acumulado_ayer_input=(TextView)findViewById(R.id.cobro_acumulado_ayer_input);
		cobro_acumulado_hoy_input=(TextView)findViewById(R.id.cobro_acumulado_hoy_input);
		cuota_de_cobros_mes_input=(TextView)findViewById(R.id.cuota_de_cobros_mes_input);
		porcentaje_cobrado_vrs_cuota_hoy_input=(TextView)findViewById(R.id.porcentaje_cobrado_vrs_cuota_hoy_input);
		cartera_morosa_cobrada_hoy_input=(TextView)findViewById(R.id.cartera_morosa_cobrada_hoy_input);
		n_pedidos_negados_input=(TextView)findViewById(R.id.n_pedidos_negados_input);
		valor_input=(TextView)findViewById(R.id.valor_input);
		n_pedidos_recuperados_input=(TextView)findViewById(R.id.n_pedidos_recuperados_input);
		valor_2_input=(TextView)findViewById(R.id.valor_2_input);
		DBAdapter=new ItaloDBAdapter(this);
		return;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.actividad_diaria_crear_paso_2, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	String aux=clientes_nuevos_proyectados_hoy_input.getText().toString();
    	switch (item.getItemId()){
			case R.id.siguiente:
				i = new Intent(getApplicationContext(), Actividad_Diaria_Crear_Paso_3.class);
				i.putExtra("n_plan", n_plan);
				i.putExtra("fecha", fecha);
				i.putExtra("sector_id", sector_id);
				i.putExtra("ruta_id", ruta_id);
				i.putExtra("vendedor", vendedor);
				if(aux.trim().equalsIgnoreCase("")){
					i.putExtra("c_nuevos", "0");
				}else{
					i.putExtra("c_nuevos", aux);
				}
				startActivityForResult(i, REQUEST_EXIT);
				return true;
			case R.id.atras:
				finish();
		    	return true;
		   	default:
		   		return super.onOptionsItemSelected(item);
		}
	}
    
    private void loadData(){
    	cursor=DBAdapter.getCrearActividadDiariaInfo(fecha);
    	if(cursor.moveToFirst()){
    		vendedor_input.setText(vendedor);
		    try {
				fecha_input.setText(dateFormat2.format(dateFormat.parse(fecha)));
			} catch (ParseException e) {
				Log.e("info","error parseando fecha " +e);
			}
//    		fecha_input.setText(fecha);
    		fecha_actualizacion_input.setText(cursor.getString(10));
    		n_plan_input.setText(n_plan);
    		acumulado_clientes_nuevo_mes_input.setText(Utility.formatNumber(cursor.getInt(0)));
    		venta_acumulada_mes_ayer_input.setText(Utility.formatNumber(cursor.getInt(1)));
    		venta_acumulada_mes_hoy_input.setText(Utility.formatNumber(0));
    		cuota_de_ventas_mes_input.setText(Utility.formatNumber(cursor.getInt(2)));
    		porcentaje_venta_vrs_cuota_hoy_input.setText(Utility.formatNumber(0));
    		venta_acumulada_hoy_c_nvos_input.setText(Utility.formatNumber(cursor.getInt(9)));
    		valor_hoy_c_nvos_input.setText(Utility.formatNumber(0));
    		cobro_acumulado_ayer_input.setText(Utility.formatNumber(cursor.getInt(3)));
    		cobro_acumulado_hoy_input.setText(Utility.formatNumber(0));
    		cuota_de_cobros_mes_input.setText(Utility.formatNumber(cursor.getInt(4)));
			porcentaje_cobrado_vrs_cuota_hoy_input.setText(Utility.formatNumber(0));
			cartera_morosa_cobrada_hoy_input.setText(Utility.formatNumber(0));
			n_pedidos_negados_input.setText(Utility.formatNumber(cursor.getInt(5)));
			valor_input.setText(Utility.formatNumber(cursor.getDouble(6)));
			n_pedidos_recuperados_input.setText(Utility.formatNumber(cursor.getInt(7)));
			valor_2_input.setText(Utility.formatNumber(cursor.getDouble(8)));
    	}
    	return;
    }
	
	@Override
	public void onBackPressed() {}
	
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 if (requestCode == REQUEST_EXIT) {
	          if (resultCode == RESULT_OK) {
	        	  setResult(RESULT_OK, null);
	        	  this.finish();
	          }
	      }
	 }
}
