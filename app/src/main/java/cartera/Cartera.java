package cartera;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import cobros_programados.Cobros_Programados;

import com.italo_view.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class Cartera extends Activity {
	private Intent i;
	private Bundle extras;
	private AlertDialog.Builder dialogBuilder;
	private TextView todas;
	private TextView menor_a_30;
	private TextView mayor_a_30;
	private TextView mayor_a_60;
	private TextView mayor_a_90;
	private TextView total_vencidas;
	private TextView total_al_dia;
	private TextView proximas_a_vencer;
	private TextView todos_los_clientes_label;
	private Cursor cursor;
	private ItaloDBAdapter DBAdapter;
	private View alertView;
	private AutoCompleteTextView nombre_input;
	private AutoCompleteTextView codigo_input;
	private AutoCompleteTextView razon_social_input;
	private RadioButton todos_input;
	private Cursor cursor_search; 
	private Spinner distrito_input;
	private Spinner subdistrito_input;
	private Spinner sector_input;
	private Spinner tipo_doc_input;
	private Button ejecutar_button;
	private static String cliente_id;
	private static String cliente_nombre;
	private Context context;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cartera2);
		init();
				
		loadDistritos();
		loadSubdistrito(distrito_input.getSelectedItem().toString());
		loadSectores(subdistrito_input.getSelectedItem().toString());
		loadTipoDoc();
		
		distrito_input.setOnItemSelectedListener((new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				loadSubdistrito(distrito_input.getSelectedItem().toString());
				loadSectores(subdistrito_input.getSelectedItem().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		}));
		
		
		final LinearLayout button_total = (LinearLayout) findViewById(R.id.total);
		final LinearLayout button_menor_a_30 = (LinearLayout) findViewById(R.id.menor_a_30);
		final LinearLayout button_mayor_a_30 = (LinearLayout) findViewById(R.id.mayor_a_30);
		final LinearLayout button_mayor_a_60 = (LinearLayout) findViewById(R.id.mayor_a_60);
		final LinearLayout button_mayor_a_90 = (LinearLayout) findViewById(R.id.mayor_a_90);
		final LinearLayout button_total_vencidas_input = (LinearLayout) findViewById(R.id.total_vencidas);
		final LinearLayout button_total_al_dia_input = (LinearLayout) findViewById(R.id.total_al_dia);
		final LinearLayout button_proximas_a_vencer = (LinearLayout) findViewById(R.id.proximas_a_vencer);
		
		
		
		button_total.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				i = new Intent(getApplicationContext(), Cartera_Detalles.class);
				i.putExtra("de", 0);
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				i.putExtra("tipo_documento_id", getTipoDocumentoID());
				i.putExtra("distrito_id", getDistritoID());
				i.putExtra("subdistrito_id", getSubDistritoID());
				i.putExtra("sector_id", getSectorID());
				startActivity(i);
			}
		});        
		
		button_menor_a_30.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				i = new Intent(getApplicationContext(), Cartera_Detalles.class);
				i.putExtra("de", 1);
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				i.putExtra("tipo_documento_id", getTipoDocumentoID());
				i.putExtra("distrito_id", getDistritoID());
				i.putExtra("subdistrito_id", getSubDistritoID());
				i.putExtra("sector_id", getSectorID());
				startActivity(i);
			}
		});        
		
		button_mayor_a_30.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				i = new Intent(getApplicationContext(), Cartera_Detalles.class);
				i.putExtra("de", 2);
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				i.putExtra("tipo_documento_id", getTipoDocumentoID());
				i.putExtra("distrito_id", getDistritoID());
				i.putExtra("subdistrito_id", getSubDistritoID());
				i.putExtra("sector_id", getSectorID());
				startActivity(i);
			}
		});        
		
		button_mayor_a_60.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				i = new Intent(getApplicationContext(), Cartera_Detalles.class);
				i.putExtra("de", 3);
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				i.putExtra("tipo_documento_id", getTipoDocumentoID());
				i.putExtra("distrito_id", getDistritoID());
				i.putExtra("subdistrito_id", getSubDistritoID());
				i.putExtra("sector_id", getSectorID());
				startActivity(i);
			}
		});        
		
		button_mayor_a_90.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				i = new Intent(getApplicationContext(), Cartera_Detalles.class);
				i.putExtra("de", 4);
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				i.putExtra("tipo_documento_id", getTipoDocumentoID());
				i.putExtra("distrito_id", getDistritoID());
				i.putExtra("subdistrito_id", getSubDistritoID());
				i.putExtra("sector_id", getSectorID());
				startActivity(i);
			}
		});       
		
		button_total_vencidas_input.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				i = new Intent(getApplicationContext(), Cartera_Detalles.class);
				i.putExtra("de", 5);
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				i.putExtra("tipo_documento_id", getTipoDocumentoID());
				i.putExtra("distrito_id", getDistritoID());
				i.putExtra("subdistrito_id", getSubDistritoID());
				i.putExtra("sector_id", getSectorID());
				startActivity(i);
			}
		});       

		button_total_al_dia_input.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				i = new Intent(getApplicationContext(), Cartera_Detalles.class);
				i.putExtra("de", 6);
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				i.putExtra("tipo_documento_id", getTipoDocumentoID());
				i.putExtra("distrito_id", getDistritoID());
				i.putExtra("subdistrito_id", getSubDistritoID());
				i.putExtra("sector_id", getSectorID());
				startActivity(i);
			}
		});       

		button_proximas_a_vencer.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				i = new Intent(getApplicationContext(), Cartera_Detalles.class);
				i.putExtra("de", 7);
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				i.putExtra("tipo_documento_id", getTipoDocumentoID());
				i.putExtra("distrito_id", getDistritoID());
				i.putExtra("subdistrito_id", getSubDistritoID());
				i.putExtra("sector_id", getSectorID());
				startActivity(i);
			}
		});   
		
		ejecutar_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				loadData();
				}
		});
		
		loadData();

	}
	
	void init(){
		context=this;
		DBAdapter=new ItaloDBAdapter(this);
		todas=(TextView)findViewById(R.id.todas_input);
		menor_a_30=(TextView)findViewById(R.id.menor_a_30_input);
		mayor_a_30=(TextView)findViewById(R.id.mayor_a_30_input);
		mayor_a_60=(TextView)findViewById(R.id.mayor_a_60_input);
		mayor_a_90=(TextView)findViewById(R.id.mayor_a_90_input);
		total_vencidas=(TextView)findViewById(R.id.total_vencidas_input);
		total_al_dia=(TextView)findViewById(R.id.total_al_dia_input);
		proximas_a_vencer=(TextView)findViewById(R.id.proximas_a_vencer_input);
		todos_los_clientes_label=(TextView)findViewById(R.id.todos_los_clientes_label);
		distrito_input=(Spinner)findViewById(R.id.distrito_input);
		subdistrito_input=(Spinner)findViewById(R.id.subdistrito_input);
		sector_input=(Spinner)findViewById(R.id.sector_input);
		tipo_doc_input=(Spinner)findViewById(R.id.tipo_doc_input);
		ejecutar_button=(Button)findViewById(R.id.ejecutar_button);
    	extras = getIntent().getExtras();
    	cliente_id=extras.getString("cliente_id");
    	cliente_nombre=extras.getString("cliente_nombre");
    	PrepararFiltros();
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cartera2, menu);
		return true;
	}
	
	private void PrepararFiltros()
	{
		if (cliente_id != null)
		{
			todos_los_clientes_label.setText(cliente_id + " " + cliente_nombre);
    		distrito_input.setEnabled(false);
    		subdistrito_input.setEnabled(false);
    		sector_input.setEnabled(false);
		}
		else
		{
			todos_los_clientes_label.setText(R.string.todos_los_clientes);
    		distrito_input.setEnabled(true);
    		subdistrito_input.setEnabled(true);
    		sector_input.setEnabled(true);
		}
	}
	
	private void loadNombres(){
		cursor_search=DBAdapter.getNombreCliente();
		int i=0;
        String strings[] = new String[cursor_search.getCount()];
		if(cursor_search.moveToFirst()){
			do{
	        	strings[i] = cursor_search.getString(0);
	        	i++;
			}while(cursor_search.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		nombre_input.setAdapter(adapter);
		return;
	}

	private void loadClientesId(){
		cursor_search=DBAdapter.getCliente_id();
		int i=0;
        String strings[] = new String[cursor_search.getCount()];
		if(cursor_search.moveToFirst()){
			do{
	        	strings[i] = cursor_search.getString(0);
	        	i++;
			}while(cursor_search.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		codigo_input.setAdapter(adapter);
		return;
	}
	
	private void loadRazonSocial(){
		cursor_search=DBAdapter.getRazonSocial();
		int i=0;
        String strings[] = new String[cursor_search.getCount()];
		if(cursor_search.moveToFirst()){
			do{
	        	strings[i] = cursor_search.getString(0);
	        	i++;
			}while(cursor_search.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		razon_social_input.setAdapter(adapter);
		return;
	}
	
	private void loadDistritos(){
		cursor_search=DBAdapter.GetDistritosCartera();
		
		//Declaraci�n de variables locales
		int lDimensionArray = (cursor_search.getCount() > 1 ? cursor_search.getCount() + 1 : cursor_search.getCount());
		int i=0;
		
        String strings[] = new String[lDimensionArray];
        
        if (lDimensionArray > cursor_search.getCount())
        {
        	strings[i]=getString(R.string.todos);
        	i++;
        }
        
		if(cursor_search.moveToFirst()){
			do{
	        	strings[i] = cursor_search.getString(0);
	        	i++;
			}while(cursor_search.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		distrito_input.setAdapter(adapter);
		
		return;
	}
	
	private void loadSubdistrito(String distrito){
		cursor_search=DBAdapter.GetSubdistritosByDistritoCartera(distrito);
		
		//Declaraci�n de variables locales
		int lDimensionArray = (cursor_search.getCount() > 1 ? cursor_search.getCount() + 1 : 1);
		int i = 0;
		
        String strings[] = new String[lDimensionArray];
        
        //Verifica si debe adicionar la opci�n TODOS
        if (cursor_search.getCount() != 1)
        {
        	strings[i]=getString(R.string.todos);
        	i++;
        }
        
		if(cursor_search.moveToFirst()){
			do{
	        	strings[i] = cursor_search.getString(0);
	        	i++;
			}while(cursor_search.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		subdistrito_input.setAdapter(adapter);
		return;
	}
	
	private void loadSectores(String subdistrito){
		cursor_search=DBAdapter.GetSectorBySubdistritosCartera(subdistrito);
		
		//Declaraci�n de variables locales
		int lDimensionArray = (cursor_search.getCount() > 1 ? cursor_search.getCount() + 1 : 1);
		int i = 0;
		
        String strings[] = new String[lDimensionArray];
        
        //Verifica si debe adicionar la opci�n TODOS
        if (cursor_search.getCount() != 1)
        {
        	strings[i]=getString(R.string.todos);
        	i++;
        }
        
        if(cursor_search.moveToFirst()){
			do{
	        	strings[i] = cursor_search.getString(0);
	        	i++;
			}while(cursor_search.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sector_input.setAdapter(adapter);
		return;
	}
	
	private void loadTipoDoc(){
		cursor_search=DBAdapter.GetTipoDocCartera();
		
		//Declaraci�n de variables locales
		int lDimensionArray = (cursor_search.getCount() > 1 ? cursor_search.getCount() + 1 : cursor_search.getCount());
		int i = 0;
		
        String strings[] = new String[lDimensionArray];
        
        //Verifiac si debe adicionar la opci�n TODOS
        if (lDimensionArray > cursor_search.getCount())
        {
        	strings[i]=getString(R.string.todos);
        	i++;
        }
        
		if(cursor_search.moveToFirst()){
			do{
	        	strings[i] = cursor_search.getString(0);
	        	i++;
			}while(cursor_search.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		tipo_doc_input.setAdapter(adapter);
		return;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.mas_filtros:
				alertView = getLayoutInflater().inflate(R.layout.search_cartera_x_cliente, new LinearLayout(this),false);
				dialogBuilder = new AlertDialog.Builder(this);
				dialogBuilder.setTitle(R.string.seleccione_filtros);
				dialogBuilder.setView(alertView);
				dialogBuilder.setCancelable(false);
				nombre_input=(AutoCompleteTextView)alertView.findViewById(R.id.nombre_input);
				codigo_input=(AutoCompleteTextView)alertView.findViewById(R.id.codigo_input);
				razon_social_input=(AutoCompleteTextView)alertView.findViewById(R.id.razon_social_input);
				todos_input=(RadioButton)alertView.findViewById(R.id.todos_input);
	
				loadNombres();
				loadClientesId();
				loadRazonSocial();
				dialogBuilder.setPositiveButton(getString(R.string.buscar), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (todos_input.isChecked())
						{
							cliente_id = null;
							PrepararFiltros();
							loadDistritos();
							loadSubdistrito(distrito_input.getSelectedItem().toString());
							loadSectores(subdistrito_input.getSelectedItem().toString());
							loadData();
						}
						else
						{
							//Verifica si el filtro devolvi� 1 �nico cliente
							cursor = DBAdapter.getContadorClientes(nombre_input.getText().toString(), 
									codigo_input.getText().toString(), razon_social_input.getText().toString());
							if (cursor.moveToFirst())
							{
								if (cursor.getInt(0) > 1) 
								{
									dialogBuilder = new AlertDialog.Builder(context);
						    		dialogBuilder.setTitle(getString(R.string.alerta));
						    		dialogBuilder.setMessage(getString(R.string.requiere_un_solo_cliente));
						    		dialogBuilder.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
						    			public void onClick(DialogInterface dialog, int which) {
						    				return;
						    			}
						    		});
						    		dialogBuilder.create().show();
								}
								else if (cursor.getInt(0) == 0)
								{
									dialogBuilder = new AlertDialog.Builder(context);
							    	dialogBuilder.setTitle(getString(R.string.alerta));
							    	dialogBuilder.setMessage(getString(R.string.no_se_encontro_clientes));
							    	dialogBuilder.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
							    		public void onClick(DialogInterface dialog, int which) {
							    			return;
							    		}
							    	});
							    	dialogBuilder.create().show();
								}
								else
								{
									//Determina el c�digo del cliente seg�n los criterios de b�squeda
									cursor = DBAdapter.getDatosCliente(nombre_input.getText().toString(), 
											codigo_input.getText().toString(), razon_social_input.getText().toString());
									if (cursor.moveToFirst())
									{
										cliente_id = cursor.getString(0);
										cliente_nombre = cursor.getString(1);
										PrepararFiltros();
										loadData();
									}
								}
							}
						}
					}
				});
				dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				dialogBuilder.create().show();
				return true;
	    	case R.id.atras:
				finish();
	    		return true;
	    	case R.id.ver_pagos_programados:
				i = new Intent(getApplicationContext(), Cobros_Programados.class);
				i.putExtra("cliente_id", cliente_id);
				i.putExtra("cliente_nombre", cliente_nombre);
				i.putExtra("distrito_id", getDistritoID());
				i.putExtra("subdistrito_id", getSubDistritoID());
				i.putExtra("sector_id", getSectorID());
				startActivity(i);
				return true;
	    	default:
	    		return super.onOptionsItemSelected(item);
		}
    }
    
    private void loadData(){
    	//Determina el c�digo del tipo de documento seleccionado
    	String lTipoDocumentoID = getTipoDocumentoID();
    	
    	if (cliente_id == null)
    	{
    		//Declaraci�n de variables locales
    		String lDistritoID = getDistritoID();
    		String lSubDistritoID = getSubDistritoID();
    		String lSectorID = getSectorID();
    		
    		//Ejecuta las consultas para las diferentes edades de cartera
    		cursor = DBAdapter.getCarteraAllSUM(lDistritoID, lSubDistritoID, lSectorID, lTipoDocumentoID);
    		if (cursor.moveToFirst())
    			todas.setText("$ "+ Utility.formatNumber(cursor.getDouble(0)));
    			
    		cursor = DBAdapter.getCarteraMenor30SUM(lDistritoID, lSubDistritoID, lSectorID, lTipoDocumentoID);
    		if (cursor.moveToFirst())
    			menor_a_30.setText("$ "+ Utility.formatNumber(cursor.getDouble(0)));
    			
    		cursor = DBAdapter.getCarteraMayor30SUM(lDistritoID, lSubDistritoID, lSectorID, lTipoDocumentoID);
    		if (cursor.moveToFirst())
    			mayor_a_30.setText("$ "+ Utility.formatNumber(cursor.getDouble(0)));
    		
    		cursor = DBAdapter.getCarteraMayor60SUM(lDistritoID, lSubDistritoID, lSectorID, lTipoDocumentoID);
    		if (cursor.moveToFirst())
    			mayor_a_60.setText("$ "+ Utility.formatNumber(cursor.getDouble(0)));
    		
    		cursor = DBAdapter.getCarteraMayor90SUM(lDistritoID, lSubDistritoID, lSectorID, lTipoDocumentoID);
    		if (cursor.moveToFirst())
    			mayor_a_90.setText("$ "+ Utility.formatNumber(cursor.getDouble(0)));
    		
    		cursor = DBAdapter.getCarteraTotalVencidas(lDistritoID, lSubDistritoID, lSectorID, lTipoDocumentoID);
    		if (cursor.moveToFirst())
    			total_vencidas.setText("$ " + Utility.formatNumber(cursor.getDouble(0)));
    		
    		cursor = DBAdapter.getCarteraTotalAlDiaSum(lDistritoID, lSubDistritoID, lSectorID, lTipoDocumentoID);
    		if (cursor.moveToFirst())
    			total_al_dia.setText("$ " + Utility.formatNumber(cursor.getDouble(0)));
    		
    		cursor = DBAdapter.getCarteraProximasAVencerSum(lDistritoID, lSubDistritoID, lSectorID, lTipoDocumentoID);
    		if (cursor.moveToFirst())
    			proximas_a_vencer.setText("$ "+ Utility.formatNumber(cursor.getDouble(0)));
    	}
    	else
    	{
    		//Ejecuta las consultas de cartera para el cliente
    		cursor = DBAdapter.getCarteraAllSUM(cliente_id, lTipoDocumentoID);
    		if(cursor.moveToFirst())
    			todas.setText("$ " + Utility.formatNumber(cursor.getDouble(0)));
    		
    		cursor = DBAdapter.getCarteraMenor30SUM(cliente_id, lTipoDocumentoID);
    		if(cursor.moveToFirst())
    			menor_a_30.setText("$ "+ Utility.formatNumber(cursor.getDouble(0)));
    		
    		cursor = DBAdapter.getCarteraMayor30SUM(cliente_id, lTipoDocumentoID);
    		if(cursor.moveToFirst())
    			mayor_a_30.setText("$ " + Utility.formatNumber(cursor.getDouble(0)));
    		
    		cursor = DBAdapter.getCarteraMayor60SUM(cliente_id, lTipoDocumentoID);
    		if(cursor.moveToFirst())
    			mayor_a_60.setText("$ " + Utility.formatNumber(cursor.getDouble(0)));
    		
    		cursor = DBAdapter.getCarteraMayor90SUM(cliente_id, lTipoDocumentoID);
    		if(cursor.moveToFirst())
    			mayor_a_90.setText("$ " + Utility.formatNumber(cursor.getDouble(0)));
    		
    		cursor = DBAdapter.getCarteraTotalVencidad(cliente_id, lTipoDocumentoID);
    		if(cursor.moveToFirst())
    			total_vencidas.setText("$ "+Utility.formatNumber(cursor.getDouble(0)));
    		
    		cursor = DBAdapter.getCarteraTotalAlDiaSum(cliente_id, lTipoDocumentoID);
    		if(cursor.moveToFirst())
    			total_al_dia.setText("$ "+Utility.formatNumber(cursor.getDouble(0)));
    		
    		cursor = DBAdapter.getCarteraProximasAVencerSum(cliente_id, lTipoDocumentoID);
    		if(cursor.moveToFirst())
    			proximas_a_vencer.setText("$ "+Utility.formatNumber(cursor.getDouble(0)));
    		
    	}
    	
		return;
    }
    
    private String getDistritoID()
    {
    	if (distrito_input.getSelectedItem().toString() == getString(R.string.todos))
		{
			return "%%";
		}
		else
		{
			cursor = DBAdapter.getDataTable("SELECT distrito_id FROM esd_distrito " + 
					"WHERE nombre_distrito = '" + distrito_input.getSelectedItem().toString() + "'");	
			if (cursor.moveToFirst())
				return cursor.getString(0);
			else
				return "%%";
		}
    }
    
    private String getSubDistritoID()
    {
    	String lDistritoID = getDistritoID();
    	if (lDistritoID == "%%")
    		return "%%";
    	else
    	{
    		if (subdistrito_input.getSelectedItem().toString() == getString(R.string.todos))
    			return "%%";
    		else
    		{
	    		cursor = DBAdapter.getDataTable("SELECT subdistrito_id FROM esd_subdistrito " +
						"WHERE distrito_id = '" + lDistritoID + "' AND nombre_subdistrito = '" + 
						subdistrito_input.getSelectedItem().toString() + "'");
				if (cursor.moveToFirst())
					return cursor.getString(0);
				else
					return "%%";
    		}
    	}
    }
    
    private String getSectorID()
    {
    	String lSubDistritoID = getSubDistritoID();
    	if (lSubDistritoID == "%%")
    		return "%%";
    	else
    		if (sector_input.getSelectedItem().toString() == getString(R.string.todos))
    			return "%%";
    		else
    			return sector_input.getSelectedItem().toString();
    }
    
    private String getTipoDocumentoID()
    {
    	if (tipo_doc_input.getSelectedItem().toString() == getString(R.string.todos))
		{
			return "%%";
		}
		else
		{
			cursor = DBAdapter.getDataTable("SELECT cartera_tipo_documento_id FROM esd_cartera_tipo_documento " +
					"WHERE descripcion = '" + tipo_doc_input.getSelectedItem().toString() + "'");
			if (cursor.moveToFirst())
				return cursor.getString(0);
			else
				return "%%";
		}
    }
    
	@Override
	public void onBackPressed() {}
}