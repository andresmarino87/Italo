package cobros;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.italo_view.R;

public class CobrosDocsNegativosArrayAdapter extends ArrayAdapter<C_documentos_negativos> {
    private Context context;
    private List<C_documentos_negativos> docs_negativos = new ArrayList<C_documentos_negativos>();
    private int layoutId;
    
    public CobrosDocsNegativosArrayAdapter(Context context, int textViewResourceId,List<C_documentos_negativos> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.docs_negativos = objects;
        this.layoutId=textViewResourceId;
		notifyDataSetChanged();
    }

	public int getCount() {
        return this.docs_negativos.size();
    }

    public C_documentos_negativos getItem(int index) {
        return this.docs_negativos.get(index);
    }

    @SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
    	final C_documentos_negativos d = getItem(position);
        View row = convertView;
        if (row == null) {
            // ROW INFLATION
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	row = inflater.inflate(layoutId, parent, false);
        }

        final TextView row_doc_input = (TextView) row.findViewById(R.id.row_doc_input);
        final TextView row_fecha_input = (TextView) row.findViewById(R.id.row_fecha_input);
        final TextView row_valor_input = (TextView) row.findViewById(R.id.row_valor_input);

        row_doc_input.setText(d.getDocId());
        row_fecha_input.setText(d.getFechaDoc());
        row_valor_input.setText(String.valueOf(d.getValorDisponible()));

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
