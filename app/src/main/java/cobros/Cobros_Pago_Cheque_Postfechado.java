package cobros;

import com.italo_view.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Cobros_Pago_Cheque_Postfechado extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cobros_pago_cheque_postfechado);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cobros__pago__cheque__postfechado,
				menu);
		return true;
	}

}
