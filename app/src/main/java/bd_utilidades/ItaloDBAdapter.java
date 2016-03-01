package bd_utilidades;

import actividad_diaria.ExtraRuta;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import com.italo_view.GlobaG;

import cobros.Transaccion_SQL;

import pedidos.Promo;
import utilidades.EasyUtilidades;
import utilidades.EasyZip;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;

public class ItaloDBAdapter {
	protected static SQLiteDatabase db;
	private final Context context;
	private DataBaseHelper dbHelper;
//	private static final int DATABASE_VERSION = 1;

	public static String Lock = "dblock";

	public ItaloDBAdapter(Context _context) {
		this.context = _context;
		dbHelper = new DataBaseHelper(context);
		try {
			dbHelper.createDataBase();
	  	} catch (IOException ioe) {
	  		//throw new Error("Unable to create database");
		}
	}

	public boolean copyDataBase() {
		try {
			return dbHelper.copyDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}	
	
	@SuppressLint("SdCardPath")
	public class DataBaseHelper extends SQLiteOpenHelper{
		//The Android's default system path of your application database.
		private static final String DB_PATH = "/data/data/com.italo_view/databases/";
		private static final String DB_NAME = "bd_italo.s3db";
		private SQLiteDatabase myDataBase; 
		private final Context myContext;
	 
	    /**
	     * Constructor
	     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	     * @param context
	     */
	    public DataBaseHelper(Context context) {
	    	super(context, DB_NAME, null, 1);
	    	this.myContext = context;
	    }	
	 
	  /**
	     * Creates a empty database on the system and rewrites it with your own database.
	     * */
	    public void createDataBase() throws IOException{
	    	boolean dbExist = checkDataBase();
	    	if(dbExist){
	    		//Do nothing
	    	}else{
	    		//By calling this method and empty database will be created into the default system path
	    		//of your application so we are gonna be able to overwrite that database with our database.
	    		this.getReadableDatabase();
	    		try {
	    			copyDataBase();
	    		} catch (IOException e) {
			  		Log.e("info","catch create");
	        	}
	    	}
	    }
	 
	    /**
	     * Check if the database already exist to avoid re-copying the file each time you open the application.
	     * @return true if it exists, false if it doesn't
	     */
	    private boolean checkDataBase(){
	    	SQLiteDatabase checkDB = null;
	    	try{
	    		String myPath = DB_PATH + DB_NAME;
	    		
	    		if (EasyUtilidades.ExisteArchivo(myPath))
	    		{
	    			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	    		}	    		
	    		
	    	}catch(SQLiteException e){
	    		Log.e("info","Do not exist");
	    		//database does't exist yet.
	    	}
	 
	    	if(checkDB != null){
	    		checkDB.close();
	    	}
	    	return checkDB != null ? true : false;
	    }
	 
	    /**
	     * Copies your database from your local assets-folder to the just created empty database in the
	     * system folder, from where it can be accessed and handled.
	     * This is done by transfering bytestream.
	     * */

	    /*
	    private void copyDataBase() throws IOException{
	    	//Open your local db as the input stream
	    	InputStream myInput = myContext.getAssets().open(DB_NAME);
	    	// Path to the just created empty db
	    	String outFileName = DB_PATH + DB_NAME;
	    	//Open the empty db as the output stream
	    	OutputStream myOutput = new FileOutputStream(outFileName);
	  		//transfer bytes from the inputfile to the outputfile
	    	byte[] buffer = new byte[1024];
	    	int length;
	    	while ((length = myInput.read(buffer))>0){
	    		myOutput.write(buffer, 0, length);
	    	}
	    	//Close the streams
	    	myOutput.flush();
	    	myOutput.close();
	    	myInput.close();
	    }
	    */
	    /*
		public boolean copyDataBase() {
			try {
				return dbHelper.copyDataBase();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return false;
			}
		}
		*/
	 
		public boolean copyDataBase() throws IOException {

			EasyZip easyZip = new EasyZip();
			easyZip.UnZippingFile(GlobaG.pathArchivosSync + "/" + GlobaG.nombreZipDown, GlobaG.DATABASE_PATH);

			return true;
		}
	    
	    public void openDataBase() throws SQLException{
	    	//Open the database
	    	String myPath = DB_PATH + DB_NAME;
	    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	    }
	 
	    @Override
		public synchronized void close() {
	    	if(myDataBase != null)
	    		myDataBase.close();
    	    super.close();
		}
	 
		@Override
		public void onCreate(SQLiteDatabase db) {}
	 
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
		// Add your public helper methods to access and get content from the database.
		// You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
		// to you to create adapters for your views.
	}
	
	 public void open() {
		 if ((db != null) && (db.isOpen())) {
		 
		 } else {
			 try {
				 openDataBase();
			 } catch (SQLiteException e) {
			 } catch (Exception e) {}
		 }
	 }
		 
	 public void openDataBase() throws SQLiteException, Exception {
		 try {
			 db = dbHelper.getWritableDatabase();
		 } catch (Exception ex) {}
	 } 

	 public void close() {
		 synchronized (Lock) {
			 try{
				 db.close();
			 }catch(Exception ex){}
		 }
	 }

	 public void beginTransaction() {
		 synchronized (Lock) {
			 open();
			 db.beginTransaction();
		 }
	 }

	 public void setTransactionSuccessful() {
		 synchronized (Lock) {
			 db.setTransactionSuccessful();
		 }
	 }

	 public void endTransaction() {
		 synchronized (Lock) {
			 db.endTransaction();
		 }
	 }

	/*
	 * 
	 * CLIENTES
	 * 
	 */
	
	public Cursor getClientes(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT cliente_id,nombre_cliente,tejido,ruta_id FROM  esd_cliente", new String [] {});
			return 	cursor;
		}
	}

	public Cursor getClientesXFecha(String fecha){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT cliente_id,nombre_cliente,tejido,C.ruta_id FROM  esd_cliente AS C JOIN esd_informacion_ruta AS I ON C.sector_id=i.sector_id AND C.ruta_id=i.ruta_id WHERE date(fecha)=? ORDER BY tejido", new String [] {fecha});
			return 	cursor;
		}
	}

	public Cursor getClientesByFilters(String sector, String ruta, int buscarPor, String busqueda){
		synchronized (Lock) {
			open();
			Cursor cursor = null;
			String input="%"+busqueda+"%";
			if(ruta.equalsIgnoreCase("todas")){
				switch(buscarPor){
					case 0:
						if(sector.equalsIgnoreCase("todos")){
							cursor=db.rawQuery("SELECT cliente_id,nombre_cliente,tejido,ruta_id FROM  esd_cliente WHERE cliente_id like ? ORDER BY tejido", new String [] {input});
						}else{
							cursor=db.rawQuery("SELECT cliente_id,nombre_cliente,tejido,ruta_id FROM  esd_cliente WHERE sector_id=? AND cliente_id like ? ORDER BY tejido", new String [] {sector,input});
						}
						break;
					case 1:
						if(sector.equalsIgnoreCase("todos")){
							cursor=db.rawQuery("SELECT cliente_id,nombre_cliente,tejido,ruta_id FROM  esd_cliente WHERE nombre_cliente LIKE ? ORDER BY tejido", new String [] {input});
						}else{
							cursor=db.rawQuery("SELECT cliente_id,nombre_cliente,tejido,ruta_id FROM  esd_cliente WHERE sector_id=? AND nombre_cliente LIKE ? ORDER BY tejido", new String [] {sector,input});
						}
						break;
					case 2:
						if(sector.equalsIgnoreCase("todos")){
							cursor=db.rawQuery("SELECT cliente_id,nombre_cliente,tejido,ruta_id FROM  esd_cliente WHERE nit LIKE ? ORDER BY tejido", new String [] {input});
						}else{
							cursor=db.rawQuery("SELECT cliente_id,nombre_cliente,tejido,ruta_id FROM  esd_cliente WHERE sector_id=? AND nit LIKE ? ORDER BY tejido", new String [] {sector,input});
						}
						break;
					case 3:
						if(sector.equalsIgnoreCase("todos")){
							cursor=db.rawQuery("SELECT cliente_id,nombre_cliente,tejido,ruta_id FROM  esd_cliente WHERE cliente_id = ? AND nombre_cliente LIKE ? AND nit LIKE ? ORDER BY tejido", new String [] {input,input,input});
						}else{
							cursor=db.rawQuery("SELECT cliente_id,nombre_cliente,tejido,ruta_id FROM  esd_cliente WHERE sector_id=? AND cliente_id = ? AND nombre_cliente LIKE ? AND nit LIKE ? ORDER BY tejido", new String [] {sector,input,input,input});
						}
						break;
				}
			}else{
				switch(buscarPor){
					case 0:
						cursor=db.rawQuery("SELECT cliente_id,nombre_cliente,tejido,ruta_id FROM  esd_cliente WHERE ruta_id=? AND cliente_id like ? ORDER BY nombre_cliente", new String [] {ruta,input});
						break;
					case 1:
						cursor=db.rawQuery("SELECT cliente_id,nombre_cliente,tejido,ruta_id FROM  esd_cliente WHERE ruta_id=? AND nombre_cliente LIKE ? ORDER BY nombre_cliente", new String [] {ruta,input});
						break;
					case 2:
						cursor=db.rawQuery("SELECT cliente_id,nombre_cliente,tejido,ruta_id FROM  esd_cliente WHERE ruta_id=? AND nit LIKE ? ORDER BY nombre_cliente", new String [] {ruta,input});
						break;
					case 3:
						cursor=db.rawQuery("SELECT cliente_id,nombre_cliente,tejido,ruta_id FROM  esd_cliente WHERE ruta_id=? AND cliente_id like ? AND nombre_cliente LIKE ? AND nit LIKE ? ORDER BY nombre_cliente", new String [] {ruta,input,input,input});
						break;
				}
			}
			return 	cursor;
		}
	}
	
	public Cursor getClienteSearchAuto(){
		synchronized (Lock) {
			open();
			Cursor cursor = db.rawQuery("SELECT cliente_id, nombre_cliente, nit FROM esd_cliente ", new String [] {});
			return 	cursor;
		}
	}

	public Cursor getClienteInfoGeneral(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor = db.rawQuery("SELECT cliente_id, TI.nombre, nit, nombre_cliente, razon_social, direccion, D.nombre, CI.nombre, barrio, telefono, celular, email_corporativo,(select strftime('%m/%d/%Y',fecha) from esd_informacion_ruta WHERE date('now','localtime')<date(fecha) AND ruta_id=C.ruta_id LIMIT 1) FROM (esd_cliente AS C JOIN esd_departamento AS D ON C.departamento_id=D.departamento_id) JOIN esd_ciudad AS CI ON (C.ciudad_id=CI.ciudad_id AND C.departamento_id=CI.departamento_id) JOIN esd_tipo_identificacion AS TI ON C.tipo_identificacion_id=TI.tipo_identificacion_id WHERE cliente_id=?", new String [] {cliente_id});
			return 	cursor;
		}
	}

	public Cursor getClienteInfoFinanciera(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor = db.rawQuery("SELECT tipo_venta, nombre_forma_pago, c.grupo_cliente_id, nombre_grupo_cliente, cu.cupo_id, vencimiento, nombre_impuesto, estado_cartera, bandera_cliente,nombre_cliente, cu.valor_cupo FROM (((esd_cliente AS c JOIN esd_cupo AS cu ON c.cupo_id=cu.cupo_id) LEFT OUTER JOIN  esd_impuesto AS i ON i.impuesto_id=c.impuesto_id ) JOIN esd_forma_pago AS fp ON c.forma_pago_id=fp.forma_pago_id) JOIN esd_grupo_cliente AS gc ON gc.grupo_cliente_id = c.grupo_cliente_id WHERE cliente_id=?", new String [] {cliente_id});
			return 	cursor;
		}
	}

	public Cursor getClienteInfoComercial(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor = db.rawQuery("SELECT d.distrito_id,nombre_distrito, c.sector_id, c.ruta_id, c.tejido, t.tipo_cliente_id,t.nombre , ca.cadena_id,ca.nombre_cadena, can.canal_id,nombre_canal, estado_erp, strftime('%m/%d/%Y',fecha_creacion_erp), strftime('%m/%d/%Y',fecha_retiro), nombre_sucursal,nombre_cliente FROM (((esd_cliente AS c JOIN esd_distrito AS d ON c.distrito_id=d.distrito_id) JOIN esd_tipo_cliente AS t ON t.tipo_cliente_id=c.tipo_cliente_id) JOIN esd_cadena AS ca ON ca.cadena_id=c.cadena_id) JOIN esd_canal AS can ON can.canal_id = c.canal_id WHERE c.cliente_id=?", new String [] {cliente_id});
			return 	cursor;
		}
	}

	public Cursor getClienteSectores(){
		synchronized (Lock) {
			open();
			Cursor cursor = db.rawQuery("SELECT DISTINCT sector_id FROM esd_cliente ORDER BY sector_id", new String [] {});
			return 	cursor;
		}
	}
	
	public Cursor getClienteRutas(String sector){
		synchronized (Lock) {
			open();
			Cursor cursor = db.rawQuery("SELECT DISTINCT ruta_id FROM esd_cliente WHERE sector_id=? ORDER BY ruta_id ASC", new String [] {sector});
			return 	cursor;
		}
	}

	public Cursor getClienteContactos(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor = db.rawQuery("SELECT nombre_contacto, (SELECT nombre_cargo FROM esd_cargo WHERE cargo_id=tipo_contacto) ,dato_de_contacto, contacto_id FROM  esd_contacto AS contac WHERE cliente_id=?", new String [] {cliente_id});
			return 	cursor;
		}
	}	
	
	public Cursor getContadorClientes(String pNombreCliente, String pCodigoCliente, String pNit)
	{
		synchronized (Lock)
		{
			pNombreCliente = "%" + pNombreCliente + "%";
			pCodigoCliente = "%" + pCodigoCliente + "%";
			pNit = "%" + pNit + "%";
			
			open();
			Cursor cursor = db.rawQuery("SELECT COUNT(id) FROM esd_cliente WHERE " + 
					"nombre_cliente LIKE ? AND cliente_id LIKE ? AND nit LIKE ?", new String[] {pNombreCliente, pCodigoCliente, pNit});
			return cursor;
		}
	}
	
	public Cursor getDatosCliente(String pNombreCliente, String pCodigoCliente, String pNit)
	{
		synchronized (Lock)
		{
			pNombreCliente = "%" + pNombreCliente + "%";
			pCodigoCliente = "%" + pCodigoCliente + "%";
			pNit = "%" + pNit + "%";
			
			open();
			Cursor cursor = db.rawQuery("SELECT cliente_id, nombre_cliente FROM esd_cliente WHERE " + 
					"nombre_cliente LIKE ? AND cliente_id LIKE ? AND nit LIKE ?", 
					new String[] {pNombreCliente, pCodigoCliente, pNit});
			return cursor;
		}
	}
	
	/*
	 * 
	 * CLIENTE CONTACTOS
	 * 
	 */
	
	public Cursor getContacto(String contacto_id){											
		synchronized (Lock) {
			open();
			Cursor cursor = db.rawQuery("SELECT nombre_contacto, (SELECT nombre_cargo FROM esd_cargo WHERE cargo_id=tipo_contacto), (SELECT nombre_medio_contacto FROM esd_medio_contacto WHERE medio_contacto_id=esd_contacto.medio_contacto_id),dato_de_contacto FROM  esd_contacto WHERE contacto_id=?", new String [] {contacto_id});
			return 	cursor;
		}
	}
	
	public boolean deleteContacto(String contacto_id){
		synchronized (Lock) {
			open();
			String sql="INSERT INTO esu_contacto SELECT (SELECT valor FROM esd_rango WHERE id=1),distrito_id,subdistrito_id,sector_id,ruta_id,cliente_id,contacto_id,tipo_contacto,nombre_contacto,medio_contacto_id,dato_de_contacto,'04', datetime('now','localtime') FROM esd_contacto WHERE contacto_id='"+contacto_id+"'";
			String sqldelete="DELETE FROM esd_contacto WHERE contacto_id='"+contacto_id+"'";
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=1) WHERE id=1";
			try{
				db.execSQL(sql);
				db.execSQL(sqldelete);
				db.execSQL(sqlUpdateRango);
			}catch(Exception e){
				db.execSQL(sqlUpdateRango);
				Log.e("info","error deleteContacto "+e);
			}
			return true;
		}
	}

	public boolean addContacto(String cliente_id, String contacto, String cargo, String medioComunicacion, String detalle){
		synchronized (Lock) {
			open();
			String sqlInsert="INSERT INTO esd_contacto SELECT " +
				"(SELECT valor FROM esd_rango WHERE id=1),distrito_id,subdistrito_id,sector_id,ruta_id,cliente_id,(SELECT valor FROM esd_rango WHERE id=1)," +
				"(SELECT cargo_id FROM esd_cargo WHERE nombre_cargo='"+cargo+"'),'"+contacto+"',(SELECT medio_contacto_id FROM esd_medio_contacto WHERE nombre_medio_contacto='"+medioComunicacion+"'),'"+detalle+"' FROM esd_cliente WHERE cliente_id='"+cliente_id+"'";
			String sql= "INSERT INTO esu_contacto SELECT " +
				"(SELECT valor FROM esd_rango WHERE id=1),distrito_id,subdistrito_id,sector_id,ruta_id,cliente_id,(SELECT valor FROM esd_rango WHERE id=1)," +
				"(SELECT cargo_id FROM esd_cargo WHERE nombre_cargo='"+cargo+"'),'"+contacto+"',(SELECT medio_contacto_id FROM esd_medio_contacto WHERE nombre_medio_contacto='"+medioComunicacion+"'),'"+detalle+"','02', datetime('now','localtime') FROM esd_cliente WHERE cliente_id='"+cliente_id+"'";
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=1) WHERE id=1";
			try{
				db.execSQL(sqlInsert);
				db.execSQL(sql);
				db.execSQL(sqlUpdateRango);
			}catch(Exception e){
				db.execSQL(sqlUpdateRango);
				Log.e("info","error addContacto "+e);
			}
			return true;
		}
	}

	public boolean updateContacto(String contacto_id, String contacto, String cargo, String medioComunicacion, String detalle){
		synchronized (Lock) {
			open();
			String sqlupdate="UPDATE esd_contacto SET nombre_contacto='"+contacto+"', tipo_contacto=(SELECT cargo_id FROM esd_cargo WHERE nombre_cargo='"+cargo+"'),medio_contacto_id=(SELECT medio_contacto_id FROM esd_medio_contacto WHERE nombre_medio_contacto='"+medioComunicacion+"'),dato_de_contacto='"+detalle+"' WHERE contacto_id='"+contacto_id+"'";
			String sql="INSERT INTO esu_contacto SELECT (SELECT valor FROM esd_rango WHERE id=1),distrito_id,subdistrito_id,sector_id,ruta_id,cliente_id,contacto_id,tipo_contacto,nombre_contacto,medio_contacto_id,dato_de_contacto,'03', datetime('now','localtime') FROM esd_contacto WHERE contacto_id='"+contacto_id+"'";
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=1) WHERE id=1";
			try{
				db.execSQL(sqlupdate);
				db.execSQL(sql);
				db.execSQL(sqlUpdateRango);
			}catch(Exception e){
				db.execSQL(sqlUpdateRango);
				Log.e("info","error updateContacto "+e);
			}
			return true;
		}
	}
	
	/*
	 * 
	 * CLIENTES HORARIOS 
	 * 
	 */

	public Cursor getClienteDireccion(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor = db.rawQuery("SELECT direccion FROM esd_cliente WHERE cliente_id=?", new String [] {cliente_id});
			return 	cursor;
		}
	}	

	public Cursor getClienteHorarios(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor = db.rawQuery("SELECT id, (SELECT descripcion FROM esd_concepto_horario WHERE concepto_id=h.concepto_id) ,bandera_lunes, bandera_martes, bandera_miercoles, bandera_jueves, bandera_viernes, bandera_sabado, bandera_domingo, hora_inicio, hora_final FROM esd_cliente_horario AS h WHERE cliente_id=?", new String [] {cliente_id});
			return 	cursor;
		}
	}	

	public Cursor getHorario(String horario_id){
		synchronized (Lock) {
			open();
			Cursor cursor = db.rawQuery("select id, (SELECT descripcion FROM esd_concepto_horario WHERE concepto_id=esd_cliente_horario.concepto_id) , lugar_cliente, hora_inicio, hora_final, bandera_lunes,bandera_martes, bandera_miercoles, bandera_jueves, bandera_viernes, bandera_sabado, bandera_domingo FROM esd_cliente_horario  WHERE id=?", new String [] {horario_id});
			return 	cursor;
		}
	}	

	public boolean addHorario(String cliente_id, String tipo, String lugar, String hora_ini, String hora_fin, String lun, String mar, String mie, String jue, String vie, String sab, String dom){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT descripcion  FROM esd_cliente_horario INNER JOIN esd_concepto_horario WHERE  esd_concepto_horario.concepto_id=esd_cliente_horario.concepto_id AND cliente_id=? AND descripcion=?", new String [] {cliente_id,tipo});
			if(!cursor.moveToFirst() && cursor.getCount() == 0){
				String sqlInsert="INSERT INTO esd_cliente_horario SELECT (SELECT valor FROM esd_rango WHERE id=2),distrito_id,subdistrito_id,sector_id,ruta_id,cliente_id,(SELECT concepto_id FROM esd_concepto_horario WHERE descripcion='"+tipo+"'),'"+lugar+"','"+lun+"','"+mar+"','"+mie+"','"+jue+"','"+vie+"','"+sab+"','"+dom+"','"+hora_ini+"','"+hora_fin+"' FROM esd_cliente WHERE cliente_id='"+cliente_id+"'";
				String sql= "INSERT INTO esu_cliente_horario SELECT (SELECT valor FROM esd_rango WHERE id=2),distrito_id,subdistrito_id,sector_id,ruta_id,cliente_id,(SELECT concepto_id FROM esd_concepto_horario WHERE descripcion='"+tipo+"'),'"+lugar+"','"+lun+"','"+mar+"','"+mie+"','"+jue+"','"+vie+"','"+sab+"','"+dom+"','"+hora_ini+"','"+hora_fin+"','02', datetime('now','localtime') FROM esd_cliente WHERE cliente_id='"+cliente_id+"'";
				String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=2) WHERE id=2";
				try{
					db.execSQL(sqlInsert);
					db.execSQL(sql);
					db.execSQL(sqlUpdateRango);
				}catch(Exception e){
					db.execSQL(sqlUpdateRango);
					Log.e("info","error addHorario "+e);
				}
				return true;
			}else{
				return false;
			}
		}
	}

	public boolean updateHorario(String id, String tipo, String lugar, String hora_ini, String hora_fin, String lun, String mar, String mie, String jue, String vie, String sab, String dom){
		synchronized (Lock) {
			open();
			String sqlupdate="UPDATE esd_cliente_horario SET concepto_id=(SELECT concepto_id FROM esd_concepto_horario WHERE descripcion='"+tipo+"'), lugar_cliente='"+lugar+"',hora_inicio='"+hora_ini+"' , hora_final='"+hora_fin+"',bandera_lunes='"+lun+"', bandera_martes='"+mar+"', bandera_miercoles='"+mie+"', bandera_jueves='"+jue+"', bandera_viernes='"+vie+"',bandera_sabado='"+sab+"',bandera_domingo='"+dom+"' WHERE id='"+id+"'";
			String sql="INSERT INTO esu_cliente_horario SELECT (SELECT valor FROM esd_rango WHERE id=2),distrito_id,subdistrito_id,sector_id,ruta_id,cliente_id,concepto_id,lugar_cliente,bandera_lunes,bandera_martes,bandera_miercoles,bandera_jueves,bandera_viernes,bandera_sabado,bandera_domingo,hora_inicio,hora_final,'03', datetime('now','localtime') FROM esd_cliente_horario WHERE id='"+id+"'";
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=2) WHERE id=2";
	
			try{
				db.execSQL(sqlupdate);
				db.execSQL(sql);
				db.execSQL(sqlUpdateRango);
			}catch(Exception e){
				db.execSQL(sqlUpdateRango);
				Log.e("info","error updateHorario "+e);
			}
			return true;
		}
	}
	
	public boolean deleteHorario(String horario_id){
		synchronized (Lock) {
			open();
			String sql="INSERT INTO esu_cliente_horario SELECT (SELECT valor FROM esd_rango WHERE id=2),distrito_id,subdistrito_id,sector_id,ruta_id,cliente_id,concepto_id,lugar_cliente,bandera_lunes,bandera_martes,bandera_miercoles,bandera_jueves,bandera_viernes,bandera_sabado,bandera_domingo,hora_inicio,hora_final,'04', datetime('now','localtime') FROM esd_cliente_horario WHERE id="+horario_id;
			String sqldelete="DELETE FROM esd_cliente_horario WHERE id="+horario_id;
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=2) WHERE id=2";
			try{
				db.execSQL(sql);
				db.execSQL(sqldelete);
				db.execSQL(sqlUpdateRango);
			}catch(Exception e){
				db.execSQL(sqlUpdateRango);
				Log.e("info","error deleteHorario "+e);
			}
			return true;
		}
	}
	
	/*
	 * 
	 * CLIENTE SUCURSALES
	 * 
	 */
	
	public Cursor getClienteSucursales(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor = db.rawQuery("SELECT l.cliente_id, l.razon_social, l.sector_id FROM  esd_cliente as l LEFT OUTER JOIN esd_cliente as r ON l.nombre_sucursal= r.nombre_sucursal WHERE l.cliente_id!=? AND r.cliente_id=? AND  l.nombre_sucursal!='' AND l.nombre_sucursal!='CLIENTES SIN SUCURSAL'", new String [] {cliente_id,cliente_id});
			return 	cursor;
		}
	}
	
	/*
	 * 
	 * CARTERA
	 * 
	 */

	public Cursor getCarteraAllSUM(){
		synchronized (Lock) {
			open();
			Cursor cursor = db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento", new String [] {});
			return 	cursor;
		}
	}	

	public Cursor getCarteraAllSUM(String pClienteID, String pTipoDocumentoID)
	{
		synchronized (Lock) {
			open();
			Cursor cursor = db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento " + 
					"WHERE cliente_id = ? AND tipo_documento LIKE ?", new String [] {pClienteID, pTipoDocumentoID});
			return 	cursor;
		}
	}	
	
	public Cursor getCarteraAllSUM(String pDistritoID, String pSubDistritoID, String pSectorID, String pTipoDocumentoID)
	{
		synchronized (Lock)
		{
			open();
			Cursor cursor = db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento WHERE " +
					"distrito_id LIKE ? AND subdistrito_id LIKE ? AND sector_id LIKE ? AND tipo_documento LIKE ?",
					new String[] {pDistritoID, pSubDistritoID, pSectorID, pTipoDocumentoID});
			return cursor;
		}
	}
	
	public Cursor getCarteraMenor30SUM(){
		synchronized (Lock) {
			open();
			Cursor cursor  = db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento WHERE (julianday('now','localtime') - julianday(fecha_vencimiento)) < 30", new String [] {});
			return 	cursor;
		}
	}	

	public Cursor getCarteraMenor30SUM(String pClienteID, String pTipoDocumentoID)
	{
		synchronized (Lock) {
			open();
			Cursor cursor = db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento " +
					"WHERE (julianday('now','localtime') - julianday(fecha_vencimiento)) < 30 " +
					"AND cliente_id = ? AND tipo_documento LIKE ?", new String [] {pClienteID, pTipoDocumentoID});
			return 	cursor;
		}
	}
	
	public Cursor getCarteraMenor30SUM(String pDistritoID, String pSubDistritoID, String pSectorID, String pTipoDocumentoID)
	{
		synchronized(Lock)
		{
			open();
			Cursor cursor = db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento " +
					"WHERE (julianday('now','localtime') - julianday(fecha_vencimiento)) < 30 AND " +
					"distrito_id LIKE ? AND subdistrito_id LIKE ? AND sector_id LIKE ? AND tipo_documento LIKE ?", 
					new String[] {pDistritoID, pSubDistritoID, pSectorID, pTipoDocumentoID});
			return cursor;
		}
	}

	public Cursor getCarteraMayor30SUM(){
		synchronized (Lock) {
			open();
			Cursor cursor =db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento WHERE (julianday('now','localtime') - julianday(fecha_vencimiento)) BETWEEN 30 AND 60", new String [] {});
			return 	cursor;
		}
	}	

	public Cursor getCarteraMayor30SUM(String pClienteID, String pTipoDocumentoID)
	{
		synchronized (Lock) {
			open();
			Cursor cursor = db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento " +
					"WHERE (julianday('now','localtime') - julianday(fecha_vencimiento)) BETWEEN 30 AND 60 " + 
					"AND cliente_id = ? AND tipo_documento LIKE ?", new String [] {pClienteID, pTipoDocumentoID});
			return 	cursor;
		}
	}
	
	public Cursor getCarteraMayor30SUM(String pDistritoID, String pSubDistritoID, String pSectorID, String pTipoDocumentoID)
	{
		synchronized (Lock)
		{
			open();
			Cursor cursor = db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento " + 
					"WHERE (julianday('now','localtime') - julianday(fecha_vencimiento)) BETWEEN 30 AND 60 AND " +
					"distrito_id LIKE ? AND subdistrito_id LIKE ? AND sector_id LIKE ? AND tipo_documento LIKE ?",
					new String[] {pDistritoID, pSubDistritoID, pSectorID, pTipoDocumentoID});
			return cursor;
		}
	}

	public Cursor getCarteraMayor60SUM(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento WHERE (julianday('now','localtime') - julianday(fecha_vencimiento)) BETWEEN 60 AND 90", new String [] {});
			return 	cursor;
		}
	}

	public Cursor getCarteraMayor60SUM(String pClienteID, String pTipoDocumentoID)
	{
		synchronized (Lock) {
			open();
			Cursor cursor =db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento " + 
					"WHERE (julianday('now','localtime') - julianday(fecha_vencimiento)) BETWEEN 60 AND 90 " +
					"AND cliente_id = ? AND tipo_documento LIKE ?", new String [] {pClienteID, pTipoDocumentoID});
			return 	cursor;
		}
	}
	
	public Cursor getCarteraMayor60SUM(String pDistritoID, String pSubDistritoID, String pSectorID, String pTipoDocumentoID)
	{
		synchronized (Lock)
		{
			open();
			Cursor cursor = db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento " + 
					"WHERE (julianday('now','localtime') - julianday(fecha_vencimiento)) BETWEEN 60 AND 90 AND " +
					"distrito_id LIKE ? AND subdistrito_id LIKE ? AND sector_id LIKE ? AND tipo_documento LIKE ?",
					new String[] {pDistritoID, pSubDistritoID, pSectorID, pTipoDocumentoID});
			return cursor;
		}
	}

	public Cursor getCarteraMayor90SUM(){
		synchronized (Lock) {
			open();
			Cursor cursor =db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento WHERE 90 < (julianday('now','localtime') - julianday(fecha_vencimiento))", new String [] {});
			return 	cursor;
		}
	}	

	public Cursor getCarteraMayor90SUM(String pClienteID, String pTipoDocumentoID)
	{
		synchronized (Lock) {
			open();
			Cursor cursor = db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento " +
					"WHERE 90 < (julianday('now','localtime') - julianday(fecha_vencimiento)) " +
					"AND cliente_id = ? AND tipo_documento LIKE ?", new String [] {pClienteID, pTipoDocumentoID});
			return 	cursor;
		}
	}
	
	public Cursor getCarteraMayor90SUM (String pDistritoID, String pSubDistritoID, String pSectorID, String pTipoDocumentoID)
	{
		synchronized (Lock)
		{
			open();
			Cursor cursor = db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento " + 
					"WHERE 90 < (julianday('now','localtime') - julianday(fecha_vencimiento)) AND " + 
					"distrito_id LIKE ? AND subdistrito_id LIKE ? AND sector_id LIKE ? AND tipo_documento LIKE ?",
					new String[] {pDistritoID, pSubDistritoID, pSectorID, pTipoDocumentoID});
			return cursor;
		}
	}

	public Cursor getCarteraTotalVencidad(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento WHERE 0 < (julianday('now','localtime') - julianday(fecha_vencimiento))", new String [] {});
			return 	cursor;
		}
	}	

	public Cursor getCarteraTotalVencidad(String pClienteID, String pTipoDocumentoID)
	{
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento " + 
					"WHERE 0 < (julianday('now','localtime') - julianday(fecha_vencimiento)) " + 
					"AND cliente_id = ? AND tipo_documento LIKE ?", new String [] {pClienteID, pTipoDocumentoID});
			return cursor;
		}
	}
	
	public Cursor getCarteraTotalVencidas(String pDistritoID, String pSubDistritoID, String pSectorID, String pTipoDocumentoID)
	{
		synchronized (Lock)
		{
			open();
			Cursor cursor = db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento " +
					"WHERE 0 < (julianday('now','localtime') - julianday(fecha_vencimiento)) AND " +
					"distrito_id LIKE ? AND subdistrito_id LIKE ? AND sector_id LIKE ? AND tipo_documento LIKE ?",
					new String[] {pDistritoID, pSubDistritoID, pSectorID, pTipoDocumentoID});
			return cursor;
		}
	}

	public Cursor getCarteraTotalAlDiaSum(){
		synchronized (Lock) {
			open();
			Cursor cursor = db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento WHERE (julianday('now','localtime') - julianday(fecha_vencimiento)) < 1", new String [] {});
			return 	cursor;
		}
	}	

	public Cursor getCarteraTotalAlDiaSum(String pClienteID, String pTipoDocumentoID)
	{
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento " +
					"WHERE (julianday('now','localtime') - julianday(fecha_vencimiento)) < 1 " +
					"AND cliente_id = ? AND tipo_documento LIKE ?", new String [] {pClienteID, pTipoDocumentoID});
			return 	cursor;
		}
	}
	
	public Cursor getCarteraTotalAlDiaSum(String pDistritoID, String pSubDistritoID, String pSectorID, String pTipoDocumentoID)
	{
		synchronized (Lock)
		{
			open();
			Cursor cursor = db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento " + 
					"WHERE (julianday('now','localtime') - julianday(fecha_vencimiento)) < 1 AND " +
					"distrito_id LIKE ? AND subdistrito_id LIKE ? AND sector_id LIKE ? AND tipo_documento LIKE ?",
					new String[] {pDistritoID, pSubDistritoID, pSectorID, pTipoDocumentoID});
			return cursor;
		}
	}

	public Cursor getCarteraProximasAVencerSum(){
		synchronized (Lock) {
			open();
			Cursor cursor = db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento WHERE (julianday('now','localtime') - julianday(fecha_vencimiento)) BETWEEN -8 AND -1", new String [] {});
			return 	cursor;
		}
	}	

	public Cursor getCarteraProximasAVencerSum(String pClienteID, String pTipoDocumentoID)
	{
		synchronized (Lock) {
			open();
			Cursor cursor =db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento " +
					"WHERE (julianday('now','localtime') - julianday(fecha_vencimiento)) BETWEEN -8 AND -1 " +
					"AND cliente_id = ? AND tipo_documento LIKE ?", new String [] {pClienteID, pTipoDocumentoID});
			return 	cursor;
		}
	}	
	
	public Cursor getCarteraProximasAVencerSum(String pDistritoID, String pSubDistritoID, String pSectorID, String pTipoDocumentoID)
	{
		synchronized (Lock)
		{
			open();
			Cursor cursor = db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento " + 
					"WHERE (julianday('now','localtime') - julianday(fecha_vencimiento)) BETWEEN -8 AND -1 AND " +
					"distrito_id LIKE ? AND subdistrito_id LIKE ? AND sector_id LIKE ? AND tipo_documento LIKE ?",
					new String[] {pDistritoID, pSubDistritoID, pSectorID, pTipoDocumentoID});
			return cursor;
		}
	}
	
	public Cursor getListaCartera(String pClienteID, String pTipoDocumentoID, int pTipoEdad)
	{
		synchronized (Lock)
		{
			pTipoDocumentoID = "%" + pTipoDocumentoID + "%";
			
			String lSentencia = "SELECT car.cliente_id, cli.nombre_cliente, tip.nombre_corto, car.documento_id, " +
					"strftime('%m/%d/%Y', car.fecha_base) AS fecha_base, " +
					"strftime('%m/%d/%Y', car.fecha_vencimiento) AS fecha_vencimiento, " +
					"(julianday(date('now','localtime')) - julianday(date(car.fecha_vencimiento))) AS dias, " +
					"car.saldo_pendiente, car.tipo_documento FROM esd_cartera_documento AS car INNER JOIN esd_cliente AS cli ON " +
					"car.cliente_id = cli.cliente_id INNER JOIN esd_cartera_tipo_documento AS tip ON " +
					"tip.cartera_tipo_documento_id = car.tipo_documento WHERE car.saldo_pendiente != 0 AND " +
					"car.cliente_id = ? AND tip.cartera_tipo_documento_id LIKE ? ";
		
			lSentencia = "SELECT * FROM (" + lSentencia + ") AS fuente ";
			
			switch(pTipoEdad)
			{
				case 1:
					lSentencia = lSentencia + "WHERE dias < 30 ";
					break;
				case 2:
					lSentencia = lSentencia + "WHERE dias BETWEEN 30 AND 60 ";
					break;
				case 3:
					lSentencia = lSentencia + "WHERE dias BETWEEN 60 AND 90 ";
					break;
				case 4:
					lSentencia = lSentencia + "WHERE dias > 90 ";
					break;
				case 5:
					lSentencia = lSentencia + "WHERE dias > 0 ";
					break;
				case 6:
					lSentencia = lSentencia + "WHERE dias < 1 ";
					break;
				case 7:
					lSentencia = lSentencia + "WHERE dias BETWEEN -8 AND -1 ";
					break;
			}
			open();
			Cursor cursor = db.rawQuery(lSentencia, new String[] {pClienteID, pTipoDocumentoID});
			return cursor;
		}
	}
	
	public Cursor getListaCartera(String pDistritoID, String pSubDistritoID, String pSectorID, String pTipoDocumentoID, int pTipoEdad)
	{
		synchronized (Lock)
		{
			pDistritoID = "%" + pDistritoID + "%";
			pSubDistritoID = "%" + pSubDistritoID + "%";
			pSectorID = "%" + pSectorID + "%";
			pTipoDocumentoID = "%" + pTipoDocumentoID + "%";
			
			String lSentencia = "SELECT car.cliente_id AS cliente_id, cli.nombre_cliente, tip.nombre_corto, car.documento_id, " +
					"strftime('%m/%d/%Y', car.fecha_base) AS fecha_base, " +
					"strftime('%m/%d/%Y', car.fecha_vencimiento) AS fecha_vencimiento, " +
					"(julianday(date('now','localtime')) - julianday(date(car.fecha_vencimiento))) AS dias, " +
					"car.saldo_pendiente, car.tipo_documento FROM esd_cartera_documento AS car INNER JOIN esd_cliente AS cli ON " +
					"car.cliente_id = cli.cliente_id INNER JOIN esd_cartera_tipo_documento AS tip ON " +
					"tip.cartera_tipo_documento_id = car.tipo_documento WHERE car.saldo_pendiente != 0 AND " +
					"cli.distrito_id LIKE ? AND cli.subdistrito_id LIKE ? AND cli.sector_id LIKE ? AND " +
					"tip.cartera_tipo_documento_id LIKE ? ";
		
			lSentencia = "SELECT * FROM (" + lSentencia + ") AS fuente ";
			
			switch(pTipoEdad)
			{
				case 1:
					lSentencia = lSentencia + "WHERE dias < 30 ";
					break;
				case 2:
					lSentencia = lSentencia + "WHERE dias BETWEEN 30 AND 60 ";
					break;
				case 3:
					lSentencia = lSentencia + "WHERE dias BETWEEN 60 AND 90 ";
					break;
				case 4:
					lSentencia = lSentencia + "WHERE dias > 90 ";
					break;
				case 5:
					lSentencia = lSentencia + "WHERE dias > 0 ";
					break;
				case 6:
					lSentencia = lSentencia + "WHERE dias < 1 ";
					break;
				case 7:
					lSentencia = lSentencia + "WHERE dias BETWEEN -8 AND -1 ";
					break;
			}
			
			lSentencia = lSentencia + "ORDER BY cliente_id";
			open();
			Cursor cursor = db.rawQuery(lSentencia, new String[] {pDistritoID, pSubDistritoID, pSectorID, pTipoDocumentoID});
			return cursor;
		}
	}
	
	public Cursor getCarteraByCliente(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor = null;
			cursor=db.rawQuery("SELECT d.cliente_id, nombre_cliente , nombre_corto,documento_id,strftime('%m/%d/%Y', fecha_base),strftime('%m/%d/%Y',fecha_vencimiento),  (julianday(date('now','localtime')) - julianday(date(fecha_vencimiento))) AS D,saldo_pendiente FROM (esd_cartera_documento AS d JOIN esd_cliente AS c ON c.cliente_id=d.cliente_id) JOIN esd_cartera_tipo_documento ON cartera_tipo_documento_id=tipo_documento  WHERE c.cliente_id=? AND saldo_pendiente!=0 ORDER BY d.cliente_id DESC", new String [] {cliente_id});
			return 	cursor;
		}
	}

	public Cursor getCarteraByClienteParaCobros(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor = null;
			cursor=db.rawQuery("SELECT documento_id,descripcion,(julianday('now','localtime') - julianday(fecha_vencimiento)) AS D,valor_total,saldo_pendiente,strftime('%m/%d/%Y',fecha_vencimiento),strftime('%m/%d/%Y', fecha_base) FROM esd_cartera_documento JOIN esd_cartera_tipo_documento ON cartera_tipo_documento_id=tipo_documento WHERE cliente_id=? AND saldo_pendiente != 0 ORDER BY D DESC", new String [] {cliente_id});
			return 	cursor;
		}
	}

	public Cursor getCarteraByClienteParaCobrosConFiltros(String cliente_id,int tipoBus, String date){
		synchronized (Lock) {
			open();
			Cursor cursor = null;
			switch(tipoBus){
				case 0:
					if(date.equalsIgnoreCase("")){
						cursor=db.rawQuery("SELECT documento_id,descripcion,(julianday('now','localtime') - julianday(fecha_vencimiento)) AS D,valor_total,saldo_pendiente,strftime('%m/%d/%Y',fecha_vencimiento),strftime('%m/%d/%Y', fecha_base) FROM esd_cartera_documento JOIN esd_cartera_tipo_documento ON cartera_tipo_documento_id=tipo_documento WHERE cliente_id=? AND (julianday('now','localtime') - julianday(fecha_vencimiento))<30 AND saldo_pendiente > 0 ORDER BY D DESC", new String [] {cliente_id});
					}else{
						cursor=db.rawQuery("SELECT documento_id,descripcion,(julianday('now','localtime') - julianday(fecha_vencimiento)) AS D,valor_total,saldo_pendiente,strftime('%m/%d/%Y',fecha_vencimiento),strftime('%m/%d/%Y', fecha_base) FROM esd_cartera_documento JOIN esd_cartera_tipo_documento ON cartera_tipo_documento_id=tipo_documento WHERE cliente_id=? AND (julianday('now','localtime') - julianday(fecha_vencimiento))<30 AND date(fecha_base)=? AND saldo_pendiente > 0 ORDER BY D DESC", new String [] {cliente_id,date});
					}
					break;
				case 1:
					if(date.equalsIgnoreCase("")){
						cursor=db.rawQuery("SELECT documento_id,descripcion,(julianday('now','localtime') - julianday(fecha_vencimiento)) AS D,valor_total,saldo_pendiente,strftime('%m/%d/%Y',fecha_vencimiento),strftime('%m/%d/%Y', fecha_base) FROM esd_cartera_documento JOIN esd_cartera_tipo_documento ON cartera_tipo_documento_id=tipo_documento WHERE cliente_id=? AND (julianday('now','localtime') - julianday(fecha_vencimiento))>=30 AND saldo_pendiente > 0 ORDER BY D DESC", new String [] {cliente_id});
					}else{
						cursor=db.rawQuery("SELECT documento_id,descripcion,(julianday('now','localtime') - julianday(fecha_vencimiento)) AS D,valor_total,saldo_pendiente,strftime('%m/%d/%Y',fecha_vencimiento),strftime('%m/%d/%Y', fecha_base) FROM esd_cartera_documento JOIN esd_cartera_tipo_documento ON cartera_tipo_documento_id=tipo_documento WHERE cliente_id=? AND (julianday('now','localtime') - julianday(fecha_vencimiento))>=30 AND date(fecha_base)=? AND saldo_pendiente > 0 ORDER BY D DESC", new String [] {cliente_id,date});
					}
					break;
				case 2:
					if(date.equalsIgnoreCase("")){
						cursor=db.rawQuery("SELECT documento_id,descripcion,(julianday('now','localtime') - julianday(fecha_vencimiento)) AS D,valor_total,saldo_pendiente,strftime('%m/%d/%Y',fecha_vencimiento),strftime('%m/%d/%Y', fecha_base) FROM esd_cartera_documento JOIN esd_cartera_tipo_documento ON cartera_tipo_documento_id=tipo_documento WHERE cliente_id=? AND (julianday('now','localtime') - julianday(fecha_vencimiento))>=60 AND saldo_pendiente > 0 ORDER BY D DESC", new String [] {cliente_id});
					}else{
						cursor=db.rawQuery("SELECT documento_id,descripcion,(julianday('now','localtime') - julianday(fecha_vencimiento)) AS D,valor_total,saldo_pendiente,strftime('%m/%d/%Y',fecha_vencimiento),strftime('%m/%d/%Y', fecha_base) FROM esd_cartera_documento JOIN esd_cartera_tipo_documento ON cartera_tipo_documento_id=tipo_documento WHERE cliente_id=? AND (julianday('now','localtime') - julianday(fecha_vencimiento))>=60 AND date(fecha_base)=? AND saldo_pendiente > 0 ORDER BY D DESC", new String [] {cliente_id,date});
					}
					break;
				case 3:
					if(date.equalsIgnoreCase("")){
						cursor=db.rawQuery("SELECT documento_id,descripcion,(julianday('now','localtime') - julianday(fecha_vencimiento)) AS D,valor_total,saldo_pendiente,strftime('%m/%d/%Y',fecha_vencimiento),strftime('%m/%d/%Y', fecha_base) FROM esd_cartera_documento JOIN esd_cartera_tipo_documento ON cartera_tipo_documento_id=tipo_documento WHERE cliente_id=? AND (julianday('now','localtime') - julianday(fecha_vencimiento))>=90 AND saldo_pendiente > 0 ORDER BY D DESC", new String [] {cliente_id});
					}else{
						cursor=db.rawQuery("SELECT documento_id,descripcion,(julianday('now','localtime') - julianday(fecha_vencimiento)) AS D,valor_total,saldo_pendiente,strftime('%m/%d/%Y',fecha_vencimiento),strftime('%m/%d/%Y', fecha_base) FROM esd_cartera_documento JOIN esd_cartera_tipo_documento ON cartera_tipo_documento_id=tipo_documento WHERE cliente_id=? AND (julianday('now','localtime') - julianday(fecha_vencimiento))>=90 AND date(fecha_base)=? AND saldo_pendiente > 0 ORDER BY D DESC", new String [] {cliente_id,date});
					}
					break;
				case 4:
					if(date.equalsIgnoreCase("")){
						cursor=db.rawQuery("SELECT documento_id,descripcion,(julianday('now','localtime') - julianday(fecha_vencimiento)) AS D,valor_total,saldo_pendiente,strftime('%m/%d/%Y',fecha_vencimiento),strftime('%m/%d/%Y', fecha_base) FROM esd_cartera_documento JOIN esd_cartera_tipo_documento ON cartera_tipo_documento_id=tipo_documento WHERE cliente_id=? AND saldo_pendiente > 0 ORDER BY D DESC", new String [] {cliente_id});
					}else{
						cursor=db.rawQuery("SELECT documento_id,descripcion,(julianday('now','localtime') - julianday(fecha_vencimiento)) AS D,valor_total,saldo_pendiente,strftime('%m/%d/%Y',fecha_vencimiento),strftime('%m/%d/%Y', fecha_base) FROM esd_cartera_documento JOIN esd_cartera_tipo_documento ON cartera_tipo_documento_id=tipo_documento WHERE cliente_id=? AND date(fecha_base)=? AND saldo_pendiente > 0 ORDER BY D DESC", new String [] {cliente_id,date});
					}
					break;
				default:
					if(date.equalsIgnoreCase("")){
						cursor=db.rawQuery("SELECT documento_id,descripcion,(julianday('now','localtime') - julianday(fecha_vencimiento)) AS D,valor_total,saldo_pendiente,strftime('%m/%d/%Y',fecha_vencimiento),strftime('%m/%d/%Y', fecha_base) FROM esd_cartera_documento JOIN esd_cartera_tipo_documento ON cartera_tipo_documento_id=tipo_documento WHERE cliente_id=? AND saldo_pendiente > 0 ORDER BY D DESC", new String [] {cliente_id});
					}else{
						cursor=db.rawQuery("SELECT documento_id,descripcion,(julianday('now','localtime') - julianday(fecha_vencimiento)) AS D,valor_total,saldo_pendiente,strftime('%m/%d/%Y',fecha_vencimiento),strftime('%m/%d/%Y', fecha_base) FROM esd_cartera_documento JOIN esd_cartera_tipo_documento ON cartera_tipo_documento_id=tipo_documento WHERE cliente_id=? AND date(fecha_base)=? AND saldo_pendiente > 0 ORDER BY D DESC", new String [] {cliente_id,date});
					}
					break;
			}
			return 	cursor;
		}
	}
	
	public Cursor getCarteraDetalle(String documento_id){
		synchronized (Lock) {
			open();
			Cursor cursor = null;
			cursor=db.rawQuery("SELECT nombre_cliente, c.cliente_id, descripcion, documento_id, c.sector_id, strftime('%m/%d/%Y', fecha_documento) AS fecha_doc, strftime('%m/%d/%Y',fecha_vencimiento) AS fecha_venc, strftime('%m/%d/%Y', fecha_ultimo_pago) AS fecha_ultimo, (julianday(date('now','localtime')) - julianday(date(fecha_vencimiento))) AS dais_venc, valor_base,	valor_total, saldo_pendiente FROM (esd_cartera_documento AS d JOIN esd_cliente AS c ON c.cliente_id=d.cliente_id) JOIN esd_cartera_tipo_documento ON cartera_tipo_documento_id=tipo_documento WHERE documento_id=?", new String [] {documento_id});
			return 	cursor;
		}
	}	

	public Cursor getCarteraByClienteXFiltros(String nombre_input, String codigo_input, String razon_social_input, boolean todos_input){
		synchronized (Lock) {
			open();
			Cursor cursor = null;
			String nombre="%"+nombre_input+"%";
			String codigo="%"+codigo_input+"%";
			String razon_social="%"+razon_social_input+"%";
	
			if(todos_input){
				cursor=db.rawQuery("SELECT d.cliente_id, nombre_cliente , nombre_corto,documento_id,strftime('%m/%d/%Y', fecha_base),strftime('%m/%d/%Y',fecha_vencimiento),  (julianday(date('now','localtime')) - julianday(date(fecha_vencimiento))) AS D,saldo_pendiente FROM (esd_cartera_documento AS d JOIN esd_cliente AS c ON c.cliente_id=d.cliente_id) JOIN esd_cartera_tipo_documento ON cartera_tipo_documento_id=tipo_documento AND saldo_pendiente!=0 ORDER BY D DESC", new String [] {});
			}else{
				cursor=db.rawQuery("SELECT d.cliente_id, nombre_cliente , nombre_corto,documento_id,strftime('%m/%d/%Y', fecha_base),strftime('%m/%d/%Y',fecha_vencimiento),  (julianday(date('now','localtime')) - julianday(date(fecha_vencimiento))) AS D,saldo_pendiente FROM (esd_cartera_documento AS d JOIN esd_cliente AS c ON c.cliente_id=d.cliente_id) JOIN esd_cartera_tipo_documento ON cartera_tipo_documento_id=tipo_documento WHERE nombre_cliente LIKE ? AND c.cliente_id LIKE ? AND nit LIKE ? AND saldo_pendiente!=0 ORDER BY D DESC", new String [] {nombre,codigo,razon_social});
			}
			return 	cursor;
		}
	}
	
	public Cursor GetDistritosCartera(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT DISTINCT (SELECT nombre_distrito FROM esd_distrito WHERE distrito_id=esd_cartera_documento.distrito_id) FROM esd_cartera_documento", new String [] {});
			return cursor;
		}
	}
	
	public Cursor GetSubdistritosByDistritoCartera(String distrito){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT nombre_subdistrito FROM esd_subdistrito WHERE distrito_id=(SELECT distrito_id FROM esd_distrito WHERE nombre_distrito=?)", new String [] {distrito});
			return cursor;
		}
	}
	
	public Cursor GetSectorBySubdistritosCartera(String subdistrito){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT sector_id FROM esd_sector WHERE subdistrito_id=(SELECT subdistrito_id FROM esd_subdistrito  WHERE nombre_subdistrito=?)", new String [] {subdistrito});
			return cursor;
		}
	}

	public Cursor GetTipoDocCartera(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT DISTINCT (SELECT descripcion FROM esd_cartera_tipo_documento WHERE cartera_tipo_documento_id=tipo_documento) FROM esd_cartera_documento", new String [] {});
			return cursor;
		}
	}
	
	public Cursor cartera_filtro_normal(String distrito, String subdistrito, String sector, String tipo_doc){
		synchronized (Lock) {
			open();
			Cursor cursor = null;
			if(distrito.equalsIgnoreCase("todos")){
				cursor=db.rawQuery("SELECT d.cliente_id, nombre_cliente , nombre_corto,documento_id,strftime('%m/%d/%Y', fecha_base),strftime('%m/%d/%Y',fecha_vencimiento),  (julianday('now','localtime') - julianday(fecha_vencimiento)) AS D,saldo_pendiente FROM (esd_cartera_documento AS d JOIN esd_cliente AS c ON c.cliente_id=d.cliente_id) JOIN esd_cartera_tipo_documento ON cartera_tipo_documento_id=tipo_documento  WHERE descripcion =? AND saldo_pendiente!=0 ORDER BY D DESC", new String [] {tipo_doc});
			}else{
				if (subdistrito.equalsIgnoreCase("todos")){
					cursor=db.rawQuery("SELECT cliente_id,(SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_cartera_documento.cliente_id), nombre_corto,documento_id,strftime('%m/%d/%Y', fecha_base),strftime('%m/%d/%Y',fecha_vencimiento),(julianday('now','localtime') - julianday(fecha_vencimiento)),saldo_pendiente FROM esd_cartera_documento JOIN esd_cartera_tipo_documento WHERE esd_cartera_documento.tipo_documento = esd_cartera_tipo_documento.cartera_tipo_documento_id AND distrito_id=(SELECT distrito_id FROM esd_distrito WHERE nombre_distrito=?) AND descripcion =? AND saldo_pendiente!=0", new String [] {distrito,tipo_doc});
				}else{
					if(sector.equalsIgnoreCase("todos")){
						cursor=db.rawQuery("SELECT cliente_id,(SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_cartera_documento.cliente_id), nombre_corto,documento_id ,strftime('%m/%d/%Y', fecha_base),strftime('%m/%d/%Y',fecha_vencimiento),(julianday('now','localtime') - julianday(fecha_vencimiento)),saldo_pendiente FROM esd_cartera_documento JOIN esd_cartera_tipo_documento WHERE esd_cartera_documento.tipo_documento = esd_cartera_tipo_documento.cartera_tipo_documento_id AND subdistrito_id=(SELECT subdistrito_id FROM esd_subdistrito WHERE nombre_subdistrito=?) AND descripcion =? AND saldo_pendiente!=0", new String [] {subdistrito,tipo_doc});
					}else{
						cursor=db.rawQuery("SELECT cliente_id,(SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_cartera_documento.cliente_id), nombre_corto,documento_id ,strftime('%m/%d/%Y', fecha_base),strftime('%m/%d/%Y',fecha_vencimiento),(julianday('now','localtime') - julianday(fecha_vencimiento)),saldo_pendiente FROM esd_cartera_documento JOIN esd_cartera_tipo_documento WHERE esd_cartera_documento.tipo_documento = esd_cartera_tipo_documento.cartera_tipo_documento_id AND sector_id=? AND descripcion =? AND saldo_pendiente!=0", new String [] {sector,tipo_doc});
					}	
				}
			}
			return 	cursor;
		}
	}
	
	/*
	 * 
	 * COBROS PROGRAMADOS
	 * 
	 */
	
	public Cursor getCobrosProgramadosFuturos(String pClienteID, String pDistritoID, String pSubDistritoID, String pSectorID)
	{
		synchronized (Lock)
		{
			open();
			Cursor cursor;
			
			String lSentencia = 
					"SELECT " + 
						"fac.tipo_documento, fac.documento_id, cli.cliente_id, cli.nombre_cliente, " +
						"car.saldo_pendiente, eve.fecha_prox_visita " +
					"FROM " +
						"esd_visita_evento_factura fac INNER JOIN " +
						"esd_visita_evento eve ON " +
							"fac.visita_id = eve.visita_id AND " +
							"fac.evento_id = eve.evento_id INNER JOIN " +
						"esd_visita vis ON " +
							"eve.visita_id = vis.visita_id INNER JOIN " +
						"esd_cliente cli ON " +
							"vis.cliente_id = cli.cliente_id INNER JOIN " +
						"esd_cartera_documento car ON " +
							"fac.tipo_documento = car.tipo_documento AND " +
							"fac.documento_id = car.documento_id " +
					"WHERE " +
						"eve.tipo_evento_id = '37' AND " +
						"julianday(eve.fecha_prox_visita) > julianday('now','localtime') AND ";
			
			//Complementa la sentencia dependiendo si es por cliente o por distrito/subdistrito/sector
			if (pClienteID != null)
			{
				lSentencia = lSentencia + "vis.cliente_id = ?";
				cursor = db.rawQuery(lSentencia, new String[] {pClienteID});
			}
			else
			{
				lSentencia = lSentencia + "cli.distrito_id LIKE ? AND " +
						"cli.subdistrito_id LIKE ? AND " +
						"cli.sector_id LIKE ?";
				cursor = db.rawQuery(lSentencia, new String[]{pDistritoID, pSubDistritoID, pSectorID});
			}
				
			return cursor;
		}
	}
	
	public Cursor getDocumentosParaProgramar(ArrayList<String> facturas_id)
	{
		synchronized (Lock) 
		{
			open();
			String lQuery = 
					"SELECT " +
						"CAR.documento_id, " + 
						"TIP.nombre_corto, " + 
						"strftime('%m/%d/%Y', CAR.fecha_documento) AS fecha_documento, " + 
						"strftime('%m/%d/%Y', CAR.fecha_vencimiento) AS fecha_vencimiento, " +
						"CAR.saldo_pendiente " +
					"FROM " +
						"esd_cartera_documento AS CAR INNER JOIN " + 
						"esd_cartera_tipo_documento AS TIP ON " +
							"CAR.tipo_documento = TIP.cartera_tipo_documento_id " +
					"WHERE " +
						"CAR.tipo_documento || '-' || CAR.documento_id IN (";
			
			for(String lDocumentoID : facturas_id)
			{
				lQuery = lQuery + lDocumentoID + ",";
			}
			
			lQuery = lQuery.substring(0, lQuery.length() - 1) + ")";
			
			Cursor cursor = db.rawQuery(lQuery, new String [] {});
			return cursor;
		}
	}

	/*
	 * 
	 * ARTICULOS
	 * 
	 */
	
	public Cursor getArticulos(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT producto_id, nombre, nombre_presentacion, peso FROM esd_producto AS P JOIN esd_producto_presentacion AS PP ON P.presentacion_id=PP.presentacion_id", new String [] {});
			return cursor;
		}
	}
	
	public Cursor getArticulosSearchAuto(){
		synchronized (Lock) {
			open();
			Cursor cursor = null;
			cursor=db.rawQuery("SELECT producto_id,codigo_primer_grado,codigo_segundo_grado, nombre FROM esd_producto", new String [] {});
			return 	cursor;
		}
	}


	public Cursor getArticulosXFiltros(int tipoBus, String familia_id, String subfamilia_id, String buscar){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			String busqueda="%"+buscar+"%";
			switch(tipoBus){
				case 0:
					if(familia_id.equalsIgnoreCase("todos")){
						cursor=db.rawQuery("SELECT producto_id, nombre, nombre_presentacion, peso FROM esd_producto AS P JOIN esd_producto_presentacion AS PP ON P.presentacion_id=PP.presentacion_id WHERE producto_id like ?", new String [] {busqueda});
					}else{
						if(subfamilia_id.equalsIgnoreCase("todos")){
							cursor=db.rawQuery("SELECT producto_id, nombre, nombre_presentacion, peso FROM esd_producto AS P JOIN esd_producto_presentacion AS PP ON P.presentacion_id=PP.presentacion_id WHERE P.unidad_negocio_id = ? AND producto_id like ?", new String [] {familia_id,busqueda});
						}else{
							cursor=db.rawQuery("SELECT producto_id, nombre, nombre_presentacion, peso FROM esd_producto AS P JOIN esd_producto_presentacion AS PP ON P.presentacion_id=PP.presentacion_id WHERE P.unidad_negocio_id = ? AND P.grupo_producto_id = ? AND producto_id like ?", new String [] {familia_id,subfamilia_id,busqueda});
						}
					}
					break;
				case 1:
					if(familia_id.equalsIgnoreCase("todos")){
						cursor=db.rawQuery("SELECT producto_id, nombre, nombre_presentacion, peso FROM esd_producto AS P JOIN esd_producto_presentacion AS PP ON P.presentacion_id=PP.presentacion_id WHERE codigo_primer_grado like ?", new String [] {busqueda});
					}else{
						if(subfamilia_id.equalsIgnoreCase("todos")){
							cursor=db.rawQuery("SELECT producto_id, nombre, nombre_presentacion, peso FROM esd_producto AS P JOIN esd_producto_presentacion AS PP ON P.presentacion_id=PP.presentacion_id WHERE P.unidad_negocio_id = ? AND codigo_primer_grado like ?", new String [] {familia_id,busqueda});
						}else{
							cursor=db.rawQuery("SELECT producto_id, nombre, nombre_presentacion, peso FROM esd_producto AS P JOIN esd_producto_presentacion AS PP ON P.presentacion_id=PP.presentacion_id WHERE P.unidad_negocio_id = ? AND P.grupo_producto_id = ? AND codigo_primer_grado like ?", new String [] {familia_id,subfamilia_id,busqueda});
						}
					}
					break;
				case 2:
					if(familia_id.equalsIgnoreCase("todos")){
						cursor=db.rawQuery("SELECT producto_id, nombre, nombre_presentacion, peso FROM esd_producto AS P JOIN esd_producto_presentacion AS PP ON P.presentacion_id=PP.presentacion_id WHERE codigo_segundo_grado like ?", new String [] {busqueda});
					}else{
						if(subfamilia_id.equalsIgnoreCase("todos")){
							cursor=db.rawQuery("SELECT producto_id, nombre, nombre_presentacion, peso FROM esd_producto AS P JOIN esd_producto_presentacion AS PP ON P.presentacion_id=PP.presentacion_id WHERE P.unidad_negocio_id = ? AND codigo_segundo_grado like ?", new String [] {familia_id,busqueda});
						}else{
							cursor=db.rawQuery("SELECT producto_id, nombre, nombre_presentacion, peso FROM esd_producto AS P JOIN esd_producto_presentacion AS PP ON P.presentacion_id=PP.presentacion_id WHERE P.unidad_negocio_id = ? AND P.grupo_producto_id = ? AND codigo_segundo_grado like ?", new String [] {familia_id,subfamilia_id,busqueda});
						}
					}
					break;
				case 3:
					if(familia_id.equalsIgnoreCase("todos")){
						cursor=db.rawQuery("SELECT producto_id, nombre, nombre_presentacion, peso FROM esd_producto AS P JOIN esd_producto_presentacion AS PP ON P.presentacion_id=PP.presentacion_id WHERE nombre LIKE ?", new String [] {busqueda});
					}else{
						if(subfamilia_id.equalsIgnoreCase("todos")){
							cursor=db.rawQuery("SELECT producto_id, nombre, nombre_presentacion, peso FROM esd_producto AS P JOIN esd_producto_presentacion AS PP ON P.presentacion_id=PP.presentacion_id WHERE P.unidad_negocio_id = ? AND nombre LIKE ?", new String [] {familia_id,busqueda});
						}else{
							cursor=db.rawQuery("SELECT producto_id, nombre, nombre_presentacion, peso FROM esd_producto AS P JOIN esd_producto_presentacion AS PP ON P.presentacion_id=PP.presentacion_id WHERE P.unidad_negocio_id = ? AND P.grupo_producto_id = ? AND nombre LIKE ?", new String [] {familia_id,subfamilia_id,busqueda});
						}
					}
					break;
				case 4:
					if(familia_id.equalsIgnoreCase("todos")){
						cursor=db.rawQuery("SELECT producto_id, nombre, nombre_presentacion, peso FROM esd_producto AS P JOIN esd_producto_presentacion AS PP ON P.presentacion_id=PP.presentacion_id WHERE (producto_id like ? OR codigo_segundo_grado like ? OR nombre LIKE ? OR codigo_primer_grado like ?)", new String [] {busqueda,busqueda,busqueda,busqueda});
					}else{
						if(subfamilia_id.equalsIgnoreCase("todos")){
							cursor=db.rawQuery("SELECT producto_id, nombre, nombre_presentacion, peso FROM esd_producto AS P JOIN esd_producto_presentacion AS PP ON P.presentacion_id=PP.presentacion_id WHERE P.unidad_negocio_id = ? AND (producto_id like ? OR codigo_segundo_grado like ? OR nombre LIKE ? OR codigo_primer_grado like ?)", new String [] {familia_id,busqueda,busqueda,busqueda,busqueda});
						}else{
							cursor=db.rawQuery("SELECT producto_id, nombre, nombre_presentacion, peso FROM esd_producto AS P JOIN esd_producto_presentacion AS PP ON P.presentacion_id=PP.presentacion_id WHERE P.unidad_negocio_id = ? AND P.grupo_producto_id = ? AND (producto_id like ? OR codigo_segundo_grado like ? OR nombre LIKE ? OR codigo_primer_grado like ?)", new String [] {familia_id,subfamilia_id,busqueda,busqueda,busqueda,busqueda});
						}
					}
					break;
			}
			return cursor;
		}
	}

	public Cursor getArticulosFamilias(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT unidad_negocio_id, descripcion FROM esd_unidad_negocio", new String [] {});
			return cursor;
		}
	}
	
	public Cursor getArticulosSubfamilias(String familia_id){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT grupo_producto_id, descripcion, unidad_negocio_id FROM esd_grupo_producto WHERE unidad_negocio_id =?", new String [] {familia_id});
			return cursor;
		}
	}
	
	public Cursor getListaPrecios(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT descripcion FROM esd_lista_precio ORDER BY descripcion", new String [] {});
			return cursor;
		}
	}	

	public Cursor getPreciosByListaPrecio(String listaPrecio,String producto_id){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			String sql="";
			try{
				cursor=db.rawQuery("SELECT campo_referencia FROM esd_lista_precio WHERE descripcion=?", new String [] {listaPrecio});			
				if(cursor.moveToFirst()){
					sql="SELECT ROUND("+cursor.getString(0) +",2)," +
						" ROUND("+cursor.getString(0) +"+"+cursor.getString(0) +"*porcentaje_iva/100,2)," +
						" ROUND("+cursor.getString(0) +"/(SELECT unidades_empaque FROM esd_producto_presentacion WHERE presentacion_id=esd_producto.presentacion_id),2)," +
						" ROUND("+cursor.getString(0) +"/(SELECT unidades_empaque FROM esd_producto_presentacion WHERE presentacion_id=esd_producto.presentacion_id)+"+cursor.getString(0) +"/(SELECT unidades_empaque FROM esd_producto_presentacion " +
						"WHERE presentacion_id=esd_producto.presentacion_id)*porcentaje_iva/100,2) " +
						"FROM esd_producto WHERE producto_id=?";
					cursor=db.rawQuery(sql, new String [] {producto_id});
				}
			}catch(Exception e){
				Log.e("info","error getPreciosByListaPrecio "+e);
			}
			return cursor;
		}
	}	
	
	public Cursor getProductoDatos(String producto_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT UN.descripcion, GP.descripcion, P.producto_id, P.nombre,PP.nombre_presentacion, PP.unidades_empaque, P.porcentaje_iva FROM ((esd_producto  AS P JOIN esd_unidad_negocio AS UN ON P.unidad_negocio_id=UN.unidad_negocio_id) JOIN esd_grupo_producto AS GP ON GP.grupo_producto_id=P.grupo_producto_id) JOIN esd_producto_presentacion AS PP ON PP.presentacion_id=P.presentacion_id WHERE producto_id=? AND GP.unidad_negocio_id=P.unidad_negocio_id", new String [] {producto_id});
			return cursor;
		}
	}	

	public Cursor getProductoDaPromociones(String producto_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT * FROM esd_promocion_enc AS PE JOIN esd_promocion_det AS PD ON PE.promocion_id=PD.promocion_id wHERE PD.producto_id=?", new String [] {producto_id});
			return cursor;
		}
	}	

	public Cursor getPromocionesXArticulo(String producto_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT PE.promocion_id, tipo_promocion, Porc_Descuento,cantidad_pedida_minima, Cantidad_Bonificacion, Cod_Producto_Especie,  strftime('%m/%d/%Y',vigencia_inicio), strftime('%m/%d/%Y',vigencia_final) FROM esd_promocion_enc AS PE JOIN esd_promocion_det AS PD ON PE.promocion_id=PD.promocion_id WHERE PD.producto_id=?", new String [] {producto_id});
			return cursor;
		}
	}
	
	
	/*
	 * 
	 * PARETOS
	 * 
	 */
	
	public Cursor getParetos(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id = esd_pareto.cliente_id),sector_id, ruta_id, valor FROM esd_pareto WHERE pareto_sector='80'", new String [] {});
			return cursor;
		}
	}

	public Cursor getParetosByFilters(String sector, String ruta, String porc){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			if(sector.equalsIgnoreCase("todas")){
				cursor=db.rawQuery("SELECT cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id = esd_pareto.cliente_id),sector_id, ruta_id, valor FROM esd_pareto WHERE pareto_sector=?", new String [] {porc});
			}else{
				if(ruta.equalsIgnoreCase("TODAS")){
					cursor=db.rawQuery("SELECT cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id = esd_pareto.cliente_id),sector_id, ruta_id, valor FROM esd_pareto WHERE sector_id= ? AND pareto_sector=?", new String [] {sector,porc});
				}else{
					cursor=db.rawQuery("SELECT cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id = esd_pareto.cliente_id),sector_id, ruta_id, valor FROM esd_pareto WHERE ruta_id=? AND pareto_ruta=? ", new String [] {ruta,porc});
				}
			}
			return cursor;
		}
	}

	public Cursor getParetosSector(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT DISTINCT sector_id FROM esd_pareto", new String [] {});
			return cursor;
		}
	}
	
	/*
	 * 
	 * ASISTENTES 
	 * 
	 */

	/** Presupuesto ventas **/
	public Cursor getAsistentePresupuestoVentas(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT descripcion, valor_presupuesto, valor_venta, cumplimiento FROM esd_presupuesto_venta AS PV JOIN esd_unidad_negocio AS UN ON PV.unidad_negocio_id=UN.unidad_negocio_id WHERE substr(strftime('%Y', 'now','localtime'),3) || strftime('%m', 'now','localtime')=periodo ", new String [] {});
			return cursor;
		}
	}

	public Cursor getAsistentePresupuestoVentas(String sector_id, String periodo){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT descripcion, valor_presupuesto, valor_venta, cumplimiento FROM esd_presupuesto_venta AS PV JOIN esd_unidad_negocio AS UN ON PV.unidad_negocio_id=UN.unidad_negocio_id WHERE  PV.sector_id=? AND PV.periodo=?", new String [] {sector_id,periodo});
			return cursor;
		}
	}
	
	public Cursor getAsistentePresupuestoVentasPeriodos(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT DISTINCT periodo FROM esd_presupuesto_venta ORDER BY periodo DESC", new String [] {});
			return cursor;
		}
	}
	
	public Cursor getAsistentePresupuestoVentasSectores(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT DISTINCT sector_id FROM esd_presupuesto_venta ORDER BY periodo DESC", new String [] {});
			return cursor;
		}
	}

	/** Facturacion **/
	public Cursor getAsistenteFacturacion(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT strftime('%m/%d/%Y',fecha),valor FROM esd_facturacion_impuesto WHERE substr(strftime('%Y', 'now','localtime'),3) || strftime('%m', 'now','localtime')=periodo ORDER BY fecha", new String [] {});
			return cursor;
		}
	}

	public Cursor getAsistenteFacturacion(String sector_id, String periodo){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT strftime('%m/%d/%Y',fecha),valor FROM esd_facturacion_impuesto WHERE sector_id=? AND periodo=? ORDER BY fecha", new String [] {sector_id,periodo});
			return cursor;
		}
	}
	
	/** Presupuesto Cartera **/
	public Cursor getAsistentePresupuestoCartera(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT periodo, cumplimiento, valor_presupuesto, valor_cobro, valor_ganado FROM esd_presupuesto_cartera ORDER BY periodo DESC LIMIT 1", new String [] {});
			return cursor;
		}
	}
	
	public Cursor getAsistentePresupuestoCarteraXFiltro(String sector,String periodo,boolean todos){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			if(todos){
				cursor=db.rawQuery("SELECT periodo, SUM(valor_cobro)/SUM(valor_presupuesto)*100, SUM(valor_presupuesto)/COUNT(), SUM(valor_cobro)/COUNT(), SUM(valor_ganado)/COUNT() FROM esd_presupuesto_cartera WHERE periodo=? ORDER BY periodo DESC", new String [] {periodo});
			}else{
				cursor=db.rawQuery("SELECT periodo, SUM(cumplimiento)/COUNT(), SUM(valor_presupuesto)/COUNT(), SUM(valor_cobro)/COUNT(), SUM(valor_ganado)/COUNT() FROM esd_presupuesto_cartera WHERE sector_id=? AND periodo=? ORDER BY periodo DESC", new String [] {sector,periodo});
			}
			return cursor;
		}
	}
	
	public Cursor getAsistentePresupuestoCarteraSectoreS(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT DISTINCT sector_id FROM esd_presupuesto_cartera", new String [] {});
			return cursor;
		}
	}

	public Cursor getAsistentePresupuestoCarteraPeriodos(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT DISTINCT periodo FROM esd_presupuesto_cartera ORDER BY periodo DESC", new String [] {});
			return cursor;
		}
	}

	/** Asistente General **/
	public Cursor getAsistenteGeneral(String sector_id){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT num_dias_venta, num_dias_trascurridos, porc_dias_transcurridos, incentivo_cartera, incentivo_venta, incentivo_clientes,  posicion FROM esd_asistente_general WHERE substr(strftime('%Y', 'now','localtime'),3) || strftime('%m', 'now','localtime')=periodo AND sector_id=?", new String [] {sector_id});
			return cursor;
		}
	}
	
	public Cursor getAsistenteGeneralByFilters(String sector, boolean todos){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			if(todos){
				cursor=db.rawQuery("SELECT num_dias_venta, num_dias_trascurridos, SUM(porc_dias_transcurridos)/COUNT(), SUM(incentivo_cartera), SUM(incentivo_venta), SUM(incentivo_clientes),  '' FROM esd_asistente_general WHERE substr(strftime('%Y', 'now','localtime'),3) || strftime('%m', 'now','localtime')=periodo", new String [] {});
			}else{
				cursor=db.rawQuery("SELECT num_dias_venta, num_dias_trascurridos, porc_dias_transcurridos, incentivo_cartera, incentivo_venta, incentivo_clientes,  posicion FROM esd_asistente_general WHERE substr(strftime('%Y', 'now','localtime'),3) || strftime('%m', 'now','localtime')=periodo AND sector_id=?", new String [] {sector});
			}
			return cursor;
		}
	}
	
	public Cursor getAsistenteGeneralSectoreS(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT DISTINCT sector_id FROM esd_asistente_general", new String [] {});
			return cursor;
		}
	}
	
	/** Presupuesto Articulos **/
	public Cursor getAsistentePresupuestosArticulos(boolean porVentas){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			if(porVentas){
				cursor=db.rawQuery("SELECT PA.producto_id,P.nombre,PA.valor_presupuesto,PA.valor_venta,PA.cumplimiento_moneda FROM esd_presupuesto_articulo AS PA JOIN esd_producto AS P ON P.producto_id=PA.producto_id WHERE  substr(strftime('%Y', 'now','localtime'),3) || strftime('%m', 'now','localtime')=periodo ORDER BY PA.producto_id", new String [] {});
			}else{
				cursor=db.rawQuery("SELECT PA.producto_id, P.nombre, PA.unidades_presupuesto, PA.unidades_venta, PA.cumplimiento_unidades FROM esd_presupuesto_articulo AS PA JOIN esd_producto AS P ON P.producto_id=PA.producto_id WHERE  substr(strftime('%Y', 'now','localtime'),3) || strftime('%m', 'now','localtime')=periodo ORDER BY PA.producto_id", new String [] {});
			}
			return cursor;
		}
	}
	
	public Cursor getSectoresAsistentePresupuestosArticulos(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT DISTINCT sector_id FROM esd_presupuesto_articulo", new String [] {});
			return cursor;
		}
	}
	
	public Cursor getPresupuestoArticulosSearchAuto(){
		synchronized (Lock) {
			open();
			Cursor cursor = null;
			cursor=db.rawQuery("SELECT DISTINCT pa.producto_id, nombre FROM esd_producto AS p JOIN esd_presupuesto_articulo AS pa WHERE p.producto_id=pa.producto_id", new String [] {});
			return 	cursor;
		}
	}

	
	public Cursor getAsistentePresupuestosArticulosXFiltro(boolean porVentas,String sector, String porcentaje, int tipoBusqueda, String buscar){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			String SQLResto="";
			String busqueda="%"+buscar+"%";
			switch(tipoBusqueda){
				case 0:
					SQLResto=" AND p.producto_id like ? ORDER BY p.producto_id";
					break;
				case 1:
					SQLResto=" AND p.codigo_primer_grado like ? ORDER BY p.producto_id";
					break;
				case 2:
					SQLResto=" AND p.codigo_segundo_grado like ? ORDER BY p.producto_id";
					break;
				case 3:
					SQLResto=" AND nombre like ? ORDER BY p.producto_id";
					break;
			}
			
			if(sector.equalsIgnoreCase("todos")){
				if(porcentaje.equalsIgnoreCase("todos")){
					if(porVentas){
						cursor=db.rawQuery("SELECT p.producto_id,nombre,valor_presupuesto,valor_venta,cumplimiento_moneda FROM esd_presupuesto_articulo AS A JOIN esd_producto AS P WHERE substr(strftime('%Y', 'now','localtime'),3) || strftime('%m', 'now','localtime')=periodo AND a.producto_id=p.producto_id "+SQLResto, new String [] {busqueda});
					}else{
						cursor=db.rawQuery("SELECT p.producto_id,nombre,unidades_presupuesto,unidades_venta,cumplimiento_unidades FROM esd_presupuesto_articulo AS A JOIN esd_producto AS P WHERE substr(strftime('%Y', 'now','localtime'),3) || strftime('%m', 'now','localtime')=periodo AND a.producto_id=p.producto_id "+SQLResto, new String [] {busqueda});
					}
				}else{
					if(porVentas){
						cursor=db.rawQuery("SELECT p.producto_id,nombre,valor_presupuesto,valor_venta,cumplimiento_moneda FROM esd_presupuesto_articulo AS A JOIN esd_producto AS P WHERE substr(strftime('%Y', 'now','localtime'),3) || strftime('%m', 'now','localtime')=periodo AND a.producto_id=p.producto_id AND cumplimiento_moneda BETWEEN 0 AND ? "+SQLResto, new String [] {porcentaje,busqueda});
					}else{
						cursor=db.rawQuery("SELECT p.producto_id,nombre,unidades_presupuesto,unidades_venta,cumplimiento_unidades FROM esd_presupuesto_articulo AS A JOIN esd_producto AS P WHERE substr(strftime('%Y', 'now','localtime'),3) || strftime('%m', 'now','localtime')=periodo AND a.producto_id=p.producto_id AND cumplimiento_unidades BETWEEN 0 AND ? "+SQLResto, new String [] {porcentaje,busqueda});
					}
				}
			}else{
				if(porcentaje.equalsIgnoreCase("todos")){
					if(porVentas){
						cursor=db.rawQuery("SELECT p.producto_id,nombre,valor_presupuesto,valor_venta,cumplimiento_moneda FROM esd_presupuesto_articulo AS A JOIN esd_producto AS P WHERE substr(strftime('%Y', 'now','localtime'),3) || strftime('%m', 'now','localtime')=periodo AND a.producto_id=p.producto_id AND a.sector_id=? "+SQLResto, new String [] {sector,busqueda});
					}else{
						cursor=db.rawQuery("SELECT p.producto_id,nombre,unidades_presupuesto,unidades_venta,cumplimiento_unidades FROM esd_presupuesto_articulo AS A JOIN esd_producto AS P WHERE substr(strftime('%Y', 'now','localtime'),3) || strftime('%m', 'now','localtime')=periodo AND a.producto_id=p.producto_id AND a.sector_id=?"+SQLResto, new String [] {sector,busqueda});
					}
				}else{
					if(porVentas){
						cursor=db.rawQuery("SELECT p.producto_id,nombre,valor_presupuesto,valor_venta,cumplimiento_moneda FROM esd_presupuesto_articulo AS A JOIN esd_producto AS P WHERE substr(strftime('%Y', 'now','localtime'),3) || strftime('%m', 'now','localtime')=periodo AND a.producto_id=p.producto_id AND a.sector_id=? AND cumplimiento_moneda BETWEEN 0 AND ? "+SQLResto, new String [] {sector,porcentaje,busqueda});
					}else{
						cursor=db.rawQuery("SELECT p.producto_id,nombre,unidades_presupuesto,unidades_venta,cumplimiento_unidades FROM esd_presupuesto_articulo AS A JOIN esd_producto AS P WHERE  substr(strftime('%Y', 'now','localtime'),3) || strftime('%m', 'now','localtime')=periodo AND a.producto_id=p.producto_id AND a.sector_id=? AND cumplimiento_unidades BETWEEN 0 AND ? "+SQLResto, new String [] {sector,porcentaje,busqueda});
					}
				}
			}
			return cursor;
		}
	}
	
	public Cursor getAsistentePresupuestosArticulosSectores(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT sector_id FROM esd_presupuesto_articulo", new String [] {});
			return cursor;
		}
	}
	
	public Cursor getAsistentePresupuestosArticulosPorcentajes(boolean tipo){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			if(tipo){
				cursor=db.rawQuery("SELECT DISTINCT cumplimiento_moneda FROM esd_presupuesto_articulo ORDER BY cumplimiento_moneda", new String [] {});
			}else{
				cursor=db.rawQuery("SELECT DISTINCT cumplimiento_unidades FROM esd_presupuesto_articulo ORDER BY cumplimiento_moneda", new String [] {});
			}
			return cursor;
		}
	}

	/** Acumulado de Venta **/
	public Cursor getAsistenteAcumuladoVentas(String month){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			String mes="%"+month;
			cursor=db.rawQuery("SELECT periodo,SUM(valor_presupuesto),SUM(valor_venta),SUM(cumplimiento)/COUNT(),posicion FROM esd_acumulado_venta WHERE periodo LIKE ?", new String [] {mes});
			return cursor;
		}
	}
	
	public Cursor getAsistenteAcumuladoVentas(String month,String sector, boolean todos){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			String mes="%"+month;
			if(todos){
				cursor=db.rawQuery("SELECT periodo,SUM(valor_presupuesto),SUM(valor_venta),SUM(cumplimiento)/COUNT(),posicion FROM esd_acumulado_venta WHERE periodo LIKE ?", new String [] {mes});
			}else{
				cursor=db.rawQuery("SELECT periodo,valor_presupuesto,valor_venta,cumplimiento/COUNT(),posicion FROM esd_acumulado_venta WHERE periodo LIKE ? AND sector_id=?", new String [] {mes,sector});
			}
			return cursor;
		}
	}

	public Cursor getAsistenteAcumuladoVentasSectores(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT DISTINCT sector_id FROM esd_acumulado_venta", new String [] {});
			return cursor;
		}
	}
	
	/** Efectividad de Visita **/

	public Cursor getAsistenteEfectividadVisitas(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT id," +
					"distrito_id," +
					"subdistrito_id," +
					"sector_id," +
					"ruta_id," +
					"periodo," +
					"strftime('%m/%d/%Y',fecha)," +
					"num_clientes_prog," +
					"num_clientes_vis," +
					"cump_efect_vis," +
					"num_clientes_ped," +
					"num_clientes_no_vis," +
					"num_clientes_no_venta," +
					"num_clientes_ruta," +
					"num_clientes_extraruta," +
					"cump_clientes_visitados FROM esd_efectividad_visita", new String [] {});
			return cursor;
		}
	}

	public Cursor getAsistenteEfectividadVisitas(String sector,String ruta, String periodo, boolean todos){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			if(todos){
				cursor=db.rawQuery("SELECT id," +
					"distrito_id," +
					"subdistrito_id," +
					"sector_id," +
					"ruta_id," +
					"periodo," +
					"strftime('%m/%d/%Y',fecha)," +
					"num_clientes_prog," +
					"num_clientes_vis," +
					"cump_efect_vis," +
					"num_clientes_ped," +
					"num_clientes_no_vis," +
					"num_clientes_no_venta," +
					"num_clientes_ruta," +
					"num_clientes_extraruta," +
					"cump_clientes_visitados FROM esd_efectividad_visita", new String [] {});
			}else{
				if(ruta.equalsIgnoreCase("todos")){
					Log.i("info","sector "+sector);
					Log.i("info","ruta "+ruta);
					Log.i("info","periodo "+periodo);

					if(periodo.equalsIgnoreCase("todos")){
						cursor=db.rawQuery("SELECT id," +
						"distrito_id," +
						"subdistrito_id," +
						"sector_id," +
						"ruta_id," +
						"periodo," +
						"strftime('%m/%d/%Y',fecha)," +
						"num_clientes_prog," +
						"num_clientes_vis," +
						"cump_efect_vis," +
						"num_clientes_ped," +
						"num_clientes_no_vis," +
						"num_clientes_no_venta," +
						"num_clientes_ruta," +
						"num_clientes_extraruta," +
						"cump_clientes_visitados FROM esd_efectividad_visita WHERE sector_id=?", new String [] {sector});
					}else{
						cursor=db.rawQuery("SELECT id," +
						"distrito_id," +
						"subdistrito_id," +
						"sector_id," +
						"ruta_id," +
						"periodo," +
						"strftime('%m/%d/%Y',fecha)," +
						"num_clientes_prog," +
						"num_clientes_vis," +
						"cump_efect_vis," +
						"num_clientes_ped," +
						"num_clientes_no_vis," +
						"num_clientes_no_venta," +
						"num_clientes_ruta," +
						"num_clientes_extraruta," +
						"cump_clientes_visitados FROM esd_efectividad_visita WHERE sector_id=? AND periodo=?", new String [] {sector,periodo});
					}
				}else{
					if(periodo.equalsIgnoreCase("todos")){
						cursor=db.rawQuery("SELECT id," +
						"distrito_id," +
						"subdistrito_id," +
						"sector_id," +
						"ruta_id," +
						"periodo," +
						"strftime('%m/%d/%Y',fecha)," +
						"num_clientes_prog," +
						"num_clientes_vis," +
						"cump_efect_vis," +
						"num_clientes_ped," +
						"num_clientes_no_vis," +
						"num_clientes_no_venta," +
						"num_clientes_ruta," +
						"num_clientes_extraruta," +
						"cump_clientes_visitados FROM esd_efectividad_visita WHERE ruta_id=?", new String [] {ruta});
					}else{
						cursor=db.rawQuery("SELECT id," +
						"distrito_id," +
						"subdistrito_id," +
						"sector_id," +
						"ruta_id," +
						"periodo," +
						"strftime('%m/%d/%Y',fecha)," +
						"num_clientes_prog," +
						"num_clientes_vis," +
						"cump_efect_vis," +
						"num_clientes_ped," +
						"num_clientes_no_vis," +
						"num_clientes_no_venta," +
						"num_clientes_ruta," +
						"num_clientes_extraruta," +
						"cump_clientes_visitados FROM esd_efectividad_visita WHERE ruta_id=? AND periodo=?", new String [] {ruta,periodo});
					}
				}
			}
			return cursor;
		}
	}

	public Cursor getAsistenteEfectividadVisitasSectores(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT DISTINCT sector_id FROM esd_efectividad_visita", new String [] {});
			return cursor;
		}
	}

	public Cursor getAsistenteEfectividadVisitasPeriodo(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT DISTINCT periodo FROM esd_efectividad_visita", new String [] {});
			return cursor;
		}
	}
	
	/*
	 * 
	 * PEDIDOS NEGADOS
	 * 
	 */
	
	public Cursor getPedidosNegados(boolean diario){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			if(diario){
				cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE date(fecha_pedido)=date('now','localtime')", new String [] {});
			}else{
				cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE strftime('%m',fecha_pedido)=strftime('%m','NOW') ", new String [] {});
			}
			return cursor;
		}
	}
		
	public Cursor getPedidosNegadosByFilters(boolean diario,String distrito,String subdistrito,String sector, String ruta,String motivo,boolean todos){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			if(distrito.equalsIgnoreCase("todos")){
				if(motivo.equalsIgnoreCase("todos")){
					if(diario){
						cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE date(fecha_pedido)=date('now','localtime')", new String [] {});
					}else{
						cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE strftime('%m',fecha_pedido)=strftime('%m','NOW')", new String [] {});
					}
				}else{
					if(diario){
						cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE date(fecha_pedido)=date('now','localtime') AND motivo_negacion_id=(SELECT motivo_negacion_id FROM esd_motivo_negacion WHERE descripcion=?)", new String [] {motivo});
					}else{
						cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE strftime('%m',fecha_pedido)=strftime('%m','NOW') AND motivo_negacion_id=(SELECT motivo_negacion_id FROM esd_motivo_negacion WHERE descripcion=?)", new String [] {motivo});
					}
				}
			}else{
				if (subdistrito.equalsIgnoreCase("todos")){
					if(motivo.equalsIgnoreCase("todos")){
						if(diario){
							cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE date(fecha_pedido)=date('now','localtime') AND distrito_id=(SELECT distrito_id FROM esd_distrito WHERE nombre_distrito=?)", new String [] {distrito});
						}else{
							cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE strftime('%m',fecha_pedido)=strftime('%m','NOW') AND distrito_id=(SELECT distrito_id FROM esd_distrito WHERE nombre_distrito=?)", new String [] {distrito});
						}
					}else{
						if(diario){
							cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE date(fecha_pedido)=date('now','localtime') AND motivo_negacion_id=(SELECT motivo_negacion_id FROM esd_motivo_negacion WHERE descripcion=? AND distrito_id=(SELECT distrito_id FROM esd_distrito WHERE nombre_distrito=?))", new String [] {motivo,distrito});
						}else{
							cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE strftime('%m',fecha_pedido)=strftime('%m','NOW') AND motivo_negacion_id=(SELECT motivo_negacion_id FROM esd_motivo_negacion WHERE descripcion=?)  AND distrito_id=(SELECT distrito_id FROM esd_distrito WHERE nombre_distrito=?)", new String [] {motivo,distrito});
						}
					}
				}else{
					if(sector.equalsIgnoreCase("todos")){
						if(motivo.equalsIgnoreCase("todos")){
							if(diario){
								cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE date(fecha_pedido)=date('now','localtime') AND subdistrito_id=(SELECT subdistrito_id FROM esd_subdistrito WHERE nombre_subdistrito=?)", new String [] {subdistrito});
							}else{
								cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE strftime('%m',fecha_pedido)=strftime('%m','NOW') AND subdistrito_id=(SELECT subdistrito_id FROM esd_subdistrito WHERE nombre_subdistrito=?)", new String [] {subdistrito});
							}
						}else{
							if(diario){
								cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE date(fecha_pedido)=date('now','localtime') AND motivo_negacion_id=(SELECT motivo_negacion_id FROM esd_motivo_negacion WHERE descripcion=? AND subdistrito_id=(SELECT subdistrito_id FROM esd_subdistrito WHERE nombre_subdistrito=?)", new String [] {motivo,subdistrito});
							}else{
								cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE strftime('%m',fecha_pedido)=strftime('%m','NOW') AND motivo_negacion_id=(SELECT motivo_negacion_id FROM esd_motivo_negacion WHERE descripcion=?)  AND subdistrito_id=(SELECT subdistrito_id FROM esd_subdistrito WHERE nombre_subdistrito=?)", new String [] {motivo,subdistrito});
							}
						}
					}else{
						if(ruta.equalsIgnoreCase("todos")){
							if(motivo.equalsIgnoreCase("todos")){
								if(diario){
									cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE date(fecha_pedido)=date('now','localtime') AND sector_id=?", new String [] {sector});
								}else{
									cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE strftime('%m',fecha_pedido)=strftime('%m','NOW') AND sector_id=?", new String [] {sector});
								}
							}else{
								if(diario){
									cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE date(fecha_pedido)=date('now','localtime') AND motivo_negacion_id=(SELECT motivo_negacion_id FROM esd_motivo_negacion WHERE descripcion=? AND sector_id=?)", new String [] {motivo,sector});
								}else{
									cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE strftime('%m',fecha_pedido)=strftime('%m','NOW') AND motivo_negacion_id=(SELECT motivo_negacion_id FROM esd_motivo_negacion WHERE descripcion=?) AND sector_id=?", new String [] {motivo,sector});
								}
							}
						}else{
							if(motivo.equalsIgnoreCase("todos")){
								if(diario){
									cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE date(fecha_pedido)=date('now','localtime') AND ruta_id=?", new String [] {ruta});
								}else{
									cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE strftime('%m',fecha_pedido)=strftime('%m','NOW') AND ruta_id=?", new String [] {ruta});
								}
							}else{
								if(diario){
									cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE date(fecha_pedido)=date('now','localtime') AND motivo_negacion_id=(SELECT motivo_negacion_id FROM esd_motivo_negacion WHERE descripcion=? AND ruta_id=?)", new String [] {motivo,ruta});
								}else{
									cursor=db.rawQuery("SELECT id, pedido_id, cliente_id, (SELECT nombre_cliente FROM esd_cliente WHERE cliente_id=esd_pedido_negado.cliente_id) , total_pedido FROM esd_pedido_negado WHERE strftime('%m',fecha_pedido)=strftime('%m','NOW') AND motivo_negacion_id=(SELECT motivo_negacion_id FROM esd_motivo_negacion WHERE descripcion=?) AND ruta_id=?", new String [] {motivo,ruta});
								}
							}
						}
					}	
				}
			}
			return cursor;
		}
	}

	public Cursor GetDistritosPedidosNegados(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT DISTINCT (SELECT nombre_distrito FROM esd_distrito WHERE distrito_id=esd_pedido_negado.distrito_id) FROM esd_pedido_negado", new String [] {});
			return cursor;
		}
	}
	
	public Cursor GetRutasPedidosNegados(String sector_id){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT ruta_id FROM esd_ruta WHERE sector_id=?", new String [] {sector_id});
			return cursor;
		}
	}	
	
	public Cursor GetPedidosNegadosMotivos(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT descripcion FROM esd_motivo_negacion", new String [] {});
			return cursor;
		}
	}	
	
	public Cursor getPedidosNegadosDetalles(String pedido_id){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT c.cliente_id || ' ' || c.nombre_cliente,pn.pedido_id,pn.sector_id,pn.ruta_id,mn.descripcion,strftime('%m/%d/%Y', fecha_pedido),strftime('%m/%d/%Y', fecha_negacion),total_pedido FROM (esd_pedido_negado AS pn JOIN esd_cliente AS c ON c.cliente_id=pn.cliente_id) LEFT OUTER JOIN esd_motivo_negacion AS mn ON mn.motivo_negacion_id=pn.motivo_negacion_id WHERE pn.pedido_id=?", new String [] {pedido_id});
			return cursor;
		}
	}
	
	/*
	 * 
	 * COBROS
	 * 
	 */

	public boolean hayCobrosEnElDia(){
		synchronized (Lock) {
			open();
			boolean res=false;
			Cursor cursor=db.rawQuery("SELECT * FROM esd_cobro WHERE date(fecha_cobro)=date('now','localtime')", new String [] {});
			if(cursor.getCount()>0){
				res=true;
			}
			return res;
		}
		
	}

	public boolean hayCobrosEnElDia(String cliente_id){
		synchronized (Lock) {
			open();
			boolean res=false;
			Cursor cursor=db.rawQuery("SELECT * FROM esd_cobro WHERE date(fecha_cobro)=date('now','localtime') AND cliente_id=?", new String [] {cliente_id});
			if(cursor.getCount()>0){
				res=true;
			}
			return res;
		}
		
	}
	
	public Cursor getListasDeCobrosDelDia(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT cobro_id FROM esd_cobro WHERE date(fecha_cobro)=date('now','localtime')", new String [] {});
			return cursor;
		}
		
	}


	public Cursor getListasDeCobrosDelDia(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT cobro_id FROM esd_cobro WHERE date(fecha_cobro)=date('now','localtime') AND cliente_id=?", new String [] {cliente_id});
			return cursor;
		}
		
	}


	public Cursor getListasDePagos(String cobro_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT cobro_id, descripcion, valor_cruzado FROM esd_cobro_cruce_pago AS P JOIN esd_tipo_pago AS TP ON P.tipo_documento=TP.tipo_pago_id WHERE cobro_id=?", new String [] {cobro_id});
			return cursor;
		}
		
	}

	public Cursor getCobrosSincronizadosPago(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT " +
					"c.cobro_id, " +
					"strftime('%m/%d/%Y'," +
					"c.fecha_cobro), " +
					"time(c.fecha_cobro)," +
					"(SELECT SUM(valor_cruzado) FROM esu_cobro_cruce_pago WHERE cobro_id=c.cobro_id)," +
					"(SELECT SUM(valor_cruzado) FROM esu_cobro_cruce_consignacion WHERE cobro_id=c.cobro_id)," +
					"(SELECT SUM(valor_cruzado) FROM esu_cobro_cruce_docs_neg WHERE cobro_id=c.cobro_id)," +
					"c.motivo_anulacion_id, " +
					"uc.cobro_id " +
					"FROM esd_cobro AS c LEFT OUTER JOIN esu_cobro AS uc ON c.cobro_id=uc.cobro_id WHERE c.cliente_id=?", new String [] {cliente_id});
			return cursor;
		}
		
	}
	
	public Cursor getCobrosDelDiaGrilla(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT c.cobro_id, c.cliente_id, nombre_cliente, time(c.fecha_cobro)," +
				"(SELECT SUM(valor_cruzado) FROM esd_cobro_cruce_pago WHERE cobro_id=c.cobro_id)," +
				"(SELECT SUM(valor_cruzado) FROM esd_cobro_cruce_consignacion WHERE cobro_id=c.cobro_id)," +
				"(SELECT SUM(valor_cruzado) FROM esd_cobro_cruce_docs_neg WHERE cobro_id=c.cobro_id)," +
				"c.motivo_anulacion_id, uc.cobro_id " +
				"FROM (esd_cobro AS c JOIN esd_cliente AS ci ON c.cliente_id=ci.cliente_id) LEFT OUTER JOIN esu_cobro AS uc ON c.cobro_id=uc.cobro_id WHERE c.cliente_id=? AND date(c.fecha_cobro)=date('now','localtime')", new String [] {cliente_id});
			return cursor;
		}
		
	}
	
	public Cursor getCobrosGrilla(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT c.cobro_id, c.cliente_id, nombre_cliente, time(c.fecha_cobro)," +
				"(SELECT SUM(valor_cruzado) FROM esd_cobro_cruce_pago WHERE cobro_id=c.cobro_id)," +
				"(SELECT SUM(valor_cruzado) FROM esd_cobro_cruce_consignacion WHERE cobro_id=c.cobro_id)," +
				"(SELECT SUM(valor_cruzado) FROM esd_cobro_cruce_docs_neg WHERE cobro_id=c.cobro_id)," +
				"c.motivo_anulacion_id, uc.cobro_id " +
				"FROM (esd_cobro AS c JOIN esd_cliente AS ci ON c.cliente_id=ci.cliente_id) LEFT OUTER JOIN esu_cobro AS uc ON c.cobro_id=uc.cobro_id", new String [] {});
			return cursor;
		}
		
	}

/*	public Cursor getCobrosSincronizadosConsignaciones(String cliente_id){
		synchronized (Lock) {
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT nombre_cliente, strftime('%m/%d/%Y',c.fecha_cobro), 	cd.documento_id,cd.saldo_anterior,(SELECT descripcion FROM esd_tipo_pago WHERE tipo_pago_id=cp.tipo_documento )," +
				"cp.valor_cruzado, c.motivo_anulacion_id, uc.cobro_id FROM (((esd_cobro AS c JOIN esd_cliente AS cl ON c.cliente_id=cl.cliente_id) JOIN esd_cobro_documento AS cd ON c.cobro_id=cd.cobro_id) " +
				"JOIN esu_cobro_cruce_consignacion AS cp ON cd.cobro_id=cp.cobro_id AND cd.documento_id=cp.documento_id ) LEFT OUTER JOIN esu_cobro AS uc ON c.cobro_id=uc.cobro_id WHERE c.cliente_id=?", new String [] {cliente_id});
			return cursor;
		}
		
	}

	public Cursor getCobrosSincronizadosDocsNega(String cliente_id){
		synchronized (Lock) {
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT nombre_cliente, strftime('%m/%d/%Y',c.fecha_cobro), 	cd.documento_id,cd.saldo_anterior,(SELECT descripcion FROM esd_tipo_pago WHERE tipo_pago_id=cp.tipo_documento )," +
				"cp.valor_cruzado, c.motivo_anulacion_id, uc.cobro_id FROM (((esd_cobro AS c JOIN esd_cliente AS cl ON c.cliente_id=cl.cliente_id) JOIN esd_cobro_documento AS cd ON c.cobro_id=cd.cobro_id) " +
				"JOIN esu_cobro_cruce_docs_neg AS cp ON cd.cobro_id=cp.cobro_id AND cd.documento_id=cp.documento_id ) LEFT OUTER JOIN esu_cobro AS uc ON c.cobro_id=uc.cobro_id WHERE c.cliente_id=?", new String [] {cliente_id});
			return cursor;
		}
		
	}*/

	
	public Cursor getCobrosDiaResumenMediosPago(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT (SELECT SUM(valor_cruzado) FROM esd_cobro_cruce_pago WHERE tipo_documento='01' AND cobro_id=c.cobro_id)," +
					"(SELECT SUM(valor_cruzado) FROM esd_cobro_cruce_pago WHERE tipo_documento='02' OR tipo_documento='08'  AND cobro_id=c.cobro_id)," +
					"(SELECT SUM(valor_cruzado) FROM esd_cobro_cruce_pago WHERE tipo_documento='07' AND cobro_id=c.cobro_id)," +
					"(SELECT SUM(valor_cruzado) FROM esd_cobro_cruce_consignacion WHERE cobro_id=c.cobro_id)," +
					"(SELECT SUM(valor_cruzado) FROM esd_cobro_cruce_pago WHERE (tipo_documento='04' OR tipo_documento='05') AND cobro_id=c.cobro_id) " +
					"FROM esd_cobro AS c WHERE motivo_anulacion_id='' AND cliente_id=?", new String [] {cliente_id});
			return cursor;
		}
		
	}
	
	
	public Cursor getMotivosAnulacionCobros(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT * FROM esd_cobro_motivo_anulacion ORDER BY descripcion", new String [] {});
			return cursor;
		}
		
	}
	
	public Cursor getMotivosAbonos(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT * FROM esd_motivo_abono ORDER BY descripcion", new String [] {});
			return cursor;
		}
		
	}
	
	public String distritoCapturarGuiaAbono(String cliente_id){
		synchronized (Lock) {
			open();
			String res="";
			Cursor cursor=db.rawQuery("SELECT capturar_guia_abono FROM esd_distrito WHERE distrito_id=(SELECT distrito_id FROM esd_cliente WHERE cliente_id=?) ", new String [] {cliente_id});
			if(cursor.moveToFirst()){
				res=cursor.getString(0);
			}
			if(cursor!=null){
				cursor.close();
			}
			return res;
			
		}
	}

	public Cursor getCobrosParaPagar(ArrayList<String> carteras_id){
		synchronized (Lock) {
			open();
			String sql="SELECT documento_id, descripcion ,strftime('%m/%d/%Y', fecha_documento), strftime('%m/%d/%Y',fecha_vencimiento),julianday('now','localtime') - julianday(fecha_vencimiento),saldo_pendiente, T.cartera_tipo_documento_id, strftime('%m/%d/%Y', fecha_documento), valor_pronto_pago,bandera_saldo FROM esd_cartera_documento AS C JOIN esd_cartera_tipo_documento AS T ON C.tipo_documento=T.cartera_tipo_documento_id WHERE ";
			int tam=carteras_id.size();
			int i= 0;
			for(String val : carteras_id){
				if(i!=tam-1){
					sql=sql+" documento_id='"+val+"' OR";
				}else{
					sql=sql+" documento_id='"+val+"' ";
				}
				i++;
			}
			Cursor cursor=db.rawQuery(sql, new String [] {});
			return cursor;
		}
	}
	
	public int getCobrosNumeroCobro(){
		synchronized (Lock) {
			open();
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=11) WHERE id=11";
			int res=0;
			Cursor cursor=db.rawQuery("SELECT valor FROM esd_rango WHERE id=11 ", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
				try{
					db.execSQL(sqlUpdateRango);
				}catch(Exception e){
					Log.e("info", "Error getPagoNumeroPago "+e);
				}
			}
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}

			return res;
		}		
	}
	
	public String getDefault_desc_pronto_pago(){
		synchronized (Lock) {
			open();
			String res="";
			Cursor cursor=db.rawQuery("SELECT default_desc_pronto_pago FROM esd_parametro_general ", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getString(0);
			}
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
			return res;
		}		
	}
	
	public Cursor getTiposPago(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT * FROM esd_tipo_pago", new String [] {});
			return cursor;
		}		
	}
	
	public Cursor getTipoPago(String tipo_pago_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT * FROM esd_tipo_pago WHERE tipo_pago_id=?", new String [] {tipo_pago_id});
			return cursor;
		}		
	}

	public Cursor getEsdParametroGeneral(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT * FROM esd_parametro_general", new String [] {});
			return cursor;
		}		
	}

	public String getEsdParametroGeneralCodimotdSinIdentificar(){
		synchronized (Lock) {
			open();
			String res="";
			Cursor cursor=db.rawQuery("SELECT codimotd_sin_identificar FROM esd_parametro_general", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getString(0);
			}
			return res;
		}		
	}
	
	public Cursor getEsdParametroDiferencia(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT * FROM esd_parametro_diferencia", new String [] {});
			return cursor;
		}		
	}
	
	public String getFormaPagoIdDiferencia(){
		synchronized (Lock) {
			open();
			String res="";
			Cursor cursor=db.rawQuery("SELECT  forma_pago_dife_id FROM esd_parametro_general", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getString(0);
			}
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
			return res;
		}		
	}
	
	public Cursor getEsdParaCmotd(String es_abono,
			String es_efectivo,
			String es_cheque,
			String es_posfechado,
			String es_doc_negativo,
			String per_chq_igual_per_actual,
			String tipo_documento){
		synchronized (Lock) {
			open();
			String sql="SELECT * FROM esd_para_cmotd WHERE es_abono='"+es_abono+"'";
			if(!es_efectivo.equalsIgnoreCase("")){
				sql=sql+" AND es_efectivo='"+es_efectivo+"'";
			}
			
			if(!es_cheque.equalsIgnoreCase("")){
				sql=sql+" AND es_cheque='"+es_cheque+"'";
				sql=sql+" AND es_posfechado='"+es_posfechado+"'";
			}

			if(!per_chq_igual_per_actual.equalsIgnoreCase("")){
				sql=sql+" AND per_chq_igual_per_actual='"+per_chq_igual_per_actual+"'";
			}
			
			if(!es_doc_negativo.equalsIgnoreCase("")){
				sql=sql+" AND es_doc_negativo='"+es_doc_negativo+"'";
			}
			
			if(!tipo_documento.equalsIgnoreCase("")){
				sql=sql+" AND tipo_documento='"+tipo_documento+"'";
			}else{
				sql=sql+" AND tipo_documento is null";
			}
			Log.i("info",sql);
			Cursor cursor=db.rawQuery(sql, new String [] {});
			return cursor;
		}		
	}
	
	public int getPagoNumeroPago(){
		synchronized (Lock) {
			open();
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=12) WHERE id=12";
			int res=0;
			Cursor cursor=db.rawQuery("SELECT valor FROM esd_rango WHERE id=12 ", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
				try{
					db.execSQL(sqlUpdateRango);
				}catch(Exception e){
					Log.e("info", "Error getPagoNumeroPago "+e);
				}
			}
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
			return res;
		}		
	}
	
	public int getPagoNumeroConsignacionDetalle(){
		synchronized (Lock) {
			open();
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=13) WHERE id=13";
			int res=0;
			Cursor cursor=db.rawQuery("SELECT valor FROM esd_rango WHERE id=13 ", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
				try{
					db.execSQL(sqlUpdateRango);
				}catch(Exception e){
					Log.e("info", "Error getPagoNumeroPago "+e);
				}
			}
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
			return res;
		}		
	}

	public int getPagoNumeroRelacion(){
		synchronized (Lock) {
			open();
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=14) WHERE id=14";
			int res=0; 
			Cursor cursor=db.rawQuery("SELECT SUBSTR(valor,length(valor)-3) FROM esd_rango WHERE id=14 ", new String [] {});
//			Cursor cursor=db.rawQuery("SELECT valor FROM esd_rango WHERE id=14 ", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
				try{
					db.execSQL(sqlUpdateRango);
				}catch(Exception e){
					Log.e("info", "Error getPagoNumeroPago "+e);
				}
			}
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
			return res;
		}		
	}

	public int getConsecutivoRegistroDoc(){
		synchronized (Lock) {
			open();
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=15) WHERE id=15";
			int res=0;
			Cursor cursor=db.rawQuery("SELECT valor FROM esd_rango WHERE id=15 ", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
				try{
					db.execSQL(sqlUpdateRango);
				}catch(Exception e){
					Log.e("info", "Error getPagoNumeroPago "+e);
				}
			}
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
			return res;
		}		
	}

	public int getConsecutivoRegistroCrucePagos(){
		synchronized (Lock) {
			open();
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=16) WHERE id=16";
			int res=0;
			Cursor cursor=db.rawQuery("SELECT valor FROM esd_rango WHERE id=16 ", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
				try{
					db.execSQL(sqlUpdateRango);
				}catch(Exception e){
					Log.e("info", "Error getPagoNumeroPago "+e);
				}
			}
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
			return res;
		}		
	}
	
	public int getConsecutivoRegistroCruceConsig(){
		synchronized (Lock) {
			open();
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=17) WHERE id=17";
			int res=0;
			Cursor cursor=db.rawQuery("SELECT valor FROM esd_rango WHERE id=17 ", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
				try{
					db.execSQL(sqlUpdateRango);
				}catch(Exception e){
					Log.e("info", "Error getPagoNumeroPago "+e);
				}
			}
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
			return res;
		}		
	}

	public int getConsecutivoRegistroDiferenciaCobros(){
		synchronized (Lock) {
			open();
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=19) WHERE id=19";
			int res=0;
			Cursor cursor=db.rawQuery("SELECT valor FROM esd_rango WHERE id=19", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
				try{
					db.execSQL(sqlUpdateRango);
				}catch(Exception e){
					Log.e("info", "Error getPagoNumeroPago "+e);
				}
			}
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}

			return res;
		}		
	}
	
	public String getTipoPagoIdDocsNeg(){
		synchronized (Lock) {
			String res="";
			Cursor cursor=db.rawQuery("SELECT tipo_pago_id FROM esd_tipo_pago WHERE es_nota_credito='S'", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getString(0);
			}
			if(cursor!=null){
				cursor.close();
			}
			return res;
		}
	}
		
	public int getConsecutivoRegistroCruceDocsNeg(){
		synchronized (Lock) {
			open();
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=20) WHERE id=20";
			int res=0;
			Cursor cursor=db.rawQuery("SELECT valor FROM esd_rango WHERE id=20 ", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
				try{
					db.execSQL(sqlUpdateRango);
				}catch(Exception e){
					Log.e("info", "Error getPagoNumeroPago "+e);
				}
			}
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}

			return res;
		}		
	}
	
	public int getConsecutivoRegistroEsuCobroObsDesnormalizado(){
		synchronized (Lock) {
			open();
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=21) WHERE id=21";
			int res=0;
			Cursor cursor=db.rawQuery("SELECT valor FROM esd_rango WHERE id=21 ", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
				try{
					db.execSQL(sqlUpdateRango);
				}catch(Exception e){
					Log.e("info", "Error getPagoNumeroPago "+e);
				}
			}
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
			return res;
		}		
	}
	
	
	
	public Transaccion_SQL insertQuerys(ArrayList<String> querys){
		synchronized (Lock) {
			open();
			Transaccion_SQL res=new Transaccion_SQL();
			for(String sql:querys){
//				Log.i("info",sql+";");
				try{
					db.execSQL(sql);
				}catch(Exception e){
					Log.e("info","insertQuerys "+e);
					Log.e("info","insertQuerys "+sql);
					res.setEstado("ERROR");
					res.setSentenciaSQL(sql);
					res.setErrorId("");
					res.setErrorMsj(e.toString());
					return res;
				}
			}
			res.setEstado("OK");
			res.setSentenciaSQL("");
			res.setErrorId("");
			res.setErrorMsj("");
			return res;
		}		
	}
	
	public float getTotalCobro(String cobro_id){
		synchronized (Lock) {
			open();
			float res=0;
			Cursor cursor=db.rawQuery("SELECT valor_documento FROM esu_cobro_pago WHERE cobro_id=?", new String [] {cobro_id});
			if(cursor.moveToFirst()){
				res=cursor.getFloat(0);
			}
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
			return res;
		}		
	}
	
	public Cursor getBancosTransferencia(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT DISTINCT banco_id,nombre_banco,cuenta FROM esd_banco_cuenta ORDER BY nombre_banco", new String [] {});
			return cursor;
		}		
	}

	public Cursor getBancos(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT entidad_id,nombre_entidad FROM esd_entidad_financiera ORDER BY nombre_entidad", new String [] {});
			return cursor;
		}		
	}

	public boolean exitsTipoPagoOrden(){
		synchronized (Lock) {
			open();
			boolean res=false;
			Cursor cursor=db.rawQuery("SELECT COUNT(*) FROM esd_tipo_pago_orden", new String [] {});
			if(cursor.moveToFirst()){
				if(cursor.getInt(0)>0){
					res=true;
				}
			}
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
			return res;
		}
	}
	
	
	
	public String getClienteManejaDifMenorValor(String cliente_id){
		synchronized (Lock) {
			open();
			String res="";
			Cursor cursor=db.rawQuery("SELECT maneja_dif_menor_valor FROM esd_cliente WHERE cliente_id=?", new String [] {cliente_id});
			if(cursor.moveToFirst()){
				res=cursor.getString(0);
			}
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
			return res;
		}
	}

	public float getValorMaxDiferencia(){
		synchronized (Lock) {
			open();
			float res=0;
			Cursor cursor=db.rawQuery("SELECT valor_max_diferencia FROM esd_parametro_general", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getFloat(0);
			}
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}

			return res;
		}
	}

	public String getFormaPagoId(String tipo_pago,String subtipo_pago){
		synchronized (Lock) {
			open();
			String res="";
			Cursor cursor=db.rawQuery("SELECT forma_pago_id FROM esd_tipo_pago_orden WHERE tipo_pago_id=? AND subtipo_pago_id=?", new String [] {tipo_pago,subtipo_pago});
			if(cursor.moveToFirst()){
				res=cursor.getString(0);
			}
			if(cursor!=null && !cursor.isClosed()){
				cursor.close();
			}
			return res;
		}
	}

	public Cursor getTipoPagoOrden(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT * FROM esd_tipo_pago_orden ORDER BY orden ASC", new String [] {});
			return cursor;
		}
	}

	public Cursor getEsuCobrosDocumentos(String cobro_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT * FROM esu_cobro_documento WHERE cobro_id=?", new String [] {cobro_id});
			return cursor;
		}
	}

	public Cursor getEsuCobrosDocumentos(String cobro_id, String tipo_documento, String documento_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT * FROM esu_cobro_documento WHERE cobro_id=? AND tipo_documento=? AND documento_id=?", new String [] {cobro_id,tipo_documento,documento_id});
			return cursor;
		}
	}

	public Cursor getEsuCobroCrucePago(String cobro_id,String doc_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT * FROM esu_cobro_cruce_pago WHERE cobro_id=? AND documento_id=? ORDER BY id DESC", new String [] {cobro_id,doc_id});
			return cursor;
		}
	}
	
	public Cursor getEsuCobroDocumento(String cobro_id,String tipoDocId, String docId){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT * FROM esu_cobro_documento WHERE cobro_id=? AND tipo_documento=? AND documento_id=?", new String [] {cobro_id,tipoDocId,docId});
			return cursor;
		}
	}
	
	public Cursor getEsuCobroPagoDet(String cobro_id,String consecutivo_pago,String consecutivo_detalle){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT * FROM esu_cobro_pago_det WHERE cobro_id=? AND consecutivo_pago=? AND consecutivo_detalle=?", new String [] {cobro_id,consecutivo_pago,consecutivo_detalle});
			return cursor;
		}
	}
	
	public Cursor getEsuCobroCruceConsignacion(String cobro_id,String doc_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT * FROM esu_cobro_cruce_consignacion WHERE cobro_id=? AND documento_id=? ORDER BY id DESC", new String [] {cobro_id,doc_id});
			return cursor;
		}
	}

	public Cursor getEsuCobroUnionCrucePagoCruceConsignacion(String cobro_id,String doc_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT Cod_mov_tipo_doc,Numero_relacion,fecha_registro FROM esu_cobro_cruce_pago WHERE cobro_id=? AND documento_id=? " +
				"UNION SELECT Cod_mov_tipo_doc,Numero_relacion,fecha_registro  FROM esu_cobro_cruce_consignacion WHERE cobro_id=? AND documento_id=? ORDER BY fecha_registro DESC", 
				new String [] {cobro_id,doc_id,cobro_id,doc_id});
			return cursor;
		}
	}

	public Cursor getEsuCobro(String cobro_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT * FROM esu_cobro WHERE cobro_id=?", new String [] {cobro_id});
			return cursor;
		}		
	}
	
	public Cursor getEsuCobroPago(String consecutivo_pago){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT * FROM esu_cobro_pago WHERE consecutivo_pago=?", new String [] {consecutivo_pago});
			return cursor;
		}		
	}

	public Cursor getEsuCobroCruceDocsNegSUMValorCru(String cobro_id,String tipo_doc, String doc_neg_id){
		synchronized (Lock) {
			open();
//			Cursor cursor=db.rawQuery("SELECT SUM(valor_cruzado) FROM esu_cobro_cruce_docs_neg WHERE cobro_id=? AND tipo_documento_neg=? AND documento_id_neg=?", new String [] {cobro_id,tipo_doc,doc_neg_id});
			Cursor cursor=db.rawQuery("SELECT SUM(valor_cruzado) FROM esu_cobro_cruce_docs_neg WHERE cobro_id=? AND tipo_documento=? AND documento_id=?", new String [] {cobro_id,tipo_doc,doc_neg_id});
			return cursor;
		}		
	}

	public Cursor getEsuCobroDiferencia(String cobro_id,String tipo_doc, String doc_id, String idenPago, String idenPagoDet, String difApli){
		synchronized (Lock) {
			open();
			String sql="SELECT * FROM esu_cobro_diferencia " +
					"WHERE cobro_id='"+cobro_id+"' " +
					"AND tipo_documento='"+tipo_doc+"' " +
					"AND documento_id='"+doc_id+"' " +
					"AND identificador_pago='"+idenPago+"' " +
					"AND identificador_pago_det='"+idenPagoDet+"' " +
					"AND dif_aplicada='"+difApli+"'";
			Cursor cursor=db.rawQuery(sql, new String [] {});
			Log.i("info","getEsuCobroDiferencia sql "+sql);
			//			Cursor cursor=db.rawQuery("SELECT * FROM esu_cobro_diferencia WHERE cobro_id=? AND tipo_documento=? AND documento_id=? AND identificador_pago=? AND identificador_pago_det=? AND dif_aplicada=?", new String [] {cobro_id,tipo_doc,doc_id,idenPago,idenPagoDet,difApli});
			return cursor;
		}		
	}

	public Cursor getEsuCobroObsDesnormalizado(String cobro_id, String cod_mov_tipo_doc, String tipo_documento, String documento_id, String tipo_observacion){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT * FROM esu_cobro_obs_desnormalizado WHERE cobro_id=? AND cod_mov_tipo_doc=? AND tipo_documento=? AND documento_id=? AND tipo_observacion=?", new String [] {cobro_id,cod_mov_tipo_doc,tipo_documento,documento_id,tipo_observacion});
			return cursor;
		}		
	}
	
	public Cursor getEsuCobroCruceDocsNeg(String cobro_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT * FROM esu_cobro_cruce_docs_neg WHERE cobro_id=?", new String [] {cobro_id});
			return cursor;
		}		
	}
	
	public Cursor getEsuCobroDiferencia(String cobro_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT * FROM esu_cobro_diferencia WHERE cobro_id=?", new String [] {cobro_id});
			return cursor;
		}		
	}

	public Cursor getCobrosDelDia(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT cd.cobro_id, documento_id,saldo_anterior,(SELECT SUM(valor_cruzado) FROM esd_cobro_cruce_pago WHERE cobro_id=cd.cobro_id),(SELECT SUM(valor_cruzado) FROM esd_cobro_cruce_consignacion WHERE cobro_id=cd.cobro_id),(SELECT SUM(valor_diferencia) FROM esd_cobro_diferencia WHERE (cobro_id=cd.cobro_id)),cu.cobro_id, c.motivo_anulacion_id  FROM (esd_cobro_documento AS cd JOIN esd_cobro AS c ON cd.cobro_id=c.cobro_id ) LEFT OUTER JOIN esu_cobro AS cu ON c.cobro_id=cu.cobro_id WHERE c.cliente_id=?", new String [] {cliente_id});
			return cursor;
		}		
	}

	public Cursor getDocsXCobro(String cobro_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT documento_id, descripcion, saldo_anterior," +
					"(SELECT SUM(valor_cruzado) FROM esd_cobro_cruce_pago WHERE cobro_id=cd.cobro_id AND documento_id=cd.documento_id)," +
					"(SELECT SUM(valor_cruzado) FROM esd_cobro_cruce_consignacion WHERE cobro_id=cd.cobro_id AND documento_id=cd.documento_id)," +
					"(SELECT SUM(valor_cruzado) FROM esd_cobro_cruce_docs_neg WHERE cobro_id=cd.cobro_id AND documento_id=cd.documento_id)" +
					//"--,(SELECT SUM(valor_diferencia) FROM esd_cobro_diferencia WHERE (cobro_id=cd.cobro_id AND documento_id=cd.documento_id)) " +
					" FROM esd_cobro_documento AS cd JOIN esd_cartera_tipo_documento AS ct ON cd.tipo_documento=ct.cartera_tipo_documento_id AND cd.cobro_id=?", new String [] {cobro_id});
			return cursor;
		}		
	}

	public Cursor getPagosXDocIdCrucePago(String cobro_id, String doc_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT forma_pago_id, valor_cruzado FROM esd_cobro_cruce_pago WHERE cobro_id=? AND documento_id=?", new String [] {cobro_id,doc_id});
			return cursor;
		}		
	}

	public Cursor getPagosXDocIdCruceConsignaciones(String cobro_id, String doc_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT forma_pago_id, valor_cruzado  FROM esd_cobro_cruce_consignacion WHERE cobro_id=? AND documento_id=?", new String [] {cobro_id,doc_id});
			return cursor;
		}		
	}

	public Cursor getPagosXDocIdCruceDocsNeg(String cobro_id, String doc_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT forma_pago_id, valor_cruzado  FROM esd_cobro_cruce_docs_neg WHERE cobro_id=? AND documento_id=?", new String [] {cobro_id,doc_id});
			return cursor;
		}		
	}
	
	public boolean updateEsuCobroDifDifApli(String cobro_id,String Dif_aplicada){
		synchronized (Lock) {
			open();
			Log.i("info","updateEsuCobroDifDifApli 0");
			boolean res=true;
			Log.i("info","updateEsuCobroDifDifApli 1");
			String sqlUpdate="UPDATE esu_cobro_diferencia SET dif_aplicada='"+Dif_aplicada+"' WHERE cobro_id='"+cobro_id+"'";
			Log.i("info","updateEsuCobroDifDifApli 2");
			try{
				Log.i("info","updateEsuCobroDifDifApli 3");
				db.execSQL(sqlUpdate);
				Log.i("info","updateEsuCobroDifDifApli 4");
			}catch(Exception e){
				Log.i("info","updateEsuCobroDifDifApli 5");
				Log.e("info","updateEsuCobroDifDifApli error "+e);
				Log.i("info","updateEsuCobroDifDifApli 6");
				res=false;
				Log.i("info","updateEsuCobroDifDifApli 7");
			}
			Log.i("info","updateEsuCobroDifDifApli 8");
			return res;
		}
	}

	public double getCobrosClientesPorFecha(String cliente_id, String fecha){
		synchronized (Lock) {
			open();
			double res=0;
			Log.i("info", "cliente_id "+cliente_id);
			Log.i("info", "fecha "+fecha);
			Cursor cursor=db.rawQuery("SELECT SUM(CC.valor_cruzado) FROM (esd_cobro AS  C JOIN esd_cobro_cruce_consignacion AS CC ON C.cobro_id=CC.cobro_id) WHERE C.cliente_id=? AND DATE(fecha_cobro)=DATE(?)", new String [] {cliente_id,fecha});
			if(cursor.moveToFirst()){
				Log.i("info", "ENTRO "+cursor.getDouble(0));
				res += cursor.getDouble(0);
			}
			cursor=db.rawQuery("SELECT SUM(CP.valor_cruzado) FROM (esd_cobro AS  C JOIN esd_cobro_cruce_pago AS CP ON C.cobro_id=CP.cobro_id) WHERE C.cliente_id=? AND DATE(fecha_cobro)=DATE(?)", new String [] {cliente_id,fecha});
			if(cursor.moveToFirst()){
				Log.i("info", "ENTRO 2"+cursor.getDouble(0));
				res += cursor.getDouble(0);
			}
			cursor=db.rawQuery("SELECT CN.valor_cruzado FROM (esd_cobro AS  C JOIN esd_cobro_cruce_docs_neg AS CN ON C.cobro_id=CN.cobro_id) WHERE C.cliente_id=? AND DATE(fecha_cobro)=DATE(?)", new String [] {cliente_id,fecha});
			if(cursor.moveToFirst()){
				Log.i("info", "ENTRO 3"+cursor.getDouble(0));

				res += cursor.getDouble(0);
			}
			if(cursor!=null){
				cursor.close();
			}
			return res;
		}
	}

	public double getPedidosPorVisita(String visita_id){
		synchronized (Lock) {
			open();
			double res=0;
			Cursor cursor=db.rawQuery("SELECT SUM(valor_pedido) FROM esd_visita_evento WHERE visita_id=?", new String [] {visita_id});
			if(cursor.moveToFirst()){
				res = cursor.getDouble(0);
			}
			if(cursor!=null){
				cursor.close();
			}
			return res;
		}
	}	

	public double getCobrosPorVisita(String visita_id){
		synchronized (Lock) {
			open();
			double res=0;
			Cursor cursor=db.rawQuery("SELECT SUM(valor_pago) FROM esd_visita_evento WHERE visita_id=?", new String [] {visita_id});
			if(cursor.moveToFirst()){
				res = cursor.getDouble(0);
			}
			if(cursor!=null){
				cursor.close();
			}
			return res;
		}
	}	

	public boolean descontarPagosACartera(String cobro_id){
		synchronized (Lock) {
			open();
			boolean res=true;
			Cursor cursorDocs=db.rawQuery("SELECT documento_id FROM esd_cobro_documento WHERE cobro_id=?", new String [] {cobro_id});
			Cursor cursorVal=null;
			String doc_id="";
			String sql="";
			try{
				if(cursorDocs.moveToFirst()){
					do{
						doc_id=cursorDocs.getString(0);
						sql="SELECT valor_cruzado FROM esd_cobro_cruce_pago WHERE documento_id='"+doc_id+"' AND cobro_id='"+cobro_id+"'";
//						Log.i("info",sql+";");
						cursorVal=db.rawQuery(sql, new String [] {});
						if(cursorVal.moveToFirst()){
							do{
								sql="UPDATE esd_cartera_documento SET saldo_pendiente=(saldo_pendiente-("+cursorVal.getDouble(0)+")) WHERE documento_id='"+doc_id+"'";
								db.execSQL(sql);
							}while(cursorVal.moveToNext());
						}

						sql="SELECT valor_cruzado FROM esd_cobro_cruce_consignacion WHERE documento_id='"+doc_id+"' AND cobro_id='"+cobro_id+"'";
//						Log.i("info",sql+";");
						cursorVal=db.rawQuery(sql, new String [] {});
						if(cursorVal.moveToFirst()){
							do{
								sql="UPDATE esd_cartera_documento SET saldo_pendiente=(saldo_pendiente-("+cursorVal.getDouble(0)+")) WHERE documento_id='"+doc_id+"'";
								db.execSQL(sql);
							}while(cursorVal.moveToNext());
						}
						
						sql="SELECT valor_cruzado,documento_id_neg FROM esd_cobro_cruce_docs_neg WHERE documento_id='"+doc_id+"'  AND cobro_id='"+cobro_id+"'";
//						Log.i("info",sql+";");
						cursorVal=db.rawQuery(sql, new String [] {});
						if(cursorVal.moveToFirst()){
							do{
								sql="UPDATE esd_cartera_documento SET saldo_pendiente=saldo_pendiente+("+cursorVal.getDouble(0)+") WHERE documento_id='"+doc_id+"'";
								db.execSQL(sql);
								sql="UPDATE esd_cartera_documento SET saldo_pendiente=saldo_pendiente-("+cursorVal.getDouble(0)+") WHERE documento_id='"+cursorVal.getString(1)+"'";
								db.execSQL(sql);
							}while(cursorVal.moveToNext());
						}
						
						sql="SELECT valor_diferencia FROM esd_cobro_diferencia WHERE documento_id='"+doc_id+"' AND cobro_id='"+cobro_id+"'";
						cursorVal=db.rawQuery(sql, new String [] {});
//						Log.i("info",sql+";");
						if(cursorVal.moveToFirst()){
							do{
								sql="UPDATE esd_cartera_documento SET saldo_pendiente=(saldo_pendiente-("+cursorVal.getDouble(0)+")) WHERE documento_id='"+doc_id+"'";
								db.execSQL(sql);
							}while(cursorVal.moveToNext());
						}
						
						sql="SELECT valor_desc_pronto_pago FROM esd_cobro_documento WHERE documento_id='"+doc_id+"' AND cobro_id='"+cobro_id+"'";
//						Log.i("info",sql+";");
						cursorVal=db.rawQuery(sql, new String [] {});
						if(cursorVal.moveToFirst()){
							do{
								sql="UPDATE esd_cartera_documento SET saldo_pendiente=(saldo_pendiente-("+cursorVal.getDouble(0)+")) WHERE documento_id='"+doc_id+"'";
								db.execSQL(sql);
							}while(cursorVal.moveToNext());
						}
					}while(cursorDocs.moveToNext());
				}
			}catch(Exception e){
				res=false;
			}finally{
				if(cursorDocs!=null){
					cursorDocs.close();
				}
				if(cursorVal!=null){
					cursorVal.close();
				}
			}
			return res;
		}
	}
	
	public boolean restaurarPagosACartera(String cobro_id){
		synchronized (Lock) {
			open();
			boolean res=true;			
			String doc_id="";
			String sql="";
			Cursor cursorDocs=db.rawQuery("SELECT documento_id FROM esu_cobro_documento WHERE cobro_id=?", new String [] {cobro_id});
			Cursor cursorVal=null;
			try{
				if(cursorDocs.moveToFirst()){
					do{
						doc_id=cursorDocs.getString(0);
						sql="SELECT valor_cruzado FROM esu_cobro_cruce_pago WHERE documento_id='"+doc_id+"' AND cobro_id='"+cobro_id+"'";
						cursorVal=db.rawQuery(sql, new String [] {});
						if(cursorVal.moveToFirst()){
							do{
								sql="UPDATE esd_cartera_documento SET saldo_pendiente=(saldo_pendiente+("+cursorVal.getDouble(0)+")) WHERE documento_id='"+doc_id+"'";
								db.execSQL(sql);
							}while(cursorVal.moveToNext());
						}

						sql="SELECT valor_cruzado FROM esu_cobro_cruce_consignacion WHERE documento_id='"+doc_id+"' AND cobro_id='"+cobro_id+"'";
						cursorVal=db.rawQuery(sql, new String [] {});
						if(cursorVal.moveToFirst()){
							do{
								sql="UPDATE esd_cartera_documento SET saldo_pendiente=(saldo_pendiente+("+cursorVal.getDouble(0)+")) WHERE documento_id='"+doc_id+"'";
								db.execSQL(sql);
							}while(cursorVal.moveToNext());
						}
						
						sql="SELECT valor_cruzado,documento_id_neg FROM esu_cobro_cruce_docs_neg WHERE documento_id='"+doc_id+"' AND cobro_id='"+cobro_id+"'";
						cursorVal=db.rawQuery(sql, new String [] {});
						if(cursorVal.moveToFirst()){
							do{
								sql="UPDATE esd_cartera_documento SET saldo_pendiente=saldo_pendiente-("+cursorVal.getDouble(0)+") WHERE documento_id='"+doc_id+"'";
								db.execSQL(sql);
								sql="UPDATE esd_cartera_documento SET saldo_pendiente=saldo_pendiente+("+cursorVal.getDouble(0)+") WHERE documento_id='"+cursorVal.getString(1)+"'";
								db.execSQL(sql);
							}while(cursorVal.moveToNext());
						}
						
						sql="SELECT valor_diferencia FROM esu_cobro_diferencia WHERE documento_id='"+doc_id+"' AND cobro_id='"+cobro_id+"'";
						cursorVal=db.rawQuery(sql, new String [] {});
						if(cursorVal.moveToFirst()){
							do{
								sql="UPDATE esd_cartera_documento SET saldo_pendiente=(saldo_pendiente+("+cursorVal.getDouble(0)+")) WHERE documento_id='"+doc_id+"'";
								db.execSQL(sql);
							}while(cursorVal.moveToNext());
						}
						
						sql="SELECT valor_desc_pronto_pago FROM esu_cobro_documento WHERE documento_id='"+doc_id+"' AND cobro_id='"+cobro_id+"'";
						cursorVal=db.rawQuery(sql, new String [] {});
						if(cursorVal.moveToFirst()){
							do{
								sql="UPDATE esd_cartera_documento SET saldo_pendiente=(saldo_pendiente+("+cursorVal.getDouble(0)+")) WHERE documento_id='"+doc_id+"'";
								db.execSQL(sql);
							}while(cursorVal.moveToNext());
						}

					}while(cursorDocs.moveToNext());
				}
			}catch(Exception e){
				res=false;
			}finally{
				if(cursorDocs!=null){
					cursorDocs.close();
				}
				if(cursorVal!=null){
					cursorVal.close();
				}
			}
			
			return res;
		}
	}
	
	public boolean deleteCobro(String cobro_id,String id,String obs){
		synchronized (Lock) {
			open();
			Log.i("info","deleteCobro 0");
			boolean res=true;
			Cursor cursor;
			String sqlGetCobro="SELECT * FROM esu_cobro WHERE cobro_id='"+cobro_id+"'";
//			String sqlUpdateDown="UPDATE esd_cobro SET motivo_anulacion_id='"+id+"', observaciones_anulacion='"+obs+"' WHERE cobro_id='"+cobro_id+"'";
			String sqlDown1="DELETE FROM esd_cobro WHERE cobro_id='"+cobro_id+"'";
			String sqlDown2="DELETE FROM esd_cobro_documento WHERE cobro_id='"+cobro_id+"'";
			String sqlDown3="DELETE FROM esd_cobro_pago WHERE cobro_id='"+cobro_id+"'";
			String sqlDown4="DELETE FROM esd_cobro_pago_det WHERE cobro_id='"+cobro_id+"'";
			String sqlDown5="DELETE FROM esd_cobro_cruce_pago WHERE cobro_id='"+cobro_id+"'";
			String sqlDown6="DELETE FROM esd_cobro_cruce_consignacion WHERE cobro_id='"+cobro_id+"'";
			String sqlDown7="DELETE FROM esd_cobro_cruce_docs_neg WHERE cobro_id='"+cobro_id+"'";
			String sqlDown8="DELETE FROM esd_cobro_diferencia WHERE cobro_id='"+cobro_id+"'";

			String sqlInsertUp="UPDATE esu_cobro SET motivo_anulacion_id='"+id+"', observaciones_anulacion='"+obs+"' WHERE cobro_id='"+cobro_id+"'";
			Log.i("info","deleteCobro 1");
			try{
				cursor=db.rawQuery(sqlGetCobro, new String [] {});
				Log.i("info","deleteCobro 2");
				if(cursor.moveToFirst()){
					Log.i("info","deleteCobro 3");
					sqlInsertUp="INSERT INTO esu_cobro VALUES('"+getCobrosNumeroCobro()+"','"+
						cursor.getString(1)+"','"+
						cursor.getString(2)+"','"+
						cursor.getString(3)+"','"+
						cursor.getString(4)+"','"+
						id+"','"+
						obs+"','A',datetime('now','localtime'))";
					Log.i("info","deleteCobro 4");
					db.execSQL(sqlDown1);
					db.execSQL(sqlDown2);
					db.execSQL(sqlDown3);
					db.execSQL(sqlDown4);
					db.execSQL(sqlDown5);
					db.execSQL(sqlDown6);
					db.execSQL(sqlDown7);
					db.execSQL(sqlDown8);
					Log.i("info","deleteCobro 5");
					db.execSQL(sqlInsertUp);
					Log.i("info","deleteCobro 6");
				}else{
					Log.i("info","deleteCobro 7");

				}
			}catch(Exception e){
				Log.e("info","deleteCobro error "+e);
				res=false;
			}
			return res;
		}
	}

	/*
	 * 
	 * VARIOS
	 * 
	 */

	public Cursor getConceptoHorarios(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT descripcion FROM esd_concepto_horario", new String [] {});
			return cursor;
		}
	}

	
	public Cursor getCargos(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT nombre_cargo FROM esd_cargo", new String [] {});
			return cursor;
		}
	}
	
	public Cursor getMedioDeComunicacion(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT nombre_medio_contacto FROM esd_medio_contacto", new String [] {});
			return cursor;
		}
	}
	
	public Cursor getNombreCliente(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT nombre_cliente FROM esd_cliente", new String [] {});
			return cursor;
		}
	}

	public Cursor getCliente_id(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT cliente_id FROM esd_cliente", new String [] {});
			return cursor;
		}
	}

	public Cursor getRazonSocial(){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT nit FROM esd_cliente", new String [] {});
			return cursor;
		}
	}

	/*
	 * 
	 * MENU ACTIVIDAD DIARIA
	 * 
	 * */

	public String getVendedor(){
		synchronized (Lock) {
			open();
			String res="";
			Cursor cursor = db.rawQuery("SELECT nombre_asesor FROM esd_vendedor", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getString(0);
			}
			if(cursor!=null){
				cursor.close();
			}
			return res;
		}
	}

	public String getVendedorString(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT nombre_asesor FROM esd_vendedor", new String [] {});
			if(cursor.moveToFirst()){
				return cursor.getString(0);
			}else{
				return "";
			}
		}
	}
	
	/*
	 * 
	 * Visita 
	 * 
	 */

	public Cursor getVisitaClienteDatos(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor;
//			cursor=db.rawQuery("SELECT C.cliente_id, C.nombre_cliente, C.direccion, C.telefono, C.celular, CI.nombre, C.barrio, C.nit, T.nombre, C.estado_cartera, C.cupo_id, F.nombre_forma_pago, estado_cartera, bloqueo_pedido  FROM ((esd_cliente AS C JOIN esd_tipo_cliente AS T ON C.tipo_cliente_id=T.tipo_cliente_id) JOIN esd_forma_pago AS F ON C.forma_pago_id=F.forma_pago_id) JOIN esd_ciudad AS CI ON C.ciudad_id=CI.ciudad_id AND C.departamento_id=CI.departamento_id WHERE cliente_id=?", new String [] {cliente_id});
			cursor=db.rawQuery("SELECT C.cliente_id,C.nombre_cliente,C.direccion, C.telefono, C.celular, CI.nombre,C.barrio,C.nit,T.nombre,C.estado_cartera, C.cupo_id,F.nombre_forma_pago,estado_cartera,bloqueo_pedido,CU.valor_cupo FROM ((esd_cliente AS C JOIN esd_tipo_cliente AS T ON C.tipo_cliente_id=T.tipo_cliente_id) JOIN esd_forma_pago AS F ON C.forma_pago_id=F.forma_pago_id) JOIN esd_ciudad AS CI ON C.ciudad_id=CI.ciudad_id AND C.departamento_id=CI.departamento_id JOIN esd_cupo AS CU ON C.cupo_id=CU.cupo_id WHERE cliente_id=?", new String [] {cliente_id});	
			return cursor;
		}
	}
	
	/*
	 * 
	 * Pedidos 
	 * 
	 */

	public String getPedidoId(){
		synchronized (Lock) {
			open();
			String res="";
			Cursor cursor=db.rawQuery("SELECT valor FROM esd_rango WHERE id=3", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getString(0);
			}
			return res;
		}
	}
	
	public Cursor getPedidoEncabezado(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT SUM(saldo_pendiente), valor_cupo, tipo_venta FROM (esd_cliente AS CL LEFT OUTER JOIN esd_cartera_documento AS CA ON CL.cliente_id=CA.cliente_id) JOIN esd_cupo AS CU ON CU.cupo_id=CL.cupo_id WHERE CL.cliente_id=?", new String [] {cliente_id});
			return cursor;
		}
	}
	
	public Cursor getPedidos(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT P.numero_pedido,valor_total,descripcion,strftime('%m/%d/%Y',fecha),valor_iva, P.tipo_documento_id ,cliente_id FROM esd_pedido AS P JOIN esd_pedido_tipo_documento AS PD  ON P.tipo_documento_id= PD.tipo_documento_id WHERE cliente_id=?", new String [] {cliente_id});
			return cursor;
		}
	}

	public Cursor getPedidosDelDia(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT P.numero_pedido,valor_total,descripcion,strftime('%m/%d/%Y',fecha),valor_iva, P.tipo_documento_id ,cliente_id FROM esd_pedido AS P JOIN esd_pedido_tipo_documento AS PD  ON P.tipo_documento_id= PD.tipo_documento_id WHERE cliente_id=? AND date(fecha)=date('now','localtime')", new String [] {cliente_id});
			return cursor;
		}
	}
	
	public String getEstadoDelPedido(String tipo_doc_id){
		synchronized (Lock) {
			open();
			String res="";
			Cursor cursor=db.rawQuery("SELECT descripcion FROM esd_pedido_tipo_documento WHERE tipo_documento_id = ?", new String [] {tipo_doc_id});
			if(cursor.moveToFirst()){
				res=cursor.getString(0);
			}
			if(cursor!=null){
				cursor.close();
			}
			return res;
		}
	}

	public Cursor getPedidosData(String pedido_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT numero_pedido,numero_articulos,porc_descuento_basico,porc_descuento_adicional, valor_bruto, valor_descuento, valor_total, valor_promocion, valor_iva FROM esd_pedido WHERE numero_pedido=? ", new String [] {pedido_id});
			return cursor;
		}
	}

	public int getPedidosNArticulos(String pedido_id){
		synchronized (Lock) {
			open();
			int res=0;
			Cursor cursor=db.rawQuery("SELECT SUM(cantidad_pedida) FROM esd_pedido_detalle WHERE valor_total>0 AND numero_pedido=?", new String [] {pedido_id});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
			}
			return res;
		}
	}
	
	public Cursor getProductosBySubfamilia(String cliente_id, String familia_id,String subfamilia_id){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			Cursor cursorRes=null;
			String sql="";
			try{
				cursor=db.rawQuery("SELECT campo_referencia FROM esd_lista_precio AS lp JOIN esd_cliente AS c ON  lp.lista_precio_id = c.lista_precio_id WHERE cliente_id=?", new String [] {cliente_id});			
				if(cursor.moveToFirst()){
					sql="SELECT Distinct P.producto_id,nombre,"+cursor.getString(0)+",porcentaje_iva,T.id,cantidad_pedida, valor_bruto FROM (esd_grupo_producto AS g JOIN esd_producto AS p ON g.grupo_producto_id=p.grupo_producto_id)LEFT OUTER JOIN est_pedido_detalle AS T ON P.producto_id=T.producto_id WHERE p.grupo_producto_id=? AND p.unidad_negocio_id=?";
					Log.i("info","sql "+sql);
					Log.i("info","familia_id "+familia_id);
					Log.i("info","subfamilia_id "+subfamilia_id);
					cursorRes=db.rawQuery(sql, new String [] {subfamilia_id,familia_id});
				}
			}catch(Exception e){
				Log.e("info","error getProductosBySubfamilia "+e);
			}
			return cursorRes;
		}
	}
	
	public String getProductoPresentacion(String producto_id){
		synchronized (Lock) {
			open();
			String res="";
			Cursor cursor=db.rawQuery("SELECT nombre_presentacion FROM esd_producto_presentacion AS PP JOIN esd_producto AS P ON pp.presentacion_id=p.presentacion_id whERE producto_id=?", new String [] {producto_id});
			if(cursor.moveToFirst()){
				res=cursor.getString(0);
			}
			
			return res;
		}
	}
	
	
	public Cursor getTemporadas(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT temporada_id, descripcion FROM esd_temporada ORDER BY descripcion ASC", new String [] {});
			return cursor;
		}
	}

	public Cursor getPromociones(String ids){
		synchronized (Lock) {
			open();
			String sql="SELECT promocion_id, descripcion FROM esd_promocion_enc WHERE promocion_id IN ("+ids+")";
			Cursor cursor=db.rawQuery(sql, new String [] {});
			return cursor;
		}
	}

	public Cursor getProductosByTemporada(String cliente_id,String temporada){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			String sql="";
			try{
				cursor=db.rawQuery("SELECT campo_referencia FROM esd_lista_precio AS lp JOIN esd_cliente AS c WHERE  lp.lista_precio_id = c.lista_precio_id AND cliente_id=?", new String [] {cliente_id});			
				if(cursor.moveToFirst()){
					sql="SELECT P.producto_id, P.nombre,"+cursor.getString(0)+",P.porcentaje_iva,T.id,cantidad_pedida, valor_bruto FROM (esd_producto AS P JOIN esd_producto_temporada AS PT ON P.producto_id=PT.producto_id) LEFT JOIN est_pedido_detalle AS T ON P.producto_id=T.producto_id WHERE PT.temporada_id=?";
					cursor=db.rawQuery(sql, new String [] {temporada});
				}
			}catch(Exception e){
				Log.e("info","error getProductosByTemporada "+e);
			}
			return cursor;
		}
	}

	public Cursor getProductosByPromocion(String cliente_id,String promo_id){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			String sql="";
			try{
				cursor=db.rawQuery("SELECT campo_referencia FROM esd_lista_precio AS lp JOIN esd_cliente AS c WHERE  lp.lista_precio_id = c.lista_precio_id AND cliente_id=?", new String [] {cliente_id});			
				if(cursor.moveToFirst()){
					sql="SELECT P.producto_id, P.nombre,"+cursor.getString(0)+",P.porcentaje_iva,T.id, cantidad_pedida,valor_bruto FROM (esd_producto AS P JOIN esd_promocion_det AS PT ON P.producto_id=PT.producto_id)  LEFT JOIN est_pedido_detalle AS T ON P.producto_id=T.producto_id WHERE PT.promocion_id=?";
					cursor=db.rawQuery(sql, new String [] {promo_id});
				}
			}catch(Exception e){
				Log.e("info","error getProductosByPromocion "+e);
			}
			return cursor;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList getListasArticulosPromocion(String cliente_id){
		synchronized (Lock) {
			open();
			ArrayList res=new ArrayList();
			Cursor aux0=null;
			Cursor aux1=null;
			Cursor aux2=null;
			Cursor aux3=null;
			Cursor aux4=null;
			Cursor cursorPromos=null;
			Cursor clienteDatos=null;
			try{
				res.add(new ArrayList<String>());
				res.add(new ArrayList<String>());
				String promo_id="";
				aux0=null;
				aux1=null;
				aux2=null;
				aux3=null;
				aux4=null;
				cursorPromos=db.rawQuery("SELECT * FROM esd_promocion_enc WHERE date('now','localtime') BETWEEN vigencia_inicio AND vigencia_final ORDER BY orden", new String [] {});
				clienteDatos=db.rawQuery("SELECT distrito_id, canal_id, cadena_id, tipo_cliente_id FROM esd_cliente WHERE cliente_id=?", new String [] {cliente_id});
				if(cursorPromos.moveToFirst() && clienteDatos.moveToFirst()){
					do{
						promo_id=cursorPromos.getString(1);
						aux0=db.rawQuery("SELECT COUNT() FROM esd_promocion_x_distrito WHERE promocion_id=? AND distrito_id=?",new String [] {promo_id,clienteDatos.getString(0)});
						aux1=db.rawQuery("SELECT COUNT() FROM esd_promocion_x_canal WHERE promocion_id=? AND canal_id=?",new String [] {promo_id,clienteDatos.getString(1)});
						aux2=db.rawQuery("SELECT COUNT() FROM esd_promocion_x_cadena WHERE promocion_id=? AND cadena_id=?",new String [] {promo_id,clienteDatos.getString(2)});
						aux3=db.rawQuery("SELECT COUNT() FROM esd_promocion_x_tipo_cliente WHERE promocion_id=? AND tipo_cliente_id=?",new String [] {promo_id,clienteDatos.getString(3)});
						aux4=db.rawQuery("SELECT COUNT() FROM esd_promocion_x_cliente WHERE promocion_id=? AND cliente_id=?",new String [] {promo_id,cliente_id});
						if(cursorPromos.getString(5).equalsIgnoreCase("S")){
							if(aux0.moveToFirst() && (aux0.getInt(0)>0)){
								if(cursorPromos.getString(6).equalsIgnoreCase("S")){
									if(aux1.moveToFirst() && (aux1.getInt(0)>0)){
										if(cursorPromos.getString(7).equalsIgnoreCase("S")){
											if(aux2.moveToFirst() && (aux2.getInt(0)>0)){
												if(cursorPromos.getString(8).equalsIgnoreCase("S")){
													if(aux3.moveToFirst() && (aux3.getInt(0)>0)){
														if(cursorPromos.getString(9).equalsIgnoreCase("S")){
															if(aux4.moveToFirst() && (aux4.getInt(0)>0)){
																if(cursorPromos.getString(10).equalsIgnoreCase("S")){
																	((ArrayList<String>)res.get(0)).add(promo_id);
																}else{
																	((ArrayList<String>)res.get(1)).add(promo_id);
																}												
															}
														}else{
															if(cursorPromos.getString(10).equalsIgnoreCase("S")){
																((ArrayList<String>)res.get(0)).add(promo_id);
															}else{
																((ArrayList<String>)res.get(1)).add(promo_id);
															}												
														}
													}
												}else{
													if(cursorPromos.getString(9).equalsIgnoreCase("S")){
														if(aux4.moveToFirst() && (aux4.getInt(0)>0)){
															if(cursorPromos.getString(10).equalsIgnoreCase("S")){
																((ArrayList<String>)res.get(0)).add(promo_id);
															}else{
																((ArrayList<String>)res.get(1)).add(promo_id);
															}												
														}
													}else{
														if(cursorPromos.getString(10).equalsIgnoreCase("S")){
															((ArrayList<String>)res.get(0)).add(promo_id);
														}else{
															((ArrayList<String>)res.get(1)).add(promo_id);
														}												
													}
												}
											}
										}else{
											if(cursorPromos.getString(8).equalsIgnoreCase("S")){
												if(aux3.moveToFirst() && (aux3.getInt(0)>0)){
													if(cursorPromos.getString(9).equalsIgnoreCase("S")){
														if(aux4.moveToFirst() && (aux4.getInt(0)>0)){
															if(cursorPromos.getString(10).equalsIgnoreCase("S")){
																((ArrayList<String>)res.get(0)).add(promo_id);
															}else{
																((ArrayList<String>)res.get(1)).add(promo_id);
															}												
														}
													}else{
														if(cursorPromos.getString(10).equalsIgnoreCase("S")){
															((ArrayList<String>)res.get(0)).add(promo_id);
														}else{
															((ArrayList<String>)res.get(1)).add(promo_id);
														}												
													}
												}
											}else{
												if(cursorPromos.getString(9).equalsIgnoreCase("S")){
													if(aux4.moveToFirst() && (aux4.getInt(0)>0)){
														if(cursorPromos.getString(10).equalsIgnoreCase("S")){
															((ArrayList<String>)res.get(0)).add(promo_id);
														}else{
															((ArrayList<String>)res.get(1)).add(promo_id);
														}												
													}
												}else{
													if(cursorPromos.getString(10).equalsIgnoreCase("S")){
														((ArrayList<String>)res.get(0)).add(promo_id);
													}else{
														((ArrayList<String>)res.get(1)).add(promo_id);
													}												
												}
											}
										}
									}
								}else{
									if(cursorPromos.getString(7).equalsIgnoreCase("S")){
										if(aux2.moveToFirst() && (aux2.getInt(0)>0)){
											if(cursorPromos.getString(8).equalsIgnoreCase("S")){
												if(aux3.moveToFirst() && (aux3.getInt(0)>0)){
													if(cursorPromos.getString(9).equalsIgnoreCase("S")){
														if(aux4.moveToFirst() && (aux4.getInt(0)>0)){
															if(cursorPromos.getString(10).equalsIgnoreCase("S")){
																((ArrayList<String>)res.get(0)).add(promo_id);
															}else{
																((ArrayList<String>)res.get(1)).add(promo_id);
															}												
														}
													}else{
														if(cursorPromos.getString(10).equalsIgnoreCase("S")){
															((ArrayList<String>)res.get(0)).add(promo_id);
														}else{
															((ArrayList<String>)res.get(1)).add(promo_id);
														}												
													}
												}
											}else{
												if(cursorPromos.getString(9).equalsIgnoreCase("S")){
													if(aux4.moveToFirst() && (aux4.getInt(0)>0)){
														if(cursorPromos.getString(10).equalsIgnoreCase("S")){
															((ArrayList<String>)res.get(0)).add(promo_id);
														}else{
															((ArrayList<String>)res.get(1)).add(promo_id);
														}												
													}
												}else{
													if(cursorPromos.getString(10).equalsIgnoreCase("S")){
														((ArrayList<String>)res.get(0)).add(promo_id);
													}else{
														((ArrayList<String>)res.get(1)).add(promo_id);
													}												
												}
											}
										}
									}else{
										if(cursorPromos.getString(8).equalsIgnoreCase("S")){
											if(aux3.moveToFirst() && (aux3.getInt(0)>0)){
												if(cursorPromos.getString(9).equalsIgnoreCase("S")){
													if(aux4.moveToFirst() && (aux4.getInt(0)>0)){
														if(cursorPromos.getString(10).equalsIgnoreCase("S")){
															((ArrayList<String>)res.get(0)).add(promo_id);
														}else{
															((ArrayList<String>)res.get(1)).add(promo_id);
														}												
													}
												}else{
													if(cursorPromos.getString(10).equalsIgnoreCase("S")){
														((ArrayList<String>)res.get(0)).add(promo_id);
													}else{
														((ArrayList<String>)res.get(1)).add(promo_id);
													}												
												}
											}
										}else{
											if(cursorPromos.getString(9).equalsIgnoreCase("S")){
												if(aux4.moveToFirst() && (aux4.getInt(0)>0)){
													if(cursorPromos.getString(10).equalsIgnoreCase("S")){
														((ArrayList<String>)res.get(0)).add(promo_id);
													}else{
														((ArrayList<String>)res.get(1)).add(promo_id);
													}												
												}
											}else{
												if(cursorPromos.getString(10).equalsIgnoreCase("S")){
													((ArrayList<String>)res.get(0)).add(promo_id);
												}else{
													((ArrayList<String>)res.get(1)).add(promo_id);
												}												
											}
										}
									}
								}
							}
						}else{
							if(cursorPromos.getString(6).equalsIgnoreCase("S")){
								if(aux1.moveToFirst() && (aux1.getInt(0)>0)){
									if(cursorPromos.getString(7).equalsIgnoreCase("S")){
										if(aux2.moveToFirst() && (aux2.getInt(0)>0)){
											if(cursorPromos.getString(8).equalsIgnoreCase("S")){
												if(aux3.moveToFirst() && (aux3.getInt(0)>0)){
													if(cursorPromos.getString(9).equalsIgnoreCase("S")){
														if(aux4.moveToFirst() && (aux4.getInt(0)>0)){
															if(cursorPromos.getString(10).equalsIgnoreCase("S")){
																((ArrayList<String>)res.get(0)).add(promo_id);
															}else{
																((ArrayList<String>)res.get(1)).add(promo_id);
															}												
														}
													}else{
														if(cursorPromos.getString(10).equalsIgnoreCase("S")){
															((ArrayList<String>)res.get(0)).add(promo_id);
														}else{
															((ArrayList<String>)res.get(1)).add(promo_id);
														}												
													}
												}
											}else{
												if(cursorPromos.getString(9).equalsIgnoreCase("S")){
													if(aux4.moveToFirst() && (aux4.getInt(0)>0)){
														if(cursorPromos.getString(10).equalsIgnoreCase("S")){
															((ArrayList<String>)res.get(0)).add(promo_id);
														}else{
															((ArrayList<String>)res.get(1)).add(promo_id);
														}												
													}
												}else{
													if(cursorPromos.getString(10).equalsIgnoreCase("S")){
														((ArrayList<String>)res.get(0)).add(promo_id);
													}else{
														((ArrayList<String>)res.get(1)).add(promo_id);
													}												
												}
											}
										}
									}else{
										if(cursorPromos.getString(8).equalsIgnoreCase("S")){
											if(aux3.moveToFirst() && (aux3.getInt(0)>0)){
												if(cursorPromos.getString(9).equalsIgnoreCase("S")){
													if(aux4.moveToFirst() && (aux4.getInt(0)>0)){
														if(cursorPromos.getString(10).equalsIgnoreCase("S")){
															((ArrayList<String>)res.get(0)).add(promo_id);
														}else{
															((ArrayList<String>)res.get(1)).add(promo_id);
														}												
													}
												}else{
													if(cursorPromos.getString(10).equalsIgnoreCase("S")){
														((ArrayList<String>)res.get(0)).add(promo_id);
													}else{
														((ArrayList<String>)res.get(1)).add(promo_id);
													}												
												}
											}
										}else{
											if(cursorPromos.getString(9).equalsIgnoreCase("S")){
												if(aux4.moveToFirst() && (aux4.getInt(0)>0)){
													if(cursorPromos.getString(10).equalsIgnoreCase("S")){
														((ArrayList<String>)res.get(0)).add(promo_id);
													}else{
														((ArrayList<String>)res.get(1)).add(promo_id);
													}												
												}
											}else{
												if(cursorPromos.getString(10).equalsIgnoreCase("S")){
													((ArrayList<String>)res.get(0)).add(promo_id);
												}else{
													((ArrayList<String>)res.get(1)).add(promo_id);
												}												
											}
										}
									}
								}
							}else{
								if(cursorPromos.getString(7).equalsIgnoreCase("S")){
									if(aux2.moveToFirst() && (aux2.getInt(0)>0)){
										if(cursorPromos.getString(8).equalsIgnoreCase("S")){
											if(aux3.moveToFirst() && (aux3.getInt(0)>0)){
												if(cursorPromos.getString(9).equalsIgnoreCase("S")){
													if(aux4.moveToFirst() && (aux4.getInt(0)>0)){
														if(cursorPromos.getString(10).equalsIgnoreCase("S")){
															((ArrayList<String>)res.get(0)).add(promo_id);
														}else{
															((ArrayList<String>)res.get(1)).add(promo_id);
														}												
													}
												}else{
													if(cursorPromos.getString(10).equalsIgnoreCase("S")){
														((ArrayList<String>)res.get(0)).add(promo_id);
													}else{
														((ArrayList<String>)res.get(1)).add(promo_id);
													}												
												}
											}
										}else{
											if(cursorPromos.getString(9).equalsIgnoreCase("S")){
												if(aux4.moveToFirst() && (aux4.getInt(0)>0)){
													if(cursorPromos.getString(10).equalsIgnoreCase("S")){
														((ArrayList<String>)res.get(0)).add(promo_id);
													}else{
														((ArrayList<String>)res.get(1)).add(promo_id);
													}												
												}
											}else{
												if(cursorPromos.getString(10).equalsIgnoreCase("S")){
													((ArrayList<String>)res.get(0)).add(promo_id);
												}else{
													((ArrayList<String>)res.get(1)).add(promo_id);
												}												
											}
										}
									}
								}else{
									if(cursorPromos.getString(8).equalsIgnoreCase("S")){
										if(aux3.moveToFirst() && (aux3.getInt(0)>0)){
											if(cursorPromos.getString(9).equalsIgnoreCase("S")){
												if(aux4.moveToFirst() && (aux4.getInt(0)>0)){
													if(cursorPromos.getString(10).equalsIgnoreCase("S")){
														((ArrayList<String>)res.get(0)).add(promo_id);
													}else{
														((ArrayList<String>)res.get(1)).add(promo_id);
													}												
												}
											}else{
												if(cursorPromos.getString(10).equalsIgnoreCase("S")){
													((ArrayList<String>)res.get(0)).add(promo_id);
												}else{
													((ArrayList<String>)res.get(1)).add(promo_id);
												}												
											}
										}
									}else{
										if(cursorPromos.getString(9).equalsIgnoreCase("S")){
											if(aux4.moveToFirst() && (aux4.getInt(0)>0)){
												if(cursorPromos.getString(10).equalsIgnoreCase("S")){
													((ArrayList<String>)res.get(0)).add(promo_id);
												}else{
													((ArrayList<String>)res.get(1)).add(promo_id);
												}												
											}
										}else{
											if(cursorPromos.getString(10).equalsIgnoreCase("S")){
												((ArrayList<String>)res.get(0)).add(promo_id);
											}else{
												((ArrayList<String>)res.get(1)).add(promo_id);
											}												
										}
									}
								}
							}
						}
					}while(cursorPromos.moveToNext());
				}
			}catch(Exception e){
				Log.e("error getListasArticulosPromocion",e.toString());
			}finally{
				if(aux0!=null){
					aux0.close();
				}
				if(aux1!=null){
					aux1.close();
				}
				if(aux2!=null){
					aux2.close();
				}
				if(aux3!=null){
					aux3.close();
				}
				if(aux4!=null){
					aux4.close();
				}
				if(cursorPromos!=null){
					cursorPromos.close();
				}
				if(clienteDatos!=null){
					clienteDatos.close();
				}
			}
			return res;
		}
	}

	
	public Cursor getProductosByNuevo(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			String sql="";
			try{
				cursor=db.rawQuery("SELECT campo_referencia FROM esd_lista_precio AS lp JOIN esd_cliente AS c WHERE  lp.lista_precio_id = c.lista_precio_id AND cliente_id=?", new String [] {cliente_id});			
				if(cursor.moveToFirst()){
					sql="SELECT P.producto_id, P.nombre,"+cursor.getString(0)+",P.porcentaje_iva,T.id, cantidad_pedida,valor_bruto FROM esd_producto AS P LEFT OUTER JOIN est_pedido_detalle AS T ON P.producto_id=T.producto_id WHERE P.articulo_nuevo='S'";
					cursor=db.rawQuery(sql, new String [] {});
				}
			}catch(Exception e){
				Log.e("info","error getProductosByNuevo "+e);
			}
			return cursor;
		}
	}

	public Cursor getProductosBySubfamiliaXfiltros(long tipo, String input,String cliente_id){
		synchronized (Lock) {
			open();
			int searchBy=(int)tipo;
			Cursor cursor=null;
			String sql="";
			String search="%"+input+"%";
			try{
				cursor=db.rawQuery("SELECT campo_referencia FROM esd_lista_precio AS lp JOIN esd_cliente AS c ON lp.lista_precio_id = c.lista_precio_id WHERE cliente_id=?", new String [] {cliente_id});			
				if(cursor.moveToFirst()){
					switch(searchBy){
						case 0:
							sql="SELECT P.producto_id,nombre,"+cursor.getString(0)+",porcentaje_iva,T.id, cantidad_pedida,valor_bruto FROM esd_producto AS P LEFT OUTER JOIN est_pedido_detalle AS T ON P.producto_id=T.producto_id WHERE p.producto_id = ?";
							search=input;
							break;
						case 1:
							sql="SELECT P.producto_id,nombre,"+cursor.getString(0)+",porcentaje_iva,T.id, cantidad_pedida,valor_bruto FROM esd_producto AS P LEFT OUTER JOIN est_pedido_detalle AS T ON P.producto_id=T.producto_id WHERE nombre like ?";
							break;
						default:
							break;
					}
					cursor=db.rawQuery(sql, new String [] {search});
				}
			}catch(Exception e){
				Log.e("info","error getProductosBySubfamiliaXfiltros "+e);
			}
			return cursor;
		}
	}

	public Cursor getPrecioArticuloPedido(String cliente_id,String producto_id){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			String sql="";
			try{
				cursor=db.rawQuery("SELECT campo_referencia FROM esd_lista_precio AS lp JOIN esd_cliente AS c WHERE  lp.lista_precio_id = c.lista_precio_id AND cliente_id=?", new String [] {cliente_id});			
				if(cursor.moveToFirst()){
					sql="SELECT "+cursor.getString(0) +", porcentaje_iva FROM esd_producto WHERE producto_id=?";
					cursor=db.rawQuery(sql, new String [] {producto_id});
				}
			}catch(Exception e){
				Log.e("info","error getPrecioArticuloPedido "+e);
			}
			return cursor;
		}
	}
	
	public Cursor getPedidoTemporal(String cliente_id, String pedido_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT " +
					"SIN_PROM.id," +
					"T.nombre," +
					"T.nombre_presentacion," +
					"SIN_PROM.precio_base," +
					"T.cantidad_sugerida," +
					"SIN_PROM.CANTIDAD_PEDIDA," +
					"CON_PROM.CANTIDAD_BONIF," +
					"SIN_PROM.porcentaje_promocion," +
					"SIN_PROM.valor_promocion," +
					"SIN_PROM.iva," +
					"SIN_PROM.valor_total, " +
					"SIN_PROM.PRODUCTO_ID, " +
					"T.porcentaje_iva " +
					"FROM (SELECT *, '0' AS CANTIDAD_BONIF, CANTIDAD_PEDIDA FROM EST_PEDIDO_DETALLE " +
					"WHERE NUMERO_PEDIDO = ? AND ARTICULO_PROMOCION != '*' OR valor_total!=0) AS SIN_PROM " +
					"LEFT JOIN (SELECT *, CANTIDAD_PEDIDA AS CANTIDAD_BONIF, '0' AS CANTIDAD_PEDIDA FROM EST_PEDIDO_DETALLE " +
					"WHERE NUMERO_PEDIDO = ? AND ARTICULO_PROMOCION = '*' AND valor_total=0) AS CON_PROM ON (SIN_PROM.PRODUCTO_ID = CON_PROM.PRODUCTO_ID) " +
					"LEFT JOIN ((esd_producto AS A LEFT JOIN esd_pedido_sugerido AS PS ON A.producto_id=PS.producto_id AND PS.cliente_id=?) " +
					"JOIN esd_producto_presentacion AS pp ON pp.presentacion_id=A.presentacion_id) AS T ON T.producto_id=SIN_PROM.producto_id  WHERE SIN_PROM.numero_pedido=?"
					, new String [] {pedido_id,pedido_id,cliente_id,pedido_id});		
			return cursor;
		}
	}
	
	public boolean insertEsuObsPedido(String cliente_id,String numero_pedido,String obs,String estado){
		synchronized (Lock) {
			open();
			boolean res=true;
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=22) WHERE id=22";
			String sql="INSERT INTO esu_pedido_obs VALUES (" +
					"(SELECT valor FROM esd_rango WHERE id=22),'"+
					numero_pedido+"','"+
					obs+"','" +
					cliente_id+"','" +
					estado+"', datetime('now','localtime'))";
			try{
				db.execSQL(sql);
				db.execSQL(sqlUpdateRango);
			}catch(Exception e){
				Log.e("info","insertObsPedido error "+e);
				res=false;
			}
			return res;
		}
	}
	
	public boolean insertEsuObsPedidoFact(String cliente_id,String numero_pedido,String obs,String estado){
		synchronized (Lock) {
			open();
			boolean res=true;
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=23) WHERE id=23";
			String sql="INSERT INTO esu_pedido_obs_fact VALUES (" +
					"(SELECT valor FROM esd_rango WHERE id=23),'"+
					numero_pedido+"','"+
					obs+"','" +
					cliente_id+"','" +
					estado+"', datetime('now','localtime'))";
			try{
				db.execSQL(sql);
				db.execSQL(sqlUpdateRango);
			}catch(Exception e){
				Log.e("info","insertObsPedido error "+e);
				res=false;
			}
			return res;
		}
	}
	
	public String getLastEsuObsPedido(String numero_pedido){
		synchronized (Lock) {
			open();
			String res="";
			Cursor cursor=db.rawQuery("SELECT observacion, estado_registro FROM esu_pedido_obs WHERE numero_pedido=? ORDER BY fecha_registro DESC LIMIT 1", new String [] {numero_pedido});
			if(cursor.moveToFirst()){
				if(!cursor.getString(1).equalsIgnoreCase("A")){
					res=cursor.getString(0);
				}
			}
			return res;
		}
	}
	
	public String getLastEsuObsPedidoFact(String numero_pedido){
		synchronized (Lock) {
			open();
			String res="";
			Cursor cursor=db.rawQuery("SELECT observacion, estado_registro FROM esu_pedido_obs_fact WHERE numero_pedido=? ORDER BY fecha_registro DESC LIMIT 1", new String [] {numero_pedido});
			if(cursor.moveToFirst()){
				if(!cursor.getString(1).equalsIgnoreCase("A")){
					res=cursor.getString(0);
				}
			}
			return res;
		}
	}
	
	public Cursor getPedidoDetalle(String cliente_id,String pedido_id){
		synchronized (Lock) {
			open();
/*			Cursor cursor=db.rawQuery("SELECT " +
					"P.id,nombre," +
					"nombre_presentacion," +
					"precio_base," +
					"cantidad_sugerida," +
					"cantidad_pedida," +
					"numero_promocion," +
					"porcentaje_promocion," +
					"valor_promocion," +
					"iva," +
					"valor_total," +
					"P.producto_id, " +
					"porcentaje_iva " +
					"FROM esd_pedido_detalle AS P JOIN ((esd_producto AS A LEFT OUTER JOIN esd_pedido_sugerido AS PS ON A.producto_id=PS.producto_id AND PS.cliente_id=?) JOIN esd_producto_presentacion AS pp ON pp.presentacion_id=A.presentacion_id) AS T ON T.producto_id=P.producto_id  WHERE numero_pedido=?", new String [] {cliente_id,pedido_id});*/
	
			Cursor cursor=db.rawQuery("SELECT " +
					"SIN_PROM.id," +
					"T.nombre," +
					"T.nombre_presentacion," +
					"SIN_PROM.precio_base," +
					"T.cantidad_sugerida," +
					"SIN_PROM.CANTIDAD_PEDIDA," +
					"CON_PROM.CANTIDAD_BONIF," +
					"SIN_PROM.porcentaje_promocion," +
					"SIN_PROM.valor_promocion," +
					"SIN_PROM.iva," +
					"SIN_PROM.valor_total, " +
					"SIN_PROM.PRODUCTO_ID, " +
					"T.porcentaje_iva " +
					"FROM (SELECT *, '0' AS CANTIDAD_BONIF, CANTIDAD_PEDIDA FROM ESD_PEDIDO_DETALLE " +
					"WHERE NUMERO_PEDIDO = ? AND ARTICULO_PROMOCION != '*' OR valor_total!=0) AS SIN_PROM " +
					"LEFT JOIN (SELECT *, CANTIDAD_PEDIDA AS CANTIDAD_BONIF, '0' AS CANTIDAD_PEDIDA FROM ESD_PEDIDO_DETALLE " +
					"WHERE NUMERO_PEDIDO = ? AND ARTICULO_PROMOCION = '*' AND valor_total=0) AS CON_PROM ON (SIN_PROM.PRODUCTO_ID = CON_PROM.PRODUCTO_ID) " +
					"LEFT JOIN ((esd_producto AS A LEFT JOIN esd_pedido_sugerido AS PS ON A.producto_id=PS.producto_id AND PS.cliente_id=?) " +
					"JOIN esd_producto_presentacion AS pp ON pp.presentacion_id=A.presentacion_id) AS T ON T.producto_id=SIN_PROM.producto_id  WHERE SIN_PROM.numero_pedido=?"
					, new String [] {pedido_id,pedido_id,cliente_id,pedido_id});

			
			return cursor;
		}
	}
	
	public boolean deletePedidoTemporal(String id){
		synchronized (Lock) {
			open();
			boolean res= true;
			String sql="";
			try{
				Cursor cursor=db.rawQuery("SELECT articulo_promocion,numero_promocion FROM est_pedido_detalle WHERE id=?", new String [] {id});
				if(cursor.moveToFirst()){
					sql="DELETE FROM est_pedido_detalle WHERE numero_promocion='"+cursor.getString(1)+"' AND precio_base=0" ;
					db.execSQL(sql);
				}
				sql="DELETE FROM est_pedido_detalle WHERE id='"+id+"'";
				db.execSQL(sql);
			}catch(Exception e){
				Log.e("info","deletePedidoTemporal error "+e);
				res=false;
			}
			return res;
		}
	}

	public boolean deletePromoPedidoTemporal(String id){
		synchronized (Lock) {
			open();
			boolean res= true;
			String sql="";
			try{
				Cursor cursor=db.rawQuery("SELECT articulo_promocion,numero_promocion FROM est_pedido_detalle WHERE id=?", new String [] {id});
				if(cursor.moveToFirst()){
					sql="DELETE FROM est_pedido_detalle WHERE numero_promocion='"+cursor.getString(1)+"' AND precio_base=0" ;
					db.execSQL(sql);
				}
			}catch(Exception e){
				Log.e("info","deletePedidoTemporal error "+e);
				res=false;
			}
			return res;
		}
	}

	
	public boolean cleanPedidosTemporales(){
		synchronized (Lock) {
			open();
			boolean res=true;
			try{
				db.execSQL("DELETE FROM est_pedido_detalle");
			}catch(Exception e){
				Log.e("info","cleanPedidosTemporales error "+e);
				res=false;
			}
			return res;
		}
	}

	public boolean loadPedidoSugerido(String cliente_id,String pedido_id, ArrayList<String> listToma){
		synchronized (Lock) {
			open();
			boolean res=true;
			int aplicaIva=0;
			String tipoPromo="";
			Cursor cursorGetSugeridos=null;
			Cursor cursorListaPrecio=null;
			Promo promoValues;
			String sqlSug;
			try{
				cursorListaPrecio=db.rawQuery("SELECT campo_referencia FROM esd_lista_precio AS lp JOIN esd_cliente AS c ON lp.lista_precio_id = c.lista_precio_id WHERE cliente_id=?", new String [] {cliente_id});			
				if(cursorListaPrecio.moveToFirst()){
					sqlSug="SELECT p.producto_id,"+cursorListaPrecio.getString(0)+",porcentaje_iva,cantidad_sugerida FROM esd_producto AS p JOIN esd_pedido_sugerido AS ps ON p.producto_id=ps.producto_id WHERE ps.cliente_id='"+cliente_id+"'";
//					cursorListaPrecio.close();
					cursorGetSugeridos=db.rawQuery(sqlSug, new String [] {});			
					aplicaIva=(AplicarIVA(cliente_id))?1:0;
					if(cursorGetSugeridos.moveToFirst()){
						do{
							promoValues=getPromoValue(cursorGetSugeridos.getString(0), cursorGetSugeridos.getInt(3), listToma);
							tipoPromo=promoValues.getTipoPromo();
							if(tipoPromo.equalsIgnoreCase("")){
								addPedidoTemporal(cliente_id,pedido_id,cursorGetSugeridos.getString(0), cursorGetSugeridos.getString(1), String.valueOf((cursorGetSugeridos.getDouble(2) *aplicaIva)), cursorGetSugeridos.getString(3),false,"",0,"");
							}else{
								if(tipoPromo.equalsIgnoreCase("valor")){
									addPedidoTemporal(cliente_id,pedido_id,cursorGetSugeridos.getString(0), cursorGetSugeridos.getString(1), String.valueOf((cursorGetSugeridos.getDouble(2) *aplicaIva)), cursorGetSugeridos.getString(3),true,promoValues.getPromocionId(),promoValues.getPorcDesc(),"");
								}else{
									if(promoValues.getCantidadBonificada()==0){
										addPedidoTemporal(cliente_id,pedido_id,cursorGetSugeridos.getString(0), cursorGetSugeridos.getString(1), String.valueOf((cursorGetSugeridos.getDouble(2) *aplicaIva)), cursorGetSugeridos.getString(3),false,promoValues.getPromocionId(),0,"");
									}else{
										addPedidoTemporal(cliente_id,pedido_id,cursorGetSugeridos.getString(0), cursorGetSugeridos.getString(1), String.valueOf((cursorGetSugeridos.getDouble(2) *aplicaIva)), cursorGetSugeridos.getString(3),true,promoValues.getPromocionId(),0,"");
										addPedidoTemporal(cliente_id,pedido_id,promoValues.getProductoIdBonificar(), "0", "0", String.valueOf(promoValues.getCantidadBonificada()),true,promoValues.getPromocionId(),0,"S");
									}
								}
							}
						}while(cursorGetSugeridos.moveToNext());
						cursorGetSugeridos.close();
					}
				}
			}catch(Exception e){
				Log.e("info","loadPedidoSugerido error "+e);
				res=false;
			}finally{
				if(cursorGetSugeridos!=null){
					cursorGetSugeridos.close();
				}
				if(cursorListaPrecio!=null){
					cursorListaPrecio.close();
				}
			}
			return res;
		}
	}
	
	public boolean loadPedidoSugeridoBlanco(String cliente_id,String pedido_id){
		synchronized (Lock) {
			open();
			boolean res=true;
			int aplicaIva=0;
			Cursor cursorGetSugeridos=null;
			Cursor cursorListaPrecio=null;
			String sqlSug;
			try{
				cursorListaPrecio=db.rawQuery("SELECT campo_referencia FROM esd_lista_precio AS lp JOIN esd_cliente AS c ON lp.lista_precio_id = c.lista_precio_id WHERE cliente_id=?", new String [] {cliente_id});			
				if(cursorListaPrecio.moveToFirst()){
					sqlSug="SELECT p.producto_id,"+cursorListaPrecio.getString(0)+",porcentaje_iva,cantidad_sugerida FROM esd_producto AS p JOIN esd_pedido_sugerido AS ps ON p.producto_id=ps.producto_id WHERE ps.cliente_id='"+cliente_id+"'";
					cursorGetSugeridos=db.rawQuery(sqlSug, new String [] {});			
					aplicaIva=(AplicarIVA(cliente_id))?1:0;
					if(cursorGetSugeridos.moveToFirst()){
						do{
							addPedidoTemporal(cliente_id,
									pedido_id,
									cursorGetSugeridos.getString(0), 
									cursorGetSugeridos.getString(1),
									String.valueOf((cursorGetSugeridos.getDouble(2) *aplicaIva)), 
									"0",false,"",0,"");
						}while(cursorGetSugeridos.moveToNext());
						cursorGetSugeridos.close();
					}
				}
			}catch(Exception e){
				Log.e("info","loadPedidoSugerido error "+e);
				res=false;
			}finally{
				if(cursorGetSugeridos!=null){
					cursorGetSugeridos.close();
				}
				if(cursorListaPrecio!=null){
					cursorListaPrecio.close();
				}
			}
			return res;
		}
	}
	
	public boolean checkAndAddPromoFinal(ArrayList<String> listTomaFinal, String  cliente_id, String pedido_id){
		synchronized (Lock) {
			open();
			int cantidadBonificada=0;
			int productosCounter=0;
			boolean res=true;
			Promo auxPromo=new Promo();
			Cursor cursorEncabezadoPromo=null;
			Cursor cursorListaPromo=null;
			Cursor productos=db.rawQuery("SELECT producto_id, cantidad_pedida FROM est_pedido_detalle", new String [] {});
			Log.i("info","checkAndAddPromoFinal 0");
			for(String promo_id:listTomaFinal){
				Log.i("info","checkAndAddPromoFinal promo_id "+promo_id);
				productosCounter=0;
				cursorEncabezadoPromo=db.rawQuery("SELECT * FROM esd_promocion_enc WHERE promocion_id=?", new String [] {promo_id});
				cursorListaPromo=db.rawQuery("SELECT producto_id FROM esd_promocion_det WHERE promocion_id=?", new String [] {promo_id});
				if(productos.moveToFirst()){
					do{
						Log.i("info","checkAndAddPromoFinal 2");
						if(cursorListaPromo.moveToFirst()){
							do{
								Log.i("info","checkAndAddPromoFinal 3");
								Log.i("info","checkAndAddPromoFinal 3 productos.getString(0) "+productos.getString(0));
								Log.i("info","checkAndAddPromoFinal 3 cursorListaPromo.getString(0) "+cursorListaPromo.getString(0));
								Log.i("info","checkAndAddPromoFinal 3 productos.getString(0).equalsIgnoreCase(cursorListaPromo.getString(0)) "+productos.getString(0).equalsIgnoreCase(cursorListaPromo.getString(0)));

								if(productos.getString(0).equalsIgnoreCase(cursorListaPromo.getString(0))){
									productosCounter=+productosCounter+productos.getInt(1);
								}
							}while(cursorListaPromo.moveToNext());
						}
					}while(productos.moveToNext());
				}
					
				Log.i("info","checkAndAddPromoFinal productosCounter "+productosCounter);	
				if(cursorEncabezadoPromo.moveToFirst()){
					Log.i("info","checkAndAddPromoFinal 5");
					Log.i("info","checkAndAddPromoFinal 5 productosCounter"+productosCounter);
					Log.i("info","checkAndAddPromoFinal 5 cursorEncabezadoPromo.getInt(cursorEncabezadoPromo.getColumnIndex(cantidad_pedida_minima))"+cursorEncabezadoPromo.getInt(cursorEncabezadoPromo.getColumnIndex("cantidad_pedida_minima")));
					if(productosCounter >= cursorEncabezadoPromo.getInt(cursorEncabezadoPromo.getColumnIndex("cantidad_pedida_minima"))){
						Log.i("info","checkAndAddPromoFinal 6");
						if(cursorEncabezadoPromo.getString(cursorEncabezadoPromo.getColumnIndex("aplica_x_rangos")).equalsIgnoreCase("S")){
							Log.i("info","checkAndAddPromoFinal 7");
							cantidadBonificada= (int) Math.floor(productosCounter*cursorEncabezadoPromo.getDouble(cursorEncabezadoPromo.getColumnIndex("porc_descuento"))/100);
							
//							Determinar la cantidad a bonificar:
//								Tomar la parte entera (acumulado de cantidad * esd_promocion_enc.porc_descuento / 100)
							
							
						}else{
							Log.i("info","checkAndAddPromoFinal 8");
							cantidadBonificada= (int) Math.floor((productosCounter/cursorEncabezadoPromo.getDouble(cursorEncabezadoPromo.getColumnIndex("valor_multiplo")))*cursorEncabezadoPromo.getDouble(cursorEncabezadoPromo.getColumnIndex("cantidad_bonificacion")));

//							Determinar la cantidad a bonificar:
//							Tomar la parte entera ((acumulado de cantidad / esd_promocion_enc.valor_multiplo) * esd_promocion_enc.cantidad_bonificacion)
						}
						Log.i("info","checkAndAddPromoFinal 9");

						auxPromo.setProductoIdBonif(cursorEncabezadoPromo.getString(cursorEncabezadoPromo.getColumnIndex("cod_producto_especie")));
						auxPromo.setCantBonificada(cantidadBonificada);
						
//						ï¿¼Llenar la clase con los siguientes datos:
//							* CoÌ�digo del producto a bonificar = esd_promocion_enc.cod_producto_especie 
	//						* Cantidad bonificada = cantidad a bonificar
					}
				}
				Log.i("info","checkAndAddPromoFinal 10");
				Log.i("info","auxPromo 10 "+auxPromo.getCantidadBonificada());
				Log.i("info","auxPromo 10 "+auxPromo.getProductoIdBonificar());

				if(auxPromo.getCantidadBonificada()>0){
					Log.i("info","checkAndAddPromoFinal 11");
					addPedidoTemporal(cliente_id,pedido_id,auxPromo.getProductoIdBonificar(), "0", "0", String.valueOf(auxPromo.getCantidadBonificada()),true,promo_id,0,"S");
//						return res;
					break;
				}
				
			}
			if(cursorEncabezadoPromo!=null){
				cursorEncabezadoPromo.close();
			}

			if(cursorListaPromo!=null){
				cursorListaPromo.close();
			}

			if(productos!=null){
				productos.close();
			}
			return res;
		}
	}

	public boolean addPedidoTemporal(String cliente_id,String pedido_id, String producto_id, String precio_base, String porc_iva, String cantidad, float descuento_porc,boolean hasPromo, String promo_id, float porcPromo){
		synchronized (Lock) {
			open();
			boolean res=true;
			String sql;
			String isPromo=hasPromo?"*":"";
			float total;
			float cant=Float.valueOf(cantidad);
			float vBase=Float.valueOf(precio_base);
			float bruto=cant*vBase;
			float iva=Float.valueOf(porc_iva);
			float descuento=(bruto*descuento_porc/100);
			float vPromo=(bruto*porcPromo/100);
			total=(bruto+(bruto*iva/100)-descuento-vPromo);
			sql="INSERT INTO est_pedido_detalle VALUES ((SELECT valor FROM esd_rango WHERE id=4),'"+
			pedido_id+"','"+
			producto_id+"','" +
			isPromo+"','" +
			promo_id+"'," +
			porcPromo +","+
			precio_base+","+
			bruto+","+
			iva+","+
			cantidad+"," +
			descuento+"," +
			vPromo+","+
			total+")";
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=4) WHERE id=4";
			try{
				db.execSQL(sql);
				db.execSQL(sqlUpdateRango);
			}catch(Exception e){
				db.execSQL(sqlUpdateRango);
				Log.e("info","insertPedidoTemporal error "+e);
				res=false;
			}
			return res;
		}
	}
	
	public boolean addPedidoTemporal(
		String cliente_id,
		String pedido_id, 
		String producto_id,
		String precio_base,
		String porc_iva,
		String cantidad,
		boolean hasPromo,
		String promo_id,
		double porcPromo,
		String isPromoEspecie){
		synchronized (Lock) {
			open();
			boolean res=true;
			String sql;
			double total;
			double descuento;
			String isPromo=hasPromo?"*":"";
			String promo_id_chequeada=hasPromo?promo_id:"0";
			double cant=Double.valueOf(cantidad);
			double vBase=Double.valueOf(precio_base);
			double bruto=cant*vBase;
			double iva=Double.valueOf(porc_iva);
			Cursor cursor=db.rawQuery("SELECT  100-100*((1-porc_descuento/100)*(1-porc_adicional/100)) FROM esd_grupo_cliente AS g JOIN esd_cliente AS c ON g.grupo_cliente_id=c.grupo_cliente_id AND c.cliente_id=?", new String [] {cliente_id});
//			float vPromo=(bruto*porcPromo/100);
			double vPromo=(bruto*porcPromo/100);
			if(cursor.moveToFirst()){
				descuento=((bruto-vPromo)*cursor.getDouble(0)/100);
			}else{
				descuento=0;
			}
			total=(bruto+((bruto-descuento-vPromo)*iva/100)-descuento-vPromo);
			sql="INSERT INTO est_pedido_detalle VALUES ((SELECT valor FROM esd_rango WHERE id=4),'"+
			pedido_id+"','"+
			producto_id+"','" +
			isPromo+"','" +
			promo_id_chequeada+"'," +
			porcPromo +","+
			precio_base+","+
			bruto+","+
	//		((iva*total)/100)+","+
			((bruto-descuento-vPromo)*iva/100)+","+
			cantidad+"," +
			descuento+"," +
			vPromo+","+
			total+",'"+
			isPromoEspecie+"')";
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=4) WHERE id=4";
			try{
				db.execSQL(sql);
				db.execSQL(sqlUpdateRango);
			}catch(Exception e){
				db.execSQL(sqlUpdateRango);
				Log.e("info","insertPedidoTemporal error "+e);
				res=false;
			}finally{
				if(cursor !=null){
					cursor.close();
				}
			}
			return res;
		}
	}
	
	public boolean isDataInPedidoTemp(){
		synchronized (Lock) {
			open();
			boolean res=false; 
			Cursor cursor=db.rawQuery("SELECT COUNT(*) FROM est_pedido_detalle", new String [] {});
			if(cursor.moveToFirst()){
				if(cursor.getInt(0)>0){
					res=true;
				}
			}
			return res;
		}

		
	}
	
	public boolean modifyPedidoTemporal(String cliente_id,String id, double valor_bruto, double iva, String cantidad, boolean hasPromo, String promo_id, double porcPromo){
		synchronized (Lock) {
			open();
			String isPromo=hasPromo?"*":"";
			String promo_id_chequeada=hasPromo?promo_id:"0";
			boolean res=true;
			String sql="";
			double descuento=0;
			double vTotal=0;
			double vPromo=(valor_bruto*porcPromo/100);
			try{
				Cursor cursor=db.rawQuery("SELECT  100-100*((1-porc_descuento/100)*(1-porc_adicional/100)) FROM esd_grupo_cliente AS g JOIN esd_cliente AS c ON g.grupo_cliente_id=c.grupo_cliente_id AND c.cliente_id=?", new String [] {cliente_id});
				if(cursor.moveToFirst()){
					descuento=((valor_bruto-vPromo)*cursor.getDouble(0)/100);
				}
				cursor=db.rawQuery("SELECT articulo_promocion,numero_promocion FROM esd_pedido_detalle WHERE id=?", new String [] {id});
				if(cursor.moveToFirst() && cursor.getString(0).equalsIgnoreCase("*")){
					cursor=db.rawQuery("SELECT tipo_promocion,cod_producto_especie FROM esd_promocion_enc WHERE promocion_id=?", new String [] {cursor.getString(1)});
					if(cursor.moveToFirst() && cursor.getString(0).equalsIgnoreCase("especie")){
						sql="DELETE FROM est_pedido_detalle WHERE producto_id='"+cursor.getString(1)+"' AND precio_base=0" ;
						db.execSQL(sql);
					}
				}
				vTotal=(valor_bruto+((valor_bruto-descuento-vPromo)*iva/100)-descuento-vPromo);
				sql="UPDATE est_pedido_detalle SET valor_bruto='"+valor_bruto+"'," +
						" articulo_promocion='"+isPromo+"'," +
						" numero_promocion='"+promo_id_chequeada+"'," +
						" cantidad_pedida='"+cantidad+"'," +
						" valor_descuento='"+descuento+"'," +
						" valor_promocion='"+vPromo+"'," +
						" iva='"+((valor_bruto-descuento-vPromo)*iva/100)+"'," +
						" valor_total='"+vTotal+"' WHERE id='"+id+"'";
				db.execSQL(sql);
			}catch(Exception e){
				Log.e("info","uodatePedidoTemporal error "+e);
				res=false;
			}
			return res;
		}
	}


	public boolean addPedido(String cliente_id, String pedido_id, String estado, String fecha_entrega, String oc, String obs_pedido, String obs_fact){
		synchronized (Lock) {
			open();
			boolean res=true;
			String codestado="";
			Cursor cursorCliente=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, porc_descuento,porc_adicional FROM esd_grupo_cliente AS g JOIN esd_cliente AS c ON g.grupo_cliente_id=c.grupo_cliente_id AND c.cliente_id=?", new String [] {cliente_id});
			Cursor cursorPedidoDetalle=db.rawQuery("SELECT SUM(valor_bruto),SUM(valor_descuento),SUM(valor_promocion),SUM(iva),SUM(valor_total),(SELECT SUM(cantidad_pedida) FROM est_pedido_detalle WHERE valor_total>0 AND numero_pedido=?) FROM est_pedido_detalle WHERE numero_pedido=?", new String [] {pedido_id,pedido_id});
			if(cursorCliente.moveToFirst() && cursorPedidoDetalle.moveToFirst()){
				if(estado.equalsIgnoreCase("FACTURAR")){
					codestado="13";
				}else if(estado.equalsIgnoreCase("VENTAS")){
					codestado="70";
				}else if(estado.equalsIgnoreCase("CREDITOS")){
					codestado="14";
				}else if(estado.equalsIgnoreCase("ANULADO")){
					codestado="71";
				}
				
				Log.i("info", ""+cursorPedidoDetalle.getString(4));
				Log.i("info", ""+cursorPedidoDetalle.getDouble(4));
				
				String sqlDown = "INSERT INTO esd_pedido VALUES(" +
					pedido_id+"," +
					pedido_id+",'" +
					cursorCliente.getString(0)+"','" +
					cursorCliente.getString(1)+"','" +
					cursorCliente.getString(2)+"','" +
					cursorCliente.getString(3)+"','" +
					cliente_id+"'," +
					"datetime('now','localtime')," +
					(cursorPedidoDetalle.getDouble(0)-cursorPedidoDetalle.getDouble(2))+"," +
					cursorPedidoDetalle.getDouble(1)+"," +
					cursorPedidoDetalle.getDouble(2)+"," +
					cursorPedidoDetalle.getDouble(3)+"," +
					cursorPedidoDetalle.getDouble(4)+",'" +
					fecha_entrega+"'," +
					cursorCliente.getString(4)+"," +
					cursorCliente.getString(5)+",'" +
					codestado+"'," +
					cursorPedidoDetalle.getString(5)+",'" +
					oc+"','" +
					obs_pedido+"','" +
					obs_fact+"')";
				String sqlUp = "INSERT INTO esu_pedido VALUES(" +
						pedido_id+"," +
						pedido_id+",'" +
						cursorCliente.getString(0)+"','" +
						cursorCliente.getString(1)+"','" +
						cursorCliente.getString(2)+"','" +
						cursorCliente.getString(3)+"','" +
						cliente_id+"',datetime('now','localtime')," +
						(cursorPedidoDetalle.getDouble(0)-cursorPedidoDetalle.getDouble(2))+"," +
						cursorPedidoDetalle.getDouble(1)+"," +
						cursorPedidoDetalle.getDouble(2)+"," +
						cursorPedidoDetalle.getDouble(3)+"," +
						cursorPedidoDetalle.getDouble(4)+",'" +
						fecha_entrega+"'," +
						cursorCliente.getString(4)+"," +
						cursorCliente.getString(5)+",'" +
						codestado+"'," +
						cursorPedidoDetalle.getString(5)+",'" +
						oc+"','" +
						obs_pedido+"','" +
						obs_fact+"','C', datetime('now','localtime'))";
				String sqlDetalleDown = "INSERT INTO esd_pedido_detalle SELECT * FROM est_pedido_detalle WHERE numero_pedido='"+pedido_id+"' AND cantidad_pedida > 0";
				String sqlDetalleUp = "INSERT INTO esu_pedido_detalle SELECT *,'C', datetime('now','localtime') FROM est_pedido_detalle WHERE numero_pedido='"+pedido_id+"' AND cantidad_pedida > 0";
				String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=3) WHERE id=3";
				try{
					db.execSQL(sqlDown);
					db.execSQL(sqlUp);
					db.execSQL(sqlDetalleDown);
					db.execSQL(sqlDetalleUp);
					db.execSQL(sqlUpdateRango);
				}catch(Exception e){
					db.execSQL(sqlUpdateRango);
					Log.e("info","Error en addPedido "+e);
					res=false;
				}
			}
			return res;
		}
	}
	
	public int getNArticulosTemp(String pedido_id){
		synchronized (Lock) {
			open();
			int res=0;
			Cursor cursor=db.rawQuery("SELECT SUM(cantidad_pedida) FROM est_pedido_detalle WHERE valor_total>0 AND numero_pedido=?", new String [] {pedido_id});			
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
			}
			return res;
		}
	}
	
	public Cursor getArtiulosPrimerGrado(String producto_id, String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			Cursor cursorListaPrecio=db.rawQuery("SELECT DISTINCT campo_referencia FROM esd_lista_precio AS lp JOIN esd_cliente AS c WHERE  lp.lista_precio_id = c.lista_precio_id AND cliente_id=?", new String [] {cliente_id});			
			if(cursorListaPrecio.moveToFirst()){
				cursor=db.rawQuery("SELECT P.producto_id,P.nombre, TP.cantidad_pedida, TP.id, P.porcentaje_iva, "+cursorListaPrecio.getString(0) +" " +
								"FROM esd_producto AS P LEFT JOIN est_pedido_detalle AS TP ON P.producto_id=TP.producto_id " +
								"WHERE (TP.valor_total!=0 OR TP.articulo_promocion!='*' OR TP.valor_total IS NULL) AND codigo_primer_grado IN (SELECT codigo_primer_grado FROM esd_producto WHERE producto_id=? AND CODIGO_PRIMER_GRADO != '') AND P.PRODUCTO_ID != ? AND "+cursorListaPrecio.getString(0)+">0", new String [] {producto_id,producto_id});
			}
			return cursor;
		}
	}

	public Cursor getArtiulosSegundoGrado(String producto_id, String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			Cursor cursorListaPrecio=db.rawQuery("SELECT DISTINCT campo_referencia FROM esd_lista_precio AS lp JOIN esd_cliente AS c WHERE  lp.lista_precio_id = c.lista_precio_id AND cliente_id=?", new String [] {cliente_id});			
			if(cursorListaPrecio.moveToFirst()){
				cursor=db.rawQuery("SELECT " +
						"P.producto_id," +
						"P.nombre, " +
						"TP.cantidad_pedida," +
						"TP.id,P.porcentaje_iva, " +
						""+cursorListaPrecio.getString(0) +" " +
						"FROM esd_producto AS P LEFT JOIN est_pedido_detalle AS TP ON P.producto_id=TP.producto_id " +
						"WHERE (TP.valor_total!=0 OR TP.articulo_promocion!='*' OR TP.valor_total IS NULL) AND codigo_segundo_grado IN (SELECT codigo_segundo_grado FROM esd_producto WHERE producto_id=? AND CODIGO_SEGUNDO_GRADO != '') " +
						"AND P.producto_id!=? AND NOT EXISTS " +
						"(SELECT * FROM ESD_PRODUCTO P1 INNER JOIN ESD_PRODUCTO P2 ON (P1.CODIGO_PRIMER_GRADO = P2.CODIGO_PRIMER_GRADO) " +
						"WHERE P2.PRODUCTO_ID = ? AND P.CODIGO_PRIMER_GRADO = P1.CODIGO_PRIMER_GRADO AND P.CODIGO_PRIMER_GRADO != '') AND "+cursorListaPrecio.getString(0)+">0", new String [] {producto_id,producto_id,producto_id});
			}
			return cursor;
		}
	}
	
	public boolean deletePedido(String pedido_id){
		synchronized (Lock) {
			open();
			boolean res=true;
			Cursor cursor;
			String sqlDeleteUp="";
			String sqlGetPedido="SELECT * FROM esd_pedido WHERE numero_pedido='"+pedido_id+"'";
			String sqlDeleteDown="DELETE FROM esd_pedido WHERE numero_pedido='"+pedido_id+"'";
			String sqlDeleteDetalleDown="DELETE FROM esd_pedido_detalle WHERE numero_pedido='"+pedido_id+"'";
			String sqlUpdateRango1="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=3) WHERE id=3";
			try{
				cursor=db.rawQuery(sqlGetPedido, new String [] {});
				if(cursor.moveToFirst()){
					sqlDeleteUp="INSERT INTO esu_pedido VALUES((SELECT valor FROM esd_rango WHERE id=3),'"+
							cursor.getString(1)+"','"+
							cursor.getString(2)+"','"+
							cursor.getString(3)+"','"+
							cursor.getString(4)+"','"+
							cursor.getString(5)+"','"+
							cursor.getString(6)+"','"+
							cursor.getString(7)+"','"+
							cursor.getString(8)+"','"+
							cursor.getString(9)+"','"+
							cursor.getString(10)+"','"+
							cursor.getString(11)+"','"+
							cursor.getString(12)+"','"+
							cursor.getString(13)+"','"+
							cursor.getString(14)+"','"+
							cursor.getString(15)+"','"+
							cursor.getString(16)+"','"+
							cursor.getString(17)+"','"+
							cursor.getString(18)+"','"+
							cursor.getString(19)+"','"+
							cursor.getString(20)+"','A',datetime('now','localtime'))";
							db.execSQL(sqlDeleteUp);
							db.execSQL(sqlUpdateRango1);
				}
				db.execSQL(sqlDeleteDown);
				db.execSQL(sqlDeleteDetalleDown);
			}catch(Exception e){
				db.execSQL(sqlUpdateRango1);
				Log.e("info","error en deletePedido "+e);
				res=false;
			}		
			return res;
		}
	}
	
	public Cursor getProductosDejadosDePedir(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			String sql="";
			try{
				cursor=db.rawQuery("SELECT DISTINCT campo_referencia FROM esd_lista_precio AS lp JOIN esd_cliente AS c WHERE  lp.lista_precio_id = c.lista_precio_id AND cliente_id=?", new String [] {cliente_id});			
				if(cursor.moveToFirst()){
					sql="SELECT DISTINCT P.producto_id, nombre, "+cursor.getString(0) +",porcentaje_iva FROM esd_producto AS P JOIN esd_pedido_sugerido AS S ON P.producto_id=S.producto_id WHERE S.cliente_id='"+cliente_id+"' AND P.producto_id NOT IN (SELECT producto_id FROM est_pedido_detalle)";
					Log.i("info",sql);
					cursor=db.rawQuery(sql, new String [] {});
				}
			}catch(Exception e){
				Log.e("info","error en getProductosDejadosDePedir "+e);
			}				
			return cursor;
		}
	}
	
	public Cursor getProductosDejadosDePedirTemporada(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			String sql="";
			try{
				cursor=db.rawQuery("SELECT DISTINCT campo_referencia FROM esd_lista_precio AS lp JOIN esd_cliente AS c WHERE  lp.lista_precio_id = c.lista_precio_id AND cliente_id=?", new String [] {cliente_id});			
				if(cursor.moveToFirst()){
					sql="SELECT DISTINCT P.producto_id, nombre, "+cursor.getString(0) +",porcentaje_iva FROM esd_producto AS P JOIN esd_producto_temporada AS T ON P.producto_id=T.producto_id WHERE T.producto_id NOT IN (SELECT producto_id FROM est_pedido_detalle)";
					cursor=db.rawQuery(sql, new String [] {});
				}
			}catch(Exception e){
				Log.e("info","error en getProductosDejadosDePedir "+e);
			}				
			return cursor;
		}
	}

	public Cursor getProductosDejadosDePedirPromociones(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			String sql="";
			try{
				cursor=db.rawQuery("SELECT DISTINCT campo_referencia, c.cadena_id, c.canal_id, c.distrito_id, c.tipo_cliente_id FROM esd_lista_precio AS lp JOIN esd_cliente AS c WHERE  lp.lista_precio_id = c.lista_precio_id AND cliente_id=?", new String [] {cliente_id});			
				if(cursor.moveToFirst()){
					sql="SELECT DISTINCT " +
						"P.producto_id, " +
						"nombre," +
						cursor.getString(0) +"," +
						"porcentaje_iva " +
						"FROM " +
						"esd_producto AS P JOIN esd_promocion_det AS T ON P.producto_id=T.producto_id " +
						"WHERE P.producto_id NOT IN (SELECT producto_id FROM est_pedido_detalle) AND T.promocion_id IN (" +
						"SELECT promocion_id FROM esd_promocion_x_cadena WHERE cadena_id=? " +
						"UNION " +
						"SELECT promocion_id FROM esd_promocion_x_canal WHERE canal_id=? " +
						"UNION " +
						"SELECT promocion_id FROM esd_promocion_x_cliente WHERE cliente_id=? " +
						"UNION " +
						"SELECT promocion_id FROM esd_promocion_x_distrito WHERE distrito_id=? " +
						"UNION " +
						"SELECT promocion_id FROM esd_promocion_x_tipo_cliente WHERE tipo_cliente_id=?)";
					cursor=db.rawQuery(sql, new String [] {cursor.getString(1),cursor.getString(2),cliente_id,cursor.getString(3),cursor.getString(4)});
				}
			}catch(Exception e){
				Log.e("info","error en getProductosDejadosDePedir "+e);
			}				
			return cursor;
		}
	}

	//Funcion que retorna la clase que contiene la promocion
	public Promo getPromoValue(String producto_id, int cantidad, ArrayList<String> listToma){
		synchronized (Lock) {
			open();
			Promo res=new Promo();
			String sql="";
			String tipoPromo="";
			String aplicaXRango="";
			int cantidadBonificar=0;
			Cursor cursor=null;
			Cursor cursorPromoData=null;
			try{
				for(String promocion_id:listToma){
					sql="SELECT COUNT() FROM esd_promocion_det WHERE promocion_id=? AND producto_id=?";
					cursor=db.rawQuery(sql, new String [] {promocion_id,producto_id});
					if(cursor.moveToFirst() && (cursor.getInt(0) >0)){
						sql="SELECT cantidad_pedida_minima, tipo_promocion, porc_descuento, aplica_x_rangos, cantidad_bonificacion, valor_multiplo, cod_producto_especie FROM esd_promocion_enc WHERE promocion_id=?";
						cursorPromoData=db.rawQuery(sql, new String [] {promocion_id});
						if(cursorPromoData.moveToFirst() && (cantidad >= cursorPromoData.getInt(0))){
							tipoPromo=cursorPromoData.getString(1);
							if(tipoPromo.equalsIgnoreCase("valor")){
								res.setTipoPromo("VALOR");
								res.setPromocionId(promocion_id);
								res.setPorcDesc(cursorPromoData.getDouble(2));
							}else if(tipoPromo.equalsIgnoreCase("especie")){
								res.setTipoPromo("ESPECIE");
								res.setPromocionId(promocion_id);
								res.setProductoIdBonif(cursorPromoData.getString(6));
								aplicaXRango=cursorPromoData.getString(3);
								if(aplicaXRango.equalsIgnoreCase("S")){
									cantidadBonificar=(int) Math.floor(cantidad * cursorPromoData.getDouble(2)/100 ) ;
								}else if(aplicaXRango.equalsIgnoreCase("N")){
									cantidadBonificar=(int) (Math.floor(cantidad / cursorPromoData.getDouble(5))*cursorPromoData.getDouble(4));
								}
								res.setCantBonificada(cantidadBonificar);
							}
						}
					}
				}
			}catch(Exception e){
				Log.e("getPromoValue","error "+e);
			}finally{
			   if(cursor != null){
				   cursor.close();
			   }
			   if(cursorPromoData!=null){
				   cursorPromoData.close();
			   }
			}
			return res;
		}
	}
	
	public Cursor getDescuentos(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT porc_descuento,porc_adicional FROM esd_grupo_cliente AS g JOIN esd_cliente AS c ON g.grupo_cliente_id=c.grupo_cliente_id AND c.cliente_id=?", new String [] {cliente_id});
			return cursor;
		}
	}
	
	public Cursor getDatosTemporales(String pedido_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT SUM(valor_bruto), SUM(valor_descuento), SUM(valor_promocion), SUM(iva), SUM(valor_bruto), SUM(cantidad_pedida) FROM est_pedido_detalle", new String [] {});
			return cursor;
		}
	}

	public Cursor getCountPromoEspecieTemp(String pedido_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT SUM(cantidad_pedida) FROM est_pedido_detalle WHERE articulo_promocion='*' AND precio_base=0 AND numero_pedido=? ", new String [] {pedido_id});
			return cursor;
		}
	}

	public Cursor getCountPromoEspecie(String pedido_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT SUM(cantidad_pedida) FROM esd_pedido_detalle WHERE articulo_promocion='*' AND precio_base=0 AND numero_pedido=? ", new String [] {pedido_id});
			return cursor;
		}
	}
	
	public Cursor getNumberOfGravablesTemp(String pedido_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT DISTINCT porcentaje_iva FROM est_pedido_detalle AS PD JOIN esd_producto AS P ON PD.producto_id=P.producto_id WHERE numero_pedido=?", new String [] {pedido_id});
			return cursor;
		}
	}

	public Cursor getNumberOfGravables(String pedido_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT DISTINCT porcentaje_iva FROM esd_pedido_detalle AS PD JOIN esd_producto AS P ON PD.producto_id=P.producto_id WHERE numero_pedido=?", new String [] {pedido_id});
			return cursor;
		}
	}

	public Cursor getNumberOfGravablesSum(String pedido_id,String numGra){
		synchronized (Lock) {
			open();
			String sql="SELECT SUM(iva) FROM esd_pedido_detalle AS PD JOIN esd_producto AS P ON PD.producto_id=P.producto_id WHERE numero_pedido='"+pedido_id+"' AND porcentaje_iva="+numGra;
			Cursor cursor=db.rawQuery(sql, new String [] {});
			return cursor;
		}
	}
	
	public Cursor getNumberOfGravablesSumTemp(String pedido_id,String numGra){
		synchronized (Lock) {
			open();
			String sql="SELECT SUM(iva) FROM  est_pedido_detalle AS PD JOIN esd_producto AS P ON PD.producto_id=P.producto_id WHERE porcentaje_iva="+numGra;
			Log.i("info",sql);
//			Cursor cursor=db.rawQuery(SELECT SUM(iva*(valor_bruto-valor_descuento-valor_promocion)/100) FROM est_pedido_detalle WHERE numero_pedido=? AND iva=?", new String [] {pedido_id,numGra});
			Cursor cursor=db.rawQuery(sql, new String [] {});
			return cursor;
		}
	}

	public Cursor getPedidoObsAndDate(String pedido_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT DATE(fecha_entrega), observacion_pedido, observacion_factura, tipo_documento_id, orden_compra FROM esu_pedido WHERE numero_pedido=? ORDER BY fecha_registro DESC", new String [] {pedido_id});
			return cursor;
		}
	}
	
	public boolean moveFromEsdPedidoToEstPedido(String pedido_id){
		synchronized (Lock) {
			open();
			boolean res=true;
			try{
				db.execSQL("INSERT INTO est_pedido_detalle SELECT * FROM esd_pedido_detalle WHERE numero_pedido=?", new String [] {pedido_id});
			}catch(Exception e){
				Log.e("info","Error en moveFromEsdPedidoToEstPedido "+e);
				res=false;
			}
			return res;
		}
	}
	
	public boolean saveModifyPedido(String cliente_id, String pedido_id, String estado, String fecha_entrega, String oc, String obs_pedido, String obs_fact){
		synchronized (Lock) {
			open();
			boolean res=true;
			boolean cen=false;
			String sql="";
			String sqlDown="";
			String sqlUp="";
			String sqlUpdateDetalleRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=4) WHERE id=4";
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=3) WHERE id=3";
			String codestado="";
			Cursor pedidosTemp; 
			Cursor pedidosOriginal;
			Cursor aux;
			Cursor cursorPedidoDetalle;
			try{
				pedidosOriginal=db.rawQuery("SELECT * FROM esd_pedido_detalle WHERE numero_pedido=?", new String [] {pedido_id});
				pedidosTemp=db.rawQuery("SELECT * FROM est_pedido_detalle WHERE numero_pedido=?", new String [] {pedido_id});
				if(pedidosTemp.moveToFirst()){
					do{
						if(pedidosTemp.getDouble(7)!=0){
							sql="SELECT producto_id, valor_bruto FROM esd_pedido_detalle WHERE numero_pedido='"+pedidosTemp.getString(1)+"' AND producto_id='"+pedidosTemp.getString(2)/*+"' AND articulo_promocion='"+pedidosTemp.getString(3)*/+"'";
						}else{
							sql="SELECT producto_id, valor_bruto FROM esd_pedido_detalle WHERE numero_pedido='"+pedidosTemp.getString(1)+"' AND producto_id='"+pedidosTemp.getString(2)/*+"' AND articulo_promocion='"+pedidosTemp.getString(3)*/+"' AND valor_total=0";
						}
						
						aux=db.rawQuery(sql, new String [] {});
						if(aux.getCount()==0){
								sqlDown= "INSERT INTO esd_pedido_detalle VALUES('" +
										pedidosTemp.getString(0)+"','" +
										pedidosTemp.getString(1)+"','" +
										pedidosTemp.getString(2)+"','" +
										pedidosTemp.getString(3)+"','" +
										pedidosTemp.getString(4)+"','" +
										pedidosTemp.getString(5)+"','" +
										pedidosTemp.getString(6)+"','" +
										pedidosTemp.getString(7)+"','" +
										pedidosTemp.getString(8)+"','" +
										pedidosTemp.getString(9)+"','" +
										pedidosTemp.getString(10)+"','" +
										pedidosTemp.getString(11)+"','" +
										pedidosTemp.getString(12)+"','" +
										pedidosTemp.getString(13)+"')";
								sqlUp="INSERT INTO esu_pedido_detalle VALUES('" +
										pedidosTemp.getString(0)+"','" +
										pedidosTemp.getString(1)+"','" +
										pedidosTemp.getString(2)+"','" +
										pedidosTemp.getString(3)+"','" +
										pedidosTemp.getString(4)+"','" +
										pedidosTemp.getString(5)+"','" +
										pedidosTemp.getString(6)+"','" +
										pedidosTemp.getString(7)+"','" +
										pedidosTemp.getString(8)+"','" +
										pedidosTemp.getString(9)+"','" +
										pedidosTemp.getString(10)+"','" +
										pedidosTemp.getString(11)+"','" +
										pedidosTemp.getString(12)+"','" +
										pedidosTemp.getString(13)+"','C',datetime('now','localtime'))";
								db.execSQL(sqlDown);
								db.execSQL(sqlUp);
								db.execSQL(sqlUpdateDetalleRango);

								cen=true;
							}else{
								Log.i("info","test 5");
								sql="SELECT COUNT(*) FROM esu_pedido_detalle WHERE numero_pedido='"+pedidosTemp.getString(1)+"' AND producto_id='"+pedidosTemp.getString(2)/*+"' AND articulo_promocion='"+pedidosTemp.getString(3)*/+"' AND cantidad_pedida='"+pedidosTemp.getString(9)+"'";
								aux=db.rawQuery(sql, new String [] {});
								if(aux.moveToFirst()){
									if(aux.getInt(0)==0){
										sqlDown= "UPDATE esd_pedido_detalle SET " +
												"articulo_promocion='"+pedidosTemp.getString(3)+"'," +
												"numero_promocion='"+pedidosTemp.getString(4)+"'," +
												"porcentaje_promocion='"+pedidosTemp.getString(5)+"'," +
												"precio_base='"+pedidosTemp.getString(6)+"'," +
												"valor_bruto='"+pedidosTemp.getString(7)+"'," +
												"iva='"+pedidosTemp.getString(8)+"'," +
												"cantidad_pedida='"+pedidosTemp.getString(9)+"'," +
												"valor_descuento='"+pedidosTemp.getString(10)+"'," +
												"valor_promocion='"+pedidosTemp.getString(11)+"'," +
												"valor_total='"+pedidosTemp.getString(12)+"' WHERE id='"+pedidosTemp.getString(0)+"'";
										sqlUp="INSERT INTO esu_pedido_detalle VALUES((SELECT valor FROM esd_rango WHERE id=4),'" +
												pedidosTemp.getString(1)+"','" +
												pedidosTemp.getString(2)+"','" +
												pedidosTemp.getString(3)+"','" +
												pedidosTemp.getString(4)+"','" +
												pedidosTemp.getString(5)+"','" +
												pedidosTemp.getString(6)+"','" +
												pedidosTemp.getString(7)+"','" +
												pedidosTemp.getString(8)+"','" +
												pedidosTemp.getString(9)+"','" +
												pedidosTemp.getString(10)+"','" +
												pedidosTemp.getString(11)+"','" +
												pedidosTemp.getString(12)+"','" +
												pedidosTemp.getString(13)+"','M',datetime('now','localtime'))";
										db.execSQL(sqlDown);
										db.execSQL(sqlUp);
										db.execSQL(sqlUpdateDetalleRango);
										cen=true;
									}
								}
							}
					}while(pedidosTemp.moveToNext());
					
					if(pedidosOriginal.moveToFirst()){
						do{
							sql="SELECT COUNT(*) FROM est_pedido_detalle WHERE numero_pedido='"+pedidosOriginal.getString(1)+"' AND producto_id='"+pedidosOriginal.getString(2)+"' AND articulo_promocion='"+pedidosOriginal.getString(3)+"'";
							aux=db.rawQuery(sql, new String [] {});
							if(aux.moveToFirst()){
								if(aux.getInt(0)==0){
									sqlDown= "DELETE FROM esd_pedido_detalle WHERE id='"+pedidosOriginal.getString(0)+"'";
									sqlUp="INSERT INTO esu_pedido_detalle VALUES((SELECT valor FROM esd_rango WHERE id=4),'" +
											pedidosOriginal.getString(1)+"','" +
											pedidosOriginal.getString(2)+"','" +
											pedidosOriginal.getString(3)+"','" +
											pedidosOriginal.getString(4)+"','" +
											pedidosOriginal.getString(5)+"','" +
											pedidosOriginal.getString(6)+"','" +
											pedidosOriginal.getString(7)+"','" +
											pedidosOriginal.getString(8)+"','" +
											pedidosOriginal.getString(9)+"','" +
											pedidosOriginal.getString(10)+"','" +
											pedidosOriginal.getString(11)+"','" +
											pedidosOriginal.getString(12)+"','" +
											pedidosOriginal.getString(13)+"','A',datetime('now','localtime'))";
									db.execSQL(sqlDown);
									db.execSQL(sqlUp);
									db.execSQL(sqlUpdateDetalleRango);
									cen=true;
								}
							}
						}while(pedidosOriginal.moveToNext());
					}
				}
				//Modifica el pedido
				cursorPedidoDetalle=db.rawQuery("SELECT SUM(valor_bruto)," +
					"SUM(valor_descuento)," +
					"SUM(valor_promocion)," +
					"SUM(iva)," +
					"SUM(valor_total)," +
					"(SELECT SUM(cantidad_pedida) FROM est_pedido_detalle WHERE valor_total>0 AND numero_pedido=?)  " +
					"FROM est_pedido_detalle WHERE numero_pedido=?", new String [] {pedido_id,pedido_id});
				if(cursorPedidoDetalle.moveToFirst()){
					if(estado.equalsIgnoreCase("FACTURAR")){
						codestado="13";
					}else if(estado.equalsIgnoreCase("VENTAS")){
						codestado="70";
					}else if(estado.equalsIgnoreCase("CREDITOS")){
						codestado="14";
					}else if(estado.equalsIgnoreCase("ANULADO")){
						codestado="71";
					}
					
					sqlDown = "UPDATE esd_pedido SET valor_bruto='"+cursorPedidoDetalle.getString(0)+"'," +
						"valor_descuento='"+cursorPedidoDetalle.getString(1)+"'," +
						"valor_promocion='"+cursorPedidoDetalle.getString(2)+"'," +
						"valor_iva='"+cursorPedidoDetalle.getString(3)+"'," +
						"valor_total='"+cursorPedidoDetalle.getString(4)+"'," +
						"fecha_entrega='"+fecha_entrega+"'," +
						"tipo_documento_id='"+codestado+"'," +
						"numero_articulos='"+cursorPedidoDetalle.getString(5)+"'," +
						"orden_compra='"+oc+"'," +
						"observacion_pedido='"+obs_pedido+"'," +
						"observacion_factura='"+obs_fact+"' WHERE numero_pedido='"+pedido_id+"'";			
					db.execSQL(sqlDown);
					aux=db.rawQuery("SELECT * FROM esd_pedido WHERE numero_pedido=?", new String [] {pedido_id});
					if(aux.moveToFirst()){
						sqlUp = "INSERT INTO esu_pedido VALUES((SELECT valor FROM esd_rango WHERE id=3),'" +
								aux.getString(1)+"','" +
								aux.getString(2)+"','" +
								aux.getString(3)+"','" +
								aux.getString(4)+"','" +
								aux.getString(5)+"','" +
								aux.getString(6)+"','" +
								aux.getString(7)+"'," +
								aux.getDouble(8)+"," +
								aux.getDouble(9)+"," +
								aux.getDouble(10)+"," +
								aux.getDouble(11)+"," +
								aux.getDouble(12)+",'" +
								aux.getString(13)+"','" +
								aux.getString(14)+"','" +
								aux.getString(15)+"','" +
								aux.getString(16)+"','" +
								aux.getString(17)+"','" +
								aux.getString(18)+"','" +
								aux.getString(19)+"','" +
								aux.getString(20)+"','M', datetime('now','localtime'))";				
						db.execSQL(sqlUp);
						db.execSQL(sqlUpdateRango);
					}
				}
			}catch(Exception e){
				db.execSQL(sqlUpdateRango);
				Log.e("info","Error en saveModifyPedido "+e);
				res=false;
			}
			return res;
		}
	}
	
	public double getTotalPedidoTemp(String pedido_id){
		synchronized (Lock) {
			open();
			double res=0;
			Cursor cursor=db.rawQuery("SELECT SUM(valor_total) from est_pedido_detalle WHERE numero_pedido=?",  new String [] {pedido_id});
			if(cursor.moveToFirst()){
				res=cursor.getDouble(0);
			}
			if(cursor!=null){
				cursor.close();
			}
			return res;
		}
	}
	
	public double getValorPedidoMinimo(String cliente_id){
		synchronized (Lock) {
			open();
			double res=0;
			Cursor cursor=db.rawQuery("SELECT pedido_valor_minimo from esd_cliente WHERE cliente_id=?",  new String [] {cliente_id});
			if(cursor.moveToFirst()){
				res=cursor.getDouble(0);
			}
			if(cursor!=null){
				cursor.close();
			}
			return res;
		}
	}
	
	public Cursor getClienteInfoEstado(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor;
			cursor=db.rawQuery("SELECT cliente_edi,pedido_valor_minimo,tipo_venta,cupo_id,dias_docs_sin_legalizar,sector_id,distrito_id,dias_promedio_pago,fecha_creacion_erp,tiene_cheques_dev,tiene_sucursales FROM esd_cliente WHERE cliente_id=?", new String [] {cliente_id});
			return cursor;
		}
	}

	public int getCupoDiasPromedioPago(String cupo_id){
		synchronized (Lock) {
			open();
			int res=0;
			Cursor cursor=db.rawQuery("SELECT dias_promedio_pago FROM esd_cupo WHERE cupo_id=?", new String [] {cupo_id});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
			}
			return res;
		}
	}
	
	public Cursor getSectorCompromiso(String sector_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT pedir_compromiso,observacion_validacion FROM esd_sector WHERE sector_id=?", new String [] {sector_id});
			return cursor;
		}
	}
	
	
	public int getFacturasVenc(String cliente_id, int dias_lega){
		synchronized (Lock) {
			open();
			int res=0;
			Cursor cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cartera_documento WHERE cliente_id=? AND julianday(date('now','localtime')) > ? + julianday(fecha_vencimiento)", new String [] {cliente_id,String.valueOf(dias_lega)});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
			}
			return res;
		}
	}

	public int getDiasVencimientoCupo(String cupo_id){
		synchronized (Lock) {
			open();
			int res=0;
			Cursor cursor=db.rawQuery("SELECT dias_vencimiento_docs FROM esd_cupo WHERE cupo_id=?", new String [] {cupo_id});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
			}
			return res;
		}
	}

	public int getDiasVencimientoFact(String cupo_id){
		synchronized (Lock) {
			open();
			int res=0;
			Cursor cursor=db.rawQuery("SELECT dias_para_vencer FROM esd_cupo WHERE cupo_id=?", new String [] {cupo_id});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
			}
			return res;
		}
	}
	
	public int getFacturasValCupoCliente1(String cliente_id, int dias){
		synchronized (Lock) {
			open();
			String diasString=""+dias;
			int res=0;
			Cursor cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cartera_documento WHERE cliente_id = ? AND (? + julianday(fecha_documento)) < julianday(date('now','localtime')) AND saldo_pendiente > 0", new String [] {cliente_id,diasString});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
			}
			return res;
		}
	}
	
	public int getFacturasValCupoCliente2(String cliente_id, int dias, int dias2){
		synchronized (Lock) {
			open();
			int res=0;
			Cursor cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cartera_documento WHERE cliente_id = ? AND + (? - ?) < julianday(date('now','localtime'))-julianday(fecha_documento) AND saldo_pendiente > 0", new String [] {cliente_id,String.valueOf(dias),String.valueOf(dias2)});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
			}
			return res;
		}
	}
	
	public int getDiasAntiguedadPermitidos(String cupo_id){
		synchronized (Lock) {
			open();
			int res=0;
			Cursor cursor=db.rawQuery("SELECT antiguedad_cliente FROM esd_cupo WHERE cupo_id = ?", new String [] {cupo_id});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
			}
			return res;
		}
	}
	
	public int getDiasCreacion(String cliente_id){
		synchronized (Lock) {
			open();
			int res=0;
			Cursor cursor=db.rawQuery("SELECT julianday(date('now','localtime'))-julianday(fecha_creacion_erp) FROM esd_cliente WHERE cliente_id = ?", new String [] {cliente_id});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
			}
			return res;
		}
	}
	
	public int getFacturasValidarPorAntiguedad(String cliente_id,int dias){
		synchronized (Lock) {
			open();
			int res=0;
			Cursor cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cartera_documento WHERE cliente_id = ? AND julianday(fecha_vencimiento)< ? + julianday(date('now','localtime'))", new String [] {cliente_id, String.valueOf(dias)});
			if(cursor.moveToFirst()){
				res=cursor.getInt(0);
			}
			return res;
		}
	}
	
	//OJO Cuando alla cobros colocarlos
	public double getSaldoAct(String cliente_id){
		synchronized (Lock) {
			open();
			double res=0;
			Cursor cursor=db.rawQuery("SELECT SUM(saldo_pendiente) FROM esd_cartera_documento WHERE cliente_id=?", new String [] {cliente_id});
			if(cursor.moveToFirst()){
				res=cursor.getDouble(0);
			}
			return res;
		}
	}

	public double getCupoAct(String cliente_id,double saldo_act){
		synchronized (Lock) {
			open();
			double res=0;
			Cursor aux0=db.rawQuery("SELECT valor_cupo FROM esd_cliente AS C JOIN esd_cupo AS CU ON C.cupo_id=CU.cupo_id WHERE cliente_id=?", new String [] {cliente_id});
			Cursor aux1=db.rawQuery("SELECT  SUM(valor_total) FROM esd_pedido WHERE cliente_id=? AND date(fecha)=date('now','localtime') ", new String [] {cliente_id});
			if(aux0.moveToFirst() && aux1.moveToFirst()){
				res=aux0.getDouble(0)-aux1.getDouble(0)-saldo_act;
			}
			return res;
		}
	}

//	Cursor cursor=db.rawQuery("SELECT SUM(valor_total), valor_cupo, tipo_venta FROM (esd_cliente AS CL JOIN esd_cartera_documento AS CA ON CL.cliente_id=CA.cliente_id) JOIN esd_cupo AS CU ON CU.cupo_id=CL.cupo_id WHERE CL.cliente_id=?", new String [] {cliente_id});

	
	
	/*
	 * 
	 * Crear Actividad Diaria
	 * 
	 */
	public Cursor getSectoresParaPlan(String fecha_plan){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT DiSTINCT sector_id FROM esd_informacion_ruta WHERE date(fecha)=date(?) ORDER BY sector_id",  new String [] {fecha_plan});
			return cursor;
		}
	}
	
	public Cursor getRutaBySector(String sector_id,String fecha_plan){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT DISTINCT ruta_id FROM esd_informacion_ruta WHERE sector_id=? and date(fecha)=date(?) ORDER BY ruta_id",  new String [] {sector_id,fecha_plan});
			return cursor;
		}
	}
	
	public boolean notExistPlanInDate(String date, String sector_id, String ruta_id){
		synchronized (Lock) {
			open();
			boolean res=true;
			Cursor cursor=db.rawQuery("SELECT COUNT(1) FROM  esd_plan_trabajo WHERE date(fecha_plan)=? AND sector_id=? AND ruta_id=?",  new String [] {date,sector_id,ruta_id});
			if(cursor.moveToFirst() && cursor.getInt(0)!=0){
				res=false;
			}
			return res;
		}
	}

	public boolean ExistInfoRutaInDate(String date){
		synchronized (Lock) {
			open();
			boolean res=true;
			Cursor cursor=db.rawQuery("SELECT COUNT(1) FROM  esd_informacion_ruta WHERE date(fecha)=?",  new String [] {date});
			if(cursor.moveToFirst() && cursor.getInt(0)==0){
				res=false;
			}
			return res;
		}
	}
	
	public Cursor getCrearActividadDiariaInfo(String fecha){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT acum_ctes_nuevos_mes,venta_acum_ayer,cuota_venta_mes,cobro_acum_ayer,cuota_cobro_mes,num_pedidos_negados,valor_pedidos_negados,num_pedidos_recuperados,valor_pedidos_recuperados,venta_acum_clientes_nvos,strftime('%m/%d/%Y', fecha_actualizacion) FROM esd_informacion_ruta WHERE date(fecha)=?", new String [] {fecha});
			return cursor;
		}
	}
	
	public Cursor getCrearInfoRuta(String fecha){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT (SELECT valor FROM esd_rango WHERE id=5), sector_id, ruta_id, distrito_id, subdistrito_id from esd_informacion_ruta WHERE date(fecha) = ?",  new String [] {fecha});
			return cursor;
		}
	}

	public Cursor getConsultarInfoRuta(String n_plan){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT sector_id, ruta_id, numero_plan, strftime('%m/%d/%Y', fecha_plan), fecha_plan from esd_plan_trabajo WHERE numero_plan = ?",  new String [] {n_plan});
			return cursor;
		}
	}

	public Cursor getCrearInfoRutaClientes(String plan_id,String distrito_id,String subdistrito_id,String sector_id,String ruta_id, String fecha_plan){
		synchronized (Lock) {
			open();
			Cursor cursor = null;
			Cursor clientes_extra=null;
			String sql="";
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=6) WHERE id=6";
			cursor=db.rawQuery("SELECT cliente_id,tejido,valor_pedido_sugerido,saldo_actual, tiene_cartera_vencida FROM esd_cliente WHERE distrito_id=? AND subdistrito_id=? AND sector_id=? AND ruta_id=? ORDER BY tejido",  new String [] {distrito_id,subdistrito_id,sector_id,ruta_id});
			try{
				Log.i("info","getCrearInfoRutaClientes 0");
				if(cursor.moveToFirst()){
					Log.e("info","getCrearInfoRutaClientes 1");
					do{
						Log.i("info","getCrearInfoRutaClientes 2");
						sql="INSERT INTO est_plan_trabajo_cliente VALUES((SELECT valor FROM esd_rango WHERE id=6),'"+plan_id+"','"+distrito_id+"','"+subdistrito_id+"','"+sector_id+"','"+ruta_id+"','"+cursor.getString(0)+"','"+cursor.getString(1)+"','N','','','"+cursor.getString(2)+"','"+cursor.getString(3)+"','"+cursor.getString(4)+"','')";
						Log.i("info","getCrearInfoRutaClientes 3 "+sql);
						db.execSQL(sql);
						Log.i("info","getCrearInfoRutaClientes 4");
						db.execSQL(sqlUpdateRango);
						Log.i("info","getCrearInfoRutaClientes 5");
					}while(cursor.moveToNext());
				}
				clientes_extra=getClientesParaAgregarPorExtraRuta(fecha_plan,distrito_id,subdistrito_id,sector_id,ruta_id);
				if(clientes_extra.moveToFirst()){
					do{
						sql="INSERT INTO est_plan_trabajo_cliente VALUES((SELECT valor FROM esd_rango WHERE id=6),'"+plan_id+"','"+clientes_extra.getString(5)+"','"+clientes_extra.getString(6)+"','"+clientes_extra.getString(7)+"','"+clientes_extra.getString(8)+"','"+clientes_extra.getString(0)+"','"+clientes_extra.getString(1)+"','S','P','','"+clientes_extra.getString(2)+"','"+clientes_extra.getString(3)+"','"+clientes_extra.getString(4)+"','')";
						Log.i("info", "extra select "+sql);
						db.execSQL(sql);
						db.execSQL(sqlUpdateRango);
					}while(clientes_extra.moveToNext());
				}
			cursor=db.rawQuery("SELECT pt.id,pt.ruta_id, pt.tejido,pt.cliente_id,nombre_cliente,pt.venta_proyectada,pt.cobro_proyectado, pt.cartera_vencida,pt.extraruta,pt.tipo_extraruta,pt.observaciones,accion FROM esd_cliente AS c JOIN est_plan_trabajo_cliente AS pt ON c.cliente_id=pt.cliente_id ORDER BY pt.tejido",  new String [] {});		
			}catch(Exception e){
				db.execSQL(sqlUpdateRango);
				Log.e("info","error getCrearInfoRutaClientes "+e);
			}
			return cursor;
		}
	}
	
	public Cursor getClientesParaAgregarPorExtraRuta(String fecha_plan, String distrito_id, String subdistrito_id, String sector_id, String ruta_id){
		Log.i("info","fecha "+fecha_plan);
		Cursor cursor = db.rawQuery("SELECT cliente_id,tejido,valor_pedido_sugerido,saldo_actual, tiene_cartera_vencida , distrito_id, subdistrito_id, sector_id, ruta_id FROM esd_cliente WHERE cliente_id IN (SELECT cliente_id FROM esd_visita WHERE visita_id IN (SELECT visita_id FROM esd_visita_evento WHERE DATE(fecha_prox_visita)=DATE(?) AND tipo_evento_id='09')) AND ruta_id != ?",  new String [] {fecha_plan/*,distrito_id,subdistrito_id,sector_id*/,ruta_id});

		return cursor;
	}
	
	public Cursor getRealoadCrearInfoRutaClientes(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT pt.id,pt.ruta_id, pt.tejido,pt.cliente_id,nombre_cliente,pt.venta_proyectada,pt.cobro_proyectado, pt.cartera_vencida,pt.extraruta,pt.tipo_extraruta,pt.observaciones,accion FROM esd_cliente AS c JOIN est_plan_trabajo_cliente AS pt ON c.cliente_id=pt.cliente_id ORDER BY pt.tejido",  new String [] {});		
			return cursor;
		}
	}
	
	public boolean updateObservacionesPlanTrabajoTemp(String id,String observaciones){
		synchronized (Lock) {
			open();
			boolean res=true;
			try{
				db.execSQL("UPDATE est_plan_trabajo_cliente SET observaciones=? WHERE id=?",  new String [] {observaciones,id});
			}catch(Exception e){
				Log.e("info","error cleanPlanTrabajoTemp "+e);
				res=false;
			}
			return res;
		}
	}
	
	public boolean cleanPlanTrabajoTemp(){
		synchronized (Lock) {
			open();
			boolean res=true;
			try{
				db.execSQL("DELETE FROM est_plan_trabajo_cliente");
			}catch(Exception e){
				Log.e("info","error cleanPlanTrabajoTemp "+e);
				res=false;
			}
			return res;
		}
	}
	
	public boolean insertPlanDeTrabajo(String fecha, String n_plan, String c_nuevos, String compromisos, String vendedor, String sector_id, String ruta_id){
		synchronized (Lock) {
			open();
			boolean res=true;
			Cursor cursor;
			Cursor cursorClientes;
			String sqlDown="";
			String sqlUp="";
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=5) WHERE id=5";
			try{
				cursorClientes=db.rawQuery("SELECT * FROM est_plan_trabajo_cliente", new String [] {});
				if(cursorClientes.moveToFirst()){
					do{
						Log.i("info","insertPlanDeTrabajo 4");
						sqlDown="INSERT INTO esd_plan_trabajo_cliente VALUES   ('"+
								cursorClientes.getString(0)+"','"+
								cursorClientes.getString(1)+"','"+
								cursorClientes.getString(2)+"','"+
								cursorClientes.getString(3)+"','"+
								cursorClientes.getString(4)+"','"+
								cursorClientes.getString(5)+"','"+
								cursorClientes.getString(6)+"','"+
								cursorClientes.getString(7)+"','"+
								cursorClientes.getString(8)+"','"+
								cursorClientes.getString(9)+"','"+
								cursorClientes.getString(11)+"','"+
								cursorClientes.getString(12)+"','"+
								cursorClientes.getString(13)+"','"+
								cursorClientes.getString(10)+"')";
						Log.i("info","insertPlanDeTrabajo 5");
						sqlUp="INSERT INTO esu_plan_trabajo_cliente VALUES ('"+
								cursorClientes.getString(0)+"','"+
								cursorClientes.getString(1)+"','"+
								cursorClientes.getString(2)+"','"+
								cursorClientes.getString(3)+"','"+
								cursorClientes.getString(4)+"','"+
								cursorClientes.getString(5)+"','"+
								cursorClientes.getString(6)+"','"+
								cursorClientes.getString(7)+"','"+
								cursorClientes.getString(8)+"','"+
								cursorClientes.getString(9)+"','"+
								cursorClientes.getString(10)+"','"+
								cursorClientes.getString(11)+"','"+
								cursorClientes.getString(12)+"','"+
								cursorClientes.getString(13)+"','C', datetime('now','localtime'))";
						Log.i("info","insertPlanDeTrabajo 6");
						db.execSQL(sqlDown);
						Log.i("info","insertPlanDeTrabajo 7");
						db.execSQL(sqlUp);
						Log.i("info","insertPlanDeTrabajo 8");
					}while(cursorClientes.moveToNext());
				}

				cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id FROM  esd_informacion_ruta WHERE date(fecha)=?", new String [] {fecha});
				if(cursor.moveToFirst()){
					sqlDown="INSERT INTO esd_plan_trabajo VALUES  ('"+n_plan+"','"+n_plan+"','"+fecha+"','"+
							cursor.getString(0)+"','"+
							cursor.getString(1)+"','"+
							sector_id+"','" +
							ruta_id+"','"+
							c_nuevos+"','"+compromisos+"','','', '',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,'"+vendedor+"')";
//							c_nuevos+"','"+compromisos+"','','', '',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)";
					sqlUp="INSERT INTO esu_plan_trabajo VALUES ('"+n_plan+"','"+n_plan+"','"+fecha+"','"+
							cursor.getString(0)+"','"+
							cursor.getString(1)+"','"+
							sector_id+"','"+
							ruta_id+"','"+
							c_nuevos+"','"+compromisos+"','"+vendedor+"','C', datetime('now','localtime'))";
//							c_nuevos+"','"+compromisos+"','C', datetime('now','localtime'))";
					Log.i("info","insertPlanDeTrabajo 0");
					db.execSQL(sqlDown);
					Log.i("info","insertPlanDeTrabajo 1");
					db.execSQL(sqlUp);
					Log.i("info","insertPlanDeTrabajo 2");
					db.execSQL(sqlUpdateRango);
					Log.i("info","insertPlanDeTrabajo 3");
				}
			}catch(Exception e){
				Log.i("info","insertPlanDeTrabajo 9");
				db.execSQL(sqlUpdateRango);
				Log.i("info","insertPlanDeTrabajo 10");
				Log.e("info","error insertPlanDeTrabajo "+e);
				res=false;
			}
			return res;
		}
	}
	
	public String getAsesorId(){
		synchronized (Lock) {
			String res="";
			open();
			Cursor cursor=db.rawQuery("SELECT asesor_id FROM esd_vendedor ", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getString(0);
			}
			return res;
		}
	}
	
	public boolean deleteActividadDiaria(String numero_plan){
		synchronized (Lock) {
			open();
			boolean res=true;
			Cursor cursor;
			String sqlUp="";
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=5) WHERE id=5";
			try{
				cursor=db.rawQuery("SELECT * FROM  esd_plan_trabajo WHERE numero_plan=?", new String [] {numero_plan});
				if(cursor.moveToFirst()){
					sqlUp="INSERT INTO esu_plan_trabajo VALUES ((SELECT valor FROM esd_rango WHERE id=5)," +
							"'"+numero_plan+"','"+
							cursor.getString(2)+"','"+
							cursor.getString(3)+"','"+
							cursor.getString(4)+"','"+
							cursor.getString(5)+"','" +
							cursor.getString(6)+"','"+
							cursor.getString(7)+"','"+
							cursor.getString(8)+"','A', datetime('now','localtime'))";										
					db.execSQL(sqlUp);
					db.execSQL("DELETE FROM esd_plan_trabajo WHERE numero_plan=?", new String [] {numero_plan});
					db.execSQL(sqlUpdateRango);
				}else{
					Log.e("info","Error no se encontro plan de trabajo");
				}
			}catch(Exception e){
				db.execSQL(sqlUpdateRango);
				Log.e("info","error deleteActividadDiaria "+e);
				res=false;
			}
			return res;
		}
	}
	
	public boolean updateBlackDates(){
		synchronized (Lock) {
			open();
			boolean res=true;
			try{
				db.execSQL("UPDATE esu_visita_evento SET duracion = '00:00:00' WHERE duracion is null OR duracion IN ('',' ',null,'null')");
				db.execSQL("UPDATE esu_visita_evento SET fecha_prox_visita = '1900-01-01 00:00:00' WHERE fecha_prox_visita is null OR fecha_prox_visita IN ('',' ',null,'null')");
			}catch(Exception e){
				Log.e("info","updateBlackDates "+e);
				res=false;
			}
			return res;
		}
	}
	
	public boolean isSupervisor(){
		synchronized (Lock) {
			open();
			boolean res=false;
			try{
				Cursor cursor=db.rawQuery("SELECT Count(*) FROM esd_sector", new String [] {});
				if(cursor.moveToFirst()){
					if(cursor.getInt(0)>1){
						res=true;
					}
				}
			}catch(Exception e){
				Log.e("info","isSupervisor "+e);
				res=false;
			}
			return res;
		}
		
	}
	
	public Cursor getPlanes(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT numero_plan, strftime('%m/%d/%Y',fecha_plan),sector_id,usuario_creador FROM esd_plan_trabajo ORDER BY fecha_plan DESC", new String [] {});
//			Cursor cursor=db.rawQuery("SELECT numero_plan, strftime('%m/%d/%Y',fecha_plan),sector_id  FROM esd_plan_trabajo ORDER BY fecha_plan DESC", new String [] {});
			return cursor;
		}
	}
	
	public Cursor getConsultaPlan(String n_plan, String fecha_plan){
		synchronized (Lock) {
			open();
			Cursor cursor;
			String sql="SELECT DISTINCT pt.id,pt.ruta_id,pt.tejido,pt.cliente_id,nombre_cliente,pt.venta_proyectada,pt.cobro_proyectado,pt.cartera_vencida,pt.extraruta,pt.tipo_extraruta,pt.observaciones," +
					"CASE WHEN  (SELECT count(*) FROM esd_visita WHERE numero_plan="+n_plan+" AND cliente_id=pt.cliente_id  AND tipo_evento='03' )>0 THEN '03' " +
					"WHEN  (SELECT count(*) FROM esd_visita WHERE numero_plan="+n_plan+" AND cliente_id=pt.cliente_id  AND tipo_evento='02' )>0 THEN '02' " +
					"WHEN  (SELECT count(*) FROM esd_visita WHERE numero_plan="+n_plan+" AND cliente_id=pt.cliente_id  AND tipo_evento='04' )>0 THEN '04' " +
					"WHEN  (SELECT count(*) FROM esd_visita WHERE numero_plan="+n_plan+" AND cliente_id=pt.cliente_id  AND tipo_evento='05' )>0 THEN '05' " +
					"WHEN  (SELECT count(*) FROM esd_visita WHERE numero_plan="+n_plan+" AND cliente_id=pt.cliente_id  AND tipo_evento='01' )>0 THEN '01'  END," +
					"(SELECT SUM(valor_total) FROM esd_pedido WHERE cliente_id=c.cliente_id AND date(fecha)=date('"+fecha_plan+"')), " +
					"(SELECT count(*) FROM esd_visita WHERE numero_plan="+n_plan+" AND cliente_id=pt.cliente_id) " +
					"FROM esd_cliente AS c JOIN esd_plan_trabajo_cliente AS pt ON c.cliente_id=pt.cliente_id WHERE pt.numero_plan="+n_plan+" ORDER BY pt.tejido ASC";
			Log.i("info","consulta sql "+sql);
			cursor=db.rawQuery(sql,new String [] {});
			return cursor;
		}
	}

	public Cursor getVisitasXCliente(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT visita_id,(SELECT COUNT(*) FROM esd_visita_evento WHERE esd_visita_evento.visita_id=esd_visita.visita_id),TIME(fecha_inicio), TIME(fecha_finalizacion), TIME(JULIANDAY(fecha_finalizacion)-JULIANDAY(fecha_inicio)+0.5),observaciones,tipo_evento FROM esd_visita WHERE cliente_id=?",new String [] {cliente_id});
			return cursor;
		}
	}
	
	public Cursor getVisitasXClienteDelDia(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT visita_id,(SELECT COUNT(*) FROM esd_visita_evento WHERE esd_visita_evento.visita_id=esd_visita.visita_id),TIME(fecha_inicio), TIME(fecha_finalizacion), TIME(JULIANDAY(fecha_finalizacion)-JULIANDAY(fecha_inicio)+0.5),observaciones,tipo_evento FROM esd_visita WHERE cliente_id=? AND date(fecha_finalizacion)=date('now','localtime')",new String [] {cliente_id});
			return cursor;
		}
	}

	public Cursor getVisitasXClienteDelDia(String cliente_id, String fecha_plan){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT visita_id,(SELECT COUNT(*) FROM esd_visita_evento WHERE esd_visita_evento.visita_id=esd_visita.visita_id),TIME(fecha_inicio), TIME(fecha_finalizacion), TIME(JULIANDAY(fecha_finalizacion)-JULIANDAY(fecha_inicio)+0.5),observaciones,tipo_evento FROM esd_visita WHERE cliente_id=? AND date(fecha_inicio)=date(?)",new String [] {cliente_id,fecha_plan});
			return cursor;
		}
	}
	
	public Cursor getEventosXClienteDelDia(String visita_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT descripcion,TIME(fecha_inicio), TIME(fecha_finalizacion), TIME(JULIANDAY(fecha_finalizacion)-JULIANDAY(fecha_inicio)+0.5),observaciones,bandera_id FROM esd_visita_evento as v JOIN esd_evento AS e ON e.evento_id=v.tipo_evento_id WHERE visita_id=?",new String [] {visita_id});
			return cursor;
		}
	}
	
	public boolean updatePlanClienteObservacion(String n_plan, String cliente_id,String Obs){
		synchronized (Lock) {
			open();
			boolean res=true;
			Cursor aux;
			String sqlUp="";
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=6) WHERE id=6";
			String sqlDown="UPDATE esd_plan_trabajo_cliente SET observaciones='"+Obs+"' WHERE numero_plan='"+n_plan+"' AND cliente_id='"+cliente_id+"'";
			try{
				db.execSQL(sqlDown);
				aux=db.rawQuery("SELECT * FROM esd_plan_trabajo_cliente WHERE numero_plan=? AND cliente_id=?", new String [] {n_plan,cliente_id});
				if(aux.moveToFirst()){
					sqlUp="INSERT INTO esu_plan_trabajo_cliente VALUES ((SELECT valor FROM esd_rango WHERE id=6),'"+
							aux.getString(1)+"','"+
							aux.getString(2)+"','"+
							aux.getString(3)+"','"+
							aux.getString(4)+"','"+
							aux.getString(5)+"','"+
							aux.getString(6)+"','"+
							aux.getString(7)+"','"+
							aux.getString(8)+"','"+
							aux.getString(9)+"','"+
							aux.getString(13)+"','"+
							aux.getString(10)+"','"+
							aux.getString(11)+"','"+
							aux.getString(12)+"','M', datetime('now','localtime'))";
					db.execSQL(sqlUp);
					db.execSQL(sqlUpdateRango);
				}
			}catch(Exception e){
				db.execSQL(sqlUpdateRango);
				res=false;
				Log.e("info","error updatePlanClienteObservacion "+e);
			}
			return res;
		}
	}
	
	
	public Cursor getNPlanXFecha(String fecha){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT numero_plan FROM esd_plan_trabajo WHERE  strftime('%Y%m%d', fecha_plan)=?", new String [] {fecha});
			return cursor;
		}
	}
	
	public boolean systemClosePlanTrabajo(){
		synchronized (Lock) {
			open();
			boolean res=true;
			String n_plan,tipoVisita;
			int cNoVisitados=0;
			int cPedido=0;
			int cNoVenta=0;
//			int cProgramados=0;
			double total_pedido=0;
			
			Cursor cursorClientes=null;
			Cursor cursor=db.rawQuery("SELECT numero_plan,fecha_final_ejecucion FROM esd_plan_trabajo WHERE date(fecha_plan)<date('now','localtime') AND (fecha_final_ejecucion IS NULL OR date(fecha_final_ejecucion)='1900-01-01')", new String [] {});
			Log.i("info","systemClosePlanTrabajo 1");
			Log.i("info","systemClosePlanTrabajo 1 cursor "+cursor.moveToFirst());
			if(cursor.moveToFirst()){
				do{
					Log.i("info","systemClosePlanTrabajo 1 cursor cursor +"+cursor.getString(1));
					Log.i("info","systemClosePlanTrabajo 1 cursor cursor +"+cursor.isNull(1));
				}while(cursor.moveToNext());				
			}
//			if(cursor.moveToFirst() && false){
			if(cursor.moveToFirst()){
				do{
					beginTransaction();
					Log.i("info","systemClosePlanTrabajo 2");
					n_plan=cursor.getString(0);
					String sql="SELECT DISTINCT " +
							"pt.cliente_id," +
							"pt.extraruta," +
							"pt.tipo_extraruta," +
							"pt.venta_proyectada," +
							"pt.cobro_proyectado," +
							"CASE WHEN  (SELECT count(*) FROM esd_visita WHERE numero_plan="+n_plan+" AND cliente_id=pt.cliente_id  AND tipo_evento='03' )>0 THEN '03' " +
							"WHEN  (SELECT count(*) FROM esd_visita WHERE numero_plan="+n_plan+" AND cliente_id=pt.cliente_id  AND tipo_evento='02' )>0 THEN '02' " +
							"WHEN  (SELECT count(*) FROM esd_visita WHERE numero_plan="+n_plan+" AND cliente_id=pt.cliente_id  AND tipo_evento='04' )>0 THEN '04' " +
							"WHEN  (SELECT count(*) FROM esd_visita WHERE numero_plan="+n_plan+" AND cliente_id=pt.cliente_id  AND tipo_evento='05' )>0 THEN '05' " +
							"WHEN  (SELECT count(*) FROM esd_visita WHERE numero_plan="+n_plan+" AND cliente_id=pt.cliente_id  AND tipo_evento='01' )>0 THEN '01'" +
							"ELSE '01'  END," +
							"(SELECT SUM(valor_total) FROM esd_pedido WHERE cliente_id=c.cliente_id AND date(fecha)=date('now','localtime')), " +
							"(SELECT count(*) FROM esd_visita WHERE numero_plan="+n_plan+" AND cliente_id=pt.cliente_id) " +
							"FROM esd_cliente AS c JOIN esd_plan_trabajo_cliente AS pt ON c.cliente_id=pt.cliente_id " +
							"WHERE pt.numero_plan="+n_plan+" ORDER BY pt.tejido ASC";
					Log.i("info","systemClosePlanTrabajo 2");
					Log.i("info","systemClosePlanTrabajo 2 + "+sql);
					cursorClientes=db.rawQuery(sql, new String [] {});
					if(cursorClientes.moveToFirst()){
						cNoVisitados=0;
						cPedido=0;
						cNoVenta=0;
//						cProgramados=0;
						do{
							tipoVisita=cursorClientes.getString(5);
							if(tipoVisita.equalsIgnoreCase("01") 
								|| tipoVisita.equalsIgnoreCase("04")
								|| tipoVisita.equalsIgnoreCase("05")){
								cNoVisitados++;
							}
							if(tipoVisita.equalsIgnoreCase("02")){
								cNoVenta++;
							}
							if(tipoVisita.equalsIgnoreCase("03")){
								cPedido++;
							}

							if( (tipoVisita.equalsIgnoreCase("01") ||
								tipoVisita.equalsIgnoreCase("02") ||
								tipoVisita.equalsIgnoreCase("03") ||
								tipoVisita.equalsIgnoreCase("04") ||
								tipoVisita.equalsIgnoreCase("05")) && cursorClientes.getInt(7)>0){								
								Log.i("info","systemClosePlanTrabajo 2 siiiiii");

							}else{
								Log.i("info","systemClosePlanTrabajo 2 noooooo");

								res=res && systemCloseVisita(n_plan,
									cursorClientes.getString(0),
									cursorClientes.getString(1), 
									cursorClientes.getString(2), -1, -1, 
									cursorClientes.getDouble(3),
									cursorClientes.getDouble(6),
									cursorClientes.getDouble(4),
									0.0, "");
							}
						}while(cursorClientes.moveToNext());
					}
					res=res && addEjecucionPlan(n_plan,
							cNoVisitados,
							cPedido,
							cNoVenta,
							0.0,"0","0","");
					if(res){
						setTransactionSuccessful();
					}else{
						Log.e("info","Fallo cierre automatico");
					}
					endTransaction();
				}while(cursor.moveToNext());
			}
			if(cursor!=null){
				cursor.close();
				if(cursorClientes!=null){
					cursorClientes.close();
				}				
			}
			return res;
		}
	}
	
	public int getNumeroClientesProgramados(String nPlan){
		synchronized (Lock) {
			open();
			int result=0;
			Cursor cursor=db.rawQuery("SELECT Count(*) FROM esd_plan_trabajo AS T JOIN esd_plan_trabajo_cliente AS TC ON T.numero_plan=TC.numero_plan WHERE extraruta='N' AND T.numero_plan=?", new String [] {nPlan});
			if(cursor.moveToFirst()){
				result=cursor.getInt(0);
			}
			cursor.close();
			return result;
		}
	}
	
	/*
	 * 
	 * Agregar Extrarutas
	 * 
	 * */

	public Cursor getClientesParaExtraRutaCrear(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE cliente_id NOT IN (SELECT cliente_id FROM est_plan_trabajo_cliente) ORDER BY nombre_cliente", new String [] {});
			return cursor;
		}
	}
	
	public Cursor getClientesParaExtraRutaConsulta(String n_plan){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE cliente_id NOT IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?) ORDER BY nombre_cliente", new String [] {n_plan});
			return cursor;
		}
	}

	public boolean addExtraRutaFromCalendar(long date, String cliente_id){
		synchronized (Lock) {
			open();
			Log.i("info","addExtraRutaFromCalendar 0");
			boolean result=false;
			String nPlan;
			ExtraRuta extraRuta;
			Cursor cursorCliente=null;
			Log.i("info","addExtraRutaFromCalendar 0.5 "+date);
			
			Cursor cursorNPlan=db.rawQuery("SELECT numero_plan FROM esd_plan_trabajo WHERE date(fecha_plan)=date(?, 'unixepoch','localtime')", new String [] {String.valueOf(date)});
			Log.i("info","addExtraRutaFromCalendar 1");
			if(cursorNPlan.moveToFirst()){
				Log.i("info","addExtraRutaFromCalendar 2");
				nPlan=cursorNPlan.getString(0);
				cursorCliente=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE cliente_id NOT IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?) AND cliente_id=? ORDER BY nombre_cliente", new String [] {nPlan,cliente_id});
				Log.i("info","addExtraRutaFromCalendar 3");
				if(cursorCliente.moveToFirst()){
					Log.i("info","addExtraRutaFromCalendar 4");
					extraRuta=new ExtraRuta(cursorCliente.getString(0),
							cursorCliente.getString(1),
							cursorCliente.getString(2),
							cursorCliente.getString(3),
							cursorCliente.getString(4),
							cursorCliente.getString(5),
							cursorCliente.getString(6),
							cursorCliente.getDouble(7),
							cursorCliente.getDouble(8),
							cursorCliente.getString(9),"");
					Log.i("info","addExtraRutaFromCalendar 5");
					result=addExtraRutaConsulta(nPlan,extraRuta);
					Log.i("info","addExtraRutaFromCalendar 6 "+result);
				}
				if(cursorCliente!=null && !cursorCliente.isClosed()){
					cursorCliente.close();
				}
			}
			if(cursorNPlan!=null && !cursorNPlan.isClosed()){
				cursorNPlan.close();
			}

			return result;
		}
	}
//	SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE cliente_id NOT IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?) ORDER BY nombre_cliente
	
	public boolean addExtraRutaCrear(String n_plan,ExtraRuta ruta){
		synchronized (Lock) {
			open();
			boolean res=true;
			String sql="INSERT INTO est_plan_trabajo_cliente VALUES((SELECT valor FROM esd_rango WHERE id=6),'"+n_plan+"','"+
					ruta.getDistritoId()+"','"+
					ruta.getSubdistritoId()+"','"+
					ruta.getSectorId()+"','"+
					ruta.getRutaID()+"','"+
					ruta.getClienteId()+"','"+
					ruta.getTejido()+"','S','P','"+
					ruta.getObservacion()+"','"+
					ruta.getVentaProy()+"','"+
					ruta.getCobroProy()+"','"+
					ruta.getCarteraVenc()+"','A')";
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=6) WHERE id=6";
			try{
				db.execSQL(sql);
				db.execSQL(sqlUpdateRango);
			}catch(Exception e){
				Log.e("info","Error addExtraRutaConsulta "+e);
				db.execSQL(sqlUpdateRango);
				res=false;
			}
			return res;
		}
	}
	
	public boolean addExtraRutaConsulta(String n_plan,ExtraRuta ruta,String tipoExtraRuta){
		synchronized (Lock) {
			open();
			boolean res=true;
			String sqlDown="INSERT INTO esd_plan_trabajo_cliente VALUES(" +
					"(SELECT valor FROM esd_rango WHERE id=6),'"+n_plan+"','"+
					ruta.getDistritoId()+"','"+
					ruta.getSubdistritoId()+"','"+
					ruta.getSectorId()+"','"+
					ruta.getRutaID()+"','"+
					ruta.getClienteId()+"','"+
					ruta.getTejido()+"','S','"+
					tipoExtraRuta+"','"+
					ruta.getVentaProy()+"','"+
					ruta.getCobroProy()+"','"+
					ruta.getCarteraVenc()+"','"+
					ruta.getObservacion()+"')";
			String sqlUp="INSERT INTO esu_plan_trabajo_cliente VALUES(" +
					"(SELECT valor FROM esd_rango WHERE id=6),'"+n_plan+"','"+
					ruta.getDistritoId()+"','"+
					ruta.getSubdistritoId()+"','"+
					ruta.getSectorId()+"','"+
					ruta.getRutaID()+"','"+
					ruta.getClienteId()+"','"+
					ruta.getTejido()+"','S','"+
					tipoExtraRuta+"','"+
					ruta.getObservacion()+"','"+
					ruta.getVentaProy()+"','"+
					ruta.getCobroProy()+"','"+
					ruta.getCarteraVenc()+"','C', datetime('now','localtime'))";
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=6) WHERE id=6";
			try{
				db.execSQL(sqlDown);
				db.execSQL(sqlUp);
				db.execSQL(sqlUpdateRango);
			}catch(Exception e){
				Log.e("info","Error addExtraRutaConsulta "+e);
				db.execSQL(sqlUpdateRango);
				res=false;
			}
			return res;
		}
	}
	
	public boolean addExtraRutaConsulta(String n_plan,ExtraRuta ruta){
		synchronized (Lock) {
			open();
			boolean res=true;
			String sqlDown="INSERT INTO esd_plan_trabajo_cliente VALUES(" +
					"(SELECT valor FROM esd_rango WHERE id=6),'"+n_plan+"','"+
					ruta.getDistritoId()+"','"+
					ruta.getSubdistritoId()+"','"+
					ruta.getSectorId()+"','"+
					ruta.getRutaID()+"','"+
					ruta.getClienteId()+"','"+
					ruta.getTejido()+"','S','P','"+
					ruta.getVentaProy()+"','"+
					ruta.getCobroProy()+"','"+
					ruta.getCarteraVenc()+"','"+
					ruta.getObservacion()+"')";
			String sqlUp="INSERT INTO esu_plan_trabajo_cliente VALUES(" +
					"(SELECT valor FROM esd_rango WHERE id=6),'"+n_plan+"','"+
					ruta.getDistritoId()+"','"+
					ruta.getSubdistritoId()+"','"+
					ruta.getSectorId()+"','"+
					ruta.getRutaID()+"','"+
					ruta.getClienteId()+"','"+
					ruta.getTejido()+"','S','P','"+
					ruta.getObservacion()+"','"+
					ruta.getVentaProy()+"','"+
					ruta.getCobroProy()+"','"+
					ruta.getCarteraVenc()+"','C', datetime('now','localtime'))";
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=6) WHERE id=6";
			try{
				db.execSQL(sqlDown);
				db.execSQL(sqlUp);
				db.execSQL(sqlUpdateRango);
			}catch(Exception e){
				Log.e("info","Error addExtraRutaConsulta "+e);
				db.execSQL(sqlUpdateRango);
				res=false;
			}
			return res;
		}
	}
	
	public boolean deleteExtrarutaConsulta(String cliente_id,String n_plan){
		synchronized (Lock) {
			open();
			boolean res=true;
			Cursor aux;
			String sqlUp="";
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=6) WHERE id=6";
			try{
				aux=db.rawQuery("SELECT * FROM esd_plan_trabajo_cliente WHERE numero_plan=? AND cliente_id=?", new String [] {n_plan,cliente_id});
				if(aux.moveToFirst()){
					sqlUp="INSERT INTO esu_plan_trabajo_cliente VALUES ((SELECT valor FROM esd_rango WHERE id=6),'"+
							aux.getString(1)+"','"+
							aux.getString(2)+"','"+
							aux.getString(3)+"','"+
							aux.getString(4)+"','"+
							aux.getString(5)+"','"+
							aux.getString(6)+"','"+
							aux.getString(7)+"','"+
							aux.getString(8)+"','"+
							aux.getString(9)+"','"+
							aux.getString(13)+"','"+
							aux.getString(10)+"','"+
							aux.getString(11)+"','"+
							aux.getString(12)+"','A', datetime('now','localtime'))";
					db.execSQL(sqlUp);
					db.execSQL("DELETE FROM esd_plan_trabajo_cliente WHERE id=?", new String [] {aux.getString(0)});
					db.execSQL(sqlUpdateRango);
				}
			}catch(Exception e){
				db.execSQL(sqlUpdateRango);
				Log.e("info","error deleteExtrarutaConsulta "+e);
			}
			return res;
		}
	}
	
	public boolean deleteExtrarutaCrear(String cliente_id){
		synchronized (Lock) {
			open();
			boolean res=true;
			try{
				db.execSQL("DELETE FROM est_plan_trabajo_cliente WHERE cliente_id=?",  new String [] {cliente_id});
			}catch(Exception e){
				Log.e("info","error deleteExtrarutaCrear "+e);
			}
			return res;
		}
	}

	public boolean existInSearchInConsultaPlan(String n_plan,String sector,String ruta,int buscarPor,String busqueda){
		synchronized (Lock) {
			open();
			boolean res=false;
			Cursor cursor=null;
			String input="%"+busqueda+"%";
			switch(buscarPor){
				case 0:
					if(sector.equalsIgnoreCase("todas")){
						cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cliente WHERE cliente_id like ? AND cliente_id IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?)", new String [] {input,n_plan});
					}else{
						if(ruta.equalsIgnoreCase("todas")){
							cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cliente WHERE sector_id=? AND cliente_id like ? AND cliente_id IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?)", new String [] {sector,input,n_plan});
						}else{
							cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cliente WHERE sector_id=? AND ruta_id=? AND cliente_id like ? AND cliente_id IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?)", new String [] {sector,ruta,input,n_plan});
						}
					}
					break;
				case 1:
					if(sector.equalsIgnoreCase("todas")){
						cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cliente WHERE nombre_cliente LIKE ? AND cliente_id IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?)", new String [] {input,n_plan});
					}else{
						if(ruta.equalsIgnoreCase("todas")){
							cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cliente WHERE sector_id=? AND nombre_cliente LIKE ? AND cliente_id IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?)", new String [] {sector,input,n_plan});
						}else{
							cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cliente WHERE sector_id=? AND ruta_id=? AND nombre_cliente LIKE ? AND cliente_id IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?)", new String [] {sector,ruta,input,n_plan});
						}
					}
					break;
				case 2:
					if(sector.equalsIgnoreCase("todas")){
						cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cliente WHERE sector_id=? AND ruta_id=? AND (cliente_id like ? OR nombre_cliente LIKE ?) AND cliente_id IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?)", new String [] {input,input,n_plan});
					}else{
						if(ruta.equalsIgnoreCase("todas")){
							cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cliente WHERE sector_id=? AND (cliente_id like ? OR nombre_cliente LIKE ?) AND cliente_id IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?)", new String [] {sector,input,input,n_plan});
						}else{
							cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cliente WHERE sector_id=? AND ruta_id=? AND (cliente_id like ? OR nombre_cliente LIKE ?) AND cliente_id IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?)", new String [] {sector,ruta,input,input,n_plan});
						}
					}
					break;
			}		
			if(cursor.moveToFirst()){
				if(cursor.getInt(0)>0){
					res=true;
				}
			}
			return res;
		}
	}
	
	public Cursor loadExtraRutaConsultaXFiltros(String n_plan,String sector,String ruta,int buscarPor,String busqueda){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			String input="%"+busqueda+"%";
			switch(buscarPor){
				case 0:
					if(sector.equalsIgnoreCase("todas")){
						cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE cliente_id like ? AND cliente_id NOT IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?) ORDER BY nombre_cliente", new String [] {input,n_plan});
					}else{
						if(ruta.equalsIgnoreCase("todas")){
							cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE sector_id=? AND cliente_id like ? AND cliente_id NOT IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?) ORDER BY nombre_cliente", new String [] {sector,input,n_plan});
						}else{
							cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE sector_id=? AND ruta_id=? AND cliente_id like ? AND cliente_id NOT IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?) ORDER BY nombre_cliente", new String [] {sector,ruta,input,n_plan});
						}
					}
					break;
				case 1:
					if(sector.equalsIgnoreCase("todas")){
						cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE nombre_cliente LIKE ? AND cliente_id NOT IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?) ORDER BY nombre_cliente", new String [] {input,n_plan});
					}else{
						if(ruta.equalsIgnoreCase("todas")){
							cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE sector_id=? AND nombre_cliente LIKE ? AND cliente_id NOT IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?) ORDER BY nombre_cliente", new String [] {sector,input,n_plan});
						}else{
							cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE sector_id=? AND ruta_id=? AND nombre_cliente LIKE ? AND cliente_id NOT IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?) ORDER BY nombre_cliente", new String [] {sector,ruta,input,n_plan});
						}
					}
					break;
				case 2:
					if(sector.equalsIgnoreCase("todas")){
						cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE sector_id=? AND ruta_id=? AND (cliente_id like ? OR nombre_cliente LIKE ?) AND cliente_id NOT IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?) ORDER BY nombre_cliente", new String [] {input,input,n_plan});
					}else{
						if(ruta.equalsIgnoreCase("todas")){
							cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE sector_id=? AND (cliente_id like ? OR nombre_cliente LIKE ?) AND cliente_id NOT IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?) ORDER BY nombre_cliente", new String [] {sector,input,input,n_plan});
						}else{
							cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE sector_id=? AND ruta_id=? AND (cliente_id like ? OR nombre_cliente LIKE ?) AND cliente_id NOT IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?) ORDER BY nombre_cliente", new String [] {sector,ruta,input,input,n_plan});
						}
					}
					break;
			}
			return cursor;
		}
	}	
	
	public boolean existInSearchInCrearPlan(String sector,String ruta,int buscarPor,String busqueda){
		synchronized (Lock) {
			open();
			boolean res=false;
			Cursor cursor=null;
			String input="%"+busqueda+"%";
			switch(buscarPor){
				case 0:
					if(sector.equalsIgnoreCase("todas")){
						cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cliente WHERE cliente_id like ? AND cliente_id IN (SELECT cliente_id FROM est_plan_trabajo_cliente)", new String [] {input});
					}else{
						if(ruta.equalsIgnoreCase("todas")){
							cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cliente WHERE sector_id=? AND cliente_id like ? AND cliente_id IN (SELECT cliente_id FROM est_plan_trabajo_cliente)", new String [] {sector,input});
						}else{
							cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cliente WHERE sector_id=? AND ruta_id=? AND cliente_id like ? AND cliente_id IN (SELECT cliente_id FROM est_plan_trabajo_cliente)", new String [] {sector,ruta,input});
						}
					}
					break;
				case 1:
					if(sector.equalsIgnoreCase("todas")){
						cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cliente WHERE nombre_cliente LIKE ? AND cliente_id IN (SELECT cliente_id FROM est_plan_trabajo_cliente)", new String [] {input});
					}else{
						if(ruta.equalsIgnoreCase("todas")){
							cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cliente WHERE sector_id=? AND  nombre_cliente LIKE ? AND cliente_id IN (SELECT cliente_id FROM est_plan_trabajo_cliente)", new String [] {sector,input});
						}else{
							cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cliente WHERE sector_id=? AND ruta_id=? AND nombre_cliente LIKE ? AND cliente_id IN (SELECT cliente_id FROM est_plan_trabajo_cliente)", new String [] {sector,ruta,input});
						}
					}
					break;
				case 2:
					if(sector.equalsIgnoreCase("todas")){
						cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cliente WHERE (cliente_id LIKE ? OR nombre_cliente LIKE ?) AND cliente_id IN (SELECT cliente_id FROM est_plan_trabajo_cliente)", new String [] {input,input});
					}else{
						if(ruta.equalsIgnoreCase("todas")){
							cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cliente WHERE sector_id=? AND (cliente_id LIKE ? OR nombre_cliente LIKE ?) AND cliente_id IN (SELECT cliente_id FROM est_plan_trabajo_cliente)", new String [] {sector,input,input});
						}else{
							cursor=db.rawQuery("SELECT COUNT(*) FROM esd_cliente WHERE sector_id=? AND ruta_id=? AND (cliente_id LIKE ? OR nombre_cliente LIKE ?) AND cliente_id IN (SELECT cliente_id FROM est_plan_trabajo_cliente)", new String [] {sector,ruta,input,input});
						}
					}
					break;
			}		
			if(cursor.moveToFirst()){
				if(cursor.getInt(0)>0){
					res=true;
				}
			}
			return res;
		}
	}
	
	public Cursor loadExtraRutaCrearXFiltros(String sector,String ruta,int buscarPor,String busqueda){
		synchronized (Lock) {
			open();
			Cursor cursor=null;
			String input="%"+busqueda+"%";
			switch(buscarPor){
				case 0:
					if(sector.equalsIgnoreCase("todas")){
						cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE cliente_id like ? AND cliente_id NOT IN (SELECT cliente_id FROM est_plan_trabajo_cliente) ORDER BY nombre_cliente", new String [] {input});
					}else{
						if(ruta.equalsIgnoreCase("todas")){
							cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE sector_id=? AND cliente_id like ? AND cliente_id NOT IN (SELECT cliente_id FROM est_plan_trabajo_cliente) ORDER BY nombre_cliente", new String [] {sector,input});
						}else{
							cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE sector_id=? AND ruta_id=? AND cliente_id like ? AND cliente_id NOT IN (SELECT cliente_id FROM est_plan_trabajo_cliente) ORDER BY nombre_cliente", new String [] {sector,ruta,input});
						}
					}
					break;
				case 1:
					if(sector.equalsIgnoreCase("todas")){
						cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE nombre_cliente LIKE ? AND cliente_id NOT IN (SELECT cliente_id FROM est_plan_trabajo_cliente) ORDER BY nombre_cliente", new String [] {input});
					}else{
						if(ruta.equalsIgnoreCase("todas")){
							cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE sector_id=? AND  nombre_cliente LIKE ? AND cliente_id NOT IN (SELECT cliente_id FROM est_plan_trabajo_cliente) ORDER BY nombre_cliente", new String [] {sector,input});
						}else{
							cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE sector_id=? AND ruta_id=? AND nombre_cliente LIKE ? AND cliente_id NOT IN (SELECT cliente_id FROM est_plan_trabajo_cliente) ORDER BY nombre_cliente", new String [] {sector,ruta,input});
						}
					}
					break;
				case 2:
					if(sector.equalsIgnoreCase("todas")){
						cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE (cliente_id LIKE ? OR nombre_cliente LIKE ?) AND cliente_id NOT IN (SELECT cliente_id FROM est_plan_trabajo_cliente) ORDER BY nombre_cliente", new String [] {input,input});
					}else{
						if(ruta.equalsIgnoreCase("todas")){
							cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE sector_id=? AND (cliente_id LIKE ? OR nombre_cliente LIKE ?) AND cliente_id NOT IN (SELECT cliente_id FROM est_plan_trabajo_cliente) ORDER BY nombre_cliente", new String [] {sector,input,input});
						}else{
							cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id, cliente_id,nombre_cliente,tejido,valor_pedido_sugerido,saldo_actual,tiene_cartera_vencida FROM esd_cliente WHERE sector_id=? AND ruta_id=? AND (cliente_id LIKE ? OR nombre_cliente LIKE ?) AND cliente_id NOT IN (SELECT cliente_id FROM est_plan_trabajo_cliente) ORDER BY nombre_cliente", new String [] {sector,ruta,input,input});
						}
					}
					break;
			}		
			return cursor;
		}
	}

	public Cursor getRutasExtraRutaConsultar(String n_plan,String sector_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT DISTINCT ruta_id FROM esd_cliente WHERE sector_id=? AND cliente_id NOT IN (SELECT cliente_id FROM esd_plan_trabajo_cliente WHERE numero_plan=?) ORDER BY ruta_id ASC", new String [] {sector_id,n_plan});
			return cursor;	
		}
	}

	public Cursor getRutasExtraRutaCrear(String sector_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT DISTINCT ruta_id FROM esd_cliente WHERE sector_id=? AND cliente_id NOT IN (SELECT cliente_id FROM est_plan_trabajo_cliente) ORDER BY ruta_id ASC", new String [] {sector_id});
			return cursor;	
		}
	}
	
	public Cursor getEventosNoVisita(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT evento_id,descripcion FROM esd_evento WHERE implica_visita='N' AND evento_id!='99'", new String [] {});
			return cursor;
		}
	}

	public Cursor getEventos(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT evento_id, descripcion, implica_visita, implica_programar_visita, implica_radicar_facturas, implica_desplazamiento FROM esd_evento WHERE implica_visita='S' AND evento_id!='01' AND evento_id!='02' AND evento_id!='99' ORDER BY descripcion ASC" , new String [] {});
			return cursor;	
		}
	}

	public boolean insertNoVisita(String n_plan, String cliente_id, String extraruta, String tipoExtra, double latitud, double longitud, double ventaProy, double ventaReal, double cobroProy, double cobroReal,  String obs, String tipo_evento_id){
		synchronized (Lock) {
			open();
			boolean res=true;
			String sqlDown="";
			String sqlUp="";
			String visita_id="";
			Cursor cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id,tiene_cartera_vencida, (SELECT valor FROM esd_rango WHERE id=7), datetime('now','localtime'),latitud,longitud FROM esd_cliente WHERE cliente_id=?",  new String [] {cliente_id});
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=7) WHERE id=7";
			float dist=0;
			try{
				if(cursor.moveToFirst()){
					visita_id=cursor.getString(5);
					sqlDown="INSERT INTO esd_visita VALUES(" +
						"(SELECT valor FROM esd_rango WHERE id=7)," +
						"(SELECT valor FROM esd_rango WHERE id=7),'"+
						n_plan+"','"+
						cursor.getString(0)+"','"+
						cursor.getString(1)+"','"+
						cursor.getString(2)+"','"+
						cursor.getString(3)+"','"+
						cliente_id+"','" +
						extraruta+"','" +
						tipoExtra+"','"+
						latitud+"','"+
						longitud+"','"+
						dist+"'," +
						"'04'," +
						"datetime('now','localtime')," +
						"datetime('now','localtime'),"+
						ventaProy+","+
						ventaReal+","+
						cobroProy+","+
						cobroReal+",'"+
						cursor.getString(4)+"','"+
						obs+"'," +
						"DATETIME(0)," +
						"'N'," +
						"0," +
						"1," +
						"'N'," +
						"'N')";
					
					sqlUp="INSERT INTO esu_visita VALUES(" +
							"(SELECT valor FROM esd_rango WHERE id=7)," +
							"(SELECT valor FROM esd_rango WHERE id=7),'"+
							n_plan+"','"+
							cursor.getString(0)+"','"+
							cursor.getString(1)+"','"+
							cursor.getString(2)+"','"+
							cursor.getString(3)+"','"+
							cliente_id+"','" +
							extraruta+"','" +
							tipoExtra+"','"+
							latitud+"','"+
							longitud+"','"+
							cursor.getString(6)+"','"+
							cursor.getString(7)+"','"+
							dist+"','" +
							"N'," +	
							"'04'," +
							"datetime('now','localtime')," +
							"datetime('now','localtime'),'" +
							obs+"'," +
							"TIME(0.5),"+
							(cobroProy-cobroReal)+"," +
							ventaReal+"," +
							cobroReal+"," +
							"1," +
							"'N'," +
							"'N'," +
							"'C'," +
							" datetime('now','localtime'))";
					db.execSQL(sqlDown);
					db.execSQL(sqlUp);
					addEvento(visita_id,tipo_evento_id,obs,null,null,null,0,0,false,-1,"1900-01-01 00:00:00");
					db.execSQL(sqlUpdateRango);
				}else{
					res=false;
				}
			}catch(Exception e){
				Log.e("info","Error insertNoVisita "+e);
				db.execSQL(sqlUpdateRango);
				res=false;
			}
			return res;
		}
	}
	
	public boolean addEjecucionPlan(
			String n_plan,
			int cNoVisitados,
			int cPedido,
			int cNoVenta,
			double total_pedido,
			String vClientesNuevosHoy,
			String nClientesNuevo,
			String AC){
		synchronized (Lock) {
			Log.i("info","addEjecucionPlan 0");
			open();
			boolean res=true;
			String sql="";
			String sqlPlanUpdate="UPDATE esd_plan_trabajo SET fecha_final_ejecucion=datetime('now','localtime') WHERE numero_plan='"+n_plan+"'";
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=10) WHERE id=10";
			String fecha_inicio="";
			String promedio_desplazamiento="00:00:00";
			String tiempo_desplazamiento="00:00:00";
			double venta_acum_mes_hoy=0;
			double porc_cump_ventas_hoy=0;
			String venta_clientes_nuevos_hoy=vClientesNuevosHoy;
			String cantidad_clientes_nvos_hoy=nClientesNuevo;
			double cobro_acumulado_hoy=0;
			double porc_cump_cobro_hoy=0;
			double cart_morosa_cobrada_hoy=0;
			int total_clientes_ruta_hoy=0;
			double porc_cumpl_visitas_hoy=0;
			int num_clientes_con_pedido=cPedido;
			int num_clientes_no_visitados=cNoVisitados;
			int num_clientes_no_venta=cNoVenta;
			Log.i("info","addEjecucionPlan 1");
			int num_clientes_programados=getNumeroClientesProgramados(n_plan);
			double porc_efectividad_visitas=0;
			try{
				Log.i("info","addEjecucionPlan 2");
				porc_cumpl_visitas_hoy=((num_clientes_con_pedido+num_clientes_no_venta)*100/(num_clientes_programados));
				porc_efectividad_visitas=(num_clientes_con_pedido*100/(num_clientes_con_pedido+num_clientes_no_venta));
				Log.i("info","addEjecucionPlan 3");
			}catch(Exception e){
				Log.i("info","Error "+e );
			}
			double venta_real_hoy=0;
			double num_pedido_hoy=0;
			double cartera_cobrada_hoy=0;
			double num_cobros_hoy=0;
			Cursor cursor=null;
			Cursor cursorAux=null;
			try{
				cursor=db.rawQuery("SELECT COUNT(*) FROM esd_plan_trabajo_cliente WHERE extraruta='N' AND numero_plan=?",  new String [] {n_plan});
				if(cursor.moveToFirst()){
					total_clientes_ruta_hoy=cursor.getInt(0);
				}

				Log.i("info","addEjecucionPlan 5");
				cursor=db.rawQuery("SELECT MIN(fecha_inicio) FROM esd_visita WHERE numero_plan=?",  new String [] {n_plan});
				if(cursor.moveToFirst()){
					if(cursor.getString(0)!=null){
						fecha_inicio=cursor.getString(0);
						Log.i("info","addEjecucionPlan 4");
						cursorAux=db.rawQuery("SELECT SUM(valor_total), COUNT(*) FROM esd_pedido WHERE date(fecha)=date(?)",  new String [] {cursor.getString(0)});
						if(cursorAux.moveToFirst()){
							venta_real_hoy=cursorAux.getDouble(0);
							num_pedido_hoy=cursorAux.getDouble(1);
						}
					}else{
						fecha_inicio="1900-01-01 00:00:00";
					}
				}else{
					fecha_inicio="1900-01-01 00:00:00";
				}

				Log.i("info","addEjecucionPlan 6");

				cursor=db.rawQuery("SELECT TIME(AVG(Julianday(desplazamiento))), TIME(SUM(Julianday(desplazamiento))) FROM esd_visita WHERE numero_plan=?",  new String [] {n_plan});
				if(cursor.moveToFirst()){
					promedio_desplazamiento=(cursor.getString(0)!=null)?cursor.getString(0):"00:00:00";
					tiempo_desplazamiento=(cursor.getString(1)!=null)?cursor.getString(1):"00:00:00";
				}
				Log.i("info","addEjecucionPlan 7");
				
				if(fecha_inicio!=null && !fecha_inicio.equalsIgnoreCase("1900-01-01 00:00:00")){
					cursor=db.rawQuery("SELECT venta_acum_ayer, cuota_venta_mes FROM esd_informacion_ruta WHERE date(fecha)=date(?)",  new String [] {fecha_inicio});
				}
				if(cursor.moveToFirst()){
					try{
						venta_acum_mes_hoy=cursor.getDouble(0)+total_pedido;
						if(cursor.getDouble(1)!=0){
							porc_cump_ventas_hoy=venta_acum_mes_hoy*100/cursor.getDouble(1);
						}else{
							porc_cump_ventas_hoy=0;
						}
					}catch(Exception e){
						venta_acum_mes_hoy=0;
						porc_cump_ventas_hoy=0;
					}
				}else{
					venta_acum_mes_hoy=0;
					porc_cump_ventas_hoy=0;
				}
				Log.i("info","addEjecucionPlan 8");

				sql="INSERT INTO esu_ejecucion_plan VALUES(" +
						"(SELECT valor FROM esd_rango WHERE id=10),'"+
						n_plan+"','"+
						fecha_inicio+"',datetime('now','localtime'),'"+
						venta_acum_mes_hoy+"','"+
						porc_cump_ventas_hoy+"','"+
						venta_clientes_nuevos_hoy+"','"+
						cantidad_clientes_nvos_hoy+"','"+
						cobro_acumulado_hoy+"','"+
						porc_cump_cobro_hoy+"','"+
						cart_morosa_cobrada_hoy+"','"+
						total_clientes_ruta_hoy+"','"+
						porc_cumpl_visitas_hoy+"','"+
						num_clientes_con_pedido+"','"+
						num_clientes_no_visitados+"','"+
						num_clientes_no_venta+"','"+
						num_clientes_programados+"','"+
						porc_efectividad_visitas+"','"+
						venta_real_hoy+"','"+
						num_pedido_hoy+"','"+
						cartera_cobrada_hoy+"','"+
						num_cobros_hoy+"','" +
						AC+"','" +      
						promedio_desplazamiento+"','"+
						tiempo_desplazamiento+"'," +
						"'C'," +
						"datetime('now','localtime'))";
				Log.i("info","addEjecucionPlan 9");

				db.execSQL(sql);
				Log.i("info","addEjecucionPlan 10");
				db.execSQL(sqlPlanUpdate);
				Log.i("info","addEjecucionPlan 11");
				db.execSQL(sqlUpdateRango);
				Log.i("info","addEjecucionPlan 12");
			}catch(Exception e){
				db.execSQL(sqlUpdateRango);
				Log.e("info","Error addEjecucionPlan "+e);
				res=false;
			}finally{
				if(cursor!=null){
					cursor.close();
				}
				
			}
			return res;
		}
	}
	
	public boolean isEjecucionPlanFinish(String nPlan){
		synchronized (Lock) {
			open();
			boolean res=false;
			Cursor cursor=db.rawQuery("SELECT date(fecha_final_ejecucion) FROM esd_plan_trabajo WHERE numero_plan=?",  new String [] {nPlan});
			if(cursor.moveToFirst()){
				if(cursor.getString(0) !=null && !cursor.getString(0).trim().equalsIgnoreCase("") && !cursor.getString(0).trim().equalsIgnoreCase("1900-01-01")){
					res=true;
				}
			}
			if(!cursor.isClosed()){
				cursor.close();
			}
			return res;
		}
	}
	
	public boolean systemCloseVisita(String n_plan, String cliente_id, String extraruta, String tipoExtra, double latitud, double longitud, double ventaProy, double ventaReal, double cobroProy, double cobroReal,  String obs){
		synchronized (Lock) {
			open();
			boolean res=true;
			String sqlDown="";
			String sqlEventoDown="";
			String sqlUp="";
			String sqlEventoUp="";
			Cursor cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id,tiene_cartera_vencida, (SELECT valor FROM esd_rango WHERE id=7), datetime('now','localtime'),latitud,longitud FROM esd_cliente WHERE cliente_id=?",  new String [] {cliente_id});
			String sqlUpdateRangoVisita="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=7) WHERE id=7";
			String sqlUpdateRangoEvento="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=8) WHERE id=8";
			float dist=0;
			try{
				if(cursor.moveToFirst()){
					sqlDown="INSERT INTO esd_visita VALUES(" +
						"(SELECT valor FROM esd_rango WHERE id=7)," +
						"(SELECT valor FROM esd_rango WHERE id=7),'"+
						n_plan+"','"+
						cursor.getString(0)+"','"+
						cursor.getString(1)+"','"+
						cursor.getString(2)+"','"+
						cursor.getString(3)+"','"+
						cliente_id+"','" +
						extraruta+"','" +
						tipoExtra+"','"+
						latitud+"','"+
						longitud+"','"+
						dist+"'," +
						"'05'," +
						"datetime('now','localtime')," +
						"datetime('now','localtime'),"+
						ventaProy+","+
						ventaReal+","+
						cobroProy+","+
						cobroReal+",'"+
						cursor.getString(4)+"','',DATETIME(0.5),'N',0,1,'N','N')";
					sqlUp="INSERT INTO esu_visita VALUES(" +
							"(SELECT valor FROM esd_rango WHERE id=7)," +
							"(SELECT valor FROM esd_rango WHERE id=7),'"+
							n_plan+"','"+
							cursor.getString(0)+"','"+
							cursor.getString(1)+"','"+
							cursor.getString(2)+"','"+
							cursor.getString(3)+"','"+
							cliente_id+"','" +
							extraruta+"','" +
							tipoExtra+"','"+
							latitud+"','"+
							longitud+"','"+
							cursor.getString(6)+"','"+
							cursor.getString(7)+"','"+
							dist+"','" +
							"N'," +	
							"'05'," +
							"datetime('now','localtime')," +
							"datetime('now','localtime'),''," +
							"TIME(0.5),"+
							(cobroProy-cobroReal)+"," +
							ventaReal+"," +
							cobroReal+"," +
							"1,'N','N','C',datetime('now','localtime'))";
					sqlEventoDown="INSERT INTO esd_visita_evento VALUES(" +
							"(SELECT valor FROM esd_rango WHERE id=8)," +
							"(SELECT valor FROM esd_rango WHERE id=7)," +
							"(SELECT valor FROM esd_rango WHERE id=8),'" +
							"99'," +
							"datetime('now','localtime')," +
							"datetime('now','localtime')," +
							"'00:00:00'," +
							"''," +
							"'1900-01-01 00:00:00'," +
							"''," +
							"0,0,0,NULL)";
					sqlEventoUp="INSERT INTO esu_visita_evento VALUES(" +
							"(SELECT valor FROM esd_rango WHERE id=8),"+
							"(SELECT valor FROM esd_rango WHERE id=7),"+
							"(SELECT valor FROM esd_rango WHERE id=8)," +
							"'99'," +
							"datetime('now','localtime')," +
							"datetime('now','localtime')," +
							"'00:00:00'," +
							"''," +
							"'1900-01-01 00:00:00'," +
							"'',0,0,0,NULL,'C', datetime('now','localtime'))";
					Log.i("info","addEvento 5");
					db.execSQL(sqlDown);
					Log.i("info","addEvento 6");
					db.execSQL(sqlUp);
					Log.i("info","addEvento 7");
					db.execSQL(sqlEventoDown);
					Log.i("info","addEvento 8");
					db.execSQL(sqlEventoUp);
					Log.i("info","addEvento 9");
					db.execSQL(sqlUpdateRangoVisita);
					Log.i("info","addEvento 10");
					db.execSQL(sqlUpdateRangoEvento);
					Log.i("info","addEvento 11");
				}else{
					Log.e("info","Error systemCloseVisita ");
					db.execSQL(sqlUpdateRangoVisita);
					db.execSQL(sqlUpdateRangoEvento);
					res=false;
				}
			}catch(Exception e){
				Log.e("info","Error systemCloseVisita "+e);
				db.execSQL(sqlUpdateRangoVisita);
				Log.i("info","addEvento 8");
				db.execSQL(sqlUpdateRangoEvento);
				res=false;
			}finally{
				if(cursor!=null){
					cursor.close();
				}
			}
			return res;
		}
	}
	
	
	
	
	/*
	 * 
	 * Visita
	 * 
	 */
	
	public boolean createNewVisita(String visita_id,String n_plan, String cliente_id, String extraruta, String tipoExtra, double latitud, double longitud, double ventaProy, double ventaReal, double cobroProy, double cobroReal,  String obs){
		synchronized (Lock) {
			open();
			boolean res=true;
			String sqlDown="";
			Cursor cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id,tiene_cartera_vencida,latitud,longitud FROM esd_cliente WHERE cliente_id=?",  new String [] {cliente_id});
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=7) WHERE id=7";
			String desplazamiento="00:00:00";
			Cursor cursorDesplazamiento=db.rawQuery("SELECT time(julianday('now','localtime')-julianday(fecha_finalizacion)+0.5) FROM esd_visita WHERE date(fecha_finalizacion)=date('now','localtime') ORDER BY fecha_finalizacion DESC  LIMIT 1",  new String [] {});
			if(cursorDesplazamiento.moveToFirst()){
				desplazamiento=cursorDesplazamiento.getString(0);
			}
			double dist=0;
			try{
				if(cursor.moveToFirst()){
					dist=calcularDistancia(latitud,longitud,cursor.getDouble(5),cursor.getDouble(6));
					sqlDown="INSERT INTO esd_visita " +
							"(id,visita_id,numero_plan,distrito_id,subdistrito_id,sector_id,ruta_id,cliente_id,extraruta,tipo_extraruta,latitud," +
							"longitud,distancia,tipo_evento,fecha_inicio,fecha_finalizacion,venta_proyectada,venta_real,cobro_proyectado,cobro_real," +
							"cartera_vencida,observaciones,desplazamiento,visita_en_sitio,saldo_cartera,cantidad_eventos,visita_realizada,horario_laboral) VALUES(" +
						visita_id+","+
						visita_id+",'"+
						n_plan+"','"+
						cursor.getString(0)+"','"+
						cursor.getString(1)+"','"+
						cursor.getString(2)+"','"+
						cursor.getString(3)+"','"+
						cliente_id+"','" +
						extraruta+"','" +
						tipoExtra+"','"+
						latitud+"','"+
						longitud+"','"+
						dist+"','" +
						"01'," +
						"datetime('now','localtime')," +
						"null,"+
						ventaProy+","+
						ventaReal+","+
						cobroProy+","+
						cobroReal+",'"+
						cursor.getString(4)+"','"+
						obs+"','"+
						desplazamiento+"','" +
						"S','"+
						(cobroProy-cobroReal)+"'," +
						"(SELECT count(*) FROM esd_visita_evento WHERE visita_id='"+visita_id+"')," +
						"'S'," +
						"(SELECT CASE WHEN time('now','localtime') BETWEEN time(p.hora_inicio_jornada) AND time(p.hora_fin_jornada) THEN 'S' ELSE 'N' END FROM esd_parametro_general AS p))";
					db.execSQL(sqlDown);
				}else{
					res=false;
				}
			}catch(Exception e){
				Log.e("info","Error createNewVisita "+e);
				db.execSQL(sqlUpdateRango);
				res=false;
			}
			return res;
		}
	}

	public boolean createNewVisitaTemp(String visita_id,String n_plan, String cliente_id, String extraruta, String tipoExtra, double latitud, double longitud, double ventaProy, double ventaReal, double cobroProy, double cobroReal,  String obs){
		synchronized (Lock) {
			open();
			boolean res=true;
			String sqlDown="";
			Cursor cursor=db.rawQuery("SELECT distrito_id,subdistrito_id,sector_id,ruta_id,tiene_cartera_vencida,latitud,longitud FROM esd_cliente WHERE cliente_id=?",  new String [] {cliente_id});
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=7) WHERE id=7";
			String desplazamiento="00:00:00";
			Cursor cursorDesplazamiento=db.rawQuery("SELECT time(julianday('now','localtime')-julianday(fecha_finalizacion)+0.5) FROM esd_visita WHERE date(fecha_finalizacion)=date('now','localtime') ORDER BY fecha_finalizacion DESC  LIMIT 1",  new String [] {});
			if(cursorDesplazamiento.moveToFirst()){
				desplazamiento=cursorDesplazamiento.getString(0);
			}
			double dist=0;
			try{
				if(cursor.moveToFirst()){
					dist=calcularDistancia(latitud,longitud,cursor.getDouble(5),cursor.getDouble(6));
					sqlDown="INSERT INTO est_visita " +
							"(id,visita_id,numero_plan,distrito_id,subdistrito_id,sector_id,ruta_id,cliente_id,extraruta,tipo_extraruta,latitud," +
							"longitud,distancia,tipo_evento,fecha_inicio,fecha_finalizacion,venta_proyectada,venta_real,cobro_proyectado,cobro_real," +
							"cartera_vencida,observaciones,desplazamiento,visita_en_sitio,saldo_cartera,cantidad_eventos,visita_realizada,horario_laboral) VALUES(" +
						visita_id+","+
						visita_id+",'"+
						n_plan+"','"+
						cursor.getString(0)+"','"+
						cursor.getString(1)+"','"+
						cursor.getString(2)+"','"+
						cursor.getString(3)+"','"+
						cliente_id+"','" +
						extraruta+"','" +
						tipoExtra+"','"+
						latitud+"','"+
						longitud+"','"+
						dist+"','" +
						"01'," +
						"datetime('now','localtime')," +
						"null,"+
						ventaProy+","+
						ventaReal+","+
						cobroProy+","+
						cobroReal+",'"+
						cursor.getString(4)+"','"+
						obs+"','"+
						desplazamiento+"','" +
						"S','"+
						(cobroProy-cobroReal)+"'," +
						"(SELECT count(*) FROM esd_visita_evento WHERE visita_id='"+visita_id+"')," +
						"'S'," +
						"(SELECT CASE WHEN time('now','localtime') BETWEEN time(p.hora_inicio_jornada) AND time(p.hora_fin_jornada) THEN 'S' ELSE 'N' END FROM esd_parametro_general AS p))";
					db.execSQL(sqlDown);
				}else{
					res=false;
				}
			}catch(Exception e){
				Log.e("info","Error createNewVisita "+e);
				db.execSQL(sqlUpdateRango);
				res=false;
			}
			return res;
		}
	}
	
	public boolean deleteVisitaTemporal(){
		synchronized (Lock) {
			open();
			boolean res=false;
			try{
				db.execSQL("DELETE FROM est_visita");
			}catch(Exception e){
				Log.e("info","Error deleteVisitaTemporal "+e);
			}
			return res;
		}		
	}

	public boolean closeVisita(String cliente_id, String visita_id){
		synchronized (Lock) {
			open();
			boolean res=true;
			String sqlDown="";
			String sqlTemp="";
			String sqlUp="";
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=7) WHERE id=7";
			String sqlDeleteVisitaTemp="DELETE FROM est_visita";
			double ventaReal;
			double cobroReal;
			Cursor cursor=null;
			Cursor cursor_cliente=null;
			try{
				cursor_cliente=db.rawQuery("SELECT latitud,longitud FROM esd_cliente WHERE cliente_id=?", new String [] {cliente_id});
				ventaReal=getPedidosPorVisita(visita_id);
				cobroReal=getCobrosPorVisita(visita_id);
								
				sqlDown="UPDATE esd_visita SET fecha_finalizacion=datetime('now','localtime')," +
						"tipo_evento=(SELECT x.bandera_id FROM esd_visita_evento AS ve JOIN (esd_evento AS e JOIN esd_bandera_visita AS b ON b.bandera_id=e.bandera_id) AS x ON ve.tipo_evento_id=x.evento_id WHERE ve.visita_id='"+visita_id+"' ORDER BY jerarquia)," +
						"venta_real="+ventaReal+" ," +
						"cantidad_eventos=(SELECT COUNT(*) FROM esd_visita_evento WHERE visita_id='"+visita_id+"')," +
						"cobro_real="+cobroReal+" WHERE visita_id='" +visita_id+"'";
				sqlTemp="UPDATE est_visita SET fecha_finalizacion=datetime('now','localtime')," +
						"tipo_evento=(SELECT x.bandera_id FROM esd_visita_evento AS ve JOIN (esd_evento AS e JOIN esd_bandera_visita AS b ON b.bandera_id=e.bandera_id) AS x ON ve.tipo_evento_id=x.evento_id WHERE ve.visita_id='"+visita_id+"' ORDER BY jerarquia)," +
						"venta_real="+ventaReal+" ," +
						"cantidad_eventos=(SELECT COUNT(*) FROM esd_visita_evento WHERE visita_id='"+visita_id+"')," +
						"cobro_real="+cobroReal;
				db.execSQL(sqlDown);
				db.execSQL(sqlTemp);
				cursor=db.rawQuery("SELECT * FROM est_visita",  new String [] {});
				if(cursor.moveToFirst() && cursor_cliente.moveToFirst()){
					sqlUp="INSERT INTO esu_visita (	id,visita_id,numero_plan,distrito_id,subdistrito_id,sector_id,ruta_id,cliente_id,extraruta," +
						"tipo_extraruta,latitud,longitud,latitud_cliente,longitud_cliente,distancia,visita_en_sitio,tipo_evento,fecha_inicio,fecha_finalizacion," +
						"observaciones,desplazamiento,saldo_cartera,valor_pedidos,valor_cobros,cantidad_eventos,visita_realizada,horario_laboral,estado_registro," +
						"fecha_registro) VALUES(" +
						"(SELECT valor FROM esd_rango WHERE id=7)," +
						visita_id+",'"+
						cursor.getString(2)+"','"+
						cursor.getString(3)+"','"+
						cursor.getString(4)+"','"+
						cursor.getString(5)+"','"+
						cursor.getString(6)+"','"+
						cursor.getString(7)+"','"+
						cursor.getString(8)+"','"+
						cursor.getString(9)+"','"+
						cursor.getString(10)+"','"+
						cursor.getString(11)+"','"+
						cursor_cliente.getString(0)+"','"+
						cursor_cliente.getString(1)+"','"+
						cursor.getString(12)+"','"+
						cursor.getString(23)+"','"+
						cursor.getString(13)+"','"+
						cursor.getString(14)+"',DATETIME('now','localtime'),'"+
						cursor.getString(21)+"','"+
						cursor.getString(22)+"','"+
						cursor.getString(24)+"',"+
						ventaReal+","+
						cobroReal+","+
						cursor.getString(25)+",'"+
						cursor.getString(26)+"','"+
						cursor.getString(27)+"','C', datetime('now','localtime'))";
					db.execSQL(sqlUp);
					db.execSQL(sqlUpdateRango);
					db.execSQL(sqlDeleteVisitaTemp);
				}else{
					Log.e("info","Error closeVisitaVisita something went wrong");
					res=false;
				}
			}catch(Exception e){
				Log.e("info","Error closeVisitaVisita "+e);
				db.execSQL(sqlUpdateRango);
				res=false;
			}finally{
				if(cursor!=null){
					cursor.close();
				}
				if(cursor_cliente!=null){
					cursor_cliente.close();
				}
			}
			return res;
		}
	}

	public boolean checkOpenActividades(String fecha){
		synchronized (Lock) {
			open();
			boolean res = true;
			Cursor cursor=db.rawQuery("SELECT fecha_plan FROM esd_plan_trabajo WHERE ? < date(fecha_plan) AND (fecha_final_ejecucion='' OR fecha_final_ejecucion=null)", new String [] {fecha});
			if(cursor.moveToFirst()){
				do{
					
					
					
	//				kk
				}while(cursor.moveToNext());
			}
			return res;
		}
	}
	
	public String getVisitaId(){
		synchronized (Lock) {
			open();
			String res="";
			Cursor cursor=db.rawQuery("SELECT valor FROM esd_rango WHERE id=7", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getString(0);
			}
			return res;
		}
	}

	public boolean exitsVisitaTemp(){
		synchronized (Lock) {
			open();
			boolean res=false;
			Cursor cursor=db.rawQuery("SELECT count(*) FROM est_visita", new String [] {});
			if(cursor.moveToFirst()){
				if(cursor.getInt(0)>0)
					res=true;
			}
			return res;
		}
	}

	public Cursor getVisitaTempData(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT v.cliente_id, c.nombre_cliente, visita_id FROM est_visita AS v JOIN esd_cliente AS c ON v.cliente_id=c.cliente_id", new String [] {});
			return cursor;
		}
	}

	public Cursor getVisitaTempDataAllData(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT v.numero_plan, v.cliente_id, v.extraruta, v.tipo_extraruta, v.venta_proyectada, v.venta_real, v.cobro_proyectado , v.cobro_real ,c.nombre_cliente FROM est_visita AS v JOIN esd_cliente AS c ON v.cliente_id=c.cliente_id", new String [] {});
			return cursor;
		}
	}
	
	public boolean isVisiaTemporalFromYesterday(){
		synchronized (Lock) {
			open();
			boolean res=false;
			Cursor cursor=db.rawQuery("SELECT COUNT(*) FROM est_visita WHERE date(fecha_inicio) < date('now','localtime')", new String [] {});
			if(cursor.moveToFirst()){
				if(cursor.getInt(0)>0){
					res=true;
				}
			}
			return res;
		}
	}
	
	public Cursor getEventosXVisita(String visita_id){
		synchronized (Lock) {
			open();
			String sql="SELECT ve.id, descripcion, strftime('%m/%d/%Y',fecha_finalizacion), TIME(fecha_finalizacion), ve.evento_id,ve.tipo_evento_id, implica_programar_visita, implica_radicar_facturas, observaciones FROM esd_visita_evento AS ve JOIN esd_evento AS e ON ve.tipo_evento_id=e.evento_id WHERE visita_id=?";
			Cursor cursor=db.rawQuery(sql, new String [] {visita_id});
			Log.i("info", sql);
			Log.i("info", visita_id);
			return cursor;
		}
	}
	
	public Cursor getFacturasXCliente(String cliente_id){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT documento_id,descripcion,(julianday('now','localtime') - julianday(fecha_vencimiento)) AS D,valor_total,strftime('%m/%d/%Y',fecha_vencimiento),strftime('%m/%d/%Y', fecha_base),tipo_documento FROM esd_cartera_documento JOIN esd_cartera_tipo_documento ON cartera_tipo_documento_id=tipo_documento WHERE cliente_id=? ORDER BY D DESC", new String [] {cliente_id});
			return cursor;
		}
	}
	
	public Cursor getEventoDataFechaMod(String evento_id, String visita_id){
		synchronized (Lock) {
			open();
			Log.i("info",evento_id);
			Log.i("info",visita_id);
			Cursor cursor=db.rawQuery("SELECT STRFTIME('%m/%d/%Y', fecha_prox_visita),STRFTIME('%H', fecha_prox_visita),STRFTIME('%M', fecha_prox_visita),tiempo_para_aviso,tipo_tiempo_id,observaciones FROM esd_visita_evento WHERE visita_id=? AND tipo_evento_id=?", new String [] {visita_id,evento_id});
			return cursor;
		}
	}
	
	public boolean addEvento(String visita_id, 
			String tipo_evento_id,
			String obs, 
			String prox_visita, 
			String tiempo_aviso,
			String tipo_tiempo_id,
			double valor_pago,
			double valor_pedido,
			boolean registrarFactura,
			int gestion_id,
			String tiempo_inicial){
		synchronized (Lock) {
			open();
//			Log.i("info","valor pedido "+valor_pedido);
			boolean res=true;
			String sqlDown="";
			String sqlUp="";
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=8) WHERE id=8";
			Cursor cursor;
			String gestion_insert=(gestion_id == -1)? null:String.valueOf(gestion_id);
			String prox_visita_chequeada=(prox_visita!=null && !prox_visita.trim().equalsIgnoreCase(""))?prox_visita:"'1900-01-01 00:00:00'";
//			Log.i("info",prox_visita_chequeada);
			String tiempo_aviso_chequeada=(tiempo_aviso!=null && !tiempo_aviso.trim().equalsIgnoreCase(""))?tiempo_aviso:"0";
			String tipo_tiempo_id_chequeada=(tipo_tiempo_id!=null && !tipo_tiempo_id.trim().equalsIgnoreCase(""))?tipo_tiempo_id:"''";
			try{
				Log.i("info","tiempo_inicial"+tiempo_inicial);

				cursor=db.rawQuery("SELECT julianday('now','localtime')-julianday('"+tiempo_inicial+"')", new String [] {});
//				cursor=db.rawQuery("SELECT julianday('now','localtime')-julianday('1900-01-01 00:00:00')", new String [] {});

				//				Log.i("info","addEvento 1");

				if(cursor.moveToFirst()){
					Log.i("info","addEvento 2");

					Log.i("info","duracion"+cursor.getString(0));
					sqlDown="INSERT INTO esd_visita_evento VALUES((SELECT valor FROM esd_rango WHERE id=8),'"+
						visita_id+"'," +
						"(SELECT valor FROM esd_rango WHERE id=8),'" +
						tipo_evento_id+"','" +
//						"(SELECT fecha_inicio FROM esd_visita WHERE visita_id='"+visita_id+"')," +
						tiempo_inicial+"'," +
						"datetime('now','localtime')," +
						"TIME('"+cursor.getString(0)+"'),'" +
						obs+"'," +
						prox_visita_chequeada+",'"+
						tiempo_aviso_chequeada+"',"+
						tipo_tiempo_id_chequeada+",'"+
						valor_pago+"','"+
						valor_pedido+"'," +
						gestion_insert+")";
					sqlUp="INSERT INTO esu_visita_evento VALUES((SELECT valor FROM esd_rango WHERE id=8),'"+
						visita_id+"',"+
						"(SELECT valor FROM esd_rango WHERE id=8),'" +
						tipo_evento_id+"','" +
						tiempo_inicial+"'," +
						"datetime('now','localtime')," +
						"TIME('"+cursor.getString(0)+"'),'" +
						obs+"'," +
						prox_visita_chequeada+",'"+
						tiempo_aviso_chequeada+"',"+
						tipo_tiempo_id_chequeada+",'"+
						valor_pago+"','"+
						valor_pedido+"',"+gestion_insert+",'C', datetime('now','localtime'))";
				}else{
//					Log.i("info","addEvento 3");

//					Log.i("info","duracion"+cursor.getString(0));
					sqlDown="INSERT INTO esd_visita_evento VALUES((SELECT valor FROM esd_rango WHERE id=8),'"+
						visita_id+"'," +
						"(SELECT valor FROM esd_rango WHERE id=8),'" +
						tipo_evento_id+"','" +
//						"(SELECT fecha_inicio FROM esd_visita WHERE visita_id='"+visita_id+"')," +
						tiempo_inicial+"'," +
						"datetime('now','localtime')," +
						"'00:00:00','" +
						obs+"'," +
						prox_visita_chequeada+",'"+
						tiempo_aviso_chequeada+"',"+
						tipo_tiempo_id_chequeada+",'"+
						valor_pago+"','"+
						valor_pedido+"'," +
						gestion_insert+")";
					sqlUp="INSERT INTO esu_visita_evento VALUES((SELECT valor FROM esd_rango WHERE id=8),'"+
						visita_id+"',"+
						"(SELECT valor FROM esd_rango WHERE id=8),'" +
						tipo_evento_id+"','" +
						tiempo_inicial+"'," +
						"datetime('now','localtime')," +
						"'00:00:00','" +
						obs+"'," +
						prox_visita_chequeada+",'"+
						tiempo_aviso_chequeada+"',"+
						tipo_tiempo_id_chequeada+",'"+
						valor_pago+"','"+
						valor_pedido+"',"+gestion_insert+",'C', datetime('now','localtime'))";
					Log.e("info","Error addEvento Something went wrong");
				}
//				Log.i("info","addEvento 4");

				db.execSQL(sqlDown);
				Log.i("info","addEvento 5");
				db.execSQL(sqlUp);
				Log.i("info","addEvento 6");
				db.execSQL(sqlUpdateRango);
				Log.i("info","addEvento 7");

			}catch(Exception e){
				Log.e("info","Error addEvento "+e);
				db.execSQL(sqlUpdateRango);
				res=false;
			}
			return res;
		}
	}
	
	public boolean addEventoFactura(String visita_id, 
			String tipo_evento_id,
			String tipo_doc, 
			String doc_id, 
			String estado){
		synchronized (Lock) {
			open();
			boolean res=true;
			String sqlUp="";
			String sqlUpdateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=9) WHERE id=9";
			try{
				sqlUp="INSERT INTO esu_visita_evento_factura VALUES((SELECT valor FROM esd_rango WHERE id=9),'"+
						visita_id+"','"+
						tipo_evento_id+"','" +
						tipo_doc+"','"+
						doc_id+"','"+estado+"', datetime('now','localtime'))";
					db.execSQL(sqlUp);
					db.execSQL(sqlUpdateRango);
			}catch(Exception e){
				Log.e("info","Error addEventoFactura "+e);
				db.execSQL(sqlUpdateRango);
				res=false;
			}
			return res;
		}
	}
	
	public boolean deleteEvento(String evento_id, String visita_id){
		synchronized (Lock) {
			open();
			boolean res=false;
			Cursor cursor=null;
			String sqlUp="";
			String sqlDelete="DELETE FROM esd_visita_evento WHERE visita_id='"+visita_id+"' AND evento_id='"+evento_id+"'";
			String updateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=8) WHERE id=8";
			try{
				cursor=db.rawQuery("SELECT * FROM esu_visita_evento WHERE evento_id=? AND visita_id=?", new String [] {evento_id,visita_id});
				if(cursor.moveToFirst()){
					deleteEventoFactura(visita_id,evento_id);
					sqlUp="INSERT INTO esu_visita_evento VALUES((SELECT valor FROM esd_rango WHERE id=8),'"+
							cursor.getString(1)+"','"+
							cursor.getString(2)+"','" +
							cursor.getString(3)+"','" +
							cursor.getString(4)+"','" +
							cursor.getString(5)+"','" +
							cursor.getString(6)+"','" +
							cursor.getString(7)+"','" +
							cursor.getString(8)+"','"+
							cursor.getString(9)+"','"+
							cursor.getString(10)+"','"+
							cursor.getString(11)+"','"+
							cursor.getString(12)+"','"+
							cursor.getString(13)+"','A', datetime('now','localtime'))";
					db.execSQL(sqlUp);
					db.execSQL(sqlDelete);
					db.execSQL(updateRango);
					res=true;
				}else{
					res=false;
				}
			}catch(Exception e){
				Log.e("info","Error deleteEvento "+e );
				db.execSQL(updateRango);
				res=false;
			}
			return res;
		}
	}
	
	public boolean deleteEvento(String gestion_id){
		synchronized (Lock) {
			open();
			boolean res=false;
			Cursor cursor=null;
			String sqlUp="";
			String sqlDelete="DELETE FROM esd_visita_evento WHERE gestion_id='"+gestion_id+"'";
			String updateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=8) WHERE id=8";
			try{
				cursor=db.rawQuery("SELECT * FROM esu_visita_evento WHERE gestion_id=? ORDER BY fecha_registro DESC", new String [] {gestion_id});
				if(cursor.moveToFirst()){
					sqlUp="INSERT INTO esu_visita_evento VALUES((SELECT valor FROM esd_rango WHERE id=8),'"+
							cursor.getString(1)+"','"+
							cursor.getString(2)+"','" +
							cursor.getString(3)+"','" +
							cursor.getString(4)+"','" +
							cursor.getString(5)+"','" +
							cursor.getString(6)+"','" +
							cursor.getString(7)+"','" +
							cursor.getString(8)+"','"+
							cursor.getString(9)+"','"+
							cursor.getString(10)+"','"+
							cursor.getString(11)+"','"+
							cursor.getString(12)+"','"+
							cursor.getString(13)+"','A', datetime('now','localtime'))";
					db.execSQL(sqlUp);
					db.execSQL(sqlDelete);
					db.execSQL(updateRango);
					res=true;
				}else{
					res=false;
				}
			}catch(Exception e){
				Log.e("info","Error deleteEvento "+e );
				db.execSQL(updateRango);
				res=false;
			}
			return res;
		}
	}
	
	public boolean updateEventoPedido(String gestion_id, double valor_pedido){
		synchronized (Lock) {
			open();
			Log.i("info","updateEventoPedido 0");
			boolean res=false;
			Cursor cursor=null;
			String sqlUp="";
			String sqlUpdate="UPDATE esd_visita_evento SET valor_pedido="+valor_pedido+" WHERE gestion_id='"+gestion_id+"'";
			String updateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=8) WHERE id=8";

			Log.i("info","updateEventoPedido 1");
			try{
				Log.i("info","updateEventoPedido 2");
				db.execSQL(sqlUpdate);
				Log.i("info","updateEventoPedido 3");
				cursor=db.rawQuery("SELECT * FROM esd_visita_evento WHERE gestion_id=?", new String [] {String.valueOf(gestion_id)});
				Log.i("info","updateEventoPedido 4");
				if(cursor.moveToFirst()){
					Log.i("info","updateEventoPedido 5");
					sqlUp="INSERT INTO esu_visita_evento VALUES((SELECT valor FROM esd_rango WHERE id=8),'"+
							cursor.getString(1)+"','"+
							cursor.getString(2)+"','" +
							cursor.getString(3)+"','" +
							cursor.getString(4)+"','" +
							cursor.getString(5)+"','" +
							cursor.getString(6)+"','" +
							cursor.getString(7)+"','" +
							cursor.getString(8)+"','"+
							cursor.getString(9)+"','"+
							cursor.getString(10)+"','"+
							cursor.getString(11)+"','"+
							cursor.getString(12)+"','"+
							cursor.getString(13)+"','M', datetime('now','localtime'))";
					db.execSQL(sqlUp);
					Log.i("info","updateEventoPedido 6");
					Log.i("info","sqlUp "+sqlUp);

					db.execSQL(updateRango);
					Log.i("info","updateRango 7");
					res=true;
				}else{
					res=false;
				}
			}catch(Exception e){
				Log.e("info","Error updateEventoPedido "+e );
				db.execSQL(updateRango);
				res=false;
			}
			return res;
		}
	}
	
	public boolean updateEventoSimple(String evento_id, String visita_id, String obs){
		synchronized (Lock) {
			open();
			boolean res=false;
			Cursor cursor=null;
			String sqlUp="";
			String sqlUpdate="UPDATE esd_visita_evento SET observaciones='"+obs+"' WHERE visita_id='"+visita_id+"' AND evento_id='"+evento_id+"'";
			String updateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=8) WHERE id=8";
			try{
				db.execSQL(sqlUpdate);
				cursor=db.rawQuery("SELECT * FROM esd_visita_evento WHERE evento_id=? AND visita_id=?", new String [] {evento_id,visita_id});
				if(cursor.moveToFirst()){
					sqlUp="INSERT INTO esu_visita_evento VALUES((SELECT valor FROM esd_rango WHERE id=8),'"+
							cursor.getString(1)+"','"+
							cursor.getString(2)+"','" +
							cursor.getString(3)+"','" +
							cursor.getString(4)+"','" +
							cursor.getString(5)+"','" +
							cursor.getString(6)+"','" +
							cursor.getString(7)+"','" +
							cursor.getString(8)+"','"+
							cursor.getString(9)+"','"+
							cursor.getString(10)+"','"+
							cursor.getString(11)+"','"+
							cursor.getString(12)+"','"+
							cursor.getString(13)+"','M', datetime('now','localtime'))";
					db.execSQL(sqlUp);
					db.execSQL(updateRango);
					res=true;
				}else{
					db.execSQL(updateRango);
					res=false;
				}
			}catch(Exception e){
				Log.e("info","Error deleteEvento "+e );
				db.execSQL(updateRango);
				res=false;
			}
			return res;
		}
	}
	
	public boolean updateEventoFecha(String evento_id,
			String visita_id,
			String fecha_prox,
			String cantidad_tiempo,
			String tipo_tiempo,
			String obs){
		synchronized (Lock) {
			open();
			boolean res=false;
			Cursor cursor=null;
			String sqlUp="";
			String sqlUpdate="UPDATE esd_visita_evento SET tiempo_para_aviso='"+cantidad_tiempo
				+"', tipo_tiempo_id='"+tipo_tiempo
				+"', fecha_prox_visita="+fecha_prox
				+", observaciones='"+obs+"' WHERE visita_id='"+visita_id+"' AND evento_id='"+evento_id+"'";
			String updateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=8) WHERE id=8";
			try{
				db.execSQL(sqlUpdate);
				cursor=db.rawQuery("SELECT * FROM esd_visita_evento WHERE evento_id=? AND visita_id=?", new String [] {evento_id,visita_id});
				if(cursor.moveToFirst()){
					sqlUp="INSERT INTO esu_visita_evento VALUES((SELECT valor FROM esd_rango WHERE id=8),'"+
							cursor.getString(1)+"','"+
							cursor.getString(2)+"','" +
							cursor.getString(3)+"','" +
							cursor.getString(4)+"','" +
							cursor.getString(5)+"','" +
							cursor.getString(6)+"','" +
							cursor.getString(7)+"','" +
							cursor.getString(8)+"','"+
							cursor.getString(9)+"','"+
							cursor.getString(10)+"','"+
							cursor.getString(11)+"','"+
							cursor.getString(12)+"','"+
							cursor.getString(13)+"','M', datetime('now','localtime'))";
					db.execSQL(sqlUp);
					db.execSQL(updateRango);
					res=true;
				}else{
					db.execSQL(updateRango);
					res=false;
				}
			}catch(Exception e){
				Log.e("info","Error deleteEvento "+e );
				db.execSQL(updateRango);
				res=false;
			}
			return res;
		}
	}

	public boolean deleteEventoFactura(String visita_id, String evento_id){
		synchronized (Lock) {
			open();
			Log.i("info","Log delete eventos");
			boolean res=false;
			Cursor cursor=null;
			String sqlUp="";
			String updateRango="UPDATE esd_rango SET valor=(SELECT valor+1 FROM esd_rango WHERE id=9) WHERE id=9";
			try{
				Log.i("info","deleteEventoFactura "+visita_id+" "+ evento_id);
				cursor=db.rawQuery("SELECT * FROM esu_visita_evento_factura WHERE visita_id=? AND evento_id=?", new String [] {visita_id,evento_id});
				if(cursor.moveToFirst()){
					do{
						sqlUp="INSERT INTO esu_visita_evento_factura VALUES((SELECT valor FROM esd_rango WHERE id=9),'"+
								cursor.getString(1)+"','"+
								cursor.getString(2)+"','" +
								cursor.getString(3)+"','" +
								cursor.getString(4)+"','A', datetime('now','localtime'))";
						db.execSQL(sqlUp);
						db.execSQL(updateRango);
					}while(cursor.moveToNext());
				}
				res=true;
				cursor.close();
			}catch(Exception e){
				Log.e("info","Error deleteEventoFactura "+e );
				db.execSQL(updateRango);
				res=false;
			}
			return res;
		}
	}
	
	public Cursor getTipoHorarios(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT tipo_id, descripcion FROM esd_tipo_prox_visita", new String [] {});
			return cursor;
		}
	}

	public boolean existEventoVisita(String visita_id){
		synchronized (Lock) {
			open();
			boolean res=true;
			Cursor cursor=null;
			cursor=db.rawQuery("SELECT COUNT(*) FROM esd_visita_evento WHERE visita_id=?", new String [] {visita_id});
			if(!(cursor.moveToFirst() && cursor.getInt(0)>0)){
				res=false;
			}
			cursor.close();
			return res;
		}
	}

	/*
	 * 
	 * Indicadores
	 * 
	 * */
	
	public Cursor getIndicadores(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT " +
				"strftime('%m/%d/%Y', fecha_plan)," +
				"(SELECT COUNT(*) FROM esd_plan_trabajo_cliente WHERE numero_plan=p.numero_plan)," +
				"(SELECT COUNT(*) FROM esd_plan_trabajo_cliente WHERE numero_plan=p.numero_plan AND extraruta='N')," +
				"(SELECT COUNT(*) FROM esd_plan_trabajo_cliente WHERE numero_plan=p.numero_plan AND extraruta='S')," +
				"(SELECT SUM(venta_proyectada) FROM esd_plan_trabajo_cliente WHERE numero_plan=p.numero_plan)," +
				"(SELECT SUM(valor_total) FROM esd_pedido WHERE date(fecha)=date('now','localtime'))," +
				"(SELECT SUM(venta_proyectada) FROM esd_plan_trabajo_cliente WHERE numero_plan=p.numero_plan)-(SELECT SUM(valor_total) FROM esd_pedido WHERE date(fecha)=date('now','localtime'))," +
				"(SELECT SUM(cobro_proyectado) FROM esd_plan_trabajo_cliente WHERE numero_plan=p.numero_plan)" +
				",p.numero_plan FROM esd_plan_trabajo AS p WHERE date(fecha_plan)=date('now','localtime')", new String [] {});
			return cursor;
		}
	}

	public Cursor getIndicadoresGestiones(String n_plan){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT DISTINCT pt.cliente_id, " +
				"CASE WHEN " +
				"(SELECT count(*) FROM esd_visita WHERE numero_plan='"+n_plan+"' AND cliente_id=pt.cliente_id  AND tipo_evento='03' )>0 THEN '03' " +
				"WHEN  (SELECT count(*) FROM esd_visita WHERE numero_plan='"+n_plan+"' AND cliente_id=pt.cliente_id  AND tipo_evento='02' )>0 THEN '02' " +
				"WHEN  (SELECT count(*) FROM esd_visita WHERE numero_plan='"+n_plan+"' AND cliente_id=pt.cliente_id  AND tipo_evento='04' )>0 THEN '04'" +
				"WHEN  (SELECT count(*) FROM esd_visita WHERE numero_plan='"+n_plan+"' AND cliente_id=pt.cliente_id  AND tipo_evento='01' )>0 THEN '01'  END " +
				"FROM esd_cliente AS c JOIN esd_plan_trabajo_cliente AS pt ON c.cliente_id=pt.cliente_id WHERE pt.numero_plan='"+n_plan+"'", new String [] {});
			return cursor;
		}
	}
	

	public Cursor getIndicadoresPedido(){
		synchronized (Lock) {
			open();
			Cursor cursor=db.rawQuery("SELECT c.cliente_id, nombre_cliente, numero_pedido, valor_total, strftime('%m/%d/%Y', fecha), TIME(fecha), valor_iva, tipo_documento_id,valor_bruto-valor_descuento-valor_promocion FROM esd_pedido AS p JOIN esd_cliente AS c ON p.cliente_id=c.cliente_id WHERE date('now','localtime')=DATE(fecha)", new String [] {});
			return cursor;
		}
	}
	
	
	
	/*
	 * 
	 * Varias
	 * 
	 * */
	
	public Cursor getDataTable(String pQuery)
	{
		synchronized (Lock)
		{
			open();
			Cursor cursor = db.rawQuery(pQuery,new String[]{});
			return cursor;
		}
	}
	
	public boolean checkRangos(){
		synchronized (Lock) {
			open();
			boolean res=true;
			Cursor cursorRangos=db.rawQuery("SELECT id,valor FROM esd_rango", new String [] {});
			Cursor cursor=null;
			String updateRango="";
			if(cursorRangos.moveToFirst()){
				do{
					switch(cursorRangos.getInt(0)){
						case 1:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_contacto", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=1";
									db.execSQL(updateRango);
								}
							}
							break;
						case 2:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_cliente_horario", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=2";
									db.execSQL(updateRango);
								}
							}
							break;
						case 3:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_pedido", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=3";
									db.execSQL(updateRango);
								}
							}
							break;
						case 4:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_pedido_detalle", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=4";
									db.execSQL(updateRango);
								}
							}
							break;
						case 5:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_plan_trabajo", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=5";
									db.execSQL(updateRango);
								}
							}
							break;
						case 6:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_plan_trabajo_cliente", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=6";
									db.execSQL(updateRango);
								}
							}
							break;
						case 7:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_visita", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=7";
									db.execSQL(updateRango);
								}
							}
							break;
						case 8:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_visita_evento", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=8";
									db.execSQL(updateRango);
								}
							}
							break;
						case 9:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_visita_evento_factura", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=9";
									db.execSQL(updateRango);
								}
							}
							break;
						case 10:
							cursor=db.rawQuery("SELECT MAX(id) FROM esu_ejecucion_plan", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=10";
									db.execSQL(updateRango);
								}
							}
							break;
						case 11:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_cobro", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=11";
									db.execSQL(updateRango);
								}
							}
							break;
/*						case 12:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_contacto", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=1";
									db.execSQL(updateRango);
								}
							}
							break;
						case 13:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_contacto", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=1";
									db.execSQL(updateRango);
								}
							}
							break;
						case 14:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_contacto", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=1";
									db.execSQL(updateRango);
								}
							}
							break;
						case 15:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_contacto", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=1";
									db.execSQL(updateRango);
								}
							}
							break;
						case 16:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_contacto", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=1";
									db.execSQL(updateRango);
								}
							}
							break;
						case 17:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_contacto", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=1";
									db.execSQL(updateRango);
								}
							}
							break;
						case 18:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_contacto", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=1";
									db.execSQL(updateRango);
								}
							}
							break;
						case 19:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_contacto", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=1";
									db.execSQL(updateRango);
								}
							}
							break;
						case 20:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_contacto", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=1";
									db.execSQL(updateRango);
								}
							}
							break;
						case 21:
							cursor=db.rawQuery("SELECT MAX(id) FROM esd_contacto", new String [] {});
							if(cursor.moveToFirst()){
								if(cursor.getInt(0)>=cursorRangos.getInt(1)){
									updateRango="UPDATE esd_rango SET valor='"+(cursor.getInt(0)+1)+"' WHERE id=1";
									db.execSQL(updateRango);
								}
							}
							break;*/
					}
				}while(cursorRangos.moveToNext());
			}else{
				res=false;
			}
			return res;
		}

/*--	1	Nuevos contactos de cliente
--	2	Nuevos horarios de cliente
--	3	Nuevos encabezados de pedidos
--	4	Nuevos detalles de pedidos
--	5	Nuevos planes de trabajo
--	6	Nuevos detalles del plan de trabajo
-	7	Nuevas visitas
--	8	Nuevos eventos durante la visita
--	9	Nuevas facturas para programar o radicar en eventos de visita
--	10	Finalizaciï¿½n del plan de trabajo
--	11	Nuevos encabezados de cobros
--	12	Nuevos tipos de pago recibidos en los cobros
	13	Nuevos detalles de consignaciï¿½n en el mï¿½dulo cobros
	14	Nuevas relaciones de cobros
	15	Nuevos identificadores para la tabla de documentos vinculados al cobro
	16	Nuevos identificadores para la tabla de cruce de pagos
	17	Nuevos identificadores para la tabla de cruce de consignaciones
	18	Nuevos identificadores para las diferencias de cobros
	19	Nuevos identificadores para la desnormalizaciï¿½n de cobros
	20	Nuevos identificadores para la tabla de cruce de documentos negativos
	21	Nuevos identificadors para la tabla de observaciones desnormalizadas*/
		
	}

	public String getDistritoNombre(String distrito_id){
		synchronized (Lock) {
			open();
			String res="";
			Cursor cursor=db.rawQuery("SELECT nombre_distrito FROM esd_distrito WHERE distrito_id=?", new String [] {distrito_id});
			if(cursor.moveToFirst()){
				res=cursor.getString(0);
			}
			return res;
		}
	}
	
	public boolean AplicarIVA(String cliente_id){
		synchronized (Lock) {
			open();
			boolean res=true;
			Cursor cursor=db.rawQuery("SELECT Aplicar_impuesto_IVA FROM esd_cliente WHERE cliente_id=?", new String [] {cliente_id});
			if(cursor.moveToFirst() && cursor.getString(0).equalsIgnoreCase("N")){
				res=false;
			}
			return res;
		}
	}

	public double getIva(String producto_id){
		synchronized (Lock) {
			open();
			double res=0;
			Cursor cursor=db.rawQuery("SELECT porcentaje_iva FROM esd_producto WHERE producto_id=?", new String [] {producto_id});
			if(cursor.moveToFirst()){
				res=cursor.getDouble(0);
			}
			cursor.close();
			return res;
		}
	}

	public String getActualDate(){
		synchronized (Lock) {
			open();
			String res="";
			Cursor cursor=db.rawQuery("SELECT strftime('%m/%d/%Y','now','localtime')", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getString(0);
			}
			cursor.close();
			return res;
		}
	}

	public double getTotalPedidoDiaXCliente(String cliente_id){
		synchronized (Lock) {
			open();
			double res=0;
			Cursor cursor=db.rawQuery("SELECT SUM(valor_total) FROM esd_pedido WHERE DATE(fecha)=date('now','localtime')", new String [] {});
			if(cursor.moveToFirst()){
				res=cursor.getDouble(0);
			}
			cursor.close();
			return res;
		}
	}
	
	public boolean hasEventoRegistrado(String visita_id){
		synchronized (Lock) {
			open();
			boolean res;
			Cursor cursor=db.rawQuery("SELECT COUNT(*) FROM esd_visita_evento WHERE visita_id=?", new String [] {visita_id});
			if(cursor.moveToFirst() && cursor.getInt(0)>0){
				res=true;
			}else{
				res=false;
			}
			return res;
		}
	}
	
	public double calcularDistancia(double lat, double lon, double latC, double lonC){
		synchronized (Lock) {		
			open();
			double res;
			final double PI_RADIANES=57.2957795131;
			final double KM_GRADO=57.2957795131;
			double lColatitud1=0;
			double lColatitud2=0;
			double lDiedro=0;
			double lTemp=0;
			double lACos=0;
			if(lat!=0 && lon!=0){
				if(lat==latC && lon==lonC){
					res=0;
				}else{
					lColatitud1=(90-latC)/PI_RADIANES;
					lColatitud2=(90-lat)/PI_RADIANES;
					lDiedro=(lonC - lon) / PI_RADIANES;
					lTemp=Math.cos(lColatitud1)* Math.cos(lColatitud2)+ (Math.sin(lColatitud1)*Math.sin(lColatitud2))*Math.cos(lDiedro) ;
					lACos=Math.acos(lTemp)*180/Math.PI;
					res= (KM_GRADO*lACos*1000);
				}
			}else{
				res=-1;
			}
			return res;
		}
	}
	
	// inicio easysync
	public void attachBaseDatos(String _tipo) {
		synchronized (Lock) {
			open();
			try {
				if (_tipo == "Down") {
					String dbPath = context.getDatabasePath(GlobaG.DATABASE_NAME_NOVEDADES_DOWN).getAbsolutePath();
					Log.i("info", "antes de attach-"+"attach database  '" + dbPath + "' as dbDown");
					db.execSQL("attach database '" + dbPath + "' as dbDown");
					//db.execSQL("ATTACH DATABASE '" + Global.DATABASE_NAME_NOVEDADES_DOWN + "' AS dbDown");
					Log.i("info", "despues de attach");
				}
				if (_tipo == "Up") {
					String dbPath = context.getDatabasePath(GlobaG.DATABASE_NAME_NOVEDADES_UP).getAbsolutePath();
					Log.i("info", "antes de attach-"+"attach database '" + dbPath + "' as dbUp");
					db.execSQL("ATTACH DATABASE '" + dbPath + "' AS dbUp");
					//db.execSQL("ATTACH DATABASE '" + Global.DATABASE_NAME_NOVEDADES_UP + "' AS dbUp");
					Log.i("info", "despues de attach");
				}
			} catch (Exception ioe) {
				Log.i("info","error attachBaseDatos "+ioe);
				// throw new Error("Unable to attach database");

			}
		}
	}

	public boolean execute(String _sql) {
		synchronized (Lock) {
			open();
			db.execSQL(_sql);
			return true;
		}
	}
	
	
	private static final String database_table_esc_configuracion_aplicacion = "esc_configuracion_aplicacion";
	public static final String esc_configuracion_aplicacion_login_dm = "login_dm";
	public static final String esc_configuracion_aplicacion_device_id_dm = "device_id_dm";
	public static final String esc_configuracion_aplicacion_nombre_usuario = "nombre_usuario";
	public static final String esc_configuracion_aplicacion_nombre_empresa = "nombre_empresa";
	public static final String esc_configuracion_aplicacion_password_dm = "password_dm";
	public static final String esc_configuracion_aplicacion_easy_asignacion_id = "easy_asignacion_id";

	public Cursor getAllConfiguracionAplicacion() {
		synchronized (Lock) {
			open();
			return db.query(database_table_esc_configuracion_aplicacion, null,
					null, null, null, null, null);
		}
	}
	
	// campos esd_vendedor
/*	private static final String database_table_esd_vendedor = "esd_vendedor";
	public static final String esd_vendedor_id = "id";
	public static final String esd_vendedor_nombre = "nombre";
	public static final String esd_vendedor_zona = "zona";
	public static final String esd_vendedor_agencia = "agencia";
	public static final String esd_vendedor_codigo_documento = "codigo_documento";
	public static final String esd_vendedor_usuario = "usuario";*/
	
	private static final String database_table_esd_vendedor = "esd_vendedor";
	public static final String esd_vendedor_id = "id";
	public static final String esd_asesor_id = "asesor_id";
	public static final String esd_nombre_asesor = "nombre_asesor";
	public static final String esd_usuario_acceso = "usuario_acceso";
	public static final String esd_clave_acceso = "clave_acceso";
//	public static final String esd_vendedor_usuario = "usuario";

	
	
	public Cursor getAllVendedor() {
		synchronized (Lock) {
			open();
			return db.query(database_table_esd_vendedor, null, null, null,
					null, null, null);
		}
	}

	// campos esd_password
	private static final String DATABASE_TABLE_PASSWORD = "esd_password";
	public static final String PASSWORD_ID = "id";
	public static final String PASSWORD_PASSWORD = "password";

	public Cursor getAllPasswordItemsCursor() {
		synchronized (Lock) {
			open();
			return db.query(DATABASE_TABLE_PASSWORD,new String[] { PASSWORD_PASSWORD }, null, null, null, null,	null);
		}
	}
	
	public void dettachBaseDatos(String _tipo) {
		synchronized (Lock) {

			open();
			try {
				if (_tipo == "Down") {
					Log.i("info", "antes de DETACH -"+"detach  dbDown");
					db.execSQL("DETACH  dbDown");
					//db.execSQL("ATTACH DATABASE '" + Global.DATABASE_NAME_NOVEDADES_DOWN + "' AS dbDown");
					Log.i("info", "despues de DETACH ");
				}
				if (_tipo == "Up") {
					Log.i("info", "antes de DETACH -"+"DETACH  dbUp");
					db.execSQL("DETACH dbUp");
					//db.execSQL("ATTACH DATABASE '" + Global.DATABASE_NAME_NOVEDADES_UP + "' AS dbUp");
					Log.i("info", "despues de DETACH ");
				}
			} catch (Exception ioe) {
				Log.e("info","error DETACH  "+ioe);
				// throw new Error("Unable to attach database");

			}
		}
	}
	
	public Cursor getAllItemCursor(String cadSql, String[] pParametros) {
		synchronized (Lock) {
			open();

			Cursor mcursor = db.rawQuery(cadSql, pParametros);

			// ////Log.i("info", "query all item. " + cadSql + ". " +
			// mcursor.getCount());

			return mcursor;

		}
	}
	
}