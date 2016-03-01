package cobros;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.italo_view.R;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Cobros_Pago_Tipo extends Activity {
	private Spinner tipo_pago;
	private View alertView;
	private AlertDialog.Builder dialogBuilder;
	private Context context;
	private TextView banco_label;
	private Spinner banco_input;
	private TextView cuenta_label;
	private EditText cuenta_input;
	private TextView numero_cheque_label;
	private EditText numero_cheque_input;
	private TextView fecha_cheque_label;
	private EditText fecha_cheque_input;
	private TextView fecha_cobro_label;
	private EditText fecha_cobro_input;
	private TextView valor_label;
	private EditText valor_input;
    static private SimpleDateFormat dateFormat2;
	static private Calendar calendar;
	private CalendarView date_picker;
	private TextView n_tranferencia_label;
	private EditText n_tranferencia_input;
	private TableLayout tableNotasCreditos;
	private TextView valor_efectivo_label;
	private EditText valor_efectivo_input;
	private TextView valor_cheque_label;
	private EditText valor_cheque_input;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cobros_pago_tipo);
		init();
	}

	private void setVisibles(int banco,int cuenta,int numeroCheque,int fechaCheque,int fechaCobro,int valor,int nTransf,int notaCredito,int vEfectivo,int vCheque){
		banco_label.setVisibility(banco);
		banco_input.setVisibility(banco);
		cuenta_label.setVisibility(cuenta);
		cuenta_input.setVisibility(cuenta);
		numero_cheque_label.setVisibility(numeroCheque);
		numero_cheque_input.setVisibility(numeroCheque);
		fecha_cheque_label.setVisibility(fechaCheque);
		fecha_cheque_input.setVisibility(fechaCheque);
		fecha_cobro_label.setVisibility(fechaCobro);
		fecha_cobro_input.setVisibility(fechaCobro);
		valor_label.setVisibility(valor);
		valor_input.setVisibility(valor);
		n_tranferencia_label.setVisibility(nTransf);
		n_tranferencia_input.setVisibility(nTransf);
		tableNotasCreditos.setVisibility(notaCredito);
		valor_efectivo_label.setVisibility(vEfectivo);
		valor_efectivo_input.setVisibility(vEfectivo);
		valor_cheque_label.setVisibility(vCheque);
		valor_cheque_input.setVisibility(vCheque);
		return;
	}

	private void cleanValues(){
		banco_input.setSelection(0);
		cuenta_input.setText("");
		numero_cheque_input.setText("");
		fecha_cheque_input.setText("");
		fecha_cobro_input.setText("");
		valor_input.setText("");
		n_tranferencia_input.setText("");
		valor_efectivo_input.setText("");
		valor_cheque_input.setText("");
		return;
	}	

	@SuppressLint("SimpleDateFormat")
	private void init(){
//		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		calendar = Calendar.getInstance();
		dateFormat2= new SimpleDateFormat("MM/dd/yyyy"); 
		context=this;
		tipo_pago=(Spinner)findViewById(R.id.tipo_pago);
		banco_label=(TextView)findViewById(R.id.banco_label);
		banco_input=(Spinner)findViewById(R.id.banco_input);
		cuenta_label=(TextView)findViewById(R.id.cuenta_label);
		cuenta_input=(EditText)findViewById(R.id.cuenta_input);
		numero_cheque_label=(TextView)findViewById(R.id.numero_cheque_label);
		numero_cheque_input=(EditText)findViewById(R.id.numero_cheque_input);
		fecha_cheque_label=(TextView)findViewById(R.id.fecha_cheque_label);
		fecha_cheque_input=(EditText)findViewById(R.id.fecha_cheque_input);
		fecha_cobro_label=(TextView)findViewById(R.id.fecha_cobro_label);
		fecha_cobro_input=(EditText)findViewById(R.id.fecha_cobro_input);
		valor_label=(TextView)findViewById(R.id.valor_label);
		valor_input=(EditText)findViewById(R.id.valor_input);
		n_tranferencia_label=(TextView)findViewById(R.id.n_tranferencia_label);
		n_tranferencia_input=(EditText)findViewById(R.id.n_tranferencia_input);
		tableNotasCreditos=(TableLayout)findViewById(R.id.tableNotasCreditos);
		valor_efectivo_label=(TextView)findViewById(R.id.valor_efectivo_label);
		valor_efectivo_input=(EditText)findViewById(R.id.valor_efectivo_input);
		valor_cheque_label=(TextView)findViewById(R.id.valor_cheque_label);
		valor_cheque_input=(EditText)findViewById(R.id.valor_cheque_input);
		banco_label.setVisibility(View.GONE);
		banco_input.setVisibility(View.GONE);
		cuenta_label.setVisibility(View.GONE);
		cuenta_input.setVisibility(View.GONE);
		numero_cheque_label.setVisibility(View.GONE);
		numero_cheque_input.setVisibility(View.GONE);
		fecha_cheque_label.setVisibility(View.GONE);
		fecha_cheque_input.setVisibility(View.GONE);
		fecha_cobro_label.setVisibility(View.GONE);
		fecha_cobro_input.setVisibility(View.GONE);
		valor_label.setVisibility(View.GONE);
		valor_input.setVisibility(View.GONE);
		n_tranferencia_label.setVisibility(View.GONE);
		n_tranferencia_input.setVisibility(View.GONE);
		tableNotasCreditos.setVisibility(View.GONE);
		valor_efectivo_label.setVisibility(View.GONE);
		valor_efectivo_input.setVisibility(View.GONE);
		valor_cheque_label.setVisibility(View.GONE);
		valor_cheque_input.setVisibility(View.GONE);

		fecha_cheque_input.setInputType(EditorInfo.TYPE_NULL);
		fecha_cobro_input.setInputType(EditorInfo.TYPE_NULL);
		tipo_pago.setOnItemSelectedListener((new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int pos, long id) {
				switch (pos){
					case 0:
						cleanValues();
						fecha_cobro_input.setText(dateFormat2.format(calendar.getTime()));
						setVisibles(View.GONE,View.GONE,View.GONE,View.GONE,View.VISIBLE,View.VISIBLE,View.GONE,View.GONE,View.GONE,View.GONE);
						break;
					case 1:
						cleanValues();
						setVisibles(View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.GONE,View.GONE,View.VISIBLE,View.VISIBLE);
						break;
					case 2:
						cleanValues();
						fecha_cheque_input.setText(dateFormat2.format(calendar.getTime()));
						fecha_cobro_input.setText(dateFormat2.format(calendar.getTime()));
						fecha_cheque_label.setText(getString(R.string.fecha_cheque));
						setVisibles(View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.GONE,View.GONE,View.GONE,View.GONE);
						break;
					case 3:
						cleanValues();
						fecha_cheque_input.setText(dateFormat2.format(calendar.getTime()));
						fecha_cobro_input.setText(dateFormat2.format(calendar.getTime()));
						fecha_cheque_label.setText(getString(R.string.fecha_cheque));
						setVisibles(View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.GONE,View.GONE,View.GONE,View.GONE);
						break;
					case 4:
						cleanValues();
						fecha_cheque_label.setText(getString(R.string.fecha_cheque));
						setVisibles(View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.GONE,View.GONE,View.GONE,View.GONE);
						break;
					case 5:
						cleanValues();
						fecha_cheque_label.setText(getString(R.string.fecha_cheque));
						setVisibles(View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.GONE,View.GONE,View.GONE,View.GONE);
						break;
					case 6:
						cleanValues();
						fecha_cheque_label.setText(getString(R.string.fecha_cheque));
						setVisibles(View.GONE,View.GONE,View.GONE,View.GONE,View.GONE,View.GONE,View.GONE,View.VISIBLE,View.GONE,View.GONE);
						break;
					case 7:
						cleanValues();
						fecha_cheque_label.setText(getString(R.string.fecha_transferencia));
						setVisibles(View.VISIBLE,View.VISIBLE,View.GONE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.GONE,View.GONE,View.GONE);
						break;
					default:
						break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		}));
		fecha_cheque_input.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int pos=tipo_pago.getSelectedItemPosition();
				if(pos == 4 || pos == 5 || pos == 7){
					alertView = getLayoutInflater().inflate(R.layout.date_picker, null);
					dialogBuilder = new AlertDialog.Builder(context);
					dialogBuilder.setTitle(getString(R.string.fecha));
					dialogBuilder.setView(alertView);
					date_picker=(CalendarView)alertView.findViewById(R.id.date_picker);
					dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							fecha_cheque_input.setText(dateFormat2.format(date_picker.getDate()));
						}
					});
					dialogBuilder.create().show();
				}
           	}
		});
		fecha_cobro_input.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int pos=tipo_pago.getSelectedItemPosition();
				if(pos == 4 || pos == 5 || pos == 7){
					alertView = getLayoutInflater().inflate(R.layout.date_picker, null);
					dialogBuilder = new AlertDialog.Builder(context);
					dialogBuilder.setTitle(getString(R.string.fecha));
					dialogBuilder.setView(alertView);
					date_picker=(CalendarView)alertView.findViewById(R.id.date_picker);
					dialogBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							fecha_cobro_input.setText(dateFormat2.format(date_picker.getDate()));
						}
					});
					dialogBuilder.create().show();
				}
           	}
		});
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.cobros_pago_tipo, menu);
		return true;
	}
	
	public boolean addPago(){
		boolean res=true;
		int pos=tipo_pago.getSelectedItemPosition();
		switch (pos){
			case 0:
				boolean val=valor_input.getText().toString().equalsIgnoreCase("");
/*				banco_input.setSelection(0);
				cuenta_input.setText("");
				numero_cheque_input.setText("");
				fecha_cheque_input.setText("");
				fecha_cobro_input.setText("");
				valor_input.setText("");
				n_tranferencia_input.setText("");
				valor_efectivo_input.setText("");
				valor_cheque_input.setText("");*/
				if(val){
					Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.favor_introduzca_un_valor), Toast.LENGTH_LONG).show();
					res=false;
				}else{
					cleanValues();
					fecha_cobro_input.setText(dateFormat2.format(calendar.getTime()));
				}
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				break;
			case 6:
				break;
			case 7:
				break;

		}
		return res;
	}

}
