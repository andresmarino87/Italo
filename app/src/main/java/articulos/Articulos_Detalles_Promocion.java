package articulos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Articulos_Detalles_Promocion extends Activity {
	private Intent i;
	private Bundle extras;
	private TextView nombre_producto_input;
	private TextView precio_total_input;
	private TextView fecha_ini_toma_input;
	private TextView fecha_fin_toma_input;
	private Cursor cursor;
	private Cursor cursor_promos;
	private ItaloDBAdapter DBAdapter;
	static private ImageView image_input;
	static private File imgFile;
	static private String path;
	static private String producto_id;
	private ListView listPromos;
	private promosArrayAdapter adapter;
	private ArrayList<promocion> promos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_articulos_detalles_promocion);
		init();
		loadData();
		loadList();
		final Button button_descripcion = (Button) findViewById(R.id.descripcion);
		button_descripcion.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				i = new Intent(getApplicationContext(), Articulos_Detalles_Descripcion.class);
				i.putExtra("de", extras.getString("de"));
				i.putExtra("producto_id",producto_id);
				startActivity(i);
				finish();
			}
		});
		listPromos.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				promocion aux=promos.get(position);
				fecha_ini_toma_input.setText(aux.getFechaIni());
		    	fecha_fin_toma_input.setText(aux.getFechaFin());
			}
		});
	}

	private void init(){
    	extras = getIntent().getExtras();
    	producto_id=extras.getString("producto_id");
    	nombre_producto_input = (TextView)findViewById(R.id.nombre_producto_input);
    	precio_total_input = (TextView)findViewById(R.id.precio_total_input);
    	fecha_ini_toma_input = (TextView)findViewById(R.id.fecha_ini_toma_input);
    	fecha_fin_toma_input = (TextView)findViewById(R.id.fecha_fin_toma_input);
    	image_input=(ImageView)findViewById(R.id.image_input);
    	listPromos=(ListView)findViewById(R.id.listPromos);
    	promos=new ArrayList<promocion>();
    	DBAdapter=new ItaloDBAdapter(this);
    	return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.articulos_detalles_promocion,menu);
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
	


	public class promosArrayAdapter extends ArrayAdapter<promocion> {
	    private List<promocion> promociones = new ArrayList<promocion>();
	    private int layoudId;

	    public promosArrayAdapter(Context context, int textViewResourceId,List<promocion> objects) {
	        super(context, textViewResourceId, objects);
	        this.promociones = objects;
	        this.layoudId=textViewResourceId;
			notifyDataSetChanged();
	    }

		public int getCount() {
	        return this.promociones.size();
	    }

	    public promocion getItem(int index) {
	        return this.promociones.get(index);
	    }

		@SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        if (row == null) {
	        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            row = inflater.inflate(layoudId, parent, false);
	        }

	        final promocion p = getItem(position);
	        final TextView row_n_prom_input = (TextView) row.findViewById(R.id.row_n_prom_input);
	        final TextView row_tipo_prom_input = (TextView) row.findViewById(R.id.row_tipo_prom_input);
	        final TextView row_porc_descuento_input = (TextView) row.findViewById(R.id.row_porc_descuento_input);
	        final TextView row_cant_req_input = (TextView) row.findViewById(R.id.row_cant_req_input);
	        final TextView row_cant_bonif_input = (TextView) row.findViewById(R.id.row_cant_bonif_input);
	        final TextView row_art_bonif_input = (TextView) row.findViewById(R.id.row_art_bonif_input);

	        row_n_prom_input.setText(p.getNPromo());
	        row_tipo_prom_input.setText(p.getTipoPromo());
	        row_porc_descuento_input.setText(p.getPorcDesc());
	        row_cant_req_input.setText(p.getCantReq());
	        row_cant_bonif_input.setText(p.cant_bonif);
	        row_art_bonif_input.setText(p.getArtBonif());

	        if(position % 2 == 0){
	        	row_n_prom_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_tipo_prom_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_porc_descuento_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_cant_req_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_cant_bonif_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_art_bonif_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
			}else{
				row_n_prom_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_tipo_prom_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_porc_descuento_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_cant_req_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_cant_bonif_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_art_bonif_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
			}
	        return row;
	    }
	}
	
	public class promocion{
		private String n_promo = "";
		private String tipo_promo = "";
		private String porc_desc = "";
		private String cant_req = "";
		private String cant_bonif = "";
		private String art_bonif = "";
		private String fecha_ini="";
		private String fecha_fin="";
		
		public promocion(String n_promo, String tipo_promo, String porc_desc, String cant_req, String cant_bonif, String art_bonif,String fecha_ini, String fecha_fin){
			this.n_promo=n_promo;
			this.tipo_promo=tipo_promo;
			this.porc_desc=porc_desc;
			this.cant_req=cant_req;
			this.cant_bonif=cant_bonif;
			this.art_bonif=art_bonif;
			this.fecha_ini=fecha_ini;
			this.fecha_fin=fecha_fin;
		}
		
		/*
		 * 
		 * Getters
		 * 
		 */
		public String getNPromo() {
			return n_promo;
		}
		
		public String getTipoPromo() {
			return tipo_promo;
		}
		
		public String getPorcDesc() {
			return porc_desc;
		}
		
		public String getCantReq() {
			return cant_req;
		}		
	
		public String getCantBonif() {
			return cant_bonif;
		}		
		
		public String getArtBonif() {
			return art_bonif;
		}

		public String getFechaIni() {
			return fecha_ini;
		}

		public String getFechaFin() {
			return fecha_fin;
		}

	}
	
	@SuppressLint("NewApi")
	private void loadData(){
		loadDatosProductos();
		path=Environment.getExternalStorageDirectory().getPath()+"/Italo/"+producto_id.trim()+".png";
		imgFile = new  File(path);
		if(imgFile.exists()){
		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		    image_input.setImageBitmap(myBitmap);
		}else{
	        image_input.setImageDrawable(getResources().getDrawableForDensity(R.drawable.default_image, DisplayMetrics.DENSITY_XXXHIGH));
		}

		return;
	}
	
	private void loadDatosProductos(){
		cursor=DBAdapter.getProductoDatos(producto_id);
		if(cursor.moveToFirst()){
			nombre_producto_input.setText(cursor.getString(2)+" "+cursor.getString(3)+" "+cursor.getString(4));	
			precio_total_input.setText(extras.getString("precio"));
		}
		return;
	}
	
	private void loadList(){
		cursor_promos = DBAdapter.getPromocionesXArticulo(producto_id);
		loadListData();
		adapter = new promosArrayAdapter(getApplicationContext(),R.layout.item_articulos_promociones, promos);
		listPromos.setAdapter(adapter);
		registerForContextMenu(listPromos);
		return;
	}
	
	private void loadListData(){
		if(cursor_promos.moveToFirst()){
			do{
				promos.add(new promocion(
					cursor_promos.getString(0),
					cursor_promos.getString(1),
					cursor_promos.getString(2),
					cursor_promos.getString(3),
					cursor_promos.getString(4),
					cursor_promos.getString(5),
					cursor_promos.getString(6),
					cursor_promos.getString(7)
				));
			}while(cursor_promos.moveToNext());
		}
		return;
	}
	
	@Override
	public void onBackPressed() {}
}