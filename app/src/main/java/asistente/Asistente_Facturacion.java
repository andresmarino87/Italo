package asistente;

import java.util.ArrayList;

import utilidades.GraphViewData;
import utilidades.Utility;

import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class Asistente_Facturacion extends Activity {
	private ListView listFacturacion;
	private Spinner sector_input;
	private Spinner periodo_input;
	private TextView promedio_input;
	private TextView dia_maximo_input;
	private TextView dia_minimo_input;
	private LinearLayout chart;
	private ItaloDBAdapter DBAdapter;
	private Cursor cursor;
	private Cursor cursor_search;
	private GraphViewData[] data;
	private String[] horizontal;
	private String[] vertical;
	private ArrayList<Asistente_Facturacion_Item> facturacion;
	private AsistenteFacturacionArrayAdapter adapter;
	private GraphView graphView;
	private GraphViewSeries chartData;
	private Button buscar_button;
	static private boolean isChartInit;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistente_facturacion);
        init();
        loadList();
        loadSectores();
        loadPeriodos();

		chartData = new GraphViewSeries(data);
        graphView = new LineGraphView(this,getString(R.string.facturacion));
        graphView.addSeries(chartData);
        graphView.getGraphViewStyle().setTextSize(10);
        graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);
        graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLACK);
        graphView.getGraphViewStyle().setNumHorizontalLabels(5);
        
        graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
			@SuppressLint("DefaultLocale")
			@Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return horizontal[(int) value];
                } else
                    return String.format("%.2f", value);
            }
        });
        if(facturacion.size()<5){
            graphView.setViewPort(0, facturacion.size()-1);
        }else{
            graphView.setViewPort(0, 5);
        }
//        graphView.setViewPort(0, 5);
        graphView.setScrollable(true);
        if(data!=null){
        	chart.addView(graphView);
        	isChartInit=true;
        }
        buscar_button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(periodo_input.getSelectedItem()!=null && sector_input.getSelectedItem()!=null){
					ReloadList(sector_input.getSelectedItem().toString(),periodo_input.getSelectedItem().toString());						
				}
			}
		});
    }
    
    private void init(){
        listFacturacion = (ListView) findViewById(R.id.listFacturacion);
        promedio_input = (TextView) findViewById(R.id.promedio_input);
        dia_maximo_input = (TextView) findViewById(R.id.dia_maximo_input);
        dia_minimo_input = (TextView) findViewById(R.id.dia_minimo_input);
        buscar_button = (Button) findViewById(R.id.buscar_button);
		sector_input=(Spinner)findViewById(R.id.sector_input);
		periodo_input=(Spinner)findViewById(R.id.periodo_input);
        isChartInit=false;
        chart = (LinearLayout) findViewById(R.id.chart);
        facturacion=new ArrayList<Asistente_Facturacion_Item>();
        DBAdapter= new ItaloDBAdapter(this); 
        return;
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.asistente_facturacion, menu);
		return true;
	}
	

	private void loadSectores(){
		cursor_search=DBAdapter.getAsistentePresupuestoCarteraSectoreS();
		int i=0;
        String strings[] = new String[cursor_search.getCount()];
		if(cursor_search.moveToFirst()){
			do{
	        	strings[i] = cursor_search.getString(0);
	        	i++;
			}while(cursor_search.moveToNext());
			ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sector_input.setAdapter(adapter);
		}
		return;
	}
	
	private void loadPeriodos(){
		cursor_search=DBAdapter.getAsistentePresupuestoCarteraPeriodos();
		int i=0;
        String strings[] = new String[cursor_search.getCount()];
		if(cursor_search.moveToFirst()){
			do{
	        	strings[i] = cursor_search.getString(0);
	        	i++;
			}while(cursor_search.moveToNext());
		}
		ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		periodo_input.setAdapter(adapter);
		return;
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
	        case R.id.atras:
				finish();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}
	
	private void loadList(){
		cursor=DBAdapter.getAsistenteFacturacion();
		loadDataList();
		adapter=new AsistenteFacturacionArrayAdapter(getApplicationContext(), R.layout.item_asistente_facturacion, facturacion);
		listFacturacion.setAdapter(adapter);
		return;
	}

	private void ReloadList(String sector_id, String periodo){
		facturacion.clear();
		cursor=DBAdapter.getAsistenteFacturacion(sector_id,periodo);
		loadDataList();
		adapter.notifyDataSetChanged();
		graphView.removeAllSeries();
		chartData = new GraphViewSeries(data);
		graphView.addSeries(chartData);
        if(!isChartInit){
        	chart.addView(graphView);
        	isChartInit=true;
        }
		graphView.redrawAll();
		return;
	}

	private void loadDataList(){
		int i=0;
		double max=0;
		double min=1000000000;
		double prom=0;
		if(cursor.moveToFirst()){
			data=new GraphViewData[cursor.getCount()];
			horizontal=new String[cursor.getCount()];
			vertical=new String[cursor.getCount()];	
			do{
				facturacion.add(new Asistente_Facturacion_Item(
						cursor.getString(0),
						cursor.getDouble(1)));
				data[i]=new GraphViewData((i), cursor.getDouble(1));
				horizontal[i]=cursor.getString(0);
				vertical[i]=Utility.formatNumber(cursor.getDouble(1));
				i++;
				max=(max<cursor.getDouble(1))?cursor.getDouble(1):max;
				min=(min>cursor.getDouble(1))?cursor.getDouble(1):min;
				prom+=cursor.getDouble(1);
			}while(cursor.moveToNext());
			prom=prom/cursor.getCount();
		}else{
//			Utility.showMessage(context, message);
		}
		promedio_input.setText(Utility.formatNumber(prom));
		dia_maximo_input.setText(Utility.formatNumber(max));
		dia_minimo_input.setText(Utility.formatNumber(min));
		return;
	}

	
	@Override
	public void onBackPressed() {}
}
