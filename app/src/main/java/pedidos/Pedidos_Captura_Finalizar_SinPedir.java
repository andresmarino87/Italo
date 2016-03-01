package pedidos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;

import actividad_diaria.Actividad_Diaria_Crear;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

public class Pedidos_Captura_Finalizar_SinPedir extends Activity {
	private Bundle extras;
	private ListView listSinPedir;
	private ListView listTemp;
	private ListView listPromo;
	private ItaloDBAdapter DBAdapter;
	static private Cursor cursor;
	static private Cursor cursorTemporada;
	static private Cursor cursorPromo;
	static private ArrayList<Dejado_pedir> dejadosPedir;
	static private ArrayList<Dejado_pedir> dejadosPedirTemporada;
	static private ArrayList<Dejado_pedir> dejadosPedirPromo;
	private dejadosPedirArrayAdapter adapter;
	private dejadosPedirArrayAdapter adapterTemporada;
	private dejadosPedirArrayAdapter adapterPromo;
	private AlertDialog.Builder dialogBuilder;
	static private String visita_id;
	static private String cliente_id;
	static private String pedido_id;
	static private String oc;
	static private String de;
	static private String estado;
	static private String fecha_entrega;
	static private String obs_pedido;
	static private String obs_compromisos;
	static private String obs_fact;
	static private String tipoPromo;
	static private Promo promoValues;
	static private ArrayList<String> listToma;
	static private ArrayList<String> listTomaFinal;
	private Context context;
	private TextView compromisos_input;
	private View alertView;
	static private int aplicaIva;
	static private ValPed aux;
	static private String horaIni;
	private SimpleDateFormat dateFormat;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedidos_captura_finalizar_sin_pedir);
		init();
		loadList();
		loadListTemporada();
		loadListPromociones();
	}

	@SuppressLint("SimpleDateFormat")
	private void init(){
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		context=this;
		dateFormat=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		horaIni=dateFormat.format(new Date());
		extras = getIntent().getExtras();
		listSinPedir = (ListView)findViewById(R.id.listSinPedir);
		listTemp = (ListView)findViewById(R.id.listTemp);
		listPromo = (ListView)findViewById(R.id.listPromo);
		DBAdapter=new ItaloDBAdapter(this);
		dejadosPedir=new ArrayList<Dejado_pedir>();
		dejadosPedirTemporada=new ArrayList<Dejado_pedir>();
		dejadosPedirPromo=new ArrayList<Dejado_pedir>();
        getApplicationContext().getString(R.string.todas);
        cliente_id=extras.getString("cliente_id");
		extras.getString("cliente_nombre");
		visita_id=extras.getString("visita_id");
        pedido_id=extras.getString("pedido_id");
        listToma=extras.getStringArrayList("listToma");
        listTomaFinal=extras.getStringArrayList("listTomaFinal");
        de=extras.getString("de");

		oc=extras.getString("oc");
		estado=extras.getString("estado");
		fecha_entrega=extras.getString("fecha_entrega");
		obs_pedido=extras.getString("obs_pedido");
		obs_fact=extras.getString("obs_fact");
		obs_compromisos=extras.getString("obs_compromisos");
		aplicaIva=(!DBAdapter.AplicarIVA(cliente_id))?0:1;
        return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.pedidos_captura_finalizar_sin_pedir, menu);
		return true;
	}
	
	@SuppressLint("InflateParams") @Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.guardar:
				aux=new ValPed();
				savePedidosSinPedir();
				savePedidosTemporal();
				savePedidosPromocion();
				DBAdapter.checkAndAddPromoFinal(listTomaFinal,cliente_id,pedido_id);
				if(DBAdapter.getTotalPedidoTemp(pedido_id)<DBAdapter.getValorPedidoMinimo(cliente_id)){
					dialogBuilder = new AlertDialog.Builder(context);
		    		dialogBuilder.setTitle(R.string.alerta);
		    		dialogBuilder.setMessage(getString(R.string.mensaje_pedido_minimo_1)+Utility.formatNumber(DBAdapter.getValorPedidoMinimo(cliente_id))+getString(R.string.mensaje_pedido_minimo_2));
		    		dialogBuilder.setCancelable(false);
		    		dialogBuilder.setPositiveButton(R.string.continuar, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							aux.setComplementoObs("");
							aux.setCompromisoObligatorio("S");
							aux.setContinuar("S");
							aux.setSolicitarCompromiso("S");
							aux.setEstdoPedido("VENTAS");
							aux.setMensajeError("");
							aux.setMotivo(getString(R.string.el_pedido_no_supera_el_minimo_establecido)+" ($"+Utility.formatNumber(DBAdapter.getValorPedidoMinimo(cliente_id))+").");
							estado=aux.getEstdoPedido();
							auxGuardar();
						}
		    		});
		    		dialogBuilder.setNegativeButton(R.string.regresar, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {

						}
		    		});
		    		dialogBuilder.create().show();
				}else{
					aux=calcularEstado();
					estado=aux.getEstdoPedido();
					auxGuardar();
				}
				return true;
	        case R.id.atras:
	        	final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
	        	dialogBuilder.setCancelable(false);
	        	dialogBuilder.setTitle(R.string.alerta);
	    		dialogBuilder.setMessage(getString(R.string.grabacion_no_satisfactoria_del_pedido_numero)+pedido_id);
	    		dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
	    	   			finish();
	    			}
	    		});
	    		dialogBuilder.create().show();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}
	
	@SuppressLint("InflateParams") private void auxGuardar(){
		if(de!=null && de.equalsIgnoreCase("editar_pedido")){
			if(aux.getContinuar().equalsIgnoreCase("S")){
				if(aux.getSolicitarCompromiso().equalsIgnoreCase("S")){
					if(aux.getCompromisoObligatorio().equalsIgnoreCase("S")){
						alertView=getLayoutInflater().inflate(R.layout.dialog_compromiso_pedido, null);
			    		dialogBuilder = new AlertDialog.Builder(this);
			    		dialogBuilder.setTitle(R.string.compromisos_del_vendedor);
						dialogBuilder.setView(alertView);
						dialogBuilder.setCancelable(false);
						compromisos_input=(TextView)alertView.findViewById(R.id.compromisos_input);
						compromisos_input.setText(obs_compromisos);
			    		dialogBuilder.setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
			    			public void onClick(DialogInterface dialog, int which) {}
			    		});
			    		dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			    			public void onClick(DialogInterface dialog, int which) {}
			    		});
			    		final AlertDialog alertDialog=dialogBuilder.create();
			    		alertDialog.show();
						final Button createButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
						createButton.setOnClickListener(new ObsObligatoriaButtonModify(alertDialog));
					}else{
						dialogBuilder = new AlertDialog.Builder(this);
			    		dialogBuilder.setTitle(R.string.compromisos_del_vendedor);
			    		dialogBuilder.setMessage(R.string.desea_ingresar_compromisos);
						dialogBuilder.setCancelable(false);
			    		dialogBuilder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
			    			public void onClick(DialogInterface dialog, int which) {
								alertView=getLayoutInflater().inflate(R.layout.dialog_compromiso_pedido, null);
					    		dialogBuilder = new AlertDialog.Builder(context);
					    		dialogBuilder.setTitle(R.string.compromisos_del_vendedor);
								dialogBuilder.setView(alertView);
								dialogBuilder.setCancelable(false);
								compromisos_input=(TextView)alertView.findViewById(R.id.compromisos_input);
								compromisos_input.setText(obs_compromisos);
					    		dialogBuilder.setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
					    			public void onClick(DialogInterface dialog, int which) {
					    				if(compromisos_input.getText().toString().length()>0){
					    					estado="CREDITOS";
					    					obs_pedido+=(compromisos_input.getText().toString().trim().equals(""))?"":"|"+compromisos_input.getText().toString();
											obs_pedido+=(aux.getComplementoObs().trim().equals(""))?"":"|"+aux.getComplementoObs();
					    					dialogBuilder = new AlertDialog.Builder(context);
								    		dialogBuilder.setTitle(R.string.guardar);
								    		dialogBuilder.setMessage("Estado: " +aux.getEstdoPedido() + "\n"+ "Motivo: "+ aux.getMotivo());
								    		dialogBuilder.setCancelable(false);
								    		dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								    			public void onClick(DialogInterface dialog, int which) {
								    				DBAdapter.beginTransaction();
							    					if(DBAdapter.saveModifyPedido(cliente_id, pedido_id,estado,fecha_entrega,oc, obs_pedido, obs_fact)){
							    						String obsVieja=DBAdapter.getLastEsuObsPedido(pedido_id);
							    						String obsFactVieja=DBAdapter.getLastEsuObsPedidoFact(pedido_id);
								    					if(!obs_pedido.equals("")){
								    						if(obsVieja.equals("")){
									    						DBAdapter.insertEsuObsPedido(cliente_id, pedido_id, obs_pedido, "C");
								    						}else{
								    							if(!obsVieja.equals(obs_pedido)){
								    								DBAdapter.insertEsuObsPedido(cliente_id, pedido_id, obs_pedido, "M");
								    							}
								    						}
								    					}else{
								    						if(!obsVieja.equals("")){
									    						DBAdapter.insertEsuObsPedido(cliente_id, pedido_id, obsVieja, "A");
								    						}
								    					}
							    						
								    					if(!obs_fact.equals("")){
								    						if(obsFactVieja.equals("")){
									    						DBAdapter.insertEsuObsPedidoFact(cliente_id, pedido_id, obs_fact, "C");
								    						}else{
								    							if(!obsFactVieja.equals(obs_fact)){
										    						DBAdapter.insertEsuObsPedidoFact(cliente_id, pedido_id, obs_fact, "M");
								    							}
								    						}
								    					}else{
								    						if(!obsFactVieja.equals("")){
									    						DBAdapter.insertEsuObsPedidoFact(cliente_id, pedido_id, obsFactVieja, "A");
								    						}
								    					}

							    						if(DBAdapter.updateEventoPedido(pedido_id,DBAdapter.getTotalPedidoTemp(pedido_id))){
							    							DBAdapter.setTransactionSuccessful();
							    						}
														cleanPedido();
														setResult(RESULT_OK, null);
											   			finish();
													}else{
														Utility.showMessage(context,getString(R.string.error_al_guardar_pedido));
													}
													DBAdapter.endTransaction();
								    			}});
									    	dialogBuilder.create().show();
					    				}else{
					    					Utility.showMessage(context,getString(R.string.favor_introduzca_un_compromiso));
					    				}
					    			}
					    		});
					    		dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					    			public void onClick(DialogInterface dialog, int which) {}
					    		});
					    		dialogBuilder.create().show();
			    			}
			    		});
			    		dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			    			public void onClick(DialogInterface dialog, int which) {
								obs_pedido+=(aux.getComplementoObs().trim().equals(""))?"":"|"+aux.getComplementoObs();
								estado="VENTAS";
								dialogBuilder = new AlertDialog.Builder(context);
								dialogBuilder.setTitle(R.string.guardar);
								dialogBuilder.setMessage("Estado: " +estado + "\n"+ "Motivo: "+ getString(R.string.no_se_ingreso_compromiso_Del_vendedor));
								dialogBuilder.setCancelable(false);
								dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										DBAdapter.beginTransaction();
										if(DBAdapter.saveModifyPedido(cliente_id, pedido_id,estado,fecha_entrega,oc, obs_pedido, obs_fact)){
				    						String obsVieja=DBAdapter.getLastEsuObsPedido(pedido_id);
				    						String obsFactVieja=DBAdapter.getLastEsuObsPedidoFact(pedido_id);
					    					if(!obs_pedido.equals("")){
					    						if(obsVieja.equals("")){
						    						DBAdapter.insertEsuObsPedido(cliente_id, pedido_id, obs_pedido, "C");
					    						}else{
					    							if(!obsVieja.equals(obs_pedido)){
					    								DBAdapter.insertEsuObsPedido(cliente_id, pedido_id, obs_pedido, "M");
					    							}
					    						}
					    					}else{
					    						if(!obsVieja.equals("")){
						    						DBAdapter.insertEsuObsPedido(cliente_id, pedido_id, obsVieja, "A");
					    						}
					    					}
				    						
					    					if(!obs_fact.equals("")){
					    						if(obsFactVieja.equals("")){
						    						DBAdapter.insertEsuObsPedidoFact(cliente_id, pedido_id, obs_fact, "C");
					    						}else{
					    							if(!obsFactVieja.equals(obs_fact)){
					    								DBAdapter.insertEsuObsPedidoFact(cliente_id, pedido_id, obs_fact, "M");
					    							}
					    						}
					    					}else{
					    						if(!obsFactVieja.equals("")){
						    						DBAdapter.insertEsuObsPedidoFact(cliente_id, pedido_id, obsFactVieja, "A");
					    						}
					    					}

											if(DBAdapter.updateEventoPedido(pedido_id,DBAdapter.getTotalPedidoTemp(pedido_id))){
												DBAdapter.setTransactionSuccessful();
											}
											cleanPedido();
											setResult(RESULT_OK, null);
											finish();
										}else{
											Utility.showMessage(context,getString(R.string.error_al_guardar_pedido));
										}
										DBAdapter.endTransaction();
									}
								});
								dialogBuilder.create().show();
			    			}
			    		});
			    		dialogBuilder.create().show();
					}
				}else{
					obs_pedido+=(aux.getComplementoObs().trim().equals(""))?"":"|"+aux.getComplementoObs();
					dialogBuilder = new AlertDialog.Builder(context);
					dialogBuilder.setTitle(R.string.guardar);
					dialogBuilder.setMessage("Estado: " +aux.getEstdoPedido() + "\n"+ "Motivo: "+ aux.getMotivo());
					dialogBuilder.setCancelable(false);
					dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							DBAdapter.beginTransaction();
							if(DBAdapter.saveModifyPedido(cliente_id, pedido_id,estado,fecha_entrega,oc, obs_pedido, obs_fact)){
	    						String obsVieja=DBAdapter.getLastEsuObsPedido(pedido_id);
	    						String obsFactVieja=DBAdapter.getLastEsuObsPedidoFact(pedido_id);
		    					if(!obs_pedido.equals("")){
		    						if(obsVieja.equals("")){
			    						DBAdapter.insertEsuObsPedido(cliente_id, pedido_id, obs_pedido, "C");
		    						}else{
		    							if(!obsVieja.equals(obs_pedido)){
		    								DBAdapter.insertEsuObsPedido(cliente_id, pedido_id, obs_pedido, "M");
		    							}
		    						}
		    					}else{
		    						if(!obsVieja.equals("")){
			    						DBAdapter.insertEsuObsPedido(cliente_id, pedido_id, obsVieja, "A");
		    						}
		    					}
	    						
		    					if(!obs_fact.equals("")){
		    						if(obsFactVieja.equals("")){
			    						DBAdapter.insertEsuObsPedidoFact(cliente_id, pedido_id, obs_fact, "C");
		    						}else{
		    							if(!obsFactVieja.equals(obs_fact)){
		    								DBAdapter.insertEsuObsPedidoFact(cliente_id, pedido_id, obs_fact, "M");
		    							}
		    						}
		    					}else{
		    						if(!obsFactVieja.equals("")){
			    						DBAdapter.insertEsuObsPedidoFact(cliente_id, pedido_id, obsFactVieja, "A");
		    						}
		    					}
								if(DBAdapter.updateEventoPedido(pedido_id,DBAdapter.getTotalPedidoTemp(pedido_id))){
									DBAdapter.setTransactionSuccessful();
								}
								cleanPedido();
								setResult(RESULT_OK, null);
								finish();
							}else{
								Utility.showMessage(context,getString(R.string.error_al_guardar_pedido));
							}
							DBAdapter.endTransaction();
						}
					});
					dialogBuilder.create().show();
				}
			}else{
				String mesajeError=(!aux.getMensajeError().trim().equalsIgnoreCase(""))?aux.getMensajeError():getString(R.string.mensaje_de_error_validar_pedido);
				Log.e("Italo",aux.getMensajeError()+" "+aux.getComplementoObs());
				Utility.showMessage(context, mesajeError);
			}
		}else{
			if(aux.getContinuar().equalsIgnoreCase("S")){
				if(aux.getSolicitarCompromiso().equalsIgnoreCase("S")){
					if(aux.getCompromisoObligatorio().equalsIgnoreCase("S")){
						alertView=getLayoutInflater().inflate(R.layout.dialog_compromiso_pedido, null);
			    		dialogBuilder = new AlertDialog.Builder(this);
			    		dialogBuilder.setTitle(R.string.compromisos_del_vendedor);
						dialogBuilder.setView(alertView);
						dialogBuilder.setCancelable(false);
						compromisos_input=(TextView)alertView.findViewById(R.id.compromisos_input);
			    		dialogBuilder.setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
			    			public void onClick(DialogInterface dialog, int which) {}
			    		});
			    		dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
			    			public void onClick(DialogInterface dialog, int which) {}
			    		});
			    		final AlertDialog alertDialog=dialogBuilder.create();
			    		alertDialog.show();
						final Button createButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
						createButton.setOnClickListener(new ObsObligatoriaButtonCreate(alertDialog));
					}else{
						dialogBuilder = new AlertDialog.Builder(this);
			    		dialogBuilder.setTitle(R.string.compromisos_del_vendedor);
			    		dialogBuilder.setMessage(R.string.desea_ingresar_compromisos);
						dialogBuilder.setCancelable(false);
			    		dialogBuilder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int which) {
								alertView=getLayoutInflater().inflate(R.layout.dialog_compromiso_pedido, null);
					    		dialogBuilder = new AlertDialog.Builder(context);
					    		dialogBuilder.setTitle(R.string.compromisos_del_vendedor);
								dialogBuilder.setView(alertView);
								dialogBuilder.setCancelable(false);
								compromisos_input=(TextView)alertView.findViewById(R.id.compromisos_input);
					    		dialogBuilder.setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
					    			public void onClick(DialogInterface dialog, int which) {
					    				if(compromisos_input.getText().toString().length()>0){
											obs_pedido+=(compromisos_input.getText().toString().trim().equals(""))?"":"|"+compromisos_input.getText().toString();
											obs_pedido+=(aux.getComplementoObs().trim().equals(""))?"":"|"+aux.getComplementoObs();
											dialogBuilder = new AlertDialog.Builder(context);
								    		dialogBuilder.setTitle(R.string.guardar);
								    		dialogBuilder.setMessage("Estado: " +aux.getEstdoPedido() + "\n"+ "Motivo: "+ aux.getMotivo());
								    		dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								    			public void onClick(DialogInterface dialog, int which) {
								    				DBAdapter.beginTransaction();
													if(DBAdapter.addPedido(cliente_id, pedido_id,estado,fecha_entrega,oc, obs_pedido, obs_fact)){
								    					if(!obs_pedido.equals("")){
								    						DBAdapter.insertEsuObsPedido(cliente_id, pedido_id, obs_pedido, "C");
								    					}
								    					if(!obs_fact.equals("")){
								    						DBAdapter.insertEsuObsPedidoFact(cliente_id, pedido_id, obs_fact, "C");
								    					}
								    					if(DBAdapter.addEvento(visita_id,
								    						"01",
								    						"",
								    						null,
								    						null,
								    						null,
								    						0,
								    						DBAdapter.getTotalPedidoTemp(pedido_id),
								    						false,
								    						Integer.valueOf(pedido_id),horaIni)){
								    						DBAdapter.setTransactionSuccessful();
								    					}
														cleanPedido();
														setResult(RESULT_OK, null);
											   			finish();
													}else{
														Utility.showMessage(context, getString(R.string.error_al_guardar_pedido));
													}
													DBAdapter.endTransaction();
								    			}});
									    	dialogBuilder.create().show();
					    				}else{
					    					Utility.showMessage(context, getString(R.string.favor_introduzca_un_compromiso));
					    				}
					    			}
					    		});
					    		dialogBuilder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
					    			public void onClick(DialogInterface dialog, int which) {
					    				
					    			}
					    		});
					    		dialogBuilder.create().show();
							}
						});
			    		dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int which) {
								obs_pedido+=(aux.getComplementoObs().toString().equals(""))?"":"|"+aux.getComplementoObs();
								estado="VENTAS";
								dialogBuilder = new AlertDialog.Builder(context);
					    		dialogBuilder.setTitle(R.string.guardar);
								dialogBuilder.setMessage("Estado: " +estado + "\n"+ "Motivo: "+ getString(R.string.no_se_ingreso_compromiso_Del_vendedor));
					    		dialogBuilder.setCancelable(false);
					    		dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					    			public void onClick(DialogInterface dialog, int which) {
					    				DBAdapter.beginTransaction();
					    				if(DBAdapter.addPedido(cliente_id, pedido_id,estado,fecha_entrega,oc, obs_pedido, obs_fact)){
					    					if(!obs_pedido.equals("")){
					    						DBAdapter.insertEsuObsPedido(cliente_id, pedido_id, obs_pedido, "C");
					    					}
					    					if(!obs_fact.equals("")){
					    						DBAdapter.insertEsuObsPedidoFact(cliente_id, pedido_id, obs_fact, "C");
					    					}
					    					if(DBAdapter.addEvento(visita_id,
					    							"01",
					    							"",
					    							null,
					    							null,
					    							null,
					    							0,
					    							//revisar
					    							DBAdapter.getTotalPedidoTemp(pedido_id),
					    							false,
					    							Integer.valueOf(pedido_id),horaIni)){
					    						DBAdapter.setTransactionSuccessful();	
					    					}
											cleanPedido();
											setResult(RESULT_OK, null);
								   			finish();
										}else{
											Utility.showMessage(context, getString(R.string.error_al_guardar_pedido));
										}
										DBAdapter.endTransaction();
					    			}});
						    	dialogBuilder.create().show();
							}
						});
			    		dialogBuilder.create().show();
					}
				}else{
					obs_pedido+=(aux.getComplementoObs().trim().equals(""))?"":"|"+aux.getComplementoObs();
					dialogBuilder = new AlertDialog.Builder(context);
		    		dialogBuilder.setTitle(R.string.guardar);
		    		dialogBuilder.setMessage("Estado: " +aux.getEstdoPedido() + "\n"+ "Motivo: "+ aux.getMotivo());
		    		dialogBuilder.setCancelable(false);
		    		dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int which) {
		    				DBAdapter.beginTransaction();
		    				if(DBAdapter.addPedido(cliente_id, pedido_id,estado,fecha_entrega,oc, obs_pedido, obs_fact)){
		    					if(!obs_pedido.equals("")){
		    						DBAdapter.insertEsuObsPedido(cliente_id, pedido_id, obs_pedido, "C");
		    					}
		    					if(!obs_fact.equals("")){
		    						DBAdapter.insertEsuObsPedidoFact(cliente_id, pedido_id, obs_fact, "C");
		    					}
		    					if(DBAdapter.addEvento(visita_id,
		    							"01",
		    							"",
		    							null,
		    							null,
		    							null,
		    							0,
		    							//revisar
		    							DBAdapter.getTotalPedidoTemp(pedido_id),
		    							false,
		    							Integer.valueOf(pedido_id),horaIni)){
		    						DBAdapter.setTransactionSuccessful();
		    					}
		    					cleanPedido();
	    						setResult(RESULT_OK, null);
	    						finish();
	    					}else{
		    					Utility.showMessage(context, getString(R.string.error_al_guardar_pedido));
							}
							DBAdapter.endTransaction();
		    			}});
			    	dialogBuilder.create().show();
				}
			}else{
				String mesajeError=(!aux.getMensajeError().trim().equalsIgnoreCase(""))?aux.getMensajeError():getString(R.string.mensaje_de_error_validar_pedido);
	    		Utility.showMessage(context, mesajeError);
			}
		}
	}

    static public class ViewHolder {
    	TextView row_nombre_input;
    	TextView row_total_input;
    	TextView precio_unitario_input;
    	TextView cantidad_input;
	    ImageButton menos;
	    ImageButton mas;
        TextWatcher textWatcher;

	}

	public class dejadosPedirArrayAdapter extends ArrayAdapter<Dejado_pedir> {
	    private List<Dejado_pedir> dejadosPedir = new ArrayList<Dejado_pedir>();
	    private TableRow tableRow;
	    private int layoutId;
	    
	    public dejadosPedirArrayAdapter(Context context, int textViewResourceId,List<Dejado_pedir> objects) {
	        super(context, textViewResourceId, objects);
	        this.dejadosPedir = objects;
	        this.layoutId=textViewResourceId;
			notifyDataSetChanged();
	    }

		public int getCount() {
	        return this.dejadosPedir.size();
	    }

	    public Dejado_pedir getItem(int index) {
	        return this.dejadosPedir.get(index);
	    }
	    
		@SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {
	       // final ViewHolder holder;
	        final Dejado_pedir p = getItem(position);

	        if (convertView == null  ) {
//	        	holder=new ViewHolder();
	        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        	convertView = inflater.inflate(layoutId, parent, false);

	        	ViewHolder holder=new ViewHolder();
	        	holder.row_nombre_input = (TextView) convertView.findViewById(R.id.row_nombre_input);
	            holder.precio_unitario_input = (TextView) convertView.findViewById(R.id.precio_unitario_input);
	            holder.row_total_input = (TextView) convertView.findViewById(R.id.row_total_input);
	            holder.cantidad_input = (TextView) convertView.findViewById(R.id.cantidad_input);
	            holder.menos=(ImageButton) convertView.findViewById(R.id.menos);
	            holder.mas=(ImageButton) convertView.findViewById(R.id.mas);
	        	convertView.setTag(holder);
	        }

	        final ViewHolder holder = (ViewHolder) convertView.getTag();
	        tableRow=(TableRow) convertView.findViewById(R.id.tableRow);
	        holder.row_nombre_input.setText(p.getId()+" "+p.getNombre());
	        holder.precio_unitario_input.setText("$"+Utility.formatNumber(p.getBase()));
        	if(p.getCantidad()==0){
        		holder.cantidad_input.setText("");
        	}else{
        		holder.cantidad_input.setText(String.valueOf(p.getCantidad()));
        	}
        	holder.row_total_input.setText(Utility.formatNumber(p.getTotal()));

        	holder.menos.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					int cantidad=0;
					try{
						cantidad=Integer.parseInt(holder.cantidad_input.getText().toString());
					}catch(Exception e){
						cantidad=0;
					}
					
					double total;
					if(cantidad>0){
						cantidad-=1;
						total=cantidad*p.getBase();
						holder.cantidad_input.setText(String.valueOf(cantidad));
						p.setCantidad(cantidad);
						p.setTotal(total);
						holder.row_total_input.setText(Utility.formatNumber(total));
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
					double total;
					cantidad+=1;
					total=cantidad*p.getBase();
					holder.cantidad_input.setText(String.valueOf(cantidad));
					p.setCantidad(cantidad);
					p.setTotal(total);
					holder.row_total_input.setText(Utility.formatNumber(total));

				}
			});
	        holder.cantidad_input.setOnClickListener(new ClickCapturaDialogo(p,holder.cantidad_input));

        	
        	if(position % 2 == 0){
        		tableRow.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_par));
        	}else{
        		tableRow.setBackgroundDrawable(getResources().getDrawable(R.drawable.celdas_impar));
        	}
	        return convertView;
	    }
	}


	//Funciones para articulos dejados de pedir
	
	void loadList(){
		cursor=DBAdapter.getProductosDejadosDePedir(cliente_id);
		loadDataList();
		adapter=new dejadosPedirArrayAdapter(getApplicationContext(), R.layout.item_pedido_captura_finalizar, dejadosPedir);
		listSinPedir.setAdapter(adapter);
		return;
	}

	private void loadDataList(){
		if(cursor.moveToFirst()){
			do{
				dejadosPedir.add(new Dejado_pedir(cursor.getString(0),
						cursor.getString(1),
						0,
						cursor.getDouble(2),
						cursor.getDouble(3),
						0));
			}while(cursor.moveToNext());
		}
		return;
	}
	
	//Funciones para las temporadas
	
	void loadListTemporada(){
		cursorTemporada=DBAdapter.getProductosDejadosDePedirTemporada(cliente_id);
		loadDataListTemporada();
		adapterTemporada=new dejadosPedirArrayAdapter(getApplicationContext(), R.layout.item_pedido_captura_finalizar, dejadosPedirTemporada);
		listTemp.setAdapter(adapterTemporada);
		return;
	}

	private void loadDataListTemporada(){
		if(cursorTemporada.moveToFirst()){
			do{
				dejadosPedirTemporada.add(new Dejado_pedir(cursorTemporada.getString(0),
						cursorTemporada.getString(1),
						0,
						cursorTemporada.getDouble(2),
						cursorTemporada.getDouble(3),
						0));
			}while(cursorTemporada.moveToNext());
		}
		return;
	}

	//Funciones para las promociones
	
	void loadListPromociones(){
		cursorPromo=DBAdapter.getProductosDejadosDePedirPromociones(cliente_id);
		loadDataListPromociones();
		adapterPromo=new dejadosPedirArrayAdapter(getApplicationContext(), R.layout.item_pedido_captura_finalizar, dejadosPedirPromo);
		listPromo.setAdapter(adapterPromo);
		return;
	}

	private void loadDataListPromociones(){
		if(cursorPromo.moveToFirst()){
			do{
				dejadosPedirPromo.add(new Dejado_pedir(cursorPromo.getString(0),
						cursorPromo.getString(1),
						0,
						cursorPromo.getDouble(2),
						cursorPromo.getDouble(3),
						0));
			}while(cursorPromo.moveToNext());
		}
		return;
	}
	
	//Clean()
    private void cleanPedido(){
    	DBAdapter.cleanPedidosTemporales();
    	return;
    }
    
    //Funciones para salvar pedidos
	public void savePedidosSinPedir(){
		for (Dejado_pedir p :dejadosPedir){
			int cant=p.getCantidad();
			if(cant>0){
				promoValues=DBAdapter.getPromoValue(p.getId(), cant, listToma);
				tipoPromo="";
				tipoPromo=promoValues.getTipoPromo();
				if(tipoPromo.equalsIgnoreCase("")){
					DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getBase()), String.valueOf(p.getIVA()*aplicaIva), String.valueOf(p.getCantidad()),false,"",0,"");
				}else{
					if(tipoPromo.equalsIgnoreCase("valor")){
						DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getBase()), String.valueOf(p.getIVA()*aplicaIva), String.valueOf(p.getCantidad()),true,promoValues.getPromocionId(),promoValues.getPorcDesc(),"");
					}else{
						if(promoValues.getCantidadBonificada()==0){
							DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getBase()), String.valueOf(p.getIVA()*aplicaIva), String.valueOf(p.getCantidad()),false,promoValues.getPromocionId(),0,"");
						}else{
							DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getBase()), String.valueOf(p.getIVA()*aplicaIva), String.valueOf(p.getCantidad()),true,promoValues.getPromocionId(),0,"");
							DBAdapter.addPedidoTemporal(cliente_id,pedido_id,promoValues.getProductoIdBonificar(), "0", "0", String.valueOf(promoValues.getCantidadBonificada()),true,promoValues.getPromocionId(),0,"S");
						}
					}
				}
			}
		}
		return;
	}

	public void savePedidosTemporal(){
		for (Dejado_pedir p :dejadosPedirTemporada){
			int cant=p.getCantidad();
			if(cant>0){
				promoValues=DBAdapter.getPromoValue(p.getId(), cant, listToma);
				tipoPromo="";
				tipoPromo=promoValues.getTipoPromo();
				if(tipoPromo.equalsIgnoreCase("")){
					DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getBase()), String.valueOf(p.getIVA()*aplicaIva), String.valueOf(p.getCantidad()),false,"",0,"");
				}else{
					if(tipoPromo.equalsIgnoreCase("valor")){
						DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getBase()), String.valueOf(p.getIVA()*aplicaIva), String.valueOf(p.getCantidad()),true,promoValues.getPromocionId(),promoValues.getPorcDesc(),"");
					}else{
						if(promoValues.getCantidadBonificada()==0){
							DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getBase()), String.valueOf(p.getIVA()*aplicaIva), String.valueOf(p.getCantidad()),false,promoValues.getPromocionId(),0,"");
						}else{
							DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getBase()), String.valueOf(p.getIVA()*aplicaIva), String.valueOf(p.getCantidad()),true,promoValues.getPromocionId(),0,"");
							DBAdapter.addPedidoTemporal(cliente_id,pedido_id,promoValues.getProductoIdBonificar(), "0", "0", String.valueOf(promoValues.getCantidadBonificada()),true,promoValues.getPromocionId(),0,"S");
						}
					}
				}
			}
		}
		return;
	}
	
	public void savePedidosPromocion(){
		for (Dejado_pedir p :dejadosPedirPromo){
			int cant=p.getCantidad();
			if(cant>0){
				promoValues=DBAdapter.getPromoValue(p.getId(), cant, listToma);
				tipoPromo="";
				tipoPromo=promoValues.getTipoPromo();
				if(tipoPromo.equalsIgnoreCase("")){
					DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getBase()), String.valueOf(p.getIVA()*aplicaIva), String.valueOf(p.getCantidad()),false,"",0,"");
				}else{
					if(tipoPromo.equalsIgnoreCase("valor")){
						DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getBase()), String.valueOf(p.getIVA()*aplicaIva), String.valueOf(p.getCantidad()),true,promoValues.getPromocionId(),promoValues.getPorcDesc(),"");
					}else{
						if(promoValues.getCantidadBonificada()==0){
							DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getBase()), String.valueOf(p.getIVA()*aplicaIva), String.valueOf(p.getCantidad()),false,promoValues.getPromocionId(),0,"");
						}else{
							DBAdapter.addPedidoTemporal(cliente_id,pedido_id,p.getId(), String.valueOf(p.getBase()), String.valueOf(p.getIVA()*aplicaIva), String.valueOf(p.getCantidad()),true,promoValues.getPromocionId(),0,"");
							DBAdapter.addPedidoTemporal(cliente_id,pedido_id,promoValues.getProductoIdBonificar(), "0", "0", String.valueOf(promoValues.getCantidadBonificada()),true,promoValues.getPromocionId(),0,"S");
						}
					}
				}
			}
		}
		return;
	}

	private ValPed calcularEstado(){
		ValPed res=new ValPed();
		int cant=0;
		double saldo_act=0;
		double cupo_act=0;
		Cursor clienteInfo=DBAdapter.getClienteInfoEstado(cliente_id);
		double total=DBAdapter.getTotalPedidoTemp(pedido_id);
		for(Dejado_pedir p :dejadosPedir){
			cant=p.getCantidad();
			if(cant>0){
				total += p.getTotal();
			}			
		}
		for(Dejado_pedir p :dejadosPedirTemporada){
			cant=p.getCantidad();
			if(cant>0){
				total += p.getTotal();
			}			
		}
		for(Dejado_pedir p :dejadosPedirPromo){
			cant=p.getCantidad();
			if(cant>0){
				total += p.getTotal();
			}			
		}
		if(clienteInfo.moveToFirst() && total>=clienteInfo.getDouble(1)){
			saldo_act=DBAdapter.getSaldoAct(cliente_id);
			cupo_act=DBAdapter.getCupoAct(cliente_id,saldo_act);
			res=validarPedido(total,cupo_act,saldo_act);


			//Continuar la validacion

			if(res.getContinuar().equalsIgnoreCase("s")){
						
			}else{

			}
		}else{
			// aqui va una validacion
			
			res.setMensajeError("El valor del pedido debe ser mínimo de $"+Utility.formatNumber(clienteInfo.getDouble(1))+". No es posible guardar el pedido");
		}
		return res;
	}
	
	private ValPed cobrosHechosHoy(){
		ValPed res= new ValPed();
		String observacion_x_cobro="";
		Cursor listaCobros =DBAdapter.getListasDeCobrosDelDia(cliente_id);
		Cursor listaPagos=null;
		if(listaCobros.moveToFirst()){
			do{
				listaPagos=DBAdapter.getListasDePagos(listaCobros.getString(0));
				if(listaPagos.moveToFirst()){
					do{
						observacion_x_cobro = observacion_x_cobro +"["+listaPagos.getString(0)+","+listaPagos.getString(1)+","+listaPagos.getString(2)+"]";
					}while(listaPagos.moveToNext());
				}
			}while(listaCobros.moveToNext());
		}
		res.setContinuar("S");
		res.setSolicitarCompromiso("S");
		res.setCompromisoObligatorio("S");
		res.setEstdoPedido("CREDITOS");
		res.setMotivo("El cliente tiene cobros sin procesar creados hoy.");
		res.setComplementoObs(observacion_x_cobro);
		res.setMensajeError("");
		return res;
	}
	
	private ValPed validarPedido(double totalPedido, double cupoAct, double saldoAct){
		ValPed res= new ValPed();
		Cursor clienteInfo=DBAdapter.getClienteInfoEstado(cliente_id);
		if(clienteInfo.moveToFirst() && clienteInfo.getDouble(1)>0){
			if(DBAdapter.hayCobrosEnElDia(cliente_id)){
				return cobrosHechosHoy();
			}else{
				if(clienteInfo.getString(clienteInfo.getColumnIndex("cliente_edi")) !=null && clienteInfo.getString(clienteInfo.getColumnIndex("cliente_edi")).equalsIgnoreCase("s")){
					res.setContinuar("S");
					res.setSolicitarCompromiso("N");
					res.setCompromisoObligatorio("N");
					res.setEstdoPedido("CREDITOS");
					res.setMotivo("El cliente maneja sistema EDI.");
					res.setComplementoObs("");
					res.setMensajeError("");
				}else{
					if(clienteInfo.getString(clienteInfo.getColumnIndex("tipo_venta")).equalsIgnoreCase("CR") && totalPedido>cupoAct){
						res.setContinuar("S");
						res.setSolicitarCompromiso("S");
						res.setCompromisoObligatorio("N");
						res.setEstdoPedido("CREDITOS");
						res.setMotivo("El cliente es crédito y el total del pedido supera al cupo actual.");
						res.setComplementoObs("");
						res.setMensajeError("");
					}else{
						if(saldoAct<=0){
							validarClienteSinCartera(res, clienteInfo.getString(clienteInfo.getColumnIndex("cupo_id")),
								clienteInfo.getString(clienteInfo.getColumnIndex("tipo_venta")),
								clienteInfo.getString(clienteInfo.getColumnIndex("distrito_id")),
								clienteInfo.getString(clienteInfo.getColumnIndex("tiene_cheques_dev")),
								clienteInfo.getString(clienteInfo.getColumnIndex("tiene_sucursales")),
								clienteInfo.getInt((clienteInfo.getColumnIndex("dias_promedio_pago"))));
						}else{
							if(clienteInfo.getString(clienteInfo.getColumnIndex("tipo_venta")).equalsIgnoreCase("CO")){
								validarClienteContado(res, clienteInfo.getString(clienteInfo.getColumnIndex("sector_id")),clienteInfo.getInt(clienteInfo.getColumnIndex("dias_docs_sin_legalizar")),cliente_id);
							}else{
								if(clienteInfo.getString(clienteInfo.getColumnIndex("tipo_venta")).equalsIgnoreCase("CR")){
									if(saldoAct>cupoAct){
										res.setContinuar("S");
										res.setSolicitarCompromiso("S");
										res.setCompromisoObligatorio("N");
										res.setEstdoPedido("CREDITOS");
										res.setMotivo("El saldo de cartera del cliente es superior al cupo actual.");
										res.setComplementoObs("");
										res.setMensajeError("");
									}else{
										if(saldoAct+totalPedido>cupoAct){
											res.setContinuar("S");
											res.setSolicitarCompromiso("S");
											res.setCompromisoObligatorio("N");
											res.setEstdoPedido("CREDITOS");
											res.setMotivo("El saldo de cartera del cliente + el valor del pedido es superior al cupo actual.");
											res.setComplementoObs("");
											res.setMensajeError("");
										}else{
											validarCupoCliente(res,clienteInfo.getString(clienteInfo.getColumnIndex("cupo_id")),cliente_id);
											if(res.getContinuar().equals("")){
												if(res.getContinuar().equals("")){
													validarPorAntiguedad(res,clienteInfo.getString(clienteInfo.getColumnIndex("fecha_creacion_erp")),clienteInfo.getString(clienteInfo.getColumnIndex("cupo_id")), cliente_id);
													if(res.getContinuar().equals("")){
														ValidarChequesDevueltosSucursales(res, clienteInfo.getString(clienteInfo.getColumnIndex("tiene_cheques_dev")), clienteInfo.getString(clienteInfo.getColumnIndex("tiene_sucursales")));
													}
												}
											}
										}
									}
								}else{
									res.setContinuar("N");
									res.setSolicitarCompromiso("N");
									res.setCompromisoObligatorio("N");
									res.setEstdoPedido("");
									res.setMotivo("El cliente tiene un dato	desconocido en el tipo de pago.");
									res.setComplementoObs("El cliente tiene el dato	"+clienteInfo.getString(clienteInfo.getColumnIndex("tipo_venta"))+" como tipo de pago.");
									res.setMensajeError("El cliente no tiene un valor válido de pedido mínimo.");
								}
								
							}
						}
								
						
					}
				}
			}
		}else{
			res.setContinuar("N");
			res.setSolicitarCompromiso("N");
			res.setCompromisoObligatorio("N");
			res.setEstdoPedido("");
			res.setMotivo("El cliente no tiene un valor válido de pedido mínimo");
			res.setComplementoObs("");
			res.setMensajeError("El cliente no tiene un valor válido de pedido mínimo");
		}
		return res;
	}
	
	private void validarClienteSinCartera(ValPed res, String cupo_id, String tipoPago, String distrito_id, String tieneCheques, String tieneSucursales, int diasPromedio){
//		Cursor aux=DBAdapter.getCupoDiasPromedioPago(cupo_id);
		if(cupo_id.equalsIgnoreCase("XX")){
			res.setContinuar("S");
			res.setSolicitarCompromiso("N");
			res.setCompromisoObligatorio("N");
			res.setEstdoPedido("CREDITOS");
			res.setMotivo("El cliente tiene cupo XX y debe ser aprobado por créditos");
			res.setComplementoObs("");
			res.setMensajeError("");
		}else{
			if(tipoPago.equalsIgnoreCase("CO")){
				if(!distrito_id.equalsIgnoreCase("01")){
					res.setContinuar("S");
					res.setSolicitarCompromiso("N");
					res.setCompromisoObligatorio("N");
					res.setEstdoPedido("CREDITOS");
					res.setMotivo("El cliente pertenece al distrito "+DBAdapter.getDistritoNombre(distrito_id));
					res.setComplementoObs("");
					res.setMensajeError("");
				}else{
					ValidarChequesDevueltosSucursales(res,tieneCheques,tieneSucursales);
				}
			}else{
				if(tipoPago.equalsIgnoreCase("CR")){
					int promedio_cupo=DBAdapter.getCupoDiasPromedioPago(cupo_id);
					if(diasPromedio>=promedio_cupo){
						res.setContinuar("S");
						res.setSolicitarCompromiso("N");
						res.setCompromisoObligatorio("N");
						res.setEstdoPedido("CREDITOS");
						res.setMotivo("El cliente tiene "+diasPromedio+" días de promedio de pago. El valor permitido debe ser menor a "+promedio_cupo);
						res.setComplementoObs("");
						res.setMensajeError("");
					}else{
						ValidarChequesDevueltosSucursales(res,tieneCheques,tieneSucursales);
					}
				}else{
					res.setContinuar("N");
					res.setSolicitarCompromiso("N");
					res.setCompromisoObligatorio("N");
					res.setEstdoPedido("");
					res.setMotivo("El cliente tiene un dato	desconocido en el tipo de pago");
					res.setComplementoObs("El cliente tiene el dato	"+tipoPago+" como tipo de pago");
					res.setMensajeError("");
				}
			}
		}
		return;
	}
	
	private void ValidarChequesDevueltosSucursales(ValPed res,String tieneDevueltos, String tieneSucursales){
		if(tieneDevueltos.equalsIgnoreCase("S")){
			res.setContinuar("S");
			res.setSolicitarCompromiso("N");
			res.setCompromisoObligatorio("N");
			res.setEstdoPedido("CREDITOS");
			res.setMotivo("El cliente tiene cheques devueltos.");
			res.setComplementoObs("");
			res.setMensajeError("");
		}else{
			if(tieneSucursales.equalsIgnoreCase("S")){
				Log.i("info","cayo en cliente tiene sucursales");
				res.setContinuar("S");
				res.setSolicitarCompromiso("N");
				res.setCompromisoObligatorio("N");
				res.setEstdoPedido("CREDITOS");
				res.setMotivo("El cliente tiene sucursales.");
				res.setComplementoObs("");
				res.setMensajeError("");
			}else{
				res.setContinuar("S");
				res.setSolicitarCompromiso("N");
				res.setCompromisoObligatorio("N");
				res.setEstdoPedido("FACTURAR");
				res.setMotivo("El pedido ha superado las validaciones.");
				res.setComplementoObs("");
				res.setMensajeError("");
			}
		}
		return;
	}
	
	private void validarClienteContado(ValPed res, String sector_id, int dias_lega, String cliente_id){
		Cursor cursor=DBAdapter.getSectorCompromiso(sector_id);
//		Cursor aux=null;
		if(cursor.moveToFirst()){
			if(cursor.getString(0)!=null && cursor.getString(0).equalsIgnoreCase("S")){
				res.setContinuar("S");
				res.setSolicitarCompromiso("S");
				res.setCompromisoObligatorio("S");
				res.setEstdoPedido("CREDITOS");
				res.setMotivo("El cliente es de contado y tiene facturas sin legalizar.");
				res.setComplementoObs(cursor.getString(1));
				res.setMensajeError("");
			}else{
				if(DBAdapter.getFacturasVenc(cliente_id,dias_lega)>0){
					res.setContinuar("S");
					res.setSolicitarCompromiso("N");
					res.setCompromisoObligatorio("N");
					res.setEstdoPedido("VENTAS");
					res.setMotivo("El cliente es de contado y tiene facturas vencidas sin legalizar.");
					res.setComplementoObs("");
					res.setMensajeError("");
				}else{
					res.setContinuar("S");
					res.setSolicitarCompromiso("N");
					res.setCompromisoObligatorio("N");
					res.setEstdoPedido("CREDITOS");
					res.setMotivo("El cliente es de contado y tiene facturas sin legalizar.");
					res.setComplementoObs(cursor.getString(1));
					res.setMensajeError("");
				}
			}
		}			
		return;
	}
	
	private void validarCupoCliente(ValPed res, String cupo_id, String cliente_id){

		if(cupo_id.equalsIgnoreCase("XX")){
			res.setContinuar("S");
			res.setSolicitarCompromiso("N");
			res.setCompromisoObligatorio("N");
			res.setEstdoPedido("CREDITOS");
			res.setMotivo("El cliente tiene cupo XX.");
			res.setComplementoObs("");
			res.setMensajeError("");
		}else{
			int diasVencCupo=DBAdapter.getDiasVencimientoCupo(cupo_id);
			int diasVencFact=DBAdapter.getDiasVencimientoFact(cupo_id);
			if(DBAdapter.getFacturasValCupoCliente1(cliente_id,diasVencCupo) >0){
				res.setContinuar("S");
				res.setSolicitarCompromiso("N");
				res.setCompromisoObligatorio("N");
				res.setEstdoPedido("CREDITOS");
				res.setMotivo("El cliente tiene facturas cuya edad supera los "+diasVencCupo+" dias.");
				res.setComplementoObs("");
				res.setMensajeError("");
			}else{
				if(DBAdapter.getFacturasValCupoCliente2(cliente_id,diasVencCupo,diasVencFact)>0){
					res.setContinuar("S");
					res.setSolicitarCompromiso("N");
					res.setCompromisoObligatorio("N");
					res.setEstdoPedido("CREDITOS");
					res.setMotivo("El cliente tiene facturas con menos de "+diasVencFact+" dias para vencerse.");
					res.setComplementoObs("");
					res.setMensajeError("");
				}
			}
				
		}
		return;
	}
	
	private void validarPorAntiguedad(ValPed res, String fecha_creacion,String cupo_id, String cliente_id){
		int diasCreacion=DBAdapter.getDiasCreacion(cliente_id);
		int diasPermitidos=DBAdapter.getDiasAntiguedadPermitidos(cupo_id);
		if(diasCreacion<=diasPermitidos){
			int dias=DBAdapter.getDiasVencimientoFact(cupo_id);
			if(DBAdapter.getFacturasValidarPorAntiguedad(cliente_id,dias)>0){
				res.setContinuar("S");
				res.setSolicitarCompromiso("N");
				res.setCompromisoObligatorio("N");
				res.setEstdoPedido("CREDITOS");
				res.setMotivo("El cliente no cumple con la antigüedad requerida; tiene "+diasCreacion+" días y para esta validación se necesitan "+dias+" días. Adicionalmente, tiene facturas próximas a vencer.");
				res.setComplementoObs("");
				res.setMensajeError("");
			}else{
				res.setContinuar("S");
				res.setSolicitarCompromiso("S");
				res.setCompromisoObligatorio("N");
				res.setEstdoPedido("CREDITOS");
				res.setMotivo("El cliente no cumple con la antigüedad requerida; tiene "+diasCreacion+" días y para esta validación se necesitan "+dias+" días.");
				res.setComplementoObs("");
				res.setMensajeError("");
			}
		}
		return;
	}
	
    public class ClickCapturaDialogo implements  OnClickListener{
    	private Dejado_pedir p;
    	private TextView cantidadText;
    	
    	public ClickCapturaDialogo(Dejado_pedir p, TextView cantidadText){
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
	
	private class ObsObligatoriaButtonModify implements View.OnClickListener {
		private final Dialog dialogPrincipal;
	    public ObsObligatoriaButtonModify(Dialog dialog) {
	        this.dialogPrincipal = dialog;
	    }
	    @Override
	    public void onClick(View v) {
	    	if(compromisos_input.getText().toString().length()>0){
				estado="CREDITOS";
				obs_pedido+=(compromisos_input.getText().toString().trim().equals(""))?"":"|"+compromisos_input.getText().toString();
				obs_pedido+=(aux.getComplementoObs().trim().equals(""))?"":"|"+aux.getComplementoObs();
				dialogBuilder = new AlertDialog.Builder(context);
	    		dialogBuilder.setTitle(R.string.guardar);
	    		dialogBuilder.setMessage("Estado: " +estado + "\n"+ "Motivo: "+ aux.getMotivo());
	    		dialogBuilder.setCancelable(false);
	    		dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
	    				DBAdapter.beginTransaction();
    					if(DBAdapter.saveModifyPedido(cliente_id, pedido_id,estado,fecha_entrega,oc, obs_pedido, obs_fact)){
    						String obsVieja=DBAdapter.getLastEsuObsPedido(pedido_id);
    						String obsFactVieja=DBAdapter.getLastEsuObsPedidoFact(pedido_id);
    						if(!obs_pedido.equals("")){
	    						if(obsVieja.equals("")){
		    						DBAdapter.insertEsuObsPedido(cliente_id, pedido_id, obs_pedido, "C");
	    						}else{
	    							if(!obsVieja.equals(obs_pedido)){
	    								DBAdapter.insertEsuObsPedido(cliente_id, pedido_id, obs_pedido, "M");
	    							}else{

	    							}
	    						}
	    					}else{
	    						if(!obsVieja.equals("")){
		    						DBAdapter.insertEsuObsPedido(cliente_id, pedido_id, obsVieja, "A");
	    						}else{
		    						
	    						}
	    					}
    						
	    					if(!obs_fact.equals("")){
	    						if(obsFactVieja.equals("")){
	    							DBAdapter.insertEsuObsPedidoFact(cliente_id, pedido_id, obs_fact, "C");
	    						}else{
	    							if(!obsFactVieja.equals(obs_fact)){
	    								DBAdapter.insertEsuObsPedidoFact(cliente_id, pedido_id, obs_fact, "M");
	    							}
	    						}
	    					}else{
	    						if(!obsFactVieja.equals("")){
		    						DBAdapter.insertEsuObsPedidoFact(cliente_id, pedido_id, obsFactVieja, "A");
	    						}
	    					}

	    					if(DBAdapter.updateEventoPedido(pedido_id,DBAdapter.getTotalPedidoTemp(pedido_id))){
    							DBAdapter.setTransactionSuccessful();
    						}
							cleanPedido();
							setResult(RESULT_OK, null);
				   			finish();
						}else{
							Utility.showMessage(context,getString(R.string.error_al_guardar_pedido));
						}
						DBAdapter.endTransaction();
						dialogPrincipal.dismiss();
	    			}});
		    	dialogBuilder.create().show();
			}else{
				Utility.showMessage(context,getString(R.string.favor_introduzca_un_compromiso));
			}
	    }
	}
	
	private class ObsObligatoriaButtonCreate implements View.OnClickListener {
		private final Dialog dialogPrincipal;
	    public ObsObligatoriaButtonCreate(Dialog dialog) {
	        this.dialogPrincipal = dialog;
	    }
	    @Override
	    public void onClick(View v) {
			if(compromisos_input.getText().toString().length()>0){
				obs_pedido+=(compromisos_input.getText().toString().trim().equals(""))?"":"|"+compromisos_input.getText().toString();
				obs_pedido+=(aux.getComplementoObs().trim().equals(""))?"":"|"+aux.getComplementoObs();
				dialogBuilder = new AlertDialog.Builder(context);
	    		dialogBuilder.setTitle(R.string.guardar);
	    		dialogBuilder.setMessage("Estado: " +aux.getEstdoPedido() + "\n"+ "Motivo: "+ aux.getMotivo());
	    		dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
	    				DBAdapter.beginTransaction();
						if(DBAdapter.addPedido(cliente_id, pedido_id,estado,fecha_entrega,oc, obs_pedido, obs_fact)){
							if(!obs_pedido.equals("")){
	    						DBAdapter.insertEsuObsPedido(cliente_id, pedido_id, obs_pedido, "C");
	    					}
	    					if(!obs_fact.equals("")){
	    						DBAdapter.insertEsuObsPedidoFact(cliente_id, pedido_id, obs_fact, "C");
	    					}
	    					if(DBAdapter.addEvento(visita_id,
	    						"01",
	    						"",
	    						null,
	    						null,
	    						null,
	    						0,
	    						DBAdapter.getTotalPedidoTemp(pedido_id),
	    						false,
	    						Integer.valueOf(pedido_id),horaIni)){
	    						DBAdapter.setTransactionSuccessful();
	    					}
							cleanPedido();
							setResult(RESULT_OK, null);
				   			finish();
						}else{
							Utility.showMessage(context, getString(R.string.error_al_guardar_pedido));
						}
						DBAdapter.endTransaction();
						dialogPrincipal.dismiss();
	    			}});
		    	dialogBuilder.create().show();
			}else{
				Utility.showMessage(context, getString(R.string.favor_introduzca_un_compromiso));
			}
	    }
	}
	
	@Override
	public void onBackPressed() {}
}