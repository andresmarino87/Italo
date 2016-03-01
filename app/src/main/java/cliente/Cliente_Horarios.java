package cliente;

import java.util.ArrayList;
import java.util.List;

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
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

@SuppressWarnings("deprecation")
public class Cliente_Horarios extends Activity{
	private AlertDialog alertDialog;
	private AlertDialog.Builder dialogBuilder;
	private AlertDialog.Builder timeBuilder;
	private ListView listHorarios;
	private ItaloDBAdapter DBAdapter;
	private Bundle extras;
	private Cursor cursor;
	private Cursor cursor_detalles;
	static private String crear_str="";
	private View alertView;
	private Spinner tipo_horario_input;
	private EditText lugar;
	private EditText hora_inicial_input;
	private EditText hora_final_input;
	private TextView estado_input;
	private CheckBox lun_input;
	private CheckBox mar_input;
	private CheckBox mie_input;
	private CheckBox jue_input;
	private CheckBox vie_input;
	private CheckBox sab_input;
	private CheckBox dom_input;
	private TimePicker time;
	private View timeView;
	private ArrayList<horario> horarios;
	private horarioArrayAdapter adapter;
	static private String cliente_id;
	static private String cliente_nombre;
	private TextView cliente_name_input;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cliente_horarios);
		init();
		loadHorarios();
	}

	private void init(){
		context=this;
		crear_str=getApplicationContext().getString(R.string.crear);
		listHorarios = (ListView)findViewById(R.id.listHorarios);
		DBAdapter=new ItaloDBAdapter(this);
    	extras = getIntent().getExtras();
    	horarios=new ArrayList<horario>();
    	cliente_id=extras.getString("cliente_id");
		cliente_nombre= extras.getString("cliente_nombre");
		cliente_name_input=(TextView)findViewById(R.id.cliente_name_input);
		cliente_name_input.setText(cliente_id+" "+cliente_nombre);
    	return;	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.cliente_horarios,menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.crear_horario:
				addHorario();
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
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.modificar);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.eliminar);
	}
	
	private void loadConceptoHorarios(){
		cursor_detalles=DBAdapter.getConceptoHorarios();
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
		tipo_horario_input.setAdapter(adapter);
		return;
	}

	private int loadCargosDialogo(String concepto){
		cursor_detalles=DBAdapter.getConceptoHorarios();
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
		tipo_horario_input.setAdapter(adapter);
		i=adapter.getPosition(concepto);
		return i;
	}

	@SuppressLint("InflateParams") private void addHorario(){
		alertView = getLayoutInflater().inflate(R.layout.activity_cliente_horarios_alerta, null);
		dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(R.string.crear_horario);
		dialogBuilder.setView(alertView);
		dialogBuilder.setCancelable(false);
		tipo_horario_input = (Spinner)alertView.findViewById(R.id.tipo_horario_input);
		lugar = (EditText)alertView.findViewById(R.id.lugar_input);
		hora_inicial_input = (EditText)alertView.findViewById(R.id.hora_inicial_input);
		hora_final_input = (EditText)alertView.findViewById(R.id.hora_final_input);
		estado_input = (TextView)alertView.findViewById(R.id.estado_input);
		lun_input = (CheckBox)alertView.findViewById(R.id.lun_input);
		mar_input = (CheckBox)alertView.findViewById(R.id.mar_input);
		mie_input = (CheckBox)alertView.findViewById(R.id.mie_input);
		jue_input = (CheckBox)alertView.findViewById(R.id.jue_input);
		vie_input = (CheckBox)alertView.findViewById(R.id.vie_input);
		sab_input = (CheckBox)alertView.findViewById(R.id.sab_input);
		dom_input = (CheckBox)alertView.findViewById(R.id.dom_input);
		estado_input.setText(crear_str);
		loadConceptoHorarios();
		cursor_detalles=DBAdapter.getClienteDireccion(cliente_id);
		if(cursor_detalles.moveToFirst()){
			lugar.setText(cursor_detalles.getString(0));
		}
		timeBuilder = new AlertDialog.Builder(this);
		hora_inicial_input.setInputType(EditorInfo.TYPE_NULL);
		hora_inicial_input.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("InflateParams") @Override
			public void onClick(View v) {
				timeView = getLayoutInflater().inflate(R.layout.time_picker, null);
				timeBuilder.setTitle(R.string.hora);
				timeBuilder.setView(timeView);
				time=(TimePicker)timeView.findViewById(R.id.timePicker);
				time.setIs24HourView(true);
				timeBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						hora_inicial_input.setText(checkDigit(time.getCurrentHour())+":"+checkDigit(time.getCurrentMinute()));
					}
				});
				timeBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});

				timeBuilder.create().show();
           	}
		});
//		timeBuilder = new AlertDialog.Builder(this);
		hora_final_input.setInputType(EditorInfo.TYPE_NULL);
		hora_final_input.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				timeView = getLayoutInflater().inflate(R.layout.time_picker, null);
				timeBuilder.setTitle(getString(R.string.hora));
				timeBuilder.setView(timeView);
				time=(TimePicker)timeView.findViewById(R.id.timePicker);
				time.setIs24HourView(true);
				timeBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						hora_final_input.setText(checkDigit(time.getCurrentHour())+":"+checkDigit(time.getCurrentMinute()));
					}
				});
				timeBuilder.create().show();
           	}
		});
		dialogBuilder.setPositiveButton(R.string.crear, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});
		dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});
		alertDialog=dialogBuilder.create();
		alertDialog.show();
		final Button modifyButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
		modifyButton.setOnClickListener(new createHorario(alertDialog));
		return;
	}

	private class createHorario implements View.OnClickListener {
		private final Dialog dialog;
	    public createHorario(Dialog dialog) {
	        this.dialog = dialog;
	    }
	    @Override
	    public void onClick(View v) {
			boolean cen =false;
			String lun="N";
			String mar="N";
			String mie="N";
			String jue="N";
			String vie="N";
			String sab="N";
			String dom="N";
			if(lun_input.isChecked()){
				lun="S";
				cen=true;
			}
			if(mar_input.isChecked()){
				mar="S";
				cen=true;
			}
			if(mie_input.isChecked()){
				mie="S";
				cen=true;
			}
			if(jue_input.isChecked()){
				jue="S";
				cen=true;
			}
			if(vie_input.isChecked()){
				vie="S";
				cen=true;
			}
			if(sab_input.isChecked()){
				sab="S";
				cen=true;
			}
			if(dom_input.isChecked()){
				dom="S";
				cen=true;
			}
			if(!lugar.getText().toString().equalsIgnoreCase("") && cen && !hora_inicial_input.getText().toString().equalsIgnoreCase("") && !hora_final_input.getText().toString().equalsIgnoreCase("")){
				if(hora_inicial_input.getText().toString().compareTo(hora_final_input.getText().toString()) < 0){
					if(DBAdapter.addHorario(cliente_id, tipo_horario_input.getSelectedItem().toString(), lugar.getText().toString(), hora_inicial_input.getText().toString(), hora_final_input.getText().toString(), lun, mar, mie, jue, vie, sab, dom)){
						reloadList();
						dialog.dismiss();
					}else{
						Utility.showMessage(context, getString(R.string.ya_existe_un_horario)+" "+tipo_horario_input.getSelectedItem().toString());
					}
				}else{
					Utility.showMessage(context, getString(R.string.hora_inicial_menor_hora_final));
				}
			}else{
				Utility.showMessage(context, getString(R.string.horarios_null));
			}
	    }
	}

	private class modifyHorario implements View.OnClickListener {
		private final Dialog dialog;
	    public modifyHorario(Dialog dialog) {
	        this.dialog = dialog;
	    }
	    @Override
	    public void onClick(View v) {
			boolean cen =false;
			String lun="N";
			String mar="N";
			String mie="N";
			String jue="N";
			String vie="N";
			String sab="N";
			String dom="N";
			if(lun_input.isChecked()){
				lun="S";
				cen=true;
			}
			if(mar_input.isChecked()){
				mar="S";
				cen=true;
			}
			if(mie_input.isChecked()){
				mie="S";
				cen=true;
			}
			if(jue_input.isChecked()){
				jue="S";
				cen=true;
			}
			if(vie_input.isChecked()){
				vie="S";
				cen=true;
			}
			if(sab_input.isChecked()){
				sab="S";
				cen=true;
			}
			if(dom_input.isChecked()){
				dom="S";
				cen=true;
			}
			if(!lugar.getText().toString().equalsIgnoreCase("") && cen && !hora_inicial_input.getText().toString().equalsIgnoreCase("") && !hora_final_input.getText().toString().equalsIgnoreCase("")){
				if(hora_inicial_input.getText().toString().compareTo(hora_final_input.getText().toString()) < 0){
					DBAdapter.updateHorario(cursor.getString(0), tipo_horario_input.getSelectedItem().toString(), lugar.getText().toString(), hora_inicial_input.getText().toString(), hora_final_input.getText().toString(), lun, mar, mie, jue, vie, sab, dom);
					reloadList();
					dialog.dismiss();
				}else{
					Utility.showMessage(context, getString(R.string.hora_inicial_menor_hora_final));
				}
			}else{
				Utility.showMessage(context, getString(R.string.horarios_null));
			}
	    }
	}

	
	@SuppressLint("InflateParams") @Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()){
			case 0:
				alertView = getLayoutInflater().inflate(R.layout.activity_cliente_horarios_alerta, null);
				dialogBuilder = new AlertDialog.Builder(this);
				dialogBuilder.setTitle(R.string.modificar);
				dialogBuilder.setView(alertView);
				dialogBuilder.setCancelable(false);
				tipo_horario_input = (Spinner)alertView.findViewById(R.id.tipo_horario_input);
				lugar = (EditText)alertView.findViewById(R.id.lugar_input);
				hora_inicial_input = (EditText)alertView.findViewById(R.id.hora_inicial_input);
				hora_final_input = (EditText)alertView.findViewById(R.id.hora_final_input);
				estado_input = (TextView)alertView.findViewById(R.id.estado_input);
				lun_input = (CheckBox)alertView.findViewById(R.id.lun_input);
				mar_input = (CheckBox)alertView.findViewById(R.id.mar_input);
				mie_input = (CheckBox)alertView.findViewById(R.id.mie_input);
				jue_input = (CheckBox)alertView.findViewById(R.id.jue_input);
				vie_input = (CheckBox)alertView.findViewById(R.id.vie_input);
				sab_input = (CheckBox)alertView.findViewById(R.id.sab_input);
				dom_input = (CheckBox)alertView.findViewById(R.id.dom_input);
				
				cursor.moveToPosition(info.position);
				cursor_detalles=DBAdapter.getHorario(cursor.getString(0));
				cursor_detalles.moveToFirst();
				estado_input.setText(R.string.modificar);
				lugar.setText(cursor_detalles.getString(2));
				hora_inicial_input.setText(cursor_detalles.getString(3));
				hora_final_input.setText(cursor_detalles.getString(4));

				if(cursor_detalles.getString(5).equalsIgnoreCase("S")){
					lun_input.setChecked(true);
				}
				if(cursor_detalles.getString(6).equalsIgnoreCase("S")){
					mar_input.setChecked(true);
				}
				if(cursor_detalles.getString(7).equalsIgnoreCase("S")){
					mie_input.setChecked(true);
				}
				if(cursor_detalles.getString(8).equalsIgnoreCase("S")){
					jue_input.setChecked(true);
				}
				if(cursor_detalles.getString(9).equalsIgnoreCase("S")){
					vie_input.setChecked(true);
				}
				if(cursor_detalles.getString(10).equalsIgnoreCase("S")){
					sab_input.setChecked(true);
				}
				if(cursor_detalles.getString(11).equalsIgnoreCase("S")){
					dom_input.setChecked(true);
				}

				timeBuilder = new AlertDialog.Builder(this);
				hora_inicial_input.setInputType(EditorInfo.TYPE_NULL);
				hora_inicial_input.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String [] tiempo= hora_inicial_input.getText().toString().split(":");
						timeView = getLayoutInflater().inflate(R.layout.time_picker, null);
						timeBuilder.setTitle(R.string.hora);
						timeBuilder.setView(timeView);
						timeBuilder.setCancelable(false);
						time=(TimePicker)timeView.findViewById(R.id.timePicker);
						time.setIs24HourView(true);
						time.setCurrentHour(Integer.parseInt(tiempo[0]));
						time.setCurrentMinute(Integer.parseInt(tiempo[1]));
						timeBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								hora_inicial_input.setText(checkDigit(time.getCurrentHour())+":"+checkDigit(time.getCurrentMinute()));
							}
						});
						timeBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {}
						});
						timeBuilder.create().show();
		           	}
				});
				timeBuilder = new AlertDialog.Builder(this);
				hora_final_input.setInputType(EditorInfo.TYPE_NULL);
				hora_final_input.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String [] tiempo= hora_final_input.getText().toString().split(":");
						timeView = getLayoutInflater().inflate(R.layout.time_picker, null);
						timeBuilder.setTitle(R.string.hora);
						timeBuilder.setView(timeView);
						timeBuilder.setCancelable(false);
						time=(TimePicker)timeView.findViewById(R.id.timePicker);
						time.setIs24HourView(true);
						time.setCurrentHour(Integer.parseInt(tiempo[0]));
						time.setCurrentMinute(Integer.parseInt(tiempo[1]));
						timeBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								hora_final_input.setText(checkDigit(time.getCurrentHour())+":"+checkDigit(time.getCurrentMinute()));
							}
						});
						timeBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {}
						});

						timeBuilder.create().show();
		           	}
				});

				tipo_horario_input.setSelection(loadCargosDialogo(cursor_detalles.getString(1)));
				tipo_horario_input.setSelected(false);
				dialogBuilder.setPositiveButton(R.string.modificar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {}
				});
				alertDialog=dialogBuilder.create();
				alertDialog.show();
				final Button modifyButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
				modifyButton.setOnClickListener(new modifyHorario(alertDialog));
				return true;
			case 1:
	    		dialogBuilder = new AlertDialog.Builder(this);
	    		dialogBuilder.setTitle(R.string.alerta);
	    		dialogBuilder.setMessage(R.string.esta_seguro_que_desea_borrar_este_horario);
	    		dialogBuilder.setCancelable(false);
	    		dialogBuilder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
	    				cursor.moveToPosition(info.position);
	    				DBAdapter.deleteHorario(cursor.getString(0));
	    				reloadList();
	    			}
	    		});
	    		dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {}
	    		});
	    		dialogBuilder.create().show();
				return true;
			default:
				return true;
		}
	}

	private void loadHorarios(){
		String cliente_id=extras.getString("cliente_id");
        cursor=DBAdapter.getClienteHorarios(cliente_id);
		loadDataList();
		adapter=new horarioArrayAdapter(getApplicationContext(), R.layout.item_horario, horarios);
		listHorarios.setAdapter(adapter);
		registerForContextMenu(listHorarios);
		if(cursor.getCount()==0){
			addHorario();
		}
		return;

	}

	public class horarioArrayAdapter extends ArrayAdapter<horario> {
	    private List<horario> horarios = new ArrayList<horario>();

	    public horarioArrayAdapter(Context context, int textViewResourceId,List<horario> objects) {
	        super(context, textViewResourceId, objects);
	        this.horarios = objects;
			notifyDataSetChanged();
	    }

		public int getCount() {
	        return this.horarios.size();
	    }

	    public horario getItem(int index) {
	        return this.horarios.get(index);
	    }

		public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        if (row == null) {
	            // ROW INFLATION
	        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            row = inflater.inflate(R.layout.item_horario, parent, false);
	        }

	        final horario h = getItem(position);
	        final TextView row_tipo_de_horario_input = (TextView) row.findViewById(R.id.row_tipo_de_horario_input);
	        final TextView row_lun_input = (TextView) row.findViewById(R.id.row_lun_input);
	        final TextView row_mar_input = (TextView) row.findViewById(R.id.row_mar_input);
	        final TextView row_mie_input = (TextView) row.findViewById(R.id.row_mie_input);
	        final TextView row_jue_input = (TextView) row.findViewById(R.id.row_jue_input);
	        final TextView row_vie_input = (TextView) row.findViewById(R.id.row_vie_input);
	        final TextView row_sab_input = (TextView) row.findViewById(R.id.row_sab_input);
	        final TextView row_dom_input = (TextView) row.findViewById(R.id.row_dom_input);
	        final TextView row_hora_inicial_input = (TextView) row.findViewById(R.id.row_hora_inicial_input);
	        final TextView row_hora_final_input = (TextView) row.findViewById(R.id.row_hora_final_input);
	        
	        row_tipo_de_horario_input.setText(h.getTipoHorario());
	        row_lun_input.setText(h.getLun());
	        row_mar_input.setText(h.getMar());
	        row_mie_input.setText(h.getMie());
	        row_jue_input.setText(h.getJue());
	        row_vie_input.setText(h.getVie());
	        row_sab_input.setText(h.getSab());
	        row_dom_input.setText(h.getDom());
	        row_hora_inicial_input.setText(h.getHoraInicial());
	        row_hora_final_input.setText(h.getHoraFinal());

	        if(position % 2 == 0){
	        	row_tipo_de_horario_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_lun_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_mar_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_mie_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_jue_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_vie_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_sab_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_dom_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_hora_inicial_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        	row_hora_final_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
	        }else{
	        	row_tipo_de_horario_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_lun_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_mar_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_mie_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_jue_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_vie_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_sab_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_dom_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_hora_inicial_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        	row_hora_final_input.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
	        }
	        return row;
	    }
	}
	
	public class horario {
		private String tipo_horario = "";
		private String lun = "";
		private String mar = "";
		private String mie = "";
		private String jue = "";
		private String vie = "";
		private String sab = "";
		private String dom = "";
		private String hora_inicial = "";
		private String hora_final = "";

		public horario(String tipo_horario, String lun, String mar, String mie, String jue, String vie, String sab, String dom, String hora_inicial, String hora_final){
			this.tipo_horario = tipo_horario;
			this.lun = lun;
			this.mar = mar;
			this.mie = mie;
			this.jue = jue;
			this.vie = vie;
			this.sab = sab;
			this.dom = dom;
			this.hora_inicial = hora_inicial;
			this.hora_final = hora_final;
		}

		/*
		 * 
		 * Getters
		 * 
		 */
		
		public String getTipoHorario() {
			return tipo_horario;
		}
		public String getLun() {
			return lun;
		}
		public String getMar() {
			return mar;
		}
		public String getMie() {
			return mie;
		}
		public String getJue() {
			return jue;
		}
		public String getVie() {
			return vie;
		}
		public String getSab() {
			return sab;
		}
		public String getDom() {
			return dom;
		}
		public String getHoraInicial() {
			return hora_inicial;
		}
		public String getHoraFinal() {
			return hora_final;
		}
	}
	
	private void loadDataList(){
		if(cursor.moveToFirst()){
			do{
				horarios.add(new horario(cursor.getString(1),
						cursor.getString(2),
						cursor.getString(3),
						cursor.getString(4),
						cursor.getString(5),
						cursor.getString(6),
						cursor.getString(7),
						cursor.getString(8),
						cursor.getString(9),
						cursor.getString(10)));				
			}while(cursor.moveToNext());
		}
		return;
	}
	
	private void reloadList(){
		horarios.clear();
		String cliente_id=extras.getString("cliente_id");
        cursor=DBAdapter.getClienteHorarios(cliente_id);
		loadDataList();
		adapter.notifyDataSetChanged();
		return;
	}
	
	 public String checkDigit(int number){
	        return number<=9?"0"+number:String.valueOf(number);
    }
		
	@Override
	public void onBackPressed() {}
}