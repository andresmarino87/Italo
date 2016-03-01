package indicadores;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Indicadores extends Activity implements OnTouchListener {
	static private boolean visible;
	static private String cliente_Str;
	static private String mas_cliente_Str;
	static private Cursor cursor;
	static private ItaloDBAdapter DBAdapter;
	static private LinearLayout clientes_con_no_gestion_layout;
	static private LinearLayout clientes_con_gestion_layout;
	static private LinearLayout clientes_visitados_layout;
	static private TextView clientes_visitados_label;
	static private TextView fecha_input;
	static private TextView clientes_totales_input;
	static private TextView clientes_en_ruta_input;
	static private TextView clientes_en_extraruta_input;
	static private TextView clientes_visitados_input;
	static private TextView clientes_con_gestion_input;
	static private TextView clientes_con_no_gestion_input;
	static private TextView clientes_no_visita_input;
	static private TextView clientes_pendientes_input;
	static private TextView efectividad_visitas_input;
	static private TextView cumplimiento_input;
	static private TextView venta_proyectada_input;
	static private TextView venta_real_input;
	static private TextView venta_pendiente_input;
	static private TextView cobro_proyectado_input;
	static private TextView cobro_real_input;
	static private TextView cobro_pendiente_input;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_indicadores);
		init();
		loadData();
	}

	private void init(){
		DBAdapter = new ItaloDBAdapter(this);
		clientes_con_no_gestion_layout=(LinearLayout)findViewById(R.id.clientes_con_no_gestion_layout);
		clientes_con_gestion_layout=(LinearLayout)findViewById(R.id.clientes_con_gestion_layout);
		clientes_visitados_layout=(LinearLayout)findViewById(R.id.clientes_visitados_layout);
		clientes_visitados_label=(TextView)findViewById(R.id.clientes_visitados_label);
		clientes_totales_input=(TextView)findViewById(R.id.clientes_totales_input);
		fecha_input=(TextView)findViewById(R.id.fecha_input);
		clientes_en_ruta_input=(TextView)findViewById(R.id.clientes_en_ruta_input);
		clientes_en_extraruta_input=(TextView)findViewById(R.id.clientes_en_extraruta_input);
		clientes_visitados_input=(TextView)findViewById(R.id.clientes_visitados_input);
		clientes_con_gestion_input=(TextView)findViewById(R.id.clientes_con_gestion_input);
		clientes_con_no_gestion_input=(TextView)findViewById(R.id.clientes_con_no_gestion_input);
		clientes_no_visita_input=(TextView)findViewById(R.id.clientes_no_visita_input);
		clientes_pendientes_input=(TextView)findViewById(R.id.clientes_pendientes_input);
		efectividad_visitas_input=(TextView)findViewById(R.id.efectividad_visitas_input);
		cumplimiento_input=(TextView)findViewById(R.id.cumplimiento_input);
		venta_proyectada_input=(TextView)findViewById(R.id.venta_proyectada_input);
		venta_real_input=(TextView)findViewById(R.id.venta_real_input);
		venta_pendiente_input=(TextView)findViewById(R.id.venta_pendiente_input);
		cobro_proyectado_input=(TextView)findViewById(R.id.cobro_proyectado_input);
		cobro_real_input=(TextView)findViewById(R.id.cobro_real_input);
		cobro_pendiente_input=(TextView)findViewById(R.id.cobro_pendiente_input);
		clientes_con_no_gestion_layout.setVisibility(LinearLayout.GONE);
		clientes_con_gestion_layout.setVisibility(LinearLayout.GONE);
		clientes_visitados_layout.setOnTouchListener(this);
		visible=false;
		cliente_Str=getApplicationContext().getString(R.string.clientes_visitados);
		mas_cliente_Str=getApplicationContext().getString(R.string.mas_clientes_visitados);
		return;
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_salir, menu);
        return true;
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()){
			case R.id.clientes_visitados_layout:
				if(visible){
					clientes_con_no_gestion_layout.setVisibility(LinearLayout.GONE);
					clientes_con_gestion_layout.setVisibility(LinearLayout.GONE);
					clientes_visitados_label.setText(mas_cliente_Str);
					visible=false;
				}else{
					clientes_con_no_gestion_layout.setVisibility(LinearLayout.VISIBLE);
					clientes_con_gestion_layout.setVisibility(LinearLayout.VISIBLE);
					clientes_visitados_label.setText(cliente_Str);
					visible=true;
				}
				break;
			default:
				break;
		}
		return false;
	}
	
	private void loadData(){
		int clientes_totales=0;
		int clientes_verdes=0;
		int clientes_amarillos=0;
		int clientes_rojos=0;
		int clientes_blancos=0;
		int efectividad=0;
		int cumplimiento=0;
		cursor=DBAdapter.getIndicadores();
		if(cursor.moveToFirst()){
			clientes_totales=cursor.getInt(1);
			
			fecha_input.setText(cursor.getString(0));
			clientes_totales_input.setText(String.valueOf(clientes_totales));
			clientes_en_ruta_input.setText(String.valueOf(cursor.getInt(2)));
			clientes_en_extraruta_input.setText(String.valueOf(cursor.getInt(3)));
			venta_proyectada_input.setText(Utility.formatNumber(cursor.getDouble(4)));
			venta_real_input.setText(Utility.formatNumber(cursor.getDouble(5)));
			venta_pendiente_input.setText(Utility.formatNumber(cursor.getDouble(6)));
			cobro_proyectado_input.setText(Utility.formatNumber(cursor.getDouble(7)));
			cobro_real_input.setText(Utility.formatNumber(0));
			cobro_pendiente_input.setText(Utility.formatNumber(0));

			cursor=DBAdapter.getIndicadoresGestiones(cursor.getString(8));
			if(cursor.moveToFirst()){
				do{
					if(cursor.getString(1)==null){
						clientes_blancos++;
					}else if(cursor.getString(1).equalsIgnoreCase("03")){
						clientes_verdes++;
					}else if(cursor.getString(1).equalsIgnoreCase("02")){
						clientes_amarillos++;
					}else if(cursor.getString(1).equalsIgnoreCase("04")){
						clientes_rojos++;
					}else{
						clientes_blancos++;
					}
				}while(cursor.moveToNext());
			}
			efectividad=(clientes_verdes*100/(clientes_verdes+clientes_amarillos));
			cumplimiento=(clientes_verdes+clientes_amarillos)*100/clientes_totales;
			clientes_con_gestion_input.setText(String.valueOf(clientes_verdes));
			clientes_con_no_gestion_input.setText(String.valueOf(clientes_amarillos));
			clientes_visitados_input.setText(String.valueOf(clientes_amarillos+clientes_verdes));
			clientes_no_visita_input.setText(String.valueOf(clientes_rojos));
			clientes_pendientes_input.setText(String.valueOf(clientes_blancos));
			efectividad_visitas_input.setText(String.valueOf(efectividad));
			cumplimiento_input.setText(String.valueOf(cumplimiento));
		}
		return;
	}
	
	@Override
	public void onBackPressed() {}
}
