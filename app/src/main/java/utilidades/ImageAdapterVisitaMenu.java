package utilidades;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ImageAdapterVisitaMenu extends BaseAdapter {
    private Context mContext;
	 
    // Keep all Images in array
    public Integer[] mThumbIds = {
		com.italo_view.R.drawable.clientes,
		com.italo_view.R.drawable.inventario,
		com.italo_view.R.drawable.consignaciones,
		com.italo_view.R.drawable.cartera,
		com.italo_view.R.drawable.indicadores,
		com.italo_view.R.drawable.asistente,	
		com.italo_view.R.drawable.pareto,
		com.italo_view.R.drawable.pedidos_negados,
	};

    public Integer[] strings = {
		com.italo_view.R.string.clientes,
		com.italo_view.R.string.articulos,
		com.italo_view.R.string.consignaciones,
		com.italo_view.R.string.cartera,
		com.italo_view.R.string.indicadores_del_dia,
		com.italo_view.R.string.asistente,	
		com.italo_view.R.string.paretos,
		com.italo_view.R.string.pedidos_negados,
    };

    // Constructor
    public ImageAdapterVisitaMenu(Context c){
        mContext = c;
    }
 
    @Override
    public int getCount() {
        return mThumbIds.length;
    }
	 
    @Override
    public Object getItem(int position) {
        return mThumbIds[position];
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
	 
    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	TextView imageView=new TextView(mContext);
    	imageView.setText(strings[position]);
    	imageView.setTextSize(18);
    	imageView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, mContext.getApplicationContext().getResources().getDrawableForDensity(mThumbIds[position],DisplayMetrics.DENSITY_XXXHIGH), null, null);
    	imageView.setTypeface(null, Typeface.BOLD);
    	imageView.setGravity(Gravity.BOTTOM | Gravity.CENTER );
    	return imageView;
    }
}