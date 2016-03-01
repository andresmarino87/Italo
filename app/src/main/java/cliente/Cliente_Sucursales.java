package cliente;

import java.util.ArrayList;
import java.util.List;
import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class Cliente_Sucursales extends Activity {
//	static private Intent i;
	static private ListView listSucursales;
	static private ItaloDBAdapter DBAdapter;
	static private Bundle extras;
	static private Cursor cursor;
	static private ArrayList<sucursal> sucursales;
	static private sucursalArrayAdapter adapter;
	static private TextView cliente_name_input;
	static private String cliente_id;
	static private String cliente_nombre;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cliente_sucursales);
		init();
		loadList();
	}
	
	private void init(){
		listSucursales = (ListView)findViewById(R.id.listSucursales);
		cliente_name_input = (TextView)findViewById(R.id.cliente_name_input);
		DBAdapter=new ItaloDBAdapter(this);
		sucursales=new ArrayList<sucursal>();
    	extras = getIntent().getExtras();
		cliente_id= extras.getString("cliente_id");
		cliente_nombre= extras.getString("cliente_nombre");
		cliente_name_input.setText(cliente_id+" "+cliente_nombre);
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cliente_sucursales,menu);
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

	void loadList(){
		cursor=DBAdapter.getClienteSucursales(cliente_id);
		loadDataList();
		adapter=new sucursalArrayAdapter(getApplicationContext(), R.layout.item_sucursales, sucursales);
		listSucursales.setAdapter(adapter);
		return;
	}

	public class sucursalArrayAdapter extends ArrayAdapter<sucursal> {
	    private List<sucursal> sucursales = new ArrayList<sucursal>();

	    public sucursalArrayAdapter(Context context, int textViewResourceId,List<sucursal> objects) {
	        super(context, textViewResourceId, objects);
	        this.sucursales = objects;
			notifyDataSetChanged();
	    }

		public int getCount() {
	        return this.sucursales.size();
	    }

	    public sucursal getItem(int index) {
	        return this.sucursales.get(index);
	    }

		public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        
	        if (row == null) {
	        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            row = inflater.inflate(R.layout.item_sucursales, parent, false);
	        }

	        final sucursal s = getItem(position);
	        final TextView row_cuenta_input = (TextView) row.findViewById(R.id.row_cuenta_input);
	        final TextView row_sucursal_input = (TextView) row.findViewById(R.id.row_sucursal_input);
	        final TextView row_sector_input = (TextView) row.findViewById(R.id.row_sector_input);

	        row_cuenta_input.setText(s.getCuenta());
	        row_sucursal_input.setText(s.getSucursal());
		    row_sector_input.setText(s.getSector());

	        if(position % 2 == 0){
	        	row_cuenta_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_sucursal_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_sector_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        }else{
	        	row_cuenta_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_sucursal_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
				row_sector_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
			}
	        return row;
	    }
	}
	
	public class sucursal {
		private String cuenta = "";
		private String sucursal = "";
		private String sector = "";

		public sucursal(String cuenta, String sucursal, String sector){
			this.cuenta=cuenta;
			this.sucursal=sucursal;
			this.sector=sector;
		}
		
		/*
		 * 
		 * Getters
		 * 
		 */
		public String getCuenta() {
			return cuenta;
		}
		
		public String getSucursal() {
			return sucursal;
		}
		
		public String getSector() {
			return sector;
		}
}
	
	private void loadDataList(){
		if(cursor.moveToFirst()){
			do{
				sucursales.add(new sucursal(cursor.getString(0),
						cursor.getString(1),
						cursor.getString(2)));
			}while(cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		return;
	}
	
	@Override
	public void onBackPressed() {}
}