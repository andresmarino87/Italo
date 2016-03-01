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

public class CobrosArrayAdapterPagosLista extends ArrayAdapter<C_Cobros_Pago_Lista> {
	private int layoutResourceId;
	private Context context;
    private List<C_Cobros_Pago_Lista> list = new ArrayList<C_Cobros_Pago_Lista>();

    public CobrosArrayAdapterPagosLista(Context context, int textViewResourceId,List<C_Cobros_Pago_Lista> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.list = objects;
        this.layoutResourceId=textViewResourceId;
		notifyDataSetChanged();
    }

	public int getCount() {
       return this.list.size();
    }

    public C_Cobros_Pago_Lista getItem(int index) {
       return this.list.get(index);
    }

    @SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
        	// ROW INFLATION
        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	row = inflater.inflate(layoutResourceId, parent, false);
        }

        final C_Cobros_Pago_Lista c = getItem(position);
        final TextView row_doc_input = (TextView) row.findViewById(R.id.row_doc_input);
        final TextView row_fecha_input = (TextView) row.findViewById(R.id.row_fecha_input);
        final TextView row_valor_input = (TextView) row.findViewById(R.id.row_valor_input);

        row_doc_input.setText(String.valueOf(c.getIdentificadorPago()));
        row_fecha_input.setText(c.getNombrePago());
        row_valor_input.setText(Utility.formatNumber(c.getValor()));

		if(position % 2 == 0){
			row_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_fecha_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		}else{
			row_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_fecha_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		}
		return row;
    }
}