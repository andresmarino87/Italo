package actividad_diaria;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import utilidades.Utility;
import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.ListView;
import android.widget.TextView;

public class Actividad_Diaria_Menu extends Activity {
	private static final int REQUEST_EXIT = 0;
	private Intent i;
	private AlertDialog alertDialog;
	private AlertDialog.Builder dialogBuilder;
	private ListView listPlanes;
	private ItaloDBAdapter DBAdapter;
	private Cursor cursor;
	private View alertView;
	static private ArrayList<Plan> planes;
	static private PlanesArrayAdapter adapter;
	private CalendarView date_picker;
	static private SimpleDateFormat dateFormat;
	static private SimpleDateFormat dateFormat2;
	static private TextView vendedor_input;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actividad_diaria_menu);
		init();
		loadName();
		loadList();
		registerForContextMenu(listPlanes);
		listPlanes.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				try{
					String [] dAux=null;
					int fAux=0;
					int fAct=0;
					int isToday;
					Date now = new Date();
				    String strDate = dateFormat2.format(now);
					Plan aux=planes.get(position);
					i = new Intent(getApplicationContext(),Actividad_Diaria_Consultar.class);
					i.putExtra("n_plan", aux.getPlan());
					dAux= aux.getFecha().split("/");
					fAux= Integer.parseInt(dAux[2]+dAux[0]+dAux[1]);
					fAct=Integer.parseInt(strDate);
					if(fAux < fAct){
						isToday=-1;
					}else if(fAux == fAct){
						isToday=0;
					}else{
						isToday=1;
					}
					i.putExtra("isToday", isToday);
					startActivityForResult(i, REQUEST_EXIT);
				}catch(Exception e){
					Log.e("info","error onclick plan "+e);
				}
			}
		});
	}

	@SuppressLint("SimpleDateFormat")
	private void init() {
		context=this;
		listPlanes = (ListView) findViewById(R.id.listPlanes);
		DBAdapter = new ItaloDBAdapter(this);
		planes = new ArrayList<Plan>();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat2 = new SimpleDateFormat("yyyyMMdd");
		vendedor_input = (TextView) findViewById(R.id.vendedor_input);
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actividad_diaria_menu, menu);
		return true;
	}

	private class CheckDate implements View.OnClickListener {
		private final Dialog dialog;
	    public CheckDate(Dialog dialog) {
	        this.dialog = dialog;
	    }
	    @Override
	    public void onClick(View v) {
			try {
				String date=dateFormat.format(date_picker.getDate());
				if (dateFormat.parse(dateFormat.format(new Date())).compareTo(dateFormat.parse(date)) <= 0) {
					if(DBAdapter.ExistInfoRutaInDate(date)){
						DBAdapter.cleanPlanTrabajoTemp();
						i = new Intent(getApplicationContext(), Actividad_Diaria_Crear.class);
						i.putExtra("fecha", date);
						i.putExtra("vendedor", vendedor_input.getText().toString());
						i.putExtra("isNew",true);
						dialog.dismiss();
						startActivityForResult(i, REQUEST_EXIT);
					}else{
						Utility.showMessage(context, R.string.no_se_puede_crear_plan_para_una_fecha);
					}
				} else {
					Utility.showMessage(context, R.string.fecha_mayor_hoy);
				}
			}catch (java.text.ParseException e) {
				Log.e("info","Menu actividad diaria"+ e.toString());
			}
	    }
	}
	
	@SuppressLint("InflateParams") @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.crear:
			alertView = getLayoutInflater().inflate(R.layout.date_picker, null);
			dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle(getString(R.string.seleccione_una_fecha));
			dialogBuilder.setView(alertView);
			dialogBuilder.setCancelable(false);
			date_picker = (CalendarView) alertView.findViewById(R.id.date_picker);
			date_picker.setOnDateChangeListener(new OnDateChangeListener(){
				@Override
				public void onSelectedDayChange(CalendarView arg0, int arg1,int arg2, int arg3) {}
				
			});
			dialogBuilder.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {}
			});
			dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {}
			});
			alertDialog=dialogBuilder.create();
			alertDialog.show();
			final Button createButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
			createButton.setOnClickListener(new CheckDate(alertDialog));
			return true;
		case R.id.atras:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	void loadList() {
		DBAdapter.updateBlackDates();
		cursor = DBAdapter.getPlanes();
		loadDataList();
		adapter = new PlanesArrayAdapter(context,R.layout.item_planes, planes,DBAdapter.getAsesorId(),DBAdapter.isSupervisor());
		listPlanes.setAdapter(adapter);
		registerForContextMenu(listPlanes);
		return;
	}

	
	void reloadList() {
		planes.clear();
		cursor = DBAdapter.getPlanes();
		loadDataList();
		adapter.notifyDataSetChanged();
		return;
	}


	private void loadDataList() {
		if(cursor.moveToFirst()){
			do{
				planes.add(new Plan(
					cursor.getString(0),
					cursor.getString(1),
					cursor.getString(2),
					cursor.getString(3)));
//					""));
			}while(cursor.moveToNext());
		}
		if(cursor!=null){
			cursor.close();
		}
		return;
		
	}

	private void loadName() {
		vendedor_input.setText(DBAdapter.getVendedor());
		return;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.eliminar);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case 0:
   				final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
				dialogBuilder.setCancelable(false);
				dialogBuilder.setTitle(R.string.alerta);
				dialogBuilder.setMessage(R.string.desea_eliminar_la_actividad_diaria);
				dialogBuilder.setPositiveButton(R.string.eliminar,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Plan aux=planes.get(info.position);
						String [] dAux=null;
						int fAux=0;
						int fAct=0;
						Date now = new Date();
					    String strDate = dateFormat2.format(now);
						dAux= aux.getFecha().split("/");
						fAux= Integer.parseInt(dAux[2]+dAux[0]+dAux[1]);
						fAct=Integer.parseInt(strDate);
						if(fAux>fAct){
							if(DBAdapter.deleteActividadDiaria((planes.get(info.position)).getPlan())){
								reloadList();
							}else{
								Utility.showMessage(context, R.string.hubo_un_error_al_eliminar_el_plan_de_trabajo);
							}
						}else{
							Utility.showMessage(context, R.string.no_se_puede_eliminar_un_par_de_trabajo_con_fecha_menor);
						}
					}
				});
				dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {}
				});
        		dialogBuilder.create().show();
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_EXIT) {
			if (resultCode == RESULT_OK) {
				setResult(RESULT_OK, null);
				this.finish();
			}
		}
	}

	@Override
	protected void onResume(){
		super.onResume();
		reloadList();
		(new checkPlanTrabajo()).execute();
	}
	
	@Override
	public void onBackPressed() {}
	
    public class checkPlanTrabajo extends AsyncTask<String, Void, Boolean>{
        @Override
        protected void onPreExecute() {
        }

		@Override
    	protected Boolean doInBackground(String... urls) {
    		Boolean result=true;
            DBAdapter.systemClosePlanTrabajo();
            return result;
    	}
    	
		@Override
        protected void onPostExecute(Boolean result) {}
    }
}