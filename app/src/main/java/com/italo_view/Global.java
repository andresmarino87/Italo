package com.italo_view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import utilidades.EasyUtilidades;

import easysync.RequerimientoSincronismo;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

@SuppressWarnings("unused")
public class Global extends Application {

	public static boolean ActualizacionDescargada = false;
	
	// uncaught exception handler variable
	private UncaughtExceptionHandler defaultUEH;

	// handler listener
	private String localPath = Environment.getExternalStorageDirectory()
			.toString() + "/download";

	private Thread.UncaughtExceptionHandler _unCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			try {
				final Writer result = new StringWriter();
				final PrintWriter printWriter = new PrintWriter(result);
				ex.printStackTrace(printWriter);
				String stacktrace = result.toString();

				// Log.i("info", "excepcion-1");
				// StringBuilder report = new StringBuilder();
				ArrayList<String> report = new ArrayList<String>();
				Date curDate = new Date();
				// Log.i("info", "excepcion-2");
				report.add("Error Report collected on : ");
				report.add(curDate.toString());
				report.add("");
				report.add("");
				// Log.i("info", "excepcion-3");
				report.add("Informations :");
				// Log.i("info", "excepcion-4-" + report);
				addInformation(report);
				// Log.i("info", "excepcion-5");
				report.add("");
				report.add("Stack: ");
				report.add(result.toString());
				printWriter.close();
				report.add("");
				report.add("**** End of current Report ***");

				// printWriter.close();

				// Log.i("info", "excepcion-6");
				String filename = new Date().getTime() + ".stacktrace";

				File file = new File(localPath);
				if (!file.exists())
					try {
						file.createNewFile();
					} catch (IOException e1) {

					}

				// writeToFile(stacktrace, filename);
				writeToFile(report, filename);

				// System.exit(0);

				Log.i("info", "lanzar sss");

				/*
				int j = arrActividadesRunning.size() - 1;
				for (int i = j; i >= 0; --i) {
					try {
						Log.i("info", "cerrar "
								+ arrActividadesRunning.get(i)
										.getTitle());
						arrActividadesRunning.get(i).finish();
						arrActividadesRunning.remove(i);
					} catch (Exception ex1) {
						arrActividadesRunning.remove(i);
						Log.i("info", "problemas cerrar "
								+ arrActividadesRunning.get(i)
										.getTitle());
					}
				}
				*/

				// Intent newIntent = new Intent(this,
				// EasySalesActivity.class);
				// newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(newIntent);

				defaultUEH.uncaughtException(thread, ex);

				// Activity _activity = arrActividades.get(0);
				// arrActividades.get(0).finish();

				// System.exit(0);

			} catch (Exception ex1) {

			}

		}
	};

	public void writeToFile(ArrayList<String> stacktrace, String filename) {
		try {
			// BufferedWriter bos = new BufferedWriter(new FileWriter(localPath
			// + "/" + filename));
			FileOutputStream bos = new FileOutputStream(localPath + "/"
					+ filename, true);

			for (int i = 0; i < stacktrace.size(); ++i) {
				// Log.i("info", "nueva linea");
				bos.write(stacktrace.get(i).getBytes());
				bos.write("\n".getBytes());
			}

			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeToFile(String stacktrace, String filename) {
		try {
			BufferedWriter bos = new BufferedWriter(new FileWriter(localPath
					+ "/" + filename));
			bos.write(stacktrace);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Global() {

		defaultUEH = Thread.getDefaultUncaughtExceptionHandler();

		// setup handler for uncaught exception
		Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);
	}

	private StatFs getStatFs() {

		File path = Environment.getDataDirectory();
		return new StatFs(path.getPath());

	}

	private long getAvailableInternalMemorySize(StatFs stat) {

		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;

	}

	private long getTotalInternalMemorySize(StatFs stat) {

		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;

	}

	private void addInformation(StringBuilder message) {

		try {
			message.append("Locale: ").append(Locale.getDefault()).append('\n');

			PackageManager pm = this.getPackageManager();
			PackageInfo pi;
			pi = pm.getPackageInfo(this.getPackageName(), 0);
			message.append("Version: ").append(pi.versionName).append('\n');
			message.append("Versionapp: " + GlobaG.Version + "." + GlobaG.Revision + "."
					+ GlobaG.Compilacion);			
			message.append("Package: ").append(pi.packageName).append('\n');

		} catch (Exception e) {
			// Log.e("CustomExceptionHandler", "Error", e);
			message.append("Could not get Version information for ").append(
					this.getPackageName());
			// Log.i("info", "yyy-" + message);
		}

		try {
			message.append("Phone Model: ").append(android.os.Build.MODEL)
					.append('\n');

			message.append("Android Version: ")
					.append(android.os.Build.VERSION.RELEASE).append('\n');

			message.append("Board: ").append(android.os.Build.BOARD)
					.append('\n');
			message.append("Brand: ").append(android.os.Build.BRAND)
					.append('\n');
			message.append("Device: ").append(android.os.Build.DEVICE)
					.append('\n');
			message.append("Host: ").append(android.os.Build.HOST).append('\n');
			message.append("ID: ").append(android.os.Build.ID).append('\n');
			message.append("Model: ").append(android.os.Build.MODEL)
					.append('\n');
			message.append("Product: ").append(android.os.Build.PRODUCT)
					.append('\n');

			message.append("Type: ").append(android.os.Build.TYPE).append('\n');
			StatFs stat = getStatFs();
			message.append("Total Internal memory: ")
					.append(getTotalInternalMemorySize(stat)).append('\n');
			message.append("Available Internal memory: ")
					.append(getAvailableInternalMemorySize(stat)).append('\n');
		} catch (Exception e) {
			// Log.e("CustomExceptionHandler", "Error", e);
			// Log.i("info", "xxx-" + message);
			message.append("Could not get Version information for ").append(
					this.getPackageName());
		}

	}

	private void addInformation(ArrayList<String> message) {

		try {
			message.add("Locale: " + Locale.getDefault());

			PackageManager pm = this.getPackageManager();
			PackageInfo pi;
			pi = pm.getPackageInfo(this.getPackageName(), 0);
			message.add("Version: " + pi.versionName);
			message.add("Versionapp: " + GlobaG.Version + "." + GlobaG.Revision + "."
					+ GlobaG.Compilacion);			
			message.add("Package: " + pi.packageName);

		} catch (Exception e) {
			// Log.e("CustomExceptionHandler", "Error", e);
			message.add("Could not get Version information for "
					+ this.getPackageName());
			// Log.i("info", "yyy-" + message);
		}

		try {
			message.add("Phone Model: " + android.os.Build.MODEL);

			message.add("Android Version: " + android.os.Build.VERSION.RELEASE);

			message.add("Board: " + android.os.Build.BOARD);
			message.add("Brand: " + android.os.Build.BRAND);
			message.add("Device: " + android.os.Build.DEVICE);
			message.add("Host: " + android.os.Build.HOST);
			message.add("ID: " + android.os.Build.ID);
			message.add("Model: " + android.os.Build.MODEL);
			message.add("Product: " + android.os.Build.PRODUCT);

			message.add("Type: " + android.os.Build.TYPE);
			StatFs stat = getStatFs();
			message.add("Total Internal memory: "
					+ getTotalInternalMemorySize(stat));
			message.add("Available Internal memory: "
					+ getAvailableInternalMemorySize(stat));
			
			message.add(EasyUtilidades.logHeap());
			
		} catch (Exception e) {
			// Log.e("CustomExceptionHandler", "Error", e);
			// Log.i("info", "xxx-" + message);
			message.add("Could not get Version information for "
					+ this.getPackageName());
		}

	}


	
}
