package com.italo_view;


import bd_utilidades.ItaloDBAdapter;
import cliente.Cliente;
import cartera.Cartera;
import consignaciones.Consignaciones_Tab;
import easysync.EasySync;
import easysync.EasySyncFileService;
import easysync.EasySyncService;
import paretos.Paretos;
import pedidos_negados.Pedidos_Negados;
import utilidades.EasyUtilidades;
import utilidades.ImageAdapter;
import utilidades.Utility;
import actividad_diaria.Actividad_Diaria_Menu;
import android.os.Bundle;
import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import articulos.Articulos;
import asistente.Asistente;


public class MainActivity extends Activity {
	private Intent i;
	private long ultimaSincronizacionMenu = 0; //System.currentTimeMillis();
	Context context;
	NotificationManager notificationManager;
	private EasySyncEstadoReceiver estadoEasySync;
	ImageView ivIconoEasySync;

	private String easySyncEstado = "";
	private String easySyncMensaje = "";
	private String easySyncFechaHora = "";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);        
		String svcName = Context.NOTIFICATION_SERVICE;
		notificationManager = (NotificationManager) getSystemService(svcName);
		ivIconoEasySync = (ImageView) findViewById(R.id.item_sincronizacion);
		
		ivIconoEasySync.setVisibility(View.GONE);
        
        context = this;
        
        Utility.exportDB(context);
        
        final GridView gridview = (GridView) findViewById(R.id.menu);
        gridview.setAdapter(new ImageAdapter(this));
        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        		switch(position){
    				case 0:
    					i = new Intent(getApplicationContext(), Actividad_Diaria_Menu.class);
    					i.putExtra("de", "menu");
    					startActivity(i);
    					break;
    				case 1:
    					i = new Intent(getApplicationContext(), Cliente.class);
    					i.putExtra("de", "menu");
    					startActivity(i);
    					break;
    				case 2:
    					i = new Intent(getApplicationContext(), Articulos.class);
    					i.putExtra("de", "menu");
    					startActivity(i);
    					break;
    				case 3:
    					i = new Intent(getApplicationContext(), Consignaciones_Tab.class);
    					i.putExtra("de", "menu");
    					startActivity(i);
    					break;
    				case 4:
    					i = new Intent(getApplicationContext(), Cartera.class);
    					i.putExtra("de", "menu");
    					startActivity(i);
    					break;
    				case 5:
/*    					i = new Intent(getApplicationContext(), Indicadores_Tab.class);
    					i.putExtra("de", "menu");
    					startActivity(i);
 */   					break;
    				case 6:
    					i = new Intent(getApplicationContext(), Asistente.class);
    					i.putExtra("de", "menu");
    					startActivity(i);
    					break;
    				case 7:
    					i = new Intent(getApplicationContext(), Paretos.class);
    					i.putExtra("de", "menu");
    					startActivity(i);
    					break;
    				case 8:
    					i = new Intent(getApplicationContext(), Pedidos_Negados.class);
    					i.putExtra("de", "menu");
    					startActivity(i);
    					break;
    				case 9:
						Log.i("info","sincronizar click");
    					if (ultimaSincronizacionMenu + 60000 <= System.currentTimeMillis()) {
    						Log.i("info","sincronizar");
    						EasySync _easySync = new EasySync(context, EasyUtilidades.InformacionGloabaG(new ItaloDBAdapter(context)));
     						_easySync.GenerarNovedadesUp();
    						ultimaSincronizacionMenu = System.currentTimeMillis();
    						EasyUtilidades.SolicitarEasySync(context, EasyUtilidades.InformacionGloabaG(new ItaloDBAdapter(context)));
    					}
    					break;
    					
    				default : break;	

    			}
            }
        });
    }

	public void onResume() {
		super.onResume();

		// IniciarServicios();

		
		notificationManager.cancel(EasySyncService.NOTIFICATION_ESTADO_ID);

		IntentFilter filter;
		filter = new IntentFilter(EasySyncService.NUEVO_ESTADO_EASYSYNC);
		estadoEasySync = new EasySyncEstadoReceiver();
		registerReceiver(estadoEasySync, filter);

	}	
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	
    	//Global global = (Global)this.getApplicationContext();
    	
        switch (item.getItemId()){
        	case R.id.salir_app:
        		finish();
        		return true;
        		
    		case R.id.menu_item_recovery:
    			try {
    				GlobaG gGlobal = EasyUtilidades.InformacionGloabaG(new ItaloDBAdapter(context));
    				gGlobal.CrearRecovery = true;
    				ultimaSincronizacionMenu = System.currentTimeMillis();
    				EasyUtilidades.SolicitarEasySync(this, gGlobal);
    			} catch (Throwable e) {
    				e.printStackTrace();
    			}
    			return true;
    		case R.id.menu_item_easysync:
    			try {
    				AlertDialog.Builder alert = new AlertDialog.Builder(context);

    				alert.setTitle(getResources().getString(
    						R.string.easysync_consultar_estado));
    				alert.setIcon(getResources()
    						.getDrawable(R.drawable.advertencia));

    				String mensajeEstadoEasySync = getResources().getString(
    						R.string.easysync_fechahora)
    						+ ": "
    						+ this.easySyncFechaHora
    						+ ".\n"
    						+ getResources().getString(R.string.easysync_estado)
    						+ ": "
    						+ this.easySyncEstado
    						+ ".\n"
    						+ getResources().getString(
    								R.string.easysync_observacion)
    						+ ": "
    						+ this.easySyncMensaje + ".\n";

    				alert.setMessage(mensajeEstadoEasySync);
    				alert.setPositiveButton(getResources().getString(R.string.si),
    						new DialogInterface.OnClickListener() {
    							public void onClick(DialogInterface dialog,
    									int whichButton) {
    								setResult(RESULT_OK);

    							}
    						});

    				alert.create().show();

    			} catch (Throwable e) {
    				e.printStackTrace();
    			}
    			return true;
        		
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }  
    
	public class EasySyncEstadoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			notificationManager.cancel(EasySyncService.NOTIFICATION_ESTADO_ID);
			////Log.i("info2", "mensaje recibido estado easysync");

			Bundle extras = intent.getExtras();
			if (extras != null) {
				easySyncEstado = extras.getString("estado");
				easySyncMensaje = extras.getString("mensaje");
				easySyncFechaHora = extras.getString("fechahora");
				ivIconoEasySync.setVisibility(View.VISIBLE);
				if (easySyncEstado.compareTo("OK") == 0) {
					ivIconoEasySync.setImageResource(R.drawable.verde);
				} else if (easySyncEstado.compareTo("ERROR") == 0) {
					ivIconoEasySync.setImageResource(R.drawable.rojo);
				} else if (easySyncEstado.compareTo("SINCRONIZANDO") == 0) {
					ivIconoEasySync.setImageResource(R.drawable.amarillo);
				} else {
					ivIconoEasySync.setImageResource(R.drawable.gris);
				}
			}

		}

	}
	
	@Override
	public void onBackPressed() {}
}