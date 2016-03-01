package cobros;

import java.util.ArrayList;
import java.util.List;

import utilidades.Utility;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.italo_view.R;

public class CobrosSincronizadosArrayAdapter extends ArrayAdapter<CobrosSincronizados> {
	private Context context;
	private int layoutId;
	private List<CobrosSincronizados> cobros = new ArrayList<CobrosSincronizados>();
	private TextView row_sync_input;
	private TextView row_cobro_input;
	private TextView row_fecha_input;
	private TextView row_hora_input;
	private TextView row_valor_input;
	    
	public CobrosSincronizadosArrayAdapter(Context context, int textViewResourceId,List<CobrosSincronizados> objects) {
		super(context, textViewResourceId, objects);
		this.cobros = objects;
		this.context = context;
		layoutId=textViewResourceId;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.cobros.size();
	}

	public CobrosSincronizados getItem(int index) {
		return this.cobros.get(index);
	}

	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		final CobrosSincronizados c = getItem(position);
		
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutId, parent, false);
		}

		row_sync_input = (TextView) row.findViewById(R.id.row_sync_input);
		row_cobro_input = (TextView) row.findViewById(R.id.row_cobro_input);
		row_fecha_input = (TextView) row.findViewById(R.id.row_fecha_input);
		row_hora_input = (TextView) row.findViewById(R.id.row_hora_input);
		row_valor_input = (TextView) row.findViewById(R.id.row_valor_input);

		row_sync_input.setText(c.getSinc());
		row_cobro_input.setText(c.getCobroId());
		row_fecha_input.setText(c.getFechaCobro());
		row_hora_input.setText(c.getHoraCobro());
		row_valor_input.setText(Utility.formatNumber(c.getValorCobro()));

        if(position % 2 == 0){
        	row_sync_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_cobro_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_fecha_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_hora_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        }else{
        	row_sync_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
        	row_cobro_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
        	row_fecha_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
        	row_hora_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
        	row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		}
        return row;
    }	
}