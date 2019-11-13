package org.fagu.fmv.image;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.image.exif.Flash;
import org.fagu.fmv.media.MetadatasContainer;
import org.fagu.fmv.utils.media.Size;


/**
 * @author Oodrive
 * @author f.agu
 * @created 7 nov. 2019 10:02:49
 */
public interface ImageMetadatas extends MetadatasContainer {

	default NavigableMap<String, Object> getMetadatas() {
		Map<String, Object> data = getData();
		if(data instanceof NavigableMap) {
			return (NavigableMap<String, Object>)data;
		}
		return new TreeMap<>(data);
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

	Optional<String> getLensModel();

	Float getExposureTime();

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
			return LTude.toCoordinates(latitudeLTude, longitudeLTude);
		} catch(Exception ignored) { // ignore
		}
		return null;
	}

	@Override
	default Map<String, Object> getData() {
		return getMetadatas();
	}

	// -------------------------------------

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

		private static double toDouble(int degrees, int minutes, float secondes) {
			return degrees + minutes / 60D + secondes / 3600D;
		}

	}

}
