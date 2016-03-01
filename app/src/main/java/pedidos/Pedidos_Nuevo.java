package pedidos;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import utilidades.Utility;
import visita.Visita_Menu;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.text.InputType;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import articulos.Articulos_Detalles_Descripcion;

@SuppressLint("DefaultLocale")
public class Pedidos_Nuevo extends Activity {
	static private final int REQUEST_EXIT = 0;
	static private final int REQUEST_UPDATE = 1;
	private View alertView;
	private AlertDialog alertDialog;
	private AlertDialog.Builder dialogBuilder;
	private AlertDialog.Builder dialogBuilderAux;
	private AlertDialog.Builder dialogBuilderDelete;
	private Intent i;
	private TextView total_input;
	private TextView view_head;
	private TextView galeria_titulo;
	private TextView n_pedido_input;
	private TextView saldo_actual_input;
	private TextView cupo_disponible_input;
	private TextView cond_pago_input;
	private TextView fecha_entrega_input;
	private ItaloDBAdapter DBAdapter;
	private Cursor cursorDetalle;
	private Cursor cursor_promocionnes;
	private Cursor cursor_familia;
	private Cursor cursor_subfamilia;
	private Cursor cursor_temporadas_spinner;
	private Cursor cursor;
	private Cursor cursor_encabezado;
	private Cursor cursor_pedidos_temporal;
	private Cursor cursorPrecio;
	private Cursor cursor_search;
	private Cursor cursor_aux;
	private GridView listProductos;
	private ListView listPedidoDetalle;
	private ProductosArrayAdapter adapter;
	private pedidoListaArrayAdapter productosAdapter;
	private ArrayList<Pedidos_producto> productos;
	private ArrayList<pedido_lista> pedidos;
	@SuppressWarnings("rawtypes")
	private ArrayList promoListas;
	private String subfamilia;
	private String subfamilia_id="";
	private String familia_id="";
	private String cliente_id;
	private String cliente_nombre;
	private String de;
	private String pedido_id;
    private String fecha;
    private String obs_pedido;
    private String obs_fact;
    private String visita_id;
    private String obs_compromisos;
	private Bundle extras;
	private double precioUnitario;
	private double iva;
	private Context context;
	private ExpandableListView familia_expand;
	private int groupPos;
	private int childPos;
	private AutoCompleteTextView text_input_search;
	private Spinner buscar_por_search;
	private CalendarView date_picker;
    private long fechaSelec;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat dateFormat2;
    private EditText oc_input;
    private EditText observaciones_pedido_input;
    private EditText observaciones_factura_input;
    private Button observaciones_button;
    private ImageView observaciones_pedido_borrar;
    private ImageView observaciones_factura_borrar;
    private int lastPosition;
    private ArrayList<String> listToma;
    private ArrayList<String> listTomaFinal;
    private Spinner oc_spinner;
    private TextView estado_input;
    private LinearLayout layoutToHide;
    private Button displayMiddle;

    private double valorTotal;
    private double porc_basico;
    private double porc_adicional;
	private LinearLayout appendGravables;
	private ArrayList<Pedidos_parent> arrayParents;
	private double saldo_actual_input_enc=0;
	private double cupo_disponible_input_enc=0;
	private String cond_pago_input_enc="";
	static private boolean ocIsSelected;
	static private boolean buscarActivo;
	static private boolean busquedaActivada;
	
	private RadioGroup obs_pedido_radio;
	private RadioGroup obs_factura_radio;
	private RadioButton obs_pedido_si;
	private RadioButton obs_pedido_no;
	private RadioButton obs_factura_si;
	private RadioButton obs_factura_no;
	private Button buscar_button;
	private InputMethodManager imm;
	private boolean isAskedOC;
	
	public static String Lock = "dblock";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedidos_nuevo);
		try {
			init();
			loadList();
		    loadExpandables();
		    reloadList();
		    pedidoPredeterminado(); 
			loadDataEncabezado();
			loadSearchAutoComplete(buscar_por_search.getSelectedItemPosition());
			displayMiddle.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					if(layoutToHide.getVisibility() == LinearLayout.GONE){
						displayMiddle.setText(R.string.menos_simbolo);
						layoutToHide.setVisibility(LinearLayout.VISIBLE);
					}else{
						displayMiddle.setText(R.string.mas_simbolo);
						layoutToHide.setVisibility(LinearLayout.GONE);
					}
				}
			});
			buscar_por_search.setOnItemSelectedListener((new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
					loadSearchAutoComplete(buscar_por_search.getSelectedItemPosition());
					text_input_search.setText("");
				}
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {}
			}));
			buscar_button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					if(buscar_por_search.getSelectedItemPosition()==0){
						busquedaActivada=true;
					}else{
						busquedaActivada=false;
					}
					imm.hideSoftInputFromWindow(text_input_search.getWindowToken(), 0);
		       		reloadListBySearch(buscar_por_search.getSelectedItemId(),text_input_search.getText().toString());
				}
			});
			
			
		} catch (ParseException e) {
			Log.e("error",e.toString());
		}
	}
	
	@SuppressWarnings("unchecked")
	@SuppressLint("SimpleDateFormat")
	private void init() throws ParseException{
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		context=this;
		buscarActivo=false;
		busquedaActivada=false;
		isAskedOC=false;
		fecha="";
		obs_pedido="";
	    obs_fact="";
	    obs_compromisos="";
		Date date ; 
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat2 = new SimpleDateFormat("MM/dd/yyyy");
		extras = getIntent().getExtras();
		visita_id=extras.getString("visita_id");
		cliente_id=extras.getString("cliente_id");
		cliente_nombre=extras.getString("cliente_nombre");
		pedido_id=extras.getString("pedido_id");
		de=extras.getString("de");
		galeria_titulo=(TextView)findViewById(R.id.galeria_titulo);
		total_input=(TextView)findViewById(R.id.total_input);
	    listProductos=(GridView)findViewById(R.id.listProductos);	
	    listPedidoDetalle=(ListView)findViewById(R.id.listPedidoDetalle);
	    familia_expand=(ExpandableListView)findViewById(R.id.familia_expand);
	    view_head=(TextView)findViewById(R.id.view_head);
		n_pedido_input=(TextView)findViewById(R.id.n_pedido_input);
		saldo_actual_input=(TextView)findViewById(R.id.saldo_actual_input);
		cupo_disponible_input=(TextView)findViewById(R.id.cupo_disponible_input);
		cond_pago_input=(TextView)findViewById(R.id.cond_pago_input);
		fecha_entrega_input=(TextView)findViewById(R.id.fecha_entrega_input);
		oc_input=(EditText)findViewById(R.id.oc_input);
		observaciones_button=(Button)findViewById(R.id.observaciones_button);
		oc_spinner=(Spinner)findViewById(R.id.oc_spinner);
		estado_input=(TextView)findViewById(R.id.estado_input);
		buscar_button=(Button)findViewById(R.id.buscar_button);
		layoutToHide=(LinearLayout)findViewById(R.id.layoutToHide);
		displayMiddle=(Button)findViewById(R.id.displayMiddle);
		text_input_search=(AutoCompleteTextView)findViewById(R.id.text_input_search);
		buscar_por_search=(Spinner)findViewById(R.id.buscar_por_search);
		DBAdapter=new ItaloDBAdapter(this);
		if(de!= null && de.equalsIgnoreCase("editar_pedido")){
			pedido_id=extras.getString("pedido_id");
			try{
				String [] obs=(extras.getString("obs_pedido")).split("\\|");
				obs_pedido=obs[0];
				obs_compromisos=obs[1];
			}catch(Exception e){
				obs_pedido=extras.getString("obs_pedido");
			}
			
			obs_fact=extras.getString("obs_fact");
			date = (Date)dateFormat.parse(extras.getString("fecha")); 
			fechaSelec=date.getTime();
			fecha=dateFormat2.format(fechaSelec);
			fecha_entrega_input.setText(fecha);
//			estado_input.setText(extras.getString("estado"));
			
			if(extras.getString("estado").equalsIgnoreCase("13")){
				estado_input.setText("FACTURAR");
			}else if(extras.getString("estado").equalsIgnoreCase("70")){
				estado_input.setText("VENTAS");
			}else if(extras.getString("estado").equalsIgnoreCase("14")){
				estado_input.setText("CREDITOS");
			}else if(extras.getString("estado").equalsIgnoreCase("71")){
				estado_input.setText("ANULADO");
			}

			
			if(extras.getString("OC")!=null && !extras.getString("OC").trim().equalsIgnoreCase("")){
				oc_input.setText(extras.getString("OC"));
				oc_spinner.setSelection(1);
				displayMiddle.setText(R.string.menos_simbolo);
				layoutToHide.setVisibility(LinearLayout.VISIBLE);

			}
		}else{
			pedido_id=DBAdapter.getPedidoId();
		}
	    promoListas=DBAdapter.getListasArticulosPromocion(cliente_id);

	    listTomaFinal=(ArrayList<String>)promoListas.get(0);
	    listToma=(ArrayList<String>)promoListas.get(1);
	    subfamilia="";
		productos=new ArrayList<Pedidos_producto>();
		pedidos=new ArrayList<pedido_lista>();
		view_head.setText(cliente_id+" "+cliente_nombre);
		lastPosition=-1;
		loadData();
		listProductos.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
           		Pedidos_producto aux= productos.get(position);
       			cursorPrecio=DBAdapter.getPrecioArticuloPedido(cliente_id, aux.getId());
       			if(aux.getValorBruto()==null){
                	if(cursorPrecio.moveToFirst()){
                		if(cursorPrecio.getDouble(0)>0){
                			precioUnitario = cursorPrecio.getDouble(0);
                			iva = cursorPrecio.getDouble(1);
                			i = new Intent(context, Pedidos_Captura.class);
                			i.putExtra("cliente_id", cliente_id);
                			i.putExtra("cliente_nombre", cliente_nombre);
                			i.putExtra("pedido_id", pedido_id);
                			i.putExtra("producto_id", aux.getId());
                			i.putExtra("presentacion", aux.getPresentacion());
                			//i.putExtra("producto_nombre", aux.. cursor.getString(1));
                			i.putExtra("producto_nombre", aux.getImageName());
                			i.putExtra("precio_unitario", precioUnitario);
                			i.putExtra("iva", iva);
                			i.putExtra("listToma", listToma);
                			if(aux.getDetalleId() != null && !aux.getDetalleId().equalsIgnoreCase("")){
                				i.putExtra("id", aux.getDetalleId());
                				i.putExtra("cantidad_pedida", Integer.parseInt(aux.getCantidad()));
                			}
                			startActivityForResult(i, REQUEST_UPDATE);
                		}else{
               				Utility.showMessage(context, getString(R.string.este_articulo_seleccionado_no_se_puede_adicionar));
               			}
                	}
       			}else{
       				if(!aux.getValorBruto().equalsIgnoreCase("0")){
       					if(cursorPrecio.moveToFirst()){
                    		if(cursorPrecio.getDouble(0)>0){
	       						precioUnitario = cursorPrecio.getDouble(0);
	       						iva = cursorPrecio.getDouble(1);
	       						i = new Intent(context, Pedidos_Captura.class);
	       						i.putExtra("cliente_id", cliente_id);
	       						i.putExtra("cliente_nombre", cliente_nombre);
	       						i.putExtra("pedido_id", pedido_id);
	       						i.putExtra("producto_id", aux.getId());
	       						i.putExtra("presentacion", aux.getPresentacion());
	       						//i.putExtra("producto_nombre", aux.. cursor.getString(1));
	       						i.putExtra("producto_nombre", aux.getImageName());
	       						i.putExtra("precio_unitario", precioUnitario);
	       						i.putExtra("iva", iva);
	       						i.putExtra("listToma", listToma);
	       						if(aux.getDetalleId() != null && !aux.getDetalleId().equalsIgnoreCase("")){
	       							i.putExtra("id", aux.getDetalleId());
	       							i.putExtra("cantidad_pedida", Integer.parseInt(aux.getCantidad()));
	       						}
	       						startActivityForResult(i, REQUEST_UPDATE);
                    		}else{
                   				Utility.showMessage(context, getString(R.string.este_articulo_seleccionado_no_se_puede_adicionar));
                   			}
       					}
	       			}
       			}
            }
		});
		fecha_entrega_input.setOnTouchListener(new View.OnTouchListener() {

			@SuppressLint({ "ClickableViewAccessibility", "InflateParams" }) @Override
			public boolean onTouch(View v, MotionEvent event) {
				if( event.getAction() == MotionEvent.ACTION_DOWN){
					alertView = getLayoutInflater().inflate(R.layout.date_picker, null);
					dialogBuilder = new AlertDialog.Builder(context);
					dialogBuilder.setTitle(R.string.modificar_fecha);
					dialogBuilder.setCancelable(false);
					dialogBuilder.setView(alertView);
					date_picker=(CalendarView)alertView.findViewById(R.id.date_picker);
//					Time time = new Time();
//					time.setToNow();
	//				time.month -=2;
		//			date_picker.setMinDate(time.normalize(true));
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
		observaciones_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				auxObservaciones();
			}
		});		
		listPedidoDetalle.setOnItemClickListener(new OnItemClickListener() {
			@Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				if(cursor_pedidos_temporal.moveToPosition(position)){
					if(cursor_pedidos_temporal.getDouble(3)!=0){
						i = new Intent(context, Pedidos_Captura.class);
			    		i.putExtra("cliente_id", cliente_id);
			    		i.putExtra("cliente_nombre", cliente_nombre);
			    		i.putExtra("pedido_id", pedido_id);
			    		i.putExtra("producto_id", cursor_pedidos_temporal.getString(11));
			    		i.putExtra("producto_nombre", cursor_pedidos_temporal.getString(1));
			    		i.putExtra("presentacion", cursor_pedidos_temporal.getString(2));
			    		i.putExtra("precio_unitario", cursor_pedidos_temporal.getDouble(3));
			    		i.putExtra("iva", cursor_pedidos_temporal.getDouble(9));
			   			i.putExtra("id", cursor_pedidos_temporal.getString(0));
			   			i.putExtra("cantidad_pedida", cursor_pedidos_temporal.getInt(5));
	            		i.putExtra("listToma", listToma);
						startActivityForResult(i, REQUEST_UPDATE);
					}
	        	}
            }
		});
		oc_spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long id) {
				if(id==0){
					oc_input.setVisibility(LinearLayout.GONE);
					oc_input.setText("");
//					imm.hideSoftInputFromInputMethod(oc_input.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}else{
					oc_input.setVisibility(LinearLayout.VISIBLE);
					oc_input.requestFocus();
					imm.showSoftInput(oc_input, 0);
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE); 

//					imm.showSoftInput(oc_input, InputMethodManager.SHOW_FORCED);
					displayMiddle.setText(R.string.menos_simbolo);
					layoutToHide.setVisibility(LinearLayout.VISIBLE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		return;
	}
	
	@SuppressLint("InflateParams") private void auxObservaciones(){
		alertView = getLayoutInflater().inflate(R.layout.observaciones_popout, null);
		dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(R.string.observaciones);
		dialogBuilder.setView(alertView);
		dialogBuilder.setCancelable(false);
		observaciones_pedido_input=(EditText)alertView.findViewById(R.id.observaciones_pedido_input);
		observaciones_factura_input=(EditText)alertView.findViewById(R.id.observaciones_factura_input);
		observaciones_pedido_borrar=(ImageView)alertView.findViewById(R.id.observaciones_pedido_borrar);
		observaciones_factura_borrar=(ImageView)alertView.findViewById(R.id.observaciones_factura_borrar);
		obs_pedido_radio=(RadioGroup)alertView.findViewById(R.id.obs_pedido_radio);
		obs_factura_radio=(RadioGroup)alertView.findViewById(R.id.obs_factura_radio);
		obs_pedido_si=(RadioButton)alertView.findViewById(R.id.obs_pedido_si);
		obs_pedido_no=(RadioButton)alertView.findViewById(R.id.obs_pedido_no);
		obs_factura_si=(RadioButton)alertView.findViewById(R.id.obs_factura_si);
		obs_factura_no=(RadioButton)alertView.findViewById(R.id.obs_factura_no);

		if(obs_pedido.trim().equalsIgnoreCase("")){
			obs_pedido_no.setChecked(true);
			observaciones_pedido_input.setVisibility(LinearLayout.GONE);
		}else{
			obs_pedido_si.setChecked(true);
			observaciones_pedido_input.setVisibility(LinearLayout.VISIBLE);
		}
		
		if(obs_fact.trim().equalsIgnoreCase("")){
			obs_factura_no.setChecked(true);
			observaciones_factura_input.setVisibility(LinearLayout.GONE);
		}else{
			obs_factura_si.setChecked(true);
			observaciones_factura_input.setVisibility(LinearLayout.VISIBLE);
		}
		
		observaciones_pedido_input.setText(obs_pedido);
		observaciones_factura_input.setText(obs_fact);
		observaciones_pedido_borrar.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				dialogBuilderDelete = new AlertDialog.Builder(context);
				dialogBuilderDelete.setTitle(R.string.alerta);
				dialogBuilderDelete.setCancelable(false);
				dialogBuilderDelete.setMessage(R.string.desea_borrar_la_observacion_del_pedido);
				dialogBuilderDelete.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
						observaciones_pedido_input.setText("");	
						Utility.showMessage(context, getString(R.string.la_observacion_del_pedido_se_ha_eliminado));
	    			}
	    		});
	    		dialogBuilderDelete.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {}
	    		});
	    		dialogBuilderDelete.create().show();
			}
		});
	    observaciones_factura_borrar.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				dialogBuilderDelete = new AlertDialog.Builder(context);
				dialogBuilderDelete.setTitle(R.string.alerta);
				dialogBuilderDelete.setCancelable(false);
				dialogBuilderDelete.setMessage(R.string.desea_borrar_la_observacion_de_la_factura);
				dialogBuilderDelete.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
						observaciones_factura_input.setText("");
						Utility.showMessage(context, getString(R.string.la_observacion_de_la_factura_se_han_eliminado));
	    			}
	    		});
	    		dialogBuilderDelete.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {}
	    		});
	    		dialogBuilderDelete.create().show();
			}
		});
	    obs_pedido_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				if(arg1 == R.id.obs_pedido_no){
					dialogBuilderDelete = new AlertDialog.Builder(context);
					dialogBuilderDelete.setTitle(getString(R.string.alerta));
					dialogBuilderDelete.setCancelable(false);
					dialogBuilderDelete.setMessage(R.string.desea_borrar_la_observacion_del_pedido);
					dialogBuilderDelete.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int which) {
							observaciones_pedido_input.setText("");	
							obs_pedido="";
							observaciones_pedido_input.setVisibility(LinearLayout.GONE);
							Utility.showMessage(context, getString(R.string.la_observacion_del_pedido_se_ha_eliminado));
		    			}
		    		});
		    		dialogBuilderDelete.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int which) {
		    				obs_pedido_si.setChecked(true);
		    				observaciones_pedido_input.setVisibility(LinearLayout.VISIBLE);
		    			}
		    		});
		    		dialogBuilderDelete.create().show();
				}else{
					observaciones_pedido_input.setVisibility(LinearLayout.VISIBLE);
				}

			}
		});
	    obs_factura_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				if(arg1 == R.id.obs_factura_no){
					dialogBuilderDelete = new AlertDialog.Builder(context);
					dialogBuilderDelete.setTitle(R.string.alerta);
					dialogBuilderDelete.setCancelable(false);
					dialogBuilderDelete.setMessage(R.string.desea_borrar_la_observacion_de_la_factura);
					dialogBuilderDelete.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int which) {
							observaciones_factura_input.setText("");	
							obs_fact="";
							observaciones_factura_input.setVisibility(LinearLayout.GONE);
							Utility.showMessage(context, getString(R.string.la_observacion_de_la_factura_se_han_eliminado));
		    			}
		    		});
		    		dialogBuilderDelete.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int which) {
		    				obs_factura_si.setChecked(true);
		    				observaciones_factura_input.setVisibility(LinearLayout.VISIBLE);
		    			}
		    		});
		    		dialogBuilderDelete.create().show();
				}else{
					observaciones_factura_input.setVisibility(LinearLayout.VISIBLE);
				}

			}
		});

		dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});
		dialogBuilder.setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				obs_pedido=observaciones_pedido_input.getText().toString();
				obs_fact=observaciones_factura_input.getText().toString();
			}
		});
		dialogBuilder.setCancelable(false);
		alertDialog=dialogBuilder.create();
		alertDialog.show();
		final Button createButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
		createButton.setOnClickListener(new ObservacionesButton(alertDialog));
		return;
	}
	
	private class ObservacionesButton implements View.OnClickListener {
		private final Dialog dialog;

		public ObservacionesButton(Dialog dialog) {
	        this.dialog = dialog;
	    }
	    @Override
	    public void onClick(View v) {
	    	boolean canSave=true;
	    	if(obs_pedido_si.isChecked()){
	    		if(observaciones_pedido_input.getText().toString().trim().length()<=0){
	    			canSave=false;
	    			Utility.showMessage(context, getString(R.string.las_observaciones_del_pedido_no_pueden_estar_vacias));
	    		}
	    	}
	    	if(obs_factura_si.isChecked() && canSave){
	    		if(observaciones_factura_input.getText().toString().trim().length()<=0){
	    			canSave=false;
	    			Utility.showMessage(context, getString(R.string.las_observaciones_de_la_factura_no_pueden_estar_vacias));
	    		}
	    	}
	    	
	    	if(canSave){
				obs_pedido=observaciones_pedido_input.getText().toString();
				obs_fact=observaciones_factura_input.getText().toString();
				dialog.dismiss();
	    	}
	    }
	}
	
	
	/*
	 * 
	 * Funciones de carga de datos
	 * 
	 */
	
	//Funciones de carga de la lista expandible
	
	@SuppressLint("DefaultLocale")
	private void loadExpandables(){
        arrayParents = new ArrayList<Pedidos_parent>();
        ArrayList<Pedidos_child> arrayChildren;
        //Carga Temporadas
        Pedidos_parent parent = new Pedidos_parent();
        parent.setTitle(getString(R.string.temporadas).toUpperCase());
        arrayChildren = new ArrayList<Pedidos_child>();
        cursor_temporadas_spinner=DBAdapter.getTemporadas();
		if(cursor_temporadas_spinner.moveToFirst()){
			do{
				arrayChildren.add(new Pedidos_child(cursor_temporadas_spinner.getString(0),cursor_temporadas_spinner.getString(1)));
			}while(cursor_temporadas_spinner.moveToNext());
		}
		parent.setArrayChildren(arrayChildren);
        arrayParents.add(parent);
        //Carga Nuevos
        parent = new Pedidos_parent();
        parent.setTitle(getString(R.string.nuevos_productos).toUpperCase());
        arrayChildren = new ArrayList<Pedidos_child>();
        arrayChildren.add(new Pedidos_child("0","Nuevo"));
		parent.setArrayChildren(arrayChildren);
        arrayParents.add(parent);
        //Carga Promociones
        parent = new Pedidos_parent();
        parent.setTitle((getString(R.string.promociones)).toUpperCase());
        arrayChildren = new ArrayList<Pedidos_child>();
		cursor_promocionnes=DBAdapter.getPromociones(StringUtils.join(listToma,','));
		if(cursor_promocionnes.moveToFirst()){
			do{
				arrayChildren.add(new Pedidos_child(cursor_promocionnes.getString(0),cursor_promocionnes.getString(1)));
			}while(cursor_promocionnes.moveToNext());
		}
		parent.setArrayChildren(arrayChildren);
        arrayParents.add(parent);
        
        cursor_familia=DBAdapter.getArticulosFamilias();
        if(cursor_familia.moveToFirst()){
			do{
		        parent = new Pedidos_parent();
		        parent.setId(cursor_familia.getString(0));
		        parent.setTitle(cursor_familia.getString(1).toUpperCase());
		        arrayChildren = new ArrayList<Pedidos_child>();
    			cursor_subfamilia=DBAdapter.getArticulosSubfamilias(cursor_familia.getString(0));
				if(cursor_subfamilia.moveToFirst()){
					do{
						arrayChildren.add(new Pedidos_child(cursor_subfamilia.getString(0),cursor_subfamilia.getString(1)));
					}while(cursor_subfamilia.moveToNext());
				}
				parent.setArrayChildren(arrayChildren);
		        arrayParents.add(parent);
			}while(cursor_familia.moveToNext());
		}
        familia_expand.setAdapter(new ExandableAdapter(this, arrayParents));
        familia_expand.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int arg0) {
				if(lastPosition!=-1 && lastPosition!=arg0){
					familia_expand.collapseGroup(lastPosition);
				}
				lastPosition=arg0;
			}
		});
        familia_expand.setOnChildClickListener(new OnChildClickListener(){
			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1,int arg2, int arg3, long arg4) {
				subfamilia=((TextView)arg1.findViewById(R.id.child_text)).getText().toString();
				galeria_titulo.setText(subfamilia);
				groupPos=arg2;
				childPos=arg3;
				familia_id=arrayParents.get(arg2).getId();
				subfamilia_id=arrayParents.get(arg2).getArrayChildren().get(arg3).getId();
				reloadList();
				return false;
			}
		});
		return;
	}
	
	//Funciones de carga de Productos
	
	private void loadData(){
		cursor=DBAdapter.getProductosBySubfamilia(cliente_id,familia_id,subfamilia_id);
		loadDataList();
	    adapter=new ProductosArrayAdapter(getApplicationContext(), R.layout.item_pedido_galeria, productos);
	    listProductos.setAdapter(adapter);
		registerForContextMenu(listProductos);
		return;
	}
	
	private void loadDataList(){
		double precioUnitario=0;
		double iva=0;
		if(cursor!= null && cursor.moveToFirst()){
			do{
				precioUnitario=cursor.getDouble(2);
				iva=cursor.getDouble(3)/100;
		    	productos.add(new Pedidos_producto(
		    		cursor.getString(1),
		    		cursor.getString(1),
		    		cursor.getString(0),
		    		"$"+Utility.formatNumber(precioUnitario+precioUnitario*iva),
		    		cursor.getString(4),
		    		cursor.getString(5),
		    		DBAdapter.getProductoPresentacion(cursor.getString(0)),
		    		cursor.getString(6)
    			));
			}while(cursor.moveToNext());
		}
	    return;
	}

	public void reloadList(){
		productos.clear();
		buscarActivo=false;
		switch(groupPos){
			case 0:
				if(cursor_temporadas_spinner.moveToPosition(childPos)){
					cursor=DBAdapter.getProductosByTemporada(cliente_id,cursor_temporadas_spinner.getString(0));
				}
				break;
			case 1:
				cursor=DBAdapter.getProductosByNuevo(cliente_id);
				break;
			case 2:
				if(cursor_promocionnes.moveToPosition(childPos)){
					cursor=DBAdapter.getProductosByPromocion(cliente_id,cursor_promocionnes.getString(0));
				}
				break;
			default:
				cursor=DBAdapter.getProductosBySubfamilia(cliente_id,familia_id,subfamilia_id);
				break;
		}
		loadDataList();
		adapter.notifyDataSetChanged();
		return;
	}

	public void reloadListBySearch(long tipo,String input){
		buscarActivo=true;
		productos.clear();
		cursor=DBAdapter.getProductosBySubfamiliaXfiltros(tipo, input,cliente_id);
		loadDataList();
		adapter.notifyDataSetChanged();
		if(tipo == 0 && busquedaActivada){
			if(cursor.moveToFirst()){
				for(Pedidos_producto aux:productos){
					if(aux.getId().equalsIgnoreCase(cursor.getString(0))){
						cursorPrecio=DBAdapter.getPrecioArticuloPedido(cliente_id, aux.getId());
						if(aux.getValorBruto()==null){
							if(cursorPrecio.moveToFirst()){
								if(cursorPrecio.getDouble(0)>0){
									precioUnitario = cursorPrecio.getDouble(0);
									iva = cursorPrecio.getDouble(1);
									i = new Intent(context, Pedidos_Captura.class);
									i.putExtra("cliente_id", cliente_id);
									i.putExtra("cliente_nombre", cliente_nombre);
									i.putExtra("pedido_id", pedido_id);
									i.putExtra("producto_id", aux.getId());
									i.putExtra("presentacion", aux.getPresentacion());
									//i.putExtra("producto_nombre", aux.. cursor.getString(1));
									i.putExtra("producto_nombre", aux.getImageName());
									i.putExtra("precio_unitario", precioUnitario);
									i.putExtra("iva", iva);
									i.putExtra("listToma", listToma);
									if(aux.getDetalleId() != null && !aux.getDetalleId().equalsIgnoreCase("")){
										i.putExtra("id", aux.getDetalleId());
										i.putExtra("cantidad_pedida", Integer.parseInt(aux.getCantidad()));
									}
									startActivityForResult(i, REQUEST_UPDATE);
								}else{
		               				Utility.showMessage(context, getString(R.string.este_articulo_seleccionado_no_se_puede_adicionar));
		               			}
							}
						}else{
							if(!aux.getValorBruto().equalsIgnoreCase("0")){
								if(cursorPrecio.moveToFirst()){
									if(cursorPrecio.getDouble(0)>0){
										precioUnitario = cursorPrecio.getDouble(0);
										iva = cursorPrecio.getDouble(1);
										i = new Intent(context, Pedidos_Captura.class);
										i.putExtra("cliente_id", cliente_id);
										i.putExtra("cliente_nombre", cliente_nombre);
			                   			i.putExtra("pedido_id", pedido_id);
			                   			i.putExtra("producto_id", aux.getId());
			                   			i.putExtra("presentacion", aux.getPresentacion());
			                   			//i.putExtra("producto_nombre", aux.. cursor.getString(1));
			                   			i.putExtra("producto_nombre", aux.getImageName());
			                   			i.putExtra("precio_unitario", precioUnitario);
			                   			i.putExtra("iva", iva);
			                   			i.putExtra("listToma", listToma);
			                        	if(aux.getDetalleId() != null && !aux.getDetalleId().equalsIgnoreCase("")){
			                        		i.putExtra("id", aux.getDetalleId());
			                   				i.putExtra("cantidad_pedida", Integer.parseInt(aux.getCantidad()));
			                   			}
			    						startActivityForResult(i, REQUEST_UPDATE);
									}else{
										Utility.showMessage(context, getString(R.string.este_articulo_seleccionado_no_se_puede_adicionar));
									}
								}
							}
						}
						break;
					}
				}
			}
		}
		return;
	}
	
	// Funciones de carga del encabezado
	
	private void loadDataEncabezado(){
		n_pedido_input.setText(pedido_id);
		cursor_encabezado=DBAdapter.getPedidoEncabezado(cliente_id);
		if(cursor_encabezado.moveToFirst()){
			saldo_actual_input_enc=cursor_encabezado.getDouble(0);
			cond_pago_input_enc=cursor_encabezado.getString(2);
			saldo_actual_input.setText(Utility.formatNumber(saldo_actual_input_enc));
			cond_pago_input.setText((cond_pago_input_enc.equalsIgnoreCase("CO"))?getString(R.string.contado):getString(R.string.credito));
//			setCupoDisponible();
			if(cond_pago_input_enc.equalsIgnoreCase("CO")){
				cupo_disponible_input.setText("");
			}else{
	//			cupo_disponible_input.setText(Utility.formatNumber(cupo_disponible_input_enc));
				setCupoDisponible();
			}

		}
		return;
	}

	//Funciones de la carga de pedidos temporales
	
	public void loadList(){
		cursor_pedidos_temporal=DBAdapter.getPedidoTemporal(cliente_id,pedido_id);
		loadDataListPedidos();
		productosAdapter=new pedidoListaArrayAdapter(getApplicationContext(), R.layout.item_cartera, pedidos);
		listPedidoDetalle.setAdapter(productosAdapter);
		return;
	}
	
	public void loadDataListPedidos(){
		valorTotal=0;
		if(cursor_pedidos_temporal.moveToFirst()){
			do{
				pedidos.add(new pedido_lista(
						cursor_pedidos_temporal.getString(0),
						cursor_pedidos_temporal.getString(11)+" "+cursor_pedidos_temporal.getString(1),
						cursor_pedidos_temporal.getString(2),
						cursor_pedidos_temporal.getDouble(3),
						cursor_pedidos_temporal.getString(4),
						cursor_pedidos_temporal.getString(5),
						cursor_pedidos_temporal.getString(6),
						cursor_pedidos_temporal.getString(7),
						Utility.formatNumber(cursor_pedidos_temporal.getDouble(8)),
						cursor_pedidos_temporal.getString(12),
						Utility.formatNumber(cursor_pedidos_temporal.getDouble(10))
						)
				);
				valorTotal=valorTotal+cursor_pedidos_temporal.getDouble(10);
			}while(cursor_pedidos_temporal.moveToNext());
		}
		total_input.setText(Utility.formatNumber(valorTotal));
		return;
	}
	
	public void reloadList2(){
		pedidos.clear();
		cursor_pedidos_temporal=DBAdapter.getPedidoTemporal(cliente_id,pedido_id);
        loadDataListPedidos();
        productosAdapter.notifyDataSetChanged();
		return;
	}

	public void reloadList2(ProgressDialog dialog){
		pedidos.clear();
		cursor_pedidos_temporal=DBAdapter.getPedidoTemporal(cliente_id,pedido_id);
        loadDataListPedidos();
        productosAdapter.notifyDataSetChanged();
        dialog.dismiss();
		return;
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.pedidos_nuevo, menu);
		return true;
	}

	@SuppressLint("InflateParams") @SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
			case R.id.menu_general:
				i = new Intent(getApplicationContext(), Visita_Menu.class);
				i.putExtra("de", "visita");
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				startActivity(i);
				return true;
			case R.id.detalles_menu:
				double vBruto=0;
				double vBasico=0;
				double vAdicional=0;
				double vTotal=0;
				int j=0;
				final View alertView=getLayoutInflater().inflate(R.layout.pedido_detalle_dialog, null);
				dialogBuilder = new AlertDialog.Builder(this);
				dialogBuilder.setCancelable(false);
				dialogBuilder.setTitle(R.string.datos_del_pedido);
				dialogBuilder.setView(alertView);
				final TextView n_pedido_input_detalle=(TextView)alertView.findViewById(R.id.n_pedido_input);
				final TextView n_articulos_input=(TextView)alertView.findViewById(R.id.n_articulos_input);
				final TextView porc_basico_input=(TextView)alertView.findViewById(R.id.porc_basico_input);
				final TextView desc_adicional_input=(TextView)alertView.findViewById(R.id.desc_adicional_input);
				final TextView cant_item_promo_en_especie_input=(TextView)alertView.findViewById(R.id.cant_item_promo_en_especie_input);
				final TextView valor_bruto_input=(TextView)alertView.findViewById(R.id.valor_bruto_input);
				final TextView descuento_basico_input=(TextView)alertView.findViewById(R.id.descuento_basico_input);
				final TextView descuento_adicional_input=(TextView)alertView.findViewById(R.id.descuento_adicional_input);
				final TextView valor_neto_input=(TextView)alertView.findViewById(R.id.valor_neto_input);
				final TextView valor_total_input=(TextView)alertView.findViewById(R.id.valor_total_input);
				final TextView total_venta_input=(TextView)alertView.findViewById(R.id.total_venta_input);
				appendGravables=(LinearLayout)alertView.findViewById(R.id.appendGravables);
				final TextView valor_total_label=(TextView)alertView.findViewById(R.id.valor_total_label);
				final TextView total_venta_label=(TextView)alertView.findViewById(R.id.total_venta_label);
	
				//Seteando valores
				n_pedido_input_detalle.setText(pedido_id);
				n_articulos_input.setText(String.valueOf(DBAdapter.getNArticulosTemp(pedido_id)));
				cursorDetalle=DBAdapter.getDescuentos(cliente_id);
				if(cursorDetalle.moveToFirst()){
					porc_basico=cursorDetalle.getDouble(0);
				    porc_adicional=cursorDetalle.getDouble(1);
				}else{
					porc_basico=0;
					porc_adicional=0;
				}
				porc_basico_input.setText(String.valueOf(porc_basico));
				desc_adicional_input.setText(String.valueOf(porc_adicional));
				cursorDetalle=DBAdapter.getDatosTemporales(pedido_id);
				if(cursorDetalle.moveToFirst()){
					vBruto=cursorDetalle.getDouble(0)-cursorDetalle.getDouble(2);
					vBasico=vBruto*porc_basico/100;
					vAdicional=(vBruto-vBasico)*porc_adicional/100;
					vTotal=vBruto-vBasico-vAdicional+cursorDetalle.getDouble(3);
				}
				valor_bruto_input.setText(Utility.formatNumber(vBruto));
				descuento_basico_input.setText(Utility.formatNumber(vBasico));
				descuento_adicional_input.setText(Utility.formatNumber(vAdicional));
				valor_neto_input.setText(Utility.formatNumber(vBruto-vBasico-vAdicional));
				valor_total_input.setText(Utility.formatNumber(vTotal));
				total_venta_input.setText(Utility.formatNumber(vTotal));
				cursorDetalle=DBAdapter.getCountPromoEspecieTemp(pedido_id);
				if(cursorDetalle.moveToFirst()){
					cant_item_promo_en_especie_input.setText(Utility.formatNumber(cursorDetalle.getDouble(0)));
				}
				
				cursorDetalle=DBAdapter.getNumberOfGravablesTemp(pedido_id);
				if(cursorDetalle.moveToFirst()){
					do{
						j++;
						cursor_aux=DBAdapter.getNumberOfGravablesSumTemp(pedido_id, cursorDetalle.getString(0));
						if(cursor_aux.moveToFirst()){
							addGravable(cursorDetalle.getString(0),cursor_aux.getDouble(0),j);
						}
					}while(cursorDetalle.moveToNext());
				}
				if(j% 2 == 1){
					valor_total_label.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_par));
					valor_total_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
					total_venta_label.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_impar));
					total_venta_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				}else{
					valor_total_label.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_impar));
					valor_total_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
					total_venta_label.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_par));
					total_venta_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
				}
	
				dialogBuilder.setPositiveButton(R.string.regresar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				dialogBuilder.create().show();		
				return true;
			case R.id.guardar:
				if(!fecha.equalsIgnoreCase("")){
					ocIsSelected=(oc_spinner.getSelectedItemId()==0)?true:false;
					if(!ocIsSelected){
						if(!oc_input.getText().toString().trim().equalsIgnoreCase("")){
							ocIsSelected=true;
						}
					}else{
						oc_input.setText("");
					}
					
					if(oc_input.getText().toString().trim().equalsIgnoreCase("")){
						if(ocIsSelected){
							if(!isAskedOC){
								isAskedOC=true;
								final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
					    		dialogBuilder.setTitle(R.string.alerta);
					    		dialogBuilder.setMessage(R.string.desea_adicionar_orden_de_compra_al_pedido);
					    		dialogBuilder.setCancelable(false);
					    		dialogBuilder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
					    			public void onClick(DialogInterface dialog, int which) {
					    				oc_spinner.setSelection(1);
										displayMiddle.setText(R.string.menos_simbolo);
										layoutToHide.setVisibility(LinearLayout.VISIBLE);
					    			}
					    		});
					    		dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					    			public void onClick(DialogInterface dialog, int which) {
					    				if(obs_pedido.trim().equalsIgnoreCase("") && obs_fact.trim().equalsIgnoreCase("")){
					    					dialogBuilderAux = new AlertDialog.Builder(context);
					    					dialogBuilderAux.setTitle(R.string.alerta);
					    					dialogBuilderAux.setMessage(R.string.desea_adicionar_observaciones_al_pedido);
					    					dialogBuilderAux.setCancelable(false);
					    					dialogBuilderAux.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
					    						public void onClick(DialogInterface dialog, int which) {
					    							auxObservaciones();
					    						}
					    					});
					    					dialogBuilderAux.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					    						public void onClick(DialogInterface dialog, int which) {
					    							auxGuardar();
					    						}
					    					});
					    					dialogBuilderAux.create().show();
					    				}else{
					    					auxGuardar();
					    				}
					    			}
					    		});
					    		dialogBuilder.create().show();
							}else{
    							auxGuardar();
							}
						}else{
							Utility.showMessage(context, getString(R.string.la_orden_de_compra_no_puede_estar_vacia));
						}
					}else{
						if(obs_pedido.trim().equalsIgnoreCase("") && obs_fact.trim().equalsIgnoreCase("")){
							final AlertDialog.Builder dialogBuilderAux = new AlertDialog.Builder(context);
							dialogBuilderAux.setTitle(R.string.alerta);
							dialogBuilderAux.setMessage(R.string.desea_adicionar_observaciones_al_pedido);
							dialogBuilderAux.setCancelable(false);
							dialogBuilderAux.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									auxObservaciones();
								}
							});
							dialogBuilderAux.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									auxGuardar();
								}
							});
							dialogBuilderAux.create().show();
						}else{
							auxGuardar();
						}
					}
				}else{
					final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		    		dialogBuilder.setTitle(R.string.alerta);
		    		dialogBuilder.setMessage(R.string.fecha_null);
		    		dialogBuilder.setCancelable(false);
		    		dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int which) {
							final View alertView = getLayoutInflater().inflate(R.layout.date_picker, null);
							dialogBuilderAux = new AlertDialog.Builder(context);
							dialogBuilderAux.setTitle(R.string.modificar_fecha);
							dialogBuilderAux.setCancelable(false);
							dialogBuilderAux.setView(alertView);
							date_picker=(CalendarView)alertView.findViewById(R.id.date_picker);
//							Time time = new Time();
	//						time.setToNow();
		//					time.month -=2;
			//				date_picker.setMinDate(time.normalize(true));
							dialogBuilderAux.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {}
							});
							dialogBuilderAux.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface arg0,int arg1) {}
							});
							alertDialog=dialogBuilderAux.create();
							alertDialog.show();
							final Button modifyButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
							modifyButton.setOnClickListener(new checkDate(alertDialog));
		    			}
		    		});
		    		dialogBuilder.create().show();
				}
				return true;
	        case R.id.atras:
	        	final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
	    		dialogBuilder.setTitle(R.string.alerta);
	    		dialogBuilder.setMessage(R.string.esta_seguro_que_desea_salir);
	    		dialogBuilder.setCancelable(false);
	    		dialogBuilder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
	    				cleanPedido();
	    	   			finish();
	    			}
	    		});
	    		dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {}
	    		});
	    		dialogBuilder.create().show();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}
	
	private void auxGuardar(){
		if(!isEmpty()){
			if(ocIsSelected){
				i = new Intent(getApplicationContext(), Pedidos_Captura_Finalizar_SinPedir.class);
				i.putExtra("visita_id", visita_id);
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				i.putExtra("pedido_id", pedido_id);
				i.putExtra("saldo_act", saldo_actual_input_enc+"");
				i.putExtra("oc", oc_input.getText().toString());
				i.putExtra("cupo_id", cupo_disponible_input_enc+"");
				i.putExtra("tipo_venta", cond_pago_input_enc+"");
				i.putExtra("estado", "");
				i.putExtra("fecha_entrega", dateFormat.format(fechaSelec));
				i.putExtra("obs_pedido", obs_pedido);
				i.putExtra("obs_fact", obs_fact);
				i.putExtra("obs_compromisos", obs_compromisos);
				i.putExtra("listToma", listToma);
				i.putExtra("listTomaFinal", listTomaFinal);
				i.putExtra("de", de);
				startActivityForResult(i, REQUEST_EXIT);
			}else{
				Utility.showMessage(context, getString(R.string.la_orden_de_compra_no_puede_estar_vacia));
			}
		}else{
			Utility.showMessage(context, getString(R.string.al_menos_seleccione_un_produto));
		}
		return;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
	    super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.ver_detalle);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
	        case 0:
	        	if(cursor.moveToPosition(info.position)){
		        	i = new Intent(getApplicationContext(), Articulos_Detalles_Descripcion.class);
					i.putExtra("cliente_id", cliente_id);
					i.putExtra("cliente_nombre", cliente_nombre);
					i.putExtra("producto_id", cursor.getString(0));
					startActivity(i);
	        	}
		    	return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	//ADD
	
	public class pedidoListaArrayAdapter extends ArrayAdapter<pedido_lista> {
	    private List<pedido_lista> pedidosLista = new ArrayList<pedido_lista>();
	    	    
	    public pedidoListaArrayAdapter(Context context, int textViewResourceId,List<pedido_lista> objects) {
	        super(context, textViewResourceId, objects);
	        this.pedidosLista = objects;
			notifyDataSetChanged();
	    }

		public int getCount() {
	        return this.pedidosLista.size();
	    }

	    public pedido_lista getItem(int index) {
	        return this.pedidosLista.get(index);
	    }

		@SuppressLint("NewApi")
		@SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {
			final pedido_lista p=getItem(position);
			View row = convertView;
	        if (row == null) {
	            // ROW INFLATION
	        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            row = inflater.inflate(R.layout.item_pedido_lista, parent, false);
	        }

	        final TextView row_delete_input = (TextView) row.findViewById(R.id.row_delete_input);
	        final TextView row_producto_input = (TextView) row.findViewById(R.id.row_producto_input);
	        final TextView row_pres_input = (TextView) row.findViewById(R.id.row_pres_input);
	        final TextView row_vr_unitario_input = (TextView) row.findViewById(R.id.row_vr_unitario_input);
	        final TextView row_sug_input = (TextView) row.findViewById(R.id.row_sug_input);
	        final TextView row_cant_input = (TextView) row.findViewById(R.id.row_cant_input);
	        final TextView row_promo_especie_input = (TextView) row.findViewById(R.id.row_promo_especie_input);
	        final TextView row_porc_promo_input = (TextView) row.findViewById(R.id.row_porc_promo_input);
	        final TextView row_vr_promo_input = (TextView) row.findViewById(R.id.row_vr_promo_input);
	        final TextView row_iva_input = (TextView) row.findViewById(R.id.row_iva_input);
	        final TextView row_subtotal_input = (TextView) row.findViewById(R.id.row_subtotal_input);
		    
	        //setear el icono
	        row_delete_input.setCompoundDrawablesRelativeWithIntrinsicBounds(null, context.getApplicationContext().getResources().getDrawableForDensity(R.drawable.trash_can,DisplayMetrics.DENSITY_LOW), null, null);
	        row_delete_input.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					if(Double.valueOf(p.getValorUnitario())!=0){
	            		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
	            		dialogBuilder.setTitle(R.string.alerta);
	            		dialogBuilder.setMessage(R.string.esta_seguro_que_desea_eliminar_item_pedido);
	            		dialogBuilder.setCancelable(false);
	            		dialogBuilder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
	            			public void onClick(DialogInterface dialog, int which) {
	            				if(!(p.getValorUnitario()==0 && p.getPromoEspecie().equalsIgnoreCase("*"))){
		            				DBAdapter.deletePedidoTemporal(p.getId());
		                       		reloadList();
		                       		reloadList2();
	            				}
	            			}
	            		});
	            		dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	            			public void onClick(DialogInterface dialog, int which) {}
	            		});
	            		dialogBuilder.create().show();
					}
				}
				
			});
	        
	        row_producto_input.setText(p.getProducto());
	        row_pres_input.setText(p.getPresentacion());
	        row_vr_unitario_input.setText(Utility.formatNumber(p.getValorUnitario()));
	        row_sug_input.setText(p.getSugerido());
	        row_cant_input.setText(p.getCantidad());
	        row_promo_especie_input.setText(p.getPromoEspecie());
	        row_porc_promo_input.setText(p.getPorcPromo());
	        row_vr_promo_input.setText(p.getValorPromo());
	        row_iva_input.setText(p.getIVA());
	        row_subtotal_input.setText(p.getSubtotal());

	        
	        if(position % 2 == 0){
	        	row_delete_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_producto_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_pres_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			    row_vr_unitario_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			    row_sug_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.cel_sug));
			    row_cant_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			    row_promo_especie_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			    row_porc_promo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			    row_vr_promo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			    row_iva_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			    row_subtotal_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			}else{
				row_delete_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_producto_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_pres_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_vr_unitario_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_sug_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.cel_sug));
				row_cant_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_promo_especie_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_porc_promo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_vr_promo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_iva_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_subtotal_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
			}
	        return row;
	    }
	}
	
	public class pedido_lista {
		private String id="";
		private String producto = "";
		private String presentacion = "";
		private double valorUnitario = 0;
		private String sugerido = "";
		private String cantidad = "";
		private String promoEspecie = "";
		private String porcPromo = "";
		private String valorPromo = "";
		private String iva = "";
		private String subtotal = "";

		public pedido_lista(String id, String producto, String presentacion, double valorUnitario, String sugerido, String cantidad, String promoEspecie, String porcPromo, String valorPromo, String iva, String subtotal){
			this.id=id;
			this.producto=producto;
			this.presentacion=presentacion;
			this.valorUnitario=valorUnitario;
			this.sugerido=sugerido;
			this.cantidad=cantidad;
			this.promoEspecie=promoEspecie;
			this.porcPromo=porcPromo;
			this.valorPromo=valorPromo;
			this.iva=iva;
			this.subtotal=subtotal;
		}
		
		/*
		 * 
		 * Getters
		 * 
		 */
		public String getId() {
			return id;
		}
	
		public String getProducto() {
			return producto;
		}
		
		public String getPresentacion() {
			return presentacion;
		}
		
		public double getValorUnitario() {
			return valorUnitario;
		}
		
		public String getSugerido() {
			return sugerido;
		}
		
		public String getCantidad() {
			return cantidad;
		}

		public String getPromoEspecie() {
			return promoEspecie;
		}

		public String getPorcPromo() {
			return porcPromo;
		}

		public String getValorPromo() {
			return valorPromo;
		}
		
		public String getIVA() {
			return iva;
		}

		public String getSubtotal(){
			return subtotal;
		}
	}
	
    private void pedidoPredeterminado(){
    	if(de!= null && de.equalsIgnoreCase("pedido_nuevo")){
    		if(!DBAdapter.isDataInPedidoTemp()){
    			final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
				dialogBuilder.setTitle(R.string.pedido_sugerido);
				dialogBuilder.setMessage(R.string.iniciar_pedido_sugerido);
				dialogBuilder.setCancelable(false);
				dialogBuilder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						new pedidoSugeridoBlancoAsync().execute();
					}
				});
				dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				dialogBuilder.create().show();
    		}
    	}
		return;
	}

    private void cleanPedido(){
    	DBAdapter.cleanPedidosTemporales();
    	return;
    }
    
	private void loadSearchAutoComplete(long id){
		int tipo=(int)id;
		cursor_search=DBAdapter.getArticulosSearchAuto();
		int i=0;
		String strings[];
		ArrayAdapter<String>  adapter;
		switch(tipo){
			case 0:
				text_input_search.setInputType(InputType.TYPE_CLASS_NUMBER);
		        strings = new String[0];
				adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, strings);
				text_input_search.setAdapter(adapter);
				break;
			case 1:
				text_input_search.setInputType(InputType.TYPE_CLASS_TEXT);
		        strings = new String[cursor_search.getCount()];
				if(cursor_search.moveToFirst()){
					do{
			        	strings[i] = cursor_search.getString(3);
			        	i++;
					}while(cursor_search.moveToNext());
				}
				adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, strings);
				text_input_search.setAdapter(adapter);
				break;
			default:
				break;
		}
		return;
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
			time.set(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
			fechaSelec=date_picker.getDate();
			hoy=time.toMillis(true);
			if(hoy<fechaSelec){
				fecha=dateFormat2.format(fechaSelec);
				fecha_entrega_input.setText(fecha);
				dialog.dismiss();
			}else{
				Utility.showMessage(context, getString(R.string.seleccione_una_fecha_actual_o_mayor));
			}
	    }
	}
	
	public boolean isEmpty(){
		boolean res=true;
		if(valorTotal!=0 && cursor_pedidos_temporal.getCount()>0){
			res=false;
		}
		return res;
	}
	
	@SuppressWarnings("deprecation")
	private void addGravable(String nombre, double valor, int pos){
		try{
			LinearLayout child=new LinearLayout(this);
			TextView grabableLabel=new TextView(this);
			TextView grabableinput=new TextView(this);
			grabableLabel.setText("Gravables "+nombre+"%");
			grabableinput.setText(Utility.formatNumber(valor));
			grabableinput.setGravity(Gravity.RIGHT);
			grabableLabel.setTypeface(Typeface.DEFAULT_BOLD);
			if(pos % 2 == 0){
				grabableLabel.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_par));
				grabableinput.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			}else{
				grabableLabel.setBackgroundDrawable(getResources().getDrawable(R.drawable.input_impar));
				grabableinput.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
			}
			LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			p1.weight=0.5f;
			child.addView(grabableLabel,p1);
			child.addView(grabableinput,p1);
			child.setWeightSum(1f);
			appendGravables.addView(child);
		}catch(Exception e){
			Log.e("info","addGravable "+e);
		}
		return;
	}
	
	private void setCupoDisponible(){
		double saldo_act=DBAdapter.getSaldoAct(cliente_id);
		double cupo_act=DBAdapter.getCupoAct(cliente_id,saldo_act);
		cupo_disponible_input.setText(Utility.formatNumber(cupo_act));
	}
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
			case REQUEST_EXIT:
				if (resultCode == RESULT_OK) {
					setResult(RESULT_OK, null);
					this.finish();
				}
				return;
			case REQUEST_UPDATE:
				if(resultCode == RESULT_OK){
					busquedaActivada=false;
					if(buscarActivo){
						text_input_search.setText("");
						reloadListBySearch(buscar_por_search.getSelectedItemId(),text_input_search.getText().toString());
					}else{
						reloadList();
					}
					reloadList2();

				}
				return;
			default:
				return;
		}
	}
	
	@Override
	public void onBackPressed() {}
	
    public class pedidoSugeridoBlancoAsync extends AsyncTask<String, Void, Boolean>{
    	ProgressDialog dialog;
    	
        @Override
        protected void onPreExecute() {
			dialog = ProgressDialog.show(context, "", getString(R.string.cargando), true);
        }

		@Override
    	protected Boolean doInBackground(String... urls) {
    		Boolean result;
    		try{
				System.gc();
				result=DBAdapter.loadPedidoSugeridoBlanco(cliente_id, pedido_id);
				System.gc();
    		}catch(Exception e){
    			Log.e("Error", "pedidoSugeridoBlancoAsync"+e);
    			result=false;
    		}
            return result;
    	}
    	
		@Override
        protected void onPostExecute(Boolean result) {
	    	reloadList();
			reloadList2();
			dialog.dismiss();
        }
    }
}