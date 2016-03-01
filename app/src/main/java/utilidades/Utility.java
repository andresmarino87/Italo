package utilidades;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.italo_view.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class Utility {
/*	public InputFilter filter = new InputFilter() { 
		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			for (int i = start; i < end; i++) { 
				if (!Character.isLetterOrDigit(source.charAt(i))) { 
					return ""; 
				}
			} 
			return null;
        }
	};*/

	
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}
	 
	 public static String formatNumber(double val){
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(new Locale( "es" , "ES"));
		otherSymbols.setDecimalSeparator(',');
		otherSymbols.setGroupingSeparator('.'); 
		DecimalFormat df;
		df = new DecimalFormat("###,###,##0.00", otherSymbols);
		return df.format(val);
	 }

	 public static void showMessage(Context context,String message){
		 final String OK="Ok";
		 Builder dialogBuilder = new AlertDialog.Builder(context);
		 dialogBuilder.setIcon(context.getResources().getDrawable(R.drawable.alerta));
		 dialogBuilder.setCancelable(false);
		 dialogBuilder.setTitle(R.string.alerta);
		 dialogBuilder.setMessage(message);
		 dialogBuilder.setPositiveButton(OK, new DialogInterface.OnClickListener() {
			 @Override
			 public void onClick(DialogInterface dialog, int which) {}
		 });
		 dialogBuilder.create().show();
		 return;
	 }

	 public static void showMessage(Context context,int message){
		 final String OK="Ok";
		 Builder dialogBuilder = new AlertDialog.Builder(context);
		 dialogBuilder.setIcon(context.getResources().getDrawable(R.drawable.alerta));
		 dialogBuilder.setCancelable(false);
		 dialogBuilder.setTitle(R.string.alerta);
		 dialogBuilder.setMessage(message);
		 dialogBuilder.setPositiveButton(OK, new DialogInterface.OnClickListener() {
			 @Override
			 public void onClick(DialogInterface dialog, int which) {}
		 });
		 dialogBuilder.create().show();
		 return;
	 }

	 
	 public static void exportDB(Context context){
		 String SAMPLE_DB_NAME="bd_italo.s3db";
		 File sd = Environment.getExternalStorageDirectory();
		 File data = Environment.getDataDirectory();
		 FileChannel source=null;
		 FileChannel destination=null;
		 String currentDBPath = "/data/"+ "com.italo_view" +"/databases/"+SAMPLE_DB_NAME;
		 String backupDBPath = Math.round((Math.random()*1000))+SAMPLE_DB_NAME;
		 File currentDB = new File(data, currentDBPath);
		 File backupDB = new File(sd, backupDBPath);
		 try {
			 source = new FileInputStream(currentDB).getChannel();
			 destination = new FileOutputStream(backupDB).getChannel();
			 destination.transferFrom(source, 0, source.size());
			 source.close();
			 destination.close();
			 Toast.makeText(context, "DB Exported!", Toast.LENGTH_LONG).show();
		} catch(IOException e) {
			Log.e("info",e+"");
	 	}
	}
	
	public static String dateToSqliteFormat(String date){
		String res;
		try{
			String [] aux=date.split("/");
			res=aux[2]+"-"+aux[0]+"-"+aux[1];
		}catch(Exception e){
			res ="1900-01-01";
		}
		return res;
	}
}
