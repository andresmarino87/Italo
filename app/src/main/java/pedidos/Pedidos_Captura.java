package pedidos;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import articulos.Articulos_Detalles_Descripcion;

@SuppressWarnings("deprecation")
public class Pedidos_Captura extends Activity {
	private Context context;
	private Bundle extras;
	private ItaloDBAdapter DBAdapter;
	static private String cliente_id;
	static private String pedido_id;
	static private String producto_id;
	static private String producto_nombre;
	static private String number;
	static private String tipoPromo;
	static private String presentacion;
	static private String id;
	static private double precio_unitario;
	static private double iva;
	static private double valorIVA;
	static private double valorPromo;
	private TextView articulo_input;
	private TextView valor_unitario_input;
	private TextView iva_input;
	private TextView promocion_input;
	private TextView valor_total_input;
	private EditText cantidad_input;
	private ImageButton menos;
	private ImageButton mas;
	static private int cantidad_pedida;
	static private Cursor CursorPrimerGrado;
	static private Cursor CursorSegundoGrado;
	static private ArrayList<Pedido_grado> pedidosPrimerGrado;
	static private ArrayList<Pedido_grado> pedidosSegundoGrado;
	private pedidosGradosArrayAdapter adapterPrimerGrado;
	private pedidosGradosArrayAdapter adapterSegundoGrado;
	private ListView listHermanos;
	private ListView listPrimos;
	static private ArrayList<String> listToma;
	static private Promo promoValues; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedidos_captura);
		init();
		loadListPrimerGrado();
		loadListSegundoGrado();
	}
	
	private void init(){
		try{
			this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
			context=this;
			extras = getIntent().getExtras();
			NumberFormat.getInstance(new Locale( "es" , "ES"));
			cliente_id=extras.getString("cliente_id");
			extras.getString("cliente_nombre");
			pedido_id=extras.getString("pedido_id");
			producto_id=extras.getString("producto_id");
			producto_nombre=extras.getString("producto_nombre");
			presentacion=extras.getString("presentacion");
			precio_unitario=extras.getDouble("precio_unitario");
			id=extras.getString("id");
			listToma=extras.getStringArrayList("listToma");
			cantidad_pedida=extras.getInt("cantidad_pedida");
			DBAdapter= new ItaloDBAdapter(this);
			articulo_input = (TextView)findViewById(R.id.articulo_input);
			valor_unitario_input = (TextView)findViewById(R.id.valor_unitario_input);
			iva_input = (TextView)findViewById(R.id.iva_input);
			promocion_input = (TextView)findViewById(R.id.promocion_input);
			valor_total_input = (TextView)findViewById(R.id.valor_total_input);
			cantidad_input = (EditText)findViewById(R.id.cantidad_input);
			menos = (ImageButton)findViewById(R.id.menos);
			mas = (ImageButton)findViewById(R.id.mas);
			listHermanos=(ListView)findViewById(R.id.listHermanos);
			listPrimos=(ListView)findViewById(R.id.listPrimos);
			pedidosPrimerGrado = new ArrayList<Pedido_grado>();
			pedidosSegundoGrado= new ArrayList<Pedido_grado>();
			iva=(!DBAdapter.AplicarIVA(cliente_id))?0:DBAdapter.getIva(producto_id);
			articulo_input.setText(producto_id+" "+producto_nombre+" "+presentacion);
			valor_unitario_input.setText(Utility.formatNumber(precio_unitario));
			valorPromo=0;
			
			if(id==null || id.equalsIgnoreCase("")){
				promoValues=DBAdapter.getPromoValue(producto_id, 0, listToma);
				valorPromo=promoValues.getPorcDesc()*precio_unitario/100;
				promocion_input.setText(Utility.formatNumber(valorPromo));
				valor_total_input.setText(Utility.formatNumber(0*(precio_unitario-valorPromo+(precio_unitario-valorPromo)*iva/100)));
			}else{
				cantidad_input.setText(String.valueOf(cantidad_pedida));
				promoValues=DBAdapter.getPromoValue(producto_id, cantidad_pedida, listToma);
				valorPromo=promoValues.getPorcDesc()*precio_unitario/100;
				promocion_input.setText(Utility.formatNumber(valorPromo));
				valor_total_input.setText(Utility.formatNumber(cantidad_pedida*(precio_unitario-valorPromo+(precio_unitario-valorPromo)*iva/100)));
			}
			valorIVA=iva*((precio_unitario-valorPromo)/100);
			iva_input.setText(Utility.formatNumber(valorIVA));
			cantidad_input.setSelection(cantidad_input.getText().length());

			cantidad_input.addTextChangedListener(new TextWatcher(){

				@Override
				public void afterTextChanged(Editable arg0) {}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

				@Override
				public void onTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {
					if(arg0.length()==0){
						promocion_input.setText("0");
						iva_input.setText("0");
						promoValues=DBAdapter.getPromoValue(producto_id, 0, listToma);
						valor_total_input.setText(Utility.formatNumber(0));	
					}else{
						promoValues=DBAdapter.getPromoValue(producto_id, Integer.parseInt(arg0.toString()), listToma);
						if(promoValues.getTipoPromo().equalsIgnoreCase("valor")){
							valorPromo=promoValues.getPorcDesc()*precio_unitario/100;
							promocion_input.setText(Utility.formatNumber(valorPromo));
						}else{
							valorPromo=0;
							promocion_input.setText(Utility.formatNumber(valorPromo));
						}
						valorIVA=iva*((precio_unitario-valorPromo)/100);
						iva_input.setText(Utility.formatNumber(valorIVA));
						valor_total_input.setText(Utility.formatNumber((precio_unitario+valorIVA-valorPromo)*Integer.parseInt(arg0.toString())));	
					}
				}});
			
			menos.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					int cantidad=0;
					try{
						cantidad=Integer.parseInt(cantidad_input.getText().toString());
					}catch(Exception e){
						cantidad=0;
					}
					if(cantidad>1){
						cantidad_input.setText(String.valueOf(cantidad-1));
					}else{
						cantidad_input.setText(String.valueOf("1"));
						
					}
					promoValues=DBAdapter.getPromoValue(producto_id, cantidad, listToma);
					System.gc();
					if(promoValues.getTipoPromo().equalsIgnoreCase("valor")){
						valorPromo=promoValues.getPorcDesc()*precio_unitario/100;
						promocion_input.setText(Utility.formatNumber(valorPromo));
					}else{
						valorPromo=0;
						promocion_input.setText(Utility.formatNumber(valorPromo));
					}
					valorIVA=iva*((precio_unitario-valorPromo)/100);
					iva_input.setText(Utility.formatNumber(valorIVA));
					cantidad_input.setSelection(cantidad_input.getText().length());
					valor_total_input.setText(Utility.formatNumber((precio_unitario+valorIVA-valorPromo)*cantidad));	
				}
			});	
	
	    	mas.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					int cantidad=0;
					try{
						cantidad=Integer.parseInt(cantidad_input.getText().toString());
					}catch(Exception e){
						cantidad=0;
					}
					cantidad+=1;
					cantidad_input.setText(String.valueOf(cantidad));
					promoValues=DBAdapter.getPromoValue(producto_id, cantidad, listToma);
					System.gc();

					if(promoValues.getTipoPromo().equalsIgnoreCase("valor")){
						valorPromo=promoValues.getPorcDesc()*precio_unitario/100;
						promocion_input.setText(Utility.formatNumber(valorPromo));
					}else{
						valorPromo=0;
						promocion_input.setText(Utility.formatNumber(valorPromo));
					}
					valorIVA=iva*((precio_unitario-valorPromo)/100);
					iva_input.setText(Utility.formatNumber(valorIVA));
					cantidad_input.setSelection(cantidad_input.getText().length());
					valor_total_input.setText(Utility.formatNumber((precio_unitario+valorIVA-valorPromo)*cantidad));	
				}
	    	});
		}catch (Exception e){
			Log.e("error", ""+e);
		}
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pedidos_captura_tabs, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.ver_producto:
				final Intent i= new Intent(getApplicationContext(), Articulos_Detalles_Descripcion.class);
				i.putExtra("producto_id", producto_id);
				startActivity(i);
				return true;
			case R.id.guardar:
				tipoPromo="";
				if(cantidad_input.getText().toString().trim().equalsIgnoreCase("")){
					number="0";
				}else{
					number=cantidad_input.getText().toString();
				}

				if(Integer.parseInt(number)>0){
					double valorBruto=precio_unitario*Integer.parseInt(number);
					tipoPromo=promoValues.getTipoPromo();

					if(id == null || id.equalsIgnoreCase("")){
						if(tipoPromo.equalsIgnoreCase("")){
							DBAdapter.addPedidoTemporal(cliente_id,pedido_id,producto_id, String.valueOf(precio_unitario), String.valueOf(iva), number,false,"",0,"");
						}else{
							if(tipoPromo.equalsIgnoreCase("valor")){
								DBAdapter.addPedidoTemporal(cliente_id,pedido_id,producto_id, String.valueOf(precio_unitario), String.valueOf(iva), number,true,promoValues.getPromocionId(),promoValues.getPorcDesc(),"");
							}else{
								if(promoValues.getCantidadBonificada()==0){
									DBAdapter.addPedidoTemporal(cliente_id,pedido_id,producto_id, String.valueOf(precio_unitario), String.valueOf(iva), number,false,promoValues.getPromocionId(),0,"");
								}else{
									DBAdapter.addPedidoTemporal(cliente_id,pedido_id,producto_id, String.valueOf(precio_unitario), String.valueOf(iva), number,true,promoValues.getPromocionId(),0,"");
									DBAdapter.addPedidoTemporal(cliente_id,pedido_id,promoValues.getProductoIdBonificar(), "0", "0", String.valueOf(promoValues.getCantidadBonificada()),true,promoValues.getPromocionId(),0,"S");
								}
							}
						}
					}else{
						if(tipoPromo.equalsIgnoreCase("")){
							DBAdapter.modifyPedidoTemporal(cliente_id,id, valorBruto,iva, number,false,"",0);
						}else{
							if(tipoPromo.equalsIgnoreCase("valor")){
								DBAdapter.modifyPedidoTemporal(cliente_id,id, valorBruto,iva, number,true,promoValues.getPromocionId(),promoValues.getPorcDesc());
							}else{
								DBAdapter.deletePromoPedidoTemporal(id);
								if(promoValues.getCantidadBonificada()==0){
									DBAdapter.modifyPedidoTemporal(cliente_id,id, valorBruto,iva, number,false,promoValues.getPromocionId(),0);
								}else{
									DBAdapter.modifyPedidoTemporal(cliente_id,id, valorBruto,iva, number,true,promoValues.getPromocionId(),0);
									DBAdapter.addPedidoTemporal(cliente_id,pedido_id,promoValues.getProductoIdBonificar(), "0", "0", String.valueOf(promoValues.getCantidadBonificada()),true,promoValues.getPromocionId(),0,"S");
								}
							}
						}
					}
					saveHermanos();
					savePrimos();
					setResult(RESULT_OK, null);
		   			finish();
		   		}else if(hasHermanos()){ 
					saveHermanos();
					savePrimos();
					setResult(RESULT_OK, null);
		   			finish();
				}else if(hasPrimos()){
					savePrimos();
					setResult(RESULT_OK, null);
		   			finish();
				}else{
					Utility.showMessage(this, getString(R.string.cantidad_minima_debe_ser_1));
				}
				return true;
	        case R.id.atras:
	        	finish();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}

    //Adaptadores y clases para la captura de productos de varios grados
    
    static public class ViewHolder {
    	TextView row_nombre_input;
	    TextView cantidad_input;
	    ImageButton menos;
	    ImageButton mas;
        TextWatcher textWatcher;
	}

    public class pedidosGradosArrayAdapter extends ArrayAdapter<Pedido_grado> {
	    private List<Pedido_grado> pedidosGrados = new ArrayList<Pedido_grado>();
	    private TableRow tableRow;
	    private int layoutId;

	    public pedidosGradosArrayAdapter(Context context, int textViewResourceId,List<Pedido_grado> objects) {
	        super(context, textViewResourceId, objects);
	        this.pedidosGrados = objects;
	        this.layoutId=textViewResourceId;
			notifyDataSetChanged();
	    }

		public int getCount() {
	        return this.pedidosGrados.size();
	    }

	    public Pedido_grado getItem(int index) {
	        return this.pedidosGrados.get(index);
	    }

        @SuppressLint("ResourceAsColor")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
	        View rowView = convertView;
	        
	        if (convertView == null  ) {
	    		LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    		rowView = inflater.inflate(layoutId, parent, false);

	        	
	        	ViewHolder holder=new ViewHolder();
	        	holder.row_nombre_input = (TextView) rowView.findViewById(R.id.row_nombre_input);
	        	holder.cantidad_input = (TextView) rowView.findViewById(R.id.cantidad_input);
	        	holder.menos=(ImageButton) rowView.findViewById(R.id.menos);
	        	holder.mas=(ImageButton) rowView.findViewById(R.id.mas);
	        	rowView.setTag(holder);
	        	
	        }

	        final ViewHolder holder = (ViewHolder) rowView.getTag();
	        final Pedido_grado p = getItem(position);
	        tableRow=(TableRow) rowView.findViewById(R.id.tableRow);
	        holder.row_nombre_input.setText(p.getId()+" "+p.getNombre());
	        holder.cantidad_input.setText(String.valueOf(p.getCantidad()));
	        holder.menos.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					int cantidad=0;
					try{
						cantidad=Integer.parseInt(holder.cantidad_input.getText().toString());
					}catch(Exception e){
						cantidad=0;
					}
					if(cantidad>0){
						cantidad-=1;
						holder.cantidad_input.setText(String.valueOf(cantidad));
						p.setCantidad(cantidad);

					}
				}
			});	
	        holder.mas.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					int cantidad=0;
					try{
						cantidad=Integer.parseInt(holder.cantidad_input.getText().toString());
					}catch(Exception e){
						cantidad=0;
					}
					cantidad+=1;
					holder.cantidad_input.setText(String.valueOf(cantidad));
					p.setCantidad(cantidad);
				}
			});
       	
	        holder.cantidad_input.setOnClickListener(new ClickCapturaDialogo(p,holder.cantidad_input));
        	if(position % 2 == 0){
        		tableRow.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
        	}else{
        		tableRow.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
        	}
	        return rowView;
	    }
	}
	
    public class ClickCapturaDialogo implements  OnClickListener{
    	private Pedido_grado p;
    	private TextView cantidadText;
    	
    	public ClickCapturaDialogo(Pedido_grado p, TextView cantidadText){
    		this.p=p;
    		this.cantidadText=cantidadText;
    	}
    	
    	
		@SuppressLint("InflateParams") @Override 
		public void onClick(View v) {
			final View alertView = getLayoutInflater().inflate(R.layout.dialogo_captura, null);		
			final Builder dialogBuilder = new AlertDialog.Builder(context);
			final EditText cantidad=(EditText)alertView.findViewById(R.id.cantidad_input);
			dialogBuilder.setTitle(getString(R.string.ingrese_una_cantidad)+" "+p.getId()+" "+p.getNombre());
			dialogBuilder.setView(alertView);
			dialogBuilder.setCancelable(false);
			if(p.getCantidad()==0){
				cantidad.setText("");
			}else{
				cantidad.setText(String.valueOf(p.getCantidad()));
			}
			cantidad.setSelection(cantidad.getText().toString().length());
			dialogBuilder.setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if(cantidad.getText().toString().length()==0){
						p.setCantidad(0);
						cantidadText.setText("0");
					}else{
						p.setCantidad(Integer.valueOf(cantidad.getText().toString()));
						cantidadText.setText(cantidad.getText().toString());
					}
				}
			});
			dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {}
			});
			dialogBuilder.create().show();			
		}
    }
    
	//Funciones de carga de datos de los productos de varios grados
	
	//Primer Grado
	void loadListPrimerGrado(){
		CursorPrimerGrado=DBAdapter.getArtiulosPrimerGrado(producto_id,cliente_id);
		loadDataListPrimerGrado();
		adapterPrimerGrado=new pedidosGradosArrayAdapter(getApplicationContext(), R.layout.item_pedido_captura, pedidosPrimerGrado);
		listHermanos.setAdapter(adapterPrimerGrado);
		return;
	}
	
	private void loadDataListPrimerGrado(){
		int cant;
		if(CursorPrimerGrado!=null && CursorPrimerGrado.moveToFirst()){
			do{
				if(CursorPrimerGrado.isNull(2)){
					cant=0;
				}else{
					cant=CursorPrimerGrado.getInt(2);
				}
				pedidosPrimerGrado.add(new Pedido_grado(CursorPrimerGrado.getString(0),
						CursorPrimerGrado.getString(1),
						cant,
						CursorPrimerGrado.getString(3),
						CursorPrimerGrado.getDouble(4),
						CursorPrimerGrado.getDouble(5)));
			}while(CursorPrimerGrado.moveToNext());
		}
		return;
	}
	
	//Segundo Grado
	void loadListSegundoGrado(){
		CursorSegundoGrado=DBAdapter.getArtiulosSegundoGrado(producto_id,cliente_id);
		loadDataListSegundoGrado();
		adapterSegundoGrado=new pedidosGradosArrayAdapter(getApplicationContext(), R.layout.item_pedido_captura, pedidosSegundoGrado);
		listPrimos.setAdapter(adapterSegundoGrado);
		return;
	}
	
	private void loadDataListSegundoGrado(){
		int cant;
		if(CursorSegundoGrado!=null && CursorSegundoGrado.moveToFirst()){
			do{
				if(CursorSegundoGrado.isNull(2)){
					cant=0;
				}else{
					cant=CursorSegundoGrado.getInt(2);
				}
				pedidosSegundoGrado.add(
					new Pedido_grado(
						CursorSegundoGrado.getString(0),
						CursorSegundoGrado.getString(1),
						cant,
						CursorSegundoGrado.getString(3),
						CursorSegundoGrado.getDouble(4),
						CursorSegundoGrado.getDouble(5)));
			}while(CursorSegundoGrado.moveToNext());
		}
		return;
	}
	
	//Funciones para captura de productos de varios grados
	private boolean hasHermanos(){
		boolean res=false;
		for (Pedido_grado p :pedidosPrimerGrado){
			int cant=p.getCantidad();
			if(cant!=0){
				res=res || true;
			}
		}		
		return res;
	}
	
	private void saveHermanos(){
		for (Pedido_grado p :pedidosPrimerGrado){
			int cant=p.getCantidad();
			if(cant!=0){
				Promo promoValues=DBAdapter.getPromoValue(p.getId(), cant, listToma);
				tipoPromo="";
				tipoPromo=promoValues.getTipoPromo();
				double valorBruto=p.getPrecioUnitario()*cant;
				if(p.getProductoPedidoId()==null){
					if(tipoPromo.equalsIgnoreCase("")){
						DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getPrecioUnitario()), String.valueOf(p.getIva()), String.valueOf(cant),false,"",0,"");
					}else{
						if(tipoPromo.equalsIgnoreCase("valor")){
							DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getPrecioUnitario()), String.valueOf(p.getIva()), String.valueOf(cant),true,promoValues.getPromocionId(),promoValues.getPorcDesc(),"");
						}else{
							if(promoValues.getCantidadBonificada()==0){
								DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getPrecioUnitario()), String.valueOf(p.getIva()), String.valueOf(cant),false,promoValues.getPromocionId(),0,"");
							}else{
								DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getPrecioUnitario()), String.valueOf(p.getIva()), String.valueOf(cant),true,promoValues.getPromocionId(),0,"");
								DBAdapter.addPedidoTemporal(cliente_id,pedido_id,promoValues.getProductoIdBonificar(), "0", "0", String.valueOf(promoValues.getCantidadBonificada()),true,promoValues.getPromocionId(),0,"S");
							}
						}
					}
				}else{
					if(tipoPromo.equalsIgnoreCase("")){
						DBAdapter.modifyPedidoTemporal(cliente_id,p.getProductoPedidoId(), valorBruto,p.getIva(), String.valueOf(cant),false,"",0);
					}else{
						if(tipoPromo.equalsIgnoreCase("valor")){
							DBAdapter.modifyPedidoTemporal(cliente_id,p.getProductoPedidoId(), valorBruto,p.getIva(), String.valueOf(cant),true,promoValues.getPromocionId(),promoValues.getPorcDesc());
						}else{
							if(promoValues.getCantidadBonificada()==0){
								DBAdapter.deletePromoPedidoTemporal(p.getProductoPedidoId());
								DBAdapter.modifyPedidoTemporal(cliente_id,p.getProductoPedidoId(), valorBruto,p.getIva(), String.valueOf(cant),false,promoValues.getPromocionId(),0);
							}else{
								DBAdapter.deletePromoPedidoTemporal(p.getProductoPedidoId());
								DBAdapter.modifyPedidoTemporal(cliente_id,p.getProductoPedidoId(), valorBruto,p.getIva(), String.valueOf(cant),true,promoValues.getPromocionId(),0);
								DBAdapter.addPedidoTemporal(cliente_id,pedido_id,promoValues.getProductoIdBonificar(), "0", "0", String.valueOf(promoValues.getCantidadBonificada()),true,promoValues.getPromocionId(),0,"S");
							}
						}
					}
				}
			}
		}
		return;
	}
	
	private boolean hasPrimos(){
		boolean res=false;
		for (Pedido_grado p :pedidosSegundoGrado){
			int cant=p.getCantidad();
			if(cant!=0){
				res=res || true;
			}
		}
		return res;
	}
	
	private void savePrimos(){
		for (Pedido_grado p :pedidosSegundoGrado){
			int cant=p.getCantidad();
			if(cant!=0){
				Promo promoValues=DBAdapter.getPromoValue(p.getId(), cant, listToma);
				tipoPromo="";
				tipoPromo=promoValues.getTipoPromo();
				double valorBruto=p.getPrecioUnitario()*cant;
				if(p.getProductoPedidoId()==null){
					tipoPromo=promoValues.getTipoPromo();
					if(tipoPromo.equalsIgnoreCase("")){
						DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getPrecioUnitario()), String.valueOf(p.getIva()), String.valueOf(cant),false,"",0,"");
					}else{
						if(tipoPromo.equalsIgnoreCase("valor")){
							DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getPrecioUnitario()), String.valueOf(p.getIva()), String.valueOf(cant),true,promoValues.getPromocionId(),promoValues.getPorcDesc(),"");
						}else{
							if(promoValues.getCantidadBonificada()!=0){
								DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getPrecioUnitario()), String.valueOf(p.getIva()), String.valueOf(cant),false,promoValues.getPromocionId(),0,"");
							}else{
								DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getPrecioUnitario()), String.valueOf(p.getIva()), String.valueOf(cant),true,promoValues.getPromocionId(),0,"");
								DBAdapter.addPedidoTemporal(cliente_id,pedido_id,promoValues.getProductoIdBonificar(), "0", "0", String.valueOf(promoValues.getCantidadBonificada()),true,promoValues.getPromocionId(),0,"S");
							}
						}
					}
				}else{
					if(tipoPromo.equalsIgnoreCase("")){
						DBAdapter.modifyPedidoTemporal(cliente_id,p.getProductoPedidoId(), valorBruto,p.getIva(), String.valueOf(cant),false,"",0);
					}else{
						if(tipoPromo.equalsIgnoreCase("valor")){
							DBAdapter.modifyPedidoTemporal(cliente_id,p.getProductoPedidoId(), valorBruto,p.getIva(), String.valueOf(cant),true,promoValues.getPromocionId(),promoValues.getPorcDesc());
						}else{
							if(promoValues.getCantidadBonificada()==0){
								DBAdapter.deletePromoPedidoTemporal(p.getProductoPedidoId());
								DBAdapter.modifyPedidoTemporal(cliente_id,p.getProductoPedidoId(), valorBruto,p.getIva(), String.valueOf(cant),false,promoValues.getPromocionId(),0);
							}else{
								DBAdapter.deletePromoPedidoTemporal(p.getProductoPedidoId());
								DBAdapter.modifyPedidoTemporal(cliente_id,p.getProductoPedidoId(), valorBruto,p.getIva(), String.valueOf(cant),true,promoValues.getPromocionId(),0);
								DBAdapter.addPedidoTemporal(cliente_id,pedido_id,promoValues.getProductoIdBonificar(), "0", "0", String.valueOf(promoValues.getCantidadBonificada()),true,promoValues.getPromocionId(),0,"S");
							}
						}
					}
				}
			}
		}
		return;
	}
	
	@Override
	public void onBackPressed() {}

}