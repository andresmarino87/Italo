package bd_utilidades;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import easysync.FileSales;
import easysync.Funciones;


import android.content.Context;
import android.database.Cursor;

public class EasySyncDB extends ItaloDBAdapter {

	private static final String DATABASE_TABLE_CTABLA_SIN_REGISTRO = "dbDown.esc_tabla_sin_registros";
	public static final String CTABLA_SIN_REGISTRO_NOMBRE_TABLA = "nombre_tabla";

	private static final String DATABASE_TABLE_CTABLA_SINCRONIZACION = "esc_tabla_sincronizacion";
	private static final String CTABLA_SINCRONIZACION_TABLA = "tabla";
	private static final String CTABLA_SINCRONIZACION_SENTIDO = "sentido";

	private static final String DATABASE_TABLE_CTABLA_SINCRONIZACION_DOWN = "dbDown.esc_tabla_sincronizacion";
	public static final String CTABLA_SINCRONIZACION_DOWN_TABLA = "tabla";

	public static final String DATABASE_TABLE_ESC_AUXILIAR_EASY_SERVER = "esc_auxiliar_easy_server";
	private static final String ESC_AUXILIAR_EASY_SERVER_ID = "id";
	private static final String ESC_AUXILIAR_EASY_SERVER_TIPO = "tipo";
	public static final String ESC_AUXILIAR_EASY_SERVER_TEXTO = "texto";
	private static final String ESC_AUXILIAR_EASY_SERVER_ORDEN = "orden";
	
	public static final String DATABASE_TABLE_ESC_FILE = "esc_file";
	public static final String FILE_PATH_RELATIVE = "file_path_relative";
	public static final String FILE_NAME = "file_name";
	public static final String FILE_EXTENSION = "file_extension";
	public static final String FILE_DATE = "file_date";
	public static final String FILE_SIZE = "file_size";
	public static final String PRODUCTO_ID = "producto_id";
	public static final String TIPO = "tipo";
	public static final String ID = "id";
	public static final String DESCARGADO = "descargado";
	public static final String SOLO_DESCARGA_WIFI = "solo_descarga_wifi";


	private List<FileSales> fileList;
	
	public EasySyncDB(Context _context) {
		super(_context);
		// TODO Auto-generated constructor stub
	}

	public Cursor getAllCTablaSinRegistroItemsCursor() {
		synchronized (Lock) {
			open();
			// ////////Log.i("info", "query bancos. ");
			return db.query(DATABASE_TABLE_CTABLA_SIN_REGISTRO,
					new String[] { CTABLA_SIN_REGISTRO_NOMBRE_TABLA }, null,
					null, null, null, null);

		}
	}

	public Cursor getAllCTablaSincronizacionDownItemsCursor() {
		synchronized (Lock) {
			open();
			// ////////Log.i("info", "query bancos. ");
			return db.query(DATABASE_TABLE_CTABLA_SINCRONIZACION_DOWN,
					new String[] { CTABLA_SINCRONIZACION_DOWN_TABLA },
					CTABLA_SINCRONIZACION_SENTIDO + " == 'D'", null, null,
					null, null);
		}
	}

	public Cursor getAllCTablaSincronizacionDownTodasItemsCursor() {
		synchronized (Lock) {
			open();
			// ////////Log.i("info", "query bancos. ");
			return db.query(DATABASE_TABLE_CTABLA_SINCRONIZACION,
					new String[] { CTABLA_SINCRONIZACION_DOWN_TABLA },
					CTABLA_SINCRONIZACION_SENTIDO + " == 'D'", null, null,
					null, null);
		}
	}

	public Cursor getAllCTablaSincronizacionUpNovItemsCursor() {
		synchronized (Lock) {
			open();
			// ////////Log.i("info", "query bancos. ");
			return db.query(DATABASE_TABLE_CTABLA_SINCRONIZACION_DOWN,
					new String[] { CTABLA_SINCRONIZACION_DOWN_TABLA },
					CTABLA_SINCRONIZACION_SENTIDO + " == 'U'", null, null,
					null, null);
		}
	}

	public Cursor getAllCTablaSincronizacionUpItemsCursor() {
		synchronized (Lock) {
			open();
			// ////////Log.i("info", "query bancos. ");
			return db.query(DATABASE_TABLE_CTABLA_SINCRONIZACION,
					new String[] { CTABLA_SINCRONIZACION_TABLA },
					CTABLA_SINCRONIZACION_SENTIDO + " == 'U'", null, null,
					null, null);
		}
	}

	public Cursor getAllCAuxiliarEasyServerItemsCursor(String pTipo) {
		synchronized (Lock) {
			open();
			// ////////Log.i("info", "query bancos. ");
			return db.rawQuery(
					"Select * from esc_auxiliar_easy_server where tipo " + "='"
							+ pTipo + "' order by  orden asc", null);
		}
	}
	

	public List<FileSales> getFileList(String productoId) {
		synchronized (ItaloDBAdapter.Lock) {
			this.open();

			fileList = new ArrayList<FileSales>();
			Cursor c1;
			if (!(productoId.equals("")))
			{
				c1 = db.query(
						DATABASE_TABLE_ESC_FILE,
						new String[] { ID, TIPO, PRODUCTO_ID, FILE_PATH_RELATIVE,
								FILE_NAME, FILE_EXTENSION, FILE_DATE, FILE_SIZE, DESCARGADO,SOLO_DESCARGA_WIFI },
						PRODUCTO_ID + " == '" + productoId + "'", null, null, null,
						null);
			}
			else
			{
				c1 = db.query(
						DATABASE_TABLE_ESC_FILE,
						new String[] { ID, TIPO, PRODUCTO_ID, FILE_PATH_RELATIVE,
								FILE_NAME, FILE_EXTENSION, FILE_DATE, FILE_SIZE, DESCARGADO, SOLO_DESCARGA_WIFI },
						null, null, null, null,
						FILE_PATH_RELATIVE + "," + FILE_NAME);				
			}
			fileList.clear();
			
			if (c1.moveToFirst())
				do {
					Date fecha_actual = new Date();
					fecha_actual =	Funciones.getStringToDateTime(c1.getString(c1.getColumnIndex(FILE_DATE)));
					fileList.add(
					new FileSales// (unidad_negocio_id, descripcion,
							// label_item_spinner)
					(
							 c1.getInt(c1.getColumnIndex(ID)) 
							,c1.getString(c1.getColumnIndex(TIPO)) 
							,c1.getString(c1.getColumnIndex(PRODUCTO_ID))
							,c1.getString(c1.getColumnIndex(FILE_PATH_RELATIVE))
							,c1.getString(c1.getColumnIndex(FILE_NAME))
							,c1.getString(c1.getColumnIndex(FILE_EXTENSION))
							,fecha_actual
							,c1.getLong(c1.getColumnIndex(FILE_SIZE))
							,c1.getString(c1.getColumnIndex(DESCARGADO))
							,c1.getString(c1.getColumnIndex(SOLO_DESCARGA_WIFI))
					));
				} while (c1.moveToNext());
			c1.close();
			return fileList;
		}
	}

	
}
