package cliente;

import java.util.ArrayList;
import java.util.List;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class Cliente_Contactos extends Activity {
	private AlertDialog alertDialog;
	private AlertDialog.Builder dialogBuilder;
	private ListView listContactos;
	private ItaloDBAdapter DBAdapter;
	private Bundle extras;
	private Cursor cursor;
	private Cursor cursor_detalles;
	private Cursor cursor_search;
	static private String crear_str="";
	static private String modificar_str="";
	static private String eliminar_str="";
	private View alertView;
	private EditText contacto_input;
	private EditText detalle_input;
	private TextView estado_input;
	private Spinner cargo_input;
	private Spinner medio_de_comunicacion_input;
	static private ArrayList<contacto> contactos;
	static private contactoArrayAdapter adapter;
	static private String cliente_id;
	static private String cliente_nombre;
	private TextView cliente_name_input;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cliente_contactos);
		init();
		loadList();
	}

	private void init(){
		context=this;
		crear_str=getApplicationContext().getString(R.string.crear);
		modificar_str=getApplicationContext().getString(R.string.modificar);
		eliminar_str=getApplicationContext().getString(R.string.eliminar);
		listContactos = (ListView)findViewById(R.id.listContactos);
		DBAdapter=new ItaloDBAdapter(this);
		contactos=new ArrayList<contacto>();
		extras = getIntent().getExtras();
		cliente_id=extras.getString("cliente_id");
		cliente_nombre= extras.getString("cliente_nombre");
		cliente_name_input=(TextView)findViewById(R.id.cliente_name_input);
		cliente_name_input.setText(cliente_id+" "+cliente_nombre);
    	return;	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cliente_contactos,menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
	        case R.id.add_contacto:
	        	addContact();
				return true;
	        case R.id.atras:
				finish();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
	    super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, 0, Menu.NONE, modificar_str);
		menu.add(Menu.NONE, 1, Menu.NONE, eliminar_str);
	}
	
	private void loadCargosDialogo(){
		cursor_detalles=DBAdapter.getCargos();
		int i=0;
        String strings[] = new String[cursor_detalles.getCount()];
		if(cursor_detalles.moveToFirst()){
			do{
	        	strings[i] = cursor_detalles.getString(0);
	        	i++;
			}while(cursor_detalles.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cargo_input.setAdapter(adapter);
		return;
	}
	
	private int loadCargosDialogo(String cargo){
		cursor_search=DBAdapter.getCargos();
		int i=0;
        String strings[] = new String[cursor_search.getCount()];
		if(cursor_search.moveToFirst()){
			do{
	        	strings[i] = cursor_search.getString(0);
	        	i++;
			}while(cursor_search.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cargo_input.setAdapter(adapter);
		i=adapter.getPosition(cargo);
		return i;
	}
	
	private void loadMedioDeComunicacionDialogo(){
		cursor_detalles=DBAdapter.getMedioDeComunicacion();
		int i=0;
        String strings[] = new String[cursor_detalles.getCount()];
		if(cursor_detalles.moveToFirst()){
			do{
	        	strings[i] = cursor_detalles.getString(0);
	        	i++;
			}while(cursor_detalles.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		medio_de_comunicacion_input.setAdapter(adapter);
		return;
	}	

	private int loadMedioDeComunicacionDialogo(String medio){
		cursor_search=DBAdapter.getMedioDeComunicacion();
		int i=0;
        String strings[] = new String[cursor_search.getCount()];
		if(cursor_search.moveToFirst()){
			do{
	        	strings[i] = cursor_search.getString(0);
	        	i++;
			}while(cursor_search.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		medio_de_comunicacion_input.setAdapter(adapter);
		i=adapter.getPosition(medio);
		return i;
	}	

	private class NewContact implements View.OnClickListener {
		private final Dialog dialog;
	    public NewContact(Dialog dialog) {
	        this.dialog = dialog;
	    }
	    @Override
	    public void onClick(View v) {
			if(!contacto_input.getText().toString().equalsIgnoreCase("")  && !detalle_input.getText().toString().equalsIgnoreCase("")){
				DBAdapter.addContacto(cliente_id, contacto_input.getText().toString(), cargo_input.getSelectedItem().toString(), medio_de_comunicacion_input.getSelectedItem().toString(), detalle_input.getText().toString());
				reloadList();
	            dialog.dismiss();
			}else{
	    		dialogBuilder = new AlertDialog.Builder(context);
	    		dialogBuilder.setTitle(getString(R.string.alerta));
	    		dialogBuilder.setMessage(getString(R.string.nombre_contacto_detalle_contacto_not_null));
	    		dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {}
	    		});
	    		dialogBuilder.create().show();
			}
	    }
	}
	
	private class ModifyContact implements View.OnClickListener {
		private final Dialog dialog;
	    public ModifyContact(Dialog dialog) {
	        this.dialog = dialog;
	    }
	    @Override
	    public void onClick(View v) {
			if(!contacto_input.getText().toString().equalsIgnoreCase("")  && !detalle_input.getText().toString().equalsIgnoreCase("")){
				DBAdapter.updateContacto(cursor.getString(3), contacto_input.getText().toString(), cargo_input.getSelectedItem().toString(), medio_de_comunicacion_input.getSelectedItem().toString(), detalle_input.getText().toString());
				reloadList();
	            dialog.dismiss();
			}else{
	    		dialogBuilder = new AlertDialog.Builder(context);
	    		dialogBuilder.setTitle(getString(R.string.alerta));
	    		dialogBuilder.setMessage(getString(R.string.nombre_contacto_detalle_contacto_not_null));
	    		dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {}
	    		});
	    		dialogBuilder.create().show();
			}
	    }
	}
	
	@SuppressLint("InflateParams") private void addContact(){
		alertView = getLayoutInflater().inflate(R.layout.activity_cliente_contactos_alerta, null);
		dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(getString(R.string.crear_contacto));
		dialogBuilder.setView(alertView);
		dialogBuilder.setCancelable(false);
		contacto_input=(EditText)alertView.findViewById(R.id.contacto_input);
		cargo_input=(Spinner)alertView.findViewById(R.id.cargo_input);
		medio_de_comunicacion_input=(Spinner)alertView.findViewById(R.id.medio_de_comunicacion_input);
		detalle_input=(EditText)alertView.findViewById(R.id.detalle_input);
		estado_input=(TextView)alertView.findViewById(R.id.estado_input);
		estado_input.setText(crear_str);
		loadCargosDialogo();
		loadMedioDeComunicacionDialogo();
		dialogBuilder.setPositiveButton(R.string.crear, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});
		dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});
		alertDialog=dialogBuilder.create();
		alertDialog.show();
		final Button createButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
		createButton.setOnClickListener(new NewContact(alertDialog));
		return;
	}
	
	@SuppressLint("InflateParams") @Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		if(item.getTitle()==modificar_str){
			alertView = getLayoutInflater().inflate(R.layout.activity_cliente_contactos_alerta, null);
			dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle(R.string.modificar);
			dialogBuilder.setView(alertView);
			dialogBuilder.setCancelable(false);
			contacto_input=(EditText)alertView.findViewById(R.id.contacto_input);
			cargo_input=(Spinner)alertView.findViewById(R.id.cargo_input);
			medio_de_comunicacion_input=(Spinner)alertView.findViewById(R.id.medio_de_comunicacion_input);
			detalle_input=(EditText)alertView.findViewById(R.id.detalle_input);
			estado_input=(TextView)alertView.findViewById(R.id.estado_input);

			cursor.moveToPosition(info.position);
			cursor_detalles=DBAdapter.getContacto(cursor.getString(3));
			cursor_detalles.moveToFirst();
			contacto_input.setText(cursor_detalles.getString(0));
			detalle_input.setText(cursor_detalles.getString(3));
			estado_input.setText(modificar_str);
			cargo_input.setSelection(loadCargosDialogo(cursor_detalles.getString(1)));
			medio_de_comunicacion_input.setSelection(loadMedioDeComunicacionDialogo(cursor_detalles.getString(2)));
			dialogBuilder.setPositiveButton(R.string.modificar, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {}
			});
			dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {}
			});
			alertDialog=dialogBuilder.create();
			alertDialog.show();
			final Button modifyButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
			modifyButton.setOnClickListener(new ModifyContact(alertDialog));
			return true;
		}else if(item.getTitle()==eliminar_str){
    		dialogBuilder = new AlertDialog.Builder(this);
    		dialogBuilder.setTitle(R.string.alerta);
    		dialogBuilder.setMessage(R.string.esta_seguro_que_desea_borrar_este_contacto);
    		dialogBuilder.setCancelable(false);
    		dialogBuilder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				cursor.moveToPosition(info.position);
    				DBAdapter.deleteContacto(cursor.getString(3));
    				reloadList();
    			}
    		});
    		dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {}
    		});
    		dialogBuilder.create().show();
			return true;
		}else{
            return super.onContextItemSelected(item);
		}
	}
	
	private void loadList(){
		String cliente_id=extras.getString("cliente_id");
        cursor=DBAdapter.getClienteContactos(cliente_id);
		loadDataList();
		adapter=new contactoArrayAdapter(getApplicationContext(), R.layout.item_contacto, contactos);
		listContactos.setAdapter(adapter);
		registerForContextMenu(listContactos);
		if(cursor.getCount()==0){
			addContact();
		}
		return;

	}
	
	public class contactoArrayAdapter extends ArrayAdapter<contacto> {
	    private List<contacto> contactos = new ArrayList<contacto>();
	    private int layoutId;
	    
	    public contactoArrayAdapter(Context context, int textViewResourceId,List<contacto> objects) {
	        super(context, textViewResourceId, objects);
	        this.contactos = objects;
	        this.layoutId=textViewResourceId;
			notifyDataSetChanged();
	    }

		public int getCount() {
	        return this.contactos.size();
	    }

	    public contacto getItem(int index) {
	        return this.contactos.get(index);
	    }

		public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        if (row == null) {
	            // ROW INFLATION
	        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            row = inflater.inflate(layoutId, parent, false);
	        }else{}

	        final contacto c = getItem(position);
	        final TextView row_contacto_input = (TextView) row.findViewById(R.id.row_contacto_input);
	        final TextView row_cargo_input = (TextView) row.findViewById(R.id.row_cargo_input);
	        final TextView row_dato_contacto_input = (TextView) row.findViewById(R.id.row_dato_contacto_input);

	        row_contacto_input.setText(c.getContacto());
	        row_cargo_input.setText(c.getCargo());
	        row_dato_contacto_input.setText(c.getDatoContacto());

	        if(position % 2 == 0){
	        	row_contacto_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_cargo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_dato_contacto_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        }else{
	        	row_contacto_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_cargo_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_dato_contacto_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        }
	        return row;
	    }
	}
	
	public class contacto {
		private String contacto = "";
		private String cargo = "";
		private String dato_contacto = "";

		public contacto(String contacto, String cargo, String dato_contacto){
			this.contacto=contacto;
			this.cargo=cargo;
			this.dato_contacto=dato_contacto;
		}
		
		/*
		 * 
		 * Getters
		 * 
		 */
		public String getContacto() {
			return contacto;
		}
		
		public String getCargo() {
			return cargo;
		}
		
		public String getDatoContacto() {
			return dato_contacto;
		}
	}
	
	private void loadDataList(){
		if(cursor.moveToFirst()){
			do{
				contactos.add(new contacto(cursor.getString(0),
					cursor.getString(1),
					cursor.getString(2)));
			}while(cursor.moveToNext());
		}
		return;
	}
	
	private void reloadList(){
		contactos.clear();
		String cliente_id=extras.getString("cliente_id");
        cursor=DBAdapter.getClienteContactos(cliente_id);
		loadDataList();
		adapter.notifyDataSetChanged();
		return;
	}
	
	@Override
	public void onBackPressed() {}
}