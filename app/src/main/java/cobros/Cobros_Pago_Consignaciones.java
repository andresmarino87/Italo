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
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class Cobros_Pago_Consignaciones extends Activity {
	private TextView n_cobro_input;
	private TextView documento_input;
	private TextView forma_pago;
	private TextView fecha_consignacion;
	private EditText numero_consignacion;
	private Spinner banco_input;
	private Spinner cuenta_input;
	private CheckBox efectivoCheck;
	private EditText efectivo_valor_input;
	private CheckBox chequeCheck;
	private EditText cheque_valor_input;
	private LinearLayout cheques_inputs;
	private EditText numero_cheque_input;
	private TextView fecha_cheque;
	private EditText cuenta_cheque_input;
	private Spinner banco_cheque_input;
	private TextView valor_total_input;
	private ListView listPagos;
	private Context context;
	private ItaloDBAdapter DBAdapter;
	private Bundle extras;
	private static ArrayList<C_pagos> pagos;
	private static ArrayList<C_detalle_consignaciones> detalle_consignaciones;
	private ArrayList<String> carteras_id;
	private static ArrayList<C_Cobros_Pago_Lista> consignaciones;
	private static String numero_cobro;
	private static String tipo_pago_id;
	private static CobrosArrayAdapterPagosLista adapter;
	private static Cursor cursorTipoPago;
	private static Cursor cursorBancos;
	private static Cursor cursorBancoCheques;
	private static Cursor cursorAux;
	private static boolean habilitarBanco;
	private static boolean habilitarCuenta;
	private static boolean habilitarNumeroConsignacion;
	private static boolean habilitarfechaConsignacion;
	private static boolean habilitarValor;
	private static float valorEfectivo;
	private static float valorCheques;
	private static float valorTotal;
	private static float var_acumulado_valor_cheques;
	private static float var_acumulado_consignacion;
	private View alertView;
	private AlertDialog.Builder dialogBuilder;
	private AlertDialog alertDialog;
	private CalendarView date_picker;
	private static SimpleDateFormat dateFormat;
	private static SimpleDateFormat dateFormat2;
	private static long fechaSelec;
	private static String fecha;
	private static boolean isPagoEfectivoRealizado; 
	private static boolean isModificarEfectivo; 
	private static boolean isModificarCheque; 
	private static boolean isModificar; 
	private static int positionEfectivo;
	private static int positionCheque;
	private static int numero_pago;
	private Intent i;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cobros_pago_consignaciones);
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
		
		chequeCheck.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1){
					cheques_inputs.setVisibility(LinearLayout.VISIBLE);
					cheque_valor_input.setVisibility(LinearLayout.VISIBLE);
				}else{
					cheques_inputs.setVisibility(LinearLayout.GONE);
					cheque_valor_input.setVisibility(LinearLayout.GONE);
					valorCheques=0;
					cheque_valor_input.setText("");
					valorTotal=valorEfectivo + valorCheques;
					setValorTotal(valorTotal);
				}
				
			}
		});
	
		efectivoCheck.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if(arg1){
					Log.i("info","isPagoEfectivoRealizado "+isPagoEfectivoRealizado);
					if(!isPagoEfectivoRealizado || isModificarEfectivo){
						efectivo_valor_input.setVisibility(LinearLayout.VISIBLE);
					}
				}else{
					efectivo_valor_input.setVisibility(LinearLayout.GONE);
					valorEfectivo=0;
					efectivo_valor_input.setText("");
					valorTotal=valorEfectivo + valorCheques;
					setValorTotal(valorTotal);
				}
				
			}
		});
	
		efectivo_valor_input.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable arg0) {
				try{
					valorEfectivo=Float.valueOf(arg0.toString());
				}catch(Exception e){
					valorEfectivo=0;
					Log.e("info","Invalid Float "+e);
				}
				valorTotal=valorEfectivo + valorCheques;
				setValorTotal(valorTotal);
				return;
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
		});
		
		cheque_valor_input.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
				try{
					valorCheques=Float.valueOf(s.toString());
				}catch(Exception e){
					valorCheques=0;
				}
				valorTotal=valorEfectivo + valorCheques;
				setValorTotal(valorTotal);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			
		});
		
		fecha_consignacion.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == MotionEvent.ACTION_DOWN){
					alertView = getLayoutInflater().inflate(R.layout.date_picker, null);
					dialogBuilder = new AlertDialog.Builder(context);
					dialogBuilder.setCancelable(false);
					dialogBuilder.setTitle(R.string.modificar_fecha);
					dialogBuilder.setView(alertView);
					date_picker=(CalendarView)alertView.findViewById(R.id.date_picker);
			//		Time time = new Time();
		//			time.setToNow();
	//				time.month -=2;
//					date_picker.setMinDate(time.normalize(true));
					dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					});
					dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					});
					alertDialog=dialogBuilder.create();
					alertDialog.show();
					final Button modifyButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
					modifyButton.setOnClickListener(new checkDate2(alertDialog));
				}
				return false;
           	}
		});
		
		fecha_cheque.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint({ "InflateParams", "ClickableViewAccessibility" }) @Override
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == MotionEvent.ACTION_DOWN){
					alertView = getLayoutInflater().inflate(R.layout.date_picker, null);
					dialogBuilder = new AlertDialog.Builder(context);
					dialogBuilder.setCancelable(false);
					dialogBuilder.setTitle(R.string.modificar_fecha);
					dialogBuilder.setView(alertView);
					date_picker=(CalendarView)alertView.findViewById(R.id.date_picker);
//					Time time = new Time();
	//				time.setToNow();
		//			time.month -=2;
			//		date_picker.setMinDate(time.normalize(true));
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
				return false;
           	}
		});
	}
	
	private void init(){
		n_cobro_input=(TextView)findViewById(R.id.n_cobro_input);
		documento_input=(TextView)findViewById(R.id.documento_input);
		forma_pago=(TextView)findViewById(R.id.forma_pago);
		fecha_consignacion=(TextView)findViewById(R.id.fecha_consignacion);
		numero_consignacion=(EditText)findViewById(R.id.numero_consignacion);
		banco_input=(Spinner)findViewById(R.id.banco_input);
		cuenta_input=(Spinner)findViewById(R.id.cuenta_input);
		efectivoCheck=(CheckBox)findViewById(R.id.efectivoCheck);
		efectivo_valor_input=(EditText)findViewById(R.id.efectivo_valor_input);
		chequeCheck=(CheckBox)findViewById(R.id.chequeCheck);
		cheque_valor_input=(EditText)findViewById(R.id.cheque_valor_input);
		cheques_inputs=(LinearLayout)findViewById(R.id.cheques_inputs);
		numero_cheque_input=(EditText)findViewById(R.id.numero_cheque_input);
		fecha_cheque=(TextView)findViewById(R.id.fecha_cheque);
		banco_cheque_input=(Spinner)findViewById(R.id.banco_cheque_input);
		valor_total_input=(TextView)findViewById(R.id.valor_total_input);
		cuenta_cheque_input=(EditText)findViewById(R.id.cuenta_cheque_input);
		listPagos=(ListView)findViewById(R.id.listPagos);
		context=this;
		extras = getIntent().getExtras();
		if(extras!=null){
			carteras_id=extras.getStringArrayList("carteras_id");
			numero_cobro=extras.getString("numero_cobro");
			tipo_pago_id=extras.getString("tipo_pago_id");
			C_pago_wrapper pagosWrapper=(C_pago_wrapper) extras.getSerializable("pagos");
			C_detalle_consignaciones_wrapper detalleConsignacionesWrapper=(C_detalle_consignaciones_wrapper) extras.getSerializable("detalle_consignaciones");
			pagos=pagosWrapper.getItems();
			detalle_consignaciones=detalleConsignacionesWrapper.getItems();
			if(detalle_consignaciones.size()>0){
				isModificar=true;
			}else{
				isModificar=false;
			}
		}else{
			finish();
		}
		n_cobro_input.setText(numero_cobro);
		DBAdapter=new ItaloDBAdapter(this);
		consignaciones=new ArrayList<C_Cobros_Pago_Lista>();
		adapter=new CobrosArrayAdapterPagosLista(getApplicationContext(), R.layout.item_documento_negativo, consignaciones);
		listPagos.setAdapter(adapter);
		cheques_inputs.setVisibility(LinearLayout.GONE);
		efectivo_valor_input.setVisibility(LinearLayout.GONE);
		cheque_valor_input.setVisibility(LinearLayout.GONE);
		valorEfectivo=0;
		valorCheques=0;
		valorTotal=0;
		setValorTotal(valorTotal);
		var_acumulado_valor_cheques=0;
		var_acumulado_consignacion=0;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat2 = new SimpleDateFormat("MM/dd/yyyy");
		isPagoEfectivoRealizado=false;
		registerForContextMenu(listPagos);
		isModificarEfectivo=false; 
		isModificarCheque=false;
		positionEfectivo=-1;
		positionCheque=-1;
		numero_pago=DBAdapter.getPagoNumeroPago();;
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cobros__pago__consignaciones, menu);
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
		    			limpiarHeader();
		    			numero_pago=DBAdapter.getPagoNumeroPago();;

		    		}
				});
				dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				dialogBuilder.create().show();
    			return true;
    		case R.id.guardar:
    			var_acumulado_consignacion=0;
    			for(C_Cobros_Pago_Lista aux:consignaciones){
    				var_acumulado_consignacion+=aux.getValor();
    			}
    			
    			Log.i("info",""+var_acumulado_consignacion);
//    			if(var_acumulado_consignacion==0){
    			if(consignaciones.size()==0){
    				if(isModificar){
						i = new Intent();
						i.putExtra("pagos", new C_pago_wrapper(pagos));
						i.putExtra("detalle_consignaciones", new C_detalle_consignaciones_wrapper(detalle_consignaciones));
						i.putExtra("removeCheck", 0);
						setResult(RESULT_OK, i);
						finish();
		    			return true;
    				}
    			}
    			
    			if(var_acumulado_consignacion>0){
    				if(banco_input.getSelectedItem()!=null && banco_input.getSelectedItemPosition()!=0){
    					if(cuenta_input.getSelectedItem()!=null){
    						if(numero_consignacion.getText()!=null && !numero_consignacion.getText().toString().trim().equalsIgnoreCase("")){
    							if(fecha_consignacion.getText()!=null && !fecha_consignacion.getText().toString().trim().equalsIgnoreCase("")){
		    						if(cursorBancos.moveToPosition(banco_input.getSelectedItemPosition()-1)){
		    							String var_consignacion="03";
		    							String var_forma_pago_id="";
		    							pagos.add(new C_pagos(
	    									numero_pago,
	    									var_consignacion,
	    									cursorBancos.getString(0),
	    									cursorBancos.getString(2),
	    									numero_consignacion.getText().toString(),
	    									Utility.dateToSqliteFormat(fecha_consignacion.getText().toString()),
	    									var_acumulado_consignacion,
	    									0));

	    									for(C_Cobros_Pago_Lista aux:consignaciones){
	    										var_forma_pago_id=DBAdapter.getFormaPagoId(var_consignacion, aux.getTipoPagoId());
	    										detalle_consignaciones.add(new C_detalle_consignaciones(
    												numero_pago,
    												DBAdapter.getPagoNumeroConsignacionDetalle(),
	    											aux.getTipoPagoId(),
	    											var_forma_pago_id,
	    											aux.getTipoPagoId(),
	    											aux.getBancoId(),
	    											aux.getCuenta(),
	    											aux.getFechaTransferencia(),
	    											aux.getValor(),
	    											0));
	    									}
	    									
	    									i = new Intent();
	    									i.putExtra("pagos", new C_pago_wrapper(pagos));
	    									i.putExtra("detalle_consignaciones", new C_detalle_consignaciones_wrapper(detalle_consignaciones));
	    									i.putExtra("removeCheck", consignaciones.size());
	    									setResult(RESULT_OK, i);
	    									finish();
		    						}
    							}else{
    								Utility.showMessage(context, R.string.para_continuar_debe_seleccionar_una_fecha);
    							}
    						}else{
    							Utility.showMessage(context, R.string.para_continuar_debe_seleccionar_un_numero_de_consignacion);
    							numero_cheque_input.requestFocus();
    						}
    					}else{
    						Utility.showMessage(context, R.string.para_continuar_debe_seleccionar_un_numero_de_cuenta);
    						cuenta_cheque_input.requestFocus();
    					}
    				}else{
						Utility.showMessage(context, R.string.por_favor_seleccione_un_banco);
    				}
    			}else{
    				Utility.showMessage(context, R.string.no_hay_datos_para_grabar);
    			}
    			return true;
    		case R.id.adicionar:
    			//Salvar Efectivo
    			if(efectivoCheck.isChecked() && (!isPagoEfectivoRealizado || isModificarEfectivo)){
    				float valorEfectivo=0;
    				try{
    					valorEfectivo=Float.valueOf(efectivo_valor_input.getText().toString());
    				}catch(Exception e){}
    				if( valorEfectivo>0){
    					cursorAux=DBAdapter.getTipoPago("01");
    					if(cursorAux.moveToFirst()){
    						if(!isModificarEfectivo){
            					consignaciones.add(new C_Cobros_Pago_Lista(
            							numero_pago,
               							null,
               							null,
               							"01",
               							cursorAux.getString(cursorAux.getColumnIndex("descripcion")),
               							null,
               							null,
               							"1900-01-01",
               							valorEfectivo));
                					var_acumulado_consignacion = var_acumulado_consignacion + valorEfectivo;
                					isPagoEfectivoRealizado=true;
    						}else{
    							C_Cobros_Pago_Lista aux=consignaciones.get(positionEfectivo);
               					var_acumulado_consignacion = var_acumulado_consignacion-aux.getValor() + valorEfectivo;
               					aux.setValor(valorEfectivo);
               					isPagoEfectivoRealizado=true;
    						}
    						adapter.notifyDataSetChanged();
        					limpiarEfectivo();
    					}
    				}else{
    					Utility.showMessage(context, getString(R.string.la_cantidad_ingresada_tiene_que_ser_mayor_a_0));
    				}
    			}

    			if(chequeCheck.isChecked()){
    				if(banco_cheque_input.getSelectedItem()!=null && banco_cheque_input.getSelectedItemPosition()!=0){
    					if(cuenta_cheque_input.getText()!=null && !cuenta_cheque_input.getText().toString().trim().equalsIgnoreCase("")){
    						if(numero_cheque_input.getText()!=null && !numero_cheque_input.getText().toString().trim().equalsIgnoreCase("")){
    							if(fecha_cheque.getText()!=null && !fecha_cheque.getText().toString().trim().equalsIgnoreCase("")){
    								if(valorCheques>0){
    			    					Cursor cursorAux=DBAdapter.getTipoPago("02");
    			    					if(cursorAux.moveToFirst()){
    			    						if(cursorBancoCheques.moveToPosition(banco_cheque_input.getSelectedItemPosition()-1)){
    			    							if(!isModificarCheque){
//    			        							numero_pago=DBAdapter.getPagoNumeroPago();
	    			    							consignaciones.add(new C_Cobros_Pago_Lista(
    			    									numero_pago,
	    			    								cursorBancoCheques.getString(0),
	    			    								cursorBancoCheques.getString(1),
		    			    							"02",
		    			    							cursorAux.getString(cursorAux.getColumnIndex("descripcion")),
		    			    							cuenta_cheque_input.getText().toString(),
		    			    							numero_cheque_input.getText().toString(),
		    			    							dateFormat.format(fechaSelec),
		    			    							valorCheques));
		        									var_acumulado_valor_cheques = var_acumulado_valor_cheques + valorCheques;
		        									var_acumulado_consignacion = var_acumulado_consignacion + valorCheques;
    			    							}else{
    			        							C_Cobros_Pago_Lista aux=consignaciones.get(positionCheque);
		        									var_acumulado_valor_cheques = var_acumulado_valor_cheques-aux.getValor() + valorCheques;
		        									var_acumulado_consignacion = var_acumulado_consignacion-aux.getValor() + valorCheques;
    					    						aux.setBancoId(cursorBancoCheques.getString(0));
    					    						aux.setBanco(cursorBancoCheques.getString(1));
    					    						aux.setCuenta(cuenta_cheque_input.getText().toString());
    					    						aux.setNumeroTransferencia(numero_cheque_input.getText().toString());
    					    						aux.setFechaTransferencia(dateFormat.format(fechaSelec));
    					    						aux.setValor(valorCheques);
    			    							}	
    			    						}
    			    						adapter.notifyDataSetChanged();
    			    		    			limpiar();
    			    					}
       								}else{
    									Utility.showMessage(context, R.string.favor_introduzca_un_valor_mayor_a_0);
    									cheque_valor_input.requestFocus();
    								}
    							}else{
    								Utility.showMessage(context, R.string.favor_introduzca_una_fecha);
    								fecha_cheque.requestFocus();
    							}
    						}else{
    							Utility.showMessage(context, R.string.favor_introduzca_un_numero_de_cheque);
    							numero_cheque_input.requestFocus();
    						}
    					}else{
    						Utility.showMessage(context, R.string.favor_introduzca_un_numero_de_cuenta);
    						cuenta_cheque_input.requestFocus();
    					}
    				}else{
    					Utility.showMessage(context, R.string.por_favor_seleccione_un_banco);
    				}
    			}
    			return true;
    		case R.id.atras:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
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
				fecha_cheque.setText(fecha);
				dialog.dismiss();
			}else{
				Utility.showMessage(context, getString(R.string.seleccione_una_fecha_actual_o_menor));
			}
	    }
	}

	private class checkDate2 implements View.OnClickListener {
		private final Dialog dialog;
	    public checkDate2(Dialog dialog) {
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
				fecha_consignacion.setText(fecha);
				dialog.dismiss();
			}else{
				Utility.showMessage(context, getString(R.string.seleccione_una_fecha_actual_o_menor));
			}
	    }
	}
	
	private void limpiarHeader(){
		numero_consignacion.setText("");
		fecha_consignacion.setText("");
		banco_input.setSelection(0);
		loadCuentas(banco_input.getSelectedItemPosition());
		return;
	}

	private void limpiar(){
		try{
			banco_cheque_input.setSelection(0);
//			numero_consignacion.setText("");
//			banco_input.setSelection(0);
			cuenta_input.setSelection(0);
			efectivo_valor_input.setText("");
			cheque_valor_input.setText("");
//			fecha_consignacion.setText("");
			fecha_cheque.setText("");
			cuenta_cheque_input.setText("");
			numero_cheque_input.setText("");
			cheques_inputs.setVisibility(LinearLayout.GONE);
			cheque_valor_input.setVisibility(LinearLayout.GONE);
			efectivo_valor_input.setVisibility(LinearLayout.GONE);
			valorEfectivo=0;
			valorCheques=0;
			valorTotal=valorEfectivo + valorCheques;
			setValorTotal(valorTotal);
			chequeCheck.setChecked(false);
			efectivoCheck.setChecked(false);
			isModificarEfectivo=false; 
			isModificarCheque=false; 
		}catch(Exception e){
			
		}
		return;
	}

	private void limpiarEfectivo(){
		efectivo_valor_input.setText("");
		efectivoCheck.setChecked(false);
		isModificarEfectivo=false; 
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
						final C_Cobros_Pago_Lista aux=consignaciones.get(info.position);
						if(aux.getTipoPagoId().equalsIgnoreCase("01")){
							isPagoEfectivoRealizado=false;
						}else{
							var_acumulado_valor_cheques=var_acumulado_valor_cheques-aux.getValor();
						}
						var_acumulado_consignacion=var_acumulado_consignacion-aux.getValor();
						consignaciones.remove(info.position);
						adapter.notifyDataSetChanged();
					}
				});
				dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				dialogBuilder.create().show();
				return true;
			case 1:
				positionEfectivo=-1;
				positionCheque=-1;
				Date oldDate;
				int pos=0;
				C_Cobros_Pago_Lista aux=consignaciones.get(info.position);
				if(aux.getTipoPagoId().equalsIgnoreCase("01")){
					isModificarEfectivo=true;
					positionEfectivo=info.position;
					efectivoCheck.setChecked(true);
					efectivo_valor_input.setText(String.valueOf(aux.getValor()));
				}else{
					positionCheque=info.position;	
					isModificarCheque=true; 
					chequeCheck.setChecked(true);
					cheque_valor_input.setText(String.valueOf(aux.getValor()));
					cuenta_cheque_input.setText(aux.getCuenta());
					numero_cheque_input.setText(aux.getNumeroTransferencia());
					if(cursorBancoCheques.moveToFirst()){
						do{
							if(cursorBancoCheques.getString(0).equals(aux.getBancoId())){
								break;
							}
							pos++;
						}while(cursorBancoCheques.moveToNext());
					}
					banco_cheque_input.setSelection(pos+1);
					try {
						oldDate = dateFormat.parse(aux.getFechaTransferencia());
						fecha_cheque.setText(dateFormat2.format(oldDate));
						fechaSelec=oldDate.getTime();
					} catch (ParseException e) {
						Log.e("info","Error modify "+e);
					}
				}
				return true;
			default:
	            return super.onContextItemSelected(item);
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
	
	private void loadBancosCheques(){
		cursorBancoCheques=DBAdapter.getBancos();
		int i=1;
        String strings[] = new String[cursorBancoCheques.getCount()+1];
        strings[0]=getString(R.string.seleccione_uno);
		if(cursorBancoCheques.moveToFirst()){
			do{
	        	strings[i] = cursorBancoCheques.getString(1);
	        	i++;
			}while(cursorBancoCheques.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		banco_cheque_input.setAdapter(adapter);
		return;
	}
	

	private void loadCuentas(int pos){
		Log.i("info","pos"+pos);
		if(habilitarCuenta && cursorBancos!=null && cursorBancos.moveToPosition(pos-1)){
				String strings[] = new String[1];
	        	strings[0] = cursorBancos.getString(2);
				ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				cuenta_input.setAdapter(adapter);
		}else{
			String strings[] = new String[1];
        	strings[0] = "";
			ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			cuenta_input.setAdapter(adapter);
		}
		return;
	}

	private void iniciarActivity(){
		String temp="";
//		numero_pago=DBAdapter.getPagoNumeroPago();
		cursorTipoPago=DBAdapter.getTipoPago(tipo_pago_id);
		if(cursorTipoPago.moveToFirst()){
			if(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("pedir_banco")).equalsIgnoreCase("S")){
				habilitarBanco=true;
				if(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("es_consignacion")).equalsIgnoreCase("S")){
					loadBancos();
					loadBancosCheques();
				}else{
					loadBancos();
					loadBancosCheques();
				}
			}
			if(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("pedir_numero_cuenta")).equalsIgnoreCase("S")){
				habilitarCuenta=true;
				if(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("es_transferencia")).equalsIgnoreCase("S")){
					loadCuentas(banco_input.getSelectedItemPosition());
				}
			}
				
			if(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("pedir_numero_documento")).equalsIgnoreCase("S")){
				habilitarNumeroConsignacion=true;
			}

			if(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("pedir_fecha_documento")).equalsIgnoreCase("S")){
				habilitarfechaConsignacion=true;
			}

			if(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("pedir_valor_documento")).equalsIgnoreCase("S")){
				habilitarValor=true;
			}
			forma_pago.setText(cursorTipoPago.getString(2));
			for(String aux:carteras_id){
				temp=temp+" "+aux;
			}
			documento_input.setText(temp);
			
			
			for(Iterator<C_pagos> it = pagos.iterator(); it.hasNext();) {
				C_pagos aux = it.next();
				if(aux.getTipoPagoId().equalsIgnoreCase(tipo_pago_id)){
    				int pos=0;
    				Date oldDate;
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
					numero_consignacion.setText(aux.getNumeroDoc());
					try {
    					oldDate = dateFormat.parse(aux.getFechaDoc());
    					fecha_consignacion.setText(dateFormat2.format(oldDate));
    				} catch (ParseException e) {
    					Log.e("info","Error modify "+e);
    				}
					it.remove();
				}
			}
			
			for(Iterator<C_detalle_consignaciones> it = detalle_consignaciones.iterator(); it.hasNext();) {
				C_detalle_consignaciones aux = it.next();
				cursorAux=DBAdapter.getTipoPago(aux.getTipoPagoId());
				if(cursorAux.moveToFirst()){
					if(aux.getTipoPagoId().equals("01")){
						var_acumulado_consignacion+=aux.getValorDoc();
					}else{
						var_acumulado_valor_cheques += aux.getValorDoc();
						
					}
		//			var_acumulado_consignacion+=aux.getValorDoc();
//					var_acumulado_valor_cheques = var_acumulado_valor_cheques-aux.getValor() + valorCheques;
	//				var_acumulado_consignacion = var_acumulado_consignacion-aux.getValor() + valorCheques;

					consignaciones.add(new C_Cobros_Pago_Lista(
						aux.getIdentificadoPago(),
						aux.getBancoId(),
						"",
						aux.getTipoPagoId(),
						cursorAux.getString(cursorAux.getColumnIndex("descripcion")),
						aux.getNumeroCuenta(),
						aux.getNumeroDoc(),
						aux.getFechaDoc(),
						aux.getValorDoc()));
					it.remove();
				}
			}
			adapter.notifyDataSetChanged();
		}else{
			Utility.showMessage(context, "Error ");
			finish();
		}
		return;
	}
	
	private void setValorTotal(float valorTotal){
		valor_total_input.setText(Utility.formatNumber(valorTotal));
		return;
	}
	
	@Override
	public void onBackPressed() {}
}
