package actividad_diaria;

import java.util.ArrayList;
import java.util.List;

import com.italo_view.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventoRowArrayAdapter extends ArrayAdapter<AC_Evento> {
		private Context context;
		private List<AC_Evento> visitaRows = new ArrayList<AC_Evento>();
		private int layoutId;

		public EventoRowArrayAdapter(Context context, int textViewResourceId,List<AC_Evento> objects) {
			super(context, textViewResourceId, objects);
			this.context = context;
			this.visitaRows = objects;
			this.layoutId = textViewResourceId;
			notifyDataSetChanged();
		}

		public int getCount() {
			return this.visitaRows.size();
		}

		public AC_Evento getItem(int index) {
			return this.visitaRows.get(index);
		}

		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(layoutId ,parent, false);
			}
			
			final AC_Evento v = getItem(position);
			final TextView evento_input = (TextView) row.findViewById(R.id.evento_input);
			final TextView hora_ini_input = (TextView) row.findViewById(R.id.hora_ini_input);
			final TextView hora_fin_input = (TextView) row.findViewById(R.id.hora_fin_input);
			final TextView duracion_min_input = (TextView) row.findViewById(R.id.duracion_min_input);
			final TextView observaciones_input = (TextView) row.findViewById(R.id.observaciones_input);
			
			evento_input.setText(v.getEvento());
			
			if(v.getBandera() == null){
				evento_input.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
			}else if(v.getBandera().equalsIgnoreCase("01")){
				evento_input.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
			}else if(v.getBandera().equalsIgnoreCase("02")){
				evento_input.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getApplicationContext().getResources().getDrawableForDensity(R.drawable.no_gestion,DisplayMetrics.DENSITY_LOW),null , null, null);
			}else if(v.getBandera().equalsIgnoreCase("03")){
				evento_input.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getApplicationContext().getResources().getDrawableForDensity(R.drawable.gestion,DisplayMetrics.DENSITY_LOW),null, null, null);
			}else if(v.getBandera().equalsIgnoreCase("04")){
				evento_input.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getApplicationContext().getResources().getDrawableForDensity(R.drawable.no_visita,DisplayMetrics.DENSITY_LOW),null, null, null);
			}else if(v.getBandera().equalsIgnoreCase("05")){
				evento_input.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getApplicationContext().getResources().getDrawableForDensity(R.drawable.bandera_anaranjada,DisplayMetrics.DENSITY_LOW),null, null, null);
			}else{
				evento_input.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
			}	
			
			evento_input.setText(v.getEvento());
			hora_ini_input.setText(v.getHoraIni());
			hora_fin_input.setText(v.getHoraFin());
			duracion_min_input.setText(v.getDuracion());
			
			if(v.getObs()==null || v.getObs().equalsIgnoreCase("") || v.getObs().equalsIgnoreCase("null")){
				observaciones_input.setText("");
			}else{
				observaciones_input.setText(v.getObs());
			}

			if (position % 2 == 0) {
				evento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
				hora_ini_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
				hora_fin_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
				duracion_min_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
				observaciones_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			} else {
				evento_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
				hora_ini_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
				hora_fin_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
				duracion_min_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
				observaciones_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			}				
			return row;
		}
}
