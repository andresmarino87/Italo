package cobros;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

@SuppressLint("ClickableViewAccessibility") public class Cobros_Pago extends Activity {
	final static private int STATIC_INTEGER_VALUE=0;
	static private final int REQUEST_EFECTIVO = 1;
	static private final int REQUEST_CONSIGNACION = 2;
	static private final int REQUEST_TRANSFERENCIA = 3;
	static private final int REQUEST_CHEQUE_DIA = 4;
	static private final int REQUEST_CHEQUE_POST = 5;
	static private final int REQUEST_CHEQUE_TERCERO_DIA = 6;
	static private final int REQUEST_CHEQUE_TERCERO_POST = 7;
	static private final int REQUEST_DOCS_NEG = 8;

	private CalendarView date_picker;
	private AlertDialog alertDialogAbono;
	private AlertDialog alertDialogFecha;
	private Intent i;
	private Bundle extras;
	private ItaloDBAdapter DBAdapter;
	private TextView valor_documentos_input;
	private TextView nombre_del_cliente_input;
	private TextView numero_pago_input;
	private TextView pronto_pago_input;
	private TextView valor_a_recaudar_input;
	private TextView obs_label;
	private ArrayList<String> carteras_id;
	static private Cursor cursor;
	static private Cursor cursorTipoPago;
	static private Cursor cursorAbono;
	static private Cursor cursorFormasPagoOrden;
	static private String cliente_id;
	static private String cliente_nombre;
	static private String visita_id;
	static private int numero_cobro;
	private RadioGroup observacion_radio;
	private RadioGroup radios;
	private ListView listFacturas;
	private Context context;
	static private ArrayList<C_lista_cobros> carteras;
	static private ArrayList<C_documentos> documentos;
	static private ArrayList<C_documentos_negativos> documentos_negativos;
	static private ArrayList<C_pagos> pagos;
	static private ArrayList<C_detalle_consignaciones> detalle_consignaciones;
	static private C_encabezado encabezado;
	private ListaCobrosArrayAdapter adapter;
	static private String imei="";
	private TelephonyManager mngr;
	static private float var_acumulado_descuento_pronto_pago;
	static private float var_acumulado_total_a_cobrar;
	static private float var_acumulado_doc_negativos;
	static private String default_desc_pronto_pago;
	private AlertDialog.Builder dialogBuilder;
	private AlertDialog.Builder dialogBuilderAbono;
	private AlertDialog.Builder dialogBuilderFecha;
	private RadioButton pronto_pago_radio_no;
	private EditText obs_input;
	private RadioButton no_obs_input;
	private RadioButton si_obs_input;
	private LinearLayout obs_layout;
	private ImageView guardar_button;
	static private int pos;
	static private C_validacion validarDatos;
	static private float var_Diferencia_validar_datos;
	private View alertView;
	private View alertViewFecha;
	static private SimpleDateFormat	dateFormat;
	static private SimpleDateFormat	dateFormatPeriodo;
	static private SimpleDateFormat dateFormat2;
	static private SimpleDateFormat dateFormat3;
	private LinearLayout guia_devolucion_layout;
	private EditText guia_devolucion_input;
	private LinearLayout fecha_guia_devolucion_layout;
	private TextView fecha_guia_devolucion_input;
	private LinearLayout numero_devolucion_layout;
	private EditText numero_devolucion_input;
	private LinearLayout fecha_devolucion_layout;
	private TextView fecha_devolucion_input;
	private LinearLayout numero_solicitud_nc_layout;
	private EditText numero_solicitud_nc_input;
	private LinearLayout fecha_solicitud_nc_layout;
	private TextView fecha_solicitud_nc_input;
	private LinearLayout fecha_cancela_saldo_layout;
	private TextView fecha_cancela_saldo_input;
	private LinearLayout observaciones_abono_layout;
	private EditText observaciones_abono_input;
	private LinearLayout numero_memo_layout;
	private EditText numero_memo_input;
	private LinearLayout fecha_memo_layout;
	private TextView fecha_memo_input;
	private LinearLayout fecha_seguimiento_layout;
	private TextView fecha_seguimiento_input;
	private TextView saldo_pendiente;
	private Spinner motivo_abono_input;
	private TextView valor_a_recaudar_abono_input;
	static private C_abonos abono;
	static private ArrayList<C_cruce_docs_negativos> cruce_docs_negativos;
	static private ArrayList<C_cruce_consignaciones> cruce_consignaciones;
	static private ArrayList<C_cruce_pagos> cruce_pagos;
	static private ArrayList<String> querys;
	static private String horaIni;
	private Button efectivo_button;
	private Button transferencia_button;
	private Button cheque_al_dia_button;
	private Button cheque_postfechado_button;
	private Button cheque_tercero_al_dia_button;
	private Button cheque_tercero_postfechado_button;
	private Button doc_negativo_parcial_a_descontar_button;
	private Button consignacion_button;
	private Drawable imagecheck;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cobros_pago);
		init();
		loadData();
		llenarDocumentosIni();

		listFacturas.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				pos=arg2;
				C_lista_cobros aux=carteras.get(pos);
				String docId=aux.getDocId();
				String var_observacion="";
				obs_label.setText(getString(R.string.observaciones_del_documento)+" "+aux.getDocTipo()+" "+aux.getDocId());
				obs_layout.setVisibility(LinearLayout.VISIBLE);
				boolean isDocNegativo=(aux.getSaldoPendiente()>0)?false:true;
				if(isDocNegativo){
					for (C_documentos_negativos auxDoc: documentos_negativos){
						if(docId.equalsIgnoreCase(auxDoc.getDocId())){
							var_observacion=auxDoc.getObs();
							break;
						}
					}
				}else{
					for (C_documentos auxDoc: documentos){
						if(docId.equalsIgnoreCase(auxDoc.getDocId())){
							var_observacion=auxDoc.getObs();
							break;
						}
					}
					
				}
				if(var_observacion.trim().equalsIgnoreCase("")){
					no_obs_input.setChecked(true);
				}else{
					si_obs_input.setChecked(true);
					obs_input.setText(var_observacion);
				}
				
			}
		});
		
		guardar_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				C_lista_cobros aux=carteras.get(pos);
				String docId=aux.getDocId();
				String par_observacion=obs_input.getText().toString();
				boolean isDocNegativo=(aux.getSaldoPendiente()>0)?false:true;
				if(par_observacion.length()>0 && si_obs_input.isChecked()){
					if(isDocNegativo){
						for (C_documentos_negativos auxDoc: documentos_negativos){
							if(docId.equalsIgnoreCase(auxDoc.getDocId())){
								auxDoc.setObs(par_observacion);
								Utility.showMessage(context, getString(R.string.la_observacion_fue_guardada));
								break;
							}
						}
					}else{
						for (C_documentos auxDoc: documentos){
							if(docId.equalsIgnoreCase(auxDoc.getDocId())){
								auxDoc.setObs(par_observacion);
								Utility.showMessage(context, getString(R.string.la_observacion_fue_guardada));
								break;
							}
						}
						
					}
				}else{
					Utility.showMessage(context, getString(R.string.observacion_vacia));
				}
			}
		});
	}

	@SuppressLint("SimpleDateFormat")
	private void init(){
		context=this;
		dateFormat3=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		horaIni=dateFormat3.format(new Date());
		extras = getIntent().getExtras();
		carteras_id=extras.getStringArrayList("carteras_id");
		cliente_id=extras.getString("cliente_id");
		cliente_nombre=extras.getString("cliente_nombre");
		visita_id=extras.getString("visita_id");
		DBAdapter=new ItaloDBAdapter(this);
		pronto_pago_radio_no=(RadioButton) this.findViewById(R.id.pronto_pago_radio_no);
		obs_label=(TextView) this.findViewById(R.id.obs_label);
		radios=(RadioGroup)findViewById(R.id.radios);
		carteras=new ArrayList<C_lista_cobros>();
		documentos=new ArrayList<C_documentos>();
		documentos_negativos=new ArrayList<C_documentos_negativos>();
		pagos= new  ArrayList<C_pagos>();
		detalle_consignaciones=new ArrayList<C_detalle_consignaciones>();
		validarDatos=new C_validacion();
		dateFormat = new SimpleDateFormat("yyyyMMdd");
		dateFormatPeriodo= new SimpleDateFormat("yyMM");
		dateFormat2 = new SimpleDateFormat("MM/dd/yyyy");

		pronto_pago_input=(TextView)findViewById(R.id.pronto_pago_input);
		listFacturas=(ListView)findViewById(R.id.listFacturas);
		nombre_del_cliente_input=(TextView)findViewById(R.id.nombre_del_cliente_input);
		numero_pago_input=(TextView)findViewById(R.id.numero_pago_input);
		valor_a_recaudar_input=(TextView)findViewById(R.id.valor_a_recaudar_input);
		obs_input=(EditText)findViewById(R.id.obs_input);
		observacion_radio=(RadioGroup)findViewById(R.id.observacion_radio);
		no_obs_input=(RadioButton)findViewById(R.id.no_obs_input);
		si_obs_input=(RadioButton)findViewById(R.id.si_obs_input);
		obs_layout=(LinearLayout)findViewById(R.id.obs_layout);
		obs_layout.setVisibility(LinearLayout.GONE);
		guardar_button=(ImageView)findViewById(R.id.guardar_button);
		efectivo_button=(Button)findViewById(R.id.efectivo_button);
		transferencia_button=(Button)findViewById(R.id.transferencia_button);
		cheque_al_dia_button=(Button)findViewById(R.id.cheque_al_dia_button);
		cheque_postfechado_button=(Button)findViewById(R.id.cheque_postfechado_button);
		cheque_tercero_al_dia_button=(Button)findViewById(R.id.cheque_tercero_al_dia_button);
		cheque_tercero_postfechado_button=(Button)findViewById(R.id.cheque_tercero_postfechado_button);
		doc_negativo_parcial_a_descontar_button=(Button)findViewById(R.id.doc_negativo_parcial_a_descontar_button);
		consignacion_button=(Button)findViewById(R.id.consignacion_button);
		
		mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
		nombre_del_cliente_input.setText(cliente_id+" "+cliente_nombre);
		numero_cobro=DBAdapter.getCobrosNumeroCobro();
		numero_pago_input.setText(String.valueOf(numero_cobro));
		imei= mngr.getDeviceId();
		var_acumulado_descuento_pronto_pago = 0;
		var_acumulado_total_a_cobrar = 0;
		var_acumulado_doc_negativos=0;
		valor_documentos_input=(TextView) this.findViewById(R.id.valor_documentos_input);
		cursorTipoPago=DBAdapter.getTiposPago();
		if(cursorTipoPago.getCount()<=0){
			Utility.showMessage(context, getString(R.string.SFV_0003));
			return;
		}
		
		Drawable dr = getResources().getDrawable(R.drawable.ic_check);
		Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
		// Scale it to 50 x 50
		imagecheck = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 30, 30, true));
//		imagecheck = dr;
		
		observacion_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				if(arg1 == R.id.si_obs_input){
					obs_input.setVisibility(LinearLayout.VISIBLE);
				}else{
					obs_input.setVisibility(LinearLayout.GONE);
					obs_input.setText("");
				}
			}
		});
	
		radios.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				if(arg1 == R.id.pronto_pago_radio_si){
	      		    i = new Intent(getApplicationContext(), Cobros_Pago_Descuento_Pronto_Pago.class);
	      		    i.putExtra("documentos", new C_documentos_wrapper(documentos));
	      		    startActivityForResult(i, STATIC_INTEGER_VALUE);
				}else{
					var_acumulado_descuento_pronto_pago=0;
					pronto_pago_input.setText(Utility.formatNumber(var_acumulado_descuento_pronto_pago));
				}
			}});
		
		//handlers checks
		efectivo_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
      		    i = new Intent(getApplicationContext(), Cobros_Pago_Efectivo.class);
      		    i.putExtra("tipo_pago_id", "01");
      		    i.putExtra("pagos", new C_pago_wrapper(pagos));
    		    startActivityForResult(i, REQUEST_EFECTIVO);
			}
		});
		
		consignacion_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
      		    i = new Intent(getApplicationContext(), Cobros_Pago_Consignaciones.class);
      		    i.putStringArrayListExtra("carteras_id", carteras_id);
      		    i.putExtra("numero_cobro", String.valueOf(numero_cobro));
      		    i.putExtra("tipo_pago_id", "03");
      		    i.putExtra("pagos", new C_pago_wrapper(pagos));
      		    i.putExtra("detalle_consignaciones", new C_detalle_consignaciones_wrapper(detalle_consignaciones));
    		    startActivityForResult(i, REQUEST_CONSIGNACION);				
			}
		});
		
		transferencia_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
      		    i = new Intent(getApplicationContext(), Cobros_Pago_Transferencia.class);
      		    i.putStringArrayListExtra("carteras_id", carteras_id);
      		    i.putExtra("numero_cobro", String.valueOf(numero_cobro));
      		    i.putExtra("tipo_pago_id", "07");
      		    i.putExtra("pagos", new C_pago_wrapper(pagos));
    		    startActivityForResult(i, REQUEST_TRANSFERENCIA);				
			}
			
		});
		
		cheque_al_dia_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
      		    i = new Intent(getApplicationContext(), Cobros_Pago_Cheque.class);
      		    i.putStringArrayListExtra("carteras_id", carteras_id);
      		    i.putExtra("numero_cobro", String.valueOf(numero_cobro));
      		    i.putExtra("tipo_pago_id", "02");
      		    i.putExtra("pagos", new C_pago_wrapper(pagos));
    		    startActivityForResult(i, REQUEST_CHEQUE_DIA);
				
			}
			
		});
		
		cheque_postfechado_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
      		    i = new Intent(getApplicationContext(), Cobros_Pago_Cheque.class);
      		    i.putStringArrayListExtra("carteras_id", carteras_id);
      		    i.putExtra("numero_cobro", String.valueOf(numero_cobro));
      		    i.putExtra("tipo_pago_id", "05");
      		    i.putExtra("pagos", new C_pago_wrapper(pagos));
    		    startActivityForResult(i, REQUEST_CHEQUE_POST);			}
		});

		cheque_tercero_al_dia_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
      		    i = new Intent(getApplicationContext(), Cobros_Pago_Cheque.class);
      		    i.putStringArrayListExtra("carteras_id", carteras_id);
      		    i.putExtra("numero_cobro", String.valueOf(numero_cobro));
      		    i.putExtra("tipo_pago_id", "08");
      		    i.putExtra("pagos", new C_pago_wrapper(pagos));
    		    startActivityForResult(i, REQUEST_CHEQUE_TERCERO_DIA);				
			}
		});
		
		cheque_tercero_postfechado_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
      		    i = new Intent(getApplicationContext(), Cobros_Pago_Cheque.class);
      		    i.putStringArrayListExtra("carteras_id", carteras_id);
      		    i.putExtra("numero_cobro", String.valueOf(numero_cobro));
      		    i.putExtra("tipo_pago_id", "04");
      		    i.putExtra("pagos", new C_pago_wrapper(pagos));
    		    startActivityForResult(i, REQUEST_CHEQUE_TERCERO_POST);
			}
		});

		doc_negativo_parcial_a_descontar_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(documentos_negativos.size()>0){
	      		    i = new Intent(getApplicationContext(), Cobros_Pago_Doc_Negativo.class);
	      		    i.putExtra("numero_cobro", numero_cobro);
	      		    i.putExtra("documentos", new C_documentos_wrapper(documentos));
	      		    i.putExtra("documentos_negativos", new C_documentos_negativos_wrapper(documentos_negativos));
	    		    startActivityForResult(i, REQUEST_DOCS_NEG);
				}else{
					Utility.showMessage(context, getString(R.string.no_existen_docs_negativos));
				}
			}
		});
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.actividad_diaria_gestiones_cobros_pago, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.guardar:
				float valor_a_recaudar=var_acumulado_total_a_cobrar-var_acumulado_descuento_pronto_pago;
				for(C_pagos aux:pagos){
					valor_a_recaudar=valor_a_recaudar-aux.getValorDocumento();
				}

				if(valor_a_recaudar>0){
					dialogBuilder = new AlertDialog.Builder(context);
					dialogBuilder.setCancelable(false);
					dialogBuilder.setTitle(R.string.alerta);
					dialogBuilder.setMessage(R.string.la_sumatoria_de_las_formas_de_pago_no_es_igual_al_total);
					dialogBuilder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {}
					});
					dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							guardarCobro(true);
						}
					});
					dialogBuilder.create().show();
				}else{
					guardarCobro(true);
				}
				return true;
			case R.id.atras:
				dialogBuilder = new AlertDialog.Builder(context);
				dialogBuilder.setCancelable(false);
				dialogBuilder.setTitle(R.string.alerta);
				dialogBuilder.setMessage(R.string.desea_eliminar_salir_del_cobro);
				dialogBuilder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {}
				});
				dialogBuilder.create().show();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}
	
	private void loadData(){
		cursor=DBAdapter.getCobrosParaPagar(carteras_id);
		loadListData();
		adapter = new ListaCobrosArrayAdapter(getApplicationContext(),R.layout.item_cartera, carteras);
		listFacturas.setAdapter(adapter);
		return;
	}
	
	private void loadListData(){
		if(cursor.moveToFirst()){
			do{
				carteras.add(new C_lista_cobros(
					cursor.getString(0),
					cursor.getString(1),
					cursor.getString(2),
					cursor.getString(3),
					cursor.getInt(4),
					cursor.getFloat(5),
					cursor.getString(6),
					cursor.getString(7),
					cursor.getFloat(8),
					cursor.getString(7)));
			}while(cursor.moveToNext());
		}
		return;
	}
	
	@SuppressLint("NewApi") private void llenarDocumentosIni(){
		float valor_a_recaudar=0;
		default_desc_pronto_pago=DBAdapter.getDefault_desc_pronto_pago();
		for(C_lista_cobros aux:carteras){
			if(aux.getSaldoPendiente()>0){
				float var_valor_disponible=0;
				String var_aplicar_descuento_pronto_pago="";
				if(default_desc_pronto_pago.equalsIgnoreCase("MOSTRAR")){
					var_valor_disponible = aux.getSaldoPendiente() - aux.getValorProntoPago();
					var_aplicar_descuento_pronto_pago = "S";
					var_acumulado_descuento_pronto_pago = var_acumulado_descuento_pronto_pago + aux.getValorProntoPago();
				}else{
					var_valor_disponible = aux.getSaldoPendiente();
					var_aplicar_descuento_pronto_pago = "N";
				}
				documentos.add(new C_documentos(
						aux.getDocTipoId(),
						aux.getDocId(),
						aux.getSaldoPendiente(),
						0,
						aux.getValorProntoPago(),
						var_aplicar_descuento_pronto_pago,
						var_valor_disponible,
						0,
						aux.getDiasVencimiento(),
						aux.getFechaFacturaBD(),
						aux.getBanderaSaldo(),
						"",
						"",
						0,
						0,
						""));
				var_acumulado_total_a_cobrar = var_acumulado_total_a_cobrar + var_valor_disponible;
			}else{
				documentos_negativos.add(new C_documentos_negativos(
					aux.getDocTipoId(),
					aux.getDocId(),
					aux.getSaldoPendiente(),
					0,
					aux.getSaldoPendiente(),
					0,
					aux.getDiasVencimiento(),
					aux.getFechaFacturaBD(),
					""));
				var_acumulado_doc_negativos = var_acumulado_doc_negativos + aux.getSaldoPendiente();
				var_acumulado_total_a_cobrar = var_acumulado_total_a_cobrar + aux.getSaldoPendiente();
				doc_negativo_parcial_a_descontar_button.setCompoundDrawablesRelativeWithIntrinsicBounds(imagecheck, null, null, null);
				
			}
		}
		if(! (var_acumulado_total_a_cobrar>0)){
			var_acumulado_total_a_cobrar = 0;
		}
		valor_documentos_input.setText(Utility.formatNumber(var_acumulado_total_a_cobrar));
		pronto_pago_input.setText(Utility.formatNumber(var_acumulado_descuento_pronto_pago));
		if(var_acumulado_total_a_cobrar-var_acumulado_descuento_pronto_pago > 0){
			valor_a_recaudar=var_acumulado_total_a_cobrar-var_acumulado_descuento_pronto_pago;
		}else{
			valor_a_recaudar=0;
		}
		valor_a_recaudar_input.setText(Utility.formatNumber(valor_a_recaudar));
		encabezado = new C_encabezado();
		encabezado.setCobroId(numero_cobro);
		encabezado.setClienteId(cliente_id);
		encabezado.setMaquinaId(imei);
		return;
	}
	
	private void setValorARecaudar(){
		float valor_a_recaudar=0;
		var_acumulado_doc_negativos = 0;
		var_acumulado_total_a_cobrar = 0;

		for(C_documentos aux:documentos){
			if(aux.getAplicarDescProntoPago().equalsIgnoreCase("S")){
				var_acumulado_total_a_cobrar = var_acumulado_total_a_cobrar + aux.getSaldoAnterior()-aux.getValorDescProntoPago();
			}else{
				var_acumulado_total_a_cobrar = var_acumulado_total_a_cobrar + aux.getSaldoAnterior();
			}
		}
		for(C_documentos_negativos aux:documentos_negativos){
			var_acumulado_doc_negativos = var_acumulado_doc_negativos + aux.getValorDisponible();
			var_acumulado_total_a_cobrar = var_acumulado_total_a_cobrar + aux.getValorDisponible();
		}
		valor_a_recaudar=var_acumulado_total_a_cobrar;
		for(C_pagos aux:pagos){
			valor_a_recaudar=valor_a_recaudar-aux.getValorDocumento();
		}
		
		valor_a_recaudar_input.setText(Utility.formatNumber(valor_a_recaudar));
		return;
	}
	
	private void validar_datos(C_validacion res){
//		C_validacion res=new C_validacion();
		ArrayList<C_documentos> documentosFiltrados=new ArrayList<C_documentos>();
		float total_doc=var_acumulado_total_a_cobrar;
		float total_pagos=0;
		float total_docs_negativos=0;
		final float total_recibido;
		float total_valor_documentos=0;
		var_Diferencia_validar_datos=0;
		float var_maximo_permitido=0;
		
		for(C_pagos aux:pagos){
			total_pagos=total_pagos+aux.getValorDocumento();
		}
		
		
		for(C_documentos_negativos aux:documentos_negativos){
			total_docs_negativos=total_docs_negativos+aux.getValorDisponible();
		}
		total_recibido=total_pagos-total_docs_negativos;
//		total_recibido=total_pagos;

		if(total_recibido>0){
			if(documentos.size()>0){
	
				for(C_documentos aux:documentos){
					total_valor_documentos=total_valor_documentos+aux.getValorDisponible();
	
				}
	
				if(total_valor_documentos==0){
					for(C_documentos aux:documentos){
						if(aux.getBanderaSaldo().equalsIgnoreCase("S")){
							documentosFiltrados.add(aux);
						}
					}
					if(documentosFiltrados.size()>0){
						res.setEstado("ERROR");
						res.setMensaje("Para esta modalidad de pago, los documentos deben tener abonos.");
						return;
					}else{
	
						if(Math.abs(total_docs_negativos) > total_doc){
							ajustes_automaticos_docs_negativos(total_doc);
							validar_datos(res);
							return;
						}/*else{
							res.setEstado("ERROR");
							res.setMensaje("No se encontraron registros para aplicar el orden de cruce en las formas de pago");
	
							
							//El no de validar
							
						}*/
					}
				}/*else{*/
					var_Diferencia_validar_datos = total_doc - total_pagos;
					if(var_Diferencia_validar_datos==0){
						encabezado.setHayAbono(false);
						encabezado.setMenorValorPago(0);
						encabezado.setMayorValorPago(0);
						if(DBAdapter.exitsTipoPagoOrden()){
							res.setEstado("OK");
							res.setMensaje(null);
							return;
						}else{
							res.setEstado("ERROR");
							res.setMensaje("No se encontraron registros para aplicar el orden de cruce en las formas de pago.");
							return;
						}
					}else{
						if(var_Diferencia_validar_datos>0){
							if(DBAdapter.getClienteManejaDifMenorValor(cliente_id) !=null && DBAdapter.getClienteManejaDifMenorValor(cliente_id).equalsIgnoreCase("S")){
								var_maximo_permitido=DBAdapter.getValorMaxDiferencia();
								if(var_Diferencia_validar_datos>var_maximo_permitido){
									dialogBuilder = new AlertDialog.Builder(context);
									dialogBuilder.setTitle(R.string.alerta);
									dialogBuilder.setCancelable(false);
									dialogBuilder.setMessage("El cobro tiene un menor valor pago que supera el máximo permitido. Si continua se manejará como abono. ¿Continuar?.");
									dialogBuilder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											loadAbonoDialog(total_recibido);
										}
									});
									dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											validarDatos.setEstado("ERROR");
											validarDatos.setMensaje("El menor valor pago supera el máximo permitido.");
											guardarCobro(false);									}
									});
									dialogBuilder.create().show();
								}else{
	
									encabezado.setHayAbono(false);
									encabezado.setMenorValorPago(var_Diferencia_validar_datos);
									encabezado.setMayorValorPago(0);
									if(DBAdapter.exitsTipoPagoOrden()){
										res.setEstado("OK");
										res.setMensaje(null);
										return;
									}else{
									res.setEstado("ERROR");
										res.setMensaje("No se encontraron registros para aplicar el orden de cruce en las formas de pago.");
										return;
									}
								}
							}else{
								dialogBuilder = new AlertDialog.Builder(context);
								dialogBuilder.setCancelable(false);
								dialogBuilder.setTitle(getString(R.string.alerta));
								dialogBuilder.setMessage("Existe una diferencia por $"+Utility.formatNumber((var_acumulado_total_a_cobrar-total_recibido-total_docs_negativos))+". Indique si se trata de menor valor pago o abono");
								dialogBuilder.setPositiveButton(R.string.abono, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										loadAbonoDialog(total_recibido);
									}
								});
	
								dialogBuilder.setNegativeButton(R.string.menor_valor_pago, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										encabezado.setHayAbono(false);
										encabezado.setMenorValorPago(var_Diferencia_validar_datos);
										encabezado.setMayorValorPago(0);
										if(DBAdapter.exitsTipoPagoOrden()){
											validarDatos.setEstado("OK");
											validarDatos.setMensaje(null);
											guardarCobro(false);
											return;
										}else {
	
										}			
									}
								});
								dialogBuilder.create().show();
							}
						}else{
	
							encabezado.setHayAbono(false);
							encabezado.setMenorValorPago(0);
							encabezado.setMayorValorPago(Math.abs(var_Diferencia_validar_datos));
							if(DBAdapter.exitsTipoPagoOrden()){
								res.setEstado("OK");
								res.setMensaje(null);
								return;
							}else{
							res.setEstado("ERROR");
								res.setMensaje("No se encontraron registros para aplicar el orden de cruce en las formas de pago.");
								return;
							}						
						}
					}
			}else{
				res.setEstado("ERROR");
				res.setMensaje("No se encontraron documentos con los cuales cruzar.");
				return;
			}
		}else{
			res.setEstado("ERROR");
			res.setMensaje("No se encontraron pagos.");
			return;
		}
		return;
	}
	
	private void resetAbonosLayout(){
		guia_devolucion_layout.setVisibility(LinearLayout.GONE);
		fecha_guia_devolucion_layout.setVisibility(LinearLayout.GONE);
		numero_devolucion_layout.setVisibility(LinearLayout.GONE);
		fecha_devolucion_layout.setVisibility(LinearLayout.GONE);
		numero_solicitud_nc_layout.setVisibility(LinearLayout.GONE);
		fecha_solicitud_nc_layout.setVisibility(LinearLayout.GONE);
		fecha_cancela_saldo_layout.setVisibility(LinearLayout.GONE);
		observaciones_abono_layout.setVisibility(LinearLayout.GONE);
		numero_memo_layout.setVisibility(LinearLayout.GONE);
		fecha_memo_layout.setVisibility(LinearLayout.GONE);
		fecha_seguimiento_layout.setVisibility(LinearLayout.GONE);
		guia_devolucion_layout.setVisibility(LinearLayout.GONE);
		fecha_guia_devolucion_input.setText("");
		fecha_devolucion_input.setText("");
		fecha_solicitud_nc_input.setText("");
		fecha_cancela_saldo_input.setText("");
		fecha_memo_input.setText("");
		fecha_seguimiento_input.setText("");
		return;
	}
	
	private class checkDate implements View.OnClickListener {
		private final Dialog dialog;
		private final int type;
		private final TextView textDate;
		private final int maxDays;
	    
		public checkDate(Dialog dialog, int type, TextView textDate, int maxDays) {
	        this.dialog = dialog;
	        this.type=type;
	        this.textDate=textDate;
	        this.maxDays=maxDays;
	    }
	    
	    @Override
	    public void onClick(View v) {
			long fechaSelec=0;
			long hoy=0;
			String fecha="";
			Calendar cal= Calendar.getInstance();
			Time time = new Time();
			time.set(cal.getTime().getTime());
			fechaSelec=date_picker.getDate();
			hoy=time.toMillis(true);
			switch(this.type){
				case 0:
					if(hoy>fechaSelec){
						fecha=dateFormat2.format(fechaSelec);
						textDate.setText(fecha);
						dialog.dismiss();
					}else{
						Utility.showMessage(context, R.string.seleccione_una_fecha_menor);
						textDate.requestFocus();
					}
					break;
				case 1:
					if(hoy>=fechaSelec){
						fecha=dateFormat2.format(fechaSelec);
						textDate.setText(fecha);
						dialog.dismiss();
					}else{
						Utility.showMessage(context, R.string.seleccione_una_fecha_actual_o_menor);
						textDate.requestFocus();
					}
					break;
				case 2:
					if(hoy<fechaSelec){
						fecha=dateFormat2.format(fechaSelec);
						textDate.setText(fecha);
						dialog.dismiss();
					}else{
						Utility.showMessage(context, R.string.seleccione_una_fecha_mayor);
						textDate.requestFocus();
					}
					break;
				case 3:
					if(maxDays==0){
						if(hoy<=fechaSelec){
							fecha=dateFormat2.format(fechaSelec);
							textDate.setText(fecha);
							dialog.dismiss();
						}else{
							Utility.showMessage(context, getString(R.string.seleccione_una_fecha_actual_o_mayor));
							textDate.requestFocus();
						}
					}else{
						if((hoy+86400000)<= fechaSelec && fechaSelec<= (hoy+(maxDays*86400000))){
							fecha=dateFormat2.format(fechaSelec);
							textDate.setText(fecha);
							dialog.dismiss();
						}else{
							Utility.showMessage(context, "Debe seleccionar una fecha mayor en un dia a actual y menor que "+maxDays+" dias.");
							textDate.requestFocus();
						}
					}
					break;
			}
	    }
	}
	
	private class checkDateSegCancelaSaldo implements View.OnClickListener {
		private final Dialog dialog;
		private final TextView textDate;
		private final TextView textDateCancela;
		private final int maxDays;
		private final int isVisible;
	    
		public checkDateSegCancelaSaldo(Dialog dialog, TextView textDate, TextView textDateCancela, int maxDays,int isVisible) {
	        this.dialog = dialog;
	        this.textDate=textDate;
	        this.textDateCancela=textDateCancela;
	        this.maxDays=maxDays;
	        this.isVisible=isVisible;
	    }
	    
	    @Override
	    public void onClick(View v) {
	    	boolean cancelaSaldoVisble=(isVisible==LinearLayout.VISIBLE)?true:false;
			long fechaSelec=0;
			String fecha="";
			Calendar cal= Calendar.getInstance();
			Time time = new Time();
//			time.set(cal.getTime().getTime());
			fechaSelec=date_picker.getDate();
			int fechaHoy=Integer.valueOf(dateFormat.format(cal.getTime()));
			int fechaSeleccinada=Integer.valueOf(dateFormat.format(fechaSelec));
			int fechaCancelaSaldo=0;

			if(cancelaSaldoVisble){
				if(textDateCancela.getText().toString().equals("")){
					Utility.showMessage(context,R.string.la_fecha_cancela_saldo_no_puede_estar_vacia);
					dialog.dismiss();
					return;
				}else{
					try {
						fechaCancelaSaldo=Integer.valueOf(dateFormat.format(dateFormat2.parse(textDateCancela.getText().toString())));
					} catch (ParseException e) {
						Utility.showMessage(context,R.string.hubo_un_error_al_capturar_la_fecha);
						dialog.dismiss();
						return;
					}
				}
			}
			
			if(maxDays!=0){
				Calendar c = Calendar.getInstance();
				int aux=0;
				try {
					c.setTime(dateFormat.parse(String.valueOf(fechaCancelaSaldo)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				c.add(Calendar.DATE, maxDays);
				aux=Integer.parseInt(dateFormat.format(c.getTime()));
				if( fechaCancelaSaldo <= fechaSeleccinada 
//					&& fechaSeleccinada< (fechaCancelaSaldo+maxDays)){
					&& fechaSeleccinada <= aux){
					fecha=dateFormat2.format(fechaSelec);
					textDate.setText(fecha);
					dialog.dismiss();
				}else{
					Utility.showMessage(context, getString(R.string.fecha_seguimiento_mensaje_1)+" "+maxDays+" "+getString(R.string.fecha_seguimiento_mensaje_2));
					textDate.requestFocus();
				}
			}else{
				if( fechaHoy < fechaSeleccinada){
					fecha=dateFormat2.format(fechaSelec);
					textDate.setText(fecha);
					dialog.dismiss();
				}else{
					Utility.showMessage(context, "Debe seleccionar una fecha mayor en un dia a actual.");
					textDate.requestFocus();
				}
			}
	    }
	}
	
	private void loadMotivosAbono(){
		cursorAbono=DBAdapter.getMotivosAbonos();
		int i=1;
        String strings[] = new String[cursorAbono.getCount()+1];
        strings[0] = getString(R.string.seleccione_un_motivo);
        if(cursorAbono.moveToFirst()){
			do{
	        	strings[i] = cursorAbono.getString(2);
	        	i++;
			}while(cursorAbono.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		motivo_abono_input.setAdapter(adapter);
		return;
	}

	@SuppressLint("InflateParams") private void loadAbonoDialog(float valorRecaudar){
		abono=null;
		alertView=getLayoutInflater().inflate(R.layout.abono_layout, null);
		dialogBuilderAbono = new AlertDialog.Builder(context);
		dialogBuilderAbono.setCancelable(false);
		dialogBuilderAbono.setTitle(getString(R.string.abono));
		dialogBuilderAbono.setView(alertView);
		guia_devolucion_layout=(LinearLayout)alertView.findViewById(R.id.guia_devolucion_layout);
		fecha_guia_devolucion_layout=(LinearLayout)alertView.findViewById(R.id.fecha_guia_devolucion_layout);
		numero_devolucion_layout=(LinearLayout)alertView.findViewById(R.id.numero_devolucion_layout);
		fecha_devolucion_layout=(LinearLayout)alertView.findViewById(R.id.fecha_devolucion_layout);
		numero_solicitud_nc_layout=(LinearLayout)alertView.findViewById(R.id.numero_solicitud_nc_layout);
		fecha_solicitud_nc_layout=(LinearLayout)alertView.findViewById(R.id.fecha_solicitud_nc_layout);
		fecha_cancela_saldo_layout=(LinearLayout)alertView.findViewById(R.id.fecha_cancela_saldo_layout);
		observaciones_abono_layout=(LinearLayout)alertView.findViewById(R.id.observaciones_abono_layout);
		numero_memo_layout=(LinearLayout)alertView.findViewById(R.id.numero_memo_layout);
		fecha_memo_layout=(LinearLayout)alertView.findViewById(R.id.fecha_memo_layout);
		fecha_seguimiento_layout=(LinearLayout)alertView.findViewById(R.id.fecha_seguimiento_layout);
		motivo_abono_input=(Spinner)alertView.findViewById(R.id.motivo_abono_input);
		guia_devolucion_input=(EditText)alertView.findViewById(R.id.guia_devolucion_input);
		fecha_guia_devolucion_input=(TextView)alertView.findViewById(R.id.fecha_guia_devolucion_input);
		numero_devolucion_input=(EditText)alertView.findViewById(R.id.numero_devolucion_input);
		fecha_devolucion_input=(TextView)alertView.findViewById(R.id.fecha_devolucion_input);
		numero_solicitud_nc_input=(EditText)alertView.findViewById(R.id.numero_solicitud_nc_input);
		fecha_solicitud_nc_input=(TextView)alertView.findViewById(R.id.fecha_solicitud_nc_input);
		fecha_cancela_saldo_input=(TextView)alertView.findViewById(R.id.fecha_cancela_saldo_input);
		observaciones_abono_input=(EditText)alertView.findViewById(R.id.observaciones_abono_input);
		numero_memo_input=(EditText)alertView.findViewById(R.id.numero_memo_input);
		fecha_memo_input=(TextView)alertView.findViewById(R.id.fecha_memo_input);
		fecha_seguimiento_input=(TextView)alertView.findViewById(R.id.fecha_seguimiento_input);
		saldo_pendiente=(TextView)alertView.findViewById(R.id.saldo_pendiente);
		valor_a_recaudar_abono_input=(TextView)alertView.findViewById(R.id.valor_a_recaudar_abono_input);
		saldo_pendiente.setText(Utility.formatNumber(var_acumulado_total_a_cobrar-valorRecaudar));
		valor_a_recaudar_abono_input.setText(Utility.formatNumber(valorRecaudar));
		resetAbonosLayout();
		loadMotivosAbono();
		motivo_abono_input.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				String idSelected="";
				String var_capturar_datos_guia="";
				if(cursorAbono.moveToPosition(arg2-1)){
					idSelected=cursorAbono.getString(1);
				}
				resetAbonosLayout();
				if(cursorAbono.moveToFirst()){
					do{
						if(cursorAbono.getString(1).equalsIgnoreCase(idSelected)){
							if(cursorAbono.getString(cursorAbono.getColumnIndex("pedir_guia_devolucion")).equalsIgnoreCase("S") ||
							   cursorAbono.getString(cursorAbono.getColumnIndex("pedir_fecha_guia_dev")).equalsIgnoreCase("S")){
								var_capturar_datos_guia=DBAdapter.distritoCapturarGuiaAbono(cliente_id);
								if(var_capturar_datos_guia.equalsIgnoreCase("")){
									guia_devolucion_layout.setVisibility(LinearLayout.VISIBLE);
									Log.e("info","Error Abono");
									break;
								}
							}
							if(cursorAbono.getString(cursorAbono.getColumnIndex("pedir_guia_devolucion")).equalsIgnoreCase("S")){
								if(var_capturar_datos_guia.equalsIgnoreCase("S")){
									guia_devolucion_layout.setVisibility(LinearLayout.VISIBLE);
								}
							}
							if(cursorAbono.getString(cursorAbono.getColumnIndex("pedir_fecha_guia_dev")).equalsIgnoreCase("S")){
								if(var_capturar_datos_guia.equalsIgnoreCase("S")){
									fecha_guia_devolucion_layout.setVisibility(LinearLayout.VISIBLE);
								}
							}
							if(cursorAbono.getString(cursorAbono.getColumnIndex("pedir_num_devolucion")).equalsIgnoreCase("S")){
								numero_devolucion_layout.setVisibility(LinearLayout.VISIBLE);
							}
							if(cursorAbono.getString(cursorAbono.getColumnIndex("pedir_fecha_devolucion")).equalsIgnoreCase("S")){
								fecha_devolucion_layout.setVisibility(LinearLayout.VISIBLE);
							}
							if(cursorAbono.getString(cursorAbono.getColumnIndex("pedir_num_solic_nc")).equalsIgnoreCase("S")){
								numero_solicitud_nc_layout.setVisibility(LinearLayout.VISIBLE);
							}
							if(cursorAbono.getString(cursorAbono.getColumnIndex("pedir_fec_cancela_saldo")).equalsIgnoreCase("S")){
								fecha_cancela_saldo_layout.setVisibility(LinearLayout.VISIBLE);
							}
							if(cursorAbono.getString(cursorAbono.getColumnIndex("pedir_observaciones")).equalsIgnoreCase("S")){
								observaciones_abono_layout.setVisibility(LinearLayout.VISIBLE);
							}
							if(cursorAbono.getString(cursorAbono.getColumnIndex("pedir_numero_memo")).equalsIgnoreCase("S")){
								numero_memo_layout.setVisibility(LinearLayout.VISIBLE);
							}
							if(cursorAbono.getString(cursorAbono.getColumnIndex("pedir_fecha_memo")).equalsIgnoreCase("S")){
								fecha_memo_layout.setVisibility(LinearLayout.VISIBLE);
							}
							if(cursorAbono.getString(cursorAbono.getColumnIndex("pedir_fecha_seguimiento")).equalsIgnoreCase("S")){
								fecha_seguimiento_layout.setVisibility(LinearLayout.VISIBLE);
							}

							/*														if(cursorAbono.getString(cursorAbono.getColumnIndex("dias_limite_fech_segui")).equalsIgnoreCase("S")){
								guia_devolucion_layout.setVisibility(LinearLayout.VISIBLE);
							}*/
							break;
						}
					}while(cursorAbono.moveToNext());
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		fecha_guia_devolucion_input.setInputType(EditorInfo.TYPE_NULL);
		fecha_devolucion_input.setInputType(EditorInfo.TYPE_NULL);
		fecha_solicitud_nc_input.setInputType(EditorInfo.TYPE_NULL);
		fecha_cancela_saldo_input.setInputType(EditorInfo.TYPE_NULL);
		fecha_memo_input.setInputType(EditorInfo.TYPE_NULL);
		fecha_seguimiento_input.setInputType(EditorInfo.TYPE_NULL);
		
		fecha_guia_devolucion_input.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == MotionEvent.ACTION_DOWN){
					alertViewFecha = getLayoutInflater().inflate(R.layout.date_picker, null);
					dialogBuilderFecha = new AlertDialog.Builder(context);
					dialogBuilderFecha.setTitle(R.string.modificar_fecha);
					dialogBuilderFecha.setCancelable(false);
					dialogBuilderFecha.setView(alertViewFecha);
					date_picker=(CalendarView)alertViewFecha.findViewById(R.id.date_picker);
					dialogBuilderFecha.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					});
					dialogBuilderFecha.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					});

					alertDialogFecha=dialogBuilderFecha.create();
					alertDialogFecha.show();
					final Button modifyButton = alertDialogFecha.getButton(DialogInterface.BUTTON_POSITIVE);
					modifyButton.setOnClickListener(new checkDate(alertDialogFecha, 0, fecha_guia_devolucion_input, 0));
				}
				return false;
           	}
		});
		
		fecha_devolucion_input.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == MotionEvent.ACTION_DOWN){
					alertViewFecha = getLayoutInflater().inflate(R.layout.date_picker, null);
					dialogBuilderFecha = new AlertDialog.Builder(context);
					dialogBuilderFecha.setCancelable(false);
					dialogBuilderFecha.setTitle(R.string.modificar_fecha);
					dialogBuilderFecha.setView(alertViewFecha);
					date_picker=(CalendarView)alertViewFecha.findViewById(R.id.date_picker);
					dialogBuilderFecha.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					});
					dialogBuilderFecha.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					});

					alertDialogFecha=dialogBuilderFecha.create();
					alertDialogFecha.show();
					final Button modifyButton = alertDialogFecha.getButton(DialogInterface.BUTTON_POSITIVE);
					modifyButton.setOnClickListener(new checkDate(alertDialogFecha, 1, fecha_devolucion_input, 0));
				}
				return false;
           	}
		});

		fecha_solicitud_nc_input.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == MotionEvent.ACTION_DOWN){
					alertViewFecha = getLayoutInflater().inflate(R.layout.date_picker, null);
					dialogBuilderFecha = new AlertDialog.Builder(context);
					dialogBuilderFecha.setCancelable(false);
					dialogBuilderFecha.setTitle(R.string.modificar_fecha);
					dialogBuilderFecha.setView(alertViewFecha);
					date_picker=(CalendarView)alertViewFecha.findViewById(R.id.date_picker);
					dialogBuilderFecha.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					});
					dialogBuilderFecha.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					});

					alertDialogFecha=dialogBuilderFecha.create();
					alertDialogFecha.show();
					final Button modifyButton = alertDialogFecha.getButton(DialogInterface.BUTTON_POSITIVE);
					modifyButton.setOnClickListener(new checkDate(alertDialogFecha, 1, fecha_solicitud_nc_input, 0));
				}
				return false;
           	}
		});

		fecha_cancela_saldo_input.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == MotionEvent.ACTION_DOWN){
					alertViewFecha = getLayoutInflater().inflate(R.layout.date_picker, null);
					dialogBuilderFecha = new AlertDialog.Builder(context);
					dialogBuilderFecha.setCancelable(false);
					dialogBuilderFecha.setTitle(R.string.modificar_fecha);
					dialogBuilderFecha.setView(alertViewFecha);
					date_picker=(CalendarView)alertViewFecha.findViewById(R.id.date_picker);
					dialogBuilderFecha.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					});
					dialogBuilderFecha.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					});

					alertDialogFecha=dialogBuilderFecha.create();
					alertDialogFecha.show();
					final Button modifyButton = alertDialogFecha.getButton(DialogInterface.BUTTON_POSITIVE);
					modifyButton.setOnClickListener(new checkDate(alertDialogFecha, 2, fecha_cancela_saldo_input, 0));
				}
				return false;
           	}
		});

		fecha_memo_input.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint({ "ClickableViewAccessibility", "InflateParams" }) @Override
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == MotionEvent.ACTION_DOWN){
					alertViewFecha = getLayoutInflater().inflate(R.layout.date_picker, null);
					dialogBuilderFecha = new AlertDialog.Builder(context);
					dialogBuilderFecha.setCancelable(false);
					dialogBuilderFecha.setTitle(R.string.modificar_fecha);
					dialogBuilderFecha.setView(alertViewFecha);
					date_picker=(CalendarView)alertViewFecha.findViewById(R.id.date_picker);
					dialogBuilderFecha.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					});
					dialogBuilderFecha.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					});
					alertDialogFecha=dialogBuilderFecha.create();
					alertDialogFecha.show();
					final Button modifyButton = alertDialogFecha.getButton(DialogInterface.BUTTON_POSITIVE);
					modifyButton.setOnClickListener(new checkDate(alertDialogFecha, 0, fecha_memo_input, 0));
				}
				return false;
           	}
		});

		fecha_seguimiento_input.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == MotionEvent.ACTION_DOWN){
					alertViewFecha = getLayoutInflater().inflate(R.layout.date_picker, null);
					dialogBuilderFecha = new AlertDialog.Builder(context);
					dialogBuilderFecha.setCancelable(false);
					dialogBuilderFecha.setTitle(R.string.modificar_fecha);
					dialogBuilderFecha.setView(alertViewFecha);
					date_picker=(CalendarView)alertViewFecha.findViewById(R.id.date_picker);
					dialogBuilderFecha.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					});
					dialogBuilderFecha.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					});

					alertDialogFecha=dialogBuilderFecha.create();
					alertDialogFecha.show();
					final Button modifyButton = alertDialogFecha.getButton(DialogInterface.BUTTON_POSITIVE);
					if(motivo_abono_input.getSelectedItemPosition()!=0){
						if(cursorAbono.moveToPosition(motivo_abono_input.getSelectedItemPosition()-1)){
							if(cursorAbono.getString(cursorAbono.getColumnIndex("pedir_fec_cancela_saldo")).equalsIgnoreCase("S")){
								modifyButton.setOnClickListener(new checkDateSegCancelaSaldo(alertDialogFecha, fecha_seguimiento_input, fecha_cancela_saldo_input, cursorAbono.getInt(cursorAbono.getColumnIndex("dias_limite_fech_segui")),fecha_cancela_saldo_layout.getVisibility()));
							}else{
								modifyButton.setOnClickListener(new checkDate(alertDialogFecha, 3, fecha_seguimiento_input, 0));
							}
						}else{
							modifyButton.setOnClickListener(new checkDate(alertDialogFecha, 3, fecha_seguimiento_input, 0));
						}
					}else{
						Utility.showMessage(context, R.string.seleccione_al_menos_un_elemento_abono);
					}
				}
				return false;
           	}
		});

		dialogBuilderAbono.setPositiveButton(getString(R.string.guardar), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		dialogBuilderAbono.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				validarDatos.setEstado("ERROR");
				validarDatos.setMensaje("El proceso de abono no se finalizó");
				guardarCobro(false);
			}
		});
		alertDialogAbono=dialogBuilderAbono.create();
		alertDialogAbono.show();
		final Button modifyButton = alertDialogAbono.getButton(DialogInterface.BUTTON_POSITIVE);
		modifyButton.setOnClickListener(new GuardarAbono(alertDialogAbono));
		return;
	}
	
	private class GuardarAbono implements View.OnClickListener {
		private final Dialog dialog;
		private int maxDays;
	    
		public GuardarAbono(Dialog dialog) {
	        this.dialog = dialog;
	    }
	    
	    @Override
	    public void onClick(View v) {
	    	boolean continuar=true;
			String motivo_abono_id="";
			String numero_guia_devolucion="";
			String fecha_guia_devolucion="";
			String numero_devolucion="";
			String fecha_devolucion="";
			String numero_solicitud_nc="";
			String fecha_solicitud_nc="";
			String fecha_cancela_saldo="";
			String observaciones_abono="";
			String numero_memorando="";
			String fecha_memorando="";
			String fecha_seguimiento="";
	    	
			if(motivo_abono_input.getSelectedItemPosition()!=0){
				if(cursorAbono.moveToPosition(motivo_abono_input.getSelectedItemPosition()-1)){
					motivo_abono_id=cursorAbono.getString(1);
					maxDays=cursorAbono.getInt(cursorAbono.getColumnIndex("dias_limite_fech_segui"));
				}else{
					continuar=false;
				}
			}else{
				continuar=false;
				Utility.showMessage(context, R.string.seleccione_al_menos_un_elemento_abono);
			}
			
	    	if(guia_devolucion_layout.getVisibility() == LinearLayout.VISIBLE && continuar){
	    		if(guia_devolucion_input.getText().toString().length()>0){
	    			numero_guia_devolucion=guia_devolucion_input.getText().toString();
	    		}else{
	    			continuar=false;
	    			Utility.showMessage(context, "Para continuar, debe ingresar el número de guía de la devolución.");
	    			guia_devolucion_input.requestFocus();
	    		}
	    	}

	    	if(fecha_guia_devolucion_layout.getVisibility() == LinearLayout.VISIBLE && continuar){
	    		if(fecha_guia_devolucion_input.getText().toString().length()>0){
	    			fecha_guia_devolucion=fecha_guia_devolucion_input.getText().toString();
	    		}else{
	    			continuar=false;
	    			Utility.showMessage(context, "Para continuar, debe ingresar la fecha de la guía de devolución.");
	    			fecha_guia_devolucion_input.requestFocus();
	    		}
	    	}else{
	    		fecha_guia_devolucion="1900-01-01";
	    	}

	    	if(numero_devolucion_layout.getVisibility() == LinearLayout.VISIBLE && continuar){
	    		if(numero_devolucion_input.getText().toString().length()>0){
	    			numero_devolucion=numero_devolucion_input.getText().toString();
	    		}else{
	    			continuar=false;
	    			Utility.showMessage(context, "Para continuar, debe ingresar el número de devolución.");
	    			numero_devolucion_input.requestFocus();
	    		}
	    	}

	    	if(fecha_devolucion_layout.getVisibility() == LinearLayout.VISIBLE && continuar){
	    		if(fecha_devolucion_input.getText().toString().length()>0){
	    			fecha_devolucion=fecha_devolucion_input.getText().toString();
	    		}else{
	    			continuar=false;
	    			Utility.showMessage(context, "Para continuar, debe ingresar la fecha de devolución.");
	    			fecha_devolucion_input.requestFocus();
	    		}
	    	}else{
	    		fecha_devolucion="1900-01-01";
	    	}

	    	if(numero_solicitud_nc_layout.getVisibility() == LinearLayout.VISIBLE && continuar){
	    		if(numero_solicitud_nc_input.getText().toString().length()>0){
	    			numero_solicitud_nc=numero_solicitud_nc_input.getText().toString();
	    		}else{
	    			continuar=false;
	    			Utility.showMessage(context, "Para continuar, debe ingresar el número de solicitud NC.");
	    			numero_solicitud_nc_input.requestFocus();
	    		}
	    	}

	    	if(fecha_solicitud_nc_layout.getVisibility() == LinearLayout.VISIBLE && continuar){
	    		if(fecha_solicitud_nc_input.getText().toString().length()>0){
	    			fecha_solicitud_nc=fecha_solicitud_nc_input.getText().toString();
	    		}else{
	    			continuar=false;
	    			Utility.showMessage(context, "Para continuar, debe ingresar la fecha de solicitud NC.");
	    			fecha_solicitud_nc_input.requestFocus();
	    		}
	    	}else{
	    		fecha_solicitud_nc="1900-01-01";
	    	}	    	

	    	if(fecha_cancela_saldo_layout.getVisibility() == LinearLayout.VISIBLE && continuar){
	    		if(fecha_cancela_saldo_input.getText().toString().length()>0){
	    			fecha_cancela_saldo=fecha_cancela_saldo_input.getText().toString();
	    		}else{
	    			continuar=false;
	    			Utility.showMessage(context, "Para continuar, debe ingresar la fecha de cancelación del saldo.");
	    			fecha_cancela_saldo_input.requestFocus();
	    		}
	    	}else{
	    		fecha_cancela_saldo="1900-01-01";
	    	}	    	
	    	
	    	if(observaciones_abono_layout.getVisibility() == LinearLayout.VISIBLE && continuar){
	    		if(cursorAbono.getString(cursorAbono.getColumnIndex("observacion_obligatoria")).equalsIgnoreCase("S")){
		    		if(observaciones_abono_input.getText().toString().length()>cursorAbono.getInt(cursorAbono.getColumnIndex("longitud_minima_obs"))){
		    			observaciones_abono=observaciones_abono_input.getText().toString();
		    		}else{
		    			continuar=false;
		    			if(cursorAbono.getInt(cursorAbono.getColumnIndex("longitud_minima_obs"))>0){
		    				Utility.showMessage(context, "Las observación del abono debe contener mínimo "+cursorAbono.getInt(cursorAbono.getColumnIndex("longitud_minima_obs"))+" caracteres.");
		    			}else{
			    			Utility.showMessage(context, "Para continuar, debe ingresar las observaciones del abono.");
		    			}
		    			
		    			observaciones_abono_input.requestFocus();
		    		}
	    		}else{
	    			observaciones_abono=observaciones_abono_input.getText().toString();
	    		}
	    		
	    	}
	    	
	    	if(numero_memo_layout.getVisibility() == LinearLayout.VISIBLE && continuar){
	    		if(numero_memo_input.getText().toString().length()>0){
	    			numero_memorando=numero_memo_input.getText().toString();
	    		}else{
	    			continuar=false;
	    			Utility.showMessage(context, "Para continuar, debe ingresar el número de memorando.");
	    			numero_memo_input.requestFocus();
	    		}
	    	}

	    	if(fecha_memo_layout.getVisibility() == LinearLayout.VISIBLE && continuar){
	    		if(fecha_memo_input.getText().toString().length()>0){
	    			fecha_memorando=fecha_memo_input.getText().toString();
	    		}else{
	    			continuar=false;
	    			Utility.showMessage(context, "Para continuar, debe ingresar la fecha de devolución.");
	    			fecha_memo_input.requestFocus();
	    		}
	    	}else{
	    		fecha_memorando="1900-01-01";
	    	}	

	    	if(fecha_seguimiento_layout.getVisibility() == LinearLayout.VISIBLE && continuar){
	    		if(fecha_seguimiento_input.getText().toString().length()>0){
	    			if(fecha_cancela_saldo_layout.getVisibility() == LinearLayout.VISIBLE){
	    				try{
		    				int fechaCancela=Integer.valueOf(dateFormat.format(dateFormat2.parse(fecha_cancela_saldo_input.getText().toString())));
		    				int fechaSeguimiento=Integer.valueOf(dateFormat.format(dateFormat2.parse(fecha_seguimiento_input.getText().toString())));
	    					Calendar c = Calendar.getInstance();
	    					int aux=0;
	    					try {
	    						c.setTime(dateFormat.parse(String.valueOf(fechaCancela)));
	    					} catch (ParseException e) {
	    						e.printStackTrace();
	    					}
	    					c.add(Calendar.DATE, maxDays);
	    					aux=Integer.parseInt(dateFormat.format(c.getTime()));
//		    				if(fechaCancela <= fechaSeguimiento && fechaSeguimiento <= fechaCancela+maxDays){
		    				if(fechaCancela <= fechaSeguimiento && fechaSeguimiento <= aux){
		    					
		    					
			    				fecha_seguimiento=fecha_seguimiento_input.getText().toString();	
		    				}else{
		    					continuar=false;
		    					Utility.showMessage(context, getString(R.string.fecha_seguimiento_mensaje_1)+" "+maxDays+" "+getString(R.string.fecha_seguimiento_mensaje_2));
		    				}
	    				}catch(Exception e){
	    					continuar=false;
	    					Utility.showMessage(context, R.string.hubo_un_error_al_guardar_el_cobro);
	    				}
	    			}else{
	    				fecha_seguimiento=fecha_seguimiento_input.getText().toString();	
	    			}	    			
	    		}else{
	    			continuar=false;
	    			Utility.showMessage(context, "Para continuar, debe ingresar la fecha de seguimiento.");
	    			fecha_seguimiento_input.requestFocus();
	    		}
	    	}else{
	    		fecha_seguimiento="1900-01-01";
	    	}	
	    	
			if(continuar){
				abono=new C_abonos(
						motivo_abono_id,
						numero_guia_devolucion,
						fecha_guia_devolucion,
						numero_devolucion,
						fecha_devolucion,
						numero_solicitud_nc,
						fecha_solicitud_nc,
						fecha_cancela_saldo,
						observaciones_abono,
						numero_memorando,
						fecha_memorando,
						fecha_seguimiento);

				if(abono!=null){
					encabezado.setHayAbono(true);
					encabezado.setMenorValorPago(0);
					encabezado.setMayorValorPago(0);
					if(DBAdapter.exitsTipoPagoOrden()){
						validarDatos.setEstado("OK");
						validarDatos.setMensaje(null);
						guardarCobro(false);
						dialog.dismiss();
						return;
					}else{
						validarDatos.setEstado("ERROR");
						validarDatos.setMensaje("No se encontraron registros para aplicar el orden de cruce en las formas de pago");
						guardarCobro(false);
						dialog.dismiss();
						return;
					}
				}else{
					validarDatos.setEstado("ERROR");
					validarDatos.setMensaje("El proceso de abono no se finalizó");
					guardarCobro(false);
					dialog.dismiss();
					return;
				}
			}
	    }
	}
	
	
	public void ajustes_automaticos_docs_negativos(float par_valor_total_documentos){
		float var_diferencia=0;
		float var_total_docs_negativos=0;
		C_documentos_negativos auxDocNeg=null;
		Collections.sort(documentos_negativos, new CustomComparator());
		for(C_documentos_negativos aux:documentos_negativos){
			var_total_docs_negativos=var_total_docs_negativos+Math.abs(aux.getValorDisponible());
			
		}
		var_diferencia=var_total_docs_negativos-par_valor_total_documentos;
		if(var_diferencia>0){
			auxDocNeg=documentos_negativos.get(0);
			if(var_diferencia>Math.abs(auxDocNeg.getValorDisponible())){
				documentos_negativos.remove(0);
				ajustes_automaticos_docs_negativos(par_valor_total_documentos);
				return;
			}else{
				auxDocNeg.setValorDisponible(auxDocNeg.getValorDisponible()+var_diferencia);
				return;
			}
		}else{
			return;
		}
	}
	
	public class CustomComparator implements Comparator<C_documentos_negativos> {
	    @Override
	    public int compare(C_documentos_negativos o1, C_documentos_negativos o2) {
        	String date0="";
        	String date1="";
        	String [] auxDate;
        	try{
        		auxDate=o1.getFechaDoc().split("/");
        		date0=auxDate[2]+auxDate[0]+auxDate[1];
        	}catch(Exception e){}
        	try{
        		auxDate=o2.getFechaDoc().split("/");
        		date1=auxDate[2]+auxDate[0]+auxDate[1];
        	}catch(Exception e){}
        	return date0.compareTo(date1);
	    }
	}
	
	public synchronized void pause() {
	}

	public synchronized void go() {
	    notify();
	}
	
	@SuppressLint("NewApi") @Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
		super.onActivityResult(requestCode, resultCode, data); 
		switch(requestCode) { 
			case STATIC_INTEGER_VALUE :	
				if (resultCode == Activity.RESULT_OK) { 
					if(data.getSerializableExtra("pagos")!=null){
						C_pago_wrapper pagosWrapper=(C_pago_wrapper) data.getSerializableExtra("pagos");
						pagos=pagosWrapper.getItems();
					}
	
					if(data.getSerializableExtra("documentos_negativos")!=null){
						C_documentos_negativos_wrapper documentosNegativosWrapper=(C_documentos_negativos_wrapper) data.getSerializableExtra("documentos_negativos");
						documentos_negativos=documentosNegativosWrapper.getItems();
					}
		    	  
					if(data.getSerializableExtra("documentos")!=null){
						C_documentos_wrapper documentosWrapper=(C_documentos_wrapper) data.getSerializableExtra("documentos");
						documentos=documentosWrapper.getItems();
					}
	
					if(data.getSerializableExtra("detalle_consignaciones")!=null){
						C_detalle_consignaciones_wrapper documentosWrapper=(C_detalle_consignaciones_wrapper) data.getSerializableExtra("detalle_consignaciones");
						detalle_consignaciones=documentosWrapper.getItems();
					}
					
					if(data.getExtras() != null && data.getExtras().getFloat("var_acumulado_descuento_pronto_pago")!=0){
						var_acumulado_descuento_pronto_pago=data.getExtras().getFloat("var_acumulado_descuento_pronto_pago");
						pronto_pago_input.setText(Utility.formatNumber(var_acumulado_descuento_pronto_pago));
					}else{
						if(var_acumulado_descuento_pronto_pago==0){
							pronto_pago_radio_no.setChecked(true);
						}
					}
					setValorARecaudar();
				}
				setValorARecaudar();
				break;
			case REQUEST_EFECTIVO:
				if(resultCode == Activity.RESULT_OK){
					C_pago_wrapper pagosWrapper=(C_pago_wrapper) data.getSerializableExtra("pagos");
					pagos=pagosWrapper.getItems();
					if(data.getExtras().getInt("removeCheck")!=0){
				//		Log.i("info","entro");
						efectivo_button.setCompoundDrawablesRelativeWithIntrinsicBounds(imagecheck, null, null, null);
					}else{
						efectivo_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
					}
				}
				break;
			case REQUEST_TRANSFERENCIA:
				if(resultCode == Activity.RESULT_OK){
					C_pago_wrapper pagosWrapper=(C_pago_wrapper) data.getSerializableExtra("pagos");
					pagos=pagosWrapper.getItems();
					if(data.getExtras().getInt("removeCheck")!=0){
//						transferencia_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
						transferencia_button.setCompoundDrawablesRelativeWithIntrinsicBounds(imagecheck, null, null, null);
					}else{
						transferencia_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
					}
				}
				break;
			case REQUEST_CHEQUE_DIA:
				if(resultCode == Activity.RESULT_OK){
					C_pago_wrapper pagosWrapper=(C_pago_wrapper) data.getSerializableExtra("pagos");
					pagos=pagosWrapper.getItems();
					if(data.getExtras().getInt("removeCheck")!=0){
						cheque_al_dia_button.setCompoundDrawablesRelativeWithIntrinsicBounds(imagecheck, null, null, null);
//						cheque_al_dia_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
					}else{
						cheque_al_dia_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
					}
				}
				break;
			case REQUEST_CHEQUE_POST:
				if(resultCode == Activity.RESULT_OK){
					C_pago_wrapper pagosWrapper=(C_pago_wrapper) data.getSerializableExtra("pagos");
					pagos=pagosWrapper.getItems();
					if(data.getExtras().getInt("removeCheck")!=0){
//						cheque_postfechado_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
						cheque_postfechado_button.setCompoundDrawablesRelativeWithIntrinsicBounds(imagecheck, null, null, null);
					}else{
						cheque_postfechado_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
					}
				}
				break;
			case REQUEST_CHEQUE_TERCERO_DIA:
				if(resultCode == Activity.RESULT_OK){
					C_pago_wrapper pagosWrapper=(C_pago_wrapper) data.getSerializableExtra("pagos");
					pagos=pagosWrapper.getItems();
					if(data.getExtras().getInt("removeCheck")!=0){
//						cheque_tercero_al_dia_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
						cheque_tercero_al_dia_button.setCompoundDrawablesRelativeWithIntrinsicBounds(imagecheck, null, null, null);
					}else{
						cheque_tercero_al_dia_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
					}
				}
				break;
			case REQUEST_CHEQUE_TERCERO_POST:
				if(resultCode == Activity.RESULT_OK){
					C_pago_wrapper pagosWrapper=(C_pago_wrapper) data.getSerializableExtra("pagos");
					pagos=pagosWrapper.getItems();
					if(data.getExtras().getInt("removeCheck")!=0){
//						cheque_tercero_postfechado_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
						cheque_tercero_postfechado_button.setCompoundDrawablesRelativeWithIntrinsicBounds(imagecheck, null, null, null);
					}else{
						cheque_tercero_postfechado_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
					}
				}
				break;
			case REQUEST_DOCS_NEG:
				if(resultCode == Activity.RESULT_OK){
					C_documentos_negativos_wrapper documentosNegativosWrapper=(C_documentos_negativos_wrapper) data.getSerializableExtra("documentos_negativos");
					documentos_negativos=documentosNegativosWrapper.getItems();
					//doc_negativo_parcial_a_descontar_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
					doc_negativo_parcial_a_descontar_button.setCompoundDrawablesRelativeWithIntrinsicBounds(imagecheck, null, null, null);
				}
				break;
			case REQUEST_CONSIGNACION:
				if(resultCode == Activity.RESULT_OK){
					C_detalle_consignaciones_wrapper documentosWrapper=(C_detalle_consignaciones_wrapper) data.getSerializableExtra("detalle_consignaciones");
					detalle_consignaciones=documentosWrapper.getItems();
					C_pago_wrapper pagosWrapper=(C_pago_wrapper) data.getSerializableExtra("pagos");
					pagos=pagosWrapper.getItems();

					if(data.getExtras().getInt("removeCheck")!=0){
//						consignacion_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
						consignacion_button.setCompoundDrawablesRelativeWithIntrinsicBounds(imagecheck, null, null, null);
					}else{
						consignacion_button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
					}
				}
				break;
		}
		setValorARecaudar();
	}
	
	public void guardarCobro(boolean recalculate){
//		int counter;
		String var_tipo_documento ="";
		String var_documento_id ="";
		String sqlSentence="";
		Transaccion_SQL result;
		
		if(recalculate || validarDatos.getEstado().equalsIgnoreCase("")){
			validar_datos(validarDatos);
		}
		
		if(validarDatos.getEstado().equalsIgnoreCase("OK")){
			for(C_documentos aux:documentos){
				aux.setValorProceso(aux.getValorDisponible());
			}
			for(C_pagos aux:pagos){
				aux.setValorProcesado(aux.getValorDocumento());
			}
			for(C_detalle_consignaciones aux:detalle_consignaciones){
				aux.setValorProceso(aux.getValorDoc());
			}
			for(C_documentos_negativos aux:documentos_negativos){
				aux.setValorProceso(aux.getValorDisponible());
			}
		    Collections.sort(documentos,new OrderByDate());
		    
		    cruce_pagos=new ArrayList<C_cruce_pagos>();
		    cruce_docs_negativos=new ArrayList<C_cruce_docs_negativos>();
		    cruce_consignaciones=new ArrayList<C_cruce_consignaciones>();
		    cursorFormasPagoOrden=DBAdapter.getTipoPagoOrden();
	    	if(cursorFormasPagoOrden.moveToFirst()){
	    		do{
	    			if(cursorTipoPago.moveToFirst()){
	    				do{
	    					if(cursorFormasPagoOrden.getString(cursorFormasPagoOrden.getColumnIndex("tipo_pago_id")).
    							equalsIgnoreCase(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("tipo_pago_id")))){
	    						if(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("es_nota_credito")).equalsIgnoreCase("S")){
	    							if(documentos_negativos.size()>0){
	    								docsNegativosPG();
	    							}
	    						}else{
	    							for(C_pagos aux:pagos){
	    								if(aux.getTipoPagoId().equalsIgnoreCase(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("tipo_pago_id")))){
	    									if(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("es_posfechado")).equalsIgnoreCase("S")){
	    										cheques_posfechadosPG(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("tipo_pago_id")));
	    									}else{
	    										if(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("es_consignacion")).equalsIgnoreCase("S")){
	    											consignacionesPG(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("tipo_pago_id")),cursorFormasPagoOrden.getString(cursorFormasPagoOrden.getColumnIndex("subtipo_pago_id")));
	    					    				}else{
	    											otrasFormasPagoPG(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("tipo_pago_id")));
	    										}
	    									}
	    								}
	    							}
	    						}
	    					}
	    				}while(cursorTipoPago.moveToNext());
	    			}
	    		}while(cursorFormasPagoOrden.moveToNext());
		    }
	    	
	    //	int counter=0;
//	    	for(C_documentos aux:documentos){
	//  //  		if(aux.getValorDisponible() == aux.getValorProceso()){
	  //  		if(true){
	    	//		documentos.remove(counter);
//	    		}
	//    		counter++;	    		
	  //  	}

			for(Iterator<C_documentos> it = documentos.iterator(); it.hasNext();) {
				C_documentos aux = it.next();
				if(aux.getValorDisponible() == aux.getValorProceso()){
					it.remove();
				}
			}
	    	
			for(Iterator<C_documentos_negativos> it = documentos_negativos.iterator(); it.hasNext();) {
				C_documentos_negativos aux = it.next();
	    		if(aux.getValorDisponible() == aux.getValorProceso()){
					it.remove();
				}
			}
	    	
//	    	counter=0;
	//    	for(C_documentos_negativos aux:documentos_negativos){
	  //  		if(aux.getValorDisponible() == aux.getValorProceso()){
	    //			documentos_negativos.remove(counter);
	    //		}
	    		//counter++;	    		
//	    	}
    	   	if(encabezado.getHayAbono()){
		    	for(C_documentos aux:documentos){
		    		if(aux.getValorProceso()>0){
		    			var_tipo_documento =aux.getTipoDoc();
		    			var_documento_id =aux.getDocId();
		    			encabezado.setAbonoTipoDoc(var_tipo_documento);
		    			encabezado.setAbonoDocumentoId(var_documento_id);
		    			abono_cambiar_datos_adicionales(var_tipo_documento,var_documento_id);
		    			break;
		    		}
		    	}

	    	}
	    	querys=new ArrayList<String>();
    	
	    	sqlSentence="INSERT INTO esu_cobro VALUES ('"+
	    		encabezado.getCobroId()+"','"+
	    		encabezado.getCobroId()+"','"+
	    		cliente_id+"',datetime('now','localtime'),'" +
   				encabezado.getMaquinaId()+"','','','C', datetime('now','localtime'))";
	    	querys.add(sqlSentence);
	    	sqlSentence="INSERT INTO esd_cobro VALUES ('"+
		   		encabezado.getCobroId()+"','"+
		   		encabezado.getCobroId()+"','"+
		    	cliente_id+"',datetime('now','localtime'),'" +
	   			encabezado.getMaquinaId()+"','','')";
		   	querys.add(sqlSentence);

	    	escrituras_documentos();
	    	if(pagos.size()>0){
	    		escritura_pagos();
	    	}
	    	
	    	if(detalle_consignaciones.size()>0){
	    		escritura_pagos_detalle();
	    	}
	    	
	    	if(cruce_pagos.size()>0){
	    		escritura_cruce_pagos();
	    	}
	    	
	    	if(cruce_docs_negativos.size()>0){
	    		escritura_cruce_docs_negativos();
	    	}
	    	
	    	if(cruce_consignaciones.size()>0){
	    		escritura_cruce_consignacion();
	    	}

	    	if(encabezado.getMenorValorPago()>0 || encabezado.getMayorValorPago()>0){
	    		escritura_diferencia();
	    	}

	    	DBAdapter.beginTransaction();
	    	result=DBAdapter.insertQuerys(querys);
	    	if(result.getEstado().equalsIgnoreCase("OK")){	    			
	    		if(hilo_desnormalizar_cobros(encabezado.getCobroId())){
		    		if(DBAdapter.descontarPagosACartera(String.valueOf(encabezado.getCobroId()))){
						if(DBAdapter.addEvento(visita_id,
								"02",
								"",
								null,
								null,
								null,
								DBAdapter.getTotalCobro(String.valueOf(encabezado.getCobroId())),
								0,
								false,
								encabezado.getCobroId(),
								horaIni)){
							DBAdapter.setTransactionSuccessful();
							dialogBuilder = new AlertDialog.Builder(context);
							dialogBuilder.setTitle(R.string.alerta);
							dialogBuilder.setMessage(R.string.cobro_grabado_satisfactoriamente);
							dialogBuilder.setCancelable(false);
							dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									finish();
								}
							});
							dialogBuilder.create().show();
						}else{
				    		Utility.showMessage(context, R.string.hubo_un_error_al_guardar_el_cobro);
						}
			    	}else{
			    		Utility.showMessage(context, R.string.hubo_un_error_al_guardar_el_cobro);
					}
	    		}else{
		    		Utility.showMessage(context, R.string.hubo_un_error_al_guardar_el_cobro);
				}
	    	}else{
	    		Log.e("info",result.getErrorMsj());
	    		Utility.showMessage(context, R.string.hubo_un_error_al_guardar_el_cobro);
	    	}
			DBAdapter.endTransaction();
		}else if(validarDatos.getEstado().equalsIgnoreCase("ERROR")){
			Utility.showMessage(context, validarDatos.getMensaje());
			validarDatos.setEstado("");
			validarDatos.setMensaje("");
		}else{}
		return;
	}
	
	private void docsNegativosPG(){
		ArrayList<C_documentos> filtradoDocs=new ArrayList<C_documentos>();
		
		C_documentos auxFiltrado;
	    Collections.sort(documentos_negativos,new CustomComparator());
   		for(C_documentos_negativos auxDocsNeg:documentos_negativos){
   			for(C_documentos auxDocs:documentos){
	    		if(auxDocs.getValorProceso()>= Math.abs(auxDocsNeg.getValorProceso())){
	    			filtradoDocs.add(auxDocs);
	    		}
   			}
   			
   			Collections.sort(filtradoDocs,new  Comparator<C_documentos>(){
				@Override
				public int compare(C_documentos lhs, C_documentos rhs) {
					return (int)(lhs.getValorProceso()-rhs.getValorProceso());
				}
   				
   			});

   			if(filtradoDocs.size()>0){
   				auxFiltrado=filtradoDocs.get(0);
   				if(auxFiltrado.getValorProceso()>0 &&  Math.abs(auxDocsNeg.getValorProceso())>0){
    				cruce_documentos_neg(auxFiltrado.getTipoDoc(), auxFiltrado.getDocId(), auxDocsNeg.getTipoDoc(), auxDocsNeg.getDocId());
    			}
   			}
   			filtradoDocs.clear();
   		}
	    
	    Collections.sort(documentos,new OrderByDate());
	    for(C_documentos_negativos auxDocsNeg:documentos_negativos){
	    	if(Math.abs(auxDocsNeg.getValorProceso())>0){
		    	for(C_documentos aux:documentos){
					if(aux.getValorProceso()>0 &&  Math.abs(auxDocsNeg.getValorProceso())>0){
			    		cruce_documentos_neg(aux.getTipoDoc(), aux.getDocId(), auxDocsNeg.getTipoDoc(), auxDocsNeg.getDocId());
			    		if( Math.abs(auxDocsNeg.getValorProceso())<=0){
			    			break;
			    		}
		    		}
		    	}
		    	
	    	}
	    
	    }	    
	    return;
	}
	
	private void cruce_documentos_neg(String docTipoDoc, String docDocId, String DocNegTipoDoc, String DocNegDocId){
		Cursor cursorTipoPago=DBAdapter.getTiposPago();
		C_documentos_negativos auxDocNeg;
		C_documentos auxDoc;
		String var_tipo_pago_id="";
		String var_forma_pago_id="";
		int posDoc=0;
		int posDocNeg=0;
		int counter=0;
		float valor_a_cruzar=0;
		for(C_documentos aux:documentos){				
			if(aux.getDocId().equalsIgnoreCase(docDocId) && aux.getTipoDoc().equalsIgnoreCase(docTipoDoc)){
				posDoc=counter;
				break;
			}
			counter++;
		}

		counter=0;
		for(C_documentos_negativos aux:documentos_negativos){
			if(aux.getDocId().equalsIgnoreCase(DocNegDocId) && aux.getTipoDoc().equalsIgnoreCase(DocNegTipoDoc)){
				posDocNeg=counter;
				break;
			}
			counter++;
		}
		
		auxDocNeg=documentos_negativos.get(posDocNeg);
		auxDoc=documentos.get(posDoc);

		if(cursorTipoPago.moveToFirst()){
			do{
				if(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("es_nota_credito")).equalsIgnoreCase("S")){
					var_tipo_pago_id=cursorTipoPago.getString(cursorTipoPago.getColumnIndex("tipo_pago_id"));
					break;
				}
			}while(cursorTipoPago.moveToNext());
		}

		if(!var_tipo_pago_id.equalsIgnoreCase("")){
			if(Math.abs(auxDocNeg.getValorProceso())> auxDoc.getValorProceso()){
				valor_a_cruzar=auxDoc.getValorProceso();
			}else{
				valor_a_cruzar=Math.abs(auxDocNeg.getValorProceso());
			}

			var_forma_pago_id=determinar_FormPago(var_tipo_pago_id, "");
			
			cruce_docs_negativos.add(new C_cruce_docs_negativos(
					auxDoc.getTipoDoc(),
					auxDoc.getDocId(),
					auxDocNeg.getTipoDoc(),
					auxDocNeg.getDocId(),
					(valor_a_cruzar*(-1)),
					var_forma_pago_id,
					auxDocNeg.getFechaDoc(),
					auxDocNeg.getSaldoAnterior()));
			auxDoc.setValorCobrado((auxDoc.getValorCobrado()+valor_a_cruzar));
			auxDoc.setValorProceso((auxDoc.getValorDisponible()-auxDoc.getValorCobrado()));
			determinar_datos_diferencia(auxDoc.getTipoDoc(),auxDoc.getDocId(),var_tipo_pago_id,0,0);
			auxDocNeg.setValorCobrado((auxDocNeg.getValorCobrado()-valor_a_cruzar));
			auxDocNeg.setValorProceso((auxDocNeg.getValorDisponible()-auxDocNeg.getValorCobrado()));
		}else{
			Utility.showMessage(context, getString(R.string.SFV_0008));
		}
		return;
	}

	private String determinar_FormPago(String tipo_pago_id, String subTipo_pago_id){
		Cursor cursorFormasPagoOrden=DBAdapter.getTipoPagoOrden();
		String res="";
		if(cursorFormasPagoOrden.moveToFirst()){
			do{
				if(cursorFormasPagoOrden.getString(cursorFormasPagoOrden.getColumnIndex("tipo_pago_id")).equalsIgnoreCase(tipo_pago_id)){
					if(subTipo_pago_id.equalsIgnoreCase("")){
						res=cursorFormasPagoOrden.getString(cursorFormasPagoOrden.getColumnIndex("forma_pago_id"));
						return res;
					}else{
						if(cursorFormasPagoOrden.getString(cursorFormasPagoOrden.getColumnIndex("subtipo_pago_id")).equalsIgnoreCase(subTipo_pago_id)){
							res=cursorFormasPagoOrden.getString(cursorFormasPagoOrden.getColumnIndex("forma_pago_id"));
							Log.i("info determinar_FormPago",res);
							return res;
						}
					}
				}
			}while(cursorFormasPagoOrden.moveToNext());
		}
		if(res.equals("")){
			Utility.showMessage(context, R.string.SFV_0001);
		}
		return res;
	}

	private void determinar_datos_diferencia(String tipoDoc, String docId, String tipoPago, int idPago, int idDetalle){
		Cursor cursorTipoPago=DBAdapter.getTiposPago();
		if(encabezado.getMenorValorPago()>0 || encabezado.getMayorValorPago()>0){
			if(cursorTipoPago.moveToFirst()){
				do{
					if(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("tipo_pago_id")).equalsIgnoreCase(tipoPago)){
						for(C_documentos aux:documentos){
							if(aux.getDocId().equalsIgnoreCase(docId) && aux.getTipoDoc().equalsIgnoreCase(tipoDoc)){
								aux.setDiferenciaUltimoIdPago(idPago);
								aux.setDiferenciaUltimoIdDet(idDetalle);
								if(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("es_cheque")).equalsIgnoreCase("S")){
									aux.setDiferenciaEsEfectivo("N");
									aux.setDiferenciaEsPosfechado(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("es_posfechado")));
								}else{
									aux.setDiferenciaEsEfectivo("S");
									aux.setDiferenciaEsPosfechado("N");
								}
								return;
							}
						}
					}
				}while(cursorTipoPago.moveToNext());
			}else{
			}
		}
		return;
	}

	private void cheques_posfechadosPG(String tipo_pago_id){
		C_documentos auxFiltrado;
		ArrayList<C_documentos> filtradoDocs=new ArrayList<C_documentos>();
	    Collections.sort(pagos,new OrderPagosByDate());
	    Collections.sort(documentos,new OrderDocsByValue());

	    for(C_pagos aux:pagos){
			if(aux.getTipoPagoId().equalsIgnoreCase(tipo_pago_id)){
				for(C_documentos auxDoc:documentos){
					if(auxDoc.getValorProceso() >= aux.getValorDocumento()){
						filtradoDocs.add(auxDoc);
					}
				}
			}

   			Collections.sort(filtradoDocs,new  Comparator<C_documentos>(){
				@Override
				public int compare(C_documentos lhs, C_documentos rhs) {
					return (int)(lhs.getValorProceso()-rhs.getValorProceso());
				}
   			});

			if(filtradoDocs.size()>0){
   				auxFiltrado=filtradoDocs.get(0);
   				if(auxFiltrado.getValorProceso()>0 &&  aux.getValorProcesado()>0){
					cruce_pagos(auxFiltrado.getTipoDoc(), auxFiltrado.getDocId(), aux.getIdentificadorPago());
   				}
   			}
   			filtradoDocs.clear();
		}

	    for(C_pagos aux:pagos){
			if(aux.getTipoPagoId().equalsIgnoreCase(tipo_pago_id)){
				if(aux.getValorProcesado()>0){
					for(C_documentos auxDoc:documentos){
						if(auxDoc.getValorProceso() >0 && aux.getValorProcesado()>0){
							cruce_pagos(auxDoc.getTipoDoc(), auxDoc.getDocId(), aux.getIdentificadorPago());
							if(aux.getValorProcesado()<=0){
								break;
							}
						}
					}	
				}
			}
		}

		return;
	}
	
	private void cruce_pagos(String tipoDoc, String docId, int identificadorPago){
		int posDoc=0;
		int posPago=0;
		int counter=0;
		C_documentos auxDoc;
		C_pagos auxPago;
		float valor_a_cruzar=0;
		String var_forma_pago_id;
		C_adicionales_cobro adicionalesCobro;
		for(C_documentos aux:documentos){				
			if(aux.getDocId().equalsIgnoreCase(docId) && aux.getTipoDoc().equalsIgnoreCase(tipoDoc)){
				posDoc=counter;
				break;
			}
			counter++;
		}
		counter=0;
		for(C_pagos aux:pagos){
			if(aux.getIdentificadorPago() == identificadorPago){
				posPago=counter;
				break;
			}
			counter++;
		}
		
		auxPago=pagos.get(posPago);
		auxDoc=documentos.get(posDoc);

		if(auxPago.getValorProcesado()>auxDoc.getValorProceso()){
			valor_a_cruzar = auxDoc.getValorProceso();
		}else{
			valor_a_cruzar = auxPago.getValorProcesado();
		}

		var_forma_pago_id = determinar_FormPago(auxPago.getTipoPagoId(),"");
		adicionalesCobro=datos_adicionales_cruce(auxDoc.getTipoDoc(),auxDoc.getDocId(),auxPago.getTipoPagoId(),auxPago.getFechaDoc(),null,valor_a_cruzar);
		Log.i("info","adicionalesCobro "+adicionalesCobro.getNumeroRelacion());
		cruce_pagos.add(new C_cruce_pagos(
				auxDoc.getTipoDoc(),
				auxDoc.getDocId(),
				auxPago.getIdentificadorPago(),
				adicionalesCobro.getCodigoMovTipoDoc(),
				var_forma_pago_id,
				adicionalesCobro.getNumeroRelacion(),
				valor_a_cruzar));
		auxDoc.setValorCobrado((auxDoc.getValorCobrado()+ valor_a_cruzar));
		auxDoc.setValorProceso((auxDoc.getValorDisponible()-auxDoc.getValorCobrado()));
		
		determinar_datos_diferencia(auxDoc.getTipoDoc(),auxDoc.getDocId(),auxPago.getTipoPagoId(),auxPago.getIdentificadorPago(),0);
		auxPago.setValorProcesado((auxPago.getValorProcesado()-valor_a_cruzar));
		return;
	}


	@SuppressWarnings("resource")
	private C_adicionales_cobro datos_adicionales_cruce(String tipoDoc, String docId, String tipoPagoId, String fechaCheque,String par_forma_pago_consignacion,float valor_a_cruzar){
		C_adicionales_cobro res=new C_adicionales_cobro();
		int posDoc=0;
		int posPago=0;
		int counter=0;
		C_documentos auxDoc;
		Cursor esd_tipo_pago;
		Cursor esd_parametro_general;
		Cursor esd_para_cmotd;
		boolean isError=true;

		String var_Efectivo = "";
		String var_Cheque = "";
		String var_cheque_posfechado="";
		String var_periodos_iguales="";
		String var_Consignacion="";
		
		Log.i("datos_adicionales_cruce","par_forma_pago_consignacion" +par_forma_pago_consignacion);
		Log.i("datos_adicionales_cruce","tipoPagoId" +tipoPagoId);

		for(C_documentos aux:documentos){				
			if(aux.getDocId().equalsIgnoreCase(docId) && aux.getTipoDoc().equalsIgnoreCase(tipoDoc)){
				posDoc=counter;
				break;
			}
			counter++;
		}
		
		auxDoc=documentos.get(posDoc);
		esd_tipo_pago=DBAdapter.getTiposPago();
		if(esd_tipo_pago.moveToFirst()){
			do{
				if(esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("tipo_pago_id")).equalsIgnoreCase(tipoPagoId)){
					isError=false;
					break;
				}
			}while(esd_tipo_pago.moveToNext());
		}
		if(!isError){
			var_Efectivo = (esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("es_efectivo"))!=null && !esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("es_efectivo")).equalsIgnoreCase(""))? esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("es_efectivo")):"N";
			var_Cheque = (esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("es_cheque"))!=null && !esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("es_cheque")).equalsIgnoreCase(""))? esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("es_cheque")):"N";
			var_Consignacion = (esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("es_consignacion"))!=null && !esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("es_consignacion")).equalsIgnoreCase(""))? esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("es_consignacion")):"N";
					
			if(var_Cheque.equalsIgnoreCase("S")){
				var_cheque_posfechado=determinar_cheque_posfechado(fechaCheque);
				if(var_cheque_posfechado.equalsIgnoreCase("S")){
					esd_para_cmotd=DBAdapter.getEsdParaCmotd("N", "", "S", "S", "", "", tipoDoc);
					if(esd_para_cmotd.getCount()==0){
						esd_para_cmotd=DBAdapter.getEsdParaCmotd("N", "", "S", "S", "", "", "");
					}
					
				}else{
					var_periodos_iguales=determinar_periodos_iguales(fechaCheque);
					esd_para_cmotd=DBAdapter.getEsdParaCmotd("N", "", "S", var_cheque_posfechado,"", var_periodos_iguales, tipoDoc);
				}
			}else{
				esd_para_cmotd=DBAdapter.getEsdParaCmotd("N", "S", "", "", "","", tipoDoc);
			}
			
			
			if(esd_para_cmotd.getCount()==0 && !var_cheque_posfechado.equalsIgnoreCase("S")){
				if(var_Cheque.equalsIgnoreCase("S")){
					esd_para_cmotd=DBAdapter.getEsdParaCmotd("N", "", "S", var_cheque_posfechado,"", var_periodos_iguales, "");
				}else{
					esd_para_cmotd=DBAdapter.getEsdParaCmotd("N", "S", "", "", "","", "");
				}
			}
			Log.i("datos_adicionales_cruce","var_cheque_posfechado" +var_cheque_posfechado);
			Log.i("datos_adicionales_cruce","esd_para_cmotd " +esd_para_cmotd.getCount());

			if(esd_para_cmotd.moveToFirst()){
				Log.i("datos_adicionales_cruce","entro ");
				String codigo_numero_relacion=esd_para_cmotd.getString(esd_para_cmotd.getColumnIndex("cod_numero_relacion"));

				if(!var_cheque_posfechado.equalsIgnoreCase("S")){
					res.setCodigoMovTipoDoc(esd_para_cmotd.getString(esd_para_cmotd.getColumnIndex("cod_mov_tipo_doc")));
				}else{
					Log.i("datos_adicionales_cruce","tipoPagoId" +tipoPagoId);
					if(auxDoc.getValorDisponible()!=valor_a_cruzar){
						Log.i("datos_adicionales_cruce","entro D");
						res.setNumeroRelacion("D");
					}else{
						Log.i("datos_adicionales_cruce","entro B");
						res.setNumeroRelacion("B");
					}
				}				
				if(codigo_numero_relacion.equalsIgnoreCase("A")){
					if(var_Cheque.equalsIgnoreCase("S")){
						if(var_periodos_iguales.equalsIgnoreCase("S")){
							res.setNumeroRelacion("A2");
						}else{
							res.setNumeroRelacion("A3");
						}
					}else{
						if(var_Consignacion.equalsIgnoreCase("S")){
							Log.i("datos_adicionales_cruce","entro consignacion");
							Log.i("datos_adicionales_cruce","entro consignacion var_Consignacion "+var_Consignacion);
							if(par_forma_pago_consignacion!=null && par_forma_pago_consignacion.equalsIgnoreCase("CH")){
								res.setNumeroRelacion("A1");
							}else{
								res.setNumeroRelacion("A3");
							}
						}else{
							res.setNumeroRelacion("A1");
						}
					}
					Log.i("datos_adicionales_cruce","res" +res.getNumeroRelacion());

				}else{
					res.setNumeroRelacion(codigo_numero_relacion+DBAdapter.getPagoNumeroRelacion());
//					res.setNumeroRelacion(esd_para_cmotd.getString(esd_para_cmotd.getColumnIndex("cod_numero_relacion"))+DBAdapter.getPagoNumeroRelacion());
				}
				return res;
			}else{
				Log.e("info",getString(R.string.SFV_0007));
				esd_parametro_general=DBAdapter.getEsdParametroGeneral();
				if(esd_parametro_general.moveToFirst()){
					res.setCodigoMovTipoDoc(esd_parametro_general.getString(esd_parametro_general.getColumnIndex("codimotd_sin_identificar")));
					res.setNumeroRelacion("");
					return res;
				}
			}
		}else{
			Log.e("info",getString(R.string.SFV_0008));
			esd_parametro_general=DBAdapter.getEsdParametroGeneral();
			if(esd_parametro_general.moveToFirst()){
				res.setCodigoMovTipoDoc(esd_parametro_general.getString(esd_parametro_general.getColumnIndex("codimotd_sin_identificar")));
				res.setNumeroRelacion("");
				return res;
			}
		}
		return res;
	}
	
	private String determinar_cheque_posfechado(String fechaCheque){
        Calendar c = Calendar.getInstance();
        String res="";
		String fCheque="";
		String fAct="";
    	Cursor esd_parametro_general=DBAdapter.getEsdParametroGeneral();
    	int var_num_dias=0;
    	int fechaC=0;
    	int fechaA=0;
    	int dias_del_parametro=0;
   		fCheque=fechaCheque.replaceAll("-", "");
   		fechaC=Integer.parseInt(fCheque);
    	fAct=dateFormat.format(c.getTime());
    	fechaA=Integer.parseInt(fAct);
    	var_num_dias=fechaC-fechaA;
    	if(var_num_dias>0){
    		if(c.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY){
    			var_num_dias = var_num_dias - 2;
    		}else if(c.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
    			var_num_dias = var_num_dias - 1;
    			
    		}
    	}

		if(esd_parametro_general.moveToFirst()){
			dias_del_parametro=esd_parametro_general.getInt(esd_parametro_general.getColumnIndex("dias_cheque_posfechado"));
		}
    	if(var_num_dias>dias_del_parametro){
    		res="S";
    	}else{
    		res="N";
    	}
		return res;
	} 
	
	private String determinar_periodos_iguales(String fechaCheque){
        Calendar c = Calendar.getInstance();
		String res="";
		String periodoAct=dateFormatPeriodo.format(c.getTime());
		String periodoCheque="";
		
    	String [] auxDate;
    	try{
    		auxDate=fechaCheque.split("-");
    		periodoCheque=auxDate[0].substring(2)+auxDate[1];
       	}catch(Exception e){
       		Log.e("info","error fecha "+e);
       	}
    	if(periodoAct.equalsIgnoreCase(periodoCheque)){
    		res="S";
    	}else{
    		res="N";
    	}
		return res;		
	}
	
	private void otrasFormasPagoPG(String tipo_pago_id){
	    Collections.sort(documentos,new OrderByDate());
		for(C_pagos aux:pagos){
			if(aux.getTipoPagoId().equalsIgnoreCase(tipo_pago_id)){
				for(C_documentos auxDoc:documentos){
					if(auxDoc.getValorProceso() > 0){
						cruce_pagos(auxDoc.getTipoDoc(), auxDoc.getDocId(), aux.getIdentificadorPago());
						if(aux.getValorProcesado()<=0){
							break;
						}
					}
				}
			}
		}
		return;
	}
	
	private void consignacionesPG(String tipo_pago_id, String subTipoPagoId){
		ArrayList<C_detalle_consignaciones> filtrados=new ArrayList<C_detalle_consignaciones>();
		Collections.sort(pagos,new OrderPagosByDate());
		for(C_pagos aux:pagos){
			if(aux.getTipoPagoId().equalsIgnoreCase(tipo_pago_id)){
				for(C_detalle_consignaciones auxDetCon: detalle_consignaciones){
					filtrados.add(auxDetCon);
				}
			}

			for(C_detalle_consignaciones auxFiltrados: filtrados){
				if(auxFiltrados.getIdentificadoPago() == aux.getIdentificadorPago() && auxFiltrados.getTipoPagoId().equalsIgnoreCase(subTipoPagoId)){
					for(C_documentos auxDoc:documentos){
						if(auxDoc.getValorProceso()>0 && auxFiltrados.getValorProceso()>0){
							cruce_consignacion(auxDoc.getTipoDoc(),auxDoc.getDocId(),auxFiltrados.getIdentificadoPago(),auxFiltrados.getIdentifiicadorDetalle(), aux.getTipoPagoId());
							if(auxFiltrados.getValorProceso()<=0){
								break;
							}
						}
					}
				}
			}
			filtrados.clear();
		}
		return;
	}

	private void cruce_consignacion(
			String tipoDoc, String docId, 
			int identificadorPago, 
			int identificadorDet, 
			String tipo_pago_id){
		int posDoc=0;
		int posCons=0;
		int posPago=0;
		int counter=0;
		C_documentos auxDoc;
		C_detalle_consignaciones auxDetCons;
		C_pagos auxPago;
		float valor_a_cruzar=0;
		String var_forma_pago_id;
		C_adicionales_cobro adicionalesCobro;

		for(C_documentos aux:documentos){				
			if(aux.getDocId().equalsIgnoreCase(docId) && aux.getTipoDoc().equalsIgnoreCase(tipoDoc)){
				posDoc=counter;
				break;
			}
			counter++;
		}
		counter=0;
		for(C_detalle_consignaciones aux:detalle_consignaciones){
			if(aux.getIdentificadoPago() == identificadorPago && aux.getIdentifiicadorDetalle()== identificadorDet){
				posCons=counter;
				break;
			}
			counter++;
		}

		counter=0;
		for(C_pagos aux:pagos){
			if(aux.getIdentificadorPago() == identificadorPago){
				posPago=counter;
				break;
			}
			counter++;
		}
//		￼Ubicar en instancia definida de “Pagos” el registro correspondiente al parámetro recibido de “par_Identificador_pago”
		
		auxDoc=documentos.get(posDoc);
		auxDetCons=detalle_consignaciones.get(posCons);
		auxPago=pagos.get(posPago);
		
		if(auxDetCons.getValorProceso()>auxDoc.getValorProceso()){
			valor_a_cruzar = auxDoc.getValorProceso();
		}else{
			valor_a_cruzar = auxDetCons.getValorProceso();
		}

		var_forma_pago_id=determinar_FormPago(tipo_pago_id,auxDetCons.getTipoPagoId());

		adicionalesCobro=datos_adicionales_cruce(
				auxDoc.getTipoDoc(),
				auxDoc.getDocId(),
				auxPago.getTipoPagoId(),
//				auxDetCons.getTipoPagoId(),
				auxDetCons.getFechaDoc(),
				var_forma_pago_id,
				valor_a_cruzar);
		
		cruce_consignaciones.add(new C_cruce_consignaciones(
			auxDoc.getTipoDoc(),
			auxDoc.getDocId(),
			auxDetCons.getIdentificadoPago(),
			auxDetCons.getIdentifiicadorDetalle(),
			valor_a_cruzar,
			adicionalesCobro.getCodigoMovTipoDoc(),
			adicionalesCobro.getNumeroRelacion(),
			var_forma_pago_id
		));

		auxDoc.setValorCobrado((auxDoc.getValorCobrado()+valor_a_cruzar));
		auxDoc.setValorProceso((auxDoc.getValorDisponible()-auxDoc.getValorCobrado()));

		determinar_datos_diferencia(tipoDoc,docId,auxDetCons.getTipoPagoId(),identificadorPago,identificadorDet);
		auxDetCons.setValorProceso((auxDetCons.getValorProceso()-valor_a_cruzar));
		return;
	}

	private void abono_cambiar_datos_adicionales(String tipoDoc, String docId){
		String numero_relacion="";
		C_adicionales_cobro adicionalesCobro;
		Cursor cursorTipoPago=DBAdapter.getTiposPago();
		String var_es_cheque = "";
		String var_es_posfechado=""; 
		for(C_cruce_pagos auxCrucePago:cruce_pagos){
			if(auxCrucePago.getDocId().equalsIgnoreCase(docId)){
				for(C_pagos auxPago:pagos){
					if(auxPago.getIdentificadorPago()==auxCrucePago.getIdentificador_pago()){
						if(cursorTipoPago.moveToFirst()){
							do{
								if(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("tipo_pago_id")).equalsIgnoreCase(auxPago.getTipoPagoId())){
									var_es_cheque = cursorTipoPago.getString(cursorTipoPago.getColumnIndex("es_cheque"));
									var_es_posfechado =cursorTipoPago.getString(cursorTipoPago.getColumnIndex("es_posfechado"));
//									adicionalesCobro=abono_datos_adicionales(var_es_cheque,var_es_posfechado,tipoDoc,auxPago.getFechaDoc());
									adicionalesCobro=abono_datos_adicionales(var_es_cheque,var_es_posfechado,tipoDoc);
									auxCrucePago.setCodigoMovTipoDoc(adicionalesCobro.getCodigoMovTipoDoc());
//									auxCrucePago.setNumeroRelacion(adicionalesCobro.getNumeroRelacion());
									if(!auxCrucePago.getNumeroRelacion().equals("")){
										numero_relacion=auxCrucePago.getNumeroRelacion();
										numero_relacion.substring(1);
										auxCrucePago.setNumeroRelacion(adicionalesCobro.getNumeroRelacion()+DBAdapter.getPagoNumeroRelacion());
									}
								}
								
							}while(cursorTipoPago.moveToNext());
						}						
					}
				}
			}
		}

		for(C_cruce_consignaciones auxCruceCons:cruce_consignaciones){
			if(auxCruceCons.getDocId()==docId){
				for(C_pagos auxPago:pagos){
					if(auxPago.getIdentificadorPago()==auxCruceCons.getIdentificador_pago()){
						if(cursorTipoPago.moveToFirst()){
							do{
								if(cursorTipoPago.getString(cursorTipoPago.getColumnIndex("tipo_pago_id")).equalsIgnoreCase(auxPago.getTipoPagoId())){
									var_es_cheque = cursorTipoPago.getString(cursorTipoPago.getColumnIndex("es_cheque"));
									var_es_posfechado =cursorTipoPago.getString(cursorTipoPago.getColumnIndex("es_posfechado"));
//									adicionalesCobro=abono_datos_adicionales(var_es_cheque,var_es_posfechado,tipoDoc,auxPago.getFechaDoc());
									adicionalesCobro=abono_datos_adicionales(var_es_cheque,var_es_posfechado,tipoDoc);
									auxCruceCons.setCodigoMovTipoDoc(adicionalesCobro.getCodigoMovTipoDoc());
									if(!auxCruceCons.getNumeroRelacion().equals("")){
										numero_relacion=auxCruceCons.getNumeroRelacion();
										numero_relacion.substring(1);
										auxCruceCons.setNumeroRelacion(adicionalesCobro.getNumeroRelacion()+DBAdapter.getPagoNumeroRelacion());
									}
								}
								
							}while(cursorTipoPago.moveToNext());
						}						
					}
				}

			}
		}
		return;
	}

	
	@SuppressWarnings("resource")
//	private C_adicionales_cobro abono_datos_adicionales(String es_cheque, String es_posfechado, String tipoDoc, String fechaDoc){
	private C_adicionales_cobro abono_datos_adicionales(String es_cheque, String es_posfechado, String tipoDoc){
		Cursor esd_para_cmotd;
		Cursor esd_parametro_general;
		C_adicionales_cobro res=new C_adicionales_cobro();
//		String var_periodos_iguales="";
		
		Log.i("info","entre abono_datos_adicionales");
		if(es_cheque.equalsIgnoreCase("S")){
//			var_periodos_iguales=determinar_periodos_iguales(fechaDoc);
			esd_para_cmotd=DBAdapter.getEsdParaCmotd("S", "", "S", es_posfechado,"", "", tipoDoc);
		}else{
			esd_para_cmotd=DBAdapter.getEsdParaCmotd("S", "S", "", "", "","", tipoDoc);
		}
		
		if(esd_para_cmotd.getCount()==0){
			if(es_cheque.equalsIgnoreCase("S")){
				esd_para_cmotd=DBAdapter.getEsdParaCmotd("S", "", "S", es_posfechado,"", "", "");
			}else{
				esd_para_cmotd=DBAdapter.getEsdParaCmotd("S", "S", "", "", "","", "");
			}
		}

		if(esd_para_cmotd.moveToFirst()){
			res.setCodigoMovTipoDoc(esd_para_cmotd.getString(esd_para_cmotd.getColumnIndex("cod_mov_tipo_doc")));
			res.setNumeroRelacion(esd_para_cmotd.getString(esd_para_cmotd.getColumnIndex("cod_numero_relacion")));
			Log.i("info","Numero Relacion "+esd_para_cmotd.getString(esd_para_cmotd.getColumnIndex("cod_numero_relacion")));
		}else{
			Log.e("info",getString(R.string.SFV_0007));
			esd_parametro_general=DBAdapter.getEsdParametroGeneral();
			if(esd_parametro_general.moveToFirst()){
				res.setCodigoMovTipoDoc(esd_parametro_general.getString(esd_parametro_general.getColumnIndex("codimotd_sin_identificar")));
				res.setNumeroRelacion("");
			}
		}
		return res;
	}

	private void escrituras_documentos(){
		int docConsecutivo=0;
		String sqlSentence="";
		String sqlSentence2="";
		double valorDescProntoPago=0;
		for(C_documentos aux:documentos){
			docConsecutivo=DBAdapter.getConsecutivoRegistroDoc();
			valorDescProntoPago=(aux.getAplicarDescProntoPago().equalsIgnoreCase("S"))?aux.getValorDescProntoPago():0;
			if(aux.getValorProceso()>0){
				sqlSentence="";
				if(encabezado.getHayAbono()){
					if(encabezado.getAbonoTipoDoc().equalsIgnoreCase(aux.getTipoDoc()) 
						&& encabezado.getAbonoDocId().equalsIgnoreCase(aux.getDocId())){
						sqlSentence="INSERT INTO esu_cobro_documento VALUES('"+
							docConsecutivo+"','"+
							encabezado.getCobroId()+"','"+
							aux.getTipoDoc()+"','"+
							aux.getDocId()+"','"+
							formatDateToBD(aux.getFechaDoc())+"','"+
							aux.getSaldoAnterior()+"','"+
							valorDescProntoPago+"','"+
//							aux.getValorDescProntoPago()+"','"+
							abono.getMotivoAbonoId()+"','"+
							formatDateToBD(abono.getFechaCancelaSado())+"','"+
							abono.getNumeroGuiaDevolucion()+"','"+
							formatDateToBD(abono.getFechaGuiaDevolucion())+"','"+
							abono.getNumeroDevolucion()+"','"+
							formatDateToBD(abono.getFechaDevolucion())+"','"+
							abono.getNumeroSolicitudNc()+"','"+
							formatDateToBD(abono.getFechaSolicitudNc())+"','"+
							abono.getObsAbono()+"','"+
	//						(aux.getSaldoAnterior()-aux.getValorDescProntoPago())+"','"+
							(aux.getSaldoAnterior()-valorDescProntoPago)+"','"+
							abono.getNueroMemorando()+"','"+
							formatDateToBD(abono.getFechaMemorando())+"','"+
							formatDateToBD(abono.getFechaSeguimiento())+"','"+
							aux.getObs()+"','C', datetime('now','localtime'))";
						sqlSentence2="INSERT INTO esd_cobro_documento VALUES('"+
								docConsecutivo+"','"+
								encabezado.getCobroId()+"','"+
								aux.getTipoDoc()+"','"+
								aux.getDocId()+"','"+
								formatDateToBD(aux.getFechaDoc())+"','"+
								aux.getSaldoAnterior()+"','"+
								valorDescProntoPago+"','"+
//								aux.getValorDescProntoPago()+"','"+
								abono.getMotivoAbonoId()+"','"+
								formatDateToBD(abono.getFechaCancelaSado())+"','"+
								abono.getNumeroGuiaDevolucion()+"','"+
								formatDateToBD(abono.getFechaGuiaDevolucion())+"','"+
								abono.getNumeroDevolucion()+"','"+
								formatDateToBD(abono.getFechaDevolucion())+"','"+
								abono.getNumeroSolicitudNc()+"','"+
								formatDateToBD(abono.getFechaSolicitudNc())+"','"+
								abono.getObsAbono()+"','"+
								(aux.getSaldoAnterior()-valorDescProntoPago)+"','"+
//								(aux.getSaldoAnterior()-aux.getValorDescProntoPago())+"','"+
								abono.getNueroMemorando()+"','"+
								formatDateToBD(abono.getFechaMemorando())+"','"+
								formatDateToBD(abono.getFechaSeguimiento())+"','"+
								aux.getObs()+"')";						
					}else{
						sqlSentence="INSERT INTO esu_cobro_documento VALUES('"+
							docConsecutivo+"','"+
							encabezado.getCobroId()+"','"+
							aux.getTipoDoc()+"','"+
							aux.getDocId()+"','"+
							formatDateToBD(aux.getFechaDoc())+"','"+
							aux.getSaldoAnterior()+"','"+
							valorDescProntoPago+"','','1900-01-01','','1900-01-01','','1900-01-01','','1900-01-01','','"+
							(aux.getSaldoAnterior()-valorDescProntoPago)+"','','1900-01-01','1900-01-01','"+
							
//aux.getValorDescProntoPago()+"','','1900-01-01','','1900-01-01','','1900-01-01','','1900-01-01','','"+
	//						(aux.getSaldoAnterior()-aux.getValorDescProntoPago())+"','','1900-01-01','1900-01-01','"+

							aux.getObs()+"','C', datetime('now','localtime'))";
						sqlSentence2="INSERT INTO esd_cobro_documento VALUES('"+
							docConsecutivo+"','"+
							encabezado.getCobroId()+"','"+
							aux.getTipoDoc()+"','"+
							aux.getDocId()+"','"+
							formatDateToBD(aux.getFechaDoc())+"','"+
							aux.getSaldoAnterior()+"','"+
							valorDescProntoPago+"','','1900-01-01','','1900-01-01','','1900-01-01','','1900-01-01','','"+
							(aux.getSaldoAnterior()-valorDescProntoPago)+"','','1900-01-01','1900-01-01','"+
//							aux.getValorDescProntoPago()+"','','1900-01-01','','1900-01-01','','1900-01-01','','1900-01-01','','"+
	//						(aux.getSaldoAnterior()-aux.getValorDescProntoPago())+"','','1900-01-01','1900-01-01','"+
							aux.getObs()+"')";
					}
				}else{
					sqlSentence="INSERT INTO esu_cobro_documento VALUES('"+
							docConsecutivo+"','"+
							encabezado.getCobroId()+"','"+
							aux.getTipoDoc()+"','"+
							aux.getDocId()+"','"+
							formatDateToBD(aux.getFechaDoc())+"','"+
							aux.getSaldoAnterior()+"','"+
							aux.getValorDescProntoPago()+"','','1900-01-01','','1900-01-01','','1900-01-01','','1900-01-01','','"+
							(aux.getSaldoAnterior()-aux.getValorDescProntoPago())+"','','1900-01-01','1900-01-01','"+
							aux.getObs()+"','C', datetime('now','localtime'))";
					sqlSentence2="INSERT INTO esd_cobro_documento VALUES('"+
							docConsecutivo+"','"+
							encabezado.getCobroId()+"','"+
							aux.getTipoDoc()+"','"+
							aux.getDocId()+"','"+
							formatDateToBD(aux.getFechaDoc())+"','"+
							aux.getSaldoAnterior()+"','"+
							valorDescProntoPago+"','','1900-01-01','','1900-01-01','','1900-01-01','','1900-01-01','','"+
							(aux.getSaldoAnterior()-valorDescProntoPago)+"','','1900-01-01','1900-01-01','"+
//							aux.getValorDescProntoPago()+"','','1900-01-01','','1900-01-01','','1900-01-01','','1900-01-01','','"+
	//						(aux.getSaldoAnterior()-aux.getValorDescProntoPago())+"','','1900-01-01','1900-01-01','"+
							aux.getObs()+"')";
				}
			}else{
				sqlSentence="INSERT INTO esu_cobro_documento VALUES('"+
						docConsecutivo+"','"+
						encabezado.getCobroId()+"','"+
						aux.getTipoDoc()+"','"+
						aux.getDocId()+"','"+
						formatDateToBD(aux.getFechaDoc())+"','"+
						aux.getSaldoAnterior()+"','"+
	//					aux.getValorDescProntoPago()+"','','1900-01-01','','1900-01-01','','1900-01-01','','1900-01-01','','"+
//						(aux.getSaldoAnterior()-aux.getValorDescProntoPago())+"','','1900-01-01','1900-01-01','"+
						valorDescProntoPago+"','','1900-01-01','','1900-01-01','','1900-01-01','','1900-01-01','','"+
						(aux.getSaldoAnterior()-valorDescProntoPago)+"','','1900-01-01','1900-01-01','"+
						aux.getObs()+"','C', datetime('now','localtime'))";
				sqlSentence2="INSERT INTO esd_cobro_documento VALUES('"+
						docConsecutivo+"','"+
						encabezado.getCobroId()+"','"+
						aux.getTipoDoc()+"','"+
						aux.getDocId()+"','"+
						formatDateToBD(aux.getFechaDoc())+"','"+
						aux.getSaldoAnterior()+"','"+
	//					aux.getValorDescProntoPago()+"','','1900-01-01','','1900-01-01','','1900-01-01','','1900-01-01','','"+
//						(aux.getSaldoAnterior()-aux.getValorDescProntoPago())+"','','1900-01-01','1900-01-01','"+
						valorDescProntoPago+"','','1900-01-01','','1900-01-01','','1900-01-01','','1900-01-01','','"+
						(aux.getSaldoAnterior()-valorDescProntoPago)+"','','1900-01-01','1900-01-01','"+
						aux.getObs()+"')";
			}
			querys.add(sqlSentence);
			querys.add(sqlSentence2);
		}
		
		for(C_documentos_negativos aux:documentos_negativos){
			docConsecutivo=DBAdapter.getConsecutivoRegistroDoc();
			sqlSentence="INSERT INTO esu_cobro_documento VALUES('"+
					docConsecutivo+"','"+
					encabezado.getCobroId()+"','"+
					aux.getTipoDoc()+"','"+
					aux.getDocId()+"','"+
					formatDateToBD(aux.getFechaDoc())+"','"+
					aux.getSaldoAnterior()+"','0','','1900-01-01','','1900-01-01','','1900-01-01','','1900-01-01','','"+
					(aux.getSaldoAnterior())+"','','1900-01-01','1900-01-01','"+
					aux.getObs()+"','C', datetime('now','localtime'))";
			querys.add(sqlSentence);
			sqlSentence="INSERT INTO esd_cobro_documento VALUES('"+
					docConsecutivo+"','"+
					encabezado.getCobroId()+"','"+
					aux.getTipoDoc()+"','"+
					aux.getDocId()+"','"+
					formatDateToBD(aux.getFechaDoc())+"','"+
					aux.getSaldoAnterior()+"','0','','1900-01-01','','1900-01-01','','1900-01-01','','1900-01-01','','"+
					(aux.getSaldoAnterior())+"','','1900-01-01','1900-01-01','"+
					aux.getObs()+"')";
			querys.add(sqlSentence);

		}
		return;
	} 
	
	private void escritura_pagos(){
		String sqlSentence="";
		Cursor esd_tipo_pago=DBAdapter.getTiposPago();
		boolean isError=true;
		float var_Valor_consignacion_EF = 0;
		float var_Valor_consignacion_CH = 0;
		String var_tipo_pago_id="";
		for(C_pagos aux:pagos){
//			Log.i("info","escritura_pagos "+aux.getFechaDoc());
			if(esd_tipo_pago.moveToFirst()){
				do{
					if(esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("tipo_pago_id")).equalsIgnoreCase(aux.getTipoPagoId())){
						isError=false;
						break;
					}
				}while(esd_tipo_pago.moveToNext());
			}
			if(isError){
				Log.e("info", getString(R.string.SFV_0008));
				return;
			}

			var_Valor_consignacion_EF = 0;
			var_Valor_consignacion_CH = 0;

			if(esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("es_consignacion")).equalsIgnoreCase("S")){
				if(esd_tipo_pago.moveToFirst()){
					do{
						if(esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("es_efectivo")).equalsIgnoreCase("S")){
							var_tipo_pago_id= esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("tipo_pago_id"));
							break;
						}
					}while(esd_tipo_pago.moveToNext());
				}
				

				for(C_detalle_consignaciones auxDetCons: detalle_consignaciones){
					if(auxDetCons.getIdentificadoPago()==aux.getIdentificadorPago()
						&& auxDetCons.getTipoPagoId().equalsIgnoreCase(var_tipo_pago_id)){
						var_Valor_consignacion_EF = var_Valor_consignacion_EF + auxDetCons.getValorDoc();
					}
					
				}
				
				if(esd_tipo_pago.moveToFirst()){
					do{
						if(esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("es_cheque")).equalsIgnoreCase("S") 
							&& esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("es_posfechado")).equalsIgnoreCase("N")){
							var_tipo_pago_id= esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("tipo_pago_id"));
							break;
						}
					}while(esd_tipo_pago.moveToNext());
				}

				for(C_detalle_consignaciones auxDetCons: detalle_consignaciones){
					if(auxDetCons.getIdentificadoPago()==aux.getIdentificadorPago()
						&& auxDetCons.getTipoPagoId().equalsIgnoreCase(var_tipo_pago_id)){
						var_Valor_consignacion_CH = var_Valor_consignacion_CH + auxDetCons.getValorDoc();
					}
				}
			}
			sqlSentence="INSERT INTO esu_cobro_pago VALUES('"+
					aux.getIdentificadorPago()+"','"+
					encabezado.getCobroId()+"','"+
					aux.getIdentificadorPago()+"','"+
					aux.getTipoPagoId()+"','"+
//					formatDateToBD( aux.getFechaDoc())+"','"+
					aux.getFechaDoc()+"','"+
					aux.getBancoId()+"','"+
					aux.getNumeroDoc()+"','"+
					aux.getNumeroCuenta()+"','"+
					aux.getValorDocumento()+"','"+
					var_Valor_consignacion_EF+"','"+
					var_Valor_consignacion_CH+"','C', datetime('now','localtime'))";
			querys.add(sqlSentence);
			sqlSentence="INSERT INTO esd_cobro_pago VALUES('"+
					aux.getIdentificadorPago()+"','"+
					encabezado.getCobroId()+"','"+
					aux.getIdentificadorPago()+"','"+
					aux.getTipoPagoId()+"','"+
//					formatDateToBD( aux.getFechaDoc())+"','"+
					aux.getFechaDoc()+"','"+
					aux.getBancoId()+"','"+
					aux.getNumeroDoc()+"','"+
					aux.getNumeroCuenta()+"','"+
					aux.getValorDocumento()+"','"+
					var_Valor_consignacion_EF+"','"+
					var_Valor_consignacion_CH+"')";
			querys.add(sqlSentence);
		}
		return;
	}
	
	private void escritura_pagos_detalle(){
		String sqlSentence="";
		for(C_detalle_consignaciones aux:detalle_consignaciones){
			sqlSentence="INSERT INTO esu_cobro_pago_det VALUES('"+
					aux.getIdentifiicadorDetalle()+"','"+
					encabezado.getCobroId()+"','"+
					aux.getIdentificadoPago()+"','"+
					aux.getIdentifiicadorDetalle()+"','"+
					aux.getTipoPagoId()+"','"+
					formatDateToBD(aux.getFechaDoc())+"','"+
					aux.getBancoId()+"','"+
					aux.getNumeroDoc()+"','"+
					aux.getNumeroCuenta()+"','"+
					aux.getValorDoc()+"','C', datetime('now','localtime'))";
			querys.add(sqlSentence);
			sqlSentence="INSERT INTO esd_cobro_pago_det VALUES('"+
					aux.getIdentifiicadorDetalle()+"','"+
					encabezado.getCobroId()+"','"+
					aux.getIdentificadoPago()+"','"+
					aux.getIdentifiicadorDetalle()+"','"+
					aux.getTipoPagoId()+"','"+
					formatDateToBD(aux.getFechaDoc())+"','"+
					aux.getBancoId()+"','"+
					aux.getNumeroDoc()+"','"+
					aux.getNumeroCuenta()+"','"+
					aux.getValorDoc()+"')";
			querys.add(sqlSentence);

		}
		return;
	}
	
	private void escritura_cruce_pagos(){
		String sqlSentence="";
		int consecutivo=0;
		for(C_cruce_pagos aux:cruce_pagos){
			consecutivo=DBAdapter.getConsecutivoRegistroCrucePagos();
			sqlSentence="INSERT INTO esu_cobro_cruce_pago VALUES('"+
					consecutivo+"','"+
					encabezado.getCobroId()+"','"+
					aux.getTipoDoc()+"','"+
					aux.getDocId()+"','"+
					aux.getIdentificador_pago()+"','"+
					aux.getFormaPagoId()+"','"+
					aux.getValorCruzado()+"','"+
					aux.getCodigoMovTipoDoc()+"','"+
					aux.getNumeroRelacion()+"','C', datetime('now','localtime'))";
			querys.add(sqlSentence);
			sqlSentence="INSERT INTO esd_cobro_cruce_pago VALUES('"+
					consecutivo+"','"+
					encabezado.getCobroId()+"','"+
					aux.getTipoDoc()+"','"+
					aux.getDocId()+"','"+
					aux.getIdentificador_pago()+"','"+
					aux.getFormaPagoId()+"','"+
					aux.getValorCruzado()+"','"+
					aux.getCodigoMovTipoDoc()+"','"+
					aux.getNumeroRelacion()+"')";
			querys.add(sqlSentence);

		}
		return;
	}

	private void escritura_cruce_docs_negativos(){
		String sqlSentence="";
		int consecutivo=0;
		for(C_cruce_docs_negativos aux:cruce_docs_negativos){
			consecutivo=DBAdapter.getConsecutivoRegistroCruceDocsNeg();
			sqlSentence="INSERT INTO esu_cobro_cruce_docs_neg VALUES('"+
					consecutivo+"','"+
					encabezado.getCobroId()+"','"+
					aux.getTipoDoc()+"','"+
					aux.getDocId()+"','"+
					aux.getTipoDocNeg()+"','"+
					aux.getDocIdNeg()+"','"+
					aux.getValorCruzado()+"','"+
					formatDateToBD(aux.getFechaDoc())+"','"+
					aux.getSaldoAnterior()+"','"+
					aux.getFormaPagoId()+"','C', datetime('now','localtime'))";
			querys.add(sqlSentence);
			sqlSentence="INSERT INTO esd_cobro_cruce_docs_neg VALUES('"+
					consecutivo+"','"+
					encabezado.getCobroId()+"','"+
					aux.getTipoDoc()+"','"+
					aux.getDocId()+"','"+
					aux.getTipoDocNeg()+"','"+
					aux.getDocIdNeg()+"','"+
					aux.getValorCruzado()+"','"+
					formatDateToBD(aux.getFechaDoc())+"','"+
					aux.getSaldoAnterior()+"','"+
					aux.getFormaPagoId()+"')";
			querys.add(sqlSentence);

		}
		return;
	}

	private void escritura_cruce_consignacion(){
		String sqlSentence="";
		int consecutivo=0;
		for(C_cruce_consignaciones aux:cruce_consignaciones){
			consecutivo=DBAdapter.getConsecutivoRegistroCruceConsig();
			sqlSentence="INSERT INTO esu_cobro_cruce_consignacion VALUES('"+
					consecutivo+"','"+
					encabezado.getCobroId()+"','"+
					aux.getTipoDoc()+"','"+
					aux.getDocId()+"','"+
					aux.getIdentificador_pago()+"','"+
					aux.getIdentificadorDetalle()+"','"+
					aux.getFormaPagoId()+"','"+
					aux.getValorCruzado()+"','"+
					aux.getCodigoMovTipoDoc()+"','"+
					aux.getNumeroRelacion()+"','C', datetime('now','localtime'))";
			querys.add(sqlSentence);
			sqlSentence="INSERT INTO esd_cobro_cruce_consignacion VALUES('"+
					consecutivo+"','"+
					encabezado.getCobroId()+"','"+
					aux.getTipoDoc()+"','"+
					aux.getDocId()+"','"+
					aux.getIdentificador_pago()+"','"+
					aux.getIdentificadorDetalle()+"','"+
					aux.getFormaPagoId()+"','"+
					aux.getValorCruzado()+"','"+
					aux.getCodigoMovTipoDoc()+"','"+
					aux.getNumeroRelacion()+"')";
			querys.add(sqlSentence);
		}
		return;
	}
	
	private void escritura_diferencia(){
		String sqlSentence="";
		String var_tipo_diferencia="";
		float var_Valor_diferencia=0;
		int counterDoc=0;
		int pos=-1;
		int consecutivo=0;
		C_documentos auxDoc=null;
		String var_codimotd=""; 
		String var_tipo_relacion="";
		Cursor esd_parametro_diferencia=DBAdapter.getEsdParametroDiferencia();
		if(encabezado.getMenorValorPago() > 0){
			for(C_documentos aux:documentos){
				if(aux.getValorProceso()>0){
					auxDoc=aux;
					counterDoc++;
				}
			}
			if(counterDoc<1){
				Utility.showMessage(context, getString(R.string.SFV_0006));
				return;
			}else if(counterDoc==1){
			    var_tipo_diferencia = "<";
			    var_Valor_diferencia = encabezado.getMenorValorPago();
			}else{
			Utility.showMessage(context, getString(R.string.SFV_0005));
				return;
			}

		}else{
//			Collections.sort(documentos,new OrderByDate());
			Collections.sort(documentos,new OrderByDateReverse());
			var_tipo_diferencia = ">";
			var_Valor_diferencia = encabezado.getMayorValorPago();
		}
		if(auxDoc==null){
			auxDoc=documentos.get(0);
		}
	if(esd_parametro_diferencia.moveToFirst()){
			if(auxDoc.getDiferenciaEsEfectivo().equalsIgnoreCase("S")){
				if(var_tipo_diferencia.equalsIgnoreCase("<")){
					var_codimotd=esd_parametro_diferencia.getString(esd_parametro_diferencia.getColumnIndex("cmotd_menor_valor_efectivo")); 
					var_tipo_relacion=esd_parametro_diferencia.getString(esd_parametro_diferencia.getColumnIndex("ctire_menor_valor_efectivo"));
				}else{
					var_codimotd=esd_parametro_diferencia.getString(esd_parametro_diferencia.getColumnIndex("cmotd_mayor_valor_efectivo")); 
					var_tipo_relacion=esd_parametro_diferencia.getString(esd_parametro_diferencia.getColumnIndex("ctire_mayor_valor_efectivo"));
				}
			}else{
				if(auxDoc.getDiferenciaEsPosfechado().equalsIgnoreCase("S")){
					if(var_tipo_diferencia.equalsIgnoreCase("<")){
						var_codimotd=esd_parametro_diferencia.getString(esd_parametro_diferencia.getColumnIndex("cmotd_menor_valor_cheque_pos")); 
						var_tipo_relacion=esd_parametro_diferencia.getString(esd_parametro_diferencia.getColumnIndex("ctire_menor_valor_cheque_pos"));
					}else{
						var_codimotd=esd_parametro_diferencia.getString(esd_parametro_diferencia.getColumnIndex("cmotd_mayor_valor_cheque_pos")); 
						var_tipo_relacion=esd_parametro_diferencia.getString(esd_parametro_diferencia.getColumnIndex("ctire_mayor_valor_cheque_pos"));
					}
				}else{
					if(var_tipo_diferencia.equalsIgnoreCase("<")){
						var_codimotd=esd_parametro_diferencia.getString(esd_parametro_diferencia.getColumnIndex("cmotd_menor_valor_cheque_dia")); 
						var_tipo_relacion=esd_parametro_diferencia.getString(esd_parametro_diferencia.getColumnIndex("ctire_menor_valor_cheque_dia"));
					}else{
						var_codimotd=esd_parametro_diferencia.getString(esd_parametro_diferencia.getColumnIndex("cmotd_mayor_valor_cheque_dia")); 
						var_tipo_relacion=esd_parametro_diferencia.getString(esd_parametro_diferencia.getColumnIndex("ctire_mayor_valor_cheque_dia"));
					}
				}
			}
		}
		consecutivo=DBAdapter.getConsecutivoRegistroDiferenciaCobros();
		var_tipo_relacion=var_tipo_relacion+DBAdapter.getPagoNumeroRelacion();
		
//		Log.i("info","auxDoc.getDiferenciaUltIdPago "+String.valueOf(auxDoc.getDiferenciaUltIdPago()));
		
		sqlSentence="INSERT INTO esu_cobro_diferencia VALUES('"+
				consecutivo+"','"+
				encabezado.getCobroId()+"','"+
				auxDoc.getTipoDoc()+"','"+
				auxDoc.getDocId()+"','"+
				var_tipo_diferencia+"','"+
				var_Valor_diferencia+"','"+
				var_codimotd+"','"+
				var_tipo_relacion+"','"+
				auxDoc.getDiferenciaUltIdPago()+"','"+
				auxDoc.getDiferenciaUltIdPagoDet()+"','N','C', datetime('now','localtime'))";
		querys.add(sqlSentence);
		sqlSentence="INSERT INTO esd_cobro_diferencia VALUES('"+
				consecutivo+"','"+
				encabezado.getCobroId()+"','"+
				auxDoc.getTipoDoc()+"','"+
				auxDoc.getDocId()+"','"+
				var_tipo_diferencia+"','"+
				var_Valor_diferencia+"','"+
				var_codimotd+"','"+
				var_tipo_relacion+"','"+
				auxDoc.getDiferenciaUltIdPago()+"','"+
				auxDoc.getDiferenciaUltIdPagoDet()+"','N')";
		querys.add(sqlSentence);
		return;
	}
	
	private boolean hilo_desnormalizar_cobros(int cobroId){
		boolean res=true;
		Cursor esu_cobro;
		Cursor esu_cobro_documento;
		Cursor esu_cobro_cruce_pago;
		Cursor esu_cobro_cruce_consignacion;
		Cursor esu_cobro_cruce_docs_neg;
		Cursor esu_cobro_diferencia;
		String cobro_id=String.valueOf(cobroId);
		esu_cobro=DBAdapter.getEsuCobro(cobro_id);
		if(esu_cobro.moveToFirst()){
			esu_cobro_documento=DBAdapter.getEsuCobrosDocumentos(cobro_id);
			if(esu_cobro_documento.moveToFirst()){
				do{
					esu_cobro_cruce_pago=DBAdapter.getEsuCobroCrucePago(cobro_id, esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("documento_id")));
					if(esu_cobro_cruce_pago.moveToFirst()){
						cobro_cruce_pago(cobro_id,esu_cobro,esu_cobro_documento,esu_cobro_cruce_pago);
					}
					esu_cobro_cruce_consignacion=DBAdapter.getEsuCobroCruceConsignacion(cobro_id,esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("documento_id")));
					if(esu_cobro_cruce_consignacion.moveToFirst()){
						Cobro_cruce_consignacion(cobro_id,esu_cobro,esu_cobro_documento,esu_cobro_cruce_consignacion);
					}
				}while(esu_cobro_documento.moveToNext());		
				esu_cobro_cruce_docs_neg=DBAdapter.getEsuCobroCruceDocsNeg(cobro_id);
				if(esu_cobro_cruce_docs_neg.moveToFirst()){
					cobro_cruce_docs_neg(esu_cobro,esu_cobro_cruce_docs_neg);
				}
				esu_cobro_diferencia= DBAdapter.getEsuCobroDiferencia(cobro_id);
				if(esu_cobro_diferencia.moveToFirst()){
					cobro_diferencia(cobro_id,esu_cobro,esu_cobro_diferencia);
				}			
			}else{
				Log.e("info",getString(R.string.SFV_0010));
				res=false;
				return res;
			}
		}else{
			Log.e("info",getString(R.string.SFV_0009));
			res=false;
			return res;
		}
		return res;
	}	
	
	private void cobro_cruce_pago(String cobro_id, Cursor esu_cobro, Cursor esu_cobro_documento, Cursor esu_cobro_cruce_pago){
		Cursor esu_cobro_pago;
		Cursor esd_tipo_pago;
		String var_es_cheque="";
		String var_es_transferencia="";
		float var_valor_neto=0;
		int consecutivo=0;
		String sqlSentence="";
		ArrayList<String> querys=new ArrayList<String>();
		if(esu_cobro_cruce_pago.moveToFirst()){
			do{
				esu_cobro_pago=DBAdapter.getEsuCobroPago(esu_cobro_cruce_pago.getString(esu_cobro_cruce_pago.getColumnIndex("consecutivo_pago")));
				if(esu_cobro_pago.moveToFirst()){
					esd_tipo_pago=DBAdapter.getTiposPago();
					if(esd_tipo_pago.moveToFirst()){
						do{
							if(esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("tipo_pago_id")).equalsIgnoreCase(esu_cobro_pago.getString(esu_cobro_pago.getColumnIndex("tipo_pago_id")))){
								var_es_cheque = esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("es_cheque"));
								var_es_transferencia = esd_tipo_pago.getString(esd_tipo_pago.getColumnIndex("es_transferencia"));
								consecutivo=DBAdapter.getConsecutivoRegistroDiferenciaCobros();
								var_valor_neto= determinar_valor_neto(cobro_id,
										esu_cobro_cruce_pago.getFloat(esu_cobro_cruce_pago.getColumnIndex("valor_cruzado")),
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("tipo_documento")),
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("documento_id")),
										esu_cobro_cruce_pago.getInt(esu_cobro_cruce_pago.getColumnIndex("consecutivo_pago")),
										0);
//								Log.i("info","var_valor_neto "+var_valor_neto);
								sqlSentence="INSERT INTO esu_cobro_desnormalizado VALUES('"+
										consecutivo+"','"+
										esu_cobro.getString(esu_cobro.getColumnIndex("cobro_id"))+"','"+
										esu_cobro.getString(esu_cobro.getColumnIndex("cliente_id"))+"','"+
										esu_cobro.getString(esu_cobro.getColumnIndex("fecha_cobro"))+"','"+
										esu_cobro.getString(esu_cobro.getColumnIndex("maquina_id"))+"','"+
										esu_cobro_cruce_pago.getString(esu_cobro_cruce_pago.getColumnIndex("cod_mov_tipo_doc"))+"','"+
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("tipo_documento"))+"','"+
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("documento_id"))+"','"+
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_documento"))+"','"+
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("saldo_anterior"))+"','"+
										esu_cobro_pago.getString(esu_cobro_pago.getColumnIndex("tipo_pago_id"))+"','"+
										esu_cobro_cruce_pago.getString(esu_cobro_cruce_pago.getColumnIndex("forma_pago_id"))+"','"+
										esu_cobro_cruce_pago.getString(esu_cobro_cruce_pago.getColumnIndex("valor_cruzado"))+"','"+
										var_valor_neto+"','"+
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("valor_desc_pronto_pago"))+"','" +
										"1900-01-01','','','','"+
										((var_es_cheque.equalsIgnoreCase("S"))? esu_cobro_pago.getString(esu_cobro_pago.getColumnIndex("numero_documento")):"") +"','"+
										((var_es_cheque.equalsIgnoreCase("S"))? esu_cobro_pago.getString(esu_cobro_pago.getColumnIndex("banco_id_documento")):"") +"','"+
										((var_es_cheque.equalsIgnoreCase("S"))? esu_cobro_pago.getString(esu_cobro_pago.getColumnIndex("cuenta_documento")):"") +"','"+
										((var_es_cheque.equalsIgnoreCase("S"))? esu_cobro_pago.getString(esu_cobro_pago.getColumnIndex("fecha_documento")):"1900-01-01") +"','"+
										((var_es_transferencia.equalsIgnoreCase("S"))? esu_cobro_pago.getString(esu_cobro_pago.getColumnIndex("numero_documento")):"") +"','"+
										((var_es_transferencia.equalsIgnoreCase("S"))? esu_cobro_pago.getString(esu_cobro_pago.getColumnIndex("banco_id_documento")):"") +"','"+
										((var_es_transferencia.equalsIgnoreCase("S"))? esu_cobro_pago.getString(esu_cobro_pago.getColumnIndex("cuenta_documento")):"") +"','"+
										((var_es_transferencia.equalsIgnoreCase("S"))? esu_cobro_pago.getString(esu_cobro_pago.getColumnIndex("fecha_documento")):"1900-01-01") +"','"+
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("tipo_documento"))+"','"+
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("documento_id"))+"','"+
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("motivo_abono_id"))+"','"+
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_cancela_saldo"))+"','"+
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("numero_guia_devolucion"))+"','"+
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_guia_devolucion"))+"','"+
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("numero_devolucion"))+"','"+
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_devolucion"))+"','"+
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("numero_solicitud_nc"))+"','"+
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_solicitud_nc"))+"','"+
										esu_cobro_cruce_pago.getString(esu_cobro_cruce_pago.getColumnIndex("numero_relacion"))+"','"+
										esu_cobro_pago.getString(esu_cobro_pago.getColumnIndex("valor_consignacion_ef"))+"','"+
										esu_cobro_pago.getString(esu_cobro_pago.getColumnIndex("valor_consignacion_ch"))+"','"+
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("valor_doc_menos_desc"))+"','"+
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("numero_memorando"))+"','"+
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_memorando"))+"','"+
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_seguimiento"))+"','','C', datetime('now','localtime'))";
								querys.add(sqlSentence);
								if(esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("observaciones_abono")).length()>0){
									desnormalizar_observacion(esu_cobro.getString(esu_cobro.getColumnIndex("cobro_id")),
										esu_cobro_cruce_pago.getString(esu_cobro_cruce_pago.getColumnIndex("cod_mov_tipo_doc")),
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("tipo_documento")),
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("documento_id")),
										"ABONO",
										esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("observaciones_abono")));
								}
								if(esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("observaciones_cobro")).length()>0){
									desnormalizar_observacion(esu_cobro.getString(esu_cobro.getColumnIndex("cobro_id")),
											esu_cobro_cruce_pago.getString(esu_cobro_cruce_pago.getColumnIndex("cod_mov_tipo_doc")),
											esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("tipo_documento")),
											esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("documento_id")),
											"COBRO",
											esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("observaciones_cobro")));
								}
								break;
							}
						}while(esd_tipo_pago.moveToNext());
					}else{
						Log.e("info", getString(R.string.SFV_0008));
					}
				}else{
					Log.e("info",getString(R.string.SFV_0011));
					return;
				}
			}while(esu_cobro_cruce_pago.moveToNext());
		}
		DBAdapter.insertQuerys(querys);
		return;
	}
	
	private void Cobro_cruce_consignacion(String cobro_id, Cursor esu_cobro, Cursor esu_cobro_documento, Cursor esu_cobro_cruce_consignacion){
		Cursor esu_cobro_pago;
		Cursor esu_cobro_pago_det;
		float var_valor_neto=0;
		int consecutivo=0;
		String sqlSentence="";
		ArrayList<String> querys=new ArrayList<String>();
		if(esu_cobro_cruce_consignacion.moveToFirst()){
			do{
				esu_cobro_pago=DBAdapter.getEsuCobroPago(esu_cobro_cruce_consignacion.getString(esu_cobro_cruce_consignacion.getColumnIndex("consecutivo_pago")));
				if(esu_cobro_pago.moveToFirst()){
					esu_cobro_pago_det=DBAdapter.getEsuCobroPagoDet(cobro_id,esu_cobro_cruce_consignacion.getString(esu_cobro_cruce_consignacion.getColumnIndex("consecutivo_pago")) , esu_cobro_cruce_consignacion.getString(esu_cobro_cruce_consignacion.getColumnIndex("consecutivo_detalle")));
					if(esu_cobro_pago_det.moveToFirst()){
						do{
							consecutivo=DBAdapter.getConsecutivoRegistroDiferenciaCobros();
							var_valor_neto= determinar_valor_neto(cobro_id,
								esu_cobro_cruce_consignacion.getFloat(esu_cobro_cruce_consignacion.getColumnIndex("valor_cruzado")),
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("tipo_documento")),
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("documento_id")),
								esu_cobro_cruce_consignacion.getInt(esu_cobro_cruce_consignacion.getColumnIndex("consecutivo_pago")),
								esu_cobro_cruce_consignacion.getInt(esu_cobro_cruce_consignacion.getColumnIndex("consecutivo_detalle")));
							sqlSentence="INSERT INTO esu_cobro_desnormalizado VALUES('"+
								consecutivo+"','"+
								esu_cobro.getString(esu_cobro.getColumnIndex("cobro_id"))+"','"+
								esu_cobro.getString(esu_cobro.getColumnIndex("cliente_id"))+"','"+
								esu_cobro.getString(esu_cobro.getColumnIndex("fecha_cobro"))+"','"+
								esu_cobro.getString(esu_cobro.getColumnIndex("maquina_id"))+"','"+
								esu_cobro_cruce_consignacion.getString(esu_cobro_cruce_consignacion.getColumnIndex("cod_mov_tipo_doc"))+"','"+
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("tipo_documento"))+"','"+
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("documento_id"))+"','"+
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_documento"))+"','"+
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("saldo_anterior"))+"','"+
								esu_cobro_pago.getString(esu_cobro_pago.getColumnIndex("tipo_pago_id"))+"','"+
								esu_cobro_cruce_consignacion.getString(esu_cobro_cruce_consignacion.getColumnIndex("forma_pago_id"))+"','"+
								esu_cobro_cruce_consignacion.getString(esu_cobro_cruce_consignacion.getColumnIndex("valor_cruzado"))+"','"+
								var_valor_neto+"','"+
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("valor_desc_pronto_pago"))+"','" +
								esu_cobro_pago.getString(esu_cobro_pago.getColumnIndex("fecha_documento"))+"','" +
								esu_cobro_pago.getString(esu_cobro_pago.getColumnIndex("banco_id_documento"))+"','" +
								esu_cobro_pago.getString(esu_cobro_pago.getColumnIndex("numero_documento"))+"','" +
								esu_cobro_pago.getString(esu_cobro_pago.getColumnIndex("cuenta_documento"))+"','" +
								esu_cobro_pago_det.getString(esu_cobro_pago_det.getColumnIndex("numero_documento")) +"','"+
								esu_cobro_pago_det.getString(esu_cobro_pago_det.getColumnIndex("banco_id_documento")) +"','"+
								esu_cobro_pago_det.getString(esu_cobro_pago_det.getColumnIndex("cuenta_documento")) +"','"+
								esu_cobro_pago_det.getString(esu_cobro_pago_det.getColumnIndex("fecha_documento")) +"'," +
								"'','','','1900-01-01','"+
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("tipo_documento"))+"','"+
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("documento_id"))+"','"+
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("motivo_abono_id"))+"','"+
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_cancela_saldo"))+"','"+
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("numero_guia_devolucion"))+"','"+
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_guia_devolucion"))+"','"+
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("numero_devolucion"))+"','"+
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_devolucion"))+"','"+
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("numero_solicitud_nc"))+"','"+
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_solicitud_nc"))+"','"+
								esu_cobro_cruce_consignacion.getString(esu_cobro_cruce_consignacion.getColumnIndex("numero_relacion"))+"','"+
								esu_cobro_pago.getString(esu_cobro_pago.getColumnIndex("valor_consignacion_ef"))+"','"+
								esu_cobro_pago.getString(esu_cobro_pago.getColumnIndex("valor_consignacion_ch"))+"','"+
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("valor_doc_menos_desc"))+"','"+
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("numero_memorando"))+"','"+
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_memorando"))+"','"+
								esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_seguimiento"))+"','','C', datetime('now','localtime'))";
							querys.add(sqlSentence);
							if(esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("observaciones_abono")).length()>0){
								desnormalizar_observacion(esu_cobro.getString(esu_cobro.getColumnIndex("cobro_id")),
									esu_cobro_cruce_consignacion.getString(esu_cobro_cruce_consignacion.getColumnIndex("cod_mov_tipo_doc")),
									esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("tipo_documento")),
									esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("documento_id")),
									"ABONO",
									esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("observaciones_abono")));
							}
							if(esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("observaciones_cobro")).length()>0){
								desnormalizar_observacion(esu_cobro.getString(esu_cobro.getColumnIndex("cobro_id")),
									esu_cobro_cruce_consignacion.getString(esu_cobro_cruce_consignacion.getColumnIndex("cod_mov_tipo_doc")),
									esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("tipo_documento")),
									esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("documento_id")),
									"COBRO",
									esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("observaciones_cobro")));
							}
							break;
						}while(esu_cobro_pago_det.moveToNext());
					}else{
						Log.e("info", getString(R.string.SFV_0013));
					}
				}else{
					Log.e("info",getString(R.string.SFV_0012));
					return;
				}
			}while(esu_cobro_cruce_consignacion.moveToNext());
		}
		DBAdapter.insertQuerys(querys);
		return;
	}
	
	private void cobro_cruce_docs_neg(Cursor esu_cobro, Cursor esu_cobro_cruce_docs_neg){
		String sqlSentence="";
		String var_tipo_pago_id="";
		ArrayList<String> querys=new ArrayList<String>();
		int consecutivo;
		var_tipo_pago_id=DBAdapter.getTipoPagoIdDocsNeg();
		C_adicionales_cobro datosAdicionales;
		Cursor esu_cobro_documento=null;
		if(esu_cobro_cruce_docs_neg.moveToFirst() && esu_cobro.moveToFirst()){
			do{
				consecutivo=DBAdapter.getConsecutivoRegistroDiferenciaCobros();
				datosAdicionales = datos_adicionales_cruce_docs_neg(
						esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("cobro_id")),
						esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("tipo_documento")),
						esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("documento_id")));
				esu_cobro_documento=DBAdapter.getEsuCobroDocumento(
						esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("cobro_id")),
						esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("tipo_documento")),
						esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("documento_id")));
				if(esu_cobro_documento.moveToFirst()){
					if(esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("motivo_abono_id"))==null 
						|| esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("motivo_abono_id")).equals("")){
						sqlSentence="INSERT INTO esu_cobro_desnormalizado (id,cobro_id,cliente_id,fecha_cobro,maquina_id,cod_mov_tipo_doc," +
							"tipo_documento,documento_id,fecha_documento,saldo_pendiente,tipo_pago_id,forma_pago_id,valor_cobrado,valor_neto," +
							"valor_desc_pronto_pago,fecha_consignacion,banco_id_consignacion,numero_consignacion,cuenta_consignacion,numero_cheque," +
							"banco_id_cheque,cuenta_cheque,fecha_cheque,numero_transferencia,banco_id_transferencia,cuenta_transferencia,fecha_transferencia," +
							"tipo_documento_ref,documento_id_ref,motivo_abono_id,fecha_cancela_saldo,numero_guia_devolucion,numero_devolucion,fecha_devolucion," +
							"numero_solicitud_nc,fecha_solicitud_nc,numero_relacion,valor_consignacion_EF,valor_consignacion_CH,valor_doc_menos_desc,numero_memorando," +
							"fecha_memorando,fecha_seguimiento,fecha_guia_devolucion,estado_registro,fecha_registro) VALUES('"+
							consecutivo+"','"+
							esu_cobro.getString(esu_cobro.getColumnIndex("cobro_id"))+"','"+
							esu_cobro.getString(esu_cobro.getColumnIndex("cliente_id"))+"','"+
							esu_cobro.getString(esu_cobro.getColumnIndex("fecha_cobro"))+"','"+
							esu_cobro.getString(esu_cobro.getColumnIndex("maquina_id"))+"','"+
							datosAdicionales.getCodigoMovTipoDoc()+"','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("tipo_documento_neg"))+"','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("documento_id_neg"))+"','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("fecha_documento"))+"','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("saldo_anterior"))+"'," +
							"'','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("forma_pago_id"))+"','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("valor_cruzado"))+"','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("valor_cruzado"))+"'," +
							"'0','" +
							"1900-01-01'," +
							"''," +
							"''," +
							"''," +
							"''," +
							"''," +
							"'','" +
							"1900-01-01'," +
							"''," +
							"''," +
							"''," +
							"'1900-01-01','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("tipo_documento"))+"','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("documento_id"))+"'," +
							"'','" +
							"1900-01-01'," +
							"''," +
							"''," +
							"'1900-01-01'," +
							"''," +
							"'1900-01-01','"+
							datosAdicionales.getNumeroRelacion()+"'," +
							"'0'," +
							"'0','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("saldo_anterior"))+"'," +
							"''," +
							"'1900-01-01'," +
							"'1900-01-01'," +
							"'1900-01-01'," +
							"'C'," +
							"datetime('now','localtime'))";
					}else{
						sqlSentence="INSERT INTO esu_cobro_desnormalizado (id,cobro_id,cliente_id,fecha_cobro,maquina_id,cod_mov_tipo_doc," +
							"tipo_documento,documento_id,fecha_documento,saldo_pendiente,tipo_pago_id,forma_pago_id,valor_cobrado,valor_neto," +
							"valor_desc_pronto_pago,fecha_consignacion,banco_id_consignacion,numero_consignacion,cuenta_consignacion,numero_cheque," +
							"banco_id_cheque,cuenta_cheque,fecha_cheque,numero_transferencia,banco_id_transferencia,cuenta_transferencia,fecha_transferencia," +
							"tipo_documento_ref,documento_id_ref,motivo_abono_id,fecha_cancela_saldo,numero_guia_devolucion,numero_devolucion,fecha_devolucion," +
							"numero_solicitud_nc,fecha_solicitud_nc,numero_relacion,valor_consignacion_EF,valor_consignacion_CH,valor_doc_menos_desc,numero_memorando," +
							"fecha_memorando,fecha_seguimiento,fecha_guia_devolucion,estado_registro,fecha_registro) VALUES('"+
							consecutivo+"','"+
							esu_cobro.getString(esu_cobro.getColumnIndex("cobro_id"))+"','"+
							esu_cobro.getString(esu_cobro.getColumnIndex("cliente_id"))+"','"+
							esu_cobro.getString(esu_cobro.getColumnIndex("fecha_cobro"))+"','"+
							esu_cobro.getString(esu_cobro.getColumnIndex("maquina_id"))+"','"+
							datosAdicionales.getCodigoMovTipoDoc()+"','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("tipo_documento_neg"))+"','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("documento_id_neg"))+"','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("fecha_documento"))+"','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("saldo_anterior"))+"'," +
							"'','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("forma_pago_id"))+"','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("valor_cruzado"))+"','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("valor_cruzado"))+"'," +
							"'0','1900-01-01','','','','','','','1900-01-01','','','','1900-01-01','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("tipo_documento"))+"','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("documento_id"))+"','" +
							esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("motivo_abono_id"))+"','" +
							esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_cancela_saldo"))+"','"+
							esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("numero_guia_devolucion"))+"','"+
							esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("numero_devolucion"))+"','"+
							esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_devolucion"))+"','"+
							esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("numero_solicitud_nc"))+"','"+
							esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_solicitud_nc"))+"','" +
							datosAdicionales.getNumeroRelacion()+"','0','0','"+
							esu_cobro_cruce_docs_neg.getString(esu_cobro_cruce_docs_neg.getColumnIndex("saldo_anterior"))+"','"+
							esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("numero_memorando"))+"','"+
							esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_memorando"))+"','"+
							esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_seguimiento"))+"'," +
							"'1900-01-01'," +
							"'C'," +
							"datetime('now','localtime'))";
					}
					querys.add(sqlSentence);
					if(esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("observaciones_cobro")).length()>0){
						desnormalizar_observacion(
							esu_cobro.getString(esu_cobro.getColumnIndex("cobro_id")),
							datosAdicionales.getCodigoMovTipoDoc(),
							esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("tipo_documento")),
							esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("documento_id")),
							"COBRO",
							esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("observaciones_cobro")));
					}
				}
			}while(esu_cobro_cruce_docs_neg.moveToNext());
		}
		DBAdapter.insertQuerys(querys);
		return;
	}
	
	private C_adicionales_cobro datos_adicionales_cruce_docs_neg(String cobro_id, String tipoDoc, String docId){
		C_adicionales_cobro res= new C_adicionales_cobro();
		Cursor esu_cobro_cruce_pago=null;
		Cursor esu_cobro_cruce_consignacion=null;
		Cursor esu_cobro_documento=null;
		Cursor esd_para_codimotd=null;
		Cursor cruce_pago_cruce_consignacion=null;
		esu_cobro_cruce_pago=DBAdapter.getEsuCobroCrucePago(cobro_id,docId);
		if(esu_cobro_cruce_pago.moveToFirst()){
			esu_cobro_cruce_consignacion=DBAdapter.getEsuCobroCruceConsignacion(cobro_id, docId);
			if(esu_cobro_cruce_consignacion.moveToFirst()){
				cruce_pago_cruce_consignacion=DBAdapter.getEsuCobroUnionCrucePagoCruceConsignacion(cobro_id, docId);
				if(cruce_pago_cruce_consignacion.moveToFirst()){
					res.setCodigoMovTipoDoc(cruce_pago_cruce_consignacion.getString(0));
					res.setNumeroRelacion(cruce_pago_cruce_consignacion.getString(1));
				}
			}else{
				res.setCodigoMovTipoDoc(esu_cobro_cruce_pago.getString(esu_cobro_cruce_pago.getColumnIndex("cod_mov_tipo_doc")));
				res.setNumeroRelacion(esu_cobro_cruce_pago.getString(esu_cobro_cruce_pago.getColumnIndex("numero_relacion")));
			}
		}else{
			esu_cobro_cruce_consignacion=DBAdapter.getEsuCobroCruceConsignacion(cobro_id, docId);
			if(esu_cobro_cruce_consignacion.moveToFirst()){
				res.setCodigoMovTipoDoc(esu_cobro_cruce_consignacion.getString(esu_cobro_cruce_consignacion.getColumnIndex("cod_mov_tipo_doc")));
				res.setNumeroRelacion(esu_cobro_cruce_consignacion.getString(esu_cobro_cruce_consignacion.getColumnIndex("numero_relacion")));
			}else{
				esu_cobro_documento=DBAdapter.getEsuCobroDocumento(cobro_id, tipoDoc, docId);
				if(esu_cobro_documento.moveToFirst()){
					if(esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("motivo_abono_id")) == null || 
						esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("motivo_abono_id")).equals("")){
						esd_para_codimotd=DBAdapter.getEsdParaCmotd("N", "", "", "", "S", "", "");
					}else{
						esd_para_codimotd=DBAdapter.getEsdParaCmotd("S", "", "", "", "S", "", "");
					}
					if(esd_para_codimotd.moveToFirst()){
						res.setCodigoMovTipoDoc(esd_para_codimotd.getString(esd_para_codimotd.getColumnIndex("cod_mov_tipo_doc")));
						res.setNumeroRelacion(esd_para_codimotd.getString(esd_para_codimotd.getColumnIndex("cod_numero_relacion"))+DBAdapter.getPagoNumeroRelacion());							
					}else{
						res.setCodigoMovTipoDoc(DBAdapter.getEsdParametroGeneralCodimotdSinIdentificar());
						res.setNumeroRelacion("");
					}
				}else{
					Log.e("info",getString(R.string.SFV_0017));
					res.setCodigoMovTipoDoc(DBAdapter.getEsdParametroGeneralCodimotdSinIdentificar());
					res.setNumeroRelacion("");
				}
				
			}
		}
		if(esu_cobro_cruce_pago!=null){
			esu_cobro_cruce_pago.close();
		}
		if(esu_cobro_cruce_consignacion!=null){
			esu_cobro_cruce_consignacion.close();
		}
		if(esu_cobro_documento!= null){
			esu_cobro_documento.close();
		}
		if(esd_para_codimotd!=null){
			esd_para_codimotd.close();
		}
		if(cruce_pago_cruce_consignacion!=null){
			cruce_pago_cruce_consignacion.close();
		}
		return res;
	}

	private void cobro_diferencia(String cobro_id,Cursor esu_cobro, Cursor esu_cobro_diferencia){
		ArrayList<String> querys=new ArrayList<String>();
		Cursor esu_cobro_documento;
		String sqlSentence="";
		int consecutivo;
		String var_forma_pago_id;
		if(esu_cobro_diferencia.moveToFirst()){
			do{
				esu_cobro_documento=DBAdapter.getEsuCobrosDocumentos(cobro_id,esu_cobro_diferencia.getString(esu_cobro_diferencia.getColumnIndex("tipo_documento")),esu_cobro_diferencia.getString(esu_cobro_diferencia.getColumnIndex("documento_id")));
				if(esu_cobro_documento.moveToFirst()){
					consecutivo=DBAdapter.getConsecutivoRegistroDiferenciaCobros();
					var_forma_pago_id=DBAdapter.getFormaPagoIdDiferencia();
					sqlSentence="INSERT INTO esu_cobro_desnormalizado VALUES('"+
						consecutivo+"','"+
						esu_cobro.getString(esu_cobro.getColumnIndex("cobro_id"))+"','"+
						esu_cobro.getString(esu_cobro.getColumnIndex("cliente_id"))+"','"+
						esu_cobro.getString(esu_cobro.getColumnIndex("fecha_cobro"))+"','"+
						esu_cobro.getString(esu_cobro.getColumnIndex("maquina_id"))+"','"+
						esu_cobro_diferencia.getString(esu_cobro_diferencia.getColumnIndex("cod_mov_tipo_doc"))+"','"+
						esu_cobro_diferencia.getString(esu_cobro_diferencia.getColumnIndex("tipo_documento"))+"','"+
						esu_cobro_diferencia.getString(esu_cobro_diferencia.getColumnIndex("documento_id"))+"','"+
						esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("fecha_documento"))+"','"+
						esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("saldo_anterior"))+"','','"+
						var_forma_pago_id+"','"+
						esu_cobro_diferencia.getString(esu_cobro_diferencia.getColumnIndex("valor_diferencia"))+"','"+
						esu_cobro_diferencia.getString(esu_cobro_diferencia.getColumnIndex("valor_diferencia"))+"','0'," +
						"'1900-01-01','','','','','','','1900-01-01','','','','1900-01-01','"+
						esu_cobro_diferencia.getString(esu_cobro_diferencia.getColumnIndex("tipo_documento"))+"','"+
						esu_cobro_diferencia.getString(esu_cobro_diferencia.getColumnIndex("documento_id"))+"'," +
						"'','1900-01-01','','1900-01-01','','1900-01-01','','1900-01-01','"+
						esu_cobro_diferencia.getString(esu_cobro_diferencia.getColumnIndex("numero_relacion"))+"','0','0','0','','1900-01-01','1900-01-01','','C', datetime('now','localtime'))";
					querys.add(sqlSentence);
					if(esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("observaciones_cobro")).length()>0){
						desnormalizar_observacion(esu_cobro.getString(esu_cobro.getColumnIndex("cobro_id")),
	//						DBAdapter.getDatosAdicionales_CodMovTipoDoc() cod_mov_tipo_doc ,
							esu_cobro_diferencia.getString(esu_cobro_diferencia.getColumnIndex("cod_mov_tipo_doc"))	,
							esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("tipo_documento")),
							esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("documento_id")),
							"COBRO",
							esu_cobro_documento.getString(esu_cobro_documento.getColumnIndex("observaciones_cobro")));
					}
				}else{
					Log.e("info",getString(R.string.SFV_0014));
				}
			}while(esu_cobro_diferencia.moveToNext());
		}
		DBAdapter.insertQuerys(querys);
		return;
	}

	private float determinar_valor_neto(String cobro_id, float valor_cruzado, String tipoDoc, String docId, int idenPago, int idenDetalle){
		float res=0;
		float var_valor_docs_neg;
		float var_valor_diferencia;	
		Cursor esu_cobro_cruce_docs_neg=DBAdapter.getEsuCobroCruceDocsNegSUMValorCru(cobro_id, tipoDoc, docId);
		Cursor esu_cobro_diferencia=DBAdapter.getEsuCobroDiferencia(cobro_id, tipoDoc, docId, String.valueOf(idenPago), String.valueOf(idenDetalle), "N");
		if(esu_cobro_cruce_docs_neg.moveToFirst()){
//			Log.i("info","determinar_valor_neto si entro");
			var_valor_docs_neg=Math.abs(esu_cobro_cruce_docs_neg.getFloat(0));
		}else{
//			Log.i("info","determinar_valor_neto no entro");
			var_valor_docs_neg=0;
		}
		
		if(esu_cobro_diferencia.moveToFirst()){
//			Log.i("info","si entro cobro dif");
			if(esu_cobro_diferencia.getString(esu_cobro_diferencia.getColumnIndex("tipo_diferencia")).equalsIgnoreCase(">")){
//				Log.i("info","si entro dif");
				var_valor_diferencia=esu_cobro_diferencia.getFloat(esu_cobro_diferencia.getColumnIndex("valor_diferencia"))*(-1);
			}else{
//				Log.i("info","no entro dif");
				var_valor_diferencia=esu_cobro_diferencia.getFloat(esu_cobro_diferencia.getColumnIndex("valor_diferencia"));
			}
			DBAdapter.updateEsuCobroDifDifApli(cobro_id, "S");
		}else{
//			Log.i("info","no entro cobro dif");
			var_valor_diferencia = 0;
		}
//		Log.i("info","valor_cruzado "+valor_cruzado);
//		Log.i("info","var_valor_docs_neg "+var_valor_docs_neg);
//		Log.i("info","var_valor_diferencia "+var_valor_diferencia);
		res=valor_cruzado+var_valor_docs_neg+var_valor_diferencia;
//		Log.i("info","res "+res);
		return res;
	}
	
	private void desnormalizar_observacion(String cobro_id, String cod_mov_tipo_doc, String tipoDoc, String docId, String tipoObs, String Obs){

		Cursor esu_cobro_obs_desnormalizado=DBAdapter.getEsuCobroObsDesnormalizado(cobro_id, cod_mov_tipo_doc, tipoDoc, docId, tipoObs);
		int consecutivo;
		ArrayList<String> querys=new ArrayList<String>();
		String sqlSentence="";
		if(esu_cobro_obs_desnormalizado.moveToFirst()){
			return;
		}else{
			consecutivo=DBAdapter.getConsecutivoRegistroEsuCobroObsDesnormalizado();
			sqlSentence="INSERT INTO esu_cobro_obs_desnormalizado VALUES('"+
					consecutivo+"','"+
					cobro_id+"','"+
					cod_mov_tipo_doc+"','"+
					tipoDoc+"','"+
					docId+"','"+
					tipoObs+"','"+
					Obs+"','C', datetime('now','localtime'))";
			querys.add(sqlSentence);
			DBAdapter.insertQuerys(querys);
		}
		return;
	}
	
    public static class OrderPagosByDate implements Comparator<C_pagos> {

        @Override
        public int compare(C_pagos arg0, C_pagos arg1) {
        	String date0=arg0.getFechaDoc();
        	String date1=arg1.getFechaDoc();
        	return date0.compareTo(date1);
        }
    }
	
    public static class OrderDocsByValue implements Comparator<C_documentos> {
        @Override
        public int compare(C_documentos arg0, C_documentos arg1) {
        	return (int)(arg0.getValorProceso()-arg1.getValorProceso());
        }
    }

    public static class OrderDocsByValueReves implements Comparator<C_documentos> {
        @Override
        public int compare(C_documentos arg0, C_documentos arg1) {
        	return (int)(arg1.getValorProceso()-arg0.getValorProceso());
        }
    }

    public static class OrderByDate implements Comparator<C_documentos> {
        @Override
        public int compare(C_documentos arg0, C_documentos arg1) {
        	String date0=arg0.getFechaDoc();
        	String date1=arg1.getFechaDoc();
        	return date0.compareTo(date1);
        }
    }

    public static class OrderByDateReverse implements Comparator<C_documentos> {
        @Override
        public int compare(C_documentos arg0, C_documentos arg1) {
        	String date0=arg0.getFechaDoc();
        	String date1=arg1.getFechaDoc();
        	return date1.compareTo(date0);
        }
    }

    private String formatDateToBD(String date){
    	String res="";
    	String [] auxDate=null;
    	try{
    		auxDate=date.split("/");
    		res=auxDate[2]+"-"+auxDate[0]+"-"+auxDate[1];
    	}catch(Exception e){
    		res="1900-01-01";
    	}
    	return res;
    }

	@Override
	public void onBackPressed() {}
}