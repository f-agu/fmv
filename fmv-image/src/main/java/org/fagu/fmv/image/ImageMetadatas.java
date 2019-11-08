package org.fagu.fmv.image;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.OptionalDouble;
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
		} catch(NumberFormatException e) {
			return OptionalInt.empty();
		}
	}

	default OptionalDouble getDouble(String propertyName) {
		String value = getFirst(propertyName);
		try {
			return OptionalDouble.of(Double.parseDouble(value));
		} catch(NumberFormatException e) {
			return OptionalDouble.empty();
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

		private int degrees;

		private int minutes;

		private float secondes;

		private String ref;

		public LTude(int degrees, int minutes, float secondes, String ref) {
			this.degrees = degrees;
			this.minutes = minutes;
			this.secondes = secondes;
			this.ref = ref;
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

		public double toDouble() {
			double value = degrees;
			value += minutes / 60D;
			value += secondes / 3600D;
			return value;
		}

	}

}
