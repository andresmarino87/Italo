package articulos;

import java.io.File;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class Articulos_Detalles_Descripcion extends Activity {
	private Intent i;
	private Cursor cursor;
	private ItaloDBAdapter DBAdapter;
	private Cursor cursor_spinner; 
	private Button button_promocion;	
	private ImageView image_input;
	private Spinner lista_precio_input;
	private TextView nombre_producto_input;	
	private TextView precio_total_input;	
	private TextView familia_input;	
	private TextView subfamilia_input;	
	private TextView codigo_input;	
	private TextView descripcion_input;	
	private TextView nombre_unidad_de_empaque_input;	
	private TextView unidades_de_empaque_input;	
	private TextView impuesto_iva_input;	
	private TextView valor_presentacion_sin_iva_input;	
	private TextView valor_presentacion_con_iva_input;	
	private TextView valor_unitario_sin_iva_input;	
	private TextView valor_unitario_con_iva_input;	
	private Bundle extras;
	static private String precio="";
	static private String producto_id;
	static private String producto_nombre;
	static private File imgFile;
	static private String path;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_articulos_detalles_descripcion);
		
		init();
		loadData();
		lista_precio_input.setOnItemSelectedListener((new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				loadPrecios(lista_precio_input.getSelectedItem().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		}));
		button_promocion.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				i = new Intent(getApplicationContext(), Articulos_Detalles_Promocion.class);
				i.putExtra("de", extras.getString("de"));
				i.putExtra("producto_id",extras.getString("producto_id"));
				i.putExtra("precio",precio);
				startActivity(i);
				finish();
			}
		});  
/*		image_input.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				i = new Intent(getApplicationContext(), Image_zoom.class);
				i.putExtra("producto_id",extras.getString("producto_id"));
				i.putExtra("producto_nombre",producto_nombre);
				startActivity(i);
				return false;
			}});
*/
	}

	private void init(){
		nombre_producto_input=(TextView)findViewById(R.id.nombre_producto_input);
		precio_total_input=(TextView)findViewById(R.id.precio_total_input);
		lista_precio_input=(Spinner)findViewById(R.id.lista_precio_input);
		familia_input=(TextView)findViewById(R.id.familia_input);
		subfamilia_input=(TextView)findViewById(R.id.subfamilia_input);
		codigo_input=(TextView)findViewById(R.id.codigo_input);
		descripcion_input=(TextView)findViewById(R.id.descripcion_input);
		nombre_unidad_de_empaque_input=(TextView)findViewById(R.id.nombre_unidad_de_empaque_input);
		unidades_de_empaque_input=(TextView)findViewById(R.id.unidades_de_empaque_input);
		impuesto_iva_input=(TextView)findViewById(R.id.impuesto_iva_input);
		valor_presentacion_sin_iva_input=(TextView)findViewById(R.id.valor_presentacion_sin_iva_input);
		valor_presentacion_con_iva_input=(TextView)findViewById(R.id.valor_presentacion_con_iva_input);
		valor_unitario_sin_iva_input=(TextView)findViewById(R.id.valor_unitario_sin_iva_input);
		valor_unitario_con_iva_input=(TextView)findViewById(R.id.valor_unitario_con_iva_input);
		image_input=(ImageView)findViewById(R.id.image_input);
		button_promocion = (Button) findViewById(R.id.promociones);
		DBAdapter=new ItaloDBAdapter(this);
    	extras = getIntent().getExtras();
    	producto_id=extras.getString("producto_id");
    	return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.articulos_detalles_descripcion,menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.zoom:
				i = new Intent(getApplicationContext(), Image_zoom.class);
				i.putExtra("producto_id",extras.getString("producto_id"));
				i.putExtra("producto_nombre",producto_nombre);
				startActivity(i);
				return true;
			case R.id.atras:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void loadListaPrecios(){
		cursor_spinner=DBAdapter.getListaPrecios();
		int i=0;
		String strings[] = new String[cursor_spinner.getCount()];
		if(cursor_spinner.moveToFirst()){
			do{
	        	strings[i] = cursor_spinner.getString(0);
	        	i++;
			}while(cursor_spinner.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		lista_precio_input.setAdapter(adapter);
		return;
	}

	private void loadPrecios(String listaPrecio){
		cursor=DBAdapter.getPreciosByListaPrecio(listaPrecio,extras.getString("producto_id"));
		if(cursor.moveToFirst()){
			precio_total_input.setText(Utility.formatNumber(cursor.getDouble(1)));
			valor_presentacion_sin_iva_input.setText(Utility.formatNumber(cursor.getDouble(0)));
			valor_presentacion_con_iva_input.setText(Utility.formatNumber(cursor.getDouble(1)));
			valor_unitario_sin_iva_input.setText(Utility.formatNumber(cursor.getDouble(2)));
			valor_unitario_con_iva_input.setText(Utility.formatNumber(cursor.getDouble(3)));
			precio=Utility.formatNumber(cursor.getDouble(1));
		}
		return;
	}

	@SuppressLint("NewApi")
	private void loadDatosProductos(){
		cursor=DBAdapter.getProductoDatos(producto_id);
		if(cursor.moveToFirst()){
			producto_nombre=cursor.getString(3);
			nombre_producto_input.setText(cursor.getString(2)+" "+cursor.getString(3)+" "+cursor.getString(4));	
			familia_input.setText(cursor.getString(0));	
			subfamilia_input.setText(cursor.getString(1));	
			codigo_input.setText(cursor.getString(2));	
			descripcion_input.setText(cursor.getString(3));	
			nombre_unidad_de_empaque_input.setText(cursor.getString(4));	
			unidades_de_empaque_input.setText(cursor.getString(5));	
			impuesto_iva_input.setText(cursor.getString(6));	
			path=Environment.getExternalStorageDirectory().getPath()+"/Italo/"+producto_id.trim()+".png";
			imgFile = new  File(path);
			if(imgFile.exists()){
			    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			    image_input.setImageBitmap(myBitmap);
			}else{
		        image_input.setImageDrawable(getResources().getDrawableForDensity(R.drawable.default_image, DisplayMetrics.DENSITY_XXXHIGH));
			}
		}
		return;
	}
	
	private void loadData(){
		loadListaPrecios();
		loadPrecios(lista_precio_input.getSelectedItem().toString());
		loadDatosProductos();
		return;
	}
	
	@Override
	public void onBackPressed() {}
}
