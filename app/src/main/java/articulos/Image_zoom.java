package articulos;

import java.io.File;

import com.italo_view.R;

import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class Image_zoom extends Activity implements OnTouchListener{
	private static ImageView imageView;
	private static File imgFile;
	private static String path;
	private static String producto_id;
	private static Bundle extras;

	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	// we can be in one of these 3 states
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;
	// remember some things for zooming
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1f;
	private float d = 0f;
	private float newRot = 0f;
	private float[] lastEvent = null;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_zoom);
		
    	extras = getIntent().getExtras();
    	producto_id=extras.getString("producto_id");
		getActionBar().setTitle(extras.getString("producto_nombre"));
		imageView=(ImageView)findViewById(R.id.imageView);
		path=Environment.getExternalStorageDirectory().getPath()+"/Italo/"+producto_id.trim()+".png";
		imgFile = new  File(path);
		if(imgFile.exists()){
		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		    imageView.setImageBitmap(myBitmap);
		}else{
			imageView.setImageDrawable(getResources().getDrawableForDensity(R.drawable.default_image, DisplayMetrics.DENSITY_XXXHIGH));
		}
		imageView.setOnTouchListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_zoom, menu);
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

	public boolean onTouch(View v, MotionEvent event) {
		// handle touch events here
		ImageView view = (ImageView) v;
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				savedMatrix.set(matrix);
				start.set(event.getX(), event.getY());
				mode = DRAG;
				lastEvent = null;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				oldDist = spacing(event);
				if (oldDist > 10f) {
					savedMatrix.set(matrix);
					midPoint(mid, event);
					mode = ZOOM;
				}
				lastEvent = new float[4];
				lastEvent[0] = event.getX(0);
				lastEvent[1] = event.getX(1);
				lastEvent[2] = event.getY(0);
				lastEvent[3] = event.getY(1);
				d = rotation(event);
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				lastEvent = null;
				break;
			case MotionEvent.ACTION_MOVE:
				if (mode == DRAG) {
					matrix.set(savedMatrix);
					float dx = event.getX() - start.x;
					float dy = event.getY() - start.y;
					matrix.postTranslate(dx, dy);
				}else if (mode == ZOOM) {
					float newDist = spacing(event);
					if (newDist > 10f) {
						matrix.set(savedMatrix);
						float scale = (newDist / oldDist);
						matrix.postScale(scale, scale, mid.x, mid.y);
					}
					if (lastEvent != null && event.getPointerCount() == 3) {
						newRot = rotation(event);
						float r = newRot - d;
						float[] values = new float[9];
						matrix.getValues(values);
						float tx = values[2];
						float ty = values[5];
						float sx = values[0];
						float xc = (view.getWidth() / 2) * sx;
						float yc = (view.getHeight() / 2) * sx;
						matrix.postRotate(r, tx + xc, ty + yc);
					}
				}
				break;
		}
		view.setImageMatrix(matrix);
		return true;
	}
	
	/**
	 * Determine the space between the first two fingers
	 */
	
	@SuppressLint("FloatMath")
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * Calculate the mid point of the first two fingers
	 */

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	/**
	 * Calculate the degree to be rotated by.
	 *
	 * @param event
	 * @return Degrees
	 */

	private float rotation(MotionEvent event) {
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}
	
	@Override
	public void onBackPressed() {}
}