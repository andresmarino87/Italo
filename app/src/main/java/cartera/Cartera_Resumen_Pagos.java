package cartera;

import com.italo_view.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

public class Cartera_Resumen_Pagos extends Activity {
	private TextView todos_los_clientes_label;
	private TextView cliente_table_label;
	private ListView listPagos;
	private	ListView listDetalleRecaudo;
	private	ListView listFormaPago;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cartera_resumen_pagos);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cartera__resumen__pagos, menu);
		return true;
	}

}
