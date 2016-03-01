package cobros;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.Time;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class Cobros_Pago_Transferencia extends Activity {
	private View alertView;
	private AlertDialog.Builder dialogBuilder;
	private AlertDialog alertDialog;
	private Context context;
	private Bundle extras;
	private static Cursor cursor;
	private static Cursor cursorBancos;
	private static ArrayList<C_pagos> pagos;
	private static String tipo_pago_id;
	private static String numero_cobro;
	private TextView n_cobro_input;
	private TextView documento_input;
	private Spinner banco_input;
	private Spinner cuenta_input;
	private EditText numero_transferencia_input;
	private TextView fecha_transferencia;
	private EditText valor_input;
	private ListView listPagos;
	private ItaloDBAdapter DBAdapter;
	private static boolean habilitarBanco=false;
	private static boolean habilitarCuenta=false;
	private static boolean habilitarNumeroTransferencia=false;
	private static boolean habilitarfechaTransferencia=false;
	private static boolean habilitarValor=false;
	private static long fechaSelec;
	private static ArrayList<C_Cobros_Pago_Lista> transferencias;
	private CobrosArrayAdapterPagosLista adapter;
	private Intent i;
	private static CalendarView date_picker;
	private static SimpleDateFormat dateFormat;
	private static SimpleDateFormat dateFormat2;
	private static String fecha;
	private static ArrayList<String> carteras_id;
	private static boolean isModificar=false;
	private static boolean isGuardarModificacion=false;
	private static int position;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cobros_pago_transferencia);
		Locale.setDefault(new Locale("us", "US"));
		init();
		iniciarActivity();
		banco_input.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				loadCuentas(arg2);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		fecha_transferencia.setOnClickListener(new OnClickListener(){
			@SuppressLint("InflateParams") @Override
			public void onClick(View arg0) {
				alertView = getLayoutInflater().inflate(R.layout.date_picker, null);
				dialogBuilder = new AlertDialog.Builder(context);
				dialogBuilder.setCancelable(false);
				dialogBuilder.setTitle(R.string.modificar_fecha);
				dialogBuilder.setView(alertView);
				date_picker=(CalendarView)alertView.findViewById(R.id.date_picker);
//				Time time = new Time();
	//			time.setToNow();
		//		time.month -=2;
			//	date_picker.setMinDate(time.normalize(true));
				dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				alertDialog=dialogBuilder.create();
				alertDialog.show();
				final Button modifyButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
				modifyButton.setOnClickListener(new checkDate(alertDialog));
			}
			
		});
	}
	
	@SuppressLint("SimpleDateFormat")
	private void init(){
		context=this;
		isGuardarModificacion=false;
		extras = getIntent().getExtras();
		if(extras!=null){
			carteras_id=extras.getStringArrayList("carteras_id");
			numero_cobro=extras.getString("numero_cobro");
			tipo_pago_id=extras.getString("tipo_pago_id");
			C_pago_wrapper pagosWrapper=(C_pago_wrapper) extras.getSerializable("pagos");
			pagos=pagosWrapper.getItems();
		}else{
			finish();
		}
		DBAdapter=new ItaloDBAdapter(this);
		n_cobro_input=(TextView)findViewById(R.id.n_cobro_input);
		documento_input=(TextView)findViewById(R.id.documento_input);
		banco_input=(Spinner)findViewById(R.id.banco_input);
		cuenta_input=(Spinner)findViewById(R.id.cuenta_input);
		numero_transferencia_input=(EditText)findViewById(R.id.numero_transferencia_input);
		fecha_transferencia=(TextView)findViewById(R.id.fecha_transferencia);
		valor_input=(EditText)findViewById(R.id.valor_input);
		listPagos=(ListView)findViewById(R.id.listPagos);
		transferencias=new ArrayList<C_Cobros_Pago_Lista>();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat2 = new SimpleDateFormat("MM/dd/yyyy");
		n_cobro_input.setText(numero_cobro);
		adapter=new CobrosArrayAdapterPagosLista(getApplicationContext(), R.layout.item_documento_negativo, transferencias);
		listPagos.setAdapter(adapter);
		registerForContextMenu(listPagos);		
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.cobros__pago__transferencia, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
    		case R.id.limpiar:
				final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
				dialogBuilder.setCancelable(false);
				dialogBuilder.setTitle(R.string.alerta);
				dialogBuilder.setMessage(R.string.desea_limpiar_los_controles_del_formulario);
				dialogBuilder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
		    			limpiar();
					}
				});
				dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				dialogBuilder.create().show();
    			return true;
    		case R.id.guardar:
    			if(transferencias.size()>0){
    				guardarDoc();
    			}else{
    				if(!isGuardarModificacion){			
    					if(adicionarDoc()){
    						guardarDoc();
    					}
    				}else{
    					guardarDoc();
    				}
    			}
    			return true;
    		case R.id.adicionar:
    			adicionarDoc();
	   			return true;
    		case R.id.atras:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void guardarDoc(){
		for(C_Cobros_Pago_Lista aux:transferencias){
			pagos.add(new C_pagos(DBAdapter.getPagoNumeroPago(),
				tipo_pago_id,
				aux.getBancoId(),
				aux.getCuenta(),
				aux.getNumeroTransferencia(),
				aux.getFechaTransferencia(),
				aux.getValor(),
				0));
		}
		i = new Intent();
		i.putExtra("pagos", new C_pago_wrapper(pagos));
		i.putExtra("removeCheck", transferencias.size());
		setResult(RESULT_OK, i);
		finish();
		return;
	}
	
	private boolean adicionarDoc(){
		boolean res = false;
		if(banco_input.getSelectedItem()!=null && banco_input.getSelectedItemPosition()!=0){
			if(cuenta_input.getSelectedItem()!=null){
				if(numero_transferencia_input.getText()!=null && !numero_transferencia_input.getText().toString().trim().equalsIgnoreCase("")){
					if(fecha_transferencia.getText()!=null && !fecha_transferencia.getText().toString().trim().equalsIgnoreCase("")){
						if(valor_input.getText()!=null && !valor_input.getText().toString().trim().equalsIgnoreCase("")){
							if(cursorBancos.moveToPosition(banco_input.getSelectedItemPosition()-1)){
								if(!isModificar){			
									transferencias.add(new C_Cobros_Pago_Lista(
										DBAdapter.getPagoNumeroPago(),
					   					cursorBancos.getString(0),
					   					cursorBancos.getString(1),
					   					tipo_pago_id,
					   					cursor.getString(2),
					   					cursorBancos.getString(2),
					   					numero_transferencia_input.getText().toString(),
					   					dateFormat.format(fechaSelec),
					   					Float.parseFloat(valor_input.getText().toString())));
		    	    			}else{
		    	    				C_Cobros_Pago_Lista aux = transferencias.get(position);
			    						aux.setBancoId(cursorBancos.getString(0));
			    						aux.setBanco(cursorBancos.getString(1));
			    						aux.setCuenta(cursorBancos.getString(2));
			    						aux.setNumeroTransferencia(numero_transferencia_input.getText().toString());
			    						aux.setFechaTransferencia(dateFormat.format(fechaSelec));
			    						aux.setValor(Float.parseFloat(valor_input.getText().toString()));
		    	    			}
								adapter.notifyDataSetChanged();
								limpiar();
								res=true;
							}else{
							}
						}else{
							Utility.showMessage(context, R.string.favor_introduzca_un_valor_mayor_a_0);
							valor_input.requestFocus();
						}
					}else{
						Utility.showMessage(context, R.string.favor_introduzca_una_fecha);
						fecha_transferencia.requestFocus();
					}
				}else{	
					Utility.showMessage(context, R.string.favor_introduzca_un_numero_de_transferencia);
					numero_transferencia_input.requestFocus();
				}
			}else{
				Log.e("info","cuenta");
			}
		}else{
			Utility.showMessage(context, R.string.por_favor_seleccione_un_banco);
		}
		return res;
	}
	
	private void limpiar(){
		numero_transferencia_input.setText("");
		fecha_transferencia.setText("");
		valor_input.setText("");
		banco_input.setSelection(0);
		isModificar=false;
		return;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.modificar);
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.eliminar);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()){
			case 0:
				final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
				dialogBuilder.setCancelable(false);
				dialogBuilder.setTitle(R.string.alerta);
				dialogBuilder.setMessage(R.string.esta_seguro_que_desea_eliminar_el_pago);
				dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						transferencias.remove(info.position);
						adapter.notifyDataSetChanged();
						limpiar();
					}
				});
				dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				dialogBuilder.create().show();
				return true;
			case 1:
				position=info.position;
				Date oldDate;
				int pos=0;
				C_Cobros_Pago_Lista aux=transferencias.get(position);
				valor_input.setText(String.valueOf(aux.getValor()));
				numero_transferencia_input.setText(String.valueOf(aux.getNumeroTransferencia()));
				if(cursorBancos.moveToFirst()){
					do{
						if(cursorBancos.getString(0).equals(aux.getBancoId())){
							break;
						}
						pos++;
					}while(cursorBancos.moveToNext());
				}
				banco_input.setSelection(pos+1);
				cuenta_input.setSelection(0);
				try {
					oldDate = dateFormat.parse(aux.getFechaTransferencia());
					fecha_transferencia.setText(dateFormat2.format(oldDate));
					fechaSelec=oldDate.getTime();
				} catch (ParseException e) {
					Log.e("info","Error modify "+e);
				}
				isModificar=true;
				return true;
			default:
	            return super.onContextItemSelected(item);
		}
	}

	private class checkDate implements View.OnClickListener {
		private final Dialog dialog;
	    public checkDate(Dialog dialog) {
	        this.dialog = dialog;
	    }
	    @Override
	    public void onClick(View v) {
			fechaSelec=0;
			long hoy=0;
			Calendar cal= Calendar.getInstance();
			Time time = new Time();
			time.set(cal.getTime().getTime());
			fechaSelec=date_picker.getDate();
			hoy=time.toMillis(true);
			if(hoy>=fechaSelec){
				fecha=dateFormat2.format(fechaSelec);
				fecha_transferencia.setText(fecha);
				dialog.dismiss();
			}else{
				Utility.showMessage(context, R.string.seleccione_una_fecha_actual_o_menor);
			}
	    }
	}

	private void loadBancos(){
		cursorBancos=DBAdapter.getBancosTransferencia();
		int i=1;
        String strings[] = new String[cursorBancos.getCount()+1];
        strings[0]=getString(R.string.seleccione_uno);
        if(cursorBancos.moveToFirst()){
			do{
	        	strings[i] = cursorBancos.getString(1);
	        	i++;
			}while(cursorBancos.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		banco_input.setAdapter(adapter);
		return;
	}

	private void loadCuentas(int pos){
		if(habilitarCuenta && cursorBancos!=null && cursorBancos.moveToPosition(pos)){
			String strings[] = new String[1];
        	strings[0] = cursorBancos.getString(2);
			ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			cuenta_input.setAdapter(adapter);
		}
		return;
	}

	private void iniciarActivity(){
		String temp="";
		cursor=DBAdapter.getTipoPago(tipo_pago_id);
		if(cursor.moveToFirst()){
			if(cursor.getString(cursor.getColumnIndex("pedir_banco")).equalsIgnoreCase("S")){
				habilitarBanco=true;
				if(cursor.getString(cursor.getColumnIndex("es_transferencia")).equalsIgnoreCase("S")){
					loadBancos();
				}else{
					loadBancos();
				}
			}
			if(cursor.getString(cursor.getColumnIndex("pedir_numero_cuenta")).equalsIgnoreCase("S")){
				habilitarCuenta=true;
				if(cursor.getString(cursor.getColumnIndex("es_consignacion")).equalsIgnoreCase("S")){
					loadCuentas(banco_input.getSelectedItemPosition());
				}
			}
				
			if(cursor.getString(cursor.getColumnIndex("pedir_numero_documento")).equalsIgnoreCase("S")){
				habilitarNumeroTransferencia=true;
			}

			if(cursor.getString(cursor.getColumnIndex("pedir_fecha_documento")).equalsIgnoreCase("S")){
				habilitarfechaTransferencia=true;
			}

			if(cursor.getString(cursor.getColumnIndex("pedir_valor_documento")).equalsIgnoreCase("S")){
				habilitarValor=true;
			}

			for(String aux:carteras_id){
				temp+=" "+aux;
			}
			documento_input.setText(temp);
			
			for(Iterator<C_pagos> it = pagos.iterator(); it.hasNext();) {
				C_pagos aux = it.next();
				if(aux.getTipoPagoId().equalsIgnoreCase(tipo_pago_id)){
					transferencias.add(new C_Cobros_Pago_Lista(
						aux.getIdentificadorPago(),
						aux.getBancoId(),
						"",
						tipo_pago_id,
						cursor.getString(2),
						aux.getNumeroCuenta(),
						aux.getNumeroDoc(),
						aux.getFechaDoc(),
						aux.getValorDocumento()));
					it.remove();
					isGuardarModificacion=true;
				}
			}
			adapter.notifyDataSetChanged();
		}else{
			Utility.showMessage(context, "Error ");
			finish();
		}
		return;
	}
	
	@Override
	public void onBackPressed() {}
}
