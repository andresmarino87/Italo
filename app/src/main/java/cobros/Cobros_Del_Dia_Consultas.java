package cobros;

import java.util.ArrayList;
import utilidades.Utility;
import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;

public class Cobros_Del_Dia_Consultas extends Activity {
	private Bundle extras;
	private Context context;
	private ListView listCobros;
	private ListView listCobrosDocumentos;
	private ListView listCobrosDocumentosPagos;
	private ItaloDBAdapter DBAdapter;
	static private Cursor cursor;
	static private Cursor cursorMotivoAnulacion;
	static private ArrayList<Cobros_Dia_Item> cobros;
	static private ArrayList<Cobros_Dia_Docs_Item> docs;
	static private ArrayList<Cobros_Dia_Docs_Pago_Item> pagos;
	private View alertView;
	private AlertDialog.Builder dialogBuilder;
	private AlertDialog dialog;
	static private String cliente_id;
	private Spinner motivo_anulacion_input;
	private EditText observaciones_anulacion_input;
	private CobrosDiaArrayAdapter adapter;
	private CobrosDocsDiaArrayAdapter adapterDocs;
	private CobrosDocsPagoDiaArrayAdapter adapterPagos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cobros_del_dia_consultas);
		init();
		loadCobros();
		loadCobrosDocs();
		loadCobrosDocsPagos();

		
		listCobros.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,	int arg2, long arg3) {
				final Cobros_Dia_Item aux=cobros.get(arg2);
				clearDocs();
				clearPagos();
				reloadListDocs(aux.getCobroId());
			}
		});
		
		listCobrosDocumentos.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				final Cobros_Dia_Docs_Item aux=docs.get(arg2);
				clearPagos();
				reloadListPagos(aux.getCobroId(),aux.getDocId());
			}
		});
		return;
	}

	private void init(){
		context=this;
		DBAdapter = new ItaloDBAdapter(this);
		listCobros = (ListView)findViewById(R.id.listCobros);
		listCobrosDocumentos = (ListView)findViewById(R.id.listCobrosDocumentos);
		listCobrosDocumentosPagos = (ListView)findViewById(R.id.listCobrosDocumentosPagos);
		cobros = new ArrayList<Cobros_Dia_Item>();
		docs = new ArrayList<Cobros_Dia_Docs_Item>();
		pagos = new ArrayList<Cobros_Dia_Docs_Pago_Item>();
	    extras = getIntent().getExtras();
	    cliente_id = extras.getString("cliente_id");
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.cobros_del_dia_consultas, menu);
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.anular_cobro);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		Cobros_Dia_Item aux=cobros.get(info.position);
		switch(item.getItemId()){
			case 0:
				if(aux.getEsuId()!=null && !aux.getEsuId().equalsIgnoreCase("")){
					alertView=getLayoutInflater().inflate(R.layout.abono_anular, new LinearLayout(context),false);
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
					modifyButton.setOnClickListener(new GuardarMotivoAnulacion(dialog,info.position));
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
		private int pos;
	    private Cobros_Dia_Item aux;
	    
		public GuardarMotivoAnulacion(Dialog dialog, int pos) {
	        this.dialog = dialog;
	        this.pos=pos;
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
			Log.i("info","onClick 0");
			if(continuar){
				DBAdapter.beginTransaction();
				Log.i("info","onClick 1");
				aux=cobros.get(pos);
				Log.i("info","onClick 2");
				Log.i("info","onClick aux.getNPago() ");
				Log.i("info","onClick cursorMotivoAnulacion.getString(cursorMotivoAnulacion.getColumnIndex()) "+cursorMotivoAnulacion.getString(cursorMotivoAnulacion.getColumnIndex("motivo_id")));
				Log.i("info","onClick observaciones_abono_input.getText().toString() "+observaciones_anulacion_input.getText().toString());
				if(DBAdapter.deleteCobro(aux.getEsuId(),cursorMotivoAnulacion.getString(cursorMotivoAnulacion.getColumnIndex("motivo_id")),observaciones_anulacion_input.getText().toString())){
					if(DBAdapter.deleteEvento(aux.getEsuId())){
						DBAdapter.setTransactionSuccessful();
					}
				}
				Log.i("info","onClick 3");
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

	void loadCobros(){
		cursor=DBAdapter.getCobrosDelDiaGrilla(cliente_id);
		loadDataList();
		adapter=new CobrosDiaArrayAdapter(context, R.layout.item_cobro_del_dia, cobros);
		listCobros.setAdapter(adapter);
		registerForContextMenu(listCobros);
		return;
	}
		
	private void loadDataList(){
		String sync="";
		if(cursor.moveToFirst()){
			do{
				if(cursor.getString(7)!=null && cursor.getString(7).equalsIgnoreCase("")){
					sync=(cursor.getString(8)== null || cursor.getString(8).equalsIgnoreCase(""))?getString(R.string.si):getString(R.string.no);
					cobros.add(new Cobros_Dia_Item(sync,
						cursor.getString(1),
						cursor.getString(2),
						cursor.getString(0),
						cursor.getString(3),
						cursor.getDouble(4)+cursor.getDouble(5)-cursor.getDouble(6),
						cursor.getString(7),
						cursor.getString(8)));
				}
			}while(cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		return;
	}
	
	void loadCobrosDocs(){
		//cursor=DBAdapter.getCobrosDelDiaGrilla(cliente_id);
		//loadDataDocs();
		adapterDocs=new CobrosDocsDiaArrayAdapter(context, R.layout.item_cobro_docs_dia, docs);
		listCobrosDocumentos.setAdapter(adapterDocs);
		return;
	}
		
	private void loadDataDocs(String cobro_id){
		if(cursor.moveToFirst()){
			do{
				docs.add(new Cobros_Dia_Docs_Item(
					cobro_id,
					cursor.getString(1),
					cursor.getString(0),
					cursor.getDouble(2),
					cursor.getDouble(3)+cursor.getDouble(4)-cursor.getDouble(5)));
			}while(cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		return;
	}
	
	private void loadCobrosDocsPagos(){
		//cursor=DBAdapter.getCobrosDelDiaGrilla(cliente_id);
		//loadDataDocs();
		adapterPagos=new CobrosDocsPagoDiaArrayAdapter(context, R.layout.item_cobro_docs_pagos_dia, pagos);
		listCobrosDocumentosPagos.setAdapter(adapterPagos);
		return;
	}
		
	private void loadDataDocsPagos(){
		if(cursor.moveToFirst()){
			do{
				pagos.add(new Cobros_Dia_Docs_Pago_Item(
					cursor.getString(0),
					cursor.getDouble(1)));
			}while(cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		return;
	}

	private void reloadList(){
		cobros.clear();
		cursor=DBAdapter.getCobrosDelDia(cliente_id);
		loadDataList();
		adapter.notifyDataSetChanged();
		return;
	}
	
	private void reloadListDocs(String cobro_id){
		docs.clear();
		cursor=DBAdapter.getDocsXCobro(cobro_id);
		loadDataDocs(cobro_id);
		adapterDocs.notifyDataSetChanged();
		return;
	}

	private void reloadListPagos(String cobro_id,String doc_id){
		pagos.clear();
		Log.i("info","test");
		cursor=DBAdapter.getPagosXDocIdCrucePago(cobro_id,doc_id);
		loadDataDocsPagos();
		cursor=DBAdapter.getPagosXDocIdCruceConsignaciones(cobro_id,doc_id);
		loadDataDocsPagos();
		cursor=DBAdapter.getPagosXDocIdCruceDocsNeg(cobro_id,doc_id);
		loadDataDocsPagos();
		Log.i("info","test count "+pagos.size());
		adapterPagos.notifyDataSetChanged();
		return;
	}

	private void clearDocs(){
		docs.clear();
		adapterDocs.notifyDataSetChanged();
		return;
	}
	
	private void clearPagos(){
		pagos.clear();
		adapterPagos.notifyDataSetChanged();
		return;
	}
}