package easygps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class EasyGps {

	int intervaloTiempo = 0;

	LocationManager locationManagerGPS;
	LocationManager locationManagerNetwork;

	LocationListener locationListenerGPS;
	LocationListener locationListenerNetwork;

	public Location mejorLocalizacion = null;

	public EasyGps(Context pContext) {

		Log.i("info", "iniciar servicios");

		locationManagerGPS = (LocationManager) pContext
				.getSystemService(pContext.LOCATION_SERVICE);
		locationManagerNetwork = (LocationManager) pContext
				.getSystemService(pContext.LOCATION_SERVICE);

		locationListenerGPS = new GPSLocationListener();
		locationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				0, 0, locationListenerGPS);

		locationListenerNetwork = new GPSLocationListener();
		locationManagerNetwork
				.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
						locationListenerNetwork);

	}

	private class GPSLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {

				Log.i("info", "onLocationChanged: " + location.getProvider());

				// anunciarNuevaPosicion(location);

				if (isBetterLocation(location, mejorLocalizacion)) {
					mejorLocalizacion = location;

				}

			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.i("info", "provider disabled. " + provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.i("info", "provider enabled. " + provider);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.i("info", "onStatusChanged." + provider + ": " + status);

		}
	}

	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		Log.i("info", "revision de posicion a evaluar");
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}
		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > intervaloTiempo;
		boolean isSignificantlyOlder = timeDelta < -intervaloTiempo;
		boolean isNewer = timeDelta > 0;
		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}
		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;
		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());
		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	public void DestroyEasyGps() {

		locationManagerGPS.removeUpdates(locationListenerGPS);
		locationManagerNetwork.removeUpdates(locationListenerNetwork);

	}

}
