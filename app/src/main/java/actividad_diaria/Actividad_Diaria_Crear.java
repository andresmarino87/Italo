package actividad_diaria;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import utilidades.Utility;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

public class Actividad_Diaria_Crear extends Activity {
	static private final int REQUEST_EXIT = 0;
	private View alertView;
	private AlertDialog.Builder dialogBuilder;
	private Intent i;
	private ItaloDBAdapter DBAdapter;
	private Bundle extras;
	static private String fecha;
	static private String n_plan;
	static private String vendedor;
	private ListView list_actividad_diaria;
	static private ArrayList<actividadRow> actividades;
	static private actividadRowArrayAdapter adapter;
	static private Cursor cursor;
	static private Cursor cursor_head;
	static private Cursor cursor_spinners;
	private Spinner sector_input;
	private Spinner ruta_input;
	private TextView n_plan_input;
	private TextView fecha_input;
	private TextView n_extaruta_input;
	private TextView n_clientes_input;
	private TextView venta_proy_input_0;
	private TextView venta_proy_input_1;
	private TextView cobro_proy_input_0;
	private TextView cobro_proy_input_1;
	private EditText observaciones;
	private Context context;
	static private SimpleDateFormat dateFormat2;
	static private SimpleDateFormat dateFormat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actividad_diaria_crear);
		Log.i("Error","Error 00");
		init();
		Log.i("Error","Error 01");
		loadHeaderData();
		Log.i("Error","Error 02");

	}

	@SuppressLint("SimpleDateFormat")
	protected void init() {
		context=this;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat2 = new SimpleDateFormat("MM/dd/yyyy");
		extras = getIntent().getExtras();
		fecha = extras.getString("fecha");
		vendedor = extras.getString("vendedor");
		sector_input=(Spinner)findViewById(R.id.sector_input);
		ruta_input=(Spinner)findViewById(R.id.ruta_input);
		n_plan_input=(TextView)findViewById(R.id.n_plan_input);
		fecha_input=(TextView)findViewById(R.id.fecha_input);
		n_extaruta_input=(TextView)findViewById(R.id.n_extaruta_input);
		n_clientes_input=(TextView)findViewById(R.id.n_clientes_input);
		venta_proy_input_0=(TextView)findViewById(R.id.venta_proy_input_0);
		venta_proy_input_1=(TextView)findViewById(R.id.venta_proy_input_1);
		cobro_proy_input_0=(TextView)findViewById(R.id.cobro_proy_input_0);
		cobro_proy_input_1=(TextView)findViewById(R.id.cobro_proy_input_1);
		list_actividad_diaria = (ListView) findViewById(R.id.list_actividad_diaria);
		DBAdapter = new ItaloDBAdapter(this);
		actividades = new ArrayList<actividadRow>();
	}
	
	
	
	private void loadHeaderData(){
		cursor_head =DBAdapter.getCrearInfoRuta(fecha);
		if(cursor_head.moveToFirst()){
			n_plan=cursor_head.getString(0);
		    try {
				fecha_input.setText(dateFormat2.format(dateFormat.parse(fecha)));
			} catch (ParseException e) {
				Log.e("info","error parseando fecha " +e);
			}
			n_plan_input.setText(n_plan);
			loadSectores();
			loadRutas(sector_input.getSelectedItem().toString(),fecha);

			if(ruta_input.getSelectedItem() != null){
				loadList(
					cursor_head.getString(3),
					cursor_head.getString(4),
					sector_input.getSelectedItem().toString(),
					ruta_input.getSelectedItem().toString());				
			}else{
				loadList(
					cursor_head.getString(3),
					cursor_head.getString(4),
					sector_input.getSelectedItem().toString(),
					"");				
				
			}
			
			sector_input.setOnItemSelectedListener(new OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
					loadRutas(((TextView)arg1).getText().toString(),fecha);					
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {}
			});

			ruta_input.setOnItemSelectedListener(new OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
					reloadListFromZero(cursor_head.getString(3),cursor_head.getString(4),sector_input.getSelectedItem().toString(),ruta_input.getSelectedItem().toString());
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {}
			});
		}
		return;
	}
	
	private void loadSectores(){
		cursor_spinners=DBAdapter.getSectoresParaPlan(fecha);
		int i;
		String strings[]=null;
		if(cursor_spinners.getCount() < 2){
		//if(true){
			i=0;
	        strings = new String[cursor_spinners.getCount()];
	        if(cursor_spinners.moveToFirst()){
				do{
		        	strings[i] = cursor_spinners.getString(0);
		        	i++;
				}while(cursor_spinners.moveToNext());
			}
		}else{
			i=1;
	        strings = new String[cursor_spinners.getCount()+1];
	        strings[0]=" ";
	        if(cursor_spinners.moveToFirst()){
				do{
		        	strings[i] = cursor_spinners.getString(0);
		        	i++;
				}while(cursor_spinners.moveToNext());
			}
		}

		/*		int i=0;
        String strings[] = new String[cursor_spinners.getCount()];
        if(cursor_spinners.moveToFirst()){
			do{
	        	strings[i] = cursor_spinners.getString(0);
	        	i++;
        	}while(cursor_spinners.moveToNext());
		}*/
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sector_input.setAdapter(adapter);
		if(cursor_spinners!=null){
			cursor_spinners.close();
		}
		return;
	}

	private void loadRutas(String sector_id,String fecha){
		cursor_spinners=DBAdapter.getRutaBySector(sector_id,fecha);
		int i=0;
        String strings[] = new String[cursor_spinners.getCount()];
        if(cursor_spinners.moveToFirst()){
			do{
	        	strings[i] = cursor_spinners.getString(0);
	        	i++;
			}while(cursor_spinners.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ruta_input.setAdapter(adapter);
		if(cursor_spinners!=null){
			cursor_spinners.close();
		}
		return;
	}
		
	private void loadList(String distrito_id, String subdistrito_id, String sector_id, String ruta_id){
		cursor = DBAdapter.getCrearInfoRutaClientes(n_plan,distrito_id,subdistrito_id,sector_id,ruta_id,fecha);
		loadListData();
		if(actividades.size()==0){
			final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle(R.string.alerta);
			dialogBuilder.setMessage(R.string.no_se_encontraron_clientes_para_esta_ruta);
			dialogBuilder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {}
			});
			dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					DBAdapter.cleanPlanTrabajoTemp();
					finish();
				}
			});
			dialogBuilder.create().show();
		}
		adapter = new actividadRowArrayAdapter(context,R.layout.item_actividad, actividades);
		list_actividad_diaria.setAdapter(adapter);
		registerForContextMenu(list_actividad_diaria);
		return;
	}

	private void reloadListFromZero(String distrito_id, String subdistrito_id, String sector_id, String ruta_id){
		actividades.clear();
		DBAdapter.cleanPlanTrabajoTemp();
		cursor = DBAdapter.getCrearInfoRutaClientes(n_plan,distrito_id,subdistrito_id,sector_id,ruta_id,fecha);
		loadListData();
		adapter.notifyDataSetChanged();
		return;
	}
	
	private void reloadList(){
		actividades.clear();
		cursor = DBAdapter.getRealoadCrearInfoRutaClientes();
		loadListData();
		adapter.notifyDataSetChanged();
		return;
	}

	private void loadListData() {
		int extraruta=0;
		double ventaProyTotal=0;
		int ventaProyCant=0;
		double cobroProyTotal=0;
		int cobroProyCant=0;
		if(cursor.moveToFirst()){ 
			do{ 
				actividades.add(new actividadRow(
				cursor.getString(0),
				cursor.getString(1),
				cursor.getString(2),
				cursor.getString(3),
				cursor.getString(4),	
				Utility.formatNumber(cursor.getDouble(5)),
				Utility.formatNumber(cursor.getDouble(6)),
				cursor.getString(7),
				cursor.getString(8),
				cursor.getString(9),
				cursor.getString(10)));
				if(cursor.getString(8).equalsIgnoreCase("s")){
					extraruta+=1;
				}
				ventaProyTotal+=cursor.getDouble(5);
				cobroProyTotal+=cursor.getDouble(6);
				if(cursor.getDouble(5)!=0){
					ventaProyCant+=1;
				}
				if(cursor.getDouble(6)!=0){
					cobroProyCant+=1;
				}
			}while(cursor.moveToNext());
			n_extaruta_input.setText(String.valueOf(extraruta));
			n_clientes_input.setText(String.valueOf(actividades.size()));
			venta_proy_input_0.setText(Utility.formatNumber(ventaProyTotal));
			venta_proy_input_1.setText(Utility.formatNumber(ventaProyCant));
			cobro_proy_input_0.setText(Utility.formatNumber(cobroProyTotal));
			cobro_proy_input_1.setText(Utility.formatNumber(cobroProyCant));
		}
		return;
	}

	public class actividadRowArrayAdapter extends ArrayAdapter<actividadRow> {
		private Context context;
		private List<actividadRow> clienteRows = new ArrayList<actividadRow>();
		private int resourceId;

		public actividadRowArrayAdapter(Context context,int textViewResourceId, List<actividadRow> objects) {
			super(context, textViewResourceId, objects);
			this.clienteRows = objects;
			this.context=context;
			this.resourceId=textViewResourceId;
			notifyDataSetChanged();
		}

		public int getCount() {
			return this.clienteRows.size();
		}

		public actividadRow getItem(int index) {
			return this.clienteRows.get(index);
		}

		@SuppressLint("NewApi") @SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {
			final actividadRow c;
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(resourceId, parent, false);
			}
			
			c = getItem(position);
			final TextView evento_row_input = (TextView) row.findViewById(R.id.evento_row_input);
			final TextView ruta_row_input = (TextView) row.findViewById(R.id.ruta_row_input);
			final TextView cuenta_row_input = (TextView) row.findViewById(R.id.cuenta_row_input);
			final TextView tejido_row_input = (TextView) row.findViewById(R.id.tejido_row_input);
			final TextView cliente_row_input = (TextView) row.findViewById(R.id.cliente_row_input);
			final TextView venta_proyectada_row_input = (TextView) row.findViewById(R.id.venta_proyectada_row_input);
			final TextView cobro_proyectado_row_input = (TextView) row.findViewById(R.id.cobro_proyectado_row_input);
			final TextView cartera_vencida_row_input = (TextView) row.findViewById(R.id.cartera_vencida_row_input);

			ruta_row_input.setText(c.getRuta());
			tejido_row_input.setText(c.getTejido());
			cuenta_row_input.setText(c.getCuenta());
			cliente_row_input.setText(c.getCliente());
			venta_proyectada_row_input.setText(c.getVenta_proyectada());
			cobro_proyectado_row_input.setText(c.getCobro_proyectado());
			if(c.getCartera_vencida()!=null){
				cartera_vencida_row_input.setText(c.getCartera_vencida());
			}else{
				cartera_vencida_row_input.setText("");
			}

			if(!c.getObservaciones().trim().equalsIgnoreCase("")){
				evento_row_input.setCompoundDrawablesRelativeWithIntrinsicBounds(null, context.getApplicationContext().getResources().getDrawableForDensity(R.drawable.obs_verde,DisplayMetrics.DENSITY_LOW), null, null);
			}else{
				evento_row_input.setCompoundDrawablesRelativeWithIntrinsicBounds(null, context.getApplicationContext().getResources().getDrawableForDensity(R.drawable.observaciones,DisplayMetrics.DENSITY_LOW), null, null);
			}
			evento_row_input.setOnClickListener(new OnClickListener(){
				@SuppressLint("InflateParams") @Override
				public void onClick(View arg0) {
       				alertView=getLayoutInflater().inflate(R.layout.observaciones_editable, null);		
       				dialogBuilder = new AlertDialog.Builder(context);
    				dialogBuilder.setTitle(getString(R.string.observaciones));
    				dialogBuilder.setView(alertView);
    				observaciones=(EditText)alertView.findViewById(R.id.observaciones);
    				observaciones.setText(c.getObservaciones());
    				dialogBuilder.setPositiveButton(getString(R.string.guardar),
    						new DialogInterface.OnClickListener() {
    							public void onClick(DialogInterface dialog, int which) {
    								if(DBAdapter.updateObservacionesPlanTrabajoTemp(c.getId(),observaciones.getText().toString())){
    									reloadList();
    								}   							
    							}
    						});
            		dialogBuilder.create().show();
				}
				
			});

			if(c.getExtraruta().equalsIgnoreCase("N")){
				if (position % 2 == 0) {
					ruta_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
					evento_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
					cuenta_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
					tejido_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
					venta_proyectada_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
					cliente_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
					cobro_proyectado_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
					cartera_vencida_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
				} else {
					ruta_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
					evento_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
					cuenta_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
					tejido_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
					venta_proyectada_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
					cliente_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
					cobro_proyectado_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
					cartera_vencida_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				}
			}else{
				ruta_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_verdes));
				evento_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_verdes));
				cuenta_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_verdes));
				tejido_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_verdes));
				venta_proyectada_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_verdes));
				cliente_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_verdes));
				cobro_proyectado_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_verdes));
				cartera_vencida_row_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_verdes));
			}
			return row;
		}
	}

	public class actividadRow {
		private String id;
		private String ruta;
		private String tejido;
		private String cuenta;
		private String cliente;
		private String venta_proyectada;
		private String cobro_proyectado;
		private String cartera_vencida;
		private String extraruta;
		private String tipoExtra;
		private String observaciones;
		
		public actividadRow(String id, String ruta, String tejido,String cuenta, String cliente, String venta_proyectada, String cobro_proyectado,String cartera_vencida,String extraruta,String tipoExtra,String observaciones) {
			this.id = id;
			this.ruta = ruta;
			this.tejido = tejido;
			this.cuenta = cuenta;
			this.cliente = cliente;
			this.venta_proyectada = venta_proyectada;
			this.cobro_proyectado = cobro_proyectado;
			this.cartera_vencida = cartera_vencida;
			this.extraruta=extraruta;
			this.tipoExtra=tipoExtra;
			this.observaciones=observaciones;
		}

		public String getId() {
			return id;
		}

		public String getRuta() {
			return ruta;
		}

		public String getTejido() {
			return tejido;
		}

		public String getCuenta() {
			return cuenta;
		}

		public String getCliente() {
			return cliente;
		}

		public String getVenta_proyectada() {
			return venta_proyectada;
		}

		public String getCobro_proyectado() {
			return cobro_proyectado;
		}

		public String getCartera_vencida() {
			return cartera_vencida;
		}
		
		public String getExtraruta() {
			return extraruta;
		}

		public String getTipo_Extraruta() {
			return tipoExtra;
		}

		public String getObservaciones() {
			return observaciones;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actividad_diaria_crear, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.indicadores:
/*			i = new Intent(getApplicationContext(), Indicadores_Tab.class);
			i.putExtra("de", "actividad_diaria_crear");
			startActivity(i);*/
			return true;
		case R.id.guardar:
			return true;
		case R.id.atras:
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle(R.string.message_advertencia);
			alert.setIcon(R.drawable.alerta);
			alert.setMessage(R.string.esta_seguro_que_desea_salir_crear_actividad);
			alert.setPositiveButton(R.string.si,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					DBAdapter.cleanPlanTrabajoTemp();
					finish();
				}
			});
			alert.setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {}
			});
			alert.create().show();
			return true;
		case R.id.siguiente:
			if(!(sector_input.getSelectedItem().toString()).trim().equals("")){
				if(DBAdapter.notExistPlanInDate(fecha,sector_input.getSelectedItem().toString(),ruta_input.getSelectedItem().toString())){
					i = new Intent(getApplicationContext(),Actividad_Diaria_Crear_Paso_2.class);
					i.putExtra("sector_id", sector_input.getSelectedItem().toString());
					i.putExtra("ruta_id", ruta_input.getSelectedItem().toString());
					i.putExtra("fecha", fecha);
					i.putExtra("n_plan", n_plan);
					i.putExtra("vendedor", vendedor);
					startActivityForResult(i, REQUEST_EXIT);
				}else{
					Utility.showMessage(context, R.string.ya_existe_un_plan_de_trabajo);
				}
			}else{
				Utility.showMessage(context, R.string.por_favor_seleccione_un_sector);
			}
			return true;
		case R.id.ir_a_extrarutas:
			i = new Intent(getApplicationContext(),	Actividad_Diaria_Extraruta.class);
			i.putExtra("de", "actividad_diaria_crear");
			i.putExtra("n_plan", n_plan);
		    try {
				i.putExtra("fecha", dateFormat2.format(dateFormat.parse(fecha)));
			} catch (ParseException e) {
				Log.e("info","error parseando fecha " +e);
			}
			startActivity(i);
			return true;
		case R.id.programar_cobro:
/*			i = new Intent(getApplicationContext(),	Actividad_Diaria_Programar_Cobro_Tabs.class);
			i.putExtra("de", "actividad_diaria_crear");
			i.putExtra("fecha", fecha);
			startActivity(i);
			finish();
*/
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		if(actividades.get(info.position).getExtraruta().equalsIgnoreCase("S")){
			menu.add(Menu.NONE, 0, Menu.NONE, context.getString(R.string.eliminar));
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case 0:
				final actividadRow aux=actividades.get(info.position);
				final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
	    		dialogBuilder.setTitle(R.string.alerta);
	    		dialogBuilder.setMessage(R.string.esta_seguro_que_desea_eliminar_extraruta);
	    		dialogBuilder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
	    				DBAdapter.deleteExtrarutaCrear(aux.getCuenta());
						reloadList();
	    			}
	    		});
	    		dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {}
	    		});
	    		dialogBuilder.create().show();
				return true;

		default:
			return super.onContextItemSelected(item);
		}
	}
	
	protected void onResume(){
	   super.onResume();
	   reloadList();
	}
	
	@Override
	public void onBackPressed() {}
	
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 if (requestCode == REQUEST_EXIT) {
	          if (resultCode == RESULT_OK) {
	             finish();
	          }
	      }
	 }
}