package utilidades;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.os.Environment;
import android.util.Log;

public class EasyZip {

	public boolean ZippingFile(String _fileSource, String _fileTarget) {
		try {

			File newFileTarget = new File(_fileTarget);

			// ZipOutputStream zos = new ZipOutputStream(new
			// FileOutputStream(_fileTarget));
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
					newFileTarget));
			FileInputStream fis = new FileInputStream(_fileSource);

			// put a new ZipEntry in the ZipOutputStream
			zos.putNextEntry(new ZipEntry(_fileSource));

			int size = 0;
			byte[] buffer = new byte[1024];

			// read data to the end of the source file and write it to the zip
			// output stream.
			while ((size = fis.read(buffer, 0, buffer.length)) > 0) {
				zos.write(buffer, 0, size);
			}

			zos.closeEntry();
			fis.close();

			// Finish zip process
			zos.close();

		} catch (IOException e) {
			Log.i("info", e.getMessage());
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean UnZippingFile(String _zipSource, String _unZipTarget) {
		try {
			FileInputStream fis = new FileInputStream(_zipSource);
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry;

			//
			// Read each entry from the ZipInputStream until no more entry found
			// indicated by a null return value of the getNextEntry() method.
			//
			while ((entry = zis.getNextEntry()) != null) {
				System.out.println("Unzipping: " + entry.getName());
				Log.i("info", "Unzipping: " + entry.getName());

				int size;
				byte[] buffer = new byte[2048];

				Log.i("info", entry.getName());
				FileOutputStream fos = new FileOutputStream(_unZipTarget + "/"
						+ entry.getName());
				BufferedOutputStream bos = new BufferedOutputStream(fos,
						buffer.length);

				while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
					bos.write(buffer, 0, size);
				}
				bos.flush();
				bos.close();
			}

			zis.close();
			fis.close();
		} catch (IOException e) {
			Log.i("info", "error:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
