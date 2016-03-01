package cobros_programados;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.italo_view.R;

public class CobrosProgramadosArrayAdapter extends ArrayAdapter<Cobros_Programados_Item> {
    private Context context;
    private String cliente_id;
    private List<Cobros_Programados_Item> CobrosProgramados = new ArrayList<Cobros_Programados_Item>();
	private int layoutId;
	
    public CobrosProgramadosArrayAdapter(Context context, int textViewResourceId,List<Cobros_Programados_Item> objects,String cliente_id) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.CobrosProgramados = objects;
        this.layoutId = textViewResourceId;
        this.cliente_id=cliente_id;
		notifyDataSetChanged();
    }

	public int getCount() {
        return this.CobrosProgramados.size();
    }

    public Cobros_Programados_Item getItem(int index) {
        return this.CobrosProgramados.get(index);
    }

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	row = inflater.inflate(layoutId, parent, false);
        }
        
        final Cobros_Programados_Item c = getItem(position);
        
	    final TextView row_image_input = (TextView) row.findViewById(R.id.row_image_input);
	    final TextView row_tipo_doc_input = (TextView) row.findViewById(R.id.row_tipo_doc_input);
	    final TextView row_documento_input = (TextView) row.findViewById(R.id.row_documento_input);
	    final TextView row_cliente_input = (TextView) row.findViewById(R.id.row_cliente_input);
	    final TextView row_valor_input = (TextView) row.findViewById(R.id.row_valor_input);
	    final TextView row_fecha_input = (TextView) row.findViewById(R.id.row_fecha_input);

        row_tipo_doc_input.setText(c.getTipoDocumento());
        row_documento_input.setText(c.getNumeroDocumento());
        row_cliente_input.setText(c.getCodigoCliente() + " " + c.getNombreCliente());
        row_valor_input.setText(c.getValorDocumento());
        row_fecha_input.setText(c.getFechaProgramacion());
        

        if(cliente_id!=null){
        	row_cliente_input.setVisibility(LinearLayout.GONE);
        }
        
        
        if (position % 2 == 0) {
			row_image_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_tipo_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_documento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_cliente_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_fecha_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		} else {
			row_image_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_tipo_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_documento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_cliente_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_fecha_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		}				
        return row;
    }
}

