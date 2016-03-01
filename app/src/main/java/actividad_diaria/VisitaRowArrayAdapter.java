package actividad_diaria;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.italo_view.R;

public class VisitaRowArrayAdapter extends ArrayAdapter<AC_Visita> {
	private Context context;
	private List<AC_Visita> visitaRows = new ArrayList<AC_Visita>();
	private int layoutId;

	public VisitaRowArrayAdapter(Context context, int textViewResourceId,List<AC_Visita> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.visitaRows = objects;
		this.layoutId=textViewResourceId;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.visitaRows.size();
	}

	public AC_Visita getItem(int index) {
		return this.visitaRows.get(index);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutId,parent, false);
		}

		final AC_Visita v = getItem(position);
		final TextView visita_id_input = (TextView) row.findViewById(R.id.visita_id_input);
		final TextView cant_eventos_input = (TextView) row.findViewById(R.id.cant_eventos_input);
		final TextView hora_ini_input = (TextView) row.findViewById(R.id.hora_ini_input);
		final TextView hora_fin_input = (TextView) row.findViewById(R.id.hora_fin_input);
		final TextView duracion_min_input = (TextView) row.findViewById(R.id.duracion_min_input);
		final TextView observaciones_input = (TextView) row.findViewById(R.id.observaciones_input);
		
		visita_id_input.setText(v.getVisitaId());
		
		if(v.getBandera() == null){
			visita_id_input.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
		}else if(v.getBandera().equalsIgnoreCase("01")){
			visita_id_input.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
		}else if(v.getBandera().equalsIgnoreCase("02")){
			visita_id_input.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getApplicationContext().getResources().getDrawableForDensity(R.drawable.no_gestion,DisplayMetrics.DENSITY_LOW),null , null, null);
		}else if(v.getBandera().equalsIgnoreCase("03")){
			visita_id_input.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getApplicationContext().getResources().getDrawableForDensity(R.drawable.gestion,DisplayMetrics.DENSITY_LOW),null, null, null);
		}else if(v.getBandera().equalsIgnoreCase("04")){
			visita_id_input.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getApplicationContext().getResources().getDrawableForDensity(R.drawable.no_visita,DisplayMetrics.DENSITY_LOW),null, null, null);
		}else if(v.getBandera().equalsIgnoreCase("05")){
			visita_id_input.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getApplicationContext().getResources().getDrawableForDensity(R.drawable.bandera_anaranjada,DisplayMetrics.DENSITY_LOW),null, null, null);
		}else{
			visita_id_input.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
		}
		
		cant_eventos_input.setText(String.valueOf(v.getCantEventos()));
		hora_ini_input.setText(v.getHoraIni());
		hora_fin_input.setText(v.getHoraFin());
		duracion_min_input.setText(v.getDuracion());
		if(v.getObs()==null || v.getObs().equalsIgnoreCase("") || v.getObs().equalsIgnoreCase("null")){
			observaciones_input.setText("");
		}else{
			observaciones_input.setText(v.getObs());
		}
		if (position % 2 == 0) {
			visita_id_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			cant_eventos_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			hora_ini_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			hora_fin_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			duracion_min_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			observaciones_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		} else {
			visita_id_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			cant_eventos_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			hora_ini_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			hora_fin_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			duracion_min_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			observaciones_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
		}				
		return row;
	}
}
