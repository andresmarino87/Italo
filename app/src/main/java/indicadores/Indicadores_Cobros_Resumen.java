package indicadores;

import com.italo_view.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class Indicadores_Cobros_Resumen extends Activity {
//	static private Bundle extras;
	private static TextView total_input;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_indicadores_cobros_resumen);
		init();
	}

	private void init(){
		total_input=(TextView)findViewById(R.id.total_input);
		total_input.setText("0");
		return;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_salir, menu);
        return true;
    }
    
	@Override
	public void onBackPressed() {}
}