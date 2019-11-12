package org.fagu.fmv.image;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.OptionalInt;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.media.MetadataProperties;
import org.fagu.fmv.media.Metadatas;
import org.fagu.fmv.utils.media.Size;

import net.sf.json.JSONObject;


/**
 * @author Oodrive
 * @author f.agu
 * @created 7 nov. 2019 10:02:49
 */
public interface ImageMetadatas extends Metadatas, MetadataProperties {

	NavigableMap<String, String> getMetadatas();

	default String getFirst(String... propertyNames) {
		String value;
		for(String propName : propertyNames) {
			value = getMetadatas().get(propName);
			if(value != null) {
				return value;
			}
		}
		return null;
	}

	default OptionalInt getInt(String propertyName) {
		String value = getFirst(propertyName);
		try {
			return OptionalInt.of(Integer.parseInt(value));
		} catch(Exception e) {
			return OptionalInt.empty();
		}
	}

	default Optional<Double> getDouble(String propertyName) {
		String value = getFirst(propertyName);
		try {
			return Optional.of(Double.parseDouble(value));
		} catch(Exception e) {
			return Optional.empty();
		}
	}

	String getFormat();

	long getCreateTime();

	OffsetDateTime getDate();

	Size getResolution();

	Size getDimension();

	String getColorSpace();

	Optional<String> getICCProfile();

	int getColorDepth();

	int getCompressionQuality();

	String getCompression();

	String getResolutionUnit();

	String getDevice();

	String getDeviceModel();

	String getSoftware();

	Integer getISOSpeed();

	default Integer getISOSpeed(String propertyName) {
		try {
			return Integer.parseInt(getFirst(propertyName).split(",")[0]);
		} catch(Exception ignored) { // ignore
		}
		return null;
	}

	Optional<String> getLensModel();

	Float getExposureTime();

	default Float getExposureTime(String propertyName) {
		try {
			String etime = getFirst(propertyName);
			if(etime.contains("/")) {
				String[] values = etime.split("/");
				return Float.valueOf(Float.parseFloat(values[0]) / Float.parseFloat(values[1]));
			}
			return Float.parseFloat(etime);
		} catch(Exception ignored) { // ignore
		}
		return null;
	}

	default String getExposureTimeFormat() {
		Float exposure = getExposureTime();
		if(exposure == null) {
			return StringUtils.EMPTY;
		}
		float floatValue = exposure.floatValue();
		if(floatValue < 1) {
			return "1/" + Math.round(1F / floatValue);
		}
		return Float.toString(floatValue);
	}

	Float getAperture();

	default Float getAperture(String propertyName) {
		try {
			String[] fNumber = getFirst(propertyName).split("/");
			if(fNumber.length == 1) {
				Float.valueOf(Float.parseFloat(fNumber[0]));
			}
			return Float.valueOf(Float.parseFloat(fNumber[0]) / Float.parseFloat(fNumber[1]));
		} catch(Exception ignored) {
			// ignore
		}
		return null;
	}

	default String getApertureFormat() {
		Float aperture = getAperture();
		if(aperture == null) {
			return StringUtils.EMPTY;
		}
		return "F/" + aperture.floatValue();
	}

	Float getFocalLength();

	Flash getFlash();

	Coordinates getCoordinates();

	default Coordinates getCoordinates(LTude latitudeLTude, LTude longitudeLTude) {
		try {
			Double latitude = latitudeLTude != null ? latitudeLTude.toDouble() : null;
			Double longitude = longitudeLTude != null ? longitudeLTude.toDouble() : null;

			if(latitude != null && ! "N".equalsIgnoreCase(latitudeLTude.ref)) {
				latitude = - latitude;
			}
			if(longitude != null && ! "E".equalsIgnoreCase(longitudeLTude.ref)) {
				longitude = - longitude;
			}
			if(latitude != null && longitude != null && ! (latitude > 90 || latitude < - 90 || longitude > 90 || longitude < - 90)) {
				return new Coordinates(latitude, longitude);
			}
		} catch(Exception ignored) {
			// ignore
		}
		return null;
	}

	@Override
	default Object get(String name) {
		return getMetadatas().get(name);
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	default Map<String, Object> getData() {
		return (Map)getMetadatas();
	}

	@Override
	default NavigableSet<String> getNames() {
		return getMetadatas().navigableKeySet();
	}

	@Override
	default String toJSON() {
		return JSONObject.fromObject(getMetadatas()).toString();
	}

	public static class LTude {

		private final int degrees;

		private final int minutes;

		private final float secondes;

		private final double value;

		private final String ref;

		public LTude(double value, String ref) {
			this.degrees = 0;
			this.minutes = 0;
			this.secondes = 0;
			this.value = value;
			this.ref = ref;
		}

		public LTude(int degrees, int minutes, float secondes, String ref) {
			this.degrees = degrees;
			this.minutes = minutes;
			this.secondes = secondes;
			this.value = toDouble(degrees, minutes, secondes);
			this.ref = ref;
		}

		public static LTude of(Double latitude, String ref) {
			if(latitude == null || ref == null) {
				return null;
			}
			return new LTude(latitude, ref);
		}

		public int getDegrees() {
			return degrees;
		}

		public int getMinutes() {
			return minutes;
		}

		public float getSecondes() {
			return secondes;
		}

		public String getRef() {
			return ref;
		}

		public double getValue() {
			return value;
		}

		public boolean isOver() {
			return value > 90D || value < - 90D;
		}

		public double toDouble() {
			if("S".equalsIgnoreCase(ref) || "W".equalsIgnoreCase(ref)) {
				return - value;
			}
			return value;
		}

		public static Coordinates toCoordinates(LTude latitude, LTude longitude) {
			if(latitude != null && longitude != null && ! (latitude.isOver() || longitude.isOver())) {
				return new Coordinates(latitude.toDouble(), longitude.toDouble());
			}
			return null;
		}

		// ***********************

		private static double toDouble(int degrees, int minutes, float secondes) {
			double value = degrees;
			value += minutes / 60D;
			value += secondes / 3600D;
			return value;
		}

	}

}
