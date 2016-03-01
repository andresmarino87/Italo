package asistente;

import java.util.ArrayList;

import utilidades.GraphViewData;
import bd_utilidades.ItaloDBAdapter;

import com.italo_view.R;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.ValueDependentColor;

import android.os.Bundle;
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

public class Asistente_Presupuesto_Ventas extends Activity {
	private ListView listPresupuestoVenta;
	private Spinner sector_input;
	private Spinner periodo_input;
	private LinearLayout chart;
	private ItaloDBAdapter DBAdapter;
	private Cursor cursor;
	private Cursor cursor_search;
	private GraphViewData[] data;
	private String[] horizontal;
	private ArrayList<Asistente_Presupuesto_Ventas_Item> ventas;
	private AsistentePresupuestoVentasArrayAdapter adapter;
	private GraphView graphView;
	private GraphViewSeries chartData;
	private GraphViewSeriesStyle seriesStyle;
	static private boolean isChartInit;
	private Button buscar_button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_asistente_presupuesto_ventas);
		init();
		loadList();
		loadSectores();
		loadPeriodos();

		graphView = new BarGraphView(this,getString(R.string.facturacion));
		seriesStyle = new GraphViewSeriesStyle();
		seriesStyle.setValueDependentColor(new ValueDependentColor() {
			@Override
			public int get(GraphViewDataInterface data) {
				if(data.getX() % 2 == 0){
					return Color.BLUE;
				}else{
					return Color.RED;
				}
			}
		});
		
        graphView.getGraphViewStyle().setTextSize(10);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLACK);
		chartData = new GraphViewSeries(getString(R.string.facturacion), seriesStyle, data);
        graphView.addSeries(chartData);
        graphView.setHorizontalLabels(horizontal);
        graphView.setViewPort(0, 5);
        if(data!=null){
        	isChartInit=true;
        	chart.addView(graphView);
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
		listPresupuestoVenta = (ListView) findViewById(R.id.listPresupuestoVenta);
        chart = (LinearLayout) findViewById(R.id.chart);
        buscar_button=(Button) findViewById(R.id.buscar_button);
		sector_input=(Spinner)findViewById(R.id.sector_input);
		periodo_input=(Spinner)findViewById(R.id.periodo_input);
        ventas=new ArrayList<Asistente_Presupuesto_Ventas_Item>();
        DBAdapter= new ItaloDBAdapter(this); 
        isChartInit=false;
		return;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.asistente_presupuesto_ventas, menu);
		return true;
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

	private void loadSectores(){
		cursor_search=DBAdapter.getAsistentePresupuestoVentasSectores();
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
		cursor_search=DBAdapter.getAsistentePresupuestoVentasPeriodos();
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
	
	private void loadList(){
		cursor=DBAdapter.getAsistentePresupuestoVentas();
		loadDataList();
		adapter=new AsistentePresupuestoVentasArrayAdapter(getApplicationContext(), R.layout.item_presupuesto_venta, ventas);
		listPresupuestoVenta.setAdapter(adapter);
		return;
	}

	private void ReloadList(String sector_id, String periodo){
		ventas.clear();
		cursor=DBAdapter.getAsistentePresupuestoVentas(sector_id,periodo);
		loadDataList();
		adapter.notifyDataSetChanged();
		graphView.removeAllSeries();
		chartData = new GraphViewSeries(getString(R.string.facturacion), seriesStyle,data);
		graphView.addSeries(chartData);
        graphView.setHorizontalLabels(horizontal);

        if(!isChartInit){
        	chart.addView(graphView);
        	isChartInit=true;
        }
		graphView.redrawAll();

		return;
	}

	private void loadDataList(){
		int i=0;
		int j=0;
		double tPresupuesto=0;
		double tVenta=0;
		double tPorcentaje=0;
		if(cursor.moveToFirst()){
			data=new GraphViewData[cursor.getCount()*2];
			horizontal=new String[cursor.getCount()*2];
		//	vertical=new String[cursor.getCount()];	
			do{
				ventas.add(new Asistente_Presupuesto_Ventas_Item(
						cursor.getString(0),
						cursor.getDouble(1),
						cursor.getDouble(2),
						cursor.getDouble(3)));
				
				tPresupuesto+=cursor.getDouble(1);
				tVenta+=cursor.getDouble(2);
				tPorcentaje+=cursor.getDouble(3);
				data[i]=new GraphViewData((i), cursor.getDouble(1));
				data[i+1]=new GraphViewData((i+1), cursor.getDouble(2));
				horizontal[j]=cursor.getString(0);
				horizontal[j+1]="";
				i=i+2;
				j=j+2;
//				j++;
			}while(cursor.moveToNext());
			ventas.add(new Asistente_Presupuesto_Ventas_Item(
						getString(R.string.todos),
						tPresupuesto,
						tVenta,
						(tPorcentaje/cursor.getCount())));
		}
//		input_registros.setText(Integer.toString(cursor.getCount()));
		return;
	}


}
