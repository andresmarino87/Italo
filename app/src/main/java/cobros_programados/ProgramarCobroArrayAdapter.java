package cobros_programados;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.italo_view.R;

public class ProgramarCobroArrayAdapter extends ArrayAdapter<Programar_Cobro_Item> {
    private Context context;
    private List<Programar_Cobro_Item> facturas = new ArrayList<Programar_Cobro_Item>();
	private int layoutId;
	
    public ProgramarCobroArrayAdapter(Context context, int textViewResourceId, List<Programar_Cobro_Item> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.facturas = objects;
        this.layoutId = textViewResourceId;
		notifyDataSetChanged();
    }

	public int getCount() {
        return this.facturas.size();
    }

    public Programar_Cobro_Item getItem(int index) {
        return this.facturas.get(index);
    }

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	row = inflater.inflate(layoutId, parent, false);
        }
        
        final Programar_Cobro_Item c = getItem(position);
        
	    final TextView row_prog_cobro_tipo_doc_input = (TextView) row.findViewById(R.id.row_prog_cobro_tipo_doc_input);
	    final TextView row_prog_cobro_documento_input = (TextView) row.findViewById(R.id.row_prog_cobro_documento_input);
	    final TextView row_prog_cobro_valor_input = (TextView) row.findViewById(R.id.row_prog_cobro_valor_input);
	    final TextView row_prog_cobro_fecha_doc_input = (TextView) row.findViewById(R.id.row_prog_cobro_fecha_input);
	    final TextView row_prog_cobro_fecha_ven_input = (TextView) row.findViewById(R.id.row_prog_cobro_vencimiento_input);
	    
	    row_prog_cobro_tipo_doc_input.setText(c.getTipoDocumento());
	    row_prog_cobro_documento_input.setText(c.getNumeroDocumento());
	    row_prog_cobro_valor_input.setText(c.getSaldoPendiente());
	    row_prog_cobro_fecha_doc_input.setText(c.getFechaDocumento());
	    row_prog_cobro_fecha_ven_input.setText(c.getFechaVencimiento());
	    
	    
	    if (position % 2 == 0) 
	    {
	    	row_prog_cobro_tipo_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		    row_prog_cobro_documento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		    row_prog_cobro_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		    row_prog_cobro_fecha_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		    row_prog_cobro_fecha_ven_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		} else{
	    	row_prog_cobro_tipo_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		    row_prog_cobro_documento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		    row_prog_cobro_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		    row_prog_cobro_fecha_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		    row_prog_cobro_fecha_ven_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		}		
        return row;
    }
}

