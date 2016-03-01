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

public class CobrosDiasArrayAdapter extends ArrayAdapter<CobroDia> {
	private Context context;
	private int layoutId;
	private List<CobroDia> cobros = new ArrayList<CobroDia>();
    private TextView row_n_pago_input;
    private TextView row_documento_input;
    private TextView row_saldo_input;
    private TextView row_pago_input;
    private TextView row_diferencia_input;
    
    
    public CobrosDiasArrayAdapter(Context context, int textViewResourceId,List<CobroDia> objects) {
        super(context, textViewResourceId, objects);
        this.cobros = objects;
        this.context = context;
        layoutId=textViewResourceId;
		notifyDataSetChanged();
    }

	public int getCount() {
        return this.cobros.size();
    }

    public CobroDia getItem(int index) {
        return this.cobros.get(index);
    }

	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final CobroDia c = getItem(position);

        if (row == null) {
        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutId, parent, false);
        }

        row_n_pago_input = (TextView) row.findViewById(R.id.row_n_pago_input);
        row_documento_input = (TextView) row.findViewById(R.id.row_documento_input);
        row_saldo_input = (TextView) row.findViewById(R.id.row_saldo_input);
        row_pago_input = (TextView) row.findViewById(R.id.row_pago_input);
        row_diferencia_input = (TextView) row.findViewById(R.id.row_diferencia_input);
        
        row_n_pago_input.setText(c.getNPago());
        row_documento_input.setText(c.getDocumento());
        row_saldo_input.setText(Utility.formatNumber(c.getSaldo()));
        row_pago_input.setText(Utility.formatNumber(c.getPago()));
        row_diferencia_input.setText(Utility.formatNumber(c.getDiferencia()));

        if(position % 2 == 0){
        	row_n_pago_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_documento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_saldo_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_pago_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_diferencia_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        }else{
        	row_n_pago_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
        	row_documento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
        	row_saldo_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
        	row_pago_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
        	row_diferencia_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		}
        return row;
    }
}
