package pedidos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import utilidades.EasyUtilidades;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.italo_view.R;

public class ProductosArrayAdapter extends ArrayAdapter<Pedidos_producto> {
	private Context context;
	private List<Pedidos_producto> productos = new ArrayList<Pedidos_producto>();
	private int layoutId;
	
	public ProductosArrayAdapter(Context context, int textViewResourceId,List<Pedidos_producto> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.productos = objects;
		this.layoutId=textViewResourceId;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.productos.size();
	}

	public Pedidos_producto getItem(int index) {
		return this.productos.get(index);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutId, parent,false);
		}

		final Pedidos_producto p = getItem(position);
		final TextView name_input = (TextView) row.findViewById(R.id.name_input);
		final ImageView image_input = (ImageView) row.findViewById(R.id.image_input);
		final LinearLayout contenedor = (LinearLayout) row.findViewById(R.id.contenedor);
		name_input.setText(p.getId().trim() + " " + p.getName().trim() + " " + p.getPrecio().trim());
		final String path = Environment.getExternalStorageDirectory().getPath() + "/Italo/" + p.getId().trim() + ".png";
		final File imgFile = new File(path);
		if (imgFile.exists()) {
			try {
				image_input.setImageBitmap(EasyUtilidades.decodeSampledBitmapFromResource(path, 100, 100));				
			} catch (Exception e) {
				image_input.setImageBitmap(EasyUtilidades.decodeSampledBitmapFromResource(context.getResources(), R.drawable.default_image, 100, 100));				
			}
		} else {
			image_input.setImageBitmap(EasyUtilidades.decodeSampledBitmapFromResource(context.getResources(), R.drawable.default_image, 100, 100));				
		}
		
		if (p.getCantidad() == null) {
			contenedor.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_par));
		} else {
			contenedor.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.celdas_seleccionada));
		}
		return row;
	}
}