package cobros;

import java.util.ArrayList;

import utilidades.Utility;

import com.italo_view.R;
import bd_utilidades.ItaloDBAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class Cobros_Sincronizados extends Activity {
	private Bundle extras;
	private Context context;
	private TextView cliente_input;
	private ListView listCobrosSincronizados;
	private ItaloDBAdapter DBAdapter;
	static private Cursor cursor;
	static private Cursor cursorMotivoAnulacion;
	static private ArrayList<CobrosSincronizados> cobros;
	private View alertView;
	private CobrosSincronizadosArrayAdapter adapter;
	private AlertDialog.Builder dialogBuilder;
	private AlertDialog dialog;
	static private String cliente_id;
	static private String cliente_nombre;
	private Spinner motivo_anulacion_input;
	private EditText observaciones_anulacion_input;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cobros_sincronizados);
		init();
		loadList();
		return;
	}

	private void init(){
		context=this;
		listCobrosSincronizados = (ListView)findViewById(R.id.listCobrosSincronizados);
		DBAdapter = new ItaloDBAdapter(this);
		cobros = new ArrayList<CobrosSincronizados>();
	    extras = getIntent().getExtras();
	    cliente_input=(TextView)findViewById(R.id.cliente_input);
	    cliente_id = extras.getString("cliente_id");
	    cliente_nombre = extras.getString("cliente_nombre");
	    cliente_input.setText(cliente_id +" "+ cliente_nombre);
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.cobros__sincronizados, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
	        case R.id.atras:
	        	finish();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}

	void loadList(){
		cursor=DBAdapter.getCobrosSincronizadosPago(cliente_id);
		loadDataList();
		adapter=new CobrosSincronizadosArrayAdapter(getApplicationContext(), R.layout.item_cobro_sincronizado, cobros);
		listCobrosSincronizados.setAdapter(adapter);
		registerForContextMenu(listCobrosSincronizados);
		return;
	}

	
	private void loadDataList(){
		String sync="";
		if(cursor.moveToFirst()){
			do{
				Log.i("info","loadDataList 0");
				Log.i("info","cursor.getString(6)!=null "+(cursor.getString(6)!=null));
				Log.i("info","cursor.getString(6)equalsIgnoreCase "+(cursor.getString(6).equalsIgnoreCase("")));
				Log.i("info","cursor.getString(6) "+(cursor.getString(6)));
				if(cursor.getString(6)!=null && cursor.getString(6).equalsIgnoreCase("")){
					Log.i("info","loadDataList 1");
					sync=(cursor.getString(7)== null || cursor.getString(7).equalsIgnoreCase(""))?getString(R.string.si):getString(R.string.no);
					cobros.add(new CobrosSincronizados(
						sync,
						cursor.getString(0),
						cursor.getString(1),
						cursor.getString(2), 
						cursor.getFloat(3)+cursor.getFloat(4)+cursor.getFloat(5),
						cursor.getString(6),
						cursor.getString(7)));
				}
			}while(cursor.moveToNext());
		}
		return;
	}
	
	private void reloadList(){
		cobros.clear();
		cursor=DBAdapter.getCobrosSincronizadosPago(cliente_id);
		loadDataList();
		adapter.notifyDataSetChanged();
		return;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.anular_cobro);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		CobrosSincronizados aux=cobros.get(info.position);
		switch(item.getItemId()){
			case 0:
				if(aux.getSinc().equalsIgnoreCase("NO")){
					alertView=getLayoutInflater().inflate(R.layout.abono_anular, null);
					dialogBuilder = new AlertDialog.Builder(this);
					dialogBuilder.setTitle(R.string.anular_cobro);
					dialogBuilder.setView(alertView);
					motivo_anulacion_input=(Spinner)alertView.findViewById(R.id.motivo_anulacion_input);
					observaciones_anulacion_input=(EditText)alertView.findViewById(R.id.observaciones_anulacion_input);
					loadMotivoAnulacionCobro();
					dialogBuilder.setPositiveButton(R.string.anular_cobro, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					});
					dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					});
					dialog=dialogBuilder.create();
					dialog.show();
					final Button modifyButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
					modifyButton.setOnClickListener(new GuardarMotivoAnulacion(dialog,aux));
				}else{
					Utility.showMessage(context, getString(R.string.el_cobro_ya_fue_sincronizado));
				}
				return true;
			default:
				return true;
		}
	}

	private class GuardarMotivoAnulacion implements View.OnClickListener {
		private final Dialog dialog;
	    private CobrosSincronizados aux;
	    
		public GuardarMotivoAnulacion(Dialog dialog, CobrosSincronizados cobro) {
	        this.dialog = dialog;
	        this.aux=cobro;
	    }
	    
		@Override
		public void onClick(View v) {
			boolean continuar=true;
			int logitud=0;
				if(cursorMotivoAnulacion.moveToPosition(motivo_anulacion_input.getSelectedItemPosition())){
					if(cursorMotivoAnulacion.getString(cursorMotivoAnulacion.getColumnIndex("observacion_obligatoria"))!=null && cursorMotivoAnulacion.getString(cursorMotivoAnulacion.getColumnIndex("observacion_obligatoria")).equalsIgnoreCase("S")){
						logitud=cursorMotivoAnulacion.getInt(cursorMotivoAnulacion.getColumnIndex("longitud_minima_obs"));
						if(logitud>0){
							if(observaciones_anulacion_input.getText().toString().length()<logitud){
								continuar=false;
								observaciones_anulacion_input.requestFocus();
								Utility.showMessage(context, "La observación debe tener mínimo "+logitud+" caracteres");
							}
						}
					}
				}else{
					continuar=false;
				}
			if(continuar){
				DBAdapter.beginTransaction();
				if(DBAdapter.restaurarPagosACartera(aux.getEsuId())){
					if(DBAdapter.deleteCobro(aux.getEsuId(),cursorMotivoAnulacion.getString(cursorMotivoAnulacion.getColumnIndex("motivo_id")),observaciones_anulacion_input.getText().toString())){
						if(DBAdapter.deleteEvento(aux.getEsuId())){
							DBAdapter.setTransactionSuccessful();
						}else{
							Utility.showMessage(context, R.string.hubo_un_error_al_anular_cobro);
						}
					}else{
						Utility.showMessage(context, R.string.hubo_un_error_al_anular_cobro);						
					}
				}else{
					Utility.showMessage(context, R.string.hubo_un_error_al_anular_cobro);
				}
				DBAdapter.endTransaction();
				reloadList();
				dialog.dismiss();
			}
		}
    }

	private void loadMotivoAnulacionCobro(){
		cursorMotivoAnulacion=DBAdapter.getMotivosAnulacionCobros();
		int i=0;
        String strings[] = new String[cursorMotivoAnulacion.getCount()];
		if(cursorMotivoAnulacion.moveToFirst()){
			do{
	        	strings[i] = cursorMotivoAnulacion.getString(2);
	        	i++;
			}while(cursorMotivoAnulacion.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		motivo_anulacion_input.setAdapter(adapter);
		return;
	}

	@Override
	public void onBackPressed() {}
}
