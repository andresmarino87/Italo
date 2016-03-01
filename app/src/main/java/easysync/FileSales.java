package easysync;

import java.io.Serializable;
import java.util.Date;

import com.italo_view.GlobaG;



public class FileSales implements Serializable{

	private int id;
	private String tipo;
	private String producto_id;
	private String file_path_relative;
	private String file_name;
	private String file_extension;
	private Date file_date;
	private long file_size;
	private String descargado;
	private String solo_descarga_wifi;

	public FileSales() {
	}

	public FileSales(int _id, String _tipo, String _producto_id,
			String _file_path_relative, String _file_name,
			String _file_extension, Date _file_date, long _file_size, String _descargado, String _solo_descarga_wifi) {
		this.id = _id;
		this.tipo = _tipo;
		this.producto_id = _producto_id;
		this.file_path_relative = _file_path_relative;
		this.file_name = _file_name;
		this.file_extension = _file_extension;
		this.file_date = _file_date;
		this.file_size = _file_size;
		this.descargado = _descargado;
		this.solo_descarga_wifi = _solo_descarga_wifi;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getProducto_id() {
		return producto_id;
	}

	public void setProducto_id(String producto_id) {
		this.producto_id = producto_id;
	}

	public String getFile_path_relative() {
		return file_path_relative;
	}

	public void setFile_path_relative(String file_path_relative) {
		this.file_path_relative = file_path_relative;
	}

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public String getFile_extension() {
		return file_extension;
	}

	public void setFile_extension(String file_extension) {
		this.file_extension = file_extension;
	}

	public Date getFile_date() {
		return file_date;
	}

	public void setFile_date(Date file_date) {
		this.file_date = file_date;
	}

	public long getFile_size() {
		return file_size;
	}

	public void setFile_size(long file_size) {
		this.file_size = file_size;
	}
	
	public String PathFull()
	{
		String _pathRelative = this.getFile_path_relative();
		_pathRelative = _pathRelative.replace('\\', '/');
		
		return GlobaG.pathArchivosEasy + "/down" + _pathRelative + "/" + this.file_name;
	}

	public String getDescargado() {
		return descargado;
	}

	public void setDescargado(String descargado) {
		this.descargado = descargado;
	}

	public String getSolo_descarga_wifi() {
		return solo_descarga_wifi;
	}

	public void setSolo_descarga_wifi(String solo_descarga_wifi) {
		this.solo_descarga_wifi = solo_descarga_wifi;
	}

}
