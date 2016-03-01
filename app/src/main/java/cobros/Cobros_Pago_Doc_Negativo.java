package cobros;

import java.util.ArrayList;
import java.util.Locale;

import utilidades.Utility;

import com.italo_view.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Cobros_Pago_Doc_Negativo extends Activity {
	private Context context;
	private static ArrayList<C_documentos_negativos> documentos_negativos;
	private TextView n_cobro_input;
	private static int numero_cobro;
	private ListView listDocsNeg;
	private Bundle extras;
	private CobrosDocsNegativosArrayAdapter adapter;
	private static C_documentos_negativos aux;
	private static float var_valor_anterior;
	private Intent i;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cobros_pago_doc_negativo);
		Locale.setDefault(new Locale("us", "US"));
		init();
		iniciarActivity();
	}

	private void init(){
		context=this;
		extras = getIntent().getExtras();
		if(extras!=null){
			numero_cobro=extras.getInt("numero_cobro");
			C_documentos_negativos_wrapper documentosNegativosWrapper=(C_documentos_negativos_wrapper) extras.getSerializable("documentos_negativos");
			documentos_negativos=documentosNegativosWrapper.getItems();
		}else{
			finish();
		}
		listDocsNeg=(ListView)findViewById(R.id.listDocsNeg);
		n_cobro_input=(TextView)findViewById(R.id.n_cobro_input);
		n_cobro_input.setText(String.valueOf(numero_cobro));
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.cobros__pago__doc__negativo, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
			case R.id.guardar:
				i = new Intent();
				i.putExtra("documentos_negativos", new C_documentos_negativos_wrapper(documentos_negativos));
				setResult(RESULT_OK, i);
				finish();
				return true;
			case R.id.atras:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	
	private void iniciarActivity(){
		Log.i("info","docs negativo "+documentos_negativos.size());
		adapter = new CobrosDocsNegativosArrayAdapter(getApplicationContext(),R.layout.item_documento_negativo, documentos_negativos);
		listDocsNeg.setAdapter(adapter);
		registerForContextMenu(listDocsNeg);
		return;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.modificar);
	}
	
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()){
			case 0:
				var_valor_anterior=0;
				final View alertView = getLayoutInflater().inflate(R.layout.docs_negativos_input, new LinearLayout(context), false);
				final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
				dialogBuilder.setTitle(R.string.detalles);
				dialogBuilder.setView(alertView);
				dialogBuilder.setCancelable(false);
				final TextView valor_input=(TextView)alertView.findViewById(R.id.valor_input);
				aux=documentos_negativos.get(info.position);
				valor_input.setText(String.format("%.2f",aux.getValorDisponible()));
				var_valor_anterior=aux.getSaldoAnterior();
				dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(!valor_input.getText().toString().trim().equalsIgnoreCase("")){
							float var_valor_ingresado=Float.parseFloat(valor_input.getText().toString());
							if(Math.abs(var_valor_ingresado)<=Math.abs(var_valor_anterior)){
								if(var_valor_ingresado>=0){
									Utility.showMessage(context, R.string.valor_a_descontar_no_valido);
								}else{
									aux.setValorDisponible(var_valor_ingresado);
									adapter.notifyDataSetChanged();
								}
							}else{
								Utility.showMessage(context, R.string.valor_a_descontar_no_valido);
							}
						}
					}
				});
				dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				dialogBuilder.create().show();
				return true;
			default:
	            return super.onContextItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed() {}
}
