package cobros;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Cobros_Pago_Efectivo extends Activity {
	private Context context;
	private TextView valor_input;
	private Bundle extras;
	private static ArrayList<C_pagos> pagos;
	private static String tipo_pago_id;
	private static float var_efectivo;
	private static boolean isNew;
	private ItaloDBAdapter DBAdapter;
	private Intent i;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cobros_pago_efectivo);
		Locale.setDefault(new Locale("us", "US"));
		init();
		iniciarActivity();
	}

	private void init(){
		context=this;
		isNew=true;
		extras = getIntent().getExtras();
		if(extras!=null){
			tipo_pago_id=extras.getString("tipo_pago_id");
			C_pago_wrapper pagosWrapper=(C_pago_wrapper) extras.getSerializable("pagos");
			pagos=pagosWrapper.getItems();
		}else{
			finish();
		}
		var_efectivo=0;
		DBAdapter=new ItaloDBAdapter(this);
		valor_input=(TextView)findViewById(R.id.valor_input);
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.cobros__pago__efectivo, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
			case R.id.guardar:
				int removeCheck=0;
				if(valor_input.getText().toString().trim().equalsIgnoreCase("")){
					var_efectivo=0;
				}else{
					var_efectivo=Float.valueOf(valor_input.getText().toString());
				}
				if(isNew){
					if(var_efectivo!=0){
						pagos.add(new C_pagos(
							DBAdapter.getPagoNumeroPago(),
							tipo_pago_id,
							null,
							null,
							null,
							"1900-01-01",
							var_efectivo,
							0)	
						);
						removeCheck=1;
						auxGuardar(removeCheck);
					}else{
						Utility.showMessage(context, R.string.la_cantidad_ingresada_tiene_que_ser_mayor_a_0);
					}
				}else{
					for(Iterator<C_pagos> it = pagos.iterator(); it.hasNext();) {
						C_pagos aux = it.next();
						if(aux.getTipoPagoId().equalsIgnoreCase(tipo_pago_id)){
							if(var_efectivo==0){
								it.remove();
								removeCheck=0;
							}else{
								aux.setValorDocumento(var_efectivo);
								removeCheck=1;
							}
						}
					}
					auxGuardar(removeCheck);
				}
				return true;
			case R.id.atras:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void auxGuardar(int removeCheck){
		i = new Intent();	
		i.putExtra("pagos", new C_pago_wrapper(pagos));
		i.putExtra("removeCheck", removeCheck);
		setResult(RESULT_OK, i);
		finish();
		return;
	}
	
	private void iniciarActivity(){		
		for(C_pagos aux:pagos){
			if(aux.getTipoPagoId().equalsIgnoreCase(tipo_pago_id)){
				var_efectivo=aux.getValorDocumento();
				isNew=false;
				break;
			}else{
				var_efectivo=0;
			}
		}
		if(var_efectivo!=0){
			valor_input.setText(String.format("%.2f",var_efectivo));
		}else{
			valor_input.setText("");
		}
		return;
	}
	
	@Override
	public void onBackPressed() {}
}
