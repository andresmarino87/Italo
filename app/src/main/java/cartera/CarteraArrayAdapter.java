package cartera;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.italo_view.R;

public class CarteraArrayAdapter extends ArrayAdapter<Cartera_item> {
    private Context context;
    private String cliente_id;
    private List<Cartera_item> carteras = new ArrayList<Cartera_item>();
	private int demora=0;
    private int layoutId;
	
    public CarteraArrayAdapter(Context context, int textViewResourceId,List<Cartera_item> objects,String cliente_id) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.carteras = objects;
        this.layoutId = textViewResourceId;
        this.cliente_id=cliente_id;
		notifyDataSetChanged();
    }

	public int getCount() {
        return this.carteras.size();
    }

    public Cartera_item getItem(int index) {
        return this.carteras.get(index);
    }

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	row = inflater.inflate(layoutId, parent, false);
        }
        
        final Cartera_item c = getItem(position);
        
	    final TextView row_image_input = (TextView) row.findViewById(R.id.row_image_input);
	    final TextView row_tipo_doc_input = (TextView) row.findViewById(R.id.row_tipo_doc_input);
	    final TextView row_documento_input = (TextView) row.findViewById(R.id.row_documento_input);
	    final TextView row_cliente_input = (TextView) row.findViewById(R.id.row_cliente_input);
	    final TextView row_valor_input = (TextView) row.findViewById(R.id.row_valor_input);
	    final TextView row_fecha_input = (TextView) row.findViewById(R.id.row_fecha_input);
	    final TextView row_vencimiento_input = (TextView) row.findViewById(R.id.row_vencimiento_input);
	    final TextView row_dias_input = (TextView) row.findViewById(R.id.row_dias_input);

        row_tipo_doc_input.setText(c.getTipo_Documento());
        row_documento_input.setText(c.getReferencia());
        row_cliente_input.setText(c.getCodigo_cliente()+" "+c.getNombre_cliente());
        row_valor_input.setText(c.getSaldo());
        row_fecha_input.setText(c.getFecha_Doc());
        row_vencimiento_input.setText(c.getFecha_Venc());
        row_dias_input.setText(c.getDemora());

        if(cliente_id!=null){
        	row_cliente_input.setVisibility(LinearLayout.GONE);
        }
        
        demora=Integer.parseInt(c.getDemora());
        if(demora < 0){
        	row_image_input.setCompoundDrawablesRelativeWithIntrinsicBounds(null, context.getResources().getDrawableForDensity(R.drawable.boton_verde,DisplayMetrics.DENSITY_MEDIUM), null, null);
        }else if(demora==0){
        	row_image_input.setCompoundDrawablesRelativeWithIntrinsicBounds(null, context.getResources().getDrawableForDensity(R.drawable.boton_amarillo,DisplayMetrics.DENSITY_MEDIUM), null, null);
        }else{
        	row_image_input.setCompoundDrawablesRelativeWithIntrinsicBounds(null, context.getResources().getDrawableForDensity(R.drawable.boton_rojo,DisplayMetrics.DENSITY_MEDIUM), null, null);
        }
        
		if (position % 2 == 0) {
			row_image_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_tipo_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_documento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_cliente_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_fecha_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_vencimiento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_dias_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		} else {
			row_image_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_tipo_doc_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_documento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_cliente_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_valor_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_fecha_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_vencimiento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_dias_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		}				
        return row;
    }
}

