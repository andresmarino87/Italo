package actividad_diaria;

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

public class PlanesArrayAdapter extends ArrayAdapter<Plan> {
	private Context context;
	private List<Plan> planes = new ArrayList<Plan>();
	private int resourceId;
	private String user_id;
	private boolean isSupervisor;
	
	public PlanesArrayAdapter(Context context, int textViewResourceId,List<Plan> objects,String user_id,boolean isSupervisor) {
		super(context, textViewResourceId, objects);
		this.context=context;
		this.planes = objects;
		this.resourceId=textViewResourceId;
		this.user_id=user_id;
		this.isSupervisor=isSupervisor;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.planes.size();
	}

	public Plan getItem(int index) {
		return this.planes.get(index);
	}

	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			// ROW INFLATION
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(resourceId, parent, false);
		}
		
		final Plan p = getItem(position);
		final TextView row_sector_input = (TextView) row.findViewById(R.id.row_sector_input);
		final TextView row_plan_input = (TextView) row.findViewById(R.id.row_plan_input);
		final TextView row_fecha_input = (TextView) row.findViewById(R.id.row_fecha_input);

		if(isSupervisor){
			if(p.getCreador()!=null && p.getCreador().equalsIgnoreCase(user_id)){
				row_sector_input.setText(p.getSectorId());
				row_sector_input.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.check),null,null,null);
			}else{
				row_sector_input.setText(p.getSectorId());
			}
		}else{
			row_sector_input.setText(p.getSectorId());
		}
		row_plan_input.setText(p.getPlan());
		row_fecha_input.setText(p.getFecha());

		if (position % 2 == 0) {
			row_sector_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_plan_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			row_fecha_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		} else {
			row_sector_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_plan_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			row_fecha_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		}
		return row;
	}
}