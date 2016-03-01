package registro_eventos;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.italo_view.R;

public class EventosArrayAdapter extends ArrayAdapter<Evento> {
	private Context context;
	private List<Evento> eventos = new ArrayList<Evento>();
    private TextView row_evento_input;
    private TextView row_fecha_input;
    private TextView row_hora_input;
    private int layoutId;
    
    public EventosArrayAdapter(Context context, int textViewResourceId,List<Evento> objects) {
        super(context, textViewResourceId, objects);
        this.context=context;
        this.eventos = objects;
        this.layoutId=textViewResourceId;
		notifyDataSetChanged();
    }

	public int getCount() {
        return this.eventos.size();
    }

    public Evento getItem(int index) {
        return this.eventos.get(index);
    }

	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
        	LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutId, parent, false);
        }

        Evento e = getItem(position);
        row_evento_input = (TextView) row.findViewById(R.id.row_evento_input);
        row_fecha_input = (TextView) row.findViewById(R.id.row_fecha_input);
        row_hora_input = (TextView) row.findViewById(R.id.row_hora_input);
        
	    row_evento_input.setText(e.getEvento());
	    row_fecha_input.setText(e.getFecha());
	    row_hora_input.setText(e.getHora());

        if(position % 2 == 0){
        	row_evento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_fecha_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
        	row_hora_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		}else{
			row_evento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_fecha_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_hora_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		}
        return row;
    }
}