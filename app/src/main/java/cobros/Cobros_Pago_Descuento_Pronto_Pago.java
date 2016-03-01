package cobros;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.italo_view.R;

import utilidades.Utility;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class Cobros_Pago_Descuento_Pronto_Pago extends Activity {
	private Intent i;
	private Context context;
	private static ArrayList<C_documentos> documentos;
	private static ArrayList<C_documentos> documentosFitrados;
	private ListView listDescuentosProntoPago;
	private TextView total_descuento_input;
	private Bundle extras;
	private static float Var_Acumulado_pronto_pago=0;
	private AlertDialog.Builder dialogBuilder;
	private CobrosDescuentoProntoPagoArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cobro_pago_descuento_pronto_pago);
		Locale.setDefault(new Locale("us", "US"));
		init();
		iniciarActividad();
	}

	private void init(){
		context=this;
		extras = getIntent().getExtras();
		if(extras!=null){
			C_documentos_wrapper documentosWrapper=(C_documentos_wrapper) extras.getSerializable("documentos");
			documentos=documentosWrapper.getItems();
		}
		listDescuentosProntoPago=(ListView)findViewById(R.id.listDescuentosProntoPago);
		total_descuento_input=(TextView)findViewById(R.id.total_descuento_input);
		documentosFitrados=new ArrayList<C_documentos>();
		return;	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.cobro__pago__descuento__pronto__pago,menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
			case R.id.guardar:
				Var_Acumulado_pronto_pago=0;
				for(C_documentos auxFilt:documentosFitrados){
					for(C_documentos auxDocs:documentos){
						if(auxDocs.getDocId().equalsIgnoreCase(auxFilt.getDocId())){
							auxDocs.setAplicarDescProntoPago(auxFilt.getAplicarDescProntoPago());
							if(auxDocs.getAplicarDescProntoPago().equalsIgnoreCase("S")){
								auxDocs.setValorDisponible(auxDocs.getSaldoAnterior()-auxDocs.getValorDescProntoPago());
								Var_Acumulado_pronto_pago = Var_Acumulado_pronto_pago + auxDocs.getValorDescProntoPago();
							}else{
								auxDocs.setValorDisponible(auxDocs.getSaldoAnterior());
							}
							
						}
					}
				}
				Log.i("info","berore close+"+ Var_Acumulado_pronto_pago);
				i = new Intent();
				i.putExtra("var_acumulado_descuento_pronto_pago", Var_Acumulado_pronto_pago);
				i.putExtra("documentos", new C_documentos_wrapper(documentos));
				setResult(RESULT_OK, i);
				finish();
				return true;
			case R.id.atras:
				i = new Intent();
				setResult(RESULT_OK, i);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void iniciarActividad(){
		Var_Acumulado_pronto_pago = 0;
		for(C_documentos aux: documentos){
			if(aux.getValorDescProntoPago()>0){
				documentosFitrados.add(aux);
				if(aux.getAplicarDescProntoPago().equalsIgnoreCase("S")){
					Var_Acumulado_pronto_pago=Var_Acumulado_pronto_pago+aux.getValorDescProntoPago();
				}
			}
		}
		if(documentosFitrados.size()==0){
			dialogBuilder = new AlertDialog.Builder(context);
			dialogBuilder.setTitle(getString(R.string.alerta));
			dialogBuilder.setMessage(R.string.no_se_encontraron_descuentos_por_pronto_pago);
			dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					i = new Intent();
					setResult(RESULT_OK, i);
					finish();
				}
			});
			dialogBuilder.create().show();
		}
		total_descuento_input.setText(Utility.formatNumber(Var_Acumulado_pronto_pago));
		Log.i("info",""+documentosFitrados.size());
		adapter=new CobrosDescuentoProntoPagoArrayAdapter(getApplicationContext(), R.layout.item_desc_pronto_pago, documentosFitrados);
		listDescuentosProntoPago.setAdapter(adapter);
		return;
	}
	
	public class CobrosDescuentoProntoPagoArrayAdapter extends ArrayAdapter<C_documentos> {
		private int layoutResourceId;
		private List<C_documentos> list = new ArrayList<C_documentos>();

		public CobrosDescuentoProntoPagoArrayAdapter(Context context, int textViewResourceId,List<C_documentos> objects) {
			super(context, textViewResourceId, objects);
			this.list = objects;
			this.layoutResourceId=textViewResourceId;
			notifyDataSetChanged();
		}

		public int getCount() {
			return this.list.size();
		}

		public C_documentos getItem(int index) {
			return this.list.get(index);
		}

	    public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        final C_documentos d = getItem(position);

	        if (row == null) {
	        	// ROW INFLATION
	        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        	row = inflater.inflate(layoutResourceId, parent, false);
	        }

	        final TextView documento_input = (TextView) row.findViewById(R.id.documento_input);
	        final TextView valor_factura_input = (TextView) row.findViewById(R.id.valor_factura_input);
	        final TextView valor_descuento_pronto_pago_input = (TextView) row.findViewById(R.id.valor_descuento_pronto_pago_input);
	        final RadioButton si = (RadioButton) row.findViewById(R.id.si);
	        final RadioButton no = (RadioButton) row.findViewById(R.id.no);

	        documento_input.setText(d.getDocId());
	        valor_factura_input.setText(Utility.formatNumber(d.getValorDisponible()));
	        valor_descuento_pronto_pago_input.setText(Utility.formatNumber(d.getValorDescProntoPago()));
	        if(d.getAplicarDescProntoPago().equalsIgnoreCase("S")){
	        	si.setChecked(true);
	        }else{
//	        	si.setSelected(false);
	        	no.setChecked(true);
	        }
	        si.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					if(arg1){
						Var_Acumulado_pronto_pago=Var_Acumulado_pronto_pago+d.getValorDescProntoPago();
						Log.i("info","Si esta chequeado no hago nada");
						total_descuento_input.setText(Utility.formatNumber(Var_Acumulado_pronto_pago));
			        	d.setAplicarDescProntoPago("S");
					}else{
						Log.i("info","SI No esta chequeado");
					}
				}
			});
	        no.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					if(arg1){
						Var_Acumulado_pronto_pago=Var_Acumulado_pronto_pago-d.getValorDescProntoPago();
						Log.i("info","NO esta chequeado no hago nada");
						total_descuento_input.setText(Utility.formatNumber(Var_Acumulado_pronto_pago));
			        	d.setAplicarDescProntoPago("N");
					}else{
						Log.i("info","NO No esta chequeado");
					}
				}
			});
//			total_descuento_input.setText(Utility.formatNumber(Var_Acumulado_pronto_pago));
			return row;
	    }
	}
	
	@Override
	public void onBackPressed() {}
}