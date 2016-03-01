package com.italo_view;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

public class EasyInstallActivity extends Activity {
	TextView textView_titulo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set the View layer
		// setContentView(R.layout.acerca_de);
		
		Log.i("info", "ejecutar actividad de easyinstall");

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(getResources().getString(R.string.messageInstalacion));
		alert.setIcon(getResources().getDrawable(R.drawable.easy_sales_2));
		alert.setMessage(getResources().getString(
				R.string.actualizacionEasySales));
		alert.setPositiveButton(getResources().getString(R.string.si),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						setResult(RESULT_OK);
						Global gGlobal = ((Global) getApplicationContext());
						gGlobal.ActualizacionDescargada = false;
						InstalarAplicacion();
						finish();

					}
				});

		alert.setNegativeButton(getResources().getString(R.string.no),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
						return;
					}
				});
		alert.create().show();
	}

	private void InstalarAplicacion() {
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/download", "Italo.apk");

		Intent intent = new Intent(Intent.ACTION_VIEW);

		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");

		startActivity(intent);
	}

}
