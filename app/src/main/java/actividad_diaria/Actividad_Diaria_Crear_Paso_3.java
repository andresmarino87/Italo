package actividad_diaria;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import utilidades.Utility;
import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class Actividad_Diaria_Crear_Paso_3 extends Activity {
	private ItaloDBAdapter DBAdapter;
	private TextView vendedor_input;
	private TextView fecha_input;
	private TextView n_plan_input;
	private EditText compromisos_del_vendedor_input;
	private Bundle extras;
	static private String fecha;
	static private String fechaMod;
	static private String vendedor;
	static private String n_plan;
	static private String c_nuevos;
	static private String sector_id;
	static private String ruta_id;
	static private SimpleDateFormat dateFormat2;
	static private SimpleDateFormat dateFormat;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actividad_diaria_crear_paso_3);
		init();
		loadData();
	}

	@SuppressLint("SimpleDateFormat")
	public void init(){
		context=this;
		extras=getIntent().getExtras();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat2 = new SimpleDateFormat("MM/dd/yyyy");
		fecha=extras.getString("fecha");
		vendedor=extras.getString("vendedor");
		n_plan=extras.getString("n_plan");
		c_nuevos=extras.getString("c_nuevos");
		sector_id=extras.getString("sector_id");
		ruta_id=extras.getString("ruta_id");
		DBAdapter=new ItaloDBAdapter(this);
		vendedor_input=(TextView)findViewById(R.id.vendedor_input);
		fecha_input=(TextView)findViewById(R.id.fecha_input);
		n_plan_input=(TextView)findViewById(R.id.n_plan_input);
		compromisos_del_vendedor_input=(EditText)findViewById(R.id.compromisos_del_vendedor_input);
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.actividad_diaria_crear_paso_3, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.finalizar:
				String compromisos=compromisos_del_vendedor_input.getText().toString();
				if(!compromisos.trim().equalsIgnoreCase("")){
					DBAdapter.beginTransaction();
					if(DBAdapter.insertPlanDeTrabajo(fecha,n_plan,c_nuevos, compromisos,DBAdapter.getAsesorId(),sector_id,ruta_id)){
						DBAdapter.cleanPlanTrabajoTemp();
						DBAdapter.setTransactionSuccessful();
						setResult(RESULT_OK, null);
						finish();
					}else{
						Utility.showMessage(context, R.string.error_al_crear_plan);
					}		
					DBAdapter.endTransaction();
				}else{
					Utility.showMessage(context, R.string.por_favor_llenar_compromisos_y_acciones);
				}
				return true;
			case R.id.atras:
				finish();
				return true;
		   	default:
		   		return super.onOptionsItemSelected(item);
		}
	}
    
    private void loadData(){
	    try {
	    	fechaMod=dateFormat2.format(dateFormat.parse(fecha));
		} catch (ParseException e) {
			Log.e("info","error parseando fecha " +e);
		}
    	vendedor_input.setText(vendedor);
		fecha_input.setText(fechaMod);
		n_plan_input.setText(n_plan);
    	return;
    }
	
	@Override
	public void onBackPressed() {}
}
