package actividad_diaria;

import java.util.ArrayList;
import java.util.List;

import utilidades.Utility;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.italo_view.R;

public class RutaRowArrayAdapter extends ArrayAdapter<RutaRow> {
	private Context context;
	private List<RutaRow> rutaRows = new ArrayList<RutaRow>();

	public RutaRowArrayAdapter(Context context, int textViewResourceId,List<RutaRow> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.rutaRows = objects;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.rutaRows.size();
	}

	public RutaRow getItem(int index) {
		return this.rutaRows.get(index);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.item_actividad_consultar,parent, false);
		}
		final RutaRow c = getItem(position);
		final TextView evento_row_input = (TextView) row.findViewById(R.id.evento_row_input);
		final TextView ruta_row_input = (TextView) row.findViewById(R.id.ruta_row_input);
		final TextView cuenta_row_input = (TextView) row.findViewById(R.id.cuenta_row_input);
		final TextView tejido_row_input = (TextView) row.findViewById(R.id.tejido_row_input);
		final TextView cliente_row_input = (TextView) row.findViewById(R.id.cliente_row_input);
		final TextView venta_proyectada_row_input = (TextView) row.findViewById(R.id.venta_proyectada_row_input);
		final TextView venta_real_row_input = (TextView) row.findViewById(R.id.venta_real_row_input);
		final TextView cobro_proyectado_row_input = (TextView) row.findViewById(R.id.cobro_proyectado_row_input);
		final TextView cobro_real_row_input = (TextView) row.findViewById(R.id.cobro_real_row_input);
		final TextView cartera_vencida_row_input = (TextView) row.findViewById(R.id.cartera_vencida_row_input);
		final TextView n_visitas_row_input = (TextView) row.findViewById(R.id.n_visitas_row_input);

		if (c.getEvento().equalsIgnoreCase("01")) {
			evento_row_input.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null, null, null);
		} else if (c.getEvento().equalsIgnoreCase("02")) {
			evento_row_input.setCompoundDrawablesRelativeWithIntrinsicBounds(null,context.getResources().getDrawableForDensity(R.drawable.no_gestion,DisplayMetrics.DENSITY_DEFAULT),null, null);
		} else if (c.getEvento().equalsIgnoreCase("03")) {
			evento_row_input.setCompoundDrawablesRelativeWithIntrinsicBounds(null,context.getResources().getDrawableForDensity(R.drawable.gestion,DisplayMetrics.DENSITY_DEFAULT),null, null);
		} else if (c.getEvento().equalsIgnoreCase("04")) {
			evento_row_input.setCompoundDrawablesRelativeWithIntrinsicBounds(null,context.getResources().getDrawableForDensity(R.drawable.no_visita,DisplayMetrics.DENSITY_DEFAULT),null, null);
		} else if (c.getEvento().equalsIgnoreCase("05")) {
			evento_row_input.setCompoundDrawablesRelativeWithIntrinsicBounds(null,context.getResources().getDrawableForDensity(R.drawable.bandera_anaranjada,DisplayMetrics.DENSITY_DEFAULT),null, null);
		} else {
			evento_row_input.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null, null, null);
		}

		ruta_row_input.setText(c.getRuta());
		tejido_row_input.setText(c.getTejido());
		cuenta_row_input.setText(c.getClienteId());
		cliente_row_input.setText(c.getClienteNombre());
		venta_proyectada_row_input.setText(Utility.formatNumber(c.getVenta_proyectada()));
		venta_real_row_input.setText(Utility.formatNumber(c.getVenta_real()));
		cobro_proyectado_row_input.setText(Utility.formatNumber(c.getCobro_proyectado()));
		cobro_real_row_input.setText(Utility.formatNumber(c.getCobro_real()));
		if (c.getCartera_vencida() != null) {
			cartera_vencida_row_input.setText(c.getCartera_vencida());
		} else {
			cartera_vencida_row_input.setText("");
		}

		n_visitas_row_input.setText(c.getNVisitas());

		if (c.getExtraRuta().equalsIgnoreCase("N")) {
			if (position % 2 == 0) {
				ruta_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
				evento_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
				cuenta_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
				tejido_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
				venta_proyectada_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
				cliente_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
				cobro_proyectado_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
				venta_real_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
				cartera_vencida_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
				cobro_real_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
				n_visitas_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
			} else {
				ruta_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
				evento_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
				cuenta_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
				tejido_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
				venta_proyectada_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
				cliente_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
				cobro_proyectado_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
				venta_real_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
				cartera_vencida_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
				cobro_real_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
				n_visitas_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_impar));
			}
		} else {
			if (c.getTipoExtra().equalsIgnoreCase("E")) {
				ruta_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_moradas));
				evento_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_moradas));
				cuenta_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_moradas));
				tejido_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_moradas));
				venta_proyectada_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_moradas));
				cliente_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_moradas));
				cobro_proyectado_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_moradas));
				venta_real_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_moradas));
				cartera_vencida_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_moradas));
				cobro_real_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_moradas));
				n_visitas_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_moradas));
			} else {
				ruta_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_verdes));
				evento_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_verdes));
				cuenta_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_verdes));
				tejido_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_verdes));
				venta_proyectada_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_verdes));
				cliente_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_verdes));
				cobro_proyectado_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_verdes));
				venta_real_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_verdes));
				cartera_vencida_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_verdes));
				cobro_real_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_verdes));
				n_visitas_row_input.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_verdes));
			}
		}
		return row;
	}
}